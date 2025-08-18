package com.yy.ppm.appWork.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.pagehelper.Page;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.SpringUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.concurrent.DistributedLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.appWork.bean.dto.*;
import com.yy.ppm.appWork.bean.po.THqYardTallyPO;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.appWork.bean.po.TYardTallyPO;
import com.yy.ppm.appWork.mapper.TallyMapper;
import com.yy.ppm.appWork.service.TallyService;
import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.business.mapper.TBusCargoInfoMapper;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.enums.CleanMassSignEnum;
import com.yy.ppm.common.enums.DistributedLockKeyPrefixEnum;
import com.yy.ppm.common.enums.InoutStorageEnum;
import com.yy.ppm.common.enums.SourceTargetTypeEnum;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageQueryDTO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;
import com.yy.ppm.dispatch.mapper.TDisShipVoyageMapper;
import com.yy.ppm.master.bean.dto.MCargoDTO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.master.bean.po.MWorkProcessPO;
import com.yy.ppm.master.mapper.MCargoMapper;
import com.yy.ppm.produce.bean.dto.THqDataDTO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondarySearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.produce.mapper.THqDataMapper;
import com.yy.ppm.produce.mapper.THqTallyMapper;
import com.yy.ppm.produce.mapper.TPrdPortStorageMapper;
import com.yy.ppm.produce.mapper.TPrdWorkTicketNewMapper;
import com.yy.ppm.produce.service.THqDataService;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TallyServiceImpl implements TallyService {
    private static final MicroLogger LOGGER = new MicroLogger(TallyService.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private TallyMapper tallyMapper;

    @Resource
    private SysFileService sysFileService;

    @Resource
    private SecurityUtils securityUtils;

    @Autowired
    private BusinessCommonService businessCommonService;

    @Resource
    private SysParameterMapper sysParameterMapper;

    @Autowired
    private TPrdPortStorageMapper tPrdPortStorageMapper;

    @Resource
    private MCargoMapper cargoMapper;
    @Autowired
    private TPrdWorkTicketNewMapper workTicketMapper;

    @Resource
    private TBusCargoInfoMapper tBusCargoInfoMapper;
    @Resource
    private TDisShipVoyageMapper tDisShipVoyageMapper;

    @Autowired
    private THqDataService hqDataService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private THqDataMapper tHqDataMapper;
    @Resource
    private THqTallyMapper tHqTallyMapper;

    private static boolean newBusiness = true;


    @Override
    public List<TPrdWorkPlanDTO> getWorkPlan(TPrdWorkPlanSearchDTO searchDTO) {
        //查询该用户是否是调度室和库场部的值班主任
        Integer flag = tallyMapper.getIsDept(securityUtils.getLoginUserId());
        searchDTO.setFlag(flag);
        searchDTO.setLoginId(securityUtils.getLoginUserId() + "");
        List<TPrdWorkPlanDTO> workPlanList = tallyMapper.getWorkPlan(searchDTO);
        List<Map<String, Object>> maps = tallyMapper.getWorkPlanCargoInfo(searchDTO);
        Iterator<TPrdWorkPlanDTO> iter = workPlanList.iterator();
        while (iter.hasNext()) {
            TPrdWorkPlanDTO po = iter.next();
            //查询该计划的所有货物的作业方式
//            List<Map<String, Object>> list = tallyMapper.getWorkType(po.getTrustId());
//            for (Map<String, Object> map : list) {
//                if ("2".equals(map.get("workType").toString())) {
//                    //删除该条数据(2:散杂)
//                    iter.remove();
//                    break;
//                }
//            }
        }
        if (!CollectionUtils.isEmpty(workPlanList)) {
            for (TPrdWorkPlanDTO prdWorkPlanDTO : workPlanList) {
                if (StringUtils.isNoneBlank(prdWorkPlanDTO.getCargoOwnerName())) {
                    List<String> cargoOwnerNameList = Arrays.asList(prdWorkPlanDTO.getCargoOwnerName().split(","));
                    prdWorkPlanDTO.setCargoOwnerName(String.join(",", cargoOwnerNameList.stream()
                            .distinct()
                            .collect(Collectors.toList())));
                }

                if (StringUtils.isNoneBlank(prdWorkPlanDTO.getCargoName())) {
                    List<String> cargoNameList = Arrays.asList(prdWorkPlanDTO.getCargoName().split(","));
                    prdWorkPlanDTO.setCargoName(String.join(",", cargoNameList.stream()
                            .distinct()
                            .collect(Collectors.toList())));
                }

                if (StringUtils.isNoneBlank(prdWorkPlanDTO.getCargoInfoNo())) {
                    List<String> cargoInfoNoList = Arrays.asList(prdWorkPlanDTO.getCargoInfoNo().split(","));
                    prdWorkPlanDTO.setCargoInfoNo(String.join(",", cargoInfoNoList.stream()
                            .distinct()
                            .collect(Collectors.toList())));
                }
                if ("1".equals(prdWorkPlanDTO.getIsDirectAccess())) {
                    //直取
                    List<String> type;
                    //源是船的为船-车
                    if (SourceTargetTypeEnum._01.getCode().equals(prdWorkPlanDTO.getSourceCd())) {
                        type = Collections.singletonList("疏港");
                    } else if (SourceTargetTypeEnum._05.getCode().equals(prdWorkPlanDTO.getSourceCd())) {
                        //场-车 (陆集陆疏)
                        type = Collections.singletonList("陆销");
                    } else {
                        //目的是船是车-船
                        type = Stream.of("集港", "拆箱集港").collect(Collectors.toList());
                    }
                    List<Map<String, Object>> tBusCargoInfoDTOS = tallyMapper.getTrustInfoNo(prdWorkPlanDTO.getTrustId().toString(), type);
                    if (!CollectionUtils.isEmpty(tBusCargoInfoDTOS)) {
                        prdWorkPlanDTO.setCargoInfoList(tBusCargoInfoDTOS);
                    }
                } else {
                    List<Map<String, Object>> tBusCargoInfoDTOS = maps.stream()
                            .filter(obj -> obj.get("planId").toString().equals(prdWorkPlanDTO.getId().toString()))
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(tBusCargoInfoDTOS)) {
                        prdWorkPlanDTO.setCargoInfoList(tBusCargoInfoDTOS);
                    }
                }

            }
        }

        for (TPrdWorkPlanDTO dto : workPlanList) {
            if ("2".equals(dto.getPlanType())) {
                //集疏港计划显示船名
                List<Map<String, Object>> list = tallyMapper.getShipName(dto.getTrustId());
                if (list != null && list.size() != 0) {
                    if (list.get(0).get("shipNameVoyage").toString() != null)
                        dto.setShipvoyageLabel(list.get(0).get("shipNameVoyage").toString());
                }

            }
        }
        return workPlanList;
    }

    @Override
    public List<Map<String, Object>> getProcessInfoList(MWorkProcessSearchDTO mWorkProcessSearchDTO) {
        final String methodName = "MWorkProcessServiceImpl: getList";
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return tallyMapper.getProcessInfoList(mWorkProcessSearchDTO);
    }

    @Override
    public List<Map<String, Object>> getMechanics(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO) {
        tPrdDispatchSecondarySearchDTO.setOperatorsId(securityUtils.getLoginUserId());
        List<Map<String, Object>> listMap = tallyMapper.getMechanics(tPrdDispatchSecondarySearchDTO);
        Map<String, Object> wu = Maps.newHashMap();
        wu.put("equipmentId", 999999999l);
        wu.put("equipmentNo", "无作业机械");
        listMap.add(wu);
        return listMap;
    }

    @Override
    public List<Map<String, Object>> getTransfer(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO) {
        tPrdDispatchSecondarySearchDTO.setOperatorsId(securityUtils.getLoginUserId());
        return tallyMapper.getTransfer(tPrdDispatchSecondarySearchDTO);
    }

    @Override
    public List<Map<String, Object>> getCarFer(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO) {
        //判断作业过程
        Map<String, Object> process = tallyMapper.getIsZq(tPrdDispatchSecondarySearchDTO.getProcessCode());
        //指令票货ID
        String planCode = "";
        if ("1".equals(process.get("isDirectAccess").toString())) {
            //直取
            List<String> type;
            //源是船的为船-车
            if (SourceTargetTypeEnum._01.getCode().equals(process.get("sourceCd").toString())) {
                type = Collections.singletonList("疏港");
            } else if (SourceTargetTypeEnum._05.getCode().equals(process.get("sourceCd").toString())) {
                //场-车 (陆集陆疏)
                type = Collections.singletonList("陆销");
            } else {
                //目的是船是车-船
                type = Stream.of("集港", "拆箱集港").collect(Collectors.toList());
            }
            tPrdDispatchSecondarySearchDTO.setType(type);
            planCode = tallyMapper.getPlanCodeZ(tPrdDispatchSecondarySearchDTO);
        } else {
            //根据指令ID查询指令票货ID
            planCode = tallyMapper.getPlanCode(tPrdDispatchSecondarySearchDTO);
        }
        if (planCode != null && (!"".equals(planCode))) {
            String ids = "(" + planCode + ")";
            //新地磅车号
            List<Map<String, Object>> list = tallyMapper.getCarFerNew(ids);
            return list;
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public List<Map<String, Object>> getCargoInfoId(String trustId, String planId, String processCode) {
        List<Map<String, Object>> cargoInfoIdList = tallyMapper.getCargoInfoId(trustId, planId);
        if (processCode != null && (!"".equals(processCode))) {
            //判断作业过程
            Map<String, Object> process = tallyMapper.getIsZq(processCode);
            //直取
            if ("1".equals(process.get("isDirectAccess").toString())) {
                //直取(根据票货ID查询指令票货ID)
                List<String> type;
                //源是船的为船-车
                if (SourceTargetTypeEnum._01.getCode().equals(process.get("sourceCd").toString())) {
                    type = Collections.singletonList("疏港");
                } else {
                    //目的是船是车-船
                    type = Stream.of("集港", "拆箱集港").collect(Collectors.toList());
                }
                for (Map<String, Object> map : cargoInfoIdList) {
                    List<Map<String, Object>> businessNoList = tallyMapper.getTrustId(Long.parseLong(map.get("id").toString()), type);
                    if (!CollectionUtils.isEmpty(businessNoList)) {
                        map.put("cargoName", map.get("cargoInfoNo") + "/" + businessNoList.get(0).get("businessNo") + "/" + map.get("cargoNameZ") + "/" + map.get("cargoOwnerName"));
                    }
                }

            }
        }

        return cargoInfoIdList;
    }

    @Override
    public List<Map<String, Object>> getCargoInfoIdTr(String planId) {
        return tallyMapper.getCargoInfoIdTr(planId);
    }

    public void insertIntOutYard(TYardTallyItemPO po, TYardTallyPO tYardTallyPO) {
        //查询出入库标识
        String inOutType = tallyMapper.getInoutType(tYardTallyPO.getProcessCode());
        if (tYardTallyPO.getIsCrk() != null) {
            inOutType = tYardTallyPO.getIsCrk();
        }
        List<TPrdPortStorageDetailPO> portStorageDetails = new ArrayList<>();
        TPrdPortStorageDetailPO tPrdPortStorageDetailPO = new TPrdPortStorageDetailPO();
        tPrdPortStorageDetailPO.setCargoTallyDetailId(po.getId()); //子表ID
        tPrdPortStorageDetailPO.setWorkDate(DateUtils.parseDate(tYardTallyPO.getWorkDate(), "yyyy-MM-dd"));
        tPrdPortStorageDetailPO.setClassCode(tYardTallyPO.getClassCode()); //班次
        tPrdPortStorageDetailPO.setClassName(tYardTallyPO.getClassName());
        tPrdPortStorageDetailPO.setProcessDetailCode(tYardTallyPO.getProcessCode()); //作业过程
        tPrdPortStorageDetailPO.setProcessDetailName(tYardTallyPO.getProcessName());
        tPrdPortStorageDetailPO.setInoutStorageCode(InoutStorageEnum._10.getCode()); //类型
        tPrdPortStorageDetailPO.setInoutStorageName(InoutStorageEnum._10.getLabel());
        tPrdPortStorageDetailPO.setInoutDate(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate());
        tPrdPortStorageDetailPO.setCleanMassSign(CleanMassSignEnum._0.getCode());
        tPrdPortStorageDetailPO.setCompanyId(tYardTallyPO.getCompanyId()); //公司信息
        tPrdPortStorageDetailPO.setCompanyName(tYardTallyPO.getCompanyName());
        tPrdPortStorageDetailPO.setCargoTallyId(tYardTallyPO.getId()); //理货主表ID
        tPrdPortStorageDetailPO.setCargoInfoId(po.getCargoInfoId());
        if (inOutType != null) {
            //出库
            if ("201".equals(inOutType)) {
                tPrdPortStorageDetailPO.setQuantity(-po.getQuantity()); //件数
                tPrdPortStorageDetailPO.setTon(po.getTon().negate()); //重量
            } else if ("101".equals(inOutType)) {
                //入库
                tPrdPortStorageDetailPO.setQuantity(po.getQuantity()); //件数
                tPrdPortStorageDetailPO.setTon(po.getTon()); //重量
            }
        } else {
            throw new BusinessRuntimeException("理货失败，获取作业过程信息失败");
        }
        tPrdPortStorageDetailPO.setStorehouseId(po.getStorehouseId()); //库场
        tPrdPortStorageDetailPO.setStorehouseName(po.getStorehouseName());
        tPrdPortStorageDetailPO.setRegionId(po.getLocationId());
        tPrdPortStorageDetailPO.setRegionName(po.getLocationNo());
        tPrdPortStorageDetailPO.setMassId(po.getStackPositionId());
        tPrdPortStorageDetailPO.setMassName(po.getStackPositionName());
        portStorageDetails.add(tPrdPortStorageDetailPO);
        if ("201".equals(inOutType) || "101".equals(inOutType)) {
            businessCommonService.insertPortStorageDetail(portStorageDetails);
        }
    }


    @Override
    public List<Map<String, Object>> getStore(Long workPlanId) {
        return tallyMapper.getStore(workPlanId);
    }

    @Override
    public List<Map<String, Object>> getStackPosition(Long id) {
        return tallyMapper.getStackPosition(id);
    }

    @Override
    public List<TYardTallyPO> getWaitWork(Long trustId) {
        final String methodName = "getWaitWork";
        LOGGER.enter(methodName, "根据指令ID查询待作业记录[start], trustId: " + trustId);
        List<TYardTallyPO> list = tallyMapper.getWaitWork(trustId);
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return list;
    }

    @Override
    public Pages<TYardTallyItemPO> getTallyRecord(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        final String methodName = "getTallyRecord";
        LOGGER.enter(methodName, "根据计划id和作业过程查询理货记录[start]");
        Pages<TYardTallyItemPO> pages = PageHelperUtils.limit(pageParameter, () -> {
            return tallyMapper.getTallyRecord(tallyRecordSearchDTO);
        });
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return pages;
    }

    @Override
    public Pages<THqYardTallyPO> getHqTallyRecord(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        Pages<THqYardTallyPO> pages = PageHelperUtils.limit(pageParameter, () -> {
            return tallyMapper.getHqTallyRecord(tallyRecordSearchDTO);
        });
        return pages;
    }
    @Override
    public List<THqDataDTO> getHqTallyData(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        //先查补录表
        List<THqDataDTO> hqDataDTOS = tHqDataMapper.getHqTallyData(tallyRecordSearchDTO.getTallyId());
        if(CollectionUtil.isNotEmpty(hqDataDTOS)){
            hqDataDTOS.stream().forEach(e->{
                e.setIsDisabled(true);
            });
//            return hqDataDTOS;
        }
        int hqDataSize = hqDataDTOS.size();
        List<THqDataDTO> reslutList = Lists.newArrayList();
        List<THqDataDTO> list = tallyMapper.getHqTallyData(tallyRecordSearchDTO);
        //入库:船/场，船/岸，车/场，车/岸
        if(Arrays.asList("01","03").stream().anyMatch(tallyRecordSearchDTO.getSource()::equals) && Arrays.asList("05","06").stream().anyMatch(tallyRecordSearchDTO.getTarget()::equals)){
            if(list.size()>0){
                THqDataDTO hqDataDTO = list.get(0);
                for(int i = 0;i< (hqDataDTO.getTallyQuantity()-hqDataSize) ;i++){
                    THqDataDTO dto = new THqDataDTO();
                    dto.setQuantity(1l);
                    dto.setTallyId(tallyRecordSearchDTO.getTallyId());
                    dto.setStatus("0");
                    reslutList.add(dto);
                }
            }
        }
        //出库:船/场，船/岸，车/场，车/岸
        else if(Arrays.asList("05","06").stream().anyMatch(tallyRecordSearchDTO.getSource()::equals) && Arrays.asList("01","03").stream().anyMatch(tallyRecordSearchDTO.getTarget()::equals)){
            if(list.size()>0){
                THqDataDTO hqDataDTO = list.get(0);
                for(int i = 0;i<(hqDataDTO.getTallyQuantity()-hqDataSize);i++){
                    THqDataDTO dto = new THqDataDTO();
                    dto.setQuantity(1l);
                    dto.setTallyId(tallyRecordSearchDTO.getTallyId());
                    dto.setStatus("10");
                    reslutList.add(dto);
                }
            }
        }else{
            if(list.size()>0){
                THqDataDTO hqDataDTO = list.get(0);
                for(int i = 0;i<(hqDataDTO.getTallyQuantity()-hqDataSize);i++){
                    THqDataDTO dto = new THqDataDTO();
                    dto.setQuantity(1l);
                    dto.setTallyId(tallyRecordSearchDTO.getTallyId());
                    dto.setStatus("10");
                    reslutList.add(dto);
                }
            }
        }
        hqDataDTOS.addAll(reslutList);
        return hqDataDTOS;
    }

    @Override
    public List<Map<String, Object>> getHqCargoName() {
        return tallyMapper.getHqCargoName();
    }

    @Override
    public List<Map<String, Object>> getHqYard() {
        return tallyMapper.getHqYard();
    }

    @Override
    public TYardTallyPO getTallyInfoById(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        TYardTallyPO tallyInfoById = tallyMapper.getTallyInfoById(tallyRecordSearchDTO);
        List<AppTallyCoilNumDTO> list = tallyMapper.getCoilTallyList(tallyInfoById.getId());
        if (!CollectionUtils.isEmpty(list)) {
            tallyInfoById.setCoilList(list);
            String result = list.stream()
                    .map(AppTallyCoilNumDTO::getCoilNum)
                    .collect(Collectors.joining(","));
            tallyInfoById.setCoil(result);
        }
        return tallyInfoById;
    }

    @Override
    public TYardTallyPO getTallyInfoByIdNew(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        TYardTallyPO tallyInfoById = tallyMapper.getTallyInfoByIdNew(tallyRecordSearchDTO);
        List<AppTallyCoilNumDTO> list = tallyMapper.getCoilTallyList(tallyInfoById.getId());
        if (!CollectionUtils.isEmpty(list)) {
            tallyInfoById.setCoilList(list);
            String result = list.stream()
                    .map(AppTallyCoilNumDTO::getCoilNum)
                    .collect(Collectors.joining(","));
            tallyInfoById.setCoil(result);
        }
        return tallyInfoById;
    }

    @Override
    public TYardTallyPO getTallyNew(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        return tallyMapper.getTallyNew(tallyRecordSearchDTO);
    }

    @Override
    public Map<String, Object> getTallyRecordSum(TallyRecordSearchDTO tallyRecordSearchDTO) {
        final String methodName = "getTallyRecordSum";
        Map<String, Object> map = tallyMapper.getTallyRecordSum(tallyRecordSearchDTO);
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return map;
    }

    @Override
    public Map<String, Object> getTallyRecordSumNew(TallyRecordSearchDTO tallyRecordSearchDTO) {
        final String methodName = "getTallyRecordSum";
        Map<String, Object> map = tallyMapper.getTallyRecordSumNew(tallyRecordSearchDTO);
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return map;
    }

    @Override
    public List<AppTallyLadingDTO> getCarDetailedList(TYardMeasureSearchDTO tYardMeasureSearchDTO) {
        final String methodName = "getCarDetailedList";
        LOGGER.enter(methodName, "查询出入库数据[start], getCarDetailedList: " + tYardMeasureSearchDTO);
        //根据指令查票货
        String str = tallyMapper.getTrustCargoId(tYardMeasureSearchDTO.getTrustId());
        if (str == null) {
            throw new BusinessRuntimeException("查询失败，未查询到票货信息");
        }
        str = "(" + str + ")";
        tYardMeasureSearchDTO.setTrustIds(str);

        SysParameterDTO sysParameter = sysParameterMapper.getByKey("TALLY_STORAGE_IS_FRONTIER");
        boolean switchIsFrontier = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
        if (switchIsFrontier) {
            tYardMeasureSearchDTO.setSwitchIsFrontier("1");
            //校验根据作业过程进行校验
            String isFrontier = tallyMapper.getProcessIsFrontier(tYardMeasureSearchDTO.getProcessCode());
            if (StringUtils.isNotEmpty(isFrontier)) {
                tYardMeasureSearchDTO.setIsFrontier(isFrontier);
            }
        } else {
            tYardMeasureSearchDTO.setSwitchIsFrontier(null);
        }
        List<AppTallyLadingDTO> carDetailedList = tallyMapper.getCarDetailedList(tYardMeasureSearchDTO);
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return carDetailedList;
    }

//    @Override
//    public Pages<AppTallyCoilNumDTO> getCoilList(AppTallyCoilNumDTO appTallyCoilNumDTO, PageParameter pageParameter) {
//        final String methodName = "getCoilList";
//        LOGGER.enter(methodName, "查询标号[start], getCarDetailedList: " + appTallyCoilNumDTO.getCargoInfoId());
//        Pages<AppTallyCoilNumDTO> pages = PageHelperUtils.limit(pageParameter, () -> {
//            return tallyMapper.getNumber(appTallyCoilNumDTO);
//        });
//        LOGGER.info("查询数据成功");
//        LOGGER.exit(methodName, "业务数据同步服务[end]");
//        return pages;
//    }

    @Override
    public List<AppTallyCoilNumDTO> getCoilList(AppTallyCoilNumDTO appTallyCoilNumDTO) {
        final String methodName = "getCoilList";
        LOGGER.enter(methodName, "查询标号[start], getCoilList: " + appTallyCoilNumDTO.getCargoInfoId());
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return tallyMapper.getNumber(appTallyCoilNumDTO);
    }

    @Override
    public List<AppTallyCoilNumDTO> getBoxList(AppTallyCoilNumDTO appTallyCoilNumDTO) {
        final String methodName = "getBoxList";
        LOGGER.enter(methodName, "查询箱号[start], getCoilList: " + appTallyCoilNumDTO.getCargoInfoId());
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return tallyMapper.getBoxList(appTallyCoilNumDTO);
    }

    @Override
    public List<DepartureDTO> getDepartureList(String truckPlate) {
        final String methodName = "getDepartureList";
        LOGGER.enter(methodName, "查询空/重车数据[start], getDepartureList: " + truckPlate);
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        List<DepartureDTO> departureList = tallyMapper.getDepartureList(truckPlate);
        return departureList;
    }

    @Override
    public List<DepartureDTO> getDepartureRecordList(String startDate, String endDate) {
        final String methodName = "getDepartureRecordList";
        LOGGER.enter(methodName, "查询空/重车数据记录[start], getDepartureList: ");
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        List<DepartureDTO> departureList = tallyMapper.getDepartureRecordList(startDate, endDate);
        return departureList;
    }

    @Override
    public String selectBh(String code) {
        return tallyMapper.selectBh(code);
    }

    @Override
    public List<Map<String, Object>> getHatch(Long id) {
        return tallyMapper.getHatch(id);
    }

    @Override
    public List<TallyCargoDTO> getCargoStatistics(List<Long> ids) {
        List<TallyCargoDTO> cargoStatistics = tallyMapper.getCargoStatistics(ids);
        cargoStatistics.stream().forEach(item -> {
            item.setQuantity(item.getZqQuantity() + item.getZyQuantity());
            item.setTon(item.getZqTon().add(item.getZyTon()));
        });
        return cargoStatistics;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void updateTally(TYardTallyPO tYardTallyPO) {
        //主表ID
        TallyRecordSearchDTO tallyRecordSearchDTO = new TallyRecordSearchDTO();
        tallyRecordSearchDTO.setTallyId(tYardTallyPO.getId());
        if (tYardTallyPO.getWeighbridgeId() != null) {
            String status = tallyMapper.getWeightStatusNew(tYardTallyPO.getWeighbridgeId().toString());
            if (status == null) {
                throw new BusinessRuntimeException("未查询到车辆信息,不允许修改!");
            }
            if ("1".equals(status)) {
                throw new BusinessRuntimeException("车辆已出港,不允许修改!");
            }
        }
        //获取理货记录详情信息
        TYardTallyPO tallyInfoById = tallyMapper.getTallyInfoById(tallyRecordSearchDTO);
        //根据理货记录子作业过程查询主作业过程的源和目的
        Map<String, Object> process = tallyMapper.getProcess(tallyInfoById.getProcessCode());
        //源:船 目的:场/岸 & 源:场/岸 目的:船 & 源:场/岸 目的: 场/岸
        boolean flag1 = SourceTargetTypeEnum._01.getCode().equals(process.get("source")) && SourceTargetTypeEnum._05.getCode().equals(process.get("destination"));
        boolean flag2 = SourceTargetTypeEnum._05.getCode().equals(process.get("source")) && SourceTargetTypeEnum._01.getCode().equals(process.get("destination")) && (!"10250005".equals(tallyInfoById.getProcessCode()));
        boolean flag3 = SourceTargetTypeEnum._05.getCode().equals(process.get("source")) && SourceTargetTypeEnum._05.getCode().equals(process.get("destination"));
        if (flag1 || flag2 || flag3 && (!"10210004".equals(tallyInfoById.getProcessCode())) && (!"10200005".equals(tallyInfoById.getProcessCode()))) {
            //装卸船倒运(有关联记录)
            //查询该理货记录的子作业过程是否有前置环节 有的话为后一环节，可直接修改
            Integer isPre = tallyMapper.getIsPre(tallyInfoById.getProcessCode());
            if (isPre == null) {
                throw new BusinessRuntimeException("获取理货记录作业过程前置环节失败");
            }
            //有前置环节，可直接修改
            if (isPre == 1) {
                updateTallyInfo(tYardTallyPO);
            } else {
                //没有前置环节的，则为前一环节,不可直接修改,后一环节未理货时才可修改，即关联ID为空
                if (tallyInfoById.getRelationId() == null) {
                    updateTallyInfo(tYardTallyPO);
                } else {
                    throw new BusinessRuntimeException("后一环节已理货,不允许修改");
                }
            }
        } else {
            updateTallyInfo(tYardTallyPO);
        }
        if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
            //先处理一下之前的数据
            //查询钢号信息
            List<AppTallyCoilNumDTO> numDTOList = tallyMapper.getCoilNumIdList(tYardTallyPO.getId().toString());
            if (numDTOList != null && numDTOList.size() != 0) {
                //删除理货钢号关系表
                tallyMapper.deleteCoilNum(tYardTallyPO.getId().toString());
                Map<String, Object> mapperProcess = tallyMapper.getProcessDetail(tallyInfoById.getProcessCode());
                if (SourceTargetTypeEnum._01.getCode().equals(mapperProcess.get("source")) && SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("destination"))) {
                    //船-岸 状态恢复成10
                    //有钢号信息。进行处理
                    tallyMapper.updateCoilNum(numDTOList, "10");
                } else if (SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("destination"))) {
                    //车-场 等入库 删除 恢复成出库状态 30
                    //有钢号信息。进行处理
                    tallyMapper.updateCoilNum(numDTOList, "30");
                } else if (SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("source"))) {
                    //岸-车 等出库 删除 恢复成入库状态 20
                    //有钢号信息。进行处理
                    tallyMapper.updateCoilNum(numDTOList, "20");
                }
                tYardTallyPO.setDestination(mapperProcess.get("destination").toString());
                tYardTallyPO.setSource(mapperProcess.get("source").toString());
            }

            saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, tYardTallyPO.getBhName());
            //修改磅单表备注信息
            String weightId = tallyMapper.getWeightId(tYardTallyPO.getId());
            if (StringUtils.isNotEmpty(weightId)) {
                String result = tYardTallyPO.getCoilList().stream()
                        .map(obj -> obj.getCoilNum()) // 将每个对象的属性拼接并用逗号分隔
                        .collect(Collectors.joining(","));
                result = "标号:" + result;
                tallyMapper.updateWeightRemark(weightId, result);
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateTallyInfoNew(TYardTallyPO tYardTallyPO) {
        //主表ID
        TallyRecordSearchDTO tallyRecordSearchDTO = new TallyRecordSearchDTO();
        tallyRecordSearchDTO.setTallyId(tYardTallyPO.getId());
        if (tYardTallyPO.getWeighbridgeId() != null) {
            String status = tallyMapper.getWeightStatusNew(tYardTallyPO.getWeighbridgeId().toString());
            if (status == null) {
                throw new BusinessRuntimeException("未查询到车辆信息,不允许修改!");
            }
            SysParameterDTO tallyCarFlag = sysParameterMapper.getByKey("TALLY_CAR_FLAG");
            boolean tallyFlag = ObjectUtil.isEmpty(tallyCarFlag) ? false : ("Y".equals(tallyCarFlag.getParamVal()) ? true : false);
            if (!tallyFlag) {
                if ("1".equals(status)) {
                    throw new BusinessRuntimeException("车辆已出港,不允许修改!");
                }
            }
        }
        //获取理货记录详情信息
        TYardTallyPO tallyInfoById = tallyMapper.getTallyInfoById(tallyRecordSearchDTO);
        //根据理货记录子作业过程查询主作业过程的源和目的
//        Map<String, Object> process = tallyMapper.getProcess(tallyInfoById.getProcessCode());
        //源:船 目的:场/岸 & 源:场/岸 目的:船 & 源:场/岸 目的: 场/岸
        SysParameterDTO sysParameter = sysParameterMapper.getByKey("TICKET_TALLY_FLAG");
        boolean flag = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
        Integer count = tallyMapper.getTicketById(tYardTallyPO.getId());
        if (flag && count != null && count > 0) {
            throw new BusinessRuntimeException("已签票，不允许修改理货记录!");
        }
        updateTallyInfoJhNew(tYardTallyPO);
        if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
            //先处理一下之前的数据
            //查询钢号信息
            List<AppTallyCoilNumDTO> numDTOList = tallyMapper.getCoilNumIdList(tYardTallyPO.getId().toString());
            if (numDTOList != null && numDTOList.size() != 0) {
                Map<String, Object> mapperProcess = tallyMapper.getProcessDetail(tallyInfoById.getProcessCode());
                String coilMaxStatus = tallyMapper.getCoilMaxStatus(String.valueOf(tYardTallyPO.getId()));
                tallyMapper.deleteCoilNum(tYardTallyPO.getId().toString());//删除理货钢号关系表
                //船/岸，船/场
                if (SourceTargetTypeEnum._01.getCode().equals(mapperProcess.get("source")) && (SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("destination")) || SourceTargetTypeEnum._06.getCode().equals(mapperProcess.get("destination")))) {
                    if("30".equals(coilMaxStatus)){
                        throw new BusinessRuntimeException("卷钢已经出库");
                    }
                    tallyMapper.updateCoilNum(numDTOList, "10");//有钢号信息。进行处理,船-岸 状态恢复成10
                }
                // -场、岸
                else if (SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("destination")) || SourceTargetTypeEnum._06.getCode().equals(mapperProcess.get("destination"))) {
                    tallyMapper.updateCoilNum(numDTOList, "30");//有钢号信息。进行处理,车-场 等入库 删除 恢复成出库状态 30
                }
                // 场、岸-
                else if (SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("source")) || SourceTargetTypeEnum._06.getCode().equals(mapperProcess.get("source"))) {
                    tallyMapper.updateCoilNum(numDTOList, "20");//有钢号信息。进行处理,岸-车 等出库 删除 恢复成入库状态 20
                }
                tYardTallyPO.setDestination(mapperProcess.get("destination").toString());
                tYardTallyPO.setSource(mapperProcess.get("source").toString());
            }

            saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, tYardTallyPO.getBhName());
            //修改磅单表备注信息
            String weightId = tallyMapper.getWeightId(tYardTallyPO.getId());
            if (StringUtils.isNotEmpty(weightId)) {
                String result = tYardTallyPO.getCoilList().stream()
                        .map(obj -> obj.getCoilNum()) // 将每个对象的属性拼接并用逗号分隔
                        .collect(Collectors.joining(","));
                result = "标号:" + result;
                tallyMapper.updateWeightRemark(weightId, result);
            }
        }
    }

    @Override
    public List<Map<String, Object>> getDept(Long planId) {
        return tallyMapper.getDept(planId);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void deleteNotes(TYardTallyPO tYardTallyPO) {
        if (tYardTallyPO.getTallyId() == null) {
            throw new BusinessRuntimeException("错误的理货ID");
        }
        //主表ID
        Long tallyId = Long.parseLong(tYardTallyPO.getTallyId());
        TallyRecordSearchDTO tallyRecordSearchDTO = new TallyRecordSearchDTO();
        tallyRecordSearchDTO.setTallyId(tallyId);
        //获取理货记录详情信息
        TYardTallyPO tallyInfoById = tallyMapper.getTallyInfoById(tallyRecordSearchDTO);
        //根据理货记录子作业过程查询主作业过程的源和目的
        Map<String, Object> process = tallyMapper.getProcess(tallyInfoById.getProcessCode());
        //源:船 目的:场/岸 & 源:场/岸 目的:船 & 源:场/岸 目的: 场/岸
        boolean flag1 = SourceTargetTypeEnum._01.getCode().equals(process.get("source")) && SourceTargetTypeEnum._05.getCode().equals(process.get("destination"));
        boolean flag2 = SourceTargetTypeEnum._05.getCode().equals(process.get("source")) && SourceTargetTypeEnum._01.getCode().equals(process.get("destination")) && (!"10250005".equals(tallyInfoById.getProcessCode())) && (!"10250006".equals(tallyInfoById.getProcessCode()));
        ;
        boolean flag3 = SourceTargetTypeEnum._05.getCode().equals(process.get("source")) && SourceTargetTypeEnum._05.getCode().equals(process.get("destination"));
        if (flag1 || flag2 || flag3 && (!"10210004".equals(tallyInfoById.getProcessCode())) && (!"10200005".equals(tallyInfoById.getProcessCode()))) {
            //装卸船倒运(有关联记录)
            //查询该理货记录的子作业过程是否有前置环节 有的话为后一环节，可直接删除
            Integer isPre = tallyMapper.getIsPre(tallyInfoById.getProcessCode());
            if (isPre == null) {
                throw new BusinessRuntimeException("获取理货记录作业过程前置环节失败");
            }
            //有前置环节，可直接删除
            if (isPre == 1) {
                delete(tallyId);
            } else {
                //没有前置环节的，则为前一环节,不可直接删除,后一环节未理货时才可删除，即关联ID为空
                if (tallyInfoById.getRelationId() == null) {
                    TYardTallyPO po = new TYardTallyPO();
                    po.setId(tallyId);
                    tallyMapper.deleteNotes(po);
                    //删除理货记录(子表)
                    tallyMapper.deleteNotesItem(tallyId);
                    //根据理货ID获取港存明细ID
                    List<Long> ids = tallyMapper.getPortStorageDetailId(tallyId);
                    if (ids != null && ids.size() != 0) {
                        //删除港存
                        businessCommonService.deletePortStorageDetail(ids);
                    }

                } else {
                    throw new BusinessRuntimeException("后一环节已理货,不允许删除");
                }
            }
        } else {
            deleteNoReshipment(tallyId, tallyInfoById);
        }
        //查询钢号信息
        List<AppTallyCoilNumDTO> numDTOList = tallyMapper.getCoilNumIdList(tYardTallyPO.getTallyId());
        if (numDTOList != null && numDTOList.size() != 0) {
            //删除理货钢号关系表
            tallyMapper.deleteCoilNum(tYardTallyPO.getTallyId());
            Map<String, Object> mapperProcess = tallyMapper.getProcessDetail(tallyInfoById.getProcessCode());
            if (SourceTargetTypeEnum._01.getCode().equals(mapperProcess.get("source")) && SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("destination"))) {
                //船-岸 状态恢复成10
                //有钢号信息。进行处理
                tallyMapper.updateCoilNum(numDTOList, "10");
            } else if (SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("destination"))) {
                //车-场 等入库 删除 恢复成出库状态 30
                //有钢号信息。进行处理
                tallyMapper.updateCoilNum(numDTOList, "30");
            } else if (SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("source"))) {
                //岸-车 等出库 删除 恢复成入库状态 20
                //有钢号信息。进行处理
                tallyMapper.updateCoilNum(numDTOList, "20");
            }

        }
    }

    public void delete(Long tallyId) {
        TYardTallyPO po = new TYardTallyPO();
        po.setId(tallyId);
        tallyMapper.deleteNotes(po);
        //删除理货记录(子表)
        tallyMapper.deleteNotesItem(tallyId);
        //更新关联ID
        tallyMapper.updateNotesRelationId(tallyId);
        //根据理货ID获取港存明细ID
        List<Long> ids = tallyMapper.getPortStorageDetailId(tallyId);
        if (ids != null && ids.size() != 0) {
            //删除港存
            businessCommonService.deletePortStorageDetail(ids);
        }

    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteNoReshipment(Long tallyId, TYardTallyPO tallyInfoById) {
        TYardTallyPO po = new TYardTallyPO();
        po.setId(tallyId);
        //删除理货记录主表
        tallyMapper.deleteNotes(po);
        //删除理货记录(子表)
        tallyMapper.deleteNotesItem(tallyId);
        //根据理货ID获取港存明细ID
        List<Long> ids = tallyMapper.getPortStorageDetailId(tallyId);
        if (ids != null && ids.size() != 0) {
            //删除港存
            businessCommonService.deletePortStorageDetail(ids);
        }
        if (tallyInfoById.getWeighbridgeId() != null) {
            if (tallyInfoById.getWorkErWeiId() == null) {
                //老地磅
                //地磅处理
                String status = SpringUtils.getBean(this.getClass()).getWeightStatus(tallyInfoById.getWeighbridgeId().toString());
                if (status == null) {
                    throw new BusinessRuntimeException("未查询到车辆信息,不允许删除!");
                }
                if ("7".equals(status)) {
                    throw new BusinessRuntimeException("车辆已出港,不允许删除!");
                }
                SpringUtils.getBean(this.getClass()).updateWeightStatus(tallyInfoById.getWeighbridgeId().toString());
            } else {
                //新地磅
                String status = tallyMapper.getWeightStatusNew(tallyInfoById.getWeighbridgeId().toString());
                if (status == null) {
                    throw new BusinessRuntimeException("未查询到车辆信息,不允许删除!");
                }
                if ("1".equals(status)) {
                    throw new BusinessRuntimeException("车辆已出港,不允许删除!");
                }
                tallyMapper.updateWeightStatusNew(tallyInfoById.getWeighbridgeId().toString());
            }
        }

    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void updateTallyInfo(TYardTallyPO tYardTallyPO) {
        final String methodName = "tYardTallyPO";
        LOGGER.enter(methodName, "修改理货记录[start], tYardTallyPO: " + tYardTallyPO);
        //修改理货记录主表
        tallyMapper.updateTally(tYardTallyPO);
        //判断作业过程的源和目的是否为场地，是的场地垛号必填
//        Map<String, Object> process = tallyMapper.getIsZq(tYardTallyPO.getProcessCode());
        for (TYardTallyItemPO po : tYardTallyPO.getListTallyItemList()) {
//            if (SourceTargetTypeEnum._05.getCode().equals(process.get("sourceCd").toString()) || SourceTargetTypeEnum._05.getCode().equals(process.get("targetCd").toString())) {
            if (("1".equals(po.getSourceOrTargetFlag()) && ("05".equals(po.getSourceCd()) || "06".equals(po.getTargetCd()))) || ("2".equals(po.getSourceOrTargetFlag()) && ("05".equals(po.getSourceCd()) || "06".equals(po.getTargetCd())))) {
                if (StringUtils.isEmpty(po.getStorehouseName())) {
                    throw new BusinessRuntimeException("请选择场地");
                }
                if (StringUtils.isEmpty(po.getStackPositionName())) {
                    throw new BusinessRuntimeException("请选择垛位");
                }
//                insertMiss(po);
                if (po.getCargoCode() != null) {
//                    Integer isTally = tallyMapper.getIsTally(po.getCargoCode());
                    //根据理货ID获取港存明细ID
                    List<Long> ids = tallyMapper.getPortStorageDetailItemId(po.getId());
                    ids.removeAll(Collections.singleton(null));
                    if (!CollectionUtils.isEmpty(ids)) {
                        //删除港存
                        businessCommonService.deletePortStorageDetail(ids);
                    }
                    //                    if ("10200005".equals(tYardTallyPO.getProcessCode()) || "10210004".equals(tYardTallyPO.getProcessCode())) {
                    if (("05".equals(po.getSourceCd()) && "06".equals(po.getTargetCd())) || ("06".equals(po.getSourceCd()) && "05".equals(po.getTargetCd()))) {
                        //场-岸 岸-场
                        Long storehouseId = po.getStorehouseId();
                        String storehouseName = po.getStorehouseName();
                        Long locationId = po.getLocationId();
                        String locationNo = po.getLocationNo();
                        Long stackPositionId = po.getStackPositionId();
                        String stackPositionName = po.getStackPositionName();
                        tYardTallyPO.setIsCrk("201");
                        TYardTallyItemPO pos = tallyMapper.getStorage(po.getId());
                        po.setStorehouseId(pos.getStorehouseTargetId()); //库场
                        po.setStorehouseName(pos.getStorehouseTargetName());
                        po.setLocationId(pos.getLocationTargetId());
                        po.setLocationNo(pos.getLocationTargetNo());
                        po.setStackPositionId(pos.getStackPositionTargetId());
                        po.setStackPositionName(pos.getStackPositionTargetName());
                        insertIntOutYard(po, tYardTallyPO);

                        tYardTallyPO.setIsCrk("101");
                        po.setStorehouseId(storehouseId); //库场
                        po.setStorehouseName(storehouseName);
                        po.setLocationId(locationId);
                        po.setLocationNo(locationNo);
                        po.setStackPositionId(stackPositionId);
                        po.setStackPositionName(stackPositionName);
                    }
                    insertIntOutYard(po, tYardTallyPO);
                }
            }
            //修改理货记录子表
            tallyMapper.updateTallyItem(po);
        }
        LOGGER.info("修改成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void updateTallyInfoJhNew(TYardTallyPO tYardTallyPO) {
        final String methodName = "tYardTallyPO";
        LOGGER.enter(methodName, "修改理货记录[start], tYardTallyPO: " + tYardTallyPO);
        //修改理货记录主表
        isLogout(tYardTallyPO.getCargoInfoId());
        tallyMapper.updateTally(tYardTallyPO);
        //判断作业过程的源和目的是否为场地，是的场地垛号必填
//        Map<String, Object> process = tallyMapper.getIsZq(tYardTallyPO.getProcessCode());
        for (TYardTallyItemPO po : tYardTallyPO.getListTallyItemList()) {
//            if (SourceTargetTypeEnum._05.getCode().equals(process.get("sourceCd").toString()) || SourceTargetTypeEnum._05.getCode().equals(process.get("targetCd").toString())) {
            isLogout(po.getCargoInfoId());
            if (("1".equals(po.getSourceOrTargetFlag()) && ("05".equals(po.getSourceCd()) || "06".equals(po.getSourceCd()))) || ("2".equals(po.getSourceOrTargetFlag()) && ("05".equals(po.getTargetCd()) || "06".equals(po.getTargetCd())))) {
                if (StringUtils.isEmpty(po.getStorehouseName())) {
                    throw new BusinessRuntimeException("请选择场地");
                }
                if (StringUtils.isEmpty(po.getStackPositionName())) {
                    throw new BusinessRuntimeException("请选择垛位");
                }
//                insertMiss(po);
                if (po.getCargoCode() != null) {
//                    Integer isTally = tallyMapper.getIsTally(po.getCargoCode());
                    //根据理货ID获取港存明细ID
                    List<Long> ids = tallyMapper.getPortStorageDetailItemId(po.getId());
                    ids.removeAll(Collections.singleton(null));
                    if (!CollectionUtils.isEmpty(ids)) {
                        //删除港存
                        businessCommonService.deletePortStorageDetail(ids);
                    }
                    //                    if ("10200005".equals(tYardTallyPO.getProcessCode()) || "10210004".equals(tYardTallyPO.getProcessCode())) {
                    //只要不是直取则变更港存
//                    if(("05".equals(po.getSourceCd())&&"06".equals(po.getTargetCd()))||("06".equals(po.getSourceCd())&&"05".equals(po.getTargetCd()))){
//                        //场-岸 岸-场
//                    }
                    if (!(("01".equals(po.getSourceCd()) || "03".equals(po.getSourceCd())) && ("01".equals(po.getTargetCd()) || "03".equals(po.getTargetCd())))) {
                        Long storehouseId = po.getStorehouseId();
                        String storehouseName = po.getStorehouseName();
                        Long locationId = po.getLocationId();
                        String locationNo = po.getLocationNo();
                        Long stackPositionId = po.getStackPositionId();
                        String stackPositionName = po.getStackPositionName();
                        tYardTallyPO.setIsCrk("1".equals(po.getSourceOrTargetFlag()) ? "201" : "2".equals(po.getSourceOrTargetFlag()) ? "101" : "301");//出库
//                        TYardTallyItemPO pos = tallyMapper.getStorage(po.getId());
//                        po.setStorehouseId(ObjectUtil.isNotEmpty(pos)?pos.getStorehouseTargetId():storehouseId); //库场
//                        po.setStorehouseName(ObjectUtil.isNotEmpty(pos)?pos.getStorehouseTargetName():storehouseName);
//                        po.setLocationId(ObjectUtil.isNotEmpty(pos)?pos.getLocationTargetId():locationId);
//                        po.setLocationNo(ObjectUtil.isNotEmpty(pos)?pos.getLocationTargetNo():locationNo);
//                        po.setStackPositionId(ObjectUtil.isNotEmpty(pos)?pos.getStackPositionTargetId():stackPositionId);
//                        po.setStackPositionName(ObjectUtil.isNotEmpty(pos)?pos.getStackPositionTargetName():stackPositionName);
                        insertIntOutYardNew(po, tYardTallyPO);
                    }
                }
            }
            //修改理货记录子表
            tallyMapper.updateTallyItem(po);
        }
        LOGGER.info("修改成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void tally(TYardTallyPO tYardTallyPO) {
        final String methodName = "tYardTallyPO";
        LOGGER.enter(methodName, "理货[start], tYardTallyPO: " + tYardTallyPO);
        long id = snowflake.nextId();
        tYardTallyPO.setId(id);
        //船-岸把关联ID赋值
        if (SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
            tYardTallyPO.setRelationId(id);
        } else {
            //是否待作业
            if (tYardTallyPO.getRelationId() != null) {
                //查询是否已经理货了
                Integer count = tallyMapper.getRelationIdHc(tYardTallyPO);
                if (count > 0) {
                    throw new BusinessRuntimeException("该数据已理货，不能重复理货");
                }
                //关联相关理货ID
                tallyMapper.updateRelationId(tYardTallyPO);
            }
        }

        //没有前置要把relationId置为空
        if (!tYardTallyPO.getIsFrontType()) {
            //指令 - 车牌号 - 作业过程 查询关联ID，理完才可继续理货
            Integer count = tallyMapper.getRelationIdQy(tYardTallyPO);
            if (count > 0) {
                throw new BusinessRuntimeException("该转运机械未作业完，请稍后再理货");
            }
            //            tYardTallyPO.setRelationId(null);
        }
        //新增主表信息
        int i = tallyMapper.tally(tYardTallyPO);
        if (i != 0) {
            SysParameterDTO sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE");
            boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
            List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
            for (TYardTallyItemPO po : listTallyList) {
                po.setId(snowflake.nextId());
                //主表ID
                po.setTallyId(id);
                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._03.getCode().equals(tYardTallyPO.getDestination())) {
                    //岸-车使用的剩余场存
                    if (po.getQuantityOut() != null) {
                        if (oneCarPlan) {
                            //判断下次理货时理货数量不能超过剩余件数数量
                            if (po.getQuantityOut() > po.getQuantitySurplus()) {
                                throw new BusinessRuntimeException("理货件数大于剩余件数!");
                            }
                        }

                        po.setQuantity(po.getQuantityOut());
                    } else {
                        po.setQuantity(0);
                    }
                    if (po.getTonOut() != null) {
                        if (oneCarPlan) {
                            if (po.getTonOut().compareTo(po.getTonSurplus()) > 0) {
                                throw new BusinessRuntimeException("理货重量大于剩余重量!");
                            }
                        }

                        po.setTon(po.getTonOut());
                    }
                }
                if (po.getCargoInfoId() == null) {
                    //货物信息
                    po.setCargoInfoId(tYardTallyPO.getCargoInfoId());
                    po.setCargoCode(tYardTallyPO.getCargoCode());
                    po.setCargoName(tYardTallyPO.getCargoName());//货物信息
                }
                //判断垛号是输入还是选择
                //处理一下垛号(是场地的)
//                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
//                    insertMiss(po);
//                }
                //场存节点是理货时，新增出入库记录(1.判断作业过程源或目的为场地的 2.货种节点为理货的 先1后2)
                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                    if (po.getCargoCode() != null) {
//                        Integer isTally = tallyMapper.getIsTally(po.getCargoCode());
//                        if (isTally != null && isTally == 1) {
                        insertIntOutYard(po, tYardTallyPO);
//                        }
                    }
                }

            }
            //添加子表信息
            tallyMapper.tallyItem(listTallyList);
            //附件保存
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            sysFileService.saveFileBusRelation(fileIds, id);
            // 标号处理
            if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
                saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, true);
            }

        } else {
            throw new BusinessRuntimeException("理货失败");
        }
        LOGGER.info("理货成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }

    public void saveCoilList(List<AppTallyCoilNumDTO> list, TYardTallyPO tYardTallyPO, boolean flag) {
        for (AppTallyCoilNumDTO dto : list) {
            if (flag) {
                if (Arrays.asList(SourceTargetTypeEnum._05.getCode(), SourceTargetTypeEnum._06.getCode()).stream().anyMatch(e -> e.equals(tYardTallyPO.getDestination()))
                        && dto.getStatus() == 20
                && !Arrays.asList(SourceTargetTypeEnum._05.getCode(), SourceTargetTypeEnum._06.getCode()).stream().anyMatch(e -> e.equals(tYardTallyPO.getSource()))
                ) {
                    throw new BusinessRuntimeException("卷钢号为" + dto.getCoilNum() + "的已经入库，请勿重新入库");
                } else if (Arrays.asList(SourceTargetTypeEnum._05.getCode(), SourceTargetTypeEnum._06.getCode()).stream().anyMatch(e -> e.equals(tYardTallyPO.getSource()))
                        && dto.getStatus() == 30) {
                    throw new BusinessRuntimeException("卷钢号为" + dto.getCoilNum() + "的已经出库，请勿重新出库");
                }
                //  船/车 ==> 场/岸  #### 入库
                if (
                        (SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._03.getCode().equals(tYardTallyPO.getSource()))
                                &&
                                (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination()) || SourceTargetTypeEnum._06.getCode().equals(tYardTallyPO.getDestination()))
                ) {
                    dto.setStatus(20);
                } else if (
                        (SourceTargetTypeEnum._06.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()))
                                &&
                                (SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getDestination()) || SourceTargetTypeEnum._03.getCode().equals(tYardTallyPO.getDestination()))
                ) {
                    dto.setStatus(30);// 岸/场 => 船/车  #### 出库
                }
            }
            dto.setTallyId(tYardTallyPO.getId());
        }
        tallyMapper.updateCoilList(list);
        //理货标号关系表
        tallyMapper.insertCoilList(list);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void tallyChang(TYardTallyPO tYardTallyPO) {
        final String methodName = "tYardTallyPO";
        LOGGER.enter(methodName, "理货[start], tYardTallyPO: " + tYardTallyPO);
        long id = snowflake.nextId();
        tYardTallyPO.setId(id);
        if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
            tYardTallyPO.setRelationId(id);
        }
        //是否待作业
        if (tYardTallyPO.getRelationId() != null) {
            //查询是否已经理货了
            Integer count = tallyMapper.getRelationIdHc(tYardTallyPO);
            if (count > 0) {
                throw new BusinessRuntimeException("该数据已理货，不能重复理货");
            }
            //关联相关理货ID
            tallyMapper.updateRelationId(tYardTallyPO);
        }
        //没有前置要把relationId置为空
        if (!tYardTallyPO.getIsFrontType()) {
            //指令 - 车牌号 - 作业过程 查询关联ID，理完才可继续理货
            Integer count = tallyMapper.getRelationIdQy(tYardTallyPO);
            if (count > 0) {
                throw new BusinessRuntimeException("该转运机械未作业完，请稍后再理货");
            }
            //tYardTallyPO.setRelationId(null);
        }
        //新增主表信息
        int i = tallyMapper.tally(tYardTallyPO);
        if (i != 0) {
            SysParameterDTO sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE");
            boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
            List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
            for (TYardTallyItemPO po : listTallyList) {
                po.setId(snowflake.nextId());
                //主表ID
                po.setTallyId(id);
                //判断垛号是输入还是选择
//                insertMiss(po);
                if (!tYardTallyPO.getIsFrontType()) {
                    //场-车
                    if (po.getQuantityOut() != null) {
                        if (oneCarPlan) {
                            //判断下次理货时理货数量不能超过剩余件数数量
                            if (po.getQuantityOut() > po.getQuantitySurplus()) {
                                throw new BusinessRuntimeException("理货件数大于剩余件数!");
                            }
                        }

                        po.setQuantity(po.getQuantityOut());
                    } else {
                        po.setQuantity(0);
                    }
                    if (po.getTonOut() != null) {
                        if (oneCarPlan) {
                            if (po.getTonOut().compareTo(po.getTonSurplus()) > 0) {
                                throw new BusinessRuntimeException("理货重量大于剩余重量!");
                            }
                        }
//
                        po.setTon(po.getTonOut());
                    }
                }
                //场存节点是理货时，新增出入库记录(1.判断作业过程源或目的为场地的 2.货种节点为理货的 先1后2)
                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                    if (po.getCargoCode() != null) {
//                        Integer isTally = tallyMapper.getIsTally(po.getCargoCode());
//                        if (isTally != null && isTally == 1) {
                        if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                            //场-岸 岸-场 先出 后入
                            tYardTallyPO.setIsCrk("201");
                            insertIntOutYard(po, tYardTallyPO);
                            tYardTallyPO.setIsCrk("101");
                            if (po.getStorehouseTargetId() == null) {
                                throw new BusinessRuntimeException("请选择目标场地");
                            }
                            po.setStorehouseId(po.getStorehouseTargetId()); //库场
                            po.setStorehouseName(po.getStorehouseTargetName());
                            po.setLocationId(po.getLocationTargetId());
                            po.setLocationNo(po.getLocationTargetNo());
                            po.setStackPositionId(po.getStackPositionTargetId());
                            po.setStackPositionName(po.getStackPositionTargetName());
                        }
                        insertIntOutYard(po, tYardTallyPO);
//                        }
                    }
                }
            }
            //添加子表信息
            tallyMapper.tallyItem(listTallyList);
            //附件保存
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            // 附件保存
            sysFileService.saveFileBusRelation(fileIds, id);
            if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
                saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, true);
            }
        } else {
            throw new BusinessRuntimeException("理货失败");
        }
        LOGGER.info("理货成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }

    /**
     * 新增垛号
     *
     * @param po
     */
//    public void insertMiss(TYardTallyItemPO po) {
//            //输入没有ID
//            Map<String, Object> map = new HashMap<>();
//            long stackPositionId = snowflake.nextId();
//            po.setStackPositionId(stackPositionId);
//            map.put("id", stackPositionId);
//            map.put("parentId", po.getLocationId());
//            map.put("storageYardNm", po.getStackPositionName());
//            map.put("shoreCd", po.getStackPositionName());
//            map.put("storageYardLevel", 3);
//            //存入垛位
//            tallyMapper.insertStackPosition(map);
//    }
    public void insertMiss(TYardTallyItemPO po) {
        //查询该场地下是否有该输入垛号名称
        List<Long> list = tallyMapper.getIsStackPosition(po);
        if (list != null && list.size() != 0) {
            po.setStackPositionId(list.get(0));
        } else {
            //输入没有ID
            Map<String, Object> map = new HashMap<>();
            long stackPositionId = snowflake.nextId();
            po.setStackPositionId(stackPositionId);
            map.put("id", stackPositionId);
            map.put("parentId", po.getLocationId());
            map.put("storageYardNm", po.getStackPositionName());
            map.put("shoreCd", po.getStackPositionName());
            map.put("storageYardLevel", 3);
            //存入垛位
            tallyMapper.insertStackPosition(map);
        }
    }

    public void getCargoIsCorrect(TYardTallyPO tYardTallyPO) {
        if (tYardTallyPO.getWeighbridgeId() != null) {
            //查询磅单号下的票货信息
            String cargoId = tallyMapper.getWeightCargo(tYardTallyPO.getWeighbridgeId());
            if (!String.valueOf(tYardTallyPO.getCargoInfoId()).equals(cargoId)) {
                throw new BusinessRuntimeException("理货失败，选择的票货与磅单票货信息不匹配");
            }
        }

    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void tallyCheChuan(TYardTallyPO tYardTallyPO) {
        final String methodName = "tYardTallyPO";
        LOGGER.enter(methodName, "理货[start], tYardTallyPO: " + tYardTallyPO);
        long id = snowflake.nextId();
        tYardTallyPO.setId(id);
        tYardTallyPO.setRelationId(id);
        getCargoIsCorrect(tYardTallyPO);
        //根据磅单ID获取磅单数据
        TYardTallyPO weightInfoPO = tallyMapper.getWeightInfo(tYardTallyPO);
        if (!ObjectUtils.isEmpty(weightInfoPO)) {
            tYardTallyPO.setTsptId(weightInfoPO.getTsptId());
            tYardTallyPO.setPlanNo(weightInfoPO.getPlanNo());
            tYardTallyPO.setWorkErWeiId(weightInfoPO.getWorkErWeiId());
        } else {
            if (tYardTallyPO.getWeighbridgeId() != null) {
                throw new BusinessRuntimeException("获取磅单信息失败,请刷新列表重试");
            }
        }
        //验重
        int count = tallyMapper.getIsBdTally(tYardTallyPO);
        if (count > 0) {
            throw new BusinessRuntimeException("该车辆已理货,不能重复理货");
        }
        //标号处理
        String result = "";
        if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
            saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, true);
            result = tYardTallyPO.getCoilList().stream()
                    .map(obj -> obj.getCoilNum()) // 将每个对象的属性拼接并用逗号分隔
                    .collect(Collectors.joining(","));
            tYardTallyPO.setRemark("标号:" + result);
        }
        //操作地磅表
        if (tYardTallyPO.getWeighbridgeId() != null) {
            SpringUtils.getBean(this.getClass()).updateWeightNew(tYardTallyPO);
        }
        //新增主表信息
        int i = tallyMapper.tally(tYardTallyPO);
        if (i != 0) {
            SysParameterDTO sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE");
            boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
            List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
            for (TYardTallyItemPO po : listTallyList) {
                po.setId(snowflake.nextId());
                //主表ID
                po.setTallyId(id);
                if ((SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) &&
                        SourceTargetTypeEnum._03.getCode().equals(tYardTallyPO.getDestination()) ||
                        SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) &&
                                SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getDestination()))) {
                    //岸-车 岸-船 使用的剩余场存
                    if (po.getQuantityOut() != null) {
                        if (oneCarPlan) {
                            //判断下次理货时理货数量不能超过剩余件数数量
                            if (po.getQuantityOut() > po.getQuantitySurplus()) {
                                throw new BusinessRuntimeException("理货件数大于剩余件数!");
                            }
                        }

                        po.setQuantity(po.getQuantityOut());
                    } else {
                        po.setQuantity(0);
                    }
                    if (po.getTonOut() != null) {
                        if (oneCarPlan) {
                            if (po.getTonOut().compareTo(po.getTonSurplus()) > 0) {
                                throw new BusinessRuntimeException("理货重量大于剩余重量!");
                            }
                        }
//
                        po.setTon(po.getTonOut());
                    }
                } else {
                    if (po.getQuantity() == null || po.getQuantity() == 0) {
                        throw new BusinessRuntimeException("请输入件数!");
                    }
                    if (po.getTon() == null || po.getTon().compareTo(BigDecimal.ZERO) == 0) {
                        throw new BusinessRuntimeException("请输入重量!");
                    }
                }
                if (po.getCargoInfoId() == null) {
                    //货物信息
                    po.setCargoInfoId(tYardTallyPO.getCargoInfoId());
                    po.setCargoCode(tYardTallyPO.getCargoCode());
                    po.setCargoName(tYardTallyPO.getCargoName());//货物信息
                }
                po.setTrustCargoInfoId(tYardTallyPO.getTrustCargoInfoId());
                //判断垛号是输入还是选择
                //处理一下垛号
//                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
//                    insertMiss(po);
//                }
                //场存节点是理货时，新增出入库记录(1.判断作业过程源或目的为场地的 2.货种节点为理货的 先1后2)
                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                    if (po.getCargoCode() != null) {
//                        Integer isTally = tallyMapper.getIsTally(po.getCargoCode());
//                        if (isTally != null && isTally == 1) {
                        insertIntOutYard(po, tYardTallyPO);
//                        }
                    }
                }
            }
            //添加子表信息
            tallyMapper.tallyItem(listTallyList);
            //附件
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            // 附件保存
            sysFileService.saveFileBusRelation(fileIds, id);
        } else {
            throw new BusinessRuntimeException("理货失败");
        }
        LOGGER.info("理货成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }

    @DS("simeauto")
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateWeight(TYardTallyPO tYardTallyPO) {
        tallyMapper.updateWeight(tYardTallyPO);
    }

    public void updateWeightNew(TYardTallyPO tYardTallyPO) {
        // 根据计划线id获取当前计划线的作业过程是否为直取
        if (tYardTallyPO.getPlanId() != null) {
            String isDirection = tallyMapper.getWorkPlanById(tYardTallyPO.getPlanId());
            if (StringUtils.isNotBlank(isDirection)) {
                tYardTallyPO.setIsDirection(Long.valueOf(isDirection));
            } else {
                LOGGER.error(tYardTallyPO.getPlanId() + "获取作业线是否为直取失败");
            }
        }
        int i = tallyMapper.updateWeightNew(tYardTallyPO);
        if (i == 0) {
            throw new BusinessRuntimeException("未找到该磅单信息,请刷新列表重试");
        }
    }

    @DS("simeauto")
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateWeightStatus(String id) {
        tallyMapper.updateWeightStatus(id);
    }

    @DS("simeauto")
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public String getWeightStatus(String id) {
        return tallyMapper.getWeightStatus(id);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void tallyChangChuan(TYardTallyPO tYardTallyPO) {
        final String methodName = "tYardTallyPO";
        LOGGER.enter(methodName, "理货[start], tYardTallyPO: " + tYardTallyPO);
        long id = snowflake.nextId();
        tYardTallyPO.setId(id);
        boolean AC = SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getDestination());
        boolean CA = SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination());
        if (AC || CA) {
            //岸-船 场-岸 没有关联ID
            tYardTallyPO.setRelationId(id);
        } else {
            //是否待作业
            if (tYardTallyPO.getRelationId() != null) {
                //查询是否已经理货了
                Integer count = tallyMapper.getRelationIdHc(tYardTallyPO);
                if (count > 0) {
                    throw new BusinessRuntimeException("该数据已理货，不能重复理货");
                }
                //关联相关理货ID
                tallyMapper.updateRelationId(tYardTallyPO);
            }
            //没有前置要把relationId置为空
            if (!tYardTallyPO.getIsFrontType()) {
                //指令 - 车牌号 - 作业过程 查询关联ID，理完才可继续理货
                Integer count = tallyMapper.getRelationIdQy(tYardTallyPO);
                if (count > 0) {
                    throw new BusinessRuntimeException("该转运机械未作业完，请稍后再理货");
                }
                //tYardTallyPO.setRelationId(null);
            }
        }


        //新增主表信息
        int i = tallyMapper.tally(tYardTallyPO);
        if (i != 0) {
            SysParameterDTO sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE");
            boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
            List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
            for (TYardTallyItemPO po : listTallyList) {
                po.setId(snowflake.nextId());
                //主表ID
                po.setTallyId(id);
                //没有默认件数和重量，赋值出库件数和重量
                if (!tYardTallyPO.getIsFrontType()) {
                    //场-车
                    if (po.getQuantityOut() != null) {
                        if (oneCarPlan) {
                            //判断下次理货时理货数量不能超过剩余件数数量
                            if (po.getQuantityOut() > po.getQuantitySurplus()) {
                                throw new BusinessRuntimeException("理货件数大于剩余件数!");
                            }
                        }

                        po.setQuantity(po.getQuantityOut());
                    } else {
                        po.setQuantity(0);
                    }
                    if (po.getTonOut() != null) {
                        if (oneCarPlan) {
                            if (po.getTonOut().compareTo(po.getTonSurplus()) > 0) {
                                throw new BusinessRuntimeException("理货重量大于剩余重量!");
                            }
                        }
//
                        po.setTon(po.getTonOut());
                    }
                }
                //场存节点是理货时，新增出入库记录(1.判断作业过程源或目的为场地的 2.货种节点为理货的 先1后2)
                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                    if (po.getCargoCode() != null) {
//                        Integer isTally = tallyMapper.getIsTally(po.getCargoCode());
//                        if (isTally != null && isTally == 1) {
                        if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                            //场-岸 岸-场 先出 后入
                            tYardTallyPO.setIsCrk("201");
                            insertIntOutYard(po, tYardTallyPO);
                            tYardTallyPO.setIsCrk("101");
                            if (po.getStorehouseTargetId() == null) {
                                throw new BusinessRuntimeException("请选择目标场地");
                            }
                            po.setStorehouseId(po.getStorehouseTargetId()); //库场
                            po.setStorehouseName(po.getStorehouseTargetName());
                            po.setLocationId(po.getLocationTargetId());
                            po.setLocationNo(po.getLocationTargetNo());
                            po.setStackPositionId(po.getStackPositionTargetId());
                            po.setStackPositionName(po.getStackPositionTargetName());
                        }
                        insertIntOutYard(po, tYardTallyPO);
//                        }
                    }
                }
            }
            //添加子表信息
            tallyMapper.tallyItem(listTallyList);

            //附件保存
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            sysFileService.saveFileBusRelation(fileIds, id);
            // 标号处理
            if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
                saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, false);
            }

        } else {
            throw new BusinessRuntimeException("理货失败");
        }
        LOGGER.info("理货成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void tallyChangChe(TYardTallyPO tYardTallyPO) {

        final String methodName = "tYardTallyPO";
        LOGGER.enter(methodName, "理货[start], tYardTallyPO: " + tYardTallyPO);
        long id = snowflake.nextId();
        tYardTallyPO.setId(id);
        tYardTallyPO.setRelationId(id);
        getCargoIsCorrect(tYardTallyPO);
        //根据磅单ID获取磅单数据
        TYardTallyPO weightInfoPO = tallyMapper.getWeightInfo(tYardTallyPO);
        if (!ObjectUtils.isEmpty(weightInfoPO)) {
            tYardTallyPO.setTsptId(weightInfoPO.getTsptId());
            tYardTallyPO.setPlanNo(weightInfoPO.getPlanNo());
            tYardTallyPO.setWorkErWeiId(weightInfoPO.getWorkErWeiId());
        } else {
            throw new BusinessRuntimeException("获取磅单信息失败,请刷新列表重试");
        }
        //验重
        int count = tallyMapper.getIsBdTally(tYardTallyPO);
        if (count > 0) {
            throw new BusinessRuntimeException("该车辆已理货,不能重复理货");
        }

        //新增主表信息
        int i = tallyMapper.tally(tYardTallyPO);
        if (i != 0) {
            SysParameterDTO sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE");
            boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
            List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
            for (TYardTallyItemPO po : listTallyList) {
                po.setId(snowflake.nextId());
                //主表ID
                po.setTallyId(id);
                po.setTrustCargoInfoId(tYardTallyPO.getTrustCargoInfoId());
                //判断垛号是输入还是选择
                //没有默认件数和重量，赋值出库件数和重量
                if (po.getQuantityOut() != null) {
                    if (oneCarPlan) {
                        //判断下次理货时理货数量不能超过剩余件数数量
                        if (po.getQuantityOut() > po.getQuantitySurplus()) {
                            throw new BusinessRuntimeException("理货件数大于剩余件数!");
                        }
                    }
                    po.setQuantity(po.getQuantityOut());
                } else {
                    po.setQuantity(0);
                }
                if (po.getTonOut() != null) {
                    if (oneCarPlan) {
                        if (po.getTonOut().compareTo(po.getTonSurplus()) > 0) {
                            throw new BusinessRuntimeException("理货重量大于剩余重量!");
                        }
                    }

                    po.setTon(po.getTonOut());
                }
                //场存节点是理货时，新增出入库记录(1.判断作业过程源或目的为场地的 2.货种节点为理货的 先1后2)
                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                    if (po.getCargoCode() != null) {
//                        Integer isTally = tallyMapper.getIsTally(po.getCargoCode());
//                        if (isTally != null && isTally == 1) {
                        insertIntOutYard(po, tYardTallyPO);
//                        }
                    }
                }
            }
            //添加子表信息
            tallyMapper.tallyItem(listTallyList);
            //附件保存
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            // 附件保存
            sysFileService.saveFileBusRelation(fileIds, id);
            //操作地磅表
            if (tYardTallyPO.getWeighbridgeId() != null) {

                SpringUtils.getBean(this.getClass()).updateWeightNew(tYardTallyPO);
            }
            String result = "";
            if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
                saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, true);
                result = tYardTallyPO.getCoilList().stream()
                        .map(obj -> obj.getCoilNum()) // 将每个对象的属性拼接并用逗号分隔
                        .collect(Collectors.joining(","));
                //tYardTallyPO.setRemark("件数:" + tYardTallyPO.getQuantity() + "件;吨数:" + tYardTallyPO.getTon() + "吨;标号:" + result);
                tYardTallyPO.setRemark("标号:" + result);
            }
            SpringUtils.getBean(this.getClass()).updateWeightNew(tYardTallyPO);

        } else {
            throw new BusinessRuntimeException("理货失败");
        }
        LOGGER.info("理货成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }

    @Override
    public Map<String, Object> getWorkProgress(Long trustId, String workDate, String classCode, String cargoInfoNo) {
        final String methodName = "TallyServiceImpl:getWorkProgress";
        LOGGER.enter(methodName, "业务执行");

        Map<String, Object> resultMap = Maps.newHashMap();

        // 查询用于统计的作业过程
        List<MWorkProcessPO> workProcessList = tallyMapper.getMWorkProcessList();

        // 查询作业通知单计划量
        Map<String, Object> trustPlanMap = tallyMapper.getTrustPlan(trustId);
        resultMap.put("trustPlan", trustPlanMap);

        // 查询票货信息
        List<Map<String, Object>> cargoInfoList = tallyMapper.getCargoInfoList(trustId, cargoInfoNo);
        for (Map<String, Object> cargoInfo : cargoInfoList) {
            Long cargoInfoId = Long.parseLong(cargoInfo.get("cargoInfoId").toString());

            // 查询集、疏港
            Map<String, Object> inOutPortWorkData = tallyMapper.getProgressWorkData(cargoInfoId,
                    workProcessList.stream()
                            .filter((MWorkProcessPO po) -> "10".equals(po.getTallyDataStat()))
                            .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), null, null);
            if (inOutPortWorkData != null) {
                cargoInfo.put("inOutPortQuantity", inOutPortWorkData.get("quantity"));
                cargoInfo.put("inOutPortTon", inOutPortWorkData.get("ton"));
                cargoInfo.put("inOutPortCars", inOutPortWorkData.get("cars"));

                // 剩余量
                cargoInfo.put("inOutPortQuantity_", getResidueCount(cargoInfo.get("cargoQuantity"), inOutPortWorkData.get("quantity"), null));
                cargoInfo.put("inOutPortTon_", getResidueCount(cargoInfo.get("cargoTon"), inOutPortWorkData.get("ton"), null));
            } else {
                inOutPortWorkData = new HashMap<>();
            }

            // 查询直取装、卸船
            Map<String, Object> zqWorkData = tallyMapper.getProgressWorkData(cargoInfoId,
                    workProcessList.stream()
                            .filter((MWorkProcessPO po) -> "20".equals(po.getTallyDataStat()))
                            .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), null, null);
            if (zqWorkData != null) {
                cargoInfo.put("zqQuantity", zqWorkData.get("quantity"));
                cargoInfo.put("zqTon", zqWorkData.get("ton"));
                cargoInfo.put("zqCars", zqWorkData.get("cars"));
            } else {
                zqWorkData = new HashMap<>();
            }

            // 查询倒运装、卸船
            Map<String, Object> dyWorkData = tallyMapper.getProgressWorkData(cargoInfoId,
                    workProcessList.stream()
                            .filter((MWorkProcessPO po) -> "30".equals(po.getTallyDataStat()))
                            .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), null, null);
            if (dyWorkData != null) {
                cargoInfo.put("dyQuantity", dyWorkData.get("quantity"));
                cargoInfo.put("dyTon", dyWorkData.get("ton"));
                cargoInfo.put("dyCars", dyWorkData.get("cars"));

                // 装船--剩余量
                cargoInfo.put("dyQuantity_", getResidueCount(cargoInfo.get("cargoQuantity"), null, dyWorkData.get("quantity")));
                cargoInfo.put("dyTon_", getResidueCount(cargoInfo.get("cargoTon"), null, dyWorkData.get("ton")));
            } else {
                dyWorkData = new HashMap<>();
            }

            // 查询落地装船
            Map<String, Object> luoWorkData = tallyMapper.getProgressWorkData(cargoInfoId,
                    workProcessList.stream()
                            .filter((MWorkProcessPO po) -> "40".equals(po.getTallyDataStat()))
                            .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), null, null);
            if (luoWorkData != null) {
                cargoInfo.put("luoQuantity", luoWorkData.get("quantity"));
                cargoInfo.put("luoTon", luoWorkData.get("ton"));
                cargoInfo.put("luoCars", luoWorkData.get("cars"));
            } else {
                luoWorkData = new HashMap<>();
            }

            // 查询车-岸前沿落地集港
            Map<String, Object> caWorkData = tallyMapper.getProgressWorkData(cargoInfoId,
                    workProcessList.stream()
                            .filter((MWorkProcessPO po) -> "50".equals(po.getTallyDataStat()))
                            .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), null, null);
            if (caWorkData != null) {
                cargoInfo.put("caQuantity", caWorkData.get("quantity"));
                cargoInfo.put("caTon", caWorkData.get("ton"));
                cargoInfo.put("caCars", caWorkData.get("cars"));
            } else {
                caWorkData = new HashMap<>();
            }

            if (StringUtils.isNotBlank(workDate) && StringUtils.isNotBlank(classCode)) {
                // 查询本班作业量-集、疏港
                Map<String, Object> inOutPortWorkTimeData = tallyMapper.getProgressWorkData(cargoInfoId,
                        workProcessList.stream()
                                .filter((MWorkProcessPO po) -> "10".equals(po.getTallyDataStat()))
                                .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), workDate, classCode);
                if (inOutPortWorkTimeData != null) {
                    cargoInfo.put("workTimeInOutPortQuantity", inOutPortWorkTimeData.get("quantity"));
                    cargoInfo.put("workTimeInOutPortTon", inOutPortWorkTimeData.get("ton"));
                    cargoInfo.put("workTimeInOutPortCars", inOutPortWorkTimeData.get("cars"));
                }

                // 查询本班作业量-直取装、卸船
                Map<String, Object> zqWorkTimeData = tallyMapper.getProgressWorkData(cargoInfoId,
                        workProcessList.stream()
                                .filter((MWorkProcessPO po) -> "20".equals(po.getTallyDataStat()))
                                .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), workDate, classCode);
                if (zqWorkTimeData != null) {
                    cargoInfo.put("workTimeZqQuantity", zqWorkTimeData.get("quantity"));
                    cargoInfo.put("workTimeZqTon", zqWorkTimeData.get("ton"));
                    cargoInfo.put("workTimeZqCars", zqWorkTimeData.get("cars"));
                }

                // 查询本班作业量-倒运装、卸船
                Map<String, Object> dyWorkTimeData = tallyMapper.getProgressWorkData(cargoInfoId,
                        workProcessList.stream()
                                .filter((MWorkProcessPO po) -> "30".equals(po.getTallyDataStat()))
                                .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), workDate, classCode);
                if (dyWorkTimeData != null) {
                    cargoInfo.put("workTimeDyQuantity", dyWorkTimeData.get("quantity"));
                    cargoInfo.put("workTimeDyTon", dyWorkTimeData.get("ton"));
                    cargoInfo.put("workTimeDyCars", dyWorkTimeData.get("cars"));
                }

                // 查询本班作业量-落地装船
                Map<String, Object> luoWorkTimeData = tallyMapper.getProgressWorkData(cargoInfoId,
                        workProcessList.stream()
                                .filter((MWorkProcessPO po) -> "40".equals(po.getTallyDataStat()))
                                .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), workDate, classCode);
                if (luoWorkTimeData != null) {
                    cargoInfo.put("luoWorkTimeDyQuantity", luoWorkTimeData.get("quantity"));
                    cargoInfo.put("luoWorkTimeDyTon", luoWorkTimeData.get("ton"));
                    cargoInfo.put("luoWorkTimeDyCars", luoWorkTimeData.get("cars"));
                }
            }
        }

        resultMap.put("cargoInfoList", cargoInfoList);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return resultMap;
    }

    public Map<String, Object> getWorkProgressNew(Long trustId, String workDate, String classCode, String cargoInfoNo, Long workPlanId) {
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取计划件数：一票货一计划
        Map<String, Object> trustPlanMap = tallyMapper.getTrustPlan(trustId);
        resultMap.put("trustPlan", trustPlanMap);
        List<Object> resultList = Lists.newArrayList();
        resultMap.put("cargoInfoList", resultList);
        List<Map<String, Object>> cabinNoList = tallyMapper.getCabinNoList(trustId);
        resultMap.put("cabinNoList", cabinNoList);
        Map<String, Object> tallyNow = tallyMapper.getTallyByPlanId(workPlanId);
        resultMap.put("tallyNow", tallyNow);
        //获取票货信息
        List<Map<String, Object>> cargoInfoList = tallyMapper.getCargoInfoListNew(trustId, cargoInfoNo);
        Map<String, List<Map<String, Object>>> carInfoMap = cargoInfoList.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.get("trustId")) + String.valueOf(e.get("cargoInfoNo"))));
//        List<Map<String,Object>> workList = tallyMapper.getWorkInfoByCargoInfoNo(trustId);
//        Map<String,List<Map<String,Object>>> workInfoMap = workList.stream().collect(Collectors.groupingBy(e->String.valueOf(e.get("trustId")) + String.valueOf(e.get("cargoInfoNo"))));
        carInfoMap.forEach((k, v) -> {
            if (!CollectionUtils.isEmpty(v)) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("cargoInfo", v.get(0));
                map.put("processList", tallyMapper.getWorkInfoByCargoInfoNo(String.valueOf(v.get(0).get("cargoInfoNo"))));
                resultList.add(map);
            }
        });
        return resultMap;
    }

    /**
     * 计算
     *
     * @param count
     * @param param1
     * @param param2
     * @return
     */
    private BigDecimal getResidueCount(Object count, Object param1, Object param2) {
        BigDecimal count_b = ObjectUtils.isNotEmpty(count) ? new BigDecimal(count.toString()) : BigDecimal.ZERO;
        BigDecimal param1_b = ObjectUtils.isNotEmpty(param1) ? new BigDecimal(param1.toString()) : BigDecimal.ZERO;
        BigDecimal param2_b = ObjectUtils.isNotEmpty(param2) ? new BigDecimal(param2.toString()) : BigDecimal.ZERO;

        if (ObjectUtils.isEmpty(count)) {
            return BigDecimal.ZERO;
        }

        return count_b.subtract(param1_b).subtract(param2_b).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Map<String, Object> getTransportEquipmentCargoInfo(Long trustId, String vehicleNo) {
        final String methodName = "TallyServiceImpl:getTransportEquipmentCargoInfo";
        LOGGER.enter(methodName, "业务执行");

        Map<String, Object> resultMap = tallyMapper.getTransportEquipmentCargoInfo(trustId, vehicleNo);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return resultMap;
    }

    @Override
    public Map<String, Object> getTransportEquipmentCargoInfoZ(Long trustId, String vehicleNo) {
        final String methodName = "TallyServiceImpl:getTransportEquipmentCargoInfoZ";
        LOGGER.enter(methodName, "业务执行");
        Map<String, Object> resultMap = tallyMapper.getTransportEquipmentCargoInfoZ(trustId, vehicleNo);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return resultMap;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void departureSub(DepartureDTO departureDTO) {
        final String methodName = "TallyServiceImpl:departureSub";
        LOGGER.enter(methodName, "业务执行");
//        if (departureDTO.getWorkErweiId() != null) {
        //新地磅
        //根据磅单ID查询理货表是否有数据 有则删除理货表 理货子表 删除港存
        List<Long> idList = tallyMapper.getNoteTally(departureDTO.getNoteId());
        if (idList != null && idList.size() != 0) {
            for (Long id : idList) {
                TYardTallyPO po = new TYardTallyPO();
                po.setId(id);
                //删除理货记录主表
                int i = tallyMapper.deleteNotes(po);
                if (i != 0) {
                    //删除理货记录(子表)
                    tallyMapper.deleteNotesItem(id);
                    //根据理货ID获取港存明细ID
                    List<Long> ids = tallyMapper.getPortStorageDetailId(id);
                    if (ids != null && ids.size() != 0) {
                        //删除港存
                        businessCommonService.deletePortStorageDetail(ids);
                    }
                }
            }
        }
        //新增一条理货记录
        TYardTallyPO pos = new TYardTallyPO();
        Long newId = snowflake.nextId();
        pos.setId(newId);
        pos.setPlanId(newId);
        pos.setProcessCode("1");
        pos.setProcessName("1");
        pos.setTallyStatus("9");
        pos.setPlanNo(departureDTO.getPlanNo());
        pos.setTsptId(departureDTO.getTsptId());
        pos.setWorkErWeiId(departureDTO.getWorkErweiId());
        pos.setTransportEquipmentNo(departureDTO.getTruckPlate());
        pos.setLoginUserId(securityUtils.getLoginUserId());
        pos.setLoginUserName(securityUtils.getLoginUserName());
        pos.setNow(new Date());
        pos.setWeighbridgeId(departureDTO.getNoteId());
        tallyMapper.tallyDepartureSub(pos);
        TYardTallyItemPO tYardTallyItemPO = new TYardTallyItemPO();
        tYardTallyItemPO.setId(snowflake.nextId());
        tYardTallyItemPO.setTallyId(newId);
        //设置票货信息
        List<Map<String, Object>> map = tallyMapper.getCargoDeparture(departureDTO.getPlanNo());
        if (map != null && map.size() != 0) {
            if (map.get(0).get("id") != null) {
                tYardTallyItemPO.setCargoInfoId(Long.parseLong(map.get(0).get("id").toString()));
            }
            if (map.get(0).get("cargoCode") != null) {
                tYardTallyItemPO.setCargoCode(map.get(0).get("cargoCode").toString());
            }
            if (map.get(0).get("cargoName") != null) {
                tYardTallyItemPO.setCargoName(map.get(0).get("cargoName").toString());
            }
        }
        List<TYardTallyItemPO> list = new ArrayList();
        list.add(tYardTallyItemPO);
        tallyMapper.tallyItem(list);
        //更新磅单理货状态
        tallyMapper.updateDeparture(departureDTO.getNoteId());
//        } else {
//            SpringUtils.getBean(this.getClass()).operateWeight(departureDTO);
//        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void departureRevoke(DepartureDTO departureDTO) {
        final String methodName = "TallyServiceImpl:departureRevoke";
        LOGGER.enter(methodName, "业务执行");
        //删除理货记录
        TYardTallyPO tYardTallyPO = new TYardTallyPO();
        tYardTallyPO.setId(departureDTO.getTallyId());
        tYardTallyPO.setWeighbridgeId(departureDTO.getNoteId());
        tallyMapper.deleteNotes(tYardTallyPO);
        int i = tallyMapper.deleteNotesItem(tYardTallyPO.getId());
        if (i != 0) {
            //根据磅单ID查询磅单状态
            TYardTallyPO weightInfo = tallyMapper.getWeightInfo(tYardTallyPO);
            if (weightInfo == null) {
                throw new BusinessRuntimeException("查询磅单信息失败");
            } else {
                if ("1".equals(weightInfo.getIsFinished())) {
                    throw new BusinessRuntimeException("该车辆已出港，不允许撤销");
                }
                tallyMapper.updateWeightRevoke(tYardTallyPO);
            }
        } else {
            throw new BusinessRuntimeException("删除理货记录失败");
        }
    }


    @DS("simeauto")
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void operateWeight(DepartureDTO departureDTO) {
        //老地磅
        tallyMapper.updateWeightInfo(departureDTO);
        tallyMapper.insertEmptyTruck(departureDTO);
    }

    @Override
    public List<Map<String, Object>> listDisShipVoyageApp() {
        return tallyMapper.listDisShipVoyageApp();
    }

    @Override
    public List<AppTallyCoilNumDTO> lookCoil(Long id) {
        return tallyMapper.getCoilTallyList(id);
    }


    @Override
    public List<Map<String, Object>> getCarFerNew(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO) {
        //判断作业过程
        Map<String, Object> process = tallyMapper.getIsZq(tPrdDispatchSecondarySearchDTO.getProcessCode());
        //指令票货ID
        String planCode = "";
        if ("1".equals(process.get("isDirectAccess").toString())) {
            //直取
            List<String> type;
            //源是船的为船-车
            if (SourceTargetTypeEnum._01.getCode().equals(process.get("sourceCd").toString())) {
                type = Collections.singletonList("疏港");
                tPrdDispatchSecondarySearchDTO.setType(type);
                planCode = tallyMapper.getPlanCodeZ(tPrdDispatchSecondarySearchDTO);
            } else if (SourceTargetTypeEnum._05.getCode().equals(process.get("sourceCd").toString())) {
                //场-车 (陆集陆疏)
                type = Collections.singletonList("陆销");
                tPrdDispatchSecondarySearchDTO.setType(type);
                planCode = tallyMapper.getPlanCodeZ(tPrdDispatchSecondarySearchDTO);
            } else if (SourceTargetTypeEnum._06.getCode().equals(process.get("sourceCd").toString()) && SourceTargetTypeEnum._03.getCode().equals(process.get("targetCd").toString())) {
                type = Collections.singletonList("疏港");
                tPrdDispatchSecondarySearchDTO.setType(type);
                planCode = tallyMapper.getPlanNoNew(tPrdDispatchSecondarySearchDTO);
            }
//            else if (SourceTargetTypeEnum._03.getCode().equals(process.get("sourceCd").toString()) && SourceTargetTypeEnum._06.getCode().equals(process.get("targetCd").toString())) {
//                type = Collections.singletonList("集港");
//                tPrdDispatchSecondarySearchDTO.setType(type);
//                planCode = tallyMapper.getPlanNoNew(tPrdDispatchSecondarySearchDTO);
//            }
            else {
                //目的是船是车-船
                type = Stream.of("集港", "拆箱集港").collect(Collectors.toList());
                tPrdDispatchSecondarySearchDTO.setType(type);
                planCode = tallyMapper.getPlanCodeZ(tPrdDispatchSecondarySearchDTO);
            }

        } else {
            //根据指令ID查询指令票货ID
            planCode = tallyMapper.getPlanCode(tPrdDispatchSecondarySearchDTO);
        }
        if (planCode != null && (!"".equals(planCode))) {
            String ids = "(" + planCode + ")";
            //新地磅车号
            List<Map<String, Object>> list = tallyMapper.getCarFerNew(ids);
            return list;
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public List<AppTallyLadingDTO> getCarDetailedListNew(TYardMeasureSearchDTO tYardMeasureSearchDTO) {
        final String methodName = "getCarDetailedList";
        LOGGER.enter(methodName, "查询出入库数据[start], getCarDetailedList: " + tYardMeasureSearchDTO);
        //根据指令查票货
        String str = tallyMapper.getTrustCargoId(tYardMeasureSearchDTO.getTrustId());
        if (str == null) {
            throw new BusinessRuntimeException("查询失败，未查询到票货信息");
        }
        str = "(" + str + ")";
        tYardMeasureSearchDTO.setTrustIds(str);

        SysParameterDTO sysParameter = sysParameterMapper.getByKey("TALLY_STORAGE_IS_FRONTIER");
        boolean switchIsFrontier = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
        if (switchIsFrontier) {
            tYardMeasureSearchDTO.setSwitchIsFrontier("1");
            //校验根据作业过程进行校验
            String isFrontier = tallyMapper.getProcessIsFrontier(tYardMeasureSearchDTO.getProcessCode());
            if (StringUtils.isNotEmpty(isFrontier)) {
                tYardMeasureSearchDTO.setIsFrontier(isFrontier);
            }
        } else {
            tYardMeasureSearchDTO.setSwitchIsFrontier(null);
        }
        List<AppTallyLadingDTO> carDetailedList = tallyMapper.getPortStorgeByPlan(tYardMeasureSearchDTO);
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return carDetailedList;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void deleteNotesNew(TYardTallyPO tYardTallyPO) {
        if (tYardTallyPO.getTallyId() == null) {
            throw new BusinessRuntimeException("错误的理货ID");
        }
        hqDataService.deleteByTallyId(Long.valueOf(tYardTallyPO.getTallyId()));
        SysParameterDTO sysParameter = sysParameterMapper.getByKey("TICKET_TALLY_FLAG");
        boolean flag = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
        Integer count = tallyMapper.getTicketById(Long.valueOf(tYardTallyPO.getTallyId()));
        if (flag && count != null && count > 0) {
            throw new BusinessRuntimeException("已签票，不允许删除理货记录!");
        }
        //主表ID
        Long tallyId = Long.parseLong(tYardTallyPO.getTallyId());
        TallyRecordSearchDTO tallyRecordSearchDTO = new TallyRecordSearchDTO();
        tallyRecordSearchDTO.setTallyId(tallyId);
        //获取理货记录详情信息
        TYardTallyPO tallyInfoById = tallyMapper.getTallyInfoById(tallyRecordSearchDTO);
        //根据理货记录子作业过程查询主作业过程的源和目的
        Map<String, Object> process = tallyMapper.getProcess(tallyInfoById.getProcessCode());
        //源:船 目的:场/岸 & 源:场/岸 目的:船 & 源:场/岸 目的: 场/岸
        boolean flag1 = SourceTargetTypeEnum._01.getCode().equals(process.get("source")) && SourceTargetTypeEnum._05.getCode().equals(process.get("destination"));
        boolean flag2 = SourceTargetTypeEnum._05.getCode().equals(process.get("source")) && SourceTargetTypeEnum._01.getCode().equals(process.get("destination")) && (!"10250005".equals(tallyInfoById.getProcessCode())) && (!"10250006".equals(tallyInfoById.getProcessCode()));
        ;
        boolean flag3 = SourceTargetTypeEnum._05.getCode().equals(process.get("source")) && SourceTargetTypeEnum._05.getCode().equals(process.get("destination"));
        if (flag1 || flag2 || flag3 && (!"10210004".equals(tallyInfoById.getProcessCode())) && (!"10200005".equals(tallyInfoById.getProcessCode()))) {
            //装卸船倒运(有关联记录)
            //查询该理货记录的子作业过程是否有前置环节 有的话为后一环节，可直接删除
            Integer isPre = tallyMapper.getIsPre(tallyInfoById.getProcessCode());
            if (isPre == null) {
                throw new BusinessRuntimeException("获取理货记录作业过程前置环节失败");
            }
            //有前置环节，可直接删除
            if (isPre == 1) {
                delete(tallyId);
            } else {
                //没有前置环节的，则为前一环节,不可直接删除,后一环节未理货时才可删除，即关联ID为空
                TYardTallyPO po = new TYardTallyPO();
                po.setId(tallyId);
                tallyMapper.deleteNotes(po);
                //删除理货记录(子表)
                tallyMapper.deleteNotesItem(tallyId);
                //根据理货ID获取港存明细ID
                List<Long> ids = tallyMapper.getPortStorageDetailId(tallyId);
                if (ids != null && ids.size() != 0) {
                    //删除港存
                    businessCommonService.deletePortStorageDetail(ids);
                }
            }
        } else {
            deleteNoReshipment(tallyId, tallyInfoById);
        }
        //查询钢号信息
        List<AppTallyCoilNumDTO> numDTOList = tallyMapper.getCoilNumIdList(tYardTallyPO.getTallyId());
        if (numDTOList != null && numDTOList.size() != 0) {
            Map<String, Object> mapperProcess = tallyMapper.getProcessDetail(tallyInfoById.getProcessCode());
            String coilMaxStatus = tallyMapper.getCoilMaxStatus(tYardTallyPO.getTallyId());
            if (SourceTargetTypeEnum._01.getCode().equals(mapperProcess.get("source")) && (SourceTargetTypeEnum._06.getCode().equals(mapperProcess.get("destination")) || SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("destination"))  )) {
                //船-岸 状态恢复成10
                //有钢号信息。进行处理
                if("30".equals(coilMaxStatus)){
                    throw new BusinessRuntimeException("卷钢已经出库禁止删除入库理货记录");
                }
                tallyMapper.updateCoilNum(numDTOList, "10");
            } else if (
                    (SourceTargetTypeEnum._03.getCode().equals(mapperProcess.get("source")))
                            &&
                            (SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("destination")) || SourceTargetTypeEnum._06.getCode().equals(mapperProcess.get("destination")))
            ) {
                //车-场 等入库 删除 恢复成出库状态 30
                //有钢号信息。进行处理
                tallyMapper.updateCoilNum(numDTOList, "30");
            } else if (
                    (SourceTargetTypeEnum._05.getCode().equals(mapperProcess.get("source")) || SourceTargetTypeEnum._06.getCode().equals(mapperProcess.get("source")))
                            &&
                            (SourceTargetTypeEnum._03.getCode().equals(mapperProcess.get("destination")) || SourceTargetTypeEnum._01.getCode().equals(mapperProcess.get("destination")))
            ) {
                //岸-车，场-车，岸-船，场-船，,恢复成入库状态 20
                tallyMapper.updateCoilNum(numDTOList, "20");
            }
            //删除理货钢号关系表
            tallyMapper.deleteCoilNum(tYardTallyPO.getTallyId());
        }
    }

    /**
     * 理货时校验签票
     *
     * @param workPlanId
     */
    public void judgmentTicket(Long workPlanId) {
        SysParameterDTO sysParameter = sysParameterMapper.getByKey("TALLY_CHECK_TICKET");
        boolean flag = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
        if (flag && workPlanId != null) {
            Integer count = workTicketMapper.getWorkTicketCount(workPlanId);
            if (count > 0) {
                throw new BusinessRuntimeException("当班次已签票禁止继续理货，请撤销当班次签票后再理货");
            }
        }
    }

    /**
     * 是否作废
     *
     * @param cargoInfoId
     */
    public void isLogout(Long cargoInfoId) {
        if (null != cargoInfoId) {
            String isLogout = tallyMapper.getIsLogout(cargoInfoId);
            if ("10".equals(isLogout)) {
                throw new BusinessRuntimeException("票货已作废无法理货");
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void tallyNew(TYardTallyPO tYardTallyPO) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.TALLY_KEY.getCode() + tYardTallyPO.getWeighbridgeId())
                .build().run(() -> {
                    final String methodName = "tYardTallyPO";
                    LOGGER.enter(methodName, "理货[start], tYardTallyPO: " + tYardTallyPO);
                    isLogout(tYardTallyPO.getCargoInfoId());
                    judgmentTicket(tYardTallyPO.getPlanId());
                    long id = snowflake.nextId();
                    tYardTallyPO.setId(id);
                    //船-岸把关联ID赋值
                    if (SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                        tYardTallyPO.setRelationId(id);
                    }
                    //岸/场
                    else if (SourceTargetTypeEnum._06.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                        tYardTallyPO.setRelationId(id);
                    }
                    //船/场
                    else if (SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                        tYardTallyPO.setRelationId(id);
                    }
                    //场/船
                    else if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getDestination())) {
                        tYardTallyPO.setRelationId(id);
                    }
                    //船/场
                    else if (SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                        tYardTallyPO.setRelationId(id);
                    } else if ((SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._06.getCode().equals(tYardTallyPO.getSource()))
                            && (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination()) || SourceTargetTypeEnum._06.getCode().equals(tYardTallyPO.getDestination()))) {
                        tYardTallyPO.setRelationId(id);//倒运
                    } else {
                        //是否待作业
                        if (tYardTallyPO.getRelationId() != null) {
                            //查询是否已经理货了
                            Integer count = tallyMapper.getRelationIdHc(tYardTallyPO);
                            if (count > 0) {
                                throw new BusinessRuntimeException("该数据已理货，不能重复理货");
                            }
                            //关联相关理货ID
                            tallyMapper.updateRelationId(tYardTallyPO);
                        }
                    }
                    //没有前置要把relationId置为空
                    if (!tYardTallyPO.getIsFrontType()) {
                        //指令 - 车牌号 - 作业过程 查询关联ID，理完才可继续理货
                        Integer count = tallyMapper.getRelationIdQy(tYardTallyPO);
                        if (count > 0) {
                            throw new BusinessRuntimeException("该转运机械未作业完，请稍后再理货");
                        }
                    }
                    //新增主表信息
                    int i = tallyMapper.tally(tYardTallyPO);
                    if (i != 0) {
                        List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
                        for (TYardTallyItemPO po : listTallyList) {
                            isLogout(po.getCargoInfoId());
                            po.setCargoName(newProcessTemp(po));
                            //判断港存
                            judgmentPortStorage(po, tYardTallyPO);
                            po.setId(snowflake.nextId());
                            //主表ID
                            po.setTallyId(id);
                            if (po.getCargoInfoId() == null) {
                                //货物信息
                                po.setCargoInfoId(tYardTallyPO.getCargoInfoId());
                                po.setCargoCode(tYardTallyPO.getCargoCode());
                                po.setCargoName(tYardTallyPO.getCargoName());//货物信息
                            }
                            //判断垛号是输入还是选择
                            //处理一下垛号(是场地的)
                            //判断源和目的，是源且是岸或者场地的，是目标且岸或者场地的，需要变更场存
                            if ((po.getSourceOrTargetFlag().equals("1") && SourceTargetTypeEnum._05.getCode().equals(po.getSource()))
                                    || (po.getSourceOrTargetFlag().equals("1") && SourceTargetTypeEnum._06.getCode().equals(po.getSource()))
                                    || (po.getSourceOrTargetFlag().equals("2") && SourceTargetTypeEnum._05.getCode().equals(po.getDestination()))
                                    || (po.getSourceOrTargetFlag().equals("2") && SourceTargetTypeEnum._06.getCode().equals(po.getDestination()))) {
                                if (po.getCargoCode() != null) {
                                    insertIntOutYardNew(po, tYardTallyPO);
                                }
                            }
                        }
                        //添加子表信息
                        tallyMapper.tallyItem(listTallyList);
                        //附件保存
                        List<Long> fileIds = new ArrayList<>();
                        for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                            fileIds.add(file.getId());
                        }
                        sysFileService.saveFileBusRelation(fileIds, id);
                        // 标号处理
                        if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
                            saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, true);
                        }

                    } else {
                        throw new BusinessRuntimeException("理货失败");
                    }
                    LOGGER.info("理货成功");
                    LOGGER.exit(methodName, "业务数据同步服务[end]");
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void zzjTally(TYardTallyPO tYardTallyPO) {
        final String methodName = "tYardTallyPO";
        LOGGER.enter(methodName, "理货[start], tYardTallyPO: " + tYardTallyPO);
        judgmentTicket(tYardTallyPO.getPlanId());//签票
        long id = snowflake.nextId();
        tYardTallyPO.setId(id);
        tYardTallyPO.setRelationId(id);
        if (tYardTallyPO.getRelationId() != null) {
            //查询是否已经理货了
            Integer count = tallyMapper.getRelationIdHc(tYardTallyPO);
            if (count > 0) {
                throw new BusinessRuntimeException("该数据已理货，不能重复理货");
            }
            //关联相关理货ID
            tallyMapper.updateRelationId(tYardTallyPO);
        }
        //没有前置要把relationId置为空
//        if (!tYardTallyPO.getIsFrontType()) {
//            //指令 - 车牌号 - 作业过程 查询关联ID，理完才可继续理货
//            Integer count = tallyMapper.getRelationIdQy(tYardTallyPO);
//            if (count > 0) {
//                throw new BusinessRuntimeException("该转运机械未作业完，请稍后再理货");
//            }
//        }
        //新增主表信息
        int i = tallyMapper.tally(tYardTallyPO);
        if (i != 0) {
            List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
            for (TYardTallyItemPO po : listTallyList) {
                po.setCargoName(newProcessTemp(po));
                //判断港存
                judgmentPortStorage(po, tYardTallyPO);
                po.setId(snowflake.nextId());
                //主表ID
                po.setTallyId(id);
                if (po.getCargoInfoId() == null) {
                    //货物信息
                    po.setCargoInfoId(tYardTallyPO.getCargoInfoId());
                    po.setCargoCode(tYardTallyPO.getCargoCode());
                    po.setCargoName(tYardTallyPO.getCargoName());//货物信息
                }
                //判断垛号是输入还是选择
                //处理一下垛号(是场地的)
//                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
//                    insertMiss(po);
//                }
                //判断源和目的，是源且是岸或者场地的，是目标且岸或者场地的，需要变更场存
                List<String> codes = Arrays.asList(SourceTargetTypeEnum._05.getCode(), SourceTargetTypeEnum._06.getCode());
                if (codes.contains(po.getSource()) && codes.contains(po.getDestination())) {
//                if(codes.stream().anyMatch(e->e == po.getSource()) || codes.stream().anyMatch(e->e == po.getDestination())){
//                    if (po.getCargoCode() != null && tallyMapper.getIsTally(po.getCargoCode()) == 1) {
                    if (po.getCargoCode() != null) {
                        insertIntOutYardNew(po, tYardTallyPO);
                    }
                }
            }
            //添加子表信息
            tallyMapper.tallyItem(listTallyList);
            //附件保存
//            List<Long> fileIds = new ArrayList<>();
//            for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
//                fileIds.add(file.getId());
//            }
//            sysFileService.saveFileBusRelation(fileIds, id);
            // 标号处理
//            if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
//                saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, true);
//            }

        } else {
            throw new BusinessRuntimeException("理货失败");
        }
        LOGGER.info("理货成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }

    //特殊临时处理
    public String newProcessTemp(TYardTallyItemPO po) {
        if (po.getCargoName().contains("/")) {
            MCargoDTO cargoDTO = cargoMapper.getCargoByCargoCode(po.getCargoCode());
            return cargoDTO.getCargoName();
        }
        return po.getCargoName();
    }


    /**
     * 判断出库港存，最大负200且不能超过港存最大量
     * Arrays.asList(SourceTargetTypeEnum._05.getCode(),SourceTargetTypeEnum._06.getCode()).stream().anyMatch(e->e.equals(tYardTallyPO.getSource()))
     * &&
     * Arrays.asList(SourceTargetTypeEnum._01.getCode(),SourceTargetTypeEnum._03.getCode()).stream().anyMatch(e->e.equals(tYardTallyPO.getDestination()))
     */
    private void judgmentPortStorage(TYardTallyItemPO po, TYardTallyPO tYardTallyPO) {
        if ((SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._06.getCode().equals(tYardTallyPO.getSource()))
                && (SourceTargetTypeEnum._03.getCode().equals(tYardTallyPO.getDestination()) || SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getDestination()))) {
            BigDecimal portStorageCount = new BigDecimal(sysParameterMapper.getByKey("PORT_STORAGE_COUNT").getParamVal());
            Map<String, Object> sumMap = tPrdPortStorageMapper.summaryQuantityTonById(po.getCargoInfoId());
            BigDecimal ton = ObjectUtil.isEmpty(po.getTon()) ? po.getTonOut() : po.getTon();
            if (ObjectUtil.isEmpty(ton)) {
                throw new BusinessRuntimeException("请填写理货数量");
            }
            if (new BigDecimal(String.valueOf(sumMap.get("ton"))).compareTo(ton) == -1) {
                throw new BusinessRuntimeException(po.getCargoName() + "港存量不足,剩余港存量:" + sumMap.get("ton") + "吨");
            } else if (po.getTonSurplus().add(portStorageCount).compareTo(ton) == -1) {
                throw new BusinessRuntimeException(po.getStackPositionName() + "垛位最大负港存量为：" + portStorageCount);
            }
        }
    }


    /**
     * 新流程
     *
     * @param tYardTallyPO
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void tallyCheChuanNew(TYardTallyPO tYardTallyPO) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.TALLY_KEY.getCode() + tYardTallyPO.getWeighbridgeId())
                .build().run(() -> {
                    isLogout(tYardTallyPO.getCargoInfoId());
                    final String methodName = "tYardTallyPO";
                    LOGGER.enter(methodName, "理货[start], tYardTallyPO: " + tYardTallyPO);
                    judgmentTicket(tYardTallyPO.getPlanId());
                    long id = snowflake.nextId();
                    tYardTallyPO.setId(id);
                    tYardTallyPO.setRelationId(id);
                    getCargoIsCorrect(tYardTallyPO);
                    //根据磅单ID获取磅单数据
                    TYardTallyPO weightInfoPO = tallyMapper.getWeightInfo(tYardTallyPO);
                    if (!ObjectUtils.isEmpty(weightInfoPO)) {
                        tYardTallyPO.setTsptId(weightInfoPO.getTsptId());
                        tYardTallyPO.setPlanNo(weightInfoPO.getPlanNo());
                        tYardTallyPO.setWorkErWeiId(weightInfoPO.getWorkErWeiId());
                    } else {
                        if (tYardTallyPO.getWeighbridgeId() != null) {
                            throw new BusinessRuntimeException("获取磅单信息失败,请刷新列表重试");
                        }
                    }
                    //验重
                    int count = tallyMapper.getIsBdTally(tYardTallyPO);
                    if (count > 0) {
                        throw new BusinessRuntimeException("该车辆已理货,不能重复理货");
                    }
                    //标号处理
                    String result = "";
                    if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
                        saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, true);
                        result = tYardTallyPO.getCoilList().stream()
                                .map(obj -> obj.getCoilNum()) // 将每个对象的属性拼接并用逗号分隔
                                .collect(Collectors.joining(","));
                        tYardTallyPO.setRemark("标号:" + result);
                    }
                    //操作地磅表
                    if (tYardTallyPO.getWeighbridgeId() != null) {
                        SpringUtils.getBean(this.getClass()).updateWeightNew(tYardTallyPO);
                    }
                    //新增主表信息
                    int i = tallyMapper.tally(tYardTallyPO);
                    if (i != 0) {
                        SysParameterDTO sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE");
                        boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
                        List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
                        for (TYardTallyItemPO po : listTallyList) {
                            isLogout(po.getCargoInfoId());
                            po.setId(snowflake.nextId());
                            //主表ID
                            po.setTallyId(id);
                            if ((SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) &&
                                    SourceTargetTypeEnum._03.getCode().equals(tYardTallyPO.getDestination()) ||
                                    SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) &&
                                            SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getDestination()))) {
                                //岸-车 岸-船 使用的剩余场存
                                if (po.getQuantityOut() != null) {
                                    if (oneCarPlan) {
                                        //判断下次理货时理货数量不能超过剩余件数数量
                                        if (po.getQuantityOut() > po.getQuantitySurplus()) {
                                            throw new BusinessRuntimeException("理货件数大于剩余件数!");
                                        }
                                    }

                                    po.setQuantity(po.getQuantityOut());
                                } else {
                                    po.setQuantity(0);
                                }
                                if (po.getTonOut() != null) {
                                    if (oneCarPlan) {
                                        if (po.getTonOut().compareTo(po.getTonSurplus()) > 0) {
                                            throw new BusinessRuntimeException("理货重量大于剩余重量!");
                                        }
                                    }
                                    po.setTon(po.getTonOut());
                                }
                            } else {
                                if ((po.getQuantity() == null || po.getQuantity() == 0) && tYardTallyPO.getPackingCode().equals("02")) {
                                    throw new BusinessRuntimeException("请输入件数!");
                                }
                                if (po.getTon() == null || po.getTon().compareTo(BigDecimal.ZERO) == 0) {
                                    throw new BusinessRuntimeException("请输入重量!");
                                }
                            }
                            if (po.getCargoInfoId() == null) {
                                //货物信息
                                po.setCargoInfoId(tYardTallyPO.getCargoInfoId());
                                po.setCargoCode(tYardTallyPO.getCargoCode());
                                po.setCargoName(tYardTallyPO.getCargoName());//货物信息
                            }
                            po.setTrustCargoInfoId(tYardTallyPO.getTrustCargoInfoId());
                            //判断垛号是输入还是选择
                            //处理一下垛号
//                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
//                    insertMiss(po);
//                }
                            //场存节点是理货时，新增出入库记录(1.判断作业过程源或目的为场地的 2.货种节点为理货的 先1后2)
                            if ((po.getSourceOrTargetFlag().equals("1") && SourceTargetTypeEnum._05.getCode().equals(po.getSource()))
                                    || (po.getSourceOrTargetFlag().equals("1") && SourceTargetTypeEnum._06.getCode().equals(po.getSource()))
                                    || (po.getSourceOrTargetFlag().equals("2") && SourceTargetTypeEnum._05.getCode().equals(po.getDestination()))
                                    || (po.getSourceOrTargetFlag().equals("2") && SourceTargetTypeEnum._06.getCode().equals(po.getDestination()))) {
                                if (po.getCargoCode() != null) {
//                                    Integer isTally = tallyMapper.getIsTally(po.getCargoCode());
                                    insertIntOutYardNew(po, tYardTallyPO);
                                }
                            }
                        }
                        //添加子表信息
                        tallyMapper.tallyItem(listTallyList);
                        //附件
                        List<Long> fileIds = new ArrayList<>();
                        for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                            fileIds.add(file.getId());
                        }
                        // 附件保存
                        sysFileService.saveFileBusRelation(fileIds, id);
                    } else {
                        throw new BusinessRuntimeException("理货失败");
                    }
                    LOGGER.info("理货成功");
                    LOGGER.exit(methodName, "业务数据同步服务[end]");

                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void tallyChangChuanNew(TYardTallyPO tYardTallyPO) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.TALLY_KEY.getCode() + tYardTallyPO.getWeighbridgeId())
                .build().run(() -> {
                    final String methodName = "tYardTallyPO";
                    LOGGER.enter(methodName, "理货[start], tYardTallyPO: " + tYardTallyPO);
                    isLogout(tYardTallyPO.getCargoInfoId());
                    judgmentTicket(tYardTallyPO.getPlanId());
                    long id = snowflake.nextId();
                    tYardTallyPO.setId(id);
                    boolean AC = SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getDestination());
                    boolean CA = SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination());
                    if (AC || CA) {
                        //岸-船 场-岸 没有关联ID
                        tYardTallyPO.setRelationId(id);
                    } else {
                        //是否待作业
                        if (tYardTallyPO.getRelationId() != null) {
                            //查询是否已经理货了
                            Integer count = tallyMapper.getRelationIdHc(tYardTallyPO);
                            if (count > 0) {
                                throw new BusinessRuntimeException("该数据已理货，不能重复理货");
                            }
                            //关联相关理货ID
                            tallyMapper.updateRelationId(tYardTallyPO);
                        }
                        //没有前置要把relationId置为空
                        if (!tYardTallyPO.getIsFrontType()) {
                            //指令 - 车牌号 - 作业过程 查询关联ID，理完才可继续理货
                            Integer count = tallyMapper.getRelationIdQy(tYardTallyPO);
                            if (count > 0) {
                                throw new BusinessRuntimeException("该转运机械未作业完，请稍后再理货");
                            }
                            //tYardTallyPO.setRelationId(null);
                        }
                    }
                    //新增主表信息
                    int i = tallyMapper.tally(tYardTallyPO);
                    if (i != 0) {
//            SysParameterDTO sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE");
//            boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
//            BigDecimal portStorageCount = new BigDecimal(sysParameterMapper.getByKey("PORT_STORAGE_COUNT").getParamVal());
                        List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
                        for (TYardTallyItemPO po : listTallyList) {
                            isLogout(po.getCargoInfoId());
                            judgmentPortStorage(po, tYardTallyPO);
                            po.setId(snowflake.nextId());
                            //主表ID
                            po.setTallyId(id);
                            //场存节点是理货时，新增出入库记录(1.判断作业过程源或目的为场地的 2.货种节点为理货的 先1后2)
                            if ((po.getSourceOrTargetFlag().equals("1") && SourceTargetTypeEnum._05.getCode().equals(po.getSource()))
                                    || (po.getSourceOrTargetFlag().equals("1") && SourceTargetTypeEnum._06.getCode().equals(po.getSource()))
                                    || (po.getSourceOrTargetFlag().equals("2") && SourceTargetTypeEnum._05.getCode().equals(po.getDestination()))
                                    || (po.getSourceOrTargetFlag().equals("2") && SourceTargetTypeEnum._06.getCode().equals(po.getDestination()))) {
                                if (po.getCargoCode() != null) {
//                                    Integer isTally = tallyMapper.getIsTally(po.getCargoCode());
//                                    if (isTally != null) {
                                    if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
                                        //场-岸 岸-场 先出 后入
                                        tYardTallyPO.setIsCrk("201");
                                        insertIntOutYardNew(po, tYardTallyPO);
                                        tYardTallyPO.setIsCrk("101");
                                        if (po.getStorehouseTargetId() == null) {
                                            throw new BusinessRuntimeException("请选择目标场地");
                                        }
                                        po.setStorehouseId(po.getStorehouseTargetId()); //库场
                                        po.setStorehouseName(po.getStorehouseTargetName());
                                        po.setLocationId(po.getLocationTargetId());
                                        po.setLocationNo(po.getLocationTargetNo());
                                        po.setStackPositionId(po.getStackPositionTargetId());
                                        po.setStackPositionName(po.getStackPositionTargetName());
                                    }
                                    insertIntOutYardNew(po, tYardTallyPO);
//                                    }
                                }
                            }
                        }
                        //添加子表信息
                        tallyMapper.tallyItem(listTallyList);
                        //附件保存
                        List<Long> fileIds = new ArrayList<>();
                        for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                            fileIds.add(file.getId());
                        }
                        sysFileService.saveFileBusRelation(fileIds, id);
                        // 标号处理
                        if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
                            saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, true);
                        }

                    } else {
                        throw new BusinessRuntimeException("理货失败");
                    }
                    LOGGER.info("理货成功");
                    LOGGER.exit(methodName, "业务数据同步服务[end]");

                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void tallyChangCheNew(TYardTallyPO tYardTallyPO) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.TALLY_KEY.getCode() + tYardTallyPO.getWeighbridgeId())
                .build().run(() -> {
                    final String methodName = "tYardTallyPO";
                    LOGGER.enter(methodName, "理货[start], tYardTallyPO: " + tYardTallyPO);
                    isLogout(tYardTallyPO.getCargoInfoId());
                    judgmentTicket(tYardTallyPO.getPlanId());
                    long id = snowflake.nextId();
                    tYardTallyPO.setId(id);
                    tYardTallyPO.setRelationId(id);
                    getCargoIsCorrect(tYardTallyPO);
                    //根据磅单ID获取磅单数据
                    TYardTallyPO weightInfoPO = tallyMapper.getWeightInfo(tYardTallyPO);
                    if (!ObjectUtils.isEmpty(weightInfoPO)) {
                        tYardTallyPO.setTsptId(weightInfoPO.getTsptId());
                        tYardTallyPO.setPlanNo(weightInfoPO.getPlanNo());
                        tYardTallyPO.setWorkErWeiId(weightInfoPO.getWorkErWeiId());
                    } else {
                        throw new BusinessRuntimeException("获取磅单信息失败,请刷新列表重试");
                    }
                    //验重
                    int count = tallyMapper.getIsBdTally(tYardTallyPO);
                    if (count > 0) {
                        throw new BusinessRuntimeException("该车辆已理货,不能重复理货");
                    }
                    //新增主表信息
                    int i = tallyMapper.tally(tYardTallyPO);
                    if (i != 0) {
                        List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
                        for (TYardTallyItemPO po : listTallyList) {
                            isLogout(po.getCargoInfoId());
                            judgmentPortStorage(po, tYardTallyPO);
                            po.setId(snowflake.nextId());
                            //主表ID
                            po.setTallyId(id);
                            po.setTrustCargoInfoId(tYardTallyPO.getTrustCargoInfoId());
                            //判断垛号是输入还是选择
                            //场存节点是理货时，新增出入库记录(1.判断作业过程源或目的为场地的 2.货种节点为理货的 先1后2)
                            if ((po.getSourceOrTargetFlag().equals("1") && SourceTargetTypeEnum._05.getCode().equals(po.getSource()))
                                    || (po.getSourceOrTargetFlag().equals("1") && SourceTargetTypeEnum._06.getCode().equals(po.getSource()))
                                    || (po.getSourceOrTargetFlag().equals("2") && SourceTargetTypeEnum._05.getCode().equals(po.getDestination()))
                                    || (po.getSourceOrTargetFlag().equals("2") && SourceTargetTypeEnum._06.getCode().equals(po.getDestination()))) {
                                if (po.getCargoCode() != null) {
                                    insertIntOutYardNew(po, tYardTallyPO);
                                }
                            }
                        }
                        //添加子表信息
                        tallyMapper.tallyItem(listTallyList);
                        //附件保存
                        List<Long> fileIds = new ArrayList<>();
                        for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                            fileIds.add(file.getId());
                        }
                        // 附件保存
                        sysFileService.saveFileBusRelation(fileIds, id);
                        String result = "";
                        if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
                            saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO, true);
                            result = tYardTallyPO.getCoilList().stream()
                                    .map(obj -> obj.getCoilNum()) // 将每个对象的属性拼接并用逗号分隔
                                    .collect(Collectors.joining(","));
                            tYardTallyPO.setRemark("标号:" + result);
                        }
                        SpringUtils.getBean(this.getClass()).updateWeightNew(tYardTallyPO);

                    } else {
                        throw new BusinessRuntimeException("理货失败");
                    }
                    LOGGER.exit(methodName, "业务数据同步服务[end]");

                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void tallyDunBao(DunBaoTallyDTO dto) {
        final String methodName = "tallyDunBao";
        LOGGER.enter(methodName, "理货[start], tYardTallyPO: " + dto);
        TYardTallyPO tYardTallyPO = new TYardTallyPO();
        //校验
        //如果是场到船，因为库场已经理货，所以不再重新理货
        TDisShipvoyagePO shipvoyagePO = tDisShipVoyageMapper.getDisShipVoyageItemId(Long.valueOf(dto.getShipVoyageItemId()));
        if ("3".equals(dto.getWorkType()) && "装".equals(shipvoyagePO.getLoadUnload())) {
            //作业过程判定为场-船，直接返回不予理货
            return;
        }
        long id = snowflake.nextId();
        //获取日志信息，判断是否是新增
        DunBaoTallyDTO dunBaoTallyDTO = tallyMapper.getDbTallyById(dto.getId());
        if ("1".equals(dto.getWorkType())) {//船-车、车-船
            tYardTallyPO.setWeighbridgeId(Long.valueOf(dto.getWeighbridgeId()));
            tYardTallyPO.setTransportEquipmentNo(dto.getTransportEquipmentNo());
            List<TYardTallyPO> tallyPOS = tallyMapper.poByCondition(tYardTallyPO);
            if (ObjectUtil.isEmpty(dunBaoTallyDTO) && CollectionUtils.isEmpty(tallyPOS)) {//新增：无理货记录，无日志记录
                insertDbTally(id, dto);//保存吨包理货记录
                convertTallyDto(dto, tYardTallyPO);//转换理货
                tYardTallyPO.setId(id);//设置主键
                tYardTallyPO.setRelationId(id);
                //根据磅单ID获取磅单数据
                TYardTallyPO weightInfoPO = tallyMapper.getWeightInfo(tYardTallyPO);
                if (!ObjectUtils.isEmpty(weightInfoPO)) {
                    tYardTallyPO.setTsptId(weightInfoPO.getTsptId());
                    tYardTallyPO.setPlanNo(weightInfoPO.getPlanNo());
                    tYardTallyPO.setWorkErWeiId(weightInfoPO.getWorkErWeiId());
                } else {
                    throw new BusinessRuntimeException("获取磅单信息失败,请刷新列表重试");
                }

                //todo.测试注释 ==> checkWeightInfo(tYardTallyPO);//校验理货信息,如果是船-车，车-船需要校验是否出港
                saveTally(tYardTallyPO);//保存理货
            } else if (!CollectionUtils.isEmpty(tallyPOS)) {//有理货记录，需要合并日志记录，并更新理货记录
                if (ObjectUtil.isEmpty(dunBaoTallyDTO)) {
                    insertDbTally(id, dto);//保存吨包理货记录
                } else {
                    updateDbTally(id, dto);//更新保存吨包理货记录
                }
                mergeDbTally(dto);//获取全部的吨包理货记录,合并数据
                convertTallyDto(dto, tYardTallyPO);//转换为理货数据
                //todo.测试注释 ==> checkWeightInfo(tYardTallyPO);//校验理货信息,如果是船-车，车-船需要校验是否出港
                tallyPOS.get(0).setTallyId(String.valueOf(tallyPOS.get(0).getId()));
                //todo.测试注释 ==> deleteNotesNew(tallyPOS.get(0));//删除旧的理货
                saveTally(tYardTallyPO);//重新保存理货记录
            }
        } else if ("2".equals(dto.getWorkType())) {//船-岸、岸-船
            if (ObjectUtil.isEmpty(dunBaoTallyDTO)) { //新增，
                insertDbTally(id, dto);//保存吨包理货记录
                convertTallyDto(dto, tYardTallyPO);//转换理货
                tYardTallyPO.setId(id);//设置主键
                tYardTallyPO.setRelationId(id);
                saveTally(tYardTallyPO);//保存理货
            } else {//修改：有理货记录，有相同的日志记录，需要更新单条 todo.需要关联理货表,在日报表添加生产系统理货id
                Long scTallyId = dunBaoTallyDTO.getScTallyId();
                updateDbTally(id, dto);//更新保存吨包理货记录
                convertTallyDto(dto, tYardTallyPO);//转换为理货数据
                tYardTallyPO.setId(id);//设置主键
                tYardTallyPO.setRelationId(id);
                TYardTallyPO tallyPO = tallyMapper.getById(scTallyId);
                tallyPO.setTallyId(String.valueOf(scTallyId));
                deleteNotesNew(tallyPO);//删除旧的理货
                saveTally(tYardTallyPO);//重新保存理货记录
            }
        } else if ("3".equals(dto.getWorkType())) {//船-场、场-船
            tYardTallyPO.setReservationPoundId(Long.valueOf(dto.getReservationPoundId()));
            List<TYardTallyPO> tallyPOS = tallyMapper.poByPoundId(tYardTallyPO);
            if (ObjectUtil.isEmpty(dunBaoTallyDTO) && CollectionUtils.isEmpty(tallyPOS)) {//新增：无理货记录，无日志记录
                insertDbTally(id, dto);//保存吨包理货记录
                convertTallyDto(dto, tYardTallyPO);//转换理货
                tYardTallyPO.setId(id);//设置主键
                tYardTallyPO.setRelationId(id);
//                checkWeightInfo(tYardTallyPO);//校验理货信息,如果是船-车，车-船需要校验是否出港
                saveTally(tYardTallyPO);//保存理货
            } else if (!CollectionUtils.isEmpty(tallyPOS)) {
                if (ObjectUtil.isEmpty(dunBaoTallyDTO)) {
                    insertDbTally(id, dto);//保存吨包理货记录
                } else {
                    updateDbTally(id, dto);//更新保存吨包理货记录
                }
                mergeDbTallyByPoundId(dto);//获取全部的吨包理货记录,合并数据
                convertTallyDto(dto, tYardTallyPO);//转换为理货数据
                tYardTallyPO.setId(id);//设置主键
                tYardTallyPO.setRelationId(id);
                tallyPOS.get(0).setTallyId(String.valueOf(tallyPOS.get(0).getId()));
                deleteNotesNew(tallyPOS.get(0));//删除旧的理货
                saveTally(tYardTallyPO);//重新保存理货记录
            }
        }
    }

    /**
     * 校验磅单信息
     *
     * @param tYardTallyPO
     */
    private void checkWeightInfo(TYardTallyPO tYardTallyPO) {
        //校验磅单票货信息
        getCargoIsCorrect(tYardTallyPO);
        //根据磅单ID获取磅单数据
        TYardTallyPO weightInfoPO = tallyMapper.getWeightInfo(tYardTallyPO);
        if (!ObjectUtils.isEmpty(weightInfoPO)) {
            tYardTallyPO.setTsptId(weightInfoPO.getTsptId());
            tYardTallyPO.setPlanNo(weightInfoPO.getPlanNo());
            tYardTallyPO.setWorkErWeiId(weightInfoPO.getWorkErWeiId());
        } else {
            throw new BusinessRuntimeException("获取磅单信息失败,请刷新列表重试");
        }
        String status = tallyMapper.getWeightStatusNew(String.valueOf(tYardTallyPO.getWeighbridgeId()));
        if ("1".equals(status)) {
            throw new BusinessRuntimeException("车辆已出港，不允许新增或修改!");
        }
    }

    /**
     * 理货信息保存，港存信息保存
     *
     * @param tYardTallyPO
     */
    private void saveTally(TYardTallyPO tYardTallyPO) {
        LOGGER.enter("吨包理货新增[start]");
        //新增主表信息
        int i = tallyMapper.tally(tYardTallyPO);
        if (i != 0) {
            List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
            for (TYardTallyItemPO po : listTallyList) {
                judgmentPortStorage(po, tYardTallyPO);
                po.setId(snowflake.nextId());
                //主表ID
                po.setTallyId(tYardTallyPO.getId());
                po.setTrustCargoInfoId(tYardTallyPO.getTrustCargoInfoId());
                //判断垛号是输入还是选择
                //场存节点是理货时，新增出入库记录(1.判断作业过程源或目的为场地的 2.货种节点为理货的 先1后2)
                if ((po.getSourceOrTargetFlag().equals("1") && SourceTargetTypeEnum._05.getCode().equals(po.getSource()))
                        || (po.getSourceOrTargetFlag().equals("1") && SourceTargetTypeEnum._06.getCode().equals(po.getSource()))
                        || (po.getSourceOrTargetFlag().equals("2") && SourceTargetTypeEnum._05.getCode().equals(po.getDestination()))
                        || (po.getSourceOrTargetFlag().equals("2") && SourceTargetTypeEnum._06.getCode().equals(po.getDestination()))) {
                    if (po.getCargoCode() != null) {
                        insertIntOutYardNew(po, tYardTallyPO);
                    }
                }
            }
            //添加子表信息
            tallyMapper.tallyItem(listTallyList);
            //更新磅单表理货状态
            if ("10050001".equals(tYardTallyPO.getProcessCode()) || "100120001".equals(tYardTallyPO.getProcessCode())) {
                SpringUtils.getBean(this.getClass()).updateWeightNew(tYardTallyPO);
            }
        } else {
            throw new BusinessRuntimeException("理货失败");
        }
        LOGGER.exit("吨包理货新增[end]");
    }

    /**
     * 保存吨包理货接口信息并返回合并的吨包理货信息
     *
     * @param dto
     * @return
     */
    private DunBaoTallyDTO saveDbTally(DunBaoTallyDTO dto) {
        //根据id,查询是否是更新
        DunBaoTallyDTO dunBaoTallyDTO = tallyMapper.getDbTallyById(dto.getId());
        if (ObjectUtil.isEmpty(dunBaoTallyDTO)) {
            LOGGER.warn("保存：" + JSONUtil.toJsonStr(dto));
            //1.新增securityUtils.getLoginUserName()
            dto.setCreateBy(2l);
            dto.setCreateByName("吨包理货:" + dto.getOperatorsName());
            dto.setCreateTime(new Date());
            dto.setLoginUserId(2l);
            dto.setLoginUserName("吨包理货:" + dto.getOperatorsName());
            dto.setNow(new Date());
            tallyMapper.saveDbTally(dto);
        } else {
            //2.更新，同步更新理货表，需后面处理
            LOGGER.warn("更新：" + JSONUtil.toJsonStr(dto));
            dto.setUpdateBy(2l);
            dto.setUpdateByName("吨包理货:" + dto.getOperatorsName());
            dto.setUpdateTime(new Date());
            dto.setLoginUserId(2l);
            dto.setLoginUserName("吨包理货:" + dto.getOperatorsName());
            dto.setNow(new Date());
            tallyMapper.updateDbTally(dto);
            //删除子表所有数据
            tallyMapper.deleteDbTallyItemByDbId(dto.getId());
        }
        //更新子表
        List<DunBaoTallyItemDTO> items = Lists.newArrayList();
        for (DunBaoTallyDTO.TallyItem item : dto.getListTallyItemList()) {
            DunBaoTallyItemDTO itemDTO = new DunBaoTallyItemDTO();
            BeanUtil.copyProperties(item, itemDTO);
            itemDTO.setId(snowflake.nextId());
            itemDTO.setTallyId(dto.getId());
            itemDTO.setCreateBy(2l);
            itemDTO.setCreateByName("吨包理货:" + dto.getOperatorsName());
            itemDTO.setCreateTime(new Date());
            itemDTO.setLoginUserId(2l);
            itemDTO.setLoginUserName("吨包理货:" + dto.getOperatorsName());
            itemDTO.setNow(new Date());
            items.add(itemDTO);
        }
        //保存
        tallyMapper.saveDbTallyItem(items);
        //如果是修改，则直接返回
        if (!ObjectUtil.isEmpty(dunBaoTallyDTO)) {
            return dto;
        } else {
            //如果不是直取则返回dto
            if (!"1".equals(dto.getWorkType())) {
                return dto;
            } else if ("1".equals(dto.getWorkType()) && StringUtils.isEmpty(dto.getWeighbridgeId())) {
                throw new BusinessRuntimeException("直取磅单不能为空！");
            }
            //根据磅单查询合并
            List<DunBaoTallyDTO> dBTallyDTOS = tallyMapper.dbTallyByWeighbridgeId(dto.getWeighbridgeId());
            DunBaoTallyDTO result = new DunBaoTallyDTO();
            BeanUtil.copyProperties(dto, result);
            BigDecimal quantity = BigDecimal.ZERO;
            BigDecimal ton = BigDecimal.ZERO;
            List<Long> ids = Lists.newArrayList();
            for (DunBaoTallyDTO tallyDTO : dBTallyDTOS) {
                quantity = quantity.add(ObjectUtil.isNotEmpty(tallyDTO.getQuantity()) ? new BigDecimal(tallyDTO.getQuantity()) : BigDecimal.ZERO);
                ton = ton.add(ObjectUtil.isNotEmpty(tallyDTO.getTon()) ? new BigDecimal(tallyDTO.getTon()) : BigDecimal.ZERO);
                ids.add(tallyDTO.getId());
            }
            result.setQuantity(String.valueOf(quantity));
            result.setTon(String.valueOf(ton));
            List<DunBaoTallyItemDTO> tallyItemDTOS = tallyMapper.getDbTallyItemByDbTallyId(ids);
            List<DunBaoTallyDTO.TallyItem> tallyItems = Lists.newArrayList();
            tallyItemDTOS.forEach(e -> {
                DunBaoTallyDTO.TallyItem item = new DunBaoTallyDTO.TallyItem();
                BeanUtil.copyProperties(e, item);
                tallyItems.add(item);
            });
            result.setListTallyItemList(tallyItems);
            return result;
        }
    }

    /**
     * 更新吨包记录表
     *
     * @param dto
     */
    private void updateDbTally(Long scTallyId, DunBaoTallyDTO dto) {
        //2.更新，同步更新理货表，需后面处理
        LOGGER.warn("更新：" + JSONUtil.toJsonStr(dto));
        dto.setScTallyId(scTallyId);
        dto.setUpdateBy(2l);
        dto.setUpdateByName("吨包理货:" + dto.getOperatorsName());
        dto.setUpdateTime(new Date());
        dto.setLoginUserId(2l);
        dto.setLoginUserName("吨包理货:" + dto.getOperatorsName());
        dto.setNow(new Date());
        tallyMapper.updateDbTally(dto);
        //删除子表所有数据
        tallyMapper.deleteDbTallyItemByDbId(dto.getId());
        //更新子表
        List<DunBaoTallyItemDTO> items = Lists.newArrayList();
        for (DunBaoTallyDTO.TallyItem item : dto.getListTallyItemList()) {
            DunBaoTallyItemDTO itemDTO = new DunBaoTallyItemDTO();
            BeanUtil.copyProperties(item, itemDTO);
            itemDTO.setId(snowflake.nextId());
            itemDTO.setTallyId(dto.getId());
            itemDTO.setCreateBy(2l);
            itemDTO.setCreateByName("吨包理货:" + dto.getOperatorsName());
            itemDTO.setCreateTime(new Date());
            itemDTO.setLoginUserId(2l);
            itemDTO.setLoginUserName("吨包理货:" + dto.getOperatorsName());
            itemDTO.setNow(new Date());
            items.add(itemDTO);
        }
        //保存
        tallyMapper.saveDbTallyItem(items);
    }

    /**
     * 保存吨包记录表
     *
     * @param dto
     */
    private void insertDbTally(Long scTallyId, DunBaoTallyDTO dto) {
        //根据id,查询是否是更新
        dto.setScTallyId(scTallyId);
        dto.setCreateBy(2l);
        dto.setCreateByName("吨包理货:" + dto.getOperatorsName());
        dto.setCreateTime(new Date());
        dto.setLoginUserId(2l);
        dto.setLoginUserName("吨包理货:" + dto.getOperatorsName());
        dto.setNow(new Date());
        tallyMapper.saveDbTally(dto);
        //更新子表
        List<DunBaoTallyItemDTO> items = Lists.newArrayList();
        for (DunBaoTallyDTO.TallyItem item : dto.getListTallyItemList()) {
            DunBaoTallyItemDTO itemDTO = new DunBaoTallyItemDTO();
            BeanUtil.copyProperties(item, itemDTO);
            itemDTO.setId(snowflake.nextId());
            itemDTO.setTallyId(dto.getId());
            itemDTO.setCreateBy(2l);
            itemDTO.setCreateByName("吨包理货:" + dto.getOperatorsName());
            itemDTO.setCreateTime(new Date());
            itemDTO.setLoginUserId(2l);
            itemDTO.setLoginUserName("吨包理货:" + dto.getOperatorsName());
            itemDTO.setNow(new Date());
            items.add(itemDTO);
        }
        //保存
        tallyMapper.saveDbTallyItem(items);
    }

    /**
     * 合并吨包记录表
     *
     * @param dto
     */
    private void mergeDbTally(DunBaoTallyDTO dto) {
        //根据id,查询是否是更新
        List<DunBaoTallyDTO> dBTallyDTOS = tallyMapper.dbTallyByWeighbridgeId(dto.getWeighbridgeId());
        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal ton = BigDecimal.ZERO;
        List<Long> ids = Lists.newArrayList();
        for (DunBaoTallyDTO tallyDTO : dBTallyDTOS) {
            quantity = quantity.add(ObjectUtil.isNotEmpty(tallyDTO.getQuantity()) ? new BigDecimal(tallyDTO.getQuantity()) : BigDecimal.ZERO);
            ton = ton.add(ObjectUtil.isNotEmpty(tallyDTO.getTon()) ? new BigDecimal(tallyDTO.getTon()) : BigDecimal.ZERO);
            ids.add(tallyDTO.getId());
        }
        dto.setQuantity(String.valueOf(quantity));
        dto.setTon(String.valueOf(ton));
        List<DunBaoTallyItemDTO> tallyItemDTOS = tallyMapper.getDbTallyItemByDbTallyId(ids);
        List<DunBaoTallyDTO.TallyItem> tallyItems = Lists.newArrayList();
        tallyItemDTOS.forEach(e -> {
            DunBaoTallyDTO.TallyItem item = new DunBaoTallyDTO.TallyItem();
            BeanUtil.copyProperties(e, item);
            tallyItems.add(item);
        });
        dto.setListTallyItemList(tallyItems);
    }

    /**
     * 合并吨包记录表
     *
     * @param dto
     */
    private void mergeDbTallyByPoundId(DunBaoTallyDTO dto) {
        //根据id,查询是否是更新
        List<DunBaoTallyDTO> dBTallyDTOS = tallyMapper.dbTallyByPoundId(dto.getReservationPoundId());
        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal ton = BigDecimal.ZERO;
        List<Long> ids = Lists.newArrayList();
        for (DunBaoTallyDTO tallyDTO : dBTallyDTOS) {
            quantity = quantity.add(ObjectUtil.isNotEmpty(tallyDTO.getQuantity()) ? new BigDecimal(tallyDTO.getQuantity()) : BigDecimal.ZERO);
            ton = ton.add(ObjectUtil.isNotEmpty(tallyDTO.getTon()) ? new BigDecimal(tallyDTO.getTon()) : BigDecimal.ZERO);
            ids.add(tallyDTO.getId());
        }
        dto.setQuantity(String.valueOf(quantity));
        dto.setTon(String.valueOf(ton));
        List<DunBaoTallyItemDTO> tallyItemDTOS = tallyMapper.getDbTallyItemByDbTallyId(ids);
        List<DunBaoTallyDTO.TallyItem> tallyItems = Lists.newArrayList();
        tallyItemDTOS.forEach(e -> {
            DunBaoTallyDTO.TallyItem item = new DunBaoTallyDTO.TallyItem();
            BeanUtil.copyProperties(e, item);
            tallyItems.add(item);
        });
        dto.setListTallyItemList(tallyItems);
    }


    /**
     * 吨包记录转换为理货信息，吨包理货转换
     *
     * @param dto
     * @param po
     */
    private void convertTallyDto(DunBaoTallyDTO dto, TYardTallyPO po) {
//        直取、港内短倒、港内直取
//        船-车：100120001
//        车-船：10050001
//        船-岸：10190001
//        岸-船：10220001
//        船-场：10260008
//        场-船：10250009
        String processCode = "";//子作业过程code
        TDisShipvoyagePO shipvoyagePO = tDisShipVoyageMapper.getDisShipVoyageItemId(Long.valueOf(dto.getShipVoyageItemId()));
//        TBusCargoInfoDTO cargoInfoDTO = tBusCargoInfoMapper.getById(Long.valueOf(dto.getCargoInfoId()));
        if ("1".equals(dto.getWorkType())) {
            processCode = "装".equals(shipvoyagePO.getLoadUnload()) ? "10050001" : "100120001";
        } else if ("2".equals(dto.getWorkType())) {
            processCode = "装".equals(shipvoyagePO.getLoadUnload()) ? "10220001" : "10190001";
        } else if ("3".equals(dto.getWorkType())) {
            processCode = "装".equals(shipvoyagePO.getLoadUnload()) ? "10250009" : "10260008";
        }
        Map<String, String> processMap = tallyMapper.getProcessByCode(processCode);
        List<Map<String, String>> trustInfos = tDisShipVoyageMapper.getTrustInfo1(Long.valueOf(dto.getShipVoyageItemId()), processMap.get("processParentCd"), dto.getWorkDate(), dto.getClassCode());
        Map<String, String> trustMap = CollectionUtils.isEmpty(trustInfos) ? new HashMap<>() : trustInfos.get(0);
        if (trustMap.isEmpty()) {
            throw new BusinessRuntimeException("无计划，无法理货");
        }
        BeanUtil.copyProperties(dto, po);
        po.setRelationId(po.getId());
        po.setCompanyId(Long.parseLong(String.valueOf(trustMap.get("companyId"))));//作业公司//
        po.setCompanyName(trustMap.get("companyName"));//作业公司//
        po.setPlanId(Long.valueOf(String.valueOf(trustMap.get("planId"))));//计划id
        po.setTrustId(Long.valueOf(String.valueOf(trustMap.get("trustId"))));//指令id
        po.setWorkDate(dto.getWorkDate());
        po.setClassCode(dto.getClassCode());
        po.setClassName(dto.getClassName());
        po.setShipvoyageId(shipvoyagePO.getId());
        po.setShipvoyageItemId(Long.parseLong(dto.getShipVoyageItemId()));
        String source = "";
        String destination = "";
        String sourceOrTargetFlag = "";
        //100120001	船-车(卸船直取)	1
        //10220001	岸-船(落地装船)	1
        //10250009	场-车-船	        1
        //10050001    车-船(装船直取)	2
        //10190001	船-岸(卸船落地)	2
        //10260008	船-车-场	        2
        if ("100120001".equals(processCode) || "10220001".equals(processCode) || "10250009".equals(processCode)) {
            sourceOrTargetFlag = "1";
        }
        if ("10190001".equals(processCode) || "10260008".equals(processCode) || "10050001".equals(processCode)) {
            sourceOrTargetFlag = "2";
        }
        po.setProcessCode(processMap.get("processCd"));//作业过程//
        po.setProcessName(processMap.get("processNm"));//作业过程//
        source = processMap.get("sourceCd");
        destination = processMap.get("targetCd");
        po.setSource(source);//源头
        po.setDestination(destination);//目的
        po.setUpdatePoint(1);
        po.setIsFrontType(false);
        Long equipmentId = tallyMapper.getIdByNo(dto.getEquipmentNo());
        po.setEquipmentId(equipmentId);//作业机械
        po.setEquipmentNo(dto.getEquipmentNo());//作业机械
        po.setTransportEquipmentNo(dto.getTransportEquipmentNo());//转运机械
        if ("1".equals(dto.getWorkType())) {
            po.setWeighbridgeId(Long.valueOf(dto.getWeighbridgeId()));
        }
        po.setOperatorsName(dto.getOperatorsName());
        po.setDeptParentId(Long.valueOf(dto.getDeptParentId()));
        po.setDeptParentName(dto.getDeptParentName());
        po.setRemark("吨包理货");
        if (StringUtils.isNotEmpty(dto.getReservationPoundId())) {
            po.setReservationPoundId(Long.valueOf(dto.getReservationPoundId()));
        }
        BigDecimal totalTon = BigDecimal.ZERO;
        List<TYardTallyItemPO> listTallyItemList = Lists.newArrayList();
        for (DunBaoTallyDTO.TallyItem e : dto.getListTallyItemList()) {
            if (StringUtils.isNotEmpty(e.getCargoInfoId())) {
                po.setCargoInfoId(Long.parseLong(e.getCargoInfoId()));
            }
            TYardTallyItemPO item = new TYardTallyItemPO();
            item.setCabinNo(e.getCabinNo());
            item.setQuantity(Integer.valueOf(e.getQuantity()));
            item.setSource(source);
            item.setDestination(destination);
            item.setSourceOrTargetFlag(sourceOrTargetFlag);
            //理货子表创建人
            item.setCreateByName(dto.getOperatorsName());
            item.setRemark("吨包理货");
            item.setCargoInfoId(Long.valueOf(e.getCargoInfoId()));
            TBusCargoInfoDTO cargoInfo = tBusCargoInfoMapper.getById(Long.valueOf(e.getCargoInfoId()));
            item.setCargoCode(cargoInfo.getCargoCode());
            item.setCargoName(cargoInfo.getCargoName());
            //取指令票货的吨数和件数
            TBusTrustCargoDTO tBusTrustCargoDTO = tallyMapper.getTonByTrustIdAndCargoInfoId(Long.valueOf(String.valueOf(trustMap.get("trustId"))), Long.valueOf(e.getCargoInfoId()));
            //吨数除以件数
            BigDecimal result = tBusTrustCargoDTO.getTon().divide(BigDecimal.valueOf(tBusTrustCargoDTO.getQuantity()), 3, RoundingMode.HALF_UP);
            //得出来的结果乘以传过来的件数 等于 本条票货的吨数
            BigDecimal ton = result.multiply(BigDecimal.valueOf(Long.parseLong(e.getQuantity()))).setScale(3, RoundingMode.HALF_UP);
            //把ton加到一起
            totalTon = totalTon.add(ton);
            item.setTon(ton);
            if (!"1".equals(dto.getWorkType())) {
                Long stackPositionId = tallyMapper.getStackIdByStackName(e.getStackPositionName());
                Map<String, String> storeMap = tallyMapper.getStorehouseByMassId(stackPositionId);
                if (storeMap == null) {
                    throw new BusinessRuntimeException("垛位名称填写错误，请检查并修改数据");
                }
                item.setStorehouseId(Long.valueOf(String.valueOf(storeMap.get("storehouseId"))));//场
                item.setStorehouseName(storeMap.get("storehouseName"));//场
                item.setLocationId(Long.parseLong(String.valueOf(storeMap.get("regionId"))));//区
                item.setLocationNo(storeMap.get("regionName"));//区
                item.setStackPositionId(Long.parseLong(String.valueOf(storeMap.get("massId"))));//垛位
                item.setStackPositionName(storeMap.get("massName"));//垛位
            }
            item.setCreateBy(2l);
            item.setCreateByName("吨包理货:" + dto.getOperatorsName());
            item.setCreateTime(new Date());
            item.setLoginUserId(2l);
            item.setLoginUserName("吨包理货:" + dto.getOperatorsName());
            item.setNow(new Date());
            listTallyItemList.add(item);
        }
        po.setTon(totalTon.setScale(3, RoundingMode.HALF_UP).doubleValue());
        po.setQuantity(Double.valueOf(dto.getQuantity()));//总和

        po.setListTallyItemList(listTallyItemList);
    }


    public void insertIntOutYardNew(TYardTallyItemPO po, TYardTallyPO tYardTallyPO) {
        //查询出入库标识
        String inOutType = tallyMapper.getInoutType(tYardTallyPO.getProcessCode());
        if (tYardTallyPO.getIsCrk() != null) {
            inOutType = tYardTallyPO.getIsCrk();
        }
        List<TPrdPortStorageDetailPO> portStorageDetails = new ArrayList<>();
        TPrdPortStorageDetailPO tPrdPortStorageDetailPO = new TPrdPortStorageDetailPO();
        tPrdPortStorageDetailPO.setCargoTallyDetailId(po.getId()); //子表ID
        tPrdPortStorageDetailPO.setWorkDate(DateUtils.parseDate(tYardTallyPO.getWorkDate(), "yyyy-MM-dd"));
        tPrdPortStorageDetailPO.setClassCode(tYardTallyPO.getClassCode()); //班次
        tPrdPortStorageDetailPO.setClassName(tYardTallyPO.getClassName());
        tPrdPortStorageDetailPO.setProcessDetailCode(tYardTallyPO.getProcessCode()); //作业过程
        tPrdPortStorageDetailPO.setProcessDetailName(tYardTallyPO.getProcessName());
        tPrdPortStorageDetailPO.setInoutStorageCode(InoutStorageEnum._10.getCode()); //类型
        tPrdPortStorageDetailPO.setInoutStorageName(InoutStorageEnum._10.getLabel());
        tPrdPortStorageDetailPO.setInoutDate(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate());
        tPrdPortStorageDetailPO.setCleanMassSign(CleanMassSignEnum._0.getCode());
        tPrdPortStorageDetailPO.setCompanyId(tYardTallyPO.getCompanyId()); //公司信息
        tPrdPortStorageDetailPO.setCompanyName(tYardTallyPO.getCompanyName());
        tPrdPortStorageDetailPO.setCargoTallyId(tYardTallyPO.getId()); //理货主表ID
        tPrdPortStorageDetailPO.setCargoInfoId(po.getCargoInfoId());
        tPrdPortStorageDetailPO.setSourceOrTarget(po.getSourceOrTargetFlag());
        if (inOutType != null) {
            //出库
            if ("201".equals(inOutType)) {
                tPrdPortStorageDetailPO.setQuantity(-po.getQuantity()); //件数
                tPrdPortStorageDetailPO.setTon(po.getTon().negate()); //重量
            } else if ("101".equals(inOutType)) {
                //入库
                tPrdPortStorageDetailPO.setQuantity(po.getQuantity()); //件数
                tPrdPortStorageDetailPO.setTon(po.getTon()); //重量
            } else if ("301".equals(inOutType)) {//出入库，直接接受前端传输的数值
                tPrdPortStorageDetailPO.setQuantity("1".equals(po.getSourceOrTargetFlag()) ? -po.getQuantity() : po.getQuantity()); //件数
                tPrdPortStorageDetailPO.setTon("1".equals(po.getSourceOrTargetFlag()) ? po.getTon().multiply(new BigDecimal(-1)) : po.getTon()); //重量

            }
        } else {
            throw new BusinessRuntimeException("理货失败，获取作业过程信息失败");
        }
        tPrdPortStorageDetailPO.setStorehouseId(po.getStorehouseId()); //库场
        tPrdPortStorageDetailPO.setStorehouseName(po.getStorehouseName());
        tPrdPortStorageDetailPO.setRegionId(po.getLocationId());
        tPrdPortStorageDetailPO.setRegionName(po.getLocationNo());
        tPrdPortStorageDetailPO.setMassId(po.getStackPositionId());
        tPrdPortStorageDetailPO.setMassName(po.getStackPositionName());
        portStorageDetails.add(tPrdPortStorageDetailPO);
        if ("201".equals(inOutType) || "101".equals(inOutType) || "301".equals(inOutType)) {
            businessCommonService.insertPortStorageDetail(portStorageDetails);
        }
    }
}
