package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MCustomerDTO;
import com.yy.ppm.master.bean.dto.MCustomerInvoiceDTO;
import com.yy.ppm.master.bean.dto.MCustomerSearchDTO;
import com.yy.ppm.master.bean.dto.MCustomerTypeDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 客户资料(MCustomer)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 16:27:00
 */
@Repository
public interface MCustomerMapper {

/**
  * 获取客户资料列表
  * @param mCustomerSearchVo
  * @return
  */
 public Page<MCustomerDTO> getList(MCustomerSearchDTO mCustomerSearchVo);

/**
  * 导出客户资料列表
  * @param mCustomerSearchDTO
  * @return
  */
 public List<MCustomerDTO> exportList(MCustomerSearchDTO mCustomerSearchDTO);

 /**
  * 根据id获取客户资料
  * @param id 主键
  * @return
  */
 public MCustomerDTO getById(Long id);

 /**
  * 查询单条客户的发票列表记录
  *
  * @param customerCode
  * @return 实体
  */
 public List<MCustomerInvoiceDTO> getInvoiceList(@Param("customerCode") String customerCode);

 /**
  * 查询单条客户的类型列表
  *
  * @param customerCode
  * @return 实体
  */
 public List<MCustomerTypeDTO> getTypeList(@Param("customerCode") String customerCode);

 /**
  * 新增客户资料
  * @param mCustomerDTO
  * @return
  */
 @Edit
 public int insert(MCustomerDTO mCustomerDTO);

 /**
  * 修改客户资料
  * @param mCustomerDTO
  * @return
  */
 @Edit
 public int update(MCustomerDTO mCustomerDTO);


 /**
  * 根据id删除客户资料
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);


 /**
  * 根据id获取客户资料
  * @param list
  * @return
  */
 public int insertInvoceBatch(List<MCustomerInvoiceDTO> list);


 /**
  * 根据id获取客户资料
  * @param list
  * @return
  */
 public int insertTypeBatch(List<MCustomerTypeDTO> list);
}

