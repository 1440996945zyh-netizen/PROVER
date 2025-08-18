package com.yy.ppm.dispatch.service.impl;

import static com.yy.ppm.common.enums.AutoNumEnum.BusinessAutoEnum.SCN;
import static com.yy.ppm.common.enums.ShipStatusEnum.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.yy.common.excel.export.ExcelExporter;
import com.yy.common.excel.export.enums.ExcelTemplate;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.DateUtils;
import com.yy.common.util.HttpUtils;
import com.yy.ppm.dispatch.bean.dto.MSjsbLogDTO;
import com.yy.ppm.dispatch.bean.dto.ShipWorkReportDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import com.yy.ppm.dispatch.bean.po.TDisLowerCabinPO;
import com.yy.ppm.dispatch.mapper.MSjsbLogMapper;
import com.yy.ppm.dispatch.mapper.TDisShipDynamicMapper;
import com.yy.ppm.master.bean.dto.MDictDataDTO;
import com.yy.ppm.master.mapper.MDictMapper;
import com.yy.ppm.produce.mapper.TPrdDispatchSecondaryMapper;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import com.yy.ppm.statement.mapper.TBusHandoverlistMapper;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.concurrent.DistributedLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.po.TBusTrustPO;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.common.enums.DistributedLockKeyPrefixEnum;
import com.yy.ppm.common.enums.InOutPortEnum;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageQueryDTO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;
import com.yy.ppm.dispatch.mapper.TDisShipVoyageMapper;
import com.yy.ppm.dispatch.service.TDisShipVoyageService;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.mapper.MShipMapper;
import com.yy.ppm.master.service.MShipService;

import cn.hutool.core.lang.Snowflake;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-04 11:14
 */
@Service
public class TDisShipVoyageServiceImpl implements TDisShipVoyageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TDisShipVoyageServiceImpl.class);
    @Resource
    private TDisShipVoyageMapper tDisShipVoyageMapper;

    @Autowired
    private MShipService mShipService;
    @Resource
    private MShipMapper mShipMapper;
    @Resource
    private MDictMapper dictMapper;
    @Autowired
    private Snowflake snowflake;

    @Autowired
    private CommonService commonService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SecurityUtils securityUtils;
    @Resource
    private TDisShipDynamicMapper shipDynamicMapper;
    @Resource
    private TBusHandoverlistMapper handoverListMapper;
    @Resource
    private TPrdDispatchSecondaryMapper secondaryMapper;
    @Resource
    private MSjsbLogMapper sjsbLogMapper;

    @Resource
    private SysParameterMapper sysParameterMapper;





    @Override
    public void downShipWorkReport(Long shipVoyageId, HttpServletResponse response) {
        try {
            //获取航次信息
            List<Map<String, Object>> shipVoyages = tDisShipVoyageMapper.getDisShipVoyageById(shipVoyageId);
            List<Long> shipVoyageItemIds = shipVoyages.stream().map(e -> Long.valueOf(String.valueOf((Object) e
                    .get("shipVoyageItemId")))).collect(Collectors.toList());
            //获取船舶动态
            Map<Long, Map<String, Object>> dynamicMap = getDynamicMap(shipVoyageItemIds);
            TDisLowerCabinPO tDisLowerCabinPO = new TDisLowerCabinPO();
            tDisLowerCabinPO.setShipVoyageItemIds(shipVoyageItemIds);
            List<TDisLowerCabinPO> allDoor = shipDynamicMapper.queryAllDoor(tDisLowerCabinPO);
            BigDecimal sumDoor = BigDecimal.ZERO;
            for (TDisLowerCabinPO po : allDoor) {
                sumDoor = sumDoor.add(po.getWorkload());
            }

            Map<Long, Map<String, Object>> handoverListMap = handoverListMap(shipVoyageItemIds);
            Map<Long, Map<String, Object>> workTickets = workTicket(shipVoyageItemIds);

            List<ShipWorkReportDTO> temples = Lists.newArrayList();
            int count = 1;
            for (Map<String, Object> shipVoyage : shipVoyages) {
                ShipWorkReportDTO shipWorkReportDTO = new ShipWorkReportDTO();
                Map<String, Object> temple = Maps.newHashMap();
                Long shipVoyageItemId = Long.valueOf(String.valueOf((Object) shipVoyage.get("shipVoyageItemId")));
                if (ObjectUtils.isNotEmpty(shipVoyage)) {
                    temple.putAll(shipVoyage);
                }
                if (!CollectionUtils.isEmpty(dynamicMap.get(shipVoyageItemId))) {
                    temple.putAll(dynamicMap.get(shipVoyageItemId));
                }
                if (!CollectionUtils.isEmpty(handoverListMap.get(shipVoyageItemId))) {
                    temple.putAll(handoverListMap.get(shipVoyageItemId));
                }
                if (!CollectionUtils.isEmpty(workTickets.get(shipVoyageItemId))) {
                    temple.putAll(workTickets.get(shipVoyageItemId));
                }
                BeanUtil.copyProperties(temple, shipWorkReportDTO);
                BigDecimal sumTime = ObjectUtils.isEmpty(temple.get("sumTime")) ? BigDecimal.ZERO : new BigDecimal(String.valueOf(temple.get("sumTime")));
                BigDecimal sumTon = (CollectionUtils.isEmpty(handoverListMap.get(shipVoyageItemId)) || ObjectUtils.isEmpty(handoverListMap.get(shipVoyageItemId).get("sumTon"))) ? BigDecimal.ZERO : (BigDecimal) handoverListMap.get(shipVoyageItemId).get("sumTon");
                BigDecimal tingGongTime = CollectionUtils.isEmpty(dynamicMap.get(shipVoyageItemId)) ? BigDecimal.ZERO : (BigDecimal) dynamicMap.get(shipVoyageItemId).get("tingGongTime");
                BigDecimal jingTime = ObjectUtils.isEmpty(sumTime) ? BigDecimal.ZERO : sumTime.subtract(tingGongTime);
                //船时效率
                BigDecimal shipEfficiency = ObjectUtils.isEmpty(sumTon) ? BigDecimal.ZERO : (ObjectUtils.isEmpty(jingTime) || BigDecimal.ZERO.equals(jingTime)) ? BigDecimal.ONE : sumTon.divide(jingTime, 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal doorEfficiency = (ObjectUtils.isEmpty((BigDecimal) shipVoyage.get("hatchNum")) || ObjectUtils.isEmpty(jingTime) || BigDecimal.ZERO.equals((BigDecimal) shipVoyage.get("hatchNum")) || BigDecimal.ZERO.equals(jingTime)) ? null : BigDecimal.ZERO.equals(sumDoor) ? BigDecimal.ZERO : sumDoor.divide(((BigDecimal) shipVoyage.get("hatchNum")).multiply(jingTime), 2, BigDecimal.ROUND_HALF_UP);
                shipWorkReportDTO.setSumTime(String.valueOf(sumTime));
                shipWorkReportDTO.setTingGongTime(String.valueOf(tingGongTime));
                shipWorkReportDTO.setJingTime(String.valueOf(jingTime));
                shipWorkReportDTO.setDoorEfficiency(String.valueOf(doorEfficiency));
                shipWorkReportDTO.setShipEfficiency(String.valueOf(shipEfficiency));

                for (ShipWorkReportDTO.HandoverList handoverList : shipWorkReportDTO.getHandoverList()) {
                    handoverList.setHatchNum(shipWorkReportDTO.getHatchNum());
                    handoverList.setSumTime(shipWorkReportDTO.getSumTime());
                    handoverList.setTingGongTime(String.valueOf(tingGongTime));
                    handoverList.setJingTime(String.valueOf(jingTime));
                    handoverList.setDoorEfficiency(String.valueOf(doorEfficiency));
                    handoverList.setShipEfficiency(String.valueOf(shipEfficiency));
                }

                //单机效率：总吨/（舱口数*总时间）
                shipWorkReportDTO.setSheetName("sheet" + count++);
                temples.add(shipWorkReportDTO);
            }
            downReport(temples, response);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new BusinessRuntimeException(e.getMessage());
        }
    }

    /**
     * 设置导出模板
     *
     * @param temples
     * @param response
     * @throws IOException
     */
    void downReport(List<ShipWorkReportDTO> temples, HttpServletResponse response) throws IOException {
        byte[] excelBytes = ExcelExporter.newBuilder()
                .templatePath(ExcelTemplate.SHIP_WORK_REPORT.getTemplatePath())
                .postHandle(workbook -> {
                    XSSFSheet sheet = workbook.getSheetAt(0);
//                    List<CostShipDetailExportDTO> detailList = tCostShipExportDTO.getDetailList();
                    ShipWorkReportDTO temple = temples.get(0);
                    int handoverListSize = CollectionUtils.isEmpty(temple.getHandoverList()) ? 0 : temple.getHandoverList().size();
                    int dynamicSize = CollectionUtils.isEmpty(temple.getDynamicList()) ? 0 : temple.getDynamicList().size();
                    int count = 10;
                    //合并舱口数量
                    if (handoverListSize > 1) {
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(6, 6 + Math.max((handoverListSize - 1), 0), 0, 0);
                        sheet.addMergedRegion(cellRangeAddress);
                    }
                    //毛作业时长
                    if (handoverListSize > 1) {
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(6, 6 + Math.max((handoverListSize - 1), 0), 3, 3);
                        sheet.addMergedRegion(cellRangeAddress);
                    }
                    //停工时长
                    if (handoverListSize > 1) {
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(6, 6 + Math.max((handoverListSize - 1), 0), 4, 4);
                        sheet.addMergedRegion(cellRangeAddress);
                    }
                    //净作业时长
                    if (handoverListSize > 1) {
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(6, 6 + Math.max((handoverListSize - 1), 0), 5, 5);
                        sheet.addMergedRegion(cellRangeAddress);
                    }
                    //船时效率
                    if (handoverListSize > 1) {
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(6, 6 + Math.max((handoverListSize - 1), 0), 6, 6);
                        sheet.addMergedRegion(cellRangeAddress);
                    }
                    //单机效率
                    if (handoverListSize > 1) {
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(6, 6 + Math.max((handoverListSize - 1), 0), 7, 7);
                        sheet.addMergedRegion(cellRangeAddress);
                    }

                    if (dynamicSize > 1) {
                        int startRow = 9 + Math.max((handoverListSize - 1), 0);
                        CellRangeAddress cellRangeAddress_col0 = new CellRangeAddress(startRow - 2, startRow - 2, 1, 9);
                        sheet.addMergedRegion(cellRangeAddress_col0);
                        CellRangeAddress cellRangeAddress_col1 = new CellRangeAddress(startRow - 1, startRow - 1, 0, 1);
                        sheet.addMergedRegion(cellRangeAddress_col1);
                        CellRangeAddress cellRangeAddress_col2 = new CellRangeAddress(startRow - 1, startRow - 1, 2, 3);
                        sheet.addMergedRegion(cellRangeAddress_col2);
                        CellRangeAddress cellRangeAddress_col3 = new CellRangeAddress(startRow - 1, startRow - 1, 4, 5);
                        sheet.addMergedRegion(cellRangeAddress_col3);
                        CellRangeAddress cellRangeAddress_col4 = new CellRangeAddress(startRow - 1, startRow - 1, 6, 7);
                        sheet.addMergedRegion(cellRangeAddress_col4);
                        CellRangeAddress cellRangeAddress_col5 = new CellRangeAddress(startRow - 1, startRow - 1, 8, 9);
                        sheet.addMergedRegion(cellRangeAddress_col5);
                        for (int i = 0; i < dynamicSize; i++) {
                            CellRangeAddress cellRangeAddress1 = new CellRangeAddress(startRow + i, startRow + i, 0, 1);
                            sheet.addMergedRegion(cellRangeAddress1);
                            CellRangeAddress cellRangeAddress2 = new CellRangeAddress(startRow + i, startRow + i, 2, 3);
                            sheet.addMergedRegion(cellRangeAddress2);
                            CellRangeAddress cellRangeAddress3 = new CellRangeAddress(startRow + i, startRow + i, 4, 5);
                            sheet.addMergedRegion(cellRangeAddress3);
                            CellRangeAddress cellRangeAddress4 = new CellRangeAddress(startRow + i, startRow + i, 6, 7);
                            sheet.addMergedRegion(cellRangeAddress4);
                            CellRangeAddress cellRangeAddress5 = new CellRangeAddress(startRow + i, startRow + i, 8, 9);
                            sheet.addMergedRegion(cellRangeAddress5);
                        }
                        CellRangeAddress cellRangeAddress6 = new CellRangeAddress(startRow + dynamicSize, startRow + dynamicSize, 1, 9);
                        sheet.addMergedRegion(cellRangeAddress6);
                    }


//                    设置行高
//                    for (int i = 6; i <= 6+Math.max((count - 1), 0); i++) {
//                        XSSFRow row = sheet.getRow(i);
//                        if (row == null) {
//                            row = sheet.createRow(i);
//                        }
//                        row.setHeightInPoints(30); // 设置行高，单位是磅，可以根据需要进行转换
//                    }
//                    //设置打印区域
//                    workbook.setPrintArea(0,
//                            0,50,
//                            0,100 );
                })
                .build().exportByTemplate(temples);
        ResponseUtils.compliantWithExcel(response, ExcelTemplate.SHIP_WORK_REPORT.getComment());
        response.getOutputStream().write(excelBytes);
    }


    /**
     * 获取船舶动态信息
     *
     * @param shipVoyageItemIds
     * @return
     */
    private Map<Long, Map<String, Object>> getDynamicMap(List<Long> shipVoyageItemIds) {
        List<TDisShipDynamicDTO> dynamics = shipDynamicMapper.getByShipVoyageItemIds(shipVoyageItemIds);
        Map<Long, List<TDisShipDynamicDTO>> shipVoyageItemId_Map = dynamics.stream().collect(Collectors.groupingBy(TDisShipDynamicDTO::getShipvoyageItemId));
        Map<Long, Map<String, Object>> resultMap = Maps.newHashMap();
        shipVoyageItemId_Map.forEach((k, v) -> {
            List<Map<String, Object>> listMap = Lists.newArrayList();
            List<TDisShipDynamicDTO> list = shipVoyageItemId_Map.get(k).stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime)).collect(Collectors.toList());
            BigDecimal tingGongTime = BigDecimal.ZERO;
            String kgsj = null;
            String wgsj = null;
            for (int i = 0; i < list.size(); i++) {
                if ("70".equals(list.get(i).getDynamicTypeCode())) {
                    kgsj = DateUtils.formatDate(list.get(i).getDynamicStartTime(), "yyyy-MM-dd HH:mm:ss");
                }
                if ("100".equals(list.get(i).getDynamicTypeCode())) {
                    wgsj = DateUtils.formatDate(list.get(i).getDynamicStartTime(), "yyyy-MM-dd HH:mm:ss");
                }
                if ("90".equals(list.get(i).getDynamicTypeCode())) {
                    Map<String, Object> map = Maps.newHashMap();
                    TDisShipDynamicDTO fuGong = list.get(i);
                    TDisShipDynamicDTO tingGong = list.get(i - 1);
                    Date startDate = tingGong.getDynamicStartTime();
                    Date endDate = fuGong.getDynamicStartTime();
                    BigDecimal hour = new BigDecimal(DateUtils.timeDiffMinutes(new DateTime(startDate), new DateTime(endDate)) / 60.0).setScale(2, BigDecimal.ROUND_HALF_UP);
                    map.put("tingGongReason", tingGong.getRemark());
                    map.put("tingGongStartTime", DateUtils.formatDate(tingGong.getDynamicStartTime(), "yyyy-MM-dd HH:mm:ss"));
                    map.put("tingGongEndTime", DateUtils.formatDate(tingGong.getDynamicStartTime(), "yyyy-MM-dd HH:mm:ss"));
                    map.put("tingGongTime", hour);
                    tingGongTime = tingGongTime.add(hour);
                    listMap.add(map);
                }
            }
            Double sumTime = DateUtils.timeDiffMinutes(new DateTime(DateUtils.parseDate(kgsj, "yyyy-MM-dd HH:mm:ss").getTime()), new DateTime(DateUtils.parseDate(wgsj, "yyyy-MM-dd HH:mm:ss").getTime())) / 60.0;
            Map<String, Object> map = Maps.newHashMap();
            map.put("kgsj", kgsj);
            map.put("wgsj", wgsj);
            map.put("sumTime", String.format("%.2f", sumTime));
            map.put("dynamicList", listMap);
            map.put("tingGongTime", tingGongTime);
            resultMap.put(k, map);
        });
        return resultMap;
    }

    /**
     * 获取交接清单信息
     *
     * @param shipVoyageItemIds
     * @return
     */
    private Map<Long, Map<String, Object>> handoverListMap(List<Long> shipVoyageItemIds) {
        List<TBusHandoverlistPO> handoverListPOS = handoverListMapper.listByShipVoyageItemIds(shipVoyageItemIds);
        Map<Long, List<TBusHandoverlistPO>> handoverList_Map = handoverListPOS.stream().collect(Collectors.groupingBy(TBusHandoverlistPO::getShipvoyageItemId));
        Map<Long, Map<String, Object>> resultMap = Maps.newHashMap();
        handoverList_Map.forEach((k, v) -> {
            List<Map<String, Object>> list = Lists.newArrayList();
            BigDecimal sumTon = BigDecimal.ZERO;
            for (TBusHandoverlistPO po : v) {
                Map<String, Object> item = Maps.newHashMap();
                item.put("cargoName", po.getCargoName());
                item.put("ton", po.getTon());
                sumTon = sumTon.add(new BigDecimal(String.valueOf(po.getTon())));
                list.add(item);
            }
            Map<String, Object> map = Maps.newHashMap();
            map.put("sumTon", sumTon);
            map.put("handoverList", list);
            resultMap.put(k, map);
        });
        return resultMap;
    }

    /**
     * 获取调度审核的签票
     *
     * @param shipVoyageItemIds
     * @return
     */
    private Map<Long, Map<String, Object>> workTicket(List<Long> shipVoyageItemIds) {
        Map<Long, Map<String, Object>> result = Maps.newHashMap();
        List<Map<String, Object>> list = secondaryMapper.getWorkTicket(shipVoyageItemIds);
        Map<Object, List<Map<String, Object>>> workTickets = list.stream().collect(Collectors.groupingBy(e -> e.get("shipvoyageItemId")));
        for (Long shipVoyageItemId : shipVoyageItemIds) {
            BigDecimal k = new BigDecimal(String.valueOf((Object) shipVoyageItemId));
            List<Map<String, Object>> handoverList = workTickets.get(k);
            if (!CollectionUtils.isEmpty(handoverList)) {
                for (Map<String, Object> stringObjectMap : handoverList) {
                    if ("门机".equals(String.valueOf(stringObjectMap.get("equipmentTypeName")))) {
                        stringObjectMap.put("menjiNo", String.valueOf(stringObjectMap.get("equipmentNo")));
                    } else {
                        stringObjectMap.put("xiaCangNo", String.valueOf(stringObjectMap.get("equipmentNo")));
                    }
                }
            }
            Map<String, Object> map = Maps.newHashMap();
            map.put("workTickets", handoverList);
            result.put(shipVoyageItemId, map);
        }
        return result;
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void insertDisShipVoyageForecast(TDisShipvoyageDTO disShipvoyage) {
        disShipvoyage.setId(snowflake.nextId());
        disShipvoyage.setScn(commonService.getAutoNum(SCN, null));
        disShipvoyage.setShipStatusCode(YUBAO.getCode());
        disShipvoyage.setShipStatusName(YUBAO.getName());
        tDisShipVoyageMapper.insertDisShipVoyage(disShipvoyage);

        if (InOutPortEnum.IN.getCode().equals(disShipvoyage.getImpExp()) || InOutPortEnum.INOUT.getCode().equals(disShipvoyage.getImpExp())) {
            TDisShipvoyageItemPO in = disShipvoyage.getIn();
            in.setId(snowflake.nextId());
            in.setShipvoyageId(disShipvoyage.getId());
            in.setImpExp(InOutPortEnum.IN.getCode());
            in.setShipStatusCode(YUBAO.getCode());
            in.setShipStatusName(YUBAO.getName());
            tDisShipVoyageMapper.insertDisShipVoyageItem(in);
        }

        if (InOutPortEnum.OUT.getCode().equals(disShipvoyage.getImpExp()) || InOutPortEnum.INOUT.getCode().equals(disShipvoyage.getImpExp())) {
            TDisShipvoyageItemPO out = disShipvoyage.getOut();
            out.setId(snowflake.nextId());
            out.setShipvoyageId(disShipvoyage.getId());
            out.setImpExp(InOutPortEnum.OUT.getCode());
            out.setShipStatusCode(YUBAO.getCode());
            out.setShipStatusName(YUBAO.getName());
            tDisShipVoyageMapper.insertDisShipVoyageItem(out);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void insertDisShipVoyage(TDisShipvoyageDTO disShipvoyage) {
        disShipvoyage.setId(snowflake.nextId());
        disShipvoyage.setScn(commonService.getAutoNum(SCN, null));
        disShipvoyage.setReceiveBy(securityUtils.getLoginUserId());
        disShipvoyage.setReceiveByName(securityUtils.getUserInfo().getUserName());
        disShipvoyage.setReceiveTime(new Date());
        disShipvoyage.setShipStatusCode(JIESHOU.getCode());
        disShipvoyage.setShipStatusName(JIESHOU.getName());
        MShipDTO mShipDTO = mShipService.getDetail(disShipvoyage.getShipId());
        Integer shipBhtId = ObjectUtils.isNotEmpty(mShipDTO) ? mShipDTO.getBoHaiTongId() : null;
        if (ObjectUtils.isNotEmpty(shipBhtId)) {
            disShipvoyage.setBoHaiTongShipId(Long.valueOf(shipBhtId));
        }
//        Map<String,Object> res = shipForecastService.synchronize(disShipvoyage);
//        if(ObjectUtils.isNotEmpty(res.get("data"))){
//            JSONObject jsonObject = JSONUtil.parseObj(res.get("data"));
//            String bhtId = ObjectUtils.isEmpty(jsonObject.get("data"))?null:String.valueOf(jsonObject.get("data"));
//            disShipvoyage.setBoHaiTongId(StringUtils.isEmpty(bhtId)?null:Long.valueOf(bhtId));
//        }
        tDisShipVoyageMapper.insertDisShipVoyage(disShipvoyage);
        //更新船舶电话
        MShipDTO shipDTO = new MShipDTO();
        shipDTO.setId(disShipvoyage.getId());
        shipDTO.setCaptainPhone(disShipvoyage.getCaptainPhone());
        mShipMapper.updatePhoneById(shipDTO);
        if (InOutPortEnum.IN.getCode().equals(disShipvoyage.getImpExp()) || InOutPortEnum.INOUT.getCode().equals(disShipvoyage.getImpExp())) {
            TDisShipvoyageItemPO in = disShipvoyage.getIn();
            in.setId(snowflake.nextId());
            in.setShipvoyageId(disShipvoyage.getId());
//            in.setBoHaiTongId(disShipvoyage.getBoHaiTongId());//船舶申报渤海通id
            in.setImpExp(InOutPortEnum.IN.getCode());
            in.setShipStatusCode(JIESHOU.getCode());
            in.setShipStatusName(JIESHOU.getName());
            tDisShipVoyageMapper.insertDisShipVoyageItem(in);
        }
        if (InOutPortEnum.OUT.getCode().equals(disShipvoyage.getImpExp()) || InOutPortEnum.INOUT.getCode().equals(disShipvoyage.getImpExp())) {
            TDisShipvoyageItemPO out = disShipvoyage.getOut();
            out.setId(snowflake.nextId());
            out.setShipvoyageId(disShipvoyage.getId());
//            out.setBoHaiTongId(disShipvoyage.getBoHaiTongId());//船舶申报渤海通id
            out.setImpExp(InOutPortEnum.OUT.getCode());
            out.setShipStatusCode(JIESHOU.getCode());
            out.setShipStatusName(JIESHOU.getName());
            tDisShipVoyageMapper.insertDisShipVoyageItem(out);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public String updateDisShipVoyage(TDisShipvoyageDTO disShipvoyage) {
        try {
            DistributedLock.newBuilder().store(redisTemplate)
                    .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_BUS_TRUST_KEY.getCode() + disShipvoyage.getId())
                    .build().run(() -> {
                        List<TBusTrustPO> trusts = tDisShipVoyageMapper.listTrust(disShipvoyage.getId());
                        if (!trusts.isEmpty()) {
                            throw new BusinessRuntimeException("航次已关联指令，禁止修改");
                        }
                        DistributedLock.newBuilder().store(redisTemplate)
                                .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_KEY.getCode() + disShipvoyage.getId())
                                .build().run(() -> {
                                    TDisShipvoyagePO currentDisShipvoyage = tDisShipVoyageMapper.getDisShipVoyage(disShipvoyage.getId());
//                    if (Stream.of(ZUOFEI, YUBAO, JIESHOU,JVSHOU).noneMatch(v1 -> v1.getCode().equals(currentDisShipvoyage.getShipStatusCode()))) {
//                        throw new BusinessRuntimeException("航次已调度，禁止修改");
//                    }
                                    tDisShipVoyageMapper.updateDisShipVoyage(disShipvoyage);
                                    //更新船舶电话
                                    MShipDTO shipDTO = new MShipDTO();
                                    shipDTO.setId(disShipvoyage.getShipId());
                                    shipDTO.setCaptainPhone(disShipvoyage.getCaptainPhone());
                                    mShipMapper.updatePhoneById(shipDTO);
                                    updateShipVoyageItem(disShipvoyage, currentDisShipvoyage);
                                });
                    });
            return "修改成功";
        } catch (Exception e) {
            try {
                if ("航次已调度，禁止修改".equals(e.getMessage()) || "航次已关联指令，禁止修改".equals(e.getMessage())) {
                    TDisShipvoyagePO po = tDisShipVoyageMapper.getDisShipVoyage(disShipvoyage.getId());
                    TDisShipvoyagePO currentDisShipvoyage = tDisShipVoyageMapper.getDisShipVoyage(disShipvoyage.getId());
                    TDisShipvoyageItemPO newIn = ObjectUtils.isEmpty(disShipvoyage.getIn()) ? new TDisShipvoyageItemPO() : disShipvoyage.getIn();
                    TDisShipvoyageItemPO newOut = ObjectUtils.isEmpty(disShipvoyage.getOut()) ? new TDisShipvoyageItemPO() : disShipvoyage.getOut();
                    List<TDisShipvoyageItemPO> inList = tDisShipVoyageMapper.getDisShipVoyageItemById(newIn.getId());
                    List<TDisShipvoyageItemPO> outList = tDisShipVoyageMapper.getDisShipVoyageItemById(newOut.getId());
                    TDisShipvoyageItemPO oldIn = CollectionUtils.isEmpty(inList) ? new TDisShipvoyageItemPO() : inList.get(0);
                    TDisShipvoyageItemPO oldOut = CollectionUtils.isEmpty(outList) ? new TDisShipvoyageItemPO() : outList.get(0);
                    String oldInPreShipNo = StringUtils.isEmpty(oldIn.getPreChangeShipNo()) ? "" : oldIn.getPreChangeShipNo();
                    String oldInPreShipName = StringUtils.isEmpty(oldIn.getPreChangeShipName()) ? "" : oldIn.getPreChangeShipName();
                    String newInPreShipNo = StringUtils.isEmpty(newIn.getPreChangeShipNo()) ? "" : newIn.getPreChangeShipNo();
                    String newInPreShipName = StringUtils.isEmpty(newIn.getPreChangeShipName()) ? "" : newIn.getPreChangeShipName();

                    String oldOutPreShipNo = StringUtils.isEmpty(oldOut.getPreChangeShipNo()) ? "" : oldOut.getPreChangeShipNo();
                    String oldOutPreShipName = StringUtils.isEmpty(oldOut.getPreChangeShipName()) ? "" : oldOut.getPreChangeShipName();
                    String newOutPreShipNo = StringUtils.isEmpty(newOut.getPreChangeShipNo()) ? "" : newOut.getPreChangeShipNo();
                    String newOutPreShipName = StringUtils.isEmpty(newOut.getPreChangeShipName()) ? "" : newOut.getPreChangeShipName();

                    if (((StringUtils.isEmpty(po.getIsStartWork()) && !StringUtils.isEmpty(disShipvoyage.getIsStartWork())) || !disShipvoyage.getIsStartWork().equals(po.getIsStartWork()))
                            || (!oldInPreShipNo.equals(newInPreShipNo))
                            || (!oldInPreShipName.equals(newInPreShipName))
                            || (!oldOutPreShipNo.equals(newOutPreShipNo))
                            || (!oldOutPreShipName.equals(newOutPreShipName))
                    ) {
                        updateShipVoyageItem(disShipvoyage, currentDisShipvoyage);
                        TDisShipvoyageDTO dto = new TDisShipvoyageDTO();
                        po.setIsStartWork(disShipvoyage.getIsStartWork());
                        BeanUtils.copyProperties(po, dto);
                        tDisShipVoyageMapper.updateDisShipVoyage(dto);
                        return "修改成功";
                    } else {
                        throw new BusinessRuntimeException(e.getMessage());
                    }
                } else {
                    throw new BusinessRuntimeException(e.getMessage());
                }
            } catch (Exception exception) {
                throw new BusinessRuntimeException(exception.getMessage());
            }
        }

    }

    /**
     * 更新航次子表
     *
     * @param disShipvoyage
     * @param currentDisShipvoyage
     */
    public void updateShipVoyageItem(TDisShipvoyageDTO disShipvoyage, TDisShipvoyagePO currentDisShipvoyage) {
        if (InOutPortEnum.IN.getCode().equals(disShipvoyage.getImpExp()) || InOutPortEnum.INOUT.getCode().equals(disShipvoyage.getImpExp())) {
            if (InOutPortEnum.IN.getCode().equals(disShipvoyage.getImpExp())) {
                TDisShipvoyageItemPO in = disShipvoyage.getIn();
                if (in.getId() == null) {
                    LOGGER.warn("updateShipVoyageItem", "IN删除子表");
                    tDisShipVoyageMapper.deleteDisShipvoyageItemByCondition(disShipvoyage.getId(), "IN");
                    in.setId(snowflake.nextId());
                    in.setShipvoyageId(disShipvoyage.getId());
                    in.setImpExp(InOutPortEnum.IN.getCode());
                    in.setShipStatusCode(currentDisShipvoyage.getShipStatusCode());
                    in.setShipStatusName(currentDisShipvoyage.getShipStatusName());
                    tDisShipVoyageMapper.insertDisShipVoyageItem(in);
                } else {
//                in.setShipvoyageId(disShipvoyage.getId());
                    in.setImpExp(InOutPortEnum.IN.getCode());
                    in.setShipStatusCode(currentDisShipvoyage.getShipStatusCode());
                    in.setShipStatusName(currentDisShipvoyage.getShipStatusName());
                    tDisShipVoyageMapper.updateDisShipVoyageItem(in);
                    tDisShipVoyageMapper.deleteDisShipvoyageItemByCondition(disShipvoyage.getId(), "OUT");
                }
            } else if (InOutPortEnum.INOUT.getCode().equals(disShipvoyage.getImpExp())) {
                TDisShipvoyageItemPO in = disShipvoyage.getIn();
                if (in.getId() == null) {
                    LOGGER.warn("updateShipVoyageItem", "IN删除子表");
                    tDisShipVoyageMapper.deleteDisShipvoyageItemByCondition(disShipvoyage.getId(), "IN");
                    in.setId(snowflake.nextId());
                    in.setShipvoyageId(disShipvoyage.getId());
                    in.setImpExp(InOutPortEnum.IN.getCode());
                    in.setShipStatusCode(currentDisShipvoyage.getShipStatusCode());
                    in.setShipStatusName(currentDisShipvoyage.getShipStatusName());
                    tDisShipVoyageMapper.insertDisShipVoyageItem(in);
                } else {
//                in.setShipvoyageId(disShipvoyage.getId());
                    in.setImpExp(InOutPortEnum.IN.getCode());
                    in.setShipStatusCode(currentDisShipvoyage.getShipStatusCode());
                    in.setShipStatusName(currentDisShipvoyage.getShipStatusName());
                    tDisShipVoyageMapper.updateDisShipVoyageItem(in);
                }
            }
        }
        if (InOutPortEnum.OUT.getCode().equals(disShipvoyage.getImpExp()) || InOutPortEnum.INOUT.getCode().equals(disShipvoyage.getImpExp())) {
            if (InOutPortEnum.OUT.getCode().equals(disShipvoyage.getImpExp())) {
                TDisShipvoyageItemPO out = disShipvoyage.getOut();
                if (out.getId() == null) {
                    LOGGER.warn("updateShipVoyageItem", "OUT删除子表");
                    tDisShipVoyageMapper.deleteDisShipvoyageItemByCondition(disShipvoyage.getId(), "OUT");
                    out.setId(snowflake.nextId());
                    out.setShipvoyageId(disShipvoyage.getId());
                    out.setImpExp(InOutPortEnum.OUT.getCode());
                    out.setShipStatusCode(currentDisShipvoyage.getShipStatusCode());
                    out.setShipStatusName(currentDisShipvoyage.getShipStatusName());
                    tDisShipVoyageMapper.insertDisShipVoyageItem(out);
                } else {
//                out.setShipvoyageId(disShipvoyage.getId());
                    out.setImpExp(InOutPortEnum.OUT.getCode());
                    out.setShipStatusCode(currentDisShipvoyage.getShipStatusCode());
                    out.setShipStatusName(currentDisShipvoyage.getShipStatusName());
                    tDisShipVoyageMapper.updateDisShipVoyageItem(out);
                    tDisShipVoyageMapper.deleteDisShipvoyageItemByCondition(disShipvoyage.getId(), "IN");
                }
            } else if (InOutPortEnum.INOUT.getCode().equals(disShipvoyage.getImpExp())) {
                TDisShipvoyageItemPO out = disShipvoyage.getOut();
                if (out.getId() == null) {
                    LOGGER.warn("updateShipVoyageItem", "OUT删除子表");
                    tDisShipVoyageMapper.deleteDisShipvoyageItemByCondition(disShipvoyage.getId(), "OUT");
                    out.setId(snowflake.nextId());
                    out.setShipvoyageId(disShipvoyage.getId());
                    out.setImpExp(InOutPortEnum.OUT.getCode());
                    out.setShipStatusCode(currentDisShipvoyage.getShipStatusCode());
                    out.setShipStatusName(currentDisShipvoyage.getShipStatusName());
                    tDisShipVoyageMapper.insertDisShipVoyageItem(out);
                } else {
//                out.setShipvoyageId(disShipvoyage.getId());
                    out.setImpExp(InOutPortEnum.OUT.getCode());
                    out.setShipStatusCode(currentDisShipvoyage.getShipStatusCode());
                    out.setShipStatusName(currentDisShipvoyage.getShipStatusName());
                    tDisShipVoyageMapper.updateDisShipVoyageItem(out);
                }
            }
        }
    }

    @Override
    public List<Map<String, Object>> getSecCargoCate() {
        return tDisShipVoyageMapper.getSecCargoCateList();
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteDisShipvoyage(List<Long> ids) {
        ids.forEach(v1 -> DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_BUS_TRUST_KEY.getCode() + v1)
                .build().run(() -> {
                    List<TBusTrustPO> trusts = tDisShipVoyageMapper.listTrust(v1);
                    if (!trusts.isEmpty()) {
                        throw new BusinessRuntimeException("航次已关联指令，禁止删除");
                    }

                    DistributedLock.newBuilder().store(redisTemplate)
                            .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_KEY.getCode() + v1)
                            .build().run(() -> {
                                TDisShipvoyagePO currentDisShipvoyage = tDisShipVoyageMapper.getDisShipVoyage(v1);
                                if (Stream.of(ZUOFEI, YUBAO, JIESHOU).noneMatch(v2 -> v2.getCode().equals(currentDisShipvoyage.getShipStatusCode()))) {
                                    throw new BusinessRuntimeException("航次已调度，禁止删除");
                                }
                                tDisShipVoyageMapper.deleteDisShipvoyage(v1);
                                tDisShipVoyageMapper.deleteDisShipvoyageItem(v1);
                            });
                }));
    }

    @Override
    public Pages<TDisShipvoyageDTO> listDisShipVoyage(TDisShipvoyageQueryDTO query, PageParameter parameter) {
        Pages<TDisShipvoyageDTO> page = PageHelperUtils.limit(parameter, () -> tDisShipVoyageMapper.listDisShipVoyage(query));
        List<Long> shipIds = page.getPages().stream().map(e -> e.getShipId()).collect(Collectors.toList());
        List<MShipDTO> shipDTOS = mShipMapper.getByIds(CollectionUtils.isEmpty(shipIds) ? null : shipIds);
        Map<String, String> dictMap = dictMapper.getDictsByType("SHIP_KIND").stream().collect(Collectors.toMap(MDictDataDTO::getDictValue, MDictDataDTO::getDictLabel));
        Map<Long, String> shipKindCodeMap = shipDTOS.stream().collect(Collectors.toMap(MShipDTO::getId, MShipDTO::getShipKindCode));
        Map<Long, String> nationCodeMap = shipDTOS.stream().collect(Collectors.toMap(MShipDTO::getId, e -> StringUtils.isEmpty(e.getNationCode()) ? "" : e.getNationCode()));
        for (TDisShipvoyageDTO e : page.getPages()) {
            String shipKindCode = shipKindCodeMap.get(e.getShipId());
            e.setShipKindName(dictMap.get(shipKindCode));
            e.setNationCode(nationCodeMap.get(e.getShipId()));
            String inVoyage = "";
            String outVoyage = "";
            if (ObjectUtils.isNotEmpty(e.getIn())) {
                inVoyage = e.getIn().getVoyage();
            }
            if (ObjectUtils.isNotEmpty(e.getOut())) {
                outVoyage = e.getOut().getVoyage();
            }
            if (!StringUtils.isEmpty(inVoyage) && !StringUtils.isEmpty(outVoyage)) {
                e.setVoyage(inVoyage + "/" + outVoyage);
            } else if (!StringUtils.isEmpty(inVoyage) && StringUtils.isEmpty(outVoyage)) {
                e.setVoyage(inVoyage);
            } else if (StringUtils.isEmpty(inVoyage) && !StringUtils.isEmpty(outVoyage)) {
                e.setVoyage(outVoyage);
            } else {
                e.setVoyage("/");
            }
        }

        return page;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void voidDisShipvoyage(List<Long> ids, String delRemark) {
        ids.forEach(v1 -> DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_BUS_TRUST_KEY.getCode() + v1)
                .build().run(() -> {
                    List<TBusTrustPO> trusts = tDisShipVoyageMapper.listTrust(v1);
                    if (!trusts.isEmpty()) {
                        throw new BusinessRuntimeException("航次已关联指令，禁止作废");
                    }

                    DistributedLock.newBuilder().store(redisTemplate)
                            .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_KEY.getCode() + v1)
                            .build().run(() -> {
                                TDisShipvoyagePO currentDisShipvoyage = tDisShipVoyageMapper.getDisShipVoyage(v1);
                                if (Stream.of(ZUOFEI, YUBAO, JIESHOU).noneMatch(v2 -> v2.getCode().equals(currentDisShipvoyage.getShipStatusCode()))) {
                                    throw new BusinessRuntimeException("航次已调度，禁止作废");
                                }
                                tDisShipVoyageMapper.voidDisShipvoyage(v1, delRemark, new BasePO());
                                currentDisShipvoyage.setDelRemark(delRemark);
                                currentDisShipvoyage.setBhtAcceptFlag("0");
                            });
                }));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void rejectionDisShipvoyage(List<Long> ids, String rejectionRemark) {
        ids.forEach(v1 -> DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_BUS_TRUST_KEY.getCode() + v1)
                .build().run(() -> {
                    List<TBusTrustPO> trusts = tDisShipVoyageMapper.listTrust(v1);
                    if (!trusts.isEmpty()) {
                        throw new BusinessRuntimeException("航次已关联指令，禁止拒收");
                    }

                    DistributedLock.newBuilder().store(redisTemplate)
                            .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_KEY.getCode() + v1)
                            .build().run(() -> {
                                TDisShipvoyagePO currentDisShipvoyage = tDisShipVoyageMapper.getDisShipVoyage(v1);
                                if (Stream.of(ZUOFEI, YUBAO, JIESHOU).noneMatch(v2 -> v2.getCode().equals(currentDisShipvoyage.getShipStatusCode()))) {
                                    throw new BusinessRuntimeException("航次已调度，禁止拒收");
                                }
                                tDisShipVoyageMapper.rejectionDisShipvoyage(v1, rejectionRemark, new BasePO());
                                currentDisShipvoyage.setDelRemark(rejectionRemark);
                                currentDisShipvoyage.setBhtAcceptFlag("0");

                            });
                }));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void receiveDisShipvoyage(List<Long> ids) {
        ids.forEach(v1 -> DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_KEY.getCode() + "1_" + v1)
                .build().run(() -> {
                    TDisShipvoyagePO currentDisShipvoyage = tDisShipVoyageMapper.getDisShipVoyage(v1);
                    if (!(YUBAO.getCode().equals(currentDisShipvoyage.getShipStatusCode()) || JVSHOU.getCode().equals(currentDisShipvoyage.getShipStatusCode()))) {
                        throw new BusinessRuntimeException("仅预报状态支持接收");
                    }
                    tDisShipVoyageMapper.receiveDisShipvoyage(v1, new BasePO());
                    tDisShipVoyageMapper.receiveDisShipvoyageItem(v1);
                    currentDisShipvoyage.setBhtAcceptFlag("1");

                }));
    }


    /**
     * 根据航次id
     *
     * @param shipVoyageId
     * @return
     */
    public MSjsbLogDTO getSjsbLog(Long shipVoyageId, String delFlag) {
        MSjsbLogDTO dto = new MSjsbLogDTO();
        dto.setId(snowflake.nextId());
        dto.setServiceId(shipVoyageId);
        dto.setJkInfo("船舶航次数据上报");
        dto.setSjsbType(delFlag);//接口类型
        Map<String, String> mapList = tDisShipVoyageMapper.getShipVoyageById(shipVoyageId);
        dto.setServiceFieldI(mapList.get("shipName") + "_" + mapList.get("voyage"));
        return dto;
    }


    @Override
    public Long changeAmount(BigDecimal dwt, String tradeType) {
        if (dwt == null || org.apache.commons.lang3.StringUtils.isBlank(tradeType)) {
            return 0L;
        }

        Long result = tDisShipVoyageMapper.changeAmount(dwt, tradeType);
        if (result == null) {
            return 0L;
        }

        return result;
    }

    /**
     * put("","首次到港");
     * put("","一年内到港");
     * put("","三年内到港");
     *
     * @param shipId
     * @return
     */
    @Override
    public String getLastArrivalType(Long shipId) {
        if (ObjectUtils.isEmpty(shipId)) {
            return "首次到港";
        }
        //查过往记录
        Date now = new Date();
        List<TDisShipvoyagePO> list = tDisShipVoyageMapper.getDisShipVoyageByShipId(shipId);
        if (list.isEmpty()) {
            //去船舶表里查比较
            MShipDTO shipDTO = mShipMapper.getById(shipId);
            if (ObjectUtils.isEmpty(shipDTO) || ObjectUtils.isEmpty(shipDTO.getLastTime())) {
                return "首次到港";
            } else {
                Date lastTime = shipDTO.getLastTime();
                Date oneLastYearTime = DateUtil.offset(now, DateField.YEAR, -1);//获取一年前的时间
                if (DateUtil.compare(lastTime, oneLastYearTime) == 1 || DateUtil.compare(lastTime, oneLastYearTime) == 0) {//一年内到港
                    return "一年内到港";
                } else {//一年内没到港
                    return "超过一年未到港";
                }
            }
        } else {
            Date berthTime = list.get(0).getBerthTime();//靠泊时间
            Date oneLastYearTime = DateUtil.offset(now, DateField.YEAR, -1);//获取一年前的时间
            if (DateUtil.compare(berthTime, oneLastYearTime) == 1 || DateUtil.compare(berthTime, oneLastYearTime) == 0) {//一年内到港
                return "一年内到港";
            } else {//一年内没到港
                return "超过一年未到港";
            }
        }
    }

    @Override
    public String getLastArrivalType(Long voyageId, Long shipId) {
        //检测自身
        if (ObjectUtils.isNotEmpty(voyageId)) {
            TDisShipvoyagePO po = tDisShipVoyageMapper.getDisShipVoyage(voyageId);
            if (ObjectUtils.isNotEmpty(po) && !StringUtils.isEmpty(po.getIsLastArrivalType())) {
                return po.getIsLastArrivalType();
            }
        }
        return getLastArrivalType(shipId);
    }
}
