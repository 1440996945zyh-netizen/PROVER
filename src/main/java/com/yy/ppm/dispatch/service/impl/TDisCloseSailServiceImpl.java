package com.yy.ppm.dispatch.service.impl;

import cn.hutool.core.io.IORuntimeException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.api.client.util.Lists;
import com.yy.common.enums.CommonEnum;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.UserHelper;

import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.cargoInfo.ExportDTO;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SelectService;
import com.yy.ppm.dispatch.bean.dto.*;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailShipDTO;
import com.yy.ppm.dispatch.mapper.TDisCloseSailShipMapper;
import com.yy.ppm.dispatch.mapper.TDisShipVoyageMapper;
import com.yy.ppm.dispatch.service.TDisCloseSailService;
import com.yy.ppm.dispatch.mapper.TDisCloseSailMapper;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPaymentDTO;
import com.yy.ppm.master.bean.dto.MDictDataDTO;
import com.yy.ppm.master.service.MDictService;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageGbCargoDTO;
import com.yy.ppm.produce.bean.dto.salary.TPrdSalaryDTO;
import com.yy.ppm.produce.bean.dto.salary.TPrdSalaryGroupByProcessDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName 封航记录表(TDisCloseSail)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:54:00
 */
@Service
public class TDisCloseSailServiceImpl implements TDisCloseSailService {

    @Resource
    private TDisCloseSailMapper tDisCloseSailMapper;

    @Resource
    private TDisCloseSailShipMapper TDisCloseSailShipMapper;

    @Resource
    private CommonService commonService;
    @Resource
    private MDictService mDictService;
    @Resource
    private SelectService selectService;

    @Resource
	private Snowflake snowflake;
    @Resource
    private SecurityUtils securityUtils;
    @Resource
    private TDisShipVoyageMapper tDisShipVoyageMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final int CURSOR_LIMIT = 5_000;


    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TDisCloseSailDTO> getList(TDisCloseSailSearchDTO searchDTO) {

        Date beginTimes = null;
        Date endTime = null;
        if (StringUtil.isNotEmpty(searchDTO.getBeginTimes())){
            if (StringUtil.isNotEmpty(searchDTO.getEndTimes())){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try{
                    beginTimes = simpleDateFormat.parse(searchDTO.getBeginTimes());
                    endTime = simpleDateFormat.parse(searchDTO.getEndTimes());

                }catch (Exception e){
                    throw new BusinessRuntimeException("日期类型转换错误！");
                }
                if(beginTimes.compareTo(endTime)!=-1){
                    throw new BusinessRuntimeException("开始日期必须大于结束日期！");
                }

            }
           
        }

        Pages<TDisCloseSailDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tDisCloseSailMapper.getList(searchDTO);
		});
        for (TDisCloseSailDTO tdis : pages.getPages()) {
            String shipNames = new String();
            int count = 0;
            for(String shipName : tdis.getShipNameList()){
                shipNames = shipNames + shipName + ',';
                count++;
            }
            if(shipNames!=null && !"".equals(shipNames)){
                shipNames = shipNames.substring(0,shipNames.length()-1);
            }else{
                shipNames = "";
            }
            tdis.setShipVoyageNum(0);
            if(StringUtil.isNotEmpty(tdis.getEffectShipvoyage())){
                tdis.setShipVoyageNum(tdis.getEffectShipvoyage().replace(",","，").split("，").length);
            }

            tdis.setStatus(StringUtil.isEmpty(tdis.getStatus())?"封航结束":"0".equals(tdis.getStatus())?"封航结束":"封航中");
            tdis.setShipNames(shipNames);
        }

        return pages;
    }


    @Override
    public byte[] export(TDisCloseSailSearchDTO searchDTO) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TDisCloseSailExcelDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                List<TDisCloseSailExcelDTO> list = tDisCloseSailMapper.exportList(searchDTO);
                List<TDisCloseSailExcelDTO> portStorages = new ArrayList<>();
                for (TDisCloseSailExcelDTO row : list) {
                    if(StringUtil.isNotEmpty(row.getEffectShipvoyage())){
                        row.setShipVoyageNum(row.getEffectShipvoyage().split(",").length);
                    }
                    portStorages.add(row);
                }
                excelWriter.write(portStorages, writeSheet);
            });
        }
        return os.toByteArray();
    }

    @Override
    public List<Map<String, Object>> getShipVoyageList(TDisCloseSailSearchDTO searchDTO) {
//        List<Map<String, Object>> result = Lists.newArrayList();
        List<Map<String,Object>> result = tDisShipVoyageMapper.getShipVoyageList(searchDTO);
//        Map<Object,List<Map<String, Object>>> listMap = list.stream().collect(Collectors.groupingBy(e->e.get("shipName")));
//        listMap.forEach((k,v)->{
//            for (Map<String, Object> map : v) {
//                result.add(String.valueOf(map.get("id")), String.valueOf(map.get("shipName"))+String.valueOf(map.get("voyage")));
//            }
//        });
        return result;
    }

    /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     @Override
     public TDisCloseSailDTO getDetail(Long id) {
         TDisCloseSailDTO dto = tDisCloseSailMapper.getById(id);
         // 航次信息
         dto.setShipList(TDisCloseSailShipMapper.getInfoByCloseSailId(dto.getId()));
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
    public boolean doSave(TDisCloseSailDTO dto) {

        int count = 0;
        //处理字典信息
        if(dto.getCloseReasonCode() == null){
            throw new RuntimeException("请填写封航原因");
        }
        if(dto.getCloseReasonCode() == 0L){
            if(StringUtil.isEmpty(dto.getCloseReasonName())){
                throw new RuntimeException("请填写封航原因");
            }
            HashMap<String, Object> serachMap = new HashMap<>();
            serachMap.put("type","DICT");
            serachMap.put("dictType","DIS_CLOSE_SAIL_REASON");
            List<Map<String, Object>> localSelect = selectService.getLocalSelect(serachMap);
            if (CollectionUtils.isEmpty(localSelect)){
                MDictDataDTO mDictDataDTO = new MDictDataDTO();
                mDictDataDTO.setDictLabel(dto.getCloseReasonName());
                mDictDataDTO.setDictType("DIS_CLOSE_SAIL_REASON");
                mDictDataDTO.setDictValue("1");
                mDictDataDTO.setSortNum(1);
                mDictDataDTO.setStatus("1");
                 mDictService.insertOrUpdateDict(mDictDataDTO);
            }
            MDictDataDTO mDictDataDTO = new MDictDataDTO();
            mDictDataDTO.setDictLabel(dto.getCloseReasonName());
            mDictDataDTO.setDictType("DIS_CLOSE_SAIL_REASON");
            mDictDataDTO.setDictValue(String.valueOf(localSelect.size()+1));//编号以此后排
            mDictDataDTO.setSortNum(localSelect.size()+1);
            mDictDataDTO.setStatus("1");
            mDictService.insertOrUpdateDict(mDictDataDTO);
        }
        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            if(ObjectUtils.isEmpty(dto.getEndTime())){
                dto.setStatus("1");//封航中
            }else{
                dto.setStatus("0");//封航结束
            }
            count = tDisCloseSailMapper.insert(dto);

            // 修改
        } else {
            // 删除航次
            commonService.delete("T_DIS_CLOSE_SAIL_SHIP", "CLOSE_SAIL_ID", StringUtil.getString(dto.getId()));
            dto.setUpdateBy(securityUtils.getLoginUserId());
            dto.setUpdateByName(securityUtils.getLoginUserName());
            dto.setUpdateTime(new Date());
            if(ObjectUtils.isEmpty(dto.getEndTime())){
                dto.setStatus("1");//封航中
            }else{
                dto.setStatus("0");//封航结束
            }
            count = tDisCloseSailMapper.update(dto);
        }
        if (dto.getShipList() != null && dto.getShipList().size() > 0) {
            for (long shipId : dto.getShipList()) {
                TDisCloseSailShipDTO shipPo=new TDisCloseSailShipDTO();
                shipPo.setId(snowflake.nextId());
                shipPo.setCloseSailId(dto.getId());
                shipPo.setShipvoyageId(shipId);
                ShipVoyageDto shipVoyageDto = TDisCloseSailShipMapper.selectBerth(shipId);
                if(shipVoyageDto != null){
                    shipPo.setBerthId(shipVoyageDto.getBerthId());
                    shipPo.setBerthName(shipVoyageDto.getBerthName());
                }
//                shipPo.setBerthId(TDisCloseSailShipMapper.selectBerth(shipId).getBerthId());
//                shipPo.setBerthName(TDisCloseSailShipMapper.selectBerth(shipId).getBerthName());
                TDisCloseSailShipMapper.insert(shipPo);
            }
        }

        return count == 1;
    }

    /**
     * 删除
     *
     * @param  id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        commonService.delete("T_DIS_CLOSE_SAIL_SHIP", "CLOSE_SAIL_ID", StringUtil.getString(id));

        return tDisCloseSailMapper.deleteById(id) == 1;

    }
}

