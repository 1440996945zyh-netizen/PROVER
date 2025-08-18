package com.yy.ppm.master.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.yy.common.enums.CommonEnum;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.cargoInfo.ExportDTO;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.dto.WaifuProcessPriceExcelDTO;
import com.yy.ppm.master.bean.dto.WaifuProcessPriceReq;
import com.yy.ppm.master.bean.dto.WaifuProcessPriceRes;
import com.yy.ppm.master.mapper.WaifuProcessPriceMapper;
import com.yy.ppm.master.service.WaifuProcessPriceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WaifuProcessPriceServiceImpl implements WaifuProcessPriceService {

    @Resource
    private WaifuProcessPriceMapper mapper;

    @Autowired
    private Snowflake snowflake;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private TransactionTemplate transactionTemplate;
    private static final int CURSOR_LIMIT = 500;

    @Override
    public List<WaifuProcessPriceRes> getList(WaifuProcessPriceReq reqDto) {
//        Pages<WaifuProcessPriceRes> pages = PageHelperUtils.limit(reqDto, () -> {
//            return mapper.getList(reqDto);
//        });
        return mapper.getList(reqDto);
    }
    @Override
    public byte[] exportExcel(WaifuProcessPriceReq reqDto) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, WaifuProcessPriceExcelDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("外付匹配规则信息").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<WaifuProcessPriceExcelDTO> cursor = mapper.exportExcel(reqDto)) {
                    Iterator<WaifuProcessPriceExcelDTO> iterator = cursor.iterator();
                    while (iterator.hasNext()) {
                        List<WaifuProcessPriceExcelDTO> wf = new ArrayList<>();
                        for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                            wf.add(iterator.next());
                        }
                        excelWriter.write(wf, writeSheet);
                    }
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }

    @Override
    public List<Map<String, String>> getMainProcessList(WaifuProcessPriceReq reqDto) {
        return mapper.getMainProcessList();
    }

    @Override
    public List<Map<String, String>> getPositionList(WaifuProcessPriceReq reqDto) {
        return mapper.getPositionList();
    }

    @Override
    public List<Map<String, String>> getProcessListNoMain(String processCode) {
        return mapper.getProcessListNoMain(processCode);
    }
    @Override
    public List<Map<String, String>> getDeptOut() {
        return mapper.getDeptOut();
    }
    @Override
    public List<Map<String, String>> machinTypeList() {
        return mapper.machinTypeList();
    }

    @Override
    public List<Map<String, String>> waifuPackageCodeList() {
        return mapper.waifuPackageCodeList();
    }

    @Override
    public List<Map<String, String>> deleteFunc(Long id) {
        mapper.deleteFunc(id);
        return Collections.emptyList();
    }

    @Override
    public void doSave(List<WaifuProcessPriceReq> list) {
        if(CollectionUtils.isEmpty(list)){
            throw new BusinessRuntimeException("没有要保存的数据");
        }
        for (WaifuProcessPriceReq waifuProcessPriceReq : list) {
            log.error(waifuProcessPriceReq.getWaifuPackageCode());
            if(waifuProcessPriceReq.getDeptId() == null){
                throw new BusinessRuntimeException("作业部门必填");
            }
            if(StringUtils.isEmpty(waifuProcessPriceReq.getProcessCode()) ){
                throw new BusinessRuntimeException("作业过程必填");
            }
            if(StringUtils.isEmpty(waifuProcessPriceReq.getWaifuPackageCode())){
                throw new BusinessRuntimeException("外付包装类型必填");
            }
            if(StringUtils.isEmpty(waifuProcessPriceReq.getAllotType())){
                throw new BusinessRuntimeException("分配类型必填");
            }
        }
        List<WaifuProcessPriceReq> insertList = list.stream().filter(o -> o.getId().equals(0L)).collect(Collectors.toList());
        List<WaifuProcessPriceReq> updateList = list.stream().filter(o -> !o.getId().equals(0L)).collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(insertList)){
            insertList.forEach(o->o.setId(snowflake.nextId()));
            mapper.insertBatch(insertList,securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),new Date());
        }
        if(!CollectionUtils.isEmpty(updateList)){
            mapper.updateBatch(updateList,securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),new Date());
        }

    }


}
