package com.yy.ppm.dispatch.service.impl;

import cn.hutool.core.io.IORuntimeException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.SelectService;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailExcelDTO;
import com.yy.ppm.dispatch.bean.dto.TDisTugServiceRecordExcelDTO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.mapper.TDisShipVoyageMapper;
import com.yy.ppm.dispatch.service.TDisShipVoyageService;
import com.yy.ppm.dispatch.service.TDisTugServiceRecordService;
import com.yy.ppm.dispatch.mapper.TDisTugServiceRecordMapper;
import com.yy.ppm.dispatch.bean.dto.TDisTugServiceRecordDTO;
import com.yy.ppm.dispatch.bean.dto.TDisTugServiceRecordSearchDTO;
import com.yy.ppm.master.bean.dto.MDictDataDTO;
import com.yy.ppm.master.service.MDictService;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import com.github.pagehelper.Page;

import cn.hutool.core.lang.Snowflake;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName 拖轮服务记录(TDisTugServiceRecord)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:45:00
 */
@Service
public class TDisTugServiceRecordServiceImpl implements TDisTugServiceRecordService {

    @Resource
    private TDisTugServiceRecordMapper tDisTugServiceRecordMapper;

    @Resource
	private Snowflake snowflake;

    @Resource
    private MDictService mDictService;
    @Resource
    private SelectService selectService;

    @Resource
    private TDisShipVoyageMapper shipVoyageMapper;

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
    public Pages<TDisTugServiceRecordDTO> getList(TDisTugServiceRecordSearchDTO searchDTO) {

    	Pages<TDisTugServiceRecordDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            Page<TDisTugServiceRecordDTO> page = tDisTugServiceRecordMapper.getTugList(searchDTO);
            page.forEach(e->{
                String shipVoyage = e.getShipVoyage();
                if(StringUtil.isNotEmpty(shipVoyage)){
                    if(shipVoyage.indexOf("/")==0){
                        e.setShipVoyage(shipVoyage.substring(1));
                    }else if(shipVoyage.indexOf("/")==(shipVoyage.length()-1)){
                        e.setShipVoyage(shipVoyage.substring(0,shipVoyage.length()-1));
                    }
                    if(e.getShipVoyage().indexOf("_")==(e.getShipVoyage().length()-1)){
                        e.setShipVoyage(shipVoyage.substring(0,shipVoyage.length()-1));
                    }
                    if((e.getShipVoyage().indexOf("_")+1)==(e.getShipVoyage().indexOf("/"))){
                        e.setShipVoyage(e.getShipVoyage().substring(0,e.getShipVoyage().indexOf("/"))+
                                e.getShipVoyage().substring(e.getShipVoyage().indexOf("/")+1,e.getShipVoyage().length()));
                    }

                }
            });
    	    return page;
		});

        return pages;
    }

    @Override
    public byte[] export(TDisTugServiceRecordSearchDTO searchDTO) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TDisTugServiceRecordExcelDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TDisTugServiceRecordExcelDTO> cursor = tDisTugServiceRecordMapper.exportList(searchDTO)) {
                    Iterator<TDisTugServiceRecordExcelDTO> iterator = cursor.iterator();
                    while (iterator.hasNext()) {
                        List<TDisTugServiceRecordExcelDTO> portStorages = new ArrayList<>();
                        for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                            portStorages.add(iterator.next());
                        }
                        excelWriter.write(portStorages, writeSheet);
                    }
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }

    /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     @Override
     public TDisTugServiceRecordDTO getDetail(Long id) {
         return tDisTugServiceRecordMapper.getById(id);
     }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TDisTugServiceRecordDTO dto) {

        if("0".equals(dto.getIsStandardUse())){
            if(StringUtil.isEmpty(dto.getReasonName())){
                throw new BusinessRuntimeException("请填写非标准原因");
            }
        }
        //写入字典
            HashMap<String, Object> serachMap = new HashMap<>();
            serachMap.put("type","DICT");
            serachMap.put("dictType","DIS_TUG_REASON");
            List<Map<String, Object>> localSelect = selectService.getLocalSelect(serachMap);
            if (CollectionUtils.isEmpty(localSelect)){
                MDictDataDTO mDictDataDTO = new MDictDataDTO();
                mDictDataDTO.setDictLabel(dto.getReasonName());
                mDictDataDTO.setDictType("DIS_TUG_REASON");
                mDictDataDTO.setDictValue("1");
                mDictDataDTO.setSortNum(1);
                dto.setReasonCode(1L);
                mDictDataDTO.setStatus("1");
                mDictService.insertOrUpdateDict(mDictDataDTO);
            }else{
                if(dto.getReasonCode()==null||dto.getReasonCode()==0L){
                    MDictDataDTO mDictDataDTO = new MDictDataDTO();
                    mDictDataDTO.setDictLabel(dto.getReasonName());
                    mDictDataDTO.setDictType("DIS_TUG_REASON");
                    mDictDataDTO.setDictValue(String.valueOf(localSelect.size()+1));//编号以此后排
                    mDictDataDTO.setStatus("1");
                    dto.setReasonCode(Long.parseLong(String.valueOf(localSelect.size()+1)));
                    mDictDataDTO.setSortNum(localSelect.size()+1);
                    mDictService.insertOrUpdateDict(mDictDataDTO);
                }
            }

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            if(dto.getShipvoyageItemId()!=null){
                TDisShipvoyageItemPO tDisShipvoyageItemPO = shipVoyageMapper.getDisShipVoyageItemById(dto.getShipvoyageItemId()).get(0);
                dto.setShipvoyageId(tDisShipvoyageItemPO.getShipvoyageId());
            }
            return tDisTugServiceRecordMapper.insert(dto) == 1;
            // 修改
        } else {
            return tDisTugServiceRecordMapper.update(dto) == 1;
        }

    }

    /**
     * 删除
     *
     * @param  id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return tDisTugServiceRecordMapper.deleteById(id) == 1;

    }
}

