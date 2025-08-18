package com.yy.ppm.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusCustomerContactDTO;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.bean.dto.TBusCustomerPropertyDTO;
import com.yy.ppm.business.bean.dto.TBusCustomerSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 合同表(TBusCustomer)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 13:09:00
 */
@Repository
public interface TBusCustomerMapper {

/**
  * 获取合同表列表
  * @param tBusCustomerSearchVo
  * @return
  */
 public Page<TBusCustomerDTO> getList(TBusCustomerSearchDTO tBusCustomerSearchVo);

/**
  * 导出合同表列表
  * @param tBusCustomerSearchDTO
  * @return
  */
 public List<TBusCustomerDTO> exportList(TBusCustomerSearchDTO tBusCustomerSearchDTO);

 /**
  * 根据id获取合同表
  * @param id 主键
  * @return
  */
 public TBusCustomerDTO getById(Long id);

 @DS("bhtnewdb")
 public TBusCustomerDTO getBHTById(Long id,String customerCode,String customerName,String customerShortName);

 @DS("bhtnewdb")
 public TBusCustomerDTO getCustomerNameById(Long id);

 public TBusCustomerDTO getByBHTId(String bhtId);

 /**
  * 新增合同表
  * @param tBusCustomerDTO
  * @return
  */
 @Edit
 public int insert(TBusCustomerDTO tBusCustomerDTO);

 /**
  * 修改合同表
  * @param tBusCustomerDTO
  * @return
  */
 @Edit
 public int update(TBusCustomerDTO tBusCustomerDTO);

 /**
  * 驳回
  * @param tBusCustomerDTO
  * @return
  */
 @Edit
 public int reject(TBusCustomerDTO tBusCustomerDTO);


 /**
  * 根据id删除合同表
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);


 @Edit
 int approveCancelById(TBusCustomerDTO tmpDto);

	public Long getCustomerIdByBHTId(@Param("bhtId") String bhtId);

	public String getCustomerNameByBHTId(@Param("bhtId") String bhtId);

	@Edit
	int doCredit(TBusCustomerDTO dto);


 Integer getMaxCode();

 int getCargoInfoById(Long id);

  @Edit
  void insertBatch(@Param("list") List<TBusCustomerContactDTO> contactList);

 void deleteByCustomerId(@Param("customerId") Long id);

 List<TBusCustomerContactDTO> getContactByCustomerId(@Param("customerId") Long id);
}

