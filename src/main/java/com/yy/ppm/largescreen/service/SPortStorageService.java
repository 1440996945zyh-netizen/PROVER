package com.yy.ppm.largescreen.service;


import com.yy.common.page.Pages;
import com.yy.ppm.largescreen.bean.dto.SPortStorageDTO;
import com.yy.ppm.largescreen.bean.dto.SPortStorageSearchDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (SPortStorage)Service
 * @Description
 * @createTime 2024年03月14日 23:13:00
 */
public interface SPortStorageService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<SPortStorageDTO> getPageList(SPortStorageSearchDTO searchDTO);

    List<SPortStorageDTO> getListByCondition(SPortStorageSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    SPortStorageDTO getDetail(Long id);


    /**
     * 保存
     *
     * @param sPortStorageDTO
     * @return 是否成功
     */
    boolean doSave(SPortStorageDTO sPortStorageDTO);


    /**
     * 批量保存
     *
     * @param sPortStorageDTOS
     * @return 是否成功
     */
    Map<String, Object> doListSave(List<SPortStorageDTO> sPortStorageDTOS);


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
     * @param sPortStorageDTO
     * @return 是否成功
     */
    boolean deleteByCondition(SPortStorageDTO sPortStorageDTO);

    byte[] exportExcel(SPortStorageSearchDTO searchDTO);

    void exportTemplate(HttpServletResponse response);

    boolean importList(MultipartFile file);

}

