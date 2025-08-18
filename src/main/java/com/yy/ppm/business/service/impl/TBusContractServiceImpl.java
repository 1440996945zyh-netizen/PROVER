package com.yy.ppm.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusContractCompanyDTO;
import com.yy.ppm.business.bean.dto.TBusContractDTO;
import com.yy.ppm.business.bean.dto.TBusContractRateDTO;
import com.yy.ppm.business.bean.dto.TBusContractSearchDTO;
import com.yy.ppm.business.bean.dto.contract.TBusRateDTO;
import com.yy.ppm.business.bean.dto.contract.TBusTrateDTO;
import com.yy.ppm.business.bean.dto.contract.TBusTrateItemDTO;
import com.yy.ppm.business.bean.po.TBusCargoMixDetailPO;
import com.yy.ppm.business.bean.po.TBusContractCustomerPO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.business.mapper.TBusContractCompanyMapper;
import com.yy.ppm.business.mapper.TBusContractMapper;
import com.yy.ppm.business.mapper.TBusContractRateMapper;
import com.yy.ppm.business.service.TBusContractService;
import com.yy.ppm.common.enums.ContractStatusEnum;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.mapper.SysFileMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.master.bean.po.MCargoPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 合同(TBusContract)ServiceImpl
 * @Description
 * @createTime 2023年06月29日 10:48:00
 */
@Service
public class TBusContractServiceImpl implements TBusContractService {

    @Resource
    private TBusContractMapper tBusContractMapper;
    @Resource
    private TBusContractCompanyMapper tBusContractCompanyMapper;
    @Resource
    private TBusContractRateMapper tBusContractRateMapper;
    @Resource
    private CommonMapper commonMapper;
    @Resource
    private CommonService commonService;
    @Resource
    private Snowflake snowflake;
    @Resource
    private SysFileService sysFileService;
    @Resource
    private SysFileMapper fileMapper;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TBusContractDTO> getList(TBusContractSearchDTO searchDTO) {
        Pages<TBusContractDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            Page<TBusContractDTO> page = tBusContractMapper.getList(searchDTO);
            for (TBusContractDTO v1 : page) {
                Stream<String> isSubMatchs = Stream.of(v1.getIsSubMatch0(), v1.getIsSubMatch1(), v1.getIsSubMatch2(), v1.getIsSubMatch3(), v1.getIsSubMatch4()).filter(StringUtils::isNotBlank);
                if (!isSubMatchs.findAny().isPresent()) {
                    break;
                }
                isSubMatchs = Stream.of(v1.getIsSubMatch0(), v1.getIsSubMatch1(), v1.getIsSubMatch2(), v1.getIsSubMatch3(), v1.getIsSubMatch4()).filter(StringUtils::isNotBlank);
                if (isSubMatchs.allMatch("1"::equals)) {
                    v1.setIsSubMatch("1");
                }
            }
            return page;
        });
        return pages;
    }

    /**
     * 根据ParentId查询补充协议
     *
     * @param parentId
     * @return
     */
    @Override
    public List<TBusContractDTO> getListByParentId(Long parentId) {
        return tBusContractMapper.getListByParentId(parentId);
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public TBusContractDTO getDetail(Long id) {
        TBusContractDTO dto = tBusContractMapper.getById(id);

        // 作业公司
        dto.setCompanyList(tBusContractCompanyMapper.getList(id));

        // 费率
        List<TBusContractRateDTO> rateList = tBusContractRateMapper.getList(id);
        dto.setRateList(rateList);

        List<TBusContractRateDTO> baoganRates = rateList.stream()
                .filter(v1 -> "10".equals(v1.getType()) && "港口作业包干费".equals(v1.getRateItemName()))
                .collect(Collectors.toList());
        Map<String, List<TBusContractRateDTO>> groupByCargoCode = baoganRates.stream().collect(Collectors.groupingBy(TBusContractRatePO::getCargoCode));
        List<Long> trateItemIds = groupByCargoCode.values().stream().map(v1 -> v1.get(0).getTrateItemId()).distinct().collect(Collectors.toList());
        List<TBusTrateDTO> trates;
        if (!trateItemIds.isEmpty()) {
            trates = tBusContractMapper.listTrateByTrateItemIds(trateItemIds);
        } else {
            trates = Collections.emptyList();
        }
        groupByCargoCode.forEach((cargoCode, _baoganRates) -> {
            Long trateItemId = _baoganRates.get(0).getTrateItemId();
            if (trateItemId != null) {
                TBusTrateDTO trate = trates.stream().filter(v1 -> v1.getItems().stream().map(TBusTrateItemDTO::getId).anyMatch(trateItemId::equals)).findFirst().orElse(null);
                _baoganRates.forEach(v1 -> {
                    v1.setTrate(trate);
                });
            }
        });

        // 从费率中获取货名
        List<TBusContractRateDTO> cargoRates = rateList.stream().filter(v1 -> "10".equals(v1.getType())).collect(Collectors.toList());
        List<String> cargoCodes = cargoRates.stream().map(TBusContractRatePO::getCargoCode).distinct().collect(Collectors.toList());
        dto.setCargoCodes(cargoCodes);

        // 关联客户
        List<TBusContractCustomerPO> customers = tBusContractMapper.listContractCustomer(id);
        dto.setCustomers(customers);

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
    public boolean doSave(TBusContractDTO dto) {

        // 合同编号重复
        commonService.isRepeate("T_BUS_CONTRACT", "CONTACT_NO", dto.getContactNo(), StringUtil.getString(dto.getId()), "合同编号", null);

        if (dto.getCompanyList() == null || dto.getCompanyList().size() == 0) {
            throw new BusinessRuntimeException("合同必须有作业公司信息");
        }

        if (dto.getRateList() == null || dto.getRateList().size() == 0) {
            throw new BusinessRuntimeException("合同必须有费率信息");
        }


        // TODO 其他验证

        int count = 0;

        // 记录作业公司名称 用于列表显示和搜索条件查询
        dto.setCompanyNames(dto.getCompanyList().stream().map(TBusContractCompanyDTO::getCompanyName).collect(Collectors.joining("、")));

        // 新增
        if (dto.getId() == null) {
            // 补充协议必须在父合同生效的基础上
            if (null != dto.getParentId()) {
                TBusContractDTO contractDTO = tBusContractMapper.getById(dto.getParentId());
                if (!ContractStatusEnum.生效.getCode().equals(contractDTO.getStatus())) {
//                    throw new BusinessRuntimeException("原合同不是生效状态，不能添加补充协议");
                }
                if (!StringUtil.isEmpty(contractDTO.getParentId())) {
                    throw new BusinessRuntimeException("只能在原合同上添加补充协议");
                }
            }

            dto.setId(snowflake.nextId());
            count = tBusContractMapper.insert(dto);

            // 费率保存
            for (TBusContractRateDTO rate : dto.getRateList()) {
                // 新增费率
                rate.setId(snowflake.nextId());
                rate.setContractId(dto.getId());
                tBusContractRateMapper.insert(rate);
            }
        } else {
            // 修改
            TBusContractDTO oldData = tBusContractMapper.getById(dto.getId());

            if (oldData == null) {
                throw new BusinessRuntimeException("合同信息不存在~");
            }

            // 先删除后修改
            commonMapper.delete("T_BUS_CONTRACT_COMPANY", "CONTRACT_ID", StringUtil.getString(dto.getId()));

            // 删除关联客户
            commonMapper.delete("T_BUS_CONTRACT_CUSTOMER", "CONTRACT_ID", StringUtil.getString(dto.getId()));

            // 保存合同
            count = tBusContractMapper.update(dto);

            if (!ContractStatusEnum.生效.getCode().equals(oldData.getStatus())) {
                List<TBusContractRateDTO> toBeInsertedContractRates = dto.getRateList().stream().filter(v1 -> v1.getId() == null).collect(Collectors.toList());
                toBeInsertedContractRates.forEach(rate -> {
                    rate.setId(snowflake.nextId());
                    rate.setContractId(dto.getId());
                    tBusContractRateMapper.insert(rate);
                });

                List<TBusContractRateDTO> toBeUpdatedContractRates = dto.getRateList().stream().filter(v1 -> v1.getId() != null).collect(Collectors.toList());
                toBeUpdatedContractRates.forEach(v1 -> {
                    tBusContractRateMapper.update(v1);
                });

                List<TBusContractRatePO> contractRates = tBusContractMapper.listContractRate(dto.getId());
                List<TBusContractRatePO> toBeDeletedContractRates = contractRates.stream().filter(v1 -> dto.getRateList().stream().noneMatch(v2 -> v1.getId().equals(v2.getId()))).collect(Collectors.toList());
                if (!toBeDeletedContractRates.isEmpty()) {
                    toBeDeletedContractRates.forEach(v1 -> {
                        commonMapper.delete("T_BUS_CONTRACT_RATE", "ID", StringUtil.getString(v1.getId()));
                    });
                }
            }
        }

        // 作业公司
        for (TBusContractCompanyDTO company : dto.getCompanyList()) {
            company.setId(snowflake.nextId());
            company.setContractId(dto.getId());
            tBusContractCompanyMapper.insert(company);
        }

        // 关联客户
        if (dto.getCustomers() != null) {
            for (TBusContractCustomerPO customer : dto.getCustomers()) {
                customer.setId(snowflake.nextId());
                customer.setContractId(dto.getId());
                tBusContractMapper.insertContractCustomer(customer);
            }
        }

        // 附件信息
//        sysFileService.saveFileBusRelation(dto.getFileIds(), dto.getId());
        fileMapper.deleteRelationByBusinessId(dto.getId());
        if(CollectionUtil.isNotEmpty(dto.getFileIds())){
            for (Long fileId : dto.getFileIds()) {
                fileMapper.insertFileBusiness(fileId,dto.getId());
            }
        }
        if(CollectionUtil.isNotEmpty(dto.getFile02Ids())){
            for (Long file02Id : dto.getFile02Ids()) {
                fileMapper.insertFileBusiness(file02Id,dto.getId());
            }
        }

        return count > 0;

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

        TBusContractDTO oldData = tBusContractMapper.getById(id);

        if (oldData == null) {
            throw new BusinessRuntimeException("合同信息不存在~");
        }

        if (ContractStatusEnum.生效.getCode().equals(oldData.getStatus())) {
            throw new BusinessRuntimeException("生效的合同不能删除~");
        }

        List<TBusContractDTO> listByParentId = tBusContractMapper.getListByParentId(id);
        if (listByParentId.size() > 0) {
            throw new BusinessRuntimeException("有补充协议不能删除~");
        }

        // 删除合同作业公司关系表
        commonMapper.delete("T_BUS_CONTRACT_COMPANY", "CONTRACT_ID", StringUtil.getString(id));
        // 删除费率
        commonMapper.delete("T_BUS_CONTRACT_RATE", "CONTRACT_ID", StringUtil.getString(id));

        // 删除关联客户
        commonMapper.delete("T_BUS_CONTRACT_CUSTOMER", "CONTRACT_ID", StringUtil.getString(id));

        // 删除附件及关系表
        sysFileService.delete(null, oldData.getId());

        return tBusContractMapper.deleteById(id) == 1;

    }

    /**
     * 修改状态, 生效
     *
     * @param tBusContractDTO
     * @return
     */
    @Override
    public boolean updateStatus(TBusContractDTO tBusContractDTO) {
        if (ContractStatusEnum.生效.getCode().equals(tBusContractDTO.getStatus())) {
            TBusContractDTO busContractDTO = tBusContractMapper.getById(tBusContractDTO.getId());
            if (tBusContractDTO.getStatus().equals(busContractDTO.getStatus())) {
                throw new BusinessRuntimeException("已经是生效状态，不能再次生效~");
            }
        }

        return tBusContractMapper.updateStatus(tBusContractDTO);
    }

    public void cancel(Long id) {
        List<TBusCargoMixDetailPO> cargoMixDetails = tBusContractMapper.listCargoMixDetail(id);
        if (!cargoMixDetails.isEmpty()) {
            throw new BusinessRuntimeException("当前合同已被混配票货关联，无法撤销发布");
        }
        boolean isUsedByStatement = tBusContractMapper.isUsedByStatement(id);
        if (isUsedByStatement) {
            throw new BusinessRuntimeException("当前合同已关联结算单，无法撤销发布");
        }
        boolean isUsedByStorageSettle = tBusContractMapper.isUsedByStorageSettle(id);
        if (isUsedByStorageSettle) {
            throw new BusinessRuntimeException("当前合同已关联堆存费结算，无法撤销发布");
        }

        tBusContractMapper.cancel(id);
    }

    @Override
    public List<Map<String, Object>> listCargoRate(Date startTime, List<String> cargoCodes) {
        List<MCargoPO> cargos = tBusContractMapper.listCargo(cargoCodes);
        List<TBusRateDTO> cRates = tBusContractMapper.listCargoRate(startTime, cargoCodes);
        List<TBusRatePO> dRates = tBusContractMapper.listDuicunRate(startTime);
        List<TBusRateDTO> tempDRates = cargos.stream()
                .filter(v1 -> dRates.stream().anyMatch(v2 -> Optional.ofNullable(v1.getWorkType()).orElseThrow(()->
                     new BusinessRuntimeException(v1.getCargoName()+"没有维护作业模式")
                ).equals(v2.getWorkType())))
                .map(v1 -> {
                    TBusRateDTO rate = new TBusRateDTO();
                    TBusRatePO tempRate = dRates.stream().filter(v2 -> v1.getWorkType().equals(v2.getWorkType())).findFirst().orElseThrow(null);
                    BeanUtils.copyProperties(tempRate, rate);
                    rate.setCargoCode(v1.getCargoCode());
                    rate.setCargoName(v1.getCargoName());
                    return rate;
                })
                .collect(Collectors.toList());
        List<TBusRateDTO> rates = Stream.of(cRates, tempDRates).flatMap(Collection::stream).collect(Collectors.toList());

        return rates.stream()
                .map(v1 -> new HashMap<String, Object>() {{
                    put("cargoCode", v1.getCargoCode());
                    put("cargoName", v1.getCargoName());
                    put("rateItemCode", v1.getRateItemCode());
                    put("rateItemName", v1.getRateItemName());
                    put("serviceContentId", v1.getServiceContentId());
                    put("serviceContentName", v1.getServiceContentName());
                    put("tradeType", v1.getInteFore());
                    put("rate", v1.getRate());
                    put("unitCode", v1.getMeasurementUnitCode1());
                    put("unitName", v1.getMeasurementUnitName1());
                    put("tax", v1.getTaxRate());
                    put("rateId", v1.getId());
                    put("freeStorageDays", v1.getFreeStorageDays());
                    put("impExp", v1.getImpExp());
                }})
                .collect(Collectors.toList());
    }

    @Override
    public List<TBusTrateDTO> matchTrate(String contractNo, Long customerId, Date startTime, Date endTime, String cargoCode) {
        return tBusContractMapper.listTrate(contractNo, customerId, startTime, endTime, cargoCode);
    }
}

