package com.yy.ppm.produce.service.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.api.client.util.Lists;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.PinYin4jUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.concurrent.DistributedLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.appWork.mapper.TallyMapper;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.common.enums.*;
import com.yy.ppm.common.mapper.PublicMapper;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.master.bean.dto.MWorkScheduleDTO;
import com.yy.ppm.master.bean.po.MPieceWorkTeamPO;
import com.yy.ppm.master.bean.po.StorageYardPO;
import com.yy.ppm.master.mapper.MWorkScheduleMapper;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanLocationDTO;
import com.yy.ppm.produce.bean.dto.workTicket.*;
import com.yy.ppm.produce.bean.po.*;
import com.yy.ppm.produce.mapper.TPrdWorkPlanMapper;
import com.yy.ppm.produce.mapper.TPrdWorkTicketMapper;
import com.yy.ppm.produce.service.TPrdWorkTicketService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 15:37
 */
@Service
public class TPrdWorkTicketServiceImpl implements TPrdWorkTicketService {
    private static final MicroLogger LOGGER = new MicroLogger(TPrdWorkTicketService.class);

    @Autowired
    private TPrdWorkTicketMapper tPrdWorkTicketMapper;

    @Resource
    private MWorkScheduleMapper mWorkScheduleMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private SysParameterMapper sysParameterMapper;

    @Autowired
    private BusinessCommonService businessCommonService;

    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private TallyMapper tallyMapper;

    @Resource
    private TPrdWorkPlanMapper tPrdWorkPlanMapper;

    @Resource
    private TBusTrustMapper tBusTrustMapper;

    @Resource
    public PublicMapper publicMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;
    /**
     * 场地类型：垛位
     */
    private static final String STORAGE_YARD_LEVEL_MASS = "3";

    private static final int CURSOR_LIMIT = 5_000;

    @Override
    public List<TPrdWorkPlanDTO> listWorkPlan(TPrdWorkPlanQuery searchDTO) {
        //查询该用户是否是调度室和库场部的值班主任
        Integer flag = tallyMapper.getIsDept(securityUtils.getLoginUserId());
        Integer admin = tallyMapper.getAdmin(securityUtils.getLoginUserId());
        if(admin != 0){
            flag = flag + admin;
        }
        searchDTO.setFlag(flag);
        searchDTO.setLoginId(securityUtils.getLoginUserId() + "");
        List<TPrdWorkPlanDTO> list = tPrdWorkTicketMapper.listWorkPlan(searchDTO);
        if (!CollectionUtils.isEmpty(list)) {
            Iterator<TPrdWorkPlanDTO> iter = list.iterator();
            while (iter.hasNext()) {
                TPrdWorkPlanDTO po = iter.next();
                if (po.getCargoOwnerId() != null) {
                    List<String> idLists = Arrays.asList(po.getCargoOwnerId().split(","));
                    Integer count = tPrdWorkTicketMapper.getIsStations(idLists);
                    if (count > 0) {
                        //有场站的删除
                        iter.remove();
                    }
                }

            }
        }
        List<TPrdWorkPlanDTO> resultList = Lists.newArrayList();
        // 查询配工状态 add by zcc 2023/11/04
        List<Long> idList = list.stream().map(TPrdWorkPlanDTO::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList) && !StringUtils.isEmpty(searchDTO.getDeptCode())) {
            // 查询计划作业的港区，根据作业通知单中的场地安排来确定
            List<Map<String, Object>> workAreaList = tPrdWorkPlanMapper.getWorkCompanyCode(idList);

            for (TPrdWorkPlanDTO data : list) {
                for (Map<String, Object> workArea : workAreaList) {
                    if (data.getId().toString().equals(workArea.get("id").toString())
                            && searchDTO.getDeptCode().equals("DGZYGS")
                            && workArea.get("workAreaCd").toString().equals("03")
                    ) {
                        resultList.add(data);
                        break;
                    }
                    if (data.getId().toString().equals(workArea.get("id").toString())
                            && searchDTO.getDeptCode().equals("ZGZYGS")
                            && workArea.get("workAreaCd").toString().equals("02")
                    ) {
                        resultList.add(data);
                        break;
                    }
                    if (data.getId().toString().equals(workArea.get("id").toString())
                            && searchDTO.getDeptCode().equals("XGZYGS")
                            && workArea.get("workAreaCd").toString().equals("01")
                    ) {
                        resultList.add(data);
                        break;
                    }
                }
            }
        } else {
            resultList.addAll(list);
        }

        if (!CollectionUtils.isEmpty(idList)) {
            List<Map<String, Object>> flowStatusList = tPrdWorkPlanMapper.getFlowStatus(idList);
            List<Map<String, Object>> fixedStatusList = tPrdWorkPlanMapper.getFixedStatus(idList);
            List<Map<String, Object>> laborStatusList = tPrdWorkPlanMapper.getLaborStatus(idList);

            for (TPrdWorkPlanDTO data : resultList) {

                for (Map<String, Object> flowStatus : flowStatusList) {
                    if (data.getId().toString().equals(flowStatus.get("id").toString())) {
                        data.setFlowStatus("1");
                        break;
                    }
                }
                for (Map<String, Object> fixedStatus : fixedStatusList) {
                    if (data.getId().toString().equals(fixedStatus.get("id").toString())) {
                        data.setFixedStatus("1");
                        break;
                    }
                }
                for (Map<String, Object> laborStatus : laborStatusList) {
                    if (data.getId().toString().equals(laborStatus.get("id").toString())) {
                        data.setLaborStatus("1");
                        break;
                    }
                }
            }
        }
        List<Long> ids = list.stream().map(TPrdWorkPlanDTO::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(ids)) {
            searchDTO.setIds(ids);
            //  位置
            List<TPrdWorkPlanLocationDTO> workPlanLocationList = tPrdWorkTicketMapper.getWorkPlanLocationList(searchDTO);
            Map<String, List<String>> tmpLoctionMap = workPlanLocationList.stream().collect(Collectors.groupingBy(
                    o -> o.getWorkPlanId() + "/" + o.getDirection(),
                    Collectors.mapping(o -> o.getStorehouseName() + "/" + o.getRegionName() + "/" + o.getMassName(),
                            Collectors.toList())));

            HashMap<String, String> locationNameMap = new HashMap<>();
            if (!org.springframework.util.CollectionUtils.isEmpty(tmpLoctionMap)) {
                tmpLoctionMap.forEach((k, v) -> {
                    String massNames = "";
                    if (!org.springframework.util.CollectionUtils.isEmpty(v)) {
                        for (int i = 0; i < v.size(); i++) {
                            if (i == 0) {
                                massNames = v.get(i);
                            } else {
                                massNames += " , " + v.get(i);
                            }
                        }
                    }
                    locationNameMap.put(k, massNames);
                });
            }
            for (TPrdWorkPlanDTO o : list) {
                //处理审核状态
                if ("已签票".equals(o.getIsSigned())) {
                    String status = tPrdWorkTicketMapper.getTicketStatus(o.getId(), "1");
                    if ("已审核".equals(status)) {
                        o.setIsSigned("已审核");
                    }
                }
                if ("已签票".equals(o.getIsSignedKc())) {
                    String status = tPrdWorkTicketMapper.getTicketStatus(o.getId(), "2");
                    if ("已审核".equals(status)) {
                        o.setIsSignedKc("已审核");
                    }
                }
                //场地
                if (!org.springframework.util.CollectionUtils.isEmpty(locationNameMap)) {
                    //前沿
                    if (!org.springframework.util.StringUtils.isEmpty(locationNameMap.get((o.getId() + "/1")))
                    ) {
                        o.setMassNamesSource(locationNameMap.get((o.getId() + "/1")));
                    }//后沿
                    if (!org.springframework.util.StringUtils.isEmpty(locationNameMap.get((o.getId() + "/2")))) {
                        o.setMassNamesTarget(locationNameMap.get((o.getId() + "/2")));
                    }
                }

            }
        }

        //集疏港计划回显船名航次
        if ("2".equals(searchDTO.getPlanType())) {
            for (TPrdWorkPlanDTO dto : list) {
                if ("2".equals(dto.getPlanType())) {
                    //集疏港计划显示船名
                    List<Map<String, Object>> listSh = tallyMapper.getShipName(dto.getTrustId());
                    if (listSh != null && listSh.size() != 0) {
                        if (listSh.get(0).get("shipNameVoyage").toString() != null)
                            dto.setShipvoyageLabel(listSh.get(0).get("shipNameVoyage").toString());
                        if (listSh.get(0).get("scn") != null) {
                            dto.setScn(listSh.get(0).get("scn").toString());
                        }

                    }

                }
            }
        }

        return list;
    }

    @Override
    public List<TBusCargoInfoDTO> listTrustCargo(Long workPlanId) {
        return tPrdWorkTicketMapper.listTrustCargo(workPlanId);
    }

    @Override
    public List<TBusCargoInfoDTO> listTargetCargo(Long cargoInfoId) {
        return Collections.emptyList();
    }

    @Override
    public List<MPieceWorkTeamPO> listPieceWorkTeam(MPieceWorkTeamPO query) {
        return tPrdWorkTicketMapper.listPieceWorkTeam(query);
    }

    @Override
    public List<TPrdWorkPlanLocationPO> listWorkPlanLocation(Long workPlanId) {
        return tPrdWorkTicketMapper.listWorkPlanLocation(workPlanId);
    }

    @Override
    public List<TPrdDispatchSecondaryPO> listLabor(Long workPlanId) {
        return tPrdWorkTicketMapper.listLabor(workPlanId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void insertWorkTicket(TPrdWorkTicketDTO workTicket) {
//        List<TPrdWorkTicketDTO> workTicketDTOS = tPrdWorkTicketMapper.getTicket(workTicket.getWorkPlanId(),workTicket.getType());
//        for (TPrdWorkTicketDTO workTicketDTO : workTicketDTOS) {
//            if("20".equals(workTicketDTO.getWorkTicketStatus())){
//                throw new BusinessRuntimeException("请先取消签票审核再修改");
//            }
//        }
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workTicket.getWorkPlanId())
                .build().run(() -> {
            TPrdWorkPlanPO workPlan = tPrdWorkTicketMapper.getWorkPlan(workTicket.getWorkPlanId());
            if (!DispatchEnum.WorkPlanTypeEnum.SUNDRY.getCode().equals(workPlan.getPlanType())) {
                workTicket.getDetails().forEach(v1 -> {
                    if (v1.getTrustId() == null) {
                        throw new BusinessRuntimeException("指令ID不能为空");
                    }
                    if (v1.getTrustCargoInfoId() == null) {
                        throw new BusinessRuntimeException("指令票货ID不能为空");
                    }
                    if (v1.getCargoInfoId() == null) {
                        throw new BusinessRuntimeException("票货ID不能为空");
                    }
                    if (StringUtils.isEmpty(v1.getCabinNo()) && ("下舱".equals(v1.getProcessDetailName()))) {
                        throw new BusinessRuntimeException("下舱作业过程，请选择舱口");
                    }
                    if (v1.getTon() == null && (!"下舱".equals(v1.getProcessDetailName()))) {
                        throw new BusinessRuntimeException("吨数（数量）不能为空");
                    }
                    if (!"下舱".equals(v1.getProcessDetailName())) {
                        if (v1.getTon().compareTo(BigDecimal.ZERO) < 0) {
                            throw new BusinessRuntimeException("吨数（数量）不能小于0");
                        }
                    }
//                    if (!CollectionUtils.isEmpty(v1.getCabinNoList())) {
//                        v1.setCabinNo(String.join(",", v1.getCabinNoList()));
//                    } else {
//                        v1.setCabinNo(null);
//                    }
                    Integer cargoFlag = tPrdWorkTicketMapper.getCargoIsUpdate(v1.getCargoCode());
                    if (cargoFlag != null && cargoFlag == 2) {
                        //校验场地
                        Map<String, Object> processFlag = tPrdWorkTicketMapper.getProcessIsUpdate(v1.getProcessDetailCode());
                        if (processFlag != null && "1".equals(processFlag.get("updatePoint").toString())) {
                            if ("201".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
                                //出库
                                if (v1.getStorehouseIdSource() == null) {
                                    throw new BusinessRuntimeException("请填写作业过程为" + v1.getProcessDetailName() + "的起始区域信息");
                                }
                            }
                            if ("101".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
                                //入库
                                if (v1.getStorehouseIdTarget() == null) {
                                    throw new BusinessRuntimeException("请填写作业过程为" + v1.getProcessDetailName() + "的终点区域信息");
                                }
                            }
                        }
                    }


                });
            } else {
                workTicket.getDetails().forEach(v1 -> {
                    if (v1.getStartTime() == null) {
                        throw new BusinessRuntimeException("开始时间不能为空");
                    }
                    if (v1.getEndTime() == null) {
                        throw new BusinessRuntimeException("结束时间不能为空");
                    }
                    if (v1.getWorkHour() == null) {
                        throw new BusinessRuntimeException("作业工时不能为空");
                    }
                });
            }

            TPrdWorkTicketDTO tempWorkTicket = tPrdWorkTicketMapper.getWorkTicket(workTicket.getWorkPlanId(), workTicket.getTicketType(), "2");
            if (tempWorkTicket != null) {
                throw new BusinessRuntimeException("当前作业计划已签票");
            }

            List<TPrdWorkTicketDetailDTO> workTicketDetails = workTicket.getDetails();

            List<Pair<Long, String>> sourceMasses = workTicketDetails.stream()
                    .filter(v1 -> v1.getMassIdSource() == null && StringUtils.isNotBlank(v1.getMassNameSource()))
                    .map(v1 -> Pair.of(v1.getRegionIdSource(), v1.getMassNameSource())).collect(Collectors.toList());
            List<Pair<Long, String>> targetMasses = workTicketDetails.stream()
                    .filter(v1 -> v1.getMassIdTarget() == null && StringUtils.isNotBlank(v1.getMassNameTarget()))
                    .map(v1 -> Pair.of(v1.getRegionIdTarget(), v1.getMassNameTarget())).collect(Collectors.toList());
            List<Pair<Long, String>> masses = new ArrayList<>();
            masses.addAll(sourceMasses);
            masses.addAll(targetMasses);
            masses = masses.stream().distinct().collect(Collectors.toList());

            Map<Pair<Long, String>, Long> map = new HashMap<>();

            masses.forEach(v1 -> {
                Long storageYardId = tPrdWorkTicketMapper.getStorageYard(v1.getLeft(), v1.getRight());
                if (storageYardId == null) {
                    StorageYardPO storageYard = new StorageYardPO();
                    storageYard.setId(snowflake.nextId());
                    storageYard.setParentId(v1.getLeft());
                    storageYard.setStorageYardNm(v1.getRight());
                    storageYard.setShortCd(PinYin4jUtils.convertCnzhToPinYinVal(storageYard.getStorageYardNm()));
                    storageYard.setStorageYardLevel(STORAGE_YARD_LEVEL_MASS);
                    tPrdWorkTicketMapper.insertStorageYard(storageYard);
                    storageYardId = storageYard.getId();
                }
                map.put(v1, storageYardId);
            });
            //根据计划查询航次id
            Map<String, Object> trustShipvoyage = new HashMap<>();
            if ("1".equals(workPlan.getPlanType())) {
                trustShipvoyage = tPrdWorkTicketMapper.getShipvoyage(workTicket.getWorkPlanId());
            } else {
                trustShipvoyage = tPrdWorkTicketMapper.getTrustShipvoyage(workPlan.getTrustId());
            }
            Map<String, Object> finalTrustShipvoyage = trustShipvoyage;
            workTicketDetails.forEach(v1 -> {
                if (v1.getMassIdSource() == null && StringUtils.isNotBlank(v1.getMassNameSource())) {
                    Long massIdSource = map.get(Pair.of(v1.getRegionIdSource(), v1.getMassNameSource()));
                    v1.setMassIdSource(massIdSource);
                }
                if (v1.getMassIdTarget() == null && StringUtils.isNotBlank(v1.getMassNameTarget())) {
                    Long massIdTarget = map.get(Pair.of(v1.getRegionIdTarget(), v1.getMassNameTarget()));
                    v1.setMassIdTarget(massIdTarget);
                }
                if (finalTrustShipvoyage != null) {
                    v1.setShipvoyageId(finalTrustShipvoyage.get("id").toString());
                    v1.setShipvoyageItemId(finalTrustShipvoyage.get("itemId").toString());
                }
            });

//            List<Long> pieceWorkTeamIds = workTicketDetails.stream()
//                    .map(TPrdWorkTicketDetailPO::getPieceWorkTeamId).distinct().collect(Collectors.toList());
//            List<MPieceWorkTeamPO> pieceWorkTeams = tPrdWorkTicketMapper.listPieceWorkTeamById(pieceWorkTeamIds);
//            List<MPieceWorkTeamPO> isPortStorageRelatedPieceWorkTeams = pieceWorkTeams.stream()
//                    .filter(v1 -> IsUpdateStorageEnum._1.getCode().equals(v1.getIsUpdateStorage())).collect(Collectors.toList());
//
//            List<String> cargoCodes = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getCargoCode).distinct().collect(Collectors.toList());
//            List<MCargoPO> cargos = tPrdWorkTicketMapper.listCargo(cargoCodes);
//            List<MCargoPO> isPortStorageRelatedCargos = cargos.stream()
//                    .filter(v1 -> UpdatePointEnum._2.getCode().equals(v1.getUpdatePoint())).collect(Collectors.toList());
//
//            List<String> processDetailCodes = workTicketDetails.stream()
//                    .map(TPrdWorkTicketDetailPO::getProcessDetailCode).distinct().collect(Collectors.toList());
//            List<MWorkProcessPO> workProcesses = tPrdWorkTicketMapper.listWorkProcess(processDetailCodes);
//            List<MWorkProcessPO> outStorehouseWorkProcess = workProcesses.stream()
//                    .filter(v1 -> SourceTargetTypeEnum._05.getCode().equals(v1.getSourceCd()))
//                    .collect(Collectors.toList());
//            List<MWorkProcessPO> inStorehouseWorkProcess = workProcesses.stream()
//                    .filter(v1 -> SourceTargetTypeEnum._05.getCode().equals(v1.getTargetCd()))
//                    .collect(Collectors.toList());
//
//            List<TPrdWorkTicketDetailDTO> isPortStorageRelatedWorkTicketDetails = workTicketDetails.stream().filter(v1 -> {
//                boolean bool = isPortStorageRelatedPieceWorkTeams.stream().anyMatch(v2 -> v1.getPieceWorkTeamId().equals(v2.getId()));
//                return bool && isPortStorageRelatedCargos.stream().anyMatch(v2 -> v1.getCargoCode().equals(v2.getCargoCode()));
//            }).collect(Collectors.toList());
//
//            isPortStorageRelatedWorkTicketDetails.stream()
//                    .filter(v1 -> outStorehouseWorkProcess.stream().anyMatch(v2 -> v1.getProcessDetailCode().equals(v2.getProcessCd())))
//                    .forEach(v1 -> {
//                        if (v1.getStorehouseIdSource() == null || StringUtils.isBlank(v1.getStorehouseNameSource()) ||
//                                v1.getRegionIdSource() == null || StringUtils.isBlank(v1.getRegionNameSource()) ||
//                                v1.getMassIdSource() == null || StringUtils.isBlank(v1.getMassNameSource())
//                        ) {
//                            throw new BusinessRuntimeException("【" + v1.getDeptName() + "、" + v1.getCargoName() + "、" + v1.getProcessDetailName() + "】参与出场，起点垛位不能为空");
//                        }
//                    });
//            isPortStorageRelatedWorkTicketDetails.stream()
//                    .filter(v1 -> inStorehouseWorkProcess.stream().anyMatch(v2 -> v1.getProcessDetailCode().equals(v2.getProcessCd())))
//                    .forEach(v1 -> {
//                        if (v1.getStorehouseIdTarget() == null || StringUtils.isBlank(v1.getStorehouseNameTarget()) ||
//                                v1.getRegionIdTarget() == null || StringUtils.isBlank(v1.getRegionNameTarget()) ||
//                                v1.getMassIdTarget() == null || StringUtils.isBlank(v1.getMassNameTarget())
//                        ) {
//                            throw new BusinessRuntimeException("【" + v1.getDeptName() + "、" + v1.getCargoName() + "、" + v1.getProcessDetailName() + "】参与入场，终点垛位不能为空");
//                        }
//                    });

            workTicket.setId(snowflake.nextId());
            workTicket.setCompanyId(workPlan.getCompanyId());
            workTicket.setCompanyName(workPlan.getCompanyName());
            workTicket.setType(workPlan.getPlanType());
            workTicket.setProcessCode(workPlan.getProcessCode());
            workTicket.setProcessName(workPlan.getProcessName());
            workTicket.setWorkDate(workPlan.getWorkDate());
            workTicket.setClassCode(workPlan.getClassCode());
            workTicket.setClassName(workPlan.getClassName());
            workTicket.setWorkTicketStatus(WorkTicketStatusEnum._10.getCode());
            workTicket.setWorkTicketStatusName(WorkTicketStatusEnum._10.getName());

            List<TPrdWorkTicketEquipmentPO> equipments = workTicket.getDetails().stream().flatMap(v1 -> {
                v1.setId(snowflake.nextId());
                v1.setWorkTicketId(workTicket.getId());
                v1.setProcessCode(workTicket.getProcessCode());
                v1.setProcessName(workTicket.getProcessName());
                v1.setWorkDate(workTicket.getWorkDate());
                v1.setClassCode(workTicket.getClassCode());
                v1.setClassName(workTicket.getClassName());

                return Optional.ofNullable(v1.getEquipments()).orElse(Collections.emptyList()).stream().peek(v2 -> {
                    v2.setId(snowflake.nextId());
                    v2.setWorkTicketDetailId(v1.getId());
                });
            }).collect(Collectors.toList());

            Optional.ofNullable(workTicket.getLabors()).orElse(Collections.emptyList()).forEach(v1 -> {
                v1.setId(snowflake.nextId());
                v1.setWorkTicketId(workTicket.getId());
            });

            tPrdWorkTicketMapper.insertWorkTicket(workTicket);
            tPrdWorkTicketMapper.insertWorkTicketDetail(workTicket.getDetails());
            if (CollectionUtils.isNotEmpty(equipments)) {
                tPrdWorkTicketMapper.insertWorkTicketEquipment(equipments);
            }
            if (CollectionUtils.isNotEmpty(workTicket.getLabors())) {
                tPrdWorkTicketMapper.insertWorkTicketLabor(workTicket.getLabors());
            }
        });
    }

    @Override
    public TPrdWorkTicketDTO getWorkTicket(Long workPlanId, String ticketType) {
        TPrdWorkTicketDTO workTicket = tPrdWorkTicketMapper.getWorkTicket(workPlanId, ticketType, "2");
        if (workTicket == null) {
            return null;
        }
        List<TPrdWorkTicketLaborPO> labors = tPrdWorkTicketMapper.listWorkTicketLabor(workTicket.getId());
        List<TPrdWorkTicketDetailDTO> workTicketDetails = tPrdWorkTicketMapper.listWorkTicketDetail(workTicket.getId());
        List<Long> workTicketDetailIds = workTicketDetails.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList());
        List<TPrdWorkTicketEquipmentPO> equipments = tPrdWorkTicketMapper.listWorkTicketEquipment(workTicketDetailIds);
        workTicket.setDetails(workTicketDetails);
        workTicketDetails.forEach(v1 -> {
            List<TPrdWorkTicketEquipmentPO> currentEquipments = equipments.stream()
                    .filter(v2 -> v1.getId().equals(v2.getWorkTicketDetailId())).collect(Collectors.toList());
            v1.setEquipments(currentEquipments);
        });
        workTicket.setDetails(workTicketDetails);
//        } else {
//            //查询有几个分组
//            List<Map<String, Object>> mapList = tPrdWorkTicketMapper.getWorkTicketGroup(workTicket.getId());
//            if (mapList != null && mapList.size() != 0) {
//                for (Map<String, Object> map : mapList) {
//                    List<TPrdWorkTicketDetailDTO> detailsList = tPrdWorkTicketMapper.listWorkTicketDetailGroup(workTicket.getId(), Long.parseLong(map.get("groupId").toString()));
//                    if(detailsList != null && detailsList.size() != 0){
//                        List<Long> workTicketDetailIds = detailsList.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList());
//                        List<TPrdWorkTicketEquipmentPO> equipments = tPrdWorkTicketMapper.listWorkTicketEquipment(workTicketDetailIds);
//                        workTicket.setDetails(detailsList);
//                        detailsList.forEach(v1 -> {
//                            List<TPrdWorkTicketEquipmentPO> currentEquipments = equipments.stream()
//                                    .filter(v2 -> v1.getId().equals(v2.getWorkTicketDetailId())).collect(Collectors.toList());
//                            v1.setEquipments(currentEquipments);
//                        });
//                        map.put("details", detailsList);
//                    }
//
//                }
//            }
//            workTicket.setTableList(mapList);
//        }
        workTicket.setLabors(labors);
        return workTicket;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateWorkTicket(TPrdWorkTicketDTO workTicket) {
        List<TPrdWorkTicketDTO> workTicketDTOS = tPrdWorkTicketMapper.getTicket(workTicket.getWorkPlanId(),workTicket.getTicketType());
        if(CollectionUtils.isNotEmpty(workTicketDTOS)){
            for (TPrdWorkTicketDTO workTicketDTO : workTicketDTOS) {
                if("20".equals(workTicketDTO.getWorkTicketStatus())){
                    throw new BusinessRuntimeException("请先取消签票审核再修改");
                }
            }
        }
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workTicket.getWorkPlanId())
                .build().run(() -> {
            TPrdWorkPlanPO workPlan = tPrdWorkTicketMapper.getWorkPlan(workTicket.getWorkPlanId());
            if (!DispatchEnum.WorkPlanTypeEnum.SUNDRY.getCode().equals(workPlan.getPlanType())) {
                workTicket.getDetails().forEach(v1 -> {
                    if (v1.getTrustId() == null) {
                        throw new BusinessRuntimeException("指令ID不能为空");
                    }
                    if (v1.getTrustCargoInfoId() == null) {
                        throw new BusinessRuntimeException("指令票货ID不能为空");
                    }
                    if (v1.getCargoInfoId() == null) {
                        throw new BusinessRuntimeException("票货ID不能为空");
                    }
                    if (StringUtils.isEmpty(v1.getCabinNo()) && ("下舱".equals(v1.getProcessDetailName()))) {
                        throw new BusinessRuntimeException("下舱作业过程，请选择舱口");
                    }
                    if (v1.getTon() == null && (!"下舱".equals(v1.getProcessDetailName()))) {
                        throw new BusinessRuntimeException("吨数（数量）不能为空");
                    }
                    if (!"下舱".equals(v1.getProcessDetailName())) {
                        if (v1.getTon().compareTo(BigDecimal.ZERO) < 0) {
                            throw new BusinessRuntimeException("吨数（数量）不能小于0");
                        }
                    }
//                    if (!CollectionUtils.isEmpty(v1.getCabinNoList())) {
//                        v1.setCabinNo(String.join(",", v1.getCabinNoList()));
//                    } else {
//                        v1.setCabinNo(null);
//                    }
                    Integer cargoFlag = tPrdWorkTicketMapper.getCargoIsUpdate(v1.getCargoCode());
                    if (cargoFlag != null && cargoFlag == 2) {
                        //校验场地
                        Map<String, Object> processFlag = tPrdWorkTicketMapper.getProcessIsUpdate(v1.getProcessDetailCode());
                        if (processFlag != null && "1".equals(processFlag.get("updatePoint").toString())) {
                            if ("201".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
                                //出库
                                if (v1.getStorehouseIdSource() == null) {
                                    throw new BusinessRuntimeException("请填写作业过程为" + v1.getProcessDetailName() + "的起始区域信息");
                                }
                            }
                            if ("101".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
                                //入库
                                if (v1.getStorehouseIdTarget() == null) {
                                    throw new BusinessRuntimeException("请填写作业过程为" + v1.getProcessDetailName() + "的终点区域信息");
                                }
                            }
                        }
                    }

                });
            } else {
                workTicket.getDetails().forEach(v1 -> {
                    if (v1.getStartTime() == null) {
                        throw new BusinessRuntimeException("开始时间不能为空");
                    }
                    if (v1.getEndTime() == null) {
                        throw new BusinessRuntimeException("结束时间不能为空");
                    }
                    if (v1.getWorkHour() == null) {
                        throw new BusinessRuntimeException("作业工时不能为空");
                    }
                });
            }

            TPrdWorkTicketDTO tempWorkTicket = tPrdWorkTicketMapper.getWorkTicket(workTicket.getWorkPlanId(), workTicket.getTicketType(), "2");
            tempWorkTicket.setDeptId(workTicket.getDeptId());
            tempWorkTicket.setDeptName(workTicket.getDeptName());
            tempWorkTicket.setQuantity(workTicket.getQuantity());
            tempWorkTicket.setTon(workTicket.getTon());
            tempWorkTicket.setTicketType(workTicket.getTicketType());
            if (tempWorkTicket == null) {
                throw new BusinessRuntimeException("当前作业计划未签票");
            }
            if (WorkTicketStatusEnum._20.getCode().equals(tempWorkTicket.getWorkTicketStatus())) {
                throw new BusinessRuntimeException("当前作业计划签票已审核");
            }

            List<TPrdWorkTicketDetailDTO> workTicketDetails = workTicket.getDetails();

            List<Pair<Long, String>> sourceMasses = workTicketDetails.stream()
                    .filter(v1 -> v1.getMassIdSource() == null && StringUtils.isNotBlank(v1.getMassNameSource()))
                    .map(v1 -> Pair.of(v1.getRegionIdSource(), v1.getMassNameSource())).collect(Collectors.toList());
            List<Pair<Long, String>> targetMasses = workTicketDetails.stream()
                    .filter(v1 -> v1.getMassIdTarget() == null && StringUtils.isNotBlank(v1.getMassNameTarget()))
                    .map(v1 -> Pair.of(v1.getRegionIdTarget(), v1.getMassNameTarget())).collect(Collectors.toList());
            List<Pair<Long, String>> masses = new ArrayList<>();
            masses.addAll(sourceMasses);
            masses.addAll(targetMasses);
            masses = masses.stream().distinct().collect(Collectors.toList());

            Map<Pair<Long, String>, Long> map = new HashMap<>();

            masses.forEach(v1 -> {
                Long storageYardId = tPrdWorkTicketMapper.getStorageYard(v1.getLeft(), v1.getRight());
                if (storageYardId == null) {
                    StorageYardPO storageYard = new StorageYardPO();
                    storageYard.setId(snowflake.nextId());
                    storageYard.setParentId(v1.getLeft());
                    storageYard.setStorageYardNm(v1.getRight());
                    storageYard.setShortCd(PinYin4jUtils.convertCnzhToPinYinVal(storageYard.getStorageYardNm()));
                    storageYard.setStorageYardLevel(STORAGE_YARD_LEVEL_MASS);
                    tPrdWorkTicketMapper.insertStorageYard(storageYard);
                    storageYardId = storageYard.getId();
                }
                map.put(v1, storageYardId);
            });
            //根据计划查询航次id
            Map<String, Object> trustShipvoyage = new HashMap<>();
            if ("1".equals(workPlan.getPlanType())) {
                trustShipvoyage = tPrdWorkTicketMapper.getShipvoyage(workTicket.getWorkPlanId());
            } else {
                trustShipvoyage = tPrdWorkTicketMapper.getTrustShipvoyage(workPlan.getTrustId());
            }
            Map<String, Object> finalTrustShipvoyage = trustShipvoyage;
            workTicketDetails.forEach(v1 -> {
                if (v1.getMassIdSource() == null && StringUtils.isNotBlank(v1.getMassNameSource())) {
                    Long massIdSource = map.get(Pair.of(v1.getRegionIdSource(), v1.getMassNameSource()));
                    v1.setMassIdSource(massIdSource);
                }
                if (v1.getMassIdTarget() == null && StringUtils.isNotBlank(v1.getMassNameTarget())) {
                    Long massIdTarget = map.get(Pair.of(v1.getRegionIdTarget(), v1.getMassNameTarget()));
                    v1.setMassIdTarget(massIdTarget);
                }
                if (finalTrustShipvoyage != null) {
                    v1.setShipvoyageId(finalTrustShipvoyage.get("id").toString());
                    v1.setShipvoyageItemId(finalTrustShipvoyage.get("itemId").toString());
                }
            });

//            List<Long> pieceWorkTeamIds = workTicketDetails.stream()
//                    .map(TPrdWorkTicketDetailPO::getPieceWorkTeamId).distinct().collect(Collectors.toList());
//            List<MPieceWorkTeamPO> pieceWorkTeams = tPrdWorkTicketMapper.listPieceWorkTeamById(pieceWorkTeamIds);
//            List<MPieceWorkTeamPO> isPortStorageRelatedPieceWorkTeams = pieceWorkTeams.stream()
//                    .filter(v1 -> IsUpdateStorageEnum._1.getCode().equals(v1.getIsUpdateStorage())).collect(Collectors.toList());
//
//            List<String> cargoCodes = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getCargoCode).distinct().collect(Collectors.toList());
//            List<MCargoPO> cargos = tPrdWorkTicketMapper.listCargo(cargoCodes);
//            List<MCargoPO> isPortStorageRelatedCargos = cargos.stream()
//                    .filter(v1 -> UpdatePointEnum._2.getCode().equals(v1.getUpdatePoint())).collect(Collectors.toList());
//
//            List<String> processDetailCodes = workTicketDetails.stream()
//                    .map(TPrdWorkTicketDetailPO::getProcessDetailCode).distinct().collect(Collectors.toList());
//            List<MWorkProcessPO> workProcesses = tPrdWorkTicketMapper.listWorkProcess(processDetailCodes);
//            List<MWorkProcessPO> outStorehouseWorkProcess = workProcesses.stream()
//                    .filter(v1 -> SourceTargetTypeEnum._05.getCode().equals(v1.getSourceCd()))
//                    .collect(Collectors.toList());
//            List<MWorkProcessPO> inStorehouseWorkProcess = workProcesses.stream()
//                    .filter(v1 -> SourceTargetTypeEnum._05.getCode().equals(v1.getTargetCd()))
//                    .collect(Collectors.toList());
//
//            List<TPrdWorkTicketDetailDTO> isPortStorageRelatedWorkTicketDetails = workTicketDetails.stream().filter(v1 -> {
//                boolean bool = isPortStorageRelatedPieceWorkTeams.stream().anyMatch(v2 -> v1.getPieceWorkTeamId().equals(v2.getId()));
//                return bool && isPortStorageRelatedCargos.stream().anyMatch(v2 -> v1.getCargoCode().equals(v2.getCargoCode()));
//            }).collect(Collectors.toList());
//
//            isPortStorageRelatedWorkTicketDetails.stream()
//                    .filter(v1 -> outStorehouseWorkProcess.stream().anyMatch(v2 -> v1.getProcessDetailCode().equals(v2.getProcessCd())))
//                    .forEach(v1 -> {
//                        if (v1.getStorehouseIdSource() == null || StringUtils.isBlank(v1.getStorehouseNameSource()) ||
//                                v1.getRegionIdSource() == null || StringUtils.isBlank(v1.getRegionNameSource()) ||
//                                v1.getMassIdSource() == null || StringUtils.isBlank(v1.getMassNameSource())
//                        ) {
//                            throw new BusinessRuntimeException("【" + v1.getDeptName() + "、" + v1.getCargoName() + "、" + v1.getProcessDetailName() + "】参与出场，起点垛位不能为空");
//                        }
//                    });
//            isPortStorageRelatedWorkTicketDetails.stream()
//                    .filter(v1 -> inStorehouseWorkProcess.stream().anyMatch(v2 -> v1.getProcessDetailCode().equals(v2.getProcessCd())))
//                    .forEach(v1 -> {
//                        if (v1.getStorehouseIdTarget() == null || StringUtils.isBlank(v1.getStorehouseNameTarget()) ||
//                                v1.getRegionIdTarget() == null || StringUtils.isBlank(v1.getRegionNameTarget()) ||
//                                v1.getMassIdTarget() == null || StringUtils.isBlank(v1.getMassNameTarget())
//                        ) {
//                            throw new BusinessRuntimeException("【" + v1.getDeptName() + "、" + v1.getCargoName() + "、" + v1.getProcessDetailName() + "】参与入场，终点垛位不能为空");
//                        }
//                    });

            workTicketDetails = tPrdWorkTicketMapper.listWorkTicketDetail(tempWorkTicket.getId());
            tPrdWorkTicketMapper.deleteWorkTicketDetail(tempWorkTicket.getId());
            List<Long> workTicketDetailIds = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getId).collect(Collectors.toList());
            tPrdWorkTicketMapper.deleteWorkTicketEquipment(workTicketDetailIds);
            tPrdWorkTicketMapper.deleteWorkTicketLabor(tempWorkTicket.getId());

            tempWorkTicket.setCompanyId(workPlan.getCompanyId());
            tempWorkTicket.setCompanyName(workPlan.getCompanyName());
            tempWorkTicket.setType(workPlan.getPlanType());
            tempWorkTicket.setProcessCode(workPlan.getProcessCode());
            tempWorkTicket.setProcessName(workPlan.getProcessName());
            tempWorkTicket.setWorkDate(workPlan.getWorkDate());
            tempWorkTicket.setClassCode(workPlan.getClassCode());
            tempWorkTicket.setClassName(workPlan.getClassName());

            List<TPrdWorkTicketEquipmentPO> equipments = workTicket.getDetails().stream().flatMap(v1 -> {
                v1.setId(snowflake.nextId());
                v1.setWorkTicketId(tempWorkTicket.getId());
                v1.setProcessCode(tempWorkTicket.getProcessCode());
                v1.setProcessName(tempWorkTicket.getProcessName());
                v1.setWorkDate(tempWorkTicket.getWorkDate());
                v1.setClassCode(tempWorkTicket.getClassCode());
                v1.setClassName(tempWorkTicket.getClassName());

                return Optional.ofNullable(v1.getEquipments()).orElse(Collections.emptyList()).stream().peek(v2 -> {
                    v2.setId(snowflake.nextId());
                    v2.setWorkTicketDetailId(v1.getId());
                });
            }).collect(Collectors.toList());

            Optional.ofNullable(workTicket.getLabors()).orElse(Collections.emptyList()).forEach(v1 -> {
                v1.setId(snowflake.nextId());
                v1.setWorkTicketId(tempWorkTicket.getId());
            });

            tPrdWorkTicketMapper.updateWorkTicket(tempWorkTicket);
            tPrdWorkTicketMapper.insertWorkTicketDetail(workTicket.getDetails());
            if (CollectionUtils.isNotEmpty(equipments)) {
                tPrdWorkTicketMapper.insertWorkTicketEquipment(equipments);
            }
            if (CollectionUtils.isNotEmpty(workTicket.getLabors())) {
                tPrdWorkTicketMapper.insertWorkTicketLabor(workTicket.getLabors());
            }
        });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteWorkTicket(Long workPlanId, String type) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workPlanId)
                .build().run(() -> {
            TPrdWorkTicketDTO workTicket = tPrdWorkTicketMapper.getWorkTicket(workPlanId, type, "2");
            if (workTicket == null) {
                return;
            }
            if (WorkTicketStatusEnum._20.getCode().equals(workTicket.getWorkTicketStatus())) {
                throw new BusinessRuntimeException("当前作业计划签票已审核");
            }

            List<TPrdWorkTicketDetailDTO> workTicketDetails = tPrdWorkTicketMapper.listWorkTicketDetail(workTicket.getId());
            tPrdWorkTicketMapper.deleteWorkTicket(workPlanId, type);
            tPrdWorkTicketMapper.deleteWorkTicketDetail(workTicket.getId());
            List<Long> workTicketDetailIds = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getId).collect(Collectors.toList());
            tPrdWorkTicketMapper.deleteWorkTicketEquipment(workTicketDetailIds);
            tPrdWorkTicketMapper.deleteWorkTicketLabor(workTicket.getId());
        });
    }

    /**
     * 作业票，更新港存,计件作业量
     *
     * @param workTicket        作业票主表信息
     * @param workTicketDetails 作业票子表信息
     * @param delFlag           是否先删除 港存和计件信息
     * @param isJsg             是否集疏港
     * @param delFlag           功能名称
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void commWorkTicketOperate(TPrdWorkTicketDTO workTicket, List<TPrdWorkTicketDetailDTO> workTicketDetails, Boolean delFlag, Boolean isJsg, String message) {
//        List<String> lists = new ArrayList<>();
//        System.out.println(lists.get(2));
        if (workTicket == null) {
            throw new BusinessRuntimeException("当前作业计划未签票");
        }
        if (!isJsg && (!"整船调整".equals(message))) {
            if (WorkTicketStatusEnum._20.getCode().equals(workTicket.getWorkTicketStatus())) {
                throw new BusinessRuntimeException("当前作业计划签票已审核");
            }
        }
        if ("整船调整".equals(message)) {
            Integer count = tPrdWorkTicketMapper.getSalaryEx(workTicket.getId());
            if (count > 0) {
                throw new BusinessRuntimeException("该作业计划计件已审核,请先取消计件审核");
            }
        }
        SysParameterDTO sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE_TICKET");
        boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
        final String methodName = "TPrdWorkTicketServiceImpl:commWorkTicketOperate";
        LOGGER.enter(methodName, "业务执行" + message + "workTicket: " + workTicket + ", workTicketDetails: " + workTicketDetails + ", delFlag: " + delFlag + ", isJsg: " + isJsg);
        if (delFlag) {
            //传入子表信息的id
            List<Long> workTicketDetailIds = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getId).collect(Collectors.toList());
            //删除作业票的场存和计件信息
            List<TPrdPortStorageDetailPO> portStorageDetails = tPrdWorkTicketMapper.listPortStorageDetailItem(workTicketDetailIds);
            if (!portStorageDetails.isEmpty()) {
                List<Long> portStorageDetailIds = portStorageDetails.stream().map(TPrdPortStorageDetailPO::getId).collect(Collectors.toList());
                try {
                    businessCommonService.deletePortStorageDetail(portStorageDetailIds);
                } catch (BusinessRuntimeException e) {
                    throw new BusinessRuntimeException("相关港存已清场，无法销审");
                }

            }
            //根据作业票子表id 删除计件明细
            tPrdWorkTicketMapper.deleteSalary(workTicketDetailIds);
            //根据作业票主表id 删除计件明细
            tPrdWorkTicketMapper.deleteSalaryZ(workTicket.getId());
        }
        Map<String, Object> cargoSalaryMap = new HashMap<>();
        if (workTicketDetails.get(0).getCargoCode() != null) {
            cargoSalaryMap = tPrdWorkTicketMapper.getCargoSalaryType(workTicketDetails.get(0).getCargoCode());
            if (cargoSalaryMap == null) {
                throw new BusinessRuntimeException("未查询到" + workTicketDetails.get(0).getCargoName() + "的计件类型详情,请先联系人事部门进行维护");
            }
        }
        BigDecimal workTicketTon = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(workTicketDetails)) {
            String cargoWorkType = tPrdWorkTicketMapper.getCargoWorkType(workTicketDetails.get(0).getCargoCode());
            for (TPrdWorkTicketDetailDTO dto : workTicketDetails) {
                //查询该货物是否需要更新场存
                if (dto.getCargoCode() == null) {
                    throw new BusinessRuntimeException("获取签票货物信息失败，请检查签票信息是否正确");
                }
                String name = "";
                if (dto.getShipvoyageItemId() != null) {
                    //根据航次id 查询船名航次
                    name = tPrdWorkTicketMapper.getShipVoyageName(Arrays.asList(dto.getShipvoyageItemId().split(",")));
                }
                Integer cargoFlag = tPrdWorkTicketMapper.getCargoIsUpdate(dto.getCargoCode());
                if (cargoFlag != null && cargoFlag == 2) {
                    //查询该作业过程是否更新场存
                    if (dto.getProcessDetailCode() == null) {
                        throw new BusinessRuntimeException("获取签票作业过程信息失败，请检查签票信息是否正确");
                    }
                    Map<String, Object> processFlag = tPrdWorkTicketMapper.getProcessIsUpdate(dto.getProcessDetailCode());
                    if (processFlag != null && "1".equals(processFlag.get("updatePoint").toString())) {
//                        //需要更新场存
                        if ("201".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
                            //出库
                            String trustNo = tPrdWorkTicketMapper.getTrustIdWicket(workTicket.getId());
                            if (dto.getStorehouseIdSource() == null) {
                                throw new BusinessRuntimeException("通知单号为" + trustNo + ",货物为" + workTicketDetails.get(0).getCargoName() + ",作业过程为" + dto.getProcessDetailName() + "的起始区域信息获取失败,请修改签票的起始区域信息");
                            }
                            if (oneCarPlan) {
                                Map<String, Object> mapSurplus = tPrdWorkTicketMapper.getStorageSurplus(dto.getCargoInfoId(), dto.getStorehouseIdSource(), dto.getRegionIdSource(), dto.getMassIdSource());
                                if (mapSurplus != null) {
                                    BigDecimal ton = new BigDecimal(mapSurplus.get("ton").toString());
                                    if (dto.getTon().compareTo(ton) > 0) {
                                        throw new BusinessRuntimeException("通知单号为" + trustNo + ",货物为" + workTicketDetails.get(0).getCargoName() + ",作业过程为" + dto.getProcessDetailName() + "的起始区域的港存量剩余不足!");
                                    }
                                }
                            }

                            //起始位置减量
                            List<TPrdPortStorageDetailPO> portStorageDetails = new ArrayList<>();
                            TPrdPortStorageDetailPO tPrdPortStorageDetailPO = new TPrdPortStorageDetailPO();
                            tPrdPortStorageDetailPO.setWorkTicketId(workTicket.getId()); //作业票主表ID
                            tPrdPortStorageDetailPO.setWorkTicketDetailId(dto.getId()); //作业票子表ID
                            tPrdPortStorageDetailPO.setWorkDate(workTicket.getWorkDate());
                            tPrdPortStorageDetailPO.setClassCode(workTicket.getClassCode()); //班次
                            tPrdPortStorageDetailPO.setClassName(workTicket.getClassName());
                            tPrdPortStorageDetailPO.setProcessDetailCode(dto.getProcessDetailCode()); //作业过程
                            tPrdPortStorageDetailPO.setProcessDetailName(dto.getProcessDetailName());
                            tPrdPortStorageDetailPO.setInoutStorageCode(InoutStorageEnum._20.getCode()); //类型
                            tPrdPortStorageDetailPO.setInoutStorageName(InoutStorageEnum._20.getLabel());
                            tPrdPortStorageDetailPO.setInoutDate(workTicket.getWorkDate());
                            tPrdPortStorageDetailPO.setCleanMassSign(CleanMassSignEnum._0.getCode());
                            tPrdPortStorageDetailPO.setCompanyId(workTicket.getCompanyId()); //公司信息
                            tPrdPortStorageDetailPO.setCompanyName(workTicket.getCompanyName());
                            tPrdPortStorageDetailPO.setCargoInfoId(dto.getCargoInfoId());
                            tPrdPortStorageDetailPO.setQuantity(dto.getQuantity() == null ? 0 : -dto.getQuantity()); //件数
                            tPrdPortStorageDetailPO.setTon(dto.getTon().negate()); //重量
                            tPrdPortStorageDetailPO.setStorehouseId(dto.getStorehouseIdSource()); //库场
                            tPrdPortStorageDetailPO.setStorehouseName(dto.getStorehouseNameSource());
                            tPrdPortStorageDetailPO.setRegionId(dto.getRegionIdSource());
                            tPrdPortStorageDetailPO.setRegionName(dto.getRegionNameSource());
                            tPrdPortStorageDetailPO.setMassId(dto.getMassIdSource());
                            tPrdPortStorageDetailPO.setMassName(dto.getMassNameSource());
                            portStorageDetails.add(tPrdPortStorageDetailPO);
                            try {
                                businessCommonService.insertPortStorageDetail(portStorageDetails);
                            } catch (BusinessRuntimeException e) {
//                                if (!"当前港存已清场".equals(e.getMessage())) {
                                    //如果是清场的报错  e.getMessage()的信息为:票货号  区/垛  +当前港存已清场
                                    throw new BusinessRuntimeException(trustNo+"_"+e.getMessage());
//                                }
                            }
                        }

                        if ("101".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
                            //入库
                            //终点位置加量
                            if (dto.getStorehouseIdTarget() == null) {
                                String trustNo = tPrdWorkTicketMapper.getTrustIdWicket(workTicket.getId());
                                throw new BusinessRuntimeException("通知单号为" + trustNo + ",货物为" + workTicketDetails.get(0).getCargoName() + ",作业过程为" + dto.getProcessDetailName() + "的终点区域信息获取失败,请修改签票的终点区域信息");
                            }
                            List<TPrdPortStorageDetailPO> portStorageTaDetails = new ArrayList<>();
                            TPrdPortStorageDetailPO tPrdPortStorageTaDetailPO = new TPrdPortStorageDetailPO();
                            tPrdPortStorageTaDetailPO.setWorkTicketId(workTicket.getId()); //作业票主表ID
                            tPrdPortStorageTaDetailPO.setWorkTicketDetailId(dto.getId()); //作业票子表ID
                            tPrdPortStorageTaDetailPO.setWorkDate(workTicket.getWorkDate());
                            tPrdPortStorageTaDetailPO.setClassCode(workTicket.getClassCode()); //班次
                            tPrdPortStorageTaDetailPO.setClassName(workTicket.getClassName());
                            tPrdPortStorageTaDetailPO.setProcessDetailCode(dto.getProcessDetailCode()); //作业过程
                            tPrdPortStorageTaDetailPO.setProcessDetailName(dto.getProcessDetailName());
                            tPrdPortStorageTaDetailPO.setInoutStorageCode(InoutStorageEnum._20.getCode()); //类型
                            tPrdPortStorageTaDetailPO.setInoutStorageName(InoutStorageEnum._20.getLabel());
                            tPrdPortStorageTaDetailPO.setInoutDate(workTicket.getWorkDate());
                            tPrdPortStorageTaDetailPO.setCleanMassSign(CleanMassSignEnum._0.getCode());
                            tPrdPortStorageTaDetailPO.setCompanyId(workTicket.getCompanyId()); //公司信息
                            tPrdPortStorageTaDetailPO.setCompanyName(workTicket.getCompanyName());
                            tPrdPortStorageTaDetailPO.setCargoInfoId(dto.getCargoInfoId());
                            tPrdPortStorageTaDetailPO.setQuantity(dto.getQuantity() == null ? 0 : dto.getQuantity()); //件数
                            tPrdPortStorageTaDetailPO.setTon(dto.getTon()); //重量
                            tPrdPortStorageTaDetailPO.setStorehouseId(dto.getStorehouseIdTarget()); //库场
                            tPrdPortStorageTaDetailPO.setStorehouseName(dto.getStorehouseNameTarget());
                            tPrdPortStorageTaDetailPO.setRegionId(dto.getRegionIdTarget());
                            tPrdPortStorageTaDetailPO.setRegionName(dto.getRegionNameTarget());
                            tPrdPortStorageTaDetailPO.setMassId(dto.getMassIdTarget());
                            tPrdPortStorageTaDetailPO.setMassName(dto.getMassNameTarget());
                            portStorageTaDetails.add(tPrdPortStorageTaDetailPO);
                            try {
                                businessCommonService.insertPortStorageDetail(portStorageTaDetails);
                            } catch (BusinessRuntimeException e) {
//                                if (!"当前港存已清场".equals(e.getMessage())) {
                                    String trustNo = tPrdWorkTicketMapper.getTrustIdWicket(workTicket.getId());
                                //如果是清场的报错  e.getMessage()的信息为:票货号  区/垛  +当前港存已清场
                                    throw new BusinessRuntimeException(trustNo+":"+e.getMessage());
//                                }
                            }
                        }
                    }
                }
                //计件分配
                //判断班组是否为内部班组,内部班组分配作业量
                if (dto.getDeptId() != null) {
                    String internal = tPrdWorkTicketMapper.getDeptInternal(dto.getDeptId());
                    if ("I".equals(internal)) {
                        //内部班组 操作工班
                        //查询该部门下是否有班组点名
                        List<Long> ids = tPrdWorkTicketMapper.getDeptRollCallItem(workTicket.getWorkDate(), workTicket.getClassCode(), dto.getDeptId());
                        if (ids.size() == 0) {
                            throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组未出勤点名");
                        }
                        if (ids.size() > 1) {
                            throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组出勤点名重复");
                        }
                        if (dto.getTon() != null && dto.getTon().compareTo(BigDecimal.ZERO) != 0) {
                            List<TPrdAttendanceUserPO> attendanceUsers = tPrdWorkTicketMapper.listAttendanceUser(ids);
                            Map<String, Object> deptMap = tPrdWorkTicketMapper.getDeptDm(ids);
                            List<TPrdSalaryPO> salaries = new ArrayList<>();
                            //调用计件分配工具类
                            BigDecimal each = getAllocationMethod(attendanceUsers, dto.getTon());
                            for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUsers) {
                                TPrdSalaryPO salary = new TPrdSalaryPO();
                                salary.setId(snowflake.nextId());
                                salary.setWorkTicketDetailId(dto.getId());
                                salary.setCompanyId(workTicket.getCompanyId());
                                salary.setCompanyName(workTicket.getCompanyName());
                                salary.setWorkDate(workTicket.getWorkDate());
                                salary.setClassCode(workTicket.getClassCode());
                                salary.setClassName(workTicket.getClassName());
                                salary.setPieceProjectCode("1");
                                salary.setPieceProjectName("1");
                                salary.setProcessDetailCode(dto.getProcessDetailCode());
                                salary.setProcessDetailName(dto.getProcessDetailName());
                                if (deptMap != null) {
                                    salary.setDeptId(Long.parseLong(deptMap.get("deptId").toString()));
                                    salary.setDeptName(deptMap.get("deptName").toString());
                                } else {
                                    throw new BusinessRuntimeException("获取点名班组信息失败");
                                }
                                salary.setUserBy(tPrdAttendanceUserPO.getUserId());
                                salary.setUserByName(tPrdAttendanceUserPO.getUserName());
                                salary.setCoefficient(tPrdAttendanceUserPO.getCoefficient());
//                                salary.setTon(
//                                        dto.getTon()
//                                                .divide(BigDecimal.valueOf(attendanceUsers.size()), 10, RoundingMode.HALF_UP)
//                                                .multiply(BigDecimal.valueOf(tPrdAttendanceUserPO.getCoefficient())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
//                                );
                                salary.setTon(each.multiply(BigDecimal.valueOf(salary.getCoefficient()))
                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                                salary.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
                                salary.setSalaryStatusName(SalaryStatusEnum._10.getLabel());

                                //计件类型
                                //查询货物计件类型
                                if ("1710197535592812544".equals(dto.getDeptId().toString())) {
                                    //固机
                                    salary.setSalaryTypeCode(cargoSalaryMap.get("gjTypeCode").toString());
                                    salary.setSalaryTypeName(cargoSalaryMap.get("gjTypeName").toString());
                                } else if ("1710203467898949632".equals(dto.getDeptId().toString())) {
                                    //流机
                                    salary.setSalaryTypeCode(cargoSalaryMap.get("ljTypeCode").toString());
                                    salary.setSalaryTypeName(cargoSalaryMap.get("ljTypeName").toString());
                                }
                                if (dto.getShipvoyageItemId() != null) {
                                    salary.setShipVoyageItemId(dto.getShipvoyageItemId());
                                    if (name != null) {
                                        salary.setShipVoyage(name);
                                    }
                                }
                                salary.setCargoCode(dto.getCargoCode());
                                salary.setCargoName(dto.getCargoName());

                                salaries.add(salary);
                            }
                            if (!salaries.isEmpty()) {
                                if ("1710197535592812544".equals(dto.getDeptId().toString())) {
                                    //固机判断一下机械类型 只分配门机
                                    String code = tPrdWorkTicketMapper.getEqTypeCode(dto.getId());
                                    if ("0007".equals(code)) {
                                        tPrdWorkTicketMapper.insertSalary(salaries);
                                    }
                                } else {
                                    tPrdWorkTicketMapper.insertSalary(salaries);
                                }
                            }
                        }
                    }
                }

                //人员工班
                if (dto.getPieceWorkTeamId() != null) {
                    String internalPer = tPrdWorkTicketMapper.getDeptInternal(dto.getPieceWorkTeamId());
                    if ("I".equals(internalPer)) {
                        //内部班组
                        //查询该部门下是否有班组点名
                        List<Long> ids = tPrdWorkTicketMapper.getDeptRollCallItem(workTicket.getWorkDate(), workTicket.getClassCode(), dto.getPieceWorkTeamId());
                        if (ids.size() == 0) {
                            throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组未出勤点名");
                        }
                        if (ids.size() > 1) {
                            throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组出勤点名重复");
                        }
                        if (dto.getTon() != null && dto.getTon().compareTo(BigDecimal.ZERO) != 0) {
                            List<TPrdAttendanceUserPO> attendanceUsers = tPrdWorkTicketMapper.listAttendanceUser(ids);
                            Map<String, Object> deptMap = tPrdWorkTicketMapper.getDeptDm(ids);
                            List<TPrdSalaryPO> salariesPer = new ArrayList<>();
                            BigDecimal each = getAllocationMethod(attendanceUsers, dto.getTon());
                            for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUsers) {
                                TPrdSalaryPO salary = new TPrdSalaryPO();
                                salary.setId(snowflake.nextId());
                                salary.setWorkTicketDetailId(dto.getId());
                                salary.setCompanyId(workTicket.getCompanyId());
                                salary.setCompanyName(workTicket.getCompanyName());
                                salary.setWorkDate(workTicket.getWorkDate());
                                salary.setClassCode(workTicket.getClassCode());
                                salary.setClassName(workTicket.getClassName());
                                salary.setPieceProjectCode("1");
                                salary.setPieceProjectName("1");
                                salary.setProcessDetailCode(dto.getProcessDetailCode());
                                salary.setProcessDetailName(dto.getProcessDetailName());
                                if (deptMap != null) {
                                    salary.setDeptId(Long.parseLong(deptMap.get("deptId").toString()));
                                    salary.setDeptName(deptMap.get("deptName").toString());
                                } else {
                                    throw new BusinessRuntimeException("获取点名班组信息失败");
                                }
                                salary.setUserBy(tPrdAttendanceUserPO.getUserId());
                                salary.setUserByName(tPrdAttendanceUserPO.getUserName());
                                salary.setCoefficient(tPrdAttendanceUserPO.getCoefficient());
//                                salary.setTon(
//                                        dto.getTon()
//                                                .divide(BigDecimal.valueOf(attendanceUsers.size()), 10, RoundingMode.HALF_UP)
//                                                .multiply(BigDecimal.valueOf(tPrdAttendanceUserPO.getCoefficient())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
//                                );
                                salary.setTon(each.multiply(BigDecimal.valueOf(salary.getCoefficient()))
                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                                salary.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
                                salary.setSalaryStatusName(SalaryStatusEnum._10.getLabel());
                                if (dto.getShipvoyageItemId() != null) {
                                    salary.setShipVoyageItemId(dto.getShipvoyageItemId().toString());
                                    if (name != null) {
                                        salary.setShipVoyage(name);
                                    }
                                }
                                salary.setCargoCode(dto.getCargoCode());
                                salary.setCargoName(dto.getCargoName());
                                salariesPer.add(salary);
                            }
                            if (!salariesPer.isEmpty()) {
                                if ("1710197535592812544".equals(dto.getDeptId().toString())) {
                                    //固机判断一下机械类型 只分配门机
                                    String code = tPrdWorkTicketMapper.getEqTypeCode(dto.getId());
                                    if ("0007".equals(code)) {
                                        tPrdWorkTicketMapper.insertSalary(salariesPer);
                                    }
                                } else {
                                    tPrdWorkTicketMapper.insertSalary(salariesPer);
                                }


                            }
                        }
                    }
                }
                //散货
                if ("2".equals(cargoWorkType)) {
                    String isMeanwhile = tPrdWorkTicketMapper.getProIsMeanwhile(dto.getProcessDetailCode());
                    if ("1".equals(isMeanwhile)) {
                        workTicketTon = workTicketTon.add(dto.getTon());
                    }
                }

            }
        } else {
            throw new BusinessRuntimeException("获取签票明细信息失败，请检查签票信息是否正确");
        }
        if (workTicket.getTon() != null && workTicket.getTon().compareTo(BigDecimal.ZERO) > 0) {
            //主表中作业量分配
            //查询该部门下是否有班组点名
            List<Long> ids = null;
            Map<String, Object> deptMap = null;
            //根据计划id查询货名信息
            Map<String, Object> cargoMap = tPrdWorkTicketMapper.getWorkCargoInfo(workTicket.getWorkPlanId());
            //根据计划id查询航次信息
            Map<String, Object> shipMap = new HashMap<>();
            TPrdWorkPlanPO plan = tPrdWorkTicketMapper.getWorkPlan(workTicket.getWorkPlanId());
            if ("1".equals(plan.getPlanType())) {
                shipMap = tPrdWorkTicketMapper.getWorkShipInfo(workTicket.getWorkPlanId());
            } else {
                shipMap = tPrdWorkTicketMapper.getTrustWorkShipInfo(workTicket.getWorkPlanId());
            }
            if (isJsg) {
                ids = tPrdWorkTicketMapper.getDeptRollCallItem(workTicket.getWorkDate(), workTicket.getClassCode(), workTicket.getDeptId());
                if (ids == null || ids.size() == 0) {
                    throw new BusinessRuntimeException(workTicket.getDeptName() + "下未出勤点名");
                }
                if (ids.size() > 1) {
                    throw new BusinessRuntimeException(workTicket.getDeptName() + "下出勤点名重复");
                }
                if (!CollectionUtils.isEmpty(ids)) {
                    deptMap = tPrdWorkTicketMapper.getDeptDm(ids);
                }

            } else {
                ids = tPrdWorkTicketMapper.getDeptRollCall(workTicket.getWorkDate(), workTicket.getClassCode(), workTicket.getDeptId());
                if (ids == null || ids.size() == 0) {
                    throw new BusinessRuntimeException(workTicket.getDeptName() + "未出勤点名");
                }
                if (ids.size() > 1) {
                    throw new BusinessRuntimeException(workTicket.getDeptName() + "出勤点名重复");
                }
            }

            List<TPrdAttendanceUserPO> attendanceUsers = tPrdWorkTicketMapper.listAttendanceUser(ids);
            List<TPrdSalaryPO> salaries = new ArrayList<>();
            BigDecimal each = getAllocationMethod(attendanceUsers, workTicket.getTon());
            for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUsers) {
                TPrdSalaryPO salary = new TPrdSalaryPO();
                salary.setId(snowflake.nextId());
                salary.setWorkTicketId(workTicket.getId());
                salary.setCompanyId(workTicket.getCompanyId());
                salary.setCompanyName(workTicket.getCompanyName());
                salary.setWorkDate(workTicket.getWorkDate());
                salary.setClassCode(workTicket.getClassCode());
                salary.setClassName(workTicket.getClassName());
                salary.setPieceProjectCode("1");
                salary.setPieceProjectName("1");
                salary.setProcessCode(workTicket.getProcessCode());
                salary.setProcessName(workTicket.getProcessName());
                if (isJsg) {
                    if (deptMap != null) {
                        salary.setDeptId(Long.parseLong(deptMap.get("deptId").toString()));
                        salary.setDeptName(deptMap.get("deptName").toString());
                    } else {
                        throw new BusinessRuntimeException("获取点名班组信息失败");
                    }
                } else {
                    salary.setDeptId(workTicket.getDeptId());
                    salary.setDeptName(workTicket.getDeptName());
                }
                salary.setUserBy(tPrdAttendanceUserPO.getUserId());
                salary.setUserByName(tPrdAttendanceUserPO.getUserName());
                salary.setCoefficient(tPrdAttendanceUserPO.getCoefficient());
//                salary.setTon(
//                        workTicket.getTon()
//                                .divide(BigDecimal.valueOf(attendanceUsers.size()), 10, RoundingMode.HALF_UP)
//                                .multiply(BigDecimal.valueOf(tPrdAttendanceUserPO.getCoefficient())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
//                );
                salary.setTon(each.multiply(BigDecimal.valueOf(salary.getCoefficient()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                salary.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
                salary.setSalaryStatusName(SalaryStatusEnum._10.getLabel());
                String deptId = tPrdWorkTicketMapper.getBanZu(workTicket.getDeptId());
                if (isJsg) {
                    //库场
                    salary.setSalaryTypeCode(cargoSalaryMap.get("kcTypeCode").toString());
                    salary.setSalaryTypeName(cargoSalaryMap.get("kcTypeName").toString());
                } else {
                    if ("1677243625832058880".equals(deptId)) {
                        //库场
                        salary.setSalaryTypeCode(cargoSalaryMap.get("kcTypeCode").toString());
                        salary.setSalaryTypeName(cargoSalaryMap.get("kcTypeName").toString());
                    } else if ("1677243491865989120".equals(deptId)) {
                        //调度
                        salary.setSalaryTypeCode(cargoSalaryMap.get("ddTypeCode").toString());
                        salary.setSalaryTypeName(cargoSalaryMap.get("ddTypeName").toString());
                    }
                }
                if (cargoMap != null) {
                    salary.setCargoCode(cargoMap.get("cargoCode").toString());
                    salary.setCargoName(cargoMap.get("cargoName").toString());
                }
                if (shipMap != null) {
                    salary.setShipVoyageItemId(shipMap.get("shipVoyageItemId").toString());
                    salary.setShipVoyage(shipMap.get("shipVoyage").toString());
                }
                salaries.add(salary);
            }
            if (!salaries.isEmpty()) {
                tPrdWorkTicketMapper.insertSalary(salaries);

            }
        }
       String ticketType = tPrdWorkTicketMapper.getTicketType(workTicket.getId());
//        if (!isJsg && workTicketTon != null && workTicketTon.compareTo(BigDecimal.ZERO) > 0 && ("1".equals(ticketType))) {
//            //给库场分配一下
//            getKcSalary(workTicketTon, workTicket, cargoSalaryMap);
//        }
//        if (1== 1) {
//            throw new BusinessRuntimeException("111111111111");
//        }
        LOGGER.exit(methodName);
    }

    /**
     * 库场分配计件(特殊分配,直取)
     * @param workTicketTon
     * @param workTicket
     * @param cargoSalaryMap
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void getKcSalary(BigDecimal workTicketTon, TPrdWorkTicketDTO workTicket, Map<String, Object> cargoSalaryMap) {
        //主表中作业量分配
        //查询该部门下是否有班组点名
        List<Long> ids = null;
        Map<String, Object> deptMap = null;
        //根据计划id查询货名信息
        Map<String, Object> cargoMap = tPrdWorkTicketMapper.getWorkCargoInfo(workTicket.getWorkPlanId());
        //根据计划id查询航次信息
        Map<String, Object> shipMap = new HashMap<>();
        TPrdWorkPlanPO plan = tPrdWorkTicketMapper.getWorkPlan(workTicket.getWorkPlanId());
        if ("1".equals(plan.getPlanType())) {
            shipMap = tPrdWorkTicketMapper.getWorkShipInfo(workTicket.getWorkPlanId());
        } else {
            shipMap = tPrdWorkTicketMapper.getTrustWorkShipInfo(workTicket.getWorkPlanId());
        }
        ids = tPrdWorkTicketMapper.getDeptRollCallItem(workTicket.getWorkDate(), workTicket.getClassCode(), Long.parseLong("1677243625832058880"));
        if (ids == null || ids.size() == 0) {
            throw new BusinessRuntimeException("库场队下未出勤点名");
        }
        if (ids.size() > 1) {
            throw new BusinessRuntimeException("库场队下出勤点名重复");
        }
        if (!CollectionUtils.isEmpty(ids)) {
            deptMap = tPrdWorkTicketMapper.getDeptDm(ids);
        }

        List<TPrdAttendanceUserPO> attendanceUsers = tPrdWorkTicketMapper.listAttendanceUser(ids);
        List<TPrdSalaryPO> salaries = new ArrayList<>();
        BigDecimal each = getAllocationMethod(attendanceUsers, workTicketTon);
        for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUsers) {
            TPrdSalaryPO salary = new TPrdSalaryPO();
            salary.setId(snowflake.nextId());
            salary.setWorkTicketId(workTicket.getId());
            salary.setCompanyId(workTicket.getCompanyId());
            salary.setCompanyName(workTicket.getCompanyName());
            salary.setWorkDate(workTicket.getWorkDate());
            salary.setClassCode(workTicket.getClassCode());
            salary.setClassName(workTicket.getClassName());
            salary.setPieceProjectCode("1");
            salary.setPieceProjectName("1");
            salary.setProcessCode(workTicket.getProcessCode());
            salary.setProcessName(workTicket.getProcessName());
            if (deptMap != null) {
                salary.setDeptId(Long.parseLong(deptMap.get("deptId").toString()));
                salary.setDeptName(deptMap.get("deptName").toString());
            } else {
                throw new BusinessRuntimeException("获取点名班组信息失败");
            }
            salary.setUserBy(tPrdAttendanceUserPO.getUserId());
            salary.setUserByName(tPrdAttendanceUserPO.getUserName());
            salary.setCoefficient(tPrdAttendanceUserPO.getCoefficient());
//                salary.setTon(
//                        workTicket.getTon()
//                                .divide(BigDecimal.valueOf(attendanceUsers.size()), 10, RoundingMode.HALF_UP)
//                                .multiply(BigDecimal.valueOf(tPrdAttendanceUserPO.getCoefficient())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
//                );
            salary.setTon(each.multiply(BigDecimal.valueOf(salary.getCoefficient()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            salary.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
            salary.setSalaryStatusName(SalaryStatusEnum._10.getLabel());
            String deptId = tPrdWorkTicketMapper.getBanZu(workTicket.getDeptId());
            //库场
            salary.setSalaryTypeCode(cargoSalaryMap.get("kcTypeCode").toString());
            salary.setSalaryTypeName(cargoSalaryMap.get("kcTypeName").toString());
            if (cargoMap != null) {
                salary.setCargoCode(cargoMap.get("cargoCode").toString());
                salary.setCargoName(cargoMap.get("cargoName").toString());
            }
            if (shipMap != null) {
                salary.setShipVoyageItemId(shipMap.get("shipVoyageItemId").toString());
                salary.setShipVoyage(shipMap.get("shipVoyage").toString());
            }
            salaries.add(salary);
        }
        if (!salaries.isEmpty()) {
            tPrdWorkTicketMapper.insertSalary(salaries);
        }
    }


    /**
     * 最新计件分配工具类
     *
     * @param attendanceUsers 点名数据
     * @param tonSum          作业票作业量
     * @return
     */
    public BigDecimal getAllocationMethod(List<TPrdAttendanceUserPO> attendanceUsers, BigDecimal tonSum) {
        //最新分配计件方法
        Map<Integer, Long> coefficientCountMap = attendanceUsers.stream()
                .collect(Collectors.groupingBy(TPrdAttendanceUserPO::getCoefficient, Collectors.counting()));
        BigDecimal ats = new BigDecimal(0);
        //循环每种值和数量
        for (Map.Entry<Integer, Long> entry : coefficientCountMap.entrySet()) {
            int coefficient = entry.getKey();
            long counts = entry.getValue();
            System.out.println("coefficient 值: " + coefficient + ", 数量: " + counts);
            BigDecimal coefficientBigDecimal = BigDecimal.valueOf(coefficient);
            BigDecimal countsBigDecimal = BigDecimal.valueOf(counts);
            BigDecimal multiplicationResult = coefficientBigDecimal
                    .divide(BigDecimal.valueOf(100))
                    .multiply(countsBigDecimal);
            //加起来
            ats = ats.add(multiplicationResult);
        }
        //平均每份分到的作业量
        return tonSum.divide(ats, 3, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void reviewWorkTicket(TicketPlanIdDTO ticketPlanIdDTO) {
        List<Map<String, Object>> workPlanIdList = ticketPlanIdDTO.getWorkPlanIdList();
        if (workPlanIdList != null && workPlanIdList.size() != 0) {
            for (Map<String, Object> map : workPlanIdList) {
                Long workPlanId = Long.parseLong(map.get("workPlanId").toString());
                String ticketType = map.get("ticketType").toString();
                //作业票主表详情
                TPrdWorkTicketDTO workTicket = tPrdWorkTicketMapper.getWorkTicket(workPlanId, ticketType, "2");
                TPrdWorkTicketPO tempWorkTicket = new TPrdWorkTicketPO();
                tempWorkTicket.setWorkPlanId(workPlanId);
                tempWorkTicket.setWorkTicketStatus(WorkTicketStatusEnum._20.getCode());
                tempWorkTicket.setWorkTicketStatusName(WorkTicketStatusEnum._20.getName());
                tempWorkTicket.setTicketType(ticketType);
                tPrdWorkTicketMapper.reviewWorkTicket(tempWorkTicket);
                //作业票子表详情
                List<TPrdWorkTicketDetailDTO> workTicketDetails = tPrdWorkTicketMapper.listWorkTicketDetail(workTicket.getId());
                //调用处理公共方法
                commWorkTicketOperate(workTicket, workTicketDetails, false, false, "船舶计划作业票审核");
            }
        } else {
            throw new BusinessRuntimeException("请至少选择一条计划");
        }

    }

//    @Override
//    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
//    public void reviewWorkTicket(TicketPlanIdDTO ticketPlanIdDTO) {
//        List<Map<String, Object>> workPlanIdList = ticketPlanIdDTO.getWorkPlanIdList();
//        if (workPlanIdList != null && workPlanIdList.size() != 0) {
//            for (Map<String, Object> map : workPlanIdList) {
//                Long workPlanId = Long.parseLong(map.get("workPlanId").toString());
//                String ticketType = map.get("ticketType").toString();
//                //commWorkTicketOperate(workTicket,workTicketDetails,false);
//                //作业票主表详情
//                TPrdWorkTicketDTO workTicket = tPrdWorkTicketMapper.getWorkTicket(workPlanId, ticketType, "2");
//                if (workTicket == null) {
//                    throw new BusinessRuntimeException("当前作业计划未签票");
//                }
//                if (WorkTicketStatusEnum._20.getCode().equals(workTicket.getWorkTicketStatus())) {
//                    throw new BusinessRuntimeException("当前作业计划签票已审核");
//                }
//
//                TPrdWorkTicketPO tempWorkTicket = new TPrdWorkTicketPO();
//                tempWorkTicket.setWorkPlanId(workPlanId);
//                tempWorkTicket.setWorkTicketStatus(WorkTicketStatusEnum._20.getCode());
//                tempWorkTicket.setWorkTicketStatusName(WorkTicketStatusEnum._20.getName());
//                tempWorkTicket.setTicketType(ticketType);
//                tPrdWorkTicketMapper.reviewWorkTicket(tempWorkTicket);
//
//                //作业票子表详情
//                Map<String, Object> cargoSalaryMap = new HashMap<>();
//                List<TPrdWorkTicketDetailDTO> workTicketDetails = tPrdWorkTicketMapper.listWorkTicketDetail(workTicket.getId());
//                if (workTicketDetails.get(0).getCargoCode() != null) {
//                    cargoSalaryMap = tPrdWorkTicketMapper.getCargoSalaryType(workTicketDetails.get(0).getCargoCode());
//                    if (cargoSalaryMap == null) {
//                        throw new BusinessRuntimeException("未查询到" + workTicketDetails.get(0).getCargoName() + "的计件类型详情");
//                    }
//                }
//
//                if (workTicketDetails != null && workTicketDetails.size() != 0) {
//
//                    for (TPrdWorkTicketDetailDTO dto : workTicketDetails) {
//
//                        //查询该货物是否需要更新场存
//                        if (dto.getCargoCode() == null) {
//                            throw new BusinessRuntimeException("获取签票货物信息失败，请检查签票信息是否正确");
//                        }
//                        Integer cargoFlag = tPrdWorkTicketMapper.getCargoIsUpdate(dto.getCargoCode());
//                        if (cargoFlag != null && cargoFlag == 2) {
//                            //查询该作业过程是否更新场存
//                            if (dto.getProcessDetailCode() == null) {
//                                throw new BusinessRuntimeException("获取签票作业过程信息失败，请检查签票信息是否正确");
//                            }
//                            Map<String, Object> processFlag = tPrdWorkTicketMapper.getProcessIsUpdate(dto.getProcessDetailCode());
//                            if (processFlag != null && "1".equals(processFlag.get("updatePoint").toString())) {
//                                //需要更新场存
//                                if ("201".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
//                                    //出库
//                                    if (dto.getStorehouseIdSource() == null) {
//                                        throw new BusinessRuntimeException("货物为" + workTicketDetails.get(0).getCargoName() + ",作业过程为" + dto.getProcessDetailName() + "的起始区域信息获取失败,请修改签票的起始区域信息");
//                                    }
//                                    //起始位置减量
//                                    List<TPrdPortStorageDetailPO> portStorageDetails = new ArrayList<>();
//                                    TPrdPortStorageDetailPO tPrdPortStorageDetailPO = new TPrdPortStorageDetailPO();
//                                    tPrdPortStorageDetailPO.setWorkTicketId(workTicket.getId()); //作业票主表ID
//                                    tPrdPortStorageDetailPO.setWorkTicketDetailId(dto.getId()); //作业票子表ID
//                                    tPrdPortStorageDetailPO.setWorkDate(workTicket.getWorkDate());
//                                    tPrdPortStorageDetailPO.setClassCode(workTicket.getClassCode()); //班次
//                                    tPrdPortStorageDetailPO.setClassName(workTicket.getClassName());
//                                    tPrdPortStorageDetailPO.setProcessDetailCode(dto.getProcessDetailCode()); //作业过程
//                                    tPrdPortStorageDetailPO.setProcessDetailName(dto.getProcessDetailName());
//                                    tPrdPortStorageDetailPO.setInoutStorageCode(InoutStorageEnum._20.getCode()); //类型
//                                    tPrdPortStorageDetailPO.setInoutStorageName(InoutStorageEnum._20.getLabel());
//                                    tPrdPortStorageDetailPO.setInoutDate(workTicket.getWorkDate());
//                                    tPrdPortStorageDetailPO.setCleanMassSign(CleanMassSignEnum._0.getCode());
//                                    tPrdPortStorageDetailPO.setCompanyId(workTicket.getCompanyId()); //公司信息
//                                    tPrdPortStorageDetailPO.setCompanyName(workTicket.getCompanyName());
//                                    tPrdPortStorageDetailPO.setCargoInfoId(dto.getCargoInfoId());
//                                    tPrdPortStorageDetailPO.setQuantity(dto.getQuantity() == null ? 0 : -dto.getQuantity()); //件数
//                                    tPrdPortStorageDetailPO.setTon(dto.getTon().negate()); //重量
//                                    tPrdPortStorageDetailPO.setStorehouseId(dto.getStorehouseIdSource()); //库场
//                                    tPrdPortStorageDetailPO.setStorehouseName(dto.getStorehouseNameSource());
//                                    tPrdPortStorageDetailPO.setRegionId(dto.getRegionIdSource());
//                                    tPrdPortStorageDetailPO.setRegionName(dto.getRegionNameSource());
//                                    tPrdPortStorageDetailPO.setMassId(dto.getMassIdSource());
//                                    tPrdPortStorageDetailPO.setMassName(dto.getMassNameSource());
//                                    portStorageDetails.add(tPrdPortStorageDetailPO);
//                                    try {
//                                        businessCommonService.insertPortStorageDetail(portStorageDetails);
//                                    } catch (BusinessRuntimeException e) {
//                                        if (!"当前港存已清场".equals(e.getMessage())) {
//                                            throw new BusinessRuntimeException(e.getMessage());
//                                        }
//                                    }
//
//                                }
//
//                                if ("101".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
//                                    //入库
//                                    //终点位置加量
//                                    if (dto.getStorehouseIdTarget() == null) {
//                                        throw new BusinessRuntimeException("货物为" + workTicketDetails.get(0).getCargoName() + ",作业过程为" + dto.getProcessDetailName() + "的终点区域信息获取失败,请修改签票的终点区域信息");
//                                    }
//                                    List<TPrdPortStorageDetailPO> portStorageTaDetails = new ArrayList<>();
//                                    TPrdPortStorageDetailPO tPrdPortStorageTaDetailPO = new TPrdPortStorageDetailPO();
//                                    tPrdPortStorageTaDetailPO.setWorkTicketId(workTicket.getId()); //作业票主表ID
//                                    tPrdPortStorageTaDetailPO.setWorkTicketDetailId(dto.getId()); //作业票子表ID
//                                    tPrdPortStorageTaDetailPO.setWorkDate(workTicket.getWorkDate());
//                                    tPrdPortStorageTaDetailPO.setClassCode(workTicket.getClassCode()); //班次
//                                    tPrdPortStorageTaDetailPO.setClassName(workTicket.getClassName());
//                                    tPrdPortStorageTaDetailPO.setProcessDetailCode(dto.getProcessDetailCode()); //作业过程
//                                    tPrdPortStorageTaDetailPO.setProcessDetailName(dto.getProcessDetailName());
//                                    tPrdPortStorageTaDetailPO.setInoutStorageCode(InoutStorageEnum._20.getCode()); //类型
//                                    tPrdPortStorageTaDetailPO.setInoutStorageName(InoutStorageEnum._20.getLabel());
//                                    tPrdPortStorageTaDetailPO.setInoutDate(workTicket.getWorkDate());
//                                    tPrdPortStorageTaDetailPO.setCleanMassSign(CleanMassSignEnum._0.getCode());
//                                    tPrdPortStorageTaDetailPO.setCompanyId(workTicket.getCompanyId()); //公司信息
//                                    tPrdPortStorageTaDetailPO.setCompanyName(workTicket.getCompanyName());
//                                    tPrdPortStorageTaDetailPO.setCargoInfoId(dto.getCargoInfoId());
//                                    tPrdPortStorageTaDetailPO.setQuantity(dto.getQuantity() == null ? 0 : dto.getQuantity()); //件数
//                                    tPrdPortStorageTaDetailPO.setTon(dto.getTon()); //重量
//                                    tPrdPortStorageTaDetailPO.setStorehouseId(dto.getStorehouseIdTarget()); //库场
//                                    tPrdPortStorageTaDetailPO.setStorehouseName(dto.getStorehouseNameTarget());
//                                    tPrdPortStorageTaDetailPO.setRegionId(dto.getRegionIdTarget());
//                                    tPrdPortStorageTaDetailPO.setRegionName(dto.getRegionNameTarget());
//                                    tPrdPortStorageTaDetailPO.setMassId(dto.getMassIdTarget());
//                                    tPrdPortStorageTaDetailPO.setMassName(dto.getMassNameTarget());
//                                    portStorageTaDetails.add(tPrdPortStorageTaDetailPO);
//                                    try {
//                                        businessCommonService.insertPortStorageDetail(portStorageTaDetails);
//                                    } catch (BusinessRuntimeException e) {
//                                        if (!"当前港存已清场".equals(e.getMessage())) {
//                                            throw new BusinessRuntimeException(e.getMessage());
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        //计件分配
//                        //判断班组是否为内部班组,内部班组分配作业量
//                        if (dto.getDeptId() != null) {
//                            String internal = tPrdWorkTicketMapper.getDeptInternal(dto.getDeptId());
//                            if ("I".equals(internal)) {
//                                //内部班组 操作工班
//                                //查询该部门下是否有班组点名
//                                List<Long> ids = tPrdWorkTicketMapper.getDeptRollCallItem(workTicket.getWorkDate(), workTicket.getClassCode(), dto.getDeptId());
//                                if (ids.size() == 0) {
//                                    throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组未出勤点名");
//                                }
//                                if (ids.size() > 1) {
//                                    throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组出勤点名重复");
//                                }
//                                List<TPrdAttendanceUserPO> attendanceUsers = tPrdWorkTicketMapper.listAttendanceUser(ids);
//                                Map<String, Object> deptMap = tPrdWorkTicketMapper.getDeptDm(ids);
//                                List<TPrdSalaryPO> salaries = new ArrayList<>();
//                                for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUsers) {
//                                    TPrdSalaryPO salary = new TPrdSalaryPO();
//                                    salary.setId(snowflake.nextId());
//                                    salary.setWorkTicketDetailId(dto.getId());
//                                    salary.setCompanyId(workTicket.getCompanyId());
//                                    salary.setCompanyName(workTicket.getCompanyName());
//                                    salary.setWorkDate(workTicket.getWorkDate());
//                                    salary.setClassCode(workTicket.getClassCode());
//                                    salary.setClassName(workTicket.getClassName());
//                                    salary.setPieceProjectCode("1");
//                                    salary.setPieceProjectName("1");
//                                    salary.setProcessDetailCode(dto.getProcessDetailCode());
//                                    salary.setProcessDetailName(dto.getProcessDetailName());
//                                    if (deptMap != null) {
//                                        salary.setDeptId(Long.parseLong(deptMap.get("deptId").toString()));
//                                        salary.setDeptName(deptMap.get("deptName").toString());
//                                    } else {
//                                        throw new BusinessRuntimeException("获取点名班组信息失败");
//                                    }
//                                    salary.setUserBy(tPrdAttendanceUserPO.getUserId());
//                                    salary.setUserByName(tPrdAttendanceUserPO.getUserName());
//                                    salary.setCoefficient(tPrdAttendanceUserPO.getCoefficient());
//                                    salary.setTon(
//                                            dto.getTon()
//                                                    .divide(BigDecimal.valueOf(attendanceUsers.size()), 10, RoundingMode.HALF_UP)
//                                                    .multiply(BigDecimal.valueOf(tPrdAttendanceUserPO.getCoefficient())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
//                                    );
//                                    salary.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
//                                    salary.setSalaryStatusName(SalaryStatusEnum._10.getLabel());
//
//                                    //计件类型
//                                    //查询货物计件类型
//                                    if ("1710197535592812544".equals(dto.getDeptId().toString())) {
//                                        //固机
//                                        salary.setSalaryTypeCode(cargoSalaryMap.get("gjTypeCode").toString());
//                                        salary.setSalaryTypeName(cargoSalaryMap.get("gjTypeName").toString());
//                                    } else if ("1710203467898949632".equals(dto.getDeptId().toString())) {
//                                        //流机
//                                        salary.setSalaryTypeCode(cargoSalaryMap.get("ljTypeCode").toString());
//                                        salary.setSalaryTypeName(cargoSalaryMap.get("ljTypeName").toString());
//                                    }
//
//                                    salaries.add(salary);
//                                }
//                                if (!salaries.isEmpty()) {
//                                    tPrdWorkTicketMapper.insertSalary(salaries);
//
//                                }
//
//                            }
//                        }
//
//                        //人员工班
//                        if (dto.getPieceWorkTeamId() != null) {
//                            String internalPer = tPrdWorkTicketMapper.getDeptInternal(dto.getPieceWorkTeamId());
//                            if ("I".equals(internalPer)) {
//                                //内部班组
//                                //查询该部门下是否有班组点名
//                                List<Long> ids = tPrdWorkTicketMapper.getDeptRollCall(workTicket.getWorkDate(), workTicket.getClassCode(), dto.getPieceWorkTeamId());
//                                if (ids.size() == 0) {
//                                    throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组未出勤点名");
//                                }
//                                if (ids.size() > 1) {
//                                    throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组出勤点名重复");
//                                }
//                                List<TPrdAttendanceUserPO> attendanceUsers = tPrdWorkTicketMapper.listAttendanceUser(ids);
//                                Map<String, Object> deptMap = tPrdWorkTicketMapper.getDeptDm(ids);
//                                List<TPrdSalaryPO> salariesPer = new ArrayList<>();
//                                for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUsers) {
//                                    TPrdSalaryPO salary = new TPrdSalaryPO();
//                                    salary.setId(snowflake.nextId());
//                                    salary.setWorkTicketDetailId(dto.getId());
//                                    salary.setCompanyId(workTicket.getCompanyId());
//                                    salary.setCompanyName(workTicket.getCompanyName());
//                                    salary.setWorkDate(workTicket.getWorkDate());
//                                    salary.setClassCode(workTicket.getClassCode());
//                                    salary.setClassName(workTicket.getClassName());
//                                    salary.setPieceProjectCode("1");
//                                    salary.setPieceProjectName("1");
//                                    salary.setProcessDetailCode(dto.getProcessDetailCode());
//                                    salary.setProcessDetailName(dto.getProcessDetailName());
//                                    if (deptMap != null) {
//                                        salary.setDeptId(Long.parseLong(deptMap.get("deptId").toString()));
//                                        salary.setDeptName(deptMap.get("deptName").toString());
//                                    } else {
//                                        throw new BusinessRuntimeException("获取点名班组信息失败");
//                                    }
//                                    salary.setUserBy(tPrdAttendanceUserPO.getUserId());
//                                    salary.setUserByName(tPrdAttendanceUserPO.getUserName());
//                                    salary.setCoefficient(tPrdAttendanceUserPO.getCoefficient());
//                                    salary.setTon(
//                                            dto.getTon()
//                                                    .divide(BigDecimal.valueOf(attendanceUsers.size()), 10, RoundingMode.HALF_UP)
//                                                    .multiply(BigDecimal.valueOf(tPrdAttendanceUserPO.getCoefficient())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
//                                    );
//                                    salary.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
//                                    salary.setSalaryStatusName(SalaryStatusEnum._10.getLabel());
//                                    salariesPer.add(salary);
//                                }
//                                if (!salariesPer.isEmpty()) {
//                                    tPrdWorkTicketMapper.insertSalary(salariesPer);
//
//                                }
//
//                            }
//                        }
//
//                    }
//                } else {
//                    throw new BusinessRuntimeException("获取签票明细信息失败，请检查签票信息是否正确");
//                }
//                if (workTicket.getTon() != null) {
//                    //主表中作业量分配
//                    //查询该部门下是否有班组点名
//                    List<Long> ids = tPrdWorkTicketMapper.getDeptRollCall(workTicket.getWorkDate(), workTicket.getClassCode(), workTicket.getDeptId());
//                    if (ids == null || ids.size() == 0) {
//                        throw new BusinessRuntimeException(workTicket.getDeptName() + "未出勤点名");
//                    }
//                    if (ids.size() > 1) {
//                        throw new BusinessRuntimeException(workTicket.getDeptName() + "出勤点名重复");
//                    }
//                    List<TPrdAttendanceUserPO> attendanceUsers = tPrdWorkTicketMapper.listAttendanceUser(ids);
//                    List<TPrdSalaryPO> salaries = new ArrayList<>();
//                    for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUsers) {
//                        TPrdSalaryPO salary = new TPrdSalaryPO();
//                        salary.setId(snowflake.nextId());
//                        salary.setWorkTicketDetailId(workTicketDetails.get(0).getId());
//                        salary.setCompanyId(workTicket.getCompanyId());
//                        salary.setCompanyName(workTicket.getCompanyName());
//                        salary.setWorkDate(workTicket.getWorkDate());
//                        salary.setClassCode(workTicket.getClassCode());
//                        salary.setClassName(workTicket.getClassName());
//                        salary.setPieceProjectCode("1");
//                        salary.setPieceProjectName("1");
//                        salary.setProcessDetailCode(workTicket.getProcessCode());
//                        salary.setProcessDetailName(workTicket.getProcessName());
//                        salary.setDeptId(workTicket.getDeptId());
//                        salary.setDeptName(workTicket.getDeptName());
//                        salary.setUserBy(tPrdAttendanceUserPO.getUserId());
//                        salary.setUserByName(tPrdAttendanceUserPO.getUserName());
//                        salary.setCoefficient(tPrdAttendanceUserPO.getCoefficient());
//                        salary.setTon(
//                                workTicket.getTon()
//                                        .divide(BigDecimal.valueOf(attendanceUsers.size()), 10, RoundingMode.HALF_UP)
//                                        .multiply(BigDecimal.valueOf(tPrdAttendanceUserPO.getCoefficient())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
//                        );
//                        salary.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
//                        salary.setSalaryStatusName(SalaryStatusEnum._10.getLabel());
//                        String deptId = tPrdWorkTicketMapper.getBanZu(workTicket.getDeptId());
//                        if ("1677243625832058880".equals(deptId)) {
//                            //库场
//                            salary.setSalaryTypeCode(cargoSalaryMap.get("kcTypeCode").toString());
//                            salary.setSalaryTypeName(cargoSalaryMap.get("kcTypeName").toString());
//                        } else if ("1677243491865989120".equals(deptId)) {
//                            //调度
//                            salary.setSalaryTypeCode(cargoSalaryMap.get("ddTypeCode").toString());
//                            salary.setSalaryTypeName(cargoSalaryMap.get("ddTypeName").toString());
//                        }
//                        salaries.add(salary);
//                    }
//                    if (!salaries.isEmpty()) {
//                        tPrdWorkTicketMapper.insertSalary(salaries);
//
//                    }
//                }
//            }
//        } else {
//            throw new BusinessRuntimeException("请至少选择一条计划");
//        }
//
//    }


//    @Override
//    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
//    public void reviewWorkTicketJsg(TPrdWorkPlanJsgDTO tPrdWorkPlanJsgDTO) {
//        List<Map<String, Object>> workPlanIdList = new ArrayList<>();
//        List<TPrdWorkPlanJsgDetailDTO> workPlanDetailList = tPrdWorkPlanJsgDTO.getWorkPlanList();
//        if (workPlanDetailList != null && workPlanDetailList.size() != 0) {
//            for (TPrdWorkPlanJsgDetailDTO dtoPlan : workPlanDetailList) {
//                //作业票主表详情
//                TPrdWorkTicketDTO ticketInfo = tPrdWorkTicketMapper.getWorkTicket(dtoPlan.getPlanId(), "2", "1");
//                if (ticketInfo != null) {
//                    throw new BusinessRuntimeException("当前作业计划签票已审核");
//                }
//                TPrdWorkTicketDTO ticket = tPrdWorkTicketMapper.getWorkTicket(dtoPlan.getPlanId(), "2", "2");
//                if (ticket != null) {
//                    //已经签过的(件杂)
//                    Map maps = new HashMap();
//                    maps.put("ticketType", "2");
//                    maps.put("workPlanId", dtoPlan.getPlanId());
//                    workPlanIdList.add(maps);
//                } else {
//                    //计划详情
//                    List<TPrdWorkPlanDTO> tPrdWorkPlanList = tPrdWorkTicketMapper.listWorkPlanById(dtoPlan.getPlanId());
//                    TPrdWorkPlanDTO tPrdWorkPlanDTO = tPrdWorkPlanList.get(0);
//                    //先添加签票
//                    TPrdWorkTicketDTO workTicket = new TPrdWorkTicketDTO();
//                    List<TPrdWorkTicketDetailDTO> detailDTOS = new ArrayList<>();
//                    Long zid = snowflake.nextId();
//                    workTicket.setId(zid);
//                    workTicket.setWorkPlanId(dtoPlan.getPlanId());
//                    workTicket.setCompanyId(tPrdWorkPlanDTO.getCompanyId());
//                    workTicket.setCompanyName(tPrdWorkPlanDTO.getCompanyName());
//                    workTicket.setWorkDate(tPrdWorkPlanDTO.getWorkDate());
//                    workTicket.setClassCode(tPrdWorkPlanDTO.getClassCode());
//                    workTicket.setClassName(tPrdWorkPlanDTO.getClassName());
//                    workTicket.setType(tPrdWorkPlanDTO.getPlanType());
//                    workTicket.setWorkTicketStatus(WorkTicketStatusEnum._20.getCode());
//                    workTicket.setWorkTicketStatusName(WorkTicketStatusEnum._20.getName());
//                    workTicket.setDeptId(Long.parseLong("1677243625832058880"));
//                    workTicket.setDeptName("库场队");
//                    workTicket.setTicketType("2");
//                    workTicket.setProcessCode(tPrdWorkPlanDTO.getProcessCode());
//                    workTicket.setProcessName(tPrdWorkPlanDTO.getProcessName());
//                    //子表信息
//                    List<TPrdWorkTiTckInfoDTO> WorkTiTckDetailList = dtoPlan.getInfoDTOList();
//                    Integer quantity = 0;
//                    BigDecimal ton = new BigDecimal(0);
//                    List<TPrdWorkTicketEquipmentPO> equipments = new ArrayList<>();
//                    for (TPrdWorkTiTckInfoDTO tPrdWorkTiTckInfoDTO : WorkTiTckDetailList) {
//                        TPrdWorkTicketDetailDTO detailDTO = new TPrdWorkTicketDetailDTO();
//                        long detailId = snowflake.nextId();
//                        detailDTO.setId(detailId);
//                        detailDTO.setWorkTicketId(zid);
//                        detailDTO.setProcessCode(tPrdWorkPlanDTO.getProcessCode());
//                        detailDTO.setProcessName(tPrdWorkPlanDTO.getProcessName());
//                        detailDTO.setWorkDate(tPrdWorkPlanDTO.getWorkDate());
//                        detailDTO.setClassCode(tPrdWorkPlanDTO.getClassCode());
//                        detailDTO.setClassName(tPrdWorkPlanDTO.getClassName());
//                        detailDTO.setTrustId(tPrdWorkTiTckInfoDTO.getTrustId());
//                        detailDTO.setTrustCargoInfoId(tPrdWorkTiTckInfoDTO.getTrustCargoInfoId());
//                        detailDTO.setCargoInfoId(tPrdWorkTiTckInfoDTO.getCargoInfoId());
//                        detailDTO.setProcessDetailCode(tPrdWorkTiTckInfoDTO.getProcessDetailCode());
//                        detailDTO.setProcessDetailName(tPrdWorkTiTckInfoDTO.getProcessDetailName());
//                        detailDTO.setDeptId(tPrdWorkTiTckInfoDTO.getDeptItemId());
//                        detailDTO.setDeptName(tPrdWorkTiTckInfoDTO.getDeptItemName());
//                        detailDTO.setCargoCode(tPrdWorkTiTckInfoDTO.getCargoCode());
//                        detailDTO.setCargoName(tPrdWorkTiTckInfoDTO.getCargoName());
//                        detailDTO.setShipvoyageId(tPrdWorkTiTckInfoDTO.getShipvoyageId());
//                        detailDTO.setShipvoyageItemId(tPrdWorkTiTckInfoDTO.getShipvoyageItemId());
//                        if (tPrdWorkTiTckInfoDTO.getStorehouseIdSource() != null) {
//                            detailDTO.setStorehouseIdSource(tPrdWorkTiTckInfoDTO.getStorehouseIdSource());
//                            detailDTO.setStorehouseNameSource(tPrdWorkTiTckInfoDTO.getStorehouseNameSource());
//                            detailDTO.setRegionIdSource(tPrdWorkTiTckInfoDTO.getRegionIdSource());
//                            detailDTO.setRegionNameSource(tPrdWorkTiTckInfoDTO.getRegionNameSource());
//                            detailDTO.setMassIdSource(tPrdWorkTiTckInfoDTO.getMassIdSource());
//                            detailDTO.setMassNameSource(tPrdWorkTiTckInfoDTO.getMassNameSource());
//                        }
//                        if (tPrdWorkTiTckInfoDTO.getStorehouseIdTarget() != null) {
//                            detailDTO.setStorehouseIdTarget(tPrdWorkTiTckInfoDTO.getStorehouseIdTarget());
//                            detailDTO.setStorehouseNameTarget(tPrdWorkTiTckInfoDTO.getStorehouseNameTarget());
//                            detailDTO.setRegionIdTarget(tPrdWorkTiTckInfoDTO.getRegionIdTarget());
//                            detailDTO.setRegionNameTarget(tPrdWorkTiTckInfoDTO.getRegionNameTarget());
//                            detailDTO.setMassIdTarget(tPrdWorkTiTckInfoDTO.getMassIdTarget());
//                            detailDTO.setMassNameTarget(tPrdWorkTiTckInfoDTO.getMassNameTarget());
//                        }
//                        detailDTO.setQuantity(tPrdWorkTiTckInfoDTO.getQuantity());
//                        detailDTO.setTon(tPrdWorkTiTckInfoDTO.getTon());
//                        detailDTO.setStartTime(tPrdWorkTiTckInfoDTO.getStartTime());
//                        detailDTO.setEndTime(tPrdWorkTiTckInfoDTO.getEndTime());
//                        detailDTO.setPieceWorkTeamId(tPrdWorkTiTckInfoDTO.getPersonNelId());
//                        detailDTO.setPieceWorkTeamName(tPrdWorkTiTckInfoDTO.getPersonNelName());
//                        //处理机械
//                        List<String> listEquipmentId = Arrays.asList(tPrdWorkTiTckInfoDTO.getEquipmentId().split(","));
//                        List<String> listEquipmentNo = Arrays.asList(tPrdWorkTiTckInfoDTO.getEquipmentNo().split(","));
//                        if (listEquipmentId != null && listEquipmentId.size() != 0) {
//                            for (int i = 0; i < listEquipmentId.size(); i++) {
//                                TPrdWorkTicketEquipmentPO equipmentPO = new TPrdWorkTicketEquipmentPO();
//                                equipmentPO.setId(snowflake.nextId());
//                                equipmentPO.setWorkTicketDetailId(detailId);
//                                equipmentPO.setEquipmentId(Long.parseLong(listEquipmentId.get(i)));
//                                equipmentPO.setEquipmentNo(listEquipmentNo.get(i));
//                                equipmentPO.setEquipmentTypeCode(tPrdWorkTiTckInfoDTO.getEquipmentTypeCode());
//                                equipmentPO.setEquipmentTypeName(tPrdWorkTiTckInfoDTO.getEquipmentTypeName());
//                                equipments.add(equipmentPO);
//                            }
//                        }
//                        //作业过程是理货量的作业量相加
//                        Integer count = tPrdWorkTicketMapper.getIsProcrss(tPrdWorkTiTckInfoDTO.getProcessDetailCode());
//                        if (count == 1) {
//                            if (tPrdWorkTiTckInfoDTO.getQuantity() != null) {
//                                quantity += tPrdWorkTiTckInfoDTO.getQuantity();
//                            }
//                            if (tPrdWorkTiTckInfoDTO.getTon() == null) {
//                                throw new BusinessRuntimeException("重量不能为空");
//                            }
//                            ton = ton.add(tPrdWorkTiTckInfoDTO.getTon());
//                        }
//                        detailDTOS.add(detailDTO);
//                    }
//                    workTicket.setQuantity(quantity);
//                    workTicket.setTon(ton);
//                    tPrdWorkTicketMapper.insertWorkTicketJsg(workTicket);
//                    tPrdWorkTicketMapper.insertWorkTicketDetail(detailDTOS);
//                    if (CollectionUtils.isNotEmpty(equipments)) {
//                        tPrdWorkTicketMapper.insertWorkTicketEquipment(equipments);
//                    }
//
//                    //作业票子表详情
//                    List<TPrdWorkTicketDetailDTO> workTicketDetails = detailDTOS;
//                    Map<String, Object> cargoSalaryMap = tPrdWorkTicketMapper.getCargoSalaryType(workTicketDetails.get(0).getCargoCode());
//                    if (cargoSalaryMap == null) {
//                        throw new BusinessRuntimeException("未查询到" + workTicketDetails.get(0).getCargoName() + "的计件类型详情");
//                    }
//                    if (workTicketDetails != null && workTicketDetails.size() != 0) {
//                        for (TPrdWorkTicketDetailDTO dto : workTicketDetails) {
//                            //查询该货物是否需要更新场存
//                            if (dto.getCargoCode() == null) {
//                                throw new BusinessRuntimeException("获取签票货物信息失败，请检查签票信息是否正确");
//                            }
//                            Integer cargoFlag = tPrdWorkTicketMapper.getCargoIsUpdate(dto.getCargoCode());
//                            if (cargoFlag != null && cargoFlag == 2) {
//                                //查询该作业过程是否更新场存
//                                if (dto.getProcessDetailCode() == null) {
//                                    throw new BusinessRuntimeException("获取签票作业过程信息失败，请检查签票信息是否正确");
//                                }
//                                Map<String, Object> processFlag = tPrdWorkTicketMapper.getProcessIsUpdate(dto.getProcessDetailCode());
//                                if (processFlag != null && "1".equals(processFlag.get("updatePoint").toString())) {
//                                    //需要更新场存
//                                    if ("201".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
//                                        //出库
//                                        if (dto.getStorehouseIdSource() == null) {
//                                            throw new BusinessRuntimeException("货物为" + workTicketDetails.get(0).getCargoName() + ",作业过程为" + dto.getProcessDetailName() + "的起始区域信息获取失败,请修改签票的起始区域信息");
//                                        }
//                                        //起始位置减量
//                                        List<TPrdPortStorageDetailPO> portStorageDetails = new ArrayList<>();
//                                        TPrdPortStorageDetailPO tPrdPortStorageDetailPO = new TPrdPortStorageDetailPO();
//                                        tPrdPortStorageDetailPO.setWorkTicketId(workTicket.getId()); //作业票主表ID
//                                        tPrdPortStorageDetailPO.setWorkTicketDetailId(dto.getId()); //作业票子表ID
//                                        tPrdPortStorageDetailPO.setWorkDate(workTicket.getWorkDate());
//                                        tPrdPortStorageDetailPO.setClassCode(workTicket.getClassCode()); //班次
//                                        tPrdPortStorageDetailPO.setClassName(workTicket.getClassName());
//                                        tPrdPortStorageDetailPO.setProcessDetailCode(dto.getProcessDetailCode()); //作业过程
//                                        tPrdPortStorageDetailPO.setProcessDetailName(dto.getProcessDetailName());
//                                        tPrdPortStorageDetailPO.setInoutStorageCode(InoutStorageEnum._20.getCode()); //类型
//                                        tPrdPortStorageDetailPO.setInoutStorageName(InoutStorageEnum._20.getLabel());
//                                        tPrdPortStorageDetailPO.setInoutDate(workTicket.getWorkDate());
//                                        tPrdPortStorageDetailPO.setCleanMassSign(CleanMassSignEnum._0.getCode());
//                                        tPrdPortStorageDetailPO.setCompanyId(workTicket.getCompanyId()); //公司信息
//                                        tPrdPortStorageDetailPO.setCompanyName(workTicket.getCompanyName());
//                                        tPrdPortStorageDetailPO.setCargoInfoId(dto.getCargoInfoId());
//                                        tPrdPortStorageDetailPO.setQuantity(dto.getQuantity() == null ? 0 : -dto.getQuantity()); //件数
//                                        tPrdPortStorageDetailPO.setTon(dto.getTon().negate()); //重量
//                                        tPrdPortStorageDetailPO.setStorehouseId(dto.getStorehouseIdSource()); //库场
//                                        tPrdPortStorageDetailPO.setStorehouseName(dto.getStorehouseNameSource());
//                                        tPrdPortStorageDetailPO.setRegionId(dto.getRegionIdSource());
//                                        tPrdPortStorageDetailPO.setRegionName(dto.getRegionNameSource());
//                                        tPrdPortStorageDetailPO.setMassId(dto.getMassIdSource());
//                                        tPrdPortStorageDetailPO.setMassName(dto.getMassNameSource());
//                                        portStorageDetails.add(tPrdPortStorageDetailPO);
//                                        try {
//                                            businessCommonService.insertPortStorageDetail(portStorageDetails);
//                                        } catch (BusinessRuntimeException e) {
//                                            if (!"当前港存已清场".equals(e.getMessage())) {
//                                                throw new BusinessRuntimeException(e.getMessage());
//                                            }
//                                        }
//                                    }
//
//                                    if ("101".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
//                                        //入库
//                                        //终点位置加量
//                                        if (dto.getStorehouseIdTarget() == null) {
//                                            throw new BusinessRuntimeException("货物为" + workTicketDetails.get(0).getCargoName() + ",作业过程为" + dto.getProcessDetailName() + "的终点区域信息获取失败,请修改签票的终点区域信息");
//                                        }
//                                        List<TPrdPortStorageDetailPO> portStorageTaDetails = new ArrayList<>();
//                                        TPrdPortStorageDetailPO tPrdPortStorageTaDetailPO = new TPrdPortStorageDetailPO();
//                                        tPrdPortStorageTaDetailPO.setWorkTicketId(workTicket.getId()); //作业票主表ID
//                                        tPrdPortStorageTaDetailPO.setWorkTicketDetailId(dto.getId()); //作业票子表ID
//                                        tPrdPortStorageTaDetailPO.setWorkDate(workTicket.getWorkDate());
//                                        tPrdPortStorageTaDetailPO.setClassCode(workTicket.getClassCode()); //班次
//                                        tPrdPortStorageTaDetailPO.setClassName(workTicket.getClassName());
//                                        tPrdPortStorageTaDetailPO.setProcessDetailCode(dto.getProcessDetailCode()); //作业过程
//                                        tPrdPortStorageTaDetailPO.setProcessDetailName(dto.getProcessDetailName());
//                                        tPrdPortStorageTaDetailPO.setInoutStorageCode(InoutStorageEnum._20.getCode()); //类型
//                                        tPrdPortStorageTaDetailPO.setInoutStorageName(InoutStorageEnum._20.getLabel());
//                                        tPrdPortStorageTaDetailPO.setInoutDate(workTicket.getWorkDate());
//                                        tPrdPortStorageTaDetailPO.setCleanMassSign(CleanMassSignEnum._0.getCode());
//                                        tPrdPortStorageTaDetailPO.setCompanyId(workTicket.getCompanyId()); //公司信息
//                                        tPrdPortStorageTaDetailPO.setCompanyName(workTicket.getCompanyName());
//                                        tPrdPortStorageTaDetailPO.setCargoInfoId(dto.getCargoInfoId());
//                                        tPrdPortStorageTaDetailPO.setQuantity(dto.getQuantity() == null ? 0 : dto.getQuantity()); //件数
//                                        tPrdPortStorageTaDetailPO.setTon(dto.getTon()); //重量
//                                        tPrdPortStorageTaDetailPO.setStorehouseId(dto.getStorehouseIdTarget()); //库场
//                                        tPrdPortStorageTaDetailPO.setStorehouseName(dto.getStorehouseNameTarget());
//                                        tPrdPortStorageTaDetailPO.setRegionId(dto.getRegionIdTarget());
//                                        tPrdPortStorageTaDetailPO.setRegionName(dto.getRegionNameTarget());
//                                        tPrdPortStorageTaDetailPO.setMassId(dto.getMassIdTarget());
//                                        tPrdPortStorageTaDetailPO.setMassName(dto.getMassNameTarget());
//                                        portStorageTaDetails.add(tPrdPortStorageTaDetailPO);
//                                        try {
//                                            businessCommonService.insertPortStorageDetail(portStorageTaDetails);
//                                        } catch (BusinessRuntimeException e) {
//                                            if (!"当前港存已清场".equals(e.getMessage())) {
//                                                throw new BusinessRuntimeException(e.getMessage());
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            //计件分配
//                            //判断班组是否为内部班组,内部班组分配作业量
//                            if (dto.getDeptId() != null) {
//                                String internal = tPrdWorkTicketMapper.getDeptInternal(dto.getDeptId());
//                                if ("I".equals(internal)) {
//                                    //内部班组 操作工班
//                                    //查询该部门下是否有班组点名
//                                    List<Long> ids = tPrdWorkTicketMapper.getDeptRollCallItem(workTicket.getWorkDate(), workTicket.getClassCode(), dto.getDeptId());
//                                    if (ids.size() == 0) {
//                                        throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组未出勤点名");
//                                    }
//                                    if (ids.size() > 1) {
//                                        throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组出勤点名重复");
//                                    }
//                                    List<TPrdAttendanceUserPO> attendanceUsers = tPrdWorkTicketMapper.listAttendanceUser(ids);
//                                    Map<String, Object> deptMap = tPrdWorkTicketMapper.getDeptDm(ids);
//                                    List<TPrdSalaryPO> salaries = new ArrayList<>();
//                                    for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUsers) {
//                                        TPrdSalaryPO salary = new TPrdSalaryPO();
//                                        salary.setId(snowflake.nextId());
//                                        salary.setWorkTicketDetailId(dto.getId());
//                                        salary.setCompanyId(workTicket.getCompanyId());
//                                        salary.setCompanyName(workTicket.getCompanyName());
//                                        salary.setWorkDate(workTicket.getWorkDate());
//                                        salary.setClassCode(workTicket.getClassCode());
//                                        salary.setClassName(workTicket.getClassName());
//                                        salary.setPieceProjectCode("1");
//                                        salary.setPieceProjectName("1");
//                                        salary.setProcessDetailCode(dto.getProcessDetailCode());
//                                        salary.setProcessDetailName(dto.getProcessDetailName());
//                                        if (deptMap != null) {
//                                            salary.setDeptId(Long.parseLong(deptMap.get("deptId").toString()));
//                                            salary.setDeptName(deptMap.get("deptName").toString());
//                                        } else {
//                                            throw new BusinessRuntimeException("获取点名班组信息失败");
//                                        }
//                                        salary.setUserBy(tPrdAttendanceUserPO.getUserId());
//                                        salary.setUserByName(tPrdAttendanceUserPO.getUserName());
//                                        salary.setCoefficient(tPrdAttendanceUserPO.getCoefficient());
//                                        salary.setTon(
//                                                dto.getTon()
//                                                        .divide(BigDecimal.valueOf(attendanceUsers.size()), 10, RoundingMode.HALF_UP)
//                                                        .multiply(BigDecimal.valueOf(tPrdAttendanceUserPO.getCoefficient())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
//                                        );
//                                        salary.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
//                                        salary.setSalaryStatusName(SalaryStatusEnum._10.getLabel());
//                                        //计件类型
//                                        //查询货物计件类型
//                                        if ("1710197535592812544".equals(dto.getDeptId().toString())) {
//                                            //固机
//                                            salary.setSalaryTypeCode(cargoSalaryMap.get("gjTypeCode").toString());
//                                            salary.setSalaryTypeName(cargoSalaryMap.get("gjTypeName").toString());
//                                        } else if ("1710203467898949632".equals(dto.getDeptId().toString())) {
//                                            //流机
//                                            salary.setSalaryTypeCode(cargoSalaryMap.get("ljTypeCode").toString());
//                                            salary.setSalaryTypeName(cargoSalaryMap.get("ljTypeName").toString());
//                                        }
//                                        salaries.add(salary);
//                                    }
//                                    if (!salaries.isEmpty()) {
//                                        tPrdWorkTicketMapper.insertSalary(salaries);
//
//                                    }
//
//                                }
//                            }
//
//                            //人员工班
//                            if (dto.getPieceWorkTeamId() != null) {
//                                String internalPer = tPrdWorkTicketMapper.getDeptInternal(dto.getPieceWorkTeamId());
//                                if ("I".equals(internalPer)) {
//                                    //内部班组
//                                    //查询该部门下是否有班组点名
//                                    List<Long> ids = tPrdWorkTicketMapper.getDeptRollCallItem(workTicket.getWorkDate(), workTicket.getClassCode(), dto.getPieceWorkTeamId());
//                                    if (ids.size() == 0) {
//                                        throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组未出勤点名");
//                                    }
//                                    if (ids.size() > 1) {
//                                        throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组出勤点名重复");
//                                    }
//                                    Map<String, Object> deptMap = tPrdWorkTicketMapper.getDeptDm(ids);
//                                    List<TPrdAttendanceUserPO> attendanceUsers = tPrdWorkTicketMapper.listAttendanceUser(ids);
//                                    List<TPrdSalaryPO> salariesPer = new ArrayList<>();
//                                    for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUsers) {
//                                        TPrdSalaryPO salary = new TPrdSalaryPO();
//                                        salary.setId(snowflake.nextId());
//                                        salary.setWorkTicketDetailId(dto.getId());
//                                        salary.setCompanyId(workTicket.getCompanyId());
//                                        salary.setCompanyName(workTicket.getCompanyName());
//                                        salary.setWorkDate(workTicket.getWorkDate());
//                                        salary.setClassCode(workTicket.getClassCode());
//                                        salary.setClassName(workTicket.getClassName());
//                                        salary.setPieceProjectCode("1");
//                                        salary.setPieceProjectName("1");
//                                        salary.setProcessDetailCode(dto.getProcessDetailCode());
//                                        salary.setProcessDetailName(dto.getProcessDetailName());
//                                        if (deptMap != null) {
//                                            salary.setDeptId(Long.parseLong(deptMap.get("deptId").toString()));
//                                            salary.setDeptName(deptMap.get("deptName").toString());
//                                        } else {
//                                            throw new BusinessRuntimeException("获取点名班组信息失败");
//                                        }
//                                        salary.setUserBy(tPrdAttendanceUserPO.getUserId());
//                                        salary.setUserByName(tPrdAttendanceUserPO.getUserName());
//                                        salary.setCoefficient(tPrdAttendanceUserPO.getCoefficient());
//                                        salary.setTon(
//                                                dto.getTon()
//                                                        .divide(BigDecimal.valueOf(attendanceUsers.size()), 10, RoundingMode.HALF_UP)
//                                                        .multiply(BigDecimal.valueOf(tPrdAttendanceUserPO.getCoefficient())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
//                                        );
//                                        salary.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
//                                        salary.setSalaryStatusName(SalaryStatusEnum._10.getLabel());
//                                        salariesPer.add(salary);
//                                    }
//                                    if (!salariesPer.isEmpty()) {
//                                        tPrdWorkTicketMapper.insertSalary(salariesPer);
//
//                                    }
//
//                                }
//                            }
//
//                        }
//                    } else {
//                        throw new BusinessRuntimeException("获取签票明细信息失败，请检查签票信息是否正确");
//                    }
//                    if (workTicket.getTon() != null) {
//                        //主表中作业量分配
//                        //查询该部门下是否有班组点名
//                        List<Long> ids = tPrdWorkTicketMapper.getDeptRollCallItem(workTicket.getWorkDate(), workTicket.getClassCode(), workTicket.getDeptId());
//                        if (ids == null || ids.size() == 0) {
//                            throw new BusinessRuntimeException(workTicket.getDeptName() + "部门下未出勤点名");
//                        }
//                        if (ids.size() > 1) {
//                            throw new BusinessRuntimeException(workTicket.getDeptName() + "部门下出勤点名重复");
//                        }
//                        Map<String, Object> deptMap = tPrdWorkTicketMapper.getDeptDm(ids);
//                        List<TPrdAttendanceUserPO> attendanceUsers = tPrdWorkTicketMapper.listAttendanceUser(ids);
//                        List<TPrdSalaryPO> salaries = new ArrayList<>();
//                        for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUsers) {
//                            TPrdSalaryPO salary = new TPrdSalaryPO();
//                            salary.setId(snowflake.nextId());
//                            salary.setWorkTicketDetailId(workTicketDetails.get(0).getId());
//                            salary.setCompanyId(workTicket.getCompanyId());
//                            salary.setCompanyName(workTicket.getCompanyName());
//                            salary.setWorkDate(workTicket.getWorkDate());
//                            salary.setClassCode(workTicket.getClassCode());
//                            salary.setClassName(workTicket.getClassName());
//                            salary.setPieceProjectCode("1");
//                            salary.setPieceProjectName("1");
//                            salary.setProcessDetailCode(workTicket.getProcessCode());
//                            salary.setProcessDetailName(workTicket.getProcessName());
//                            if (deptMap != null) {
//                                salary.setDeptId(Long.parseLong(deptMap.get("deptId").toString()));
//                                salary.setDeptName(deptMap.get("deptName").toString());
//                            } else {
//                                throw new BusinessRuntimeException("获取点名班组信息失败");
//                            }
//                            salary.setUserBy(tPrdAttendanceUserPO.getUserId());
//                            salary.setUserByName(tPrdAttendanceUserPO.getUserName());
//                            salary.setCoefficient(tPrdAttendanceUserPO.getCoefficient());
//                            salary.setTon(
//                                    workTicket.getTon()
//                                            .divide(BigDecimal.valueOf(attendanceUsers.size()), 10, RoundingMode.HALF_UP)
//                                            .multiply(BigDecimal.valueOf(tPrdAttendanceUserPO.getCoefficient())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
//                            );
//                            salary.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
//                            salary.setSalaryStatusName(SalaryStatusEnum._10.getLabel());
//                            //库场
//                            salary.setSalaryTypeCode(cargoSalaryMap.get("kcTypeCode").toString());
//                            salary.setSalaryTypeName(cargoSalaryMap.get("kcTypeName").toString());
//                            salaries.add(salary);
//                        }
//                        if (!salaries.isEmpty()) {
//                            tPrdWorkTicketMapper.insertSalary(salaries);
//
//                        }
//                    }
//                }
//            }
//
//        }
//        if (!CollectionUtils.isEmpty(workPlanIdList)) {
//            TicketPlanIdDTO ticketPlanIdDTO = new TicketPlanIdDTO();
//            ticketPlanIdDTO.setWorkPlanIdList(workPlanIdList);
//            reviewWorkTicket(ticketPlanIdDTO);
//        }
//    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void reviewWorkTicketJsg(TPrdWorkPlanJsgDTO tPrdWorkPlanJsgDTO) {
        List<Map<String, Object>> workPlanIdList = new ArrayList<>();
        List<TPrdWorkPlanJsgDetailDTO> workPlanDetailList = tPrdWorkPlanJsgDTO.getWorkPlanList();
        if (workPlanDetailList != null && workPlanDetailList.size() != 0) {
            for (TPrdWorkPlanJsgDetailDTO dtoPlan : workPlanDetailList) {
                List<TPrdWorkPlanDTO> tPrdWorkPlanList = tPrdWorkTicketMapper.listWorkPlanById(dtoPlan.getPlanId());
                //计划详情
                TPrdWorkPlanDTO tPrdWorkPlanDTO = tPrdWorkPlanList.get(0);
                //作业票主表详情
                TPrdWorkTicketDTO ticketInfo = tPrdWorkTicketMapper.getWorkTicket(dtoPlan.getPlanId(), "2", "1");
                if (ticketInfo != null) {
                    throw new BusinessRuntimeException("当前作业计划签票已审核");
                }
                TPrdWorkTicketDTO ticket = null;
                String ticketFlag = "2"; //车到岸审核作业票标记  1是调度，2是库场
                if ("1039".equals(tPrdWorkPlanDTO.getProcessCode())) {
                    //车-岸
                    ticket = tPrdWorkTicketMapper.getWorkTicket(dtoPlan.getPlanId(), "2", "2");
                    if(ticket==null){
                        ticket = tPrdWorkTicketMapper.getWorkTicket(dtoPlan.getPlanId(), "1", "2");
                        ticketFlag = "1";
                    }
                } else {
                    ticket = tPrdWorkTicketMapper.getWorkTicket(dtoPlan.getPlanId(), "2", "2");
                }
                if (ticket != null) {
                    //已经签过的(件杂)
                    Map maps = new HashMap();
                    if ("1039".equals(tPrdWorkPlanDTO.getProcessCode())) {
                        maps.put("ticketType", ticketFlag);
                    } else {
                        maps.put("ticketType", "2");
                    }
                    maps.put("workPlanId", dtoPlan.getPlanId());
                    workPlanIdList.add(maps);
                } else {
                    //先添加签票
                    TPrdWorkTicketDTO workTicket = new TPrdWorkTicketDTO();
                    List<TPrdWorkTicketDetailDTO> detailDTOS = new ArrayList<>();
                    Long zid = snowflake.nextId();
                    workTicket.setId(zid);
                    workTicket.setWorkPlanId(dtoPlan.getPlanId());
                    workTicket.setCompanyId(tPrdWorkPlanDTO.getCompanyId());
                    workTicket.setCompanyName(tPrdWorkPlanDTO.getCompanyName());
                    workTicket.setWorkDate(tPrdWorkPlanDTO.getWorkDate());
                    workTicket.setClassCode(tPrdWorkPlanDTO.getClassCode());
                    workTicket.setClassName(tPrdWorkPlanDTO.getClassName());
                    workTicket.setType(tPrdWorkPlanDTO.getPlanType());
                    workTicket.setWorkTicketStatus(WorkTicketStatusEnum._20.getCode());
                    workTicket.setWorkTicketStatusName(WorkTicketStatusEnum._20.getName());
                    if ("1039".equals(tPrdWorkPlanDTO.getProcessCode())) {
                        //车-岸
                        workTicket.setDeptId(Long.parseLong("1677243491865989120"));
                        workTicket.setDeptName("调度室");
                        workTicket.setTicketType("1");
                    } else {
                        workTicket.setDeptId(Long.parseLong("1677243625832058880"));
                        workTicket.setDeptName("库场队");
                        workTicket.setTicketType("2");
                    }

                    workTicket.setProcessCode(tPrdWorkPlanDTO.getProcessCode());
                    workTicket.setProcessName(tPrdWorkPlanDTO.getProcessName());
                    //子表信息
                    List<TPrdWorkTiTckInfoDTO> WorkTiTckDetailList = dtoPlan.getInfoDTOList();
                    Integer quantity = 0;
                    BigDecimal ton = new BigDecimal(0);
                    List<TPrdWorkTicketEquipmentPO> equipments = new ArrayList<>();
                    for (TPrdWorkTiTckInfoDTO tPrdWorkTiTckInfoDTO : WorkTiTckDetailList) {
                        TPrdWorkTicketDetailDTO detailDTO = new TPrdWorkTicketDetailDTO();
                        long detailId = snowflake.nextId();
                        detailDTO.setId(detailId);
                        detailDTO.setWorkTicketId(zid);
                        detailDTO.setProcessCode(tPrdWorkPlanDTO.getProcessCode());
                        detailDTO.setProcessName(tPrdWorkPlanDTO.getProcessName());
                        detailDTO.setWorkDate(tPrdWorkPlanDTO.getWorkDate());
                        detailDTO.setClassCode(tPrdWorkPlanDTO.getClassCode());
                        detailDTO.setClassName(tPrdWorkPlanDTO.getClassName());
                        detailDTO.setTrustId(tPrdWorkTiTckInfoDTO.getTrustId());
                        detailDTO.setTrustCargoInfoId(tPrdWorkTiTckInfoDTO.getTrustCargoInfoId());
                        detailDTO.setCargoInfoId(tPrdWorkTiTckInfoDTO.getCargoInfoId());
                        detailDTO.setProcessDetailCode(tPrdWorkTiTckInfoDTO.getProcessDetailCode());
                        detailDTO.setProcessDetailName(tPrdWorkTiTckInfoDTO.getProcessDetailName());
                        detailDTO.setDeptId(tPrdWorkTiTckInfoDTO.getDeptItemId());
                        detailDTO.setDeptName(tPrdWorkTiTckInfoDTO.getDeptItemName());
                        detailDTO.setCargoCode(tPrdWorkTiTckInfoDTO.getCargoCode());
                        detailDTO.setCargoName(tPrdWorkTiTckInfoDTO.getCargoName());
                        detailDTO.setShipvoyageId(tPrdWorkTiTckInfoDTO.getShipvoyageId());
                        detailDTO.setShipvoyageItemId(tPrdWorkTiTckInfoDTO.getShipvoyageItemId());
                        if (tPrdWorkTiTckInfoDTO.getStorehouseIdSource() != null) {
                            detailDTO.setStorehouseIdSource(tPrdWorkTiTckInfoDTO.getStorehouseIdSource());
                            detailDTO.setStorehouseNameSource(tPrdWorkTiTckInfoDTO.getStorehouseNameSource());
                            detailDTO.setRegionIdSource(tPrdWorkTiTckInfoDTO.getRegionIdSource());
                            detailDTO.setRegionNameSource(tPrdWorkTiTckInfoDTO.getRegionNameSource());
                            detailDTO.setMassIdSource(tPrdWorkTiTckInfoDTO.getMassIdSource());
                            detailDTO.setMassNameSource(tPrdWorkTiTckInfoDTO.getMassNameSource());
                        }
                        if (tPrdWorkTiTckInfoDTO.getStorehouseIdTarget() != null) {
                            detailDTO.setStorehouseIdTarget(tPrdWorkTiTckInfoDTO.getStorehouseIdTarget());
                            detailDTO.setStorehouseNameTarget(tPrdWorkTiTckInfoDTO.getStorehouseNameTarget());
                            detailDTO.setRegionIdTarget(tPrdWorkTiTckInfoDTO.getRegionIdTarget());
                            detailDTO.setRegionNameTarget(tPrdWorkTiTckInfoDTO.getRegionNameTarget());
                            detailDTO.setMassIdTarget(tPrdWorkTiTckInfoDTO.getMassIdTarget());
                            detailDTO.setMassNameTarget(tPrdWorkTiTckInfoDTO.getMassNameTarget());
                        }
                        detailDTO.setQuantity(tPrdWorkTiTckInfoDTO.getQuantity());
                        detailDTO.setTon(tPrdWorkTiTckInfoDTO.getTon());
                        detailDTO.setStartTime(tPrdWorkTiTckInfoDTO.getStartTime());
                        detailDTO.setEndTime(tPrdWorkTiTckInfoDTO.getEndTime());
                        detailDTO.setPieceWorkTeamId(tPrdWorkTiTckInfoDTO.getPersonNelId());
                        detailDTO.setPieceWorkTeamName(tPrdWorkTiTckInfoDTO.getPersonNelName());
                        //处理机械
                        List<String> listEquipmentId = Arrays.asList(tPrdWorkTiTckInfoDTO.getEquipmentId().split(","));
                        List<String> listEquipmentNo = Arrays.asList(tPrdWorkTiTckInfoDTO.getEquipmentNo().split(","));
                        if (listEquipmentId != null && listEquipmentId.size() != 0) {
                            for (int i = 0; i < listEquipmentId.size(); i++) {
                                TPrdWorkTicketEquipmentPO equipmentPO = new TPrdWorkTicketEquipmentPO();
                                equipmentPO.setId(snowflake.nextId());
                                equipmentPO.setWorkTicketDetailId(detailId);
                                equipmentPO.setEquipmentId(Long.parseLong(listEquipmentId.get(i)));
                                equipmentPO.setEquipmentNo(listEquipmentNo.get(i));
                                equipmentPO.setEquipmentTypeCode(tPrdWorkTiTckInfoDTO.getEquipmentTypeCode());
                                equipmentPO.setEquipmentTypeName(tPrdWorkTiTckInfoDTO.getEquipmentTypeName());
                                equipments.add(equipmentPO);
                            }
                        }
                        //作业过程是理货量的作业量相加
                        Integer count = tPrdWorkTicketMapper.getIsProcrss(tPrdWorkTiTckInfoDTO.getProcessDetailCode());
                        if (count == 1) {
                            if (tPrdWorkTiTckInfoDTO.getQuantity() != null) {
                                quantity += tPrdWorkTiTckInfoDTO.getQuantity();
                            }
                            if (tPrdWorkTiTckInfoDTO.getTon() == null) {
                                throw new BusinessRuntimeException("重量不能为空");
                            }
                            ton = ton.add(tPrdWorkTiTckInfoDTO.getTon());
                        }
                        detailDTOS.add(detailDTO);
                    }
                    workTicket.setQuantity(quantity);
                    workTicket.setTon(ton);
                    tPrdWorkTicketMapper.insertWorkTicketJsg(workTicket);
                    tPrdWorkTicketMapper.insertWorkTicketDetail(detailDTOS);
                    if (CollectionUtils.isNotEmpty(equipments)) {
                        tPrdWorkTicketMapper.insertWorkTicketEquipment(equipments);
                    }
                    commWorkTicketOperate(workTicket, detailDTOS, false, true, "集疏港散货审核");
                }
            }
        }
        if (!CollectionUtils.isEmpty(workPlanIdList)) {
            TicketPlanIdDTO ticketPlanIdDTO = new TicketPlanIdDTO();
            ticketPlanIdDTO.setWorkPlanIdList(workPlanIdList);
            reviewWorkTicket(ticketPlanIdDTO);
        }
    }

//    @Override
//    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
//    public void reviewWorkTicket(Long workPlanId) {
//        DistributedLock.newBuilder().store(redisTemplate)
//                .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workPlanId)
//                .build().run(() -> {
//            TPrdWorkTicketDTO workTicket = tPrdWorkTicketMapper.getWorkTicket(workPlanId);
//            if (workTicket == null) {
//                throw new BusinessRuntimeException("当前作业计划未签票");
//            }
//            if (WorkTicketStatusEnum._20.getCode().equals(workTicket.getWorkTicketStatus())) {
//                throw new BusinessRuntimeException("当前作业计划签票已审核");
//            }
//
//            TPrdWorkTicketPO tempWorkTicket = new TPrdWorkTicketPO();
//            tempWorkTicket.setWorkPlanId(workPlanId);
//            tempWorkTicket.setWorkTicketStatus(WorkTicketStatusEnum._20.getCode());
//            tempWorkTicket.setWorkTicketStatusName(WorkTicketStatusEnum._20.getName());
//            tPrdWorkTicketMapper.reviewWorkTicket(tempWorkTicket);
//
//            List<TPrdWorkTicketDetailDTO> workTicketDetails = tPrdWorkTicketMapper.listWorkTicketDetail(workTicket.getId());
//
//            List<Long> pieceWorkTeamIds = workTicketDetails.stream()
//                    .map(TPrdWorkTicketDetailPO::getPieceWorkTeamId).distinct().collect(Collectors.toList());
//            List<MPieceWorkTeamPO> pieceWorkTeams = tPrdWorkTicketMapper.listPieceWorkTeamById(pieceWorkTeamIds);
//            List<MPieceWorkTeamPO> isPortStorageRelatedPieceWorkTeams = pieceWorkTeams.stream()
//                    .filter(v1 -> IsUpdateStorageEnum._1.getCode().equals(v1.getIsUpdateStorage())).collect(Collectors.toList());
//
//            List<String> cargoCodes = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getCargoCode).distinct().collect(Collectors.toList());
//            List<MCargoPO> cargos = tPrdWorkTicketMapper.listCargo(cargoCodes);
//            List<MCargoPO> isPortStorageRelatedCargos = cargos.stream()
//                    .filter(v1 -> UpdatePointEnum._2.getCode().equals(v1.getUpdatePoint())).collect(Collectors.toList());
//
//            List<String> processDetailCodes = workTicketDetails.stream()
//                    .map(TPrdWorkTicketDetailPO::getProcessDetailCode).distinct().collect(Collectors.toList());
//            List<MWorkProcessPO> workProcesses = tPrdWorkTicketMapper.listWorkProcess(processDetailCodes);
//            List<MWorkProcessPO> outStorehouseWorkProcess = workProcesses.stream()
//                    .filter(v1 -> SourceTargetTypeEnum._05.getCode().equals(v1.getSourceCd()))
//                    .collect(Collectors.toList());
//            List<MWorkProcessPO> inStorehouseWorkProcess = workProcesses.stream()
//                    .filter(v1 -> SourceTargetTypeEnum._05.getCode().equals(v1.getTargetCd()))
//                    .collect(Collectors.toList());
//
//            List<TPrdWorkTicketDetailDTO> isPortStorageRelatedWorkTicketDetails = workTicketDetails.stream().filter(v1 -> {
//                boolean bool = isPortStorageRelatedPieceWorkTeams.stream().anyMatch(v2 -> v1.getPieceWorkTeamId().equals(v2.getId()));
//                return bool && isPortStorageRelatedCargos.stream().anyMatch(v2 -> v1.getCargoCode().equals(v2.getCargoCode()));
//            }).collect(Collectors.toList());
//
//            List<TPrdPortStorageDetailPO> outStorehousePortStorageDetails = isPortStorageRelatedWorkTicketDetails.stream()
//                    .filter(v1 -> outStorehouseWorkProcess.stream().anyMatch(v2 -> v1.getProcessDetailCode().equals(v2.getProcessCd())))
//                    .map(v1 -> {
//                        TPrdPortStorageDetailPO portStorageDetail = new TPrdPortStorageDetailPO();
//                        portStorageDetail.setCargoInfoId(v1.getCargoInfoId());
//                        portStorageDetail.setWorkDate(workTicket.getWorkDate());
//                        portStorageDetail.setClassCode(workTicket.getClassCode());
//                        portStorageDetail.setClassName(workTicket.getClassName());
//                        portStorageDetail.setProcessDetailCode(v1.getProcessDetailCode());
//                        portStorageDetail.setProcessDetailName(v1.getProcessDetailName());
//                        portStorageDetail.setStorehouseId(v1.getStorehouseIdSource());
//                        portStorageDetail.setStorehouseName(v1.getStorehouseNameSource());
//                        portStorageDetail.setRegionId(v1.getRegionIdSource());
//                        portStorageDetail.setRegionName(v1.getRegionNameSource());
//                        portStorageDetail.setMassId(v1.getMassIdSource());
//                        portStorageDetail.setMassName(v1.getMassNameSource());
//                        if (v1.getQuantity() != null) {
//                            portStorageDetail.setQuantity(v1.getQuantity() * -1);
//                        }
//                        portStorageDetail.setTon(v1.getTon().multiply(BigDecimal.valueOf(-1)));
//                        portStorageDetail.setInoutStorageCode(InoutStorageEnum._20.getCode());
//                        portStorageDetail.setInoutStorageName(InoutStorageEnum._20.getLabel());
//                        portStorageDetail.setWorkTicketId(workTicket.getId());
//                        portStorageDetail.setWorkTicketDetailId(v1.getId());
//                        portStorageDetail.setInoutDate(workTicket.getWorkDate());
//                        portStorageDetail.setCompanyId(workTicket.getCompanyId());
//                        portStorageDetail.setCompanyName(workTicket.getCompanyName());
//                        return portStorageDetail;
//                    })
//                    .collect(Collectors.toList());
//            List<TPrdPortStorageDetailPO> inStorehousePortStorageDetails = isPortStorageRelatedWorkTicketDetails.stream()
//                    .filter(v1 -> inStorehouseWorkProcess.stream().anyMatch(v2 -> v1.getProcessDetailCode().equals(v2.getProcessCd())))
//                    .map(v1 -> {
//                        TPrdPortStorageDetailPO portStorageDetail = new TPrdPortStorageDetailPO();
//                        portStorageDetail.setCargoInfoId(v1.getCargoInfoId());
//                        portStorageDetail.setWorkDate(workTicket.getWorkDate());
//                        portStorageDetail.setClassCode(workTicket.getClassCode());
//                        portStorageDetail.setClassName(workTicket.getClassName());
//                        portStorageDetail.setProcessDetailCode(v1.getProcessDetailCode());
//                        portStorageDetail.setProcessDetailName(v1.getProcessDetailName());
//                        portStorageDetail.setStorehouseId(v1.getStorehouseIdTarget());
//                        portStorageDetail.setStorehouseName(v1.getStorehouseNameTarget());
//                        portStorageDetail.setRegionId(v1.getRegionIdTarget());
//                        portStorageDetail.setRegionName(v1.getRegionNameTarget());
//                        portStorageDetail.setMassId(v1.getMassIdTarget());
//                        portStorageDetail.setMassName(v1.getMassNameTarget());
//                        portStorageDetail.setQuantity(v1.getQuantity());
//                        portStorageDetail.setTon(v1.getTon());
//                        portStorageDetail.setInoutStorageCode(InoutStorageEnum._20.getCode());
//                        portStorageDetail.setInoutStorageName(InoutStorageEnum._20.getLabel());
//                        portStorageDetail.setWorkTicketId(workTicket.getId());
//                        portStorageDetail.setWorkTicketDetailId(v1.getId());
//                        portStorageDetail.setInoutDate(workTicket.getWorkDate());
//                        portStorageDetail.setCompanyId(workTicket.getCompanyId());
//                        portStorageDetail.setCompanyName(workTicket.getCompanyName());
//                        return portStorageDetail;
//                    })
//                    .collect(Collectors.toList());
//
//            List<TPrdPortStorageDetailPO> portStorageDetails = new ArrayList<>();
//            portStorageDetails.addAll(outStorehousePortStorageDetails);
//            portStorageDetails.addAll(inStorehousePortStorageDetails);
//            if (!portStorageDetails.isEmpty()) {
//                businessCommonService.insertPortStorageDetail(portStorageDetails);
//            }
//
//            List<Long> deptIds = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getDeptId).distinct().collect(Collectors.toList());
//            List<SysDeptPO> depts = tPrdWorkTicketMapper.listDept(deptIds);
//            List<Long> innerDeptIds = deptIds.stream().filter(v1 -> "I".equals(depts.stream().filter(v2 -> v1.equals(v2.getId())).findFirst().orElseThrow(null).getInOutType())).collect(Collectors.toList());
//            List<TPrdAttendancePO> attendances = tPrdWorkTicketMapper.listAttendance(workTicket.getWorkDate(), workTicket.getClassCode(), deptIds);
//
//            boolean anyMatch = deptIds.stream()
//                    .filter(v1 -> attendances.stream().noneMatch(v2 -> v1.equals(v2.getDeptId())))
//                    .anyMatch(innerDeptIds::contains);
//            if (anyMatch) {
//                throw new BusinessRuntimeException("存在未出勤点名的作业工班！");
//            }
//
//            List<Long> attendanceIds = attendances.stream().map(TPrdAttendancePO::getId).collect(Collectors.toList());
//            if(attendanceIds != null && attendanceIds.size() != 0){
//                throw new BusinessRuntimeException("未查询到点名信息！");
//            }
//            List<TPrdAttendanceUserPO> attendanceUsers = tPrdWorkTicketMapper.listAttendanceUser(attendanceIds);
//
//            List<TPrdSalaryPO> salaries = workTicketDetails.stream().flatMap(v1 -> {
//                MPieceWorkTeamPO currentPieceWorkTeam = pieceWorkTeams.stream()
//                        .filter(v2 -> v1.getPieceWorkTeamId().equals(v2.getId())).findFirst().orElseThrow(BusinessRuntimeException::new);
//                List<TPrdAttendanceUserPO> currentAttendanceUsers = attendanceUsers.stream()
//                        .filter(v2 ->
//                                v2.getAttendanceId().equals(attendances.stream()
//                                        .filter(v3 -> v1.getDeptId().equals(v3.getDeptId())).findFirst().orElse(new TPrdAttendancePO())
//                                        .getId())
//                        ).collect(Collectors.toList());
//
//                return currentAttendanceUsers.stream().map(v2 -> {
//                    TPrdSalaryPO salary = new TPrdSalaryPO();
//                    salary.setId(snowflake.nextId());
//                    salary.setWorkTicketDetailId(v1.getId());
//                    salary.setCompanyId(workTicket.getCompanyId());
//                    salary.setCompanyName(workTicket.getCompanyName());
//                    salary.setWorkDate(workTicket.getWorkDate());
//                    salary.setClassCode(workTicket.getClassCode());
//                    salary.setClassName(workTicket.getClassName());
//                    salary.setPieceProjectCode(currentPieceWorkTeam.getPieceProjectCode());
//                    salary.setPieceProjectName(currentPieceWorkTeam.getPieceProjectName());
//                    salary.setProcessDetailCode(v1.getProcessDetailCode());
//                    salary.setProcessDetailName(v1.getProcessDetailName());
//                    salary.setDeptId(v1.getDeptId());
//                    salary.setDeptName(v1.getDeptName());
//                    salary.setUserBy(v2.getUserId());
//                    salary.setUserByName(v2.getUserName());
//                    salary.setCoefficient(v2.getCoefficient());
//                    salary.setTon(
//                            v1.getTon()
//                                    .divide(BigDecimal.valueOf(currentAttendanceUsers.size()), 10, RoundingMode.HALF_UP)
//                                    .multiply(BigDecimal.valueOf(v2.getCoefficient())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
//                    );
//                    salary.setSalaryStatusCode(SalaryStatusEnum._10.getCode());
//                    salary.setSalaryStatusName(SalaryStatusEnum._10.getLabel());
//                    return salary;
//                });
//            }).collect(Collectors.toList());
//            if (!salaries.isEmpty()) {
//                tPrdWorkTicketMapper.insertSalary(salaries);
//            }
//        });
//    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelReviewWorkTicket(TicketPlanIdDTO ticketPlanIdDTO) {
        List<Map<String, Object>> workPlanIdList = ticketPlanIdDTO.getWorkPlanIdList();
        if (workPlanIdList != null && workPlanIdList.size() != 0) {
            for (Map<String, Object> map : workPlanIdList) {
                Long workPlanId = Long.parseLong(map.get("workPlanId").toString());
                String ticketType = map.get("ticketType").toString();
                TPrdWorkTicketDTO workTicket = tPrdWorkTicketMapper.getWorkTicket(workPlanId, ticketType, "2");
                DistributedLock.newBuilder().store(redisTemplate)
                        .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workPlanId)
                        .build().run(() -> {
                    String trustNo = tPrdWorkTicketMapper.getTrustIdWicketJsg(workPlanId);
                    ;
                    if (workTicket == null) {
//                        System.out.println(workPlanId);
                        throw new BusinessRuntimeException("通知单号为" + trustNo + "的作业计划未签票");
                    }
                    Integer count = tPrdWorkTicketMapper.getSalaryEx(workTicket.getId());
                    if (count > 0) {
                        throw new BusinessRuntimeException("通知单号为" + trustNo + "的作业计划计件已审核,请先取消计件审核");
                    }
                    TPrdWorkTicketPO tempWorkTicket = new TPrdWorkTicketPO();
                    tempWorkTicket.setWorkPlanId(workPlanId);
                    tempWorkTicket.setWorkTicketStatus(WorkTicketStatusEnum._10.getCode());
                    tempWorkTicket.setWorkTicketStatusName(WorkTicketStatusEnum._10.getName());
                    tempWorkTicket.setTicketType(ticketType);
                    tempWorkTicket.setExamineByUp(workTicket.getExamineBy());
                    tempWorkTicket.setExamineByNameUp(workTicket.getExamineByName());
                    tempWorkTicket.setExamineTimeUp(workTicket.getExamineTime());
                    tPrdWorkTicketMapper.cancelReviewWorkTicket(tempWorkTicket);
                    //更新港存
                    List<TPrdPortStorageDetailPO> portStorageDetails = tPrdWorkTicketMapper.listPortStorageDetail(workTicket.getId());
                    if (!portStorageDetails.isEmpty()) {
                        List<Long> portStorageDetailIds = portStorageDetails.stream().map(TPrdPortStorageDetailPO::getId).collect(Collectors.toList());
                        try {
                            businessCommonService.deletePortStorageDetail(portStorageDetailIds);
                        } catch (BusinessRuntimeException e) {
                            throw new BusinessRuntimeException("相关港存已清场，无法销审");
                        }
                    }

                    List<TPrdWorkTicketDetailDTO> workTicketDetails = tPrdWorkTicketMapper.listWorkTicketDetail(workTicket.getId());
                    List<Long> workTicketDetailIds = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getId).collect(Collectors.toList());
                    tPrdWorkTicketMapper.deleteSalary(workTicketDetailIds);
                    tPrdWorkTicketMapper.deleteSalaryZ(workTicket.getId());
                    List<TPrdWorkPlanDTO> tPrdWorkPlanDTO = tPrdWorkTicketMapper.listWorkPlanById(workPlanId);
                    String cargoWorkType = tPrdWorkTicketMapper.getCargoWorkType(workTicketDetails.get(0).getCargoCode());
                    if ("2".equals(tPrdWorkPlanDTO.get(0).getPlanType()) && "2".equals(cargoWorkType)) {
                        //集疏港计划需要删除签票信息
                        List<TPrdWorkTicketDetailDTO> workTicketDetailsJsg = tPrdWorkTicketMapper.listWorkTicketDetail(workTicket.getId());
                        if (!"1039".equals(tPrdWorkPlanDTO.get(0).getProcessCode())) {
                            tPrdWorkTicketMapper.deleteWorkTicket(workPlanId, "2");
                            tPrdWorkTicketMapper.deleteWorkTicketDetail(workTicket.getId());
                            List<Long> workTicketDetailIdsJsg = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getId).collect(Collectors.toList());
                            tPrdWorkTicketMapper.deleteWorkTicketEquipment(workTicketDetailIdsJsg);
                        }
                    }
                });

            }
        } else {
            throw new BusinessRuntimeException("请至少选择一条计划");
        }

    }

    @Override
    public List<TPrdWorkTiTckInfoDTO> getWorkTicketList(TPrdWorkPlanQuery query) {
        if (query.getWorkDate() == null) {
            throw new BusinessRuntimeException("日期不能为空");
        }
        if (query.getClassCode() == null) {
            throw new BusinessRuntimeException("班次不能为空");
        }
        // 创建一个线程池，可以根据实际情况调整线程数量
        ExecutorService executor = Executors.newFixedThreadPool(10); // 这里使用10个线程
        if ("2".equals(query.getPlanType())) {
            List<TPrdWorkTiTckInfoDTO> ticketList = new ArrayList<>();
            //查询工班设置信息
            List<MWorkScheduleDTO> scheduLelist = mWorkScheduleMapper.getList();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatM = new SimpleDateFormat("HH:mm");
            String startTime = "";
            String endTime = "";
            if (!CollectionUtils.isEmpty(scheduLelist)) {
                for (MWorkScheduleDTO mWorkScheduleDTO : scheduLelist) {
                    if (query.getClassCode().equals(mWorkScheduleDTO.getWorkScheduleCode())) {
                        if ("01".equals(mWorkScheduleDTO.getWorkScheduleCode())) {
                            startTime = format.format(query.getWorkDate()) + " " + formatM.format(mWorkScheduleDTO.getStartTime());
                            endTime = format.format(query.getWorkDate()) + " " + formatM.format(mWorkScheduleDTO.getEndTime());
                        } else {
                            startTime = format.format(query.getWorkDate()) + " " + formatM.format(mWorkScheduleDTO.getStartTime());
                            // 创建一个Calendar实例
                            Calendar calendar = Calendar.getInstance();
                            // 将日期设定为当前日期
                            calendar.setTime(query.getWorkDate());
                            // 将日期加1天
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            // 获取加1天后的日期
                            Date tomorrow = calendar.getTime();
                            endTime = format.format(tomorrow) + " " + formatM.format(mWorkScheduleDTO.getEndTime());
                        }

                    }
                }
            }
            //集疏港计划
            //查询出当前日期班次的集疏港计划
            List<TPrdWorkPlanDTO> tPrdWorkPlanDTOS = tPrdWorkTicketMapper.listWorkPlanJsg(query);
            if (!CollectionUtils.isEmpty(tPrdWorkPlanDTOS)) {
                Iterator<TPrdWorkPlanDTO> iter = tPrdWorkPlanDTOS.iterator();
                while (iter.hasNext()) {
                    TPrdWorkPlanDTO po = iter.next();
                    if (po.getCargoOwnerId() != null) {
                        List<String> idLists = Arrays.asList(po.getCargoOwnerId().split(","));
                        Integer count = tPrdWorkTicketMapper.getIsStations(idLists);
                        if (count > 0) {
                            //有场站的删除
                            iter.remove();
                        }
                    }

                }
            }
            List<Long> ids = tPrdWorkPlanDTOS.stream().map(TPrdWorkPlanDTO::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(ids)) {
                query.setIds(ids);
                //当前班次所有计划是否签票
                List<TPrdWorkTiTckInfoDTO> dtoList = tPrdWorkTicketMapper.getIsWorkTickets(query);
                //获取当前计划的所有的指令ID
                List<Long> trustIds = tPrdWorkTicketMapper.getTrustIDs(ids);
                if (!trustIds.isEmpty()) {
                    List<Map<String, Object>> shipvoyageItems = tBusTrustMapper.listShipvoyageItemByTrustIds(trustIds);
                    tPrdWorkPlanDTOS.stream().filter(v1 -> trustIds.contains(v1.getTrustId()))
                            .forEach(v1 -> {
                                String shipNameVoyages = shipvoyageItems.stream()
                                        .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                        .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                        .collect(Collectors.joining("，"));
                                v1.setShipNameVoyage(shipNameVoyages);
                                String shipVoyageIds = shipvoyageItems.stream()
                                        .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                        .map(v2 -> String.valueOf(v2.get("shipvoyageItemId")))
                                        .collect(Collectors.joining("，"));
                                v1.setShipVoyageIds(shipVoyageIds);

                            });
                    if (query.getShipvoyageItemId() != null) {
                        tPrdWorkPlanDTOS = tPrdWorkPlanDTOS.stream().filter(s -> s.getShipVoyageIds().contains(query.getShipvoyageItemId().toString())).collect(Collectors.toList());
                    }
                }
                //当前班次所有计划机械配工详情
                List<TPrdWorkTicketDetailDTO> ticketInfos = tPrdWorkTicketMapper.getTickets(ids, "2");
                //当前班次所有计划装卸配工详情
                List<TPrdWorkTicketDetailDTO> poLwList = tPrdWorkTicketMapper.getTicketInfoLwList(ids);
                //当前班次所有计划指令票货详情
                List<TBusCargoInfoDTO> tBusCargoInfoList = tPrdWorkTicketMapper.listTrustCargos(ids);
                //当前班次所有计划件货理货量详情
                //List<TYardTallyItemPO> tYardTallyItemPOList = tPrdWorkTicketMapper.getTally(ids);
                //当前班次所有计划场地详情
                List<Map<String, Object>> locationStartList = tPrdWorkTicketMapper.getLocation(ids, "2");
                List<Map<String, Object>> locationEndList = tPrdWorkTicketMapper.getLocation(ids, "1");
                List<Map<String, Object>> locationtList = tPrdWorkTicketMapper.getTallyLocation(ids);
                for (TPrdWorkPlanDTO pos : tPrdWorkPlanDTOS) {
                    String finalStartTime = startTime;
                    String finalEndTime = endTime;
                    executor.submit(() -> {
                        List<TPrdWorkTiTckInfoDTO> tiTckInfoList = dtoList.stream()
                                .filter(obj -> obj.getWorkPlanId().equals(pos.getId()))
                                .collect(Collectors.toList());
                        //如果不为空,则已经签票
                        if (!CollectionUtils.isEmpty(tiTckInfoList)) {
                            List<Map<String, Object>> shipvoyageItems = tBusTrustMapper.listShipvoyageItemByTrustIds(trustIds);
                            tiTckInfoList.stream().filter(v1 -> trustIds.contains(v1.getTrustId()))
                                    .forEach(v1 -> {
                                        String shipNameVoyages = shipvoyageItems.stream()
                                                .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                                .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                                .collect(Collectors.joining("，"));
                                        v1.setShipNameVoyage(shipNameVoyages);
                                        String shipVoyageIds = shipvoyageItems.stream()
                                                .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                                .map(v2 -> String.valueOf(v2.get("shipvoyageItemId")))
                                                .collect(Collectors.joining("，"));
                                        v1.setShipVoyageIds(shipVoyageIds);

                                    });
                            ticketList.addAll(tiTckInfoList);
                        } else {

                            // List<TPrdWorkTicketDetailDTO> ticketInfo = tPrdWorkTicketMapper.getTicketInfo(pos.getId(), "2");
                            List<TPrdWorkTicketDetailDTO> ticketInfo = ticketInfos.stream()
                                    .filter(obj -> obj.getWorkPlanId().equals(pos.getId()))
                                    .collect(Collectors.toList());
                            List<TBusCargoInfoDTO> tBusCargoInfoDTOS = tBusCargoInfoList.stream()
                                    .filter(obj -> obj.getWorkPlanId().equals(pos.getId()))
                                    .collect(Collectors.toList());
                            if (CollectionUtils.isEmpty(ticketInfo) && "1039".equals(pos.getProcessCode())) {
                                List<Long> idList = new ArrayList<>();
                                idList.add(pos.getId());
                                ticketInfo = tPrdWorkTicketMapper.getTickets(idList, "1");
                            }
                            if (ticketInfo != null && ticketInfo.size() != 0) {
                                for (int i = 0; i < ticketInfo.size(); i++) {
                                    //根据作业过程查询装卸队派工
                                    TPrdWorkTicketDetailDTO po = ticketInfo.get(i);
                                    List<TPrdWorkTicketDetailDTO> poLw = poLwList.stream()
                                            .filter(obj -> obj.getWorkPlanId().equals(pos.getId()) && obj.getProcessDetailCode().equals(po.getProcessDetailCode()))
                                            .collect(Collectors.toList());
//                            List<TPrdWorkTicketDetailDTO> poLw = tPrdWorkTicketMapper.getTicketInfoLw(po.getProcessDetailCode(), pos.getId());
                                    if (poLw != null && poLw.size() != 0) {
                                        po.setPieceWorkTeamId(poLw.get(0).getPieceWorkTeamId());
                                        po.setPieceWorkTeamName(poLw.get(0).getPieceWorkTeamName());
                                    }

                                }
                            }

                            if (ticketInfo != null && ticketInfo.size() != 0) {
                                List<String> cargoList = Arrays.asList(pos.getCargoCode().split(","));
                                for (String cargoCode : cargoList) {
                                    //查询该货物是件杂还是散杂
                                    String cargoWorkType = tPrdWorkTicketMapper.getCargoWorkType(cargoCode);
                                    TYardTallyItemPO tYardTallyItemPO = new TYardTallyItemPO();

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    for (TPrdWorkTicketDetailDTO detailDTO : ticketInfo) {
                                        //如果已经作业
                                        TPrdWorkTiTckInfoDTO tPrdWorkTiTckInfoDTO = new TPrdWorkTiTckInfoDTO();
                                        tPrdWorkTiTckInfoDTO.setWorkPlanId(pos.getId());
                                        tPrdWorkTiTckInfoDTO.setId(pos.getId());
                                        tPrdWorkTiTckInfoDTO.setTicketType("2");
                                        tPrdWorkTiTckInfoDTO.setTrustNo(pos.getTrustNo());
                                        //根据计划ID查询是否已经签票
//                                            TPrdWorkTiTckInfoDTO dto = tPrdWorkTicketMapper.getIsWorkTicket(pos.getId());
                                        tPrdWorkTiTckInfoDTO.setClassName(dateFormat.format(pos.getWorkDate()) + " " + pos.getClassName());
                                        tPrdWorkTiTckInfoDTO.setWorkTicketStatusName("待审核");
                                        //赋值
                                        tPrdWorkTiTckInfoDTO.setDeptItemId(detailDTO.getDeptId());
                                        tPrdWorkTiTckInfoDTO.setDeptItemName(detailDTO.getDeptName());
                                        tPrdWorkTiTckInfoDTO.setPersonNelId(detailDTO.getPieceWorkTeamId());
                                        tPrdWorkTiTckInfoDTO.setPersonNelName(detailDTO.getPieceWorkTeamName());
                                        tPrdWorkTiTckInfoDTO.setEquipmentTypeName(detailDTO.getEquipmentTypeName());
                                        tPrdWorkTiTckInfoDTO.setEquipmentTypeCode(detailDTO.getEquipmentTypeCode());
                                        tPrdWorkTiTckInfoDTO.setEquipmentNo(detailDTO.getEquipmentNo());
                                        tPrdWorkTiTckInfoDTO.setEquipmentId(detailDTO.getEquipmentId());
                                        tPrdWorkTiTckInfoDTO.setProcessDetailCode(detailDTO.getProcessDetailCode());
                                        tPrdWorkTiTckInfoDTO.setProcessDetailName(detailDTO.getProcessDetailName());
                                        Map<String, Object> trustShipvoyage = tPrdWorkTicketMapper.getTrustShipvoyage(pos.getTrustId());
                                        if (trustShipvoyage != null) {
                                            tPrdWorkTiTckInfoDTO.setShipvoyageId(trustShipvoyage.get("id").toString());
                                            tPrdWorkTiTckInfoDTO.setShipvoyageItemId(trustShipvoyage.get("itemId").toString());
                                        }
                                        for (TBusCargoInfoDTO cargoInfoDTO : tBusCargoInfoDTOS) {
                                            //赋值货物信息
                                            if (cargoCode.equals(cargoInfoDTO.getCargoCode())) {
                                                tPrdWorkTiTckInfoDTO.setCargoCode(cargoInfoDTO.getCargoCode());
                                                tPrdWorkTiTckInfoDTO.setCargoName(cargoInfoDTO.getCargoName());
                                                tPrdWorkTiTckInfoDTO.setTrustId(cargoInfoDTO.getTrustId());
                                                tPrdWorkTiTckInfoDTO.setCargoInfoId(cargoInfoDTO.getId());
                                                tPrdWorkTiTckInfoDTO.setTrustCargoInfoId(cargoInfoDTO.getTrustCargoId());
                                                String cargoInfo = tPrdWorkTicketMapper.getCargoInfo(tPrdWorkTiTckInfoDTO.getCargoInfoId());
                                                if (cargoInfo != null) {
                                                    tPrdWorkTiTckInfoDTO.setCargoNameLabel(cargoInfo);
                                                }
                                                //设置船名
                                                Map<String, Object> map = tPrdWorkTicketMapper.getTrustWorkShipInfo(pos.getId());
                                                if (map != null) {
                                                    tPrdWorkTiTckInfoDTO.setShipNameVoyage(map.get("shipVoyage").toString());
                                                }
                                                break;
                                            }
                                        }
                                        if ("1".equals(cargoWorkType)) {
                                            //件货
//                                    List<TYardTallyItemPO> tallyItemList = tYardTallyItemPOList.stream()
//                                            .filter(obj -> obj.getPlanId().equals(pos.getId().toString()) && obj.getCargoCode().equals(cargoCode))
//                                            .collect(Collectors.toList());
//                                    if (!CollectionUtils.isEmpty(tallyItemList)) {
//                                        BigDecimal totalTon = tallyItemList.stream()
//                                                .map(item -> item.getTon())
//                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
//                                        Integer totalQuantity = tallyItemList.stream()
//                                                .mapToInt(item -> item.getQuantity())
//                                                .sum();
//                                        if (totalQuantity != null) {
//                                            tYardTallyItemPO.setQuantity(totalQuantity);
//                                        }
//                                        if (totalTon != null) {
//                                            tYardTallyItemPO.setTon(totalTon);
//                                        }
//                                    }

                                        } else {
                                            if ("2".equals(query.getCargoType()) || query.getCargoType() == null || "".equals(query.getCargoType())) {
                                                tYardTallyItemPO = tPrdWorkTicketMapper.getTallySh(tPrdWorkTiTckInfoDTO.getTrustCargoInfoId(), cargoCode, finalStartTime, finalEndTime);
                                            }
                                        }
                                        if (tYardTallyItemPO == null || tYardTallyItemPO.getTon() == null) {
                                            continue;
                                        }

                                        //赋值作业量
                                        if (tYardTallyItemPO.getQuantity() != null) {
                                            tPrdWorkTiTckInfoDTO.setQuantity(tYardTallyItemPO.getQuantity());
                                        }
                                        if (tYardTallyItemPO.getTon() != null) {
                                            tPrdWorkTiTckInfoDTO.setTon(tYardTallyItemPO.getTon());
                                        }
                                        //赋值开始时间和结束时间
                                        if (!CollectionUtils.isEmpty(scheduLelist)) {
                                            for (MWorkScheduleDTO mWorkScheduleDTO : scheduLelist) {
                                                if (pos.getClassCode().equals(mWorkScheduleDTO.getWorkScheduleCode())) {
                                                    if ("01".equals(mWorkScheduleDTO.getWorkScheduleCode())) {
                                                        tPrdWorkTiTckInfoDTO.setStartTime(format.format(query.getWorkDate()) + " " + formatM.format(mWorkScheduleDTO.getStartTime()));
                                                        tPrdWorkTiTckInfoDTO.setEndTime(format.format(query.getWorkDate()) + " " + formatM.format(mWorkScheduleDTO.getEndTime()));
                                                    } else {
                                                        tPrdWorkTiTckInfoDTO.setStartTime(format.format(query.getWorkDate()) + " " + formatM.format(mWorkScheduleDTO.getStartTime()));
                                                        // 创建一个Calendar实例
                                                        Calendar calendar = Calendar.getInstance();
                                                        // 将日期设定为当前日期
                                                        calendar.setTime(query.getWorkDate());
                                                        // 将日期加1天
                                                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                                                        // 获取加1天后的日期
                                                        Date tomorrow = calendar.getTime();
                                                        tPrdWorkTiTckInfoDTO.setEndTime(format.format(tomorrow) + " " + formatM.format(mWorkScheduleDTO.getEndTime()));
                                                    }

                                                }
                                            }
                                        }

                                        //赋值开始时间和结束时间
//                                            if ("01".equals(pos.getClassCode())) {
//                                                //白班
//                                                tPrdWorkTiTckInfoDTO.setStartTime(dateFormat.format(pos.getWorkDate()) + " 08:30");
//                                                tPrdWorkTiTckInfoDTO.setEndTime(dateFormat.format(pos.getWorkDate()) + " 19:30");
//                                            } else {
//                                                //夜班
//                                                tPrdWorkTiTckInfoDTO.setStartTime(dateFormat.format(pos.getWorkDate()).toString() + " 19:30");
//                                                // 创建一个Calendar实例
//                                                Calendar calendar = Calendar.getInstance();
//                                                // 将日期设定为当前日期
//                                                calendar.setTime(pos.getWorkDate());
//                                                // 将日期加1天
//                                                calendar.add(Calendar.DAY_OF_YEAR, 1);
//                                                // 获取加1天后的日期
//                                                Date tomorrow = calendar.getTime();
//                                                tPrdWorkTiTckInfoDTO.setEndTime(dateFormat.format(tomorrow) + " 08:30");
//                                            }
                                        //赋值场地
//                                    List<Map<String, Object>> locationStartList = tPrdWorkTicketMapper.getLocation(pos.getId(), "1");
//                                    List<Map<String, Object>> locationEndList = tPrdWorkTicketMapper.getLocation(pos.getId(), "2");
                                        List<Map<String, Object>> startList = locationtList.stream()
                                                .filter(obj -> obj.get("workPlanId").toString().equals(pos.getId().toString()) && obj.get("cargoInfoId").toString().equals(tPrdWorkTiTckInfoDTO.getCargoInfoId().toString()))
                                                .collect(Collectors.toList());
                                        if (!CollectionUtils.isEmpty(startList)) {
                                            if("集港".equals(pos.getType())){
                                                tPrdWorkTiTckInfoDTO.setStorehouseIdTarget(Long.parseLong(startList.get(0).get("storehouseId").toString()));
                                                tPrdWorkTiTckInfoDTO.setStorehouseNameTarget(startList.get(0).get("storehouseName").toString());
                                                tPrdWorkTiTckInfoDTO.setRegionIdTarget(Long.parseLong(startList.get(0).get("regionId").toString()));
                                                tPrdWorkTiTckInfoDTO.setRegionNameTarget(startList.get(0).get("regionName").toString());
                                                tPrdWorkTiTckInfoDTO.setMassIdTarget(Long.parseLong(startList.get(0).get("massId").toString()));
                                                tPrdWorkTiTckInfoDTO.setMassNameTarget(startList.get(0).get("massName").toString());
                                                tPrdWorkTiTckInfoDTO.setStorehouseNameTargetLabel(tPrdWorkTiTckInfoDTO.getStorehouseNameTarget() + "/" + tPrdWorkTiTckInfoDTO.getRegionNameTarget() + "/" + tPrdWorkTiTckInfoDTO.getMassNameTarget());
                                            }else if("疏港".equals(pos.getType()) || "陆销".equals(pos.getType())){
                                                tPrdWorkTiTckInfoDTO.setStorehouseIdSource(Long.parseLong(startList.get(0).get("storehouseId").toString()));
                                                tPrdWorkTiTckInfoDTO.setStorehouseNameSource(startList.get(0).get("storehouseName").toString());
                                                tPrdWorkTiTckInfoDTO.setRegionIdSource(Long.parseLong(startList.get(0).get("regionId").toString()));
                                                tPrdWorkTiTckInfoDTO.setRegionNameSource(startList.get(0).get("regionName").toString());
                                                tPrdWorkTiTckInfoDTO.setMassIdSource(Long.parseLong(startList.get(0).get("massId").toString()));
                                                tPrdWorkTiTckInfoDTO.setMassNameSource(startList.get(0).get("massName").toString());
                                                tPrdWorkTiTckInfoDTO.setStorehouseNameSourceLabel(tPrdWorkTiTckInfoDTO.getStorehouseNameSource() + "/" + tPrdWorkTiTckInfoDTO.getRegionNameSource() + "/" + tPrdWorkTiTckInfoDTO.getMassNameSource());
                                            }

                                        }else{
                                            if("集港".equals(pos.getType())){
                                                List<Map<String, Object>> startListLo = locationStartList.stream()
                                                        .filter(obj -> obj.get("workPlanId").toString().equals(pos.getId().toString()))
                                                        .collect(Collectors.toList());
                                                if(!CollectionUtils.isEmpty(startListLo)){
                                                    tPrdWorkTiTckInfoDTO.setStorehouseIdTarget(Long.parseLong(startListLo.get(0).get("storehouseId").toString()));
                                                    tPrdWorkTiTckInfoDTO.setStorehouseNameTarget(startListLo.get(0).get("storehouseName").toString());
                                                    tPrdWorkTiTckInfoDTO.setRegionIdTarget(Long.parseLong(startListLo.get(0).get("regionId").toString()));
                                                    tPrdWorkTiTckInfoDTO.setRegionNameTarget(startListLo.get(0).get("regionName").toString());
                                                    tPrdWorkTiTckInfoDTO.setMassIdTarget(Long.parseLong(startListLo.get(0).get("massId").toString()));
                                                    tPrdWorkTiTckInfoDTO.setMassNameTarget(startListLo.get(0).get("massName").toString());
                                                    tPrdWorkTiTckInfoDTO.setStorehouseNameTargetLabel(tPrdWorkTiTckInfoDTO.getStorehouseNameTarget() + "/" + tPrdWorkTiTckInfoDTO.getRegionNameTarget() + "/" + tPrdWorkTiTckInfoDTO.getMassNameTarget());
                                                }
                                            }else if ("疏港".equals(pos.getType()) || "陆销".equals(pos.getType())){
                                                List<Map<String, Object>> endListLo = locationEndList.stream()
                                                        .filter(obj -> obj.get("workPlanId").toString().equals(pos.getId().toString()))
                                                        .collect(Collectors.toList());
                                                if(!CollectionUtils.isEmpty(endListLo)){
                                                    tPrdWorkTiTckInfoDTO.setStorehouseIdSource(Long.parseLong(endListLo.get(0).get("storehouseId").toString()));
                                                    tPrdWorkTiTckInfoDTO.setStorehouseNameSource(endListLo.get(0).get("storehouseName").toString());
                                                    tPrdWorkTiTckInfoDTO.setRegionIdSource(Long.parseLong(endListLo.get(0).get("regionId").toString()));
                                                    tPrdWorkTiTckInfoDTO.setRegionNameSource(endListLo.get(0).get("regionName").toString());
                                                    tPrdWorkTiTckInfoDTO.setMassIdSource(Long.parseLong(endListLo.get(0).get("massId").toString()));
                                                    tPrdWorkTiTckInfoDTO.setMassNameSource(endListLo.get(0).get("massName").toString());
                                                    tPrdWorkTiTckInfoDTO.setStorehouseNameSourceLabel(tPrdWorkTiTckInfoDTO.getStorehouseNameSource() + "/" + tPrdWorkTiTckInfoDTO.getRegionNameSource() + "/" + tPrdWorkTiTckInfoDTO.getMassNameSource());
                                                }
                                            }
                                        }
                                        ticketList.add(tPrdWorkTiTckInfoDTO);
                                    }
                                }
                            }
                        }
                    });

                }
                // 关闭线程池
                executor.shutdown();
                try {
                    // 等待所有任务执行完成，最多等待1小时
                    if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                        // 如果超时，输出提示信息
                        System.out.println("任务超时！");
                    }
                } catch (InterruptedException e) {
                    // 捕获中断异常
                    e.printStackTrace();
                }
            }
            //过滤车/岸 作业过程
            List<TPrdWorkTiTckInfoDTO> filteredList = ticketList.stream()
                    .filter(ticket -> (!"10390001".equals(ticket.getProcessDetailCode())) || ticket.getCreateTime() != null)
                    .collect(Collectors.toList());
            return filteredList.stream()
                    .sorted(Comparator.comparingLong(TPrdWorkTiTckInfoDTO::getId).reversed()) // 按照 id 字段降序排序
                    .collect(Collectors.toList());
        } else {
            List<TPrdWorkTiTckInfoDTO> list = tPrdWorkTicketMapper.getWorkTicketList(query);
            return list;
        }

    }

    @Override
    public Map<String, Object> getWorkMeasure(TicketMeasureDTO ticketMeasureDTO) {
        //判断货物信息是件杂还是散杂 件杂取理货数据 散杂取地磅数据
        String workFlag = tPrdWorkTicketMapper.getCargoWorkType(ticketMeasureDTO.getCargoCode());
        if ("1".equals(workFlag)) {
            //件杂
            //根据计划ID，作业过程，票货ID 汇总作业量
            return tPrdWorkTicketMapper.getTallyWorkMeasure(ticketMeasureDTO);
        } else {
            TPrdWorkPlanPO workPlan = tPrdWorkTicketMapper.getWorkPlan(ticketMeasureDTO.getWorkPlanId());
            List<Long> cargoTrustIds = tPrdWorkTicketMapper.getWorkCargoTrustId(ticketMeasureDTO.getCargoInfoId());
            List<MWorkScheduleDTO> scheduLelist = mWorkScheduleMapper.getList();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatM = new SimpleDateFormat("HH:mm");
            String startTime = "";
            String endTime = "";
            if (!CollectionUtils.isEmpty(scheduLelist)) {
                for (MWorkScheduleDTO mWorkScheduleDTO : scheduLelist) {
                    if (workPlan.getClassCode().equals(mWorkScheduleDTO.getWorkScheduleCode())) {
                        if ("01".equals(mWorkScheduleDTO.getWorkScheduleCode())) {
                            startTime = format.format(workPlan.getWorkDate()) + " " + formatM.format(mWorkScheduleDTO.getStartTime());
                            endTime = format.format(workPlan.getWorkDate()) + " " + formatM.format(mWorkScheduleDTO.getEndTime());
                        } else {
                            startTime = format.format(workPlan.getWorkDate()) + " " + formatM.format(mWorkScheduleDTO.getStartTime());
                            // 创建一个Calendar实例
                            Calendar calendar = Calendar.getInstance();
                            // 将日期设定为当前日期
                            calendar.setTime(workPlan.getWorkDate());
                            // 将日期加1天
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            // 获取加1天后的日期
                            Date tomorrow = calendar.getTime();
                            endTime = format.format(tomorrow) + " " + formatM.format(mWorkScheduleDTO.getEndTime());
                        }

                    }
                }
            }
            TYardTallyItemPO tallySh = tPrdWorkTicketMapper.getTallyShZq(cargoTrustIds, ticketMeasureDTO.getCargoCode(), startTime, endTime);
            if(tallySh != null){
                Map<String,Object> map = new HashMap<>();
                map.put("quantity","1");
                map.put("ton",tallySh.getTon());
                return map;
            }else{
                return null;
            }
        }
    }

    @Override
    public Integer getUserRole(String flag) {
        Long loginUserId = securityUtils.getLoginUserId();
        String str = "";
        if ("1".equals(flag)) {
            //调度
            str = " ROLE_ID IN ( 1706473088360779776,1710194450317316096,1679029256648265728,1736388455765774336,1 )";
        } else {
            //库场
            str = "ROLE_ID IN ( 1719384815528185856,1710202082142851072,1684477296381136896,1 )";
        }
        return tPrdWorkTicketMapper.getUserRole(str, loginUserId);
    }

    @Override
    public List<TPrdWorkTiTckInfoDTO> getMonthWorkTicketList(TPrdWorkPlanQuery query) {

        if (query.getStartDay() == null) {
            throw new BusinessRuntimeException("开始日期不能为空");
        }

        if (query.getEndDay() == null) {
            throw new BusinessRuntimeException("结束日期不能为空");
        }

        List<TPrdWorkTiTckInfoDTO> list = tPrdWorkTicketMapper.getMonthWorkTicketList(query);
        if ("2".equals(query.getPlanType())) {
            List<Long> ids = list.stream().map(TPrdWorkTiTckInfoDTO::getTrustId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(ids)) {
                List<Map<String, Object>> shipvoyageItems = tBusTrustMapper.listShipvoyageItemByTrustIds(ids);
                if (!ids.isEmpty()) {
                    list.stream().filter(v1 -> ids.contains(v1.getTrustId()))
                            .forEach(v1 -> {
                                String shipNameVoyages = shipvoyageItems.stream()
                                        .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                        .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                        .collect(Collectors.joining("，"));
                                v1.setShipNameVoyage(shipNameVoyages);
                                String shipVoyageIds = shipvoyageItems.stream()
                                        .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                        .map(v2 -> String.valueOf(v2.get("shipvoyageItemId")))
                                        .collect(Collectors.joining("，"));
                                v1.setShipVoyageIds(shipVoyageIds);

                            });
                    if (query.getShipvoyageItemId() != null) {
                        ArrayList<TPrdWorkTiTckInfoDTO> tPrdWorkTiTckInfoDTOS = new ArrayList<>();
                        if (query.getShipvoyageItemId() != null) {
                            for (TPrdWorkTiTckInfoDTO s : list) {
                                String[] split = s.getShipVoyageIds().split("，");
                                for (String s1 : split) {
                                    if (s1.equals(query.getShipvoyageItemId().toString())) {
                                        tPrdWorkTiTckInfoDTOS.add(s);
                                        continue;
                                    }
                                }
                            }
                        }
                        list = tPrdWorkTiTckInfoDTOS;
                    }

                }
            }
        }
        return list;

    }

    @Override
    public byte[] exportExcel(TPrdWorkPlanQuery query) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TPrdWorkTiTickInfoExportDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TPrdWorkTiTickInfoExportDTO> cursor = tPrdWorkTicketMapper.getExportMonthWorkTicketList(query)) {
                    Iterator<TPrdWorkTiTickInfoExportDTO> iterator = cursor.iterator();
                    if (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            List<TPrdWorkTiTickInfoExportDTO> salarys = new ArrayList<>();
                            for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                                salarys.add(iterator.next());
                            }
                            if ("2".equals(query.getPlanType())) {
                                List<Long> ids = salarys.stream().map(TPrdWorkTiTickInfoExportDTO::getTrustId).collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(ids)) {
                                    List<Map<String, Object>> shipvoyageItems = tBusTrustMapper.listShipvoyageItemByTrustIds(ids);
                                    if (!ids.isEmpty()) {
                                        salarys.stream().filter(v1 -> ids.contains(v1.getTrustId()))
                                                .forEach(v1 -> {
                                                    String shipNameVoyages = shipvoyageItems.stream()
                                                            .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                                            .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                                            .collect(Collectors.joining("，"));
                                                    v1.setShipNameVoyage(shipNameVoyages);
                                                    String shipVoyageIds = shipvoyageItems.stream()
                                                            .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                                            .map(v2 -> String.valueOf(v2.get("shipvoyageItemId")))
                                                            .collect(Collectors.joining("，"));
                                                    v1.setShipVoyageIds(shipVoyageIds);

                                                });
                                        if (query.getShipvoyageItemId() != null) {
                                            ArrayList<TPrdWorkTiTickInfoExportDTO> tPrdWorkTiTckInfoDTOS = new ArrayList<>();
                                            if (query.getShipvoyageItemId() != null) {
                                                for (TPrdWorkTiTickInfoExportDTO s : salarys) {
                                                    String[] split = s.getShipVoyageIds().split("，");
                                                    for (String s1 : split) {
                                                        if (s1.equals(query.getShipvoyageItemId().toString())) {
                                                            tPrdWorkTiTckInfoDTOS.add(s);
                                                            continue;
                                                        }
                                                    }
                                                }
                                            }
                                            salarys = tPrdWorkTiTckInfoDTOS;
                                        }

                                    }
                                }
                            }
                            excelWriter.write(salarys, writeSheet);
                        }
                    } else {
                        excelWriter.write(Collections.emptyList(), writeSheet);
                    }

                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }


    @Override
    public List<TPrdWorkTicketDetailDTO> getTicketInfo(Long workPlanId, String type, String cargoCode, String processCode) {
        String typeY = type;
        if ("040000020001".contains(cargoCode) || processCode.equals("1025") || processCode.equals("1026")) {
            type = "3";
        }
        List<TPrdWorkTicketDetailDTO> list = tPrdWorkTicketMapper.getTicketInfo(workPlanId, type);
        if(CollectionUtils.isEmpty(list) && "2".equals(type)){
            List<TPrdWorkTicketDetailDTO> ticketInfo = tPrdWorkTicketMapper.getTicketInfo(workPlanId, "4");
            if(!CollectionUtils.isEmpty(ticketInfo)){
                list.addAll(ticketInfo);
            }
        }
        if (list != null && list.size() != 0) {
            //船-场 场-船 才走此步骤
            /**
             * 实现思路:把岸-车 车-岸作业过程签票部门置为空，
             * 船-场，场-船时把全部的二次派工信息查询出来，
             * 然后在依次匹配，不一致的删掉
             * 作业过程为空的就是岸-车 车-岸 ，判断 件货 - 调度 散货 - 库场 不符合 删掉
             */
            if (processCode.equals("1025") || processCode.equals("1026")) {
                Iterator<TPrdWorkTicketDetailDTO> iter = list.iterator();
                while (iter.hasNext()) {
                    TPrdWorkTicketDetailDTO po = iter.next();
                    String flag = tPrdWorkTicketMapper.getProcessTicketType(po.getProcessDetailCode());
                    if (flag != null) {
                        if (!flag.equals(typeY)) {
                            //iter.remove();
                        }
                    } else {
                        //岸-车 车-岸
                        List<String> cargo = Arrays.asList(cargoCode.split(","));
                        String cargoWorkType = tPrdWorkTicketMapper.getCargoWorkType(cargo.get(0));
                        if (!cargoWorkType.equals(typeY)) {
                            //cargoWorkType 1:件货  typeY 1 :调度
                            //cargoWorkType 2:散货  typeY 1 :库场
                            //iter.remove();
                        }
                    }
                }
            }
            for (int i = 0; i < list.size(); i++) {
                //根据作业过程查询装卸队派工
                TPrdWorkTicketDetailDTO po = list.get(i);
                List<TPrdWorkTicketDetailDTO> poLw = tPrdWorkTicketMapper.getTicketInfoLw(po.getProcessDetailCode(), workPlanId);
                if (poLw != null && poLw.size() != 0) {
                    //找人员班组的部门
                    Map<String, Object> maps = tPrdWorkTicketMapper.getDeptRy(poLw.get(0).getPieceWorkTeamId());
                    if (maps == null) {
                        throw new BusinessRuntimeException("获取人员班组上级部门失败");
                    }
                    po.setPieceWorkTeamId(Long.parseLong(maps.get("deptId").toString()));
                    po.setPieceWorkTeamName(maps.get("deptName").toString());
//                    if(poLw.size() == list.size()){
//                        //如果相同一一赋值
//                        po.setPieceWorkTeamId(poLw.get(i).getPieceWorkTeamId());
//                    }else if(poLw.size() > list.size()){
//
//                    }
                }

            }
        }

        return list;
    }

    @Override
    public List<SysDeptDTO> getDepts(String type) {
        return tPrdWorkTicketMapper.getDepts(type);
    }

    @Override
    public List<SysDeptDTO> getDeptsTally() {
        UserInfo userInfo = securityUtils.getUserInfo();
        return tPrdWorkTicketMapper.getDeptsTally(userInfo.getDeptId());
    }

    @Override
    public Integer getProcessType(String code) {
        return tPrdWorkTicketMapper.getIsProcrss(code);
    }

    @Override
    public List<String> getProcessIsTally(String processCode, String type, String cargoCode, Long workPlanId) {
        if (cargoCode != null) {

            //卷钢
            if ("040000020001".contains(cargoCode)) {
                type = "1";
            }
        }
        List<String> processIsTally = tPrdWorkTicketMapper.getProcessIsTally(processCode, type);
        if(CollectionUtils.isEmpty(processIsTally) && "2".equals(type) ){
            List<String> list = tPrdWorkTicketMapper.getProcessIsTally(processCode, "4");
            if(!CollectionUtils.isEmpty(list)){
                processIsTally.addAll(list);
            }
        }
//        Iterator<String> iter = processIsTally.iterator();
//        while (iter.hasNext()) {
//            Integer count = tPrdWorkTicketMapper.getIsPg(iter.next(), workPlanId);
//            if (count == 0) {
//                //二次派工没有该作业过程
//                iter.remove();
//            }
//        }
        return processIsTally;
    }

    @Override
    public String getProcess(String processCode, String type) {
        String str = tPrdWorkTicketMapper.getProcess(processCode, type);
        if (str == null) {
            return "请检查该计划二次派工中是否指派对应作业过程";
        }
        return "请检查该计划二次派工中是否已指派" + str + "中任意作业过程";
    }


    public Map<String, String> getDays(String yue) {
        Map<String, String> map = new HashMap<>();
        if (yue == null) {
            throw new BusinessRuntimeException("月份不能为空");
        }
        String[] date = yue.split("-");
        int year = Integer.parseInt(date[0]); // 要查询的年份
        int month = Integer.parseInt(date[1]); // 要查询的月份（1-12）
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1); // 设置日期为1号
        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH); // 获取这个月的天数
        Integer startDay = 0;
        Integer endDay = 0;
        //获取字典的开始天数和结束天数
        List<Map<String, Object>> resList = publicMapper.getDictListByType("SALARY_DAY");
        if (resList != null) {
            startDay = Integer.parseInt((String) resList.get(0).get("value"));
            endDay = Integer.parseInt((String) resList.get(1).get("value"));
        }
        if (days < endDay) {
            //某月天数小于于维护天数
            endDay = days;
        }
        map.put("startDay", year + "-" + month + "-" + startDay);
        map.put("endDay", year + "-" + month + "-" + endDay);
        return map;
    }
}
