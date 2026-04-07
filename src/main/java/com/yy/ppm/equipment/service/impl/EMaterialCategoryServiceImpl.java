package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EMaterialCategoryDTO;
import com.yy.ppm.equipment.bean.po.EMaterialCategoryPO;
import com.yy.ppm.equipment.mapper.EMaterialCategoryMapper;
import com.yy.ppm.equipment.service.EMaterialCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 物资类别Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EMaterialCategoryServiceImpl implements EMaterialCategoryService {

    @Resource
    private EMaterialCategoryMapper mapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 查询物资类别树形列表
     */
    @Override
    public List<EMaterialCategoryDTO> getTreeList(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            // 如果没有查询条件，直接查询所有数据
            List<EMaterialCategoryDTO> allList = mapper.selectMaterialCategoryTree(null);
            return buildTree(allList, null);
        }

        // 先查询所有匹配的节点
        List<EMaterialCategoryDTO> matchedList = mapper.selectMaterialCategoryTree(categoryName);
        if (matchedList.isEmpty()) {
            return new ArrayList<>();
        }

        // 收集所有需要查询的节点ID（包括匹配节点及其所有父节点）
        Set<Long> nodeIds = new HashSet<>();
        for (EMaterialCategoryDTO item : matchedList) {
            nodeIds.add(item.getId());
            // 递归收集所有父节点ID
            collectParentIds(item.getParentId(), nodeIds);
        }

        // 查询所有需要的节点（包括匹配节点和它们的父节点）
        List<EMaterialCategoryDTO> allList = new ArrayList<>();
        for (Long id : nodeIds) {
            EMaterialCategoryDTO node = mapper.selectById(id);
            if (node != null) {
                allList.add(node);
            }
        }

        // 构建树形结构
        return buildTree(allList, null);
    }

    /**
     * 递归收集所有父节点ID
     */
    private void collectParentIds(Long parentId, Set<Long> nodeIds) {
        if (parentId != null && parentId != 0 && !nodeIds.contains(parentId)) {
            nodeIds.add(parentId);
            EMaterialCategoryDTO parent = mapper.selectById(parentId);
            if (parent != null && parent.getParentId() != null && parent.getParentId() != 0) {
                collectParentIds(parent.getParentId(), nodeIds);
            }
        }
    }

    /**
     * 构建树形结构
     */
    private List<EMaterialCategoryDTO> buildTree(List<EMaterialCategoryDTO> allList, Long parentId) {
        List<EMaterialCategoryDTO> result = new ArrayList<>();
        for (EMaterialCategoryDTO item : allList) {
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
     * 根据ID查询物资类别
     */
    @Override
    public EMaterialCategoryDTO getById(Long id) {
        return mapper.selectById(id);
    }

    /**
     * 新增或修改物资类别
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMaterialCategoryDTO dto) {
        // 验证分类级别
        if (dto.getCategoryLevel() == null || dto.getCategoryLevel() < 1 || dto.getCategoryLevel() > 3) {
            throw new BusinessRuntimeException("分类级别必须在1-3之间");
        }

        // 验证父级
        if (dto.getCategoryLevel() > 1) {
            if (dto.getParentId() == null || dto.getParentId() == 0) {
                throw new BusinessRuntimeException("二级和三级类别必须选择父级");
            }
            // 验证父级是否存在
            EMaterialCategoryDTO parent = mapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessRuntimeException("父级类别不存在");
            }
            // 验证级别关系
            if (dto.getCategoryLevel() != parent.getCategoryLevel() + 1) {
                throw new BusinessRuntimeException("分类级别不正确，二级类别的父级必须是一级类别，三级类别的父级必须是二级类别");
            }
        } else {
            // 一级类别的父级必须为空或0
            dto.setParentId(null);
        }

        // 验证同级下名称不能重复
        int nameCount = mapper.countByNameAndParent(
            dto.getCategoryName(),
            dto.getParentId(),
            dto.getId()
        );
        if (nameCount > 0) {
            throw new BusinessRuntimeException("同级下已存在相同的类别名称");
        }

        EMaterialCategoryPO po = new EMaterialCategoryPO();
        BeanUtils.copyProperties(dto, po);

        if (dto.getId() == null) {
            // 新增
            po.setId(snowflake.nextId());
            if (po.getSortOrder() == null) {
                po.setSortOrder(0);
            }
//            if (po.getIsLaborProtection() == null) {
//                po.setIsLaborProtection(0);
//            }

            // 如果是二级或三级类别，需要自动生成编码
            if (dto.getCategoryLevel() != null && (dto.getCategoryLevel() == 2 || dto.getCategoryLevel() == 3)) {
                // 获取父级信息（重新查询，确保获取最新的codeCount）
                EMaterialCategoryDTO parent = mapper.selectById(dto.getParentId());
                if (parent == null) {
                    throw new BusinessRuntimeException("父级类别不存在");
                }
                if (parent.getCategoryCode() == null || parent.getCategoryCode().trim().isEmpty()) {
                    throw new BusinessRuntimeException("父级类别编码为空，无法生成子级编码");
                }

                // 从数据库中重新查询父级的codeCount（不使用前端传递的值）
                Integer parentCodeCount = parent.getCodeCount();
                if (parentCodeCount == null) {
                    parentCodeCount = 0;
                }
                if (parentCodeCount < 0) {
                    throw new BusinessRuntimeException("父级编码序号无效");
                }

                // 先加1，得到新的编码序号
                Integer newCodeCount = parentCodeCount + 1;

                // 格式化newCodeCount：0-9补0为00-09，10以上不补
                String formattedCodeCount;
                if (newCodeCount < 10) {
                    formattedCodeCount = String.format("%02d", newCodeCount);
                } else {
                    formattedCodeCount = String.valueOf(newCodeCount);
                }

                // 生成编码：父级编码 + '-' + formattedCodeCount
                String generatedCode = parent.getCategoryCode() + "-" + formattedCodeCount;
                po.setCategoryCode(generatedCode);

                // 验证生成的编码是否重复
                int codeCountCheck = mapper.countByCode(generatedCode, null);
                if (codeCountCheck > 0) {
                    throw new BusinessRuntimeException("生成的编码已存在，编码必须全表唯一");
                }

                // 更新父级的codeCount为加1后的值
                mapper.updateParentCodeCount(dto.getParentId(), newCodeCount);
            } else {
                // 一级类别，验证编码不能重复（如果编码不为空）
                if (dto.getCategoryCode() != null && !dto.getCategoryCode().trim().isEmpty()) {
                    int codeCount = mapper.countByCode(
                        dto.getCategoryCode(),
                        null
                    );
                    if (codeCount > 0) {
                        throw new BusinessRuntimeException("已存在相同的类别编码，编码必须全表唯一");
                    }
                }
            }

            // 设置codeCount（新增时默认为0，不管几级分类都是0）
            // 只有新增子级时，才会更新父级的codeCount
            po.setCodeCount(0);

            mapper.insert(po);
        } else {
            // 修改
            // 检查是否有子级，如果有子级，不能修改分类级别
            if (dto.getCategoryLevel() != null) {
                int childCount = mapper.countByParentId(dto.getId());
                if (childCount > 0) {
                    throw new BusinessRuntimeException("该类别下存在子级，不能修改分类级别");
                }
            }

            // 验证编码不能重复（如果编码不为空）
            if (dto.getCategoryCode() != null && !dto.getCategoryCode().trim().isEmpty()) {
                int codeCount = mapper.countByCode(
                    dto.getCategoryCode(),
                    dto.getId()
                );
                if (codeCount > 0) {
                    throw new BusinessRuntimeException("已存在相同的类别编码，编码必须全表唯一");
                }
            }

            mapper.update(po);
        }
    }

    /**
     * 删除物资类别
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        // 检查是否有子级
        int childCount = mapper.countByParentId(id);
        if (childCount > 0) {
            throw new BusinessRuntimeException("该类别下存在子级，不能删除");
        }
        EMaterialCategoryPO po = new EMaterialCategoryPO();
        po.setId(id);
        mapper.deleteById(po);
    }

    /**
     * 根据父级ID查询子级列表
     */
    @Override
    public List<EMaterialCategoryDTO> getByParentId(Long parentId) {
        return mapper.selectByParentId(parentId);
    }

    /**
     * 根据级别和父级ID查询物资类别列表
     */
    @Override
    public List<EMaterialCategoryDTO> getByLevelAndParent(Integer categoryLevel, Long parentId) {
        return mapper.selectByLevelAndParent(categoryLevel, parentId);
    }

    /**
     * 更新类别的CODE_COUNT
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void updateCodeCount(Long categoryId, Integer codeCount) {
        mapper.updateParentCodeCount(categoryId, codeCount);
    }
}

