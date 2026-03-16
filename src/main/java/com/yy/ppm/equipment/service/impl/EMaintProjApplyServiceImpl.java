package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.flowable.enums.ApprovalStatusEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.service.impl.CommonServiceImpl;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMaintProjApplyDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoDTO;
import com.yy.ppm.equipment.mapper.EMEquipRepairContractMapper;
import com.yy.ppm.equipment.mapper.EMaintProjApplyMapper;
import com.yy.ppm.equipment.mapper.MEquipmentInfoMapper;
import com.yy.ppm.equipment.service.EMEquipRepairContractService;
import com.yy.ppm.equipment.service.EMaintProjApplyService;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yy.common.util.SecurityUtils.getLoginUserId;

@Service
public class EMaintProjApplyServiceImpl implements EMaintProjApplyService {

    @Autowired
    private EMaintProjApplyMapper mapper;

    @Resource
    private MEquipmentInfoMapper mEquipmentInfoMapper;

    @Resource
    private Snowflake snowflake;

    @Autowired
    CommonServiceImpl commonService;
    @Autowired
    EMaintInfoServiceImpl eMaintInfoServiceImpl;

    @Resource
    BpmProcessInstanceService bpmProcessInstanceService;

    @Override
    public Pages<EMaintProjApplyDTO> getList(EMaintProjApplyDTO searchDTO, PageParameter parameter) {
        Pages<EMaintProjApplyDTO> pages = PageHelperUtils.limit(parameter, () -> {
            return mapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public EMaintProjApplyDTO getById(EMaintProjApplyDTO searchDTO) {
        EMaintProjApplyDTO po = mapper.getById(searchDTO);
        po.setList(mapper.getApplyQuataList(po.getId()));
        return po;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(EMaintProjApplyDTO po) {
        if (po.getId() == null) {
            po.setId(snowflake.nextId());
            // 自动生成采购单号
            String appNumber = commonService.generateSerialNumber(SerialNumberPrefixEnum.PROJ_APPLY);
            po.setAppNumber(appNumber);

            mapper.insert(po);
        }else {
            mapper.update(po);
            mapper.deleteApplyQuata(po.getId());

        }


        po.getList().forEach(item -> {
            item.setId(snowflake.nextId());
            item.setParentId(po.getId());
            mapper.insertApplyQuata(item);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("请选择一条数据作废");
        }
        EMaintProjApplyDTO eMaintProjApplyDTO = new EMaintProjApplyDTO();
        eMaintProjApplyDTO.setId( id);
        EMaintProjApplyDTO byId = mapper.getById(eMaintProjApplyDTO);
       int num = eMaintInfoServiceImpl.number(byId.getAppNumber());
       if(num>0){
           throw new BusinessRuntimeException("已派工,不可作废");
       }

        mapper.deleteById(id, "1");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProJect(Long id) {
        mapper.deleteApplyQuata( id);
        mapper.deleteApply(id);

    }


    /**
     * 提交发起流程
     */
    @Override
    public void submit(BpmProcessInstanceDTO dto) {
        // 调用流程实例发起
        bpmProcessInstanceService.createProcessInstance(getLoginUserId(), dto);
    }

}
