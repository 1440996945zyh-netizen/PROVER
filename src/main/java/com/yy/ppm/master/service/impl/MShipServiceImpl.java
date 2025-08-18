package com.yy.ppm.master.service.impl;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

import jakarta.annotation.Resource;

import cn.hutool.json.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.google.api.client.util.Lists;
import com.yy.common.magic.FileUploadBusinessTypeEnum;
import com.yy.common.util.*;
import com.yy.framework.config.MinioConfig;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.mapper.TBusCustomerMapper;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.mapper.SysFileMapper;
import com.yy.ppm.master.bean.dto.FieldRemark;
import com.yy.ppm.master.bean.po.MShipLogPO;
import com.yy.ppm.system.enums.SysEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.yy.common.page.Pages;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.dto.MShipSearchDTO;
import com.yy.ppm.master.mapper.MShipMapper;
import com.yy.ppm.master.service.MShipService;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 海轮资料(MShip)ServiceImpl
 * @Description
 * @createTime 2023年06月27日 15:44:00
 */
@Service
public class MShipServiceImpl implements MShipService {

    @Resource
    private MShipMapper mShipMapper;
    @Resource
    private Snowflake snowflake;
    @Resource
    private CommonService commonService;
    @Resource
    private SysFileService sysFileService;
    @Resource
    private SecurityUtils securityUtils;
    @Resource
    private MinioConfig minIoConfig;
    @Resource
    private SysFileMapper sysFileMapper;
    @Resource
    private TBusCustomerMapper customerMapper;
    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<MShipDTO> getList(MShipSearchDTO searchDTO) {

        Pages<MShipDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mShipMapper.getList(searchDTO);
        });

        return pages;
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public MShipDTO getDetail(Long id) {
        MShipDTO shipDTO = mShipMapper.getById(id);
        if(shipDTO.getApplicationUnitCode()!=null){
            TBusCustomerDTO customerDTO = customerMapper.getById(shipDTO.getApplicationUnitCode());
            if(ObjectUtils.isEmpty(customerDTO)){
                TBusCustomerDTO dto = customerMapper.getCustomerNameById(shipDTO.getApplicationUnitCode());
                shipDTO.setBhtCustomerName(ObjectUtils.isEmpty(dto)?"渤海通也没有...":dto.getCustomerName());
            }
        }

        return shipDTO;
    }

    @Override
    public List<MShipLogPO> getShipLog(Long id) {
        List<MShipLogPO> shipLogPOS = mShipMapper.getByShipId(id);
        shipLogPOS.forEach(e->{
            JSONArray jsonArray = JSONUtil.parseArray(e.getUpdateInfo());
            e.setUpdateInfos(jsonArray);
        });

        return shipLogPOS;
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    //@Transactional(rollbackFor = Exception.class)
    public boolean doSave(MShipDTO dto) {

        // 名称重复 IMO重复 MMSI重复
        if(dto.getId()==null){
            MShipSearchDTO mShipSearchDTO = new MShipSearchDTO();
            mShipSearchDTO.setShipName(dto.getShipName());
            mShipSearchDTO.setImo(dto.getImo());
            mShipSearchDTO.setMmsi(dto.getMmsi());
            List<MShipDTO> shipList = mShipMapper.getCheckList(mShipSearchDTO);
            if(!CollectionUtils.isEmpty(shipList)){
                throw new BusinessRuntimeException(dto.getShipName()+"-"+dto.getImo()+"-"+dto.getMmsi()+"船名-IMO-MMSI-重复");
            }
        }
        //如果助记码为空，则自动生成。
        if (StringUtil.isEmpty(dto.getShorthandCode())) {
            dto.setShorthandCode(PinYin4jUtils.getPinYinHeadChar(dto.getShipName(), dto.getShipName().length()));
        }
        if (CollectionUtils.isEmpty(dto.getFileIds())) {
            throw new BusinessRuntimeException("附件必须上传船舶证书、船舶规范");
        }
        int count = 0;
        // 通过时驳回意见清空
        if ("10".equals(dto.getStatus())) {
            dto.setApprovalBy(securityUtils.getLoginUserId());
            dto.setApprovalName(securityUtils.getLoginUserName());
            dto.setApprovalTime(new Date());
            dto.setIdea("");
        }
        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            count =  mShipMapper.insert(dto);
        } else {// 修改
            MShipDTO mShipDTO = mShipMapper.getById(dto.getId());
            List<SysFileDTO> sysFileDTOS = sysFileMapper.getBusFiles(mShipDTO.getId(),"MASTER_SHIP_01");
            List<Long> ids = Lists.newArrayList();
            sysFileDTOS.forEach(e->ids.add(e.getId()));
            mShipDTO.setFileIds(ids);
            dto.setBoHaiTongId(mShipDTO.getBoHaiTongId());
            dto.setUpdateBy(securityUtils.getLoginUserId());
            dto.setUpdateByName(securityUtils.getLoginUserName());
            dto.setUpdateTime(new Date());
            saveShipLog(mShipDTO,dto);//保存船舶资料修改日志
            count = mShipMapper.update(dto);
        }
        // 附件保存
        sysFileService.saveFileBusRelation(dto.getFileIds(), dto.getId());
        return count == 1;
    }

    private void saveShipLog(MShipDTO oldObj,MShipDTO newObj){
        List<String> compareInfo = compareObjects(oldObj,newObj);

        MShipLogPO shipLogPO = new MShipLogPO();
        shipLogPO.setId(snowflake.nextId());
        shipLogPO.setShipId(oldObj.getId());
        shipLogPO.setImo(newObj.getImo());
        shipLogPO.setImo(newObj.getMmsi());
        shipLogPO.setUpdateInfo(JSONUtil.toJsonStr(compareInfo));
        mShipMapper.insertShipLog(shipLogPO);
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
                        diffs.add((remark != null ? remark.value() : null) + "： " + (ObjectUtils.isEmpty(oldValue)?"":oldValue) + "     ———>     " + (ObjectUtils.isEmpty(newValue)?"":newValue) );
                    }
                }
            }
            return diffs;
        }catch (IllegalAccessException e){
            throw new BusinessRuntimeException("修改日志提取失败");
        }
    }



    /**
     * 驳回
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doReject(MShipDTO dto) {
        dto.setStatus("9");
        dto.setApprovalBy(securityUtils.getLoginUserId());
        dto.setApprovalName(securityUtils.getLoginUserName());
        dto.setApprovalTime(new Date());
        return mShipMapper.reject(dto) == 1;
    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {

        MShipDTO byId = mShipMapper.getById(id);
        if(byId.getStatus().equals("10")){
            throw new BusinessRuntimeException("已审核通过，无法删除");
        }
        int count = mShipMapper.getShipVoyageByShipId(id);
        if(count>0){
            throw new BusinessRuntimeException("已有航次，无法删除");
        }
        // 删除附件及关系表
        sysFileService.delete(null, id);
        // 删除船舶
        return mShipMapper.deleteById(id) == 1;

    }

    /**
     * 审核
     * @param id
     * @return
     */
    @Override
    @Transactional
    public boolean approveById(Long id) {
        MShipDTO dto = mShipMapper.getById(id);
        if("0".equals(dto.getStatus())){
            throw new BusinessRuntimeException("停用状态，不可审核");
        }
        if("9".equals(dto.getStatus())){
            throw new BusinessRuntimeException("驳回状态，不可审核");
        }
        if("10".equals(dto.getStatus())){
            throw new BusinessRuntimeException("已审核，不能重复审核!");
        }

        MShipDTO tmpDto = new MShipDTO();
        tmpDto.setId(id);
        tmpDto.setStatus("10");
        dto.setStatus("10");
        tmpDto.setApprovalBy(securityUtils.getLoginUserId());
        tmpDto.setApprovalName(securityUtils.getLoginUserName());
        tmpDto.setApprovalTime(new Date());
        return  mShipMapper.approveCancelById(tmpDto) == 1;
    }

    /**
     * 消审
     * @param id
     * @return
     */
    @Override
    public boolean cancelById(Long id) {
        MShipDTO dto = mShipMapper.getById(id);
        if(!"10".equals(dto.getStatus())){
            throw new BusinessRuntimeException("不是审核状态，不能消审!");
        }

        MShipDTO tmpDto = new MShipDTO();
        tmpDto.setId(id);
        tmpDto.setStatus("1");
        dto.setStatus("1");
        return  mShipMapper.approveCancelById(tmpDto) == 1;
    }



    /**
     * 批量同步数据
     * @param list
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sync(List<MShipDTO> list) {

        list.forEach(dto->{
//            // 客户id重复
//            commonService.isRepeat("M_SHIP", "ID", String.valueOf(dto.getId()), StringUtil.getString(dto.getId()), "船舶信息", null);
//
//            // 名称重复
//            commonService.isRepeate("M_SHIP", "SHIP_NAME", dto.getShipName(), StringUtil.getString(dto.getId()), "船名", null);
//
//            if (dto.getImo() !=null){
//                // IMO重复
//                commonService.isRepeate("M_SHIP", "IMO", dto.getImo(), StringUtil.getString(dto.getId()), "IMO", null);
//            }
//            if (dto.getMmsi() !=null) {
//                // MMSI重复
//                commonService.isRepeate("M_SHIP", "MMSI", dto.getMmsi(), StringUtil.getString(dto.getId()), "MMSI", null);
//            }
            MShipSearchDTO mShipSearchDTO = new MShipSearchDTO();
            mShipSearchDTO.setShipName(dto.getShipName());
            mShipSearchDTO.setImo(dto.getImo());
            mShipSearchDTO.setMmsi(dto.getMmsi());
            List<MShipDTO> shipList = mShipMapper.getCheckList(mShipSearchDTO);
            if(!CollectionUtils.isEmpty(shipList)){
                throw new BusinessRuntimeException(dto.getShipName()+"-"+dto.getImo()+"-"+dto.getMmsi()+"船名-IMO-MMSI-重复");
            }


            mShipMapper.insert(dto);
        });
        return true;
    }


    @Override
    public String getBlackShip(List<String> list) {
        List<MShipDTO> list2 = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        list.forEach(dto->{
            MShipDTO shipDTO = mShipMapper.getShipBlackByIMO(dto);
            list2.add(shipDTO);
        });
        list2.stream()
                .filter(Objects::nonNull)
                .forEach(dto -> {
                    stringBuilder.append("船名:").append(dto.getShipName()).append(",").append("IMO:").append(dto.getImo()).append(",");
                });

        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }


    /**
     * 审核
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(MShipDTO dto) {
//        // 客户id重复
//        commonService.isRepeat("M_SHIP", "ID", String.valueOf(dto.getId()), StringUtil.getString(dto.getId()), "船舶信息", null);
//
//        // 名称重复
//        commonService.isRepeate("M_SHIP", "SHIP_NAME", dto.getShipName(), StringUtil.getString(dto.getId()), "船名", null);
//
//        // IMO重复
//        commonService.isRepeate("M_SHIP", "IMO", dto.getImo(), StringUtil.getString(dto.getId()), "IMO", null);
//
//        // MMSI重复
//        commonService.isRepeate("M_SHIP", "MMSI", dto.getMmsi(), StringUtil.getString(dto.getId()), "MMSI", null);
        dto.setStatus("10");
        dto.setCreateTime(new Date());
        dto.setNow(new Date());

        dto.setApprovalBy(securityUtils.getLoginUserId());
        dto.setApprovalName(securityUtils.getLoginUserName());
        dto.setApprovalTime(new Date());
        mShipMapper.approveCancelById(dto);
        String jsonString = com.alibaba.fastjson2.JSONObject.toJSONString(dto);
        com.alibaba.fastjson2.JSONObject jsonObject = com.alibaba.fastjson2.JSONObject.parseObject(jsonString);

        return true;
    }

}

