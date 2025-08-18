package com.yy.ppm.business.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 货权转移记录表(TBusCargoTransfer)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 19:37:00
 */
@Repository
public interface TBusCargoTransferMapper {

 /**
  *
  * @param searchDTO
  * @return
  */
  Page<TBusCargoTransferDTO> getPage(TBusCargoTransferSearchDTO searchDTO);

  /**
    * 获取货权转移记录表列表
    * @param cargoInfoId
    * @return
    */
   public List<TBusCargoTransferDTO> getList(Long cargoInfoId);

    /**
     * 查询港存
     * @param cargoInfoId
     * @return
     */
   List<TBusCargoStorageDTO> getStorageList(Long cargoInfoId);

 /**
  * 查询票货所有的港存动态
  * @param cargoInfoId
  * @return
  */
 List<TBusCargoStorageDTO> getAllStorageList(@Param("cargoInfoId") Long cargoInfoId,
                                             @Param("cargoTransferId") Long cargoTransferId,
                                             @Param("massIdList") List<Long> massIdList);

   /**
    * 根据id获取货权转移记录表
    * @param id 主键
    * @return
    */
   public TBusCargoTransferDTO getById(Long id);

   /**
    * 新增货权转移记录表
    * @param tBusCargoTransferDTO
    * @return
    */
   @Edit
   public int insert(TBusCargoTransferDTO tBusCargoTransferDTO);

   /**
    * 修改货权转移记录表
    * @param tBusCargoTransferDTO
    * @return
    */
   @Edit
   public int update(TBusCargoTransferDTO tBusCargoTransferDTO);
   @Edit
   public int yardApprove(TBusCargoTransferDTO tBusCargoTransferDTO);


 /**
  * 修改货权转移记录表
  * @param tBusCargoTransferDTO
  * @return
  */
 @Edit
 public int updateNotNull(TBusCargoTransferDTO tBusCargoTransferDTO);

 /**
  * 审批
  * @param tBusCargoTransferDTO
  * @return
  */
 @Edit
 public int approve(TBusCargoTransferDTO tBusCargoTransferDTO);


   /**
    * 根据id删除货权转移记录表
    * @param id 主键
    * @return
    */
   public int deleteById(Long id);

   List<TBusCargoFreeStorageDTO> getCargoFreeStorageDays(@Param("companyId") Long companyId, @Param("customerId") Long customerId,
                                                   @Param("cargoCode") String cargoCode, @Param("time") String time);
   List<TBusCargoWeightDTO> getWeightByTrustAndCargoInfo(@Param("trustId") Long trustId, @Param("cargoInfoId") Long cargoInfoId);
   List<TBusCargoWeightDTO> getWeightByCargoInfo(@Param("cargoInfoId") Long cargoInfoId);

    List<TBusTrustDTO> getTrustByCargoInfoId(TBusCargoTransferDTO cargoTransferDTO);
}

