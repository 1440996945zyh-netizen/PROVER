package com.yy.ppm.business.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.PinYin4jUtils;
import com.yy.common.util.SecurityUtils;

import com.yy.common.util.str.StringUtil;
import com.yy.framework.config.MinioConfig;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCustomerContactDTO;
import com.yy.ppm.business.bean.dto.TBusCustomerPropertyDTO;
import com.yy.ppm.business.mapper.TBusCustomerPropertyMapper;
import com.yy.ppm.business.service.TBusCustomerService;
import com.yy.ppm.business.mapper.TBusCustomerMapper;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.bean.dto.TBusCustomerSearchDTO;
import com.yy.ppm.common.mapper.SysFileMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.master.service.MShipService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName 客户资料表(TBusCustomer)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 13:09:00
 */
@Service
public class TBusCustomerServiceImpl implements TBusCustomerService {

    @Resource
    private TBusCustomerMapper tBusCustomerMapper;
    @Resource
    private TBusCustomerPropertyMapper tBusCustomerPropertyMapper;
    @Resource
    private CommonService commonService;
    @Resource
	private Snowflake snowflake;
    @Resource
    private SysFileMapper sysFileMapper;
    @Resource
    private SysFileService sysFileService;
    @Resource
    private SecurityUtils securityUtils;
    @Resource
    private MinioConfig minIoConfig;
    @Resource
    private MShipService mShipService;
    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TBusCustomerDTO> getList(TBusCustomerSearchDTO searchDTO) {
    	Pages<TBusCustomerDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tBusCustomerMapper.getList(searchDTO);
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
     public TBusCustomerDTO getDetail(Long id) {
         TBusCustomerDTO dto = tBusCustomerMapper.getById(id);
         if (dto == null) {
             throw new BusinessRuntimeException("客户信息不存在~");
         }
         dto.setPropertyList(tBusCustomerPropertyMapper.getList(id));
         dto.setContactList(tBusCustomerMapper.getContactByCustomerId(id));
         return dto;
     }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TBusCustomerDTO dto) {
        int count = 0;
        //判重
        if(StringUtils.isNotEmpty(dto.getCustomerCode())){
            TBusCustomerDTO customerDTO1 = tBusCustomerMapper.getBHTById(dto.getId(),dto.getCustomerCode(),null,null);
            if(ObjectUtil.isNotEmpty(customerDTO1)){
                throw new BusinessRuntimeException("客户代码重复");
            }
        }
        if(StringUtils.isNotEmpty(dto.getCustomerName())){
            TBusCustomerDTO customerDTO2 = tBusCustomerMapper.getBHTById(dto.getId(),null,dto.getCustomerName(),null);
            if(ObjectUtil.isNotEmpty(customerDTO2)){
                throw new BusinessRuntimeException("客户名称重复");
            }
        }
        if(StringUtils.isNotEmpty(dto.getCustomerShortName())){
            TBusCustomerDTO customerDTO3 = tBusCustomerMapper.getBHTById(dto.getId(),null,null,dto.getCustomerShortName());
            if(ObjectUtil.isNotEmpty(customerDTO3)){
                throw new BusinessRuntimeException("客户简称重复");
            }
        }
        // 通过时设置
        if ("10".equals(dto.getStatus())) {
            dto.setApprovalBy(securityUtils.getLoginUserId());
            dto.setApprovalName(securityUtils.getLoginUserName());
            dto.setApprovalTime(new Date());
            dto.setIdea("");
        }
        // 客户属性
        String customerPropertyNames = "";
        if (dto.getPropertyList() != null && dto.getPropertyList().size() > 0) {
            for (TBusCustomerPropertyDTO property : dto.getPropertyList()) {
                customerPropertyNames += property.getCustomerPropertyName() + "、";
            }
            customerPropertyNames = customerPropertyNames.substring(0, customerPropertyNames.length() - 1);
        }
        dto.setCustomerPropertyNames(customerPropertyNames);

        // 助记码
        //如果助记码为空，则自动生成。
        if (StringUtils.isEmpty(dto.getShorthandCode())) {
            dto.setShorthandCode(PinYin4jUtils.getPinYinHeadChar(dto.getCustomerName(), dto.getCustomerName().length()));
        }

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());

            Integer maxCode = tBusCustomerMapper.getMaxCode();
            if(maxCode == null){
                maxCode = 1;
            }else if ( maxCode >= Integer.MAX_VALUE) {
               throw new BusinessRuntimeException("超出Integer最大范围");
            } else {
                // 客户代码插入最大位加1
                maxCode++;
            }
            dto.setCustomerCode(String.valueOf(maxCode));
            List<TBusCustomerContactDTO> contactList = dto.getContactList();

            if(!CollectionUtils.isEmpty(contactList)){
                for (TBusCustomerContactDTO tBusCustomerContactDTO : contactList) {
                    tBusCustomerContactDTO.setId(snowflake.nextId());
                    tBusCustomerContactDTO.setCustomerId(dto.getId());
                }
                tBusCustomerMapper.insertBatch(contactList);
            }
            count = tBusCustomerMapper.insert(dto);
            // 修改
        } else {

            commonService.delete("T_BUS_CUSTOMER_PROPERTY", "CUSTOMER_ID", StringUtil.getString(dto.getId()));
            tBusCustomerMapper.deleteByCustomerId(dto.getId());
            List<TBusCustomerContactDTO> contactList = dto.getContactList();
            if(!CollectionUtils.isEmpty(contactList)){
                for (TBusCustomerContactDTO tBusCustomerContactDTO : contactList) {
                    tBusCustomerContactDTO.setId(snowflake.nextId());
                    tBusCustomerContactDTO.setCustomerId(dto.getId());
                }
                tBusCustomerMapper.insertBatch(contactList);
            }
            count =  tBusCustomerMapper.update(dto);
        }

        // 客户属性
        if (dto.getPropertyList() != null && dto.getPropertyList().size() > 0) {
            for (TBusCustomerPropertyDTO property : dto.getPropertyList()) {
                property.setId(snowflake.nextId());
                property.setCustomerId(dto.getId());
                tBusCustomerPropertyMapper.insert(property);
            }
        }
        dto.setUpdateBy(securityUtils.getLoginUserId());
        dto.setUpdateByName(securityUtils.getLoginUserName());
        dto.setUpdateTime(new Date());
        // 先删除
        sysFileMapper.deleteRelationByBusinessId(dto.getId());

        // 再插入
        // 营业执照
        if (dto.getLicenseFileIds() != null && dto.getLicenseFileIds().size() > 0) {
            for (Long fileId : dto.getLicenseFileIds()) {
                sysFileMapper.insertFileBusiness(fileId, dto.getId());
            }
        }
        // 授权书
        if (dto.getAuthorizationFileIds() != null && dto.getAuthorizationFileIds().size() > 0) {
            for (Long fileId : dto.getAuthorizationFileIds()) {
                sysFileMapper.insertFileBusiness(fileId, dto.getId());
            }
        }

        if (dto.getBillingFileIds() != null && dto.getBillingFileIds().size() > 0) {
            for (Long fileId : dto.getBillingFileIds()) {
                sysFileMapper.insertFileBusiness(fileId, dto.getId());
            }
        }

        return count > 0;

    }

    /**
     * 驳回
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doReject(TBusCustomerDTO dto) {
        dto.setStatus("9");
        dto.setApprovalBy(securityUtils.getLoginUserId());
        dto.setApprovalName(securityUtils.getLoginUserName());
        dto.setApprovalTime(new Date());
        return tBusCustomerMapper.reject(dto) == 1;
    }

    /**
     * 删除
     *
     * @param  id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {

        int count  = tBusCustomerMapper.getCargoInfoById(id);
        if(count>1){
            throw new BusinessRuntimeException("客户已存在票货，无法删除");
        }

        tBusCustomerMapper.deleteByCustomerId(id);

        commonService.delete("T_BUS_CUSTOMER_PROPERTY", "CUSTOMER_ID", StringUtil.getString(id));

        sysFileService.delete(null, id);

        return tBusCustomerMapper.deleteById(id) == 1;

    }

    /**
     * 审核
     * @param id
     * @return
     */
    @Override
    public boolean approveById(Long id) {
        TBusCustomerDTO dto = tBusCustomerMapper.getById(id);
        if("0".equals(dto.getStatus())){
            throw new BusinessRuntimeException("停用状态，不可审核");
        }
        if("9".equals(dto.getStatus())){
            throw new BusinessRuntimeException("驳回状态，不可审核");
        }
        if("10".equals(dto.getStatus())){
            throw new BusinessRuntimeException("已审核，不能重复审核!");
        }

        TBusCustomerDTO tmpDto = new TBusCustomerDTO();
        tmpDto.setStatus("10");
        tmpDto.setId(id);
        tmpDto.setApprovalBy(securityUtils.getLoginUserId());
        tmpDto.setApprovalName(securityUtils.getLoginUserName());
        tmpDto.setApprovalTime(new Date());
        return  tBusCustomerMapper.approveCancelById(tmpDto) == 1;
    }

    /**
     * 审核
     * @param id
     * @return
     */
    @Override
    public boolean cancelById(Long id) {
        TBusCustomerDTO dto = tBusCustomerMapper.getById(id);
        if(!"10".equals(dto.getStatus())){
            throw new BusinessRuntimeException("不是审核状态，不能消审!");
        }

        TBusCustomerDTO tmpDto = new TBusCustomerDTO();
        tmpDto.setId(id);
        tmpDto.setStatus("1");
        return  tBusCustomerMapper.approveCancelById(tmpDto) == 1;
    }

    @Override
    public boolean doCredit(TBusCustomerDTO dto) {

        if(dto==null){
            throw new BusinessRuntimeException("发送的数据为空");
        }
        if(dto.getId()==null){
            throw new BusinessRuntimeException("ID不能为空");
        }
        if(dto.getIsCredit()==null){
            throw new BusinessRuntimeException("授信不能为空");
        }
        TBusCustomerDTO tmpDto = tBusCustomerMapper.getById(dto.getId());
        if(tmpDto==null){
            throw new BusinessRuntimeException("授信异常，未查询到数据");
        }
        if(tmpDto.getIsCredit()!=null){
            if(tmpDto.getIsCredit().compareTo(dto.getIsCredit())==0){
                throw new BusinessRuntimeException("授信异常，授信状态重复，请刷新重试");
            }
        }
        return tBusCustomerMapper.doCredit(dto)==1;
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sync(List<TBusCustomerDTO> list) {

        list.forEach(dto->{
            // 客户id重复
            commonService.isRepeat("T_BUS_CUSTOMER", "ID", String.valueOf(dto.getId()), StringUtil.getString(dto.getId()), "客户信息", null);
            // 客户代码重复
            commonService.isRepeat("T_BUS_CUSTOMER", "CUSTOMER_CODE", dto.getCustomerCode(), StringUtil.getString(dto.getId()), "客户代码", null);
            // 客户名称重复
            commonService.isRepeat("T_BUS_CUSTOMER", "CUSTOMER_NAME", dto.getCustomerName(), StringUtil.getString(dto.getId()), "客户名称", null);
            // 客户简称重复
            commonService.isRepeat("T_BUS_CUSTOMER", "CUSTOMER_SHORT_NAME", dto.getCustomerShortName(), StringUtil.getString(dto.getId()), "客户简称", null);
            // 助记码
            //如果助记码为空，则自动生成。
            if (StringUtils.isEmpty(dto.getShorthandCode())) {
                dto.setShorthandCode(PinYin4jUtils.getPinYinHeadChar(dto.getCustomerName(), dto.getCustomerName().length()));
            }

            // 客户属性
            if (dto.getPropertyList() != null && dto.getPropertyList().size() > 0) {
                for (TBusCustomerPropertyDTO property : dto.getPropertyList()) {
                    tBusCustomerPropertyMapper.insert(property);
                }
            }

            // 客户属性
            String customerPropertyNames = "";
            if (dto.getPropertyList() != null && dto.getPropertyList().size() > 0) {
                for (TBusCustomerPropertyDTO property : dto.getPropertyList()) {
                    customerPropertyNames += property.getCustomerPropertyName() + "、";
                }
                customerPropertyNames = customerPropertyNames.substring(0, customerPropertyNames.length() - 1);
            }
            dto.setCustomerPropertyNames(customerPropertyNames);

            tBusCustomerMapper.insert(dto);

        });
        return true;
    }

}

