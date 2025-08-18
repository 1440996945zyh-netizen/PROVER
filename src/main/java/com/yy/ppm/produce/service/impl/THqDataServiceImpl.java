package com.yy.ppm.produce.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.*;

import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TrustStopLogRes;
import com.yy.ppm.business.mapper.ScheduledForCloseTrustMapper;
import com.yy.ppm.business.service.impl.TBusTrustServiceImpl;
import com.yy.ppm.dispatch.bean.dto.MHqDataLogDTO;
import com.yy.ppm.dispatch.mapper.MHqDataLogMapper;
import com.yy.ppm.machine.controller.MLocationHistoryController;
import com.yy.ppm.master.bean.dto.FieldRemark;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.po.MShipLogPO;
import com.yy.ppm.produce.bean.dto.THqTallyDTO;
import com.yy.ppm.produce.bean.dto.THqTallySearchDTO;
import com.yy.ppm.produce.mapper.THqTallyMapper;
import com.yy.ppm.produce.service.THqDataService;
import com.yy.ppm.produce.mapper.THqDataMapper;
import com.yy.ppm.produce.bean.dto.THqDataDTO;
import com.yy.ppm.produce.bean.dto.THqDataSearchDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import jakarta.annotation.Resource;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清数据补录表(THqData)ServiceImpl
 * @Description
 * @createTime 2025年04月24日 17:23:00
 */
@Service
public class THqDataServiceImpl implements THqDataService {

    @Resource
    private THqDataMapper tHqDataMapper;
    @Resource
    private THqTallyMapper tHqTallyMapper;
    @Resource
    private MHqDataLogMapper hqDataLogMapper;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    private static final MicroLogger LOGGER = new MicroLogger(THqDataServiceImpl.class);


    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<THqDataDTO> getList(THqDataSearchDTO searchDTO) {

        Pages<THqDataDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tHqDataMapper.getList(searchDTO);
        });

        return pages;
    }

    /**
     * 查询单条记录
     * @param id
     * @return 实体
     */
    @Override
    public THqDataDTO getDetail(Long id) {
        return tHqDataMapper.getById(id);
    }

    /**
     * 查询单条记录
     * @param ids
     * @return 实体
     */
    @Override
    public List<Map<String,String>> getHqByCargoInfoId(List<Long> ids) {
        List<Map<String,String>> listMap = tHqDataMapper.getHqByCargoInfoId(ids);
        List<Map<String,String>> result = Lists.newArrayList();
        int i = 1;
        for (Map<String, String> map : listMap) {
            Map<String,String> item = Maps.newHashMap();
            item.put("id",String.valueOf(map.get("id")));
            if(String.valueOf(map.get("cargoName")).contains(",")){
                String cargoName = String.valueOf(map.get("cargoName")).split(",")[0];
                if(StringUtil.isNotEmpty(map.get("pqNo")) && !"无喷漆编码".equals(map.get("pqNo"))){
                    item.put("name",map.get("pqNo")  + "_" + cargoName);
                }else if(StringUtil.isNotEmpty(map.get("yardName"))){
                    item.put("name",map.get("yardName")  + "_" + cargoName + "_(" + i++ + ")");
                }else{
                    item.put("name","无喷漆编码"  + "_" + cargoName + "_(" + i++ + ")");
                }
            }else{
                String cargoName = map.get("cargoName");
                if(StringUtil.isNotEmpty(map.get("pqNo")) && !"无喷漆编码".equals(map.get("pqNo"))){
                    item.put("name",map.get("pqNo")  + "_" + cargoName);
                }else if(StringUtil.isNotEmpty(map.get("yardName"))){
                    item.put("name",map.get("yardName")  + "_" + cargoName + "_(" + i++ + ")");
                }else{
                    item.put("name","无喷漆编码"  + "_" + cargoName + "_(" + i++ + ")");
                }
            }
            item.put("pqNo",String.valueOf(map.get("pqNo")));
            result.add(item);

        }
        return result;
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(THqDataDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tHqDataMapper.insert(dto) == 1;

            // 修改
        } else {
            return tHqDataMapper.update(dto) == 1;
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean listSave(List<THqDataDTO> list) {
        Boolean delFlag = false;
        //保存更新数据补录信息
        if(CollectionUtil.isEmpty(list)){
            throw new BusinessRuntimeException("没有要保存的数据");
        }
        Long tallyId = list.get(0).getTallyId();
        //获取船名航次
        Map<String,String> shipVoyageMap = tHqDataMapper.getShipVoyage(tallyId);
        List<THqDataDTO> insertList = Lists.newArrayList();
        List<THqDataDTO> updateList = Lists.newArrayList();
        List<THqDataDTO> deleteList = Lists.newArrayList();
        for (THqDataDTO tHqDataDTO : list) {
            if(ObjectUtil.isEmpty(tHqDataDTO.getId())){  //新增
                if(StringUtil.isNotEmpty(tHqDataDTO.getCargoName())){
                    tHqDataDTO.setId(snowflake.nextId());
                    tHqDataDTO.setDelFlag("1");//未删除
                    //船/场，船/岸，车/场，车/岸
                    if(Arrays.asList("01","03").stream().anyMatch(tHqDataDTO.getSource()::equals) && Arrays.asList("05","06").stream().anyMatch(tHqDataDTO.getTarget()::equals)){
                        tHqDataDTO.setStatus("10");//入库
                        tHqDataDTO.setInPortTime(new Date());//入库
                        tHqDataDTO.setInPortName(securityUtils.getLoginUserName());//入库
                        tHqDataDTO.setInShipName(CollectionUtil.isEmpty(shipVoyageMap)?null:shipVoyageMap.get("shipName"));
                        tHqDataDTO.setInVoyage(CollectionUtil.isEmpty(shipVoyageMap)?null:shipVoyageMap.get("voyage"));
                        tHqDataDTO.setDelFlag("1");
                        insertList.add(tHqDataDTO);
                    }
                    //场/船，岸/船，场/车，岸/车
                    else if(Arrays.asList("05","06").stream().anyMatch(tHqDataDTO.getSource()::equals) && Arrays.asList("01","03").stream().anyMatch(tHqDataDTO.getTarget()::equals)){
//                        tHqDataDTO.setStatus("20");//出库
//                        tHqDataDTO.setOutPortTime(new Date());//出库
//                        tHqDataDTO.setOutPortName(securityUtils.getLoginUserName());//出库
//                        tHqDataDTO.setOutShipName(CollectionUtil.isEmpty(shipVoyageMap)?null:shipVoyageMap.get("shipName"));
//                        tHqDataDTO.setOutVoyage(CollectionUtil.isEmpty(shipVoyageMap)?null:shipVoyageMap.get("voyage"));
//                        tHqDataDTO.setDelFlag("1");
                        throw new BusinessRuntimeException(tHqDataDTO.getCargoName()+"未入库，不允许出库");
                    }
                }
            }else{
                //删除
                if("0".equals(tHqDataDTO.getDelFlag())){
                    delFlag = true;
                    //船/场，船/岸，车/场，车/岸
                    if("10".equals(tHqDataDTO.getStatus()) && Arrays.asList("01","03").stream().anyMatch(tHqDataDTO.getSource()::equals) && Arrays.asList("05","06").stream().anyMatch(tHqDataDTO.getTarget()::equals)){
                        tHqDataDTO.setStatus("0");//入库回退到未入库
                        deleteList.add(tHqDataDTO);
                    }
                    //场/船，岸/船，场/车，岸/车
                    else if("20".equals(tHqDataDTO.getStatus()) && Arrays.asList("05","06").stream().anyMatch(tHqDataDTO.getSource()::equals) && Arrays.asList("01","03").stream().anyMatch(tHqDataDTO.getTarget()::equals)){
                        tHqDataDTO.setStatus("10");//出库回退到入库
                        tHqDataDTO.setDelFlag("1");//回退的入库不需要删除
                        deleteList.add(tHqDataDTO);
                    }
                }else{//更新
                    //船/场，船/岸，车/场，车/岸
                    if(Arrays.asList("01","03").stream().anyMatch(tHqDataDTO.getSource()::equals) && Arrays.asList("05","06").stream().anyMatch(tHqDataDTO.getTarget()::equals)){
                        if(Integer.valueOf(tHqDataDTO.getStatus()) < 10){
                            tHqDataDTO.setStatus("10");//入库
                        }
                        tHqDataDTO.setInPortTime(ObjectUtil.isEmpty(tHqDataDTO.getInPortTime())?new Date():tHqDataDTO.getInPortTime());//入库
                        tHqDataDTO.setInPortName(ObjectUtil.isEmpty(tHqDataDTO.getInPortName())?securityUtils.getLoginUserName():tHqDataDTO.getInPortName());//入库
                        tHqDataDTO.setInShipName(ObjectUtil.isEmpty(tHqDataDTO.getInShipName())?(CollectionUtil.isEmpty(shipVoyageMap)?null:shipVoyageMap.get("shipName")):tHqDataDTO.getInShipName());
                        tHqDataDTO.setInVoyage(ObjectUtil.isEmpty(tHqDataDTO.getInVoyage())?(CollectionUtil.isEmpty(shipVoyageMap)?null:shipVoyageMap.get("voyage")):tHqDataDTO.getInVoyage());
                        tHqDataDTO.setDelFlag("1");
                    }
                    //场/船，岸/船，场/车，岸/车
                    else if(Arrays.asList("05","06").stream().anyMatch(tHqDataDTO.getSource()::equals) && Arrays.asList("01","03").stream().anyMatch(tHqDataDTO.getTarget()::equals)){
                        if(Integer.valueOf(tHqDataDTO.getStatus()) < 20){
                            tHqDataDTO.setStatus("20");//出库
                        }
                        tHqDataDTO.setOutPortTime(ObjectUtil.isEmpty(tHqDataDTO.getOutPortTime())?new Date():tHqDataDTO.getOutPortTime());//入库
                        tHqDataDTO.setOutPortName(ObjectUtil.isEmpty(tHqDataDTO.getOutPortName())?securityUtils.getLoginUserName():tHqDataDTO.getOutPortName());//入库
                        tHqDataDTO.setOutShipName(ObjectUtil.isEmpty(tHqDataDTO.getOutShipName())?(CollectionUtil.isEmpty(shipVoyageMap)?null:shipVoyageMap.get("shipName")):tHqDataDTO.getOutShipName());
                        tHqDataDTO.setOutVoyage(ObjectUtil.isEmpty(tHqDataDTO.getOutVoyage())?(CollectionUtil.isEmpty(shipVoyageMap)?null:shipVoyageMap.get("voyage")):tHqDataDTO.getOutVoyage());
                        tHqDataDTO.setDelFlag("1");
                    }
                    updateList.add(tHqDataDTO);
                }
            }
        }
        //新增
        if(CollectionUtil.isNotEmpty(insertList)){
            tHqDataMapper.insertList(insertList);
        }
        //修改
        if(CollectionUtil.isNotEmpty(updateList)){
            Set<Long> seen = new HashSet<>();
            updateList = updateList.stream()
                    .filter(p -> seen.add(p.getId())) // 根据id去重
                    .collect(Collectors.toList());
            //获取旧数据
            List<Long> ids = updateList.stream().map(THqDataDTO::getId).collect(Collectors.toList());
            List<THqDataDTO> oldList= tHqDataMapper.getByIds(ids);
            saveHqDataLog(oldList,updateList);
            tHqDataMapper.updateList(updateList);
        }
        //删除
        if(CollectionUtil.isNotEmpty(deleteList)){
            List<Long> ids = deleteList.stream().map(THqDataDTO::getId).collect(Collectors.toList());
            List<THqDataDTO> oldList= tHqDataMapper.getByIds(ids);
            saveHqDataLog(oldList,deleteList);
            tHqDataMapper.deleteByIds(deleteList);
            tHqDataMapper.deleteHqTallyByDataIds(deleteList);//海清理货关系表
        }else if(delFlag && CollectionUtil.isEmpty(deleteList)){
            throw new BusinessRuntimeException("不能删除状态与当前作业过程不符的记录");
        }

        //保存理货关系表，insertList
        List<THqTallyDTO> insertTallyDTOS = Lists.newArrayList();
        List<THqTallyDTO> updateTallyDTOS = Lists.newArrayList();
        for (THqDataDTO tHqDataDTO : insertList) {
            THqTallyDTO tHqTallyDTO = new THqTallyDTO();
            tHqTallyDTO.setId(snowflake.nextId());
            tHqTallyDTO.setTallyId(tallyId);
            tHqTallyDTO.setHqDataId(tHqDataDTO.getId());
            insertTallyDTOS.add(tHqTallyDTO);
        }
        for (THqDataDTO tHqDataDTO : updateList) {
            THqTallyDTO tHqTallyDTO = new THqTallyDTO();
            tHqTallyDTO.setId(snowflake.nextId());
            tHqTallyDTO.setTallyId(tallyId);
            tHqTallyDTO.setHqDataId(tHqDataDTO.getId());
            updateTallyDTOS.add(tHqTallyDTO);
        }
        THqTallySearchDTO tHqTallySearchDTO = new THqTallySearchDTO();
        tHqTallySearchDTO.setTallyId(tallyId);
        List<THqTallyDTO> tHqTallyDTOS = tHqTallyMapper.exportList(tHqTallySearchDTO);
        List<Long> hqDataIds = tHqTallyDTOS.stream().map(e -> e.getHqDataId()).collect(Collectors.toList());
        List<THqTallyDTO> saveHqTallyList = Lists.newArrayList();
        insertTallyDTOS.forEach(e->{
            if(!hqDataIds.stream().anyMatch(e.getHqDataId()::equals)){
                saveHqTallyList.add(e);
            }
        });
        updateTallyDTOS.forEach(e->{
            if(!hqDataIds.stream().anyMatch(e.getHqDataId()::equals)){
                saveHqTallyList.add(e);
            }
        });
        if(CollectionUtil.isNotEmpty(saveHqTallyList)){
            tHqTallyMapper.insertList(saveHqTallyList);
        }
        return true;
    }

    //判断出库入库
    private String isInOutYard(String source,String target){
        //船/场，船/岸，车/场，车/岸
        if(Arrays.asList("01","03").stream().anyMatch(source::equals) && Arrays.asList("05","06").stream().anyMatch(target::equals)){
            return "10";//入库
        }
        //船/场，船/岸，车/场，车/岸
        else if(Arrays.asList("05","06").stream().anyMatch(source::equals) && Arrays.asList("01","03").stream().anyMatch(target::equals)){
            return "20";//出库
        }
        return "30";//水平
    }


    /**
     * 删除
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return tHqDataMapper.deleteById(id) == 1;

    }

    /**
     * 删除
     * @param tallyId
     * @return 是否成功
     */
    @Override
    public boolean deleteByTallyId(Long tallyId) {
        List<Long> ids = tHqDataMapper.getHqDataId(tallyId);
        if(CollectionUtil.isEmpty(ids)){
            return true;
        }
        Map<String,String> processMap = tHqDataMapper.getProcess(tallyId);
        String sourceCd = processMap.get("sourceCd");
        String targetCd = processMap.get("targetCd");
        //船/场，船/岸，车/场，车/岸  未入库：0  入库：10  出库：20
        if(Arrays.asList("01","03").stream().anyMatch(sourceCd::equals) && Arrays.asList("05","06").stream().anyMatch(targetCd::equals)){
            Map<String,Object> condition = Maps.newHashMap();
            condition.put("ids",ids);
            condition.put("status","0");
            tHqDataMapper.updateByIds(condition);
        }
        //场/船，岸/船，场/车，岸/车
        else if(Arrays.asList("05","06").stream().anyMatch(sourceCd::equals) && Arrays.asList("01","03").stream().anyMatch(targetCd::equals)){
            Map<String,Object> condition = Maps.newHashMap();
            condition.put("ids",ids);
            condition.put("status","10");
            tHqDataMapper.updateByIds(condition);
        }
        return true;
    }

    /**
     * 启动新的线程保存日志，不影响流程
     * @param oldObjList
     * @param newObjList
     */
    private void saveHqDataLog(List<THqDataDTO> oldObjList, List<THqDataDTO> newObjList){
        // 创建大小为5的线程池
//        Long loginUserId = securityUtils.getLoginUserId();
//        String loginUserName = securityUtils.getLoginUserName();
//        ExecutorService executor = Executors.newFixedThreadPool(5);
//        executor.submit(() -> {
//            LOGGER.warn("Task is running on thread " + Thread.currentThread().getName());
//            insertHqLog(oldObjList,newObjList,loginUserId,loginUserName);
//            LOGGER.warn("Task is finished on thread " + Thread.currentThread().getName());
//        });
//        // 关闭线程池
//        executor.shutdown();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            Long loginUserId = securityUtils.getLoginUserId();
            String loginUserName = securityUtils.getLoginUserName();
            SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
            SqlSession sqlSession = sqlSessionFactory.openSession();
            Connection connection = sqlSession.getConnection();
            try{
                //3、设置手动提交
                connection.setAutoCommit(false);
                //4、获取Mapper
                ScheduledForCloseTrustMapper mapper = sqlSession.getMapper(ScheduledForCloseTrustMapper.class);
                //5、将传入List中的数据按200一组均分
//                List<List<Long>> lists = splitList(collect,100);
                //6、新建任务列表
                List<Callable<Integer>> callableList = new ArrayList<>();
                //7、根据均分的数据分别新建Callable任务
                Callable<Integer> callable = new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        try{
                            insertHqLog(oldObjList,newObjList,loginUserId,loginUserName);
                        }catch (Exception e){
                            LOGGER.warn(e.getMessage());
                            //插入失败返回
                            return 0;
                        }
                        //插入成功返回成功提交数
                        return 1;
                    }
                };
                callableList.add(callable);
                //8、任务放入线程池开始执行
                List<Future<Integer>> futures = executorService.invokeAll(callableList);
                //9、对比每个任务的返回值 <= 0 代表执行失败
                for(Future<Integer> future : futures){
                    if(future.get() <= 0){
                        //12、只要有一组任务失败回滚整个connection
                        connection.rollback();
                        LOGGER.warn("定时任务关闭集疏港失败子任务执行失败");
                        return;
                    }
                }
                //10、主线程和子线程都执行成功 直接提交
                connection.commit();
            }catch (Exception e){
                //11、主线程报错回滚
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    LOGGER.warn("定时任务关闭集疏港失败"+ex.getMessage());
                }
                LOGGER.warn("定时任务关闭集疏港失败"+e.getMessage());
            } finally {
                executorService.shutdown();
            }
        }catch (Exception e){
            LOGGER.warn("定时任务关闭集疏港失败"+e.getMessage());
        }
    }

    private void insertHqLog(List<THqDataDTO> oldObjList, List<THqDataDTO> newObjList,Long loginUserId,String loginUserName){
        List<MHqDataLogDTO> insertList = Lists.newArrayList();
        Map<Long, THqDataDTO> newObjMap = newObjList.stream().collect(Collectors.toMap(THqDataDTO::getId,dto -> dto, (existing, replacement) -> existing));
        oldObjList.stream().forEach(oldObj->{
            THqDataDTO newObj = newObjMap.get(oldObj.getId());
            List<String> compareInfo = compareObjects(oldObj,newObj);
            MHqDataLogDTO hqDataLogDTO = new MHqDataLogDTO();
            hqDataLogDTO.setId(snowflake.nextId());
            hqDataLogDTO.setHqDataId(oldObj.getId());
            hqDataLogDTO.setUpdateInfo(JSONUtil.toJsonStr(compareInfo));
            hqDataLogDTO.setCreateBy(loginUserId);
            hqDataLogDTO.setCreateByName(loginUserName);
            hqDataLogDTO.setCreateTime(new Date());

            insertList.add(hqDataLogDTO);
        });
        hqDataLogMapper.insertList(insertList);
    }


    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        // 递归获取所有父类的字段
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            Collections.addAll(fields, c.getDeclaredFields());
        }
        return fields;
    }

    /**
     * 比较两个对象并返回差异信息
     */
    public static List<String> compareObjects(Object obj1, Object obj2){
        try {
            if (obj1 == null || obj2 == null) {
                throw new IllegalArgumentException("比较对象不能为null");
            }
            if (!obj1.getClass().equals(obj2.getClass())) {
                throw new IllegalArgumentException("两个对象类型不同");
            }
            List<String> diffs = new ArrayList<>();
            List<Field> fields = getAllFields(obj1.getClass());
            for (Field field : fields) {
                field.setAccessible(true); // 允许访问私有字段
                Object oldValue = field.get(obj1);
                Object newValue = field.get(obj2);
                if (!Objects.equals(oldValue, newValue)) {
                    FieldRemark remark = field.getAnnotation(FieldRemark.class);
                    if(!ObjectUtils.isEmpty(newValue)){
                        if(oldValue instanceof Date){
                            oldValue = DateUtils.formatDate((Date) oldValue,"yyyy-MM-dd HH:mm:ss");
                        }
                        if(newValue instanceof Date){
                            newValue = DateUtils.formatDate((Date) newValue,"yyyy-MM-dd HH:mm:ss");
                        }
                        if(remark != null){
                            diffs.add((remark != null ? remark.value() : null) + "： " + (ObjectUtils.isEmpty(oldValue)?"":oldValue) + "     ———>     " + (ObjectUtils.isEmpty(newValue)?"":newValue) );
                        }
                    }
                }
            }
            return diffs;
        }catch (IllegalAccessException e){
            throw new BusinessRuntimeException("修改日志提取失败");
        }
    }


}

