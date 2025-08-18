package com.yy.ppm.largescreen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.largescreen.bean.SInportCarInfoDTO;
import com.yy.ppm.largescreen.bean.dto.*;
import com.yy.ppm.largescreen.controller.SPortStorageController;
import com.yy.ppm.largescreen.mapper.SPortStorageMapper;
import com.yy.ppm.largescreen.service.SPortStorageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.cursor.Cursor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (SPortStorage)ServiceImpl
 * @Description
 * @createTime 2024年03月14日 23:13:00
 */
@Service
public class SPortStorageServiceImpl implements SPortStorageService {

    @Resource
    private SPortStorageMapper sPortStorageMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;


    private static final int CURSOR_LIMIT = 5_000;
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SPortStorageController.class);

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<SPortStorageDTO> getPageList(SPortStorageSearchDTO searchDTO) {
        final String methodName = "SPortStorageServiceImpl:getPageList";
        try {
            LOGGER.info(methodName, "获取列表（翻页）");
            //按照创建时间倒叙排列
            Pages<SPortStorageDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
                Page<SPortStorageDTO> page = sPortStorageMapper.getPageList(searchDTO);
                page.sort(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
                return page;
            });
            return pages;
        } catch (Exception e) {
            LOGGER.error(methodName, e.getMessage());
            return new Pages<>();
        }
    }

    /**
     * 获取列表
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<SPortStorageDTO> getListByCondition(SPortStorageSearchDTO searchDTO) {
        final String methodName = "SPortStorageServiceImpl:getListByCondition";
        try {
            LOGGER.info(methodName, "获取列表");
            List<SPortStorageDTO> list = sPortStorageMapper.exportList(searchDTO);
            //按照创建时间倒叙排列
            list.sort(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
            return list;
        } catch (Exception e) {
            LOGGER.error(methodName, e.getMessage());
            return Lists.newArrayList();
        }
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public SPortStorageDTO getDetail(Long id) {
        return sPortStorageMapper.getById(id);
    }


    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(SPortStorageDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return sPortStorageMapper.insert(dto) == 1;
            // 修改
        } else {
            return sPortStorageMapper.update(dto) == 1;
        }

    }


    /**
     * 批量保存
     *
     * @param sPortStorageDTOS
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> doListSave(List<SPortStorageDTO> sPortStorageDTOS) {
        final String methodName = "SPortStorageServiceImpl:doListSave";
        Map<String, Object> resultMap = Maps.newHashMap();
        try {

            LOGGER.info(methodName, "批量保存");
            if (CollectionUtils.isEmpty(sPortStorageDTOS)) {
                resultMap.put("flag", false);
                resultMap.put("msg", "不能保存空数据");
                return resultMap;
            }
            Map<Integer, List<SPortStorageDTO>> statusMap = sPortStorageDTOS
                    .stream().collect(Collectors.groupingBy(SPortStorageDTO::getStatus));
            List<SPortStorageDTO> deleteList = statusMap.get(0);//删除
            List<SPortStorageDTO> saveList = statusMap.get(1);//保存
            List<SPortStorageDTO> updateList = statusMap.get(2);//更新
            if (CollectionUtils.isNotEmpty(saveList)) {//批量保存
                if (CollectionUtils.isEmpty(deleteList) && CollectionUtils.isEmpty(updateList)) {
                    SPortStorageSearchDTO searchDTO = new SPortStorageSearchDTO();
                    BeanUtil.copyProperties(saveList.get(0), searchDTO);
                    List<SPortStorageDTO> list = getListByCondition(searchDTO);
                    if (CollectionUtils.isNotEmpty(list)) {
                        resultMap.put("flag", false);
                        resultMap.put("msg", "不能保存重复数据");
                        return resultMap;
                    }
                }
                saveList.forEach(e -> e.setId(snowflake.nextId()));
                sPortStorageMapper.insertList(saveList);
            }
            //批量删除
            if (CollectionUtils.isNotEmpty(deleteList)) {
                List<Long> ids = deleteList.stream().map(SPortStorageDTO::getId).collect(Collectors.toList());
                deleteListByIds(ids);
            }
            //批量更新
            if (CollectionUtils.isNotEmpty(updateList)) {
                sPortStorageMapper.updateListById(updateList);
            }
            resultMap.put("flag", true);
            resultMap.put("msg", "保存成功");
            return resultMap;
        } catch (Exception e) {
            LOGGER.error(methodName, e.getMessage());
            resultMap.put("flag", false);
            resultMap.put("msg", "保存失败");
            return resultMap;
        }
    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return sPortStorageMapper.deleteById(id) == 1;
    }

    /**
     * 批量删除
     * List<Long> ids
     *
     * @param ids
     * @return 是否成功
     */
    @Override
    public boolean deleteListByIds(List<Long> ids) {
        return sPortStorageMapper.deleteListByIds(ids) >= 1;
    }

    /**
     * 批量删除
     *
     * @param sPortStorageDTO
     * @return 是否成功
     */
    @Override
    public boolean deleteByCondition(SPortStorageDTO sPortStorageDTO) {
        return sPortStorageMapper.deleteByCondition(sPortStorageDTO) >= 1;
    }

    @Override
    public byte[] exportExcel(SPortStorageSearchDTO searchDTO) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, SPortStorageExportDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<SPortStorageExportDTO> cursor = sPortStorageMapper.getExportList(searchDTO)) {
                    Iterator<SPortStorageExportDTO> iterator = cursor.iterator();
                    if (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            List<SPortStorageExportDTO> salarys = new ArrayList<>();
                            for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                                salarys.add(iterator.next());
                            }
                            excelWriter.write(salarys, writeSheet);
                        }
                    } else {
                        excelWriter.write(Collections.emptyList(), writeSheet);
                    }

                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }

    @Override
    public void exportTemplate(HttpServletResponse response) {
        //不继承baseNo
        List<SPortStorageDTOTemplate> resultList = new ArrayList<>();
        try {
            WriteCellStyle headCellStyle = new WriteCellStyle();
            headCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            WriteFont writeFont = new WriteFont();
            writeFont.setFontName("微软雅黑");              //字体
            writeFont.setFontHeightInPoints((short) 10);  //设置字体大小
            writeFont.setBold(false);                     //是否加粗
            headCellStyle.setWriteFont(writeFont);
            headCellStyle.setFillPatternType(FillPatternType.NO_FILL);
            HorizontalCellStyleStrategy handler = new HorizontalCellStyleStrategy();
            handler.setHeadWriteCellStyle(headCellStyle);
            //导出模板
            EasyExcelFactory.write(response.getOutputStream(), SPortStorageDTOTemplate.class).registerWriteHandler(handler).sheet("港存导入模板").doWrite(resultList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean importList(MultipartFile file) {
        //继承basePO
        List<SPortStorageInfoDTO> sPortStorageInfoDTOS = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            EasyExcel.read(is, SPortStorageInfoDTO.class, new PageReadListener<SPortStorageInfoDTO>(dataList -> {

                sPortStorageInfoDTOS.addAll(dataList);
            })).sheet().doRead();


            sPortStorageInfoDTOS.forEach(o->{
                o.setId(snowflake.nextId());
            });
            sPortStorageMapper.insertFileList(sPortStorageInfoDTOS);
            return true;

        } catch (IOException e) {
            throw new BusinessRuntimeException("插入失败");
        }
    }

}

