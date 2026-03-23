package com.yy.ppm.equipment.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningConfigDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningConfigSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialWarningConfigPO;
import org.apache.ibatis.annotations.Param;

/**
 * @author FanQi
 * @version 1.0
 * @data 2026/3/20 10:53
 * @Description 物资预警配置
 */
public interface EMaterialWarningConfigMapper {

    /**
     * 查询物资预警配置列表（分页）
     *
     * @param searchDTO 查询条件
     * @return 分页数据
     */
    Page<EMaterialWarningConfigDTO> selectList(EMaterialWarningConfigSearchDTO searchDTO);

    /**
     * 根据主键ID查询详情
     *
     * @param id 主键ID
     * @return 详情数据
     */
    EMaterialWarningConfigDTO selectById(@Param("id") Long id);

    /**
     * 新增物资预警配置
     *
     * @param dto 请求参数
     */
    @Edit
    void add(EMaterialWarningConfigPO dto);

    /**
     * 修改物资预警配置
     *
     * @param dto 请求参数
     */
    @Edit
    void update(EMaterialWarningConfigPO dto);

    /**
     * 修改状态
     * @param po
     */
    void updateStatus(EMaterialWarningConfigPO po);

    /**
     * 删除物资预警配置
     *
     * @param id 主键ID
     */
    @Edit
    void delete(@Param("id") Long id);


    /**
     * 查启用中的预警配置
     */
    java.util.List<EMaterialWarningConfigDTO> selectEnabledList();
}
