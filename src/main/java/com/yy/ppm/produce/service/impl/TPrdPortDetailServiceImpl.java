package com.yy.ppm.produce.service.impl;

import cn.hutool.core.io.IORuntimeException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageQueryDTO;
import com.yy.ppm.produce.mapper.TPrdPortDetailMapper;
import com.yy.ppm.produce.service.TPrdPortDetailService;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class TPrdPortDetailServiceImpl implements TPrdPortDetailService {

    @Autowired
    private TPrdPortDetailMapper tPrdPortDetailMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final int CURSOR_LIMIT = 5_000;

    @Override
    public Pages<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdPortDetailMapper.listPortStorage(query);
        });
    }

    @Override
    public Map<String, Object> summaryQuantityTon(TPrdPortStorageQueryDTO query) {
        Map<String, Object> totalQuantityTon = tPrdPortDetailMapper.summaryQuantityTon(query);
        return Optional.ofNullable(totalQuantityTon).orElse(Collections.emptyMap());
    }

    @Override
    public byte[] exportPortStorage(TPrdPortStorageQueryDTO query) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TPrdPortStorageDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TPrdPortStorageDTO> cursor = tPrdPortDetailMapper.cursorListPortStorage(query)) {
                    Iterator<TPrdPortStorageDTO> iterator = cursor.iterator();
                    while (iterator.hasNext()) {
                        List<TPrdPortStorageDTO> portStorages = new ArrayList<>();
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

}
