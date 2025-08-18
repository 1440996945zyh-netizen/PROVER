package com.yy.ppm.tallyExtrinsic.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.google.api.client.util.Maps;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.SpringUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.appWork.mapper.TallyMapper;
import com.yy.ppm.appWork.service.TallyService;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.enums.CleanMassSignEnum;
import com.yy.ppm.common.enums.InoutStorageEnum;
import com.yy.ppm.common.enums.SourceTargetTypeEnum;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.master.bean.po.MWorkProcessPO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import com.yy.ppm.tallyExtrinsic.bean.dto.*;
import com.yy.ppm.tallyExtrinsic.bean.po.*;
import com.yy.ppm.tallyExtrinsic.mapper.TallyExtrinsicMapper;
import com.yy.ppm.tallyExtrinsic.mapper.TallyReservationMapper;
import com.yy.ppm.tallyExtrinsic.service.TallyExtrinsicService;
import com.yy.ppm.tallyExtrinsic.service.TallyReservationService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TallyServiceReservationImpl implements TallyReservationService {
    private static final MicroLogger LOGGER = new MicroLogger(TallyService.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private TallyReservationMapper tallyReservationMapper;

    @Resource
    private SysFileService sysFileService;

    @Resource
    private SecurityUtils securityUtils;

    @Autowired
    private BusinessCommonService businessCommonService;

    @Resource
    private SysParameterMapper sysParameterMapper;

    private static boolean newBusiness = true;


    @Override
    public List<TPrdWorkPlanDTO> getWorkPlan(TPrdWorkPlanSearchDTO searchDTO) {
        //查询该用户是否是调度室和库场部的值班主任
//        Integer flag = tallyReservationMapper.getIsDept(securityUtils.getLoginUserId());
//        searchDTO.setFlag(flag);
//        searchDTO.setLoginId(securityUtils.getLoginUserId() + "");
        List<TPrdWorkPlanDTO> workPlanList = tallyReservationMapper.getWorkPlan(searchDTO);
        List<Map<String, Object>> maps = tallyReservationMapper.getWorkPlanCargoInfo(searchDTO);
        Iterator<TPrdWorkPlanDTO> iter = workPlanList.iterator();
        while (iter.hasNext()) {
            TPrdWorkPlanDTO po = iter.next();
            //查询该计划的所有货物的作业方式
//            List<Map<String, Object>> list = tallyReservationMapper.getWorkType(po.getTrustId());
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
                    List<Map<String, Object>> tBusCargoInfoDTOS = tallyReservationMapper.getTrustInfoNo(prdWorkPlanDTO.getTrustId().toString(), type);
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
                List<Map<String, Object>> list = tallyReservationMapper.getShipName(dto.getTrustId());
                if (list != null && list.size() != 0) {
                    if (list.get(0).get("shipNameVoyage").toString() != null)
                        dto.setShipvoyageLabel(list.get(0).get("shipNameVoyage").toString());
                }

            }
        }
        return workPlanList;
    }

    @Override
    public List<Map<String, Object>> getProcessInfoList(String processCode) {
        final String methodName = "MWorkProcessServiceImpl: getList";
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return tallyReservationMapper.getProcessInfoList(processCode);
    }

    @Override
    public List<Map<String, Object>> getMechanics(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO) {
        return tallyReservationMapper.getMechanics(tPrdDispatchSecondarySearchDTO);
    }

    @Override
    public List<Map<String, Object>> getStorageList(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO) {
        return tallyReservationMapper.getStorageList(tPrdDispatchSecondarySearchDTO);
    }

    @Override
    public List<Map<String, Object>> getTransfer(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO) {
//        tPrdDispatchSecondarySearchDTO.setOperatorsId(securityUtils.getLoginUserId());
        return tallyReservationMapper.getTransfer(tPrdDispatchSecondarySearchDTO);
    }

    @Override
    public List<Map<String, Object>> getCarFer(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO) {
        //判断作业过程
        Map<String, Object> process = tallyReservationMapper.getIsZq(tPrdDispatchSecondarySearchDTO.getProcessCode());
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
            planCode = tallyReservationMapper.getPlanCodeZ(tPrdDispatchSecondarySearchDTO);
        } else {
            //根据指令ID查询指令票货ID
            planCode = tallyReservationMapper.getPlanCode(tPrdDispatchSecondarySearchDTO);
        }
        if (planCode != null && (!"".equals(planCode))) {
            String ids = "(" + planCode + ")";
            //新地磅车号
            List<Map<String, Object>> list = tallyReservationMapper.getCarFerNew(ids);

            //            List<Map<String, Object>> map = new ArrayList<>();
            //            Map<String, Object> mp = new HashMap<>();
            //            mp.put("value","123456789");
            //            mp.put("text","鲁A12345");
            //            map.add(mp);
            return list;
        } else {
            return new ArrayList<>();
        }

    }


    @Override
    public List<Map<String, Object>> getTruckNo(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO) {
        //判断作业过程
        Map<String, Object> process = tallyReservationMapper.getIsZq(tPrdDispatchSecondarySearchDTO.getProcessCode());
        if (process == null) {
            throw new BusinessRuntimeException("获取作业过程信息失败");
        }
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
            planCode = tallyReservationMapper.getPlanCodeZ(tPrdDispatchSecondarySearchDTO);
        } else {
            //根据指令ID查询指令票货ID
            planCode = tallyReservationMapper.getPlanCode(tPrdDispatchSecondarySearchDTO);
        }
        if (planCode != null && (!"".equals(planCode))) {
            String ids = "(" + planCode + ")";
            //新地磅车号
            List<Map<String, Object>> list = tallyReservationMapper.getCarFerNewEx(ids);

            //            List<Map<String, Object>> map = new ArrayList<>();
            //            Map<String, Object> mp = new HashMap<>();
            //            mp.put("value","123456789");
            //            mp.put("text","鲁A12345");
            //            map.add(mp);
            return list;
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public List<Map<String, Object>> getCargoInfoId(String trustId, String planId, String processCode) {
        List<Map<String, Object>> cargoInfoIdList = tallyReservationMapper.getCargoInfoId(trustId, planId);
        if (processCode != null && (!"".equals(processCode))) {
            //判断作业过程
            Map<String, Object> process = tallyReservationMapper.getIsZq(processCode);
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
                    List<Map<String, Object>> businessNoList = tallyReservationMapper.getTrustId(Long.parseLong(map.get("id").toString()), type);
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
        return tallyReservationMapper.getCargoInfoIdTr(planId);
    }

    public void insertIntOutYard(TYardTallyItemPO po, TYardTallyPO tYardTallyPO) {
        //查询出入库标识
        String inOutType = tallyReservationMapper.getInoutType(tYardTallyPO.getProcessCode());
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
        return tallyReservationMapper.getStore(workPlanId);
    }

    @Override
    public List<Map<String, Object>> getStackPosition(Long id) {
        return tallyReservationMapper.getStackPosition(id);
    }

    @Override
    public List<TYardTallyPO> getWaitWork(Long trustId,String macName) {
        final String methodName = "getWaitWork";
        LOGGER.enter(methodName, "根据指令ID查询待作业记录[start], trustId: " + trustId);
        List<TYardTallyPO> list = tallyReservationMapper.getWaitWork(trustId,macName);
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return list;
    }

    @Override
    public Pages<TYardTallyItemPO> getTallyRecord(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        final String methodName = "getTallyRecord";
        LOGGER.enter(methodName, "根据计划id和作业过程查询理货记录[start]");
        Pages<TYardTallyItemPO> pages = PageHelperUtils.limit(pageParameter, () -> {
            return tallyReservationMapper.getTallyRecord(tallyRecordSearchDTO);
        });
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return pages;
    }

    @Override
    public TYardTallyPO getTallyInfoById(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        return tallyReservationMapper.getTallyInfoById(tallyRecordSearchDTO);
    }

    @Override
    public TYardTallyPO getTallyNew(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        return tallyReservationMapper.getTallyNew(tallyRecordSearchDTO);
    }

    @Override
    public Map<String, Object> getTallyRecordSum(TallyRecordSearchDTO tallyRecordSearchDTO) {
        final String methodName = "getTallyRecordSum";
        Map<String, Object> map = tallyReservationMapper.getTallyRecordSum(tallyRecordSearchDTO);
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return map;
    }

    @Override
    public List<AppTallyLadingDTO> getCarDetailedList(TYardMeasureSearchDTO tYardMeasureSearchDTO) {
        final String methodName = "getCarDetailedList";
        LOGGER.enter(methodName, "查询出入库数据[start], getCarDetailedList: " + tYardMeasureSearchDTO);
        //根据指令查票货
        String str = tallyReservationMapper.getTrustCargoId(tYardMeasureSearchDTO.getTrustId());
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
            String isFrontier = tallyReservationMapper.getProcessIsFrontier(tYardMeasureSearchDTO.getProcessCode());
            if (StringUtils.isNotEmpty(isFrontier)) {
                tYardMeasureSearchDTO.setIsFrontier(isFrontier);
            }
        } else {
            tYardMeasureSearchDTO.setSwitchIsFrontier(null);
        }
        List<AppTallyLadingDTO> carDetailedList = tallyReservationMapper.getCarDetailedList(tYardMeasureSearchDTO);
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        return carDetailedList;
    }

//    @Override
//    public Pages<AppTallyCoilNumDTO> getCoilList(AppTallyCoilNumDTO appTallyCoilNumDTO, PageParameter pageParameter) {
//        final String methodName = "getCoilList";
//        LOGGER.enter(methodName, "查询标号[start], getCarDetailedList: " + appTallyCoilNumDTO.getCargoInfoId());
//        Pages<AppTallyCoilNumDTO> pages = PageHelperUtils.limit(pageParameter, () -> {
//            return tallyReservationMapper.getNumber(appTallyCoilNumDTO);
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
        return tallyReservationMapper.getNumber(appTallyCoilNumDTO);
    }

    @Override
    public List<DepartureDTO> getDepartureList(String truckPlate) {
        final String methodName = "getDepartureList";
        LOGGER.enter(methodName, "查询空/重车数据[start], getDepartureList: " + truckPlate);
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        List<DepartureDTO> departureList = tallyReservationMapper.getDepartureList(truckPlate);
        return departureList;
    }

    @Override
    public List<DepartureDTO> getDepartureRecordList(String startDate, String endDate) {
        final String methodName = "getDepartureRecordList";
        LOGGER.enter(methodName, "查询空/重车数据记录[start], getDepartureList: ");
        LOGGER.info("查询数据成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
        List<DepartureDTO> departureList = tallyReservationMapper.getDepartureRecordList(startDate, endDate);
        return departureList;
    }

    @Override
    public String selectBh(String code) {
        return tallyReservationMapper.selectBh(code);
    }

    @Override
    public List<Map<String, Object>> getHatch(Long id) {
        return tallyReservationMapper.getHatch(id);
    }

    @Override
    public List<TallyCargoDTO> getCargoStatistics(List<Long> ids) {
        List<TallyCargoDTO> cargoStatistics = tallyReservationMapper.getCargoStatistics(ids);
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
        //获取理货记录详情信息
        TYardTallyPO tallyInfoById = tallyReservationMapper.getTallyInfoById(tallyRecordSearchDTO);
        //根据理货记录子作业过程查询主作业过程的源和目的
        Map<String, Object> process = tallyReservationMapper.getProcess(tallyInfoById.getProcessCode());
        //源:船 目的:场/岸 & 源:场/岸 目的:船 & 源:场/岸 目的: 场/岸
        boolean flag1 = SourceTargetTypeEnum._01.getCode().equals(process.get("source")) && SourceTargetTypeEnum._05.getCode().equals(process.get("destination"));
        boolean flag2 = SourceTargetTypeEnum._05.getCode().equals(process.get("source")) && SourceTargetTypeEnum._01.getCode().equals(process.get("destination")) && (!"10250005".equals(tallyInfoById.getProcessCode()));
        boolean flag3 = SourceTargetTypeEnum._05.getCode().equals(process.get("source")) && SourceTargetTypeEnum._05.getCode().equals(process.get("destination"));
        if (flag1 || flag2 || flag3 && (!"10210004".equals(tallyInfoById.getProcessCode())) && (!"10200005".equals(tallyInfoById.getProcessCode()))) {
            //装卸船倒运(有关联记录)
            //查询该理货记录的子作业过程是否有前置环节 有的话为后一环节，可直接修改
            Integer isPre = tallyReservationMapper.getIsPre(tallyInfoById.getProcessCode());
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
            List<AppTallyCoilNumDTO> numDTOList = tallyReservationMapper.getCoilNumIdList(tYardTallyPO.getId().toString());
            if (numDTOList != null && numDTOList.size() != 0) {
                //删除理货钢号关系表
                tallyReservationMapper.deleteCoilNum(tYardTallyPO.getId().toString());
                //有钢号信息。进行处理
                tallyReservationMapper.updateCoilNum(numDTOList);
            }
            saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO);
            //修改磅单表备注信息
            String weightId = tallyReservationMapper.getWeightId(tYardTallyPO.getId());
            if (StringUtils.isNotEmpty(weightId)) {
                String result = tYardTallyPO.getCoilList().stream()
                        .map(obj -> obj.getCoilNum()) // 将每个对象的属性拼接并用逗号分隔
                        .collect(Collectors.joining(","));
                result = "标号:" + result;
                tallyReservationMapper.updateWeightRemark(weightId, result);
            }
        }
    }

    @Override
    public void insertMacTally(TYardTallyMacPO tYardTallyMacPO) {
        TPrdWorkPlanPO workPlanInfo = tallyReservationMapper.getWorkPlanInfo(tYardTallyMacPO.getPlanId());
        TBusReservationPoundPO byPlanId = tallyReservationMapper.getByPlanId(String.valueOf(tYardTallyMacPO.getPlanId()));
        if(workPlanInfo == null){
            throw new BusinessRuntimeException("获取计划信息失败");
        }
        if(tYardTallyMacPO.getStorageYardNm() == null || "".equals(tYardTallyMacPO.getStorageYardNm())){
            throw new BusinessRuntimeException("请选择垛位");
        }
        //判断是开始还是结束,1开始2结束
        if ("1".equals(tYardTallyMacPO.getConfirmStatus())) {
            if("1025".equals(workPlanInfo.getProcessCode())){
                if(tYardTallyMacPO.getQuantity() == null || tYardTallyMacPO.getQuantity() == 0){
                    throw new BusinessRuntimeException("请填写件数");
                }
            }
            //查询该门机下有没有未理货的
            List<String> unTallyList = tallyReservationMapper.getUnTallyList(tYardTallyMacPO);
            if (!unTallyList.isEmpty()) {
                throw new BusinessRuntimeException("该门机正在作业中" + "【"+unTallyList.get(0)+"】");
            }
            tYardTallyMacPO.setId(snowflake.nextId());
            tYardTallyMacPO.setReservationPoundId(byPlanId.getId());
            tallyReservationMapper.insertMacTally(tYardTallyMacPO);
        }else {
            if("1026".equals(workPlanInfo.getProcessCode())){
                if(tYardTallyMacPO.getQuantity() == null || tYardTallyMacPO.getQuantity() == 0){
                    throw new BusinessRuntimeException("请填写件数");
                }
            }
            tYardTallyMacPO.setProcessCode(workPlanInfo.getProcessCode());
            tallyReservationMapper.updateMacTally(tYardTallyMacPO);
        }
    }

    @Override
    public List<Map<String, Object>> getDept() {
        return tallyReservationMapper.getDept();
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
        TYardTallyPO tallyInfoById = tallyReservationMapper.getTallyInfoById(tallyRecordSearchDTO);
        //根据理货记录子作业过程查询主作业过程的源和目的
        Map<String, Object> process = tallyReservationMapper.getProcess(tallyInfoById.getProcessCode());
        //源:船 目的:场/岸 & 源:场/岸 目的:船 & 源:场/岸 目的: 场/岸
        boolean flag1 = SourceTargetTypeEnum._01.getCode().equals(process.get("source")) && SourceTargetTypeEnum._05.getCode().equals(process.get("destination"));
        boolean flag2 = SourceTargetTypeEnum._05.getCode().equals(process.get("source")) && SourceTargetTypeEnum._01.getCode().equals(process.get("destination")) && (!"10250005".equals(tallyInfoById.getProcessCode())) && (!"10250006".equals(tallyInfoById.getProcessCode()));
        ;
        boolean flag3 = SourceTargetTypeEnum._05.getCode().equals(process.get("source")) && SourceTargetTypeEnum._05.getCode().equals(process.get("destination"));
        if (flag1 || flag2 || flag3 && (!"10210004".equals(tallyInfoById.getProcessCode())) && (!"10200005".equals(tallyInfoById.getProcessCode()))) {
            //装卸船倒运(有关联记录)
            //查询该理货记录的子作业过程是否有前置环节 有的话为后一环节，可直接删除
            Integer isPre = tallyReservationMapper.getIsPre(tallyInfoById.getProcessCode());
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
                    tallyReservationMapper.deleteNotes(po);
                    //删除理货记录(子表)
                    tallyReservationMapper.deleteNotesItem(tallyId);
                    //根据理货ID获取港存明细ID
                    List<Long> ids = tallyReservationMapper.getPortStorageDetailId(tallyId);
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
        List<AppTallyCoilNumDTO> numDTOList = tallyReservationMapper.getCoilNumIdList(tYardTallyPO.getTallyId());
        if (numDTOList != null && numDTOList.size() != 0) {
            //删除理货钢号关系表
            tallyReservationMapper.deleteCoilNum(tYardTallyPO.getTallyId());
            //有钢号信息。进行处理
            tallyReservationMapper.updateCoilNum(numDTOList);
        }
    }

    public void delete(Long tallyId) {
        TYardTallyPO po = new TYardTallyPO();
        po.setId(tallyId);
        tallyReservationMapper.deleteNotes(po);
        //删除理货记录(子表)
        tallyReservationMapper.deleteNotesItem(tallyId);
        //更新关联ID
        tallyReservationMapper.updateNotesRelationId(tallyId);
        //根据理货ID获取港存明细ID
        List<Long> ids = tallyReservationMapper.getPortStorageDetailId(tallyId);
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
        tallyReservationMapper.deleteNotes(po);
        //删除理货记录(子表)
        tallyReservationMapper.deleteNotesItem(tallyId);
        //根据理货ID获取港存明细ID
        List<Long> ids = tallyReservationMapper.getPortStorageDetailId(tallyId);
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
                String status = tallyReservationMapper.getWeightStatusNew(tallyInfoById.getWeighbridgeId().toString());
                if (status == null) {
                    throw new BusinessRuntimeException("未查询到车辆信息,不允许删除!");
                }
                if ("1".equals(status)) {
                    throw new BusinessRuntimeException("车辆已出港,不允许删除!");
                }
                tallyReservationMapper.updateWeightStatusNew(tallyInfoById.getWeighbridgeId().toString());
            }
        }

    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void updateTallyInfo(TYardTallyPO tYardTallyPO) {
        final String methodName = "tYardTallyPO";
        LOGGER.enter(methodName, "修改理货记录[start], tYardTallyPO: " + tYardTallyPO);
        //修改理货记录主表
        tallyReservationMapper.updateTally(tYardTallyPO);
        //判断作业过程的源和目的是否为场地，是的场地垛号必填
        Map<String, Object> process = tallyReservationMapper.getIsZq(tYardTallyPO.getProcessCode());
        for (TYardTallyItemPO po : tYardTallyPO.getListTallyItemList()) {
            if (SourceTargetTypeEnum._05.getCode().equals(process.get("sourceCd").toString()) || SourceTargetTypeEnum._05.getCode().equals(process.get("targetCd").toString())) {
                if (StringUtils.isEmpty(po.getStorehouseName())) {
                    throw new BusinessRuntimeException("请选择场地");
                }
                if (StringUtils.isEmpty(po.getStackPositionName())) {
                    throw new BusinessRuntimeException("请选择垛位");
                }
//                insertMiss(po);
                if (po.getCargoCode() != null) {
                    Integer isTally = tallyReservationMapper.getIsTally(po.getCargoCode());
                    if (isTally != null && isTally == 1) {
                        //根据理货ID获取港存明细ID
                        List<Long> ids = tallyReservationMapper.getPortStorageDetailItemId(po.getId());
                        ids.removeAll(Collections.singleton(null));
                        if (ids != null && !ids.isEmpty()) {
                            //删除港存
                            businessCommonService.deletePortStorageDetail(ids);
                        }
                        if ("10200005".equals(tYardTallyPO.getProcessCode()) || "10210004".equals(tYardTallyPO.getProcessCode())) {
                            //场-岸 岸-场
                            Long storehouseId = po.getStorehouseId();
                            String storehouseName = po.getStorehouseName();
                            Long locationId = po.getLocationId();
                            String locationNo = po.getLocationNo();
                            Long stackPositionId = po.getStackPositionId();
                            String stackPositionName = po.getStackPositionName();
                            tYardTallyPO.setIsCrk("201");
                            TYardTallyItemPO pos = tallyReservationMapper.getStorage(po.getId());
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
            }
            //修改理货记录子表
            tallyReservationMapper.updateTallyItem(po);
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
                Integer count = tallyReservationMapper.getRelationIdHc(tYardTallyPO);
                if (count > 0) {
                    throw new BusinessRuntimeException("该数据已理货，不能重复理货");
                }
                //关联相关理货ID
                tallyReservationMapper.updateRelationId(tYardTallyPO);
            }
        }
        //没有前置要把relationId置为空
        if (!tYardTallyPO.getIsFrontType()) {
            //指令 - 车牌号 - 作业过程 查询关联ID，理完才可继续理货
            Integer count = tallyReservationMapper.getRelationIdQy(tYardTallyPO);
            if (count > 0) {
                throw new BusinessRuntimeException("该转运机械未作业完，请稍后再理货");
            }
            if (StringUtils.isNotEmpty(tYardTallyPO.getTransportEquipmentNo())) {
                //赋值转运机械信息
                Map<String, Object> eqInfo = getEqInfo(tYardTallyPO.getPlanId(), tYardTallyPO.getTransportEquipmentNo(), "2");
                if (eqInfo != null) {
                    tYardTallyPO.setTransportOperatorsId(Long.parseLong(eqInfo.get("equipmentId").toString()));
                    if (eqInfo.get("operatorsName") != null) {
                        tYardTallyPO.setTransportOperatorsId(Long.parseLong(eqInfo.get("operatorsId").toString()));
                        tYardTallyPO.setTransportOperatorsName(eqInfo.get("operatorsName").toString());
                    }
                } else {
                    throw new BusinessRuntimeException("获取转运机械信息失败");
                }
            }
        }

        //新增主表信息
        int i = tallyReservationMapper.tally(tYardTallyPO);
        if (i != 0) {
            SysParameterDTO sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE");
            boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
            List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
            TYardMeasureSearchDTO tYardMeasureSearchDTO = new TYardMeasureSearchDTO();
            tYardMeasureSearchDTO.setTrustId(tYardTallyPO.getTrustId());
            tYardMeasureSearchDTO.setProcessCode(tYardTallyPO.getProcessCode());
            List<AppTallyLadingDTO> carDetailedList = getCarDetailedList(tYardMeasureSearchDTO);
            for (TYardTallyItemPO po : listTallyList) {
                po.setId(snowflake.nextId());
                //主表ID
                po.setTallyId(id);
//                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._03.getCode().equals(tYardTallyPO.getDestination())) {
//                    List<AppTallyLadingDTO> resultStore = carDetailedList.stream()
//                            .filter(dto -> dto.getInOutId() == po.getInOutId())
//                            .collect(Collectors.toList());
//                    if (CollectionUtils.isEmpty(resultStore)) {
//                        throw new BusinessRuntimeException("获取港存信息失败!");
//                    }
//                    AppTallyLadingDTO store = resultStore.get(0);
//                    po.setStorehouseId(store.getStorehouseId());
//                    po.setStorehouseName(store.getStorehouseName());
//                    po.setLocationId(store.getLocationId());
//                    po.setLocationNo(store.getLocationNo());
//                    po.setStackPositionId(store.getStorehouseId());
//                    po.setStackPositionName(store.getStorehouseName());
//                    //岸-车使用的剩余场存
//                    if (po.getQuantity() != null) {
//                        if (oneCarPlan) {
//                            //判断下次理货时理货数量不能超过剩余件数数量
//                            if (po.getQuantity() > store.getQuantitySurplus()) {
//                                throw new BusinessRuntimeException("理货件数大于剩余件数!");
//                            }
//                        }
//                    }
//                    if (po.getTon() != null) {
//                        if (oneCarPlan) {
//                            if (po.getTon().compareTo(store.getTonSurplus()) > 0) {
//                                throw new BusinessRuntimeException("理货重量大于剩余重量!");
//                            }
//                        }
//                    }
//                }
                if (po.getCargoInfoId() == null) {
                    //货物信息
                    po.setCargoInfoId(tYardTallyPO.getCargoInfoId());
                    po.setCargoCode(tYardTallyPO.getCargoCode());
                    po.setCargoName(tYardTallyPO.getCargoName());//货物信息
                }
                //场存节点是理货时，新增出入库记录(1.判断作业过程源或目的为场地的 2.货种节点为理货的 先1后2)
//                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
//                    if (po.getCargoCode() != null) {
//                        Integer isTally = tallyReservationMapper.getIsTally(po.getCargoCode());
//                        if (isTally != null && isTally == 1) {
//                            insertIntOutYard(po, tYardTallyPO);
//                        }
//                    }
//                }
            }
            //添加子表信息
            tallyReservationMapper.tallyItem(listTallyList);
            //附件保存
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            sysFileService.saveFileBusRelation(fileIds, id);

        } else {
            throw new BusinessRuntimeException("理货失败");
        }
        LOGGER.info("理货成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }

    public Map<String, Object> getEqInfo(Long planId, String equipmentNo, String type) {
        //根据计划id查询作业机械
        TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO = new TPrdDispatchExtrinsicDTO();
        tPrdDispatchSecondarySearchDTO.setPlanId(planId);
        List<Map<String, Object>> mechanics = new ArrayList<>();
        if ("1".equals(type)) {
            mechanics = tallyReservationMapper.getMechanics(tPrdDispatchSecondarySearchDTO);
        } else {
            mechanics = tallyReservationMapper.getTransfer(tPrdDispatchSecondarySearchDTO);
        }
        List<Map<String, Object>> filteredMechanics = mechanics.stream()
                .filter(mechanic -> {
                    String equipmentTypeName = (String) mechanic.get("equipmentNo");
                    return equipmentTypeName.equals(equipmentNo);
                })
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(filteredMechanics)) {
            return filteredMechanics.get(0);
        } else {
            return null;
        }
    }


    public void saveCoilList(List<AppTallyCoilNumDTO> list, TYardTallyPO tYardTallyPO) {

        for (AppTallyCoilNumDTO dto : list) {
            if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination()) && dto.getStatus() == 20) {
                throw new BusinessRuntimeException("卷钢号为" + dto.getCoilNum() + "的已经入库，请勿重新入库");
            } else if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && dto.getStatus() == 30) {
                throw new BusinessRuntimeException("卷钢号为" + dto.getCoilNum() + "的已经出库，请勿重新出库");
            }
            dto.setTallyId(tYardTallyPO.getId());
            //  船/车 ==> 场/岸  #### 入库
            if (
                ( SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._03.getCode().equals(tYardTallyPO.getSource()) )
                &&
                ( SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination()) || SourceTargetTypeEnum._06.getCode().equals(tYardTallyPO.getDestination()) )
            )
            {
                dto.setStatus(20);
            }
            else if(
                ( SourceTargetTypeEnum._06.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource())  )
                &&
                ( SourceTargetTypeEnum._01.getCode().equals(tYardTallyPO.getDestination()) || SourceTargetTypeEnum._03.getCode().equals(tYardTallyPO.getDestination()) )
            )
            {
                dto.setStatus(30);// 岸/场 => 船/车  #### 出库
            }
        }
        tallyReservationMapper.updateCoilList(list);
        //理货标号关系表
        tallyReservationMapper.insertCoilList(list);
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
            Integer count = tallyReservationMapper.getRelationIdHc(tYardTallyPO);
            if (count > 0) {
                throw new BusinessRuntimeException("该数据已理货，不能重复理货");
            }
            //关联相关理货ID
            tallyReservationMapper.updateRelationId(tYardTallyPO);
        }
        //没有前置要把relationId置为空
        if (!tYardTallyPO.getIsFrontType()) {
            //指令 - 车牌号 - 作业过程 查询关联ID，理完才可继续理货
            Integer count = tallyReservationMapper.getRelationIdQy(tYardTallyPO);
            if (count > 0) {
                throw new BusinessRuntimeException("该转运机械未作业完，请稍后再理货");
            }
            //tYardTallyPO.setRelationId(null);
        }
        //新增主表信息
        int i = tallyReservationMapper.tally(tYardTallyPO);
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
                        Integer isTally = tallyReservationMapper.getIsTally(po.getCargoCode());
                        if (isTally != null && isTally == 1) {
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
                        }
                    }
                }
            }
            //添加子表信息
            tallyReservationMapper.tallyItem(listTallyList);
            //附件保存
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            // 附件保存
            sysFileService.saveFileBusRelation(fileIds, id);
            if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
                saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO);
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
//            tallyReservationMapper.insertStackPosition(map);
//    }
    public void insertMiss(TYardTallyItemPO po) {
        //查询该场地下是否有该输入垛号名称
        List<Long> list = tallyReservationMapper.getIsStackPosition(po);
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
            tallyReservationMapper.insertStackPosition(map);
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
        tYardTallyPO.setLoginUserId(2L);
        tYardTallyPO.setLoginUserName("外理");
        if (tYardTallyPO.getNoteId() == null) {
            throw new BusinessRuntimeException("磅单id为空");
        }
        tYardTallyPO.setWeighbridgeId(tYardTallyPO.getNoteId());
        tYardTallyPO.setTransportEquipmentNo(tYardTallyPO.getTruckNo());
        //根据磅单ID获取磅单数据
        TYardTallyPO weightInfoPO = tallyReservationMapper.getWeightInfo(tYardTallyPO);
        if (!ObjectUtils.isEmpty(weightInfoPO)) {
            tYardTallyPO.setTsptId(weightInfoPO.getTsptId());
            tYardTallyPO.setPlanNo(weightInfoPO.getPlanNo());
            tYardTallyPO.setWorkErWeiId(weightInfoPO.getWorkErWeiId());

            tYardTallyPO.setCargoCode(weightInfoPO.getCargoCode());
            tYardTallyPO.setCargoName(weightInfoPO.getCargoName());
            tYardTallyPO.setCargoInfoId(weightInfoPO.getCargoInfoId());
        } else {
            if (tYardTallyPO.getWeighbridgeId() != null) {
                throw new BusinessRuntimeException("获取磅单信息失败,请刷新列表重试");
            }
        }
        //验重
        int count = tallyReservationMapper.getIsBdTally(tYardTallyPO);
        if (count > 0) {
            throw new BusinessRuntimeException("该车辆已理货,不能重复理货");
        }
        //操作地磅表
//        if (tYardTallyPO.getWeighbridgeId() != null) {
//            SpringUtils.getBean(this.getClass()).updateWeightNew(tYardTallyPO);
//        }
        //新增主表信息
        int i = tallyReservationMapper.tally(tYardTallyPO);
        if (i != 0) {
            SysParameterDTO sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE");
            boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
            List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
            TYardMeasureSearchDTO tYardMeasureSearchDTO = new TYardMeasureSearchDTO();
            tYardMeasureSearchDTO.setTrustId(tYardTallyPO.getTrustId());
            tYardMeasureSearchDTO.setProcessCode(tYardTallyPO.getProcessCode());
            List<AppTallyLadingDTO> carDetailedList = getCarDetailedList(tYardMeasureSearchDTO);
            for (TYardTallyItemPO po : listTallyList) {
                po.setId(snowflake.nextId());
                //主表ID
                po.setTallyId(id);
                po.setLoginUserId(2L);
                po.setLoginUserName("外理");
                if (po.getCargoInfoId() == null) {
                    //货物信息
                    po.setCargoInfoId(tYardTallyPO.getCargoInfoId());
                    po.setCargoCode(tYardTallyPO.getCargoCode());
                    po.setCargoName(tYardTallyPO.getCargoName());//货物信息
                }
                po.setTrustCargoInfoId(tYardTallyPO.getTrustCargoInfoId());
            }
            //添加子表信息
            tallyReservationMapper.tallyItem(listTallyList);
//            //附件
//            List<Long> fileIds = new ArrayList<>();
//            for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
//                fileIds.add(file.getId());
//            }
//            // 附件保存
//            sysFileService.saveFileBusRelation(fileIds, id);
        } else {
            throw new BusinessRuntimeException("理货失败");
        }
        LOGGER.info("理货成功");
        LOGGER.exit(methodName, "业务数据同步服务[end]");
    }

    @DS("simeauto")
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateWeight(TYardTallyPO tYardTallyPO) {
        tallyReservationMapper.updateWeight(tYardTallyPO);
    }

    public void updateWeightNew(TYardTallyPO tYardTallyPO) {
        int i = tallyReservationMapper.updateWeightNew(tYardTallyPO);
        if (i == 0) {
            throw new BusinessRuntimeException("未找到该磅单信息,请刷新列表重试");
        }
    }

    @DS("simeauto")
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateWeightStatus(String id) {
        tallyReservationMapper.updateWeightStatus(id);
    }

    @DS("simeauto")
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public String getWeightStatus(String id) {
        return tallyReservationMapper.getWeightStatus(id);
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
                Integer count = tallyReservationMapper.getRelationIdHc(tYardTallyPO);
                if (count > 0) {
                    throw new BusinessRuntimeException("该数据已理货，不能重复理货");
                }
                //关联相关理货ID
                tallyReservationMapper.updateRelationId(tYardTallyPO);
            }
            //没有前置要把relationId置为空
            if (!tYardTallyPO.getIsFrontType()) {
                //指令 - 车牌号 - 作业过程 查询关联ID，理完才可继续理货
                Integer count = tallyReservationMapper.getRelationIdQy(tYardTallyPO);
                if (count > 0) {
                    throw new BusinessRuntimeException("该转运机械未作业完，请稍后再理货");
                }
                if (StringUtils.isNotEmpty(tYardTallyPO.getTransportEquipmentNo())) {
                    //赋值转运机械信息
                    Map<String, Object> eqInfo = getEqInfo(tYardTallyPO.getPlanId(), tYardTallyPO.getTransportEquipmentNo(), "2");
                    if (eqInfo != null) {
                        tYardTallyPO.setTransportOperatorsId(Long.parseLong(eqInfo.get("equipmentId").toString()));
                        if (eqInfo.get("operatorsName") != null) {
                            tYardTallyPO.setTransportOperatorsId(Long.parseLong(eqInfo.get("operatorsId").toString()));
                            tYardTallyPO.setTransportOperatorsName(eqInfo.get("operatorsName").toString());
                        }
                    } else {
                        throw new BusinessRuntimeException("获取转运机械信息失败");
                    }
                }
            }
        }
        //新增主表信息
        int i = tallyReservationMapper.tally(tYardTallyPO);
        if (i != 0) {
            SysParameterDTO sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE");
            boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
            List<TYardTallyItemPO> listTallyList = tYardTallyPO.getListTallyItemList();
            TYardMeasureSearchDTO tYardMeasureSearchDTO = new TYardMeasureSearchDTO();
            tYardMeasureSearchDTO.setTrustId(tYardTallyPO.getTrustId());
            tYardMeasureSearchDTO.setProcessCode(tYardTallyPO.getProcessCode());
            List<AppTallyLadingDTO> carDetailedList = getCarDetailedList(tYardMeasureSearchDTO);
            for (TYardTallyItemPO po : listTallyList) {
                po.setId(snowflake.nextId());
                //主表ID
                po.setTallyId(id);

                //没有默认件数和重量，赋值出库件数和重量
//                if (!tYardTallyPO.getIsFrontType()) {
//                    List<AppTallyLadingDTO> resultStore = carDetailedList.stream()
//                            .filter(dto -> dto.getInOutId() == po.getInOutId())
//                            .collect(Collectors.toList());
//                    if (CollectionUtils.isEmpty(resultStore)) {
//                        throw new BusinessRuntimeException("获取港存信息失败!");
//                    }
//                    AppTallyLadingDTO store = resultStore.get(0);
//                    po.setStorehouseId(store.getStorehouseId());
//                    po.setStorehouseName(store.getStorehouseName());
//                    po.setLocationId(store.getLocationId());
//                    po.setLocationNo(store.getLocationNo());
//                    po.setStackPositionId(store.getStorehouseId());
//                    po.setStackPositionName(store.getStorehouseName());
//                    //场-车
//                    if (po.getQuantity() != null) {
//                        if (oneCarPlan) {
//                            //判断下次理货时理货数量不能超过剩余件数数量
//                            if (po.getQuantity() > store.getQuantitySurplus()) {
//                                throw new BusinessRuntimeException("理货件数大于剩余件数!");
//                            }
//                        }
//                    }
//                    if (po.getTon() != null) {
//                        if (oneCarPlan) {
//                            if (po.getTon().compareTo(store.getTonSurplus()) > 0) {
//                                throw new BusinessRuntimeException("理货重量大于剩余重量!");
//                            }
//                        }
//                    }
//                }
                //场存节点是理货时，新增出入库记录(1.判断作业过程源或目的为场地的 2.货种节点为理货的 先1后2)
//                if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) || SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
//                    if (po.getCargoCode() != null) {
//                        Integer isTally = tallyReservationMapper.getIsTally(po.getCargoCode());
//                        if (isTally != null && isTally == 1) {
//                            if (SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getSource()) && SourceTargetTypeEnum._05.getCode().equals(tYardTallyPO.getDestination())) {
//                                //场-岸 岸-场 先出 后入
//                                tYardTallyPO.setIsCrk("201");
//                                insertIntOutYard(po, tYardTallyPO);
//                                tYardTallyPO.setIsCrk("101");
//                                if (po.getStorehouseTargetId() == null) {
//                                    throw new BusinessRuntimeException("请选择目标场地");
//                                }
//                                po.setStorehouseId(po.getStorehouseTargetId()); //库场
//                                po.setStorehouseName(po.getStorehouseTargetName());
//                                po.setLocationId(po.getLocationTargetId());
//                                po.setLocationNo(po.getLocationTargetNo());
//                                po.setStackPositionId(po.getStackPositionTargetId());
//                                po.setStackPositionName(po.getStackPositionTargetName());
//                            }
//                            insertIntOutYard(po, tYardTallyPO);
//
//
//                        }
//                    }
//                }
            }
            //添加子表信息
            tallyReservationMapper.tallyItem(listTallyList);

            //附件保存
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            sysFileService.saveFileBusRelation(fileIds, id);

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
        //根据磅单ID获取磅单数据
        TYardTallyPO weightInfoPO = tallyReservationMapper.getWeightInfo(tYardTallyPO);
        if (!ObjectUtils.isEmpty(weightInfoPO)) {
            tYardTallyPO.setTsptId(weightInfoPO.getTsptId());
            tYardTallyPO.setPlanNo(weightInfoPO.getPlanNo());
            tYardTallyPO.setWorkErWeiId(weightInfoPO.getWorkErWeiId());
        } else {
            throw new BusinessRuntimeException("获取磅单信息失败,请刷新列表重试");
        }
        //验重
        int count = tallyReservationMapper.getIsBdTally(tYardTallyPO);
        if (count > 0) {
            throw new BusinessRuntimeException("该车辆已理货,不能重复理货");
        }

        //新增主表信息
        int i = tallyReservationMapper.tally(tYardTallyPO);
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
                        Integer isTally = tallyReservationMapper.getIsTally(po.getCargoCode());
                        if (isTally != null && isTally == 1) {
                            insertIntOutYard(po, tYardTallyPO);
                        }
                    }
                }
            }
            //添加子表信息
            tallyReservationMapper.tallyItem(listTallyList);
            //附件保存
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file : tYardTallyPO.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            // 附件保存
            sysFileService.saveFileBusRelation(fileIds, id);
            //操作地磅表
//            if (tYardTallyPO.getWeighbridgeId() != null) {
//
//                SpringUtils.getBean(this.getClass()).updateWeightNew(tYardTallyPO);
//            }
            String result = "";
            if (tYardTallyPO.getCoilList() != null && tYardTallyPO.getCoilList().size() != 0) {
                saveCoilList(tYardTallyPO.getCoilList(), tYardTallyPO);
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
        List<MWorkProcessPO> workProcessList = tallyReservationMapper.getMWorkProcessList();

        // 查询作业通知单计划量
        Map<String, Object> trustPlanMap = tallyReservationMapper.getTrustPlan(trustId);
        resultMap.put("trustPlan", trustPlanMap);

        // 查询票货信息
        List<Map<String, Object>> cargoInfoList = tallyReservationMapper.getCargoInfoList(trustId, cargoInfoNo);
        for (Map<String, Object> cargoInfo : cargoInfoList) {
            Long cargoInfoId = Long.parseLong(cargoInfo.get("cargoInfoId").toString());

            // 查询集、疏港
            Map<String, Object> inOutPortWorkData = tallyReservationMapper.getProgressWorkData(cargoInfoId,
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
            Map<String, Object> zqWorkData = tallyReservationMapper.getProgressWorkData(cargoInfoId,
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
            Map<String, Object> dyWorkData = tallyReservationMapper.getProgressWorkData(cargoInfoId,
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
            Map<String, Object> luoWorkData = tallyReservationMapper.getProgressWorkData(cargoInfoId,
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
            Map<String, Object> caWorkData = tallyReservationMapper.getProgressWorkData(cargoInfoId,
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
                Map<String, Object> inOutPortWorkTimeData = tallyReservationMapper.getProgressWorkData(cargoInfoId,
                        workProcessList.stream()
                                .filter((MWorkProcessPO po) -> "10".equals(po.getTallyDataStat()))
                                .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), workDate, classCode);
                if (inOutPortWorkTimeData != null) {
                    cargoInfo.put("workTimeInOutPortQuantity", inOutPortWorkTimeData.get("quantity"));
                    cargoInfo.put("workTimeInOutPortTon", inOutPortWorkTimeData.get("ton"));
                    cargoInfo.put("workTimeInOutPortCars", inOutPortWorkTimeData.get("cars"));
                }

                // 查询本班作业量-直取装、卸船
                Map<String, Object> zqWorkTimeData = tallyReservationMapper.getProgressWorkData(cargoInfoId,
                        workProcessList.stream()
                                .filter((MWorkProcessPO po) -> "20".equals(po.getTallyDataStat()))
                                .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), workDate, classCode);
                if (zqWorkTimeData != null) {
                    cargoInfo.put("workTimeZqQuantity", zqWorkTimeData.get("quantity"));
                    cargoInfo.put("workTimeZqTon", zqWorkTimeData.get("ton"));
                    cargoInfo.put("workTimeZqCars", zqWorkTimeData.get("cars"));
                }

                // 查询本班作业量-倒运装、卸船
                Map<String, Object> dyWorkTimeData = tallyReservationMapper.getProgressWorkData(cargoInfoId,
                        workProcessList.stream()
                                .filter((MWorkProcessPO po) -> "30".equals(po.getTallyDataStat()))
                                .collect(Collectors.toList()).stream().map(MWorkProcessPO::getProcessCd).collect(Collectors.toList()), workDate, classCode);
                if (dyWorkTimeData != null) {
                    cargoInfo.put("workTimeDyQuantity", dyWorkTimeData.get("quantity"));
                    cargoInfo.put("workTimeDyTon", dyWorkTimeData.get("ton"));
                    cargoInfo.put("workTimeDyCars", dyWorkTimeData.get("cars"));
                }

                // 查询本班作业量-落地装船
                Map<String, Object> luoWorkTimeData = tallyReservationMapper.getProgressWorkData(cargoInfoId,
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

        Map<String, Object> resultMap = tallyReservationMapper.getTransportEquipmentCargoInfo(trustId, vehicleNo);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return resultMap;
    }

    @Override
    public Map<String, Object> getTransportEquipmentCargoInfoZ(Long trustId, String vehicleNo) {
        final String methodName = "TallyServiceImpl:getTransportEquipmentCargoInfoZ";
        LOGGER.enter(methodName, "业务执行");
        Map<String, Object> resultMap = tallyReservationMapper.getTransportEquipmentCargoInfoZ(trustId, vehicleNo);

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
        List<Long> idList = tallyReservationMapper.getNoteTally(departureDTO.getNoteId());
        if (idList != null && idList.size() != 0) {
            for (Long id : idList) {
                TYardTallyPO po = new TYardTallyPO();
                po.setId(id);
                //删除理货记录主表
                int i = tallyReservationMapper.deleteNotes(po);
                if (i != 0) {
                    //删除理货记录(子表)
                    tallyReservationMapper.deleteNotesItem(id);
                    //根据理货ID获取港存明细ID
                    List<Long> ids = tallyReservationMapper.getPortStorageDetailId(id);
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
        tallyReservationMapper.tallyDepartureSub(pos);
        TYardTallyItemPO tYardTallyItemPO = new TYardTallyItemPO();
        tYardTallyItemPO.setId(snowflake.nextId());
        tYardTallyItemPO.setTallyId(newId);
        //设置票货信息
        List<Map<String, Object>> map = tallyReservationMapper.getCargoDeparture(departureDTO.getPlanNo());
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
        tallyReservationMapper.tallyItem(list);
        //更新磅单理货状态
        tallyReservationMapper.updateDeparture(departureDTO.getNoteId());
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
        tallyReservationMapper.deleteNotes(tYardTallyPO);
        int i = tallyReservationMapper.deleteNotesItem(tYardTallyPO.getId());
        if (i != 0) {
            //根据磅单ID查询磅单状态
            TYardTallyPO weightInfo = tallyReservationMapper.getWeightInfo(tYardTallyPO);
            if (weightInfo == null) {
                throw new BusinessRuntimeException("查询磅单信息失败");
            } else {
                if ("1".equals(weightInfo.getIsFinished())) {
                    throw new BusinessRuntimeException("该车辆已出港，不允许撤销");
                }
                tallyReservationMapper.updateWeightRevoke(tYardTallyPO);
            }
        } else {
            throw new BusinessRuntimeException("删除理货记录失败");
        }
    }


    @DS("simeauto")
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void operateWeight(DepartureDTO departureDTO) {
        //老地磅
        tallyReservationMapper.updateWeightInfo(departureDTO);
        tallyReservationMapper.insertEmptyTruck(departureDTO);
    }

    @Override
    public List<Map<String, Object>> listDisShipVoyageApp() {
        return tallyReservationMapper.listDisShipVoyageApp();
    }

    @Override
    public void tallyConfirm(TYardTallyPO tYardTallyPO) {
        TPrdWorkPlanPO workPlanInfo = tallyReservationMapper.getWorkPlanInfo(tYardTallyPO.getPlanId());
        if (workPlanInfo == null) {
            throw new BusinessRuntimeException("获取作业计划信息失败");
        }
        tYardTallyPO.setWorkDate(workPlanInfo.getWorkDate());
        tYardTallyPO.setClassCode(workPlanInfo.getClassCode());
        tYardTallyPO.setClassName(workPlanInfo.getClassName());
        tYardTallyPO.setTrustId(workPlanInfo.getTrustId());
        tYardTallyPO.setShipvoyageId(workPlanInfo.getShipvoyageId());
        tYardTallyPO.setShipvoyageItemId(workPlanInfo.getShipvoyageItemId());
        tYardTallyPO.setCompanyId(workPlanInfo.getCompanyId());
        tYardTallyPO.setCompanyName(workPlanInfo.getCompanyName());

        //赋值作业机械信息
        Map<String, Object> eqInfo = getEqInfo(tYardTallyPO.getPlanId(), tYardTallyPO.getEquipmentNo(), "1");
        if (eqInfo != null) {
            tYardTallyPO.setEquipmentId(Long.parseLong(eqInfo.get("equipmentId").toString()));
            if (eqInfo.get("operatorsName") != null) {
                tYardTallyPO.setOperatorsId(Long.parseLong(eqInfo.get("operatorsId").toString()));
                tYardTallyPO.setOperatorsName(eqInfo.get("operatorsName").toString());
            }

        } else {
            throw new BusinessRuntimeException("获取作业机械信息失败");
        }

        //判断磅单ID是否为空,不为空的为直取的
        if (tYardTallyPO.getNoteId() != null) {
            //根据磅单ID获取磅单


        } else {
            throw new BusinessRuntimeException("数据有误，未获取到过磅记录!");
        }

//        Map<String, Object> process = tallyReservationMapper.getProcess(tYardTallyPO.getProcessCode());
//        if (process == null) {
//            throw new BusinessRuntimeException("获取主作业过程信息失败");
//        }
//        Map<String, Object> processDetailInfo = tallyReservationMapper.getIsFrontType(tYardTallyPO.getProcessCode());
//        tYardTallyPO.setDestination(processDetailInfo.get("destination").toString());
//        tYardTallyPO.setSource(processDetailInfo.get("source").toString());
//        tYardTallyPO.setIsFrontType("1".equals(processDetailInfo.get("isPreProcess").toString()) ? true : false);
        List<TYardTallyItemPO> listTallyItemList = tYardTallyPO.getListTallyItemList();
        if (CollectionUtils.isEmpty(listTallyItemList)) {
            throw new BusinessRuntimeException("理货明细不能为空");
        }
//        Integer quantity = 0;
//        BigDecimal ton = new BigDecimal(0);
        for (TYardTallyItemPO po : listTallyItemList) {

        }
//        tYardTallyPO.setQuantity(quantity);
//        tYardTallyPO.setTon(ton);

//        if (SourceTargetTypeEnum._01.getCode().equals(process.get("source")) && SourceTargetTypeEnum._05.getCode().equals(process.get("destination"))) {
//            //船-场
//            tally(tYardTallyPO);
//        } else if (SourceTargetTypeEnum._05.getCode().equals(process.get("source")) && SourceTargetTypeEnum._01.getCode().equals(process.get("destination"))) {
//            //场-船
//            tallyChangChuan(tYardTallyPO);
//        } else {
            //直取
            tallyCheChuan(tYardTallyPO);
//        }

    }

    @Override
    public void updateStatus(TBusReservationPoundPO tBusReservationPoundPO) {
        if("2".equals(tBusReservationPoundPO.getWorkStatus())){
            tallyReservationMapper.updateStatus2(tBusReservationPoundPO);
        }else {
            tBusReservationPoundPO.setId(snowflake.nextId());
            tallyReservationMapper.updateStatus(tBusReservationPoundPO);
        }
    }

    @Override
    public TYardTallyMacPO getNewDoorById(String planId, String macName) {
        return tallyReservationMapper.getNewDoorById(planId,macName);
    }
}
