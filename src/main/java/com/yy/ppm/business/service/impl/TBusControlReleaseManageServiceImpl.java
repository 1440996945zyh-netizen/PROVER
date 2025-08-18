package com.yy.ppm.business.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusReleaseManageDTO;
import com.yy.ppm.business.bean.dto.TBusReleaseManageSearchDTO;
import com.yy.ppm.business.mapper.TBusCargoInfoMapper;
import com.yy.ppm.business.mapper.TBusControlReleaseManageMapper;
import com.yy.ppm.business.service.TBusControlReleaseManageService;
import com.yy.ppm.common.service.SysFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.Date;

@Service
public class TBusControlReleaseManageServiceImpl implements TBusControlReleaseManageService {

    @Resource
    private TBusControlReleaseManageMapper tBusControlReleaseManageMapper;

    @Resource
    private SysFileService sysFileService;

    @Override
    public Pages<TBusReleaseManageDTO> getList(TBusReleaseManageSearchDTO searchDTO) {

        Pages<TBusReleaseManageDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tBusControlReleaseManageMapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean release(TBusReleaseManageDTO tBusReleaseManageDTO) {
        tBusReleaseManageDTO.setIsRelease("1");
        if(CollectionUtils.isEmpty(tBusReleaseManageDTO.getFileIds())){
            throw new BusinessRuntimeException("请上传凭证!");
        }
        sysFileService.saveFileBusRelation(tBusReleaseManageDTO.getFileIds(), tBusReleaseManageDTO.getId());
        return tBusControlReleaseManageMapper.updateRelease(tBusReleaseManageDTO);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean revokeRelease(Long id) {

        int count = tBusControlReleaseManageMapper.getTrustById(id);
        if(count>0){
            throw new BusinessRuntimeException("已存在疏港通知单 不允许撤销");
        }
        TBusReleaseManageDTO tBusReleaseManageDTO = new TBusReleaseManageDTO();
        tBusReleaseManageDTO.setId(id);
        tBusReleaseManageDTO.setIsRelease("0");
        tBusReleaseManageDTO.setReleaseRemark("");
        return tBusControlReleaseManageMapper.updateRelease(tBusReleaseManageDTO);
    }

    @Override
    public TBusReleaseManageDTO getDetail(Long id) {
        return tBusControlReleaseManageMapper.getById(id);
    }

}
