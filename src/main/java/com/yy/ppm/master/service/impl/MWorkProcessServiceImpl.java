package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.MessageUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.PinYin4jUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.master.bean.dto.MWorkProcessDTO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.master.mapper.MWorkProcessMapper;
import com.yy.ppm.master.service.MWorkProcessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * (MWorkProcess)表服务实现类
 *
 * @author 张超
 * @date 2021-03-10 13:57:00
 */
@Service
public class MWorkProcessServiceImpl implements MWorkProcessService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(MWorkProcessServiceImpl.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private MWorkProcessMapper mWorkProcessMapper;

    @Resource
    private CommonMapper commonMapper;

    @Resource
    private MessageUtils messageUtils;

    @Override
    public Pages<MWorkProcessDTO> getList(MWorkProcessSearchDTO mWorkProcessSearchDTO) {
        final String methodName = "MWorkProcessServiceImpl: getList";
        LOGGER.enter(methodName, "业务执行");

        Pages<MWorkProcessDTO> pages = PageHelperUtils.limit(mWorkProcessSearchDTO, () -> {
            return mWorkProcessMapper.getList(mWorkProcessSearchDTO);
        });

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }

    @Override
    public MWorkProcessDTO getById(Long id) {
        final String methodName = "MWorkProcessServiceImpl: getById";
        LOGGER.enter(methodName, "业务执行");

        MWorkProcessDTO mWorkProcessDTO = mWorkProcessMapper.getById(id);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return mWorkProcessDTO;
    }

    @Override
    @Transactional
    public int save(MWorkProcessDTO mWorkProcessDTO) {
        final String methodName = "MWorkProcessServiceImpl: save";
        LOGGER.enter(methodName, "业务执行");

        //如果助记码为空，则自动生成。
        if (StringUtils.isEmpty(mWorkProcessDTO.getShortCd())) {
            mWorkProcessDTO.setShortCd(PinYin4jUtils.getPinYinHeadChar(mWorkProcessDTO.getProcessNm(), mWorkProcessDTO.getProcessNm().length()));
        }

        //获取排序号
        if (mWorkProcessDTO.getSortNum() == null) {
            mWorkProcessDTO.setSortNum(commonMapper.getNextValue("m_work_process", "sort_num", null));
        }

        int count = 0;
        // 新增的场合
        if (mWorkProcessDTO.getId() == null) {
            //id
            mWorkProcessDTO.setId(snowflake.nextId());

            count = mWorkProcessMapper.insert(mWorkProcessDTO);

            LOGGER.exit(methodName, StringUtils.EMPTY);
            // 修改的场合
        } else {

            count = mWorkProcessMapper.update(mWorkProcessDTO);

            LOGGER.exit(methodName, StringUtils.EMPTY);
        }
        return count;
    }

    @Override
    @Transactional
    public int saveChildProcess(MWorkProcessDTO mWorkProcessDTO) {
        final String methodName = "MWorkProcessServiceImpl: saveChildProcess";
        LOGGER.enter(methodName, "业务执行");

        //如果助记码为空，则自动生成。
        if (StringUtils.isEmpty(mWorkProcessDTO.getShortCd())) {
            mWorkProcessDTO.setShortCd(PinYin4jUtils.getPinYinHeadChar(mWorkProcessDTO.getProcessNm(), mWorkProcessDTO.getProcessNm().length()));
        }

        //获取排序号
        if (mWorkProcessDTO.getSortNum() == null) {
            mWorkProcessDTO.setSortNum(commonMapper.getNextValue("m_work_process", "sort_num", null));
        }

        int count = 0;
        // 新增的场合
        if (mWorkProcessDTO.getId() == null) {
            //id
            mWorkProcessDTO.setId(snowflake.nextId());

            count = mWorkProcessMapper.insert(mWorkProcessDTO);

            LOGGER.exit(methodName, StringUtils.EMPTY);
            // 修改的场合
        } else {

            count = mWorkProcessMapper.update(mWorkProcessDTO);

            LOGGER.exit(methodName, StringUtils.EMPTY);
        }
        return count;
    }

    /**
     * 判断子过程是否重复*/
    @Override
    public void isRepeateSubProcess(MWorkProcessDTO mWorkProcessDTO) {
        int count = mWorkProcessMapper.isRepeateSubProcess(mWorkProcessDTO.getParentId(),mWorkProcessDTO.getProcessNm(),mWorkProcessDTO.getProcessCd());
        if(count>=1){
            throw new BusinessRuntimeException("重复的作业过程名称");
        }
    }
}
