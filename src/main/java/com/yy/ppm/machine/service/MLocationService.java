package com.yy.ppm.machine.service;


import com.yy.common.page.Pages;
import com.yy.ppm.machine.bean.dto.MLocationDTO;
import com.yy.ppm.machine.bean.dto.MLocationSearchDTO;

import java.util.*;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 实时车辆表(MLocation)Service
 * @Description
 * @createTime 2023年10月25日 10:21:00
 */
public interface MLocationService {

    /**
     * 查询
     * @param searchDTO
     * @return 是否成功
     */
    List<MLocationDTO> getListByCondition(MLocationSearchDTO searchDTO);

    /**
     * 保存
     * @param mLocationDTO
     * @return 是否成功
     */
    boolean doSave(MLocationDTO mLocationDTO);


}

