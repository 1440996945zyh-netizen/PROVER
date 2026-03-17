package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.io.IORuntimeException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockFlowDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockSearchDTO;
import com.yy.ppm.equipment.mapper.EMaterialStockMapper;
import com.yy.ppm.equipment.service.EMaterialStockService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * 物资库存Service业务层处理
 * @author system
 */
@Service
public class EMaterialStockServiceImpl implements EMaterialStockService {

    @Resource
    private EMaterialStockMapper mapper;

    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 查询物资库存列表（分页）
     */
    @Override
    public Pages<EMaterialStockDTO> getList(EMaterialStockSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectStockList(searchDTO));
    }

    /**
     * 查询物资库存明细列表（根据仓库ID和物资ID）
     */
    @Override
    public List<EMaterialStockDetailDTO> getStockDetailList(Long warehouseId, Long materialId, String warehouseInTimeStart, String warehouseInTimeEnd) {
        return mapper.selectStockDetailList(warehouseId, materialId, warehouseInTimeStart, warehouseInTimeEnd);
    }

    /**
     * 查询物资库存流水列表（根据仓库ID和物资ID）
     */
    @Override
    public List<EMaterialStockFlowDTO> getStockFlowList(Long warehouseId, Long materialId, String warehouseInTimeStart, String warehouseInTimeEnd) {
        return mapper.selectStockFlowList(warehouseId, materialId, warehouseInTimeStart, warehouseInTimeEnd);
    }

    /**
     * 导出物资库存列表
     */
    @Override
    public byte[] pageExport(EMaterialStockSearchDTO searchDTO) {
        Page<EMaterialStockDTO> page = mapper.selectStockListForExport(searchDTO);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, EMaterialStockDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try {
                    excelWriter.write(page.getResult(), writeSheet);
                } catch (Exception e) {
                    throw new IORuntimeException("库存查询导出异常" + e.getMessage());
                }
            });
        }
        return os.toByteArray();
    }
}

