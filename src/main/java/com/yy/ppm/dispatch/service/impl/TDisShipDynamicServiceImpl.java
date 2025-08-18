package com.yy.ppm.dispatch.service.impl;

import static com.yy.ppm.common.enums.LoadUnloadEnum.LOAD;
import static com.yy.ppm.common.enums.LoadUnloadEnum.LOAD_UNLOAD;
import static com.yy.ppm.common.enums.LoadUnloadEnum.UNLOAD;
import static com.yy.ppm.common.enums.ShipStatusEnum.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Resource;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.yy.common.enums.ApiEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.DateUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.api.HttpUtils;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.produce.bean.SyncDTO;
import com.yy.ppm.dispatch.bean.dto.MSjsbLogDTO;
import com.yy.ppm.dispatch.bean.dto.TAnchApplyDTO;
import com.yy.ppm.dispatch.bean.po.*;
import com.yy.ppm.dispatch.mapper.MSjsbLogMapper;
import com.yy.ppm.dispatch.mapper.TAnchApplyMapper;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import com.yy.ppm.statement.mapper.TBusHandoverlistUnloadMapper;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.apache.ibatis.cursor.Cursor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.github.pagehelper.Page;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.concurrent.DistributedLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.DistributedLockKeyPrefixEnum;
import com.yy.ppm.common.enums.ShipStatusEnum;
import com.yy.ppm.common.service.SelectService;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicQueryDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageQueryDTO;
import com.yy.ppm.dispatch.mapper.TDisShipDynamicMapper;
import com.yy.ppm.dispatch.mapper.TDisShipVoyageMapper;
import com.yy.ppm.dispatch.service.TDisShipDynamicService;
import com.yy.ppm.master.bean.dto.MDictDataDTO;
import com.yy.ppm.master.service.MDictService;
import com.yy.ppm.statement.bean.dto.busHandoverlist.TDisShipvoyageItemDTO;
import com.yy.ppm.statement.bean.dto.busHandoverlist.UpdateBusHandoverlistDTO;
import com.yy.ppm.statement.service.TBusHandoverlistService;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-12 11:05
 */
@Service
public class TDisShipDynamicServiceImpl implements TDisShipDynamicService {

    @Resource
    private TDisShipDynamicMapper tDisShipDynamicMapper;

    @Resource
    private TDisShipVoyageMapper tDisShipVoyageMapper;
    @Resource
    private SecurityUtils securityUtils;
    @Autowired
    private Snowflake snowflake;

    @Resource
    private MDictService mDictService;
    @Resource
    private SelectService selectService;
    @Resource
    private TAnchApplyMapper anchApplyMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private TBusHandoverlistService tBusHandoverlistService;
    @Autowired
    private TBusHandoverlistUnloadMapper handoverlistUnloadMapper;
    @Resource
    private MSjsbLogMapper sjsbLogMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private SysParameterMapper sysParameterMapper;

    private static final MicroLogger LOGGER = new MicroLogger(TDisShipDynamicServiceImpl.class);


    private static final Map<String, ShipStatusEnum[]> STATUS_BRANCHS_MAP = new HashMap<String, ShipStatusEnum[]>() {{
        put(JIESHOU.getCode(), new ShipStatusEnum[]{DIMAO, KAOBO});
        put(DIMAO.getCode(), new ShipStatusEnum[]{KAOBO});
        put(KAOBO.getCode(), new ShipStatusEnum[]{YIBO,FUGONG, KAIGONG,LIBO,LIGANG});
        put(YIBO.getCode(), new ShipStatusEnum[]{FUGONG,KAIGONG,LIBO,LIGANG});
        put(KAIGONG.getCode(), new ShipStatusEnum[]{TINGGONG, WANGONG});
        put(TINGGONG.getCode(), new ShipStatusEnum[]{YIBO, FUGONG,LIBO});
        put(FUGONG.getCode(), new ShipStatusEnum[]{TINGGONG, WANGONG});
        put(WANGONG.getCode(), new ShipStatusEnum[]{KAIGONG, YIBO, LIBO,LIGANG}); // 只有当船舶有装卸航次且最后一次完工航次为卸时，下一步才能是开工
        put(LIBO.getCode(), new ShipStatusEnum[]{KAOBO,LIGANG,DIMAO});
    }};

    private static final int CURSOR_LIMIT = 5_000;

    @Override
    public Pages<TDisShipvoyageDTO> listDisShipVoyage(TDisShipvoyageQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            Page<TDisShipvoyageDTO> page = new Page<>();
            if("20".equals(query.getShipStatusBroadCode())){
                page = tDisShipDynamicMapper.listDisShipVoyageOrderByBerthId(query);
            }else if("30".equals(query.getShipStatusBroadCode())){
                page = tDisShipDynamicMapper.listDisShipVoyageOrderByLeabePortTime(query);
            }else{
                page = tDisShipDynamicMapper.listDisShipVoyage(query);
            }
            if(CollectionUtils.isEmpty(page.getResult())){
                return page;
            }
            Map<Long,Date> time = Maps.newHashMap();
            if(!"00".equals(query.getShipStatusBroadCode())){
                List<Long> shipVoyageIds = page.getResult().stream().map(TDisShipvoyageDTO::getId).collect(Collectors.toList());
                List<TDisShipDynamicDTO> dynamicDTOS = tDisShipDynamicMapper.listByShipVoyageIds(shipVoyageIds);
                Map<Long,List<TDisShipDynamicDTO>> map = dynamicDTOS.stream().collect(Collectors.groupingBy(TDisShipDynamicDTO::getShipvoyageId));
                map.forEach((k,v)->{
                    v.sort((b,a)->a.getDynamicStartTime().compareTo(b.getDynamicStartTime()));
                    time.put(k,v.get(0).getDynamicStartTime());
                });
            }
            page.getResult().forEach(v1 -> {
                if (Stream.of(UNLOAD, LOAD).anyMatch(v2 -> v1.getLoadUnload().equals(v2.getName()))) {
                    v1.setNextLoadUnload(v1.getLoadUnload());
                }
                if (LOAD_UNLOAD.getName().equals(v1.getLoadUnload())) {
                    TDisShipDynamicPO dynQuery = new TDisShipDynamicPO();
                    dynQuery.setShipvoyageId(v1.getId());
                    dynQuery.setDynamicTypeCode(WANGONG.getCode());
                    List<TDisShipDynamicDTO> disShipDynamics = tDisShipDynamicMapper.listDisShipDynamic(dynQuery);
                    if (disShipDynamics.isEmpty()) {
                        v1.setNextLoadUnload(UNLOAD.getName());
                    } else {
                        v1.setNextLoadUnload(LOAD.getName());
                    }

                    dynQuery = new TDisShipDynamicPO();
                    dynQuery.setShipvoyageId(v1.getId());
                    dynQuery.setDynamicTypeCode(WANGONG.getCode());
                    disShipDynamics = tDisShipDynamicMapper.listDisShipDynamic(dynQuery);
                    String lastEndWorkLoadUnload = disShipDynamics.stream()
                            .max(Comparator.comparing(TDisShipDynamicPO::getCreateTime))
                            .orElse(new TDisShipDynamicDTO()).getLoadUnload();
                    v1.setAllowStartWork(LOAD_UNLOAD.getName().equals(v1.getLoadUnload()) && UNLOAD.getName().equals(lastEndWorkLoadUnload));
                }
                if(!"00".equals(query.getShipStatusBroadCode())){
                    v1.setArrivalTimePlan(time.get(v1.getId()));
                }
            });
            return page;
        });
    }


    @Override
    public Pages<TDisShipvoyageDTO> listDisShipVoyageApp(TDisShipvoyageQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            Page<TDisShipvoyageDTO> page = tDisShipDynamicMapper.listDisShipVoyageApp(query);
            page.getResult().forEach(v1 -> {
              Map<String,Object> map = tDisShipDynamicMapper.getStatus(v1.getIn().getId());
              if(map != null){
                  v1.setShipStatusName(map.get("dynamicTypeName").toString());
              }else{
                  v1.setShipStatusName(v1.getIn().getShipStatusName());
              }
//                if (Stream.of(UNLOAD, LOAD).anyMatch(v2 -> v1.getLoadUnload().equals(v2.getName()))) {
//                    v1.setNextLoadUnload(v1.getLoadUnload());
//                }
//                if (LOAD_UNLOAD.getName().equals(v1.getLoadUnload())) {
//                    TDisShipDynamicPO dynQuery = new TDisShipDynamicPO();
//                    dynQuery.setShipvoyageId(v1.getId());
//                    dynQuery.setDynamicTypeCode(WANGONG.getCode());
//                    List<TDisShipDynamicDTO> disShipDynamics = tDisShipDynamicMapper.listDisShipDynamic(dynQuery);
//                    if (disShipDynamics.isEmpty()) {
//                        v1.setNextLoadUnload(UNLOAD.getName());
//                    } else {
//                        v1.setNextLoadUnload(LOAD.getName());
//                    }
//
//                    dynQuery = new TDisShipDynamicPO();
//                    dynQuery.setShipvoyageId(v1.getId());
//                    dynQuery.setDynamicTypeCode(WANGONG.getCode());
//                    disShipDynamics = tDisShipDynamicMapper.listDisShipDynamic(dynQuery);
//                    String lastEndWorkLoadUnload = disShipDynamics.stream()
//                            .max(Comparator.comparing(TDisShipDynamicPO::getCreateTime))
//                            .orElse(new TDisShipDynamicDTO()).getLoadUnload();
//                    v1.setAllowStartWork(LOAD_UNLOAD.getName().equals(v1.getLoadUnload()) && UNLOAD.getName().equals(lastEndWorkLoadUnload));
//                }
            });
            return page;
        });
    }


//    @Override
//    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
//    public void updateDisShipvoyageStatusApp(TDisShipDynamicDTO disShipDynamic) {
//                    //判断是否符合时间要求
//                    List<TDisShipDynamicDTO> shipDynamicDTOS  = tDisShipDynamicMapper.getByShipvoyageId(disShipDynamic.getShipvoyageId());
//                    if(!CollectionUtils.isEmpty(shipDynamicDTOS)){
//                        shipDynamicDTOS = shipDynamicDTOS.stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime).reversed()).collect(Collectors.toList());
////                        if(DateUtil.compare(disShipDynamic.getDynamicStartTime(),shipDynamicDTOS.get(0).getDynamicStartTime())<0){
////                            throw new BusinessRuntimeException("时间不能早于之前的动态时间");
////                        }
////                        if(!YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())){
////                            if(disShipDynamic.getDynamicTypeCode().equals(shipDynamicDTOS.get(0).getDynamicTypeCode())){
////                                String dynamicName = "已经" + disShipDynamic.getDynamicTypeName() +",无法重复操作";
////                                throw new BusinessRuntimeException(dynamicName);
////                            }
////                        }
//                    }
//                    // 判断新状态是否在有效状态变更分支内
//                    TDisShipvoyageDTO disShipVoyage = tDisShipDynamicMapper.getDisShipVoyage(disShipDynamic.getShipvoyageId());
//                    ShipStatusEnum[] validStatusBranches;
//                    if (WANGONG.getCode().equals(disShipVoyage.getShipStatusCode())) {
//                        TDisShipDynamicPO query = new TDisShipDynamicPO();
//                        query.setShipvoyageId(disShipDynamic.getShipvoyageId());
//                        query.setDynamicTypeCode(WANGONG.getCode());
//                        List<TDisShipDynamicDTO> disShipDynamics = tDisShipDynamicMapper.listDisShipDynamic(query);
//                        String lastEndWorkLoadUnload = disShipDynamics.stream()
//                                .max(Comparator.comparing(TDisShipDynamicPO::getCreateTime))
//                                .orElse(new TDisShipDynamicDTO()).getLoadUnload();
//                        if (LOAD_UNLOAD.getName().equals(disShipVoyage.getLoadUnload()) && UNLOAD.getName().equals(lastEndWorkLoadUnload)) {
//                            validStatusBranches = STATUS_BRANCHS_MAP.get(WANGONG.getCode());
//                        } else {
//                            validStatusBranches = new ShipStatusEnum[]{YIBO, LIBO};
//                        }
//                    } else {
//                        validStatusBranches = STATUS_BRANCHS_MAP.get(disShipVoyage.getShipStatusCode());
//                    }
////                    boolean isValidStatus = Arrays.stream(validStatusBranches)
////                            .map(ShipStatusEnum::getCode)
////                            .anyMatch(v1 -> v1.equals(disShipDynamic.getDynamicTypeCode()));
//                    /*if (!isValidStatus) {
//                        throw new BusinessRuntimeException("禁止操作：" + disShipVoyage.getShipStatusName() + " => " + disShipDynamic.getDynamicTypeName());
//                    }*/
//
//                    /*
//                      判断装卸是否正确
//                      航次仅有卸：只能选择卸
//                      航次仅有装：只能选择装
//                      航次有卸和装：
//                        - 开工
//                          - 未开过工：限制为只能卸
//                          - 开过工：只能装
//                        - 停工、复工、完工：限制为同最后一次开工
//                     */
//                    boolean isVoyageRelated = Stream.of(KAIGONG, TINGGONG, FUGONG, WANGONG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
//                    if (isVoyageRelated) {
//                        if (UNLOAD.getName().equals(disShipVoyage.getLoadUnload())) {
//                            if (!UNLOAD.getName().equals(disShipDynamic.getLoadUnload())) {
//                                throw new BusinessRuntimeException("航次仅支持卸");
//                            }
//                        }
//                        if (LOAD.getName().equals(disShipVoyage.getLoadUnload())) {
//                            if (!LOAD.getName().equals(disShipDynamic.getLoadUnload())) {
//                                throw new BusinessRuntimeException("航次仅支持装");
//                            }
//                        }
//                        if (LOAD_UNLOAD.getName().equals(disShipVoyage.getLoadUnload())) {
//                            TDisShipDynamicPO query = new TDisShipDynamicPO();
//                            query.setShipvoyageId(disShipDynamic.getShipvoyageId());
//                            query.setDynamicTypeCode(KAIGONG.getCode());
//                            List<TDisShipDynamicDTO> disShipDynamics = tDisShipDynamicMapper.listDisShipDynamic(query);
//                            String lastStartWorkLoadUnload = disShipDynamics.stream()
//                                    .max(Comparator.comparing(TDisShipDynamicPO::getCreateTime))
//                                    .orElse(new TDisShipDynamicDTO()).getLoadUnload();
//                            if (KAIGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                                if (disShipDynamics.isEmpty()) {
//                                    if (!UNLOAD.getName().equals(disShipDynamic.getLoadUnload())) {
//                                        throw new BusinessRuntimeException("航次当前仅支持卸");
//                                    }
//                                } else {
//                                    if (!LOAD.getName().equals(disShipDynamic.getLoadUnload())) {
//                                        throw new BusinessRuntimeException("航次当前仅支持装");
//                                    }
//                                }
//                            } else {
//                                if (!disShipDynamic.getLoadUnload().equals(lastStartWorkLoadUnload)) {
//                                    throw new BusinessRuntimeException("航次当前仅支持" + lastStartWorkLoadUnload);
//                                }
//                            }
//                        }
//
//                        // 复工记录停工时长（小时）
//                        if(FUGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                        	// 查询上一次停工时间
//                        	TDisShipDynamicPO disShipDynamicPO = tDisShipDynamicMapper.getLastTigongDateTime(disShipDynamic.getShipvoyageId(), TINGGONG.getCode());
//                        	if(TINGGONG.getCode().equals(disShipDynamicPO.getDynamicTypeCode())) {
//                            	Long stopTimeLen = this.timePhaseDiffHours(disShipDynamicPO.getDynamicStartTime(), disShipDynamic.getDynamicStartTime());
//                            	disShipDynamic.setStopTimeLen(stopTimeLen);
//                        	}
//                        }
//                    }
//                    // 参数校验完毕，执行插入与修改
//                    if(LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                        List<TDisShipDynamicDTO> list  = tDisShipDynamicMapper.getByShipvoyageId(disShipDynamic.getShipvoyageId());
//                        list.sort(Comparator.comparing(TDisShipDynamicDTO::getCreateTime).reversed());
//                        a:for (TDisShipDynamicDTO x : list) {
//                            if (x.getDynamicTypeCode().equals("110")) {
//                                // 满足条件时，跳出整个循环
//                                break a;
//                            } else if (x.getDynamicTypeCode().equals("100")) {
//                                disShipDynamic.setId(snowflake.nextId());
//                                disShipDynamic.setDynamicTypeCode("110");
//                                disShipDynamic.setDynamicTypeName("离泊");
//                                tDisShipDynamicMapper.insertDisShipDynamic(disShipDynamic);
//                            } else if (x.getDynamicTypeCode().equals("50") || x.getDynamicTypeCode().equals("60")) {//特殊船只可以直接由靠泊或者移泊直接离港
//                                disShipDynamic.setId(snowflake.nextId());
//                                disShipDynamic.setDynamicTypeCode("110");
//                                disShipDynamic.setDynamicTypeName("离泊");
//                                tDisShipDynamicMapper.insertDisShipDynamic(disShipDynamic);
//                            }
//                        }
//                        disShipDynamic.setDynamicTypeCode("120");
//                        disShipDynamic.setDynamicTypeName("离港");
//                    }
//                    disShipDynamic.setId(snowflake.nextId());
//                    tDisShipDynamicMapper.insertDisShipDynamic(disShipDynamic);
//
//                    TDisShipvoyagePO disShipvoyage = new TDisShipvoyagePO();
//                    disShipvoyage.setId(disShipDynamic.getShipvoyageId());
//                    if (DIMAO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                        disShipvoyage.setArrivalAnchorageTime(disShipDynamic.getDynamicStartTime());
//                    }
//                    if (QIMAO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                        disShipvoyage.setLeaveAnchorageTime(disShipDynamic.getDynamicStartTime());
//                    }
//                    if (KAOBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                        TDisShipvoyagePO po = tDisShipVoyageMapper.getDisShipVoyage(disShipDynamic.getShipvoyageId());
//                        if(ObjectUtils.isEmpty(po.getBerthTime()) ){
//                            disShipvoyage.setBerthTime(disShipDynamic.getDynamicStartTime());
//                        }
//                        disShipvoyage.setBerthId(disShipDynamic.getBerthId());
//                        disShipvoyage.setBerthName(disShipDynamic.getBerthName());
//                        disShipvoyage.setBerthType(disShipDynamic.getBerthType());
//                        disShipvoyage.setBollardNoStart(disShipDynamic.getBollardNoStart());
//                        disShipvoyage.setBollardNoEnd(disShipDynamic.getBollardNoEnd());
//                    }
//                    if (YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                        disShipvoyage.setBerthId(disShipDynamic.getBerthId());
//                        disShipvoyage.setBerthName(disShipDynamic.getBerthName());
//                        disShipvoyage.setBerthType(disShipDynamic.getBerthType());
//                        disShipvoyage.setBollardNoStart(disShipDynamic.getBollardNoStart());
//                        disShipvoyage.setBollardNoEnd(disShipDynamic.getBollardNoEnd());
//                    }
//                    if (LIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                        disShipvoyage.setLeaveBerthTime(disShipDynamic.getDynamicStartTime());
//                    }
//                    if (!YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                        disShipvoyage.setShipStatusCode(disShipDynamic.getDynamicTypeCode());
//                        disShipvoyage.setShipStatusName(disShipDynamic.getDynamicTypeName());
//                    }
//                    if (LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                        disShipvoyage.setLeaveBerthTime(disShipDynamic.getDynamicStartTime());
//                        disShipvoyage.setLeavePortTime(disShipDynamic.getDynamicStartTime());
//                        disShipvoyage.setShipStatusCode(disShipDynamic.getDynamicTypeCode());
//                        disShipvoyage.setShipStatusName(disShipDynamic.getDynamicTypeName());
//                    }
//                    tDisShipDynamicMapper.updateDisShipVoyage(disShipvoyage);
//                    if (isVoyageRelated) {
//                        TDisShipvoyageItemPO disShipvoyageItem = new TDisShipvoyageItemPO();
//                        disShipvoyageItem.setId(disShipDynamic.getShipvoyageItemId());
//                        if (KAIGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                            disShipvoyageItem.setWorkStartTime(disShipDynamic.getDynamicStartTime());
//                        }
//                        if (WANGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                            disShipvoyageItem.setWorkEndTime(disShipDynamic.getDynamicStartTime());
//                        }
//                        disShipvoyageItem.setShipStatusCode(disShipDynamic.getDynamicTypeCode());
//                        disShipvoyageItem.setShipStatusName(disShipDynamic.getDynamicTypeName());
//                        tDisShipDynamicMapper.updateDisShipVoyageItem(disShipvoyageItem);
//                    }
//
//                    boolean isBerthRelated = Stream.of(KAOBO, YIBO, LIBO, LIGANG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
//                    if (isBerthRelated) {
////                        disShipDynamic.getTugs().forEach(v1 -> {
////                            v1.setId(snowflake.nextId());
////                            v1.setShipvoyageId(disShipDynamic.getShipvoyageId());
////                            v1.setShipId(disShipVoyage.getShipId());
////                            v1.setShipName(disShipVoyage.getShipName());
////                            v1.setShipDynamicId(disShipDynamic.getId());
////                            v1.setTimeLength(
////                                    BigDecimal.valueOf((double) (v1.getEndTime().getTime() - v1.getStartTime().getTime()) / (1000 * 60 * 60))
////                                            .setScale(1, RoundingMode.HALF_UP)
////                            );
////                        });
////                        tDisShipDynamicMapper.insertDisTugServiceRecord(disShipDynamic.getTugs());
//                    }
//                    //交接清单
//
//                    if(UNLOAD.getName().equals(disShipVoyage.getLoadUnload())
//                    		&& KAOBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                        UpdateBusHandoverlistDTO dto = disShipDynamic.getBusHandoverListDto();
//                        if(dto != null){
//                            tBusHandoverlistService.updateBusHandoverlist(dto);
//                        } else {
//                            throw new BusinessRuntimeException("未查询到交接清单信息");
//                        }
//                    } else if(LOAD.getName().equals(disShipVoyage.getLoadUnload())
//                    		&& (LIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())
//                    		|| WANGONG.getCode().equals(disShipDynamic.getDynamicTypeCode()) || LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode()))){
//
//                        UpdateBusHandoverlistDTO dto = disShipDynamic.getBusHandoverListDto();
//                        if(dto != null){
//                            tBusHandoverlistService.updateBusHandoverlist(dto);
//                        } else {
//                            throw new BusinessRuntimeException("未查询到交接清单信息");
//                        }
//                    }
//    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public StringBuffer updateDisShipvoyageStatusApp(TDisShipDynamicDTO disShipDynamic) {
        AtomicReference<StringBuffer> cargoNos = new AtomicReference<>(new StringBuffer());
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_KEY.getCode() + disShipDynamic.getShipvoyageId())
                .build().run(() -> {
        Long shipVoyageId = disShipDynamic.getShipvoyageId();
        Long shipvoyageItemId = disShipDynamic.getShipvoyageItemId();
        Long dId = null;
        Long sId = null;
        TDisShipvoyageDTO disShipVoyage = tDisShipDynamicMapper.getDisShipVoyage(shipVoyageId);
        TDisShipvoyageItemDTO disShipVoyageItem = tDisShipDynamicMapper.getDisShipVoyageItem(shipvoyageItemId);

        List<TDisShipDynamicDTO> shipDynamicDTOS  = tDisShipDynamicMapper.getByShipvoyageId(shipVoyageId);
        shipDynamicDTOS = shipDynamicDTOS.stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime).reversed()).collect(Collectors.toList());
        //app判断是否是更新操作
        boolean isUpdate = false;
        if(!CollectionUtils.isEmpty(shipDynamicDTOS)){
            for (TDisShipDynamicDTO shipDynamicDTO : shipDynamicDTOS) {
                //判断是不是更新,根据动态时间和类型
                Date dynamicStartTime = shipDynamicDTO.getDynamicStartTime();
                String dynamicTypeCode = shipDynamicDTO.getDynamicTypeCode();
                if(disShipDynamic.getDynamicTypeCode().equals(dynamicTypeCode) && !ObjectUtils.isEmpty(dynamicStartTime) && !ObjectUtils.isEmpty(disShipDynamic.getDynamicStartTime()) && disShipDynamic.getDynamicStartTime().equals(dynamicStartTime)){
                    isUpdate = true;
                    disShipDynamic.setId(shipDynamicDTO.getId());
                    disShipDynamic.setDynamicStartTime(null);
                    disShipDynamic.setDynamicEndTime(null);
                }
            }
        }
        if(KAIGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())){

            //开工 如果一装一卸，则需判断卸船的的开工完工做完才可以做装船的开工完工
           String loadUnLoad = tDisShipDynamicMapper.getDressEvil(disShipDynamic.getShipvoyageId());
           String evil = tDisShipDynamicMapper.getIsEvil(disShipDynamic.getShipvoyageItemId());
           if("装卸".equals(loadUnLoad) ){
              String time = tDisShipDynamicMapper.getEvilTime(disShipDynamic.getShipvoyageId());
              if("卸".equals(evil)){
                  //查询是否靠泊
                  Integer count = tDisShipDynamicMapper.getIsKb(disShipDynamic.getShipvoyageItemId());
                  if(count == 0){
                      throw new BusinessRuntimeException("请先靠泊");
                  }
              }
              if( "装".equals(evil) && time == null){
                  //卸还没有完工
                  throw new BusinessRuntimeException("该航次卸船还没有完工，请先完工");
              }
           }else{
               //查询是否靠泊
               Integer count = tDisShipDynamicMapper.getIsKb(disShipDynamic.getShipvoyageItemId());
               if(count == 0){
                   throw new BusinessRuntimeException("请先靠泊");
               }
           }
        }
        //更新
        if(isUpdate && disShipDynamic.getId() != null){
            disShipDynamic.setNow(new Date());
            disShipDynamic.setLoginUserId(securityUtils.getLoginUserId());
            disShipDynamic.setLoginUserName(securityUtils.getLoginUserName());
            tDisShipDynamicMapper.updateDynamic(disShipDynamic);
        }else{//新增
            //校验时间保证顺序
            if(!CollectionUtils.isEmpty(shipDynamicDTOS) && DateUtil.compare(disShipDynamic.getDynamicStartTime(),shipDynamicDTOS.get(0).getDynamicStartTime())<=0){
                throw new BusinessRuntimeException("时间不能早于之前的动态时间");
            }
            //判断是否在有效变更分支内
            isValidStatusBranches(shipDynamicDTOS,disShipDynamic,disShipVoyage);
            // 可能存在靠泊-离泊-靠泊-开工 取最新的靠泊时间
            if ("70".equals(disShipDynamic.getDynamicTypeCode()) && "内贸".equals(disShipVoyage.getTradeType())) {
                Optional<TDisShipDynamicDTO> optionalShipKaiGongDTO = shipDynamicDTOS.stream()
                        .filter(x -> x.getDynamicTypeName().equals("开工"))
                        .findFirst();
                if(!optionalShipKaiGongDTO.isPresent()) {
                    // 做开工时，内贸船舶需要校验靠泊到开工时间，超过30分钟增加必填项备注原因；
                    Optional<TDisShipDynamicDTO> optionalShipKaoBoDynamicDTO = shipDynamicDTOS.stream()
                            .filter(x -> x.getDynamicTypeName().equals("靠泊"))
                            .max(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime)); // 获取最新的靠泊动态
                    if (optionalShipKaoBoDynamicDTO.isPresent()) {
                        TDisShipDynamicDTO shipKaoBoDynamicDTO = optionalShipKaoBoDynamicDTO.get();

                        Instant dynamicStartTime = disShipDynamic.getDynamicStartTime().toInstant();
                        Instant kaoBoStartTime = shipKaoBoDynamicDTO.getDynamicStartTime().toInstant();

                        // 使用 Duration 计算时间差
                        Duration duration = Duration.between(kaoBoStartTime, dynamicStartTime);
                        long diffInMinutes = duration.toMinutes();

                        if (diffInMinutes > 30) {
                            if (disShipDynamic.getRemark() == null || disShipDynamic.getRemark().trim().isEmpty()) {
                                throw new BusinessRuntimeException("靠泊到开工时间超过30分钟需要填写备注原因");
                            }
                        }
                    }
                }
            }
            //参数校验完毕，执行新增
            Date dynamicStartTime = disShipDynamic.getDynamicStartTime();
            Date dynamicLiBoTime = DateUtil.offset(dynamicStartTime, DateField.MINUTE, -1);
            boolean liGangAndLiBo = false;
            if(LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {//离港自动填写离泊
                a:for (TDisShipDynamicDTO x : shipDynamicDTOS) {
                    if (x.getDynamicTypeCode().equals("110")) {
                        // 满足条件时，跳出整个循环
                        break a;
                    } else if (x.getDynamicTypeCode().equals("100") || x.getDynamicTypeCode().equals("50") || x.getDynamicTypeCode().equals("60")) {
                        dId = snowflake.nextId();
                        disShipDynamic.setId(dId);
                        disShipDynamic.setDynamicTypeCode("110");
                        disShipDynamic.setDynamicTypeName("离泊");
                        disShipDynamic.setDynamicStartTime(dynamicLiBoTime);
                        liGangAndLiBo = true;
                        tDisShipDynamicMapper.insertDisShipDynamic(disShipDynamic);
//                        sendShuJvShangBao(disShipDynamic,"0");
                        break a;
                    }
                }
                disShipDynamic.setDynamicStartTime(dynamicStartTime);
                disShipDynamic.setDynamicTypeCode("120");
                disShipDynamic.setDynamicTypeName("离港");
            }
            sId = snowflake.nextId();
            disShipDynamic.setId(sId);
            tDisShipDynamicMapper.insertDisShipDynamic(disShipDynamic);
//            sendShuJvShangBao(disShipDynamic,"0");
            //更新航次
            updateShipVoyage(disShipDynamic,liGangAndLiBo,dynamicLiBoTime);
            //更新航次子表
            updateShipVoyageItem(disShipDynamic);
        }
            //交接清单
        if(UNLOAD.getName().equals(disShipVoyageItem.getLoadUnload())
                && KAIGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            UpdateBusHandoverlistDTO dto = disShipDynamic.getBusHandoverListDto();
            if(dto != null){
                cargoNos.set(tBusHandoverlistService.updateBusHandoverlist(dto));
            } else {
                 throw new BusinessRuntimeException("未查询到交接清单信息");
            }
        } else if(LOAD.getName().equals(disShipVoyageItem.getLoadUnload())
                && (LIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())
                || WANGONG.getCode().equals(disShipDynamic.getDynamicTypeCode()) || LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode()))){
            UpdateBusHandoverlistDTO dto = disShipDynamic.getBusHandoverListDto();
            if(dto != null){
                cargoNos.set(tBusHandoverlistService.updateBusHandoverlist(dto));
            } else {
                throw new BusinessRuntimeException("未查询到交接清单信息");
            }
        }
        //完工更新卸船交接清单
        if(WANGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())){
        //获取交接清单中的该船的票货信息
            List<TDisShipvoyageItemPO> voyageInfo = tDisShipVoyageMapper.getDisShipVoyageItemById(disShipDynamic.getShipvoyageItemId());
            List<TBusHandoverlistPO> busHandoverList = handoverlistUnloadMapper.getBusHandoverListByShipId(disShipDynamic.getShipvoyageItemId());
            Map<String, List<TBusHandoverlistPO>> busHandoverListMap = busHandoverList.stream().collect(Collectors.groupingBy(TBusHandoverlistPO::getLoadUnload));
            List<TBusHandoverlistPO> doHandoverList = busHandoverListMap.get(voyageInfo.stream().findFirst().orElseThrow(() -> new BusinessRuntimeException("没有获取到航次信息")).getLoadUnload());
            if(CollectionUtils.isEmpty(doHandoverList)){
                throw new BusinessRuntimeException("未查询到交接清单信息");
            }
            //获取理货记录回写交接清单
            List<TYardTallyItemPO> tallyInfo = handoverlistUnloadMapper.getTallyInfo(disShipDynamic.getShipvoyageItemId(),
                    doHandoverList.stream().map(TBusHandoverlistPO::getCargoInfoId).distinct().collect(Collectors.toList()));
            Map<Long, List<TYardTallyItemPO>> tallyMap = tallyInfo.stream().collect(Collectors.groupingBy(TYardTallyItemPO::getCargoInfoId));
            doHandoverList.forEach(
                o->{
                    List<TYardTallyItemPO> tYardTallyItemPOS = tallyMap.get(o.getCargoInfoId());
                    if(tYardTallyItemPOS != null){
                        o.setTallyTon(tYardTallyItemPOS.stream().map(tallyItemPO->Optional.of(tallyItemPO.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add));
                        o.setTallyQuantity(tYardTallyItemPOS.stream().map(tallyItemPO->Optional.of(tallyItemPO.getQuantity()).orElse(0)).reduce(0,Integer::sum));
                    }
                }
            );
            handoverlistUnloadMapper.updateHandoverTally(doHandoverList);
        }
                    shipDynamicDTOS = shipDynamicDTOS.stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime).reversed()).collect(Collectors.toList());
                    Date dynamicStartTime = disShipDynamic.getDynamicStartTime();
                    Date dynamicLiBoTime = DateUtil.offset(dynamicStartTime, DateField.MINUTE, -1);
                    if(LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {//离港自动填写离泊
                        a:for (TDisShipDynamicDTO x : shipDynamicDTOS) {
                            if (x.getDynamicTypeCode().equals("110")) {
                                // 满足条件时，跳出整个循环
                                break a;
                            } else if (x.getDynamicTypeCode().equals("100") || x.getDynamicTypeCode().equals("50") || x.getDynamicTypeCode().equals("60")) {
                                disShipDynamic.setId(dId);
                                disShipDynamic.setDynamicTypeCode("110");
                                disShipDynamic.setDynamicTypeName("离泊");
                                disShipDynamic.setDynamicStartTime(dynamicLiBoTime);
                                break a;
                            }
                        }
                        disShipDynamic.setDynamicStartTime(dynamicStartTime);
                        disShipDynamic.setDynamicTypeCode("120");
                        disShipDynamic.setDynamicTypeName("离港");
                    }
                    disShipDynamic.setId(sId);

        });
        return cargoNos.get();
    }

    @Override
    public void updateTeShuTingBoFei(TDisShipDynamicDTO disShipDynamic) {
        if(disShipDynamic.getId()==null){
            disShipDynamic.setId(snowflake.nextId());
            tDisShipDynamicMapper.insertDisShipDynamic(disShipDynamic);
        }
    }

    /**
     * 判断是否在有效变更分支内
     */
    private boolean isValidStatusBranches(List<TDisShipDynamicDTO> shipDynamicDTOS,TDisShipDynamicDTO disShipDynamic,TDisShipvoyageDTO disShipVoyage){
        // 判断新状态是否在有效状态变更分支内
        ShipStatusEnum[] validStatusBranches;
        if (WANGONG.getCode().equals(disShipVoyage.getShipStatusCode())) {
            TDisShipDynamicPO query = new TDisShipDynamicPO();
            query.setShipvoyageId(disShipDynamic.getShipvoyageId());
            query.setDynamicTypeCode(WANGONG.getCode());
            List<TDisShipDynamicDTO> disShipDynamics = tDisShipDynamicMapper.listDisShipDynamic(query);
            String lastEndWorkLoadUnload = disShipDynamics.stream()
                    .max(Comparator.comparing(TDisShipDynamicPO::getCreateTime))
                    .orElse(new TDisShipDynamicDTO()).getLoadUnload();
            if (LOAD_UNLOAD.getName().equals(disShipVoyage.getLoadUnload()) && UNLOAD.getName().equals(lastEndWorkLoadUnload)) {
                validStatusBranches = STATUS_BRANCHS_MAP.get(WANGONG.getCode());
            } else {
                //完工后可以移泊，离泊，离港
                validStatusBranches = new ShipStatusEnum[]{YIBO, LIBO,LIGANG};
            }
        } else {
            validStatusBranches = STATUS_BRANCHS_MAP.get(disShipVoyage.getShipStatusCode());
        }
        boolean isValidStatus = Arrays.stream(validStatusBranches)
                .map(ShipStatusEnum::getCode)
                .anyMatch(v1 -> v1.equals(disShipDynamic.getDynamicTypeCode()));
        if (!isValidStatus) {
            throw new BusinessRuntimeException("禁止操作：" + disShipVoyage.getShipStatusName() + " => " + disShipDynamic.getDynamicTypeName());
        }
        if(
            ("50".equals(disShipVoyage.getShipStatusCode())||"60".equals(disShipVoyage.getShipStatusCode())||"80".equals(disShipVoyage.getShipStatusCode()))
            && ("120".equals(disShipDynamic.getDynamicTypeCode()))
            && "1".equals(disShipVoyage.getIsStartWork())
        ){
            throw new BusinessRuntimeException("禁止操作：" + disShipVoyage.getShipStatusName() + " => " + disShipDynamic.getDynamicTypeName());
        }
        //如果是复工，则要判断是否之前停工

        if(FUGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())){
            int count = 0;
            for (TDisShipDynamicDTO item : shipDynamicDTOS) {
                if(TINGGONG.getCode().equals(item.getDynamicTypeCode())){
                    count++;
                }
                if(FUGONG.getCode().equals(item.getDynamicTypeCode())){
                    count--;
                }
            }
            if(count<=0){
                throw new BusinessRuntimeException("尚未停工，不可复工");
            }
        }
        //离港判断完工，开工-完工对应
//        if("120".equals(disShipDynamic.getDynamicTypeCode())){
//            if("120".equals(disShipDynamic.getDynamicTypeCode())){
//                int count = 0;
//                for (TDisShipDynamicDTO dynamic : shipDynamicDTOS) {
//                    if(KAIGONG.getCode().equals(dynamic.getDynamicTypeCode())){
//                        count++;
//                    }
//                    if(WANGONG.getCode().equals(dynamic.getDynamicTypeCode())){
//                        count++;
//                    }
//                }
//                if(0!=count%2){
//                    throw new BusinessRuntimeException("未完工");
//                }
//            }
//        }
        /*
          判断装卸是否正确
          航次仅有卸：只能选择卸
          航次仅有装：只能选择装
          航次有卸和装：
            - 开工
              - 未开过工：限制为只能卸
              - 开过工：只能装
            - 停工、复工、完工：限制为同最后一次开工
        */
        boolean isVoyageRelated = Stream.of(KAIGONG, TINGGONG, FUGONG, WANGONG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
        if (isVoyageRelated) {
            if (UNLOAD.getName().equals(disShipVoyage.getLoadUnload())) {
                if (!UNLOAD.getName().equals(disShipDynamic.getLoadUnload())) {
                    throw new BusinessRuntimeException("航次仅支持卸");
                }
            }
            if (LOAD.getName().equals(disShipVoyage.getLoadUnload())) {
                if (!LOAD.getName().equals(disShipDynamic.getLoadUnload())) {
                    throw new BusinessRuntimeException("航次仅支持装");
                }
            }
            if (LOAD_UNLOAD.getName().equals(disShipVoyage.getLoadUnload())) {
                TDisShipDynamicPO query = new TDisShipDynamicPO();
                query.setShipvoyageId(disShipDynamic.getShipvoyageId());
                query.setDynamicTypeCode(KAIGONG.getCode());
                List<TDisShipDynamicDTO> disShipDynamics = tDisShipDynamicMapper.listDisShipDynamic(query);
                String lastStartWorkLoadUnload = disShipDynamics.stream()
                        .max(Comparator.comparing(TDisShipDynamicPO::getCreateTime))
                        .orElse(new TDisShipDynamicDTO()).getLoadUnload();
                if (KAIGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                    if (disShipDynamics.isEmpty()) {
                        if (!UNLOAD.getName().equals(disShipDynamic.getLoadUnload())) {
                            throw new BusinessRuntimeException("航次当前仅支持卸");
                        }
                    } else {
                        if (!LOAD.getName().equals(disShipDynamic.getLoadUnload())) {
                            throw new BusinessRuntimeException("航次当前仅支持装");
                        }
                    }
                } else {
                    if (!disShipDynamic.getLoadUnload().equals(lastStartWorkLoadUnload)) {
                        throw new BusinessRuntimeException("航次当前仅支持" + lastStartWorkLoadUnload);
                    }
                }
            }
            // 复工记录停工时长（小时）
            if(FUGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                // 查询上一次停工时间
                TDisShipDynamicPO disShipDynamicPO = tDisShipDynamicMapper.getLastTigongDateTime(disShipDynamic.getShipvoyageId(), TINGGONG.getCode());
                if(TINGGONG.getCode().equals(disShipDynamicPO.getDynamicTypeCode())) {
                    String stopTimeLen = this.timePhaseDiffHours(disShipDynamicPO.getDynamicStartTime(), disShipDynamic.getDynamicStartTime());
                    disShipDynamic.setStopTimeLen(stopTimeLen);
                }
            }
        }
        return isVoyageRelated;
    }


    /**
     * app更新船舶航次
     */
    private void updateShipVoyage(TDisShipDynamicDTO disShipDynamic,boolean liGangAndLiBo,Date dynamicLiBoTime){
        TDisShipvoyagePO disShipvoyage = new TDisShipvoyagePO();
        disShipvoyage.setId(disShipDynamic.getShipvoyageId());
        if (DIMAO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            disShipvoyage.setArrivalAnchorageTime(disShipDynamic.getDynamicStartTime());
        }
        if (QIMAO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            disShipvoyage.setLeaveAnchorageTime(disShipDynamic.getDynamicStartTime());
        }
        if (KAOBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            //靠泊取最开始的时间，如果有了就不需要更新
            TDisShipvoyagePO po = tDisShipVoyageMapper.getDisShipVoyage(disShipDynamic.getShipvoyageId());
            if(ObjectUtils.isEmpty(po.getBerthTime()) ){
                disShipvoyage.setBerthTime(disShipDynamic.getDynamicStartTime());
            }
            disShipvoyage.setBerthId(disShipDynamic.getBerthId());
            disShipvoyage.setBerthName(disShipDynamic.getBerthName());
            disShipvoyage.setBerthType(disShipDynamic.getBerthType());
            disShipvoyage.setBollardNoStart(disShipDynamic.getBollardNoStart());
            disShipvoyage.setBollardNoEnd(disShipDynamic.getBollardNoEnd());
        }
        if (YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            disShipvoyage.setBerthId(disShipDynamic.getBerthId());
            disShipvoyage.setBerthName(disShipDynamic.getBerthName());
            disShipvoyage.setBerthType(disShipDynamic.getBerthType());
            disShipvoyage.setBollardNoStart(disShipDynamic.getBollardNoStart());
            disShipvoyage.setBollardNoEnd(disShipDynamic.getBollardNoEnd());
        }
        if (LIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            disShipvoyage.setLeaveBerthTime(disShipDynamic.getDynamicStartTime());
        }
        if (LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            if(liGangAndLiBo){
                disShipvoyage.setLeaveBerthTime(dynamicLiBoTime);
            }
            disShipvoyage.setLeavePortTime(disShipDynamic.getDynamicStartTime());
        }
        if (!YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            disShipvoyage.setShipStatusCode(disShipDynamic.getDynamicTypeCode());
            disShipvoyage.setShipStatusName(disShipDynamic.getDynamicTypeName());
        }
        tDisShipDynamicMapper.updateDisShipVoyage(disShipvoyage);
//        shipForecastService.acceptance(disShipvoyage);//渤海通接口受理
    }

    /**
     * app更新船舶航次子表
     */
    private void updateShipVoyageItem(TDisShipDynamicDTO disShipDynamic){
        TDisShipvoyageItemPO disShipvoyageItem = new TDisShipvoyageItemPO();
        disShipvoyageItem.setId(disShipDynamic.getShipvoyageItemId());
        if (KAIGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            disShipvoyageItem.setWorkStartTime(disShipDynamic.getDynamicStartTime());
        }
        if (WANGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            disShipvoyageItem.setWorkEndTime(disShipDynamic.getDynamicStartTime());
        }
        disShipvoyageItem.setShipStatusCode(disShipDynamic.getDynamicTypeCode());
        disShipvoyageItem.setShipStatusName(disShipDynamic.getDynamicTypeName());
        tDisShipDynamicMapper.updateDisShipVoyageItem(disShipvoyageItem);
    }


//    @Override
//    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
//    public void updateDisShipvoyageStatus(TDisShipDynamicDTO disShipDynamic) {
//        DistributedLock.newBuilder().store(redisTemplate)
//                .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_KEY.getCode() + disShipDynamic.getShipvoyageId())
//                .build().run(() -> {
//            //判断是否符合时间要求
//            List<TDisShipDynamicDTO> shipDynamicDTOS  = tDisShipDynamicMapper.getByShipvoyageId(disShipDynamic.getShipvoyageId());
//            if(!CollectionUtils.isEmpty(shipDynamicDTOS)){
//                shipDynamicDTOS = shipDynamicDTOS.stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime).reversed()).collect(Collectors.toList());
////                if(DateUtil.compare(disShipDynamic.getDynamicStartTime(),shipDynamicDTOS.get(0).getDynamicStartTime())<0){
////                    throw new BusinessRuntimeException("时间不能早于之前的动态时间");
////                }
////                if(!YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())){
////                    if(disShipDynamic.getDynamicTypeCode().equals(shipDynamicDTOS.get(0).getDynamicTypeCode())){
////                        String dynamicName = "已经" + disShipDynamic.getDynamicTypeName() +",无法重复操作";
////                        throw new BusinessRuntimeException(dynamicName);
////                    }
////                }
//            }
//            // 判断新状态是否在有效状态变更分支内
//            TDisShipvoyageDTO disShipVoyage = tDisShipDynamicMapper.getDisShipVoyage(disShipDynamic.getShipvoyageId());
//            ShipStatusEnum[] validStatusBranches;
//            if (WANGONG.getCode().equals(disShipVoyage.getShipStatusCode())) {
//                TDisShipDynamicPO query = new TDisShipDynamicPO();
//                query.setShipvoyageId(disShipDynamic.getShipvoyageId());
//                query.setDynamicTypeCode(WANGONG.getCode());
//                List<TDisShipDynamicDTO> disShipDynamics = tDisShipDynamicMapper.listDisShipDynamic(query);
//                String lastEndWorkLoadUnload = disShipDynamics.stream()
//                        .max(Comparator.comparing(TDisShipDynamicPO::getCreateTime))
//                        .orElse(new TDisShipDynamicDTO()).getLoadUnload();
//                if (LOAD_UNLOAD.getName().equals(disShipVoyage.getLoadUnload()) && UNLOAD.getName().equals(lastEndWorkLoadUnload)) {
//                    validStatusBranches = STATUS_BRANCHS_MAP.get(WANGONG.getCode());
//                } else {
//                    validStatusBranches = new ShipStatusEnum[]{YIBO, LIBO};
//                }
//            } else {
//                validStatusBranches = STATUS_BRANCHS_MAP.get(disShipVoyage.getShipStatusCode());
//            }
//            boolean isValidStatus = Arrays.stream(validStatusBranches)
//                    .map(ShipStatusEnum::getCode)
//                    .anyMatch(v1 -> v1.equals(disShipDynamic.getDynamicTypeCode()));
//
////            if (!isValidStatus) {
////                throw new BusinessRuntimeException("禁止操作：" + disShipVoyage.getShipStatusName() + " => " + disShipDynamic.getDynamicTypeName());
////            }
//
//                    /*
//                      判断装卸是否正确
//                      航次仅有卸：只能选择卸
//                      航次仅有装：只能选择装
//                      航次有卸和装：
//                        - 开工
//                          - 未开过工：限制为只能卸
//                          - 开过工：只能装
//                        - 停工、复工、完工：限制为同最后一次开工
//                     */
//            boolean isVoyageRelated = Stream.of(KAIGONG, TINGGONG, FUGONG, WANGONG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
//            if (isVoyageRelated) {
//                if (UNLOAD.getName().equals(disShipVoyage.getLoadUnload())) {
//                    if (!UNLOAD.getName().equals(disShipDynamic.getLoadUnload())) {
//                        throw new BusinessRuntimeException("航次仅支持卸");
//                    }
//                }
//                if (LOAD.getName().equals(disShipVoyage.getLoadUnload())) {
//                    if (!LOAD.getName().equals(disShipDynamic.getLoadUnload())) {
//                        throw new BusinessRuntimeException("航次仅支持装");
//                    }
//                }
//                if (LOAD_UNLOAD.getName().equals(disShipVoyage.getLoadUnload())) {
//                    TDisShipDynamicPO query = new TDisShipDynamicPO();
//                    query.setShipvoyageId(disShipDynamic.getShipvoyageId());
//                    query.setDynamicTypeCode(KAIGONG.getCode());
//                    List<TDisShipDynamicDTO> disShipDynamics = tDisShipDynamicMapper.listDisShipDynamic(query);
//                    String lastStartWorkLoadUnload = disShipDynamics.stream()
//                            .max(Comparator.comparing(TDisShipDynamicPO::getCreateTime))
//                            .orElse(new TDisShipDynamicDTO()).getLoadUnload();
//                    if (KAIGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                        if (disShipDynamics.isEmpty()) {
//                            if (!UNLOAD.getName().equals(disShipDynamic.getLoadUnload())) {
//                                throw new BusinessRuntimeException("航次当前仅支持卸");
//                            }
//                        } else {
//                            if (!LOAD.getName().equals(disShipDynamic.getLoadUnload())) {
//                                throw new BusinessRuntimeException("航次当前仅支持装");
//                            }
//                        }
//                    } else {
//                        if (!disShipDynamic.getLoadUnload().equals(lastStartWorkLoadUnload)) {
//                            throw new BusinessRuntimeException("航次当前仅支持" + lastStartWorkLoadUnload);
//                        }
//                    }
//                }
//                // 复工记录停工时长（小时）
//                if(FUGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                	// 查询上一次停工时间
//                	TDisShipDynamicPO disShipDynamicPO = tDisShipDynamicMapper.getLastTigongDateTime(disShipDynamic.getShipvoyageId(), TINGGONG.getCode());
//                	if(TINGGONG.getCode().equals(disShipDynamicPO.getDynamicTypeCode())) {
//                    	Long stopTimeLen = this.timePhaseDiffHours(disShipDynamicPO.getDynamicStartTime(), disShipDynamic.getDynamicStartTime());
//                    	disShipDynamic.setStopTimeLen(stopTimeLen);
//                	}
//                }
//            }
//
//            // 参数校验完毕，执行插入与修改
//
//            if(LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                List<TDisShipDynamicDTO> list  = tDisShipDynamicMapper.getByShipvoyageId(disShipDynamic.getShipvoyageId());
//                list.sort(Comparator.comparing(TDisShipDynamicDTO::getCreateTime).reversed());
//                a:for (TDisShipDynamicDTO x : list) {
//                    if (x.getDynamicTypeCode().equals("110")) {
//                        // 满足条件时，跳出整个循环
//                        break a;
//                    } else if (x.getDynamicTypeCode().equals("100")) {
//                        disShipDynamic.setId(snowflake.nextId());
//                        disShipDynamic.setDynamicTypeCode("110");
//                        disShipDynamic.setDynamicTypeName("离泊");
//                        tDisShipDynamicMapper.insertDisShipDynamic(disShipDynamic);
//                    } else if (x.getDynamicTypeCode().equals("50") || x.getDynamicTypeCode().equals("60")) {//特殊船只可以直接由靠泊或者移泊直接离港
//                        disShipDynamic.setId(snowflake.nextId());
//                        disShipDynamic.setDynamicTypeCode("110");
//                        disShipDynamic.setDynamicTypeName("离泊");
//                        tDisShipDynamicMapper.insertDisShipDynamic(disShipDynamic);
//                    }
//                }
//                disShipDynamic.setDynamicTypeCode("120");
//                disShipDynamic.setDynamicTypeName("离港");
//            }
//            disShipDynamic.setId(snowflake.nextId());
//            tDisShipDynamicMapper.insertDisShipDynamic(disShipDynamic);
//
//            TDisShipvoyagePO disShipvoyage = new TDisShipvoyagePO();
//            disShipvoyage.setId(disShipDynamic.getShipvoyageId());
//            if (DIMAO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                disShipvoyage.setArrivalAnchorageTime(disShipDynamic.getDynamicStartTime());
//            }
//            if (QIMAO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                disShipvoyage.setLeaveAnchorageTime(disShipDynamic.getDynamicStartTime());
//            }
//            if (KAOBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                TDisShipvoyagePO po = tDisShipVoyageMapper.getDisShipVoyage(disShipDynamic.getShipvoyageId());
//                if(ObjectUtils.isEmpty(po.getBerthTime()) ){
//                    disShipvoyage.setBerthTime(disShipDynamic.getDynamicStartTime());
//                }
//                disShipvoyage.setBerthId(disShipDynamic.getBerthId());
//                disShipvoyage.setBerthName(disShipDynamic.getBerthName());
//                disShipvoyage.setBerthType(disShipDynamic.getBerthType());
//                disShipvoyage.setBollardNoStart(disShipDynamic.getBollardNoStart());
//                disShipvoyage.setBollardNoEnd(disShipDynamic.getBollardNoEnd());
//            }
//            if (YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                disShipvoyage.setBerthId(disShipDynamic.getBerthId());
//                disShipvoyage.setBerthName(disShipDynamic.getBerthName());
//                disShipvoyage.setBerthType(disShipDynamic.getBerthType());
//                disShipvoyage.setBollardNoStart(disShipDynamic.getBollardNoStart());
//                disShipvoyage.setBollardNoEnd(disShipDynamic.getBollardNoEnd());
//            }
//            if (LIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                disShipvoyage.setLeaveBerthTime(disShipDynamic.getDynamicStartTime());
//            }
//            if (!YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                disShipvoyage.setShipStatusCode(disShipDynamic.getDynamicTypeCode());
//                disShipvoyage.setShipStatusName(disShipDynamic.getDynamicTypeName());
//            }
//            if (LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                disShipvoyage.setLeaveBerthTime(disShipDynamic.getDynamicStartTime());
//                disShipvoyage.setLeavePortTime(disShipDynamic.getDynamicStartTime());
//                disShipvoyage.setShipStatusCode(disShipDynamic.getDynamicTypeCode());
//                disShipvoyage.setShipStatusName(disShipDynamic.getDynamicTypeName());
//            }
//            tDisShipDynamicMapper.updateDisShipVoyage(disShipvoyage);
//            shipForecastService.acceptance(disShipvoyage);//渤海通接口受理
//            if (isVoyageRelated) {
//                TDisShipvoyageItemPO disShipvoyageItem = new TDisShipvoyageItemPO();
//                disShipvoyageItem.setId(disShipDynamic.getShipvoyageItemId());
//                if (KAIGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                    disShipvoyageItem.setWorkStartTime(disShipDynamic.getDynamicStartTime());
//                }
//                if (WANGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
//                    disShipvoyageItem.setWorkEndTime(disShipDynamic.getDynamicStartTime());
//                }
//                disShipvoyageItem.setShipStatusCode(disShipDynamic.getDynamicTypeCode());
//                disShipvoyageItem.setShipStatusName(disShipDynamic.getDynamicTypeName());
//                tDisShipDynamicMapper.updateDisShipVoyageItem(disShipvoyageItem);
//            }
//
//            boolean isBerthRelated = Stream.of(KAOBO, YIBO, LIBO, LIGANG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
//            if (isBerthRelated) {
//                disShipDynamic.getTugs().forEach(v1 -> {
//                    v1.setId(snowflake.nextId());
//                    v1.setShipvoyageId(disShipDynamic.getShipvoyageId());
//                    v1.setShipId(disShipVoyage.getShipId());
//                    v1.setShipName(disShipVoyage.getShipName());
//                    v1.setShipDynamicId(disShipDynamic.getId());
//                    v1.setTimeLength(
//                            BigDecimal.valueOf((double) (v1.getEndTime().getTime() - v1.getStartTime().getTime()) / (1000 * 60 * 60))
//                                    .setScale(1, RoundingMode.HALF_UP)
//                    );
//                    //判断非标准原因是否要写入字典
//
//                    if("1".equals(v1.getIsStandardUse())){
//                        if(v1.getReasonCode()!=null){
//                            throw new BusinessRuntimeException("非标准原因填写异常！");
//                        }
//                    }
//                    if("0".equals(v1.getIsStandardUse())){
//                        if(v1.getReasonCode()==null&&StringUtil.isEmpty(v1.getReasonName())){
//                            throw new BusinessRuntimeException("请填写非标准原因！");
//                        }
//                    }
//                    HashMap<String, Object> serachMap = new HashMap<>();
//                    serachMap.put("type","DICT");
//                    serachMap.put("dictType","DIS_TUG_REASON");
//                    List<Map<String, Object>> localSelect = selectService.getLocalSelect(serachMap);
//                    if (CollectionUtils.isEmpty(localSelect)){
//                        MDictDataDTO mDictDataDTO = new MDictDataDTO();
//                        mDictDataDTO.setDictLabel(v1.getReasonName());
//                        mDictDataDTO.setDictType("DIS_TUG_REASON");
//                        mDictDataDTO.setDictValue("1");
//                        mDictDataDTO.setSortNum(1);
//                        v1.setReasonCode(1L);
//                        mDictDataDTO.setStatus("1");
//                        mDictService.insertOrUpdateDict(mDictDataDTO);
//                    }else{
//                        if(v1.getReasonCode()==null||v1.getReasonCode()==0L){
//                            MDictDataDTO mDictDataDTO = new MDictDataDTO();
//                            mDictDataDTO.setDictLabel(v1.getReasonName());
//                            v1.setReasonCode(Long.parseLong(String.valueOf(localSelect.size()+1)));
//                            mDictDataDTO.setDictType("DIS_TUG_REASON");
//                            mDictDataDTO.setDictValue(String.valueOf(localSelect.size()+1));//编号以此后排
//                            mDictDataDTO.setStatus("1");
//                            mDictDataDTO.setSortNum(localSelect.size()+1);
//                            mDictService.insertOrUpdateDict(mDictDataDTO);
//                        }
//                    }
//                });
//                if (disShipDynamic.getTugs() != null && disShipDynamic.getTugs().size() > 0) {
//                    tDisShipDynamicMapper.insertDisTugServiceRecord(disShipDynamic.getTugs());
//                }
//            }
//            });
//    }


    /**
     * 船舶调度动态更新-新
     * @param disShipDynamic
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateDisShipvoyageStatus(TDisShipDynamicDTO disShipDynamic) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_KEY.getCode() + disShipDynamic.getShipvoyageId())
                .build().run(() -> {
            Long shipVoyageId = disShipDynamic.getShipvoyageId();
            TDisShipvoyageDTO disShipVoyage = tDisShipDynamicMapper.getDisShipVoyage(shipVoyageId);
            List<TDisShipDynamicDTO> shipDynamicDTOS  = tDisShipDynamicMapper.getByShipvoyageId(shipVoyageId);
            shipDynamicDTOS = shipDynamicDTOS.stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime).reversed()).collect(Collectors.toList());
            //判断是否符合时间要求
            if(!CollectionUtils.isEmpty(shipDynamicDTOS)){
                if(disShipDynamic.getId()==null && DateUtil.compare(disShipDynamic.getDynamicStartTime(),shipDynamicDTOS.get(0).getDynamicStartTime())<=0){
                    throw new BusinessRuntimeException("时间不能早于之前的动态时间");
                }
                if(!YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())){
                    if(disShipDynamic.getId()==null && disShipDynamic.getDynamicTypeCode().equals(shipDynamicDTOS.get(0).getDynamicTypeCode())){
                        String dynamicName = "已经" + disShipDynamic.getDynamicTypeName() +",无法重复操作";
                        throw new BusinessRuntimeException(dynamicName);
                    }
                }
            }
            //判断有没有抵锚
            if("50".equals(disShipDynamic.getDynamicTypeCode())){
                //存在抵锚 但是不存在离泊
                Optional<TDisShipDynamicDTO> optionalShipDiMaoDTO = shipDynamicDTOS.stream()
                    .filter(x -> x.getDynamicTypeCode().equals("30"))
                    .findFirst();
                if(optionalShipDiMaoDTO.isPresent()){
                    TDisShipDynamicDTO shipDiMaoDynamicDTO = optionalShipDiMaoDTO.get(); // 获取抵锚动态对象
                    Optional<TDisShipDynamicDTO> optionalShipLiBoDTO = shipDynamicDTOS.stream()
                            .filter(x -> x.getDynamicTypeCode().equals("110"))
                            .findFirst();
                    if (!optionalShipLiBoDTO.isPresent()) {
                        // 将时间转换为 LocalDateTime
                        LocalDateTime disShipDynamicStart = disShipDynamic.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        LocalDateTime shipDiMaoDynamicStart = shipDiMaoDynamicDTO.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                        // 使用 Duration 计算时间差（以分钟为单位）
                        Duration duration = Duration.between(shipDiMaoDynamicStart, disShipDynamicStart);
                        long diffInMinutes = duration.toMinutes(); // 获取总的分钟数

                        // 如果时间差超过 240 分钟（即 4 小时）
                        if (diffInMinutes > 240) {
                            String remark = disShipDynamic.getRemark(); // 获取当前动态的备注
                            if (remark == null || remark.trim().isEmpty()) { // 如果备注为空
                                throw new BusinessRuntimeException("抵锚到靠泊时间超过4小时需要填写备注原因"); // 抛出异常
                            }
                        }
                    }
                }
            }
            //存在靠泊-离泊-靠泊-开工-完工-离泊
            if ("110".equals(disShipDynamic.getDynamicTypeCode())) {
                //离泊时，校验完工到离泊时间，超过2小时需要必填项备注原因。
                if(shipDynamicDTOS.get(0).getDynamicTypeCode().equals("100")) {
                    TDisShipDynamicDTO shipWanGongDynamicDTO = shipDynamicDTOS.get(0);
                    LocalDateTime disShipDynamicStart = disShipDynamic.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime shipDiMaoDynamicStart = shipWanGongDynamicDTO.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                    // 使用 Duration 计算时间差
                    Duration duration = Duration.between(shipDiMaoDynamicStart, disShipDynamicStart);
                    long diffInMinutes = duration.toMinutes(); // 获取总的分钟数

                    if (diffInMinutes > 120) {
                        if (disShipDynamic.getRemark() == null || disShipDynamic.getRemark().trim().isEmpty()) {
                            throw new BusinessRuntimeException("完工到离泊时间超过2小时需要填写备注原因");
                        }
                    }
                }
                /*Optional<TDisShipDynamicDTO> optionShipWanGongDTO = shipDynamicDTOS.stream()
                        .filter(x -> x.getDynamicTypeCode().equals("100"))
                        .max(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime)); // 获取最晚的完工动态的时间
                //如果完工存在
                if(optionShipWanGongDTO.isPresent()){
                    TDisShipDynamicDTO shipWanGongDynamicDTO = optionShipWanGongDTO.get();
                    LocalDateTime disShipDynamicStart = disShipDynamic.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime shipDiMaoDynamicStart = shipWanGongDynamicDTO.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                    // 使用 Duration 计算时间差
                    Duration duration = Duration.between(shipDiMaoDynamicStart, disShipDynamicStart);
                    long diffInMinutes = duration.toMinutes(); // 获取总的分钟数

                    if (diffInMinutes > 120) {
                        if (disShipDynamic.getRemark() == null || disShipDynamic.getRemark().trim().isEmpty()) {
                            throw new BusinessRuntimeException("完工到离泊时间超过2小时需要填写备注原因");
                        }
                    }*/

            }

            //判断是否在有效变更分支内
            isValidStatusBranches(shipDynamicDTOS,disShipDynamic,disShipVoyage);
            // 参数校验完毕，执行插入与修改
            Date dynamicStartTime = disShipDynamic.getDynamicStartTime();
            Date dynamicLiBoTime = DateUtil.offset(dynamicStartTime, DateField.MINUTE, -1);
            boolean liGangAndLiBo = false;
            int count=0;
            if(LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                for (TDisShipDynamicDTO shipDynamicDTO : shipDynamicDTOS) {
                    if(KAIGONG.getCode().equals(shipDynamicDTO.getDynamicTypeCode())){
                        count++;
                    }
                    if(WANGONG.getCode().equals(shipDynamicDTO.getDynamicTypeCode())){
                        count++;
                    }
                }
                if(count%2!=0){
                    throw new BusinessRuntimeException("禁止离港，缺少对应的开工、完工信息");
                }
                //开工离港校验
                if("1".equals(disShipVoyage.getIsStartWork())){
                    List<TDisShipvoyageItemPO> list = tDisShipVoyageMapper.getShipVoyageItemByVoyageId(disShipVoyage.getId());
                    Map<Long,List<TDisShipDynamicDTO>> maps = shipDynamicDTOS.stream().collect(Collectors.groupingBy(TDisShipDynamicDTO::getShipvoyageItemId));
                    for (TDisShipvoyageItemPO tDisShipvoyageItemPO : list) {
                        List<TDisShipDynamicDTO> dynamicDTOS = maps.get(tDisShipvoyageItemPO.getId());
                        int count1 = 0;
                        if(StringUtil.isNotEmpty(dynamicDTOS)){
                            for (TDisShipDynamicDTO dynamicDTO : dynamicDTOS) {
                                if(KAIGONG.getCode().equals(dynamicDTO.getDynamicTypeCode())){
                                    count1++;
                                }
                                if(WANGONG.getCode().equals(dynamicDTO.getDynamicTypeCode())){
                                    count1++;
                                }
                            }
                        }
                        if(count1==0 || count1%2!=0){
                            throw new BusinessRuntimeException("缺少"+ ("IN".equals(tDisShipvoyageItemPO.getImpExp())?"进口":"出口") +"完工信息");
                        }
                    }
                }
                a:for (TDisShipDynamicDTO x : shipDynamicDTOS) {
                    if (x.getDynamicTypeCode().equals("110")) {
                        // 满足条件时，跳出整个循环
                        break a;
                    } else if (x.getDynamicTypeCode().equals("100") || x.getDynamicTypeCode().equals("50") || x.getDynamicTypeCode().equals("60")) {
                        disShipDynamic.setId(snowflake.nextId());
                        disShipDynamic.setDynamicTypeCode("110");
                        disShipDynamic.setDynamicTypeName("离泊");
                        disShipDynamic.setDynamicStartTime(dynamicLiBoTime);
                        liGangAndLiBo = true;
                        tDisShipDynamicMapper.insertDisShipDynamic(disShipDynamic);
//                        updateAnchTime(disShipDynamic);
                        break a;
                    }
                }
                disShipDynamic.setDynamicStartTime(dynamicStartTime);
                disShipDynamic.setDynamicTypeCode("120");
                disShipDynamic.setDynamicTypeName("离港");
            }
            disShipDynamic.setId(snowflake.nextId());
            tDisShipDynamicMapper.insertDisShipDynamic(disShipDynamic);
            updateAnchTime(disShipDynamic);

            //首次靠泊把船舶id存到表里
            TDisShipvoyagePO po = tDisShipVoyageMapper.getDisShipVoyage(disShipDynamic.getShipvoyageId());
            //首次靠泊把z船舶id存到表里
            if(ObjectUtils.isEmpty(po.getBerthTime()) && KAOBO.getCode().equals(disShipDynamic.getDynamicTypeCode())){
                SyncDTO dto = new SyncDTO();
                dto.setId(snowflake.nextId());
                dto.setBizId(disShipDynamic.getShipvoyageId());
                //dto.setBizType(BusSyncEnum.SHIP_INFORMATION.getCode());
                dto.setIsDelete("0");
                //SpringUtils.getBean(BusSyncServiceImpl.class).insertBatch(dto);
            }

            //更新航次
            updateShipVoyage(disShipDynamic,liGangAndLiBo,dynamicLiBoTime);
            //更新航次子表
            updateShipVoyageItem(disShipDynamic);
            //更新拖轮
            boolean isBerthRelated = Stream.of(KAOBO, YIBO, LIBO, LIGANG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
            if (isBerthRelated) {
                insertDisTugRecord(disShipDynamic,disShipVoyage);
            }
        });
    }




    /**
     * 根据航次id
     * @return
     */
    public MSjsbLogDTO getSjsbLog(TDisShipDynamicPO disShipDynamic,String delFlag) {
        MSjsbLogDTO dto = new MSjsbLogDTO();
        dto.setId(snowflake.nextId());
        dto.setServiceId(disShipDynamic.getId());
        dto.setJkInfo("船舶动态数据上报");
        dto.setSjsbType(delFlag);//接口类型
        Map<String, String> mapList = tDisShipVoyageMapper.getShipVoyageById(disShipDynamic.getShipvoyageId());
        dto.setServiceFieldI(String.valueOf(disShipDynamic.getShipvoyageId()));
        dto.setServiceFieldIi(mapList.get("shipName") + "_" + mapList.get("voyage"));
        dto.setServiceFieldIii(DateUtil.format(disShipDynamic.getDynamicStartTime(),"yyyy-MM-dd HH:mm:ss"));
        return dto;
    }



    /**
     * 更新锚地抛锚时间
     * @param disShipDynamic
     */
    private void updateAnchTime(TDisShipDynamicDTO disShipDynamic){
        if(DIMAO.getCode().equals(disShipDynamic.getDynamicTypeCode())){
            List<TAnchApplyDTO> anchApplyDTOS = anchApplyMapper.getByShipvoyageId(disShipDynamic.getShipvoyageId());
            if(anchApplyDTOS.size()==1){
                TAnchApplyDTO tAnchApplyDTO = new TAnchApplyDTO();
                tAnchApplyDTO.setAnchTime(disShipDynamic.getDynamicStartTime());
                tAnchApplyDTO.setDynamicId(disShipDynamic.getId());
                tAnchApplyDTO.setId(anchApplyDTOS.get(0).getId());
                int count = anchApplyMapper.updateAnchTime(tAnchApplyDTO);
                if(count!=1){
                    throw new BusinessRuntimeException("锚位申报更新失败");
                }
            }
        }
        if(KAOBO.getCode().equals(disShipDynamic.getDynamicTypeCode())){
            List<TAnchApplyDTO> anchApplyDTOS = anchApplyMapper.getByShipvoyageId2(disShipDynamic.getShipvoyageId());
            if(!CollectionUtils.isEmpty(anchApplyDTOS)  && ObjectUtils.isEmpty(anchApplyDTOS.get(0).getLeaveAnchTime()) ){
                TAnchApplyDTO tAnchApplyDTO = new TAnchApplyDTO();
                long time = disShipDynamic.getDynamicStartTime().getTime()-5400000l;
                Date date = new Date(time);
//                tAnchApplyDTO.setPreLeaveAnchTime(date);  //更新起锚时间
                tAnchApplyDTO.setLeaveAnchTime(date);
                tAnchApplyDTO.setStatus(1);
                tAnchApplyDTO.setId(anchApplyDTOS.get(0).getId());
                int count = anchApplyMapper.updateQiMaoTime(tAnchApplyDTO);
                if(count!=1){
                    throw new BusinessRuntimeException("起锚时间更新失败");
                }
            }
        }
    }

    /**
     * 添加拖轮服务记录
     * @param disShipDynamic
     * @param disShipVoyage
     */
    private void insertDisTugRecord(TDisShipDynamicDTO disShipDynamic, TDisShipvoyageDTO disShipVoyage){
        disShipDynamic.getTugs().forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setShipvoyageId(disShipDynamic.getShipvoyageId());
            v1.setShipId(disShipVoyage.getShipId());
            v1.setShipName(disShipVoyage.getShipName());
            v1.setShipDynamicId(disShipDynamic.getId());
            v1.setTimeLength(
                    BigDecimal.valueOf((double) (v1.getEndTime().getTime() - v1.getStartTime().getTime()) / (1000 * 60 * 60))
                            .setScale(1, RoundingMode.HALF_UP)
            );
            //判断非标准原因是否要写入字典
            if("1".equals(v1.getIsStandardUse())){
                if(v1.getReasonCode()!=null){
                    throw new BusinessRuntimeException("非标准原因填写异常！");
                }
            }
            if("0".equals(v1.getIsStandardUse())){
                if(v1.getReasonCode()==null&&StringUtil.isEmpty(v1.getReasonName())){
                    throw new BusinessRuntimeException("请填写非标准原因！");
                }
            }
            HashMap<String, Object> serachMap = new HashMap<>();
            serachMap.put("type","DICT");
            serachMap.put("dictType","DIS_TUG_REASON");
            List<Map<String, Object>> localSelect = selectService.getLocalSelect(serachMap);
            if (CollectionUtils.isEmpty(localSelect)){
                MDictDataDTO mDictDataDTO = new MDictDataDTO();
                mDictDataDTO.setDictLabel(v1.getReasonName());
                mDictDataDTO.setDictType("DIS_TUG_REASON");
                mDictDataDTO.setDictValue("1");
                mDictDataDTO.setSortNum(1);
                v1.setReasonCode(1L);
                mDictDataDTO.setStatus("1");
                mDictService.insertOrUpdateDict(mDictDataDTO);
            }else{
                if(v1.getReasonCode()==null||v1.getReasonCode()==0L){
                    MDictDataDTO mDictDataDTO = new MDictDataDTO();
                    mDictDataDTO.setDictLabel(v1.getReasonName());
                    v1.setReasonCode(Long.parseLong(String.valueOf(localSelect.size()+1)));
                    mDictDataDTO.setDictType("DIS_TUG_REASON");
                    mDictDataDTO.setDictValue(String.valueOf(localSelect.size()+1));//编号以此后排
                    mDictDataDTO.setStatus("1");
                    mDictDataDTO.setSortNum(localSelect.size()+1);
                    mDictService.insertOrUpdateDict(mDictDataDTO);
                }
            }
        });
        if (disShipDynamic.getTugs() != null && disShipDynamic.getTugs().size() > 0) {
            tDisShipDynamicMapper.insertDisTugServiceRecord(disShipDynamic.getTugs());
        }
    }

    private String timePhaseDiffHours(Date startDateTime, Date endDateTime) {
		if(startDateTime == null || endDateTime == null) {
			return "0";
		}
		//todo.复工时长
//		LocalDateTime startTime = LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneId.systemDefault());
//		LocalDateTime endTime = LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneId.systemDefault());

        Integer timeRange = DateUtils.timeDiffMinutes(new DateTime(startDateTime),new DateTime(endDateTime));
        Double hourNum = timeRange/60.0;

//		Duration duration = Duration.between(startTime, endTime);

//		return duration.toHours();
		return String.format("%.2f",hourNum);
	}


	@Override
    public List<TDisShipDynamicDTO> listDisShipDynamic(TDisShipDynamicQueryDTO query) {
        List<TDisShipDynamicDTO> dynamicList = tDisShipDynamicMapper.listDisShipDynamic(query);
        List<TDisShipDynamicDTO> dynamicDTOS = Lists.newArrayList();
        List<TDisShipDynamicDTO> list = Lists.newArrayList();
        for (TDisShipDynamicDTO dto : dynamicList) {
            if("130".equals(dto.getDynamicTypeCode())){
                list.add(dto);
            }else{
                dynamicDTOS.add(dto);
            }
        }
        dynamicDTOS.addAll(list);
        boolean ligang = dynamicDTOS.stream().anyMatch(v->v.getDynamicTypeCode().equals("120"));
        boolean libo = dynamicDTOS.stream().anyMatch(v->v.getDynamicTypeCode().equals("110"));
        List<TDisShipDynamicDTO> result = Lists.newArrayList();
        if(ligang && libo){
            if("120".equals(dynamicDTOS.get(0).getDynamicTypeCode())){
                for(int i=0;i<dynamicDTOS.size();i++){
                    result.add(dynamicDTOS.get(i));
                }
            }else{
                for(int i=0;i<dynamicDTOS.size();i++){
                    if(i==0){
                        result.add(dynamicDTOS.get(1));
                    } else if(i==1){
                        result.add(dynamicDTOS.get(0));
                    }else {
                        result.add(dynamicDTOS.get(i));
                    }
                }
            }
        }else{
            return dynamicDTOS;
        }
        return result;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteDisShipDynamic (Long id) {
        TDisShipDynamicQueryDTO query = new TDisShipDynamicQueryDTO();
        query.setId(id);
        TDisShipDynamicDTO disShipDynamic = tDisShipDynamicMapper.listDisShipDynamic(query).get(0);
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_KEY.getCode() + disShipDynamic.getShipvoyageId())
                .build().run(() -> {
                    query.setId(null);
                    query.setShipvoyageId(disShipDynamic.getShipvoyageId());
                    List<TDisShipDynamicDTO> disShipDynamics = tDisShipDynamicMapper.listDisShipDynamic(query);
                    disShipDynamics = disShipDynamics.stream()
                            .sorted(Comparator.comparing(TDisShipDynamicPO::getDynamicStartTime).reversed())
                            .collect(Collectors.toList());
                    Iterator<TDisShipDynamicDTO> iterator = disShipDynamics.iterator();
                    TDisShipDynamicDTO lastestDisShipDynamic = iterator.next();

                    if (!id.equals(lastestDisShipDynamic.getId()) && !"130".equals(lastestDisShipDynamic.getDynamicTypeCode())) {
                        throw new BusinessRuntimeException("仅支持删除最新的船舶动态");
                    }
                    int count = tDisShipDynamicMapper.getStopCost(disShipDynamic.getShipvoyageItemId());
                    if(count>0){
                        throw new BusinessRuntimeException("已生成停泊费无法删除");
                    }
                    boolean isWanGong = Stream.of(WANGONG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
                    if (isWanGong) {
                    	// 判断船舶是否已经离港，离港不能删除完工记录
                    	List<String> dynamicTypeCodeList = disShipDynamics.stream().map(o -> o.getDynamicTypeCode()).collect(Collectors.toList());
                    	if(dynamicTypeCodeList.contains(LIGANG.getCode())) {
                            throw new BusinessRuntimeException("船舶已离港，无法删除完工记录！");
                        }
                    }

                    // 参数校验完毕，执行删除与修改
                    tDisShipDynamicMapper.deleteDisShipDynamic(id);
                    TDisShipvoyageDTO disShipvoyage = tDisShipDynamicMapper.getDisShipVoyage(disShipDynamic.getShipvoyageId());
                    if (LIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                        disShipvoyage.setLeaveBerthTime(null);
                        TAnchApplyDTO anchApplyDTO = new TAnchApplyDTO();
                        anchApplyDTO.setDynamicId(id);
//                        anchApplyMapper.deleteAnchTimeByDynamicId(anchApplyDTO);
                    }
                    if (KAOBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                        disShipvoyage.setBerthTime(null);
                        disShipvoyage.setBerthId(null);
                        disShipvoyage.setBerthName(null);
                        disShipvoyage.setBerthType(null);
                        disShipvoyage.setBollardNoStart(null);
                        disShipvoyage.setBollardNoEnd(null);
                    }
                    if (QIMAO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                        disShipvoyage.setLeaveAnchorageTime(null);
                    }
                    if (DIMAO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                        disShipvoyage.setArrivalAnchorageTime(null);
                        TAnchApplyDTO anchApplyDTO = new TAnchApplyDTO();
                        anchApplyDTO.setDynamicId(id);
                        anchApplyMapper.deleteAnchTimeByDynamicId(anchApplyDTO);
                    }
                    if (LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                        disShipvoyage.setArrivalAnchorageTime(null);
                        tDisShipDynamicMapper.updateLeavePortTime(disShipvoyage);
                    }


                    TDisShipDynamicDTO previousDisShipDynamic;
                    if (iterator.hasNext()) {
                        previousDisShipDynamic = iterator.next();
                        if (YIBO.getCode().equals(previousDisShipDynamic.getDynamicTypeCode())) {
                            TDisShipDynamicDTO antepenultimateDisShipDynamic = iterator.next();
                            disShipvoyage.setShipStatusCode(antepenultimateDisShipDynamic.getDynamicTypeCode());
                            disShipvoyage.setShipStatusName(antepenultimateDisShipDynamic.getDynamicTypeName());
                        } else {
                            if(!TE_SHU_TING_BO_FEI.getCode().equals(previousDisShipDynamic.getDynamicTypeCode())){
                                disShipvoyage.setShipStatusCode(previousDisShipDynamic.getDynamicTypeCode());
                                disShipvoyage.setShipStatusName(previousDisShipDynamic.getDynamicTypeName());
                            }
                        }
                    } else {
                        disShipvoyage.setShipStatusCode(JIESHOU.getCode());
                        disShipvoyage.setShipStatusName(JIESHOU.getName());
                    }
                    tDisShipDynamicMapper.updateDisShipVoyageAllowNull(disShipvoyage);

                    boolean isVoyageRelated = Stream.of(KAIGONG, TINGGONG, FUGONG, WANGONG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
                    if (isVoyageRelated) {
                        List<TDisShipvoyageItemPO> disShipVoyageItems = tDisShipDynamicMapper.listDisShipVoyageItem(disShipDynamic.getShipvoyageId());
                        TDisShipvoyageItemPO disShipvoyageItem = disShipVoyageItems.stream()
                                .filter(v1 -> disShipDynamic.getLoadUnload().equals(v1.getLoadUnload()))
                                .findFirst()
                                .orElseThrow(null); // 只是为了屏蔽编译器警告
                        if (WANGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                            disShipvoyageItem.setWorkEndTime(null);
                        }
                        if (KAIGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                            disShipvoyageItem.setWorkStartTime(null);
                        }
//                        ShipStatusEnum previousVoyageRelatedShipStatus = disShipDynamics.stream()
//                                .skip(1)
//                                .filter(v1 -> Stream.of(KAIGONG, TINGGONG, FUGONG, WANGONG).anyMatch(v2 -> v1.getDynamicTypeCode().equals(v2.getCode()))
//                                        && v1.getLoadUnload().equals(disShipDynamic.getLoadUnload()))
//                                .map(v1 -> {
//                                    return Arrays.stream(ShipStatusEnum.values())
//                                            .filter(v2 -> v1.getDynamicTypeCode().equals(v2.getCode()))
//                                            .findFirst()
//                                            .orElseThrow(null); // 只是为了屏蔽编译器警告
//                                })
//                                .findFirst()
//                                .orElse(YUBAO);
                        disShipvoyageItem.setShipStatusCode(disShipvoyage.getShipStatusCode());
                        disShipvoyageItem.setShipStatusName(disShipvoyage.getShipStatusName());
                        tDisShipDynamicMapper.updateDisShipVoyageItemAllowNull(disShipvoyageItem);
                    }else{
                        List<TDisShipvoyageItemPO> disShipVoyageItems = tDisShipDynamicMapper.listDisShipVoyageItem(disShipDynamic.getShipvoyageId());
                        for (TDisShipvoyageItemPO disShipVoyageItem : disShipVoyageItems) {
                            disShipVoyageItem.setShipStatusCode(disShipvoyage.getShipStatusCode());
                            disShipVoyageItem.setShipStatusName(disShipvoyage.getShipStatusName());
                            tDisShipDynamicMapper.updateDisShipVoyageItemAllowNull(disShipVoyageItem);
                        }
                    }
                    boolean isBerthRelated = Stream.of(LIBO, YIBO, KAOBO,LIGANG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
                    if (isBerthRelated) {
                        tDisShipDynamicMapper.deleteDisTugServiceRecord(id);
                    }
                });
    }

    @Override
    public List<TDisLowerCabinPO> queryAll(TDisLowerCabinPO tDisLowerCabinPO) {
        return tDisShipDynamicMapper.queryAll(tDisLowerCabinPO);
    }

    @Override
    public List<TDisLowerCabinPO> queryAllDoor(TDisLowerCabinPO tDisLowerCabinPO) {
        return tDisShipDynamicMapper.queryAllDoor(tDisLowerCabinPO);
    }

    @Override
    public List<Map<String, Object>> getListDevice(Long equipmentTypeId,String macName) {
        return tDisShipDynamicMapper.getListDevice(equipmentTypeId,macName);
    }

    @Override
    public void insert(TDisLowerCabinPO tDisLowerCabinPO) {
        tDisLowerCabinPO.setId(snowflake.nextId());
        tDisLowerCabinPO.setWorkType("1");
        int i = tDisShipDynamicMapper.insert(tDisLowerCabinPO);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void addDoor(TDisLowerDoorPO tDisLowerDoorPO) {
//        if(tDisLowerDoorPO.getShipvoyageId()==null){
//            throw new BusinessRuntimeException("缺少必要的请求参数，请检查是否为最新版本");
//        }
            List<TDisLowerCabinPO> list = tDisLowerDoorPO.getHandDoorList();
            //查门机记录
        TDisLowerCabinPO query = new TDisLowerCabinPO();
        query.setShipvoyageId(tDisLowerDoorPO.getShipvoyageId());
        List<TDisLowerCabinPO> list2 = tDisShipDynamicMapper.queryAllDoor(query);
            if(!CollectionUtils.isEmpty(list)){
                if(!"1".equals(tDisLowerDoorPO.getFlag())){
                    if("2".equals(tDisLowerDoorPO.getFlag())){
                        //删除门机记录
                        List<TDisLowerCabinPO> collect = list2.stream().filter(  o->
                                !list.stream().filter(v4 -> v4.getId()!=null).map(v2 -> v2.getId()).distinct().collect(Collectors.toList())
                                        .stream().anyMatch(v3 -> v3.equals(o.getId()))
                        ).collect(Collectors.toList());
                        if(collect!=null && collect.size()>0){
                            tDisShipDynamicMapper.delByIds(collect.stream().map(o->o.getId()).collect(Collectors.toList()));
                        }
                    }else{
                        List<Long> ids = list.stream().map(TDisLowerCabinPO::getShipvoyageId).collect(Collectors.toList());
                        tDisShipDynamicMapper.deleteDoor(ids);
                    }
                }

                for (TDisLowerCabinPO tDisLowerCabinPO:list) {
                    if(tDisLowerCabinPO.getId() == null){
                        tDisLowerCabinPO.setId(snowflake.nextId());
                        tDisShipDynamicMapper.insertDoor(tDisLowerCabinPO);
                    }else{
                        tDisShipDynamicMapper.updateDoor(tDisLowerCabinPO);
                    }

                }

            }else{
                tDisShipDynamicMapper.delByIds(list2.stream().map(o->o.getId()).collect(Collectors.toList()));
            }
    }

    @Override
    public void updateJxXc(TDisLowerCabinPO tDisLowerCabinPO) {
        tDisShipDynamicMapper.updateJxXc(tDisLowerCabinPO);
    }

    @Override
    public void deleteById(Long id) {
        int i = tDisShipDynamicMapper.deleteById(id);
        if(i == 0) {
            throw new BusinessRuntimeException("删除失败");
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean updateDynamic(TDisShipDynamicPO disShipDynamic) {
        //获取这个船所有的动态记录
        List<TDisShipDynamicPO> shipDynamiclist1 = tDisShipDynamicMapper.getDynamicListByShipvoyageId(disShipDynamic.getShipvoyageId());
        //过滤掉特殊停泊费
        List<TDisShipDynamicPO>  shipDynamiclist2 = shipDynamiclist1.stream().filter(x -> !"130".equals(x.getDynamicTypeCode())).sorted(Comparator.comparing(TDisShipDynamicPO::getDynamicStartTime)).collect(Collectors.toList());
        //拿到要更新的那条数据
        TDisShipDynamicPO tDisShipDynamicPO = tDisShipDynamicMapper.getDynamicById(disShipDynamic.getId());
        if(!tDisShipDynamicPO.getDynamicTypeCode().equals("130")){
            Date targetDynamicStartTime = tDisShipDynamicPO.getDynamicStartTime();
            // 遍历列表
            Iterator<TDisShipDynamicPO> iterator = shipDynamiclist2.iterator();
            // 前后两个动态时间的PO对象
            TDisShipDynamicPO previousPO = null;
            TDisShipDynamicPO nextPO = null;

            // 遍历列表以找到目标PO及其前后PO
            while (iterator.hasNext()) {
                TDisShipDynamicPO po = iterator.next();
                if (po.getDynamicStartTime().equals(targetDynamicStartTime)) {
                    if (iterator.hasNext()) {
                        nextPO = iterator.next();
                    }
                    break;
                } else {
                    previousPO = po;
                }
            }
            LocalDateTime previousTime = null;
            LocalDateTime nextTime = null;
            LocalDateTime updateTime = disShipDynamic.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if(previousPO!=null){
                previousTime = previousPO.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            if(nextPO!=null){
                nextTime = nextPO.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            if (previousTime != null && nextTime != null) {
                if (updateTime.isBefore(previousTime) || updateTime.isAfter(nextTime) || updateTime.isEqual(previousTime) || updateTime.isEqual(nextTime) ) {
                    throw new BusinessRuntimeException("时间超过范围请重新填写");
                }
            } else if (nextTime == null) {
                if (previousTime != null && (updateTime.isBefore(previousTime) || updateTime.isEqual(previousTime))) {
                    throw new BusinessRuntimeException("时间超过范围请重新填写");
                }
            } else if (previousTime == null) {
                if (nextTime != null && (updateTime.isAfter(nextTime) || updateTime.isEqual(nextTime))) {
                    throw new BusinessRuntimeException("时间超过范围请重新填写");
                }
            }
        }

        //编辑
        int count = 0;
        // 新增
        if (disShipDynamic.getId() == null) {
            throw new BusinessRuntimeException("本数据未传id为空");
            // 修改
        } else {
            count =  tDisShipDynamicMapper.updateDynamic(disShipDynamic) ;
            updateTug(disShipDynamic);
        }
        boolean isVoyageRelated = Stream.of(KAIGONG, TINGGONG, FUGONG, WANGONG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
        TDisShipvoyagePO disShipvoyage = new TDisShipvoyagePO();
        disShipvoyage.setId(disShipDynamic.getShipvoyageId());
        if (DIMAO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            disShipvoyage.setArrivalAnchorageTime(disShipDynamic.getDynamicStartTime());
            TAnchApplyDTO anchApplyDTO = new TAnchApplyDTO();
            anchApplyDTO.setDynamicId(disShipDynamic.getId());
            anchApplyDTO.setAnchTime(disShipDynamic.getDynamicStartTime());
            anchApplyMapper.updateAnchTimeByDynamicId(anchApplyDTO);
        }
        if (QIMAO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            disShipvoyage.setLeaveAnchorageTime(disShipDynamic.getDynamicStartTime());
        }
        if (KAOBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            TDisShipvoyagePO po = tDisShipVoyageMapper.getDisShipVoyage(disShipDynamic.getShipvoyageId());
            List<TDisShipDynamicPO> listPo = tDisShipDynamicMapper.getDynamicListByShipvoyageId(disShipDynamic.getShipvoyageId());
            if(ObjectUtils.isEmpty(po.getBerthTime()) ){
                disShipvoyage.setBerthTime(disShipDynamic.getDynamicStartTime());
            }else {
                Optional<TDisShipDynamicPO> earliestPo = listPo.stream()
                        .filter(x -> "50".equals(x.getDynamicTypeCode())) // 筛选出DYNAMIC_TYPE_CODE为"靠泊"的对象
                        .sorted(Comparator.comparing(TDisShipDynamicPO::getDynamicStartTime)) // 按日期排序
                        .findFirst(); // 获取第一个（即最早日期）的对象
                earliestPo.ifPresent(x -> {
                    Date earliestStartTime = x.getDynamicStartTime();
                    Date berthTime = po.getBerthTime();
                    if (earliestStartTime != null && berthTime != null && !earliestStartTime.equals(berthTime)) {
                        disShipvoyage.setBerthTime(disShipDynamic.getDynamicStartTime());
                    }
                });
            }
            Optional<TDisShipDynamicPO> earliestPo = listPo.stream()
                    .filter(x -> "50".equals(x.getDynamicTypeCode())) // 筛选出DYNAMIC_TYPE_CODE为"靠泊"的对象
                    .sorted(Comparator.comparing(TDisShipDynamicPO::getDynamicStartTime).reversed()) // 按日期排序
                    .findFirst(); // 获取最后一个（即最晚日期）的对象
            earliestPo.ifPresent(x -> {
                //根据传过来的时间与最大的时间比较 如果相同就是更改了最后一次时间
                Date lateStartTime = x.getDynamicStartTime();
                Date berthLateTime = disShipDynamic.getDynamicStartTime();
                if (lateStartTime != null && berthLateTime != null && lateStartTime.equals(berthLateTime)) {
                    disShipvoyage.setBerthId(disShipDynamic.getBerthId());
                    disShipvoyage.setBerthName(disShipDynamic.getBerthName());
                    disShipvoyage.setBerthType(disShipDynamic.getBerthType());
                    disShipvoyage.setBollardNoStart(disShipDynamic.getBollardNoStart());
                    disShipvoyage.setBollardNoEnd(disShipDynamic.getBollardNoEnd());
                }
            });

            /*disShipvoyage.setBerthId(disShipDynamic.getBerthId());
            disShipvoyage.setBerthName(disShipDynamic.getBerthName());
            disShipvoyage.setBerthType(disShipDynamic.getBerthType());
            disShipvoyage.setBollardNoStart(disShipDynamic.getBollardNoStart());
            disShipvoyage.setBollardNoEnd(disShipDynamic.getBollardNoEnd());*/
        }
        if (YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            disShipvoyage.setBerthId(disShipDynamic.getBerthId());
            disShipvoyage.setBerthName(disShipDynamic.getBerthName());
            disShipvoyage.setBerthType(disShipDynamic.getBerthType());
            disShipvoyage.setBollardNoStart(disShipDynamic.getBollardNoStart());
            disShipvoyage.setBollardNoEnd(disShipDynamic.getBollardNoEnd());
        }
        if (LIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            TDisShipvoyagePO po = tDisShipVoyageMapper.getDisShipVoyage(disShipDynamic.getShipvoyageId());
            List<TDisShipDynamicPO> listPo = tDisShipDynamicMapper.getDynamicListByShipvoyageId(disShipDynamic.getShipvoyageId());
            if(ObjectUtils.isEmpty(po.getLeaveBerthTime()) ){
                disShipvoyage.setLeaveBerthTime(disShipDynamic.getDynamicStartTime());
            }else {
                Optional<TDisShipDynamicPO> earliestPo = listPo.stream()
                        .filter(x -> "110".equals(x.getDynamicTypeCode())) // 筛选出DYNAMIC_TYPE_CODE为"离泊"的对象
                        .sorted(Comparator.comparing(TDisShipDynamicPO::getDynamicStartTime).reversed()) // 按日期排序
                        .findFirst(); // 获取最晚日期的对象
                earliestPo.ifPresent(x -> {
                    Date lateStartTime = x.getDynamicStartTime();
                    Date leaveBerthTime = po.getLeaveBerthTime();
                    if (lateStartTime != null && leaveBerthTime != null && !lateStartTime.equals(leaveBerthTime)) {
                        disShipvoyage.setLeaveBerthTime(disShipDynamic.getDynamicStartTime());
                    }
                });
            }
            TAnchApplyDTO anchApplyDTO = new TAnchApplyDTO();
            anchApplyDTO.setDynamicId(disShipDynamic.getId());
            anchApplyDTO.setAnchTime(disShipDynamic.getDynamicStartTime());
//            anchApplyMapper.updateAnchTimeByDynamicId(anchApplyDTO);
        }
        if (LIGANG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            disShipvoyage.setLeaveBerthTime(disShipDynamic.getDynamicStartTime());
            disShipvoyage.setLeavePortTime(disShipDynamic.getDynamicStartTime());
        }
        tDisShipDynamicMapper.updateDisShipVoyage(disShipvoyage);
        if (isVoyageRelated) {
            TDisShipvoyageItemPO disShipvoyageItem = new TDisShipvoyageItemPO();
            disShipvoyageItem.setId(disShipDynamic.getShipvoyageItemId());
            if (KAIGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                disShipvoyageItem.setWorkStartTime(disShipDynamic.getDynamicStartTime());
            }
            if (WANGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
                disShipvoyageItem.setWorkEndTime(disShipDynamic.getDynamicStartTime());
            }
            tDisShipDynamicMapper.updateDisShipVoyageItem(disShipvoyageItem);
        }
        return count > 0;
    }

    /**
     * 更新拖轮信息
     * @param disShipDynamic
     */
    private void updateTug(TDisShipDynamicPO disShipDynamic){
        //先删除//再保存
//        SHIP_DYNAMIC_ID
        if(disShipDynamic.getId() == null){
            return;
        }
        tDisShipDynamicMapper.deleteDisTugServiceRecord(disShipDynamic.getId());
        TDisShipvoyageDTO disShipVoyage = tDisShipDynamicMapper.getDisShipVoyage(disShipDynamic.getShipvoyageId());
        boolean isBerthRelated = Stream.of(KAOBO, YIBO, LIBO, LIGANG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
        if (isBerthRelated) {
            List<TDisTugServiceRecordPO> tugs = disShipDynamic.getTugs();
            tugs.forEach(v1 -> {
                v1.setId(snowflake.nextId());
                v1.setShipvoyageId(disShipDynamic.getShipvoyageId());
                v1.setShipId(disShipVoyage.getShipId());
                v1.setShipName(disShipVoyage.getShipName());
                v1.setShipDynamicId(disShipDynamic.getId());
                v1.setTimeLength(
                        BigDecimal.valueOf((double) (v1.getEndTime().getTime() - v1.getStartTime().getTime()) / (1000 * 60 * 60))
                                .setScale(1, RoundingMode.HALF_UP)
                );


                v1.setShipDynamicId(disShipDynamic.getId());
                v1.setTimeLength(
                        BigDecimal.valueOf((double) (v1.getEndTime().getTime() - v1.getStartTime().getTime()) / (1000 * 60 * 60))
                                .setScale(1, RoundingMode.HALF_UP)
                );
                //判断非标准原因是否要写入字典

                if("1".equals(v1.getIsStandardUse())){
                    if(v1.getReasonCode()!=null){
                        throw new BusinessRuntimeException("非标准原因填写异常！");
                    }
                }
                if("0".equals(v1.getIsStandardUse())){
                    if(v1.getReasonCode()==null&&StringUtil.isEmpty(v1.getReasonName())){
                        throw new BusinessRuntimeException("请填写非标准原因！");
                    }
                }
                HashMap<String, Object> serachMap = new HashMap<>();
                serachMap.put("type","DICT");
                serachMap.put("dictType","DIS_TUG_REASON");
                List<Map<String, Object>> localSelect = selectService.getLocalSelect(serachMap);
                if (CollectionUtils.isEmpty(localSelect)){
                    MDictDataDTO mDictDataDTO = new MDictDataDTO();
                    mDictDataDTO.setDictLabel(v1.getReasonName());
                    mDictDataDTO.setDictType("DIS_TUG_REASON");
                    mDictDataDTO.setDictValue("1");
                    mDictDataDTO.setSortNum(1);
                    v1.setReasonCode(1L);
                    mDictDataDTO.setStatus("1");
                    mDictService.insertOrUpdateDict(mDictDataDTO);
                }else{
                    if(v1.getReasonCode()==null||v1.getReasonCode()==0L){
                        MDictDataDTO mDictDataDTO = new MDictDataDTO();
                        mDictDataDTO.setDictLabel(v1.getReasonName());
                        v1.setReasonCode(Long.parseLong(String.valueOf(localSelect.size()+1)));
                        mDictDataDTO.setDictType("DIS_TUG_REASON");
                        mDictDataDTO.setDictValue(String.valueOf(localSelect.size()+1));//编号以此后排
                        mDictDataDTO.setStatus("1");
                        mDictDataDTO.setSortNum(localSelect.size()+1);
                        mDictService.insertOrUpdateDict(mDictDataDTO);
                    }
                }
            });
            if (!CollectionUtils.isEmpty(tugs)) {
                tDisShipDynamicMapper.insertDisTugServiceRecord(tugs);
            }
        }
    }

    @Override
    public TDisShipDynamicDTO getDetail(Long id) {
        TDisShipDynamicDTO dto = tDisShipDynamicMapper.getById(id);
        List<TDisTugServiceRecordPO> tugs = tDisShipDynamicMapper.selectDisTugServiceRecord(dto.getId());
        dto.setTugs(tugs);
        return dto;
    }

    @Override
    public byte[] exportExcel(Long shipvoyageId) {
        TDisLowerCabinPO tDisLowerCabinPO = new TDisLowerCabinPO();
        tDisLowerCabinPO.setShipvoyageId(shipvoyageId);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TDisLowerCabinExportPO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TDisLowerCabinExportPO> cursor = tDisShipDynamicMapper.exportExcel(tDisLowerCabinPO)) {
                    Iterator<TDisLowerCabinExportPO> iterator = cursor.iterator();
                    if (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            List<TDisLowerCabinExportPO> salarys = new ArrayList<>();
                            for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                                salarys.add(iterator.next());
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
    public String getTrustRemark(TDisShipDynamicQueryDTO query) {
        List<String> trustRemarkList = tDisShipDynamicMapper.getTrustRemark(query.getShipvoyageItemId());
        return CollectionUtils.isEmpty(trustRemarkList)?"无":String.join("。", trustRemarkList);
    }
}
