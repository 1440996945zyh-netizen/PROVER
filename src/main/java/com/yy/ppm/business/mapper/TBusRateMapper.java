package com.yy.ppm.business.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.BusServiceSearchDTO;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.business.bean.dto.TBusRateSearchDTO;
import com.yy.ppm.business.bean.dto.TBusServiceDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @ClassName 费率(TBusRate)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 16:48:00
 */
@Repository
public interface TBusRateMapper {

/**
  * 获取费率列表
  * @param tBusRateSearchVo
  * @return
  */
 public Page<TBusRateDTO> getList(TBusRateSearchDTO tBusRateSearchVo);

/**
  * 导出费率列表
  * @param tBusRateSearchDTO
  * @return
  */
 public List<TBusRateDTO> exportList(TBusRateSearchDTO tBusRateSearchDTO);

 /**
  * 根据id获取费率
  * @param id 主键
  * @return
  */
 public TBusRateDTO getById(Long id);

 /**
  * 新增费率
  * @param tBusRateDTO
  * @return
  */
 @Edit
 public int insert(TBusRateDTO tBusRateDTO);

 /**
  * 修改费率
  * @param tBusRateDTO
  * @return
  */
 @Edit
 public int update(TBusRateDTO tBusRateDTO);

 /**
  * 新增费率(货物包干费)
  * @param tBusRateDTO
  * @return
  */
 @Edit
 public int insertCargo(TBusRateDTO tBusRateDTO);

 /**
  * 修改费率(货物包干费)
  * @param list
  * @return
  */
 @Edit
 public int updateCargo(List<TBusRateDTO> list);

 @Edit
 public int updateStatusPassCancle(TBusRateDTO tBusRateDTO);


 /**
  * 根据id删除费率
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);


 /**
  * 跟据时间段查询
  * @param tBusRateDTO
  * @return
  */
 public int selectByTime(TBusRateDTO tBusRateDTO);

 List<TBusServiceDTO> getListService(BusServiceSearchDTO busServiceSearchDTO);

 List<TBusRateDTO> getDetailCargo(TBusRateSearchDTO tBusRateSearchDTO);

 public Page<TBusRateDTO> getListCargo(TBusRateSearchDTO tBusRateSearchVo);

 @Edit
 void busRatePassCargo(TBusRateDTO tBusRateDTO);
 void busRateRevokeCargo(TBusRateDTO tBusRateDTO);

 void delRateCargo(TBusRateDTO tBusRateDTO);

 int getCargoNameByCode(String cargoCode);
 /**
  * 根据费目，作业过程，内外贸，有效时间查询标准费率
  */
 List<TBusRateDTO> getBusRateList(TBusRateSearchDTO searchDTO);

 void delRateList(List<Long> ids);

 @Edit
 void insertCargoList(List<TBusRateDTO> list);
}

