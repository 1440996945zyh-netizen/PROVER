package com.yy.ppm.master.service;


import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MWorkProcessDTO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;

/**
 * (MWorkProcess)表服务接口
 *
 * @author 张超
 * @date 2021-03-10 13:56:59
 */
public interface MWorkProcessService {

    /**
     * 获取数据列表
     *
     * @param mWorkProcessSearchDTO
     * @return
     */
    public Pages<MWorkProcessDTO> getList(MWorkProcessSearchDTO mWorkProcessSearchDTO);

    /**
     * 根据ID获取
     *
     * @param id 主键
     * @return
     */
    public MWorkProcessDTO getById(Long id);

    /**
     * 保存
     *
     * @param mWorkProcessDTO
     * @return
     */
    public int save(MWorkProcessDTO mWorkProcessDTO);

    /**
     * 保存子过程
     *
     * @param mWorkProcessDTO
     * @return
     */
    public int saveChildProcess(MWorkProcessDTO mWorkProcessDTO);

    void isRepeateSubProcess(MWorkProcessDTO mWorkProcessDTO);
}
