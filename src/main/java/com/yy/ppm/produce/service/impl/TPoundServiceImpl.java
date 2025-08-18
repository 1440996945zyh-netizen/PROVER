package com.yy.ppm.produce.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.appWork.mapper.TallyMapper;
import com.yy.ppm.produce.bean.SyncDTO;
import com.yy.ppm.common.enums.CleanMassSignEnum;
import com.yy.ppm.common.enums.InoutStorageEnum;
import com.yy.ppm.common.mapper.BusinessCommonMapper;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.produce.bean.dto.workTicket.PoundToPortStorageDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.produce.bean.po.TPrdPortStoragePO;
import com.yy.ppm.produce.mapper.TPrdVehicleReleaseMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.yy.client.CustomsTcpClient;
import com.yy.common.enums.ApiEnum;
import com.yy.common.enums.CustomsPoundEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.FtpUtils;
import com.yy.common.util.JSONUtils;
import com.yy.common.util.api.HttpUtils;
import com.yy.framework.config.CustomsConfig;
import com.yy.ppm.appWork.bean.po.TYardTallyPO;
import com.yy.ppm.produce.bean.po.CustomsLog;
import com.yy.ppm.produce.bean.po.TPoundPO;
import com.yy.ppm.produce.mapper.TPoundMapper;
import com.yy.ppm.produce.service.TPoundService;

import cn.hutool.core.lang.Snowflake;

/**
 * @ClassName 地磅接口,每次过磅地磅系统调用生产系统，如果是二次过磅生产系统调用海关
 * @author ningjp
 * @version 1.0.0
 * @Description
 * @createTime 2023年10月26日 08:21:00
 */
@Service
public class TPoundServiceImpl implements TPoundService {
    private static final MicroLogger LOGGER = new MicroLogger(TPoundServiceImpl.class);

    @Resource
    private TPoundMapper tPoundMapper;

    @Resource
    private CustomsConfig customsConfig;

    @Autowired
    private SecurityUtils securityUtils;
    @Resource
    private Snowflake snowflake;
    @Resource
    private TPrdVehicleReleaseMapper vehicleReleaseMapper;
    @Autowired
    private BusinessCommonService businessCommonService;
    @Resource
    private BusinessCommonMapper businessMapper;
    @Resource
    private TallyMapper tallyMapper;

    @Override
    public Map<String, Object> pound2PPM(String unionNo) {

		Map<String, Object> res = new HashMap<>();

        TPoundPO tPoundPO = tPoundMapper.getWeighByUnionNo(unionNo);
        //根据磅单号，查询过磅数据
        if(ObjectUtils.isEmpty(tPoundPO)){
    		res.put("state", "N");
    		res.put("message", "未查询到磅单数据");
            return res;
        }
        String carCount = String.valueOf(tPoundMapper.getEnterPortCount(tPoundPO.getTsptId()));
        if("0".equals(tPoundPO.getIsFinished())){
//            reservationService.synchronize(tPoundPO.getTsptId(),"3",null);
//            reservationService.synchronize(tPoundPO.getTsptId(),"4",null);

        }else if("1".equals(tPoundPO.getIsFinished())){
//            reservationService.synchronize(tPoundPO.getTsptId(),"6",null);
//            reservationService.synchronize(tPoundPO.getTsptId(),"6",carCount);
//            reservationService.synchronize(tPoundPO.getTsptId(),"7",null);

            //车辆出港时取消激活状态，并移除白名单
            try {
                //移除集疏港计划选中状态
                tPoundMapper.updateStatus(tPoundPO.getTsptId());
                vehicleReleaseMapper.updateStatus(tPoundPO.getTsptId());
                //移除杂货计划选中状态
                //tPoundMapper.updateSundryStatus(tPoundPO.getTruckPlate());
            }catch (Exception e){

            }
            /*
            *   1.二次过磅推送时判断本次过磅记录是否是装载机理货，如果是装载机理货则根据理货记录更新港存。
            *   2.如果查到该磅单不是装载机理货则不需要处理任何逻辑
            *   3.如果查到先判断其作业过程是否需要更新港存，如果不需要则不需要做任何逻辑处理
            *   4.如果查到该过程需要更新场存，先判断是进还是处。
            *   5.根据理货记录查询到票货、作业位置后调用港存更新方法。
            * 注意：所有理货卸载try-catch中，出现异常不需要做任何处理。
            *   新增一个查询界面（车载理货场存变更详情）查询界面必须含有状态条件，默认查询失败的。
            *   1.装载机理货记录连表港存流水，有港存流水记录的为成功，没有的为失败。失败的可以点击更新按钮，重新执行上述逻辑
            *   （增加时间段，查询时间>=系统切换时间，系统切换时间可以写死，或者维护系统参数）。
            *   2.新增定时任务，每天早晨10点执行，查询所有失败的理货记录（查询时间为上2天的10点至今天执行时间），失败的自动更新，自动更新的代码卸载try-catch中，失败不需要阻断任务执行。
            */
            try{
                Map<String,Object> params = Maps.newHashMap();
                params.put("noteId",tPoundPO.getNoteId());
                params.put("startDate",null);
                params.put("endDate",null);
                updatePortStage(getTallyByParams(params));
            }catch (Exception e){
                LOGGER.error("散货装载机理货更新港存失败：" + e.getMessage());
            }
        }
        //中港上传海关
        if ("02".equals(tPoundPO.getPortCode())) {

            try {

                //外贸疏港的
                if (("MY02".equals(tPoundPO.getTradeType()) || "KA08".equals(tPoundPO.getTradeType()))
                		&& ("1".equals(tPoundPO.getWeighNumberype())
                		|| "3".equals(tPoundPO.getWeighNumberype()))) {

                    //************** 第一步，生成XML内容
                    SAXReader sax=new SAXReader();//创建一个SAXReader对象
                    InputStream in = TPoundServiceImpl.class.getClassLoader().getResourceAsStream("xmltemplates/pound.xml");

                    Document document=sax.read(in);//获取document对象,如果文档无节点，则会抛出Exception提前结束
                    Element root = document.getRootElement();//获取根节点

                    // 根节点设置属性
                    Attribute I_E_TYPE = root.attribute("I_E_TYPE");
                    I_E_TYPE.setValue("0".equals(tPoundPO.getIsFinished())?"I":"E");

                    Attribute SEQ_NO = root.attribute("SEQ_NO");
                    String seqNoStr = "9" + snowflake.nextIdStr();
                    SEQ_NO.setValue(seqNoStr);

                    Attribute CHNL_NO = root.attribute("CHNL_NO");
                    CHNL_NO.setValue(CustomsPoundEnum.getCustomsNo("0".equals(tPoundPO.getIsFinished())?tPoundPO.getInBangNo():tPoundPO.getOutBangNo()));

                    // IC
                    Element DR_IC_NO = (Element) document.selectSingleNode("//IC//DR_IC_NO");
                    DR_IC_NO.setText("");
                    Element IC_DR_CUSTOMS_NO = (Element) document.selectSingleNode("//IC//IC_DR_CUSTOMS_NO");
                    IC_DR_CUSTOMS_NO.setText("");
                    Element IC_CO_CUSTOMS_NO = (Element) document.selectSingleNode("//IC//IC_CO_CUSTOMS_NO");
                    IC_CO_CUSTOMS_NO.setText("");
                    Element IC_BILL_NO = (Element) document.selectSingleNode("//IC//IC_BILL_NO");
                    IC_BILL_NO.setText("");
                    Element IC_GROSS_WT = (Element) document.selectSingleNode("//IC//IC_GROSS_WT");
                    IC_GROSS_WT.setText("");
                    Element IC_VE_CUSTOMS_NO = (Element) document.selectSingleNode("//IC//IC_VE_CUSTOMS_NO");
                    IC_VE_CUSTOMS_NO.setText("");
                    Element IC_VE_NAME = (Element) document.selectSingleNode("//IC//IC_VE_NAME");
                    IC_VE_NAME.setText("");
                    Element IC_CONTA_ID = (Element) document.selectSingleNode("//IC//IC_CONTA_ID");
                    IC_CONTA_ID.setText("");
                    Element IC_ESEAL_ID = (Element) document.selectSingleNode("//IC//IC_ESEAL_ID");
                    IC_ESEAL_ID.setText("");
                    Element INFORM_NO = (Element) document.selectSingleNode("//CAR//INFORM_NO");
                    INFORM_NO.setText(tPoundPO.getPlanNo());

                    // WEIGHT  二次过磅的，放重量
                    if ("1".equals(tPoundPO.getIsFinished())) {
                    	Element GROSS_WT = (Element) document.selectSingleNode("//WEIGHT//GROSS_WT");// 地磅称重（值可以为0，不能为空字符串）
                        GROSS_WT.setText(tPoundPO.getWeighGoods());
                    } else {
                    	Element GROSS_WT = (Element) document.selectSingleNode("//WEIGHT//GROSS_WT");// 地磅称重（值可以为0，不能为空字符串）
                        GROSS_WT.setText(StringUtils.isNotBlank(tPoundPO.getWeighSelf())?tPoundPO.getWeighSelf():tPoundPO.getWeighAll());
                    }

                    // CAR
                    Element VE_NAME = (Element) document.selectSingleNode("//CAR//VE_NAME");// 实际车辆牌照号（值可以为空）
                    VE_NAME.setText(tPoundPO.getTruckPlate());
                    Element CAR_EC_NO = (Element) document.selectSingleNode("//CAR//CAR_EC_NO");
                    CAR_EC_NO.setText(tPoundPO.getTruckPlate());
                    Element CAR_EC_NO2 = (Element) document.selectSingleNode("//CAR//CAR_EC_NO2");
                    CAR_EC_NO2.setText(tPoundPO.getTruckPlate());
                    Element VE_CUSTOMS_NO = (Element) document.selectSingleNode("//CAR//VE_CUSTOMS_NO");
                    VE_CUSTOMS_NO.setText("");
                    Element VE_WT = (Element) document.selectSingleNode("//CAR//VE_WT");
                    VE_WT.setText("");

                    // CONTA
                    Element CONTA_NUM = (Element) document.selectSingleNode("//CONTA//CONTA_NUM");
                    CONTA_NUM.setText("");
                    Element CONTA_RECO = (Element) document.selectSingleNode("//CONTA//CONTA_RECO");
                    CONTA_RECO.setText("1");
                    Element CONTA_ID_F = (Element) document.selectSingleNode("//CONTA//CONTA_ID_F");
                    CONTA_ID_F.setText("");
                    Element CONTA_ID_B = (Element) document.selectSingleNode("//CONTA//CONTA_ID_B");
                    CONTA_ID_B.setText("");

                    // SEAL
                    Element ESEAL_ID = (Element) document.selectSingleNode("//SEAL//ESEAL_ID");
                    ESEAL_ID.setText("");
                    Element SEAL_KEY = (Element) document.selectSingleNode("//SEAL//SEAL_KEY");
                    SEAL_KEY.setText("");

                    String xmlStr = document.asXML();

                    // 发送报文给海关
                    CustomsTcpClient customsTcpClient = new CustomsTcpClient("172.19.21.81", 9005);
                    customsTcpClient.sendMsg(xmlStr);

                    CustomsLog customsLog = new CustomsLog();
                    customsLog.setSeqNo(seqNoStr);
                    customsLog.setSendText(xmlStr);
                    customsLog.setPoundNo(tPoundPO.getUnionNo());
                    customsLog.setRecTim(new Date());
                    customsLog.setGateNo("0".equals(tPoundPO.getIsFinished())?tPoundPO.getInBangNo():tPoundPO.getOutBangNo());// gateNo
                    customsLog.setIoFlag("0".equals(tPoundPO.getIsFinished())?"i":"e");

                    int count = tPoundMapper.insertCustomsLog(customsLog);
                    if(count < 1) {
                		res.put("state", "N");
                		res.put("message", "添加数据异常");
                        return res;
                    }

                    res.put("state", "Y");
            		res.put("message", "正常");

                    return res;
                } else if("MY01".equals(tPoundPO.getTradeType())){

                    res.put("state", "Y");
            		res.put("message", "内贸车辆");

                    return res;
                }else{
                    res.put("state", "Y");
                    res.put("message", "内贸车辆");

                    return res;
                }
            } catch (Exception e) {
        		res.put("state", "Y");
        		res.put("message", "正常");

                return res;
            }
        }
        else { //车辆管控
            if ("1".equals(tPoundPO.getIsFinished())) {
                //开始推送车辆管控系统
                HashMap dto = new HashMap();
                dto.put("plateNumber",tPoundPO.getTruckPlate());
                dto.put("companyName","散杂货码头");
                dto.put("secondTime",tPoundPO.getWeighOutDt());
                String josnParam = JSONUtils.NON_NULL.toJSONString(dto);
                try {
                    String carString = HttpUtils.postJson(ApiEnum.HTTP, "172.18.5.187:8082/dev-api/api/carControl/SecondWeigh",josnParam, new HashMap<>());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //车辆管控推送结束
//            Map<String,Object> logParams  = new HashMap<String,Object>();
//            try {
//                createSendXML(tPoundPO);
//                logParams.put("logType", "info");
//                logParams.put("title", "发送海关报文");
//                logParams.put("content","报文磅单号:"+unionNo);
//                logParams.put("errMsg","");
//                tPoundMapper.addLog(logParams);
//            } catch (Exception e) {
//                logParams.put("logType", "error");
//                logParams.put("title", "发送海关报文");
//                logParams.put("content","报文磅单号:"+unionNo);
//                logParams.put("errMsg",e.toString());
//                tPoundMapper.addLog(logParams);
//                throw new RuntimeException(e);
//            }
            }
        }
        return null;
    }


    /**
     * 更新港存
     * @param tallyList
     */
    public void updatePortStage(List<PoundToPortStorageDTO> tallyList){
        List<TPrdPortStorageDetailPO> storageDetailPOS = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(tallyList)){
            for(PoundToPortStorageDTO dto : tallyList){
//                String sourceOrTargetFlag = dto.getSourceOrTargetFlag();//1源，2目的
                String sourceCd = dto.getSourceCd();//1源，2目的
                String targetCd = dto.getTargetCd();//1源，2目的
                //源，且是在场、岸，则减少港存
                if( "05".equals(sourceCd) || "06".equals(sourceCd) ){
                    TPrdPortStorageDetailPO storageDetailPO = new TPrdPortStorageDetailPO();
                    setPortStorageDetailDTO(storageDetailPO,dto);
                    storageDetailPO.setInoutType("1");//1：出库，2：入库
                    storageDetailPO.setQuantity(-dto.getQuantity());
                    storageDetailPO.setTon(new BigDecimal("-1").multiply(dto.getTon()));
                    storageDetailPO.setCreateBy(101l);storageDetailPO.setLoginUserId(101l);
                    storageDetailPO.setCreateByName("装载机理货更新港存");storageDetailPO.setLoginUserName("装载机理货更新港存");
                    storageDetailPO.setCreateTime(new Date());storageDetailPO.setNow(new Date());
                    storageDetailPO.setCompanyId(dto.getCompanyId());
                    storageDetailPO.setCompanyName(dto.getCompanyName());
                    storageDetailPOS.add(storageDetailPO);
                    tallyMapper.updateSourceOrTargetFlag("1",dto.getTallyItemId());
                }
                //目的，且是在场、岸，则增加港存
                else if( "05".equals(targetCd) || "06".equals(targetCd)){
                    TPrdPortStorageDetailPO storageDetailPO = new TPrdPortStorageDetailPO();
                    setPortStorageDetailDTO(storageDetailPO,dto);
                    storageDetailPO.setInoutType("2");//1：出库，2：入库
                    storageDetailPO.setQuantity(dto.getQuantity());
                    storageDetailPO.setTon(dto.getTon());
                    storageDetailPO.setCreateBy(101l);storageDetailPO.setLoginUserId(101l);
                    storageDetailPO.setCreateByName("装载机理货更新港存");storageDetailPO.setLoginUserName("装载机理货更新港存");
                    storageDetailPO.setCreateTime(new Date());storageDetailPO.setNow(new Date());
                    storageDetailPO.setCompanyId(dto.getCompanyId());
                    storageDetailPO.setCompanyName(dto.getCompanyName());
                    storageDetailPOS.add(storageDetailPO);
                    tallyMapper.updateSourceOrTargetFlag("2",dto.getTallyItemId());
                }
            }
            for (TPrdPortStorageDetailPO tPrdPortStorageDetailPO : storageDetailPOS) {
                SyncDTO syncDto = new SyncDTO();
                syncDto.setId(snowflake.nextId());
                syncDto.setBizId(tPrdPortStorageDetailPO.getId());
                //syncDto.setBizType(BusSyncEnum.GOODS_DETAIL.getCode());
                syncDto.setIsDelete("0");
            }
            //如果有主表，则更新
            //如果没有港存主表则保存
            if(!CollectionUtils.isEmpty(storageDetailPOS)){
                insertPortStorageDetail(storageDetailPOS);
            }
        } else{
            throw new BusinessRuntimeException("更新数据为空...或者...地磅信息已经理货并变更港存，请勿重复操作");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, noRollbackFor = BusinessRuntimeException.class)
    public void insertPortStorageDetail(List<TPrdPortStorageDetailPO> storageDetailPOS) {
        List<TPrdPortStoragePO> portStorageSaveList = Lists.newArrayList();
        List<TPrdPortStoragePO> portStorageUpdateList = Lists.newArrayList();
        List<TPrdPortStorageDetailPO> portStorageDetailList = Lists.newArrayList();
        Map<String, List<TPrdPortStorageDetailPO>> listMap = storageDetailPOS.stream().collect(Collectors.groupingBy(e->String.valueOf(e.getCargoInfoId())+String.valueOf(e.getStorehouseId())+String.valueOf(e.getRegionId())+String.valueOf(e.getMassId())));
        listMap.forEach((k,list)->{
            list.forEach(v1 -> {
                if (InoutStorageEnum._70.getCode().equals(v1.getInoutStorageCode())) {
                    if (v1.getCargoMixRecordId() == null && v1.getCargoMixDetailId() == null || v1.getCargoMixRecordId() != null && v1.getCargoMixDetailId() != null) {
                        throw new BusinessRuntimeException("混配时票货混配记录ID与票货混配明细ID必须有且仅能有其一");
                    }
                }
            });
            TPrdPortStorageDetailPO po = list.get(0);
            Map<String, Object> params = Maps.newHashMap();
            params.put("cargoInfoId",po.getCargoInfoId());
            params.put("storehouseId",po.getStorehouseId());
            params.put("regionId",po.getRegionId());
            params.put("massId",po.getMassId());
            TPrdPortStoragePO portStorage = businessMapper.getPortStorage(params);
            if(ObjectUtils.isEmpty(portStorage)){
                portStorage = new TPrdPortStoragePO();
                portStorage.setId(snowflake.nextId());
                portStorage.setCompanyId(po.getCompanyId());
                portStorage.setCompanyName(po.getCompanyName());
                portStorage.setCargoInfoId(po.getCargoInfoId());
                portStorage.setStorehouseId(po.getStorehouseId());
                portStorage.setStorehouseName(po.getStorehouseName());
                portStorage.setRegionId(po.getRegionId());
                portStorage.setRegionName(po.getRegionName());
                portStorage.setMassId(po.getMassId());
                portStorage.setMassName(po.getMassName());
                portStorage.setCreateBy(101l);portStorage.setLoginUserId(101l);
                portStorage.setCreateByName("装载机理货更新港存");portStorage.setLoginUserName("装载机理货更新港存");
                portStorage.setCreateTime(new Date());portStorage.setNow(new Date());
                if (!list.stream().allMatch(v2 -> v2.getQuantity() == null)) {
                    portStorage.setQuantity(list.stream().mapToInt(v2 -> Optional.ofNullable(v2.getQuantity()).orElse(0)).sum());
                }
                portStorage.setTon(list.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add));
                portStorage.setCleanMassSign(CleanMassSignEnum._0.getCode());
//                businessMapper.insertPortStorage(portStorage);
                portStorageSaveList.add(portStorage);
            }else{
//                if (CleanMassSignEnum._1.getCode().equals(portStorage.getCleanMassSign())) {
//                    TBusCargoInfoDTO dto = businessMapper.getCargoInfoById(portStorage.getCargoInfoId());
//                    LOGGER.error(dto.getCargoInfoNo()+" "+portStorage.getRegionName()+"/"+portStorage.getMassName()+" 当前港存已清场");
//                }
                List<TPrdPortStorageDetailPO> tempPortStorageDetails = businessMapper.listPortStorageDetail(portStorage.getId());
                tempPortStorageDetails.addAll(list);
                if (!tempPortStorageDetails.stream().allMatch(v2 -> v2.getQuantity() == null)) {
                    portStorage.setQuantity(tempPortStorageDetails.stream().mapToInt(v2 -> Optional.ofNullable(v2.getQuantity()).orElse(0)).sum());
                } else {
                    portStorage.setQuantity(null);
                }
                portStorage.setTon(tempPortStorageDetails.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add));
//                businessMapper.updatePortStorage(portStorage);
                portStorageUpdateList.add(portStorage);
            }
            TPrdPortStoragePO finalPortStorage = portStorage;
            list.forEach(v2 -> v2.setPortStorageId(finalPortStorage.getId()));
//            businessMapper.insertPortStorageDetail(list);
            portStorageDetailList.addAll(list);
        });
        if(!CollectionUtils.isEmpty(portStorageSaveList)){
            businessMapper.insertPortStorages(portStorageSaveList);
        }
        if(!CollectionUtils.isEmpty(portStorageUpdateList)){
            businessMapper.updatePortStorages(portStorageUpdateList);
        }
        if(!CollectionUtils.isEmpty(portStorageDetailList)){
            businessMapper.insertPortStorageDetail(portStorageDetailList);
        }
//                businessCommonService.insertPortStorageDetail(storageDetailPOS);
//                businessMapper.insertPortStorage(portStorage);
    }


    /**
     * 获取
     * @param params
     * @return
     */
    public List<PoundToPortStorageDTO> getTallyByParams(Map<String, Object> params){
//        List<PoundToPortStorageDTO> tallyList = tPoundMapper.getTallyByParams(params);
        return tPoundMapper.getTallyByParams(params);
    }

    void setPortStorageDetailDTO(TPrdPortStorageDetailPO storageDetailPO, PoundToPortStorageDTO dto){
        storageDetailPO.setId(snowflake.nextId());
        storageDetailPO.setCargoInfoId(dto.getCargoInfoId());
        storageDetailPO.setWorkDate(dto.getWorkDate());
        storageDetailPO.setClassCode(dto.getClassCode());
        storageDetailPO.setClassName(dto.getClassName());
        storageDetailPO.setProcessDetailCode(dto.getProcessCode());
        storageDetailPO.setProcessDetailName(dto.getProcessName());
        storageDetailPO.setStorehouseId(dto.getStorehouseId());//场
        storageDetailPO.setStorehouseName(dto.getStorehouseName());
        storageDetailPO.setRegionId(dto.getRegionId());//区
        storageDetailPO.setRegionName(dto.getRegionName());//区
        storageDetailPO.setMassId(dto.getMassId());//垛位
        storageDetailPO.setMassName(dto.getMassName());
        storageDetailPO.setInoutStorageCode(InoutStorageEnum._10.getCode());
        storageDetailPO.setInoutStorageName(InoutStorageEnum._10.getLabel());
        storageDetailPO.setCargoTallyId(dto.getTallyId());
        storageDetailPO.setCargoTallyDetailId(dto.getTallyItemId());
        storageDetailPO.setInoutDate(dto.getWorkDate());
        storageDetailPO.setCleanMassSign(CleanMassSignEnum._0.getCode());//未清垛
        storageDetailPO.setCreateByName("地磅更新港存");
        storageDetailPO.setCreateBy(101l);
        storageDetailPO.setCreateTime(new Date());
    }

    @XmlRootElement(name = "COMMAND_INFO")
    static class CommandInfo {

        private String checkResult;

        private Map<String, Object> gps;

        private Map<String, Object> seal;

        private String formId;

        private String opHint;

        private Map<String, Object> led;

        @XmlElement(name = "CHECK_RESULT")
		public String getCheckResult() {
			return checkResult;
		}

		public void setCheckResult(String checkResult) {
			this.checkResult = checkResult;
		}

        @XmlElement(name = "GPS")
		public Map<String, Object> getGps() {
			return gps;
		}

		public void setGps(Map<String, Object> gps) {
			this.gps = gps;
		}

        @XmlElement(name = "SEAL")
		public Map<String, Object> getSeal() {
			return seal;
		}

		public void setSeal(Map<String, Object> seal) {
			this.seal = seal;
		}

        @XmlElement(name = "FORM_ID")
		public String getFormId() {
			return formId;
		}

		public void setFormId(String formId) {
			this.formId = formId;
		}

        @XmlElement(name = "OP_HINT")
		public String getOpHint() {
			return opHint;
		}

		public void setOpHint(String opHint) {
			this.opHint = opHint;
		}

        @XmlElement(name = "LED")
		public Map<String, Object> getLed() {
			return led;
		}

		public void setLed(Map<String, Object> led) {
			this.led = led;
		}
    }

    @Override
    public Map<String, Object> ppm2Pound(String poundNo, String gateNo, String seqNo) {

		Map<String, Object> res = new HashMap<>();

		CustomsLog data = tPoundMapper.getCustomsLogByPoundNoAndGateNo(poundNo, gateNo, seqNo);
		if(data != null && StringUtils.isNoneBlank(data.getReceiveText())) {

			try {
//				String receiveText = data.getReceiveText();
//
//				// 创建Unmarshaller对象
//				Unmarshaller unmarshaller = JAXBContext.newInstance(CommandInfo.class).createUnmarshaller();
//
//				// 从XML字符串读取数据并转换为对象
//				CommandInfo commandInfo = (CommandInfo) unmarshaller.unmarshal(new StringReader(receiveText));

				res.put("release", data.getRelease());
				res.put("reason", data.getReason());
	            return res;
			} catch (Exception e) {
	            return res;
			}
		} else {
			res.put("release", "Y");
			res.put("reason", "正常");
            return res;
		}
    }

    public static void main(String[] args) {
    	Map<String, Object> res = new HashMap<>();
		try {
			String receiveText = "<COMMAND_INFO AREA_ID=\"000000000\" CHNL_NO=\"4310040002\" I_E_TYPE=\"I\" SEQ_NO=\"SDGKWFG1683684306943\"><CHECK_RESULT>Y</CHECK_RESULT><GPS><VE_NAME>3GX1598</VE_NAME><GPS_ID></GPS_ID><ORIGIN_CUSTOMS></ORIGIN_CUSTOMS><DEST_CUSTOMS></DEST_CUSTOMS></GPS><SEAL><ESEAL_ID></ESEAL_ID><SEAL_KEY></SEAL_KEY></SEAL><FORM_ID></FORM_ID><OP_HINT>海关放行</OP_HINT><LED><DISPLAY>海关放行</DISPLAY></LED></COMMAND_INFO>";

			// 创建Unmarshaller对象
			Unmarshaller unmarshaller = JAXBContext.newInstance(CommandInfo.class).createUnmarshaller();

			// 从XML字符串读取数据并转换为对象
			CommandInfo commandInfo = (CommandInfo) unmarshaller.unmarshal(new StringReader(receiveText));

			res.put("release", "Y".equals(commandInfo.getCheckResult().trim())?"Y":"N");
			res.put("reason", "Y".equals(commandInfo.getCheckResult().trim())?"放行":commandInfo.getOpHint());
           	System.out.println(res);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

    private void createSendXML(TPoundPO tPoundPO) throws Exception {
        //从配置文件中读取海关报文信息

        String localTempPath = customsConfig.getLocalTempPath();
        String unitCode = customsConfig.getUnitCode();
        String unitName = customsConfig.getUnitName();
        String customsCode = customsConfig.getCustomsCode();

        //将磅单数据生成xml文件
        SAXReader sax = new SAXReader();//创建一个SAXReader对象
        InputStream in = TPoundServiceImpl.class.getClassLoader().getResourceAsStream("xmltemplates/weight.xml");

        Document document = sax.read(in);//获取document对象,如果文档无节点，则会抛出Exception提前结束
        Element root=document.getRootElement();//获取根节点

        List<Element> firstList =  root.elements();
        Element MessageHead = firstList.get(0);
        Element MessageBody = firstList.get(1);


        List<Element> headList =  MessageHead.elements();
        Element MessageType =  headList.get(0);
        Element MessageId =  headList.get(1);
        Element MessageTime =  headList.get(2);
        Element SenderId =  headList.get(3);
        Element SenderAddress =  headList.get(4);
        Element ReceiverId =  headList.get(5);
        Element ReceiverAddress =  headList.get(6);
        Element PlatFormNo =  headList.get(7);
        Element CustomCode =  headList.get(8);
        Element SeqNo =  headList.get(9);
        Element Note =  headList.get(10);


        String randomNum =  generateNumber();
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmm");
        String messageId = unitCode + formatter2.format(currentTime)+randomNum;
        MessageId.setText(messageId);
        MessageTime.setText(formatter.format(currentTime));


        List<Element> bodyList =  MessageBody.elements();
        Element WFG_DBXX =  (Element) bodyList.get(0).elements().get(0);
        String sender = "";
        if ("01".equals(tPoundPO.getPortCode())) {
            sender = "JN02";
        }
        if ("03".equals(tPoundPO.getPortCode())) {
            sender = "JN15";
        }
        SenderId.setText(sender);
        SenderAddress.setText(sender);
        if ("02".equals(tPoundPO.getPortCode())) {
            return;
        }

        WFG_DBXX.addElement("AUTO_ID").setText(tPoundPO.getUnionNo());
        WFG_DBXX.addElement("SCN").setText(tPoundPO.getScn() == null?"":tPoundPO.getScn());
        WFG_DBXX.addElement("BIZ_TYPE").setText(tPoundPO.getTradeType() == null?"":tPoundPO.getTradeType());
        WFG_DBXX.addElement("TRAF_NAME").setText(tPoundPO.getComName() == null?"":tPoundPO.getComName());
        WFG_DBXX.addElement("TRAF_CODE").setText(tPoundPO.getComNo() == null?"":tPoundPO.getComNo());
        WFG_DBXX.addElement("TRADE_NAME").setText(tPoundPO.getConUnit() == null?"":tPoundPO.getConUnit());
        WFG_DBXX.addElement("GOODS_NAME").setText(tPoundPO.getGoodsName() == null?"":tPoundPO.getGoodsName());
        WFG_DBXX.addElement("CAR_NO").setText(tPoundPO.getTruckPlate());

        WFG_DBXX.addElement("GOODS_WT").setText(tPoundPO.getWeighGoods());
        WFG_DBXX.addElement("GROSS_WT").setText(tPoundPO.getWeighAll());
        WFG_DBXX.addElement("TARE_WT").setText(tPoundPO.getWeighSelf());

        WFG_DBXX.addElement("ISINVALID").setText(tPoundPO.getIsDelete());
        WFG_DBXX.addElement("AREA_CODE").setText(sender);
        WFG_DBXX.addElement("IE_FLAG").setText("E");

        WFG_DBXX.addElement("CUSTOMS_CODE").setText(customsCode);
        WFG_DBXX.addElement("INPUT_CODE").setText(unitCode);
        WFG_DBXX.addElement("INPUT_NAME").setText(unitName);
        WFG_DBXX.addElement("DECLARE_CODE").setText(unitCode);
        WFG_DBXX.addElement("DECLARE_NAME").setText(unitName);
        WFG_DBXX.addElement("DECLARE_PERSON").setText("system");
        WFG_DBXX.addElement("DECLARE_DATE").setText(formatter.format(currentTime));


        File xmlFile = new File(localTempPath+messageId+".xml");
        FileOutputStream outStream = new FileOutputStream(xmlFile);
        java.io.Writer wr = new java.io.OutputStreamWriter(outStream, "UTF-8");
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");//设置编码格式

        XMLWriter xmlWriter = new XMLWriter(wr,format);

        xmlWriter.write(document);
        xmlWriter.close();

        //通过ftp发送报文给海关
//        sendXML(localTempPath+messageId+".xml",messageId+".xml",tPoundPO);
    }

    public void sendXML(String xmlFilePath,String xmlFileName,TPoundPO tPoundPO) throws Exception{

        String ftp_host = customsConfig.getHost();
        String ftp_port = customsConfig.getPort();
        String ftp_username = customsConfig.getUsername();
        String ftp_password = customsConfig.getPassword();
        String ftp_saveFilePath = customsConfig.getSaveFilePath();

        File file = new File(xmlFilePath);
        InputStream inputStream =  new FileInputStream(file);

        FtpUtils ftp = new FtpUtils();
        ftp.uploadFile(ftp_saveFilePath,ftp_host,ftp_port,ftp_username,ftp_password, xmlFileName, inputStream);
        //现在由于我们无法登陆到服务器上看文件，提供以下方法down下来核对上传文件是否成功
//        ftp.downloadFile(ftp_saveFilePath, xmlFileName, "E://");
        System.out.println("ok");

//        uploadFileByFtp(ftp_host, ftp_port, ftp_username, ftp_password, ftp_saveFilePath, xmlFileName, in);

        //保存发送记录
//        tPoundMapper.insertXmlSendRecord(tPoundPO);
    }

    public static String generateNumber() {
        String no="";
        int num[]=new int[8];
        int c=0;
        for (int i = 0; i < 8; i++) {
            num[i] = new Random().nextInt(10);
            c = num[i];
            for (int j = 0; j < i; j++) {
                if (num[j] == c) {
                    i--;
                    break;
                }
            }
        }
        if (num.length>0) {
            for (int i = 0; i < num.length; i++) {
                no+=num[i];
            }
        }
        return no;
    }

    /**
     * Description: 向FTP服务器上传文件
     * @Version1.0
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param path FTP服务器保存目录
     * @param filename 上传到FTP服务器上的文件名
     * @param input 输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFileByFtp(String host,int port,String username, String password, String path, String filename, InputStream input) throws Exception {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host,port);//连接FTP服务器
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);//登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            ftp.changeWorkingDirectory(path);
            ftp.storeFile(filename, input);

            input.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

//    public static void main(String[] args) {
//
//        String headerHex = "E25C4B89";
//        Socket client = null;
//        try {
//            client = new Socket("127.0.0.1", 9006);
//            OutputStream out = client.getOutputStream();
//            byte[] b = FtpUtils.hexStringToBytes(headerHex);
//            out.write(b);
//
//            out.close();
//            client.close();
//            System.out.println("发送成功");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * @Title: int2Bytes
     * @Description: 数据长度
     * @param: @param num
     * @param: @return
     * @return: byte[]
     */
    public static byte[] int2Bytes(int num, int len) {
        StringBuffer sb = new StringBuffer(String.valueOf(num));
        int length = len - sb.length();
        for (int i = 0; i < length; i++) {
            sb.insert(0, '0');
        }
        return sb.toString().getBytes();
    }

//	@Override
//	public void XMLInfoWLJKRet(CustomsBaseMessage baseMessage) {
//
//		StringReader reader = new StringReader(baseMessage.getMessageXML());
//		SAXReader saxReader = new SAXReader();
//		try {
//		    Document document = saxReader.read(reader);
//		    Element root = document.getRootElement();//获取根节点
//
//		    String CHECK_RESULT = root.selectSingleNode("//CHECK_RESULT").getText();
//		    String VE_NAME = root.selectSingleNode("//GPS//VE_NAME").getText();
//		    String GPS_ID = root.selectSingleNode("//GPS//GPS_ID").getText();
//		    String ORIGIN_CUSTOMS = root.selectSingleNode("//GPS//ORIGIN_CUSTOMS").getText();
//		    String DEST_CUSTOMS = root.selectSingleNode("//GPS//DEST_CUSTOMS").getText();
//
//		    String CONTA_NUM = root.selectSingleNode("//SEAL//CONTA_NUM").getText();
//		    String ESEAL_ID = root.selectSingleNode("//SEAL//ESEAL_ID").getText();
//		    String SEAL_KEY = root.selectSingleNode("//SEAL//SEAL_KEY").getText();
//
//		    String FORM_ID = root.selectSingleNode("//FORM_ID").getText();
//
//		    String OP_HINT = root.selectSingleNode("//OP_HINT").getText();
//
//		    // 解析CHECK_RESULT数据 -- 参照文档
//
//		    CHECK_RESULT.substring(0, 1);// 0：放行1：不放行
//		    CHECK_RESULT.substring(1, 1);// 0：抬杠  1：不抬杠 2：落杆 其余不予处理
//
//		    // 数据写入数据库
//		} catch (Exception e) {
//		    e.printStackTrace();
//		} finally {
//		    reader.close();
//		}
//	}

	@Override
	public Map<String, Object> grGateInOut(String unionNo, String gateNo, String state, String truckno, String informno) {

		Map<String, Object> res = new HashMap<>();

        String seqNoStr = "9" + snowflake.nextIdStr();
        String xmlStr = "";

		if(StringUtils.isNotBlank(unionNo)) {
	        TPoundPO tPoundPO = tPoundMapper.getWeighByUnionNo(unionNo);
	        if(tPoundPO == null) {
    	    	res.put("state", "N");
    	    	res.put("message", "未查询到磅单数据！");
    	    	return res;
	        }

	        if ("02".equals(tPoundPO.getPortCode())) {

    			CustomsTcpClient socketUtil = new CustomsTcpClient("172.19.21.81", 9005);
    			xmlStr = getInOutMes(tPoundPO, seqNoStr, gateNo, state);
    			socketUtil.sendMsg(xmlStr);
    			res.put("state", "Y");
    			res.put("message", "发送数据成功");
	       } else {
	   			res.put("state", "N");
		    	res.put("message", "非中港区车辆");
	       }
		} else {
			// 查询车辆最近一次的理货信息
			TYardTallyPO yardTallyPO = tPoundMapper.getYardTallyByTruckNo(truckno, informno);
			//if(yardTallyPO != null) {

				TPoundPO tPoundPO = new TPoundPO();
				tPoundPO.setTruckPlate(truckno);
				tPoundPO.setPlanNo(informno);
				tPoundPO.setWeighSelf((yardTallyPO == null || yardTallyPO.getTon() == null)?"":yardTallyPO.getTon().toString());
				tPoundPO.setWeighAll((yardTallyPO == null || yardTallyPO.getTon() == null)?"":yardTallyPO.getTon().toString());

    			CustomsTcpClient socketUtil = new CustomsTcpClient("172.19.21.81", 9005);
    			xmlStr = getInOutMes(tPoundPO, seqNoStr, gateNo, state.toUpperCase());
    			socketUtil.sendMsg(xmlStr);
    			res.put("state", "Y");
    			res.put("message", "发送数据成功");
//			} else {
//	   			res.put("state", "N");
//		    	res.put("message", "未查询到理货记录");
//			}
			/**
			 * 1、卸船计划、短倒计划、提货计划生成同步生成报文发送海关（新增报文）
				2、计划绑定车辆信息发送海关（与1报文格式相同，补充车辆明细报文）
				--短倒/卸船
				3、车辆称重。（地磅调用华东接口生成地磅称重报文）
				4、卸船/短倒计划，车辆进入粮食监管区。闸口进门，地磅闸口调用华东接口生成磅单报文发送海关。
				5、卸船/短倒计划，车辆出粮食监管区。闸口出门，地磅闸口调用华东接口生成磅单报文发送海关。海关发送放行指令（与港区放行指令报文格式一致），华东解析。闸口调用接口校验是否抬干

				--提货
				6、车辆刷卡选择作业计划，发送华东，华东生成过闸计划报文发送海关
				7、车辆称皮，生成报文。（地磅调用华东接口生成车辆空皮报文，发送海关）
				8、提货计划，车辆进入粮食监管区。闸口进门，地磅闸口调用华东接口生成磅单报文发送海关。
				7、提货计划，车辆出粮食监管区。闸口出门，地磅闸口调用华东接口生成磅单报文发送海关。海关发送放行指令（与港区放行指令报文格式一致），华东解析。闸口调用接口校验是否抬干
				8、车辆重车过磅，生成过磅报文，发送海关。海关发送放行指令，华东解析
				9、提货计划，车辆出港区闸口。闸口调用接口校验是否抬干
			 */
//			// 根据车牌号、计划号查询集疏港车辆信息
//			TBusVehicleReservationPO busVehicleReservation = tPoundMapper.getBusVehicleReservation(truckno, informno);
//			if(busVehicleReservation != null) {
//    			CustomsTcpClient socketUtil = new CustomsTcpClient("172.19.21.81", 9005);
//    			TPoundPO tPoundPO = new TPoundPO();
//    			tPoundPO.setPlanNo(busVehicleReservation.getPlanNo());
//    			tPoundPO.setTruckPlate(busVehicleReservation.getVehicleNo());
//    			tPoundPO.setwei
//
//    			xmlStr = getInOutMes(tPoundPO, seqNoStr, gateNo, state.toUpperCase());
//    			socketUtil.sendMsg(xmlStr);
//    			res.put("state", "Y");
//    			res.put("message", "发送数据成功");
//			}

			// 判断倒运车是否下发指令
//			List<TBusVehicleTransferPO> busVehicleTransfer = tPoundMapper.getTBusVehicleTransferPOList(truckno, informno);
//			if(CollectionUtils.isEmpty(busVehicleTransfer)) {
//	   			res.put("state", "N");
//		    	res.put("message", "当前设备未查询到可用计划！");
//		    	return res;
//			}
//
//			// 查询当前设备最近一次的过磅数据
//	        TPoundPO tPoundPO = tPoundMapper.getWeighByCondition(truckno, informno);
//
//	        if(tPoundPO == null) {
//    	    	res.put("state", "N");
//    	    	res.put("message", "未查询到最近一次过磅数据！");
//    	    	return res;
//	        }
//
//			CustomsTcpClient socketUtil = new CustomsTcpClient("172.19.21.81", 9005);
//			xmlStr = getInOutMes(tPoundPO, seqNoStr, gateNo, state.toUpperCase());
//			socketUtil.sendMsg(xmlStr);
//			res.put("state", "Y");
//			res.put("message", "发送数据成功");
		}

		CustomsLog customsLog = new CustomsLog();
		customsLog.setSeqNo(seqNoStr);
		customsLog.setSendText(xmlStr);
		customsLog.setPoundNo(unionNo);
		customsLog.setRecTim(new Date());
		customsLog.setGateNo(gateNo);// gateNo
		customsLog.setIoFlag(state);// "i":"e"

		int count = tPoundMapper.insertCustomsLog(customsLog);
		if(count < 1) {
			res.put("state", "N");
			res.put("message", "添加数据异常");
		}
        return res;
	}

	@Override
	public Map<String, Object> grGateIfPass(String gateNo, String state) {

		Map<String, Object> res = new HashMap<>();

		CustomsLog data = tPoundMapper.grGateIfPass(gateNo, state);

	    String release = "";
	    String reason = "";
	    if (data != null && StringUtils.isNotBlank(data.getReason()) && StringUtils.isNotBlank(data.getRelease())) {
	      release = data.getRelease();
	      reason = data.getReason();
	    } else {
			res.put("release", "N");
			res.put("reason", "");
	    }
	    if ("Y".equals(release)) {
			res.put("release", "Y");
			res.put("reason", reason);
	    } else if ("N".equals(release)) {
			res.put("release", "N");
			res.put("reason", reason);
	    } else if (StringUtils.isBlank(release)) {
			res.put("release", "N");
			res.put("reason", "放行结果未知");
	    }
	    return res;
	}

	public static String getInOutMes(TPoundPO tPoundPO, String seqNo, String gateNo, String state) {
	    String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \r\n <GATHER_INFO AREA_ID=\"000000000\" CHNL_NO=\"" + CustomsPoundEnum.getCustomsNo(gateNo) + "\"  I_E_TYPE=\"" + state + "\" SEQ_NO=\"" + seqNo + "\"> \r\n";
	    str = str + "<IC> \r\n";
	    str = str + "<DR_IC_NO></DR_IC_NO> \r\n";
	    str = str + "<IC_DR_CUSTOMS_NO></IC_DR_CUSTOMS_NO> \r\n";
	    str = str + "<IC_CO_CUSTOMS_NO>GKJ4444422</IC_CO_CUSTOMS_NO> \r\n";
	    str = str + "<IC_BILL_NO></IC_BILL_NO> \r\n";
	    str = str + "<IC_GROSS_WT></IC_GROSS_WT> \r\n";
	    str = str + "<IC_VE_CUSTOMS_NO></IC_VE_CUSTOMS_NO> \r\n";
	    str = str + "<IC_VE_NAME></IC_VE_NAME> \r\n";
	    str = str + "<IC_CONTA_ID></IC_CONTA_ID> \r\n";
	    str = str + "<IC_ESEAL_ID></IC_ESEAL_ID> \r\n";
	    str = str + "</IC> \r\n";
	    str = str + "<WEIGHT> \r\n";
        // WEIGHT  二次过磅的，放重量
        if ("i".equals(state) || "1".equals(tPoundPO.getIsFinished())) {
    	    str = str + "<GROSS_WT>" + (StringUtils.isNotBlank(tPoundPO.getWeighSelf())?tPoundPO.getWeighSelf():tPoundPO.getWeighAll()) + "</GROSS_WT> \r\n";
        } else {
    	    str = str + "<GROSS_WT>" + (StringUtils.isNotBlank(tPoundPO.getWeighSelf())?tPoundPO.getWeighSelf():tPoundPO.getWeighAll()) + "</GROSS_WT> \r\n";
        }
	    str = str + "</WEIGHT> \r\n";
	    str = str + "<CAR> \r\n";
	    str = str + "<VE_NAME>" + tPoundPO.getTruckPlate() + "</VE_NAME> \r\n";
	    str = str + "<CAR_EC_NO>" + tPoundPO.getTruckPlate() + "</CAR_EC_NO> \r\n";
	    str = str + "<CAR_EC_NO2>" + tPoundPO.getTruckPlate() + "</CAR_EC_NO2> \r\n";
	    str = str + "<VE_CUSTOMS_NO></VE_CUSTOMS_NO> \r\n";
	    str = str + "<VE_WT></VE_WT> \r\n";
	    str = str + "<CUSTOMS_BILL></CUSTOMS_BILL> \r\n";
	    str = str + "<INFORM_NO>" + tPoundPO.getPlanNo() + "</INFORM_NO> \r\n";
	    str = str + "</CAR> \r\n";
	    str = str + "<CONTA> \r\n";
	    str = str + "<CONTA_NUM></CONTA_NUM> \r\n";
	    str = str + "<CONTA_RECO>1</CONTA_RECO> \r\n";
	    str = str + "<CONTA_ID_F></CONTA_ID_F> \r\n";
	    str = str + "<CONTA_ID_B></CONTA_ID_B> \r\n";
	    str = str + "</CONTA> \r\n";
	    str = str + "<SEAL> \r\n";
	    str = str + "<ESEAL_ID ></ESEAL_ID > \r\n";
	    str = str + "<SEAL_KEY></SEAL_KEY> \r\n";
	    str = str + "</SEAL> \r\n";
	    str = str + "</GATHER_INFO> \r\n";
	    return str;
	  }

	public static String getInOutMes2(TPoundPO tPoundPO, String seqNo, String gateNo, String state) {
	    String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \r\n <GATHER_INFO AREA_ID=\"000000000\" CHNL_NO=\"" + CustomsPoundEnum.getCustomsNo(gateNo) + "\"  I_E_TYPE=\"" + state + "\" SEQ_NO=\"" + seqNo + "\"> \r\n";
	    str = str + "<IC> \r\n";
	    str = str + "<DR_IC_NO></DR_IC_NO> \r\n";
	    str = str + "<IC_DR_CUSTOMS_NO></IC_DR_CUSTOMS_NO> \r\n";
	    str = str + "<IC_CO_CUSTOMS_NO>GKJ4444422</IC_CO_CUSTOMS_NO> \r\n";
	    str = str + "<IC_BILL_NO></IC_BILL_NO> \r\n";
	    str = str + "<IC_GROSS_WT></IC_GROSS_WT> \r\n";
	    str = str + "<IC_VE_CUSTOMS_NO></IC_VE_CUSTOMS_NO> \r\n";
	    str = str + "<IC_VE_NAME></IC_VE_NAME> \r\n";
	    str = str + "<IC_CONTA_ID></IC_CONTA_ID> \r\n";
	    str = str + "<IC_ESEAL_ID></IC_ESEAL_ID> \r\n";
	    str = str + "</IC> \r\n";
	    str = str + "<WEIGHT> \r\n";
        // WEIGHT  二次过磅的，放重量
        if ("1".equals(tPoundPO.getIsFinished())) {
    	    str = str + "<GROSS_WT>" + ("集港存栈".equals(tPoundPO.getComName())?tPoundPO.getWeighAll():tPoundPO.getWeighSelf()) + "</GROSS_WT> \r\n";
        } else {
    	    str = str + "<GROSS_WT>" + ("集港存栈".equals(tPoundPO.getComName())?tPoundPO.getWeighSelf():tPoundPO.getWeighAll()) + "</GROSS_WT> \r\n";
        }
	    str = str + "</WEIGHT> \r\n";
	    str = str + "<CAR> \r\n";
	    str = str + "<VE_NAME>" + tPoundPO.getTruckPlate() + "</VE_NAME> \r\n";
	    str = str + "<CAR_EC_NO>" + tPoundPO.getTruckPlate() + "</CAR_EC_NO> \r\n";
	    str = str + "<CAR_EC_NO2>" + tPoundPO.getTruckPlate() + "</CAR_EC_NO2> \r\n";
	    str = str + "<VE_CUSTOMS_NO></VE_CUSTOMS_NO> \r\n";
	    str = str + "<VE_WT></VE_WT> \r\n";
	    str = str + "<CUSTOMS_BILL></CUSTOMS_BILL> \r\n";
	    str = str + "<INFORM_NO>" + tPoundPO.getPlanNo() + "</INFORM_NO> \r\n";
	    str = str + "</CAR> \r\n";
	    str = str + "<CONTA> \r\n";
	    str = str + "<CONTA_NUM></CONTA_NUM> \r\n";
	    str = str + "<CONTA_RECO>1</CONTA_RECO> \r\n";
	    str = str + "<CONTA_ID_F></CONTA_ID_F> \r\n";
	    str = str + "<CONTA_ID_B></CONTA_ID_B> \r\n";
	    str = str + "</CONTA> \r\n";
	    str = str + "<SEAL> \r\n";
	    str = str + "<ESEAL_ID ></ESEAL_ID > \r\n";
	    str = str + "<SEAL_KEY></SEAL_KEY> \r\n";
	    str = str + "</SEAL> \r\n";
	    str = str + "</GATHER_INFO> \r\n";
	    return str;
	}
}
