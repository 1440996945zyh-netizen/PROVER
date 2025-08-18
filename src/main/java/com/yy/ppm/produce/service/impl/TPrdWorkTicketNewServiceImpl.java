package com.yy.ppm.produce.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.analysis.v07.handlers.MergeCellTagHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.Page;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.*;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.concurrent.DistributedLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.appWork.bean.dto.TallyRecordSearchDTO;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.appWork.mapper.TallyMapper;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.common.enums.DispatchEnum;
import com.yy.ppm.common.enums.DistributedLockKeyPrefixEnum;
import com.yy.ppm.common.enums.SalaryStatusEnum;
import com.yy.ppm.common.enums.WorkTicketStatusEnum;
import com.yy.ppm.common.mapper.PublicMapper;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.largescreen.bean.dto.SInportCarExportDTO;
import com.yy.ppm.master.bean.dto.MWorkProcessDTO;
import com.yy.ppm.master.bean.dto.MWorkScheduleDTO;
import com.yy.ppm.master.bean.po.MPieceWorkTeamPO;
import com.yy.ppm.master.bean.po.StorageYardPO;
import com.yy.ppm.master.mapper.MWorkScheduleMapper;
import com.yy.ppm.produce.bean.dto.*;
import com.yy.ppm.produce.bean.dto.workTicket.*;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.po.*;
import com.yy.ppm.produce.mapper.TPrdDySumMapper;
import com.yy.ppm.produce.mapper.TPrdTicketSecondAllotMapper;
import com.yy.ppm.produce.mapper.TPrdWorkPlanMapper;
import com.yy.ppm.produce.mapper.TPrdWorkTicketNewMapper;
import com.yy.ppm.produce.service.TPrdDispatchSecondaryService;
import com.yy.ppm.produce.service.TPrdDySumService;
import com.yy.ppm.produce.service.TPrdWorkTicketNewService;
import com.yy.ppm.produce.service.TPrdWorkTicketService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import com.yy.ppm.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.cursor.defaults.DefaultCursor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 15:37
 */
@Service
@Slf4j
public class TPrdWorkTicketNewServiceImpl implements TPrdWorkTicketNewService {
    private static final MicroLogger LOGGER = new MicroLogger(TPrdWorkTicketService.class);

    @Autowired
    private TPrdWorkTicketNewMapper workTicketMapper;

    @Resource
    private MWorkScheduleMapper mWorkScheduleMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

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

    @Autowired
    private TPrdDispatchSecondaryService tPrdDispatchSecondaryService;
    @Resource
    private TPrdTicketSecondAllotMapper ticketSecondAllotMapper;

    @Resource
    private TPrdDySumMapper dySumMapper;

    @Autowired
    private TPrdDySumService dySumService;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    TPrdWorkTicketNewMapper tPrdWorkTicketNewMapper;

    private static final ThreadPoolTaskExecutor TICKET_EXECUTOR = new ThreadPoolTaskExecutor();

    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    static {
        TICKET_EXECUTOR.setQueueCapacity(Integer.MAX_VALUE);
        TICKET_EXECUTOR.setCorePoolSize(CORE_SIZE * 2);
        TICKET_EXECUTOR.setMaxPoolSize(CORE_SIZE * 2);
        TICKET_EXECUTOR.setThreadNamePrefix("STORAGE_AMOUNT_CALCULATE_TASK_");
        TICKET_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        TICKET_EXECUTOR.initialize();
        TICKET_EXECUTOR.getThreadPoolExecutor().prestartAllCoreThreads();
    }

    /**
     * 场地类型：垛位
     */
    private static final String STORAGE_YARD_LEVEL_MASS = "3";

    private static final int CURSOR_LIMIT = 5_000;
    private static final String STATUS_APPROVE = "0";
    private static final String PROCESS_CD_SHIP = "01"; //作业过程中的 源或目的为船 process_cd

    private static final String FOR_MACHINE = "2";//分配类型是机械
    private static final String FOR_LABOR = "3";//分配类型是人工
    private static final String DISP_FOR_MACHINE = "1";//分配类型是机械
    private static final String DISP_FOR_LABOR = "2";//分配类型是人工
    /**
     * 需要进行特殊处理的作业过程 process_cd
     */
    private static final String SHIP_CAR_AREA = "10260008";//船-车-场
    private static final String AREA_CAR_SHIP  = "10250009";//场-车-船



    @Autowired
    private SpringContextUtils springContextUtils;


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
        List<TPrdWorkPlanDTO> list = workTicketMapper.listWorkPlan(searchDTO);
        if (!CollectionUtils.isEmpty(list)) {
            Iterator<TPrdWorkPlanDTO> iter = list.iterator();
            while (iter.hasNext()) {
                TPrdWorkPlanDTO po = iter.next();
                if (po.getCargoOwnerId() != null) {
                    List<String> idLists = Arrays.asList(po.getCargoOwnerId().split(","));
                    Integer count = workTicketMapper.getIsStations(idLists);
                    if (count > 0) {
                        //有场站的删除
                        iter.remove();
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

        //过滤船名航次
        if(!list.isEmpty()&&StringUtils.isNotBlank(searchDTO.getShipName())){
            list  = list.stream().filter(o->StringUtils.isNotBlank(o.getShipvoyageLabel())&&o.getShipvoyageLabel().split("_")[0].contains(searchDTO.getShipName())).collect(Collectors.toList());
        }
        if(!list.isEmpty()&&StringUtils.isNotBlank(searchDTO.getVoyage())){
            list  = list.stream().filter(o->StringUtils.isNotBlank(o.getShipvoyageLabel())&&o.getShipvoyageLabel().split("_")[1].contains(searchDTO.getVoyage())).collect(Collectors.toList());
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
            List<TPrdWorkPlanLocationDTO> workPlanLocationList = workTicketMapper.getWorkPlanLocationList(searchDTO);
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
                    String status = workTicketMapper.getTicketStatus(o.getId(), "1");
                    if ("已审核".equals(status)) {
                        o.setIsSigned("已审核");
                    }
                }
//                if ("已签票".equals(o.getIsSignedKc())) {
//                    String status = workTicketMapper.getTicketStatus(o.getId(), "2");
//                    if ("已审核".equals(status)) {
//                        o.setIsSignedKc("已审核");
//                    }
//                }
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

        //过滤理货状态
        if(!list.isEmpty()&&StringUtils.isNotBlank(searchDTO.getIsTicket())){
            list  = list.stream().filter(o->searchDTO.getIsTicket().equals(o.getIsSigned())).collect(Collectors.toList());
        }

        return list.stream().sorted(Comparator.comparing(o->o.getTrustNo()+"_"+o.getShipvoyageLabel())).collect(Collectors.toList());
    }


    /**
     * 获取工班计划列表(可编辑列表用）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO> lgListWorkPlan(TPrdWorkPlanSearchDTO searchDTO) {

        List<com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO> workPlanList = new ArrayList<>();
        workPlanList = tPrdWorkPlanMapper.getLgWorkPlanList(searchDTO);

        List<Long> ids = workPlanList.stream().map(com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO::getId).collect(Collectors.toList());
        searchDTO.setIds(ids);
        Map<String,Map<String,String>> macMap = Maps.newHashMap();
        if(CollectionUtils.isNotEmpty(ids)){
            List<Map<String,String>> macNumList = tPrdWorkPlanMapper.getLgMacNum(ids);
            macMap = macNumList.stream().collect(Collectors.toMap(e->e.get("workLocation"), Function.identity()));
        }
        if (org.springframework.util.CollectionUtils.isEmpty(ids)){
            return  workPlanList;
        }
        //配机配工
        List<TPrdDispatchDTO> dispatchList = tPrdWorkPlanMapper.getPrdDispatch(searchDTO);
        Map<String, List<TPrdDispatchDTO>> dispatchMap = new HashMap<>();
        //回显配机列表
        if (!org.springframework.util.CollectionUtils.isEmpty(dispatchList)){
            dispatchMap = dispatchList.stream().collect(Collectors.groupingBy(o -> {
                return o.getWorkPlanId() + "/" + o.getDispatchType() + "/" + o.getWorkPositionCode();
            }));
        }
        HashMap<String, String> machineStrins = new HashMap<>();
        if(!org.springframework.util.CollectionUtils.isEmpty(dispatchMap)){
//            dispatchMap.forEach((k,v)->{
//                String s = "";
//                if(!org.springframework.util.CollectionUtils.isEmpty(v)){
//
//                    for (int i = 0; i < v.size(); i++) {
//                        if(i==0){
//                            s = v.get(i).getEquipmentTypeName()+v.get(i).getNum();
//                        }else {
//                            s+=","+v.get(i).getEquipmentTypeName()+v.get(i).getNum();
//                        }
//                    }
//
//                }
//                machineStrins.put(k,s);
//            });
        }
        //  位置
        List<TPrdWorkPlanLocationDTO> workPlanLocationList = tPrdWorkPlanMapper.getWorkPlanLocationList(searchDTO);
        Map<String, List<String>> tmpLoctionMap = workPlanLocationList.stream().collect(Collectors.groupingBy(
                o ->  o.getWorkPlanId() + "/" + o.getDirection(),
                Collectors.mapping(o->o.getStorehouseName()+"/"+o.getRegionName()+"/"+o.getMassName(),
                        Collectors.toList())));

        HashMap<String, String> locationNameMap = new HashMap<>();
        if(!org.springframework.util.CollectionUtils.isEmpty(tmpLoctionMap)){
//            tmpLoctionMap.forEach((k,v)->{
//                String massNames = "";
//                if(!org.springframework.util.CollectionUtils.isEmpty(v)){
//                    for (int i = 0; i < v.size(); i++) {
//                        if(i==0){
//                            massNames = v.get(i);
//                        }else {
//                            massNames += " , " +v.get(i);
//                        }
//                    }
//                }
//                locationNameMap.put(k,massNames);
//            });
        }

        //货主货代
        List<Long> searchTrustIds = workPlanList.stream().map(com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO::getTrustId).collect(Collectors.toList());
        searchDTO.setIds(searchTrustIds);
        List<TBusTrustCargoDTO> trustCargoList = tPrdWorkPlanMapper.getTrustCargoList(searchDTO);
        Map<Long, List<TBusTrustCargoDTO>> trustCargoMap =
                trustCargoList.stream().collect(Collectors.groupingBy(TBusTrustCargoDTO::getTrustId));
        //根据票货信息获取货主货代
        List<Long> cargoInfoIds = workPlanList.stream().map(com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO::getBusCargoInfoId).collect(Collectors.toList());
        List<com.yy.ppm.business.bean.dto.TBusCargoInfoDTO> cargoInfoDTOS = tPrdWorkPlanMapper.getCargoInfoByIds(cargoInfoIds);
        Map<Long, List<com.yy.ppm.business.bean.dto.TBusCargoInfoDTO>> cargoInfoMap = cargoInfoDTOS.stream().collect(Collectors.groupingBy(com.yy.ppm.business.bean.dto.TBusCargoInfoDTO::getId));

        if (!CollectionUtils.isEmpty(ids)) {
            List<Map<String, Object>> flowStatusList = tPrdWorkPlanMapper.getFlowStatus(ids);
            List<Map<String, Object>> fixedStatusList = tPrdWorkPlanMapper.getFixedStatus(ids);
            List<Map<String, Object>> laborStatusList = tPrdWorkPlanMapper.getLaborStatus(ids);

            for (com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO data : workPlanList) {

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

        //  01 前沿 02 后场  03 水平 04 辅助
        for (com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO o : workPlanList) {
            if(!org.springframework.util.CollectionUtils.isEmpty(dispatchMap)){
                //前沿
                if(!org.springframework.util.CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/01")))){
                    o.setEqumentListFront(dispatchMap.get((o.getId()+"/"+"1/01")));
                    o.setEquipmentNamesFront(machineStrins.get((o.getId()+"/"+"1/01")));
                }
                //后沿
                if(!org.springframework.util.CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/02")))){
                    o.setEqumentListBack(dispatchMap.get((o.getId()+"/"+"1/02")));
                    o.setEquipmentNamesBack(machineStrins.get((o.getId()+"/"+"1/02")));
                }
                //水平
                if(!org.springframework.util.CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/03")))){
                    o.setEqumentListReshipment(dispatchMap.get((o.getId()+"/"+"1/03")));
                    o.setEquipmentNamesReshipment(machineStrins.get((o.getId()+"/"+"1/03")));
                }
                //辅助
                if(!org.springframework.util.CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/04")))){
                    o.setEqumentListAssist(dispatchMap.get((o.getId()+"/"+"1/04")));
                    o.setEquipmentNamesAssist(machineStrins.get((o.getId()+"/"+"1/04")));
                }
                //装卸队
                if(!org.springframework.util.CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"2/null")))){
                    o.setLaborNum(dispatchMap.get((o.getId()+"/"+"2/null")).get(0).getNum());
                }
            }else if(!org.springframework.util.CollectionUtils.isEmpty(macMap)){
                if(StringUtils.isBlank(o.getEquipmentNamesFront())){
                    o.setEquipmentNamesFront(ObjectUtil.isNotEmpty(macMap.get(o.getId()+"_前沿"))?macMap.get(o.getId()+"_前沿").get("workLocationLabel"):null);
                }
                if(StringUtils.isBlank(o.getEquipmentNamesBack())){
                    o.setEquipmentNamesBack(ObjectUtil.isNotEmpty(macMap.get(o.getId()+"_后场"))?macMap.get(o.getId()+"_后场").get("workLocationLabel"):null);
                }
                if(StringUtils.isBlank(o.getEquipmentNamesReshipment())){
                    o.setEquipmentNamesReshipment(ObjectUtil.isNotEmpty(macMap.get(o.getId()+"_水平"))?macMap.get(o.getId()+"_水平").get("workLocationLabel"):null);
                }
                if(StringUtils.isBlank(o.getEquipmentNamesAssist())){
                    o.setEquipmentNamesAssist(ObjectUtil.isNotEmpty(macMap.get(o.getId()+"_辅助"))?macMap.get(o.getId()+"_辅助").get("workLocationLabel"):null);
                }
            }

            //场地
            if (!org.springframework.util.CollectionUtils.isEmpty(locationNameMap)){
                //前沿
                if(!org.springframework.util.StringUtils.isEmpty(locationNameMap.get((o.getId()+"/1")))
                ){
                    o.setMassNamesSource(locationNameMap.get((o.getId()+"/1")));
                }//后沿
                if(!org.springframework.util.StringUtils.isEmpty(locationNameMap.get((o.getId()+"/2")))){
                    o.setMassNamesTarget(locationNameMap.get((o.getId()+"/2")));
                }
            }
            if(!org.springframework.util.CollectionUtils.isEmpty(trustCargoMap)){
                if(!org.springframework.util.CollectionUtils.isEmpty(trustCargoMap.get(o.getTrustId()))){
                    List<TBusTrustCargoDTO> dtos = trustCargoMap.get(o.getTrustId());
                    if(!org.springframework.util.CollectionUtils.isEmpty(dtos)){
                        String cargoAgentNames = "";
                        String cargoOwnerName = "";
                        String cargoOwnerIds ="";
                        String cargoName = "";
                        String cargoCode = "";
                        for (int i = 0; i < dtos.size(); i++) {
                            if(i==0){
                                cargoAgentNames = dtos.get(i).getCargoAgentName();
                                cargoOwnerName = dtos.get(i).getCargoOwnerName();
                                cargoOwnerIds = String.valueOf(dtos.get(i).getCargoOwnerId());
                                cargoName = dtos.get(i).getCargoName();
                                cargoCode = dtos.get(i).getCargoCode();
                            }else {
                                cargoAgentNames +=","+dtos.get(i).getCargoAgentName();
                                cargoOwnerIds += "," + String.valueOf(dtos.get(i).getCargoOwnerId());
                                cargoOwnerName +=","+dtos.get(i).getCargoOwnerName();
                                cargoName +=","+ dtos.get(i).getCargoName();
                            }
                        }
                        o.setCargoAgentName(cargoAgentNames);
                        o.setCargoOwnerName(cargoOwnerName);
                        o.setCargoOwnerIds(cargoOwnerIds);
                        o.setCargoCode(cargoCode);
                        o.setCargoName(cargoName);
                    }
                } else if(!org.springframework.util.CollectionUtils.isEmpty(cargoInfoMap.get(o.getBusCargoInfoId()))){
                    List<com.yy.ppm.business.bean.dto.TBusCargoInfoDTO> cargoInfoDTOList = cargoInfoMap.get(o.getBusCargoInfoId());
                    String cargoAgentNames = "";
                    String cargoOwnerName = "";
                    String cargoOwnerIds ="";
                    String cargoName = "";
                    String cargoCode = "";
                    String shipName = "";
                    String voyage = "";
                    Long shipvoyageId = null;
                    Long shipvoyageItemId = null;
                    for (int i = 0; i < cargoInfoDTOList.size(); i++) {
                        com.yy.ppm.business.bean.dto.TBusCargoInfoDTO cargoInfoDTO = cargoInfoDTOList.get(i);
                        if(i==0){
                            cargoAgentNames = cargoInfoDTO.getCargoAgentName();
                            cargoOwnerName = cargoInfoDTO.getCargoOwnerName();
                            cargoOwnerIds = String.valueOf(cargoInfoDTO.getCargoOwnerId());
                            cargoName = cargoInfoDTO.getCargoName();
                            cargoCode = cargoInfoDTO.getCargoCode();
                            shipName = cargoInfoDTO.getShipName();
                            voyage = cargoInfoDTO.getVoyage();
                            shipvoyageId = cargoInfoDTO.getShipvoyageId();
                            shipvoyageItemId = cargoInfoDTO.getShipvoyageItemId();
                        }else {
                            cargoAgentNames +=","+cargoInfoDTO.getCargoAgentName();
                            cargoOwnerIds += "," + String.valueOf(cargoInfoDTO.getCargoOwnerId());
                            cargoOwnerName +=","+cargoInfoDTO.getCargoOwnerName();
                            cargoName +=","+ cargoInfoDTO.getCargoName();
                        }
                    }
                    o.setCargoAgentName(cargoAgentNames);
                    o.setCargoOwnerName(cargoOwnerName);
                    o.setCargoOwnerIds(cargoOwnerIds);
                    o.setCargoCode(cargoCode);
                    o.setCargoName(cargoName);
                    o.setShipvoyageLabel((!"*".equals(shipName) && StringUtil.isNotEmpty(shipName))?(shipName+"_"+voyage):"");//船名_航次
                    o.setShipName((!"*".equals(shipName) && StringUtil.isNotEmpty(shipName))?shipName:"");//船名
                    o.setVoyage((!"*".equals(voyage) && StringUtil.isNotEmpty(voyage))?voyage:"");
                    o.setShipvoyageId(shipvoyageId);
                    o.setShipvoyageItemId(shipvoyageItemId);
                }
            }
        }
        if(searchDTO.getCargoOwnerId() != null ) {
            workPlanList = workPlanList.stream().filter(s ->
                    s.getCargoOwnerIds().contains(searchDTO.getCargoOwnerId().toString())).collect(Collectors.toList());
        }
        if(searchDTO.getCargoCode() != null ) {
            workPlanList = workPlanList.stream().filter(s ->
                    s.getCargoCode().contains(searchDTO.getCargoCode())).collect(Collectors.toList());
        }
        if(org.springframework.util.CollectionUtils.isEmpty(workPlanList)){
            return workPlanList;
        }
        return workPlanList;
    }

    @Override
    public List<TBusCargoInfoDTO> listTrustCargo(Long workPlanId) {
        return workTicketMapper.listTrustCargo(workPlanId);
    }

    @Override
    public List<TBusCargoInfoDTO> listTargetCargo(Long cargoInfoId) {
        return Collections.emptyList();
    }

    @Override
    public List<MPieceWorkTeamPO> listPieceWorkTeam(MPieceWorkTeamPO query) {
        return workTicketMapper.listPieceWorkTeam(query);
    }

    @Override
    public List<TPrdWorkPlanLocationPO> listWorkPlanLocation(Long workPlanId) {
        return workTicketMapper.listWorkPlanLocation(workPlanId);
    }

    @Override
    public List<TPrdDispatchSecondaryPO> listLabor(Long workPlanId) {
        return workTicketMapper.listLabor(workPlanId);
    }

    /**
     * 新增作业票 xin
     * 理货确认
     * @param workTicket
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void insertWorkTicket(TPrdWorkTicketDTO workTicket) {
        Long expire = redisTemplate.getExpire(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workTicket.getWorkPlanId());
        if(expire!=null && expire.compareTo(new Long(0))>0){
            throw new BusinessRuntimeException("正在处理，请在60秒后重试");
        }

        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workTicket.getWorkPlanId())
                .timeout(20)
                .build().run(() -> {
                    long lT = System.currentTimeMillis();

                    TPrdWorkPlanPO workPlan = workTicketMapper.getWorkPlan(workTicket.getWorkPlanId());

                    if(!workPlan.getPlanType().equals("4")){
                        int count = workTicketMapper.getExByWorkDate(workPlan.getWorkDate());
                        if(count>0){
                            String date = DateUtils.formatDate(workPlan.getWorkDate(),"yyyy-MM");
                            throw new BusinessRuntimeException(date + "已存在HR审核");
                        }
                    }
                    //辅助计划
                    if(workPlan.getPlanType().equals("4")){
                        int count = workTicketMapper.getFuZhuExByWorkDate(workPlan.getWorkDate());
                        if(count>0){
                            String date = DateUtils.formatDate(workPlan.getWorkDate(),"yyyy-MM");
                            throw new BusinessRuntimeException(date + "已存在HR审核");
                        }
                    }


                    //杂项计划必填项检验
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
//                    Integer cargoFlag = workTicketMapper.getCargoIsUpdate(v1.getCargoCode());
//                    if (cargoFlag != null && cargoFlag == 2) {
//                        //校验场地
//                        Map<String, Object> processFlag = workTicketMapper.getProcessIsUpdate(v1.getProcessDetailCode());
//                        if (processFlag != null && "1".equals(processFlag.get("updatePoint").toString())) {
//                            if ("201".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
//                                //出库
//                                if (v1.getStorehouseIdSource() == null) {
//                                    throw new BusinessRuntimeException("请填写作业过程为" + v1.getProcessDetailName() + "的起始区域信息");
//                                }
//                            }
//                            if ("101".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
//                                //入库
//                                if (v1.getStorehouseIdTarget() == null) {
//                                    throw new BusinessRuntimeException("请填写作业过程为" + v1.getProcessDetailName() + "的终点区域信息");
//                                }
//                            }
//                        }
//                    }
                        });
                        //其他计划的必填项的检验
                    }else {
                        workTicket.getDetails().forEach(o->{
                          if(StringUtils.isBlank(o.getDeptName())){
                              throw new BusinessRuntimeException("部门列填写不完全");
                          }
                          if(StringUtils.isBlank(o.getProcessDetailCode())){
                              throw new BusinessRuntimeException("作业过程列填写不完全");
                          }
                          if(o.getTrustCargoInfoId()==null){
                              throw new BusinessRuntimeException("票货信息列信息不完全");
                          }
                        });
                    }
                    //检验是否存在理货数据
                    List<TPrdWorkTicketDetailDTO> list = workTicketMapper.getTicketInfoWithTally(workTicket.getWorkPlanId());
                    if(list==null || list.isEmpty()){
                        throw new BusinessRuntimeException("没有理货");
                    }

                    if("1".equals(workPlan.getPlanType())||"3".equals(workPlan.getPlanType())){
                        SysParameterDTO WHOLE_IS_ADJUST = sysParameterMapper.getByKey("WHOLE_IS_ADJUST");
                        if("Y".equals(WHOLE_IS_ADJUST.getParamVal())){
                            if("20".equals(workPlan.getIsAdjust())){
                                throw new BusinessRuntimeException("已经进行机械整船调整，不能调整分配");
                            }else if("20".equals(workPlan.getIsAdjustLabor())){
                                throw new BusinessRuntimeException("已经进行人员整船调整，不能调整分配");
                            }
                        }
                    }


                    //校验是否存在未完成磅单数据
                    List<TPrdWorkTicketDetailDTO> tmpPoundRecordForCheck = workTicketMapper.getPoundRecordInfo(workTicket.getWorkPlanId());
                    if(!tmpPoundRecordForCheck.isEmpty()){
                        throw new BusinessRuntimeException(tmpPoundRecordForCheck.stream().map(TPrdWorkTicketDetailDTO::getTruckPlate).collect(Collectors.joining("<br/>"))+"<br/>尚未二次过磅，无法签票");
                    }
                    //校验同一部门是否在同一作业过程下的同一票货下是否重复签票
                    Map<String, List<TPrdWorkTicketDetailDTO>> tmpCheckMap = workTicket.getDetails().stream().collect(Collectors.groupingBy(o -> {
                        return o.getCargoInfoId().toString()+"_"+o.getProcessDetailCode()+"_"+ o.getDeptId().toString()+"_"+o.getEquipmentTypeCode();
                    }));
                    tmpCheckMap.forEach((k,v)->{
                        if(v.size()>1){
                            throw new BusinessRuntimeException(v.get(0).getCargoInfoName()+"_"+v.get(0).getProcessName()+"_"+v.get(0).getDeptName()+v.get(0).getEquipmentTypeCode()+"重复签票");
                        }
                    });

                    TPrdWorkTicketDTO tempWorkTicket = workTicketMapper.getWorkTicket(workTicket.getWorkPlanId(), null, "2");
                    if (tempWorkTicket != null) {
                        throw new BusinessRuntimeException("当前作业计划已签票，请先撤销签票。");
                    }
                    tempWorkTicket = workTicketMapper.getWorkTicketInfoList(workTicket.getWorkPlanId(), workTicket.getTicketType(),null);
                    if (tempWorkTicket!=null) {
                        throw new BusinessRuntimeException("当前作业计划已签票,请先撤销签票。");
                    }

                    List<TPrdWorkTicketDetailDTO> workTicketDetails = workTicket.getDetails();
                    //根据计划查询航次id
                    Map<String, Object> trustShipvoyage = new HashMap<>();
                    if ("1".equals(workPlan.getPlanType())) {
                        trustShipvoyage = workTicketMapper.getShipvoyage(workTicket.getWorkPlanId());
                    } else {
                        trustShipvoyage = workTicketMapper.getTrustShipvoyage(workPlan.getTrustId());
                    }
                    Map<String, Object> finalTrustShipvoyage = trustShipvoyage;
                    workTicketDetails.forEach(v1 -> {
                        if (finalTrustShipvoyage != null) {
                            v1.setShipvoyageId(finalTrustShipvoyage.get("id").toString());
                            v1.setShipvoyageItemId(finalTrustShipvoyage.get("itemId").toString());
                        }
                    });

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

                    //返回作业票机械子表数据，给作业票子表赋值
                    List<TPrdWorkTicketEquipmentPO> equipments = workTicket.getDetails().stream().flatMap(v1 -> {
                        v1.setId(snowflake.nextId());
                        v1.setWorkTicketId(workTicket.getId());
                        v1.setProcessCode(workTicket.getProcessCode());
                        v1.setProcessName(workTicket.getProcessName());
                        v1.setWorkDate(workTicket.getWorkDate());
                        v1.setClassCode(workTicket.getClassCode());
                        v1.setClassName(workTicket.getClassName());

                        return Optional.ofNullable(v1.getEquipments()).orElse(Collections.emptyList()).stream().filter(v3->v3.getEquipmentId()!=null).peek(v2 -> {
                            v2.setId(snowflake.nextId());
                            v2.setWorkTicketDetailId(v1.getId());
                        });
                    }).collect(Collectors.toList());

                    Optional.ofNullable(workTicket.getLabors()).orElse(Collections.emptyList()).forEach(v1 -> {
                        v1.setId(snowflake.nextId());
                        v1.setWorkTicketId(workTicket.getId());
                    });
                    //区分新老签票 0 新 1 旧
                    workTicket.setIsOld("0");
                    workTicket.setTon(Optional.of(workTicket.getDetails().stream().filter(o->!o.getDeptName().equals("固机队")).map(o->Optional.of(o.getTon()).orElseThrow(()->new BusinessRuntimeException("没有汇总出件数"))).reduce(BigDecimal.ZERO,BigDecimal::add))
                            .orElse(BigDecimal.ZERO));
                    workTicketMapper.insertWorkTicket(workTicket);
                    //默认签票类型是签票 1签票，2机械，3人工
                    workTicket.getDetails().forEach(o->{
                        o.setAllotType("1");
                    });

                    workTicketMapper.insertWorkTicketDetail(workTicket.getDetails());
                    if (CollectionUtils.isNotEmpty(equipments)) {
                        workTicketMapper.insertWorkTicketEquipment(equipments);
                    }
                    if (CollectionUtils.isNotEmpty(workTicket.getLabors())) {
                        workTicketMapper.insertWorkTicketLabor(workTicket.getLabors());
                    }

                    //分配作业量
//                    String planType = workTicketMapper.getWorkPlanType(workTicket.getWorkPlanId());
//                    TPrdWorkPlanPO workPlanDto = workTicketMapper.getWorkPlan(workTicket.getWorkPlanId());

                    /**
                     * 船-车-场，场-车-船计件同时分配给调度和库场
                     */
                    List<TPrdWorkTicketDetailDTO> details = new ArrayList<>();
                    List<Map<String,Object>> deptMap = workTicketMapper.getDept(Arrays.asList("调度室","库场队"));
                    if (deptMap.size()!=2) {
                        throw new BusinessRuntimeException("部门信息获取异常");
                    }
                    Map<String, Long> realDeptMap = deptMap.stream().collect(Collectors.toMap(o -> String.valueOf(o.get("deptName")), i -> new Long(String.valueOf(i.get("deptId"))), (k1, k2) -> k1));
                   //场-车-船、船-车-场
                    Map<Long, List<TPrdWorkTicketDetailDTO>> collect = workTicket.getDetails().stream().collect(Collectors.groupingBy(o -> o.getCargoInfoId()));
                    for (Long l : collect.keySet()) {
                        List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = collect.get(l);
                        if(!workTicketDetailDTOS.isEmpty()){
                            workTicketDetailDTOS.forEach(o->{
                                if(AREA_CAR_SHIP.equals(o.getProcessDetailCode())||SHIP_CAR_AREA.equals(o.getProcessDetailCode())){
                                    if("调度室".equals(o.getDeptName())||"库场队".equals(o.getDeptName())){
                                        TPrdWorkTicketDetailDTO clone = SerializationUtils.clone(o);
                                        if("调度室".equals(clone.getDeptName())){
                                            clone.setDeptId(realDeptMap.get("库场队"));
                                            clone.setDeptName("库场队");
                                        }else if("库场队".equals(clone.getDeptName())){
                                            clone.setDeptId(realDeptMap.get("调度室"));
                                            clone.setDeptName("调度室");
                                        }
                                        details.add(clone);
                                    }
                                }
                            });
                        }
                    }
                    if(!details.isEmpty()){
                        workTicket.getDetails().addAll(details);
                    }

                    long l1 = System.currentTimeMillis();
                    System.out.println("新增插入作业票"+(l1-lT));
                    SpringUtils.getBean(this.getClass()).commonUpdateSalary(workTicket,workTicket.getDetails(),true,"2".equals(workPlan.getPlanType() ),"新增作业票");
                    System.out.println("新增作业票 插入计件总用时"+(System.currentTimeMillis()-l1));
                    //船舶\倒运计划自动生成分配数据
                    if("1".equals(workPlan.getPlanType())||"3".equals(workPlan.getPlanType())){
                        SpringUtils.getBean(this.getClass()).autoSecondALlotTicket(workTicket,workPlan);
                    }
                    System.out.println("不分计件 "+(System.currentTimeMillis()-l1));

                });
    }
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void lgInsertWorkTicket(TPrdWorkTicketDTO workTicket) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workTicket.getWorkPlanId())
                .build().run(() -> {
                    TPrdWorkPlanPO workPlan = workTicketMapper.getWorkPlan(workTicket.getWorkPlanId());
                    //杂项计划必填项检验
                    if (!DispatchEnum.WorkPlanTypeEnum.SUNDRY.getCode().equals(workPlan.getPlanType())) {
                        workTicket.getDetails().forEach(v1 -> {
                            if (StringUtils.isEmpty(v1.getCabinNo()) && ("下舱".equals(v1.getProcessDetailName()))) {
                                throw new BusinessRuntimeException("下舱作业过程，请选择舱口");
                            }
                            if (v1.getTon() == null || v1.getTon().compareTo(BigDecimal.ZERO) < 0) {
                                throw new BusinessRuntimeException("吨数（数量）不能为空,不能小于0");
                            }
                            if(StringUtils.isBlank(v1.getProcessDetailCode())){
                                throw new BusinessRuntimeException("作业过程列填写不完全");
                            }
                            if(StringUtils.isBlank(v1.getDeptName())){
                                throw new BusinessRuntimeException("部门列填写不完全");
                            }
                        });
                    }
                    TPrdWorkTicketDTO tempWorkTicket = workTicketMapper.getWorkTicket(workTicket.getWorkPlanId(), null, "2");
                    if (tempWorkTicket != null) {
                        throw new BusinessRuntimeException("当前作业计划已签票（旧票）");
                    }
                    tempWorkTicket = workTicketMapper.getWorkTicketInfoList(workTicket.getWorkPlanId(), workTicket.getTicketType(),null);
                    if (tempWorkTicket!=null) {
                        throw new BusinessRuntimeException("当前作业计划已签票（新票）");
                    }
                    List<TPrdWorkTicketDetailDTO> workTicketDetails = workTicket.getDetails();
                    //根据计划查询航次id
                    Map<String, Object> trustShipvoyage = new HashMap<>();
                    trustShipvoyage = workTicketMapper.getShipvoyage(workTicket.getWorkPlanId());
                    Map<String, Object> finalTrustShipvoyage = trustShipvoyage;
                    workTicketDetails.forEach(v1 -> {
                        if (finalTrustShipvoyage != null) {
                            v1.setShipvoyageId(String.valueOf(Optional.ofNullable(finalTrustShipvoyage.get("id")).orElse("")));
                            v1.setShipvoyageItemId(String.valueOf(Optional.ofNullable(finalTrustShipvoyage.get("itemId")).orElse("")));
                        }
                    });

                    workTicket.setId(snowflake.nextId());
                    workTicket.setCompanyId(workPlan.getCompanyId());
                    workTicket.setCompanyName(workPlan.getCompanyName());
                    workTicket.setType(workPlan.getPlanType());
                    workTicket.setTicketType(workTicket.getTicketType());
                    workTicket.setProcessCode(workPlan.getProcessCode());
                    workTicket.setProcessName(workPlan.getProcessName());
                    workTicket.setWorkDate(workPlan.getWorkDate());
                    workTicket.setClassCode(workPlan.getClassCode());
                    workTicket.setClassName(ObjectUtil.isNotEmpty(workPlan.getClassName())?workPlan.getClassName():"01".equals(workPlan.getClassCode())?"白班":"夜班");
                    workTicket.setWorkTicketStatus(WorkTicketStatusEnum._10.getCode());
                    workTicket.setWorkTicketStatusName(WorkTicketStatusEnum._10.getName());

                    //返回作业票机械子表数据，给作业票子表赋值
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
                    //区分新老签票 0 新 1 旧
                    workTicket.setIsOld("0");
                    workTicket.setTon(Optional.of(workTicket.getDetails().stream().filter(o->!o.getDeptName().equals("固机队")).map(o->Optional.of(o.getTon()).orElseThrow(()->new BusinessRuntimeException("没有汇总出件数"))).reduce(BigDecimal.ZERO,BigDecimal::add))
                            .orElse(BigDecimal.ZERO));
                    workTicketMapper.insertWorkTicket(workTicket);
                    //默认签票类型是签票 1签票，2机械，3人工
                    workTicket.getDetails().forEach(o->o.setAllotType("1"));

                    workTicketMapper.insertWorkTicketDetail(workTicket.getDetails());
                    if (CollectionUtils.isNotEmpty(equipments)) {
                        workTicketMapper.insertWorkTicketEquipment(equipments);
                    }
                    if (CollectionUtils.isNotEmpty(workTicket.getLabors())) {
                        workTicketMapper.insertWorkTicketLabor(workTicket.getLabors());
                    }
                      //辅助计划不分配计件
                    String planType = workTicketMapper.getWorkPlanType(workTicket.getWorkPlanId());
                    SpringUtils.getBean(this.getClass()).commonUpdateSalary(workTicket,workTicket.getDetails(),true,"2".equals(planType ),"新增作业票");
                });
    }

    @Override
    public TPrdWorkTicketDTO getWorkTicket(Long workPlanId, String ticketType) {
        TPrdWorkTicketDTO workTicket = workTicketMapper.getWorkTicket(workPlanId, ticketType, "2");
        if (workTicket == null) {
            return null;
        }
        List<TPrdWorkTicketLaborPO> labors = workTicketMapper.listWorkTicketLabor(workTicket.getId());
        List<TPrdWorkTicketDetailDTO> workTicketDetails = workTicketMapper.listWorkTicketDetail(workTicket.getId());
        List<Long> workTicketDetailIds = workTicketDetails.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList());
        List<TPrdWorkTicketEquipmentPO> equipments = workTicketMapper.listWorkTicketEquipment(workTicketDetailIds);
        workTicket.setDetails(workTicketDetails);
        workTicketDetails.forEach(v1 -> {
            List<TPrdWorkTicketEquipmentPO> currentEquipments = equipments.stream()
                    .filter(v2 -> v1.getId().equals(v2.getWorkTicketDetailId())).collect(Collectors.toList());
            v1.setEquipments(currentEquipments);
        });
        workTicket.setDetails(workTicketDetails);

        workTicket.setLabors(labors);
        return workTicket;
    }

    //更新签票
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateWorkTicket(TPrdWorkTicketDTO workTicket) {
//        List<TPrdWorkTicketDTO> workTicketDTOS = workTicketMapper.getTicket(workTicket.getWorkPlanId(),workTicket.getTicketType());
//        if(CollectionUtils.isNotEmpty(workTicketDTOS)){
//            for (TPrdWorkTicketDTO workTicketDTO : workTicketDTOS) {
//                if("20".equals(workTicketDTO.getWorkTicketStatus())){
//                    throw new BusinessRuntimeException("请先取消签票审核再修改");
//                }
//            }
//        }
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workTicket.getWorkPlanId())
                .build().run(() -> {
                    TPrdWorkPlanPO workPlan = workTicketMapper.getWorkPlan(workTicket.getWorkPlanId());
                    //杂项计划必填项检验
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
//                    Integer cargoFlag = workTicketMapper.getCargoIsUpdate(v1.getCargoCode());
//                    if (cargoFlag != null && cargoFlag == 2) {
//                        //校验场地
//                        Map<String, Object> processFlag = workTicketMapper.getProcessIsUpdate(v1.getProcessDetailCode());
//                        if (processFlag != null && "1".equals(processFlag.get("updatePoint").toString())) {
//                            if ("201".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
//                                //出库
//                                if (v1.getStorehouseIdSource() == null) {
//                                    throw new BusinessRuntimeException("请填写作业过程为" + v1.getProcessDetailName() + "的起始区域信息");
//                                }
//                            }
//                            if ("101".equals(processFlag.get("inoutType").toString()) || "301".equals(processFlag.get("inoutType").toString())) {
//                                //入库
//                                if (v1.getStorehouseIdTarget() == null) {
//                                    throw new BusinessRuntimeException("请填写作业过程为" + v1.getProcessDetailName() + "的终点区域信息");
//                                }
//                            }
//                        }
//                    }
                        });
                    }
                    //以上校验结束
                    TPrdWorkTicketDTO tempWorkTicket = workTicketMapper.getWorkTicketInfoList(workTicket.getWorkPlanId(), workTicket.getTicketType(), null);
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
                        Long storageYardId = workTicketMapper.getStorageYard(v1.getLeft(), v1.getRight());
                        if (storageYardId == null) {
                            StorageYardPO storageYard = new StorageYardPO();
                            storageYard.setId(snowflake.nextId());
                            storageYard.setParentId(v1.getLeft());
                            storageYard.setStorageYardNm(v1.getRight());
                            storageYard.setShortCd(PinYin4jUtils.convertCnzhToPinYinVal(storageYard.getStorageYardNm()));
                            storageYard.setStorageYardLevel(STORAGE_YARD_LEVEL_MASS);
                            workTicketMapper.insertStorageYard(storageYard);
                            storageYardId = storageYard.getId();
                        }
                        map.put(v1, storageYardId);
                    });
                    //根据计划查询航次id
                    Map<String, Object> trustShipvoyage = new HashMap<>();
                    if ("1".equals(workPlan.getPlanType())) {
                        trustShipvoyage = workTicketMapper.getShipvoyage(workTicket.getWorkPlanId());
                    } else {
                        trustShipvoyage = workTicketMapper.getTrustShipvoyage(workPlan.getTrustId());
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



                    workTicketDetails = workTicketMapper.listWorkTicketDetail(tempWorkTicket.getId());
                    workTicketMapper.deleteWorkTicketDetail(tempWorkTicket.getId());
                    List<Long> workTicketDetailIds = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getId).collect(Collectors.toList());
                    workTicketMapper.deleteWorkTicketEquipment(workTicketDetailIds);
                    workTicketMapper.deleteWorkTicketLabor(tempWorkTicket.getId());

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

                    workTicketMapper.updateWorkTicket(tempWorkTicket);
                    workTicketMapper.insertWorkTicketDetail(workTicket.getDetails());
                    if (CollectionUtils.isNotEmpty(equipments)) {
                        workTicketMapper.insertWorkTicketEquipment(equipments);
                    }
                    if (CollectionUtils.isNotEmpty(workTicket.getLabors())) {
                        workTicketMapper.insertWorkTicketLabor(workTicket.getLabors());
                    }

                    String planType = workTicketMapper.getWorkPlanType(workTicket.getWorkPlanId());
                    SpringUtils.getBean(this.getClass()).commonUpdateSalary(workTicket,workTicket.getDetails(),true,"2".equals(planType ),"更新作业票");

                });
    }

    /**
     *
     * 撤销签票
     * @param workPlanId
     * @param type
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteWorkTicket(Long workPlanId, String type) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workPlanId)
                .build().run(() -> {
                    TPrdWorkTicketDTO workTicket = workTicketMapper.getWorkTicketInfoList(workPlanId, type, null);
                    if(workTicket==null){
                        throw new BusinessRuntimeException("尚未签票无需撤销");
                    }
                    List<TPrdWorkTicketDetailDTO> ticketDetils= workTicketMapper.getTicketDetils(workTicket.getId());

                    if (workTicket == null) {
                        throw new BusinessRuntimeException("没有查询到签票信息无需撤销签票");
                    }


                    TPrdWorkPlanPO workPlan = workTicketMapper.getWorkPlan(workPlanId);

                    if(!workPlan.getPlanType().equals("4")){
                        int count = workTicketMapper.getExByWorkDate(workPlan.getWorkDate());
                        if(count>0){
                            String date = DateUtils.formatDate(workPlan.getWorkDate(),"yyyy-MM");
                            throw new BusinessRuntimeException(date + "已存在HR审核");
                        }
                    }
                    //辅助计划
                    if(workPlan.getPlanType().equals("4")){
                        int count = workTicketMapper.getFuZhuExByWorkDate(workPlan.getWorkDate());
                        if(count>0){
                            String date = DateUtils.formatDate(workPlan.getWorkDate(),"yyyy-MM");
                            throw new BusinessRuntimeException(date + "已存在HR审核");
                        }
                    }

                    if("1".equals(workPlan.getPlanType())||"3".equals(workPlan.getPlanType())){
                        SysParameterDTO WHOLE_IS_ADJUST = sysParameterMapper.getByKey("WHOLE_IS_ADJUST");
                        if("Y".equals(WHOLE_IS_ADJUST.getParamVal())){
                            if("20".equals(workPlan.getIsAdjust())){
                                throw new BusinessRuntimeException("已经进行机械整船调整，不能调整分配");
                            }else if("20".equals(workPlan.getIsAdjustLabor())){
                                throw new BusinessRuntimeException("已经进行人员整船调整，不能调整分配");
                            }
                        }
                    }


                    //判断签票人是否符合操作按钮的部门
                    /*if("1".equals(securityUtils.getUserInfo().getIsSuperadmin())){
                        //判断是否是调度撤销调度的，库场撤销库场的
                    }else {
                        Map<String, Object> deptLevel2InfoByUserId = workTicketMapper.getDeptLevel2InfoByUserId(securityUtils.getLoginUserId());
                        if("1".equals(type)){
                            if(!"调度室".equals(String.valueOf(deptLevel2InfoByUserId.get("deptName")))){
                                throw new BusinessRuntimeException("当前登录人属于"+deptLevel2InfoByUserId.get("deptName"));
                            }
                        }else if("2".equals(type)){
                            if(!"库场队".equals(String.valueOf(deptLevel2InfoByUserId.get("deptName")))){
                                throw new BusinessRuntimeException("当前登录人属于"+deptLevel2InfoByUserId.get("deptName"));
                            }
                        }
                    }*/

                    //获取全部的签票信息 判断是否已经进行了二次分配
                    List<TPrdWorkTicketDetailDTO> workTicketDetails = workTicketMapper.listWorkTicketDetail(workTicket.getId());


                    if("1".equals(workPlan.getPlanType())||"3".equals(workPlan.getPlanType())){

                    }else{
                        //不是船舶、倒运的判断是否进行了二次分配
                        if(!"4".equals(workTicket.getTicketType())){
                            workTicketDetails.forEach(
                                    o->{
                                        if(!"1".equals(o.getAllotType())){
                                            if("2".equals(o.getAllotType())){
                                                throw new BusinessRuntimeException("签票已进行机械分配");
                                            }
                                            if("3".equals(o.getAllotType())){
                                                throw new BusinessRuntimeException("签票已进行人员分配");
                                            }
                                        }
                                    }
                            );
                        }
                    }

                    //删除作业票
                    workTicketMapper.deleteWorkTicket(workPlanId, type);
                    workTicketMapper.deleteWorkTicketDetail(workTicket.getId());
                    //删除机械
                    List<Long> workTicketDetailIds = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getId).collect(Collectors.toList());
                    workTicketMapper.deleteWorkTicketEquipment(workTicketDetailIds);
                    //workTicketMapper.deleteWorkTicketLabor(workTicket.getId());
                    //判断计件
                    List<TPrdSalaryPO> TPrdSalaryPO = workTicketMapper.getSalary(workTicket.getId());
                    if (!TPrdSalaryPO.isEmpty()) {
                        List<TPrdSalaryPO> collect = TPrdSalaryPO.stream().filter(o -> !"10".equals(o.getSalaryStatusCode())).collect(Collectors.toList());
                        if (!collect.isEmpty()) {
                            throw new BusinessRuntimeException("计件已审核，请先撤销计件");
                        }

                    }
                    //根据作业票子表删除计件
                    if(!ticketDetils.isEmpty()){
                        List<TPrdSalaryPO> detailSalarys = workTicketMapper.getSalaryByTicketDetial(ticketDetils.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList()));
                        List<com.yy.ppm.produce.bean.po.TPrdSalaryPO> collect = detailSalarys.stream().filter(o -> !"10".equals(o.getSalaryStatusCode())).collect(Collectors.toList());
                        if (!collect.isEmpty()) {
                            throw new BusinessRuntimeException("计件已审核，请先撤销计件。");
                        }
                        //删除计件
                        workTicketMapper.deleteSalary(ticketDetils.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList()));

                    }
                    //再次根据作业票主表删除计件
                    if(!"4".equals(workTicket.getTicketType())){
                        workTicketMapper.deleteSalaryZ(workTicket.getId());
                    }
                });
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void commonUpdateSalary(TPrdWorkTicketDTO workTicket, List<TPrdWorkTicketDetailDTO> workTicketDetails,
                                   Boolean delFlag, Boolean isJsg, String message) {
        long l = System.currentTimeMillis();

        if (workTicket == null) {
            throw new BusinessRuntimeException("当前作业计划未签票");
        }

        final String methodName = "TPrdWorkTicketServiceImpl:commonUpdateSalary";
        LOGGER.enter(methodName, "业务执行" + message + "workTicket: " + workTicket + ", workTicketDetails: " + workTicketDetails + ", delFlag: " + delFlag + ", isJsg: " + isJsg);
        if (delFlag) {
            //传入子表信息的id
            List<Long> workTicketDetailIds = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getId).collect(Collectors.toList());
            //根据作业票子表id 删除计件明细
            workTicketMapper.deleteSalary(workTicketDetailIds);
            if(!Stream.of("2","3").anyMatch(o->o.equals(workTicketDetails.get(0).getAllotType()))){
                //根据作业票主表id 删除计件明细
                workTicketMapper.deleteSalaryZ(workTicket.getId());
            }
        }
        Map<String, Object> cargoSalaryMap = new HashMap<>();
        //判断货物是否维护计件工资类型
        if (workTicketDetails.get(0).getCargoCode() != null) {
            cargoSalaryMap = workTicketMapper.getCargoSalaryType(workTicketDetails.get(0).getCargoCode());
            if (cargoSalaryMap == null) {
                throw new BusinessRuntimeException("未查询到" + workTicketDetails.get(0).getCargoName() + "的计件类型详情,请先联系人事部门进行维护");
            }
        }
        if (!CollectionUtils.isEmpty(workTicketDetails)) {
            for (TPrdWorkTicketDetailDTO dto : workTicketDetails) {
                if (dto.getCargoCode() == null) {
                    throw new BusinessRuntimeException("获取签票货物信息失败，请检查签票信息是否正确");
                }
                String name = "";
                if (dto.getShipvoyageItemId() != null) {
                    //根据航次id 查询船名航次
                    name = workTicketMapper.getShipVoyageName(Arrays.asList(dto.getShipvoyageItemId().split(",")));
                }
                //获取作业过程信息 根据作业过程中的是否作业理货理货量判断是否进行分配计件
                MWorkProcessDTO processDTO = workTicketMapper.getProcessInfo(dto.getProcessCode());
                if(processDTO == null){
                    throw new BusinessRuntimeException(dto.getId()+dto.getProcessCode()+"获取作业过程信息失败");
                }
                //不是作业理货量分配计件
                if(StringUtils.isNotBlank(processDTO.getIsTallyTon())&&!"1".equals(processDTO.getIsTallyTon())){
                    continue;
                }
                    //计件分配
                //操作班组计件工资分配
                if (dto.getDeptId() != null) {
                    //是否内部班组
                    String internal = workTicketMapper.getDeptInternal(dto.getDeptId());
                    if ("I".equals(internal)) {
                        //内部班组 操作工班
                        //查询该部门 下是否有班组 点名
                        List<Long> ids = workTicketMapper.getDeptRollCallItem(workTicket.getWorkDate(),
                                workTicket.getClassCode(),
                                dto.getDeptId());
                        if (ids.size() == 0) {
                            throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组未出勤点名");
                        }
                        if (ids.size() > 1) {
                            throw new BusinessRuntimeException(dto.getDeptName() + "部门下班组出勤点名重复");
                        }
                        if (dto.getTon() != null && dto.getTon().compareTo(BigDecimal.ZERO) != 0) {
                            List<TPrdAttendanceUserPO> attendanceUsers = workTicketMapper.listAttendanceUser(ids);
                            Map<String, Object> deptMap = workTicketMapper.getDeptDm(ids);
                            List<TPrdSalaryPO> salaries = new ArrayList<>();
                            //调用计件分配工具类
                            BigDecimal each = getAllocationMethod(attendanceUsers, dto.getTon());
                            long l2 = System.currentTimeMillis();
                            for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUsers) {
                                TPrdSalaryPO salary = new TPrdSalaryPO();
                                salary.setId(snowflake.nextId());
                                salary.setWorkTicketDetailId(dto.getId());
                                salary.setWorkTicketId(workTicket.getId());
                                salary.setCompanyId(workTicket.getCompanyId());
                                salary.setCompanyName(workTicket.getCompanyName());
                                salary.setWorkDate(workTicket.getWorkDate());
                                salary.setClassCode(workTicket.getClassCode());
                                salary.setClassName(workTicket.getClassName());
                                salary.setPieceProjectCode("1");
                                salary.setPieceProjectName("1");
                                salary.setProcessDetailCode(dto.getProcessDetailCode());
                                salary.setProcessDetailName(dto.getProcessDetailName());
                                salary.setRemark(message);
                                if (deptMap != null) {
                                    salary.setDeptId(Long.parseLong(deptMap.get("deptId").toString()));
                                    salary.setDeptName(deptMap.get("deptName").toString());
                                } else {
                                    throw new BusinessRuntimeException("获取点名班组信息失败");
                                }
                                salary.setUserBy(tPrdAttendanceUserPO.getUserId());
                                salary.setUserByName(tPrdAttendanceUserPO.getUserName());
                                salary.setCoefficient(tPrdAttendanceUserPO.getCoefficient());
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
                                if ("1677243625832058880".equals(dto.getDeptId().toString())) {
                                    //库场
                                    salary.setSalaryTypeCode(cargoSalaryMap.get("kcTypeCode").toString());
                                    salary.setSalaryTypeName(cargoSalaryMap.get("kcTypeName").toString());
                                } else if ("1677243491865989120".equals(dto.getDeptId().toString())) {
                                    //调度
                                    salary.setSalaryTypeCode(cargoSalaryMap.get("ddTypeCode").toString());
                                    salary.setSalaryTypeName(cargoSalaryMap.get("ddTypeName").toString());
                                }
                                if (dto.getShipvoyageItemId() != null) {
                                    salary.setShipVoyageItemId(dto.getShipvoyageItemId());
                                    if (name != null) {
                                        salary.setShipVoyage(name);
                                    }
                                }
                                salary.setCargoCode(
                                        Stream.of("2","3").anyMatch(o->o.equals(workTicketDetails.get(0).getAllotType()))?
                                        null:dto.getCargoCode());
                                salary.setCargoName(Stream.of("2","3").anyMatch(o->o.equals(workTicketDetails.get(0).getAllotType()))?
                                        null:dto.getCargoName());

                                salaries.add(salary);
                            }
                            long l4 = System.currentTimeMillis();
                            System.out.println("组装计件"+(l4-l2));
                            if (!salaries.isEmpty()) {
                                //调度只分配源或者目的中是船的
                                if ("1677243491865989120".equals(dto.getDeptId().toString())){
                                    if(PROCESS_CD_SHIP.equals(processDTO.getSourceCd())||PROCESS_CD_SHIP.equals(processDTO.getTargetCd())){
                                        workTicketMapper.insertSalary(salaries);
                                    }
                                }else if ("1710197535592812544".equals(dto.getDeptId().toString())) {
                                    //固机判断一下机械类型 只分配门机
                                    String code = "";
                                    try {
                                        code = workTicketMapper.getEqTypeCode(dto.getId());
                                    }catch (Exception e){
                                        List<Map<String,Object>> tmpCheck = workTicketMapper.getEqTypeCodeResultMap(dto.getId());
                                        List<Map<String,Object>> result = workTicketMapper.getinfo(dto.getId());
                                        throw new BusinessRuntimeException("获取机械类型错误"+result);
                                    }
                                    if ("0007".equals(code)) {
                                        workTicketMapper.insertSalary(salaries);
                                    }
                                } else {
                                    workTicketMapper.insertSalary(salaries);
                                }
                            }

                            long l3 = System.currentTimeMillis();
                            System.out.println("插入计件"+(l3-l4));

                        }
                    }
                }
            }
            long l1 = System.currentTimeMillis();
            System.out.println(message+(l1-l));
        } else {
            throw new BusinessRuntimeException("获取签票明细信息失败，请检查签票信息是否正确");
        }
        LOGGER.exit(methodName);
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



    /**
     * 销审作业票
     * @param ticketPlanIdDTO
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelReviewWorkTicket(TicketPlanIdDTO ticketPlanIdDTO) {
        List<Map<String, Object>> workPlanIdList = ticketPlanIdDTO.getWorkPlanIdList();
        if (workPlanIdList != null && workPlanIdList.size() != 0) {
            for (Map<String, Object> map : workPlanIdList) {
                Long workPlanId = Long.parseLong(map.get("workPlanId").toString());
                String ticketType = map.get("ticketType").toString();
                TPrdWorkTicketDTO workTicket = workTicketMapper.getWorkTicket(workPlanId, ticketType, "2");
                DistributedLock.newBuilder().store(redisTemplate)
                        .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workPlanId)
                        .build().run(() -> {
//                            String trustNo = workTicketMapper.getTrustIdWicketJsg(workPlanId);
//                            ;
//                            if (workTicket == null) {
////                        System.out.println(workPlanId);
//                                throw new BusinessRuntimeException("通知单号为" + trustNo + "的作业计划未签票");
//                            }
//                            Integer count = workTicketMapper.getSalaryEx(workTicket.getId());
//                            if (count > 0) {
//                                throw new BusinessRuntimeException("通知单号为" + trustNo + "的作业计划计件已审核,请先取消计件审核");
//                            }
//                            TPrdWorkTicketPO tempWorkTicket = new TPrdWorkTicketPO();
//                            tempWorkTicket.setWorkPlanId(workPlanId);
//                            tempWorkTicket.setWorkTicketStatus(WorkTicketStatusEnum._10.getCode());
//                            tempWorkTicket.setWorkTicketStatusName(WorkTicketStatusEnum._10.getName());
//                            tempWorkTicket.setTicketType(ticketType);
//                            tempWorkTicket.setExamineByUp(workTicket.getExamineBy());
//                            tempWorkTicket.setExamineByNameUp(workTicket.getExamineByName());
//                            tempWorkTicket.setExamineTimeUp(workTicket.getExamineTime());
//                            workTicketMapper.cancelReviewWorkTicket(tempWorkTicket);
//                            //更新港存
//                            List<TPrdPortStorageDetailPO> portStorageDetails = workTicketMapper.listPortStorageDetail(workTicket.getId());
//                            if (!portStorageDetails.isEmpty()) {
//                                List<Long> portStorageDetailIds = portStorageDetails.stream().map(TPrdPortStorageDetailPO::getId).collect(Collectors.toList());
//                                try {
//                                    businessCommonService.deletePortStorageDetail(portStorageDetailIds);
//                                } catch (BusinessRuntimeException e) {
//                                    throw new BusinessRuntimeException("相关港存已清场，无法销审");
//                                }
//                            }
//
//                            List<TPrdWorkTicketDetailDTO> workTicketDetails = workTicketMapper.listWorkTicketDetail(workTicket.getId());
//                            List<Long> workTicketDetailIds = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getId).collect(Collectors.toList());
//                            workTicketMapper.deleteSalary(workTicketDetailIds);
//                            workTicketMapper.deleteSalaryZ(workTicket.getId());
//                            List<TPrdWorkPlanDTO> tPrdWorkPlanDTO = workTicketMapper.listWorkPlanById(workPlanId);
//                            String cargoWorkType = workTicketMapper.getCargoWorkType(workTicketDetails.get(0).getCargoCode());
//                            if ("2".equals(tPrdWorkPlanDTO.get(0).getPlanType()) && "2".equals(cargoWorkType)) {
//                                //集疏港计划需要删除签票信息
//                                List<TPrdWorkTicketDetailDTO> workTicketDetailsJsg = workTicketMapper.listWorkTicketDetail(workTicket.getId());
//                                if (!"1039".equals(tPrdWorkPlanDTO.get(0).getProcessCode())) {
//                                    workTicketMapper.deleteWorkTicket(workPlanId, "2");
//                                    workTicketMapper.deleteWorkTicketDetail(workTicket.getId());
//                                    List<Long> workTicketDetailIdsJsg = workTicketDetails.stream().map(TPrdWorkTicketDetailPO::getId).collect(Collectors.toList());
//                                    workTicketMapper.deleteWorkTicketEquipment(workTicketDetailIdsJsg);
//                                }
//                            }
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
            List<TPrdWorkPlanDTO> tPrdWorkPlanDTOS = workTicketMapper.listWorkPlanJsg(query);
            if (!CollectionUtils.isEmpty(tPrdWorkPlanDTOS)) {
                Iterator<TPrdWorkPlanDTO> iter = tPrdWorkPlanDTOS.iterator();
                while (iter.hasNext()) {
                    TPrdWorkPlanDTO po = iter.next();
                    if (po.getCargoOwnerId() != null) {
                        List<String> idLists = Arrays.asList(po.getCargoOwnerId().split(","));
                        Integer count = workTicketMapper.getIsStations(idLists);
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
                List<TPrdWorkTiTckInfoDTO> dtoList = workTicketMapper.getIsWorkTickets(query);
                //获取当前计划的所有的指令ID
                List<Long> trustIds = workTicketMapper.getTrustIDs(ids);
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
                List<TPrdWorkTicketDetailDTO> ticketInfos = workTicketMapper.getTickets(ids, "2");
                //当前班次所有计划装卸配工详情
                List<TPrdWorkTicketDetailDTO> poLwList = workTicketMapper.getTicketInfoLwList(ids);
                //当前班次所有计划指令票货详情
                List<TBusCargoInfoDTO> tBusCargoInfoList = workTicketMapper.listTrustCargos(ids);
                //当前班次所有计划件货理货量详情
                //List<TYardTallyItemPO> tYardTallyItemPOList = workTicketMapper.getTally(ids);
                //当前班次所有计划场地详情
                List<Map<String, Object>> locationStartList = workTicketMapper.getLocation(ids, "2");
                List<Map<String, Object>> locationEndList = workTicketMapper.getLocation(ids, "1");
                List<Map<String, Object>> locationtList = workTicketMapper.getTallyLocation(ids);
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

                            // List<TPrdWorkTicketDetailDTO> ticketInfo = workTicketMapper.getTicketInfo(pos.getId(), "2");
                            List<TPrdWorkTicketDetailDTO> ticketInfo = ticketInfos.stream()
                                    .filter(obj -> obj.getWorkPlanId().equals(pos.getId()))
                                    .collect(Collectors.toList());
                            List<TBusCargoInfoDTO> tBusCargoInfoDTOS = tBusCargoInfoList.stream()
                                    .filter(obj -> obj.getWorkPlanId().equals(pos.getId()))
                                    .collect(Collectors.toList());
                            if (CollectionUtils.isEmpty(ticketInfo) && "1039".equals(pos.getProcessCode())) {
                                List<Long> idList = new ArrayList<>();
                                idList.add(pos.getId());
                                ticketInfo = workTicketMapper.getTickets(idList, "1");
                            }
                            if (ticketInfo != null && ticketInfo.size() != 0) {
                                for (int i = 0; i < ticketInfo.size(); i++) {
                                    //根据作业过程查询装卸队派工
                                    TPrdWorkTicketDetailDTO po = ticketInfo.get(i);
                                    List<TPrdWorkTicketDetailDTO> poLw = poLwList.stream()
                                            .filter(obj -> obj.getWorkPlanId().equals(pos.getId()) && obj.getProcessDetailCode().equals(po.getProcessDetailCode()))
                                            .collect(Collectors.toList());
//                            List<TPrdWorkTicketDetailDTO> poLw = workTicketMapper.getTicketInfoLw(po.getProcessDetailCode(), pos.getId());
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
                                    String cargoWorkType = workTicketMapper.getCargoWorkType(cargoCode);
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
//                                            TPrdWorkTiTckInfoDTO dto = workTicketMapper.getIsWorkTicket(pos.getId());
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
                                        Map<String, Object> trustShipvoyage = workTicketMapper.getTrustShipvoyage(pos.getTrustId());
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
                                                String cargoInfo = workTicketMapper.getCargoInfo(tPrdWorkTiTckInfoDTO.getCargoInfoId());
                                                if (cargoInfo != null) {
                                                    tPrdWorkTiTckInfoDTO.setCargoNameLabel(cargoInfo);
                                                }
                                                //设置船名
                                                Map<String, Object> map = workTicketMapper.getTrustWorkShipInfo(pos.getId());
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
                                                tYardTallyItemPO = workTicketMapper.getTallySh(tPrdWorkTiTckInfoDTO.getTrustCargoInfoId(), cargoCode, finalStartTime, finalEndTime);
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
//                                    List<Map<String, Object>> locationStartList = workTicketMapper.getLocation(pos.getId(), "1");
//                                    List<Map<String, Object>> locationEndList = workTicketMapper.getLocation(pos.getId(), "2");
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
            List<TPrdWorkTiTckInfoDTO> list = workTicketMapper.getWorkTicketList(query);
            return list;
        }

    }

    @Override
    public Map<String, Object> getWorkMeasure(TicketMeasureDTO ticketMeasureDTO) {
        //判断货物信息是件杂还是散杂 件杂取理货数据 散杂取地磅数据
        String workFlag = workTicketMapper.getCargoWorkType(ticketMeasureDTO.getCargoCode());
        if ("1".equals(workFlag)) {
            //件杂
            //根据计划ID，作业过程，票货ID 汇总作业量
            return workTicketMapper.getTallyWorkMeasure(ticketMeasureDTO);
        } else {
            TPrdWorkPlanPO workPlan = workTicketMapper.getWorkPlan(ticketMeasureDTO.getWorkPlanId());
            List<Long> cargoTrustIds = workTicketMapper.getWorkCargoTrustId(ticketMeasureDTO.getCargoInfoId());
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
            TYardTallyItemPO tallySh = workTicketMapper.getTallyShZq(cargoTrustIds, ticketMeasureDTO.getCargoCode(), startTime, endTime);
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
        return workTicketMapper.getUserRole("ROLE_ID IN ( 1706473088360779776,1710194450317316096,1679029256648265728,1736388455765774336, 1719384815528185856,1710202082142851072,1684477296381136896,1 )", loginUserId);
    }

    @Override
    public List<TPrdWorkTiTckInfoDTO> getMonthWorkTicketList(TPrdWorkPlanQuery query) {

        if (query.getStartDay() == null) {
            throw new BusinessRuntimeException("开始日期不能为空");
        }

        if (query.getEndDay() == null) {
            throw new BusinessRuntimeException("结束日期不能为空");
        }

        List<TPrdWorkTiTckInfoDTO> list = workTicketMapper.getMonthWorkTicketList(query);
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
                try (Cursor<TPrdWorkTiTickInfoExportDTO> cursor = workTicketMapper.getExportMonthWorkTicketList(query)) {
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

    /**
     * 获取签票数据 xin
     * @param workPlanId
     * @param type
     * @param cargoCode
     * @param processCode
     * @return
     */

    @Override
    public List<TPrdWorkTicketDetailDTO> getTicketInfo(Long workPlanId, String type, String cargoCode, String processCode) {

        //判断签票人是否符合操作按钮的部门
//        if(!"1".equals(securityUtils.getUserInfo().getIsSuperadmin())){
//            Map<String, Object> deptLevel2InfoByUserId = workTicketMapper.getDeptLevel2InfoByUserId(securityUtils.getLoginUserId());
//
//            if("1".equals(type)){
//                if(!"调度室".equals(String.valueOf(deptLevel2InfoByUserId.get("deptName")))){
//                    throw new BusinessRuntimeException("当前登录人属于"+deptLevel2InfoByUserId.get("deptName"));
//                }
//            }else if("2".equals(type)){
//                if(!"库场队".equals(String.valueOf(deptLevel2InfoByUserId.get("deptName")))){
//                    throw new BusinessRuntimeException("当前登录人属于"+deptLevel2InfoByUserId.get("deptName"));
//                }
//            }
//        }

        //判断是否存在签票数据
        List<TPrdWorkTicketDetailDTO> list = workTicketMapper.getTicketInfoByPlanId(workPlanId,type,null);

        if (!list.isEmpty()) {
            List<TPrdWorkTicketEquipmentPO> equipmentInfoByTickets = workTicketMapper.getEquipmentInfoByTicket(list.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList()));
            Map<Long, List<TPrdWorkTicketEquipmentPO>> tmpMachine = equipmentInfoByTickets.stream().collect(Collectors.groupingBy(TPrdWorkTicketEquipmentPO::getWorkTicketDetailId));
            //回写派工
            list.forEach(o->{
                if(tmpMachine.get(o.getId())!=null && (!tmpMachine.get(o.getId()).isEmpty())){
                    List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = tmpMachine.get(o.getId());
                    o.setEquipments(tPrdWorkTicketEquipmentPOS);
                    o.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                    o.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                    o.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                    o.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(tmp->{return tmp.getEquipmentId().toString();}).distinct().collect(Collectors.joining(",")));
                }
            });

            list = list.stream().sorted(Comparator.comparing(TPrdWorkTicketDetailDTO::getCargoInfoId)).collect(Collectors.toList());
            //前端禁止修改标志位
            list.forEach(o->{
                o.setDisabled("1");
            });
            return list;
        }
        //查询理货数据表获取票货信息
        //优先赋值地磅的吨数，地磅为null时再赋值理货表里面的数据 地磅只查新磅单数据
        // 作业过程中源或目的是（场,岸）  的理货过程会有两条  此时理货记录应该选其中一条
//        List<TPrdWorkTicketDetailDTO> list = null;
        //校验是否存在未完成磅单数据
        List<TPrdWorkTicketDetailDTO> tmpPoundRecordForCheck = workTicketMapper.getPoundRecordInfo(workPlanId);
        if(!tmpPoundRecordForCheck.isEmpty()){
            throw new BusinessRuntimeException(tmpPoundRecordForCheck.stream().map(TPrdWorkTicketDetailDTO::getTruckPlate).collect(Collectors.joining("<br/>"))+"<br/>尚未二次过磅，无法签票");
        }
        list = workTicketMapper.getTicketInfoWithTally(workPlanId);
        if(list.isEmpty()){
            throw new BusinessRuntimeException("没有理货");
        }
        list = new ArrayList<>(list.stream().collect(Collectors.toMap(o -> o.getCargoInfoId() + "_" + o.getProcessDetailCode(), Function.identity(), (k1, k2) -> k1)).values());
        //根据当前计划查询固机队的二次配工信息 判断是否是否要给固机队签票
        List<TPrdWorkTicketEquipmentPO> dispMachineList = workTicketMapper.getEquipmentInfoByWorkPlanId(workPlanId);
        //获取当前登录人的部门信息 为回写操作班组做数据源
        Map<String, Object> userDept = workTicketMapper.getDeptLevel2InfoByUserId(securityUtils.getLoginUserId());
        if (!"1".equals(securityUtils.getUserInfo().getIsSuperadmin())&&userDept==null) {
            throw new BusinessRuntimeException("获取当前登录人的二级部门信息失败");
        }
        //获取计划下的货物信息列表
        List<Map<String, Object>> workCargoInfo = workTicketMapper.getCargoList(workPlanId);
        //获取计划信息
        TPrdWorkPlanPO workPlan = workTicketMapper.getWorkPlan(workPlanId);
        MWorkProcessDTO processDTO = workTicketMapper.getProcessInfo(workPlan.getProcessCode());

        if(!dispMachineList.isEmpty()){
            dispMachineList = dispMachineList.stream().filter(tmpMachine->"门机".equals(tmpMachine.getEquipmentTypeName())).collect(Collectors.toList());
        }
        //集合增量
        if(!dispMachineList.isEmpty()){
            //获取数据库中固机队的id
            Long tmpGJDId = workTicketMapper.getDeptGJD();
            if (tmpGJDId==null) {
                throw new BusinessRuntimeException("获取固机队信息失败");
            }
            List<TPrdWorkTicketDetailDTO> tPrdWorkTicketDetailDTOS = new ArrayList<>();
            for (TPrdWorkTicketDetailDTO o : list) {
                // 集疏运 直取： 签票量优先赋值过磅量 （件货用理货数据 ， 散货用地磅）
                if("2".equals(workPlan.getPlanType())||"1".equals(processDTO.getIsDirectAccess())){
                    System.out.println(workCargoInfo.stream().map(item->String.valueOf(Optional.of(item.get("packingCode")).orElse(""))).
                            filter(StringUtils::isNotBlank).collect(Collectors.toList()));
                    if(workCargoInfo.stream().map(item->String.valueOf(Optional.of(item.get("packingCode")).orElse(""))).
                            filter(StringUtils::isNotBlank).anyMatch("01"::equals)){
                        o.setTon(o.getWeightGoods());
                    }
                }
                //件货如果理货量为null 再次赋值地磅量
                if(workCargoInfo.stream().map(item->String.valueOf(Optional.of(item.get("packingCode")).orElse(""))).
                        filter(StringUtils::isNotBlank).anyMatch("02"::equals)){
                    if(o.getTon()==null||o.getTon().compareTo(BigDecimal.ZERO)==0){
                        o.setTon(o.getWeightGoods());
                    }
                }



                //操作班组赋值
                o.setDeptName("1".equals(securityUtils.getUserInfo().getIsSuperadmin())?"":String.valueOf(userDept.get("deptName")));
                o.setDeptId("1".equals(securityUtils.getUserInfo().getIsSuperadmin())?null:Long.valueOf(String.valueOf(String.valueOf(userDept.get("deptId")))));
                //固机队机械信息赋值
                TPrdWorkTicketDetailDTO tmpDto = SerializationUtils.clone(o);
                for (TPrdWorkTicketEquipmentPO tmpEquipmentInfo : dispMachineList) {

                    tmpDto.setEquipmentNo(dispMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                    tmpDto.setEquipmentId(dispMachineList.stream().map(tmpPo->{return tmpPo.getEquipmentId().toString();}).distinct().collect(Collectors.joining(",")));
                    tmpDto.setEquipmentTypeName(dispMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                    tmpDto.setEquipmentTypeCode(dispMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                    tmpDto.setEquipments(dispMachineList);
                    tmpDto.setDeptId(tmpGJDId);
                    tmpDto.setDeptName("固机队");
                }
                tPrdWorkTicketDetailDTOS.add(tmpDto);
            };
            list.addAll( tPrdWorkTicketDetailDTOS);
            //不需要给固机队签票的正常流程
        }else{
            list.forEach(o->{
                // 集疏运 直取： 签票量优先赋值过磅量 （件货用理货数据 ， 散货用地磅）
                if("2".equals(workPlan.getPlanType())||"1".equals(processDTO.getIsDirectAccess())){
                    System.out.println(workCargoInfo.stream().map(item->String.valueOf(Optional.of(item.get("packingCode")).orElse(""))).
                            filter(StringUtils::isNotBlank).collect(Collectors.toList()));
                    if(workCargoInfo.stream().map(item -> String.valueOf(Optional.of(item.get("packingCode")).orElse(""))).
                            filter(StringUtils::isNotBlank).anyMatch("01"::equals)){
                        o.setTon(o.getWeightGoods());
                    }
                }
                //件货如果理货量为null 再次赋值地磅量
                if(workCargoInfo.stream().map(item->String.valueOf(Optional.of(item.get("packingCode")).orElse(""))).
                        filter(StringUtils::isNotBlank).anyMatch("02"::equals)){
                    if(o.getTon()==null||o.getTon().compareTo(BigDecimal.ZERO)==0){
                        o.setTon(o.getWeightGoods());
                    }
                }
                //操作班组赋值
                o.setDeptName("1".equals(securityUtils.getUserInfo().getIsSuperadmin())?"":String.valueOf(userDept.get("deptName")));
                o.setDeptId("1".equals(securityUtils.getUserInfo().getIsSuperadmin())?null:Long.valueOf(String.valueOf(String.valueOf(userDept.get("deptId")))));

            });
        }
        list = list.stream().sorted(Comparator.comparing(TPrdWorkTicketDetailDTO::getCargoInfoId)).collect(Collectors.toList());
        //前端禁止修改标志位
        list.forEach(o->{
            o.setDisabled("1");
        });
        return list;
    }
    public List<TPrdWorkTicketDetailDTO> getLgTicketInfo(Long workPlanId, String type, String cargoCode, String processCode) {

        //判断签票人是否符合操作按钮的部门
//        if(!"1".equals(securityUtils.getUserInfo().getIsSuperadmin())){
//            Map<String, Object> deptLevel2InfoByUserId = workTicketMapper.getDeptLevel2InfoByUserId(securityUtils.getLoginUserId());
//
//            if("1".equals(type)){
//                if(!"调度室".equals(String.valueOf(deptLevel2InfoByUserId.get("deptName")))){
//                    throw new BusinessRuntimeException("当前登录人属于"+deptLevel2InfoByUserId.get("deptName"));
//                }
//            }else if("2".equals(type)){
//                if(!"库场队".equals(String.valueOf(deptLevel2InfoByUserId.get("deptName")))){
//                    throw new BusinessRuntimeException("当前登录人属于"+deptLevel2InfoByUserId.get("deptName"));
//                }
//            }
//        }
        //判断是否存在签票数据
        List<TPrdWorkTicketDetailDTO> list = workTicketMapper.getTicketInfoByPlanId(workPlanId,type,null);
        if (!list.isEmpty()) {
            List<TPrdWorkTicketEquipmentPO> equipmentInfoByTickets = workTicketMapper.getEquipmentInfoByTicket(list.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList()));
            Map<Long, List<TPrdWorkTicketEquipmentPO>> tmpMachine = equipmentInfoByTickets.stream().collect(Collectors.groupingBy(TPrdWorkTicketEquipmentPO::getWorkTicketDetailId));
            //回写派工
            list.forEach(o->{
                if(tmpMachine.get(o.getId())!=null && (!tmpMachine.get(o.getId()).isEmpty())){
                    List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = tmpMachine.get(o.getId());
                    o.setEquipments(tPrdWorkTicketEquipmentPOS);
                    o.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                    o.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                    o.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                    o.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(tmp->{return tmp.getEquipmentId().toString();}).distinct().collect(Collectors.joining(",")));
                }
                o.setDisabled("1");
            });
        }
        if(!list.isEmpty()){
            return list.stream().sorted(Comparator.comparing(o -> o.getDeptId()+"_"+o.getEquipmentTypeCode())).collect(Collectors.toList());
        }
        //获取辅助计划作业线信息
        TPrdWorkPlanPO workPlan = workTicketMapper.getWorkPlan(workPlanId);

        //没有签票根据二次派工信息自动带出
        TPrdTicketSeconAllotQuery tmpQuery = new TPrdTicketSeconAllotQuery();
            //机械配工情况
            //获取二次配工中的机械分配情况
            //获取单类型分配量
            tmpQuery.setDispatchType(DISP_FOR_MACHINE);
            tmpQuery.setWorkPlanId(workPlanId);
            List<TPrdDispatchSecondaryDTO> tmpSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
            if(tmpSecDispInfo.isEmpty()){

            }else{

                //为数组增量做准备
                Map<String, List<TPrdDispatchSecondaryDTO>> tmpMachineMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o->o.getDeptId().toString()+"_"+o.getEquipmentTypeId().toString()+"_"+o.getSubProcessCode()));

                for (String tmpKey : tmpMachineMap.keySet()) {
                    List<TPrdDispatchSecondaryDTO> v = tmpMachineMap.get(tmpKey);
                    if (v.isEmpty()) {
                        //正常情况
                    } else {
                        TPrdWorkTicketDetailDTO tmpResDto = new TPrdWorkTicketDetailDTO();
                        tmpResDto.setCargoInfoId(null);
                        //回写前端派机信息 供后端新增使用
                        List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = new ArrayList<>();
                        for (TPrdDispatchSecondaryDTO tmpForDto : v) {
                            TPrdWorkTicketEquipmentPO tPrdWorkTicketEquipmentPO = new TPrdWorkTicketEquipmentPO();
                            tPrdWorkTicketEquipmentPO.setEquipmentId(tmpForDto.getEquipmentId());
                            tPrdWorkTicketEquipmentPO.setEquipmentNo(tmpForDto.getEquipmentNo());
                            tPrdWorkTicketEquipmentPO.setEquipmentTypeCode(tmpForDto.getEquipmentTypeCode());
                            tPrdWorkTicketEquipmentPO.setEquipmentTypeName(tmpForDto.getEquipmentTypeName());
                            tPrdWorkTicketEquipmentPOS.add(tPrdWorkTicketEquipmentPO);
                        }
                        //回写前端派机信息
                        tmpResDto.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(o -> o.getEquipmentId().toString()).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipments(tPrdWorkTicketEquipmentPOS);
                        tmpResDto.setDeptId(v.get(0).getDeptId());
                        tmpResDto.setDeptName(v.get(0).getDeptName());
                        tmpResDto.setProcessDetailCode(v.get(0).getSubProcessCode());
                        tmpResDto.setProcessDetailName(v.get(0).getSubProcessName());
                        //分配量
                        tmpResDto.setTon(null);
                        tmpResDto.setCargoName(workPlan.getCargoName());
                        tmpResDto.setCargoCode(workPlan.getCargoCode());

                        list.add(tmpResDto);
                    }
                }
            }
            //人员配工情况
            tmpQuery.setDispatchType(DISP_FOR_LABOR);
            tmpQuery.setWorkPlanId(workPlanId);
            List<TPrdDispatchSecondaryDTO> tmpLaborSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
            if(tmpLaborSecDispInfo.isEmpty()){

            }else{

                if (tmpLaborSecDispInfo.isEmpty()){
                    throw new BusinessRuntimeException("没有人员配工信息");
                }
                tmpLaborSecDispInfo.forEach(o->{
                    if(o.getDeptParentId()==null){
                        throw new BusinessRuntimeException("装卸队二次配工中缺少部门信息");
                    }
                    o.setTmpDeptId(o.getDeptId());
                    o.setTmpDeptName(o.getDeptName());
                    o.setDeptId(o.getDeptParentId());
                    o.setDeptName(o.getDeptParentName());
                });
                if(tmpLaborSecDispInfo.isEmpty()){

                }else {
                    // 结算单人作业单价
                    //拆分每个部分派工多少人，为集合增量做准备，为计算吨数做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> laborMap = tmpLaborSecDispInfo.stream()
                            .collect(Collectors.groupingBy(o->o.getDeptId()+"_"+o.getSubProcessCode()));

                    //分配签票
                    for (String tmpId : laborMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> tmpLabor = laborMap.get(tmpId);
                        if(tmpLabor.isEmpty()){

                        }else{

                            TPrdWorkTicketDetailDTO tmpResDto = new TPrdWorkTicketDetailDTO();
                            tmpResDto.setCargoInfoId(null);

                            tmpResDto.setDeptName(tmpLabor.get(0).getDeptName());
                            tmpResDto.setDeptId(tmpLabor.get(0).getDeptId());
                            tmpResDto.setProcessDetailName(tmpLabor.get(0).getSubProcessName());
                            tmpResDto.setProcessDetailCode(tmpLabor.get(0).getSubProcessCode());
                            tmpResDto.setTon(null);
                            tmpResDto.setLaborNumber(null);

                            tmpResDto.setCargoName(workPlan.getCargoName());
                            tmpResDto.setCargoCode(workPlan.getCargoCode());
                            list.add(tmpResDto);
                        }
                    }
                }
            }
        return list.stream().sorted(Comparator.comparing(o -> o.getDeptId()+"_"+o.getEquipmentTypeCode())).collect(Collectors.toList());
    }

    /**
     * 为页面上的pc添加、app新增票货按钮做数据源
     * @param workPlanId
     * @param type
     * @param cargoCode
     * @param processCode
     * @return
     */
    @Override
    public List<TPrdWorkTicketDetailDTO> getTicketInfoForAdd(Long workPlanId, String type, String cargoCode, String processCode) {

//        //判断签票人是否符合操作按钮的部门
//        if(!"1".equals(securityUtils.getUserInfo().getIsSuperadmin())) {
//
//            Map<String, Object> deptLevel2InfoByUserId = workTicketMapper.getDeptLevel2InfoByUserId(securityUtils.getLoginUserId());
//            if ("1".equals(type)) {
//                if (!"调度室".equals(String.valueOf(deptLevel2InfoByUserId.get("deptName")))) {
//                    throw new BusinessRuntimeException("当前登录人属于" + deptLevel2InfoByUserId.get("deptName"));
//                }
//            } else if ("2".equals(type)) {
//                if (!"库场队".equals(String.valueOf(deptLevel2InfoByUserId.get("deptName")))) {
//                    throw new BusinessRuntimeException("当前登录人属于" + deptLevel2InfoByUserId.get("deptName"));
//                }
//            }
//        }
        List<TPrdWorkTicketDetailDTO> result = workTicketMapper.getTicketInfoForAdd(workPlanId, type);
        if (result.isEmpty()) {
            throw new BusinessRuntimeException("没有找到票货信息");
        }
        //获取当前登录人的二级部门信息
        Map<String, Object> userDept = workTicketMapper.getDeptLevel2InfoByUserId(securityUtils.getLoginUserId());

        result.forEach(tmpDto->{
            tmpDto.setDeptName(userDept==null?"":String.valueOf(userDept.get("deptName")));
            tmpDto.setDeptId(userDept==null?null:Long.valueOf(String.valueOf(String.valueOf(userDept.get("deptId")))));
        });
        return result;
    }

    @Override
    public List<SysDeptDTO> getDepts(String type) {
        return workTicketMapper.getDepts(type);
//        List<SysDeptDTO> depts = workTicketMapper.getDepts(type);
//
//        ArrayList<SysDeptDTO> result = new ArrayList<>();
//
//        if(!"1".equals(securityUtils.getUserInfo().getIsSuperadmin())){
//            depts.forEach(o->{
//                if ("固机队".equals(o.getDeptName())){
//                    result.add(o);
//                }else if("1".equals(type)&&"调度室".equals(o.getDeptName())){
//                    result.add(o);
//                }else if("2".equals(type)&&"库场队".equals(o.getDeptName())){
//                    result.add(o);
//                }
//            });
//        }else
//        {
//            return depts;
//        }
//        return result;
    }

    @Override
    public List<SysDeptDTO> getDeptsTally() {
        UserInfo userInfo = securityUtils.getUserInfo();
        return workTicketMapper.getDeptsTally(userInfo.getDeptId());
    }

    @Override
    public Integer getProcessType(String code) {
        return workTicketMapper.getIsProcrss(code);
    }

    @Override
    public List<String> getProcessIsTally(String processCode, String type, String cargoCode, Long workPlanId) {
        if (cargoCode != null) {

            //卷钢
            if ("040000020001".contains(cargoCode)) {
                type = "1";
            }
        }
        List<String> processIsTally = workTicketMapper.getProcessIsTally(processCode, type);
        if(CollectionUtils.isEmpty(processIsTally) && "2".equals(type) ){
            List<String> list = workTicketMapper.getProcessIsTally(processCode, "4");
            if(!CollectionUtils.isEmpty(list)){
                processIsTally.addAll(list);
            }
        }
//        Iterator<String> iter = processIsTally.iterator();
//        while (iter.hasNext()) {
//            Integer count = workTicketMapper.getIsPg(iter.next(), workPlanId);
//            if (count == 0) {
//                //二次派工没有该作业过程
//                iter.remove();
//            }
//        }
        return processIsTally;
    }

    @Override
    public String getProcess(String processCode, String type) {
        String str = workTicketMapper.getProcess(processCode, type);
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

    @Override
    public List<TPrdWorkTicketEquipmentPO> getInitMahineSlectData(Long workPlanId, String type) {
        return workTicketMapper.getEquipmentInfoByWorkPlanId(workPlanId);
    }


    //自动生成作业票
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void autoSecondALlotTicket(TPrdWorkTicketDTO workTicketDTO,TPrdWorkPlanPO workPlanDto) {
        long l1 = System.currentTimeMillis();
        List<TPrdWorkTicketDetailDTO> realResult = new ArrayList<>();
        //删除原来的分配
        ticketSecondAllotMapper.deleteWorkTicketDetailByWorkPlanId(workTicketDTO.getWorkPlanId());
        List<TPrdWorkTicketDetailDTO> ticketInfoByPlanId = workTicketMapper.getTicketInfoByPlanId(workTicketDTO.getWorkPlanId(), null,"1");

        MWorkProcessDTO processInfo = workTicketMapper.getProcessInfo(workPlanDto.getProcessCode());
        List<TPrdWorkTicketDetailDTO> specilaResult = new ArrayList<TPrdWorkTicketDetailDTO>();
        List<TPrdWorkTicketDetailDTO> tmpResult = new ArrayList<>();
        BigDecimal allTon = ticketInfoByPlanId.stream().map(o->Optional.of(o.getTon()).orElseThrow(()->new BusinessRuntimeException("获取签票量失败"))).reduce(BigDecimal.ZERO, BigDecimal::add);

        //直取特殊处理和正常的逻辑是分开的两部分
        if( "1".equals(processInfo.getIsDirectAccess())){
            List<TPrdWorkTicketDetailDTO> specialList = ticketSecondAllotMapper.getTallListForAllot(workPlanDto.getId());
//            FOR_MACHINE
//        处理机械的信息，为了合并转运机械和普通的作业机械

            List<TPrdWorkTicketDetailDTO> tmpMachineList = specialList.stream().filter(o -> o.getEquipmentId() != null).collect(Collectors.toList());
                tmpMachineList.forEach(o->{
                    o.setDeptId(o.getMachineDeptId());
                    o.setDeptName(o.getMachineDeptName());
                });
                List<TPrdWorkTicketDetailDTO> tmpMachineTranList = specialList.stream().filter(o ->( o.getTransportEquipmentNo() != null) && ( o.getTransportEquipmentTypeCode() !=null)).collect(Collectors.toList());
                tmpMachineTranList.forEach(o->{
                    o.setEquipmentTypeCode(o.getTransportEquipmentTypeCode());
                    o.setEquipmentTypeName(o.getTransportEquipmentTypeName());
                    o.setEquipmentId(o.getTransportEquipmentId()==null?null:o.getTransportEquipmentId().toString());
                    o.setEquipmentNo(o.getTransportEquipmentNo());
                    o.setDeptId(o.getTransportDeptId());
                    o.setDeptName(o.getTransportDeptName());
                });
                //合并
                tmpMachineList.addAll(tmpMachineTranList);
                if(tmpMachineList.isEmpty()){

                }else {
                    tmpMachineList = tmpMachineList.stream().filter(tmpMachine -> !"固机队".equals(tmpMachine.getDeptName())).collect(Collectors.toList());
                    if(tmpMachineList.isEmpty()){

                    }else {
                        tmpMachineList = tmpMachineList.stream().filter(o -> !"999999999".equals(o.getEquipmentId())).collect(Collectors.toList());
                        if(tmpMachineList.isEmpty()){

                        }else {
                            Map<String, List<TPrdWorkTicketDetailDTO>> machineTallyMap = tmpMachineList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" +o.getDeptId()+"_"+o.getEquipmentTypeCode()));

                            //回显机械信息 为后面插入作业票做数据准备
                            for (String s : machineTallyMap.keySet()) {
                                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = machineTallyMap.get(s);
                                if(workTicketDetailDTOS.isEmpty()){
                                    continue;
                                }
                                TPrdWorkTicketDetailDTO tmpResDto = new TPrdWorkTicketDetailDTO();
                                tmpResDto.setId(snowflake.nextId());
                                tmpResDto.setAllotType(FOR_MACHINE);
                                List<TPrdWorkTicketEquipmentPO> tmpRowMachineList = new ArrayList<>();
                                workTicketDetailDTOS.forEach(curTick->{
                                    TPrdWorkTicketEquipmentPO tPrdWorkTicketEquipmentPO = new TPrdWorkTicketEquipmentPO();
                                    tPrdWorkTicketEquipmentPO.setEquipmentId(new Long(curTick.getEquipmentId()));
                                    tPrdWorkTicketEquipmentPO.setEquipmentNo(curTick.getEquipmentNo());
                                    tPrdWorkTicketEquipmentPO.setEquipmentTypeCode(curTick.getEquipmentTypeCode());
                                    tPrdWorkTicketEquipmentPO.setEquipmentTypeName(curTick.getEquipmentTypeName());
                                    tmpRowMachineList.add(tPrdWorkTicketEquipmentPO);
                                });
                                tmpResDto.setEquipments(tmpRowMachineList);
                                tmpResDto.setEquipmentNo(tmpRowMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentId(tmpRowMachineList.stream().map(o -> o.getEquipmentId().toString()).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentTypeCode(tmpRowMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentTypeName(tmpRowMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                                //作业票列表展示的信息
                                tmpResDto.setDeptId(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配获取理货，获取部门信息失败")).getDeptId());
                                tmpResDto.setDeptName(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配获取理货，获取部门信息失败")).getDeptName());
                                tmpResDto.setWorkTicketId(ticketInfoByPlanId.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("获取签票信息失败")).getWorkTicketId());
                                tmpResDto.setEquipmentTypeCode(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配理货，获取机械类型信息失败")).getEquipmentTypeCode());
                                tmpResDto.setEquipmentTypeName(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配理货，获取机械类型信息失败")).getEquipmentTypeName());
                                tmpResDto.setProcessDetailCode(workTicketDetailDTOS.get(0).getProcessDetailCode());
                                tmpResDto.setProcessDetailName(workTicketDetailDTOS.get(0).getProcessDetailName());
                                tmpResDto.setProcessCode(workPlanDto.getProcessCode());
                                tmpResDto.setProcessName(workPlanDto.getProcessName());
                                tmpResDto.setTon(workTicketDetailDTOS.stream().map(o->Optional.of(o.getTon()).orElseThrow(()->new BusinessRuntimeException("自动分配理货，获取理货重量失败"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                                tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                                tmpResDto.setClassCode(workPlanDto.getClassCode());
                                tmpResDto.setClassName(workPlanDto.getClassName());
                                tmpResult.add(tmpResDto);
                            }
                        }

                    }
                }

                //获取二次配工信息处理下仓作业过程
                TPrdTicketSeconAllotQuery tmpQuery = new TPrdTicketSeconAllotQuery();
                tmpQuery.setDispatchType(DISP_FOR_MACHINE);
                tmpQuery.setWorkPlanId(workTicketDTO.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                tmpSecDispInfo = tmpSecDispInfo.stream().filter(o -> "0".equals(o.getIsTallyCourse())).collect(Collectors.toList());
                //获取签票中的辅助作业过程
                if(!tmpSecDispInfo.isEmpty()){
                    //为数组增量做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> tmpMachineMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o->o.getDeptId().toString()+"_"+o.getSubProcessCode()+"_"+o.getWorkPositionCode()+"_"+o.getEquipmentTypeCode()));
                    //为计算机械单价做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> machineTypeMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(
                            o->o.getSubProcessCode()+"_"+o.getWorkPositionCode()));
                    HashMap<String, BigDecimal> singleMachine = new HashMap<>();
                    for (String tmpId : machineTypeMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = machineTypeMap.get(tmpId);
                        if (!tPrdDispatchSecondaryDTOS.isEmpty()) {
                            singleMachine.put(tmpId,
                                    allTon.divide(Optional.of(BigDecimal.valueOf(tPrdDispatchSecondaryDTOS.size())).orElse(BigDecimal.ZERO), 2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                    for (String tmpKey : tmpMachineMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> v = tmpMachineMap.get(tmpKey);
                        if (v.isEmpty()) {
                            //正常情况
                        } else {
                            TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(ticketInfoByPlanId.get(0));
                            tmpResDto.setId(snowflake.nextId());
                            tmpResDto.setAllotType(FOR_MACHINE);
                            tmpResDto.setWorkTicketId(workTicketDTO.getId());
                            tmpResDto.setCargoInfoId(null);
                            //回写前端派机信息 供后端新增使用
                            List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = new ArrayList<>();
                            for (TPrdDispatchSecondaryDTO tmpForDto : v) {
                                TPrdWorkTicketEquipmentPO tPrdWorkTicketEquipmentPO = new TPrdWorkTicketEquipmentPO();
                                tPrdWorkTicketEquipmentPO.setEquipmentId(tmpForDto.getEquipmentId());
                                tPrdWorkTicketEquipmentPO.setEquipmentNo(tmpForDto.getEquipmentNo());
                                tPrdWorkTicketEquipmentPO.setEquipmentTypeCode(tmpForDto.getEquipmentTypeCode());
                                tPrdWorkTicketEquipmentPO.setEquipmentTypeName(tmpForDto.getEquipmentTypeName());
                                tPrdWorkTicketEquipmentPOS.add(tPrdWorkTicketEquipmentPO);
                            }
                            //回写前端派机信息
                            tmpResDto.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(o -> o.getEquipmentId().toString()).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipments(tPrdWorkTicketEquipmentPOS);
                            tmpResDto.setDeptId(v.get(0).getDeptId());
                            tmpResDto.setDeptName(v.get(0).getDeptName());
                            tmpResDto.setProcessDetailCode(v.get(0).getSubProcessCode());
                            tmpResDto.setProcessDetailName(v.get(0).getSubProcessName());
                            tmpResDto.setWorkPositionName(v.get(0).getWorkPositionName());
                            tmpResDto.setWorkPositionCode(v.get(0).getWorkPositionCode());
                            tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                            tmpResDto.setClassCode(workPlanDto.getClassCode());
                            tmpResDto.setClassName(workPlanDto.getClassName());
                            //分配量
                            tmpResDto.setTon(BigDecimal.valueOf(v.size()).multiply(
                                    Optional.of(singleMachine.get(v.get(0).getSubProcessCode()+"_"+v.get(0).getWorkPositionCode())).orElse(BigDecimal.ZERO))
                                    .setScale(2,BigDecimal.ROUND_HALF_UP));

                            tmpResult.add(tmpResDto);
                        }
                    }
                }
                realResult.addAll(tmpResult);
            //    FOR_LABOR
            //获取签票中人员信息
            List<Map<String,Object>> specialListLabor = ticketSecondAllotMapper.getTallListForAllotWithMap(workPlanDto.getId());
            List<Map<String,Object>> laborTallyInfo = specialListLabor.stream().filter(o -> StringUtils.isNotBlank(String.valueOf(o.get("deptId"))) ).collect(Collectors.toList());
                //过滤固机队
                laborTallyInfo = laborTallyInfo.stream().filter(o -> !"固机队".equals(String.valueOf(o.get("deptName")))).collect(Collectors.toList());
                if(laborTallyInfo.isEmpty()){

                }else{
                    //过滤无装卸部门
                    laborTallyInfo = laborTallyInfo.stream().filter(o -> !"999999999".equals(String.valueOf(o.get("deptId")))).collect(Collectors.toList());
                    if(laborTallyInfo.isEmpty()){

                    }else {

                        Map<String, List<Map<String,Object>>> resultMap = laborTallyInfo.stream().collect(Collectors.groupingBy(o -> String.valueOf(o.get("processDetailCode")) + "_" +String.valueOf(o.get("deptId")) ));
                        for (String s : resultMap.keySet()) {
                            List<Map<String,Object>> workTicketDetailDTOS = resultMap.get(s);
                            if(workTicketDetailDTOS.isEmpty()){
                                continue;
                            }
                            TPrdWorkTicketDetailDTO tmpResDto = new TPrdWorkTicketDetailDTO();
                            tmpResDto.setId(snowflake.nextId());
                            tmpResDto.setWorkTicketId(workTicketDTO.getId());
                            tmpResDto.setAllotType(FOR_LABOR);
                            tmpResDto.setDeptId(new Long(String.valueOf(workTicketDetailDTOS.get(0).get("deptId"))));
                            tmpResDto.setDeptName(String.valueOf(workTicketDetailDTOS.get(0).get("deptName")));
                            tmpResDto.setProcessName(workPlanDto.getProcessName());
                            tmpResDto.setProcessCode(workPlanDto.getProcessCode());
                            tmpResDto.setProcessDetailCode(String.valueOf(workTicketDetailDTOS.get(0).get("processDetailCode")));
                            tmpResDto.setProcessDetailName(String.valueOf(workTicketDetailDTOS.get(0).get("ProcessDetailName")));
                            tmpResDto.setTon(workTicketDetailDTOS.stream()
                                    .map(o->Optional.of(new BigDecimal(String.valueOf(o.get("ton")))).orElseThrow(()->new BusinessRuntimeException("获取理货量失败")))
                                    .reduce(BigDecimal.ZERO,BigDecimal::add));
                            tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                            tmpResDto.setClassCode(workPlanDto.getClassCode());
                            tmpResDto.setClassName(workPlanDto.getClassName());
                            specilaResult.add(tmpResDto);
                        }
                    }
                }

                //判断是否存在辅助作业过程
                tmpQuery.setDispatchType(DISP_FOR_LABOR);
                tmpQuery.setWorkPlanId(workTicketDTO.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpLaborSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                tmpLaborSecDispInfo = tmpLaborSecDispInfo.stream().filter(o -> "0".equals(o.getIsTallyCourse())).collect(Collectors.toList());
                if (!tmpLaborSecDispInfo.isEmpty()) {

                    // 结算单人作业单价
                    //拆分每个部分派工多少人，为集合增量做准备，为计算吨数做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> laborMap = tmpLaborSecDispInfo.stream()
                            .collect(Collectors.groupingBy(o->o.getDeptId()+"_"+o.getSubProcessCode()+"_"+o.getWorkPositionName()));
                    Map<String, List<TPrdDispatchSecondaryDTO>> tmpSingleMap = tmpLaborSecDispInfo.stream().collect(Collectors.groupingBy(o -> o.getSubProcessCode()+"_"+o.getWorkPositionName()));

                    //分配签票
                    for (String tmpId : laborMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> tmpLabor = laborMap.get(tmpId);
                        List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = tmpSingleMap.get(tmpLabor.get(0).getSubProcessCode()+"_"+tmpLabor.get(0).getWorkPositionName());
                        BigDecimal signleLabor = allTon.divide(tPrdDispatchSecondaryDTOS.stream().map(o->new BigDecimal(o.getNumberCount())).reduce(BigDecimal.ZERO,BigDecimal::add), 2, BigDecimal.ROUND_HALF_UP);

                        if(tmpLabor.isEmpty()){

                        }else{

                            TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(ticketInfoByPlanId.get(0));
                            tmpResDto.setId(snowflake.nextId());
                            tmpResDto.setCargoInfoId(null);
                            tmpResDto.setAllotType(FOR_LABOR);
                            tmpResDto.setWorkTicketId(workTicketDTO.getId());
                            tmpResDto.setDeptName(tmpLabor.get(0).getDeptParentName());
                            tmpResDto.setDeptId(tmpLabor.get(0).getDeptParentId());
                            tmpResDto.setProcessDetailName(tmpLabor.get(0).getSubProcessName());
                            tmpResDto.setProcessDetailCode(tmpLabor.get(0).getSubProcessCode());
                            tmpResDto.setWorkPositionName(tmpLabor.get(0).getWorkPositionName());
                            tmpResDto.setWorkPositionCode(tmpLabor.get(0).getWorkPositionCode());
                            tmpResDto.setTon(signleLabor.multiply(
                                    tmpLabor.stream().map(tmpLaborItem->Optional.of(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add)
                            ).setScale(2, BigDecimal.ROUND_HALF_UP));
                            tmpResDto.setLaborNumber(
                                    tmpLabor.stream().map(tmpLaborItem->Optional.of(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add)
                            );
                            tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                            tmpResDto.setClassCode(workPlanDto.getClassCode());
                            tmpResDto.setClassName(workPlanDto.getClassName());
                            specilaResult.add(tmpResDto);
                        }
                    }
                }


            realResult.addAll(specilaResult);
        }
        else{

            //正常的分配
            List<TPrdWorkTicketDetailDTO> resultWithoutTicket = new ArrayList<>();
            List<TPrdWorkTicketDetailDTO> resultWithoutTicketForLabor = new ArrayList<>();
            resultWithoutTicket = new ArrayList<>();
            resultWithoutTicketForLabor = new ArrayList<>();

            //获取签票总量
            allTon = ticketInfoByPlanId.stream().map(o -> Optional.of(o.getTon()).orElseThrow(() -> new BusinessRuntimeException("获取签票量失败"))).reduce(BigDecimal.ZERO, BigDecimal::add);

            //FOR_MACHINE
            //获取二次配工中的机械分配情况
            //获取单类型分配量
            TPrdTicketSeconAllotQuery tmpQuery = new TPrdTicketSeconAllotQuery();
            tmpQuery.setDispatchType(DISP_FOR_MACHINE);
            tmpQuery.setWorkPlanId(workTicketDTO.getWorkPlanId());
            tmpQuery.setNoGj("1");
            List<TPrdDispatchSecondaryDTO> tmpSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
            if(tmpSecDispInfo.isEmpty()){

            }else {

                //为数组增量做准备
                Map<String, List<TPrdDispatchSecondaryDTO>> tmpMachineMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o -> o.getDeptId().toString() + "_" + o.getSubProcessCode() + "_" + o.getWorkPositionName() + "_" + o.getEquipmentTypeCode()));
                //为计算机械单价做准备
                Map<String, List<TPrdDispatchSecondaryDTO>> machineTypeMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o -> o.getSubProcessCode() + "_" + o.getWorkPositionCode()));
                HashMap<String, BigDecimal> singleMachine = new HashMap<>();
                for (String tmpId : machineTypeMap.keySet()) {
                    List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = machineTypeMap.get(tmpId);
                    if (!tPrdDispatchSecondaryDTOS.isEmpty()) {
                        singleMachine.put(tmpId,
                                allTon.divide(Optional.of(BigDecimal.valueOf(tPrdDispatchSecondaryDTOS.size())).orElse(BigDecimal.ZERO), 2, BigDecimal.ROUND_HALF_UP));
                    }
                }
                for (String tmpKey : tmpMachineMap.keySet()) {
                    List<TPrdDispatchSecondaryDTO> v = tmpMachineMap.get(tmpKey);
                    if (v.isEmpty()) {
                        //正常情况
                    } else {
                        TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(ticketInfoByPlanId.get(0));
                        tmpResDto.setId(snowflake.nextId());
                        tmpResDto.setCargoInfoId(null);
                        tmpResDto.setAllotType(FOR_MACHINE);
                        tmpResDto.setWorkTicketId(workTicketDTO.getId());
                        tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                        tmpResDto.setClassCode(workPlanDto.getClassCode());
                        tmpResDto.setClassName(workPlanDto.getClassName());
                        //回写前端派机信息 供后端新增使用
                        List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = new ArrayList<>();
                        for (TPrdDispatchSecondaryDTO tmpForDto : v) {
                            TPrdWorkTicketEquipmentPO tPrdWorkTicketEquipmentPO = new TPrdWorkTicketEquipmentPO();
                            tPrdWorkTicketEquipmentPO.setEquipmentId(tmpForDto.getEquipmentId());
                            tPrdWorkTicketEquipmentPO.setEquipmentNo(tmpForDto.getEquipmentNo());
                            tPrdWorkTicketEquipmentPO.setEquipmentTypeCode(tmpForDto.getEquipmentTypeCode());
                            tPrdWorkTicketEquipmentPO.setEquipmentTypeName(tmpForDto.getEquipmentTypeName());
                            tPrdWorkTicketEquipmentPOS.add(tPrdWorkTicketEquipmentPO);
                        }
                        //回写前端派机信息
                        tmpResDto.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(o -> o.getEquipmentId().toString()).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipments(tPrdWorkTicketEquipmentPOS);
                        tmpResDto.setDeptId(v.get(0).getDeptId());
                        tmpResDto.setDeptName(v.get(0).getDeptName());
                        tmpResDto.setProcessDetailCode(v.get(0).getSubProcessCode());
                        tmpResDto.setProcessDetailName(v.get(0).getSubProcessName());
                        tmpResDto.setWorkPositionName(v.get(0).getWorkPositionName());
                        tmpResDto.setWorkPositionCode(v.get(0).getWorkPositionCode());
                        //分配量
                        tmpResDto.setTon(BigDecimal.valueOf(v.size()).multiply(Optional.of(singleMachine.get(v.get(0).getSubProcessCode() + "_" + v.get(0).getWorkPositionCode())).orElse(BigDecimal.ZERO)).setScale(2, BigDecimal.ROUND_HALF_UP));

                        resultWithoutTicket.add(tmpResDto);
                    }
                }

                realResult.addAll(resultWithoutTicket);
            }
            //FOR_LABOR
            tmpQuery.setDispatchType(DISP_FOR_LABOR);
            tmpQuery.setWorkPlanId(workTicketDTO.getWorkPlanId());
            tmpQuery.setNoGj("1");
            List<TPrdDispatchSecondaryDTO> tmpLaborSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
            if(tmpSecDispInfo.isEmpty()&&tmpLaborSecDispInfo.isEmpty()){
                throw new BusinessRuntimeException("自动二次分配失败，没有二次配工");
            }
            if (tmpLaborSecDispInfo.isEmpty()) {
//                        throw new BusinessRuntimeException("没有人员配工信息");
            }else{

                tmpLaborSecDispInfo.forEach(o -> {
                    if (o.getDeptParentId() == null) {
                        throw new BusinessRuntimeException("装卸队二次配工中缺少部门信息");
                    }
                    o.setTmpDeptId(o.getDeptId());
                    o.setTmpDeptName(o.getDeptName());
                    o.setDeptId(o.getDeptParentId());
                    o.setDeptName(o.getDeptParentName());
                });
                if (tmpLaborSecDispInfo.isEmpty()) {

                } else {
                    // 结算单人作业单价
                    //拆分每个部分派工多少人，为集合增量做准备，为计算吨数做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> laborMap = tmpLaborSecDispInfo.stream()
                            .collect(Collectors.groupingBy(o -> o.getDeptId() + "_" + o.getSubProcessCode() + "_" + o.getWorkPositionName()));
                    Map<String, List<TPrdDispatchSecondaryDTO>> tmpSingleMap = tmpLaborSecDispInfo.stream().collect(Collectors.groupingBy(o -> o.getSubProcessCode() + "_" + o.getWorkPositionName()));

                    //分配签票
                    for (String tmpId : laborMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> tmpLabor = laborMap.get(tmpId);
                        List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = tmpSingleMap.get(tmpLabor.get(0).getSubProcessCode() + "_" + tmpLabor.get(0).getWorkPositionName());
                        BigDecimal signleLabor = allTon.divide(tPrdDispatchSecondaryDTOS.stream().map(o -> new BigDecimal(o.getNumberCount())).reduce(BigDecimal.ZERO, BigDecimal::add), 2, BigDecimal.ROUND_HALF_UP);

                        if (tmpLabor.isEmpty()) {

                        } else {
                            TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(ticketInfoByPlanId.get(0));
                            tmpResDto.setId(snowflake.nextId());
                            tmpResDto.setWorkTicketId(workTicketDTO.getId());
                            tmpResDto.setCargoInfoId(null);
                            tmpResDto.setAllotType(FOR_LABOR);

                            tmpResDto.setDeptName(tmpLabor.get(0).getDeptName());
                            tmpResDto.setDeptId(tmpLabor.get(0).getDeptId());
                            tmpResDto.setProcessDetailName(tmpLabor.get(0).getSubProcessName());
                            tmpResDto.setProcessDetailCode(tmpLabor.get(0).getSubProcessCode());
                            tmpResDto.setWorkPositionName(tmpLabor.get(0).getWorkPositionName());
                            tmpResDto.setWorkPositionCode(tmpLabor.get(0).getWorkPositionCode());
                            tmpResDto.setTon(signleLabor.multiply(
                                    tmpLabor.stream().map(tmpLaborItem -> Optional.of(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add)
                            ).setScale(2, BigDecimal.ROUND_HALF_UP));
                            tmpResDto.setLaborNumber(
                                    tmpLabor.stream().map(tmpLaborItem -> Optional.of(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add)
                            );
                            tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                            tmpResDto.setClassCode(workPlanDto.getClassCode());
                            tmpResDto.setClassName(workPlanDto.getClassName());
                            resultWithoutTicketForLabor.add(tmpResDto);
                        }
                    }
                }
            }
            realResult.addAll(resultWithoutTicketForLabor);
        }

        //插入作业票机械数据

        //返回作业票机械子表数据，给作业票子表赋值
        List<TPrdWorkTicketEquipmentPO> equipments = realResult.stream().filter(o->FOR_MACHINE.equals(o.getAllotType())).flatMap(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setWorkTicketId(workTicketDTO.getId());
            v1.setProcessCode(workTicketDTO.getProcessCode());
            v1.setProcessName(workTicketDTO.getProcessName());
            v1.setWorkDate(workTicketDTO.getWorkDate());
            v1.setClassCode(workTicketDTO.getClassCode());
            v1.setClassName(workTicketDTO.getClassName());

            return Optional.ofNullable(v1.getEquipments())
                    .orElse(Collections.emptyList()).stream().filter(v3->v3.getEquipmentId()!=null)
                    .peek(v2 -> {
                v2.setId(snowflake.nextId());
                v2.setWorkTicketDetailId(v1.getId());
            });
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(equipments)) {
            //删除机械数据
            workTicketMapper.delTicketEquipmentByPlanId(workTicketDTO.getWorkPlanId());
            workTicketMapper.insertWorkTicketEquipment(equipments);
        }
        //插入子表数据
        if(realResult.isEmpty()){
            throw new BusinessRuntimeException("自动二次分配失败，没有要分配的签票数据，请检查二次配工是否正确");
        }else{
            workTicketMapper.insertWorkTicketDetail(realResult);
        }

        long l2 = System.currentTimeMillis();
        System.out.println("自动分配  用时"+(l2-l1));

        //分配计件
        //删除分配计件
//        String planType = workTicketMapper.getWorkPlanType(workTicketDTO.getWorkPlanId());
        TPrdTicketSeconAllotQuery query = new TPrdTicketSeconAllotQuery();
        query.setNoGj("1");
        query.setWorkPlanId(workTicketDTO.getWorkPlanId());
        query.setAllotType("1");

        ArrayList<TPrdWorkTicketDetailDTO> ticketDetailList = ticketSecondAllotMapper.getTicketDetailList(query);
        realResult.forEach(o->{
            o.setCargoCode(ticketDetailList.get(0).getCargoCode());
            o.setCargoName(ticketDetailList.get(0).getCargoName());
        });
        workTicketDTO.setDetails(realResult);
        SpringUtils.getBean(this.getClass()).commonUpdateSalary(workTicketDTO,realResult,false,"2".equals(workPlanDto.getPlanType() ),"自动二次分配");
        System.out.println("自动分配 插入计件用时"+(System.currentTimeMillis()-l2));
    }
    //自动生成作业票
/*    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void autoSecondALlotTicketWithThread(TPrdWorkTicketDTO workTicketDTO,TPrdWorkPlanPO workPlanDto) {


        long l1 = System.currentTimeMillis();
        List<TPrdWorkTicketDetailDTO> realResult = new ArrayList<>();
        //删除原来的分配
        ticketSecondAllotMapper.deleteWorkTicketDetailByWorkPlanId(workTicketDTO.getWorkPlanId());
        List<TPrdWorkTicketDetailDTO> ticketInfoByPlanId = workTicketMapper.getTicketInfoByPlanId(workTicketDTO.getWorkPlanId(), null,null);

        MWorkProcessDTO processInfo = workTicketMapper.getProcessInfo(workPlanDto.getProcessCode());
        List<TPrdWorkTicketDetailDTO> specilaResult = new ArrayList<TPrdWorkTicketDetailDTO>();
        List<TPrdWorkTicketDetailDTO> tmpResult = new ArrayList<>();
        BigDecimal allTon = ticketInfoByPlanId.stream().map(o->Optional.of(o.getTon()).orElseThrow(()->new BusinessRuntimeException("获取签票量失败"))).reduce(BigDecimal.ZERO, BigDecimal::add);

        //直取特殊处理和正常的逻辑是分开的两部分
        if( "1".equals(processInfo.getIsDirectAccess())){
            List<TPrdWorkTicketDetailDTO> specialList = ticketSecondAllotMapper.getTallListForAllot(workPlanDto.getId());
//            FOR_MACHINE
//        处理机械的信息，为了合并转运机械和普通的作业机械

            List<TPrdWorkTicketDetailDTO> tmpMachineList = specialList.stream().filter(o -> o.getEquipmentId() != null).collect(Collectors.toList());
                tmpMachineList.forEach(o->{
                    o.setDeptId(o.getMachineDeptId());
                    o.setDeptName(o.getMachineDeptName());
                });
                List<TPrdWorkTicketDetailDTO> tmpMachineTranList = specialList.stream().filter(o ->( o.getTransportEquipmentNo() != null) && ( o.getTransportEquipmentTypeCode() !=null)).collect(Collectors.toList());
                tmpMachineTranList.forEach(o->{
                    o.setEquipmentTypeCode(o.getTransportEquipmentTypeCode());
                    o.setEquipmentTypeName(o.getTransportEquipmentTypeName());
                    o.setEquipmentId(o.getTransportEquipmentId()==null?null:o.getTransportEquipmentId().toString());
                    o.setEquipmentNo(o.getTransportEquipmentNo());
                    o.setDeptId(o.getTransportDeptId());
                    o.setDeptName(o.getTransportDeptName());
                });
                //合并
                tmpMachineList.addAll(tmpMachineTranList);
                if(tmpMachineList.isEmpty()){

                }else {
                    tmpMachineList = tmpMachineList.stream().filter(tmpMachine -> !"固机队".equals(tmpMachine.getDeptName())).collect(Collectors.toList());
                    if(tmpMachineList.isEmpty()){

                    }else {
                        tmpMachineList = tmpMachineList.stream().filter(o -> !"999999999".equals(o.getEquipmentId())).collect(Collectors.toList());
                        if(tmpMachineList.isEmpty()){

                        }else {
                            Map<String, List<TPrdWorkTicketDetailDTO>> machineTallyMap = tmpMachineList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" +o.getDeptId()+"_"+o.getEquipmentTypeCode()));

                            //回显机械信息 为后面插入作业票做数据准备
                            for (String s : machineTallyMap.keySet()) {
                                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = machineTallyMap.get(s);
                                if(workTicketDetailDTOS.isEmpty()){
                                    continue;
                                }
                                TPrdWorkTicketDetailDTO tmpResDto = new TPrdWorkTicketDetailDTO();
                                tmpResDto.setId(snowflake.nextId());
                                tmpResDto.setAllotType(FOR_MACHINE);
                                List<TPrdWorkTicketEquipmentPO> tmpRowMachineList = new ArrayList<>();
                                workTicketDetailDTOS.forEach(curTick->{
                                    TPrdWorkTicketEquipmentPO tPrdWorkTicketEquipmentPO = new TPrdWorkTicketEquipmentPO();
                                    tPrdWorkTicketEquipmentPO.setEquipmentId(new Long(curTick.getEquipmentId()));
                                    tPrdWorkTicketEquipmentPO.setEquipmentNo(curTick.getEquipmentNo());
                                    tPrdWorkTicketEquipmentPO.setEquipmentTypeCode(curTick.getEquipmentTypeCode());
                                    tPrdWorkTicketEquipmentPO.setEquipmentTypeName(curTick.getEquipmentTypeName());
                                    tmpRowMachineList.add(tPrdWorkTicketEquipmentPO);
                                });
                                tmpResDto.setEquipments(tmpRowMachineList);
                                tmpResDto.setEquipmentNo(tmpRowMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentId(tmpRowMachineList.stream().map(o -> o.getEquipmentId().toString()).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentTypeCode(tmpRowMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentTypeName(tmpRowMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                                //作业票列表展示的信息
                                tmpResDto.setDeptId(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配获取理货，获取部门信息失败")).getDeptId());
                                tmpResDto.setDeptName(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配获取理货，获取部门信息失败")).getDeptName());
                                tmpResDto.setWorkTicketId(ticketInfoByPlanId.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("获取签票信息失败")).getWorkTicketId());
                                tmpResDto.setEquipmentTypeCode(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配理货，获取机械类型信息失败")).getEquipmentTypeCode());
                                tmpResDto.setEquipmentTypeName(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配理货，获取机械类型信息失败")).getEquipmentTypeName());
                                tmpResDto.setProcessDetailCode(workTicketDetailDTOS.get(0).getProcessDetailCode());
                                tmpResDto.setProcessDetailName(workTicketDetailDTOS.get(0).getProcessDetailName());
                                tmpResDto.setProcessCode(workPlanDto.getProcessCode());
                                tmpResDto.setProcessName(workPlanDto.getProcessName());
                                tmpResDto.setTon(workTicketDetailDTOS.stream().map(o->Optional.of(o.getTon()).orElseThrow(()->new BusinessRuntimeException("自动分配理货，获取理货重量失败"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                                tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                                tmpResDto.setClassCode(workPlanDto.getClassCode());
                                tmpResDto.setClassName(workPlanDto.getClassName());
                                tmpResult.add(tmpResDto);
                            }
                        }

                    }
                }

                //获取二次配工信息处理下仓作业过程
                TPrdTicketSeconAllotQuery tmpQuery = new TPrdTicketSeconAllotQuery();
                tmpQuery.setDispatchType(DISP_FOR_MACHINE);
                tmpQuery.setWorkPlanId(workTicketDTO.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                tmpSecDispInfo = tmpSecDispInfo.stream().filter(o -> "0".equals(o.getIsTallyCourse())).collect(Collectors.toList());
                //获取签票中的辅助作业过程
                if(!tmpSecDispInfo.isEmpty()){
                    //为数组增量做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> tmpMachineMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o->o.getDeptId().toString()+"_"+o.getSubProcessCode()+"_"+o.getWorkPositionName()+"_"+o.getEquipmentTypeCode()));
                    //为计算机械单价做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> machineTypeMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(
                            o->o.getSubProcessCode()+"_"+o.getWorkPositionName()+"_"+o.getDeptId()));
                    HashMap<String, BigDecimal> singleMachine = new HashMap<>();
                    for (String tmpId : machineTypeMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = machineTypeMap.get(tmpId);
                        if (!tPrdDispatchSecondaryDTOS.isEmpty()) {
                            singleMachine.put(tmpId,
                                    allTon.divide(Optional.of(BigDecimal.valueOf(tPrdDispatchSecondaryDTOS.size())).orElse(BigDecimal.ZERO), 2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                    for (String tmpKey : tmpMachineMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> v = tmpMachineMap.get(tmpKey);
                        if (v.isEmpty()) {
                            //正常情况
                        } else {
                            TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(ticketInfoByPlanId.get(0));
                            tmpResDto.setId(snowflake.nextId());
                            tmpResDto.setAllotType(FOR_MACHINE);
                            tmpResDto.setCargoInfoId(null);
                            //回写前端派机信息 供后端新增使用
                            List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = new ArrayList<>();
                            for (TPrdDispatchSecondaryDTO tmpForDto : v) {
                                TPrdWorkTicketEquipmentPO tPrdWorkTicketEquipmentPO = new TPrdWorkTicketEquipmentPO();
                                tPrdWorkTicketEquipmentPO.setEquipmentId(tmpForDto.getEquipmentId());
                                tPrdWorkTicketEquipmentPO.setEquipmentNo(tmpForDto.getEquipmentNo());
                                tPrdWorkTicketEquipmentPO.setEquipmentTypeCode(tmpForDto.getEquipmentTypeCode());
                                tPrdWorkTicketEquipmentPO.setEquipmentTypeName(tmpForDto.getEquipmentTypeName());
                                tPrdWorkTicketEquipmentPOS.add(tPrdWorkTicketEquipmentPO);
                            }
                            //回写前端派机信息
                            tmpResDto.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(o -> o.getEquipmentId().toString()).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipments(tPrdWorkTicketEquipmentPOS);
                            tmpResDto.setDeptId(v.get(0).getDeptId());
                            tmpResDto.setDeptName(v.get(0).getDeptName());
                            tmpResDto.setProcessDetailCode(v.get(0).getSubProcessCode());
                            tmpResDto.setProcessDetailName(v.get(0).getSubProcessName());
                            tmpResDto.setWorkPositionName(v.get(0).getWorkPositionName());
                            tmpResDto.setWorkPositionCode(v.get(0).getWorkPositionCode());
                            tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                            tmpResDto.setClassCode(workPlanDto.getClassCode());
                            tmpResDto.setClassName(workPlanDto.getClassName());
                            //分配量
                            tmpResDto.setTon(BigDecimal.valueOf(v.size()).multiply(
                                    Optional.of(singleMachine.get(v.get(0).getSubProcessCode()+"_"+v.get(0).getWorkPositionName()+"_"+v.get(0).getDeptId())).orElse(BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));

                            tmpResult.add(tmpResDto);
                        }
                    }
                }
                realResult.addAll(tmpResult);
            //    FOR_LABOR
            //获取签票中人员信息
                List<TPrdWorkTicketDetailDTO> laborTallyInfo = specialList.stream().filter(o -> o.getDeptId() != null).collect(Collectors.toList());
                //过滤固机队
                laborTallyInfo = laborTallyInfo.stream().filter(o -> !"固机队".equals(o.getDeptName())).collect(Collectors.toList());
                if(laborTallyInfo.isEmpty()){

                }else{
                    //过滤无装卸部门
                    laborTallyInfo = laborTallyInfo.stream().filter(o -> !new Long(999999999).equals(o.getDeptId())).collect(Collectors.toList());
                    if(laborTallyInfo.isEmpty()){

                    }else {

                        Map<String, List<TPrdWorkTicketDetailDTO>> resultMap = laborTallyInfo.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" + o.getDeptId()));
                        for (String s : resultMap.keySet()) {
                            List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = resultMap.get(s);
                            if(workTicketDetailDTOS.isEmpty()){
                                continue;
                            }
                            TPrdWorkTicketDetailDTO tmpResDto = new TPrdWorkTicketDetailDTO();
                            tmpResDto.setId(snowflake.nextId());
                            tmpResDto.setAllotType(FOR_LABOR);
                            tmpResDto.setDeptId(workTicketDetailDTOS.get(0).getDeptId());
                            tmpResDto.setDeptName(workTicketDetailDTOS.get(0).getDeptName());
                            tmpResDto.setProcessName(workPlanDto.getProcessName());
                            tmpResDto.setProcessCode(workPlanDto.getProcessCode());
                            tmpResDto.setProcessDetailCode(workTicketDetailDTOS.get(0).getProcessDetailCode());
                            tmpResDto.setProcessDetailName(workTicketDetailDTOS.get(0).getProcessDetailName());
                            tmpResDto.setTon(workTicketDetailDTOS.stream().map(o->Optional.of(o.getTon()).orElseThrow(()->new BusinessRuntimeException("获取理货量失败"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                            tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                            tmpResDto.setClassCode(workPlanDto.getClassCode());
                            tmpResDto.setClassName(workPlanDto.getClassName());
                            specilaResult.add(tmpResDto);
                        }
                    }
                }

                //判断是否存在辅助作业过程
                tmpQuery.setDispatchType(DISP_FOR_LABOR);
                tmpQuery.setWorkPlanId(workTicketDTO.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpLaborSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                tmpLaborSecDispInfo = tmpLaborSecDispInfo.stream().filter(o -> "0".equals(o.getIsTallyCourse())).collect(Collectors.toList());
                if (!tmpLaborSecDispInfo.isEmpty()) {

                    // 结算单人作业单价
                    //拆分每个部分派工多少人，为集合增量做准备，为计算吨数做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> laborMap = tmpLaborSecDispInfo.stream()
                            .collect(Collectors.groupingBy(o->o.getDeptId()+"_"+o.getSubProcessCode()+"_"+o.getWorkPositionName()));
                    Map<String, List<TPrdDispatchSecondaryDTO>> tmpSingleMap = tmpLaborSecDispInfo.stream().collect(Collectors.groupingBy(o -> o.getSubProcessCode()+"_"+o.getWorkPositionName()));

                    //分配签票
                    for (String tmpId : laborMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> tmpLabor = laborMap.get(tmpId);
                        List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = tmpSingleMap.get(tmpLabor.get(0).getSubProcessCode()+"_"+tmpLabor.get(0).getWorkPositionName());
                        BigDecimal signleLabor = allTon.divide(tPrdDispatchSecondaryDTOS.stream().map(o->new BigDecimal(o.getNumberCount())).reduce(BigDecimal.ZERO,BigDecimal::add), 2, BigDecimal.ROUND_HALF_UP);

                        if(tmpLabor.isEmpty()){

                        }else{

                            TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(ticketInfoByPlanId.get(0));
                            tmpResDto.setId(snowflake.nextId());
                            tmpResDto.setCargoInfoId(null);
                            tmpResDto.setAllotType(FOR_LABOR);

                            tmpResDto.setDeptName(tmpLabor.get(0).getDeptParentName());
                            tmpResDto.setDeptId(tmpLabor.get(0).getDeptParentId());
                            tmpResDto.setProcessDetailName(tmpLabor.get(0).getSubProcessName());
                            tmpResDto.setProcessDetailCode(tmpLabor.get(0).getSubProcessCode());
                            tmpResDto.setWorkPositionName(tmpLabor.get(0).getWorkPositionName());
                            tmpResDto.setWorkPositionCode(tmpLabor.get(0).getWorkPositionCode());
                            tmpResDto.setTon(signleLabor.multiply(
                                    tmpLabor.stream().map(tmpLaborItem->Optional.of(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add)
                            ).setScale(2, BigDecimal.ROUND_HALF_UP));
                            tmpResDto.setLaborNumber(
                                    tmpLabor.stream().map(tmpLaborItem->Optional.of(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add)
                            );
                            tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                            tmpResDto.setClassCode(workPlanDto.getClassCode());
                            tmpResDto.setClassName(workPlanDto.getClassName());
                            specilaResult.add(tmpResDto);
                        }
                    }
                }


            realResult.addAll(specilaResult);
        }
        else{

            //正常的分配
            List<TPrdWorkTicketDetailDTO> resultWithoutTicket = new ArrayList<>();
            List<TPrdWorkTicketDetailDTO> resultWithoutTicketForLabor = new ArrayList<>();

            //获取数据库中签票子表
            Map<Long, List<TPrdWorkTicketDetailDTO>> bigSourceMap = ticketInfoByPlanId.stream().collect(Collectors.groupingBy(o -> o.getId()));
            for (Long l : bigSourceMap.keySet()) {
                resultWithoutTicket = new ArrayList<>();
                resultWithoutTicketForLabor = new ArrayList<>();
                List<TPrdWorkTicketDetailDTO> result = bigSourceMap.get(l);
                if (!result.isEmpty()) {
                    //获取签票总量
                    allTon = result.stream().map(o -> Optional.of(o.getTon()).orElseThrow(() -> new BusinessRuntimeException("获取签票量失败"))).reduce(BigDecimal.ZERO, BigDecimal::add);

                    //FOR_MACHINE
                    //获取二次配工中的机械分配情况
                    //获取单类型分配量
                    TPrdTicketSeconAllotQuery tmpQuery = new TPrdTicketSeconAllotQuery();
                    tmpQuery.setDispatchType(DISP_FOR_MACHINE);
                    tmpQuery.setWorkPlanId(workTicketDTO.getWorkPlanId());
                    List<TPrdDispatchSecondaryDTO> tmpSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                    if(tmpSecDispInfo.isEmpty()){

                    }else {

                        //为数组增量做准备
                        Map<String, List<TPrdDispatchSecondaryDTO>> tmpMachineMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o -> o.getDeptId().toString() + "_" + o.getSubProcessCode() + "_" + o.getWorkPositionName() + "_" + o.getEquipmentTypeCode()));
                        //为计算机械单价做准备
                        Map<String, List<TPrdDispatchSecondaryDTO>> machineTypeMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o -> o.getSubProcessCode() + "_" + o.getWorkPositionName()));
                        HashMap<String, BigDecimal> singleMachine = new HashMap<>();
                        for (String tmpId : machineTypeMap.keySet()) {
                            List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = machineTypeMap.get(tmpId);
                            if (!tPrdDispatchSecondaryDTOS.isEmpty()) {
                                singleMachine.put(tmpId,
                                        allTon.divide(Optional.of(BigDecimal.valueOf(tPrdDispatchSecondaryDTOS.size())).orElse(BigDecimal.ZERO), 2, BigDecimal.ROUND_HALF_UP));
                            }
                        }
                        for (String tmpKey : tmpMachineMap.keySet()) {
                            List<TPrdDispatchSecondaryDTO> v = tmpMachineMap.get(tmpKey);
                            if (v.isEmpty()) {
                                //正常情况
                            } else {
                                TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(result.get(0));
                                tmpResDto.setId(snowflake.nextId());
                                tmpResDto.setCargoInfoId(null);
                                tmpResDto.setAllotType(FOR_MACHINE);
                                tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                                tmpResDto.setClassCode(workPlanDto.getClassCode());
                                tmpResDto.setClassName(workPlanDto.getClassName());
                                //回写前端派机信息 供后端新增使用
                                List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = new ArrayList<>();
                                for (TPrdDispatchSecondaryDTO tmpForDto : v) {
                                    TPrdWorkTicketEquipmentPO tPrdWorkTicketEquipmentPO = new TPrdWorkTicketEquipmentPO();
                                    tPrdWorkTicketEquipmentPO.setEquipmentId(tmpForDto.getEquipmentId());
                                    tPrdWorkTicketEquipmentPO.setEquipmentNo(tmpForDto.getEquipmentNo());
                                    tPrdWorkTicketEquipmentPO.setEquipmentTypeCode(tmpForDto.getEquipmentTypeCode());
                                    tPrdWorkTicketEquipmentPO.setEquipmentTypeName(tmpForDto.getEquipmentTypeName());
                                    tPrdWorkTicketEquipmentPOS.add(tPrdWorkTicketEquipmentPO);
                                }
                                //回写前端派机信息
                                tmpResDto.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(o -> o.getEquipmentId().toString()).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipments(tPrdWorkTicketEquipmentPOS);
                                tmpResDto.setDeptId(v.get(0).getDeptId());
                                tmpResDto.setDeptName(v.get(0).getDeptName());
                                tmpResDto.setProcessDetailCode(v.get(0).getSubProcessCode());
                                tmpResDto.setProcessDetailName(v.get(0).getSubProcessName());
                                tmpResDto.setWorkPositionName(v.get(0).getWorkPositionName());
                                tmpResDto.setWorkPositionCode(v.get(0).getWorkPositionCode());
                                //分配量
                                tmpResDto.setTon(BigDecimal.valueOf(v.size()).multiply(Optional.of(singleMachine.get(v.get(0).getSubProcessCode() + "_" + v.get(0).getWorkPositionName())).orElse(BigDecimal.ZERO)).setScale(2, BigDecimal.ROUND_HALF_UP));

                                resultWithoutTicket.add(tmpResDto);
                            }
                        }

                        realResult.addAll(resultWithoutTicket);
                    }
                    //FOR_LABOR
                    tmpQuery.setDispatchType(DISP_FOR_LABOR);
                    tmpQuery.setWorkPlanId(workTicketDTO.getWorkPlanId());
                    List<TPrdDispatchSecondaryDTO> tmpLaborSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                    if (tmpLaborSecDispInfo.isEmpty()) {
//                        throw new BusinessRuntimeException("没有人员配工信息");
                    }else{

                        tmpLaborSecDispInfo.forEach(o -> {
                            if (o.getDeptParentId() == null) {
                                throw new BusinessRuntimeException("装卸队二次配工中缺少部门信息");
                            }
                            o.setTmpDeptId(o.getDeptId());
                            o.setTmpDeptName(o.getDeptName());
                            o.setDeptId(o.getDeptParentId());
                            o.setDeptName(o.getDeptParentName());
                        });
                        if (tmpLaborSecDispInfo.isEmpty()) {

                        } else {
                            // 结算单人作业单价
                            //拆分每个部分派工多少人，为集合增量做准备，为计算吨数做准备
                            Map<String, List<TPrdDispatchSecondaryDTO>> laborMap = tmpLaborSecDispInfo.stream()
                                    .collect(Collectors.groupingBy(o -> o.getDeptId() + "_" + o.getSubProcessCode() + "_" + o.getWorkPositionName()));
                            Map<String, List<TPrdDispatchSecondaryDTO>> tmpSingleMap = tmpLaborSecDispInfo.stream().collect(Collectors.groupingBy(o -> o.getSubProcessCode() + "_" + o.getWorkPositionName()));

                            //分配签票
                            for (String tmpId : laborMap.keySet()) {
                                List<TPrdDispatchSecondaryDTO> tmpLabor = laborMap.get(tmpId);
                                List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = tmpSingleMap.get(tmpLabor.get(0).getSubProcessCode() + "_" + tmpLabor.get(0).getWorkPositionName());
                                BigDecimal signleLabor = allTon.divide(tPrdDispatchSecondaryDTOS.stream().map(o -> new BigDecimal(o.getNumberCount())).reduce(BigDecimal.ZERO, BigDecimal::add), 2, BigDecimal.ROUND_HALF_UP);

                                if (tmpLabor.isEmpty()) {

                                } else {
                                    TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(result.get(0));
                                    tmpResDto.setId(snowflake.nextId());
                                    tmpResDto.setCargoInfoId(null);
                                    tmpResDto.setAllotType(FOR_LABOR);

                                    tmpResDto.setDeptName(tmpLabor.get(0).getDeptName());
                                    tmpResDto.setDeptId(tmpLabor.get(0).getDeptId());
                                    tmpResDto.setProcessDetailName(tmpLabor.get(0).getSubProcessName());
                                    tmpResDto.setProcessDetailCode(tmpLabor.get(0).getSubProcessCode());
                                    tmpResDto.setWorkPositionName(tmpLabor.get(0).getWorkPositionName());
                                    tmpResDto.setWorkPositionCode(tmpLabor.get(0).getWorkPositionCode());
                                    tmpResDto.setTon(signleLabor.multiply(
                                            tmpLabor.stream().map(tmpLaborItem -> Optional.of(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add)
                                    ).setScale(2, BigDecimal.ROUND_HALF_UP));
                                    tmpResDto.setLaborNumber(
                                            tmpLabor.stream().map(tmpLaborItem -> Optional.of(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add)
                                    );
                                    tmpResDto.setWorkDate(workPlanDto.getWorkDate());
                                    tmpResDto.setClassCode(workPlanDto.getClassCode());
                                    tmpResDto.setClassName(workPlanDto.getClassName());
                                    resultWithoutTicketForLabor.add(tmpResDto);
                                }
                            }
                        }
                    }
                    realResult.addAll(resultWithoutTicketForLabor);
                }
            }
        }

        //插入作业票机械数据

        //返回作业票机械子表数据，给作业票子表赋值
        List<TPrdWorkTicketEquipmentPO> equipments = realResult.stream().filter(o->FOR_MACHINE.equals(o.getAllotType())).flatMap(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setWorkTicketId(workTicketDTO.getId());
            v1.setProcessCode(workTicketDTO.getProcessCode());
            v1.setProcessName(workTicketDTO.getProcessName());
            v1.setWorkDate(workTicketDTO.getWorkDate());
            v1.setClassCode(workTicketDTO.getClassCode());
            v1.setClassName(workTicketDTO.getClassName());

            return Optional.ofNullable(v1.getEquipments())
                    .orElse(Collections.emptyList()).stream().filter(v3->v3.getEquipmentId()!=null)
                    .peek(v2 -> {
                v2.setId(snowflake.nextId());
                v2.setWorkTicketDetailId(v1.getId());
            });
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(equipments)) {
            //删除机械数据
            workTicketMapper.delTicketEquipmentByPlanId(workTicketDTO.getWorkPlanId());
            workTicketMapper.insertWorkTicketEquipment(equipments);
        }
        //插入子表数据
            workTicketMapper.insertWorkTicketDetail(realResult);

        long l2 = System.currentTimeMillis();
        System.out.println("自动分配  用时"+(l2-l1));

        //分配计件
        //删除分配计件
//        String planType = workTicketMapper.getWorkPlanType(workTicketDTO.getWorkPlanId());
        workTicketDTO.setDetails(realResult);
        SpringUtils.getBean(this.getClass()).commonUpdateSalary(workTicketDTO,realResult,false,"2".equals(workPlanDto.getPlanType() ),"自动二次分配");
        System.out.println("自动分配 插入计件用时"+(System.currentTimeMillis()-l2));
    }*/

    @Override
    public Map<String, Object> getSummaryQuantityTon(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        Page<TYardTallyItemPO> tallyRecord = tallyMapper.getTallyRecord(tallyRecordSearchDTO);
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();

        if(tallyRecord!=null&&!tallyRecord.isEmpty()){
            stringObjectHashMap.put("quantity",tallyRecord.stream().map(o->Optional.ofNullable(o.getQuantity()).orElse(0)).reduce(0,Integer::sum));
            stringObjectHashMap.put("ton",tallyRecord.stream().map(o->Optional.ofNullable(o.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add));
        }else {
            stringObjectHashMap.put("quantity",null);
            stringObjectHashMap.put("ton",null);
        }
        return stringObjectHashMap;
    }

    @Override
    public List<WorkTicketTableDTO> getWorkTiccketTableV2(WorkTicketTableDTO query){
        List<WorkTicketTableDTO> result = workTicketMapper.getWorkTicketTable(query);
        List<WorkTicketTableDTO> waiFuTicketTable = workTicketMapper.getWaiFuTicketTable(query);
        result.addAll(waiFuTicketTable);
        result = result.stream().sorted(Comparator.comparing(o -> o.getWorkTicketId() + "_" + o.getAllotType() + "_" + o.getDeptName())).collect(Collectors.toList());
        result.forEach(o->{
            if("生产已审核".equals(o.getStatus())){
                o.setStatus(o.getDeptName()+"已审核");
            }
            if("后沿".equals(o.getWorkPositionName())){
                o.setWorkPositionName("后场");
            }
            if(StringUtils.isNotBlank(o.getWorkPositionCode())&& StringUtils.isBlank(o.getWorkPositionName())){
                if("01".equals(o.getWorkPositionCode())){
                    o.setWorkPositionName("前沿");
                }else if("02".equals(o.getWorkPositionCode())){
                    o.setWorkPositionName("后场");
                }else if("03".equals(o.getWorkPositionCode())){
                    o.setWorkPositionName("水平");
                }
            }
            if(FOR_MACHINE.equals(o.getAllotType())) o.setAllotType("机械分配");
            if(FOR_LABOR.equals(o.getAllotType())) o.setAllotType("人员分配");
        });
        return result;
    }

    public List<WorkTicketTableDTO> getWorkTicketDetailList(WorkTicketTableDTO query){
        List<WorkTicketTableDTO> result = workTicketMapper.getWorkTicketTableTask(query);
        List<WorkTicketTableDTO> waiFuTicketTable = workTicketMapper.getWaiFuTicketTableTask(query);

        result.addAll(waiFuTicketTable);
        result = result.stream().sorted(Comparator.comparing(o -> o.getWorkTicketId() + "_" + o.getAllotType() + "_" + o.getDeptName())).collect(Collectors.toList());
        result.forEach(o->{
            if("生产已审核".equals(o.getStatus())){
                o.setStatus(o.getDeptName()+"已审核");
            }
            if("后沿".equals(o.getWorkPositionName())){
                o.setWorkPositionName("后场");
            }
            if(StringUtils.isNotBlank(o.getWorkPositionCode())&& StringUtils.isBlank(o.getWorkPositionName())){
                if("01".equals(o.getWorkPositionCode())){
                    o.setWorkPositionName("前沿");
                }else if("02".equals(o.getWorkPositionCode())){
                    o.setWorkPositionName("后场");
                }else if("03".equals(o.getWorkPositionCode())){
                    o.setWorkPositionName("水平");
                }
            }
            if(FOR_MACHINE.equals(o.getAllotType())) o.setAllotType("机械分配");
            if(FOR_LABOR.equals(o.getAllotType())) o.setAllotType("人员分配");
        });
        return result;
    }

    @Override
    public List<Map<String,String>> getDaoYunWeightTable(WorkTicketTableDTO query){

        //查当前登陆人的二级部门
        SysDeptDTO sysDeptDTO = workTicketMapper.getByLoginUserId(securityUtils.getLoginUserId());
        if(sysDeptDTO != null && "外部".equals(sysDeptDTO.getInOutTypeLabel()) && sysDeptDTO.getId() !=9999999999L ){
            query.setDeptId(sysDeptDTO.getId());
        }

        List<Map<String,String>> result = workTicketMapper.getDaoYunWeightTable(query);
        return result;
    }

    @Override
    public List<Map<String,String>> getSettlementStatistics(Map<String,Object> map){
//        List<Map<String,String>> result = workTicketMapper.getSettlementStatistics(map);
//        if(){
//
//        }
//        map.get("workPositionCodeList").
        List<Map<String,String>> result = Lists.newArrayList();

        SysDeptDTO sysDeptDTO = workTicketMapper.getInOutTypeByLoginUserId(securityUtils.getLoginUserId());
        if(sysDeptDTO != null && "外部".equals(sysDeptDTO.getInOutTypeLabel())){
            //查当前登陆人的一级部门
            Map<String, Object> userDept  = workTicketMapper.getDeptLevel1InfoByUserId(securityUtils.getLoginUserId());
            if (userDept != null && Long.parseLong(String.valueOf(userDept.get("deptId"))) != 9999999999L) {
                map.put("mainDeptId", userDept.get("deptId"));
            }
        }

        //一部分查外付汇总
        List<Map<String,String>> wfResult = workTicketMapper.getSettlementWFStatistics(map);

        //查倒运汇总
        List<Map<String,String>> dyResult = workTicketMapper.getSettlementDYStatistics(map);
        for (Map<String, String> dyMap : dyResult) {
            for (Map<String, String> wfMap : wfResult) {
                if(String.valueOf(dyMap.get("outwardTypeCode")).equals(String.valueOf(wfMap.get("outwardTypeCode")))
                        && String.valueOf(dyMap.get("workPositionCode")).equals(String.valueOf(wfMap.get("workPositionCode")))
                        && String.valueOf(dyMap.get("deptId")).equals(String.valueOf(wfMap.get("deptId")))
                ){
                    System.out.println("nice");
                    wfMap.put("weightTon",dyMap.get("weightTon"));
                    break;
                }
            }
        }
        return wfResult;
    }

    public Boolean updateDaoYunWeightStatus(List<TPrdDySumDTO> query,String settlementDate) {
        query.stream().forEach(e->dySumService.doSave(e));
        saveStatementOrder(query,settlementDate);
        return true;
    }

    public Boolean updateSettlementStatus(List<Map<String,Object>> query,String settlementDate,String type) {
//        query.stream().forEach(e->dySumService.doSave(e)); //distributeTypeName
        if(CollectionUtils.isEmpty(query)){
            throw new BusinessRuntimeException("没有需要审核的数据");
        }
        List<Long> list = Lists.newArrayList();
        for (Map<String, Object> stringObjectMap : query) {
            for (String id : Arrays.asList(String.valueOf(stringObjectMap.get("ids")).split(","))) {
                list.add(Long.valueOf(id));
            }
        }
//        Map<String,Object> objectMap = Maps.newHashMap();
//        objectMap.put("ddStatus",query.get(0).get("ddStatus"));
//        objectMap.put("kcStatus",query.get(0).get("kcStatus"));
//        objectMap.put("lzStatus",query.get(0).get("lzStatus"));
//        objectMap.put("wfStatus",query.get(0).get("wfStatus"));
//        objectMap.put("startDate",cn.hutool.core.date.DateUtil.beginOfMonth(DateUtils.parseDate(settlementDate,"yyyy-MM-dd HH:mm:ss")));
//        objectMap.put("endDate",cn.hutool.core.date.DateUtil.endOfMonth(DateUtils.parseDate(settlementDate,"yyyy-MM-dd HH:mm:ss")));
//        query.stream().
//        Map<String, List<TPrdDispatchDTO>> dispatchMap = new HashMap<>();
//        //回显配机列表
//        if (!org.springframework.util.CollectionUtils.isEmpty(dispatchList)){
//            dispatchMap = dispatchList.stream().collect(Collectors.groupingBy(o -> {
//                return o.getWorkPlanId() + "/" + o.getDispatchType() + "/" + o.getWorkPositionCode();
//            }));
//        }
        Map<String,List<Map<String,Object>>> dispatchMap = query.stream().collect(Collectors.groupingBy(o->String.valueOf(o.get("distributeTypeName"))));
//        分配类型：3人员，2机械
        String distributeType = "";
        if(dispatchMap.size()==1){
            distributeType = String.valueOf(query.get(0).get("distributeTypeName")).contains("人员")?"3":"2";
        }
//        WfSettlementInsertDto tmpResult = workTicketMapper.getWfSettkementList(distributeType, DateUtils.parseDate(settlementDate,"yyyy-MM-dd hh24:mi:ss"));
        if("dd".equals(type)){
            if("1".equals(String.valueOf(query.get(0).get("ddStatus")))){
                workTicketMapper.updateSettlementStatusLevel1("1",distributeType, StringUtils.substring(settlementDate,0,7),
                        securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),new Date());
            }else{
                workTicketMapper.updateSettlementStatusLevel1("0",distributeType, StringUtils.substring(settlementDate,0,7),
                        null,null,null);
            }

        }else
        if("kc".equals(type)){
            if("1".equals(String.valueOf(query.get(0).get("kcStatus")))){
                workTicketMapper.updateSettlementStatusLevel2("1",distributeType, StringUtils.substring(settlementDate,0,7),
                        securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),new Date());
            }else{
                workTicketMapper.updateSettlementStatusLevel2("0",distributeType, StringUtils.substring(settlementDate,0,7),
                        null,null,null);
            }
        }else
        if("lj".equals(type)||"zx".equals(type)){
            if("1".equals(String.valueOf(query.get(0).get("lzStatus")))){
                workTicketMapper.updateSettlementStatusLevel31("1",distributeType,StringUtils.substring(settlementDate,0,7),
                        securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),new Date(),list);
            }else{
                workTicketMapper.updateSettlementStatusLevel31("0",distributeType, StringUtils.substring(settlementDate,0,7),
                        null,null,null,list);
            }
        }else
        if("wf".equals(type)){
            if("1".equals(String.valueOf(query.get(0).get("wfStatus")))){
                workTicketMapper.updateSettlementStatusLevel41("1",distributeType, StringUtils.substring(settlementDate,0,7),
                        securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),new Date(),list);
            }else{
                workTicketMapper.updateSettlementStatusLevel41("0",distributeType, StringUtils.substring(settlementDate,0,7),
                        null,null,null,list);
            }

        }

//        workTicketMapper.updateSettlementStatus(
//                String.valueOf(query.get(0).get("ddStatus")),
//                String.valueOf(query.get(0).get("kcStatus")),
//                String.valueOf(query.get(0).get("lzStatus")),
//                String.valueOf(query.get(0).get("wfStatus")),
//                cn.hutool.core.date.DateUtil.beginOfMonth(DateUtils.parseDate(settlementDate,"yyyy-MM-dd HH:mm:ss")),
//                cn.hutool.core.date.DateUtil.endOfMonth(DateUtils.parseDate(settlementDate,"yyyy-MM-dd HH:mm:ss")),
//                distributeType
//        );
        return true;
    }

    @Override
    public List<WorkTicketTableDTO> getWfHrNewTable(WorkTicketTableDTO query) {
        return workTicketMapper.getWfHrNewTableList(query);
    }


    /**
     * 保存结算单，当倒运过磅汇总    hr审核后
     */
    public void saveStatementOrder(List<TPrdDySumDTO> query,String settlementDate){
        //判断是审核还是撤销
        Boolean flag = query.size () == (int) query.stream().filter(person -> "1".equals(person.getHrStatus())).count();
        if(flag){
            //删除已经存在的倒运过磅汇总
            tPrdWorkTicketNewMapper.deleteDySettlement(settlementDate.substring(0,7));
            //保存新的倒运过磅汇总
            ArrayList<WfSettlementInsertDto> list = Lists.newArrayList();
            query.stream().forEach(e->{
                WfSettlementInsertDto settlementInsertDto = new WfSettlementInsertDto();
                settlementInsertDto.setId(snowflake.nextId());
                settlementInsertDto.setDistributeType("2");//倒运都是机械，2机械，3人员
                settlementInsertDto.setSettlementDate(DateUtils.parseDate(settlementDate,"yyyy-MM-dd HH:mm:ss"));
                settlementInsertDto.setIsDaoyun("1");
                settlementInsertDto.setDeptId(Long.parseLong(e.getDeptId()));
                settlementInsertDto.setDeptName(e.getDeptName());
                settlementInsertDto.setOutwardTypeCode(e.getOutwardType());
                settlementInsertDto.setOutwardTypeName(e.getOutwardTypeName());
                settlementInsertDto.setWorkPositionCode("03");
                settlementInsertDto.setWorkPositionName("水平");
                settlementInsertDto.setMechanicalType("0");//是否是行吊
                settlementInsertDto.setWorkTon(BigDecimal.ZERO);
                settlementInsertDto.setWeightTon(new BigDecimal(e.getTon()));
                settlementInsertDto.setDdStatus("0");
                settlementInsertDto.setKcStatus("0");
                settlementInsertDto.setLzStatus("0");
                settlementInsertDto.setWfStatus("0");
                list.add(settlementInsertDto);
            });
            tPrdWorkTicketNewMapper.insertWfSettlementBatch(list);
        }else{
            tPrdWorkTicketNewMapper.deleteDySettlement(settlementDate.substring(0,7));
        }
    }


    /**
     * v.1
     * @param query
     * @return
     */
    @Override
    public List<WorkTicketTableDTO> getWorkTiccketTable(WorkTicketTableDTO query) {
        if(query.getStartTime()==null){
            throw new BusinessRuntimeException("请先选择开始时间");
        }
        if(query.getEndTime()==null){
            throw new BusinessRuntimeException("请先选择结束时间");
        }
        List<WorkTicketTableDTO> resultFront = workTicketMapper.getWorkTiccketTableMain(query);

        //获取辅助计划签票；
        List<WorkTicketTableDTO>  fuZhuMain= workTicketMapper.getFuZhuMain(query);
        List<WorkTicketTableDTO> fuZhuBack =null;
        if(resultFront.isEmpty()&& fuZhuMain.isEmpty()){
            return Collections.emptyList();
        }
        List<WorkTicketTableDTO> workTiccketTableBack = new ArrayList<>();
        List<Long> daoYunList;
        if(!resultFront.isEmpty()){
            //获取倒运的签票id
            daoYunList = workTicketMapper.getDaoYunList(resultFront.stream().map(WorkTicketTableDTO::getWorkTicketId).filter(Objects::nonNull).distinct().collect(Collectors.toList()));

            workTiccketTableBack = workTicketMapper.getWorkTiccketTableBack(resultFront.stream().map(WorkTicketTableDTO::getWorkTicketId).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        } else {
            daoYunList = Collections.emptyList();
        }

        if(fuZhuMain.isEmpty()){
        }else{
            resultFront.addAll(fuZhuMain);
            fuZhuBack =  workTicketMapper.getFuZhuMainBack(fuZhuMain.stream().map(WorkTicketTableDTO::getWorkTicketId).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
            workTiccketTableBack.addAll(fuZhuBack);
        }
        //直取的
        List<Long> collect2 = resultFront.stream().filter(v ->"车/岸(前沿直取)".equals(v.getProcessName())|| "1".equals(v.getIsDirectAccess())).map(v -> v.getWorkTicketId()).collect(Collectors.toList());
        List<Long> collect3 = resultFront.stream().filter(v ->  "车/场(集港)".equals(v.getProcessName())||"疏港".equals(v.getTrustType())).map(v -> v.getWorkTicketId()).collect(Collectors.toList());
        workTiccketTableBack.forEach(o->{
            if(StringUtils.isEmpty( o.getWorkPositionCode())){
                if(collect3.stream().anyMatch(v->v.equals(o.getWorkTicketId()))){
                    o.setWorkPositionCode("02");
                    o.setWorkPositionName("后场");
                }
                if(collect2.stream().anyMatch(v->v.equals(o.getWorkTicketId()))) {
                    o.setWorkPositionCode("01");
                    o.setWorkPositionName("前沿");
                }
            }
        });
        Map<String, List<WorkTicketTableDTO>> collect = workTiccketTableBack.stream().collect(Collectors.groupingBy(o -> o.getWorkTicketId() + "_" + o.getAllotType()+"_"+o.getProcessDetailCode()));

        List<WorkTicketTableDTO> tmpList = new ArrayList<>();
        resultFront.forEach(v1->{
            if(collect.get(v1.getWorkTicketId()+"_"+v1.getAllotType()+"_"+v1.getProcessDetailCode())!=null){
                List<WorkTicketTableDTO> workTicketTableDTOS = collect.get(v1.getWorkTicketId() + "_" + v1.getAllotType()+"_"+v1.getProcessDetailCode());
                if(workTicketTableDTOS.isEmpty()){

                }else{
                    Map<String, List<WorkTicketTableDTO>> collect1 =
                            workTicketTableDTOS.stream()
                                    .collect(Collectors.groupingBy(tmp -> String.valueOf(tmp.getWorkPositionCode())));
                    //  获取填充索引值
                    if(!collect1.isEmpty()){
                        List<WorkTicketTableDTO> maxTickets = new ArrayList<>();

                        int maxSize=0;
                        String positionFlag="";
                        for (String s : collect1.keySet()) {
                            List<WorkTicketTableDTO> v = collect1.get(s);
                            if(v.size()>maxSize){
                                maxSize= v.size();
                                maxTickets = v;
                                positionFlag = String.valueOf(v.get(0).getWorkPositionCode());
                            }
                        }

                        for (WorkTicketTableDTO workTicketTableDTO : maxTickets) {
                            workTicketTableDTO.setGroupId(v1.getWorkTicketId().toString());
                            workTicketTableDTO.setTrustNo(v1.getTrustNo());
                            workTicketTableDTO.setWorkTon(v1.getWorkTon());
                            workTicketTableDTO.setWorkDate(v1.getWorkDate());
                            workTicketTableDTO.setClassName(v1.getClassName());
                            workTicketTableDTO.setCargoName(v1.getCargoName());
                            workTicketTableDTO.setCargoInfoNo(v1.getCargoInfoNo());
                            workTicketTableDTO.setAllotType(v1.getAllotType());
                            workTicketTableDTO.setPackingName(v1.getPackingName());
                            workTicketTableDTO.setPackingNameWaiBao(v1.getPackingNameWaiBao());
                            workTicketTableDTO.setProcessName(v1.getProcessName());
                            workTicketTableDTO.setShipvoyage(v1.getShipvoyage());
                            workTicketTableDTO.setScn(v1.getScn());

                            if("01".equals(workTicketTableDTO.getWorkPositionCode())){
                                workTicketTableDTO.setFrontWorkTon(workTicketTableDTO .getTicketTon());
                                workTicketTableDTO.setFrontDeptName( workTicketTableDTO.getDeptName());
                                workTicketTableDTO.setFrontEquipmentTypeName(workTicketTableDTO.getEquipmentTypeName());
                            }else if("02".equals(workTicketTableDTO.getWorkPositionCode())){
                                workTicketTableDTO.setBackWorkTon(workTicketTableDTO.getTicketTon());
                                workTicketTableDTO.setBackDeptName(workTicketTableDTO.getDeptName());
                                workTicketTableDTO.setBackEquipmentTypeName(workTicketTableDTO.getEquipmentTypeName());
                            }else if("03".equals(workTicketTableDTO.getWorkPositionCode())){
                                workTicketTableDTO.setLineWorkTon(workTicketTableDTO.getTicketTon());
                                workTicketTableDTO.setLineDeptName(workTicketTableDTO.getDeptName());
                                workTicketTableDTO.setLineEquipmentTypeName(workTicketTableDTO.getEquipmentTypeName());
                            }else{
                                workTicketTableDTO.setOtherWorkTon(workTicketTableDTO.getTicketTon());
                                workTicketTableDTO.setOtherDeptName(workTicketTableDTO.getDeptName());
                                workTicketTableDTO.setOtherEquipmentTypeName(workTicketTableDTO.getEquipmentTypeName());
                            }
                        }
                        //查询倒运过磅数;
//                        if(!daoYunList.isEmpty()){
//                            if(daoYunList.stream().anyMatch(amp->amp.equals(v1.getWorkTicketId()))){
//                                //判断是否已经赋值过了
//                                if(!tmpList.isEmpty() && tmpList.stream().noneMatch(tmp -> tmp.getWorkTicketId().equals(v1.getWorkTicketId()) && tmp.getDaoYunWeightGoods() != null)){
//                                    //写根据签票的id找作业线id ，根据作业线的班次判断时间范围，然后根据时间范围和作业线关联的计划号找计划上的票货的过磅的标志，取查地磅
//                                    List<DaoYunWeightGoodsDTO> tmpDaoyun = workTicketMapper.getPlanByTicket(v1.getWorkTicketId());
//                                    if(tmpDaoyun.isEmpty()){
//                                        log.error("外付报表：根据签票id查作业线数据获取作业线的开始结束时间失败，作业线id:{}",v1.getWorkTicketId());
//                                    }else{
//                                        maxTickets.get(0).setDaoYunWeightGoods(workTicketMapper.getDaoYunWeightGoods(tmpDaoyun.get(0)));
//                                    }
//                                }
//
//                            }
//                        }
                        for (String s : collect1.keySet()) {
                            if(!s.equals(positionFlag)){
                                List<WorkTicketTableDTO> otherCollection = collect1.get(s);
                                for (int i = 0; i < otherCollection.size(); i++) {
                                    if("01".equals(otherCollection.get(i).getWorkPositionCode())){
                                        maxTickets.get(i).setFrontWorkTon(otherCollection.get(i).getTicketTon());
                                        maxTickets.get(i).setFrontDeptName(otherCollection.get(i).getDeptName());
                                        maxTickets.get(i).setFrontEquipmentTypeName(otherCollection.get(i).getEquipmentTypeName());
                                    }else if("02".equals(otherCollection.get(i).getWorkPositionCode())){
                                        maxTickets.get(i).setBackWorkTon(otherCollection.get(i).getTicketTon());
                                        maxTickets.get(i).setBackDeptName(otherCollection.get(i).getDeptName());
                                        maxTickets.get(i).setBackEquipmentTypeName(otherCollection.get(i).getEquipmentTypeName());
                                    }else if("03".equals(otherCollection.get(i).getWorkPositionCode())){
                                        maxTickets.get(i).setLineWorkTon(otherCollection.get(i).getTicketTon());
                                        maxTickets.get(i).setLineDeptName(otherCollection.get(i).getDeptName());
                                        maxTickets.get(i).setLineEquipmentTypeName(otherCollection.get(i).getEquipmentTypeName());
                                    }else{
                                        maxTickets.get(i).setOtherWorkTon(otherCollection.get(i).getTicketTon());
                                        maxTickets.get(i).setOtherDeptName(otherCollection.get(i).getDeptName());
                                        maxTickets.get(i).setOtherEquipmentTypeName(otherCollection.get(i).getEquipmentTypeName());
                                    }
                                }
                            }
                        }
                        tmpList.addAll(maxTickets);
                    }
                }
            }
        });
        tmpList.forEach(o->{
            if(FOR_MACHINE.equals(o.getAllotType())) o.setAllotType("机械分配");
            if(FOR_LABOR.equals(o.getAllotType())) o.setAllotType("人员分配");
        });
        return tmpList;
    }

    /**
     * 外付报表导出
     * @param query
     * @return
     */
    @Override
    public byte[] exportWaiFuTableExcel(WorkTicketTableDTO query) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        List<WorkTicketTableDTO> list = SpringUtils.getBean(this.getClass()).getWorkTiccketTable(query);
        List<CellRangeAddress> addressList = new ArrayList<>();
//        list = list.stream().sorted(Comparator.comparing(o ->o.getWorkDate() + "_" + o.getClassCode()+"_"+o.getShipvoyage())).collect(Collectors.toList());
        for (int i = 1; i <= list.size(); i++) {
            list.get(i-1).setSortNum(i);
        }
        Map<String, List<WorkTicketTableDTO>> collect = list.stream().collect(Collectors.groupingBy(o -> o.getGroupId()));
        collect.forEach((k,v)->{
            if(v.size()>1){
                ArrayList<Integer> integers = new ArrayList<>();
                v.forEach(o->{
                    integers.add(o.getSortNum());
                });
                addressList.add(new CellRangeAddress(Collections.min(integers),Collections.max(integers),11,11));
            }
        });

        try (ExcelWriter excelWriter = EasyExcel.write(os, WorkTicketTableDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0")
                    .registerWriteHandler(new SheetWriteHandler() {
                @Override
                public void afterSheetCreate(SheetWriteHandlerContext context) {
                    Sheet sheet = context.getWriteSheetHolder().getSheet();
                    for (CellRangeAddress cellAddresses : addressList) {
                        sheet.addMergedRegion(cellAddresses);
                    }
                    SheetWriteHandler.super.afterSheetCreate(context);
                }
            }).build();
            writeSheet.setUseDefaultStyle(false);
            List<WorkTicketTableDTO> finalList = list;
            transactionTemplate.executeWithoutResult(status -> {
                try  {

                    Iterator<WorkTicketTableDTO> iterator = finalList.iterator();
                    if (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            List<WorkTicketTableDTO> salarys = new ArrayList<>();
                            for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                                salarys.add(iterator.next());
                            }
                            excelWriter.write(salarys, writeSheet);
                        }
                    } else {
                        excelWriter.write(Collections.emptyList(), writeSheet);
                    }

                } catch (Exception e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }
}
