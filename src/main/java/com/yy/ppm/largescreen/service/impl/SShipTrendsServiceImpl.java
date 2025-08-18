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
import com.yy.ppm.largescreen.bean.dto.*;
import com.yy.ppm.largescreen.controller.SShipTrendsController;
import com.yy.ppm.largescreen.mapper.SShipTrendsMapper;
import com.yy.ppm.largescreen.service.SShipTrendsService;
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
 * @ClassName (SShipTrends)ServiceImpl
 * @Description
 * @createTime 2024年03月15日 09:35:00
 */
@Service
public class SShipTrendsServiceImpl implements SShipTrendsService {

    @Resource
    private SShipTrendsMapper sShipTrendsMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;


    private static final int CURSOR_LIMIT = 5_000;

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SShipTrendsController.class);

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<SShipTrendsDTO> getPageList(SShipTrendsSearchDTO searchDTO) {
        final String methodName = "SShipTrendsServiceImpl:getPageList";
        try {
            LOGGER.info(methodName, "获取列表（翻页）");
            //按照创建时间倒叙排列
            Pages<SShipTrendsDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
                Page<SShipTrendsDTO> page = sShipTrendsMapper.getPageList(searchDTO);
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
    public List<SShipTrendsDTO> getListByCondition(SShipTrendsSearchDTO searchDTO) {
        final String methodName = "SShipTrendsServiceImpl:getListByCondition";
        try {
            LOGGER.info(methodName, "获取列表");
            List<SShipTrendsDTO> list = sShipTrendsMapper.exportList(searchDTO);
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
    public SShipTrendsDTO getDetail(Long id) {
        return sShipTrendsMapper.getById(id);
    }


    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(SShipTrendsDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return sShipTrendsMapper.insert(dto) == 1;
            // 修改
        } else {
            return sShipTrendsMapper.update(dto) == 1;
        }

    }


    /**
     * 批量保存
     *
     * @param sShipTrendsDTOS
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> doListSave(List<SShipTrendsDTO> sShipTrendsDTOS) {
        final String methodName = "SShipTrendsServiceImpl:doListSave";
        Map<String, Object> resultMap = Maps.newHashMap();
        try {

            LOGGER.info(methodName, "批量保存");
            if (CollectionUtils.isEmpty(sShipTrendsDTOS)) {
                resultMap.put("flag", false);
                resultMap.put("msg", "不能保存空数据");
                return resultMap;
            }
            Map<Integer, List<SShipTrendsDTO>> statusMap = sShipTrendsDTOS
                    .stream().collect(Collectors.groupingBy(SShipTrendsDTO::getStatus));
            List<SShipTrendsDTO> deleteList = statusMap.get(0);//删除
            List<SShipTrendsDTO> saveList = statusMap.get(1);//保存
            List<SShipTrendsDTO> updateList = statusMap.get(2);//更新
            if (CollectionUtils.isNotEmpty(saveList)) {//批量保存
                if (CollectionUtils.isEmpty(deleteList) && CollectionUtils.isEmpty(updateList)) {
                    SShipTrendsSearchDTO searchDTO = new SShipTrendsSearchDTO();
                    BeanUtil.copyProperties(saveList.get(0), searchDTO);
                    List<SShipTrendsDTO> list = getListByCondition(searchDTO);
                    if (CollectionUtils.isNotEmpty(list)) {
                        resultMap.put("flag", false);
                        resultMap.put("msg", "不能保存重复数据");
                        return resultMap;
                    }
                }
                saveList.forEach(e -> e.setId(snowflake.nextId()));
                sShipTrendsMapper.insertList(saveList);
            }
            //批量删除
            if (CollectionUtils.isNotEmpty(deleteList)) {
                List<Long> ids = deleteList.stream().map(SShipTrendsDTO::getId).collect(Collectors.toList());
                deleteListByIds(ids);
            }
            //批量更新
            if (CollectionUtils.isNotEmpty(updateList)) {
                sShipTrendsMapper.updateListById(updateList);
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
        return sShipTrendsMapper.deleteById(id) == 1;
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
        return sShipTrendsMapper.deleteListByIds(ids) >= 1;
    }

    /**
     * 批量删除
     *
     * @param sShipTrendsDTO
     * @return 是否成功
     */
    @Override
    public boolean deleteByCondition(SShipTrendsDTO sShipTrendsDTO) {
        return sShipTrendsMapper.deleteByCondition(sShipTrendsDTO) >= 1;
    }

    @Override
    public byte[] exportExcel(SShipTrendsSearchDTO searchDTO) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, SShipTrendsExportDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<SShipTrendsExportDTO> cursor = sShipTrendsMapper.getExportList(searchDTO)) {
                    Iterator<SShipTrendsExportDTO> iterator = cursor.iterator();
                    if (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            List<SShipTrendsExportDTO> salarys = new ArrayList<>();
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
        List<SShipTrendsDTOTemplate> resultList = new ArrayList<>();
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
            EasyExcelFactory.write(response.getOutputStream(), SShipTrendsDTOTemplate.class).registerWriteHandler(handler).sheet("船舶动态导入模板").doWrite(resultList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean importList(MultipartFile file) {
        //继承basePO
        List<SShipTrendsInfoDTO> sShipTrendsInfoDTOS = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            EasyExcel.read(is, SShipTrendsInfoDTO.class, new PageReadListener<SShipTrendsInfoDTO>(dataList -> {

                sShipTrendsInfoDTOS.addAll(dataList);
            })).sheet().doRead();


            sShipTrendsInfoDTOS.forEach(o->{
                o.setId(snowflake.nextId());
            });
            sShipTrendsMapper.insertFileList(sShipTrendsInfoDTOS);
            return true;

        } catch (IOException e) {
            throw new BusinessRuntimeException("插入失败");
        }
    }

}

