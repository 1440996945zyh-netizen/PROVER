package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EMaterialSupplierDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialSupplierSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialSupplierPO;
import com.yy.ppm.equipment.mapper.EMaterialSupplierMapper;
import com.yy.ppm.equipment.service.EMaterialSupplierService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 供应商Service实现
 */
@Service
public class EMaterialSupplierServiceImpl implements EMaterialSupplierService {

    @Resource
    private EMaterialSupplierMapper mapper;

    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<EMaterialSupplierDTO> getList(EMaterialSupplierSearchDTO searchDTO) {
        // 这里要接 Page，不然和 PageHelperUtils.limit 的入参对不上
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    @Override
    public EMaterialSupplierDTO getById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMaterialSupplierDTO dto) {
        normalize(dto);
        validate(dto);

        EMaterialSupplierPO po = new EMaterialSupplierPO();
        BeanUtils.copyProperties(dto, po);

        if (dto.getId() == null) {
            po.setId(snowflake.nextId());
            po.setSupplierCode(generateCode());
            // 当前页面走企业供应商，不单独开放个人类型
            po.setCustomerType("1");
            // 内部建档默认视为已通过认证
            if (isBlank(po.getIsAuth())) {
                po.setIsAuth("1");
            }
            // 新增默认启用
            if (isBlank(po.getStatus())) {
                po.setStatus("2");
            }
            mapper.insert(po);
        } else {
            mapper.update(po);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("请选择一条数据删除");
        }

        int usedCount = mapper.countUsedById(id);
        if (usedCount > 0) {
            throw new BusinessRuntimeException("该供应商已被业务单据引用，不可删除");
        }

        EMaterialSupplierPO po = new EMaterialSupplierPO();
        po.setId(id);
        mapper.deleteById(po);
    }

    /**
     * 保存前先把基础值规整一下
     */
    private void normalize(EMaterialSupplierDTO dto) {
        if (dto == null) {
            return;
        }

        dto.setSupplierName(trim(dto.getSupplierName()));
        dto.setCompanyName(trim(dto.getCompanyName()));
        dto.setCompanyShort(trim(dto.getCompanyShort()));
        dto.setFormerName(trim(dto.getFormerName()));
        dto.setECompanyName(trim(dto.getECompanyName()));
        dto.setIndustryType(trim(dto.getIndustryType()));
        dto.setCountry(trim(dto.getCountry()));
        dto.setProvince(trim(dto.getProvince()));
        dto.setCity(trim(dto.getCity()));
        dto.setRegisteredCurrency(trim(dto.getRegisteredCurrency()));
        dto.setAddress(trim(dto.getAddress()));
        dto.setUniformSocialCreditCode(trim(dto.getUniformSocialCreditCode()));
        dto.setOrganizationCode(trim(dto.getOrganizationCode()));
        dto.setBusinessType(trim(dto.getBusinessType()));
        dto.setDuns(trim(dto.getDuns()));
        dto.setMnemonicCode(trim(dto.getMnemonicCode()));
        dto.setEstiblishTime(trim(dto.getEstiblishTime()));
        dto.setRegisteredCapital(trim(dto.getRegisteredCapital()));
        dto.setLegalPerson(trim(dto.getLegalPerson()));
        dto.setLegalPersonCardNo(trim(dto.getLegalPersonCardNo()));
        dto.setBusinessState(trim(dto.getBusinessState()));
        dto.setBusinessScope(trim(dto.getBusinessScope()));
        dto.setCompanyTel(trim(dto.getCompanyTel()));
        dto.setBusinessAddress(trim(dto.getBusinessAddress()));
        dto.setSupplierPerson(trim(dto.getSupplierPerson()));
        dto.setSupplierPhone(trim(dto.getSupplierPhone()));
        dto.setCompanyInfoRemark(trim(dto.getCompanyInfoRemark()));
        dto.setStatus(trim(dto.getStatus()));

        // 两个名字至少保一个，避免主数据建出来后一边有值一边空着
        if (isBlank(dto.getSupplierName()) && !isBlank(dto.getCompanyName())) {
            dto.setSupplierName(dto.getCompanyName());
        }
        if (isBlank(dto.getCompanyName()) && !isBlank(dto.getSupplierName())) {
            dto.setCompanyName(dto.getSupplierName());
        }
    }

    /**
     * 保存前基础校验
     */
    private void validate(EMaterialSupplierDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("参数不能为空");
        }
        if (isBlank(dto.getSupplierName())) {
            throw new BusinessRuntimeException("供应商名称不能为空");
        }
        if (isBlank(dto.getCompanyName())) {
            throw new BusinessRuntimeException("企业名称不能为空");
        }
        if (isBlank(dto.getUniformSocialCreditCode())) {
            throw new BusinessRuntimeException("统一社会信用代码不能为空");
        }
        if (isBlank(dto.getLegalPerson())) {
            throw new BusinessRuntimeException("法定代表人不能为空");
        }
        if (isBlank(dto.getSupplierPerson())) {
            throw new BusinessRuntimeException("联系人不能为空");
        }
        if (isBlank(dto.getSupplierPhone())) {
            throw new BusinessRuntimeException("联系电话不能为空");
        }
        if (isBlank(dto.getBusinessState())) {
            throw new BusinessRuntimeException("经营状态不能为空");
        }
        if (isBlank(dto.getStatus())) {
            throw new BusinessRuntimeException("状态不能为空");
        }

        int count = mapper.countByNameAndCreditCode(dto.getSupplierName(), dto.getUniformSocialCreditCode(), dto.getId());
        if (count > 0) {
            throw new BusinessRuntimeException("供应商名称和统一社会信用代码已存在");
        }
    }

    /**
     * 供应商编码：GYS-YYYYMMDD-0001
     */
    private String generateCode() {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String maxCode = mapper.selectMaxCodeToday();
        int number = 1;

        if (!isBlank(maxCode)) {
            int idx = maxCode.lastIndexOf('-');
            if (idx > -1 && idx < maxCode.length() - 1) {
                String seqStr = maxCode.substring(idx + 1);
                try {
                    number = Integer.parseInt(seqStr) + 1;
                } catch (NumberFormatException ignore) {
                    number = 1;
                }
            }
        }
        return "GYS-" + date + "-" + String.format("%04d", number);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
