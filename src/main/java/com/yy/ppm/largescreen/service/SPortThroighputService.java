package com.yy.ppm.largescreen.service;


import com.yy.common.page.Pages;
import com.yy.ppm.largescreen.bean.dto.SPortThroighputDTO;
import com.yy.ppm.largescreen.bean.dto.SPortThroighputSearchDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 港区吞吐量表(SPortThroighput)Service
 * @Description
 * @createTime 2024年03月15日 09:24:00
 */
public interface SPortThroighputService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<SPortThroighputDTO> getPageList(SPortThroighputSearchDTO searchDTO);

    List<SPortThroighputDTO> getListByCondition(SPortThroighputSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    SPortThroighputDTO getDetail(Long id);


    /**
     * 保存
     *
     * @param sPortThroighputDTO
     * @return 是否成功
     */
    boolean doSave(SPortThroighputDTO sPortThroighputDTO);


    /**
     * 批量保存
     *
     * @param sPortThroighputDTOS
     * @return 是否成功
     */
    Map<String, Object> doListSave(List<SPortThroighputDTO> sPortThroighputDTOS);


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
     * @param sPortThroighputDTO
     * @return 是否成功
     */
    boolean deleteByCondition(SPortThroighputDTO sPortThroighputDTO);

    byte[] exportExcel(SPortThroighputSearchDTO searchDTO);

    void exportTemplate(HttpServletResponse response);

    boolean importList(MultipartFile file);
}

