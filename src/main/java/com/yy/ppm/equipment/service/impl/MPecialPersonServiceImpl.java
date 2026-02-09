package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.MPecialPersonDTO;
import com.yy.ppm.equipment.bean.dto.MPecialPersonSearchDTO;
import com.yy.ppm.equipment.bean.po.MPecialPersonPO;
import com.yy.ppm.equipment.mapper.MPecialPersonMapper;
import com.yy.ppm.equipment.service.MPecialPersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;

/**
 * 特种作业人员证书Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class MPecialPersonServiceImpl implements MPecialPersonService {

    @Resource
    private MPecialPersonMapper mapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 查询特种作业人员证书列表（分页）
     */
    @Override
    public Pages<MPecialPersonDTO> getList(MPecialPersonSearchDTO searchDTO) {
        Pages<MPecialPersonDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mapper.selectList(searchDTO);
        });
        return pages;
    }

    /**
     * 根据ID查询特种作业人员证书
     */
    @Override
    public MPecialPersonDTO getById(Long id) {
        MPecialPersonDTO dto = mapper.selectById(id);
        return dto;
    }

    /**
     * 新增或修改特种作业人员证书
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(MPecialPersonDTO dto) {
        // 验证必填字段
        if (dto.getCertifiUser() == null || dto.getCertifiUser().trim().isEmpty()) {
            throw new BusinessRuntimeException("证书所属人不能为空");
        }
        if (dto.getExpireDate() == null) {
            throw new BusinessRuntimeException("到期时间不能为空");
        }
        if (dto.getValidDate() == null) {
            throw new BusinessRuntimeException("有效期不能为空");
        }
        if (dto.getFirstDate() == null) {
            throw new BusinessRuntimeException("批准时间不能为空");
        }
        if (dto.getUseOrgId() == null) {
            throw new BusinessRuntimeException("所属部门不能为空");
        }

        MPecialPersonPO po = new MPecialPersonPO();
        BeanUtils.copyProperties(dto, po);

        if (dto.getId() == null) {
            // 新增
            Long id = snowflake.nextId();
            po.setId(id);
            po.setNow(new Date());
            mapper.insert(po);
        } else {
            // 修改
            po.setNow(new Date());
            mapper.update(po);
        }
    }

    /**
     * 删除特种作业人员证书
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        MPecialPersonPO po = new MPecialPersonPO();
        po.setId(id);
        po.setNow(new Date());
        mapper.deleteById(po);
    }
}

