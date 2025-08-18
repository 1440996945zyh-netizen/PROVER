package com.yy.ppm.business.mapper;


import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusCustomerPropertyDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName (TBusCustomerProperty)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 13:10:00
 */
@Repository
public interface TBusCustomerPropertyMapper {

/**
  * 获取列表
  * @param id
  * @return
  */
 public List<TBusCustomerPropertyDTO> getList(Long id);

 /**
  * 根据id获取
  * @param id 主键
  * @return
  */
 public TBusCustomerPropertyDTO getById(Long id);

 /**
  * 新增
  * @param tBusCustomerPropertyDTO
  * @return
  */
 @Edit
 public int insert(TBusCustomerPropertyDTO tBusCustomerPropertyDTO);

 /**
  * 修改
  * @param tBusCustomerPropertyDTO
  * @return
  */
 @Edit
 public int update(TBusCustomerPropertyDTO tBusCustomerPropertyDTO);

 /**
  * 根据id删除
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);
}

