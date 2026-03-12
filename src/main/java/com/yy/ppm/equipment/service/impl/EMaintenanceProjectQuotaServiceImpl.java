package com.yy.ppm.equipment.service.impl;
import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;
import com.yy.ppm.equipment.mapper.EMaintenanceProjectQuotaMapper;
import com.yy.ppm.equipment.service.EMaintenanceProjectQuotaService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;


import java.util.Date;


/**
 * 维修定额项目Service实现
 * 说明：
 * 1) getList 使用 PageHelperUtils.limit 做分页封装，返回 Pages
 * 2) save 新增时自动生成定额编号：DE-YYYY-MM-DD-0001，并自动写入创建/更新信息
 */

@RequiredArgsConstructor
@Service
public class EMaintenanceProjectQuotaServiceImpl implements EMaintenanceProjectQuotaService {

    @Resource
    private EMaintenanceProjectQuotaMapper mapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 查询列表（分页）
     */
    @Override
    public Pages<EMaintenanceProjectQuotaDTO> list(EMaintenanceProjectQuotaDTO searchDTO, PageParameter parameter) {
        EMaintenanceProjectQuotaDTO dto = (searchDTO == null) ? new EMaintenanceProjectQuotaDTO() : searchDTO;
        return PageHelperUtils.limit(parameter, () -> mapper.selectList(dto));
    }

    /**
     * 根据ID查询
     */
    @Override
    public EMaintenanceProjectQuotaDTO get(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        return mapper.selectById(id);
    }

    /**
     * 新增：生成主键ID + 自动生成 定额编号quotaCode
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void add(EMaintenanceProjectQuotaDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("参数不能为空");
        }
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
        }
        // 由后端自动生成定额编号
        dto.setQuotaCode(generateCode());
        mapper.add(dto);
    }

    /**
     * 修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void update(EMaintenanceProjectQuotaDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        mapper.update(dto);
    }

    /**
     * 删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        mapper.delete(id);
    }

    /**
     * 自动生成定额编号：DE-YYYY-MM-DD-0001
     * 规则：查询当天最大 QUOTA_CODE，序号 +1；无则从 0001 开始
     */
    private String generateCode() {

        // 1）获取当天日期字符串
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // 2）查询数据库中“当天最大定额编号”
        String maxCode = mapper.selectMaxCodeToday();

        // 3）默认序号从 1 开始（对应 0001）
        int number = 1;

        // 4）如果能查到当天最大编号，则尝试解析其末尾序号并 +1
        if (maxCode != null && !maxCode.isBlank()) {

            // 找到最后一个 '-' 的位置
            int idx = maxCode.lastIndexOf('-');

            // idx 必须合法：不能为 -1，且不能是字符串最后一位
            if (idx > -1 && idx < maxCode.length() - 1) {

                // 截取最后一段序号字符串，例如 "0008"
                String seqStr = maxCode.substring(idx + 1);
                try {
                    // 解析为数字并 +1
                    number = Integer.parseInt(seqStr) + 1;
                } catch (NumberFormatException ignore) {
                    // 若解析失败（例如末尾不是纯数字），则回退到 1
                    number = 1;
                }
            }
        }
        // 5）拼装最终编号：DE-2026-03-12-0009
        return "DE-" + date + "-" + String.format("%04d", number);
    }
}