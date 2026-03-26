package com.yy.ppm.common.mapper;

import com.yy.ppm.common.bean.dto.SelecSearchDTO;
import com.yy.ppm.equipment.bean.dto.EquipSmallCategorySelectDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 公共服务DAO
 *
 * @author yangcl
 * @date 2021-3-3 09:44:33
 */
public interface SelectMapper {

    /**
     * 获取下拉框数据源
     * @param selectCommonSearch
     * @param tableName
     * @param valueName
     * @param labelName
     * @returns
     */
    List<Map<String, Object>> getRemoteSelect(@Param("search") SelecSearchDTO selectCommonSearch,
                                          @Param("tableName") String tableName,
                                          @Param("valueName") String valueName,
                                          @Param("labelName") String labelName);
    /**
     * 获取本地下拉框数据源
     * @param tableName
     * @param valueName
     * @param labelName
     * @return
     */
    List<Map<String, Object>> getLocalSelect(@Param("tableName") String tableName,
                                            @Param("valueName") String valueName,
                                            @Param("labelName") String labelName,
                                            @Param("other") String other);


    /**
     * 查询省市
     * @param provinceCode 省编号（传则获取省下的区，不传则获取所有省列表）
     *
     * @return
     */
    List<Map<String, Object>> getCityList(String provinceCode);

    /**
     * 船舶
     * @param selectCommonSearch
     * @return
     */
    List<Map<String, Object>> getShipList(@Param("search") SelecSearchDTO selectCommonSearch);
    List<Map<String, Object>> getShipByName(@Param("search") SelecSearchDTO selectCommonSearch);

    /**
     * 航次查询
     * @param selectCommonSearch
     * @return
     */
    List<Map<String, Object>> getVoyageList(@Param("search") SelecSearchDTO selectCommonSearch);

    /**
     * 按角色查询人员
     * @return
     */
    List<Map<String, Object>> getUserByRole(@Param("role") String role);

    /**
     * 按岗位查询人员
     * @return
     */
    List<Map<String, Object>> getUserByPost(@Param("post") String post);

    /**
     * 按作业班组查询人员
     * @return
     */
    List<Map<String, Object>> getUserByWorkClass(@Param("workClass") String post);

    /**
     * 查询作业公司下的某级组织架构
     */
    List<Map<String, Object>> getDeptByLevelWorkCompany(@Param("deptLevel") String deptLevel, @Param("deptId") Long deptId);


    List<Map<String, Object>> listContract(Map<String, Object> params);

    List<Map<String, Object>> getEqptType(@Param("categoryLevel") String categoryLevel, @Param("parentId") String parentId);

    /**
     * 按部门查询人员
     * @param deptId 部门ID
     * @return
     */
    List<Map<String, Object>> getUserByDept(@Param("deptId") Long deptId);

    /**
     * 按公司查询人员
     * @param companyId 公司ID
     * @return
     */
    List<Map<String, Object>> getUserByCompany(@Param("companyId") Long companyId);

    List<Map<String, Object>> materialSupplier(@Param("search") SelecSearchDTO selectCommonSearch);

    List<Map<String, Object>> getEqptInfo();
}
