package com.yy.ppm.master.service;

import com.yy.ppm.master.bean.dto.MWorkScheduleDTO;

import java.util.List;

/**
 * (MWorkSchedule)表服务接口
 *
 * @author yy
 * @date 2021-03-11 14:45:20
 */
public interface MWorkScheduleService {

    /**
     * 获取数据列表
     * @param
     * @return
     */
    public List<MWorkScheduleDTO> getList();

    /**
     * 保存
     * @param workScheduleList
     * @return
     */
    public int save(List<MWorkScheduleDTO> workScheduleList);

}
