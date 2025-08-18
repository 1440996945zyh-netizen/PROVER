package com.yy.ppm.largescreen.service;


import com.yy.common.page.Pages;
import com.yy.ppm.largescreen.bean.dto.SShipTrendsDTO;
import com.yy.ppm.largescreen.bean.dto.SShipTrendsSearchDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (SShipTrends)Service
 * @Description
 * @createTime 2024年03月15日 09:35:00
 */
public interface SShipTrendsService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<SShipTrendsDTO> getPageList(SShipTrendsSearchDTO searchDTO);

    List<SShipTrendsDTO> getListByCondition(SShipTrendsSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    SShipTrendsDTO getDetail(Long id);


    /**
     * 保存
     *
     * @param sShipTrendsDTO
     * @return 是否成功
     */
    boolean doSave(SShipTrendsDTO sShipTrendsDTO);


    /**
     * 批量保存
     *
     * @param sShipTrendsDTOS
     * @return 是否成功
     */
    Map<String, Object> doListSave(List<SShipTrendsDTO> sShipTrendsDTOS);


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
     * @param sShipTrendsDTO
     * @return 是否成功
     */
    boolean deleteByCondition(SShipTrendsDTO sShipTrendsDTO);

    byte[] exportExcel(SShipTrendsSearchDTO searchDTO);

    void exportTemplate(HttpServletResponse response);

    boolean importList(MultipartFile file);
}

