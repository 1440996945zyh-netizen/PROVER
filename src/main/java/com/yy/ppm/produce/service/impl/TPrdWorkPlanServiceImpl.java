package com.yy.ppm.produce.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import com.google.api.client.util.Lists;
import com.yy.common.enums.CommonEnum;
import com.yy.common.util.DateUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.SpringUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.businessKH.mapper.CargoInfoMapper;
import com.yy.ppm.common.bean.dto.ResponsePopupTrustDTO;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.enums.DirectionEnum;
import com.yy.ppm.common.enums.DispatchEnum;
import com.yy.ppm.common.enums.MachineLocationEnum;
import com.yy.ppm.common.mapper.SelectMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.PublicService;
import com.yy.ppm.common.service.SelectService;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationDTO;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationSearchDTO;
import com.yy.ppm.dispatch.bean.po.TDisShipDaynigttplanPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.mapper.TDisShipVoyageMapper;
import com.yy.ppm.dispatch.service.TBusTrustLocationService;
import com.yy.ppm.produce.bean.dto.*;
import com.yy.ppm.produce.bean.po.TPrdWorkPlanPO;
import com.yy.ppm.produce.mapper.*;
import com.yy.ppm.produce.service.TPrdDispatchSecondaryService;
import com.yy.ppm.produce.service.TPrdWorkPlanService;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 作业计划表(TPrdWorkPlan)ServiceImpl
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
@Service
public class TPrdWorkPlanServiceImpl implements TPrdWorkPlanService {

    @Resource
    PublicService publicService;
    @Resource
    private TBusTrustMapper tBusTrustMapper;
    @Resource
    private TBusTrustLocationService tBusTrustLocationService;
    @Resource
    private SelectMapper selectMapper;

    @Resource
    private TPrdWorkPlanMapper tPrdWorkPlanMapper;

    @Resource
    private TPrdWorkPlanLocationMapper tPrdWorkPlanLocationMapper;

    @Resource
    private TPrdWorkPlanTrustMapper tPrdWorkPlanTrustMapper;

    @Resource
    private TPrdDispatchMapper tPrdDispatchMapper;

    @Resource
    private TPrdDispatchSecondaryMapper secondaryMapper;

    @Resource
    private SelectService selectService;

    @Resource
    private CommonService commonService;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;
    @Resource
    private CargoInfoMapper cargoInfoMapper;
    @Resource
    private TDisShipVoyageMapper tDisShipVoyageMapper;
    @Resource
    private SysParameterMapper sysParameterMapper;
    /**
     * 获取工班计划列表(可编辑列表用）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<TPrdWorkPlanDTO> getWorkPlanList(TPrdWorkPlanSearchDTO searchDTO) {

        List<TPrdWorkPlanDTO> workPlanList = new ArrayList<>();
        if(searchDTO.getJSGtemp()!= null && searchDTO.getJSGtemp().equals("1")){
             workPlanList = tPrdWorkPlanMapper.getJSGWorkPlanList(searchDTO);
        }else{
             workPlanList = tPrdWorkPlanMapper.getWorkPlanList(searchDTO);
        }

//         List<TPrdWorkPlanDTO> workPlanList = tPrdWorkPlanMapper.getWorkPlanList(searchDTO);

  /*      List<TPrdWorkPlanDTO> workPlanList =collectList.stream().filter(dto ->
                dto.getStatementStatusList().isEmpty() || dto.getStatementStatusList().stream().anyMatch(status ->  !"30".equals(status))
        ).collect(Collectors.toList());*/


        List<Long> ids = workPlanList.stream().map(TPrdWorkPlanDTO::getId).collect(Collectors.toList());
        searchDTO.setIds(ids);
        if (CollectionUtils.isEmpty(ids)){
            return  workPlanList;
        }
        //配机配工
        List<TPrdDispatchDTO> dispatchList = tPrdWorkPlanMapper.getPrdDispatch(searchDTO);
        Map<String, List<TPrdDispatchDTO>> dispatchMap = new HashMap<>();
        //回显配机列表
        if (!CollectionUtils.isEmpty(dispatchList)){
            dispatchMap = dispatchList.stream().collect(Collectors.groupingBy(o -> {
                return o.getWorkPlanId() + "/" + o.getDispatchType() + "/" + o.getWorkPositionCode();
            }));
        }
        HashMap<String, String> machineStrins = new HashMap<>();
        if(!CollectionUtils.isEmpty(dispatchMap)){
            dispatchMap.forEach((k,v)->{
                String s = "";
                if(!CollectionUtils.isEmpty(v)){

                    for (int i = 0; i < v.size(); i++) {
                        if(i==0){
                            s = v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                        }else {
                            s+=","+v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                        }
                    }

                }
                machineStrins.put(k,s);
            });
        }



        //  位置
        List<TPrdWorkPlanLocationDTO> workPlanLocationList = tPrdWorkPlanMapper.getWorkPlanLocationList(searchDTO);
        Map<String, List<String>> tmpLoctionMap = workPlanLocationList.stream().collect(Collectors.groupingBy(
                o ->  o.getWorkPlanId() + "/" + o.getDirection(),
                Collectors.mapping(o->o.getStorehouseName()+"/"+o.getRegionName()+"/"+o.getMassName(),
                Collectors.toList())));

        HashMap<String, String> locationNameMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(tmpLoctionMap)){
            tmpLoctionMap.forEach((k,v)->{
                String massNames = "";
                if(!CollectionUtils.isEmpty(v)){
                    for (int i = 0; i < v.size(); i++) {
                        if(i==0){
                            massNames = v.get(i);
                        }else {
                            massNames += " , " +v.get(i);
                        }
                    }
                }
                locationNameMap.put(k,massNames);
            });
        }

        //货主货代
        List<Long> searchTrustIds = workPlanList.stream().map(TPrdWorkPlanDTO::getTrustId).collect(Collectors.toList());
        searchDTO.setIds(searchTrustIds);
        List<TBusTrustCargoDTO> trustCargoList = tPrdWorkPlanMapper.getTrustCargoList(searchDTO);
        Map<Long, List<TBusTrustCargoDTO>> trustCargoMap =
                trustCargoList.stream().collect(Collectors.groupingBy(TBusTrustCargoDTO::getTrustId));
        //根据票货信息获取货主货代
        List<Long> cargoInfoIds = workPlanList.stream().map(TPrdWorkPlanDTO::getBusCargoInfoId).collect(Collectors.toList());
        List<TBusCargoInfoDTO> cargoInfoDTOS = tPrdWorkPlanMapper.getCargoInfoByIds(cargoInfoIds);
        Map<Long, List<TBusCargoInfoDTO>> cargoInfoMap =
                cargoInfoDTOS.stream().collect(Collectors.groupingBy(TBusCargoInfoDTO::getId));

        //  01 前沿 02 后场  03 水平 04 辅助
        for (TPrdWorkPlanDTO o : workPlanList) {

            if(!CollectionUtils.isEmpty(dispatchMap)){
                //前沿
                if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/01")))){
                    o.setEqumentListFront(dispatchMap.get((o.getId()+"/"+"1/01")));
                    o.setEquipmentNamesFront(machineStrins.get((o.getId()+"/"+"1/01")));
                }
                //后沿
                if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/02")))){
                    o.setEqumentListBack(dispatchMap.get((o.getId()+"/"+"1/02")));
                    o.setEquipmentNamesBack(machineStrins.get((o.getId()+"/"+"1/02")));

                }
                //水平
                if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/03")))){
                    o.setEqumentListReshipment(dispatchMap.get((o.getId()+"/"+"1/03")));
                    o.setEquipmentNamesReshipment(machineStrins.get((o.getId()+"/"+"1/03")));

                }
                //辅助
                if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/04")))){
                    o.setEqumentListAssist(dispatchMap.get((o.getId()+"/"+"1/04")));
                    o.setEquipmentNamesAssist(machineStrins.get((o.getId()+"/"+"1/04")));
                }
                //装卸队
                if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"2/null")))){
                    o.setLaborNum(dispatchMap.get((o.getId()+"/"+"2/null")).get(0).getNum());
                }
            }

            //场地
            if (!CollectionUtils.isEmpty(locationNameMap)){
                //前沿
                if(!StringUtils.isEmpty(locationNameMap.get((o.getId()+"/1")))
                ){
                    o.setMassNamesSource(locationNameMap.get((o.getId()+"/1")));
                }//后沿
                if(!StringUtils.isEmpty(locationNameMap.get((o.getId()+"/2")))){
                    o.setMassNamesTarget(locationNameMap.get((o.getId()+"/2")));
                }
            }
            if(!CollectionUtils.isEmpty(trustCargoMap)){
                if(!CollectionUtils.isEmpty(trustCargoMap.get(o.getTrustId()))){
                    List<TBusTrustCargoDTO> dtos = trustCargoMap.get(o.getTrustId());
                    if(!CollectionUtils.isEmpty(dtos)){
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
                } else if(!CollectionUtils.isEmpty(cargoInfoMap.get(o.getBusCargoInfoId()))){
                    List<TBusCargoInfoDTO> cargoInfoDTOList = cargoInfoMap.get(o.getBusCargoInfoId());
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
                        TBusCargoInfoDTO cargoInfoDTO = cargoInfoDTOList.get(i);
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
        if(CollectionUtils.isEmpty(workPlanList)){
            return workPlanList;
        }
        //集疏港计划回显船名航次
        if("2".equals(searchDTO.getPlanType())){

            List<Long> trustIdList = workPlanList.stream().map(TPrdWorkPlanDTO::getTrustId).collect(Collectors.toList());
            List<ResponsePopupTrustDTO> isClearList= tBusTrustMapper.getIsClearInfoByTrustIds(trustIdList);

            List<Long> clearIds = isClearList.stream().filter(o -> o.getIdCount() - o.getClearNumber() == 0).collect(Collectors.toList()).stream().map(ResponsePopupTrustDTO::getId).collect(Collectors.toList());

            if("1".equals(searchDTO.getIsClear())){
                workPlanList = workPlanList.stream().filter(o -> clearIds.contains(o.getTrustId())).collect(Collectors.toList());
            }
            if("0".equals(searchDTO.getIsClear())){
                workPlanList = workPlanList.stream().filter(o -> !clearIds.contains(o.getTrustId())).collect(Collectors.toList());
            }

            if(CollectionUtils.isEmpty(workPlanList)){
                return workPlanList;
            }

            List<Long> trustIds = workPlanList.stream().filter(v1 -> v1.getShipvoyageItemId() == null).map(TPrdWorkPlanDTO::getTrustId).collect(Collectors.toList());
            if (!trustIds.isEmpty()) {
                List<Map<String, Object>> shipvoyageItems = tBusTrustMapper.listShipvoyageItemByTrustIds(trustIds);
                workPlanList.stream().filter(v1 -> trustIds.contains(v1.getTrustId()))
                        .forEach(v1 -> {
                            String shipNameVoyages = shipvoyageItems.stream()
                                    .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                    .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                    .collect(Collectors.joining("，"));
                            v1.setShipvoyageLabel(shipNameVoyages);
                            String shipVoyageIds = shipvoyageItems.stream()
                                    .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                    .map(v2 -> String.valueOf(v2.get("shipvoyageItemId")))
                                    .collect(Collectors.joining("，"));
                            v1.setShipVoyageIds(shipVoyageIds);

                        });
            }
            List<TPrdWorkPlanDTO> resultList = new ArrayList<>(workPlanList.size());


            if(!StringUtils.isEmpty(searchDTO.getShipName())){
                for (TPrdWorkPlanDTO dto : workPlanList) {
                    if(!StringUtils.isEmpty(dto.getShipvoyageLabel())){
                        String[] parts = dto.getShipvoyageLabel().split("_");
                        if (parts.length > 1) {
                            String beforeUnderscore = parts[0];
                            if (beforeUnderscore.contains(searchDTO.getShipName())) {
                                resultList.add(dto);
                            }
                        }
                    }
                }
                workPlanList = resultList;
            }
            if(!StringUtils.isEmpty(searchDTO.getVoyage())){
                resultList =new ArrayList<>();
                for (TPrdWorkPlanDTO dto : workPlanList) {
                    if(!StringUtils.isEmpty(dto.getShipvoyageLabel())){
                        String[] parts = dto.getShipvoyageLabel().split("_");
                        if (parts.length > 1) {
                            String afterUnderscore = parts[1];
                            if (afterUnderscore.contains(searchDTO.getVoyage())) {
                                resultList.add(dto);
                            }
                        }
                    }
                }
                workPlanList = resultList;
            }

            //集疏港申请 没有作业过程的时候  根据通知单类型获取作业计划过程
            //获取集疏港计划类型
            List<Map<String, String>> mapList = tPrdWorkPlanMapper.workProcessType(2L);
            workPlanList.stream().forEach(o->{
                if(StringUtil.isEmpty(o.getProcessCode())){
                    TBusTrustDTO tmpTrust = tBusTrustMapper.getById(o.getTrustId());
                    //疏港 源是场
                    if("疏港".equals(tmpTrust.getType())){
                        for (Map<String, String> item : mapList) {
                            if("05".equals(String.valueOf(item.get("sourceCd")))){
                                o.setProcessCode(String.valueOf(item.get("value")));
                                o.setProcessName(String.valueOf(item.get("label")));
                                break;
                            }
                        }
                    }
                    //集港目的是场
                    if("集港".equals(tmpTrust.getType()) || "拆箱集港".equals(tmpTrust.getType())){
                        for (Map<String, String> item : mapList) {
                            if("05".equals(String.valueOf(item.get("targetCd")))){
                                o.setProcessCode(String.valueOf(item.get("value")));
                                o.setProcessName(String.valueOf(item.get("label")));
                                break;
                            }
                        }
                    }
                }
            });
        }
        if("3".equals(searchDTO.getPlanType())){
            List<Long> trustIds = workPlanList.stream().filter(v1 -> v1.getShipvoyageItemId() == null).map(TPrdWorkPlanDTO::getTrustId).collect(Collectors.toList());
            if (!trustIds.isEmpty()) {
                List<Map<String, Object>> shipvoyageItems = tBusTrustMapper.listShipvoyageItemByTrustIds(trustIds);
                workPlanList.stream().filter(v1 -> trustIds.contains(v1.getTrustId()))
                        .forEach(v1 -> {
                            String shipNameVoyages = shipvoyageItems.stream()
                                    .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                    .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                    .collect(Collectors.joining("，"));
                            v1.setShipvoyageLabel(shipNameVoyages);
                            String shipVoyageIds = shipvoyageItems.stream()
                                    .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                    .map(v2 -> String.valueOf(v2.get("shipvoyageItemId")))
                                    .collect(Collectors.joining("，"));
                            v1.setShipVoyageIds(shipVoyageIds);

                        });
            }
        }
        return workPlanList;
    }
    /**
     * 获取导入上班次工班计划列表
     *
     * @param searchDTO
     * @return 对象列表
     */
   /* @Override
    public List<TPrdWorkPlanDTO> getLastWorkPlanList(TPrdWorkPlanSearchDTO searchDTO) {
        List<TPrdWorkPlanDTO> collectList = tPrdWorkPlanMapper.getLastWorkPlanList(searchDTO);

        List<TPrdWorkPlanDTO> workPlanList =collectList.stream().filter(dto ->
                dto.getStatementStatusList().isEmpty() || dto.getStatementStatusList().stream().anyMatch(status ->  !"30".equals(status))
        ).collect(Collectors.toList());


        List<Long> ids = workPlanList.stream().map(TPrdWorkPlanDTO::getId).collect(Collectors.toList());
        searchDTO.setIds(ids);
        if (CollectionUtils.isEmpty(ids)){
            return  workPlanList;
        }
        //配机配工
        List<TPrdDispatchDTO> dispatchList = tPrdWorkPlanMapper.getPrdDispatch(searchDTO);
        Map<String, List<TPrdDispatchDTO>> dispatchMap = new HashMap<>();
        //回显配机列表
        if (!CollectionUtils.isEmpty(dispatchList)){
            dispatchMap = dispatchList.stream().collect(Collectors.groupingBy(o -> {
                return o.getWorkPlanId() + "/" + o.getDispatchType() + "/" + o.getWorkPositionCode();
            }));
        }
        HashMap<String, String> machineStrins = new HashMap<>();
        if(!CollectionUtils.isEmpty(dispatchMap)){
            dispatchMap.forEach((k,v)->{
                String s = "";
                if(!CollectionUtils.isEmpty(v)){

                    for (int i = 0; i < v.size(); i++) {
                        if(i==0){
                            s = v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                        }else {
                            s+=","+v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                        }
                    }

                }
                machineStrins.put(k,s);
            });
        }



        //  位置
        List<TPrdWorkPlanLocationDTO> workPlanLocationList = tPrdWorkPlanMapper.getWorkPlanLocationList(searchDTO);
        Map<String, List<String>> tmpLoctionMap = workPlanLocationList.stream().collect(Collectors.groupingBy(
                o ->  o.getWorkPlanId() + "/" + o.getDirection(),
                Collectors.mapping(o->o.getStorehouseName()+"/"+o.getRegionName()+"/"+o.getMassName(),
                        Collectors.toList())));

        HashMap<String, String> locationNameMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(tmpLoctionMap)){
            tmpLoctionMap.forEach((k,v)->{
                String massNames = "";
                if(!CollectionUtils.isEmpty(v)){
                    for (int i = 0; i < v.size(); i++) {
                        if(i==0){
                            massNames = v.get(i);
                        }else {
                            massNames += " , " +v.get(i);
                        }
                    }
                }
                locationNameMap.put(k,massNames);
            });
        }

        //货主货代
        List<Long> searchTrustIds = workPlanList.stream().map(TPrdWorkPlanDTO::getTrustId).collect(Collectors.toList());
        searchDTO.setIds(searchTrustIds);
        List<TBusTrustCargoDTO> trustCargoList = tPrdWorkPlanMapper.getTrustCargoList(searchDTO);

        Map<Long, List<TBusTrustCargoDTO>> trustCargoMap =
                trustCargoList.stream().collect(Collectors.groupingBy(TBusTrustCargoDTO::getTrustId));


        //  01 前沿 02 后沿  03 水平 04 辅助
        for (TPrdWorkPlanDTO o : workPlanList) {

            if(!CollectionUtils.isEmpty(dispatchMap)){
                //前沿
                if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/01")))){
                    o.setEqumentListFront(dispatchMap.get((o.getId()+"/"+"1/01")));
                    o.setEquipmentNamesFront(machineStrins.get((o.getId()+"/"+"1/01")));
                }
                //后沿
                if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/02")))){
                    o.setEqumentListBack(dispatchMap.get((o.getId()+"/"+"1/02")));
                    o.setEquipmentNamesBack(machineStrins.get((o.getId()+"/"+"1/02")));

                }
                //水平
                if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/03")))){
                    o.setEqumentListReshipment(dispatchMap.get((o.getId()+"/"+"1/03")));
                    o.setEquipmentNamesReshipment(machineStrins.get((o.getId()+"/"+"1/03")));

                }
                //辅助
                if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/04")))){
                    o.setEqumentListAssist(dispatchMap.get((o.getId()+"/"+"1/04")));
                    o.setEquipmentNamesAssist(machineStrins.get((o.getId()+"/"+"1/04")));
                }
                //装卸队
                if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"2/null")))){
                    o.setLaborNum(dispatchMap.get((o.getId()+"/"+"2/null")).get(0).getNum());
                }
            }

            //场地
            if (!CollectionUtils.isEmpty(locationNameMap)){
                //前沿
                if(!StringUtils.isEmpty(locationNameMap.get((o.getId()+"/1")))
                ){
                    o.setMassNamesSource(locationNameMap.get((o.getId()+"/1")));
                }//后沿
                if(!StringUtils.isEmpty(locationNameMap.get((o.getId()+"/2")))){
                    o.setMassNamesTarget(locationNameMap.get((o.getId()+"/2")));
                }
            }

            if(!CollectionUtils.isEmpty(trustCargoMap)){
                if(!CollectionUtils.isEmpty(trustCargoMap.get(o.getTrustId()))){
                    List<TBusTrustCargoDTO> dtos = trustCargoMap.get(o.getTrustId());
                    if(!CollectionUtils.isEmpty(dtos)){
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
        //集疏港计划回显船名航次
        if("2".equals(searchDTO.getPlanType())){
            List<Long> trustIds = workPlanList.stream().filter(v1 -> v1.getShipvoyageItemId() == null).map(TPrdWorkPlanDTO::getTrustId).collect(Collectors.toList());
            if (!trustIds.isEmpty()) {
                List<Map<String, Object>> shipvoyageItems = tBusTrustMapper.listShipvoyageItemByTrustIds(trustIds);
                workPlanList.stream().filter(v1 -> trustIds.contains(v1.getTrustId()))
                        .forEach(v1 -> {
                            String shipNameVoyages = shipvoyageItems.stream()
                                    .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                    .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                    .collect(Collectors.joining("，"));
                            v1.setShipvoyageLabel(shipNameVoyages);
                            String shipVoyageIds = shipvoyageItems.stream()
                                    .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                    .map(v2 -> String.valueOf(v2.get("shipvoyageItemId")))
                                    .collect(Collectors.joining("，"));
                            v1.setShipVoyageIds(shipVoyageIds);

                        });
            }

            if(searchDTO.getShipvoyageItemId() != null ) {
                workPlanList = workPlanList.stream().filter(s -> s.getShipVoyageIds().contains(searchDTO.getShipvoyageItemId().toString())).collect(Collectors.toList());
            }
            //集疏港申请 没有作业过程的时候  根据通知单类型获取作业计划过程
            //获取集疏港计划类型
            List<Map<String, String>> mapList = tPrdWorkPlanMapper.workProcessType(2L);
            workPlanList.stream().forEach(o->{
                if(StringUtil.isEmpty(o.getProcessCode())){
                    TBusTrustDTO tmpTrust = tBusTrustMapper.getById(o.getTrustId());
                    //疏港 源是场
                    if("疏港".equals(tmpTrust.getType())){
                        for (Map<String, String> item : mapList) {
                            if("05".equals(String.valueOf(item.get("sourceCd")))){
                                o.setProcessCode(String.valueOf(item.get("value")));
                                o.setProcessName(String.valueOf(item.get("label")));
                                break;
                            }
                        }
                    }
                    //集港目的是场
                    if("集港".equals(tmpTrust.getType())){
                        for (Map<String, String> item : mapList) {
                            if("05".equals(String.valueOf(item.get("targetCd")))){
                                o.setProcessCode(String.valueOf(item.get("value")));
                                o.setProcessName(String.valueOf(item.get("label")));
                                break;
                            }
                        }
                    }
                }
            });
        }
        return workPlanList;
    }
*/
    /**
     * 取工班计划列表（非可编辑列表用）
     *(2023/11/03 集疏港申请再用，调整了)
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<TPrdWorkPlanDTO> getList(TPrdWorkPlanSearchDTO searchDTO) {

    	List<TPrdWorkPlanDTO> resultList = Lists.newArrayList();

        List<TPrdWorkPlanDTO> list = tPrdWorkPlanMapper.getList(searchDTO);

        // 查询配工状态 add by zcc 2023/11/04
        List<Long> idList = list.stream().map(TPrdWorkPlanDTO::getId).collect(Collectors.toList());

        searchDTO.setDeptCode(null);// 后续功能完善，删掉此处就可以了
        if(!CollectionUtils.isEmpty(idList) && !StringUtils.isEmpty(searchDTO.getDeptCode())) {
            // 查询计划作业的港区，根据作业通知单中的场地安排来确定
        	List<Map<String, Object>> workAreaList = tPrdWorkPlanMapper.getWorkCompanyCode(idList);

        	for (TPrdWorkPlanDTO data : list) {
            	for (Map<String, Object> workArea : workAreaList) {
    				if(data.getId().toString().equals(workArea.get("id").toString())
    						&& searchDTO.getDeptCode().equals("DGZYGS")
    						&& workArea.get("workAreaCd").toString().equals("03")
    						) {
    					resultList.add(data);
    					break;
    				}
    				if(data.getId().toString().equals(workArea.get("id").toString())
    						&& searchDTO.getDeptCode().equals("ZGZYGS")
    						&& workArea.get("workAreaCd").toString().equals("02")
    						) {
    					resultList.add(data);
    					break;
    				}
    				if(data.getId().toString().equals(workArea.get("id").toString())
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

        if(!CollectionUtils.isEmpty(idList)) {
        	List<Map<String, Object>> flowStatusList = tPrdWorkPlanMapper.getFlowStatus(idList);
        	List<Map<String, Object>> fixedStatusList = tPrdWorkPlanMapper.getFixedStatus(idList);
        	List<Map<String, Object>> laborStatusList = tPrdWorkPlanMapper.getLaborStatus(idList);

        	for (TPrdWorkPlanDTO data : resultList) {

            	for (Map<String, Object> flowStatus : flowStatusList) {
    				if(data.getId().toString().equals(flowStatus.get("id").toString())) {
    					data.setFlowStatus("1");
    					break;
    				}
    			}
            	for (Map<String, Object> fixedStatus : fixedStatusList) {
    				if(data.getId().toString().equals(fixedStatus.get("id").toString())) {
    					data.setFixedStatus("1");
    					break;
    				}
    			}
            	for (Map<String, Object> laborStatus : laborStatusList) {
    				if(data.getId().toString().equals(laborStatus.get("id").toString())) {
    					data.setLaborStatus("1");
    					break;
    				}
    			}
			}
        }

        //集疏港计划 回显船名航次 库场安排中得垛位区域
        if("2".equals(searchDTO.getPlanType())){
            List<Long> trustIds = resultList.stream().filter(v1 -> v1.getShipvoyageItemId() == null).map(TPrdWorkPlanDTO::getTrustId).collect(Collectors.toList());
            if (!trustIds.isEmpty()) {
                List<Map<String, Object>> shipvoyageItems = tBusTrustMapper.listShipvoyageItemByTrustIds(trustIds);
                resultList.stream()
                    .filter(v1 -> trustIds.contains(v1.getTrustId()))
                    .forEach(v1 -> {
                        String shipNameVoyages = shipvoyageItems.stream()
                                .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                .collect(Collectors.joining("，"));
                        v1.setShipNameVoyages(shipNameVoyages);

                        String shipVoyageIds = shipvoyageItems.stream()
                                        .filter(v2 -> v1.getTrustId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                        .map(v2 -> String.valueOf(v2.get("shipvoyageItemId")))
                                        .collect(Collectors.joining("，"));
                        v1.setShipVoyageIds(shipVoyageIds);
                    });
            }
            //船名航次过滤
            /*List<TPrdWorkPlanDTO> responseList = resultList;
            if(StringUtil.isNotEmpty(searchDTO.getShipvoyageItemId())){
                responseList = resultList
                        .stream()
                        .filter(s -> StringUtil.isNotEmpty(s.getShipVoyageIds()) && s.getShipVoyageIds().contains(searchDTO.getShipvoyageItemId().toString()))
                        .collect(Collectors.toList());
            }*/
            List<TPrdWorkPlanDTO> responseList = new ArrayList<>(resultList.size());

            if(!StringUtils.isEmpty(searchDTO.getShipName())){
                for (TPrdWorkPlanDTO dto : resultList) {
                    if(!StringUtils.isEmpty(dto.getShipNameVoyages())){
                        String[] parts = dto.getShipNameVoyages().split("_");
                        if (parts.length > 1) {
                            String beforeUnderscore = parts[0];
                            if (beforeUnderscore.contains(searchDTO.getShipName())) {
                                responseList.add(dto);
                            }
                        }
                    }
                }
                resultList = responseList;
            }
            if(!StringUtils.isEmpty(searchDTO.getVoyage())){
                responseList =new ArrayList<>();
                for (TPrdWorkPlanDTO dto : resultList) {
                    if(!StringUtils.isEmpty(dto.getShipNameVoyages())){
                        String[] parts = dto.getShipNameVoyages().split("_");
                        if (parts.length > 1) {
                            String afterUnderscore = parts[1];
                            if (afterUnderscore.contains(searchDTO.getVoyage())) {
                                responseList.add(dto);
                            }
                        }
                    }
                }
                resultList = responseList;
            }

            resultList.forEach(o->{
                TBusTrustLocationSearchDTO tmpDto = new TBusTrustLocationSearchDTO();
                tmpDto.setTrustId(o.getTrustId());
                List<TBusTrustLocationDTO> tmpList = tBusTrustLocationService.getListByCondition(tmpDto);
                if(org.apache.commons.collections.CollectionUtils.isNotEmpty(tmpList)){
                    List<TBusTrustLocationDTO.Location> locations = com.google.common.collect.Lists.newArrayList();
                    List<String> regionIdsTarget = com.google.common.collect.Lists.newArrayList();
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
    /*    //船名航次返回值
        return responseList;*/
        }
        return resultList;
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public TPrdWorkPlanDTO getDetail(Long id) {
        TPrdWorkPlanDTO dto = tPrdWorkPlanMapper.getById(id);
        if(id != null){
            dto.setRegionIdsSource(tPrdWorkPlanLocationMapper.getRegionIds(id, DirectionEnum.SOURCE.getCode()));
            dto.setRegionIdsTarget(tPrdWorkPlanLocationMapper.getRegionIds(id, DirectionEnum.TARGET.getCode()));
        }
        return dto;
    }

    /**
     * 保存单条申请（船舶以外）
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TPrdWorkPlanDTO dto) {

        int count = 0;

        // 修改的场合，判断
        if (dto.getId() != null) {
            TPrdWorkPlanDTO oldData = tPrdWorkPlanMapper.getById(dto.getId());
            if (!DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode().equals(oldData.getStatus())) {
                throw new BusinessRuntimeException("只有待审核的数据可以修改");
            }
        }

        // 垛位处理，用于计划查询方便
        if (dto.getLocationListSource() != null && dto.getLocationListSource().size() > 0) {
            dto.setMassNamesSource(dto.getLocationListSource().stream().map(TPrdWorkPlanLocationDTO::getMassNameFull).collect(Collectors.joining(",")));
        } else {
            dto.setMassNamesSource("");
        }
        if (dto.getLocationListTarget() != null && dto.getLocationListTarget().size() > 0) {
            dto.setMassNamesTarget(dto.getLocationListTarget().stream().map(TPrdWorkPlanLocationDTO::getMassNameFull).collect(Collectors.joining(",")));
        } else {
            dto.setMassNamesTarget("");
        }

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            dto.setStatus(DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode());
            dto.setDeptId(securityUtils.getUserInfo().getDeptId());
            dto.setDeptName(securityUtils.getUserInfo().getDeptName());

            // 生成计划号
            switch (dto.getPlanType()) {

                // 集疏港
                case "2":
                    dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WROK_PLAN_TRANSPORT, null));
                    break;
                // 转运
                case "3":
                    dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WROK_PLAN_TRANSPORT, null));
                    break;
                // 杂项
                case "4":
                    dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WORK_PLAN_SUNDRY, null));
                    break;
                default:
                    throw new BusinessRuntimeException("计划类型错误~");
            }

            count = tPrdWorkPlanMapper.insert(dto);

            // 修改
        } else {
            // 先删除 派工机械和派工人员
            commonService.delete("T_PRD_DISPATCH", "WORK_PLAN_ID", StringUtil.getString(dto.getId()));
            // 删除集疏港垛位
            commonService.delete("T_PRD_WORK_PLAN_LOCATION", "WORK_PLAN_ID", StringUtil.getString(dto.getId()));

            count = tPrdWorkPlanMapper.update(dto);
        }

        // 机械信息
        if (dto.getEqumentListBack() != null && dto.getEqumentListBack().size() > 0) {
            for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListBack()) {
                if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                    dispatchDTO.setWorkPlanId(dto.getId());
                    dispatchDTO.setId(snowflake.nextId());
                    dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                    dispatchDTO.setWorkPositionCode(MachineLocationEnum.BACK.getCode());
                    dispatchDTO.setWorkPositionName(MachineLocationEnum.BACK.getName());
                    tPrdDispatchMapper.insert(dispatchDTO);
                }
            }
        }

        // 劳务信息
        if (dto.getLaborNum() != null && dto.getLaborNum().intValue() > 0) {
            TPrdDispatchDTO dispatchDTO = new TPrdDispatchDTO();
            dispatchDTO.setWorkPlanId(dto.getId());
            dispatchDTO.setId(snowflake.nextId());
            dispatchDTO.setNum(dto.getLaborNum());
            dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.LABOR.getCode());
            tPrdDispatchMapper.insert(dispatchDTO);
        }

        // 源垛位信息
        if (dto.getLocationListSource() != null && dto.getLocationListSource().size() > 0) {
            for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListSource()) {
                locationDTO.setWorkPlanId(dto.getId());
                locationDTO.setId(snowflake.nextId());
                locationDTO.setDirection(DirectionEnum.SOURCE.getCode());
                tPrdWorkPlanLocationMapper.insert(locationDTO);
            }
        }

        // 目标垛位信息
        if (dto.getLocationListTarget() != null && dto.getLocationListTarget().size() > 0) {
            for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListTarget()) {
                locationDTO.setWorkPlanId(dto.getId());
                locationDTO.setId(snowflake.nextId());
                locationDTO.setDirection(DirectionEnum.TARGET.getCode());
                tPrdWorkPlanLocationMapper.insert(locationDTO);
            }
        }

        return count == 1;

    }

    /**
     * 批量新增船舶计划
     *
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertWorkPlan(List<Long> trustIds, String workDate, String classCode, String className,String planType) {

        if (StringUtil.isEmpty(workDate) || StringUtil.isEmpty(classCode)) {
            throw new BusinessRuntimeException("请选择要导入的日期班次~");
        }

        if (trustIds == null || trustIds.size() == 0) {
            throw new BusinessRuntimeException("请选择要导入的指令信息~");
        }
        int count = 0;
        // 遍历商务指令
        for (Long trustId : trustIds) {

        	List<Long> paramTrustIds = Lists.newArrayList();
        	paramTrustIds.add(trustId);

            // 新建的计划
            TPrdWorkPlanDTO dto = new TPrdWorkPlanDTO();

            // 指令
            List<TPrdWorkPlanTrustDTO> prdWorkPlanTrustList = new ArrayList<>();

            HashMap<String, Object> map = new HashMap<>();
            map.put("trustIds", paramTrustIds);

            List<Map<String, Object>> trustList = selectMapper.getPopupTrust(map);

            // 查询指令信息组合船舶工班计划及工班计划-指令信息
            if (trustList == null || trustList.size() == 0) {
                throw new BusinessRuntimeException("指令信息不存在~");
            }


            // 根据商务指令、日期查询昼夜计划作业量
            List<TDisShipDaynigttplanPO> dayNigttPlanList = tPrdWorkPlanMapper.getDayNigttPlan(trustId, workDate);
            if (!CollectionUtils.isEmpty(dayNigttPlanList)) {
                TDisShipDaynigttplanPO disShipDaynigttplanPO = dayNigttPlanList.get(0);

                dto.setBerthId(disShipDaynigttplanPO.getBerthId());
                dto.setBerthName(disShipDaynigttplanPO.getBerthName());
                if("01".equals(classCode)) {// 白班
                    dto.setTonPlan(disShipDaynigttplanPO.getMornWorkNum());
                } else if("02".equals(classCode)) {// 夜班
                    dto.setTonPlan(disShipDaynigttplanPO.getNightWorkNum());
                }
                dto.setStartTimePlan(disShipDaynigttplanPO.getStarttimePlan());// 计划开工时间
                dto.setEndTimePlan(disShipDaynigttplanPO.getEndtimePlan());// 计划完工时间
            }

            // 新增工班计划
            dto.setId(snowflake.nextId());
            dto.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
            dto.setClassCode(classCode);
            dto.setClassName(className);
            dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WORK_PLAN_SHIP, null));
            if(!(StringUtils.isEmpty(planType))&& DispatchEnum.WorkPlanTypeEnum.RESHIPMENT.getCode().equals(planType)){
                dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WORK_PLAN_RESHIPMENT, null));
            }
            dto.setPlanType(DispatchEnum.WorkPlanTypeEnum.SHIP.getCode());//默认设置到船舶计划中去
            if(!(StringUtils.isEmpty(planType))&& DispatchEnum.WorkPlanTypeEnum.RESHIPMENT.getCode().equals(planType)){
                dto.setPlanType(DispatchEnum.WorkPlanTypeEnum.RESHIPMENT.getCode());
            }
            if(!(StringUtils.isEmpty(planType))&& DispatchEnum.WorkPlanTypeEnum.LING_GONG.getCode().equals(planType)){
                dto.setPlanType(DispatchEnum.WorkPlanTypeEnum.LING_GONG.getCode());
            }
            dto.setStatus(DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode());
            dto.setDeptId(securityUtils.getUserInfo().getDeptId());
            dto.setDeptName(securityUtils.getUserInfo().getDeptName());
            dto.setCompanyId(StringUtil.getLong(trustList.get(0).get("companyId")));
            dto.setCompanyName(StringUtil.getString(trustList.get(0).get("companyName")));
            dto.setProcessCode(StringUtil.getString(trustList.get(0).get("processCode")));
            dto.setProcessName(StringUtil.getString(trustList.get(0).get("processName")));
            if(trustList.get(0).get("shipvoyageId") == null){
                dto.setShipvoyageId(null);
            }else{
                dto.setShipvoyageId(StringUtil.getLong(trustList.get(0).get("shipvoyageId")));
            }
            if(trustList.get(0).get("shipvoyageItemId") == null){
                dto.setShipvoyageItemId(null);
            }else{
                dto.setShipvoyageItemId(StringUtil.getLong(trustList.get(0).get("shipvoyageItemId")));
            }
            dto.setBerthId(trustList.get(0).get("berthId") == null ? null : StringUtil.getLong(trustList.get(0).get("berthId")));
            dto.setBerthName(trustList.get(0).get("berthName") == null ? null : StringUtil.getString(trustList.get(0).get("berthName")));
            dto.setTrustId(trustId);

            // 指令计划组合，以及指令客户、货种组合
//            for (Map<String, Object> temp : trustList) {
//                TPrdWorkPlanTrustDTO trustDTO = new TPrdWorkPlanTrustDTO();
//                trustDTO.setId(snowflake.nextId());
//                trustDTO.setTrustId(StringUtil.getLong(temp.get("id")));
//                trustDTO.setWorkPlanId(dto.getId());
//                prdWorkPlanTrustList.add(trustDTO);
//            }
//
//            // 批量新增计划指令
//            tPrdWorkPlanTrustMapper.insertBatch(prdWorkPlanTrustList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
            // 新增计划
            count += tPrdWorkPlanMapper.insert(dto);
		}
        return count == trustIds.size();
    }

    @Override
    public boolean insertWorkPlan(List<TPrdWorkPlanDTO> list) {
        int count = 0;
        // 遍历商务指令
        for (TPrdWorkPlanDTO dto : list) {
            List<TDisShipvoyageItemPO> shipvoyageItemPOS = tDisShipVoyageMapper.getDisShipVoyageItemById(dto.getShipvoyageItemId());
            // 新增工班计划
            dto.setId(snowflake.nextId());
            dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WORK_PLAN_SUNDRY, null));
            dto.setPlanType(DispatchEnum.WorkPlanTypeEnum.SUNDRY.getCode());
            dto.setStatus(DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode());
            dto.setDeptId(securityUtils.getUserInfo().getDeptId());
            dto.setDeptName(securityUtils.getUserInfo().getDeptName());
            dto.setShipvoyageId(CollectionUtils.isEmpty(shipvoyageItemPOS)?null:shipvoyageItemPOS.get(0).getShipvoyageId());
//            dto.setCompanyId(StringUtil.getLong(trustList.get(0).get("companyId")));
//            dto.setCompanyName(StringUtil.getString(trustList.get(0).get("companyName")));
//            dto.setProcessCode(StringUtil.getString(trustList.get(0).get("processCode")));
//            dto.setProcessName(StringUtil.getString(trustList.get(0).get("processName")));
//            dto.setShipvoyageId(StringUtil.getLong(trustList.get(0).get("shipvoyageId")));
//            dto.setShipvoyageItemId(StringUtil.getLong(trustList.get(0).get("shipvoyageItemId")));
//            dto.setBerthId(trustList.get(0).get("berthId") == null ? null : StringUtil.getLong(trustList.get(0).get("berthId")));
//            dto.setBerthName(trustList.get(0).get("berthName") == null ? null : StringUtil.getString(trustList.get(0).get("berthName")));
//            dto.setTrustId(trustId);
            count += tPrdWorkPlanMapper.insert(dto);
        }
        return count == list.size();
    }

    /***
     * 特殊货物处理
     * @param dto
     */
    TPrdWorkPlanDTO handleSpecialCargo(TPrdWorkPlanDTO dto,String machineCode) {
        if(dto==null){
            return null;
        }
        if(dto.getId()==null){
            return null;
        }
        if(StringUtils.isEmpty(machineCode)){
            return null;
        }
        List<Map<String, Object>> machineList  = null;
        if(dto.getCargoName().contains("废钢")||dto.getCargoName().contains("玉米纤维")){
            //macCode
            HashMap<String, Object> searchMap = new HashMap<>();
            searchMap.put("macCode", machineCode);
            machineList = publicService.getMachineList(searchMap);
            if(CollectionUtils.isEmpty(machineList)){
                //没有找到机械信息
                return null;
            }
        }

        //二次派工
        ArrayList<TPrdDispatchSecondaryDTO> secondaryDispatchList = new ArrayList<>();

        //获取二次配工信息
        TPrdDispatchSecondarySearchDTO secondarySearchDTO = new TPrdDispatchSecondarySearchDTO();
        secondarySearchDTO.setWorkPlanId(dto.getId());
        secondarySearchDTO.setDispatchType(1L);
        List<TPrdDispatchSecondaryDTO> secondaryListTmp = secondaryMapper.getList(secondarySearchDTO);
        Map<String, List<TPrdDispatchSecondaryDTO>> dispatchMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(secondaryListTmp)){
            dispatchMap = secondaryListTmp.stream().collect(Collectors.groupingBy(o -> {
                return o.getWorkPlanId() + "/" + o.getDispatchType() + "/" + o.getWorkPositionCode();
            }));
        }


        //判断前沿
        boolean flagFront = false;
        if(!CollectionUtils.isEmpty(dispatchMap.get((dto.getId()+"/"+"1/01")))){
            List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = dispatchMap.get((dto.getId() + "/" + "1/01"));

            //判断是否已经存在
            for (TPrdDispatchSecondaryDTO o : tPrdDispatchSecondaryDTOS) {
                if(o.getEquipmentTypeId().equals(Long.parseLong(String.valueOf(machineList.get(0).get("equipmentTypeId"))))){
                    flagFront = true;
                }
            }
        }else {
            flagFront = false;
            //前沿机械不在二次派工中 直接添加

        }

        //判断后场
        boolean flagBack = false;
        if(!CollectionUtils.isEmpty(dispatchMap.get((dto.getId()+"/"+"1/02")))){
            List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = dispatchMap.get((dto.getId() + "/" + "1/02"));
            //判断是否已经存在
            for (TPrdDispatchSecondaryDTO o : tPrdDispatchSecondaryDTOS) {
                if(o.getEquipmentTypeId().equals(Long.parseLong(String.valueOf(machineList.get(0).get("equipmentTypeId"))))){
                    flagBack = true;
                }
            }

        }else {
            // 后沿机械不在二次派工中 直接添加
            flagBack = false;

        }

        //查询子过程 +是否理货量
        HashMap<String, Object> searchMap = new HashMap<>();
        searchMap.put("type", "SUB_WORK_PROCESS");
        searchMap.put("parent", dto.getProcessCode());
        searchMap.put("isTallyTon",1);
        List<Map<String, Object>> childProcessListIsTallTon = selectService.getLocalSelect(searchMap);

        //添加前沿
        if(!flagFront){
            for (Map<String, Object> item : childProcessListIsTallTon) {

                    TPrdDispatchSecondaryDTO secondaryDTOFront = new TPrdDispatchSecondaryDTO();

                    secondaryDTOFront.setWorkPlanId(dto.getId());
                    secondaryDTOFront.setId(snowflake.nextId());
                    secondaryDTOFront.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                    secondaryDTOFront.setWorkPositionCode(MachineLocationEnum.FRONT.getCode());
                    secondaryDTOFront.setWorkPositionName(MachineLocationEnum.FRONT.getName());
                    secondaryDTOFront.setEquipmentId(Long.parseLong(String.valueOf(machineList.get(0).get("equipmentId"))));
                    secondaryDTOFront.setEquipmentNo(String.valueOf(machineList.get(0).get("equipmentNo")));
                    secondaryDTOFront.setEquipmentTypeId(Long.parseLong(String.valueOf(machineList.get(0).get("equipmentTypeId"))));
                    secondaryDTOFront.setEquipmentTypeName(String.valueOf(machineList.get(0).get("equipmentTypeName")));
                    secondaryDTOFront.setSubProcessCode(item.get("value")!=null?String.valueOf(item.get("value")):"");
                    secondaryDTOFront.setSubProcessName(item.get("label")!=null?String.valueOf(item.get("label")):"");
                    secondaryDTOFront.setDeptId(machineList.get(0).get("deptId")!=null ? Long.parseLong(String.valueOf(machineList.get(0).get("deptId"))):null);
                    secondaryDTOFront.setDeptName(machineList.get(0).get("deptName")!=null ?String.valueOf(machineList.get(0).get("deptName")):"");
                    secondaryDispatchList.add(secondaryDTOFront);

            }
        }

        //添加后沿
        if(!flagBack){
            for (Map<String, Object> item : childProcessListIsTallTon) {

                    TPrdDispatchSecondaryDTO secondaryDTOBack = new TPrdDispatchSecondaryDTO();

                    secondaryDTOBack.setWorkPlanId(dto.getId());
                    secondaryDTOBack.setId(snowflake.nextId());
                    secondaryDTOBack.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                    secondaryDTOBack.setWorkPositionCode(MachineLocationEnum.BACK.getCode());
                    secondaryDTOBack.setWorkPositionName(MachineLocationEnum.BACK.getName());
                    secondaryDTOBack.setEquipmentId(Long.parseLong(String.valueOf(machineList.get(0).get("equipmentId"))));
                    secondaryDTOBack.setEquipmentNo(String.valueOf(machineList.get(0).get("equipmentNo")));
                    secondaryDTOBack.setEquipmentTypeId(StringUtils.isEmpty(machineList.get(0).get("equipmentTypeId"))?null:Long.parseLong(String.valueOf(machineList.get(0).get("equipmentTypeId"))));
                    secondaryDTOBack.setEquipmentTypeName(String.valueOf(machineList.get(0).get("equipmentTypeName")));
                    secondaryDTOBack.setSubProcessCode(item.get("value") != null ? String.valueOf(item.get("value")) : "");
                    secondaryDTOBack.setSubProcessName(item.get("label") != null ? String.valueOf(item.get("label")) : "");
                    secondaryDTOBack.setDeptId(machineList.get(0).get("deptId")!=null ? Long.parseLong(String.valueOf(machineList.get(0).get("deptId"))):null);
                    secondaryDTOBack.setDeptName(machineList.get(0).get("deptName")!=null ?String.valueOf(machineList.get(0).get("deptName")):"");
                    secondaryDispatchList.add(secondaryDTOBack);

            }
        }

        if(!CollectionUtils.isEmpty(secondaryDispatchList)){
            //二次派工增加新的派工信息
            secondaryMapper.insertBatch(secondaryDispatchList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(),new Date() );
        }
        return null;
    }

    /**
     * 页面上的保存
     *
     * @param list
     * @return 是否成功
     */
    public boolean updateWorkPlan(List<TPrdWorkPlanDTO> list) {

        if (list == null || list.size() == 0) {
            throw new BusinessRuntimeException("请选中要修改的数据~");
        }
        //判断相同班次下、同计划号下、同作业过程下 是否存在重复数据
        HashMap<String, List<TPrdWorkPlanDTO>> stringListHashMap = new HashMap<>();

        SysParameterDTO sysParameter = sysParameterMapper.getByKey("INVOICE_SEND_TYPE");
        String paramVal = sysParameter.getParamVal();

        for (TPrdWorkPlanDTO dto : list) {
            int count = tPrdWorkPlanMapper.getTallyCountByPlanId(dto.getId());
            TPrdWorkPlanDTO prdWorkPlanDTO = tPrdWorkPlanMapper.getById(dto.getId());
            if(count>0 && !prdWorkPlanDTO.getProcessCode().equals(dto.getProcessCode())){//大于0且作业过程改变
                throw new BusinessRuntimeException("计划编号："+dto.getTrustNo()+"，作业过程为:"+ dto.getProcessName() +",的计划已经进行理货不允许修改作业过程");
            }
            String key=dto.getTrustNo()+"_"+dto.getProcessCode()+"_"+dto.getPlanType();
            if(stringListHashMap.containsKey(key)){
                List<TPrdWorkPlanDTO> tPrdWorkPlanDTOS = stringListHashMap.get(key);
                tPrdWorkPlanDTOS.add(dto);
                stringListHashMap.put(key, tPrdWorkPlanDTOS);
            }else {
                ArrayList<TPrdWorkPlanDTO> tPrdWorkPlanDTOS = new ArrayList<>();
                tPrdWorkPlanDTOS.add(dto);
                stringListHashMap.put(key, tPrdWorkPlanDTOS);
            }
        }
        stringListHashMap.forEach((k,v)->{
            if(v.size()>1){
                throw new BusinessRuntimeException("计划编号："+v.get(0).getTrustNo()+"，作业过程为:"+v.get(0).getProcessName()+",的计划重复，请删除后进行保存");
            }
        });

        //  过滤已审核的数据 只有未审核的数据才运行保存
        List<TPrdWorkPlanDTO> listTmp = new ArrayList<>();
        list.forEach(item->{
            if (StringUtil.isEmpty(item.getProcessCode())||StringUtil.isEmpty(item.getProcessName())){
                throw new BusinessRuntimeException("请将作业过程一列填写完整");
            }
            if(item.getId()==null){
                throw new BusinessRuntimeException("数据异常,选中的计划ID为空");
            }
            if(tPrdWorkPlanMapper.getCheckCountById(item.getId(), "UPDATE")==0){
                listTmp.add(item);
            }
        });

        if(CollectionUtils.isEmpty(listTmp)){
            throw  new BusinessRuntimeException("所有的数据都已审核，无需再次保存!");
        }

        //保留二次配工信息
        List<Long> ids = listTmp.stream().map(TPrdWorkPlanDTO::getId).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(listTmp)){
            List<TPrdWorkPlanLocationDTO>  workPlanLocationList=  tPrdWorkPlanLocationMapper.getWorkPlanLocationList(ids) ;
            listTmp.forEach(o->{
                for (TPrdWorkPlanLocationDTO dto : workPlanLocationList) {
                    List<TDisShipvoyageItemPO> shipvoyageItemPOS = tDisShipVoyageMapper.getDisShipVoyageItemById(o.getShipvoyageItemId());
                    Long shipvoyageId= CollectionUtils.isEmpty(shipvoyageItemPOS)?null:shipvoyageItemPOS.get(0).getShipvoyageId();
                    o.setShipvoyageId(shipvoyageId==null?null:shipvoyageId);
                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("1")){
                        if(CollectionUtils.isEmpty(o.getLocationListSource())){
                            o.setLocationListSource(Lists.newArrayList());
                        }
                        o.getLocationListSource().add(dto);
                    }
                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("2")){
                        if(CollectionUtils.isEmpty(o.getLocationListTarget())){
                            o.setLocationListTarget(Lists.newArrayList());
                        }
                        o.getLocationListTarget().add(dto);
                    }
                }
            });
        }

        // 船舶计划 (更新的字段不一样）
        if (DispatchEnum.WorkPlanTypeEnum.SHIP.getCode().equals(listTmp.get(0).getPlanType())) {
            tPrdWorkPlanMapper.updateShipPlanBatch(listTmp, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
            // 其他计划
        } else {
            // 批量更新
            tPrdWorkPlanMapper.updateOtherPlanBatch(listTmp, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        // 新建的机械及人员
        List<TPrdDispatchDTO> dispatchList = new ArrayList<>();
        // 新建的垛位
        List<TPrdWorkPlanLocationDTO> locationList = new ArrayList<>();

        for (TPrdWorkPlanDTO dto : listTmp) {
            // 前沿
            if (dto.getEqumentListFront() != null && dto.getEqumentListFront().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListFront()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.FRONT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.FRONT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }

            // 机械信息（后场）
            if (dto.getEqumentListBack() != null && dto.getEqumentListBack().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListBack()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.BACK.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.BACK.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }

            // 辅助
            if (dto.getEqumentListAssist() != null && dto.getEqumentListAssist().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListAssist()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.ASSIST.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.ASSIST.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }

            // 转运（水平）
            if (dto.getEqumentListReshipment() != null && dto.getEqumentListReshipment().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListReshipment()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.RESHIPMENT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.RESHIPMENT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }

            // 劳务信息
            if (dto.getLaborNum() != null && dto.getLaborNum().intValue() > 0) {
                TPrdDispatchDTO dispatchDTO = new TPrdDispatchDTO();
                dispatchDTO.setWorkPlanId(dto.getId());
                dispatchDTO.setId(snowflake.nextId());
                dispatchDTO.setNum(dto.getLaborNum());
                dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.LABOR.getCode());
                dispatchList.add(dispatchDTO);
            }

            // 源垛位信息
            if (dto.getLocationListSource() != null && dto.getLocationListSource().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListSource()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.SOURCE.getCode());
                    locationList.add(locationDTO);
                }
            }

            // 目标垛位信息
            if (dto.getLocationListTarget() != null && dto.getLocationListTarget().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListTarget()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.TARGET.getCode());
                    locationList.add(locationDTO);
                }
            }

        }

        // 删除位置信息
        tPrdWorkPlanLocationMapper.deleteByWorkPlanIds(ids);

        // 删除派工信息
        tPrdDispatchMapper.deleteByWorkPlanIds(ids);

        // 新增调度表
        if (dispatchList.size() > 0) {
            tPrdDispatchMapper.insertBatch(dispatchList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        // 新增位置表
        if (locationList.size() > 0) {
            tPrdWorkPlanLocationMapper.insertBatch(locationList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        return true;
    }

    /**
     * 派工，派场地
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDispatch(TPrdWorkPlanDTO dto) {
        if(dto==null){
            throw new BusinessRuntimeException("传入的数据为空");
        }
        // 工班计划派工-船舶计划：派调度



        //理货员派工 场第派工
        int count = 0;

        // 理货员工指派 批量
        if ("1".equals(dto.getUpdateType())) {

            //if (!DispatchEnum.WorkPlanStatusEnum.REVIEW.getCode().equals(oldData.getStatus())) {
            //    throw new BusinessRuntimeException("只有已审核的数据可以指派理货员~");
            //}
            if(CollectionUtils.isEmpty(dto.getPlanIds())){
                throw new BusinessRuntimeException("要操作的计划Id集合为空");
            }
            for (Long tmpId : dto.getPlanIds()){
                dto.setId(tmpId);
                count += tPrdWorkPlanMapper.updateDispatch(dto);
            }
            if (count!=dto.getPlanIds().size()){
                throw new BusinessRuntimeException("保存成功的计划条数和实际要保存的条数不一致，请重新保存");
            }else {
                return true;
            }
        }
        // 场地指派 批量
        else if ("2".equals(dto.getUpdateType())) {
            if(CollectionUtils.isEmpty(dto.getPlanIds())){
                throw new BusinessRuntimeException("要操作的计划Id集合为空");
            }
            for (Long tmpId : dto.getPlanIds()){
                dto.setId(tmpId);
                // 删除集疏港垛位
                commonService.delete("T_PRD_WORK_PLAN_LOCATION", "WORK_PLAN_ID", StringUtil.getString(dto.getId()));

                // 更新源垛位信息
                if (dto.getLocationListSource() != null && dto.getLocationListSource().size() > 0) {
                    for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListSource()) {
                        locationDTO.setWorkPlanId(dto.getId());
                        locationDTO.setId(snowflake.nextId());
                        locationDTO.setDirection(DirectionEnum.SOURCE.getCode());
                        tPrdWorkPlanLocationMapper.insert(locationDTO);
                    }
                    dto.setMassNamesSource(dto.getLocationListSource().stream().map(TPrdWorkPlanLocationDTO::getMassNameFull).collect(Collectors.joining(",")));
                } else {
                    dto.setMassNamesSource("");
                }

                // 目标垛位信息
                if (dto.getLocationListTarget() != null && dto.getLocationListTarget().size() > 0) {
                    for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListTarget()) {
                        locationDTO.setWorkPlanId(dto.getId());
                        locationDTO.setId(snowflake.nextId());
                        locationDTO.setDirection(DirectionEnum.TARGET.getCode());
                        tPrdWorkPlanLocationMapper.insert(locationDTO);
                    }
                    dto.setMassNamesTarget(dto.getLocationListTarget().stream().map(TPrdWorkPlanLocationDTO::getMassNameFull).collect(Collectors.joining(",")));
                } else {
                    dto.setMassNamesTarget("");
                }

                count += tPrdWorkPlanMapper.updateDispatch(dto);
                //全部保存失败的时候进行回滚

            }
            if (count!=dto.getPlanIds().size()){
                throw new BusinessRuntimeException("保存成功的计划条数和实际要保存的条数不一致，请重新保存");
            }else {
                return true;
            }
        }
        //调度员派工 批量
        else if ("3".equals(dto.getUpdateType())) {
            if(StringUtil.isEmpty(dto.getBatchId())){
                throw new BusinessRuntimeException("没有作业计划ID");
            }
            String[] tmpStringList = dto.getBatchId().split(",");
            List<Long> list = new ArrayList<>(tmpStringList.length);
            for (int i = 0; i < tmpStringList.length; i++) {
                list.add(Long.parseLong(tmpStringList[i]));
            }
            tPrdWorkPlanMapper.updateDispatchPeople(list,dto);
            return  true;
        }
        return false;
    }

    /**
     * 复制、导入工班计划 船舶计划在用
     *
     * @param ids
     * @return 是否成功
     */
    public boolean importWorkPlan(List<Long> ids, String workDate, String classCode, String className) {

        if(CollectionUtils.isEmpty(ids)){
            throw new BusinessRuntimeException("请选择数据之后再进行导入操作");
        }

        TPrdWorkPlanSearchDTO searchDTO = new TPrdWorkPlanSearchDTO();
        searchDTO.setIds(ids);
        List<TPrdWorkPlanDTO> oldList = tPrdWorkPlanMapper.getWorkPlanList(searchDTO);

        if(!CollectionUtils.isEmpty(ids)){
            //位置信息 原位置和目的位置
            List<TPrdWorkPlanLocationDTO>  workPlanLocationList=  tPrdWorkPlanLocationMapper.getWorkPlanLocationList(ids) ;
            //装卸队
            //配机配工
            List<TPrdDispatchDTO> dispatchList = tPrdWorkPlanMapper.getPrdDispatch(searchDTO);
            Map<String, List<TPrdDispatchDTO>> dispatchMap = new HashMap<>();
            //回显配机列表
            if (!CollectionUtils.isEmpty(dispatchList)){
                dispatchMap = dispatchList.stream().collect(Collectors.groupingBy(o -> {
                    return o.getWorkPlanId() + "/" + o.getDispatchType() + "/" + o.getWorkPositionCode();
                }));
            }

            HashMap<String, String> machineStrins = new HashMap<>();
            if(!CollectionUtils.isEmpty(dispatchMap)){
                dispatchMap.forEach((k,v)->{
                    String s = "";
                    if(!CollectionUtils.isEmpty(v)){

                        for (int i = 0; i < v.size(); i++) {
                            if(i==0){
                                s = v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                            }else {
                                s+=","+v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                            }
                        }

                    }
                    machineStrins.put(k,s);
                });
            }

            for (TPrdWorkPlanDTO o : oldList) {

                for (TPrdWorkPlanLocationDTO dto : workPlanLocationList) {
                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("1")){
                        if(CollectionUtils.isEmpty(o.getLocationListSource())){
                            o.setLocationListSource(Lists.newArrayList());
                        }
                        o.getLocationListSource().add(dto);
                    }

                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("2")){
                        if(CollectionUtils.isEmpty(o.getLocationListTarget())){
                            o.setLocationListTarget(Lists.newArrayList());
                        }
                        o.getLocationListTarget().add(dto);
                    }
                }

                if(!CollectionUtils.isEmpty(dispatchMap)){
                    //前沿
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/01")))){
                        o.setEqumentListFront(dispatchMap.get((o.getId()+"/"+"1/01")));
                        o.setEquipmentNamesFront(machineStrins.get((o.getId()+"/"+"1/01")));
                    }
                    //后沿
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/02")))){
                        o.setEqumentListBack(dispatchMap.get((o.getId()+"/"+"1/02")));
                        o.setEquipmentNamesBack(machineStrins.get((o.getId()+"/"+"1/02")));

                    }
                    //水平
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/03")))){
                        o.setEqumentListReshipment(dispatchMap.get((o.getId()+"/"+"1/03")));
                        o.setEquipmentNamesReshipment(machineStrins.get((o.getId()+"/"+"1/03")));

                    }
                    //辅助
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/04")))){
                        o.setEqumentListAssist(dispatchMap.get((o.getId()+"/"+"1/04")));
                        o.setEquipmentNamesAssist(machineStrins.get((o.getId()+"/"+"1/04")));
                    }
                    //装卸队
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"2/null")))){
                        o.setLaborNum(dispatchMap.get((o.getId()+"/"+"2/null")).get(0).getNum());
                    }
                }
            }
        }


        if (oldList == null || oldList.size() == 0) {
            throw new BusinessRuntimeException("选择的工班计划不存在~");
        }

        List<TPrdDispatchDTO> dispatchList = new ArrayList<>();
        List<TPrdWorkPlanLocationDTO> locationList = new ArrayList<>();

            for (TPrdWorkPlanDTO dto : oldList) {

                TPrdWorkPlanSearchDTO searchDTOTmp = new TPrdWorkPlanSearchDTO();
                searchDTOTmp.setClassCode(classCode);
                searchDTOTmp.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
                searchDTOTmp.setTrustId(dto.getTrustId());
                searchDTOTmp.setProcessCode(dto.getProcessCode());
                searchDTOTmp.setPlanType(dto.getPlanType());
                List<TPrdWorkPlanDTO> workPlanList = tPrdWorkPlanMapper.getWorkPlanList(searchDTOTmp);
                if(!CollectionUtils.isEmpty(workPlanList)){
                    throw new BusinessRuntimeException("作业编号为："
                            +workPlanList.get(0).getTrustNo()
                            +"，作业过程为:"
                            +workPlanList.get(0).getProcessName()
                            +"的计划在当前班次已经存在，请取消勾选之后再进行导入"
                    );
                }


            List<TPrdWorkPlanDTO> tmpList = new ArrayList<>();

            dto.setId(snowflake.nextId());
            dto.setStatus(DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode());
            dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WORK_PLAN_SHIP, null));

            // 按班次导入时，需要设置日期班次
            if (workDate != null) {
                dto.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
                dto.setClassCode(classCode);
                dto.setClassName(className);
            }
            // 前沿
            if (dto.getEqumentListFront() != null && dto.getEqumentListFront().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListFront()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.FRONT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.FRONT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 机械信息（后场）
            if (dto.getEqumentListBack() != null && dto.getEqumentListBack().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListBack()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.BACK.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.BACK.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 辅助
            if (dto.getEqumentListAssist() != null && dto.getEqumentListAssist().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListAssist()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.ASSIST.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.ASSIST.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 转运（水平）
            if (dto.getEqumentListReshipment() != null && dto.getEqumentListReshipment().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListReshipment()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.RESHIPMENT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.RESHIPMENT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }

            // 劳务信息
            if (dto.getLaborNum() != null && dto.getLaborNum().intValue() > 0) {
                TPrdDispatchDTO dispatchDTO = new TPrdDispatchDTO();
                dispatchDTO.setWorkPlanId(dto.getId());
                dispatchDTO.setId(snowflake.nextId());
                dispatchDTO.setNum(dto.getLaborNum());
                dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.LABOR.getCode());
                dispatchList.add(dispatchDTO);
            }

            // 源垛位信息
            if (dto.getLocationListSource() != null && dto.getLocationListSource().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListSource()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.SOURCE.getCode());
                    locationList.add(locationDTO);
                }
            }

            // 目标垛位信息
            if (dto.getLocationListTarget() != null && dto.getLocationListTarget().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListTarget()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.TARGET.getCode());
                    locationList.add(locationDTO);
                }
            }
            tmpList.add(dto);
            tPrdWorkPlanMapper.insertBatch( tmpList , securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());

        }

        // 劳务信息 新增
        if (dispatchList.size() > 0) {
            tPrdDispatchMapper.insertBatch(dispatchList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        // 新增位置表
        if (locationList.size() > 0) {
            tPrdWorkPlanLocationMapper.insertBatch(locationList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        return true;
    }

    /**
     * 删除
     *
     * @param ids
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {

        if (ids == null || ids.size() == 0) {
            throw new BusinessRuntimeException("请选中要删除的数据~");
        }

        int count = tPrdWorkPlanMapper.getCheckCount(ids, "DELETE");

        if (count == 0) {
            throw new BusinessRuntimeException("只有未审核的数据可以删除~");
        }

        for (Long id : ids) {
             Integer tallyCount = tPrdWorkPlanMapper.getYardTallyInfo(id);
            //存在理货记录不允许删除
             if(tallyCount!=null && tallyCount>0){
                 TPrdWorkPlanDTO planDTO = tPrdWorkPlanMapper.getWorkPlanById(id);
                 throw new BusinessRuntimeException("通知单号为:"+planDTO.getTrustNo()+"的作业计划已理货，不允许删除！");
             }
            //存在签票
            Integer ticketCount = tPrdWorkPlanMapper.getTicketInfo(id);

            if(ticketCount!=null && ticketCount>0){
                 TPrdWorkPlanDTO planDTO = tPrdWorkPlanMapper.getWorkPlanById(id);
                 throw new BusinessRuntimeException("通知单号为:"+planDTO.getTrustNo()+"的作业计划已签票，不允许删除！");
             }
        }

        // 删除主表
        tPrdWorkPlanMapper.deleteByIds(ids);

        // 删除主表
        tPrdWorkPlanLocationMapper.deleteByWorkPlanIds(ids);

        // 删除主表
        tPrdDispatchMapper.deleteByWorkPlanIds(ids);

        return true;
    }

    @Autowired
    private TPrdDispatchSecondaryService tPrdDispatchSecondaryService;

    /**
     * 审核
     *
     * @param ids
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveByIds(List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            throw new BusinessRuntimeException("请选择未审核的数据~");
        }
        int count =0;
        List<TPrdWorkPlanDTO> tmpResult = tPrdWorkPlanMapper.getByIds(ids);
        for (TPrdWorkPlanDTO item : tmpResult) {
            if("10".equals(item.getStatus())){
                count++;
            }
            if(StringUtils.isEmpty(item.getProcessCode())||StringUtils.isEmpty(item.getProcessName())){
                throw new BusinessRuntimeException("作业过程不能为空~");
            }
        }
        if(count != ids.size()){
            throw new BusinessRuntimeException("只有未审核的数据才能进行审核");
        }

        //判断是否有配工的作业线，如果有则不允许审核
//        for (Long id : ids) {
//            TPrdDispatchSecondarySearchDTO searchDto = new TPrdDispatchSecondarySearchDTO();
//            searchDto.setWorkPlanId(id);
//            searchDto.setDispatchType(1l);
//            List<TPrdDispatchSecondaryDTO> list = tPrdDispatchSecondaryService.getAllList(searchDto);
//            searchDto.setDispatchType(2l);
//            List<TPrdDispatchSecondaryDTO> list2 = tPrdDispatchSecondaryService.getAllList(searchDto);
//            list.addAll(list2);
//            if(list.size()>0){
//                TPrdWorkPlanDTO workPlanDTO = tPrdWorkPlanService.getDetail(id);
//                throw new BusinessRuntimeException("计划"+ workPlanDTO.getPlanNo() +"已经配工，无法进行审核，请先取消派工！");
//            }
//        }
        //特殊货物处理 废钢
        tmpResult.forEach(dto->{
            if(StringUtil.isNotEmpty(dto.getCargoName())&&
                    (dto.getCargoName().contains("废钢")||dto.getCargoName().contains("玉米纤维"))){
                SpringUtils.getBean(this.getClass()).handleSpecialCargo(dto,"HZZX");
            }
        });
        List<TBusTrustDTO> tmpWorkIds = new ArrayList<>();
        for (Long o : ids) {
            List<TPrdWorkPlanDTO> tmpList = tPrdWorkPlanMapper.getStatusCountById(o,""); //获取状态是已经审核的
            if(CollectionUtils.isEmpty(tmpList)){
                continue;
            }
            Map<String, List<TPrdWorkPlanDTO>> statusMap = tmpList.stream().collect(Collectors.groupingBy(TPrdWorkPlanDTO::getStatus));
            if((!CollectionUtils.isEmpty(statusMap.get("10")))&&(statusMap.get("10").size()==tmpList.size())){
                TBusTrustDTO dto = new TBusTrustDTO();
                dto.setId(tmpList.get(0).getTrustId());
                dto.setStatus("40");
                tmpWorkIds.add(dto);
            }
        }
        //更新作业通知单状态 T_BUS_TRUST
        if(!CollectionUtils.isEmpty(tmpWorkIds)){
            tPrdWorkPlanMapper.updateTrustStatus(tmpWorkIds);
        }
        return (tPrdWorkPlanMapper.updateStatusByIds(ids,
                DispatchEnum.WorkPlanStatusEnum.REVIEW.getCode(),
                "APPROVE",
                securityUtils.getLoginUserId(),
                securityUtils.getLoginUserName(),
                new Date()))>0;
    }

    /**
     * 撤销审核
     *
     * @param ids
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelByIds(List<Long> ids) {

        if (ids == null || ids.size() == 0) {
            throw new BusinessRuntimeException("请选择已审核的数据~");
        }

        int count = tPrdWorkPlanMapper.getCheckCount(ids, "CANCEL");

        if (count == 0) {
            throw new BusinessRuntimeException("请选择已审核的数据进行撤销审核~");
        }

        //判断是否有配工的作业线，如果有则不允许审核
        for (Long id : ids) {
            TPrdDispatchSecondarySearchDTO searchDto = new TPrdDispatchSecondarySearchDTO();
            searchDto.setWorkPlanId(id);
            searchDto.setDispatchType(1l);
            List<TPrdDispatchSecondaryDTO> list = tPrdDispatchSecondaryService.getAllList(searchDto);
            searchDto.setDispatchType(2l);
            List<TPrdDispatchSecondaryDTO> list2 = tPrdDispatchSecondaryService.getAllList(searchDto);
            list.addAll(list2);
            if(list.size()>0){
                TPrdWorkPlanDTO workPlanDTO = this.getDetail(id);
                throw new BusinessRuntimeException("计划"+ workPlanDTO.getPlanNo() +"已经配工，不能撤销审核，请先取消派工！");
            }
        }

        int tmpCount = tPrdWorkPlanMapper.updateStatusByIds(ids,
                DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode(),
                "CANCEL",
                securityUtils.getLoginUserId(),
                securityUtils.getLoginUserName(),
                new Date());

        List<TBusTrustDTO> tmpWorkIds = new ArrayList<>();
        for (Long o : ids) {
            List<TPrdWorkPlanDTO> tmpList = tPrdWorkPlanMapper.getStatusCountById(o,""); //获取状态是已经审核的
            if(CollectionUtils.isEmpty(tmpList)){
                continue;
            }
            Map<String, List<TPrdWorkPlanDTO>> statusMap = tmpList.stream().collect(Collectors.groupingBy(TPrdWorkPlanDTO::getStatus));
            if(!CollectionUtils.isEmpty(statusMap.get("10"))){
                if(statusMap.get("10").size()==tmpList.size()){
                    TBusTrustDTO dto = new TBusTrustDTO();
                    dto.setId(tmpList.get(0).getTrustId());
                    dto.setStatus("30");
                    tmpWorkIds.add(dto);
                }
            }
        }
        //更新作业通知单状态T_BUS_TRUST
        if(!CollectionUtils.isEmpty(tmpWorkIds)){
            tPrdWorkPlanMapper.updateTrustStatus(tmpWorkIds);
        }
        return tmpCount > 0;
    }

    @Override
    public List<Map<String, String>> getProcessName() {
        List<TPrdWorkPlanPO> mMachineTypeModelPOS = tPrdWorkPlanMapper.getProcessName();

        List<Map<String, String>> arrayList = new ArrayList<>();
        for (TPrdWorkPlanPO processWithSystem : mMachineTypeModelPOS) {
            Map<String, String> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("label", processWithSystem.getProcessName());
            objectObjectHashMap.put("value", String.valueOf(processWithSystem.getProcessCode()));
            arrayList.add(objectObjectHashMap);
        }
        return arrayList;
    }

    @Override
    public List<Map<String, String>> normalWorkProcess() {
        return  tPrdWorkPlanMapper.getNormalWorkProcess();
    }

    /**
     * 批量导入转运/零工计划
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addBatch(List<Long> ids, String workDate, String classCode, String className) {
        if(CollectionUtils.isEmpty(ids)){
            throw new BusinessRuntimeException("请选择数据之后再进行导入操作");
        }

        TPrdWorkPlanSearchDTO searchDTO = new TPrdWorkPlanSearchDTO();
        searchDTO.setIds(ids);
        List<TPrdWorkPlanDTO> oldList = tPrdWorkPlanMapper.getWorkPlanList(searchDTO);

        if (oldList == null || oldList.size() == 0) {
            throw new BusinessRuntimeException("选择的工班计划不存在~");
        }

        if(!CollectionUtils.isEmpty(ids)){
            //位置信息 原位置和目的位置
            List<TPrdWorkPlanLocationDTO>  workPlanLocationList=  tPrdWorkPlanLocationMapper.getWorkPlanLocationList(ids) ;
            //装卸队
            //配机配工
            List<TPrdDispatchDTO> dispatchList = tPrdWorkPlanMapper.getPrdDispatch(searchDTO);
            Map<String, List<TPrdDispatchDTO>> dispatchMap = new HashMap<>();
            //回显配机列表
            if (!CollectionUtils.isEmpty(dispatchList)){
                dispatchMap = dispatchList.stream().collect(Collectors.groupingBy(o -> {
                    return o.getWorkPlanId() + "/" + o.getDispatchType() + "/" + o.getWorkPositionCode();
                }));
            }

            HashMap<String, String> machineStrins = new HashMap<>();
            if(!CollectionUtils.isEmpty(dispatchMap)){
                dispatchMap.forEach((k,v)->{
                    String s = "";
                    if(!CollectionUtils.isEmpty(v)){

                        for (int i = 0; i < v.size(); i++) {
                            if(i==0){
                                s = v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                            }else {
                                s+=","+v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                            }
                        }

                    }
                    machineStrins.put(k,s);
                });
            }


            for (TPrdWorkPlanDTO o : oldList) {



                for (TPrdWorkPlanLocationDTO dto : workPlanLocationList) {
                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("1")){
                        if(CollectionUtils.isEmpty(o.getLocationListSource())){
                            o.setLocationListSource(Lists.newArrayList());
                        }
                        o.getLocationListSource().add(dto);
                    }

                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("2")){
                        if(CollectionUtils.isEmpty(o.getLocationListTarget())){
                            o.setLocationListTarget(Lists.newArrayList());
                        }
                        o.getLocationListTarget().add(dto);
                    }
                }

                if(!CollectionUtils.isEmpty(dispatchMap)){
                    //前沿
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/01")))){
                        o.setEqumentListFront(dispatchMap.get((o.getId()+"/"+"1/01")));
                        o.setEquipmentNamesFront(machineStrins.get((o.getId()+"/"+"1/01")));
                    }
                    //后沿
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/02")))){
                        o.setEqumentListBack(dispatchMap.get((o.getId()+"/"+"1/02")));
                        o.setEquipmentNamesBack(machineStrins.get((o.getId()+"/"+"1/02")));

                    }
                    //水平
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/03")))){
                        o.setEqumentListReshipment(dispatchMap.get((o.getId()+"/"+"1/03")));
                        o.setEquipmentNamesReshipment(machineStrins.get((o.getId()+"/"+"1/03")));

                    }
                    //辅助
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/04")))){
                        o.setEqumentListAssist(dispatchMap.get((o.getId()+"/"+"1/04")));
                        o.setEquipmentNamesAssist(machineStrins.get((o.getId()+"/"+"1/04")));
                    }
                    //装卸队
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"2/null")))){
                        o.setLaborNum(dispatchMap.get((o.getId()+"/"+"2/null")).get(0).getNum());
                    }
                }
            }
        }

        List<TPrdDispatchDTO> dispatchList = new ArrayList<>();
        List<TPrdWorkPlanLocationDTO> locationList = new ArrayList<>();

        for (TPrdWorkPlanDTO dto : oldList) {

            TPrdWorkPlanSearchDTO searchDTOTmp = new TPrdWorkPlanSearchDTO();
            searchDTOTmp.setClassCode(classCode);
            searchDTOTmp.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
            searchDTOTmp.setTrustId(dto.getTrustId());
            searchDTOTmp.setProcessCode(dto.getProcessCode());
            searchDTOTmp.setPlanType(dto.getPlanType());
            List<TPrdWorkPlanDTO> workPlanList = tPrdWorkPlanMapper.getWorkPlanList(searchDTOTmp);
            if(!CollectionUtils.isEmpty(workPlanList)){
                throw new BusinessRuntimeException("作业编号为："
                        +workPlanList.get(0).getTrustNo()
                        +"，作业过程为:"
                        +workPlanList.get(0).getProcessName()
                        +"的计划在当前班次已经存在，请取消勾选之后再进行导入"
                );
            }

            List<TPrdWorkPlanDTO> tmpList = new ArrayList<>();

            dto.setId(snowflake.nextId());
            dto.setStatus(DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode());
            dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WROK_PLAN_TRANSPORT, null));

            // 按班次导入时，需要设置日期班次
            if (workDate != null) {
                dto.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
                dto.setClassCode(classCode);
                dto.setClassName(className);
            }
            // 前沿
            if (dto.getEqumentListFront() != null && dto.getEqumentListFront().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListFront()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.FRONT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.FRONT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 机械信息（后场）
            if (dto.getEqumentListBack() != null && dto.getEqumentListBack().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListBack()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.BACK.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.BACK.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 辅助
            if (dto.getEqumentListAssist() != null && dto.getEqumentListAssist().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListAssist()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.ASSIST.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.ASSIST.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 转运（水平）
            if (dto.getEqumentListReshipment() != null && dto.getEqumentListReshipment().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListReshipment()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.RESHIPMENT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.RESHIPMENT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }

            // 劳务信息
            if (dto.getLaborNum() != null && dto.getLaborNum().intValue() > 0) {
                TPrdDispatchDTO dispatchDTO = new TPrdDispatchDTO();
                dispatchDTO.setWorkPlanId(dto.getId());
                dispatchDTO.setId(snowflake.nextId());
                dispatchDTO.setNum(dto.getLaborNum());
                dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.LABOR.getCode());
                dispatchList.add(dispatchDTO);
            }

            // 源垛位信息
            if (dto.getLocationListSource() != null && dto.getLocationListSource().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListSource()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.SOURCE.getCode());
                    locationList.add(locationDTO);
                }
            }

            // 目标垛位信息
            if (dto.getLocationListTarget() != null && dto.getLocationListTarget().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListTarget()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.TARGET.getCode());
                    locationList.add(locationDTO);
                }
            }
            tmpList.add(dto);
            tPrdWorkPlanMapper.insertBatch( tmpList , securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());

        }

        // 劳务信息 新增
        if (dispatchList.size() > 0) {
            tPrdDispatchMapper.insertBatch(dispatchList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        // 新增位置表
        if (locationList.size() > 0) {
            tPrdWorkPlanLocationMapper.insertBatch(locationList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        return true;
    }


    /**
     * 批量导入集疏港计划
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addJSGBatch(List<TPrdWorkPlanDTO> dtos, String workDate, String classCode, String className ) {
        //获取工班计划id
        List<Long> ids = dtos.stream().filter(x->x.getId()!=null).map(TPrdWorkPlanDTO::getId).distinct().collect(Collectors.toList());
        List<Long> trustIds = dtos.stream().filter(x->x.getId()==null).map(TPrdWorkPlanDTO::getTrustId).distinct().collect(Collectors.toList());

        if(CollectionUtils.isEmpty(ids) && CollectionUtils.isEmpty(trustIds)){
            throw new BusinessRuntimeException("请选择数据之后再进行导入操作");
        }
        if(!CollectionUtils.isEmpty(ids)) {
            TPrdWorkPlanSearchDTO searchDTO = new TPrdWorkPlanSearchDTO();
            searchDTO.setIds(ids);
            List<TPrdWorkPlanDTO> oldList = tPrdWorkPlanMapper.getWorkPlanList(searchDTO);

            if (oldList == null || oldList.size() == 0) {
                throw new BusinessRuntimeException("选择的工班计划不存在~");
            }

            if (!CollectionUtils.isEmpty(ids)) {
                //位置信息 原位置和目的位置
                List<TPrdWorkPlanLocationDTO> workPlanLocationList = tPrdWorkPlanLocationMapper.getWorkPlanLocationList(ids);
                //装卸队
                //配机配工
                List<TPrdDispatchDTO> dispatchList = tPrdWorkPlanMapper.getPrdDispatch(searchDTO);
                Map<String, List<TPrdDispatchDTO>> dispatchMap = new HashMap<>();
                //回显配机列表
                if (!CollectionUtils.isEmpty(dispatchList)) {
                    dispatchMap = dispatchList.stream().collect(Collectors.groupingBy(o -> {
                        return o.getWorkPlanId() + "/" + o.getDispatchType() + "/" + o.getWorkPositionCode();
                    }));
                }

                HashMap<String, String> machineStrins = new HashMap<>();
                if (!CollectionUtils.isEmpty(dispatchMap)) {
                    dispatchMap.forEach((k, v) -> {
                        String s = "";
                        if (!CollectionUtils.isEmpty(v)) {

                            for (int i = 0; i < v.size(); i++) {
                                if (i == 0) {
                                    s = v.get(i).getEquipmentTypeName() + v.get(i).getNum();
                                } else {
                                    s += "," + v.get(i).getEquipmentTypeName() + v.get(i).getNum();
                                }
                            }

                        }
                        machineStrins.put(k, s);
                    });
                }


                for (TPrdWorkPlanDTO o : oldList) {


                    for (TPrdWorkPlanLocationDTO dto : workPlanLocationList) {
                        if (o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("1")) {
                            if (CollectionUtils.isEmpty(o.getLocationListSource())) {
                                o.setLocationListSource(Lists.newArrayList());
                            }
                            o.getLocationListSource().add(dto);
                        }

                        if (o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("2")) {
                            if (CollectionUtils.isEmpty(o.getLocationListTarget())) {
                                o.setLocationListTarget(Lists.newArrayList());
                            }
                            o.getLocationListTarget().add(dto);
                        }
                    }

                    if (!CollectionUtils.isEmpty(dispatchMap)){
                        //前沿
                        if (!CollectionUtils.isEmpty(dispatchMap.get((o.getId() + "/" + "1/01")))) {
                            o.setEqumentListFront(dispatchMap.get((o.getId() + "/" + "1/01")));
                            o.setEquipmentNamesFront(machineStrins.get((o.getId() + "/" + "1/01")));
                        }
                        //后沿
                        if (!CollectionUtils.isEmpty(dispatchMap.get((o.getId() + "/" + "1/02")))) {
                            o.setEqumentListBack(dispatchMap.get((o.getId() + "/" + "1/02")));
                            o.setEquipmentNamesBack(machineStrins.get((o.getId() + "/" + "1/02")));

                        }
                        //水平
                        if (!CollectionUtils.isEmpty(dispatchMap.get((o.getId() + "/" + "1/03")))) {
                            o.setEqumentListReshipment(dispatchMap.get((o.getId() + "/" + "1/03")));
                            o.setEquipmentNamesReshipment(machineStrins.get((o.getId() + "/" + "1/03")));

                        }
                        //辅助
                        if (!CollectionUtils.isEmpty(dispatchMap.get((o.getId() + "/" + "1/04")))) {
                            o.setEqumentListAssist(dispatchMap.get((o.getId() + "/" + "1/04")));
                            o.setEquipmentNamesAssist(machineStrins.get((o.getId() + "/" + "1/04")));
                        }
                        //装卸队
                        if (!CollectionUtils.isEmpty(dispatchMap.get((o.getId() + "/" + "2/null")))) {
                            o.setLaborNum(dispatchMap.get((o.getId() + "/" + "2/null")).get(0).getNum());
                        }
                    }
                }
            }

            List<TPrdDispatchDTO> dispatchList = new ArrayList<>();
            List<TPrdWorkPlanLocationDTO> locationList = new ArrayList<>();

            for (TPrdWorkPlanDTO dto : oldList) {

                TPrdWorkPlanSearchDTO searchDTOTmp = new TPrdWorkPlanSearchDTO();
                searchDTOTmp.setClassCode(classCode);
                searchDTOTmp.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
                searchDTOTmp.setTrustId(dto.getTrustId());
                searchDTOTmp.setProcessCode(dto.getProcessCode());
                searchDTOTmp.setPlanType(dto.getPlanType());
                List<TPrdWorkPlanDTO> workPlanList = tPrdWorkPlanMapper.getWorkPlanList(searchDTOTmp);
                if (!CollectionUtils.isEmpty(workPlanList)) {
                    throw new BusinessRuntimeException("作业编号为："
                            + workPlanList.get(0).getTrustNo()
                            + "，作业过程为:"
                            + workPlanList.get(0).getProcessName()
                            + "的计划在当前班次已经存在，请取消勾选之后再进行导入"
                    );
                }

                List<TPrdWorkPlanDTO> tmpList = new ArrayList<>();

                dto.setId(snowflake.nextId());
                dto.setStatus(DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode());
                dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WROK_PLAN_TRANSPORT, null));

                // 按班次导入时，需要设置日期班次
                if (workDate != null) {
                    dto.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
                    dto.setClassCode(classCode);
                    dto.setClassName(className);
                }
                // 前沿
                if (dto.getEqumentListFront() != null && dto.getEqumentListFront().size() > 0) {
                    for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListFront()) {
                        if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                            dispatchDTO.setWorkPlanId(dto.getId());
                            dispatchDTO.setId(snowflake.nextId());
                            dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                            dispatchDTO.setWorkPositionCode(MachineLocationEnum.FRONT.getCode());
                            dispatchDTO.setWorkPositionName(MachineLocationEnum.FRONT.getName());
                            dispatchList.add(dispatchDTO);
                        }
                    }
                }
                // 机械信息（后场）
                if (dto.getEqumentListBack() != null && dto.getEqumentListBack().size() > 0) {
                    for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListBack()) {
                        if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                            dispatchDTO.setWorkPlanId(dto.getId());
                            dispatchDTO.setId(snowflake.nextId());
                            dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                            dispatchDTO.setWorkPositionCode(MachineLocationEnum.BACK.getCode());
                            dispatchDTO.setWorkPositionName(MachineLocationEnum.BACK.getName());
                            dispatchList.add(dispatchDTO);
                        }
                    }
                }
                // 辅助
                if (dto.getEqumentListAssist() != null && dto.getEqumentListAssist().size() > 0) {
                    for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListAssist()) {
                        if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                            dispatchDTO.setWorkPlanId(dto.getId());
                            dispatchDTO.setId(snowflake.nextId());
                            dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                            dispatchDTO.setWorkPositionCode(MachineLocationEnum.ASSIST.getCode());
                            dispatchDTO.setWorkPositionName(MachineLocationEnum.ASSIST.getName());
                            dispatchList.add(dispatchDTO);
                        }
                    }
                }
                // 转运（水平）
                if (dto.getEqumentListReshipment() != null && dto.getEqumentListReshipment().size() > 0) {
                    for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListReshipment()) {
                        if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                            dispatchDTO.setWorkPlanId(dto.getId());
                            dispatchDTO.setId(snowflake.nextId());
                            dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                            dispatchDTO.setWorkPositionCode(MachineLocationEnum.RESHIPMENT.getCode());
                            dispatchDTO.setWorkPositionName(MachineLocationEnum.RESHIPMENT.getName());
                            dispatchList.add(dispatchDTO);
                        }
                    }
                }

                // 劳务信息
                if (dto.getLaborNum() != null && dto.getLaborNum().intValue() > 0) {
                    TPrdDispatchDTO dispatchDTO = new TPrdDispatchDTO();
                    dispatchDTO.setWorkPlanId(dto.getId());
                    dispatchDTO.setId(snowflake.nextId());
                    dispatchDTO.setNum(dto.getLaborNum());
                    dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.LABOR.getCode());
                    dispatchList.add(dispatchDTO);
                }

                // 源垛位信息
                if (dto.getLocationListSource() != null && dto.getLocationListSource().size() > 0) {
                    for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListSource()) {
                        locationDTO.setWorkPlanId(dto.getId());
                        locationDTO.setId(snowflake.nextId());
                        locationDTO.setDirection(DirectionEnum.SOURCE.getCode());
                        locationList.add(locationDTO);
                    }
                }

                // 目标垛位信息
                if (dto.getLocationListTarget() != null && dto.getLocationListTarget().size() > 0) {
                    for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListTarget()) {
                        locationDTO.setWorkPlanId(dto.getId());
                        locationDTO.setId(snowflake.nextId());
                        locationDTO.setDirection(DirectionEnum.TARGET.getCode());
                        locationList.add(locationDTO);
                    }
                }
                tmpList.add(dto);
                tPrdWorkPlanMapper.insertBatch(tmpList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());

            }

            // 劳务信息 新增
            if (dispatchList.size() > 0) {
                tPrdDispatchMapper.insertBatch(dispatchList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
            }

            // 新增位置表
            if (locationList.size() > 0) {
                tPrdWorkPlanLocationMapper.insertBatch(locationList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
            }
        }

        //没有下过集疏港昼夜计划（没有作业计划id的）进行导入指令操作
        if(!CollectionUtils.isEmpty(trustIds)){
            SpringUtils.getBean(this.getClass()).insertOpenPortTrust(trustIds, workDate, classCode, className);
        }

        return true;
    }

    /**
     *
     * 集疏港专用
     * @param trustIds
     * @param workDate
     * @param classCode
     * @param className
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOpenPortTrust(List<Long> trustIds, String workDate, String classCode, String className) {


        if (StringUtil.isEmpty(workDate) || StringUtil.isEmpty(classCode)) {
            throw new BusinessRuntimeException("请选择要导入的日期班次~");
        }

        if (trustIds == null || trustIds.size() == 0) {
            throw new BusinessRuntimeException("请选择要导入的指令信息~");
        }
        int count = 0;

        for (Long trustId : trustIds) {
            // 新建申请
            TPrdWorkPlanDTO dto = new TPrdWorkPlanDTO();
            dto.setId(snowflake.nextId());
            dto.setStatus(DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode());
            dto.setDeptId(securityUtils.getUserInfo().getDeptId());
            dto.setDeptName(securityUtils.getUserInfo().getDeptName());
            dto.setTrustId(trustId);
            dto.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
            dto.setClassCode(classCode);
            dto.setClassName(className);
            dto.setPlanType("2");
            // 生成计划号  集疏港
            dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WROK_PLAN_TRANSPORT, null));

            List<Long> paramTrustIds = Lists.newArrayList();
            paramTrustIds.add(trustId);
            HashMap<String, Object> map = new HashMap<>();
            map.put("trustIds", paramTrustIds);

            List<Map<String, Object>> trustList = selectMapper.getPopupTrust(map);
            // 查询指令信息组合船舶工班计划及工班计划-指令信息
            if (trustList == null || trustList.size() == 0) {
                throw new BusinessRuntimeException("指令信息不存在~");
            }
            List<TDisShipDaynigttplanPO> dayNigttPlanList = tPrdWorkPlanMapper.getDayNigttPlan(trustId, workDate);
            if (!CollectionUtils.isEmpty(dayNigttPlanList)) {
                TDisShipDaynigttplanPO disShipDaynigttplanPO = dayNigttPlanList.get(0);

                dto.setBerthId(disShipDaynigttplanPO.getBerthId());
                dto.setBerthName(disShipDaynigttplanPO.getBerthName());
                if("01".equals(classCode)) {// 白班
                    dto.setTonPlan(disShipDaynigttplanPO.getMornWorkNum());
                } else if("02".equals(classCode)) {// 夜班
                    dto.setTonPlan(disShipDaynigttplanPO.getNightWorkNum());
                }
                dto.setStartTimePlan(disShipDaynigttplanPO.getStarttimePlan());// 计划开工时间
                dto.setEndTimePlan(disShipDaynigttplanPO.getEndtimePlan());// 计划完工时间
            }
            dto.setCompanyId(StringUtil.getLong(trustList.get(0).get("companyId")));
            dto.setCompanyName(StringUtil.getString(trustList.get(0).get("companyName")));
            dto.setProcessCode(StringUtil.getString(trustList.get(0).get("processCode")));
            dto.setProcessName(StringUtil.getString(trustList.get(0).get("processName")));
            dto.setBerthId(trustList.get(0).get("berthId") == null ? null : StringUtil.getLong(trustList.get(0).get("berthId")));
            dto.setBerthName(trustList.get(0).get("berthName") == null ? null : StringUtil.getString(trustList.get(0).get("berthName")));

            count += tPrdWorkPlanMapper.insert(dto);
        }

        return count>0;
    }

    @Override
    public List<Map<String, String>> workProcessType(Long type) {
        return  tPrdWorkPlanMapper.workProcessType(type);
    }

    @Override
    public List<Map<String, String>> workProcessType(Long type, String dictValue) {
        return  tPrdWorkPlanMapper.workProcessType2(type,dictValue);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean copyWorkPlan(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            throw new BusinessRuntimeException("请选择数据之后再进行复制操作");
        }

        TPrdWorkPlanSearchDTO searchDTO = new TPrdWorkPlanSearchDTO();
        searchDTO.setIds(ids);
        List<TPrdWorkPlanDTO> oldList = tPrdWorkPlanMapper.getWorkPlanList(searchDTO);

        if(!CollectionUtils.isEmpty(ids)){
            //位置信息 原位置和目的位置
            List<TPrdWorkPlanLocationDTO>  workPlanLocationList=  tPrdWorkPlanLocationMapper.getWorkPlanLocationList(ids) ;
            //装卸队
            //配机配工
            List<TPrdDispatchDTO> dispatchList = tPrdWorkPlanMapper.getPrdDispatch(searchDTO);
            Map<String, List<TPrdDispatchDTO>> dispatchMap = new HashMap<>();
            //回显配机列表
            if (!CollectionUtils.isEmpty(dispatchList)){
                dispatchMap = dispatchList.stream().collect(Collectors.groupingBy(o -> {
                    return o.getWorkPlanId() + "/" + o.getDispatchType() + "/" + o.getWorkPositionCode();
                }));
            }

            HashMap<String, String> machineStrins = new HashMap<>();
            if(!CollectionUtils.isEmpty(dispatchMap)){
                dispatchMap.forEach((k,v)->{
                    String s = "";
                    if(!CollectionUtils.isEmpty(v)){

                        for (int i = 0; i < v.size(); i++) {
                            if(i==0){
                                s = v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                            }else {
                                s+=","+v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                            }
                        }

                    }
                    machineStrins.put(k,s);
                });
            }

            for (TPrdWorkPlanDTO o : oldList) {

                for (TPrdWorkPlanLocationDTO dto : workPlanLocationList) {
                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("1")){
                        if(CollectionUtils.isEmpty(o.getLocationListSource())){
                            o.setLocationListSource(Lists.newArrayList());
                        }
                        o.getLocationListSource().add(dto);
                    }

                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("2")){
                        if(CollectionUtils.isEmpty(o.getLocationListTarget())){
                            o.setLocationListTarget(Lists.newArrayList());
                        }
                        o.getLocationListTarget().add(dto);
                    }
                }

                if(!CollectionUtils.isEmpty(dispatchMap)){
                    //前沿
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/01")))){
                        o.setEqumentListFront(dispatchMap.get((o.getId()+"/"+"1/01")));
                        o.setEquipmentNamesFront(machineStrins.get((o.getId()+"/"+"1/01")));
                    }
                    //后沿
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/02")))){
                        o.setEqumentListBack(dispatchMap.get((o.getId()+"/"+"1/02")));
                        o.setEquipmentNamesBack(machineStrins.get((o.getId()+"/"+"1/02")));

                    }
                    //水平
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/03")))){
                        o.setEqumentListReshipment(dispatchMap.get((o.getId()+"/"+"1/03")));
                        o.setEquipmentNamesReshipment(machineStrins.get((o.getId()+"/"+"1/03")));

                    }
                    //辅助
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/04")))){
                        o.setEqumentListAssist(dispatchMap.get((o.getId()+"/"+"1/04")));
                        o.setEquipmentNamesAssist(machineStrins.get((o.getId()+"/"+"1/04")));
                    }
                    //装卸队
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"2/null")))){
                        o.setLaborNum(dispatchMap.get((o.getId()+"/"+"2/null")).get(0).getNum());
                    }
                }
            }
        }


        if (oldList == null || oldList.size() == 0) {
            throw new BusinessRuntimeException("选择的工班计划不存在~");
        }

        List<TPrdDispatchDTO> dispatchList = new ArrayList<>();
        List<TPrdWorkPlanLocationDTO> locationList = new ArrayList<>();

        for (TPrdWorkPlanDTO dto : oldList) {

            /*TPrdWorkPlanSearchDTO searchDTOTmp = new TPrdWorkPlanSearchDTO();
            searchDTOTmp.setClassCode(dto.getClassCode());
            searchDTOTmp.setWorkDate(dto.getWorkDate());
            searchDTOTmp.setTrustId(dto.getTrustId());
            //复制回显为空
            searchDTOTmp.setProcessCode(dto.getProcessCode());
            searchDTOTmp.setPlanType(dto.getPlanType());
            List<TPrdWorkPlanDTO> workPlanList = tPrdWorkPlanMapper.getWorkPlanList(searchDTOTmp);
            if(!CollectionUtils.isEmpty(workPlanList)){
                throw new BusinessRuntimeException("作业编号为："
                        +workPlanList.get(0).getTrustNo()
                        +"，作业过程为:"
                        +workPlanList.get(0).getProcessName()
                        +"的计划在当前班次已经存在，请取消勾选之后再进行导入"
                );
            }*/


            List<TPrdWorkPlanDTO> tmpList = new ArrayList<>();

            dto.setId(snowflake.nextId());
            dto.setStatus(DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode());
            dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WORK_PLAN_SHIP, null));
            dto.setProcessCode("");
            dto.setProcessName("");
            /*// 按班次导入时，需要设置日期班次
            if (workDate != null) {
                dto.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
                dto.setClassCode(classCode);
                dto.setClassName(className);
            }*/
            // 前沿
            if (dto.getEqumentListFront() != null && dto.getEqumentListFront().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListFront()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.FRONT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.FRONT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 机械信息（后场）
            if (dto.getEqumentListBack() != null && dto.getEqumentListBack().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListBack()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.BACK.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.BACK.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 辅助
            if (dto.getEqumentListAssist() != null && dto.getEqumentListAssist().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListAssist()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.ASSIST.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.ASSIST.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 转运（水平）
            if (dto.getEqumentListReshipment() != null && dto.getEqumentListReshipment().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListReshipment()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.RESHIPMENT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.RESHIPMENT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }

            // 劳务信息
            if (dto.getLaborNum() != null && dto.getLaborNum().intValue() > 0) {
                TPrdDispatchDTO dispatchDTO = new TPrdDispatchDTO();
                dispatchDTO.setWorkPlanId(dto.getId());
                dispatchDTO.setId(snowflake.nextId());
                dispatchDTO.setNum(dto.getLaborNum());
                dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.LABOR.getCode());
                dispatchList.add(dispatchDTO);
            }

            // 源垛位信息
            if (dto.getLocationListSource() != null && dto.getLocationListSource().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListSource()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.SOURCE.getCode());
                    locationList.add(locationDTO);
                }
            }

            // 目标垛位信息
            if (dto.getLocationListTarget() != null && dto.getLocationListTarget().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListTarget()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.TARGET.getCode());
                    locationList.add(locationDTO);
                }
            }
            tmpList.add(dto);
            tPrdWorkPlanMapper.insertBatch( tmpList , securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());

        }

        // 劳务信息 新增
        if (dispatchList.size() > 0) {
            tPrdDispatchMapper.insertBatch(dispatchList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        // 新增位置表
        if (locationList.size() > 0) {
            tPrdWorkPlanLocationMapper.insertBatch(locationList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean copyJSGWorkPlan(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            throw new BusinessRuntimeException("请选择数据之后再进行导入操作");
        }

        TPrdWorkPlanSearchDTO searchDTO = new TPrdWorkPlanSearchDTO();
        searchDTO.setIds(ids);
        List<TPrdWorkPlanDTO> oldList = tPrdWorkPlanMapper.getWorkPlanList(searchDTO);

        if (oldList == null || oldList.size() == 0) {
            throw new BusinessRuntimeException("选择的工班计划不存在~");
        }

        if(!CollectionUtils.isEmpty(ids)){
            //位置信息 原位置和目的位置
            List<TPrdWorkPlanLocationDTO>  workPlanLocationList=  tPrdWorkPlanLocationMapper.getWorkPlanLocationList(ids) ;
            //装卸队
            //配机配工
            List<TPrdDispatchDTO> dispatchList = tPrdWorkPlanMapper.getPrdDispatch(searchDTO);
            Map<String, List<TPrdDispatchDTO>> dispatchMap = new HashMap<>();
            //回显配机列表
            if (!CollectionUtils.isEmpty(dispatchList)){
                dispatchMap = dispatchList.stream().collect(Collectors.groupingBy(o -> {
                    return o.getWorkPlanId() + "/" + o.getDispatchType() + "/" + o.getWorkPositionCode();
                }));
            }

            HashMap<String, String> machineStrins = new HashMap<>();
            if(!CollectionUtils.isEmpty(dispatchMap)){
                dispatchMap.forEach((k,v)->{
                    String s = "";
                    if(!CollectionUtils.isEmpty(v)){

                        for (int i = 0; i < v.size(); i++) {
                            if(i==0){
                                s = v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                            }else {
                                s+=","+v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                            }
                        }

                    }
                    machineStrins.put(k,s);
                });
            }


            for (TPrdWorkPlanDTO o : oldList) {



                for (TPrdWorkPlanLocationDTO dto : workPlanLocationList) {
                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("1")){
                        if(CollectionUtils.isEmpty(o.getLocationListSource())){
                            o.setLocationListSource(Lists.newArrayList());
                        }
                        o.getLocationListSource().add(dto);
                    }

                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("2")){
                        if(CollectionUtils.isEmpty(o.getLocationListTarget())){
                            o.setLocationListTarget(Lists.newArrayList());
                        }
                        o.getLocationListTarget().add(dto);
                    }
                }

                if(!CollectionUtils.isEmpty(dispatchMap)){
                    //前沿
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/01")))){
                        o.setEqumentListFront(dispatchMap.get((o.getId()+"/"+"1/01")));
                        o.setEquipmentNamesFront(machineStrins.get((o.getId()+"/"+"1/01")));
                    }
                    //后沿
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/02")))){
                        o.setEqumentListBack(dispatchMap.get((o.getId()+"/"+"1/02")));
                        o.setEquipmentNamesBack(machineStrins.get((o.getId()+"/"+"1/02")));

                    }
                    //水平
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/03")))){
                        o.setEqumentListReshipment(dispatchMap.get((o.getId()+"/"+"1/03")));
                        o.setEquipmentNamesReshipment(machineStrins.get((o.getId()+"/"+"1/03")));

                    }
                    //辅助
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/04")))){
                        o.setEqumentListAssist(dispatchMap.get((o.getId()+"/"+"1/04")));
                        o.setEquipmentNamesAssist(machineStrins.get((o.getId()+"/"+"1/04")));
                    }
                    //装卸队
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"2/null")))){
                        o.setLaborNum(dispatchMap.get((o.getId()+"/"+"2/null")).get(0).getNum());
                    }
                }
            }
        }

        List<TPrdDispatchDTO> dispatchList = new ArrayList<>();
        List<TPrdWorkPlanLocationDTO> locationList = new ArrayList<>();

        for (TPrdWorkPlanDTO dto : oldList) {

            /*TPrdWorkPlanSearchDTO searchDTOTmp = new TPrdWorkPlanSearchDTO();
            searchDTOTmp.setClassCode(classCode);
            searchDTOTmp.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
            searchDTOTmp.setTrustId(dto.getTrustId());
            searchDTOTmp.setProcessCode(dto.getProcessCode());
            searchDTOTmp.setPlanType(dto.getPlanType());
            List<TPrdWorkPlanDTO> workPlanList = tPrdWorkPlanMapper.getWorkPlanList(searchDTOTmp);
            if(!CollectionUtils.isEmpty(workPlanList)){
                throw new BusinessRuntimeException("作业编号为："
                        +workPlanList.get(0).getTrustNo()
                        +"，作业过程为:"
                        +workPlanList.get(0).getProcessName()
                        +"的计划在当前班次已经存在，请取消勾选之后再进行导入"
                );
            }*/

            List<TPrdWorkPlanDTO> tmpList = new ArrayList<>();

            dto.setId(snowflake.nextId());
            dto.setStatus(DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode());
            dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WROK_PLAN_TRANSPORT, null));
            dto.setProcessCode("");
            dto.setProcessName("");

           /* // 按班次导入时，需要设置日期班次
            if (workDate != null) {
                dto.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
                dto.setClassCode(classCode);
                dto.setClassName(className);
            }*/
            // 前沿
            if (dto.getEqumentListFront() != null && dto.getEqumentListFront().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListFront()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.FRONT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.FRONT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 机械信息（后场）
            if (dto.getEqumentListBack() != null && dto.getEqumentListBack().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListBack()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.BACK.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.BACK.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 辅助
            if (dto.getEqumentListAssist() != null && dto.getEqumentListAssist().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListAssist()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.ASSIST.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.ASSIST.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 转运（水平）
            if (dto.getEqumentListReshipment() != null && dto.getEqumentListReshipment().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListReshipment()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.RESHIPMENT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.RESHIPMENT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }

            // 劳务信息
            if (dto.getLaborNum() != null && dto.getLaborNum().intValue() > 0) {
                TPrdDispatchDTO dispatchDTO = new TPrdDispatchDTO();
                dispatchDTO.setWorkPlanId(dto.getId());
                dispatchDTO.setId(snowflake.nextId());
                dispatchDTO.setNum(dto.getLaborNum());
                dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.LABOR.getCode());
                dispatchList.add(dispatchDTO);
            }

            // 源垛位信息
            if (dto.getLocationListSource() != null && dto.getLocationListSource().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListSource()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.SOURCE.getCode());
                    locationList.add(locationDTO);
                }
            }

            // 目标垛位信息
            if (dto.getLocationListTarget() != null && dto.getLocationListTarget().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListTarget()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.TARGET.getCode());
                    locationList.add(locationDTO);
                }
            }
            tmpList.add(dto);
            tPrdWorkPlanMapper.insertBatch( tmpList , securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());

        }

        // 劳务信息 新增
        if (dispatchList.size() > 0) {
            tPrdDispatchMapper.insertBatch(dispatchList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        // 新增位置表
        if (locationList.size() > 0) {
            tPrdWorkPlanLocationMapper.insertBatch(locationList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        return true;
    }

    @Override
    public boolean copyZYWorkPlan(List<Long> id) {
        if(CollectionUtils.isEmpty(id)){
            throw new BusinessRuntimeException("请选择数据之后再进行导入操作");
        }

        TPrdWorkPlanSearchDTO searchDTO = new TPrdWorkPlanSearchDTO();
        searchDTO.setIds(id);
        List<TPrdWorkPlanDTO> oldList = tPrdWorkPlanMapper.getWorkPlanList(searchDTO);

        if (oldList == null || oldList.size() == 0) {
            throw new BusinessRuntimeException("选择的工班计划不存在~");
        }

        if(!CollectionUtils.isEmpty(id)){
            //位置信息 原位置和目的位置
            List<TPrdWorkPlanLocationDTO>  workPlanLocationList=  tPrdWorkPlanLocationMapper.getWorkPlanLocationList(id) ;
            //装卸队
            //配机配工
            List<TPrdDispatchDTO> dispatchList = tPrdWorkPlanMapper.getPrdDispatch(searchDTO);
            Map<String, List<TPrdDispatchDTO>> dispatchMap = new HashMap<>();
            //回显配机列表
            if (!CollectionUtils.isEmpty(dispatchList)){
                dispatchMap = dispatchList.stream().collect(Collectors.groupingBy(o -> {
                    return o.getWorkPlanId() + "/" + o.getDispatchType() + "/" + o.getWorkPositionCode();
                }));
            }

            HashMap<String, String> machineStrins = new HashMap<>();
            if(!CollectionUtils.isEmpty(dispatchMap)){
                dispatchMap.forEach((k,v)->{
                    String s = "";
                    if(!CollectionUtils.isEmpty(v)){

                        for (int i = 0; i < v.size(); i++) {
                            if(i==0){
                                s = v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                            }else {
                                s+=","+v.get(i).getEquipmentTypeName()+v.get(i).getNum();
                            }
                        }

                    }
                    machineStrins.put(k,s);
                });
            }


            for (TPrdWorkPlanDTO o : oldList) {



                for (TPrdWorkPlanLocationDTO dto : workPlanLocationList) {
                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("1")){
                        if(CollectionUtils.isEmpty(o.getLocationListSource())){
                            o.setLocationListSource(Lists.newArrayList());
                        }
                        o.getLocationListSource().add(dto);
                    }

                    if(o.getId().equals(dto.getWorkPlanId()) && dto.getDirection().equals("2")){
                        if(CollectionUtils.isEmpty(o.getLocationListTarget())){
                            o.setLocationListTarget(Lists.newArrayList());
                        }
                        o.getLocationListTarget().add(dto);
                    }
                }

                if(!CollectionUtils.isEmpty(dispatchMap)){
                    //前沿
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/01")))){
                        o.setEqumentListFront(dispatchMap.get((o.getId()+"/"+"1/01")));
                        o.setEquipmentNamesFront(machineStrins.get((o.getId()+"/"+"1/01")));
                    }
                    //后沿
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/02")))){
                        o.setEqumentListBack(dispatchMap.get((o.getId()+"/"+"1/02")));
                        o.setEquipmentNamesBack(machineStrins.get((o.getId()+"/"+"1/02")));

                    }
                    //水平
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/03")))){
                        o.setEqumentListReshipment(dispatchMap.get((o.getId()+"/"+"1/03")));
                        o.setEquipmentNamesReshipment(machineStrins.get((o.getId()+"/"+"1/03")));

                    }
                    //辅助
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"1/04")))){
                        o.setEqumentListAssist(dispatchMap.get((o.getId()+"/"+"1/04")));
                        o.setEquipmentNamesAssist(machineStrins.get((o.getId()+"/"+"1/04")));
                    }
                    //装卸队
                    if(!CollectionUtils.isEmpty(dispatchMap.get((o.getId()+"/"+"2/null")))){
                        o.setLaborNum(dispatchMap.get((o.getId()+"/"+"2/null")).get(0).getNum());
                    }
                }
            }
        }

        List<TPrdDispatchDTO> dispatchList = new ArrayList<>();
        List<TPrdWorkPlanLocationDTO> locationList = new ArrayList<>();

        for (TPrdWorkPlanDTO dto : oldList) {

            /*TPrdWorkPlanSearchDTO searchDTOTmp = new TPrdWorkPlanSearchDTO();
            searchDTOTmp.setClassCode(classCode);
            searchDTOTmp.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
            searchDTOTmp.setTrustId(dto.getTrustId());
            searchDTOTmp.setProcessCode(dto.getProcessCode());
            searchDTOTmp.setPlanType(dto.getPlanType());
            List<TPrdWorkPlanDTO> workPlanList = tPrdWorkPlanMapper.getWorkPlanList(searchDTOTmp);
            if(!CollectionUtils.isEmpty(workPlanList)){
                throw new BusinessRuntimeException("作业编号为："
                        +workPlanList.get(0).getTrustNo()
                        +"，作业过程为:"
                        +workPlanList.get(0).getProcessName()
                        +"的计划在当前班次已经存在，请取消勾选之后再进行导入"
                );
            }*/

            List<TPrdWorkPlanDTO> tmpList = new ArrayList<>();

            dto.setId(snowflake.nextId());
            dto.setStatus(DispatchEnum.WorkPlanStatusEnum.TODO_REVIEW.getCode());
            dto.setPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WORK_PLAN_RESHIPMENT, null));
            dto.setProcessCode("");
            dto.setProcessName("");

           /* // 按班次导入时，需要设置日期班次
            if (workDate != null) {
                dto.setWorkDate(DateUtils.parseDate(workDate, CommonEnum.DateFormatType.E_1.getCode()));
                dto.setClassCode(classCode);
                dto.setClassName(className);
            }*/
            // 前沿
            if (dto.getEqumentListFront() != null && dto.getEqumentListFront().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListFront()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.FRONT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.FRONT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 机械信息（后场）
            if (dto.getEqumentListBack() != null && dto.getEqumentListBack().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListBack()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.BACK.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.BACK.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 辅助
            if (dto.getEqumentListAssist() != null && dto.getEqumentListAssist().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListAssist()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.ASSIST.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.ASSIST.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }
            // 转运（水平）
            if (dto.getEqumentListReshipment() != null && dto.getEqumentListReshipment().size() > 0) {
                for (TPrdDispatchDTO dispatchDTO : dto.getEqumentListReshipment()) {
                    if (dispatchDTO.getNum() != null || !StringUtil.isEmpty(dispatchDTO.getEquipmentIds())) {
                        dispatchDTO.setWorkPlanId(dto.getId());
                        dispatchDTO.setId(snowflake.nextId());
                        dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.EQUIP.getCode());
                        dispatchDTO.setWorkPositionCode(MachineLocationEnum.RESHIPMENT.getCode());
                        dispatchDTO.setWorkPositionName(MachineLocationEnum.RESHIPMENT.getName());
                        dispatchList.add(dispatchDTO);
                    }
                }
            }

            // 劳务信息
            if (dto.getLaborNum() != null && dto.getLaborNum().intValue() > 0) {
                TPrdDispatchDTO dispatchDTO = new TPrdDispatchDTO();
                dispatchDTO.setWorkPlanId(dto.getId());
                dispatchDTO.setId(snowflake.nextId());
                dispatchDTO.setNum(dto.getLaborNum());
                dispatchDTO.setDispatchType(DispatchEnum.DispatchTypeEnum.LABOR.getCode());
                dispatchList.add(dispatchDTO);
            }

            // 源垛位信息
            if (dto.getLocationListSource() != null && dto.getLocationListSource().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListSource()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.SOURCE.getCode());
                    locationList.add(locationDTO);
                }
            }

            // 目标垛位信息
            if (dto.getLocationListTarget() != null && dto.getLocationListTarget().size() > 0) {
                for (TPrdWorkPlanLocationDTO locationDTO : dto.getLocationListTarget()) {
                    locationDTO.setWorkPlanId(dto.getId());
                    locationDTO.setId(snowflake.nextId());
                    locationDTO.setDirection(DirectionEnum.TARGET.getCode());
                    locationList.add(locationDTO);
                }
            }
            tmpList.add(dto);
            tPrdWorkPlanMapper.insertBatch( tmpList , securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());

        }

        // 劳务信息 新增
        if (dispatchList.size() > 0) {
            tPrdDispatchMapper.insertBatch(dispatchList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        // 新增位置表
        if (locationList.size() > 0) {
            tPrdWorkPlanLocationMapper.insertBatch(locationList, securityUtils.getLoginUserId(), securityUtils.getLoginUserName(), new Date());
        }

        return true;
    }

     @Override
    public List<ResponsePopupTrustDTO> getJSGDayNightWorkPlanList(TPrdWorkPlanSearchDTO searchDTO) {
        return tPrdWorkPlanMapper.getJSGDayNightWorkPlanList(searchDTO);
    }

    @Override
    public List<Map<String, Object>> getMassIdsWithPlanId(Long planId,String tmpParam) {
        List<Map<String, Object>> massIdsWithPlanId = tPrdWorkPlanMapper.getMassIdsWithPlanId(planId);
        //设置计划类型
        if (!massIdsWithPlanId.isEmpty()) {
            Map<String, Object> planTypeById = tPrdWorkPlanMapper.getPlanTypeById(planId);
            if (!planTypeById.isEmpty()){
                for (Map<String, Object> stringObjectMap : massIdsWithPlanId) {
                    stringObjectMap.put("planType",String.valueOf(planTypeById.get("planType")));
                    stringObjectMap.put("targetCd",String.valueOf(planTypeById.get("targetCd")));
                    stringObjectMap.put("sourceCd",String.valueOf(planTypeById.get("sourceCd")));
                }
            }
        }else if("YES".equals(tmpParam)){
            Map<String, Object> planTypeById = tPrdWorkPlanMapper.getPlanTypeById(planId);
            Map<String, Object> stringObjectMap = new HashMap<>();
            stringObjectMap.put("planType",String.valueOf(planTypeById.get("planType")));
            stringObjectMap.put("targetCd",String.valueOf(planTypeById.get("targetCd")));
            stringObjectMap.put("sourceCd",String.valueOf(planTypeById.get("sourceCd")));
            massIdsWithPlanId = new ArrayList<>();
            massIdsWithPlanId.add(stringObjectMap);
        }
        return  massIdsWithPlanId;
    }
}

