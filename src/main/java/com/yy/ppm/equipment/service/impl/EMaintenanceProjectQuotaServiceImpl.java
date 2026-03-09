package com.yy.ppm.equipment.service.impl;
import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;
import com.yy.ppm.equipment.mapper.EMaintenanceProjectQuotaMapper;
import com.yy.ppm.equipment.service.EMaintenanceProjectQuotaService;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 维修定额项目 Service实现类
 */
@RequiredArgsConstructor
@Service
public class EMaintenanceProjectQuotaServiceImpl implements EMaintenanceProjectQuotaService {

    @Autowired
    private EMaintenanceProjectQuotaMapper mapper;


    /**
     * 查询列表
     */
    @Override
    public List<EMaintenanceProjectQuotaDTO> list() {

        return mapper.selectList();

    }


    /**
     * 根据ID查询
     */
    @Override
    public EMaintenanceProjectQuotaDTO get(Long id) {

        return mapper.selectById(id);

    }


    /**
     * 新增维修定额项目
     */
    @Override
    public int add(EMaintenanceProjectQuotaDTO quota) {

        // 自动生成定额编号
        quota.setQuotaCode(generateCode());

        return mapper.insert(quota);

    }


    /**
     * 修改
     */
    @Override
    public int update(EMaintenanceProjectQuotaDTO quota) {

        return mapper.update(quota);

    }


    /**
     * 删除
     */
    @Override
    public int delete(Long id) {

        return mapper.delete(id);

    }


    /**
     * 自动生成定额编号
     *
     * 格式：
     * DE-2026-03-09-0001
     *
     * 规则：
     * 1 获取当前日期
     * 2 查询当天最大编号
     * 3 序号+1
     */
    private String generateCode(){

        // 当前日期
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // 查询当天最大编号
        String maxCode = mapper.selectMaxCodeToday();

        int number = 1;
        if(maxCode != null){
            // 编号示例
            // DE-2026-03-09-0003
            String[] arr = maxCode.split("-");
            // 获取序号
            number = Integer.parseInt(arr[3]) + 1;
        }

        // 拼接编号
        return "DE-"+date+"-"+String.format("%04d",number);

    }

}