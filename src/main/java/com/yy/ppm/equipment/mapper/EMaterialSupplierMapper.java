package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialSupplierDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialSupplierSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialSupplierPO;
import org.apache.ibatis.annotations.Param;

/**
 * 供应商Mapper
 */
public interface EMaterialSupplierMapper {

    /**
     * 分页查询供应商列表
     */
    Page<EMaterialSupplierDTO> selectList(EMaterialSupplierSearchDTO searchDTO);

    /**
     * 根据ID查详情
     */
    EMaterialSupplierDTO selectById(@Param("id") Long id);

    /**
     * 新增
     */
    @Edit
    void insert(EMaterialSupplierPO po);

    /**
     * 修改
     */
    @Edit
    void update(EMaterialSupplierPO po);

    /**
     * 作废
     */
    @Edit
    void deleteById(EMaterialSupplierPO po);

    /**
     * 查当天最大编码
     */
    String selectMaxCodeToday();

    /**
     * 供应商名称 + 统一社会信用代码查重
     */
    int countByNameAndCreditCode(@Param("supplierName") String supplierName,
                                 @Param("uniformSocialCreditCode") String uniformSocialCreditCode,
                                 @Param("excludeId") Long excludeId);

    /**
     * 检查是否已经被业务单据引用
     */
    int countUsedById(@Param("id") Long id);
}
