package com.yy.ppm.finance.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.finance.bean.dto.FFeeItemDTO;
import com.yy.ppm.finance.bean.dto.FFeeItemSearchDTO;
import com.yy.ppm.finance.mapper.FFeeItemMapper;
import com.yy.ppm.finance.service.FFeeItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * (FFeeItem)表服务实现类
 *
 * @author 韩旭
 * @date 2021-03-29 11:10:55
 */
@Service
public class FFeeItemServiceImpl implements FFeeItemService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(FFeeItemServiceImpl.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private FFeeItemMapper fFeeItemMapper;

    @Override
    public Pages<FFeeItemDTO> getList(FFeeItemSearchDTO fFeeItemSearchDTO) {
        final String methodName = "getList";
        LOGGER.enter(methodName, "业务执行");

        Pages<FFeeItemDTO> pages = PageHelperUtils.limit(fFeeItemSearchDTO, () -> {
            return fFeeItemMapper.getList(fFeeItemSearchDTO);
        });

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }

    @Override
    public FFeeItemDTO getById(Long id) {
        final String methodName = "getById";
        LOGGER.enter(methodName, "业务执行");

        FFeeItemDTO fFeeItemDTO = fFeeItemMapper.getById(id);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return fFeeItemDTO;
    }

    @Override
    @Transactional
    public int save(FFeeItemDTO fFeeItemDTO) {
        final String methodName = "save";
        LOGGER.enter(methodName, "业务执行");

        int count = 0;
        // 新增的场合
        if (fFeeItemDTO.getId() == null) {
            //id
            fFeeItemDTO.setId(snowflake.nextId());

            count = fFeeItemMapper.insert(fFeeItemDTO);

            LOGGER.exit(methodName, StringUtils.EMPTY);
            // 修改的场合
        } else {

            count = fFeeItemMapper.update(fFeeItemDTO);

            LOGGER.exit(methodName, StringUtils.EMPTY);
        }
        return count;
    }
}
