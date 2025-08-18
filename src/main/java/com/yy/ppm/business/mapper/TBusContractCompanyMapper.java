package com.yy.ppm.business.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusContractCompanyDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName (TBusContractCompany)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 11:57:00
 */
@Repository
public interface TBusContractCompanyMapper {

/**
  * 获取列表
  * @param contractId
  * @return
  */
 public List<TBusContractCompanyDTO> getList(Long contractId);

 /**
  * 根据id获取
  * @param id 主键
  * @return
  */
 public TBusContractCompanyDTO getById(Long id);

 /**
  * 新增
  * @param tBusContractCompanyDTO
  * @return
  */
 public int insert(TBusContractCompanyDTO tBusContractCompanyDTO);

 /**
  * 修改
  * @param tBusContractCompanyDTO
  * @return
  */
 public int update(TBusContractCompanyDTO tBusContractCompanyDTO);

 /**
  * 根据id删除
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);
}

