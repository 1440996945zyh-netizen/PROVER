package com.yy.ppm.equipment.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockFlowDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockSearchDTO;
import com.yy.ppm.equipment.mapper.EMaterialAppStockMapper;
import com.yy.ppm.equipment.mapper.EMaterialStockMapper;
import com.yy.ppm.equipment.service.EMaterialAppStockService;
import com.yy.ppm.equipment.service.EMaterialStockService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 物资库存Service业务层处理
 * @author system
 */
@Service
public class EMaterialAppStockServiceImpl implements EMaterialAppStockService {

    @Resource
    private EMaterialAppStockMapper mapper;

    /**
     * 查询物资库存列表（分页）
     */
    @Override
    public List<EMaterialStockDTO> getList(EMaterialStockSearchDTO searchDTO) {
        List<EMaterialStockDTO> dataList = mapper.selectStockList(searchDTO);
        if(dataList!=null && dataList.size()>0){
            for(EMaterialStockDTO eMaterialStockDTO : dataList){
                BigDecimal count = BigDecimal.ZERO ;
                List<EMaterialStockDTO> detailList = eMaterialStockDTO.getDetailList();
                if(detailList!=null && detailList.size()>0){
                    for(EMaterialStockDTO dto : detailList){
                        count = count.add(dto.getStockQuantity());
                    }
                }
                eMaterialStockDTO.setWarehouseName(eMaterialStockDTO.getWarehouseName()+"    (库存数："+count+")");
            }
        }
        return dataList;
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
}

