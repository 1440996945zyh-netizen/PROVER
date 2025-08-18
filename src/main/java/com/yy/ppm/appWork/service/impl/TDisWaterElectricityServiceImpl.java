package com.yy.ppm.appWork.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.UserHelper;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.appWork.bean.dto.TDisWaterDTO;
import com.yy.ppm.appWork.mapper.TDisWaterElectricityMapper;
import com.yy.ppm.appWork.service.TDisWaterElectricityService;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.business.bean.po.TBusTrustPO;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.enums.ProcessEnum;
import com.yy.ppm.common.service.SysFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TDisWaterElectricityServiceImpl implements TDisWaterElectricityService {
    private static final MicroLogger LOGGER = new MicroLogger(TDisWaterElectricityService.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private TDisWaterElectricityMapper tDisWaterElectricityMapper;

    @Resource
    private SysFileService sysFileService;

    @Override
    public List<TDisWaterDTO> queryAllApp(TDisWaterDTO tDisWaterElectricityDTO) {
        final String methodName = "queryAllApp";
        //获取枚举作业过程代码
        if ("JIASHUI".equals(tDisWaterElectricityDTO.getProcessCode())) {
            tDisWaterElectricityDTO.setProcessCode("10011");
        } else {
            if ("JD".equals(tDisWaterElectricityDTO.getProcessCode())) {
                tDisWaterElectricityDTO.setProcessCode("10010");
            } else {
                throw new BusinessRuntimeException("作业过程代码异常");
            }
        }

        List<TDisWaterDTO> pages =
                tDisWaterElectricityMapper.queryAllApp(tDisWaterElectricityDTO);

        LOGGER.exit(methodName, "查询作业指令[end]");
        return pages;
    }

    @Override
    public List<TDisWaterDTO> queryIdApp(String trustId) {
        LOGGER.info("queryIdApp");

        List<TDisWaterDTO> tDisWaterElectricityDTO = tDisWaterElectricityMapper.queryIdApp(trustId);
        if (!CollectionUtils.isEmpty(tDisWaterElectricityDTO)) {
            for (TDisWaterDTO disWaterElectricityDTO : tDisWaterElectricityDTO) {
                List<SysFileDTO> fileInfo = sysFileService.getBusFiles(disWaterElectricityDTO.getId(), "BUSINESS_WATER");
                disWaterElectricityDTO.setMattachmentInfoList(fileInfo);

            }
        }

        return tDisWaterElectricityDTO;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void AppInsert(TDisWaterDTO tDisWaterElectricityDTO) {
        final String methodName = "AppInsert";
        LOGGER.enter(methodName, "业务数据同步服务[start], tDisWaterElectricityDTO: " + tDisWaterElectricityDTO);
        //新增
        if (tDisWaterElectricityDTO.getId() == null) {
            long id = snowflake.nextId();
            tDisWaterElectricityDTO.setId(id);
            TDisWaterDTO tDisWaterElectricityDTO1 = tDisWaterElectricityMapper.count(tDisWaterElectricityDTO.getTrustId().toString());
            if ("50".equals(tDisWaterElectricityDTO1.getStatus())) {
                throw new BusinessRuntimeException("指令已核销,新增失败");
            }
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file:tDisWaterElectricityDTO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            // 附件保存
            sysFileService.saveFileBusRelation(fileIds, id);


            int i = tDisWaterElectricityMapper.AppInsert(tDisWaterElectricityDTO);
            tDisWaterElectricityMapper.updateTrustStutas(tDisWaterElectricityDTO);
        } else {
            TDisWaterDTO tDisWaterElectricityDTO1 = tDisWaterElectricityMapper.count(tDisWaterElectricityDTO.getTrustId().toString());
            if ("50".equals(tDisWaterElectricityDTO1.getStatus())) {
                throw new BusinessRuntimeException("指令已核销,修改失败");
            }
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file:tDisWaterElectricityDTO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            //删除附件
//            if(fileIds != null && fileIds.size() != 0){
//                for (Long fileId:fileIds) {
//                    sysFileService.delete(fileId,tDisWaterElectricityDTO.getId());
//                }
//            }
            // 附件保存
            sysFileService.saveFileBusRelation(fileIds, tDisWaterElectricityDTO.getId());
            int i = tDisWaterElectricityMapper.AppUpdate(tDisWaterElectricityDTO);
        }

        LOGGER.info("加水接电操作成功！");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void deleteApp(Long id) {
        final String methodName = "delete";
        LOGGER.enter(methodName, "卸船检尺信息删除[start], id: " + id);
        int count = tDisWaterElectricityMapper.deleteApp(id);
        if(count == 0) {
            throw new BusinessRuntimeException("删除失败");
        }
        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    public TDisWaterDTO queryById(Long id) {
        LOGGER.info("queryById");
        TDisWaterDTO tDisWaterElectricityDTO =tDisWaterElectricityMapper.queryById(id);
        List<SysFileDTO> fileInfo = sysFileService.getBusFiles(id, "BUSINESS_WATER");
        tDisWaterElectricityDTO.setMattachmentInfoList(fileInfo);
        return tDisWaterElectricityDTO;
    }

    @Override
    public List<Map<String, Object>> getUserList() {
        LOGGER.info("getUserList");
        return tDisWaterElectricityMapper.getUserList();
    }

}
