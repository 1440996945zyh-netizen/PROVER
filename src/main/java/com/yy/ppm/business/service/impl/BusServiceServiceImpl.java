package com.yy.ppm.business.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.PinYin4jUtils;
import com.yy.ppm.business.bean.dto.TBusServiceDTO;
import com.yy.ppm.business.bean.dto.BusServiceSearchDTO;
import com.yy.ppm.business.mapper.BusServiceMapper;
import com.yy.ppm.business.service.BusServiceService;
import com.yy.ppm.common.mapper.CommonMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * (BusService)表服务实现类
 *
 * @author 韩旭
 * @date 2021-03-18 10:52:04
 */
@Service
public class BusServiceServiceImpl implements BusServiceService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(BusServiceServiceImpl.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private BusServiceMapper busServiceMapper;

    @Resource
    private CommonMapper commonMapper;

    @Override
    public Pages<TBusServiceDTO> getList(BusServiceSearchDTO busServiceSearchDTO) {
        final String methodName = "getList";
        LOGGER.enter(methodName, "业务执行");

        Pages<TBusServiceDTO> pages = PageHelperUtils.limit(busServiceSearchDTO, () -> {
            return busServiceMapper.getList(busServiceSearchDTO);
        });

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }

    @Override
    public TBusServiceDTO getById(Long id) {
        final String methodName = "getById";
        LOGGER.enter(methodName, "业务执行");

        TBusServiceDTO busServiceDTO = busServiceMapper.getById(id);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return busServiceDTO;
    }

    @Override
    @Transactional
    public int save(TBusServiceDTO busServiceDTO) {
        final String methodName = "save";
        LOGGER.enter(methodName, "业务执行");

        int count = 0;

        //如果助记码为空，则自动生成。
        if (StringUtils.isEmpty(busServiceDTO.getShortCd())) {
            busServiceDTO.setShortCd(PinYin4jUtils.getPinYinHeadChar(busServiceDTO.getServiceNm(), busServiceDTO.getServiceNm().length()));
        }

        // 新增的场合
        if (busServiceDTO.getId() == null) {
            //id
            busServiceDTO.setId(snowflake.nextId());
            //保存服务
            count = busServiceMapper.insert(busServiceDTO);

            LOGGER.exit(methodName, StringUtils.EMPTY);
            // 修改的场合
        } else {

            count = busServiceMapper.update(busServiceDTO);
            //删除服务表和主票货关系表
            commonMapper.delete("t_bus_service_process", "bus_service_gid", busServiceDTO.getId() + "");

            LOGGER.exit(methodName, StringUtils.EMPTY);
        }
        //把客户属性添加到客户属性表
        count = busServiceMapper.insertBusServiceProcess(busServiceDTO);
        return count;
    }
}
