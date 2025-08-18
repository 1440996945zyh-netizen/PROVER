package com.yy.ppm.largescreen.service;


import com.yy.common.page.Pages;
import com.yy.ppm.largescreen.bean.dto.SInportCarDTO;
import com.yy.ppm.largescreen.bean.dto.SInportCarSearchDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 在港车辆表(SInportCar)Service
 * @Description
 * @createTime 2024年03月14日 10:42:00
 */
public interface SInportCarService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<SInportCarDTO> getPageList(SInportCarSearchDTO searchDTO);

    List<SInportCarDTO> getListByCondition(SInportCarSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    SInportCarDTO getDetail(Long id);


    /**
     * 保存
     *
     * @param sInportCarDTO
     * @return 是否成功
     */
    boolean doSave(SInportCarDTO sInportCarDTO);


    /**
     * 批量保存
     *
     * @param sInportCarDTOS
     * @return 是否成功
     */
    Map<String, Object> doListSave(List<SInportCarDTO> sInportCarDTOS);


    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除
     * List<Long> ids
     *
     * @param ids
     * @return 是否成功
     */
    boolean deleteListByIds(List<Long> ids);

    /**
     * 批量删除
     *
     * @param sInportCarDTO
     * @return 是否成功
     */
    boolean deleteByCondition(SInportCarDTO sInportCarDTO);

    byte[] exportExcel(SInportCarSearchDTO searchDTO);

    void exportTemplate(HttpServletResponse response);

    boolean importList(MultipartFile file);
}

