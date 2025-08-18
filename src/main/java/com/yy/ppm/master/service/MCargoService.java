package com.yy.ppm.master.service;

import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MCargoCategoryDTO;
import com.yy.ppm.master.bean.dto.MCargoCategorySearchDTO;
import com.yy.ppm.master.bean.dto.MCargoDTO;
import com.yy.ppm.master.bean.dto.MCargoSearchDTO;
import com.yy.ppm.master.bean.po.MCargoPO;

/**
 * (MCargoType)表服务接口
 *
 * @author makejava
 * @date 2021-03-08 11:18:37
 */
public interface MCargoService {

    /**
     * 获取数据列表
     * @param mCargoTypeSearchDTO
     * @return
     */
    public Pages<MCargoCategoryDTO> getListCargoCategory(MCargoCategorySearchDTO mCargoTypeSearchDTO);

    /**
     * 根据ID获取
     * @param id 主键
     * @return
     */
    public MCargoCategoryDTO getCargoCategoryById(Long id);

    /**
     * 保存
     * @param mCargoCategoryPO
     * @return
     */
    public int saveCargoCategory(MCargoCategoryDTO mCargoCategoryPO);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteCargoCategory(Long  id);

    //↑ 货种操作
    //↓ 货物操作

    public Pages<MCargoDTO> getListCargo(MCargoSearchDTO mCargoSearchDTO);

    /**
     * 根据ID获取
     * @param id 主键
     * @return
     */
    public MCargoDTO getCargoById(Long id);
    public MCargoDTO getDetailById(Long id);

    /**
     * 保存
     * @param mCargoPO
     * @return
     */
    public int insertCargo(MCargoPO mCargoPO);

    /**
     * 修改
     * @param mCargoPO
     * @return
     */
    int updateCargo(MCargoPO mCargoPO);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteCargo(Long  id);


    Pages<MCargoDTO> getListCargoNew(MCargoSearchDTO mCargoSearchDTO);

    Pages<MCargoDTO> getOutwardGoods(MCargoSearchDTO mCargoSearchDTO);

    void updateStatus(Long id, String status);
}
