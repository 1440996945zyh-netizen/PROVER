package com.yy.ppm.statement.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.appWork.bean.dto.AppTallyCoilNumDTO;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.largescreen.bean.dto.SPortThroighputExportDTO;
import com.yy.ppm.largescreen.bean.dto.SPortThroighputSearchDTO;
import com.yy.ppm.statement.bean.dto.ConfirmForMiscAndStorageDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementExportDTO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import com.yy.ppm.statement.mapper.TCostStatementMapper;
import com.yy.ppm.statement.service.TCostStatementService;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class TCostStatementServiceImpl implements TCostStatementService {

    @Autowired
    private TCostStatementMapper tCostStatementMapper;
    @Autowired
    private Snowflake snowflake;
    @Autowired
    private CommonService commonService;
    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final int CURSOR_LIMIT = 5_000;

    @Override
    public Pages<TCostStatementPO> queryAll(TCostStatementDTO tCostStatementDTO, PageParameter parameter) {
        Pages< TCostStatementPO > pages =  PageHelperUtils.limit(parameter, () -> {
            return tCostStatementMapper.queryAll(tCostStatementDTO);
        });
        TCostStatementPO sumDto = tCostStatementMapper.queryAllSum(tCostStatementDTO);
        if (sumDto != null) {
            pages.setExtraData(JSON.toJSONString(sumDto));
        }
        return pages;
    }

    @Override
    public Pages<TCostStatementPO> queryAllDetail(TCostStatementDTO tCostStatementDTO, PageParameter parameter) {
        Pages< TCostStatementPO > pages =  PageHelperUtils.limit(parameter, () -> {
            return tCostStatementMapper.queryAllDetail(tCostStatementDTO);
        });
        TCostStatementPO sumDto = tCostStatementMapper.queryAllDetailSum(tCostStatementDTO);
        if (sumDto != null) {
            pages.setExtraData(JSON.toJSONString(sumDto));
        }
        return pages;
    }

    @Override
    public void review(TCostStatementDTO tCostStatementDTO) {
        // 审核
        if ("1".equals(tCostStatementDTO.getFlag())) {
            List<TCostStatementPO> statementPO = tCostStatementMapper.queryById1(tCostStatementDTO);
            for (TCostStatementPO dto:statementPO) {
                if ("31".equals(dto.getStatus())) {
                    throw new BusinessRuntimeException("已商务审核");
                }
            }
            tCostStatementDTO.setStatus("31");
            tCostStatementMapper.updateReview(tCostStatementDTO);
        }
        // 撤销审核
        if ("2".equals(tCostStatementDTO.getFlag())) {
            TCostStatementPO statementPO = tCostStatementMapper.queryById(tCostStatementDTO);
            if ("30".equals(statementPO.getStatus())) {
                throw new BusinessRuntimeException("未商务审核,不可销审");
            }
            tCostStatementDTO.setStatus("30");
            tCostStatementMapper.updateMarketReview(tCostStatementDTO);
        }
    }

    @Override
    public void printMark(TCostStatementDTO tCostStatementDTO) {
        tCostStatementMapper.printMark(tCostStatementDTO);
    }

    @Override
    public void financeReview(TCostStatementDTO tCostStatementDTO) {

        // 审核
        if ("1".equals(tCostStatementDTO.getFlag())) {
            List<TCostStatementPO> statementPO = tCostStatementMapper.queryById1(tCostStatementDTO);
            for (TCostStatementPO dto:statementPO) {
                if ("20".equals(dto.getFinanceStatus())) {
                    throw new BusinessRuntimeException("已财务审核");
                }
                if (!"31".equals(dto.getStatus())) {
                    throw new BusinessRuntimeException("结算单状态异常，请刷新列表重新操作！");
                }
            }
            tCostStatementMapper.financeReview(tCostStatementDTO);
        }
        // 撤销审核
        if ("2".equals(tCostStatementDTO.getFlag())) {
            TCostStatementPO statementPO = tCostStatementMapper.queryById(tCostStatementDTO);
            if ("10".equals(statementPO.getFinanceStatus())) {
                throw new BusinessRuntimeException("未财务审核,不可销审");
            }
            tCostStatementMapper.updateMarketfinanceReview(tCostStatementDTO);
        }
    }

    @Override
    public List<TBusRateDTO> queryRate() {
        List<TBusRateDTO> list = tCostStatementMapper.queryRate();
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void insert(TCostStatementDTO tCostStatementDTO) {
        if (CollectionUtils.isEmpty(tCostStatementDTO.getDetails())){
            throw new BusinessRuntimeException("明细列表不能为空");
        }
        tCostStatementDTO.setId(snowflake.nextId());
        tCostStatementDTO.setStatus("30");
        tCostStatementDTO.setStatementNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.STATEMENT_NO, null));
        tCostStatementDTO.setIsFinal("1");
        tCostStatementMapper.insert(tCostStatementDTO);
        for (TCostStatementDetailDTO dto: tCostStatementDTO.getDetails()) {
            dto.setId(snowflake.nextId());
            dto.setStatement(tCostStatementDTO.getId());
            tCostStatementMapper.insertItem(dto);
        }
        sysFileService.saveFileBusRelation(tCostStatementDTO.getFileIds(), tCostStatementDTO.getId());
    }

    @Override
    public TCostStatementDTO queryById(TCostStatementDTO tCostStatementDTO) {
        TCostStatementDTO dto = tCostStatementMapper.queryStatement(tCostStatementDTO);
        dto.setDetails(tCostStatementMapper.queryItemById(tCostStatementDTO));
        //拖轮计费 作业时间 最新的拖轮记录的结束时间
        /*if("60".equals(dto.getType())){
            TCostStatementDTO workDto = tCostStatementMapper.queryTugWorkTime(tCostStatementDTO);
            if (workDto != null) {
                dto.setLeaveBerthTime(workDto.getLeaveBerthTime());
            }
        }*/
        return dto;
    }

    @Override
    public TCostStatementDTO queryByIdzk(TCostStatementDTO tCostStatementDTO) {
        TCostStatementDTO dto = tCostStatementMapper.queryStatement(tCostStatementDTO);
        dto.setDetails(tCostStatementMapper.queryItemByIdzk(tCostStatementDTO));
        //拖轮计费 作业时间 最新的拖轮记录的结束时间
        if("60".equals(dto.getType())){
            TCostStatementDTO workDto = tCostStatementMapper.queryTugWorkTime(tCostStatementDTO);
            if (workDto != null) {
                dto.setLeaveBerthTime(workDto.getLeaveBerthTime());
            }
        }
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void update(TCostStatementDTO tCostStatementDTO) {
        if (CollectionUtils.isEmpty(tCostStatementDTO.getDetails())){
            throw new BusinessRuntimeException("明细列表不能为空");
        }
        tCostStatementMapper.updateById(tCostStatementDTO);

        tCostStatementMapper.deleteDetailById(tCostStatementDTO);
        for (TCostStatementDetailDTO dto: tCostStatementDTO.getDetails()) {
            dto.setId(snowflake.nextId());
            dto.setStatement(tCostStatementDTO.getId());
            tCostStatementMapper.insertItem(dto);
        }
        sysFileService.saveFileBusRelation(tCostStatementDTO.getFileIds(), tCostStatementDTO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void deleteById(TCostStatementDTO tCostStatementDTO) {
        TCostStatementPO statementPO = tCostStatementMapper.queryById(tCostStatementDTO);
        if ("31".equals(statementPO.getStatus())){
            throw new BusinessRuntimeException("该订单已回执确认,无法删除");
        }
        tCostStatementMapper.deleteById(tCostStatementDTO);
        tCostStatementMapper.deleteDetailById(tCostStatementDTO);
    }

    @Override
    public List<Map<String, Object>> queryRateItem() {
         return tCostStatementMapper.queryRateItem();
    }

    @Override
    public void saveConfirmFile(ConfirmForMiscAndStorageDTO dto) {
        if (CollectionUtils.isEmpty(dto.getFileIds())) {
            throw new BusinessRuntimeException("请先上传文件在进行保存");
        }
        dto.getIds().forEach(o -> {
            sysFileService.saveFileBusRelation(dto.getFileIds(), o);
        });
    }

    @Override
    public void recheck(TCostStatementDTO tCostStatementDTO) {
        // 审核
        if ("1".equals(tCostStatementDTO.getFlag())) {
            List<TCostStatementPO> statementPO = tCostStatementMapper.queryById1(tCostStatementDTO);
            for (TCostStatementPO dto:statementPO) {
                if (!"31".equals(dto.getStatus())) {
                    throw new BusinessRuntimeException("未商务审核");
                }
            }
            tCostStatementDTO.setStatus("32");
            tCostStatementMapper.updateRecheck(tCostStatementDTO);
        }
        // 撤销审核
        if ("2".equals(tCostStatementDTO.getFlag())) {
            TCostStatementPO statementPO = tCostStatementMapper.queryById(tCostStatementDTO);
            if (!"32".equals(statementPO.getStatus())) {
                throw new BusinessRuntimeException("未复核,不可销审");
            }
            tCostStatementDTO.setStatus("31");
            tCostStatementMapper.updateMarketRecheck(tCostStatementDTO);
        }
    }

    @Override
    public void applyInvoice(TCostStatementDTO tCostStatementDTO) {
        // 申请开票
        if ("1".equals(tCostStatementDTO.getFlag())) {
            List<TCostStatementPO> statementPO = tCostStatementMapper.queryById1(tCostStatementDTO);
            for (TCostStatementPO dto:statementPO) {
                if (!"32".equals(dto.getStatus())) {
                    throw new BusinessRuntimeException("未复核");
                }
            }
            tCostStatementDTO.setStatus("33");
            tCostStatementMapper.updateApplyInvoice(tCostStatementDTO);
        }
        // 撤销审核
        if ("2".equals(tCostStatementDTO.getFlag())) {
            TCostStatementPO statementPO = tCostStatementMapper.queryById(tCostStatementDTO);
            if (!"33".equals(statementPO.getStatus())) {
                throw new BusinessRuntimeException("未复核,不可销审");
            }
            tCostStatementDTO.setStatus("32");
            tCostStatementMapper.updateMarketApplyInvoice(tCostStatementDTO);
        }
    }

    @Override
    public List<Map<String, Object>> queryFiles(TCostStatementDTO tCostStatementDTO) {
        List<Map<String, Object>> list = new ArrayList<>();
        // 水电费
        if ("杂项计费".equals(tCostStatementDTO.getType())){
            list = tCostStatementMapper.queryWaterFiles(tCostStatementDTO);
        }
        return list;
    }

    @Override
    public void updateStatementItem(TCostStatementDetailDTO tCostStatementDetailDTO) {
        if (null == tCostStatementDetailDTO.getId()){
            throw new BusinessRuntimeException("请选择一条结算单明细进行修改");
        }
        tCostStatementMapper.updateStatementItem(tCostStatementDetailDTO);
    }

    @Override
    public void updateStatementItemD(TCostStatementDetailDTO tCostStatementDetailDTO) {
        if (null == tCostStatementDetailDTO.getId()){
            throw new BusinessRuntimeException("请选择一条结算单明细进行修改");
        }
        tCostStatementMapper.updateStatementItemD(tCostStatementDetailDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void redRush(TCostStatementDTO tCostStatementDTO) {
        //todo 根据实际业务取消结算单和计费数据的关联关系 允许再次计费
        // 船舶货方计费 -- 装卸费  交接清单
        /*if ("10".equals(tCostStatementDTO.getTypeCode())||"20".equals(tCostStatementDTO.getTypeCode())){
            tCostStatementMapper.updateHandoverlistByStatementId(tCostStatementDTO);
        }
        // 船方计费 -- 停泊费  杂项计费 -- 船舶水电费
        if ("30".equals(tCostStatementDTO.getTypeCode()) ||
                ("40".equals(tCostStatementDTO.getTypeCode())&&tCostStatementDTO.getRateItemCode().contains("03")||tCostStatementDTO.getRateItemCode().contains("05"))){
            tCostStatementMapper.updateCostShipByShipvoyageId(tCostStatementDTO);
        }
        // 杂项计费 -- 杂项
        if ("40".equals(tCostStatementDTO.getTypeCode())&&(!tCostStatementDTO.getRateItemCode().contains("03")||!tCostStatementDTO.getRateItemCode().contains("05"))){
            tCostStatementMapper.updateMiscBillingById(tCostStatementDTO);
        }
        // 拖轮计费
        if ("60".equals(tCostStatementDTO.getTypeCode())){
            tCostStatementMapper.updateTugByStatementId(tCostStatementDTO);
        }
        // 运输计费 -- 应收
        if ("110".equals(tCostStatementDTO.getTypeCode()) || "120".equals(tCostStatementDTO.getTypeCode())){
            tCostStatementMapper.updateWorkLoadByStatementId(tCostStatementDTO);
        }
        // 堆存计费
        if ("50".equals(tCostStatementDTO.getTypeCode())){
            tCostStatementMapper.updateStorageSettleByStatementId(tCostStatementDTO);
        }*/
        // 查询结算单
        TCostStatementPO statementPO = tCostStatementMapper.queryById(tCostStatementDTO);
        if ("1".equals(statementPO.getIsRedRush())){
            throw new BusinessRuntimeException("该结算单已红冲!");
        }
        List<TCostStatementDetailDTO> detailList = tCostStatementMapper.queryItemById(tCostStatementDTO);
        TCostStatementDTO dto = new TCostStatementDTO();
        BeanUtil.copyProperties(statementPO,dto);
        dto.setId(snowflake.nextId());
        dto.setSettlementDate(tCostStatementDTO.getSettlementDate());
        dto.setStatementNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.STATEMENT_NO, null));
        dto.setStatus("31");
        dto.setIsRedRush(1);
        dto.setRelationId(tCostStatementDTO.getId());
        /*if(true){
            throw new BusinessRuntimeException("该结算单已红冲!");
        }*/
        // 新增结算单 -- 红冲
        tCostStatementMapper.insert(dto);
        // 修改结算单子表
        tCostStatementMapper.updateStatementItemById(tCostStatementDTO);
        tCostStatementDTO.setRelationId(dto.getId());
        // 修改结算单 -- 关联ID
        tCostStatementMapper.updateStatementById(tCostStatementDTO);
        detailList.stream().forEach(v -> {
            v.setId(snowflake.nextId());
            v.setStatement(dto.getId());
            v.setNumber(v.getNumber().negate());
            v.setNumber2(v.getNumber2()==null?null:v.getNumber2().negate());
            v.setAmount(v.getAmount().negate());
            v.setInvoiceNumber(null);
            v.setInvoiceAmount(null);
            tCostStatementMapper.insertItem(v);
        });
    }

    @Override
    public byte[] exportExcel(TCostStatementDTO searchDTO) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TCostStatementExportDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TCostStatementExportDTO> cursor = tCostStatementMapper.getExportList(searchDTO)) {
                    Iterator<TCostStatementExportDTO> iterator = cursor.iterator();
                    if (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            List<TCostStatementExportDTO> salarys = new ArrayList<>();
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
}
