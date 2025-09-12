package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.MapUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.dto.CheckDTO;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.*;

import com.yy.ppm.master.mapper.MPrintMapper;
import com.yy.ppm.master.service.MPrintService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 字典及字典类型操作实现类
 *
 * @author 孙琦
 * @date 2023-4-26 16:57:35
 */
@Service
public class MPrintServiceImpl implements MPrintService {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MDictServiceImpl.class);
    /**
     * 雪花算法
     **/
    @Autowired
    private Snowflake snowflake;

    @Resource
    private MPrintMapper mPrintMapper;


    @Override
    public void insert(MPrintDTO po) {
        final String methodName = "DictServiceImpl:insertDict";
        LOGGER.enter(methodName, "业务执行");

        po.setId(snowflake.nextId());
        mPrintMapper.insert(po);

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }
    /**
     * 查询列表
     *
     * @param mPrintSearchDTO  实体类
     * @return 响应数据
     */
    @Override
    public Pages<MPrintDTO> getList(MPrintSearchDTO mPrintSearchDTO) {
        final String methodName = "PrintServiceImpl:getList";
        LOGGER.enter(methodName, "业务执行");

        Pages<MPrintDTO> pages = PageHelperUtils.limit(mPrintSearchDTO, () -> mPrintMapper.getList(mPrintSearchDTO));

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }
}
