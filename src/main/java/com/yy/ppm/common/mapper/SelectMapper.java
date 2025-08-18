package com.yy.ppm.common.mapper;

import com.yy.ppm.common.bean.dto.ResponsePopupTrustDTO;
import com.yy.ppm.common.bean.dto.SelecSearchDTO;
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
     * 航次查询
     * @param selectCommonSearch
     * @return
     */
    List<Map<String, Object>> getScnList(@Param("search") SelecSearchDTO selectCommonSearch);

    /**
     * 航次查询
     * @param selectCommonSearch
     * @return
     */
    List<Map<String, Object>> getScnSTATUSList(@Param("search") SelecSearchDTO selectCommonSearch);
    /**
     * 航次查询
     * @param selectCommonSearch
     * @return
     */
    List<Map<String, Object>> getCustomerList(@Param("search") SelecSearchDTO selectCommonSearch, @Param("property") String property);

    /**
     * 查询渤海通客户
     * @param selectCommonSearch
     * @return
     */
    List<Map<String, Object>> getCustomerBHTList(@Param("search") SelecSearchDTO selectCommonSearch, @Param("property") String property);

    /**
     * 票货查询
     * @param selectCommonSearch
     * @return
     */
    List<Map<String, Object>> getBusCargoInfoList(@Param("search") SelecSearchDTO selectCommonSearch);

    /**
     * 港口查询
     * @param selectCommonSearch
     * @return
     */
    List<Map<String, Object>> getPortList(@Param("search") SelecSearchDTO selectCommonSearch);

    /**
     * 场/区/垛查询
     * @param selectCommonSearch
     * @return
     */
    List<Map<String, Object>> getAllStorageMassList(@Param("search") SelecSearchDTO selectCommonSearch);
    List<Map<String, Object>> getAllStorageRegionList(@Param("search") SelecSearchDTO selectCommonSearch);

    /**
     * 主过程查询
     * @param
     * @return
     */
    List<Map<String, Object>> getMainWorkProcessList(String planTypeCode);

    /**
     * 机械类型
     * @return
     */
    List<Map<String, Object>> getMacTypetList();

    /**
     * 机械型号
     * @param macTypeCode
     * @return
     */
    List<Map<String, Object>> getMacModelList(@Param("macTypeCode") String macTypeCode);

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

    /**
     * 查询指令信息
     */
    public List<Map<String, Object>> getPopupTrust(Map<String, Object> map);
    public List<ResponsePopupTrustDTO>  getPopupTrustNew(Map<String, Object> map);

    List<Map<String, Object>> getCargoInfoList(@Param("search") SelecSearchDTO selectCommonSearch);
    List<Map<String, Object>> getBHTCargoInfoList(@Param("search") SelecSearchDTO selectCommonSearch);

    List<Map<String, Object>> listShipvoyageItem(SelecSearchDTO selectCommonSearch);

    List<Map<String, Object>> listShipvoyageItemExcludeJzxCm(SelecSearchDTO selectCommonSearch);

    List<Map<String, Object>> listShipvoyageItemForTrustExcludeJzxCm(SelecSearchDTO selectCommonSearch);

    List<Map<String, Object>> listContract(Map<String, Object> params);

    List<Map<String, Object>> getCargoInfoSignList(@Param("search") SelecSearchDTO selectCommonSearch);

    List<Map<String, Object>> getEnabledCargoInfoSignList(@Param("search") SelecSearchDTO selectCommonSearch);

    List<Map<String, Object>> listSubProcess(Map<String, Object> params);

    /**
     * 新流程下的作业过程下拉框
     * @param params
     * @return
     */
    List<Map<String, Object>> listSubProcessNew(Map<String, Object> params);

    List<Map<String, Object>> listMac(Map<String, Object> params);

    List<Map<String, Object>> listMachineUser(Map<String, Object> params);

    List<Map<String, Object>> getDeliveryInfo(@Param("search") SelecSearchDTO selectCommonSearch);

     List<Map<String, Object>> listBusinessWorkType();

    List<Map<String, Object>> listProduceWorkType();

    List<Map<String, Object>> getCargoInfoNoList(@Param("search") SelecSearchDTO selectCommonSearch);

    List<Map<String, Object>> getVoyageNoLeaveList(@Param("search") SelecSearchDTO selectCommonSearch);

    List<Map<String, Object>> listStopHourType(Map<String, Object> params);

    List<Map<String, Object>> listStopReason(Map<String, Object> params);

}
