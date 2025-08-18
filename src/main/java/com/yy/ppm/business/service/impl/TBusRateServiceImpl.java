package com.yy.ppm.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;
import com.yy.common.util.SecurityUtils;

import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.service.TBusRateService;
import com.yy.ppm.business.mapper.TBusRateMapper;
import com.yy.ppm.master.bean.dto.MCargoDTO;
import com.yy.ppm.master.mapper.MCargoMapper;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkPlanDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 费率(TBusRate)ServiceImpl
 * @Description
 * @createTime 2023年07月03日 16:48:00
 */
@Service
public class TBusRateServiceImpl implements TBusRateService {

    @Resource
    private TBusRateMapper tBusRateMapper;
    @Resource
    private MCargoMapper mCargoTypeMapper;
    @Resource
    private Snowflake snowflake;
    @Resource
    private SecurityUtils securityUtils;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TBusRateDTO> getList(TBusRateSearchDTO searchDTO) {

        Pages<TBusRateDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            searchDTO.setDataSource(1);
            return tBusRateMapper.getList(searchDTO);
        });

        return pages;
    }

    @Override
    public Pages<TBusRateDTO> getListCargo(TBusRateSearchDTO searchDTO) {

        Pages<TBusRateDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            searchDTO.setDataSource(2);
            return tBusRateMapper.getListCargo(searchDTO);
        });

        return pages;
    }

    @Override
    public void busRatePassCargo(TBusRateDTO tBusRateDTO) {
        if ("10".equals(tBusRateDTO.getStatus())) {
            throw new BusinessRuntimeException("该包干费已经审核通过！");
        }
        tBusRateDTO.setStatus("10");
        tBusRateMapper.busRatePassCargo(tBusRateDTO);
    }

    @Override
    public void busRateRevokeCargo(TBusRateDTO tBusRateDTO) {
        if ("1".equals(tBusRateDTO.getStatus())) {
            throw new BusinessRuntimeException("该包干费还未审核！");
        }
        tBusRateDTO.setStatus("1");
        tBusRateMapper.busRateRevokeCargo(tBusRateDTO);
    }

    @Override
    public void delRateCargo(TBusRateDTO tBusRateDTO) {
        tBusRateMapper.delRateCargo(tBusRateDTO);
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public TBusRateDTO getDetail(Long id) {
        return tBusRateMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TBusRateDTO dto) {

        // 新增
        if (dto.getId() == null) {

            //根据时间段判重
//            int count = tBusRateMapper.selectByTime(dto.getStartDate(),dto.getEndDate());
//            if(count > 0){
//                throw new BusinessRuntimeException("该时间段已有费率信息！");
//            }
            dto.setId(snowflake.nextId());
            dto.setStatus("1");
            dto.setDataSource(1);
            MCargoDTO mCargoDTO = mCargoTypeMapper.getCargoByCargoCode(dto.getCargoCode());
            if (mCargoDTO != null) {
                dto.setCargoName(mCargoDTO.getCargoName());
            }
            return tBusRateMapper.insert(dto) == 1;

            // 修改
        } else {
            dto.setUpdateBy(securityUtils.getLoginUserId());
            dto.setUpdateByName(securityUtils.getLoginUserName());
            dto.setUpdateTime(new Date());
            dto.setDataSource(1);
            return tBusRateMapper.update(dto) == 1;
        }

    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return tBusRateMapper.deleteById(id) == 1;
    }

    @Override
    public boolean busRatePass(Long id) {
        TBusRateDTO tBusRateDTO = tBusRateMapper.getById(id);
        if (!("1".equals(tBusRateDTO.getStatus()))) {
            throw new BusinessRuntimeException("状态不是审核中不允许审核");
        }
        if (StringUtils.isEmpty(tBusRateDTO.getRateCodeEas()) || StringUtils.isEmpty(tBusRateDTO.getRateNameEas())) {
            throw new BusinessRuntimeException("请联系系统管理员添加金蝶计费科目");
        }
        tBusRateDTO.setId(id);
        tBusRateDTO.setStatus("10");
        return tBusRateMapper.updateStatusPassCancle(tBusRateDTO) == 1;
    }

    /**
     * 消审
     *
     * @param id
     * @return
     */
    @Override
    public boolean busRateCancle(Long id) {
        TBusRateDTO tBusRateDTO = tBusRateMapper.getById(id);
        if (!("10".equals(tBusRateDTO.getStatus()))) {
            throw new BusinessRuntimeException("不是已经审核状态，不允许消审");
        }
        tBusRateDTO.setId(id);
        tBusRateDTO.setStatus("1");
        return tBusRateMapper.updateStatusPassCancle(tBusRateDTO) == 1;
    }

    @Override
    public List<TBusServiceDTO> getListService(BusServiceSearchDTO busServiceSearchDTO) {
        return tBusRateMapper.getListService(busServiceSearchDTO);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void doSaveCargo(List<TBusRateDTO> list) {
        if (list != null && list.size() != 0) {
            //根据时间段判重
            int count = tBusRateMapper.selectByTime(list.get(0));
            if (count > 0) {
                throw new BusinessRuntimeException("该时间段已有费率信息！");
            }
            for (TBusRateDTO dto : list) {

                dto.setId(snowflake.nextId());
                dto.setStatus("1");
                dto.setDataSource(2);
                MCargoDTO mCargoDTO = mCargoTypeMapper.getCargoByCargoCode(dto.getCargoCode());
                if (mCargoDTO != null) {
                    dto.setCargoName(mCargoDTO.getCargoName());
                }
                tBusRateMapper.insertCargo(dto);
            }
        } else {
            throw new BusinessRuntimeException("请填写至少一条信息！");
        }

    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void updateCargo(TBusRateUpdateDTO dto) {
        if ("10".equals(dto.getRows().getStatus())) {
            throw new BusinessRuntimeException("当前信息已审核，不能修改");
        }
        //老数据 插入日志
        TBusRateSearchDTO tBusRateSearchDTO = new TBusRateSearchDTO();
        tBusRateSearchDTO.setCargoCode(dto.getRows().getCargoCode());
        tBusRateSearchDTO.setStartDate(dto.getRows().getStartDate());
        tBusRateSearchDTO.setEndDate(dto.getRows().getEndDate());
        List<TBusRateDTO> oldLogList = tBusRateMapper.getDetailCargo(tBusRateSearchDTO);
        if (!CollectionUtil.isEmpty(oldLogList)) {
            for (TBusRateDTO oldPO : oldLogList) {
                oldPO.setRateId(oldPO.getId());
                oldPO.setId(snowflake.nextId());
                oldPO.setFlag("1");
            }
            tBusRateMapper.insertCargoList(oldLogList);
        }

        List<TBusRateDTO> list = dto.getList();
        if (list != null && list.size() != 0) {
            //有id的进行修改
            List<TBusRateDTO> filteredList = list.stream()
                    .filter(obj -> obj.getId() != null && obj.getRate() != null)
                    .collect(Collectors.toList());
            if (!CollectionUtil.isEmpty(filteredList)) {
                tBusRateMapper.updateCargo(filteredList);
            }

            //没有id的,且rate有值的 新增
            List<TBusRateDTO> newList = list.stream()
                    .filter(obj -> obj.getId() == null && obj.getRate() != null)
                    .collect(Collectors.toList());
            if (!CollectionUtil.isEmpty(newList)) {
                for (TBusRateDTO dtos : newList) {
                    dtos.setId(snowflake.nextId());
                    dtos.setStatus("1");
                    dtos.setDataSource(2);
                    MCargoDTO mCargoDTO = mCargoTypeMapper.getCargoByCargoCode(dtos.getCargoCode());
                    dtos.setCargoName(mCargoDTO.getCargoName());
                    tBusRateMapper.insertCargo(dtos);
                }
            }

            //有id的,且rate没有值的 删除
            List<TBusRateDTO> delList = list.stream()
                    .filter(obj -> obj.getId() != null && obj.getRate() == null)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(delList)) {
                List<Long> ids = delList.stream().map(TBusRateDTO::getId).collect(Collectors.toList());
                tBusRateMapper.delRateList(ids);
            }

            // 现数据插入日志
            List<TBusRateDTO> newLogList = new ArrayList<>();
            if (!CollectionUtil.isEmpty(filteredList)) {
                newLogList.addAll(filteredList);
            }
            if (!CollectionUtil.isEmpty(newList)) {
                newLogList.addAll(newList);
            }
            if (!CollectionUtil.isEmpty(newLogList)) {
                for (TBusRateDTO newPO : newLogList) {
                    newPO.setRateId(newPO.getId());
                    newPO.setId(snowflake.nextId());
                    newPO.setFlag("2");
                }
                tBusRateMapper.insertCargoList(newLogList);
            }

        }

    }

    @Override
    public List<TBusRateDTO> getDetailCargo(TBusRateSearchDTO tBusRateSearchDTO) {
        return tBusRateMapper.getDetailCargo(tBusRateSearchDTO);
    }
}

