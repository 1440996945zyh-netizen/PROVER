package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.CommonEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.PinYin4jUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.MPortDTO;
import com.yy.ppm.master.bean.dto.MPortSearchDTO;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.dto.MStopReasonDTO;
import com.yy.ppm.master.bean.po.MPortPO;
import com.yy.ppm.master.mapper.MPortMapper;
import com.yy.ppm.master.service.MPortService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 港口信息操作实现类
 * @author yangcl
 * */
@Service
public class MPortServiceImpl implements MPortService {

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
    MPortMapper mPortMapper;

    @Resource
    CommonService commonService;

    /**
     * 查询港口信息集合
     * */
    @Override
    public Pages<MPortDTO> getList(MPortSearchDTO searchDTO) {

        Pages<MPortDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mPortMapper.getList(searchDTO);
        });

        return pages;
    }

    /**
     * 根据id获取港口信息
     * */
    @Override
    public MPortDTO getPortById(Long id) {
        final String methodName = "MPortServiceImpl:getPortById";
        LOGGER.enter(methodName, "业务执行");

        MPortDTO po = mPortMapper.getPortById(id);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return po;
    }

    /**
     * 更新港口信息
     * @param po
     * @return
     */
    @Override
    public int savePort(MPortDTO po) {

        // 泊位code name验证重复
        commonService.isRepeate("M_PORT", "PORT_CODE", po.getPortCode(), StringUtil.getString(po.getId()), "港口代码", null);
        commonService.isRepeate("M_PORT", "PORT_NAME", po.getPortName(), StringUtil.getString(po.getId()), "港口名称", null);

        if (po.getSortNum() == null) {
            po.setSortNum(commonService.getNextValue("M_PORT", "sort_num", null));
        }

        //如果助记码为空，则自动生成。
        if (StringUtils.isEmpty(po.getShorthandCode())) {
            po.setShorthandCode(PinYin4jUtils.getPinYinHeadChar(po.getPortName(), po.getPortName().length()));
        }

        // 国内的场合
        if ("1".equals(po.getIsDomestic())) {
            po.setNationCode("87");
            po.setNationName("中国");
        }

        if (po.getId() == null) {
            po.setId(snowflake.nextId());
            return mPortMapper.insertPort(po);

        } else {
            return mPortMapper.updatePort(po);
        }
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    public int deleteById(Long id) {
        return mPortMapper.deleteById(id);
    }

}
