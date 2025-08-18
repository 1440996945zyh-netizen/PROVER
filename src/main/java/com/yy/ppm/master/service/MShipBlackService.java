package com.yy.ppm.master.service;

import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.dto.MShipSearchDTO;
import com.yy.ppm.master.bean.po.MShipLogPO;

import java.util.List;

public interface MShipBlackService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<MShipDTO> getList(MShipSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    public MShipDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mShipDTO
     * @return 是否成功
     */
    public boolean doSave(MShipDTO mShipDTO);


    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

}
