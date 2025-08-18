package com.yy.ppm.business.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.PinYin4jUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.business.bean.dto.BusServiceSearchDTO;
import com.yy.ppm.business.bean.dto.TBusServiceDTO;
import com.yy.ppm.business.bean.dto.reCargoName.ReCargoNameDTO;
import com.yy.ppm.business.mapper.BusServiceMapper;
import com.yy.ppm.business.mapper.ReCargoNameMapper;
import com.yy.ppm.business.service.ReCargoNameService;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.master.bean.dto.MCargoCategoryDTO;
import com.yy.ppm.master.bean.dto.MCargoDTO;
import com.yy.ppm.master.bean.dto.MCargoSearchDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;

/**
 * (BusService)表服务实现类
 *
 * @author 韩旭
 * @date 2021-03-18 10:52:04
 */
@Service
public class ReCargoNameServiceImpl implements ReCargoNameService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(ReCargoNameServiceImpl.class);
    @Autowired
    private SecurityUtils securityUtils;


    @Autowired
    private Snowflake snowflake;

    @Resource
    private ReCargoNameMapper reCargoNameMapper;

    @Override
    public Pages<MCargoDTO> getList(MCargoSearchDTO searchDTO) {
        if(StringUtils.isNotBlank(searchDTO.getCargoName())){
            searchDTO.setCargoName(searchDTO.getCargoName().trim());
        }
        if(StringUtils.isNotBlank(searchDTO.getCargoCategoryName())){
            searchDTO.setCargoCategoryName(searchDTO.getCargoCategoryName().trim());
        }
        Pages<MCargoDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return reCargoNameMapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void update(ReCargoNameDTO reCargoNameDTO) {
        reCargoNameMapper.update(reCargoNameDTO);
        //保存修改记录
        reCargoNameDTO.setId(snowflake.nextId());
        reCargoNameDTO.setCreateByName(securityUtils.getLoginUserName());
        reCargoNameDTO.setCreateBy(securityUtils.getLoginUserId());
        reCargoNameDTO.setCreateTime(new Date());
        reCargoNameMapper.save(reCargoNameDTO);
    }
}
