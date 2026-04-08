package com.yy.ppm.common.service.impl;

import com.yy.common.log.MicroLogger;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.common.bean.dto.SelecSearchDTO;
import com.yy.ppm.common.enums.SelectEnum;
import com.yy.ppm.common.mapper.SelectMapper;
import com.yy.ppm.common.service.SelectService;
import com.yy.ppm.equipment.bean.dto.EquipmentSelectDTO;

import com.yy.ppm.equipment.bean.dto.EMaintProjApplyDTO;
import com.yy.ppm.equipment.service.EMaintInfoService;
import com.yy.ppm.equipment.service.MEquipmentInfoService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

import  com.yy.ppm.common.enums.DictTypeEnum;
import  com.yy.ppm.common.enums.ConstantsTypeEnum;
/**
 * 下拉框数据源
 */
@Service
public class SelectServiceImpl implements SelectService {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SelectServiceImpl.class);

    @Resource
    public SelectMapper selectMapper;
    @Autowired
    private MEquipmentInfoService mEquipmentInfoService;

    @Autowired
    private EMaintInfoService eMaintInfoService;



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
            return Arrays.stream(ShipStatusBroadEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
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
            case "CUSTOMER_SUPPLIER" :
                res = selectMapper.materialSupplier(selectCommonSearch);
                break;
            case "USER" :
                res = selectMapper.getUserRemoteList(selectCommonSearch);
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
                    // 按部门查询
                } else if (params.get("deptId") != null) {
                    Long deptId = Long.parseLong(StringUtil.getString(params.get("deptId")));
                    res = selectMapper.getUserByDept(deptId);

                } else if (params.get("companyId") != null) {
                    Long companyId = Long.parseLong(StringUtil.getString(params.get("companyId")));
                    res = selectMapper.getUserByCompany(companyId);

                }else {
                    res = selectMapper.getLocalSelect(
                            SelectEnum.USER.getTableName(),
                            SelectEnum.USER.getValueName(),
                            SelectEnum.USER.getLabelName(),
                            null);
                }
                break;

            // 设备类型
            case "EQUIP_TYPE" :
                String categoryLevel = StringUtil.getString(params.get("categoryLevel"));
                String parentId = StringUtil.getString(params.get("parentId"));
                res = selectMapper.getEqptType(categoryLevel,parentId);
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
            // 字典
            case "DICT" :

                String dictType = StringUtil.getString(params.get("dictType"));
                if("E_ENERGY".equals(dictType)){
                    System.out.println(dictType);
                }
                String remark = StringUtil.getString(params.get("remark"));
                // 基础过滤条件
                String condition = " DICT_TYPE = '" + DictTypeEnum.match(dictType) + "' AND STATUS = '1'";
                // 动态拼接 remark 条件：只有当 remark 不为空时才加入 SQL
                if (StringUtil.isNotEmpty(remark)) {
                    condition += " AND REMARK = '" + remark + "'";
                }
                res = selectMapper.getLocalSelect(
                        SelectEnum.DICT.getTableName(),
                        SelectEnum.DICT.getValueName(),
                        SelectEnum.DICT.getLabelName(),
                        condition);
                break;
//
            case "CONSTANT" :
                String constantType = StringUtil.getString(params.get("types"));
                res = selectMapper.getLocalSelect(
                        SelectEnum.CONSTANT.getTableName(),
                        SelectEnum.CONSTANT.getValueName(),
                        SelectEnum.CONSTANT.getLabelName(),
                        " TYPE_CD = '" + ConstantsTypeEnum.match(constantType) + "'");
                break;
//
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
//                高级查询业务枚举
            case "BUSINESS":
                // 业务类型：根据 types 参数判断具体业务逻辑
                String businessType = StringUtil.getString(params.get("types"));
                res = handleBusinessType(businessType, params);
                break;
                // 流程表单
            case "BPM_FORM":
                res = selectMapper.getLocalSelect(
                        SelectEnum.BPM_FORM.getTableName(),
                        SelectEnum.BPM_FORM.getValueName(),
                        SelectEnum.BPM_FORM.getLabelName(),
                        "");
                break;
                // 流程分类
            case "BPM_CATEGORY":
                res = selectMapper.getLocalSelect(
                        SelectEnum.BPM_CATEGORY.getTableName(),
                        SelectEnum.BPM_CATEGORY.getValueName(),
                        SelectEnum.BPM_CATEGORY.getLabelName(),
                        "STATUS = 0");
                break;
                // 角色
            case "ROLE":
                res = selectMapper.getLocalSelect(
                        SelectEnum.ROLE.getTableName(),
                        SelectEnum.ROLE.getValueName(),
                        SelectEnum.ROLE.getLabelName(),
                        "status = 1");
                break;
            case "BPM_MODEL":
                res = selectMapper.getLocalSelect(
                        SelectEnum.BPM_MODEL.getTableName(),
                        SelectEnum.BPM_MODEL.getValueName(),
                        SelectEnum.BPM_MODEL.getLabelName(),
                        "");
                break;
                        // 设备
            case "EQUIPMENT" :
                String keyword = StringUtil.getString(params.get("keyword"));
                List<EquipmentSelectDTO> equipmentList = mEquipmentInfoService.getEquipmentSelectList(keyword);
                // 转换为 Map 格式
                res = new ArrayList<>();
                for (EquipmentSelectDTO equipment : equipmentList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", equipment.getValue());
                    map.put("label", equipment.getLabel());
                    res.add(map);
                }
                break;
            // 维修项目申请单
            case "MAINT_PROJ_APPLY" :
                String equipId = StringUtil.getString(params.get("equipId"));
                String appType = StringUtil.getString(params.get("appType"));
                String appNumber = StringUtil.getString(params.get("appNumber"));
                String maintInfoId = StringUtil.getString(params.get("maintInfoId"));
                List<EMaintProjApplyDTO> maintProjSelectList = eMaintInfoService.getMaintProjSelectList(equipId,appType,appNumber,maintInfoId);
                // 转换为 Map 格式
                res = new ArrayList<>();
                for (EMaintProjApplyDTO eMaintProjApplyDTO : maintProjSelectList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", eMaintProjApplyDTO.getAppNumber());
                    map.put("label", eMaintProjApplyDTO.getAppUnitName());
                    res.add(map);
                }
                break;

            // 设备
            case "EQUIP_INFO" :
                res = selectMapper.getEqptInfo();
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
     * 处理业务类型逻辑
     * @param businessType 业务类型标识
     * @param params 参数集合
     * @return 业务数据列表
     */
    private List<Map<String, Object>> handleBusinessType(String businessType, Map<String, Object> params) {
        if (StringUtil.isEmpty(businessType)) {
            return new ArrayList<>();
        }
        // 类型
        String type = StringUtil.getString(params.get("types"));

        List<Map<String, Object>> res = new ArrayList<>();
        switch (type.toUpperCase()) {
            // 业务类型1：用户相关
            case "USER":
                res = selectMapper.getLocalSelect(
                        SelectEnum.USER.getTableName(),
                        SelectEnum.USER.getValueName(),
                        SelectEnum.USER.getLabelName(),
                        null);
                break;
            // 默认情况
            default:
               break;
        }
        return res;
    }

}
