package com.yy.ppm.appWork.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.appWork.bean.dto.TDisCargoWaterDTO;
import com.yy.ppm.appWork.bean.dto.TDisWaterDTO;
import com.yy.ppm.appWork.mapper.TDisCargoWaterMapper;
import com.yy.ppm.appWork.mapper.TDisWaterElectricityMapper;
import com.yy.ppm.appWork.service.TDisCargoWaterService;
import com.yy.ppm.appWork.service.TDisWaterElectricityService;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class TDisCargoWaterServiceImpl implements TDisCargoWaterService {

    private static final MicroLogger LOGGER = new MicroLogger(TDisCargoWaterService.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private TDisCargoWaterMapper tDisCargoWaterMapper;

    @Resource
    private SysFileService sysFileService;

    @Override
    public List<TDisCargoWaterDTO> queryCargoAllApp(TDisCargoWaterDTO tDisCargoWaterDTO) {
        final String methodName = "queryCargoAllApp";
        //获取枚举作业过程代码
        if (StringUtils.isEmpty(tDisCargoWaterDTO.getProcessCode())){
            throw new BusinessRuntimeException("作业过程代码异常");
        }
        List<TDisCargoWaterDTO> pages =
                tDisCargoWaterMapper.queryCargoAllApp(tDisCargoWaterDTO);

        LOGGER.exit(methodName, "查询作业指令[end]");
        return pages;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cargoWaterAppInsert(TDisCargoWaterDTO tDisCargoWaterDTO) {
        final String methodName = "cargoWaterAppInsert";
        LOGGER.enter(methodName, "业务数据同步服务[start], TDisCargoWaterDTO: " + tDisCargoWaterDTO);
        //新增
        if (tDisCargoWaterDTO.getId() == null) {
            long id = snowflake.nextId();
            tDisCargoWaterDTO.setId(id);
            TDisCargoWaterDTO tDisCargoWaterDTO1 = tDisCargoWaterMapper.count(tDisCargoWaterDTO.getTrustId().toString());
            if ("50".equals(tDisCargoWaterDTO1.getStatus())) {
                throw new BusinessRuntimeException("指令已核销,新增失败");
            }
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file:tDisCargoWaterDTO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            // 附件保存
            sysFileService.saveFileBusRelation(fileIds, id);


            int i = tDisCargoWaterMapper.cargoWaterAppInsert(tDisCargoWaterDTO);
            tDisCargoWaterMapper.updateTrustStatus(tDisCargoWaterDTO);
        } else {
            TDisCargoWaterDTO tDisCargoWaterDTO1 = tDisCargoWaterMapper.count(tDisCargoWaterDTO.getTrustId().toString());
            if ("50".equals(tDisCargoWaterDTO1.getStatus())) {
                throw new BusinessRuntimeException("指令已核销,修改失败");
            }
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file:tDisCargoWaterDTO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            //删除附件
//            if(fileIds != null && fileIds.size() != 0){
//                for (Long fileId:fileIds) {
//                    sysFileService.delete(fileId,tDisWaterElectricityDTO.getId());
//                }
//            }
            // 附件保存
            sysFileService.saveFileBusRelation(fileIds, tDisCargoWaterDTO.getId());
            int i = tDisCargoWaterMapper.cargoWaterAppUpdate(tDisCargoWaterDTO);
        }

        LOGGER.info("货物加水操作成功！");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }

    @Override
    public List<TDisCargoWaterDTO> queryCargoWaterByIdApp(String trustId) {
        LOGGER.info("queryCargoWaterByIdApp");

        List<TDisCargoWaterDTO> tDisCargoWaterDTO = tDisCargoWaterMapper.queryCargoWaterByIdApp(trustId);
        if (!CollectionUtils.isEmpty(tDisCargoWaterDTO)) {
            for (TDisCargoWaterDTO disCargoWaterDTO : tDisCargoWaterDTO) {
                List<SysFileDTO> fileInfo = sysFileService.getBusFiles(disCargoWaterDTO.getId(), "BUSINESS_WATER");
                disCargoWaterDTO.setMattachmentInfoList(fileInfo);

            }
        }

        return tDisCargoWaterDTO;
    }

    @Override
    public TDisCargoWaterDTO queryById(Long id) {
        LOGGER.info("queryById");
        TDisCargoWaterDTO tDisCargoWaterDTO =tDisCargoWaterMapper.queryById(id);
        List<SysFileDTO> fileInfo = sysFileService.getBusFiles(id, "BUSINESS_WATER");
        tDisCargoWaterDTO.setMattachmentInfoList(fileInfo);
        return tDisCargoWaterDTO;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteApp(Long id) {

        final String methodName = "delete";
        LOGGER.enter(methodName, "货物加水信息删除[start], id: " + id);
        int count = tDisCargoWaterMapper.deleteApp(id);
        if(count == 0) {
            throw new BusinessRuntimeException("删除失败");
        }
        LOGGER.exit(methodName, org.apache.commons.lang3.StringUtils.EMPTY);
    }
}
