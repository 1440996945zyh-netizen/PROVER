package com.yy.ppm.business.service;



import cn.hutool.core.bean.BeanUtil;
import com.yy.common.page.Pages;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.common.enums.AutoNumEnum;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 票货信息表(TBusCargoInfo)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 18:47:00
 */
public interface TBusCargoTransferService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TBusCargoTransferDTO> getList(TBusCargoTransferSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     TBusCargoTransferDTO getDetail(Long id);

    /**
     * 保存货转
     *
     * @param dto
     * @return 是否成功
     */
    boolean doSave(TBusCargoTransferDTO dto);

    /**
     * 库场审批
     * @param dto
     * @return
     */
    boolean yardApprove(TBusCargoStorageTransferDTO dto);

    /**
     * 库场撤销审批
     * @param dto
     * @return
     */
    boolean yardCancelApprove(TBusCargoTransferDTO dto);

    /**
     * 删除货转
     *
     * @param  id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 商务审核
     *
     * @param dto
     * @return 是否成功
     */
    boolean doApprove(TBusCargoTransferDTO dto);
    /**
     * 撤销商务审核
     *
     * @param dto
     * @return 是否成功
     */
    boolean cancelApprove(TBusCargoTransferDTO dto);

    /**
     * 查询场存列表
     * @return
     */
    List<TBusCargoStorageDTO> getStorageList(Long cargoInfoId);

}

