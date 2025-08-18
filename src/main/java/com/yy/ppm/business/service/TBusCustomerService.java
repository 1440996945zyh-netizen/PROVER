package com.yy.ppm.business.service;

import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.bean.dto.TBusCustomerSearchDTO;
import com.yy.ppm.master.bean.dto.MShipDTO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 合同表(TBusCustomer)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 13:09:00
 */
public interface TBusCustomerService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<TBusCustomerDTO> getList(TBusCustomerSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public TBusCustomerDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tBusCustomerDTO
     * @return 是否成功
     */
    public boolean doSave(TBusCustomerDTO tBusCustomerDTO);

    /**
     * 驳回
     *
     * @param tBusCustomerDTO
     * @return 是否成功
     */
    public boolean doReject(TBusCustomerDTO tBusCustomerDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

    boolean approveById(Long id);
    boolean cancelById(Long id);

    boolean doCredit(TBusCustomerDTO tBusCustomerDTO);




    /**
     * 同步
     *
     * @param list
     * @return 是否成功
     */
    public boolean sync(List<TBusCustomerDTO> list);
}

