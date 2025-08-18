package com.yy.ppm.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.common.bean.dto.ResponsePopupTrustDTO;
import com.yy.ppm.common.bean.dto.SelecSearchDTO;
import com.yy.ppm.common.enums.ConstantsTypeEnum;
import com.yy.ppm.common.enums.CustomerPropertyEnum;
import com.yy.ppm.common.enums.DictTypeEnum;
import com.yy.ppm.common.enums.SelectEnum;
import com.yy.ppm.common.mapper.SelectMapper;
import com.yy.ppm.common.service.SelectService;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationDTO;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationSearchDTO;
import com.yy.ppm.dispatch.service.TBusTrustLocationService;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.yy.ppm.common.enums.ShipStatusEnum.*;
/**
 * 下拉框数据源
 */
@Service
public class SelectServiceImpl implements SelectService {

    @Resource
    private TBusTrustMapper tBusTrustMapper;
    @Autowired
    private TBusTrustLocationService tBusTrustLocationService;

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SelectServiceImpl.class);

    @Resource
    public SelectMapper selectMapper;


    /**
     * 宽泛船舶状态枚举
     * <p>
     * 预到 包含：接收
     * 锚地 包含：抵锚、起锚
     * 在港 包含：靠泊、移泊、开工、停工、复工、完工
     * 离泊 包含：离泊、离港
     * <p>
     * *不包含作废状态*
     * *不包含船舶动态之前的状态：预报*
     * *移泊是“操作”而不是“状态”，也不包含*
     */
    @Getter
    private enum ShipStatusBroadEnum {

        YUDAO("00", "预到"),

        MAODI("10", "锚地"),

        ZAIGANG("20", "在港"),

        LIBO("30", "离泊");

        private final String code;

        private final String name;

        ShipStatusBroadEnum(String code, String comment) {
            this.code = code;
            this.name = comment;
        }

        public static boolean isContains(String code) {
            return Arrays.stream(SelectServiceImpl.ShipStatusBroadEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
        }
    }



    @Override
    public List<Map<String, Object>> getRemoteSelect(SelecSearchDTO selectCommonSearch) {
        // 搜索值和选中值都为空时 直接返回
        if((selectCommonSearch.getValueContent() == null || "".equals(selectCommonSearch.getValueContent()))
                && (selectCommonSearch.getLabelContent() == null || "".equals(selectCommonSearch.getLabelContent()))){
            return new ArrayList<Map<String, Object>>();
        }
        if(selectCommonSearch.getValueContent() == null || "".equals(selectCommonSearch.getValueContent())){
            selectCommonSearch.setValueContentList(new ArrayList<>());
        }else{
            // 多选时兼容逗号分隔的多个key
            selectCommonSearch.setValueContentList(Arrays.asList(selectCommonSearch.getValueContent().split(",")));
        }
        List<Map<String, Object>> res = new ArrayList<>();
        switch (selectCommonSearch.getType()){
            // 货物信息
            case "CARGO_INFO" :
                res = selectMapper.getCargoInfoList(selectCommonSearch);
                break;
            // 货物信息
            case "BHT_CARGO_INFO" :
                res = selectMapper.getBHTCargoInfoList(selectCommonSearch);
                break;
            // 票货号
            case "CARGO_INFO_NO" :
                res = selectMapper.getCargoInfoNoList(selectCommonSearch);
                break;
            // 货物信息(带货物标识码）
            case "CARGO_INFO_SIGN" :
                res = selectMapper.getCargoInfoSignList(selectCommonSearch);
                break;
            // 货物信息(带货物标识码）（带状态）
            case "CARGO_INFO_SIGN_ENABLED" :
                res = selectMapper.getEnabledCargoInfoSignList(selectCommonSearch);
                break;
                //审批通过的客户
            case "CUSTOMER_PASS" :
                res = selectMapper.getCustomerList(selectCommonSearch, CustomerPropertyEnum.STATUS_10.getValue());
                break;
            //渤海通客户
            case "CUSTOMER_BHT" :
                res = selectMapper.getCustomerBHTList(selectCommonSearch, "");
                break;
                // 全部客户
            case "CUSTOMER_ALL" :
                res = selectMapper.getCustomerList(selectCommonSearch, "");
                break;
                // 货主
            case "CUSTOMER_CARGO_OWNER" :
                res = selectMapper.getCustomerList(selectCommonSearch, CustomerPropertyEnum.HZ.getValue());
                break;
            // 货代
            case "CUSTOMER_CARGO_AGENT" :
                res = selectMapper.getCustomerList(selectCommonSearch, CustomerPropertyEnum.HD.getValue());
                break;
                // 船主
            case "CUSTOMER_SHIP_OWNER" :
                res = selectMapper.getCustomerList(selectCommonSearch, CustomerPropertyEnum.CZ.getValue());
                break;
            // 船代
            case "CUSTOMER_SHIP_AGENT" :
                res = selectMapper.getCustomerList(selectCommonSearch, CustomerPropertyEnum.CD.getValue());
                break;
                // 物流车队
            case "CUSTOMER_FLEET" :
                res = selectMapper.getCustomerList(selectCommonSearch, CustomerPropertyEnum.WLCD.getValue());
                break;
            case "SHIP" :
                res = selectMapper.getShipList(selectCommonSearch);
                break;
            case "SHIP_NAME" :
                res = selectMapper.getShipByName(selectCommonSearch);
                break;
                // 航次
            case "VOYAGE" :
                res = selectMapper.getVoyageList(selectCommonSearch);
                break;
                // 航次（不包括离岗的）
            case "VOYAGE_NO_LEAVE" :
                res = selectMapper.getVoyageNoLeaveList(selectCommonSearch);
                break;
            // 航次
            case "SCN" :
                res = selectMapper.getScnList(selectCommonSearch);
                break;
            // 航次 抵锚状态之后（含抵锚），离港状态之前（不含离港）
            case "SCN_STATUS" :
                res = selectMapper.getScnSTATUSList(selectCommonSearch);
                break;
                // 票货
            case "BUS_CARGO_INFO":
                res = selectMapper.getBusCargoInfoList(selectCommonSearch);
                break;
                // 港口
            case "PORT":
                res = selectMapper.getPortList(selectCommonSearch);
                break;              // 港口
            case "DELIVERY_INFO":
                res = selectMapper.getDeliveryInfo(selectCommonSearch);
                break;
            // 货种
            case "CARGO_CATEGORY" :
                res = selectMapper.getRemoteSelect(selectCommonSearch,
                        SelectEnum.CARGO_CATEGORY.getTableName(),
                        SelectEnum.CARGO_CATEGORY.getValueName(),
                        SelectEnum.CARGO_CATEGORY.getLabelName()
                        );
                break;
                // 场/区/垛
            case "ALL_STORAGE_MASS" :
                res = selectMapper.getAllStorageMassList(selectCommonSearch);break;
            case "ALL_STORAGE_REGION" :
                res = selectMapper.getAllStorageRegionList(selectCommonSearch);break;
            case "SHIPVOYAGE_ITEM" :
                if (selectCommonSearch.getNumber() == null) {
                    selectCommonSearch.setNumber(50L);
                }
                res = selectMapper.listShipvoyageItem(selectCommonSearch);
                res.forEach(v1 -> {
                    Object hatchNumObj;
                    if ((hatchNumObj = v1.remove("hatchNum")) != null) {
                        int hatchNum = Integer.parseInt(String.valueOf(hatchNumObj));
                        if (hatchNum > 0) {
                            int offset = 1;
                            Integer[] hatchArr = IntStream.range(offset, hatchNum + offset).boxed().toArray(Integer[]::new);
                            v1.put("hatchArr", hatchArr);
                        } else {
                            v1.put("hatchArr", new Object[0]);
                        }
                    } else {
                        v1.put("hatchArr", new Object[0]);
                    }
                });
                break;
            case "SHIPVOYAGE_ITEM_EXCLUDE_JZX_CM" :
                if (selectCommonSearch.getNumber() == null) {
                    selectCommonSearch.setNumber(50L);
                }
                res = selectMapper.listShipvoyageItemExcludeJzxCm(selectCommonSearch);
                res.forEach(v1 -> {
                    Object hatchNumObj;
                    if ((hatchNumObj = v1.remove("hatchNum")) != null) {
                        int hatchNum = Integer.parseInt(String.valueOf(hatchNumObj));
                        if (hatchNum > 0) {
                            int offset = 1;
                            Integer[] hatchArr = IntStream.range(offset, hatchNum + offset).boxed().toArray(Integer[]::new);
                            v1.put("hatchArr", hatchArr);
                        } else {
                            v1.put("hatchArr", new Object[0]);
                        }
                    } else {
                        v1.put("hatchArr", new Object[0]);
                    }
                });
                break;
            case "SHIPVOYAGE_ITEM_EXCLUDE_JZX_CM_TRUST" :
                if (selectCommonSearch.getNumber() == null) {
                    selectCommonSearch.setNumber(50L);
                }
                res = selectMapper.listShipvoyageItemForTrustExcludeJzxCm(selectCommonSearch);
                res.forEach(v1 -> {
                    Object hatchNumObj;
                    if ((hatchNumObj = v1.remove("hatchNum")) != null) {
                        int hatchNum = Integer.parseInt(String.valueOf(hatchNumObj));
                        if (hatchNum > 0) {
                            int offset = 1;
                            Integer[] hatchArr = IntStream.range(offset, hatchNum + offset).boxed().toArray(Integer[]::new);
                            v1.put("hatchArr", hatchArr);
                        } else {
                            v1.put("hatchArr", new Object[0]);
                        }
                    } else {
                        v1.put("hatchArr", new Object[0]);
                    }
                });
                break;
            default:
                break;
        }

        return res;

    }

    /**
     * 获取本地下拉框数据源
     * @param params
     * @return
     */
    @Override
    public List<Map<String, Object>> getLocalSelect(Map<String, Object> params) {

        // 类型
        String type = StringUtil.getString(params.get("type"));
        // 父节点
        Object parent = params.get("parent");

        List<Map<String, Object>> res = new ArrayList<>();

        switch (type) {
            // 人员
            case "USER" :

                // 按角色查询
                if (!StringUtil.isEmpty(StringUtil.getString(params.get("role")))) {
                    res = selectMapper.getUserByRole(StringUtil.getString(params.get("role")));

                    // 按岗位
                } else if (!StringUtil.isEmpty(StringUtil.getString(params.get("post")))) {
                    res = selectMapper.getUserByPost(StringUtil.getString(params.get("post")));

                    // 按作业班组
                } else if (!StringUtil.isEmpty(StringUtil.getString(params.get("workClass")))) {
                    res = selectMapper.getUserByWorkClass(StringUtil.getString(params.get("WorkClass")));

                } else {
                    res = selectMapper.getLocalSelect(
                            SelectEnum.USER.getTableName(),
                            SelectEnum.USER.getValueName(),
                            SelectEnum.USER.getLabelName(),
                            null);
                }

                break;

                // 作业公司
            case "DEPT_WORK_COMPANY" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.DEPT_WORK_COMPANY.getTableName(),
                        SelectEnum.DEPT_WORK_COMPANY.getValueName(),
                        SelectEnum.DEPT_WORK_COMPANY.getLabelName(),
                        " IS_WORK_COMPANY = '1' AND STATUS = '1'");
                break;
            case "DEPT_WORK_COMPANY_CODE" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.DEPT_WORK_COMPANY_CODE.getTableName(),
                        SelectEnum.DEPT_WORK_COMPANY_CODE.getValueName(),
                        SelectEnum.DEPT_WORK_COMPANY_CODE.getLabelName(),
                        " IS_WORK_COMPANY = '1' AND STATUS = '1'");
                break;
            // 内部部门
            case "DEPT_INNER_ID" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.DEPT_INNER_ID.getTableName(),
                        SelectEnum.DEPT_INNER_ID.getValueName(),
                        SelectEnum.DEPT_INNER_ID.getLabelName(),
                        " DEPT_LEVEL = 2 AND STATUS = '1' AND IN_OUT_TYPE = 'I'");
                break;
            // 按组织架构级别查询组织架构
            case "DEPT_LEVEL" :

                String deptLevel = StringUtil.getString(params.get("deptLevel"));

                String parentDeptGid = StringUtil.getString(params.get("parentDeptGid"));
                String parentDeptId = StringUtil.getString(params.get("parentDeptId")); // 计件工班用

                if (StringUtil.isEmpty(parentDeptGid)&&StringUtil.isEmpty(parentDeptId)) {
                    res = selectMapper.getLocalSelect(
                            SelectEnum.DEPT_LEVEL.getTableName(),
                            SelectEnum.DEPT_LEVEL.getValueName(),
                            SelectEnum.DEPT_LEVEL.getLabelName(),
                            " DEPT_LEVEL = '" + DictTypeEnum.DictDataEnum.match(DictTypeEnum.DEPT_LEVEL, deptLevel) + "' AND STATUS = '1'");

                    // 上级组织架构下的 （parent可能为所有上级的id）
                } else {
                    if(StringUtil.isNotEmpty(parentDeptId)){
                        res = selectMapper.getDeptByLevelWorkCompany(deptLevel, StringUtil.getLong(parentDeptId));
                    }else{
                        res = selectMapper.getDeptByLevelWorkCompany(deptLevel, StringUtil.getLong(parentDeptGid));
                    }
                }

                break;
                case "DEPT_LEVEL_SALARY" :

                String deptLevelSalary = StringUtil.getString(params.get("deptLevel"));

                String parentDeptGidSalary = StringUtil.getString(params.get("parentDeptGid"));
                String parentDeptIdSalary = StringUtil.getString(params.get("parentDeptId")); // 计件工班用

                if (StringUtil.isEmpty(parentDeptGidSalary)&&StringUtil.isEmpty(parentDeptIdSalary)) {
                    res = selectMapper.getLocalSelect(
                            SelectEnum.DEPT_LEVEL_SALARY.getTableName(),
                            SelectEnum.DEPT_LEVEL_SALARY.getValueName(),
                            SelectEnum.DEPT_LEVEL_SALARY.getLabelName(),
                            " DEPT_LEVEL = '" + DictTypeEnum.DictDataEnum.match(DictTypeEnum.DEPT_LEVEL, deptLevelSalary)+ "'" +
                                    " AND STATUS = '1'"
                                    + " AND ID  IN ('1677243491865989120','1710197535592812544', " +
                                    "'1677243625832058880','1710203467898949632')");

                    // 上级组织架构下的 （parent可能为所有上级的id）
                } else {
                    if(StringUtil.isNotEmpty(parentDeptIdSalary)){
                        res = selectMapper.getDeptByLevelWorkCompany(deptLevelSalary, StringUtil.getLong(parentDeptIdSalary));
                    }else{
                        res = selectMapper.getDeptByLevelWorkCompany(deptLevelSalary, StringUtil.getLong(parentDeptGidSalary));
                    }
                }

                break;
            // 机械队
            case "DEPT_MACHINE" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.DEPT_MACHINE.getTableName(),
                        SelectEnum.DEPT_MACHINE.getValueName(),
                        SelectEnum.DEPT_MACHINE.getLabelName(),
                        " IS_MACHINE = '1' AND STATUS = '1' ");
                break;
            // 劳务队
            case "DEPT_LABOR" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.DEPT_LABOR.getTableName(),
                        SelectEnum.DEPT_LABOR.getValueName(),
                        SelectEnum.DEPT_LABOR.getLabelName(),
                        " IS_LABOR = '1' AND STATUS = '1' ");
                break;
                // 费目
            case "FEE_ITEM" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.FEE_ITME.getTableName(),
                        SelectEnum.FEE_ITME.getValueName(),
                        SelectEnum.FEE_ITME.getLabelName(),
                        null);
                break;
                // 服务
            case "SERVICE" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.SERVICE.getTableName(),
                        SelectEnum.SERVICE.getValueName(),
                        SelectEnum.SERVICE.getLabelName(),
                        null);
                break;
            //标准化货种
            case "STD_CARGO_CATEGORY" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.STD_CARGO_CATEGORY.getTableName(),
                        SelectEnum.STD_CARGO_CATEGORY.getValueName(),
                        SelectEnum.STD_CARGO_CATEGORY.getLabelName(),
                        null);
                break;
                // 作业过程（主）
            case "MAIN_WORK_PROCESS" :
                String planTypeCode = StringUtil.getString(params.get("planTypeCode"));
                res = selectMapper.getMainWorkProcessList(planTypeCode);
                break;
            // 作业过程（子）
            case "SUB_WORK_PROCESS":
                params.put("parent", StringUtil.getString(params.get("parent")));
                res = selectMapper.listSubProcess(params);
                break;
                // 作业过程（子）（新）
            case "SUB_WORK_PROCESS_NEW":
                params.put("parent", StringUtil.getString(params.get("parent")));
                res = selectMapper.listSubProcessNew(params);
                break;
            // 泊位
            case "BERTH" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.BERTH.getTableName(),
                        SelectEnum.BERTH.getValueName(),
                        SelectEnum.BERTH.getLabelName(),
                        " STATUS = '1' ");
                break;
                // 泊位缆庄
            case "BERTH_BOLLARD" :
                if (StringUtil.isEmpty(StringUtil.getString(parent))) {
                    return new ArrayList<>();
                } else {
                    res = selectMapper.getLocalSelect(
                            SelectEnum.BERTH_BOLLARD.getTableName(),
                            SelectEnum.BERTH_BOLLARD.getValueName(),
                            SelectEnum.BERTH_BOLLARD.getLabelName(),
                            " BERTH_ID = " + (StringUtils.isEmpty(StringUtil.getString(parent)) ? StringUtils.EMPTY : Long.parseLong(StringUtil.getString(parent))));
                }
                break;
            // 省
            case "PROVINCE" :
                res = selectMapper.getCityList(null);
                break;
            // 市
            case "CITY" :
                if (StringUtil.isEmpty(StringUtil.getString(parent))) {
                    return new ArrayList<>();
                } else {
                    res = selectMapper.getCityList(StringUtil.getString(parent));
                }
                break;
            // 机械类型
            case "MAC_TYPE" :
                res = selectMapper.getMacTypetList();
                break;
            // 机械型号
            case "MAC_MODEL" :
                if (StringUtil.isEmpty(StringUtil.getString(parent))) {
                    return new ArrayList<>();
                } else {
                    res = selectMapper.getMacModelList(StringUtil.getString(parent));
                }
                break;
                // 设备
            case "MAC" :
                res = selectMapper.listMac(params);
                break;
            //服务类型最新（商务过磅计划用）
            case "BUSINESS_WORK_TYPE" :
                res = selectMapper.listBusinessWorkType();
                break;
            //服务类型最新（杂货过磅计划用）
            case "PRODUCE_WORK_TYPE" :
                res = selectMapper.listProduceWorkType();
                break;
            // 固机队人员回显
            case "MACHINE_USER" :
                res = selectMapper.listMachineUser(params);
                break;
                // 拖轮
            case "TUG" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.TUG.getTableName(),
                        SelectEnum.TUG.getValueName(),
                        SelectEnum.TUG.getLabelName(),
                        null);
                break;
                // 停工原因
           /* case "STOP_REASON" :
                if (StringUtil.isEmpty(StringUtil.getString(parent))) {
                    return new ArrayList<>();
                } else {
                    res = selectMapper.getLocalSelect(
                            SelectEnum.STOP_REASON.getTableName(),
                            SelectEnum.STOP_REASON.getValueName(),
                            SelectEnum.STOP_REASON.getLabelName(),
                            " STOP_REASON_CLASS_CODE = '" + DictTypeEnum.DictDataEnum.match(DictTypeEnum.STOP_REASON_CLASS, StringUtil.getString(parent)) + "'");
                }
                break;*/
            // 停时类别
            case "STOP_HOUR_TYPE" :
                res = selectMapper.listStopHourType(params);
                break;
            // 停工原因
            case "STOP_REASON" :
                res = selectMapper.listStopReason(params);
                break;
            // 字典
            case "DICT" :
                String dictType = StringUtil.getString(params.get("dictType"));
                res = selectMapper.getLocalSelect(
                        SelectEnum.DICT.getTableName(),
                        SelectEnum.DICT.getValueName(),
                        SelectEnum.DICT.getLabelName(),
                        " DICT_TYPE = '" + DictTypeEnum.match(dictType) + "' AND STATUS = '1' ");
                break;
            case "CONSTANT" :
                String constantType = StringUtil.getString(params.get("types"));
                res = selectMapper.getLocalSelect(
                        SelectEnum.CONSTANT.getTableName(),
                        SelectEnum.CONSTANT.getValueName(),
                        SelectEnum.CONSTANT.getLabelName(),
                        " TYPE_CD = '" + ConstantsTypeEnum.match(constantType) + "'");
                break;
            case "STORAGE_YARD_LEVEL" :
                if (StringUtil.isEmpty(StringUtil.getString(parent))) {
                    return new ArrayList<>();
                } else {
                    String level = StringUtil.getString(params.get("level"));
                    level = StringUtils.isEmpty(level) ? StringUtils.EMPTY : String.valueOf(Integer.parseInt(level));

                    res = selectMapper.getLocalSelect(
                            SelectEnum.STORAGE_YARD_LEVEL.getTableName(),
                            SelectEnum.STORAGE_YARD_LEVEL.getValueName(),
                            SelectEnum.STORAGE_YARD_LEVEL.getLabelName(),
                            " PARENT_ID = " + Long.parseLong(String.valueOf(parent)) + " AND STORAGE_YARD_LEVEL = '" + level + "'");
                }
                break;
            case "CONTRACT":
                Object customerIdsObj;
                if ((customerIdsObj = params.get("customerIds")) != null) {
                    String customerIds = String.valueOf(customerIdsObj);
                    if (!customerIds.isEmpty()) {
                        Long[] customerIdArr = Arrays.stream(customerIds.split(",")).map(Long::valueOf).toArray(Long[]::new);
                        params.put("customerIdArr", customerIdArr);
                    }
                }
                res = selectMapper.listContract(params);
                break;
                //标准体系
            case "STANDARD_SYSTEM" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.STANDARD_SYSTEM.getTableName(),
                        SelectEnum.STANDARD_SYSTEM.getValueName(),
                        SelectEnum.STANDARD_SYSTEM.getLabelName(),null);
                break;
            //工艺流程
            case "PROCESS" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.PROCESS.getTableName(),
                        SelectEnum.PROCESS.getValueName(),
                        SelectEnum.PROCESS.getLabelName(),null);
                break;
            //行名航次
            case "T_DIS_SHIPVOYAGE" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.T_DIS_SHIPVOYAGE.getTableName(),
                        SelectEnum.T_DIS_SHIPVOYAGE.getValueName(),
                        SelectEnum.T_DIS_SHIPVOYAGE.getLabelName(),null);
                break;
            case "WBHTFL" :
                res = selectMapper.getLocalSelect(
                        SelectEnum.WBHTFL.getTableName(),
                        SelectEnum.WBHTFL.getValueName(),
                        SelectEnum.WBHTFL.getLabelName(),"DICT_TYPE = 'WBHTFL'");
                break;
            default:
                break;
        }

        return res;

    }

    /**
     * 获取本地下拉框数据源
     * @param types
     * @return
     */
    @Override
    public HashMap<String, List<Map<String, Object>>> getLocalSelects(String types) {

        String[] arrayTypes = types.split(",");

        HashMap<String, List<Map<String, Object>>> map = new HashMap<>();

        // 循环查询多个本地下拉框
        for (String type : arrayTypes) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("type", type);
            map.put(type, this.getLocalSelect(params));
        }

        return map;
    }

    /**
     * 获取指令信息
     */
    @Override
    public List<ResponsePopupTrustDTO> getPopupTrust(Map<String, Object> params) {
        if(params!=null && "1".equals(String.valueOf(params.get("trustType"))) && params.get("shipStatusBroadCode")!=null){

            String shipStatusBroadCode = String.valueOf(params.get("shipStatusBroadCode"));
            if(StringUtils.isNotBlank(shipStatusBroadCode)){
                if (SelectServiceImpl.ShipStatusBroadEnum.YUDAO.getCode().equals(shipStatusBroadCode)) {
                    params.put("shipStatusCodes",Collections.singletonList(JIESHOU.getCode()));
                }
                if (SelectServiceImpl.ShipStatusBroadEnum.MAODI.getCode().equals(shipStatusBroadCode)) {
                    params.put("shipStatusCodes",Arrays.asList(DIMAO.getCode(), QIMAO.getCode(),LIBO.getCode()));
                }
                if (SelectServiceImpl.ShipStatusBroadEnum.ZAIGANG.getCode().equals(shipStatusBroadCode)) {
                    params.put("shipStatusCodes",Arrays.asList(KAOBO.getCode(), YIBO.getCode(), KAIGONG.getCode(), TINGGONG.getCode(), FUGONG.getCode(), WANGONG.getCode()));
                }
                if (SelectServiceImpl.ShipStatusBroadEnum.LIBO.getCode().equals(shipStatusBroadCode)) {
                    params.put("shipStatusCodes",Arrays.asList(LIGANG.getCode()));
                }
            }
        }

        List<ResponsePopupTrustDTO> list = selectMapper.getPopupTrustNew(params);
        if(CollectionUtils.isEmpty(list)){
            return list;
        }

        List<Long> trustIds = list.stream().map(ResponsePopupTrustDTO::getId).collect(Collectors.toList());
        List<TBusTrustDTO> tBusTrustDTOS = tBusTrustMapper.listShipvoyageItemByTrustIdsNew(trustIds);
        for (ResponsePopupTrustDTO o : list) {
            if(StringUtils.isEmpty(o.getShipNameVoyages()) || o.getShipNameVoyages().equals("/")){
                String shipNameVoyages = tBusTrustDTOS.stream()
                        .filter(v2 -> o.getId().equals(Long.valueOf(String.valueOf(v2.getTrustId()))))
                        .map(v2 -> String.valueOf(v2.getShipNameVoyage()))
                        .collect(Collectors.joining("，"));
                o.setShipNameVoyages(shipNameVoyages);
                o.setVoyage(shipNameVoyages);
            }
        }
        if("2".equals(String.valueOf(params.get("trustType")))){
            List<Long> tmpTrustIds = list.stream().map(ResponsePopupTrustDTO::getId).collect(Collectors.toList());
            List<ResponsePopupTrustDTO> isClearList= tBusTrustMapper.getIsClearInfoByTrustIds(tmpTrustIds);
            String isclear = params.get("isClear")!=null?String.valueOf(params.get("isClear")):"";

            List<Long> clearIds = isClearList.stream().filter(o -> o.getIdCount() - o.getClearNumber() == 0).collect(Collectors.toList()).stream().map(ResponsePopupTrustDTO::getId).collect(Collectors.toList());

            if("1".equals(isclear)){
                list = list.stream().filter(o -> clearIds.contains(o.getId())).collect(Collectors.toList());
            }
            if("0".equals(isclear)){
                list = list.stream().filter(o -> !clearIds.contains(o.getId())).collect(Collectors.toList());
            }

            if(CollectionUtils.isEmpty(list)){
                return list;
            }

            list.forEach(o->{
                TBusTrustLocationSearchDTO tmpDto = new TBusTrustLocationSearchDTO();
                tmpDto.setTrustId(o.getId());
                List<TBusTrustLocationDTO> tmpList = tBusTrustLocationService.getListByCondition(tmpDto);
                if(org.apache.commons.collections.CollectionUtils.isNotEmpty(tmpList)){
                    List<TBusTrustLocationDTO.Location> locations = Lists.newArrayList();
                    List<String> regionIdsTarget = Lists.newArrayList();
                    String storehouseName = new String();
                    for (int i = 0; i < tmpList.size(); i++) {
                        if(i==0){
                            storehouseName +=tmpList.get(i).getStorehouseName()+"/"+tmpList.get(i).getRegionName();
                        }else {
                            storehouseName +=","+tmpList.get(i).getStorehouseName()+"/"+tmpList.get(i).getRegionName();
                        }
                        TBusTrustLocationDTO.Location location = new TBusTrustLocationDTO.Location();
                        BeanUtil.copyProperties(tmpList.get(i),location);
                        locations.add(location);
                        regionIdsTarget.add(tmpList.get(i).getRegionId());
                    }
                    o.setMassNamesTarget(storehouseName);
                }
            });
        }

        return list;
    }

}
