package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.magic.FileUploadBusinessTypeEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.mapper.SysFileMapper;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairUserDTO;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairUserDetailDTO;
import com.yy.ppm.equipment.mapper.EMEquipRepairContractMapper;
import com.yy.ppm.equipment.mapper.EMEquipRepairUserMapper;
import com.yy.ppm.equipment.service.EMEquipRepairContractService;
import com.yy.ppm.equipment.service.EMEquipRepairUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EMEquipRepairUserServiceImpl implements EMEquipRepairUserService {

    @Autowired
    private EMEquipRepairUserMapper mapper;
    @Resource
    private Snowflake snowflake;

    @Resource
    private SysFileMapper sysFileMapper;
    @Resource
    private SysFileService sysFileService;

    @Override
    public Pages<EMEquipRepairUserDTO> getList(EMEquipRepairUserDTO searchDTO, PageParameter parameter) {
        Pages<EMEquipRepairUserDTO> pages = PageHelperUtils.limit(parameter, () -> {
            return mapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public EMEquipRepairUserDTO getById(EMEquipRepairUserDTO searchDTO) {
        EMEquipRepairUserDTO po = mapper.getById(searchDTO);
        po.setList(mapper.getUserDetailList(po.getId()));
        po.getList().forEach(item -> {
            item.setFileList(sysFileMapper.getBusFiles(item.getId(), FileUploadBusinessTypeEnum.PERSONAL_SIGN.getCode()));
        });
        return po;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)

    public void save(EMEquipRepairUserDTO po) {
        // 新增
        if (po.getId() == null) {
            po.setId(snowflake.nextId());
            mapper.insert(po);
            for (EMEquipRepairUserDetailDTO userDetailDTO : po.getList()) {
                userDetailDTO.setId(snowflake.nextId());
                userDetailDTO.setRepairUserId(po.getId());

                // 关联文件
                if (userDetailDTO.getFileIds() != null && !userDetailDTO.getFileIds().isEmpty()) {
                    sysFileService.saveFileBusRelation(userDetailDTO.getFileIds(), userDetailDTO.getId());
                }
            }

            //新增子表
            mapper.insertUserDetail( po.getList());
        } else {
            mapper.update(po);

            //删除子表
            mapper.deleteUserDetail(po.getId());

            for (EMEquipRepairUserDetailDTO userDetailDTO : po.getList()) {
                userDetailDTO.setId(snowflake.nextId());
                userDetailDTO.setRepairUserId(po.getId());

                // 关联文件
                if (userDetailDTO.getFileIds() != null && !userDetailDTO.getFileIds().isEmpty()) {
                    sysFileService.saveFileBusRelation(userDetailDTO.getFileIds(), userDetailDTO.getId());
                }
            }
            //新增子表
            mapper.insertUserDetail( po.getList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("请选择一条数据删除");
        }
        //删除子表
        mapper.deleteUserDetail(id);
        mapper.deleteById(id);
    }
}
