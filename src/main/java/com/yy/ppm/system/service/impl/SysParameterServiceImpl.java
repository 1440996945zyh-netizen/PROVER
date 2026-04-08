package com.yy.ppm.system.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.bean.dto.SysParameterLogDTO;
import com.yy.ppm.system.mapper.SysParameterLogMapper;
import com.yy.ppm.system.mapper.SysParameterMapper;
import com.yy.ppm.system.service.SysParameterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 系统参数(SysParameter)表服务实现类
 *
 * @author 张超
 * @date 2021-03-02 16:29:15
 */
@Service
public class SysParameterServiceImpl implements SysParameterService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(SysParameterServiceImpl.class);

    @Resource
    private SysParameterMapper sysParameterMapper;

    @Resource
    private SysParameterLogMapper sysParameterLogMapper;


    private final SecurityUtils securityUtils;

    private final Snowflake snowflake;

    public SysParameterServiceImpl(SecurityUtils securityUtils,Snowflake snowflake){
        this.snowflake = snowflake;
        this.securityUtils = securityUtils;
    }

    @Override
    public List<SysParameterDTO> getList(SysParameterDTO sysParameterDTO) {
        final String methodName = "SysParameterServiceImpl:getList";
        LOGGER.enter(methodName, "业务执行");

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return sysParameterMapper.getList(sysParameterDTO);
    }

    @Override
    @Transactional
    public void save(List<SysParameterDTO> parameterList) {
        final String methodName = "SysParameterServiceImpl:save";
        LOGGER.enter(methodName, "业务执行");

        List<String> codes = new ArrayList<>();

        // 重复验证
        for(SysParameterDTO dto:parameterList) {
            if (codes.indexOf(dto.getParamCd()) >= 0) {
                throw new BusinessRuntimeException("参数编号重复~");
            }
            codes.add(dto.getParamCd());
        }


        SysParameterDTO sysParameterDTO = new SysParameterDTO();
        sysParameterDTO.setFlag("2");
        List<SysParameterDTO> list = sysParameterMapper.getList(sysParameterDTO);

        Map<Long, SysParameterDTO> idToParameterMap = list.stream().collect(Collectors.toMap(SysParameterDTO::getId, Function.identity()));

        //删除全部数据
//        commonMapper.deleteAll("sys_parameter");
        // 批量新增
        for(SysParameterDTO dto:parameterList) {
            if(dto.getId() == null){
                dto.setId(snowflake.nextId());
                dto.setFlag("2");
                sysParameterMapper.insert(dto);
                //插入日志
                SysParameterLogDTO sysParameterLogDTO = new SysParameterLogDTO();
                sysParameterLogDTO.setId(snowflake.nextId());
                sysParameterLogDTO.setParamCdNew(dto.getParamCd());
                sysParameterLogDTO.setParamNmNew(dto.getParamNm());
                sysParameterLogDTO.setRemarkNew(dto.getRemark());
                sysParameterLogDTO.setParamValNew(dto.getParamVal());
                sysParameterLogDTO.setFlag("2");
                sysParameterLogDTO.setOperationType(1L);
                sysParameterLogDTO.setCreateBy(securityUtils.getLoginUserId());
                sysParameterLogDTO.setCreateByName(securityUtils.getLoginUserName());
                sysParameterLogDTO.setCreateTime(new Date());
                sysParameterLogMapper.insert(sysParameterLogDTO);
            }else{
                dto.setFlag("2");
                sysParameterMapper.update(dto);
                //插入日志
                SysParameterDTO oldDto = idToParameterMap.get(dto.getId());
                if(!oldDto.equals(dto)){
                    SysParameterLogDTO sysParameterLogDTO = new SysParameterLogDTO();
                    sysParameterLogDTO.setId(snowflake.nextId());
                    sysParameterLogDTO.setParamCdNew(dto.getParamCd());
                    sysParameterLogDTO.setParamNmNew(dto.getParamNm());
                    sysParameterLogDTO.setRemarkNew(dto.getRemark());
                    sysParameterLogDTO.setParamValNew(dto.getParamVal());
                    sysParameterLogDTO.setParamCdOld(oldDto.getParamCd());
                    sysParameterLogDTO.setParamNmOld(oldDto.getParamNm());
                    sysParameterLogDTO.setParamValOld(oldDto.getParamVal());
                    sysParameterLogDTO.setRemarkOld(oldDto.getRemark());
                    sysParameterLogDTO.setFlag("2");
                    sysParameterLogDTO.setOperationType(2L);
                    sysParameterLogDTO.setCreateBy(securityUtils.getLoginUserId());
                    sysParameterLogDTO.setCreateByName(securityUtils.getLoginUserName());
                    sysParameterLogDTO.setCreateTime(new Date());
                    sysParameterLogMapper.insert(sysParameterLogDTO);
                }
            }

        }

        LOGGER.exit(methodName, StringUtils.EMPTY);

    }

    @Override
    public SysParameterDTO getConfig(String code) {
        return sysParameterMapper.getByKey(code);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {

        SysParameterDTO sysParameterDTO = sysParameterMapper.getById(id);
        //写入日志
        SysParameterLogDTO sysParameterLogDTO = new SysParameterLogDTO();
        sysParameterLogDTO.setId(snowflake.nextId());
        sysParameterLogDTO.setParamValOld(sysParameterDTO.getParamVal());
        sysParameterLogDTO.setParamNmOld(sysParameterDTO.getParamNm());
        sysParameterLogDTO.setParamCdOld(sysParameterDTO.getParamCd());
        sysParameterLogDTO.setRemarkOld(sysParameterDTO.getRemark());
        sysParameterLogDTO.setOperationType(0L);
        sysParameterLogDTO.setFlag("2");
        sysParameterLogDTO.setCreateBy(securityUtils.getLoginUserId());
        sysParameterLogDTO.setCreateByName(securityUtils.getLoginUserName());
        sysParameterLogDTO.setCreateTime(new Date());
        sysParameterLogMapper.insert(sysParameterLogDTO);
        return sysParameterMapper.deleteById(id) == 1;

    }

    @Override
    @Transactional
    public void saveUser(List<SysParameterDTO> parameterList) {

        final String methodName = "SysParameterServiceImpl:save";
        LOGGER.enter(methodName, "业务执行");

        List<String> codes = new ArrayList<>();

        // 重复验证
        for(SysParameterDTO dto:parameterList) {
            if (codes.indexOf(dto.getParamCd()) >= 0) {
                throw new BusinessRuntimeException("参数编号重复~");
            }
            codes.add(dto.getParamCd());
        }
        //用户
        SysParameterDTO sysParameterDTO = new SysParameterDTO();
        sysParameterDTO.setFlag("1");
        List<SysParameterDTO> list = sysParameterMapper.getList(sysParameterDTO);

        Map<Long, SysParameterDTO> idToParameterMap = list.stream().collect(Collectors.toMap(SysParameterDTO::getId, Function.identity()));


        // 批量新增
        for(SysParameterDTO dto:parameterList) {
            if(dto.getId() == null){
                dto.setId(snowflake.nextId());
                dto.setFlag("1");
                sysParameterMapper.insert(dto);
                //插入日志
                SysParameterLogDTO sysParameterLogDTO = new SysParameterLogDTO();
                sysParameterLogDTO.setId(snowflake.nextId());
                sysParameterLogDTO.setParamCdNew(dto.getParamCd());
                sysParameterLogDTO.setParamNmNew(dto.getParamNm());
                sysParameterLogDTO.setRemarkNew(dto.getRemark());
                sysParameterLogDTO.setParamValNew(dto.getParamVal());
                sysParameterLogDTO.setFlag("1");
                sysParameterLogDTO.setOperationType(1L);
                sysParameterLogDTO.setCreateBy(securityUtils.getLoginUserId());
                sysParameterLogDTO.setCreateByName(securityUtils.getLoginUserName());
                sysParameterLogDTO.setCreateTime(new Date());
                sysParameterLogMapper.insert(sysParameterLogDTO);
            }else{
                dto.setFlag("1");
                sysParameterMapper.update(dto);
                //插入日志
                SysParameterDTO oldDto = idToParameterMap.get(dto.getId());
                if(!oldDto.equals(dto)){
                    SysParameterLogDTO sysParameterLogDTO = new SysParameterLogDTO();
                    sysParameterLogDTO.setId(snowflake.nextId());
                    sysParameterLogDTO.setParamCdNew(dto.getParamCd());
                    sysParameterLogDTO.setParamNmNew(dto.getParamNm());
                    sysParameterLogDTO.setRemarkNew(dto.getRemark());
                    sysParameterLogDTO.setParamValNew(dto.getParamVal());
                    sysParameterLogDTO.setParamCdOld(oldDto.getParamCd());
                    sysParameterLogDTO.setParamNmOld(oldDto.getParamNm());
                    sysParameterLogDTO.setParamValOld(oldDto.getParamVal());
                    sysParameterLogDTO.setRemarkOld(oldDto.getRemark());
                    sysParameterLogDTO.setFlag("1");
                    sysParameterLogDTO.setOperationType(2L);
                    sysParameterLogDTO.setCreateBy(securityUtils.getLoginUserId());
                    sysParameterLogDTO.setCreateByName(securityUtils.getLoginUserName());
                    sysParameterLogDTO.setCreateTime(new Date());
                    sysParameterLogMapper.insert(sysParameterLogDTO);
                }
            }

        }

        LOGGER.exit(methodName, StringUtils.EMPTY);

    }

    @Override
    public List<SysParameterDTO> getUserList(SysParameterDTO sysParameterDTO) {
        final String methodName = "SysParameterServiceImpl:getUserList";
        LOGGER.enter(methodName, "业务执行");

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return sysParameterMapper.getUserList(sysParameterDTO);
    }

    @Override
    @Transactional
    public boolean deleteUserById(Long id) {
        SysParameterDTO sysParameterDTO = sysParameterMapper.getById(id);
        //写入日志
        SysParameterLogDTO sysParameterLogDTO = new SysParameterLogDTO();
        sysParameterLogDTO.setId(snowflake.nextId());
        sysParameterLogDTO.setParamValOld(sysParameterDTO.getParamVal());
        sysParameterLogDTO.setParamNmOld(sysParameterDTO.getParamNm());
        sysParameterLogDTO.setParamCdOld(sysParameterDTO.getParamCd());
        sysParameterLogDTO.setRemarkOld(sysParameterDTO.getRemark());
        sysParameterLogDTO.setOperationType(0L);
        sysParameterLogDTO.setFlag("1");
        sysParameterLogDTO.setCreateBy(securityUtils.getLoginUserId());
        sysParameterLogDTO.setCreateByName(securityUtils.getLoginUserName());
        sysParameterLogDTO.setCreateTime(new Date());
        sysParameterLogMapper.insert(sysParameterLogDTO);
        return sysParameterMapper.deleteUserById(id) == 1;
    }



}
