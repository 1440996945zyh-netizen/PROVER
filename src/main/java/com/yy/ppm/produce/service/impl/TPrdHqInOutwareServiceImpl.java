package com.yy.ppm.produce.service.impl;

import cn.hutool.core.io.IORuntimeException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.produce.bean.dto.TPrdHqInOutwareDTO;
import com.yy.ppm.produce.bean.dto.TPrdHqInOutwareExportDTO;
import com.yy.ppm.produce.bean.dto.TPrdHqInOutwareSearchDTO;
import com.yy.ppm.produce.mapper.TPrdHqInOutwareMapper;
import com.yy.ppm.produce.service.TPrdHqInOutwareService;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class TPrdHqInOutwareServiceImpl implements TPrdHqInOutwareService {

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private TPrdHqInOutwareMapper tPrdHqInOutwareMapper;


    private static final int CURSOR_LIMIT = 5_000;
    
    @Override
    public Pages<TPrdHqInOutwareDTO> getList(TPrdHqInOutwareSearchDTO searchDTO) {
        Pages<TPrdHqInOutwareDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tPrdHqInOutwareMapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public TPrdHqInOutwareDTO getDetail(Long id) {
        return tPrdHqInOutwareMapper.getById(id);
    }

    @Override
    public boolean doSave(TPrdHqInOutwareDTO tPrdHqInOutwareDTO) {
        return tPrdHqInOutwareMapper.update(tPrdHqInOutwareDTO) == 1;
    }

    @Override
    public boolean deleteById(Long id) {
        return tPrdHqInOutwareMapper.deleteById(id) == 1;
    }

    @Override
    public byte[] exportExcel(TPrdHqInOutwareSearchDTO searchDTO) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TPrdHqInOutwareExportDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TPrdHqInOutwareExportDTO> cursor = tPrdHqInOutwareMapper.getExportList(searchDTO)) {
                    Iterator<TPrdHqInOutwareExportDTO> iterator = cursor.iterator();
                    if (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            List<TPrdHqInOutwareExportDTO> salarys = new ArrayList<>();
                            for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                                salarys.add(iterator.next());
                            }
                            excelWriter.write(salarys, writeSheet);
                        }
                    } else {
                        excelWriter.write(Collections.emptyList(), writeSheet);
                    }

                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }
}
