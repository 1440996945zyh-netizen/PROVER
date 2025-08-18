package com.yy.ppm.produce.service.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.dispatch.bean.dto.TDisShipDaynigttplanDTO;
import com.yy.ppm.produce.bean.dto.*;
import com.yy.ppm.produce.bean.po.TWeightPlanItemPO;
import com.yy.ppm.produce.mapper.TWeightPlanItemMapper;
import com.yy.ppm.produce.mapper.TWeightPlanMapper;
import com.yy.ppm.produce.service.TWeightPlanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 杂项过磅计划表(TWeightPlan)ServiceImpl
 * @Description
 * @createTime 2023年12月05日 08:39:00
 */
@Service
@Slf4j
public class TWeightPlanServiceImpl implements TWeightPlanService {

    @Resource
    private TWeightPlanMapper tWeightPlanMapper;

    @Resource
    private TWeightPlanItemMapper detailMapper;

    @Resource
    private Snowflake snowflake;

    @Autowired
    private CommonService commonService;

    @Resource
    private SysFileService sysFileService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TWeightPlanDTO> getList(TWeightPlanSearchDTO searchDTO) {

        Pages<TWeightPlanDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tWeightPlanMapper.getList(searchDTO);
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
    public TWeightPlanDTO getDetail(Long id) {
        if(id==null){
            throw new BusinessRuntimeException("请选择数据(没有id)");
        }
        TWeightPlanDTO result = tWeightPlanMapper.getById(id);
        //禁止form表单
        int count = 0;
        count = tWeightPlanMapper.getListByPound(result.getPlanNo());
        if(count > 0){
            result.setIsPound(1L);
        }
        TWeightPlanItemSearchDTO detailSearchDto = new TWeightPlanItemSearchDTO();
        detailSearchDto.setParentId(id);
        result.setList(detailMapper.getList(detailSearchDto));

        //禁止表单下面的列表
        List<TWeightPlanItemDTO> byParentId = detailMapper.getByParentId(id);
        for(TWeightPlanItemDTO tWeightPlanItemDTO :byParentId){
            count = tWeightPlanMapper.getListByPlanNoAndIdNumber(result.getPlanNo(),tWeightPlanItemDTO.getIdNumber());
            if(count>0){
                result.getList().forEach(x->{
                    if(x.getIdNumber().equals(tWeightPlanItemDTO.getIdNumber())){
                        x.setIsPounds(2L);
                    }
                });
            }
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
    public boolean doSave(TWeightPlanDTO dto) {

        // 新增
        if (dto.getId() == null) {
            //杂货过磅计划附件必填
            if("6".equals(dto.getWorkTypeCode())){
                if(CollectionUtils.isEmpty(dto.getFileIds())){
                    throw new BusinessRuntimeException("请先上传附件");
                }
            }
            dto.setExamineStatus(0L);

            dto.setId(snowflake.nextId());
            dto.setPlanNo( commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WEIGHT_PLAN_NO,""));
            dto.setStatus(0L);
            List<TWeightPlanDTO> list1 = tWeightPlanMapper.getListStatus();
            List<TWeightPlanItemDTO> importTruckList= dto.getList();
            importTruckList.forEach(o->{
                if(StringUtils.isEmpty(o.getTruckNo())){
                    throw new BusinessRuntimeException("数据异常，车牌号为空！");
                }
                o.setTruckNo(o.getTruckNo().replace(" ",""));
                o.setDriver(o.getDriver().replace(" ",""));
                o.setTel(o.getTel().replace(" ",""));
                o.setIdNumber(o.getIdNumber().replace(" ",""));
            });
            list1.forEach(x->{
                List<TWeightPlanItemDTO> carList = detailMapper.getByParentId(x.getId());
                for(TWeightPlanItemDTO tWeightPlanItemDTO :carList){
                    for(TWeightPlanItemDTO tWeightPlanItemDTO1 : importTruckList){
                        if(tWeightPlanItemDTO.getTruckNo().equals(tWeightPlanItemDTO1.getTruckNo()) && tWeightPlanItemDTO.getStatus() == 0L){
                            throw new BusinessRuntimeException(tWeightPlanItemDTO1.getTruckNo()+"已存在其他杂货计划中");
                        }
                        if(tWeightPlanItemDTO.getIdNumber().equals(tWeightPlanItemDTO1.getIdNumber()) && tWeightPlanItemDTO.getStatus() == 0L){
                            throw new BusinessRuntimeException(tWeightPlanItemDTO1.getIdNumber()+"已存在其他杂货计划中");
                        }
                    }
                }
            });

            if(!CollectionUtils.isEmpty(importTruckList)){
                for (TWeightPlanItemPO tWeightPlanItemPO : importTruckList) {
                    tWeightPlanItemPO.setId(snowflake.nextId());
                    tWeightPlanItemPO.setParentId(dto.getId());
                    if(tWeightPlanItemPO.getStatus()==null){
                        tWeightPlanItemPO.setStatus(0L);
                    }
                }
                detailMapper.insertBatch(importTruckList);
            }
            sysFileService.saveFileBusRelation(dto.getFileIds(),dto.getId());
            return tWeightPlanMapper.insert(dto) == 1;

            // 修改
        } else {

//            detailMapper.deleteByParentId(dto.getId());
            List<TWeightPlanDTO> list1 = tWeightPlanMapper.getListStatus();
            List<TWeightPlanDTO> collect = list1.stream()
                    .filter(x -> !Objects.equals(x.getId(), dto.getId()))
                    .collect(Collectors.toList());
            collect.forEach(x->{
                List<TWeightPlanItemDTO> carList = detailMapper.getByParentId(x.getId());
                for(TWeightPlanItemDTO tWeightPlanItemDTO :carList){
                    for(TWeightPlanItemDTO tWeightPlanItemDTO1 : dto.getList()){
                        if(tWeightPlanItemDTO.getTruckNo().equals(tWeightPlanItemDTO1.getTruckNo()) && tWeightPlanItemDTO.getStatus() == 0L){
                            throw new BusinessRuntimeException(tWeightPlanItemDTO1.getTruckNo()+"已存在其他杂货计划中");
                        }
                        if(tWeightPlanItemDTO.getIdNumber().equals(tWeightPlanItemDTO1.getIdNumber()) && tWeightPlanItemDTO.getStatus() == 0L){
                            throw new BusinessRuntimeException(tWeightPlanItemDTO1.getIdNumber()+"已存在其他杂货计划中");
                        }
                    }
                }
            });

            List<TWeightPlanItemDTO> toInsert = new ArrayList<>();
            List<TWeightPlanItemDTO> toUpdate = new ArrayList<>();

            List<TWeightPlanItemDTO> carOldList = tWeightPlanMapper.getItemListById(dto.getId());
            List<TWeightPlanItemDTO> list2 = dto.getList();
            Map<Long, TWeightPlanItemDTO> dbMap = carOldList.stream()
                    .collect(Collectors.toMap(TWeightPlanItemDTO::getId, Function.identity()));


            if (list2 != null && !list2.isEmpty()) {
                for (TWeightPlanItemDTO tWeightPlanItemDTO : list2) {

                    if (tWeightPlanItemDTO.getId() != null && dbMap.containsKey(tWeightPlanItemDTO.getId())) {
                        // 存在ID且在数据库中存在 → 更新
                        toUpdate.add(tWeightPlanItemDTO);
                    } else {
                        // 无ID或数据库不存在 → 新增
                        if (tWeightPlanItemDTO.getId() == null) {
                            tWeightPlanItemDTO.setId(snowflake.nextId());
                            tWeightPlanItemDTO.setParentId(dto.getId());
                        }
                        toInsert.add(tWeightPlanItemDTO);
                    }
                }
            }

            if (list2 == null) list2 = Collections.emptyList();

            Set<Long> newItemIds = list2.stream()
                    .map(TWeightPlanItemDTO::getId)  // 根据实际DTO的ID字段调整
                    .filter(Objects::nonNull)        // 过滤null值
                    .collect(Collectors.toSet());

            List<TWeightPlanItemDTO> idsToDelete = carOldList.stream()
                    .filter(oldItem -> !newItemIds.contains(oldItem.getId()))
                    .collect(Collectors.toList());

            if (!idsToDelete.isEmpty()) {
                for(TWeightPlanItemDTO tWeightPlanItemDTO : idsToDelete){
                    int count = tWeightPlanMapper.getPoundByTruckNo(tWeightPlanItemDTO.getTruckNo(),dto.getPlanNo());
                    if(count>0){
                        throw new BusinessRuntimeException(tWeightPlanItemDTO.getTruckNo() + "已存在过磅数据，无法删除");
                    }
                    detailMapper.deleteById(tWeightPlanItemDTO.getId());
                }
            }
            if (!toInsert.isEmpty()) {
                for (TWeightPlanItemDTO tWeightPlanItemDTO : toInsert) {
                    sysFileService.saveFileBusRelation(dto.getFileIds(),dto.getId());
                    detailMapper.insert(tWeightPlanItemDTO);
                }
            }

            if (!toUpdate.isEmpty()) {
                for(TWeightPlanItemDTO tWeightPlanItemDTO : toUpdate){
                    sysFileService.saveFileBusRelation(dto.getFileIds(),dto.getId());
                    detailMapper.update(tWeightPlanItemDTO);
                }
            }


            /*List<TWeightPlanItemDTO> list = dto.getList();
            if(!CollectionUtils.isEmpty(list)){
                //删除子表数据
                for (TWeightPlanItemPO tWeightPlanItemPO : list) {
                    tWeightPlanItemPO.setId(snowflake.nextId());
                    tWeightPlanItemPO.setParentId(dto.getId());

                    if(tWeightPlanItemPO.getStatus()==null){
                        tWeightPlanItemPO.setStatus(0L);
                    }

                }
                sysFileService.saveFileBusRelation(dto.getFileIds(),dto.getId());
                detailMapper.insertBatch(list);
            }*/
            sysFileService.saveFileBusRelation(dto.getFileIds(),dto.getId());
            return tWeightPlanMapper.update(dto) == 1;
        }

    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        TWeightPlanDTO tWeightPlanDTO1 = tWeightPlanMapper.getById(id);
        int count = 0;
        count = tWeightPlanMapper.getListByPound(tWeightPlanDTO1.getPlanNo());
        if(count > 0){
            throw new BusinessRuntimeException("过磅数据已存在，无法删除");
        }
        detailMapper.deleteByParentId(id);
        return tWeightPlanMapper.deleteById(id) == 1;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeMainStatus(TWeightPlanDTO tWeightPlanDTO) {
        if(tWeightPlanDTO.getStatus() == 0L) {
            TWeightPlanDTO detail = this.getDetail(tWeightPlanDTO.getId());
            List<TWeightPlanDTO> list1 = tWeightPlanMapper.getListStatus();
            list1.forEach(x->{
                List<TWeightPlanItemDTO> carList = detailMapper.getByParentId(x.getId());
                for(TWeightPlanItemDTO tWeightPlanItemDTO :carList){
                    for(TWeightPlanItemDTO tWeightPlanItemDTO1 : detail.getList()){
                        if(tWeightPlanItemDTO.getTruckNo().equals(tWeightPlanItemDTO1.getTruckNo()) && tWeightPlanItemDTO.getStatus() == 0L){
                            throw new BusinessRuntimeException(tWeightPlanItemDTO1.getTruckNo()+"已存在其他杂货计划中");
                        }
                        if(tWeightPlanItemDTO.getIdNumber().equals(tWeightPlanItemDTO1.getIdNumber()) && tWeightPlanItemDTO.getStatus() == 0L){
                            throw new BusinessRuntimeException(tWeightPlanItemDTO1.getIdNumber()+"已存在其他杂货计划中");
                        }
                    }
                }

            });
        }
        return tWeightPlanMapper.changeMainStatus(tWeightPlanDTO);
    }

    @Override
    public List<TWeightRecordDTO> getSundryList(String planNo) {

        return tWeightPlanMapper.getListByPlanNo(planNo);
    }

    @Override
    public boolean examine(TWeightPlanDTO dto) {
        if(dto==null){
            throw new BusinessRuntimeException("要审核的数据为空");
        }
        if(dto.getId()==null){
            throw new BusinessRuntimeException("缺少ID");
        }
        if("6".equals(dto.getWorkTypeCode())){
            if(StringUtils.isEmpty(dto.getWorkAreaName())){
                throw new BusinessRuntimeException("请填写作业区域");
            }
            if(CollectionUtils.isEmpty(dto.getFileIds())){
                throw new BusinessRuntimeException("请上传附件");
            }
        }
        detailMapper.deleteByParentId(dto.getId());
        List<TWeightPlanDTO> list1 = tWeightPlanMapper.getListStatus();
        list1.forEach(x->{
            List<TWeightPlanItemDTO> carList = detailMapper.getByParentId(x.getId());
            for(TWeightPlanItemDTO tWeightPlanItemDTO :carList){
                for(TWeightPlanItemDTO tWeightPlanItemDTO1 : dto.getList()){
                    if(tWeightPlanItemDTO.getTruckNo().equals(tWeightPlanItemDTO1.getTruckNo()) && tWeightPlanItemDTO.getStatus() == 0L){
                        throw new BusinessRuntimeException(tWeightPlanItemDTO1.getTruckNo()+"已存在其他杂货计划中");
                    }
                    if(tWeightPlanItemDTO.getIdNumber().equals(tWeightPlanItemDTO1.getIdNumber()) && tWeightPlanItemDTO.getStatus() == 0L){
                        throw new BusinessRuntimeException(tWeightPlanItemDTO1.getIdNumber()+"已存在其他杂货计划中");
                    }
                }
            }

        });
        List<TWeightPlanItemDTO> list = dto.getList();
        if(!CollectionUtils.isEmpty(list)){
            //删除子表数据
            for (TWeightPlanItemPO tWeightPlanItemPO : list) {
                tWeightPlanItemPO.setId(snowflake.nextId());
                tWeightPlanItemPO.setParentId(dto.getId());

                if(tWeightPlanItemPO.getStatus()==null){
                    tWeightPlanItemPO.setStatus(0L);
                }

            }
            detailMapper.insertBatch(list);
        }
        sysFileService.saveFileBusRelation(dto.getFileIds(),dto.getId());

        dto.setExamineStatus(1L);
        return tWeightPlanMapper.update(dto) == 1;
    }


    @Override
    public List<TWeightPlanItemDTO> parseCars(MultipartFile file) {
        List<TWeightPlanItemDTO> cars = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row == null) {
                    break;
                }
                XSSFCell cell0 = row.getCell(0);
                if (cell0 == null) {
                    continue;
                }
                if (cell0.getCellType() != CellType.STRING) {
                    continue;
                }
                if (cell0.getStringCellValue().isEmpty()) {
                    continue;
                }
                TWeightPlanItemDTO car = new TWeightPlanItemDTO();
                if(StringUtils.isEmpty(cell0.getStringCellValue())){
                    throw new BusinessRuntimeException("没有车号！");
                }

                car.setTruckNo(cell0.getStringCellValue().replace(" ",""));
                XSSFCell cell1 = row.getCell(1);
                if (cell1 != null && cell1.getCellType() == CellType.STRING) {
                    car.setDriver(cell1.getStringCellValue());
                }
                XSSFCell cell2 = row.getCell(2);
                if (cell2 != null && cell2.getCellType() == CellType.STRING) {
                    car.setIdNumber(cell2.getStringCellValue());
                }
                XSSFCell cell3 = row.getCell(3);
                if (cell3 != null && cell3.getCellType() == CellType.STRING) {
                    car.setTel(cell3.getStringCellValue());
                }
                car.setStatus(0L);
                cars.add(car);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return cars;
    }
}

