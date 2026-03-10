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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.micrometer.common.util.StringUtils.isBlank;


/**
 * 维修定额项目 Service实现类
 */
@Service
public class EMaintenanceProjectQuotaServiceImpl implements EMaintenanceProjectQuotaService {

    @Resource
    private EMaintenanceProjectQuotaMapper mapper;

    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<EMaintenanceProjectQuotaDTO> getList(EMaintenanceProjectQuotaDTO searchDTO, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> mapper.getList(searchDTO));
    }

    @Override
    public EMaintenanceProjectQuotaDTO getById(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        return mapper.getById(id);
    }

    @Override
    public void save(EMaintenanceProjectQuotaDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("参数不能为空");
        }

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            // 定额编号：系统自动生成
            if (isBlank(dto.getQuotaNo())) {
                dto.setQuotaNo(generateQuotaNo());
            }
            mapper.insert(dto);
            return;
        }

        // 修改
        mapper.update(dto);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("请选择一条数据删除");
        }
        mapper.deleteById(id);
    }

    /**
     * 生成定额编号：DE-YYYY-MM-DD-0001
     *
     * 规则：同一天内序号递增；第二天重新从0001开始。
     */
    private String generateQuotaNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String prefix = "DE-" + dateStr + "-";

        String maxNo = mapper.getMaxQuotaNo(prefix);
        int nextSeq = 1;
        if (!isBlank(maxNo) && maxNo.length() >= 4) {
            try {
                String tail = maxNo.substring(maxNo.length() - 4);
                nextSeq = Integer.parseInt(tail) + 1;
            } catch (Exception ignored) {
                // 解析失败则回退到0001，避免阻断业务
                nextSeq = 1;
            }
        }

        return prefix + String.format("%04d", nextSeq);
    }
}
