package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.MOperationProcessDTO;
import com.yy.ppm.master.bean.dto.MOperationSubProcessDTO;
import com.yy.ppm.master.bean.po.MOperationProcessPO;
import com.yy.ppm.master.bean.po.MOperationSubProcessPO;
import com.yy.ppm.master.mapper.MOperationProcessMapper;
import com.yy.ppm.master.service.MOperationProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业过程Service业务层处理
 */
@RequiredArgsConstructor
@Service
public class MOperationProcessServiceImpl implements MOperationProcessService {

    @Resource
    MOperationProcessMapper operationProcessMapper;

    @Resource
    private CommonService commonService;

    @Autowired
    private Snowflake snowflake;

    /**
     * 子过程查询
     */
    @Override
    public Pages<MOperationSubProcessDTO> listSubProcess(String processCode, PageParameter pageQuery, String name) {

        Pages<MOperationSubProcessDTO> typePages = PageHelperUtils.limit(pageQuery,
                () -> operationProcessMapper.selectAllSubProcess(processCode, name));
        return typePages;
    }

    /**
     * 查询作业过程列表
     */
    @Override
    public Pages<MOperationProcessDTO> listOperationProcess(PageParameter pageQuery, String name) {

        Pages<MOperationProcessDTO> typePages = PageHelperUtils.limit(pageQuery,
                () -> operationProcessMapper.selectAllProcess(name));

        return typePages;
    }

    /**
     * 根据id查询某个作业过程
     */
    @Override
    public MOperationProcessDTO selectOneById(Long id) {
        //查询一个过程
        return operationProcessMapper.selectOneById(id);
    }

    /**
     * 根据id查询某个子作业过程
     */
    @Override
    public MOperationSubProcessPO selectOneSubById(Long id) {
        //查询一个子过程
        return operationProcessMapper.selectOneSubById(id);
    }

    /**
     * 新增作业过程
     */
    @Override
    public void insertByBo(MOperationProcessPO bo) {
        bo.setId(snowflake.nextId());

        commonService.isRepeate("M_OPERATION_PROCESS", "PROCESS_NAME", bo.getProcessName(), StringUtil.getString(bo.getId()), "过程名称~", null);

        String maxCode = operationProcessMapper.getMaxProcessCode();
        if (maxCode == null || maxCode.length() == 0) {
            maxCode = "0001";
        } else {
            Integer temp = Integer.parseInt(maxCode);
            temp++;
            //不足4位 前面补0
            maxCode = org.apache.commons.lang3.StringUtils.leftPad(temp + "", 4, "0");
        }

        bo.setProcessCode(maxCode);

        operationProcessMapper.insert(bo);
    }

    /**
     * 修改作业过程
     */
    @Override
    public void updateProcess(MOperationProcessPO bo) {

        operationProcessMapper.updateProcess(bo);
    }

    /**
     * 删除作业过程
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public int deleteById(List<Long> ids) {
        List<String> code = new ArrayList<>();
        ids.forEach(id -> {
            MOperationProcessPO bo = operationProcessMapper.selectOneById(id);
            if (bo != null) {
                code.add(bo.getProcessCode());
            }
        });
        //查询作业过程下是否有子过程
        Long count1 = operationProcessMapper.selectCount(code);
        //查询作业过程下是否有作业工艺
        Long count = operationProcessMapper.selectCountTechnique(code);


        if (count == 0 && count1 == 0) {
            //若没有子过程和作业工艺，则删除
            operationProcessMapper.deleteById(ids);
            return 1;
        } else if (count > 0) {
            return 0;
        }
        return 2;
    }

    /**
     * 修改子作业过程
     */
    @Override
    public void updateSubProcess(MOperationSubProcessPO bo) {
        operationProcessMapper.updateSubProcess(bo);
    }

    /**
     * 删除子作业过程
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public int deleteSubById(Long id) {
        return operationProcessMapper.deleteSubById(id);
    }

    /**
     * 新增子作业过程
     */
    @Override
    public void insertSub(MOperationSubProcessPO bo) {
        bo.setId(snowflake.nextId());

        String maxModelCode = operationProcessMapper.getMaxSubprocessCode(bo.getProcessCode());
        if (maxModelCode == null || maxModelCode.length() == 0) {
            maxModelCode = bo.getProcessCode() + "0001";
        } else {
            Integer temp = Integer.parseInt(maxModelCode);
            temp++;
            //不足8位 前面补0
            maxModelCode = org.apache.commons.lang3.StringUtils.leftPad(temp + "", 8, "0");
        }
        bo.setSubprocessCode(maxModelCode);
        operationProcessMapper.insertSubProcess(bo);
    }

    /**
     * 查询作业过程
     */
    @Override
    public List<MOperationProcessPO> selectOperationProcess(String name) {
        return operationProcessMapper.selectOperationProcess(name);
    }

    /**
     * 查询所有作业过程及子过程
     */
    @Override
    public Pages<MOperationProcessDTO> selectAll(PageParameter pageQuery, String name) {
        Pages<MOperationProcessDTO> typePages = PageHelperUtils.limit(pageQuery,
                () -> operationProcessMapper.selectAllProcess(name));

        typePages.getPages().forEach(type -> {
            //查询所有子过程
            type.setList(operationProcessMapper.selectAllSubProcess(null, name));
        });

        return typePages;
    }
}
