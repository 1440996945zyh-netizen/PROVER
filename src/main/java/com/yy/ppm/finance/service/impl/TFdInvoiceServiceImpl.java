package com.yy.ppm.finance.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SpringUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.finance.bean.dto.*;
import com.yy.ppm.finance.controller.TFdInvoiceDetailController;
import com.yy.ppm.finance.mapper.TFdDebtorpaymentMapper;
import com.yy.ppm.finance.mapper.TFdInvoiceDetailMapper;
import com.yy.ppm.finance.mapper.TFdInvoiceMapper;
import com.yy.ppm.finance.service.TFdDebtorpaymentService;
import com.yy.ppm.finance.service.TFdInvoiceService;
import com.yy.ppm.statement.bean.dto.costShip.TDisShipvoyageItemDTO;
import com.yy.ppm.statement.bean.po.TCostStatementDetailPO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票表(TFdInvoice)ServiceImpl
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
@Service
public class TFdInvoiceServiceImpl implements TFdInvoiceService {

    @Resource
    private TFdInvoiceMapper tFdInvoiceMapper;
    @Resource
    private TFdInvoiceDetailMapper detailMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SysFileService sysFileService;
    @Autowired
    private CommonService commonService;
    @Resource
    private TFdDebtorpaymentMapper debtorpaymentMapper;

    @Autowired
    private TFdDebtorpaymentService tFdDebtorpaymentService;

    @Resource
    private SysParameterMapper sysParameterMapper;


    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TFdInvoiceDetailController.class);

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TFdInvoiceDTO> getList(TFdInvoiceSearchDTO searchDTO) {

        Pages<TFdInvoiceDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tFdInvoiceMapper.getList(searchDTO);
        });
        pages.getPages().stream().forEach(item->{
            if ("_".equals(item.getShipNameVoyage())){
                item.setShipNameVoyage("");
            }
        });
        return pages;
    }

    @Override
    public String invoiceDownload(TFdInvoiceSearchDTO searchDTO) {
        return tFdInvoiceMapper.invoiceDownload(searchDTO);
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public TFdInvoiceDTO getDetail(Long id) {
        TFdInvoiceDTO tFdInvoiceDTO = tFdInvoiceMapper.getById(id);
        tFdInvoiceDTO.setStatementList(detailMapper.getListByInvoiceId(id));
        return tFdInvoiceDTO;
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TFdInvoiceDTO dto) {

            dto.setId(snowflake.nextId());
            // 设置初始状态
            dto.setStatus(1L);
            dto.setInvoiceAmount(BigDecimal.ZERO);

            /*
            船舶VI
            货物CI   10
            杂项MI
            堆存费SI
            { label: '货物发票', value: '10' },
            { label: '船舶发票', value: '30' },
            { label: '杂项发票', value: '40' },
            { label: '堆存费', value: '50' },
            */
        String tmpYear = String.valueOf(LocalDate.now().getYear()).substring(2);
        if(dto.getInvoiceTypeCode()==10L){
                //CI
                dto.setSysInvoiceCode(autoInvoiceCode("CI"+tmpYear));
                //设置预缴类型
                // * 对账类型  PREPAYMENT_TYPE_CODE
                dto.setPrepaymentTypeCode(10L);
                dto.setPrepaymentTypeName("货方");
            }else if(dto.getInvoiceTypeCode()==30L){
                //VI
                dto.setSysInvoiceCode(autoInvoiceCode("VI"+tmpYear));
                dto.setPrepaymentTypeCode(30L);
                dto.setPrepaymentTypeName("船方");
            }else if(dto.getInvoiceTypeCode()==40L){
                if(!CollectionUtils.isEmpty(dto.getStatementList())){
                    List<String> rateItemCodeList = dto.getStatementList().stream().map(TFdInvoiceDetailDTO::getRateItemCode).collect(Collectors.toList());
                    if(CollectionUtils.isEmpty(rateItemCodeList)){
                        throw new BusinessRuntimeException("选择的结算单缺少费率信息");

                    }
                    List<String> rateItemCodeResult = tFdInvoiceMapper.getFeeItemList(rateItemCodeList);
                    if(CollectionUtils.isEmpty(rateItemCodeResult)){
                        throw new BusinessRuntimeException("未找到对应的费目信息");
                    }
                    if(rateItemCodeResult.size()>1){
                        throw new BusinessRuntimeException("请选择相同的费目类型开具杂项发票");
                    }
                    //1船2货3杂项
                    if("1".equals(rateItemCodeResult.get(0))){
                        dto.setPrepaymentTypeCode(30L);
                        dto.setPrepaymentTypeName("船方");
                    }else if("2".equals(rateItemCodeResult.get(0))){
                        dto.setPrepaymentTypeCode(10L);
                        dto.setPrepaymentTypeName("货方");
                    }else if("3".equals(rateItemCodeResult.get(0))){
                        //MI
                        dto.setPrepaymentTypeCode(40L);
                        dto.setPrepaymentTypeName("杂项");
                    }else {
                        throw new BusinessRuntimeException("没有找到对应的对账类型");
                    }

                    dto.setSysInvoiceCode(autoInvoiceCode("MI"+tmpYear));


                }


            }else if(dto.getInvoiceTypeCode()==50L){
                //SI
                dto.setPrepaymentTypeCode(10L);
                dto.setPrepaymentTypeName("货方");
                boolean flag = false;
                Map<Long,List<TFdInvoiceDetailDTO>> tmpMap = dto.getStatementList().stream().collect(Collectors.groupingBy(TFdInvoiceDetailDTO::getFeeType));
                if(tmpMap.size()>1){
                    throw new BusinessRuntimeException("所选费用类型不同");
                }
                dto.setSysInvoiceCode(autoInvoiceCode("SI"+tmpYear));
            }


            if (!CollectionUtils.isEmpty(dto.getStatementList())){

                //校验发票子表的数据，客户必须是相同的
                Map<String, List<TFdInvoiceDetailDTO>> tmpCheckMap = dto.getStatementList().stream().collect(Collectors.groupingBy(TFdInvoiceDetailDTO::getCustomerId));
                if(tmpCheckMap.size()>1){
                    throw new BusinessRuntimeException("选择的开票数据中客户信息不一致");
                }

                dto.getStatementList().forEach(o->{
                    o.setStatementDetailId(o.getId());
                });
                List<TFdInvoiceDetailDTO> statement = tFdInvoiceMapper.getStatementByInvoiceDetailList(dto.getStatementList());
                if(CollectionUtils.isEmpty(statement)){
                    throw new BusinessRuntimeException("结算单数据异常");
                }
                //判断结算单状态
                statement.forEach(o->{
                    if("50".equals(o.getStatus())){
                        throw new BusinessRuntimeException("结算单："+o.getStatementNo()+" 已开票");
                    }
                });
                Map<Long, TFdInvoiceDetailDTO> tmpMap = statement.stream()
                        .collect(Collectors.toMap(TFdInvoiceDetailDTO::getId, Function.identity()));

                List<TCostStatementDetailPO> updateStatementDetail = new ArrayList<TCostStatementDetailPO>();
                dto.getStatementList().forEach(item->{
                    item.setStatementDetailId(item.getId());
                    dto.setInvoiceAmount(dto.getInvoiceAmount().add(item.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
                    //数据库中结算单子表的信息
                    TFdInvoiceDetailDTO tFdInvoiceDetailDTO = tmpMap.get(item.getId());
                    if(tFdInvoiceDetailDTO!=null){
                        TCostStatementDetailPO tCostStatementDetailPO = new TCostStatementDetailPO();
                        //设置结算单的开票金额
                        tCostStatementDetailPO.setId(item.getId()); //此时的id是 statementDetail 的ID
                        tCostStatementDetailPO.setStatement(tFdInvoiceDetailDTO.getStatementId()); //此时的id是 statementDetail 的ID
                        tCostStatementDetailPO.setInvoiceAmount(tFdInvoiceDetailDTO.getInvoiceAmount().add(item.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
                        tCostStatementDetailPO.setNumber(tFdInvoiceDetailDTO.getNumberCount());
                        tCostStatementDetailPO.setInvoiceNumber(tFdInvoiceDetailDTO.getInvoiceNumber().add(item.getNumberCount()).setScale(4,BigDecimal.ROUND_HALF_UP));
                        updateStatementDetail.add(tCostStatementDetailPO);
                    }
                    //更新状态
                    item.setId(snowflake.nextId());
                    item.setInvoiceId(dto.getId());
                });
                //插入子表数据
                detailMapper.insertBatch(dto.getStatementList());
                //更新结账单子表
                if(!CollectionUtils.isEmpty(updateStatementDetail)){
                    tFdInvoiceMapper.updateStatementDetailBatch(updateStatementDetail);
                }
                updateStatement(updateStatementDetail);
            }

            //停泊费退还押金
            if(dto.getInvoiceTypeCode()==30L && dto.getStatementList().stream().anyMatch(o->o.getType()==30L)){
                List<Long> shipvoyageItemId = dto.getStatementList().stream().map(TFdInvoiceDetailDTO::getShipvoyageItemId).distinct().collect(Collectors.toList());
                for (Long aLong : shipvoyageItemId) {
                    //需要退还停泊费
                        SpringUtils.getBean(this.getClass()).handleShipPayAmount(aLong);
                }
            }
            sysFileService.saveFileBusRelation(dto.getFileIds(),dto.getId());
            boolean result = tFdInvoiceMapper.insert(dto) == 1;
        if(dto.getPrepaymentTypeCode().equals(30L)){
            //拿到发票的数据
            TFdDebtorpaymentSearchDTO dto2 = new TFdDebtorpaymentSearchDTO();
            dto2.setId(dto.getId());
            List<TFdDebtorpaymentDetailDTO> invoiceList = debtorpaymentMapper.getInvoiceList(dto2);
            invoiceList.forEach(x->{
                x.setType(1L);
            });
            BigDecimal invoiceTotalAmount = invoiceList.stream()
                    .map(TFdDebtorpaymentDetailDTO::getInvoicePrepayAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            //拿到预缴的数据
            TFdDebtorpaymentSearchDTO dto3 = new TFdDebtorpaymentSearchDTO();
            dto3.setCompanyId(dto.getCompanyId());
            dto3.setCustomerId(dto.getCustomerId());
            dto3.setPrepaymentTypeCode(30L);
            List<TFdDebtorpaymentDetailDTO> tmpPrePayList = debtorpaymentMapper.getPrePayList(dto3);
            tmpPrePayList.forEach(x->{
                x.setType(2L);
            });
            BigDecimal prePayTotalAmount = tmpPrePayList.stream()
                    .map(TFdDebtorpaymentDetailDTO::getInvoicePrepayAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            //发票大于预缴
            if(invoiceTotalAmount.compareTo(prePayTotalAmount) > 0){
                TFdDebtorpaymentDTO tFdDebtorpaymentDTO = new TFdDebtorpaymentDTO();
                if(invoiceList==null|| invoiceList.size()<1){
                    invoiceList =  new ArrayList<>() ;
                }
                invoiceList.addAll(tmpPrePayList);

                tFdDebtorpaymentDTO.setReceiptList(invoiceList);
                tFdDebtorpaymentDTO.setDebTypeCode("40");
                tFdDebtorpaymentDTO.setDebTypeName("平账");
                tFdDebtorpaymentDTO.setPrepaymentTypeCode(30L);
                tFdDebtorpaymentDTO.setPrepaymentTypeName("船方");
                tFdDebtorpaymentDTO.setDebtorpaymentTime(new Date());
                tFdDebtorpaymentDTO.setCompanyId(dto.getCompanyId());
                tFdDebtorpaymentDTO.setCompanyName(dto.getCompanyName());
                tFdDebtorpaymentDTO.setCustomerId(dto.getCustomerId());
                tFdDebtorpaymentDTO.setCustomerName(dto.getCustomerName());
                TFdDebtorpaymentDetailDTO formDataDo = new TFdDebtorpaymentDetailDTO();
                formDataDo.setInvoicePrepayAmount(invoiceTotalAmount.subtract(prePayTotalAmount));
                formDataDo.setDebtorpayPaymentTypeCode(40L);
                formDataDo.setDebtorpayPaymentTypeName("平账");
                tFdDebtorpaymentDTO.setFormDataDo(formDataDo);

                tFdDebtorpaymentService.doSave(tFdDebtorpaymentDTO);
            }
            //发票等于预缴
            else if(invoiceTotalAmount.compareTo(prePayTotalAmount) == 0){
                TFdDebtorpaymentDTO tFdDebtorpaymentDTO = new TFdDebtorpaymentDTO();
                if(invoiceList==null|| invoiceList.size()<1){
                    invoiceList =  new ArrayList<>() ;
                }
                invoiceList.addAll(tmpPrePayList);

                tFdDebtorpaymentDTO.setReceiptList(invoiceList);
                tFdDebtorpaymentDTO.setDebtorpaymentTime(new Date());
                tFdDebtorpaymentDTO.setPrepaymentTypeCode(30L);
                tFdDebtorpaymentDTO.setPrepaymentTypeName("船方");
                tFdDebtorpaymentDTO.setCompanyId(dto.getCompanyId());
                tFdDebtorpaymentDTO.setCompanyName(dto.getCompanyName());
                tFdDebtorpaymentDTO.setCustomerId(dto.getCustomerId());
                tFdDebtorpaymentDTO.setCustomerName(dto.getCustomerName());
                TFdDebtorpaymentDetailDTO formDataDo = new TFdDebtorpaymentDetailDTO();
                formDataDo.setInvoicePrepayAmount(invoiceTotalAmount.subtract(prePayTotalAmount));
                tFdDebtorpaymentDTO.setFormDataDo(formDataDo);

                tFdDebtorpaymentService.doSave(tFdDebtorpaymentDTO);

            }
            //发票小于预缴
            else if(invoiceTotalAmount.compareTo(prePayTotalAmount) < 0) {
                TFdDebtorpaymentDTO tFdDebtorpaymentDTO = new TFdDebtorpaymentDTO();
                if(invoiceList==null|| invoiceList.size()<1){
                    invoiceList =  new ArrayList<>() ;
                }
                invoiceList.addAll(tmpPrePayList);

                tFdDebtorpaymentDTO.setReceiptList(invoiceList);
                tFdDebtorpaymentDTO.setDebTypeCode("10");
                tFdDebtorpaymentDTO.setDebTypeName("预缴");
                tFdDebtorpaymentDTO.setPrepaymentTypeCode(30L);
                tFdDebtorpaymentDTO.setPrepaymentTypeName("船方");
                tFdDebtorpaymentDTO.setCompanyId(dto.getCompanyId());
                tFdDebtorpaymentDTO.setCompanyName(dto.getCompanyName());
                tFdDebtorpaymentDTO.setCustomerId(dto.getCustomerId());
                tFdDebtorpaymentDTO.setCustomerName(dto.getCustomerName());
                tFdDebtorpaymentDTO.setDebtorpaymentTime(new Date());
                TFdDebtorpaymentDetailDTO formDataDo = new TFdDebtorpaymentDetailDTO();
                formDataDo.setInvoicePrepayAmount(prePayTotalAmount.subtract(invoiceTotalAmount));
                tFdDebtorpaymentDTO.setFormDataDo(formDataDo);
                formDataDo.setDebtorpayPaymentTypeCode(10L);
                formDataDo.setDebtorpayPaymentTypeName("预缴");

                tFdDebtorpaymentService.doSave(tFdDebtorpaymentDTO);
            }
        }

        //获取当前发票的开具的计算单数据
        List<Map<Object,Object>> tmpCostStatmen = tFdInvoiceMapper.getCostStatemmentByInvoiceDto(dto.getId());
        LOGGER.info("发票开具之后发票关联的结算单数据",tmpCostStatmen.toString());

        SysParameterDTO sysParameter = sysParameterMapper.getByKey("INVOICE_SEND_TYPE");
        String paramVal = sysParameter.getParamVal();
//        if("YD".equals(paramVal)){
//            // 远得接口
//            if(!"山东省港口集团潍坊港有限公司".equals(dto.getCompanyName())){
//                ydWebService.invoiceSendYdRequest(dto.getId());
//            }
//        }else if("SD".equals(paramVal) && (dto.getTaxationInvoiceCode().equals(3l) || dto.getTaxationInvoiceCode().equals(4l))){
//            //数电接口,只开电子票
//            if(!"山东省港口集团潍坊港有限公司".equals(dto.getCompanyName())){
//                sdInvoiceService.lqSdInvoiceSendRequest(dto.getId(),"INVOICE",false);
//            }
//        }
//        //金蝶接口-发票传输
//        if(!"山东省港口集团潍坊港有限公司".equals(dto.getCompanyName())){
//            easService.preSend("FP",dto.getSysInvoiceCode(),"1");
//        }
        return result;
    }

    /**
     * 修改发票
     * @param dto
     * @return
     */
    @Override
    public boolean updateData(TFdInvoiceDTO dto) {
        return false;

        //没测试别用！
        //没测试别用！
        //没测试别用！
//        dto.setInvoiceAmount(BigDecimal.ZERO);
//
//        List<TFdInvoiceDetailDTO> statementList = dto.getStatementList();
//        //根据前端传递的id
//        List<TFdInvoiceDetailDTO> statement = tFdInvoiceMapper.getStatementByInvoiceDetailList(statementList);
//
//        if(CollectionUtils.isEmpty(statement)){
//            throw new BusinessRuntimeException("结算单数据异常");
//        }
//        Map<Long, TFdInvoiceDetailDTO> tmpMap = statement.stream().collect(Collectors.toMap(TFdInvoiceDetailDTO::getId, Function.identity()));
//        //结算单详情表需要更新的数据
//        if (!CollectionUtils.isEmpty(dto.getStatementList())){
//            List<TCostStatementDetailPO> updateStatementDetail = new ArrayList<TCostStatementDetailPO>();
//            Long statementDetailId =0L;
//            dto.getStatementList().forEach(item->{
//                item.setStatementDetailId(item.getId());
//                //获取现在数据库的数据方便更新的时候进行回复金额
//                List<TFdInvoiceDetailDTO>  tmpInvoiceDetails = detailMapper.getListByInvoiceIds(dto.getStatementList());
//                Map<Long, TFdInvoiceDetailDTO> dbInvoiceDetailMap = tmpInvoiceDetails.stream().collect(Collectors.toMap(TFdInvoiceDetailDTO::getId, Function.identity()));
//                dto.setInvoiceAmount(dto.getInvoiceAmount().add(item.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
//                TFdInvoiceDetailDTO tFdInvoiceDetailDTO = tmpMap.get(item.getStatementDetailId());
//                TFdInvoiceDetailDTO tmpInvoiceDetailDTO = dbInvoiceDetailMap.get(item.getId());
//                if(tmpInvoiceDetailDTO==null){
//                    throw new BusinessRuntimeException("发票子表数据错误");
//                }
//                if(tFdInvoiceDetailDTO!=null){
//                    TCostStatementDetailPO tCostStatementDetailPO = new TCostStatementDetailPO();
//                    //设置结算单的开票金额
//                    tCostStatementDetailPO.setStatement(tFdInvoiceDetailDTO.getStatementId());
//                    tCostStatementDetailPO.setId(tmpInvoiceDetailDTO.getStatementDetailId()); // 设置该对象在结算子表中的主键id;
//                    //更新金额
//                    tCostStatementDetailPO.setInvoiceAmount(tFdInvoiceDetailDTO.getAmount().subtract(tmpInvoiceDetailDTO.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
//                    tCostStatementDetailPO.setInvoiceAmount(tFdInvoiceDetailDTO.getAmount().add(item.getAmount()).setScale(1,BigDecimal.ROUND_HALF_UP));
//                    //更新数量
//                    tCostStatementDetailPO.setInvoiceNumber(tFdInvoiceDetailDTO.getNumberCount().subtract(tmpInvoiceDetailDTO.getNumberCount()).setScale(4,BigDecimal.ROUND_HALF_UP));
//                    tCostStatementDetailPO.setInvoiceNumber(tFdInvoiceDetailDTO.getNumberCount().add(item.getNumberCount()).setScale(4,BigDecimal.ROUND_HALF_UP));
//
//                    updateStatementDetail.add(tCostStatementDetailPO);
//                }
//                //设置id
//                item.setId(snowflake.nextId());
//                item.setInvoiceId(dto.getId());
//            });
//
//            updateStatement(updateStatementDetail);
//            //插入子表数据
//            detailMapper.insertBatch(dto.getStatementList());
//            //更新结账单子表
//            if(!CollectionUtils.isEmpty(updateStatementDetail)){
//                tFdInvoiceMapper.updateStatementDetailBatch(updateStatementDetail);
//            }
//        }
//        //先删除原来的子表数据
//        detailMapper.deleteByInvoiceId(dto.getId());
//        //插入新的子表数据
//        detailMapper.insertBatch(dto.getStatementList());
//        //文件保存
//        sysFileService.saveFileBusRelation(dto.getFileIds(),dto.getId());
//
//        return tFdInvoiceMapper.update(dto) == 1;
    }

    /**
     * 发票作废
     *
     * @param id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean voidInvoice(Long id) {

        TFdInvoiceDTO invoiceDTO = tFdInvoiceMapper.getById(id);
        if(invoiceDTO==null){
            throw new BusinessRuntimeException("发票不存在");
        }
        if (invoiceDTO.getDebtorpaymentId()!=null){
            throw new BusinessRuntimeException("发票已经开具收据 不可作废！");
        }


        //更新发票子表
        TFdInvoiceDTO tFdInvoiceDTO = new TFdInvoiceDTO();
        tFdInvoiceDTO.setId(id);
        tFdInvoiceDTO.setStatus(2L);//状态设置为作废


        LOGGER.enter("发票作废操作:voidInvoice");
        List<TFdInvoiceDetailDTO> listByInvoiceId = detailMapper.getListByInvoiceId(id);

        Map<Long, TFdInvoiceDetailDTO> invoiceDetailMap = listByInvoiceId.stream().collect(Collectors.toMap(TFdInvoiceDetailDTO::getStatementDetailId, Function.identity()));

        //没有发票子表
        if(CollectionUtils.isEmpty(listByInvoiceId)){
            LOGGER.exit("发票作废操作没有没有发票子表");
            return tFdInvoiceMapper.voidInvoice(tFdInvoiceDTO) == 1;
        }
        //根据获取的发票的子表获取对应的结算详情
        List<TFdInvoiceDetailDTO> statement = tFdInvoiceMapper.getStatementByInvoiceDetailList(listByInvoiceId);
        //准备更新的TCostStatementDetailList
        List<TCostStatementDetailPO> statementDetailUpdateList = new ArrayList<>(statement.size());
        //设置恢复的子对象
        for (TFdInvoiceDetailDTO statementDTO : statement) {

            if (invoiceDetailMap.get(statementDTO.getId())!=null){
                TCostStatementDetailPO tmpStatementDetail = new TCostStatementDetailPO();
                TFdInvoiceDetailDTO tFdInvoiceDetailDTO = invoiceDetailMap.get(statementDTO.getId());

                //设置要更新的结算单子表ID
                tmpStatementDetail.setId(statementDTO.getId());
                //设置发票的父ID更新结算单主表的状态用
                tmpStatementDetail.setStatement(statementDTO.getStatementId());
                //减去已经开票的数量
                tmpStatementDetail.setInvoiceNumber(statementDTO.getInvoiceNumber().subtract(tFdInvoiceDetailDTO.getNumberCount().setScale(4,BigDecimal.ROUND_HALF_UP)));
                //减去已开票金额
                tmpStatementDetail.setInvoiceAmount(statementDTO.getInvoiceAmount().subtract(tFdInvoiceDetailDTO.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
                //保存要更新的结算单
                statementDetailUpdateList.add(tmpStatementDetail);

            }
        }//todo 判断statementDetailUpdateList是否为空
        //更新结算单子表
        if(!CollectionUtils.isEmpty(statementDetailUpdateList)){
            tFdInvoiceMapper.updateStatementDetailBatch(statementDetailUpdateList);
            updateStatement(statementDetailUpdateList);
        }
        //停泊费回退押金
        if(invoiceDTO.getInvoiceTypeCode()==30L && listByInvoiceId.stream().anyMatch(o->30L==o.getType())){
            List<Long> statementIds = statement.stream().map(TFdInvoiceDetailDTO::getStatementId).collect(Collectors.toList());
            if(!statementIds.isEmpty()){
                List<TFdInvoiceDetailDTO> statementList = tFdInvoiceMapper.getStatementListByIds(statementIds);
                    if(!statementList.isEmpty()){
                        List<Long> collect = statementList.stream().map(TFdInvoiceDetailDTO::getShipvoyageItemId).distinct().collect(Collectors.toList());
                        for (Long aLong : collect) {
                            SpringUtils.getBean(this.getClass()).cancleWithShipInvoice(aLong);
                        }
                    }
            }

        }

        return tFdInvoiceMapper.voidInvoice(tFdInvoiceDTO) == 1;
     }

    /***
     * 那条件获取结算单详情列表
     * @param searchDTO
     * @return
     */
    @Override
    public Pages<TFdInvoiceDetailDTO> getStatementList(TFdInvoiceSearchDTO searchDTO) {

        Pages<TFdInvoiceDetailDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tFdInvoiceMapper.getStatement(searchDTO);
        });

        List<TFdInvoiceDetailDTO> pages1 = pages.getPages();
        pages1.forEach(o->{
            //todo 做判空抛异常
            if(o.getTaxAmount()==null){
                throw new BusinessRuntimeException("结算单子表数据异常，税额为空");
            }
            if(o.getTax()==null){
                throw new BusinessRuntimeException("结算单子表数据异常，税率为空");
            }
            if (o.getInvoiceAmount()==null){
                o.setInvoiceAmount(BigDecimal.ZERO);
            }
            if (o.getNumberCount()==null){
                o.setNumberCount(BigDecimal.ZERO);
            }
            if (o.getAmount()==null){
                o.setAmount(BigDecimal.ZERO);
            }
            if (o.getInvoiceNumber()==null){
                o.setInvoiceNumber(BigDecimal.ZERO);
            }
            o.setNumberCount(o.getNumberCount().subtract(o.getInvoiceNumber()).setScale(4,BigDecimal.ROUND_HALF_UP));

            //
            o.setAmount(o.getAmount().subtract(o.getInvoiceAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
            //计算税额
            //金额*税率/（1+税率）
            BigDecimal tmpTax = o.getTax().divide(new BigDecimal(100));
            o.setTaxAmount(o.getAmount().multiply(tmpTax).divide(BigDecimal.ONE.add(tmpTax),MathContext.DECIMAL128).setScale(2,BigDecimal.ROUND_HALF_UP));
        });
        return pages;
    }

    //更新结算单
    public  void updateStatement(List<TCostStatementDetailPO> dtos){
        if(CollectionUtils.isEmpty(dtos)){
            return;
        }
        Map<Long, List<TCostStatementDetailPO>> dtoMap = dtos.stream().collect(Collectors.groupingBy(TCostStatementDetailPO::getStatement));
        List<Long> ids = new ArrayList<>(dtoMap.size());
        dtoMap.forEach((k,v)->{
            ids.add(k);
        });
        //通过父id获取所有的结算单
        List<TFdInvoiceDetailDTO> statementByParentIds = tFdInvoiceMapper.getStatementByParentIds(ids);
        Map<Long, List<TFdInvoiceDetailDTO>> collect = statementByParentIds.stream().collect(Collectors.groupingBy(TFdInvoiceDetailDTO::getStatementId));
        List<StatementStatusUpdateDTO> doInvoiceIds = new ArrayList<>(collect.size());  //暂存完全开票的
        List<StatementStatusUpdateDTO> notInvoiceIds = new ArrayList<>(collect.size()); //暂存部分开票的
        List<StatementStatusUpdateDTO> backInvoiceIds = new ArrayList<>(collect.size());//暂存结算状态的
        collect.forEach((k,v)->{
            StatementStatusUpdateDTO statementStatusUpdateDTO = new StatementStatusUpdateDTO();
            statementStatusUpdateDTO.setId(k);
            boolean flag = Boolean.TRUE;
            int i = 0; // 统计完全开票的数量
            int j = 0; // 统计未完全开票的数量
            for (TFdInvoiceDetailDTO o : v) {
                if(o.getInvoiceNumber().compareTo(BigDecimal.ZERO) == 1){
                    //不是结算状态
                    i++;
                    if (o.getInvoiceNumber().compareTo(o.getNumberCount()) == 0){
                        //已开票完全开票
                        j++;
                    }
                }
            }
            // i>0 说明此结账单下的有结算的那进行了开票操作
            // j=v.size时 说明此结账单下的所有子结算单的都已经开票了
            // i+j = 0 说明此结账单下的所有的子结算都没有开票操作
            if(j==v.size()&&i!=0){
                //已经开票
                statementStatusUpdateDTO.setStatus(50L);
                doInvoiceIds.add(statementStatusUpdateDTO);
            }
            if(i!=0&&j<v.size()){
                //部分开票
                statementStatusUpdateDTO.setStatus(40L);
                notInvoiceIds.add(statementStatusUpdateDTO);
            }
            if( i==0) {
                //货物发票、堆存费发票 状态为31（商务确认）的结算单允许开票，其他类型发票不变
                if(v.get(0).getType()==10L ||v.get(0).getType()==20L || v.get(0).getType()==50L||v.get(0).getType()==40L){
                    //结算审核
                    statementStatusUpdateDTO.setStatus(31L);
                }else {
                    statementStatusUpdateDTO.setStatus(30L);
                }
                backInvoiceIds.add(statementStatusUpdateDTO);
            }
            i=0;j=0;
        });
        if (!CollectionUtils.isEmpty(doInvoiceIds)){
            tFdInvoiceMapper.updateStatementStatusBatch(doInvoiceIds);//更新状态为已开票
        }
        if (!CollectionUtils.isEmpty(notInvoiceIds)){
            tFdInvoiceMapper.updateStatementStatusBatch(notInvoiceIds);//更新状态为未开票
        }
        if (!CollectionUtils.isEmpty(backInvoiceIds)){
            tFdInvoiceMapper.updateStatementStatusBatch(backInvoiceIds);//更新状态为部分开票
        }
    }

    /**
     * 自动生成编号
     * @param startWith
     * @return
     */
    public String autoInvoiceCode(String startWith){
        String invoiceCodeSearch=  tFdInvoiceMapper.getInvoiceCode(startWith);
        if(StringUtils.isEmpty(invoiceCodeSearch)){
            return startWith+String.format("%05d", 1);
        }
        return startWith+String.format("%05d", Integer.parseInt(invoiceCodeSearch.substring(4))+1);
    }

    /**
     * 更新税务服务发票编号
     * @param searchDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateInvoiceCode(TFdInvoiceDTO searchDTO) {
        return tFdInvoiceMapper.updateInvoiceCode(searchDTO)==1;
    }

    @Override
    public TFdInvoiceDTO getInvoice(Long id) {
        TFdInvoiceDTO tFdInvoiceDTO = tFdInvoiceMapper.getInvoiceByCustomerId(id);
        return tFdInvoiceDTO;
    }

    /**
     * 计算查询条件下的发票金额
     * @param searchDTO
     * @return
     */
    @Override
    public TFdInvoiceDTO getCountAmount(TFdInvoiceSearchDTO searchDTO) {
        Page<TFdInvoiceDTO> list = tFdInvoiceMapper.getList(searchDTO);
        TFdInvoiceDTO invoiceDTO = new TFdInvoiceDTO();
        invoiceDTO.setCountAmount(BigDecimal.ZERO);
        list.forEach(item->{
            invoiceDTO.setCountAmount(invoiceDTO.getCountAmount().add(item.getInvoiceAmount()));
        });
        invoiceDTO.setTotal(list.size());
        return invoiceDTO;
    }



    @Transactional(rollbackFor = Exception.class)
    public void handleShipPayAmount(Long shipvoyageItemId){
        if (shipvoyageItemId==null){
            return;
        }

        TDisShipvoyageItemDTO disShipvoyageItem = tFdInvoiceMapper.getShipVoyageItemByItemId(shipvoyageItemId);
        if(disShipvoyageItem !=null && (disShipvoyageItem.getPaymentAmount()!=null
                && disShipvoyageItem.getPaymentAmount().compareTo(BigDecimal.ZERO) !=0)){
            //更新子表
            disShipvoyageItem.setPaymentAmountBack(Optional.ofNullable(disShipvoyageItem.getPaymentAmount()).orElse(BigDecimal.ZERO));
            disShipvoyageItem.setPaymentAmount(BigDecimal.ZERO);
            tFdInvoiceMapper.updateShipVoyageItem(disShipvoyageItem);
            //更新主表
            List<TDisShipvoyageItemDTO> tmpDetailList = tFdInvoiceMapper.getAllItemByOneItemId(disShipvoyageItem.getId());
            TDisShipvoyageDTO shipvoyageDTO = new TDisShipvoyageDTO();
            shipvoyageDTO.setId(Long.parseLong(tmpDetailList.get(0).getShipvoyageId()));
            shipvoyageDTO.setPaymentAmount(BigDecimal.ZERO);
            tmpDetailList.forEach(o->{
                shipvoyageDTO.setPaymentAmount(shipvoyageDTO.getPaymentAmount().add(o.getPaymentAmount()));
            });
            tFdInvoiceMapper.updateShipVoyage(shipvoyageDTO);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancleWithShipInvoice(Long shipvoyageItemId){
        TDisShipvoyageItemDTO disShipvoyageItem = tFdInvoiceMapper.getShipVoyageItemByItemId(shipvoyageItemId);
        if(disShipvoyageItem !=null){
            //更新子表
            if(disShipvoyageItem.getPaymentAmount()==null){
                disShipvoyageItem.setPaymentAmount(BigDecimal.ZERO);
            }
            disShipvoyageItem.setPaymentAmount(Optional.ofNullable(disShipvoyageItem.getPaymentAmount()).orElse(BigDecimal.ZERO).add(Optional.ofNullable(disShipvoyageItem.getPaymentAmountBack()).orElse(BigDecimal.ZERO)));
            disShipvoyageItem.setPaymentAmountBack(BigDecimal.ZERO);
            tFdInvoiceMapper.updateShipVoyageItem(disShipvoyageItem);
            //更新主表
            List<TDisShipvoyageItemDTO> tmpDetailList = tFdInvoiceMapper.getAllItemByOneItemId(disShipvoyageItem.getId());
            TDisShipvoyageDTO shipvoyageDTO = new TDisShipvoyageDTO();
            shipvoyageDTO.setId(Long.parseLong(tmpDetailList.get(0).getShipvoyageId()));
            shipvoyageDTO.setPaymentAmount(BigDecimal.ZERO);
            tmpDetailList.forEach(o->{
                shipvoyageDTO.setPaymentAmount(shipvoyageDTO.getPaymentAmount().add(o.getPaymentAmount()));
            });
            tFdInvoiceMapper.updateShipVoyage(shipvoyageDTO);
        }
    }

}
