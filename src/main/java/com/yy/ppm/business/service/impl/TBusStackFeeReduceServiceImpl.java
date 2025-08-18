package com.yy.ppm.business.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusStackFeeReduceDTO;
import com.yy.ppm.business.mapper.TBusStackFeeReduceMapper;
import com.yy.ppm.business.service.TBusStackFeeReduceService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.statement.bean.dto.storageFee.TCostStorageSettleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class TBusStackFeeReduceServiceImpl implements TBusStackFeeReduceService {
    @Resource
    private Snowflake snowflake;
    @Autowired
    private TBusStackFeeReduceMapper mapper;
    @Resource
    private SysFileService sysFileService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void add(TBusStackFeeReduceDTO dto) {
        if(dto.getCargoInfoId()==null){
            throw new BusinessRuntimeException("票货id不允许为空");
        }
        if(!StringUtils.isEmpty(dto.getReduceType())&&CollectionUtils.isEmpty(dto.getFileList())){
            throw new BusinessRuntimeException("请先上传减免凭证");
        }
        if(dto.getId()!=null){
            mapper.update(dto);
        }else {
            dto.setId(snowflake.nextId());
            mapper.insert(dto);

        }
        sysFileService.saveFileBusRelation(dto.getFileList(), dto.getCargoInfoId());

    }

    @Override
    public List<TBusStackFeeReduceDTO> getList(TBusStackFeeReduceDTO dto) {
        List<TBusStackFeeReduceDTO> list = mapper.getList(dto);
        for (TBusStackFeeReduceDTO tBusStackFeeReduceDTO : list) {
            tBusStackFeeReduceDTO.setReduceTypeLabel(
                    "0".equals(tBusStackFeeReduceDTO.getReduceType())?"免堆存费":
                            ("1".equals(tBusStackFeeReduceDTO.getReduceType())?"减免指定天数":
                                    ("2".equals(tBusStackFeeReduceDTO.getReduceType()))?"指定日期":""));

        }
        return list;
    }

    @Override
    public List<TCostStorageSettleDTO> getSettleList(Long cargoInfoId) {
//        List<TCostStorageSettleDTO> = mapper.getSettleList(cargoInfoId);
        if(cargoInfoId==null){
            throw new BusinessRuntimeException("没有票货");
        }
        return mapper.getSettleList(cargoInfoId);
    }
}
