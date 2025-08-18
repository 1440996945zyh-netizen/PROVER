package com.yy.ppm.master.service;


import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MMachineDTO;
import com.yy.ppm.master.bean.dto.MMachineSearchDTO;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 机械信息(MMachine)Service
 * @Description
 * @createTime 2023年06月05日 17:28:00
 */
public interface MMachineService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<MMachineDTO> getList(MMachineSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    MMachineDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mMachineDTO
     * @return 是否成功
     */
    boolean doSave(MMachineDTO mMachineDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

}

