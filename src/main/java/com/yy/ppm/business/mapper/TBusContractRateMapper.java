package com.yy.ppm.business.mapper;


import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusContractRateDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 合同费率表(TBusContractRate)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 10:49:00
 */
@Repository
public interface TBusContractRateMapper {

/**
  * 获取合同费率表列表
  * @param contractId
  * @return
  */
 public List<TBusContractRateDTO> getList(Long contractId);

 /**
  * 根据id获取合同费率表
  * @param id 主键
  * @return
  */
 public TBusContractRateDTO getById(Long id);

 /**
  * 新增合同费率表
  * @param tBusContractRateDTO
  * @return
  */
 @Edit
 public int insert(TBusContractRateDTO tBusContractRateDTO);

 /**
  * 修改合同费率表
  * @param tBusContractRateDTO
  * @return
  */
 @Edit
 public int update(TBusContractRateDTO tBusContractRateDTO);


 /**
  * 根据id删除合同费率表
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);

 int getCargoNameByCode(String cargoCode);
}

