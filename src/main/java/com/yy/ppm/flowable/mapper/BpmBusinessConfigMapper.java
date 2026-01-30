package com.yy.ppm.flowable.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigDTO;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigSearchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description BPM业务配置Mapper
 */
@Mapper
public interface BpmBusinessConfigMapper {

    /**
     * 分页查询列表
     */
    Page<BpmBusinessConfigDTO> getList(BpmBusinessConfigSearchDTO searchDTO);

    /**
     * 新增
     */
    @Edit
    void insert(BpmBusinessConfigDTO dto);

    /**
     * 修改
     */
    @Edit
    void update(BpmBusinessConfigDTO dto);

    /**
     * 根据ID删除
     */
    @Edit
    Integer deleteById(Long id);


    /**
     * 根据ID查询详情
     */
    BpmBusinessConfigDTO getDetail(Long id);


    /**
     * 查询最新的流程定义ID
     * @param procModelId
     * @return
     */
    String getprocDefId(@Param("procModelId") String procModelId);

    /**
     * 根据菜单和流程业务类型获取流程定义
     */
    String getProcDefId(@Param("businessId") Long businessId, @Param("businessTypeCode")String businessTypeCode);

    /**
     * 根据流程模型id查询是否存在业务关联数据
     */
    List<BpmBusinessConfigDTO> getBusinessConfigCount(@Param("bpmModelId") String bpmModelId);

}
