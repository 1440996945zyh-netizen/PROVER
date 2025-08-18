package com.yy.ppm.finance.service.impl;

import com.github.pagehelper.Page;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.finance.bean.dto.TFdInvoiceSearchDTO;
import com.yy.ppm.finance.controller.TFdInvoiceDetailController;
import com.yy.ppm.finance.mapper.TFdInvoiceMapper;
import com.yy.ppm.finance.service.TFdInvoiceDetailService;
import com.yy.ppm.finance.mapper.TFdInvoiceDetailMapper;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDetailSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票子表(TFdInvoiceDetail)ServiceImpl
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
@Service
public class TFdInvoiceDetailServiceImpl implements TFdInvoiceDetailService {

    @Resource
    private TFdInvoiceDetailMapper tFdInvoiceDetailMapper;
    @Resource
    private TFdInvoiceMapper invoiceMapper;

    @Resource
    private Snowflake snowflake;

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
    public Pages<TFdInvoiceDetailDTO> getList(TFdInvoiceDetailSearchDTO searchDTO) {

        Pages<TFdInvoiceDetailDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tFdInvoiceDetailMapper.getList(searchDTO);
        });

        return pages;
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public TFdInvoiceDetailDTO getDetail(Long id) {
        return tFdInvoiceDetailMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TFdInvoiceDetailDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tFdInvoiceDetailMapper.insert(dto) == 1;

            // 修改
        } else {
            return tFdInvoiceDetailMapper.update(dto) == 1;
        }

    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return tFdInvoiceDetailMapper.deleteById(id) == 1;

    }

    /***
     * 计算金额
     * @param tFdInvoiceDetailDTO
     * @return
     */
    @Override
    public TFdInvoiceDetailDTO calculateAmount(TFdInvoiceDetailDTO tFdInvoiceDetailDTO) {

        final String methodName = "calculateAmount";
        LOGGER.enter(methodName + "计算金额：[start]", "tFdInvoiceDetailDTO:" + tFdInvoiceDetailDTO);


        //获取该条信息在数据库中保存的
        BigDecimal pieceAmount = tFdInvoiceDetailDTO.getPieceAmount();
        if(pieceAmount==null){
            throw new BusinessRuntimeException("单价为空！");
        }
        BigDecimal tax = tFdInvoiceDetailDTO.getTax();
        tax = tax.divide(new BigDecimal("100"));
        if(tax==null||tax.equals(BigDecimal.ZERO)){
            throw new BusinessRuntimeException("税率为空！");
        }
        BigDecimal numberCount = tFdInvoiceDetailDTO.getNumberCount();
        if(numberCount == null){
            throw new BusinessRuntimeException("请输入数量！");
        }

        //对比结算单的数量
        //新添加的结算单
        TFdInvoiceSearchDTO tFdInvoiceSearchDTO = new TFdInvoiceSearchDTO();
        tFdInvoiceSearchDTO.setId(tFdInvoiceDetailDTO.getId());
        TFdInvoiceDetailDTO dbInvoiceDetailDto = invoiceMapper.getStatementByID(tFdInvoiceSearchDTO.getId());
        if(dbInvoiceDetailDto==null){
            throw new BusinessRuntimeException("结算单数据异常");
        }
        //结算单子表的数量》结算单子表的已开票数量+当前的开票数量
        if (dbInvoiceDetailDto.getNumberCount().compareTo(dbInvoiceDetailDto.getInvoiceNumber().add(tFdInvoiceDetailDTO.getNumberCount()).setScale(4,BigDecimal.ROUND_HALF_UP))==-1){
            throw new BusinessRuntimeException("输入的数量不能大于"+dbInvoiceDetailDto.getNumberCount().subtract(dbInvoiceDetailDto.getInvoiceNumber()).setScale(2,BigDecimal.ROUND_HALF_UP)+"!");
        }
        //数量2 的操作
        BigDecimal numberCount2 = tFdInvoiceDetailDTO.getNumberCount2();
        //计算金额
        if (numberCount2 == null || numberCount2.compareTo(BigDecimal.ZERO)==0){
            numberCount2 = BigDecimal.ONE;
        }
        BigDecimal tmpAmount = pieceAmount.multiply(numberCount.multiply(numberCount2)).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        tFdInvoiceDetailDTO.setAmount(tmpAmount);

        //计算税额
        BigDecimal taxAmount = tmpAmount.multiply(tax.divide(BigDecimal.ONE.add(tax),MathContext.DECIMAL128)).setScale(2, BigDecimal.ROUND_HALF_UP);
        tFdInvoiceDetailDTO.setTaxAmount(taxAmount);
        LOGGER.exit(methodName + "计算金额：[END]", "tFdInvoiceDetailDTO:" + tFdInvoiceDetailDTO);

        return tFdInvoiceDetailDTO;
    }
}

