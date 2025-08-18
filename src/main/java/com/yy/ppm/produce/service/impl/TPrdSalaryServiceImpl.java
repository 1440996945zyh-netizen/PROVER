package com.yy.ppm.produce.service.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.common.enums.SalaryStatusEnum;
import com.yy.ppm.common.mapper.PublicMapper;
import com.yy.ppm.master.bean.dto.MPiecePriceDTO;
import com.yy.ppm.produce.bean.dto.TPrdSalaryResultDTO;
import com.yy.ppm.produce.bean.dto.salary.*;
import com.yy.ppm.produce.bean.po.TPrdSalaryLogPO;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import com.yy.ppm.produce.mapper.TPrdSalaryLogMapper;
import com.yy.ppm.produce.mapper.TPrdSalaryMapper;
import com.yy.ppm.produce.service.TPrdSalaryService;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-04 17:03
 */
@Service
public class TPrdSalaryServiceImpl implements TPrdSalaryService {

    @Resource
    private TPrdSalaryMapper tPrdSalaryMapper;

    @Resource
    private SecurityUtils securityUtils;
    @Resource
    public PublicMapper publicMapper;
    @Autowired
    private Snowflake snowflake;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private TPrdSalaryLogMapper tPrdSalaryLogMapper;
    private static final Logger log = LoggerFactory.getLogger(TPrdSalaryServiceImpl.class);


    private static final int CURSOR_LIMIT = 5_000;
    private static final String STATUS_APPROVE = "0";
    private static final String DEPT_NAME_LJD = "流机队";

    public Map<String,String> getDays(String yue){
        Map<String,String> map = new HashMap<>();
        if(yue == null){
            throw new BusinessRuntimeException("月份不能为空");
        }
        String[] date = yue.split("-");
        int year = Integer.parseInt(date[0]); // 要查询的年份
        int month = Integer.parseInt(date[1]); // 要查询的月份（1-12）
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1); // 设置日期为1号
        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH); // 获取这个月的天数
        Integer startDay = 0;
        Integer endDay = 0;
        //获取字典的开始天数和结束天数
        List<Map<String, Object>> resList = publicMapper.getDictListByType("SALARY_DAY");
        if(resList != null){
            startDay = Integer.parseInt((String) resList.get(0).get("value"));
            endDay = Integer.parseInt((String) resList.get(1).get("value"));
        }
        if(days < endDay){
            //某月天数小于于维护天数
            endDay = days;
        }
        map.put("startDay",year +"-" + month + "-" + startDay);
        map.put("endDay",year +"-" + month + "-" + endDay);
        return map;
    }

    @Override
    public Pages<TPrdSalaryResultDTO> listSalary(SalaryQueryDTO query, PageParameter parameter) {
//        Map<String, String> days = getDays(query.getMonth());
//        query.setStartDay(days.get("startDay"));
//        query.setEndDay(days.get("endDay"));
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdSalaryMapper.listSalary(query);
        });
    }

    @Override
    public Map<String, String> getSalarySum(SalaryQueryDTO query) {
        return tPrdSalaryMapper.getSalarySumNew(query);
    }

    @Override
    public byte[] exportSalary(SalaryQueryDTO query) {
//        Map<String, String> days = getDays(query.getMonth());
//        query.setStartDay(days.get("startDay"));
//        query.setEndDay(days.get("endDay"));
        if(query.getStartDate() == null){
            throw new BusinessRuntimeException("请选择开始日期");
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os).build()) {
            WriteSheet writeSheet0 = EasyExcel.writerSheet("Sheet0").head(TPrdSalaryDTO.class).build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TPrdSalaryDTO> cursor = tPrdSalaryMapper.cursorListSalary(query)) {
                    Iterator<TPrdSalaryDTO> iterator = cursor.iterator();
                    if (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            List<TPrdSalaryDTO> salarys = new ArrayList<>();
                            for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                                salarys.add(iterator.next());
                            }
                            excelWriter.write(salarys, writeSheet0);
                        }
                    } else {
                        excelWriter.write(Collections.emptyList(), writeSheet0);
                    }

                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });

            WriteSheet writeSheet1 = EasyExcel.writerSheet("Sheet1").head(TPrdSalaryGroupByProcessDTO.class).build();
            List<TPrdSalaryGroupByProcessDTO> list = tPrdSalaryMapper.listSalaryGroupByProcess(query);
            excelWriter.write(list, writeSheet1);
        }
        return os.toByteArray();
    }

    @Override
    public byte[] exportSalary2(SalaryQueryDTO query) {
//        Map<String, String> days = getDays(query.getMonth());
//        query.setStartDay(days.get("startDay"));
//        query.setEndDay(days.get("endDay"));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TPrdSalaryExcelDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TPrdSalaryExcelDTO> cursor = tPrdSalaryMapper.cursorListSalary2(query)) {
                    Iterator<TPrdSalaryExcelDTO> iterator = cursor.iterator();
                    if (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            List<TPrdSalaryExcelDTO> salarys = new ArrayList<>();
                            for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                                salarys.add(iterator.next());
                            }
                            excelWriter.write(salarys, writeSheet);
                        }
                    } else {
                        excelWriter.write(Collections.emptyList(), writeSheet);
                    }

                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }

    @Override
    @Async
    //@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void examine(SalaryQueryExamineDTO dto, UserInfo userInfo, String redisKey) {
        long start = System.currentTimeMillis();
        List<TPrdSalaryPO> list = new ArrayList<>();
        //获取开始和截止日期
//        Map<String, String> days = getDays(dto.getAuditMonth());
//        dto.setStartDay(days.get("startDay"));
//        dto.setEndDay(days.get("endDay"));
        //计件作业量
        List<TPrdSalaryResultDTO> salaryList = tPrdSalaryMapper.getSalary2(dto);
        // 错误日志
        StringBuilder errorMsg = new StringBuilder();
        // 校验开始结束日期想关联的所有月份内是否存在hr审核的计件
        List<String> checkDateList = new ArrayList<>();
        //获取开始结束时间内的所有的月份信息
        LocalDateTime begainTime = LocalDateTime.ofInstant(dto.getStartDate().toInstant(), ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(dto.getEndDate().toInstant(), ZoneId.systemDefault());
        LocalDateTime currentTime = begainTime;
        while (!currentTime.isAfter(endTime)){
            System.out.println("开始"+currentTime);
            checkDateList.add(currentTime.getYear() + "-" + String.format("%02d", currentTime.getMonthValue()));
            currentTime = currentTime.plusMonths(1);
            System.out.println("结束"+currentTime);
        }
        checkDateList.add(endTime.getYear() + "-" + String.format("%02d", endTime.getMonthValue()));
        checkDateList = checkDateList.stream().distinct().collect(Collectors.toList());
        Integer count = tPrdSalaryMapper.getSalaryByCheckTimes(checkDateList);
        if(count>0){
            errorMsg.append("在"+checkDateList.stream().collect(Collectors.joining(","))+" 存在已HR审核的计件数据；");
        }

        // 查询所有需要根据作业过程计价的部门
        List<Long> deptIdList = tPrdSalaryMapper.getAllProcessDept();
        Map<Long, Long> processDeptMap = deptIdList.stream().collect(Collectors.toMap(item -> item, item -> item));
        // 查询所有计件单价
        List<MPiecePriceDTO> priceList =  tPrdSalaryMapper.getAllPiecePrice();
        Map<String, MPiecePriceDTO> priceMap = new HashMap<>();
        Map<String, MPiecePriceDTO> priceProcessMap = new HashMap<>();
        if (CollectionUtils.isEmpty(priceList)) {
            errorMsg.append("未找到计件单价;");
        }
        // 判断计件单价是否存在重复，一部分部门需要加入作业过程判重
        priceList.stream().forEach(item -> {
            if (processDeptMap.containsKey(item.getDeptId())) {
                String itemFlag = new StringBuilder().append(item.getDeptId())
                        .append(item.getSalaryTypeCode()).append(item.getProcessCd()).toString();
                if (priceProcessMap.containsKey(itemFlag)) {
                    errorMsg.append(new StringBuilder().append(item.getCompanyName()).append("-")
                            .append(item.getDeptName()).append("-").append(item.getWorkProcessChildName())
                            .append(item.getSalaryTypeName()).append(" 计件单价重复，无法审核;"));
                    /*throw new BusinessRuntimeException(new StringBuilder().append(item.getCompanyName()).append("-")
                            .append(item.getDeptName()).append("-").append(item.getWorkProcessChildName())
                            .append(item.getSalaryTypeName()).append(" 计件单价重复，无法审核").toString());*/
                } else {
                    priceProcessMap.put(itemFlag, item);
                }
            } else {
                String itemFlag = new StringBuilder().append(item.getDeptId()).append(item.getSalaryTypeCode()).toString();
                if (priceMap.containsKey(itemFlag)) {
                    errorMsg.append(new StringBuilder().append(item.getCompanyName()).append("-")
                            .append(item.getDeptName()).append("-").append(item.getSalaryTypeName()).append(" 计件单价重复，无法审核;"));
                    /*throw new BusinessRuntimeException(new StringBuilder().append(item.getCompanyName()).append("-")
                            .append(item.getDeptName()).append("-").append(item.getSalaryTypeName()).append(" 计件单价重复，无法审核").toString());*/
                } else {
                    priceMap.put(itemFlag, item);
                }
            }
        });
        if (!StringUtils.isEmpty(errorMsg.toString())) {
            // 更新错误日志，并解锁
            addErrLog(errorMsg.toString(), userInfo, dto);
            redisTemplate.delete(redisKey);
            return;
        }
        //生产审核
        salaryList.stream().forEach(item -> {
            StringBuilder errorMsgHead = new StringBuilder().append(DateUtils.formatDate(item.getWorkDate(), "yyyy-MM-dd"))
                    .append(" ").append(item.getClassName()).append(" ").append(item.getDeptName())
                    .append(" ").append(item.getUserByName()).append(" ");
            //String salaryMsg = new StringBuilder().append(item.getDeptName()).append("(").append(item.getUserByName()).append(")").toString();
            TPrdSalaryPO po = new TPrdSalaryPO();
            po.setId(item.getId());
            if (STATUS_APPROVE.equals(dto.getFlag())) { // 审核
                if(SalaryStatusEnum._20.getCode().equals(item.getSalaryStatusCode())){
                    errorMsg.append(errorMsgHead).append("已生产审核;");
                    //throw new BusinessRuntimeException("请勿重新审核");
                }
                if(SalaryStatusEnum._30.getCode().equals(item.getSalaryStatusCode())){
                    errorMsg.append(errorMsgHead).append("已HR审核;");
                    //throw new BusinessRuntimeException("请勿重新审核");
                }
                if (StringUtils.isEmpty(item.getSalaryTypeCode())) {
                    errorMsg.append(errorMsgHead).append("计件工资类型为空，无法审核;");
                    //throw new BusinessRuntimeException(salaryMsg + " 计件工资类型为空，无法审核");
                }
                if (item.getCompanyId() == null) {
                    errorMsg.append(errorMsgHead).append("作业公司为空;");
                    //throw new BusinessRuntimeException(salaryMsg + " 数据异常，无法审核");
                }
                if (item.getDeptId() == null) {
                    errorMsg.append(errorMsgHead).append("部门为空;");
                    //throw new BusinessRuntimeException(salaryMsg + " 数据异常，无法审核");
                }
                if (processDeptMap.containsKey(item.getDeptId())) {
                    if (StringUtils.isEmpty(item.getProcessDetailCode())) {
                        errorMsg.append(errorMsgHead).append("作业过程为空;");
                        //throw new BusinessRuntimeException(salaryMsg + " 作业过程为空，无法审核");
                    }
                    String itemFlag =  new StringBuilder().append(item.getDeptId())
                            .append(item.getSalaryTypeCode()).append(item.getProcessDetailCode()).toString();
                    if (priceProcessMap.containsKey(itemFlag)) {
                        MPiecePriceDTO mPiecePriceDTO = priceProcessMap.get(itemFlag);
                        po.setAmount(item.getTon().multiply(mPiecePriceDTO.getPrice()));
                        po.setPrice(mPiecePriceDTO.getPrice());
                        po.setSalaryPriceId(mPiecePriceDTO.getId());
                    } else {
                        errorMsg.append(errorMsgHead).append(item.getDeptName()+"_"+item.getSalaryTypeCode()+"_"+item.getProcessDetailCode()+" 未匹配到计件单价;");
                        //throw new BusinessRuntimeException(salaryMsg + " 未找到对应的计件单价，无法审核");
                    }
                } else {
                    String itemFlag =  new StringBuilder().append(item.getDeptId())
                        .append(item.getSalaryTypeCode()).toString();
                    if (priceMap.containsKey(itemFlag)) {
                        MPiecePriceDTO mPiecePriceDTO = priceMap.get(itemFlag);
                        po.setAmount((item.getTon().multiply(mPiecePriceDTO.getPrice())).setScale(2,BigDecimal.ROUND_HALF_UP));
                        po.setPrice(mPiecePriceDTO.getPrice());
                        po.setSalaryPriceId(mPiecePriceDTO.getId());
                    } else {
                        errorMsg.append(errorMsgHead).append(item.getDeptName()+"_"+item.getSalaryTypeCode()+" 未匹配到计件单价;");
                        //throw new BusinessRuntimeException(salaryMsg + " 未找到对应的计件单价，无法审核");
                    }
                }
                po.setSalaryStatusCode(SalaryStatusEnum._20.getCode());
                po.setSalaryStatusName(SalaryStatusEnum._20.getLabel());
                po.setCreateBy(userInfo.getId());
                po.setCreateByName(userInfo.getUserName());
                po.setCreateTime(new Date());
                po.setSalaryStatusCodeWhere(SalaryStatusEnum._10.getCode());
            } else { // 销审
                if(SalaryStatusEnum._30.getCode().equals(item.getSalaryStatusCode())){
                    errorMsg.append(errorMsgHead).append("已HR审核;");
                    //throw new BusinessRuntimeException("该数据未审核");
                }
                //取消审核
                po.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
                po.setSalaryStatusName(SalaryStatusEnum._10.getLabel());
                po.setCreateBy(null);
                po.setCreateByName(null);
                po.setCreateTime(null);
                po.setPrice(null);
                po.setSalaryPriceId(null);
                po.setAmount(null);
                po.setSalaryStatusCodeWhere(SalaryStatusEnum._20.getCode());
            }
            list.add(po);
        });
        if (!StringUtils.isEmpty(errorMsg.toString())) {
            // 更新错误日志，并解锁
            addErrLog(errorMsg.toString(), userInfo, dto);
            redisTemplate.delete(redisKey);
            return;
        }
        int cpuNubmer = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cpuNubmer);
        //1、根据sqlSessionTemplate获取SqlSession工厂
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //2、获取Connection来手动控制事务
        Connection connection = sqlSession.getConnection();
        try{
            //3、设置手动提交
            connection.setAutoCommit(false);
            //4、获取Mapper
            TPrdSalaryMapper mapper = sqlSession.getMapper(TPrdSalaryMapper.class);
            //5、将传入List中的数据按200一组均分
            List<List<TPrdSalaryPO>> lists = splitList(list,200);
            //6、新建任务列表
            List<Callable<Integer>> callableList = new ArrayList<>();
            //7、根据均分的数据分别新建Callable任务
            lists.stream().forEach(updateList -> {
                Callable<Integer> callable = new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        try{
                            mapper.examine(updateList);
                        }catch (Exception e){
                            log.error(e.getMessage());
                            //插入失败返回
                            return 0;
                        }
                        //插入成功返回成功提交数
                        return 1;
                    }
                };
                callableList.add(callable);
            });

            //8、任务放入线程池开始执行
            List<Future<Integer>> futures = executor.invokeAll(callableList);
            //9、对比每个任务的返回值 <= 0 代表执行失败
            for(Future<Integer> future : futures){
                if(future.get() <= 0){
                    //12、只要有一组任务失败回滚整个connection
                    connection.rollback();
                    return;
                }
            }
            //10、主线程和子线程都执行成功 直接提交
            addErrLog("执行成功", userInfo, dto);
            connection.commit();
        }catch (Exception e){
            //11、主线程报错回滚
            try {
                connection.rollback();
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
            log.error(e.getMessage());
            //throw new BusinessRuntimeException("出现异常！");
        } finally {
            redisTemplate.delete(redisKey);
            executor.shutdown();
        }
        long end = System.currentTimeMillis();
        log.info("计件工资生产审核用时 " + (end - start) + "毫秒");
    }

    /**
     * hr审核
     * @param dto
     */
    @Override
    public void examineHr(SalaryQueryExamineDTO dto) {
        long start = System.currentTimeMillis();
        //获取开始和截止日期
//        Map<String, String> days = getDays(dto.getAuditMonth());
//        dto.setStartDay(days.get("startDay"));
//        dto.setEndDay(days.get("endDay"));
        //1、根据sqlSessionTemplate获取SqlSession工厂
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //2、获取Connection来手动控制事务
        Connection connection = sqlSession.getConnection();
        int cpuNubmer = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cpuNubmer);
        try{
            //3、设置手动提交
            connection.setAutoCommit(false);
            //4、获取Mapper
            TPrdSalaryMapper mapper = sqlSession.getMapper(TPrdSalaryMapper.class);
            SalaryQueryExamineDTO updateDto = new SalaryQueryExamineDTO();
            BeanUtils.copyProperties(dto, updateDto);
            if (STATUS_APPROVE.equals(dto.getFlag())) {
                dto.setSalaryStatusCode(SalaryStatusEnum._30.getCode());
                //dto.setDeptId(null);
                List<TPrdSalaryResultDTO> approveList = mapper.getSalary2(dto);
                if (!CollectionUtils.isEmpty(approveList)) {
                    throw new BusinessRuntimeException("该数据已审核,请勿重复操作");
                }
                dto.setSalaryStatusCode(SalaryStatusEnum._20.getCode());
                List<TPrdSalaryResultDTO> NoApproveList = mapper.getSalary2(dto);

                // 定义输出日期的格式
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                // 格式化日期为字符串
                String startDate = outputFormat.format(dto.getStartDate());
                String endDate = outputFormat.format(dto.getEndDate());;

//                if (!CollectionUtils.isEmpty(NoApproveList)) {
//                    throw new BusinessRuntimeException(startDate + "到" +endDate + " 存在未生成的计件工资,无法审核");
//                }
                updateDto.setSalaryStatusCode(SalaryStatusEnum._30.getCode());
                updateDto.setSalaryStatusName(SalaryStatusEnum._30.getLabel());
                updateDto.setExamineByHr(securityUtils.getLoginUserId());
                updateDto.setExamineByNameHr(securityUtils.getLoginUserName());
                updateDto.setExamineTimeHr(new Date());
                updateDto.setSalaryStatusCodeWhere(SalaryStatusEnum._20.getCode());
                List<TPrdSalaryPO> sumList = mapper.getSalarySum(updateDto);
                mapper.examineHr(updateDto);
                if (!CollectionUtils.isEmpty(sumList)) {
                    //审核
                    sumList.stream().forEach(pos -> {
                        pos.setId(snowflake.nextId());
                        pos.setSalaryMonth(dto.getMonth());
                        pos.setLoginUserId(securityUtils.getLoginUserId());
                        pos.setLoginUserName(securityUtils.getLoginUserName());
                        pos.setNow(new Date());
                    });
                    //5、将传入List中的数据按200一组均分
                    List<List<TPrdSalaryPO>> lists = splitList(sumList,100);
                    //6、新建任务列表
                    List<Callable<Integer>> callableList = new ArrayList<>();
                    //7、根据均分的数据分别新建Callable任务
                    lists.stream().forEach(updateList -> {
                        Callable<Integer> callable = new Callable<Integer>() {
                            @Override
                            public Integer call() throws Exception {
                                try{
                                    for (TPrdSalaryPO tPrdSalaryPO:updateList) {
                                        tPrdSalaryPO.setStartDate(updateDto.getStartDate());
                                        tPrdSalaryPO.setEndDate(updateDto.getEndDate());
                                        if(!StringUtils.isEmpty(updateDto.getClassCode())){
                                            tPrdSalaryPO.setClassCode(updateDto.getClassCode());
                                        }
                                        if(!StringUtils.isEmpty(updateDto.getSalaryTypeCode())){
                                            tPrdSalaryPO.setSalaryTypeCode(updateDto.getSalaryTypeCode());
                                        }
                                        if(updateDto.getDeptId() != null){
                                            tPrdSalaryPO.setDeptId(updateDto.getDeptId());
                                        }
                                        if(updateDto.getCompanyId() != null){
                                            tPrdSalaryPO.setCompanyId(updateDto.getCompanyId());
                                        }
                                    }
                                    mapper.insertSum(updateList);
                                }catch (Exception e){
                                    log.error(e.getMessage());
                                    //插入失败返回
                                    return 0;
                                }
                                //插入成功返回成功提交数
                                return 1;
                            }
                        };
                        callableList.add(callable);
                    });

                    //8、任务放入线程池开始执行
                    List<Future<Integer>> futures = executor.invokeAll(callableList);
                    //9、对比每个任务的返回值 <= 0 代表执行失败
                    for(Future<Integer> future : futures){
                        if(future.get() <= 0){
                            //12、只要有一组任务失败回滚整个connection
                            connection.rollback();
                            return;
                        }
                    }
                }else{
                    throw new BusinessRuntimeException("审核失败，未汇总到数据");
                }
            } else {
                dto.setSalaryStatusCode(SalaryStatusEnum._30.getCode());
                dto.setDeptId(null);
                List<TPrdSalaryResultDTO> approveList = mapper.getSalary2(dto);
                if (CollectionUtils.isEmpty(approveList)) {
                    throw new BusinessRuntimeException("未审核,无法撤销");
                }
                updateDto.setSalaryStatusCode(SalaryStatusEnum._20.getCode());
                updateDto.setSalaryStatusName(SalaryStatusEnum._20.getLabel());
                updateDto.setExamineByHr(null);
                updateDto.setExamineByNameHr(null);
                updateDto.setExamineTimeHr(null);
                updateDto.setSalaryStatusCodeWhere(SalaryStatusEnum._30.getCode());
                mapper.examineHr(updateDto);
                mapper.deleteSum(dto);
            }
            //10、执行成功 直接提交
            connection.commit();
            log.info("操作成功！");
        } catch (Exception e){
            //11、主线程报错回滚
            try {
                connection.rollback();
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
            log.error(e.getMessage());
            throw new BusinessRuntimeException(e.getMessage());
        } finally {
            executor.shutdown();
        }
        long end = System.currentTimeMillis();
        log.info("计件工资HR审核用时 " + (end - start) + "毫秒");
    }

    @Override
    public String getExamineLog(SalaryQueryDTO query) {
        Long deptId = null;
        if (!StringUtils.isEmpty(query.getDeptId())) {
            deptId = Long.parseLong(query.getDeptId());
        }
//        TPrdSalaryLogPO log = tPrdSalaryLogMapper.getLog(query.getAuditMonth(), deptId);
        TPrdSalaryLogPO log = tPrdSalaryLogMapper.getLog(query.getStartDate(),query.getEndDate(), deptId);
        if (log != null) {
            String errMsg = log.getErrMsg().replaceAll(";","<br>");

            // 定义输出日期的格式
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            // 格式化日期为字符串
            String startDate = outputFormat.format(log.getStartDate());
            String endDate ="";
            if(log.getEndDate()==null){
                LocalDate currentDate = LocalDate.now();

                // 将当前日期格式化为字符串
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                endDate = currentDate.format(formatter);
            }else {
                 endDate = outputFormat.format(log.getEndDate());
            }

            return new StringBuilder().append(startDate).append("到").append(endDate).append(log.getDeptName()).append(log.getFlag())
                    .append("日志(").append(DateUtils.formatDate(log.getCreateTime(),"yyyy-MM-dd HH:mm:ss")).append(")：<br>").append(errMsg).toString();
        }
        return "";
    }

    private <T> List<List<T>> splitList(List<T> list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return Lists.newArrayList();
        }
        List<List<T>> result = Lists.newArrayList();
        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }

    /**
     * 添加审核日志
     * @param errMsg
     * @param userInfo
     * @param dto
     * @return
     */
    private int addErrLog(String errMsg, UserInfo userInfo, SalaryQueryExamineDTO dto) {
        tPrdSalaryLogMapper.deleteLog(dto.getStartDate(),dto.getEndDate(), dto.getDeptId());
        TPrdSalaryLogPO po = new TPrdSalaryLogPO();
        if (errMsg.length() > 1000) {
            errMsg = errMsg.substring(0, 1000) + "...";
        }
        po.setId(snowflake.nextId());
        po.setCreateBy(userInfo.getId());
        po.setCreateByName(userInfo.getUserName());
        po.setNow(new Date());
//        po.setExamineMonth(dto.getAuditMonth());
        po.setDeptId(dto.getDeptId());
        po.setErrMsg(errMsg);
        po.setFlag(dto.getFlag());
        po.setStartDate(dto.getStartDate());
        po.setEndDate(dto.getEndDate());
        return tPrdSalaryLogMapper.addLog(po);
    }
}
