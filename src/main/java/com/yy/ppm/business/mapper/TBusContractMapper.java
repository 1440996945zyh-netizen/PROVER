package com.yy.ppm.business.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusContractDTO;
import com.yy.ppm.business.bean.dto.TBusContractSearchDTO;
import com.yy.ppm.business.bean.dto.contract.TBusRateDTO;
import com.yy.ppm.business.bean.dto.contract.TBusTrateDTO;
import com.yy.ppm.business.bean.po.TBusCargoMixDetailPO;
import com.yy.ppm.business.bean.po.TBusContractCustomerPO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.master.bean.po.MCargoPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @ClassName 合同(TBusContract)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 10:48:00
 */
@Repository
public interface TBusContractMapper {

/**
  * 获取合同列表
  * @param tBusContractSearchVo
  * @return
  */
 public Page<TBusContractDTO> getList(TBusContractSearchDTO tBusContractSearchVo);

/**
  * 导出合同列表
  * @param tBusContractSearchDTO
  * @return
  */
 public List<TBusContractDTO> exportList(TBusContractSearchDTO tBusContractSearchDTO);

 /**
  * 根据id获取合同
  * @param id 主键
  * @return
  */
 public TBusContractDTO getById(Long id);

 /**
  * 新增合同
  * @param tBusContractDTO
  * @return
  */
 @Edit
 public int insert(TBusContractDTO tBusContractDTO);

 /**
  * 修改合同
  * @param tBusContractDTO
  * @return
  */
 @Edit
 public int update(TBusContractDTO tBusContractDTO);


 /**
  * 根据id删除合同
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);

 /**
  * 修改状态 , 生效
  * @param tBusContractDTO
  * @return
  */
 @Edit
 boolean updateStatus(TBusContractDTO tBusContractDTO);

 List<TBusCargoMixDetailPO> listCargoMixDetail(Long contractId);

 boolean isUsedByStatement(Long contractId);

 boolean isUsedByStorageSettle(Long contractId);

 void cancel(Long id);

 /**
  * 根据ParentId查询补充协议
  * @param parentId
  * @return
  */
 List<TBusContractDTO> getListByParentId(@Param("parentId") Long parentId);

 int insertContractCustomer(TBusContractCustomerPO customer);

 List<MCargoPO> listCargo(@Param("cargoCodes") List<String> cargoCodes);

 List<TBusRateDTO> listCargoRate(@Param("startTime") Date startTime, @Param("cargoCodes") List<String> cargoCodes);

 List<TBusRatePO> listDuicunRate(@Param("startTime") Date startTime);

 List<TBusTrateDTO> listTrateByTrateItemIds(@Param("trateItemIds") List<Long> trateItemIds);

 List<TBusContractCustomerPO> listContractCustomer(Long contractId);

 List<TBusTrateDTO> listTrate(
         @Param("contractNo") String contractNo, @Param("customerId") Long customerId,
         @Param("startTime") Date startTime, @Param("endTime") Date endTime,
         @Param("cargoCode") String cargoCode
 );

 List<TBusContractRatePO> listContractRate(Long contractId);
}

