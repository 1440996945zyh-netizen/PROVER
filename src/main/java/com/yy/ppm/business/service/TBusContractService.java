package com.yy.ppm.business.service;


import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusContractDTO;
import com.yy.ppm.business.bean.dto.TBusContractSearchDTO;
import com.yy.ppm.business.bean.dto.contract.TBusTrateDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 合同(TBusContract)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 10:48:00
 */
public interface TBusContractService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<TBusContractDTO> getList(TBusContractSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public TBusContractDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tBusContractDTO
     * @return 是否成功
     */
    public boolean doSave(TBusContractDTO tBusContractDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

     /**
      * 修改状态, 生效
      * @param tBusContractDTO
      * @return
      */
     boolean updateStatus(TBusContractDTO tBusContractDTO);

    void cancel(Long id);

     List<TBusContractDTO> getListByParentId(Long parentId);

    List<Map<String, Object>> listCargoRate(Date startTime, List<String> cargoCodes);

    List<TBusTrateDTO> matchTrate(String contractNo, Long customerId, Date startTime, Date endTime, String cargoCode);
}

