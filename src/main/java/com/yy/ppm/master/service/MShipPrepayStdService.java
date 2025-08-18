package com.yy.ppm.master.service;


import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MShipPrepayStdDTO;
import com.yy.ppm.master.bean.dto.MShipPrepayStdSearchDTO;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 船舶预缴标准, (MShipPrepayStd)Service
 * @Description
 * @createTime 2023年10月23日 15:50:00
 */
public interface MShipPrepayStdService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<MShipPrepayStdDTO> getList(MShipPrepayStdSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    MShipPrepayStdDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mShipPrepayStdDTO
     * @return 是否成功
     */
    boolean doSave(MShipPrepayStdDTO mShipPrepayStdDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

}

