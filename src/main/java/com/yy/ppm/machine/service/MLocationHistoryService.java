package com.yy.ppm.machine.service;


import com.yy.common.page.Pages;
import com.yy.ppm.machine.bean.dto.MLocationHistoryDTO;
import com.yy.ppm.machine.bean.dto.MLocationHistorySearchDTO;

import java.util.*;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 车辆历史表(MLocationHistory)Service
 * @Description
 * @createTime 2023年10月25日 10:46:00
 */
public interface MLocationHistoryService {

    List<MLocationHistoryDTO> getListByCondition(MLocationHistorySearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param macId
     * @return 实体
     */
    MLocationHistoryDTO getDetail(String macId);


    /**
     * 保存
     *
     * @param mLocationHistoryDTO
     * @return 是否成功
     */
    boolean doSave(MLocationHistoryDTO mLocationHistoryDTO);


    public boolean delete();

}

