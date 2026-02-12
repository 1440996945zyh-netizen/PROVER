package com.yy.ppm.applet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.applet.bean.dto.TStdEnterPortNoticeDTO;
import com.yy.ppm.applet.bean.dto.TStdEnterPortNoticeSearchDTO;
import com.yy.ppm.applet.controller.TStdEnterPortNoticeController;
import com.yy.ppm.applet.mapper.TStdEnterPortNoticeMapper;
import com.yy.ppm.applet.service.TStdEnterPortNoticeService;
import jakarta.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 入港公告(TStdEnterPortNotice)ServiceImpl
 * @Description
 * @createTime 2023年12月01日 14:08:00
 */
@Service
public class TStdEnterPortNoticeServiceImpl implements TStdEnterPortNoticeService {

    @Resource
    private TStdEnterPortNoticeMapper tStdEnterPortNoticeMapper;

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TStdEnterPortNoticeController.class);

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TStdEnterPortNoticeDTO> getPageList(TStdEnterPortNoticeSearchDTO searchDTO) {
        final String methodName = "TStdEnterPortNoticeServiceImpl:getPageList";
        try {
            LOGGER.info(methodName, "获取列表（翻页）");
            //按照创建时间倒叙排列
            Pages<TStdEnterPortNoticeDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
                Page<TStdEnterPortNoticeDTO> page = tStdEnterPortNoticeMapper.getPageList(searchDTO);
//                page.sort(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
                return page;
            });
            pages.getPages().stream().forEach(item -> {
                if (StringUtils.isNotBlank(item.getRoleCode())) {
                    item.setNoticeRoleCodes(Arrays.asList(item.getRoleCode().split(",")));
                }
                if (StringUtils.isNotBlank(item.getRoleName())) {
                    item.setNoticeRoleNames(Arrays.asList(item.getRoleName().split(",")));
                }
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
    public List<TStdEnterPortNoticeDTO> getListByCondition(TStdEnterPortNoticeSearchDTO searchDTO) {
        final String methodName = "TStdEnterPortNoticeServiceImpl:getListByCondition";
        try {
            LOGGER.info(methodName, "获取列表");
            List<TStdEnterPortNoticeDTO> list = tStdEnterPortNoticeMapper.exportList(searchDTO);
            //按照创建时间倒叙排列
            list.sort(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
            list.stream().forEach(item -> {
                if (StringUtils.isNotBlank(item.getRoleCode())) {
                    item.setNoticeRoleCodes(Arrays.asList(item.getRoleCode().split(",")));
                }
                if (StringUtils.isNotBlank(item.getRoleName())) {
                    item.setNoticeRoleNames(Arrays.asList(item.getRoleName().split(",")));
                }
            });
            return list;
        } catch (Exception e) {
            LOGGER.error(methodName, e.getMessage());
            return Lists.newArrayList();
        }
    }

    @Override
    public List<TStdEnterPortNoticeDTO> getLatestOne(TStdEnterPortNoticeSearchDTO searchDTO) {
        final String methodName = "TStdEnterPortNoticeServiceImpl:getLatestOne";
        try {
            List<TStdEnterPortNoticeDTO> list = tStdEnterPortNoticeMapper.getLatestOne(searchDTO);
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
    public TStdEnterPortNoticeDTO getDetail(Long id) {
        TStdEnterPortNoticeDTO entity = tStdEnterPortNoticeMapper.getById(id);
        if (StringUtils.isNotBlank(entity.getRoleCode())) {
            entity.setNoticeRoleCodes(Arrays.asList(entity.getRoleCode().split(",")));
        }
        if (StringUtils.isNotBlank(entity.getRoleName())) {
            entity.setNoticeRoleNames(Arrays.asList(entity.getRoleName().split(",")));
        }
        return entity;
    }


    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TStdEnterPortNoticeDTO dto) {
        // 新增
        if (CollectionUtils.isNotEmpty(dto.getNoticeRoleCodes())) {
            String roleCode = String.join(",",dto.getNoticeRoleCodes());
            String roleName = String.join(",",dto.getNoticeRoleNames());
            dto.setRoleCode(roleCode);
            dto.setRoleName(roleName);
        }
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tStdEnterPortNoticeMapper.insert(dto) == 1;
            // 修改
        } else {
            return tStdEnterPortNoticeMapper.update(dto) == 1;
        }

    }


    /**
     * 批量保存
     *
     * @param tStdEnterPortNoticeDTOS
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> doListSave(List<TStdEnterPortNoticeDTO> tStdEnterPortNoticeDTOS) {
        final String methodName = "TStdEnterPortNoticeServiceImpl:doListSave";
        Map<String, Object> resultMap = Maps.newHashMap();
        try {

            LOGGER.info(methodName, "批量保存");
            if (CollectionUtils.isEmpty(tStdEnterPortNoticeDTOS)) {
                resultMap.put("flag", false);
                resultMap.put("msg", "不能保存空数据");
                return resultMap;
            }
            Map<Integer, List<TStdEnterPortNoticeDTO>> statusMap = tStdEnterPortNoticeDTOS
                    .stream().collect(Collectors.groupingBy(TStdEnterPortNoticeDTO::getFlag));
            List<TStdEnterPortNoticeDTO> deleteList = statusMap.get(0);//删除
            List<TStdEnterPortNoticeDTO> saveList = statusMap.get(1);//保存
            List<TStdEnterPortNoticeDTO> updateList = statusMap.get(2);//更新
            if (CollectionUtils.isNotEmpty(saveList)) {//批量保存
                if (CollectionUtils.isEmpty(deleteList) && CollectionUtils.isEmpty(updateList)) {
                    TStdEnterPortNoticeSearchDTO searchDTO = new TStdEnterPortNoticeSearchDTO();
                    BeanUtil.copyProperties(saveList.get(0), searchDTO);
                    List<TStdEnterPortNoticeDTO> list = getListByCondition(searchDTO);
                    if (CollectionUtils.isNotEmpty(list)) {
                        resultMap.put("flag", false);
                        resultMap.put("msg", "不能保存重复数据");
                        return resultMap;
                    }
                }
                saveList.forEach(e -> {
                    e.setId(snowflake.nextId());
                    if (CollectionUtils.isNotEmpty(e.getNoticeRoleCodes())) {
                        String roleCode = String.join(",",e.getNoticeRoleCodes());
                        String roleName = String.join(",",e.getNoticeRoleNames());
                        e.setRoleCode(roleCode);
                        e.setRoleName(roleName);
                    }
                });
                tStdEnterPortNoticeMapper.insertList(saveList);
            }
            //批量删除
            if (CollectionUtils.isNotEmpty(deleteList)) {
                List<Long> ids = deleteList.stream().map(TStdEnterPortNoticeDTO::getId).collect(Collectors.toList());
                deleteListByIds(ids);
            }
            //批量更新
            if (CollectionUtils.isNotEmpty(updateList)) {
                updateList.stream().forEach(e -> {
                    if (CollectionUtils.isNotEmpty(e.getNoticeRoleCodes())) {
                        String roleCode = String.join(",",e.getNoticeRoleCodes());
                        String roleName = String.join(",",e.getNoticeRoleNames());
                        e.setRoleCode(roleCode);
                        e.setRoleName(roleName);
                    }
                });
                tStdEnterPortNoticeMapper.updateListById(updateList);
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
        return tStdEnterPortNoticeMapper.deleteById(id) == 1;
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
        return tStdEnterPortNoticeMapper.deleteListByIds(ids) >= 1;
    }

    /**
     * 批量删除
     *
     * @param tStdEnterPortNoticeDTO
     * @return 是否成功
     */
    @Override
    public boolean deleteByCondition(TStdEnterPortNoticeDTO tStdEnterPortNoticeDTO) {
        return tStdEnterPortNoticeMapper.deleteByCondition(tStdEnterPortNoticeDTO) >= 1;
    }

}

