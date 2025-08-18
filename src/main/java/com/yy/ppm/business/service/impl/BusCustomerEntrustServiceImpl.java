package com.yy.ppm.business.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.mapper.BusCustomerEntrustMapper;
import com.yy.ppm.business.service.BusCustomerEntrustService;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.service.CommonService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusCustomerEntrustServiceImpl implements BusCustomerEntrustService {

    @Autowired
    BusCustomerEntrustMapper entrustMapper;
    @Autowired
    private Snowflake snowflake;
    @Autowired
    private CommonService commonService;

    @Override
    public Pages<TBusCustomerEntrustDTO> getList(TBusCustomerEntrustReqDTO tBusCustomerEntrustReqDTO) {
        return PageHelperUtils.limit(tBusCustomerEntrustReqDTO, () -> {
            return entrustMapper.getList(tBusCustomerEntrustReqDTO);
        });
    }
    @Override
    public List<TBusEntrustDetailDTO> getDetailList(TBusEntrustDetailReqDTO query) {
        List<TBusEntrustDetailDTO> result = entrustMapper.getEntrustDetailList(query);
        return result;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void saveOrUpdate(TBusCustomerEntrustDTO dto) {
        //新增
        if (dto.getId()==null) {
            //插入票货
            dto.setId(snowflake.nextId());
            dto.setStatus("10");
            dto.setEntrustNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.ENTRUST_NO, "WT" + new DateTime(new Date()).toString("yyMMdd")));
            dto.setCustomerName(dto.getCargoList().stream().map(TBusEntrustDetailDTO::getCargoOwnerName).distinct().collect(Collectors.joining(",")));
            dto.setCargoName(dto.getCargoList().stream().map(TBusEntrustDetailDTO::getCargoName).distinct().collect(Collectors.joining(",")));
            entrustMapper.insertEntrust(dto);
            dto.getCargoList().forEach(o->{
                o.setId(snowflake.nextId());
                o.setEntrustId(dto.getId());
                o.setCompanyId(dto.getCompanyId());
                o.setCompanyName(dto.getCompanyName());
            });
            entrustMapper.insertEntrustDetail(dto.getCargoList());

            //修改
        }else {
            TBusEntrustDetailReqDTO detailQuery = new TBusEntrustDetailReqDTO();
            detailQuery.setEntrustId(dto.getId());

            List<TBusEntrustDetailDTO> entrustDetailList = entrustMapper.getEntrustDetailList(detailQuery);
            if(entrustDetailList.isEmpty()){
               throw new BusinessRuntimeException("没有要进行更新的提货委托单");
            }
            //区分增删改的货物信息
            ArrayList<TBusEntrustDetailDTO> addDetailList = new ArrayList<>();
            ArrayList<TBusEntrustDetailDTO> updateDetailList = new ArrayList<>();
            ArrayList<TBusEntrustDetailDTO> delDetailList = new ArrayList<>();
            List<Long> sourceDetailId = entrustDetailList.stream().map(TBusEntrustDetailDTO::getId).collect(Collectors.toList());
            dto.getCargoList().forEach(o->{
                o.setCompanyName(dto.getCompanyName());
                o.setCompanyId(dto.getCompanyId());
                if(o.getId()!=null){
                    if(sourceDetailId.contains(o.getId())){
                        updateDetailList.add(o);
                    }else {
                        delDetailList.add(o);
                    }
                }else{
                    o.setId(snowflake.nextId());
                    o.setEntrustId(dto.getId());
                    addDetailList.add(o);
                }
            });
            //增删改
            if (!addDetailList.isEmpty()) {
                entrustMapper.insertEntrustDetail(addDetailList);
            }
            if(!updateDetailList.isEmpty()){
                entrustMapper.updateEntrustDetail(updateDetailList);
            }
            if(!delDetailList.isEmpty()){
                entrustMapper.delEntrustDetailByIds(dto.getId(),delDetailList.stream().map(TBusEntrustDetailDTO::getId).collect(Collectors.toList()));
            }
            dto.setCustomerName(dto.getCargoList().stream().map(TBusEntrustDetailDTO::getCargoOwnerName).distinct().collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
            dto.setCustomerName(dto.getCargoList().stream().map(TBusEntrustDetailDTO::getCargoName).distinct().collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
            //更新主表
            entrustMapper.updateEntrust(dto);

        }
    }

    /**
     * 提货委托单删除
     * @param entrustId
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public Boolean delCustomerEntrust(Long entrustId) {
//        判断提货委托单生成的票货是否已经下了通知单，若是存在就不允许删除

        TBusEntrustDetailReqDTO tBusEntrustDetailReqDTO = new TBusEntrustDetailReqDTO();
        tBusEntrustDetailReqDTO.setEntrustId(entrustId);
        List<TBusEntrustDetailDTO> entrustDetailList = entrustMapper.getEntrustDetailList(tBusEntrustDetailReqDTO);
        if (!entrustDetailList.isEmpty()) {
            List<TBusTrustDTO>  tmpEntrustDTOS= entrustMapper.checkCargoTrust (entrustDetailList.stream().map(TBusEntrustDetailDTO::getCargoInfoId).collect(Collectors.toList()));
            if (!tmpEntrustDTOS.isEmpty()) {
                throw new BusinessRuntimeException("已经生成通知单不能删除");
            }
        }
        TBusCustomerEntrustReqDTO tBusCustomerEntrustReqDTO = new TBusCustomerEntrustReqDTO();
        tBusCustomerEntrustReqDTO.setId(entrustId);
        Page<TBusCustomerEntrustDTO> list = entrustMapper.getList(tBusCustomerEntrustReqDTO);
        if (list.get(0).getTrustId()!=null) {
            throw new BusinessRuntimeException("委托单已经关联集港通知单不能删除");
        }

        //删除子表

        entrustMapper.delEntrustDetailByIds(entrustId,entrustDetailList.stream().map(TBusEntrustDetailDTO::getId).collect(Collectors.toList()));

        //删除主表
        entrustMapper.delCustomerEntrustById(entrustId);

        return Boolean.TRUE;
    }

    @Override
    public TBusCustomerEntrustDTO getEntrust(Long entrustId) {
        TBusCustomerEntrustReqDTO tBusCustomerEntrustReqDTO = new TBusCustomerEntrustReqDTO();
        tBusCustomerEntrustReqDTO.setId(entrustId);
        Page<TBusCustomerEntrustDTO> list = entrustMapper.getList(tBusCustomerEntrustReqDTO);
        if(list.isEmpty()){
            throw new BusinessRuntimeException("未查询到客户委托单数据，请返回主列表刷新页面重试");
        }
        TBusCustomerEntrustDTO result = list.get(0);
        TBusEntrustDetailReqDTO tBusEntrustDetailReqDTO = new TBusEntrustDetailReqDTO();
        tBusEntrustDetailReqDTO.setEntrustId(entrustId);
        result.setCargoList(entrustMapper.getEntrustDetailList(tBusEntrustDetailReqDTO));
        return result;
    }

    @Override
    public TBusCustomerEntrustDTO getCustomerEntrustForAddTrust(Long entrustId) {
        TBusCustomerEntrustReqDTO tBusCustomerEntrustReqDTO = new TBusCustomerEntrustReqDTO();
        tBusCustomerEntrustReqDTO.setId(entrustId);
        TBusCustomerEntrustDTO result = entrustMapper.getCustomerEntrustForAddTrust(tBusCustomerEntrustReqDTO);
        TBusEntrustDetailReqDTO tBusEntrustDetailReqDTO = new TBusEntrustDetailReqDTO();
        tBusEntrustDetailReqDTO.setEntrustId(entrustId);
        result.setCargoList(entrustMapper.getEntrustDetailListForTrustAdd(tBusEntrustDetailReqDTO));
        return result;
    }
}
