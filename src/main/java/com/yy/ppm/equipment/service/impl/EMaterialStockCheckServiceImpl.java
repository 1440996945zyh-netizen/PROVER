package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.service.impl.CommonServiceImpl;
import com.yy.ppm.equipment.bean.dto.*;
import com.yy.ppm.equipment.bean.po.*;
import com.yy.ppm.equipment.mapper.*;
import com.yy.ppm.equipment.service.EMaterialStockCheckService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物资库存盘点Service业务层处理
 * @author system
 */
@Service
public class EMaterialStockCheckServiceImpl implements EMaterialStockCheckService {

    @Resource
    private EMaterialStockCheckMapper mapper;

    @Resource
    private EMaterialStockMapper stockMapper;

    @Resource
    private EMaterialWarehouseInMapper warehouseInMapper;

    @Resource
    private EMaterialWarehouseInDetailMapper warehouseInDetailMapper;

    @Resource
    private EMaterialWarehouseMapper materialWarehouseMapper;

    @Resource
    private EMaterialWarehouseOutMapper warehouseOutMapper;

    @Resource
    private EMaterialWarehouseOutDetailMapper warehouseOutDetailMapper;

    @Resource
    private EMaterialWarehouseInOutRelMapper inOutRelMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    @Autowired
    private CommonServiceImpl commonService;

    /**
     * 创建盘点单（根据仓库ID，自动加载该仓库所有物资的账面数量）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public EMaterialStockCheckDTO createCheck(Long warehouseId, Date checkDate, String remark) {
        if (warehouseId == null) {
            throw new BusinessRuntimeException("仓库ID不能为空");
        }
        if (checkDate == null) {
            throw new BusinessRuntimeException("盘点日期不能为空");
        }

        // 查询该仓库的所有物资库存
        List<EMaterialStockDTO> stockList = stockMapper.selectStockListByWarehouseId(warehouseId);
        if (stockList == null || stockList.isEmpty()) {
            throw new BusinessRuntimeException("该仓库暂无库存物资，无法创建盘点单");
        }

        // 生成盘点单号
        String checkNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.STOCK_CHECK);

        // 检查盘点单号是否重复
        int count = mapper.countByCheckNo(checkNo, null);
        if (count > 0) {
            throw new BusinessRuntimeException("盘点单号已存在，请重试");
        }

        // 创建盘点主表
        EMaterialStockCheckPO checkPO = new EMaterialStockCheckPO();
        checkPO.setId(snowflake.nextId());
        checkPO.setCheckNo(checkNo);
        checkPO.setWarehouseId(warehouseId);
        // 从第一条库存记录获取仓库名称
        if (!stockList.isEmpty()) {
            checkPO.setWarehouseName(stockList.get(0).getWarehouseName());
        }
        checkPO.setCheckDate(checkDate);
        checkPO.setCheckStatus(0); // 待盘点
        checkPO.setRemark(remark);
        checkPO.setLoginUserId(securityUtils.getLoginUserId());
        checkPO.setLoginUserName(securityUtils.getLoginUserName());
        mapper.insert(checkPO);

        // 创建盘点明细
        List<EMaterialStockCheckDetailPO> detailPOList = new ArrayList<>();
        for (EMaterialStockDTO stock : stockList) {
            EMaterialStockCheckDetailPO detailPO = new EMaterialStockCheckDetailPO();
            detailPO.setId(snowflake.nextId());
            detailPO.setCheckId(checkPO.getId());
            detailPO.setMaterialId(stock.getMaterialId());
            detailPO.setMaterialName(stock.getMaterialName());
            detailPO.setSpecificationModel(stock.getSpecificationModel());
            detailPO.setUnitCode(stock.getUnitCode());
            detailPO.setUnitName(stock.getUnitName());
            detailPO.setBookQuantity(stock.getStockQuantity()); // 账面数量
            detailPO.setCheckQuantity(null); // 盘点数量为空
            detailPO.setDifferenceQuantity(BigDecimal.ZERO);
            detailPO.setDifferenceType(0); // 无差异
            detailPOList.add(detailPO);
        }

        if (!detailPOList.isEmpty()) {
            mapper.batchInsertDetail(detailPOList);
        }

        // 返回DTO
        EMaterialStockCheckDTO dto = new EMaterialStockCheckDTO();
        BeanUtils.copyProperties(checkPO, dto);
        return dto;
    }

    /**
     * 创建盘点单（根据仓库ID和物资ID，只创建单个物资的盘点明细）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public EMaterialStockCheckDTO createCheckForMaterial(Long warehouseId, Long materialId, Date checkDate, String remark, BigDecimal checkQuantity) {
        if (warehouseId == null) {
            throw new BusinessRuntimeException("仓库ID不能为空");
        }
        if (materialId == null) {
            throw new BusinessRuntimeException("物资ID不能为空");
        }
        if (checkDate == null) {
            throw new BusinessRuntimeException("盘点日期不能为空");
        }

        // 查询该物资的库存信息（使用分页查询，但只取第一条）
        EMaterialStockSearchDTO searchDTO = new EMaterialStockSearchDTO();
        searchDTO.setWarehouseId(warehouseId);
        searchDTO.setMaterialId(materialId);
        searchDTO.setStartPage(1);
        searchDTO.setPageSize(1);
        Pages<EMaterialStockDTO> stockPages = PageHelperUtils.limit(searchDTO, () -> stockMapper.selectStockList(searchDTO));

        if (stockPages == null || stockPages.getPages() == null || stockPages.getPages().isEmpty()) {
            throw new BusinessRuntimeException("该物资在该仓库暂无库存，无法创建盘点单");
        }

        EMaterialStockDTO stock = stockPages.getPages().get(0);

        // 生成盘点单号
        String checkNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.STOCK_CHECK);

        // 检查盘点单号是否重复
        int count = mapper.countByCheckNo(checkNo, null);
        if (count > 0) {
            throw new BusinessRuntimeException("盘点单号已存在，请重试");
        }

        // 创建盘点主表
        EMaterialStockCheckPO checkPO = new EMaterialStockCheckPO();
        checkPO.setId(snowflake.nextId());
        checkPO.setCheckNo(checkNo);
        checkPO.setWarehouseId(warehouseId);
        checkPO.setWarehouseName(stock.getWarehouseName());
        checkPO.setCheckDate(checkDate);
        checkPO.setCheckStatus(0); // 待盘点
        checkPO.setRemark(remark);
        checkPO.setLoginUserId(securityUtils.getLoginUserId());
        checkPO.setLoginUserName(securityUtils.getLoginUserName());
        mapper.insert(checkPO);

        // 创建该物资的盘点明细
        EMaterialStockCheckDetailPO detailPO = new EMaterialStockCheckDetailPO();
        detailPO.setId(snowflake.nextId());
        detailPO.setCheckId(checkPO.getId());
        detailPO.setMaterialId(stock.getMaterialId());
        detailPO.setMaterialName(stock.getMaterialName());
        detailPO.setSpecificationModel(stock.getSpecificationModel());
        detailPO.setUnitCode(stock.getUnitCode());
        detailPO.setUnitName(stock.getUnitName());
        detailPO.setBookQuantity(stock.getStockQuantity()); // 账面数量

        // 如果提供了盘点数量，直接计算差异
        if (checkQuantity != null) {
            detailPO.setCheckQuantity(checkQuantity);
            BigDecimal difference = checkQuantity.subtract(stock.getStockQuantity());
            detailPO.setDifferenceQuantity(difference);
            if (difference.compareTo(BigDecimal.ZERO) > 0) {
                detailPO.setDifferenceType(1); // 盘盈
            } else if (difference.compareTo(BigDecimal.ZERO) < 0) {
                detailPO.setDifferenceType(2); // 盘亏
            } else {
                detailPO.setDifferenceType(0); // 无差异
            }
        } else {
            detailPO.setCheckQuantity(null); // 盘点数量为空
            detailPO.setDifferenceQuantity(BigDecimal.ZERO);
            detailPO.setDifferenceType(0); // 无差异
        }
        detailPO.setRemark(remark);

        List<EMaterialStockCheckDetailPO> detailPOList = new ArrayList<>();
        detailPOList.add(detailPO);
        mapper.batchInsertDetail(detailPOList);

        // 返回DTO
        EMaterialStockCheckDTO dto = new EMaterialStockCheckDTO();
        BeanUtils.copyProperties(checkPO, dto);

        // 将明细PO转换为DTO并设置到返回对象中
        EMaterialStockCheckDetailDTO detailDTO = new EMaterialStockCheckDetailDTO();
        BeanUtils.copyProperties(detailPO, detailDTO);
        List<EMaterialStockCheckDetailDTO> detailDTOList = new ArrayList<>();
        detailDTOList.add(detailDTO);
        dto.setDetailList(detailDTOList);

        return dto;
    }

    /**
     * 查询盘点单列表（分页）
     */
    @Override
    public Pages<EMaterialStockCheckDTO> getCheckList(EMaterialStockCheckSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectCheckList(searchDTO));
    }

    /**
     * 根据ID查询盘点单（包含明细）
     */
    @Override
    public EMaterialStockCheckDTO getCheckById(Long id) {
        EMaterialStockCheckDTO dto = mapper.selectById(id);
        if (dto != null) {
            List<EMaterialStockCheckDetailDTO> detailList = mapper.selectDetailListByCheckId(id);
            dto.setDetailList(detailList);
        }
        return dto;
    }

    /**
     * 查询盘点明细列表（根据盘点单ID）
     */
    @Override
    public List<EMaterialStockCheckDetailDTO> getCheckDetailList(Long checkId) {
        return mapper.selectDetailListByCheckId(checkId);
    }

    /**
     * 更新盘点数量（单条明细）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void updateCheckQuantity(Long checkId, List<EMaterialStockCheckDetailDTO> detailList) {
        if (checkId == null) {
            throw new BusinessRuntimeException("盘点单ID不能为空");
        }
        if (detailList == null || detailList.isEmpty()) {
            throw new BusinessRuntimeException("盘点明细不能为空");
        }
        if (detailList.size() > 1) {
            throw new BusinessRuntimeException("一次只能更新一条盘点明细");
        }

        EMaterialStockCheckDetailDTO detailDTO = detailList.get(0);
        if (detailDTO.getId() == null) {
            throw new BusinessRuntimeException("明细ID不能为空");
        }

        // 检查盘点单状态
        EMaterialStockCheckDTO check = mapper.selectById(checkId);
        if (check == null) {
            throw new BusinessRuntimeException("盘点单不存在");
        }
        if (check.getCheckStatus() == 2 || check.getCheckStatus() == 3) {
            throw new BusinessRuntimeException("已完成或已调整的盘点单不能更新盘点数量");
        }

        // 校验盘点时间范围
        validateCheckTimeRange(check);

        // 查询当前明细
        EMaterialStockCheckDetailDTO existingDetail = mapper.selectDetailById(detailDTO.getId());
        if (existingDetail == null) {
            throw new BusinessRuntimeException("盘点明细不存在");
        }
        if (!existingDetail.getCheckId().equals(checkId)) {
            throw new BusinessRuntimeException("明细不属于该盘点单");
        }

        // 查询最新的库存数据作为账面数量
        BigDecimal bookQuantity = BigDecimal.ZERO;
        EMaterialWarehouseInDetailDTO inDetail = warehouseInDetailMapper.selectById(existingDetail.getWarehouseInDetailId());
        if (inDetail != null && inDetail.getRemainingQuantity() != null) {
            bookQuantity = inDetail.getRemainingQuantity();
        }
        // 更新当前明细
        EMaterialStockCheckDetailPO detailPO = new EMaterialStockCheckDetailPO();
        detailPO.setId(detailDTO.getId());
        // 保存账面数量（使用从入库明细表查询的最新库存数据）
        detailPO.setBookQuantity(bookQuantity);
        // 保存盘点数量
        detailPO.setCheckQuantity(detailDTO.getCheckQuantity());

        // 计算差异数量和差异类型（使用最新的账面数量）
        if (detailDTO.getCheckQuantity() != null) {
            BigDecimal difference = detailDTO.getCheckQuantity().subtract(bookQuantity);
            detailPO.setDifferenceQuantity(difference);
            if (difference.compareTo(BigDecimal.ZERO) > 0) {
                detailPO.setDifferenceType(1); // 盘盈
            } else if (difference.compareTo(BigDecimal.ZERO) < 0) {
                detailPO.setDifferenceType(2); // 盘亏
            } else {
                detailPO.setDifferenceType(0); // 无差异
            }
        } else {
            detailPO.setDifferenceQuantity(BigDecimal.ZERO);
            detailPO.setDifferenceType(0);
        }
        detailPO.setCheckStatus(1);
        // 更新备注（允许空字符串）
        detailPO.setRemark(detailDTO.getRemark());
        // 更新单条明细
        mapper.updateDetail(detailPO);
        refreshMainCheckStatus(checkId);
    }

    /**
     * 完成盘点（计算差异并自动执行出入库调整）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void completeCheck(Long checkId) {
        if (checkId == null) {
            throw new BusinessRuntimeException("盘点单ID不能为空");
        }

        // 检查盘点单状态
        EMaterialStockCheckDTO check = mapper.selectById(checkId);
        if (check == null) {
            throw new BusinessRuntimeException("盘点单不存在");
        }

        // 查询盘点明细
        List<EMaterialStockCheckDetailDTO> detailList = mapper.selectDetailListByCheckId(checkId);

        // 调用内部方法完成盘点
        completeCheckInternal(check, detailList);
    }

    /**
     * 完成盘点内部方法（使用已有的check对象和明细列表）
     */
    private void completeCheckInternal(EMaterialStockCheckDTO check, List<EMaterialStockCheckDetailDTO> detailList) {
        if (check == null) {
            throw new BusinessRuntimeException("盘点单不能为空");
        }

        // 检查是否都已填写盘点数量
        for (EMaterialStockCheckDetailDTO detail : detailList) {
            if (detail.getCheckQuantity() == null) {
                throw new BusinessRuntimeException(
                    String.format("物资【%s】的盘点数量未填写，无法完成盘点", detail.getMaterialName()));
            }
        }

        // 自动执行盘点调整（生成出入库单）
        // 按差异类型分组处理
        List<EMaterialStockCheckDetailDTO> profitList = new ArrayList<>(); // 盘盈
        List<EMaterialStockCheckDetailDTO> lossList = new ArrayList<>();    // 盘亏

        for (EMaterialStockCheckDetailDTO detail : detailList) {
            if (detail.getDifferenceQuantity() != null) {
                if (detail.getDifferenceQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    profitList.add(detail); // 盘盈
                } else if (detail.getDifferenceQuantity().compareTo(BigDecimal.ZERO) < 0) {
                    lossList.add(detail);   // 盘亏
                }
            }
        }

        // 处理盘盈：生成盘点入库单
        if (!profitList.isEmpty()) {
            createCheckInOrder(check, profitList, null);
        }

        // 处理盘亏：生成盘点出库单
        if (!lossList.isEmpty()) {
            createCheckOutOrder(check, lossList, null);
        }

        // 更新盘点单状态为已调整
        EMaterialStockCheckPO updatePO = new EMaterialStockCheckPO();
        updatePO.setId(check.getId());
        updatePO.setCheckStatus(3); // 已调整（直接设为已调整，因为已经生成了出入库单）
        updatePO.setLoginUserId(securityUtils.getLoginUserId());
        updatePO.setLoginUserName(securityUtils.getLoginUserName());
        mapper.update(updatePO);
    }

    /**
     * 盘点调整（更新库存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void adjustCheck(Long checkId, String remark) {
        if (checkId == null) {
            throw new BusinessRuntimeException("盘点单ID不能为空");
        }

        // 检查盘点单状态
        EMaterialStockCheckDTO check = mapper.selectById(checkId);
        if (check == null) {
            throw new BusinessRuntimeException("盘点单不存在");
        }
        if (check.getCheckStatus() != 2) {
            throw new BusinessRuntimeException("只有已完成的盘点单才能进行调整");
        }

        // 查询盘点明细
        List<EMaterialStockCheckDetailDTO> detailList = mapper.selectDetailListByCheckId(checkId);
        if (detailList == null || detailList.isEmpty()) {
            throw new BusinessRuntimeException("盘点明细为空，无法进行调整");
        }

        // 按差异类型分组处理
        List<EMaterialStockCheckDetailDTO> profitList = new ArrayList<>(); // 盘盈
        List<EMaterialStockCheckDetailDTO> lossList = new ArrayList<>();    // 盘亏

        for (EMaterialStockCheckDetailDTO detail : detailList) {
            if (detail.getDifferenceQuantity() != null) {
                if (detail.getDifferenceQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    profitList.add(detail); // 盘盈
                } else if (detail.getDifferenceQuantity().compareTo(BigDecimal.ZERO) < 0) {
                    lossList.add(detail);   // 盘亏
                }
            }
        }

        // 处理盘盈：生成盘点入库单
        if (!profitList.isEmpty()) {
            createCheckInOrder(check, profitList, remark);
        }

        // 处理盘亏：生成盘点出库单
        if (!lossList.isEmpty()) {
            createCheckOutOrder(check, lossList, remark);
        }

        // 更新盘点单状态为已调整
        EMaterialStockCheckPO updatePO = new EMaterialStockCheckPO();
        updatePO.setId(checkId);
        updatePO.setCheckStatus(3); // 已调整
        if (remark != null) {
            updatePO.setRemark(remark);
        }
        updatePO.setLoginUserId(securityUtils.getLoginUserId());
        updatePO.setLoginUserName(securityUtils.getLoginUserName());
        mapper.update(updatePO);
    }

    /**
     * 创建盘点入库单（盘盈）
     */
    private void createCheckInOrder(EMaterialStockCheckDTO check,
                                    List<EMaterialStockCheckDetailDTO> profitList,
                                    String remark) {
        // 创建入库主表
        EMaterialWarehouseInPO inPO = new EMaterialWarehouseInPO();
        inPO.setId(snowflake.nextId());
        String warehouseInNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.WAREHOUSE_IN);
        inPO.setWarehouseInNo(warehouseInNo);
        // 格式化日期为yyyyMMdd格式（如20260105）
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        inPO.setWarehouseInTitle("盘点入库" + dateStr);
        inPO.setWarehouseId(check.getWarehouseId());
        inPO.setWarehouseName(check.getWarehouseName());
        inPO.setWarehouseInDate(new Date());
        inPO.setWarehouseInTypeCode("06"); // 盘点入库类型编码
        inPO.setWarehouseInTypeName("盘点入库");
        inPO.setAcceptanceStatus(1); // 直接设为已验收
        inPO.setRemarks(remark);
        inPO.setCheckId(check.getId()); // 关联盘点单ID
        UserInfo userInfo = securityUtils.getUserInfo();
        if (userInfo != null) {
            inPO.setDeptId(userInfo.getDeptId());
            inPO.setDeptName(userInfo.getDeptName());
        }

        // 创建入库明细
        List<EMaterialWarehouseInDetailPO> detailList = new ArrayList<>();
        for (EMaterialStockCheckDetailDTO checkDetail : profitList) {
            EMaterialWarehouseInDetailPO detailPO = new EMaterialWarehouseInDetailPO();
            detailPO.setId(snowflake.nextId());
            detailPO.setWarehouseInId(inPO.getId());
            detailPO.setMaterialId(checkDetail.getMaterialId());
            detailPO.setMaterialName(checkDetail.getMaterialName());
            detailPO.setWarehouseInQuantity(checkDetail.getDifferenceQuantity()); // 盘盈数量
            detailPO.setRemainingQuantity(checkDetail.getDifferenceQuantity());    // 剩余数量 = 盘盈数量
            detailPO.setOutQuantity(BigDecimal.ZERO);

            // 从入库明细关联主表查询完整信息
            EMaterialWarehouseInDetailDTO inDetail = warehouseInDetailMapper.selectById(checkDetail.getWarehouseInDetailId());

            // 使用入库明细的规格型号（优先使用入库明细的，如果为空则使用盘点明细的）
            detailPO.setSpecification(inDetail.getSpecification() != null && !inDetail.getSpecification().trim().isEmpty()
                ? inDetail.getSpecification() : checkDetail.getSpecificationModel());
            detailPO.setSpecificationDesc(inDetail.getSpecificationDesc() != null && !inDetail.getSpecificationDesc().trim().isEmpty()
                ? inDetail.getSpecificationDesc() : (checkDetail.getSpecificationModel() != null ? checkDetail.getSpecificationModel() : inDetail.getSpecification()));
            // 使用入库明细的计量单位
            detailPO.setUnit(inDetail.getUnit() != null && !inDetail.getUnit().trim().isEmpty()
                ? inDetail.getUnit() : checkDetail.getUnitName());
            detailPO.setUnitCode(inDetail.getUnitCode() != null && !inDetail.getUnitCode().trim().isEmpty()
                ? inDetail.getUnitCode() : checkDetail.getUnitCode());
            // 使用入库明细的质保到期时间
            detailPO.setWarrantyExpiryDate(inDetail.getWarrantyExpiryDate());
            // 使用入库明细的采购员信息
            detailPO.setPurchaserId(inDetail.getPurchaserId());
            detailPO.setPurchaserName(inDetail.getPurchaserName());

            // 使用入库明细的品牌
            detailPO.setBrand(inDetail.getBrand());
            detailPO.setApplicationDeptId(inDetail.getApplicationDeptId());
            detailPO.setApplicationDeptName(inDetail.getApplicationDeptName());
            // 使用入库主表的供应商信息（通过关联查询已获取）
            inPO.setSupplierId(inDetail.getSupplierId());
            inPO.setSupplierName(inDetail.getSupplierName());
            inPO.setBatchNo(inDetail.getBatchNo());
            // 使用入库明细的含税单价
            if (inDetail.getTaxIncludedUnitPrice() != null && inDetail.getTaxIncludedUnitPrice().compareTo(BigDecimal.ZERO) > 0) {
                detailPO.setTaxIncludedUnitPrice(inDetail.getTaxIncludedUnitPrice());
            } else {
                // 如果没有价格，查询最近一次入库的含税单价
                BigDecimal taxIncludedUnitPrice = warehouseInDetailMapper.selectLatestTaxIncludedUnitPrice(
                    checkDetail.getMaterialId(),
                    check.getWarehouseId()
                );
                detailPO.setTaxIncludedUnitPrice(taxIncludedUnitPrice != null && taxIncludedUnitPrice.compareTo(BigDecimal.ZERO) > 0
                    ? taxIncludedUnitPrice : BigDecimal.ZERO);
            }

            // 计算含税金额
            BigDecimal taxIncludedAmount = detailPO.getTaxIncludedUnitPrice().multiply(checkDetail.getDifferenceQuantity());
            detailPO.setTaxIncludedAmount(taxIncludedAmount);

            detailList.add(detailPO);
        }
        warehouseInMapper.insert(inPO);
        if (!detailList.isEmpty()) {
            warehouseInDetailMapper.batchInsert(detailList);
        }
    }

    /**
     * 创建盘点出库单（盘亏）
     */
    private void createCheckOutOrder(EMaterialStockCheckDTO check,
                                     List<EMaterialStockCheckDetailDTO> lossList,
                                     String remark) {
        // 创建出库主表
        EMaterialWarehouseOutPO outPO = new EMaterialWarehouseOutPO();
        outPO.setId(snowflake.nextId());
        String warehouseOutNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.WAREHOUSE_OUT);
        outPO.setWarehouseOutNo(warehouseOutNo);
        // 格式化日期为yyyyMMdd格式（如20260105）
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(check.getCheckDate() != null ? check.getCheckDate() : new Date());
        outPO.setWarehouseOutTitle("盘点出库" + dateStr);
        outPO.setWarehouseId(check.getWarehouseId());
        outPO.setWarehouseName(check.getWarehouseName());
        outPO.setStatus(1); // 直接设为已确认
        outPO.setWarehouseOutTypeCode("02");
        outPO.setWarehouseOutTypeName("盘点出库");
        outPO.setCheckId(check.getId()); // 关联盘点单ID
        outPO.setLoginUserId(securityUtils.getLoginUserId());
        outPO.setLoginUserName(securityUtils.getLoginUserName());

        UserInfo userInfo = securityUtils.getUserInfo();
        if (userInfo != null) {
            outPO.setDeptId(userInfo.getDeptId());
            outPO.setDeptName(userInfo.getDeptName());
        }

        // 创建出库明细
        List<EMaterialWarehouseOutDetailPO> outDetailList = new ArrayList<>();
        for (EMaterialStockCheckDetailDTO checkDetail : lossList) {
            EMaterialWarehouseOutDetailPO outDetailPO = new EMaterialWarehouseOutDetailPO();
            outDetailPO.setId(snowflake.nextId());
            outDetailPO.setWarehouseOutId(outPO.getId());
            outDetailPO.setMaterialId(checkDetail.getMaterialId());
            outDetailPO.setMaterialName(checkDetail.getMaterialName());
            outDetailPO.setOutQuantity(checkDetail.getDifferenceQuantity().abs()); // 盘亏数量（绝对值）

            // 根据盘点明细中的入库明细ID，查询入库明细的规格型号和计量单位
            if (checkDetail.getWarehouseInDetailId() != null) {
                EMaterialWarehouseInDetailDTO inDetail = warehouseInDetailMapper.selectById(checkDetail.getWarehouseInDetailId());
                if (inDetail != null) {
                    // 使用入库明细的规格型号（优先使用入库明细的，如果为空则使用盘点明细的）
                    outDetailPO.setSpecificationModel(inDetail.getSpecification() != null && !inDetail.getSpecification().trim().isEmpty()
                        ? inDetail.getSpecification() : checkDetail.getSpecificationModel());
                    // 使用入库明细的计量单位
                    outDetailPO.setUnitCode(inDetail.getUnitCode() != null && !inDetail.getUnitCode().trim().isEmpty()
                        ? inDetail.getUnitCode() : checkDetail.getUnitCode());
                    outDetailPO.setUnitName(inDetail.getUnit() != null && !inDetail.getUnit().trim().isEmpty()
                        ? inDetail.getUnit() : checkDetail.getUnitName());
                    // 使用入库明细的品牌
                    outDetailPO.setBrand(inDetail.getBrand());
                } else {
                    // 如果查询不到入库明细，使用盘点明细的信息
                    outDetailPO.setSpecificationModel(checkDetail.getSpecificationModel());
                    outDetailPO.setUnitCode(checkDetail.getUnitCode());
                    outDetailPO.setUnitName(checkDetail.getUnitName());
                }
            } else {
                // 如果没有关联入库明细，使用盘点明细的信息
                outDetailPO.setSpecificationModel(checkDetail.getSpecificationModel());
                outDetailPO.setUnitCode(checkDetail.getUnitCode());
                outDetailPO.setUnitName(checkDetail.getUnitName());
            }

            outDetailPO.setLoginUserId(securityUtils.getLoginUserId());
            outDetailPO.setLoginUserName(securityUtils.getLoginUserName());
            outDetailList.add(outDetailPO);
        }
        warehouseOutMapper.insert(outPO);
        if (!outDetailList.isEmpty()) {
            warehouseOutDetailMapper.batchInsert(outDetailList);
        }
//         按FIFO原则分配入库明细并更新库存
        for (EMaterialWarehouseOutDetailPO outDetailPO : outDetailList) {
            allocateInDetailsForCheck(check.getWarehouseId(), outDetailPO);
        }
    }

    /**
     * 为盘点出库明细分配入库明细（FIFO原则）并更新库存
     */
    private void allocateInDetailsForCheck(Long warehouseId, EMaterialWarehouseOutDetailPO outDetailPO) {
        // 查询可用的入库明细（按创建时间升序，FIFO）
        List<EMaterialWarehouseInDetailDTO> availableInDetails =
            warehouseInDetailMapper.selectAvailableInDetails(outDetailPO.getMaterialId(), warehouseId);

        BigDecimal remainingOutQuantity = outDetailPO.getOutQuantity();
        List<EMaterialWarehouseInOutRelPO> relList = new ArrayList<>();
        List<EMaterialWarehouseInDetailPO> updateInDetailList = new ArrayList<>();

        // 按FIFO原则分配
        for (EMaterialWarehouseInDetailDTO inDetail : availableInDetails) {
            if (remainingOutQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal availableQuantity = inDetail.getRemainingQuantity();
            BigDecimal allocateQuantity = remainingOutQuantity.min(availableQuantity);

            // 建立关系记录
            EMaterialWarehouseInOutRelPO relPO = new EMaterialWarehouseInOutRelPO();
            relPO.setId(snowflake.nextId());
            relPO.setWarehouseInDetailId(inDetail.getId());
            relPO.setWarehouseOutDetailId(outDetailPO.getId());
            relPO.setQuantity(allocateQuantity);
            relPO.setLoginUserId(securityUtils.getLoginUserId());
            relPO.setLoginUserName(securityUtils.getLoginUserName());
            relList.add(relPO);

            // 准备更新入库明细
            EMaterialWarehouseInDetailPO updateInDetailPO = new EMaterialWarehouseInDetailPO();
            updateInDetailPO.setId(inDetail.getId());
            updateInDetailPO.setOutQuantity(
                (inDetail.getOutQuantity() != null ? inDetail.getOutQuantity() : BigDecimal.ZERO)
                    .add(allocateQuantity));
            updateInDetailPO.setRemainingQuantity(
                (inDetail.getRemainingQuantity() != null ? inDetail.getRemainingQuantity() : BigDecimal.ZERO)
                    .subtract(allocateQuantity));
            updateInDetailPO.setLoginUserId(securityUtils.getLoginUserId());
            updateInDetailPO.setLoginUserName(securityUtils.getLoginUserName());
            updateInDetailList.add(updateInDetailPO);

            remainingOutQuantity = remainingOutQuantity.subtract(allocateQuantity);
        }

        // 检查是否分配完成
        if (remainingOutQuantity.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuntimeException(
                String.format("物资【%s】库存不足，无法完成盘点调整", outDetailPO.getMaterialName()));
        }

        // 批量插入关系记录
        if (!relList.isEmpty()) {
            inOutRelMapper.batchInsert(relList);
        }

        // 批量更新入库明细数量
        if (!updateInDetailList.isEmpty()) {
            warehouseInDetailMapper.batchUpdateOutQuantity(updateInDetailList);
        }
    }

    /**
     * 删除盘点单（只有待盘点状态才能删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteCheck(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("盘点单ID不能为空");
        }

        // 检查盘点单状态
        EMaterialStockCheckDTO check = mapper.selectById(id);
        if (check == null) {
            throw new BusinessRuntimeException("盘点单不存在");
        }
        if (check.getCheckStatus() != 0) {
            throw new BusinessRuntimeException("只有待盘点状态的盘点单才能删除");
        }

        // 删除盘点明细
        mapper.deleteDetailByCheckId(id);

        // 逻辑删除盘点单
        mapper.deleteById(id);
    }

    /**
     * 快速盘点（创建盘点单、保存盘点数量、完成盘点并生成出入库单）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public EMaterialStockCheckDTO quickCheck(Long warehouseId, Long materialId, Date checkDate, BigDecimal checkQuantity, String remark) {
        if (warehouseId == null) {
            throw new BusinessRuntimeException("仓库ID不能为空");
        }
        if (materialId == null) {
            throw new BusinessRuntimeException("物资ID不能为空");
        }
        if (checkDate == null) {
            throw new BusinessRuntimeException("盘点日期不能为空");
        }
        if (checkQuantity == null) {
            throw new BusinessRuntimeException("盘点数量不能为空");
        }

        // 1. 创建盘点单和明细（在创建时直接设置盘点数量和差异）
        EMaterialStockCheckDTO check = createCheckForMaterial(warehouseId, materialId, checkDate, remark, checkQuantity);
        // 3. 完成盘点（自动生成出入库单，使用已有的check对象和明细列表）
        completeCheckInternal(check, check.getDetailList());
        // 4. 返回最新的盘点单信息
        return mapper.selectById(check.getId());
    }

    /**
     * 创建盘点单（根据仓库ID，查询该仓库所有入库明细，不合并）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public EMaterialStockCheckDTO createCheckByWarehouse(Long warehouseId, Date checkStartDate, Date checkEndDate, String checkTitle, String remark) {
        if (warehouseId == null) {
            throw new BusinessRuntimeException("仓库ID不能为空");
        }
        if (checkStartDate == null) {
            throw new BusinessRuntimeException("盘点开始日期不能为空");
        }
        if (checkEndDate == null) {
            throw new BusinessRuntimeException("盘点结束日期不能为空");
        }
        if (checkStartDate.after(checkEndDate)) {
            throw new BusinessRuntimeException("盘点开始日期不能晚于结束日期");
        }

        // 生成盘点单号
        String checkNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.STOCK_CHECK);

        // 检查盘点单号是否重复
        int count = mapper.countByCheckNo(checkNo, null);
        if (count > 0) {
            throw new BusinessRuntimeException("盘点单号已存在，请重试");
        }

        // 获取仓库名称
        String warehouseName = null;
        EMaterialWarehouseDTO warehouse = materialWarehouseMapper.selectById(warehouseId);
        if (warehouse != null) {
            warehouseName = warehouse.getWarehouseName();
        }

        // 创建盘点主表
        EMaterialStockCheckPO checkPO = new EMaterialStockCheckPO();
        checkPO.setId(snowflake.nextId());
        checkPO.setCheckNo(checkNo);
        checkPO.setWarehouseId(warehouseId);
        checkPO.setWarehouseName(warehouseName);
        checkPO.setCheckDate(checkStartDate); // 保留字段，使用开始日期
        checkPO.setCheckStartDate(checkStartDate);
        checkPO.setCheckEndDate(checkEndDate);
        checkPO.setCheckTitle(checkTitle);
        checkPO.setCheckType(1); // 默认全量盘点
        checkPO.setCheckStatus(0); // 待盘点
        checkPO.setRemark(remark);
        checkPO.setLoginUserId(securityUtils.getLoginUserId());
        checkPO.setLoginUserName(securityUtils.getLoginUserName());
        mapper.insert(checkPO);

        // 直接在SQL中查询入库记录并插入明细表（全量盘点）
        mapper.insertDetailFromInDetailsByWarehouse(checkPO.getId(), warehouseId);

        // 返回DTO
        EMaterialStockCheckDTO dto = new EMaterialStockCheckDTO();
        BeanUtils.copyProperties(checkPO, dto);
        return dto;
    }

    /**
     * 保存盘点单（新增或修改）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void saveCheck(EMaterialStockCheckDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("盘点单信息不能为空");
        }
        if (dto.getWarehouseId() == null) {
            throw new BusinessRuntimeException("仓库ID不能为空");
        }
        if (dto.getCheckStartDate() == null) {
            throw new BusinessRuntimeException("盘点开始日期不能为空");
        }
        if (dto.getCheckEndDate() == null) {
            throw new BusinessRuntimeException("盘点结束日期不能为空");
        }
        if (dto.getCheckStartDate().after(dto.getCheckEndDate())) {
            throw new BusinessRuntimeException("盘点开始日期不能晚于结束日期");
        }
        if (dto.getCheckType() == null) {
            throw new BusinessRuntimeException("盘点类型不能为空");
        }
        if (dto.getCheckType() == 2 && (dto.getMaterialIds() == null || dto.getMaterialIds().isEmpty())) {
            throw new BusinessRuntimeException("部分盘点时，必须选择至少一个物资");
        }

        EMaterialStockCheckPO checkPO = new EMaterialStockCheckPO();
        BeanUtils.copyProperties(dto, checkPO);
        // 新增时，盘点人字段不设置（去掉）

        if (dto.getId() == null) {
            // 新增
            // 生成盘点单号
            String checkNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.STOCK_CHECK);
            // 检查盘点单号是否重复
            int count = mapper.countByCheckNo(checkNo, null);
            if (count > 0) {
                throw new BusinessRuntimeException("盘点单号已存在，请重试");
            }
            checkPO.setId(snowflake.nextId());
            checkPO.setCheckNo(checkNo);
            checkPO.setCheckStatus(0); // 待盘点
            checkPO.setCheckDate(dto.getCheckStartDate()); // 保留字段，使用开始日期
            checkPO.setLoginUserId(securityUtils.getLoginUserId());
            checkPO.setLoginUserName(securityUtils.getLoginUserName());
            mapper.insert(checkPO);

            // 根据盘点类型，直接在SQL中查询入库记录并插入明细表
            if (dto.getCheckType() == 1) {
                // 全量盘点：查询该仓库下所有入库记录(库存大于0的)
                mapper.insertDetailFromInDetailsByWarehouse(checkPO.getId(), dto.getWarehouseId());
            } else if (dto.getCheckType() == 2) {
                // 部分盘点：根据仓库和物资ID列表查询入库记录
                mapper.insertDetailFromInDetailsByWarehouseAndMaterials(checkPO.getId(), dto.getWarehouseId(), dto.getMaterialIds());
            }
        } else {
            // 修改：先删后插
            // 检查盘点单是否存在
            EMaterialStockCheckDTO existingCheck = mapper.selectById(dto.getId());
            if (existingCheck == null) {
                throw new BusinessRuntimeException("盘点单不存在");
            }
            // 只有待盘点状态才能修改
            if (existingCheck.getCheckStatus() != null && existingCheck.getCheckStatus() != 0) {
                throw new BusinessRuntimeException("只有待盘点状态的盘点单才能修改");
            }
            checkPO.setCheckDate(dto.getCheckStartDate()); // 保留字段，使用开始日期
            checkPO.setLoginUserId(securityUtils.getLoginUserId());
            checkPO.setLoginUserName(securityUtils.getLoginUserName());
            mapper.update(checkPO);

            // 删除旧明细
            mapper.deleteDetailByCheckId(dto.getId());

            // 根据盘点类型，直接在SQL中查询入库记录并插入明细表
            if (dto.getCheckType() == 1) {
                // 全量盘点：查询该仓库下所有入库记录(库存大于0的)
                mapper.insertDetailFromInDetailsByWarehouse(checkPO.getId(), dto.getWarehouseId());
            } else if (dto.getCheckType() == 2) {
                // 部分盘点：根据仓库和物资ID列表查询入库记录
                mapper.insertDetailFromInDetailsByWarehouseAndMaterials(checkPO.getId(), dto.getWarehouseId(), dto.getMaterialIds());
            }
        }
    }

    /**
     * 根据仓库ID查询所有入库明细（用于盘点，不合并）
     */
    @Override
    public List<EMaterialWarehouseInDetailDTO> getInDetailsByWarehouseId(Long warehouseId) {
        if (warehouseId == null) {
            throw new BusinessRuntimeException("仓库ID不能为空");
        }
        return warehouseInDetailMapper.selectListByWarehouseId(warehouseId);
    }

    /**
     * 查询盘点明细列表（包含入库明细和入库主表信息）- 分页
     */
    @Override
    public Pages<EMaterialStockCheckDetailWithInDTO> getCheckDetailListWithInInfo(EMaterialStockCheckDetailSearchDTO searchDTO) {
        if (searchDTO.getCheckId() == null) {
            throw new BusinessRuntimeException("盘点单ID不能为空");
        }
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectDetailListWithInInfo(searchDTO));
    }

    /**
     * 撤销盘点明细（状态回退，清空账面数量和盘点数量）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void cancelCheckDetail(Long checkId, Long detailId) {
        if (checkId == null) {
            throw new BusinessRuntimeException("盘点单ID不能为空");
        }
        if (detailId == null) {
            throw new BusinessRuntimeException("明细ID不能为空");
        }

        // 检查盘点单状态
        EMaterialStockCheckDTO check = mapper.selectById(checkId);
        if (check == null) {
            throw new BusinessRuntimeException("盘点单不存在");
        }
        if (check.getCheckStatus() == 2 || check.getCheckStatus() == 3) {
            throw new BusinessRuntimeException("已完成或已调整的盘点单不能撤销明细");
        }

        // 查询明细
        EMaterialStockCheckDetailDTO detail = mapper.selectDetailById(detailId);
        if (detail == null) {
            throw new BusinessRuntimeException("盘点明细不存在");
        }
        if (!detail.getCheckId().equals(checkId)) {
            throw new BusinessRuntimeException("明细不属于该盘点单");
        }
        // 已审核的明细不能撤销（checkStatus = 2 表示已审核）
        if (detail.getCheckStatus() != null && detail.getCheckStatus() == 2) {
            throw new BusinessRuntimeException("已审核的明细不能撤销");
        }
        if (detail.getCheckStatus() == null || detail.getCheckStatus() != 1) {
            throw new BusinessRuntimeException("只有已盘点的明细才能撤销");
        }

        // 撤销明细：状态回退为待盘点，清空账面数量和盘点数量
        EMaterialStockCheckDetailPO updatePO = new EMaterialStockCheckDetailPO();
        updatePO.setId(detailId);
        updatePO.setCheckStatus(0); // 回退为待盘点
        updatePO.setRemark("");
        updatePO.setBookQuantity(BigDecimal.ZERO); // 清空账面数量
        updatePO.setCheckQuantity(BigDecimal.ZERO); // 清空盘点数量
        updatePO.setDifferenceQuantity(BigDecimal.ZERO); // 清空差异数量
        updatePO.setDifferenceType(0); // 清空差异类型
        updatePO.setLoginUserId(securityUtils.getLoginUserId());
        updatePO.setLoginUserName(securityUtils.getLoginUserName());
        mapper.updateDetail(updatePO);
        refreshMainCheckStatus(checkId);
    }

    /**
     * 审核盘点明细（单个明细审核）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void auditCheckDetail(Long checkId, Long detailId) {
        if (checkId == null) {
            throw new BusinessRuntimeException("盘点单ID不能为空");
        }
        if (detailId == null) {
            throw new BusinessRuntimeException("明细ID不能为空");
        }

        // 检查盘点单状态
        EMaterialStockCheckDTO check = mapper.selectById(checkId);
        if (check == null) {
            throw new BusinessRuntimeException("盘点单不存在");
        }
        if (check.getCheckStatus() == 2 || check.getCheckStatus() == 3) {
            throw new BusinessRuntimeException("已完成或已调整的盘点单不能审核");
        }

        // 校验盘点时间范围
        validateCheckTimeRange(check);

        // 查询当前明细
        EMaterialStockCheckDetailDTO detail = mapper.selectDetailById(detailId);
        if (detail == null) {
            throw new BusinessRuntimeException("盘点明细不存在");
        }
        if (!detail.getCheckId().equals(checkId)) {
            throw new BusinessRuntimeException("明细不属于该盘点单");
        }
        if (detail.getCheckStatus() == null || detail.getCheckStatus() != 1) {
            throw new BusinessRuntimeException("只有已盘点的明细才能审核");
        }
        if (detail.getCheckStatus() == 2) {
            throw new BusinessRuntimeException("该明细已经审核，不能重复审核");
        }

        // 更新明细审核信息
        EMaterialStockCheckDetailPO detailPO = new EMaterialStockCheckDetailPO();
        detailPO.setId(detailId);
        detailPO.setCheckStatus(2); // 已审核
        detailPO.setLoginUserId(securityUtils.getLoginUserId());
        detailPO.setLoginUserName(securityUtils.getLoginUserName());
        mapper.updateDetail(detailPO);

        // 重新查询已审核的明细（包含审核信息）
        detail = mapper.selectDetailById(detailId);

        // 如果当前明细有差异，立即生成对应的出入库单
        if (detail.getDifferenceQuantity() != null && detail.getDifferenceQuantity().compareTo(BigDecimal.ZERO) != 0) {
            if (detail.getDifferenceQuantity().compareTo(BigDecimal.ZERO) > 0) {
                // 盘盈：立即生成盘点入库单（单个明细）
                List<EMaterialStockCheckDetailDTO> profitList = new ArrayList<>();
                profitList.add(detail);
                createCheckInOrder(check, profitList, "盘点审核-盘盈");
            } else if (detail.getDifferenceQuantity().compareTo(BigDecimal.ZERO) < 0) {
                // 盘亏：立即生成盘点出库单（单个明细）
                List<EMaterialStockCheckDetailDTO> lossList = new ArrayList<>();
                lossList.add(detail);
                createCheckOutOrder(check, lossList, "盘点审核-盘亏");
            }
        }

        refreshMainCheckStatus(checkId);
    }

    /**
     * 无差异操作（批量）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void noDifference(Long checkId, List<EMaterialStockCheckDetailDTO> detailList) {
        if (checkId == null) {
            throw new BusinessRuntimeException("盘点单ID不能为空");
        }
        if (detailList == null || detailList.isEmpty()) {
            throw new BusinessRuntimeException("盘点明细不能为空");
        }
        if (detailList.size() > 1) {
            throw new BusinessRuntimeException("一次只能更新一条盘点明细");
        }

        EMaterialStockCheckDetailDTO detailDTO = detailList.get(0);
        if (detailDTO.getId() == null) {
            throw new BusinessRuntimeException("明细ID不能为空");
        }

        // 检查盘点单状态
        EMaterialStockCheckDTO check = mapper.selectById(checkId);
        if (check == null) {
            throw new BusinessRuntimeException("盘点单不存在");
        }
        if (check.getCheckStatus() == 2 || check.getCheckStatus() == 3) {
            throw new BusinessRuntimeException("已完成或已调整的盘点单不能进行无差异操作");
        }

        // 校验盘点时间范围
        validateCheckTimeRange(check);

        // 查询当前明细
        EMaterialStockCheckDetailDTO existingDetail = mapper.selectDetailById(detailDTO.getId());
        if (existingDetail == null) {
            throw new BusinessRuntimeException("盘点明细不存在");
        }
        if (!existingDetail.getCheckId().equals(checkId)) {
            throw new BusinessRuntimeException("明细不属于该盘点单");
        }

        // 查询最新的库存数据作为账面数量
        BigDecimal bookQuantity = BigDecimal.ZERO;
        EMaterialWarehouseInDetailDTO inDetail = warehouseInDetailMapper.selectById(existingDetail.getWarehouseInDetailId());
        if (inDetail != null && inDetail.getRemainingQuantity() != null) {
            bookQuantity = inDetail.getRemainingQuantity();
        }

        // 更新当前明细
        EMaterialStockCheckDetailPO detailPO = new EMaterialStockCheckDetailPO();
        detailPO.setId(detailDTO.getId());
        // 保存账面数量（使用从入库明细表查询的最新库存数据）
        detailPO.setBookQuantity(bookQuantity);
        // 保存盘点数量（等于账面数量，无差异）
        detailPO.setCheckQuantity(detailDTO.getCheckQuantity() != null ? detailDTO.getCheckQuantity() : bookQuantity);

        // 无差异：差异数量为0，差异类型为0
        detailPO.setDifferenceQuantity(BigDecimal.ZERO);
        detailPO.setDifferenceType(0); // 无差异
        detailPO.setCheckStatus(2); // 直接进入最终状态
        // 更新备注（允许空字符串）
        detailPO.setRemark(detailDTO.getRemark());
        // 更新单条明细
        mapper.updateDetail(detailPO);
        refreshMainCheckStatus(checkId);
    }

    /**
     * 校验盘点时间范围（包括开始时间和结束时间，只比较年月日）
     * @param check 盘点单
     */
    private void validateCheckTimeRange(EMaterialStockCheckDTO check) {
        Date now = new Date();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");

        // 将当前时间转换为只包含日期的格式（年月日，时分秒设为0）
        java.util.Calendar nowCal = java.util.Calendar.getInstance();
        nowCal.setTime(now);
        nowCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        nowCal.set(java.util.Calendar.MINUTE, 0);
        nowCal.set(java.util.Calendar.SECOND, 0);
        nowCal.set(java.util.Calendar.MILLISECOND, 0);
        Date nowDateOnly = nowCal.getTime();

        // 如果设置了开始时间，当前日期必须大于等于开始日期（包括开始日期）
        if (check.getCheckStartDate() != null) {
            java.util.Calendar startCal = java.util.Calendar.getInstance();
            startCal.setTime(check.getCheckStartDate());
            startCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            startCal.set(java.util.Calendar.MINUTE, 0);
            startCal.set(java.util.Calendar.SECOND, 0);
            startCal.set(java.util.Calendar.MILLISECOND, 0);
            Date startDateOnly = startCal.getTime();

            if (nowDateOnly.getTime() < startDateOnly.getTime()) {
                throw new BusinessRuntimeException("当前时间不在盘点时间范围内，盘点开始时间为：" +
                    displayFormat.format(check.getCheckStartDate()));
            }
        }

        // 如果设置了结束时间，当前日期必须小于等于结束日期（包括结束日期）
        if (check.getCheckEndDate() != null) {
            java.util.Calendar endCal = java.util.Calendar.getInstance();
            endCal.setTime(check.getCheckEndDate());
            endCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            endCal.set(java.util.Calendar.MINUTE, 0);
            endCal.set(java.util.Calendar.SECOND, 0);
            endCal.set(java.util.Calendar.MILLISECOND, 0);
            Date endDateOnly = endCal.getTime();

            if (nowDateOnly.getTime() > endDateOnly.getTime()) {
                throw new BusinessRuntimeException("当前时间不在盘点时间范围内，盘点结束时间为：" +
                    displayFormat.format(check.getCheckEndDate()));
            }
        }
    }

    /**
     * 根据明细状态重新汇总主表状态。
     */
    private void refreshMainCheckStatus(Long checkId) {
        List<EMaterialStockCheckDetailDTO> detailList = mapper.selectDetailListByCheckId(checkId);
        if (detailList == null || detailList.isEmpty()) {
            return;
        }

        boolean allPending = true;
        boolean allCompleted = true;
        boolean hasDifference = false;
        for (EMaterialStockCheckDetailDTO detail : detailList) {
            Integer status = detail.getCheckStatus();
            if (status != null && status != 0) {
                allPending = false;
            }
            if (status == null || status != 2) {
                allCompleted = false;
            }
            if (detail.getDifferenceQuantity() != null
                && detail.getDifferenceQuantity().compareTo(BigDecimal.ZERO) != 0) {
                hasDifference = true;
            }
        }

        Integer targetStatus;
        if (allPending) {
            targetStatus = 0;
        } else if (!allCompleted) {
            targetStatus = 1;
        } else {
            targetStatus = hasDifference ? 3 : 2;
        }

        EMaterialStockCheckPO updatePO = new EMaterialStockCheckPO();
        updatePO.setId(checkId);
        updatePO.setCheckStatus(targetStatus);
        updatePO.setLoginUserId(securityUtils.getLoginUserId());
        updatePO.setLoginUserName(securityUtils.getLoginUserName());
        mapper.update(updatePO);
    }
}

