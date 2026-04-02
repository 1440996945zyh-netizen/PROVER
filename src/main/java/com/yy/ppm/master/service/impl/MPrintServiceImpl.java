package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.master.bean.dto.*;

import com.yy.ppm.master.mapper.MPrintMapper;
import com.yy.ppm.master.service.MPrintService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
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
    private final Snowflake snowflake;

    public MPrintServiceImpl(Snowflake snowflake){
        this.snowflake = snowflake;
    }

    @Resource
    private MPrintMapper mPrintMapper;


    @Override
    public void insert(MPrintDTO po) {
        final String methodName = "PrintServiceImpl:insert";
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

    @Override
    public void update(MPrintDTO po) {
        final String methodName = "PrintServiceImpl:update";
        LOGGER.enter(methodName, "业务执行");

        mPrintMapper.update(po);

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    public void deleteById(Long id) {
        final String methodName = "PrintServiceImpl:deleteById";
        LOGGER.enter(methodName, "业务执行");

        Integer count = mPrintMapper.deleteById(id);
        if(count<=0){
            throw new BusinessRuntimeException("删除失败！");
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    public MPrintDTO getDetail(Long id) {
        final String methodName = "PrintServiceImpl:getDetail";
        LOGGER.enter(methodName, "业务执行");

        MPrintDTO list = mPrintMapper.getDetail(id);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return list;
    }

    @Override
    public List<MPrintDTO> getModelTypeList(MPrintSearchDTO mPrintSearchDTO) {
        final String methodName = "PrintServiceImpl:getModelTypeList";
        LOGGER.enter(methodName, "业务执行");

        List<MPrintDTO> list = mPrintMapper.getModelTypeList();

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return list;
    }
}
