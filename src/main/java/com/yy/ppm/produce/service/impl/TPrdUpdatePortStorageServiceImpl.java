package com.yy.ppm.produce.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.business.bean.dto.TPrdUpdatePortStorageReqDTO;
import com.yy.ppm.produce.bean.dto.workTicket.PoundToPortStorageDTO;
import com.yy.ppm.produce.mapper.TPrdUpdatePortStorageMapper;
import com.yy.ppm.produce.service.TPrdUpdatePortStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TPrdUpdatePortStorageServiceImpl implements TPrdUpdatePortStorageService {
    @Autowired
    TPrdUpdatePortStorageMapper mapper;
    @Override
    public Pages<PoundToPortStorageDTO> getList(TPrdUpdatePortStorageReqDTO query) {
        return  PageHelperUtils.limit(query, () -> {
            return mapper.getUpdatePortStoragePage(query);
        });
    }

//    public Pages<Map<String,Object>> getUpdatePortStoragePage(TPrdUpdatePortStorageReqDTO query) {
//        //查询没有更新的
//        if("0".equals(query.getStatus())){
//            return  PageHelperUtils.limit(query, () -> {
//                return mapper.getNotUpdatePortStoragePage(query);
//            });
//        }else{
//            return  PageHelperUtils.limit(query, () -> {
//                return mapper.getNotUpdatePortStoragePage(query);
//            });
//        }
//    }
}
