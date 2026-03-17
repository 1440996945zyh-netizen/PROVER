package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EquipmentTypePathDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentTypeDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentTypePO;
import com.yy.ppm.equipment.mapper.MEquipmentTypeMapper;
import com.yy.ppm.equipment.service.MEquipmentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 设备类型分类Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class MEquipmentTypeServiceImpl implements MEquipmentTypeService {

    @Resource
    private MEquipmentTypeMapper mapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 查询设备类型分类树形列表
     */
    @Override
    public List<MEquipmentTypeDTO> getTreeList(String typeName) {
        if (typeName == null || typeName.trim().isEmpty()) {
            // 如果没有查询条件，直接查询所有数据
            List<MEquipmentTypeDTO> allList = mapper.selectEquipmentTypeTree(null);
            return buildTree(allList, null);
        }

        // 先查询所有匹配的节点
        List<MEquipmentTypeDTO> matchedList = mapper.selectEquipmentTypeTree(typeName);
        if (matchedList.isEmpty()) {
            return new ArrayList<>();
        }

        // 收集所有需要查询的节点ID（包括匹配节点及其所有父节点）
        Set<Long> nodeIds = new HashSet<>();
        for (MEquipmentTypeDTO item : matchedList) {
            nodeIds.add(item.getId());
            // 递归收集所有父节点ID
            collectParentIds(item.getParentId(), nodeIds);
        }

        // 查询所有需要的节点（包括匹配节点和它们的父节点）
        List<MEquipmentTypeDTO> allList = new ArrayList<>();
        for (Long id : nodeIds) {
            MEquipmentTypeDTO node = mapper.selectById(id);
            if (node != null) {
                allList.add(node);
            }
        }

        // 构建树形结构
        return buildTree(allList, null);
    }

    /**
     * 查询设备零部件树形列表（设备小类 -> 设备机构 -> 设备部件）
     */
    @Override
    public List<MEquipmentTypeDTO> partsTree(MEquipmentTypeDTO mEquipmentTypeDTO) {
        // 关键字
        String keyword = mEquipmentTypeDTO == null ? null : mEquipmentTypeDTO.getTypeName();
        keyword = (keyword == null) ? "" : keyword.trim();
        final String kw = keyword;

        // 1) 永远先取全量(当前模块为3级：小类/机构/部件)，用于“父链/子树”裁剪
        List<MEquipmentTypeDTO> allRaw = mapper.partsTree(new MEquipmentTypeDTO());

        // 2) 防止 children 残留：拷贝节点并清空 children
        List<MEquipmentTypeDTO> allList = allRaw.stream()
                .map(this::copyNodeWithoutChildren)
                .collect(Collectors.toList());

        // 原始名称映射（用于搜索匹配，避免后续展示名称被拼接后影响搜索）
        Map<Long, String> origNameMap = allList.stream()
                .filter(x -> x.getId() != null)
                .collect(Collectors.toMap(
                        MEquipmentTypeDTO::getId,
                        x -> x.getTypeName() == null ? "" : x.getTypeName(),
                        (a, b) -> a
                ));

        // 设备类别管理（设备大类/设备中类/设备小类）名称映射，用于拼接展示：大类/中类/小类
        List<MEquipmentTypeDTO> categoryList = mapper.selectEquipmentTypeTree(null);
        Map<Long, MEquipmentTypeDTO> categoryMap = categoryList == null ? new HashMap<>() :
                categoryList.stream()
                        .filter(x -> x.getId() != null)
                        .collect(Collectors.toMap(MEquipmentTypeDTO::getId, x -> x, (a, b) -> a));

        // 无关键字：直接整树
        if (kw.isEmpty()) {
            List<MEquipmentTypeDTO> tree = buildTreeClean(allList);
            applyCategoryDisplayName(tree, categoryMap);
            return tree;
        }

        // 3) 建索引：id->node / parent->children
        Map<Long, MEquipmentTypeDTO> byId = allList.stream()
                .filter(x -> x.getId() != null)
                .collect(Collectors.toMap(MEquipmentTypeDTO::getId, a -> a, (a, b) -> a));

        Map<Long, List<MEquipmentTypeDTO>> childrenMap = allList.stream()
                .collect(Collectors.groupingBy(x -> {
                    Long pid = x.getParentId();
                    return (pid == null ? 0L : pid);
                }));

        // 4) 先精确匹配；如果没有精确匹配，再走模糊匹配
        List<MEquipmentTypeDTO> matched = allList.stream()
                .filter(n -> {
                    String name = origNameMap.getOrDefault(n.getId(), n.getTypeName());
                    return name != null && name.trim().equals(kw);
                })
                .collect(Collectors.toList());

        if (matched.isEmpty()) {
            matched = allList.stream()
                    .filter(n -> {
                        String name = origNameMap.getOrDefault(n.getId(), n.getTypeName());
                        return name != null && name.contains(kw);
                    })
                    .collect(Collectors.toList());
        }

        if (matched.isEmpty()) {
            return new ArrayList<>();
        }

        // 5) 计算所有命中节点需要保留的节点ID：命中节点 + 父链 + (命中节点的子树)
        //    规则：
        //    - 命中“设备部件”(最后一级) => 保留父链 + 自身
        //    - 命中“设备小类/设备机构”(非最后一级) => 额外保留其所有子级（完整子树）
        Set<Long> keepIds = new HashSet<>();

        for (MEquipmentTypeDTO hit : matched) {
            if (hit == null || hit.getId() == null) {
                continue;
            }

            // 保留命中节点本身
            keepIds.add(hit.getId());

            // 保留父链（一直到根：parentId = null / 0）
            Long pid = hit.getParentId();
            while (pid != null && pid != 0) {
                keepIds.add(pid);
                MEquipmentTypeDTO p = byId.get(pid);
                if (p == null) {
                    break;
                }
                pid = p.getParentId();
            }

            // 如果命中节点有子节点，则保留整棵子树
            boolean hasChildren = childrenMap.containsKey(hit.getId())
                    && childrenMap.get(hit.getId()) != null
                    && !childrenMap.get(hit.getId()).isEmpty();

            if (hasChildren) {
                collectDescendants(hit.getId(), childrenMap, keepIds);
            }
        }

        // 6) 只用 keepIds 子集构树（这样多个匹配项都会显示）
        List<MEquipmentTypeDTO> subset = allList.stream()
                .filter(n -> n.getId() != null && keepIds.contains(n.getId()))
                .map(this::copyNodeWithoutChildren)
                .collect(Collectors.toList());

        List<MEquipmentTypeDTO> tree = buildTreeClean(subset);

        // 7) 拼接展示名称：仅对“设备小类”,显示为“大类/中类/小类”
        applyCategoryDisplayName(tree, categoryMap);
        return tree;
    }

    /**
     * 对树中的“设备小类”节点拼接展示名称：设备大类/设备中类/设备小类
     * 数据来源：设备类别管理
     */
    private void applyCategoryDisplayName(List<MEquipmentTypeDTO> tree, Map<Long, MEquipmentTypeDTO> categoryMap) {
        if (tree == null || tree.isEmpty() || categoryMap == null || categoryMap.isEmpty()) {
            return;
        }
        Deque<MEquipmentTypeDTO> stack = new ArrayDeque<>(tree);
        while (!stack.isEmpty()) {
            MEquipmentTypeDTO node = stack.pop();
            if (node == null) {
                continue;
            }
            // 仅处理设备小类（在零部件树中为根节点，categoryLevel=3）
            if (node.getCategoryLevel() != null && node.getCategoryLevel() == 3 && node.getId() != null) {
                String fullName = buildCategoryFullName(node.getId(), categoryMap);
                if (fullName != null && !fullName.isBlank()) {
                    node.setTypeName(fullName);
                }
            }
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                for (MEquipmentTypeDTO c : node.getChildren()) {
                    stack.push(c);
                }
            }
        }
    }

    /**
     * 根据设备小类ID拼接“大类/中类/小类”，若缺失父级则尽量降级返回已有名称
     */
    private String buildCategoryFullName(Long smallId, Map<Long, MEquipmentTypeDTO> categoryMap) {
        MEquipmentTypeDTO small = categoryMap.get(smallId);
        if (small == null) {
            return null;
        }
        String smallName = small.getTypeName();

        MEquipmentTypeDTO mid = null;
        if (small.getParentId() != null && small.getParentId() != 0) {
            mid = categoryMap.get(small.getParentId());
        }
        String midName = mid == null ? null : mid.getTypeName();

        MEquipmentTypeDTO big = null;
        if (mid != null && mid.getParentId() != null && mid.getParentId() != 0) {
            big = categoryMap.get(mid.getParentId());
        }
        String bigName = big == null ? null : big.getTypeName();

        // 拼接：大类/中类/小类（存在什么拼什么）
        List<String> parts = new ArrayList<>();
        if (bigName != null && !bigName.isBlank()) {
            parts.add(bigName);
        }
        if (midName != null && !midName.isBlank()) {
            parts.add(midName);
        }
        if (smallName != null && !smallName.isBlank()) {
            parts.add(smallName);
        }
        return String.join("/", parts);
    }


    /**
     * 深拷贝并清 children，避免历史 children 残留导致“带出其他子节点”
     */
    private MEquipmentTypeDTO copyNodeWithoutChildren(MEquipmentTypeDTO src) {
        MEquipmentTypeDTO t = new MEquipmentTypeDTO();
        BeanUtils.copyProperties(src, t);
        t.setChildren(null);
        return t;
    }

    /**
     * 收集某节点的全部后代ID（命中父级时返回完整子树）
     */
    private void collectDescendants(Long id, Map<Long, List<MEquipmentTypeDTO>> childrenMap, Set<Long> keepIds) {
        List<MEquipmentTypeDTO> cs = childrenMap.get(id);
        if (cs == null || cs.isEmpty()) {
            return;
        }
        for (MEquipmentTypeDTO c : cs) {
            if (c.getId() == null) {
                continue;
            }
            if (keepIds.add(c.getId())) {
                collectDescendants(c.getId(), childrenMap, keepIds);
            }
        }
    }

    /**
     * 构树：每次都重新生成 children（不复用旧对象 children）
     */
    private List<MEquipmentTypeDTO> buildTreeClean(List<MEquipmentTypeDTO> nodes) {
        Map<Long, List<MEquipmentTypeDTO>> byParent = nodes.stream()
                .collect(Collectors.groupingBy(x -> {
                    Long pid = x.getParentId();
                    return (pid == null ? 0L : pid);
                }));

        Function<Long, List<MEquipmentTypeDTO>> build = new Function<>() {
            @Override
            public List<MEquipmentTypeDTO> apply(Long pid) {
                List<MEquipmentTypeDTO> list = byParent.getOrDefault(pid, new ArrayList<>());
                for (MEquipmentTypeDTO n : list) {
                    n.setChildren(apply(n.getId()));
                }
                return list;
            }
        };
        return build.apply(0L);
    }

    /**
     * 递归收集所有父节点ID
     */
    private void collectParentIds(Long parentId, Set<Long> nodeIds) {
        if (parentId != null && parentId != 0 && !nodeIds.contains(parentId)) {
            nodeIds.add(parentId);
            MEquipmentTypeDTO parent = mapper.selectById(parentId);
            if (parent != null && parent.getParentId() != null && parent.getParentId() != 0) {
                collectParentIds(parent.getParentId(), nodeIds);
            }
        }
    }

    /**
     * 构建树形结构
     */
    private List<MEquipmentTypeDTO> buildTree(List<MEquipmentTypeDTO> allList, Long parentId) {
        List<MEquipmentTypeDTO> result = new ArrayList<>();
        for (MEquipmentTypeDTO item : allList) {
            Long itemParentId = item.getParentId();
            if ((parentId == null && (itemParentId == null || itemParentId == 0)) ||
                    (parentId != null && parentId.equals(itemParentId))) {
                item.setChildren(buildTree(allList, item.getId()));
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 根据ID查询设备类型分类
     */
    @Override
    public MEquipmentTypeDTO getById(Long id) {
        return mapper.selectById(id);
    }

    /**
     * 新增或修改设备类型分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(MEquipmentTypeDTO dto) {
        // 验证分类级别
        if (dto.getCategoryLevel() == null || dto.getCategoryLevel() < 1 || dto.getCategoryLevel() > 3) {
            throw new BusinessRuntimeException("分类级别必须在1-3之间");
        }

        // 验证父级
        if (dto.getCategoryLevel() > 1) {
            if (dto.getParentId() == null || dto.getParentId() == 0) {
                throw new BusinessRuntimeException("设备中类和小类必须选择父级");
            }
            // 验证父级是否存在
            MEquipmentTypeDTO parent = mapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessRuntimeException("父级分类不存在");
            }
            // 验证级别关系
            if (dto.getCategoryLevel() != parent.getCategoryLevel() + 1) {
                throw new BusinessRuntimeException("分类级别不正确，设备中类的父级必须是设备大类，设备小类的父级必须是设备中类");
            }
        } else {
            // 设备大类的父级必须为空或0
            dto.setParentId(null);
        }

        // 验证同级下名称不能重复
        int count = mapper.countByNameAndParent(
                dto.getTypeName(),
                dto.getParentId(),
                dto.getId()
        );
        if (count > 0) {
            throw new BusinessRuntimeException("同级下已存在相同的设备类型名称");
        }

        MEquipmentTypePO po = new MEquipmentTypePO();
        BeanUtils.copyProperties(dto, po);

        if (dto.getId() == null) {
            // 新增
            po.setId(snowflake.nextId());
            if (po.getSortOrder() == null) {
                po.setSortOrder(0);
            }
            mapper.insert(po);
        } else {
            // 修改
            // 检查是否有子级，如果有子级，不能修改分类级别
            if (dto.getCategoryLevel() != null) {
                int childCount = mapper.countByParentId(dto.getId());
                if (childCount > 0) {
                    throw new BusinessRuntimeException("该分类下存在子级，不能修改分类级别");
                }
            }
            mapper.update(po);
        }
    }

    /**
     * 新增或修改设备零部件分类（3~5）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void addParts(MEquipmentTypeDTO dto) {
        // 验证分类级别
        if (dto.getCategoryLevel() == null || dto.getCategoryLevel() < 3 || dto.getCategoryLevel() > 5) {
            throw new BusinessRuntimeException("分类级别必须在3-5之间");
        }

        // 验证父级
        if (dto.getCategoryLevel() > 3) {
            if (dto.getParentId() == null || dto.getParentId() == 0) {
                throw new BusinessRuntimeException("设备机构和部件必须选择父级");
            }
            // 验证父级是否存在
            MEquipmentTypeDTO parent = mapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessRuntimeException("父级分类不存在");
            }
            // 验证级别关系
            if (dto.getCategoryLevel() != parent.getCategoryLevel() + 1) {
                throw new BusinessRuntimeException("分类级别不正确，设备中类的父级必须是设备大类，设备小类的父级必须是设备中类");
            }
        } else {
            // 设备大类的父级必须为空或0
            dto.setParentId(null);
        }

        // 验证同级下名称不能重复
        int count = mapper.countByNameAndParent(
                dto.getTypeName(),
                dto.getParentId(),
                dto.getId()
        );
        if (count > 0) {
            throw new BusinessRuntimeException("同级下已存在相同的设备类型名称");
        }

        MEquipmentTypePO po = new MEquipmentTypePO();
        BeanUtils.copyProperties(dto, po);

        if (dto.getId() == null) {
            // 新增
            po.setId(snowflake.nextId());
            if (po.getSortOrder() == null) {
                po.setSortOrder(0);
            }
            mapper.insert(po);
        } else {
            // 修改
            // 检查是否有子级，如果有子级，不能修改分类级别
            if (dto.getCategoryLevel() != null) {
                int childCount = mapper.countByParentId(dto.getId());
                if (childCount > 0) {
                    throw new BusinessRuntimeException("该分类下存在子级，不能修改分类级别");
                }
            }
            mapper.update(po);
        }
    }

    /**
     * 删除设备类型分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        // 检查是否有子级
        int childCount = mapper.countByParentId(id);
        if (childCount > 0) {
            throw new BusinessRuntimeException("该分类下存在子级，不能删除");
        }
        MEquipmentTypePO po = new MEquipmentTypePO();
        po.setId(id);
        mapper.deleteById(po);
    }

    /**
     * 根据父级ID查询子级列表
     */
    @Override
    public List<MEquipmentTypeDTO> getByParentId(Long parentId) {
        return mapper.selectByParentId(parentId);
    }

    /**
     * 根据级别和父级ID查询设备类型列表
     */
    @Override
    public List<MEquipmentTypeDTO> getByLevelAndParent(Integer categoryLevel, Long parentId) {
        return mapper.selectByLevelAndParent(categoryLevel, parentId);
    }

    /**
     * 根据小类ID获取完整路径（大类、中类、小类）
     */
    @Override
    public EquipmentTypePathDTO getPathBySmallCategoryId(Long smallCategoryId) {
        if (smallCategoryId == null) {
            throw new BusinessRuntimeException("小类ID不能为空");
        }

        // 查询小类信息
        MEquipmentTypeDTO smallCategory = mapper.selectById(smallCategoryId);
        if (smallCategory == null) {
            throw new BusinessRuntimeException("设备小类不存在");
        }

        // 验证是否为小类（级别为3）
        if (smallCategory.getCategoryLevel() == null || smallCategory.getCategoryLevel() != 3) {
            throw new BusinessRuntimeException("所选设备类型不是小类");
        }

        EquipmentTypePathDTO pathDTO = new EquipmentTypePathDTO();
        pathDTO.setEquipSmallCategoryId(smallCategory.getId());
        pathDTO.setEquipSmallCategoryName(smallCategory.getTypeName());

        // 查询中类信息
        if (smallCategory.getParentId() != null && smallCategory.getParentId() != 0) {
            MEquipmentTypeDTO middleCategory = mapper.selectById(smallCategory.getParentId());
            if (middleCategory != null) {
                pathDTO.setEquipMiddleCategoryId(middleCategory.getId());
                pathDTO.setEquipMiddleCategoryName(middleCategory.getTypeName());

                // 查询大类信息
                if (middleCategory.getParentId() != null && middleCategory.getParentId() != 0) {
                    MEquipmentTypeDTO bigCategory = mapper.selectById(middleCategory.getParentId());
                    if (bigCategory != null) {
                        pathDTO.setEquipBigCategoryId(bigCategory.getId());
                        pathDTO.setEquipBigCategoryName(bigCategory.getTypeName());
                    }
                }
            }
        }

        return pathDTO;
    }
}
