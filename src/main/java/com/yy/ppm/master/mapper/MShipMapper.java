package com.yy.ppm.master.mapper;


import java.util.List;

import com.yy.ppm.master.bean.po.MShipLogPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.dto.MShipSearchDTO;

/**
 * @ClassName 海轮资料(MShip)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月27日 15:44:00
 */
@Repository
public interface MShipMapper {

/**
  * 获取海轮资料列表
  * @param mShipSearchVo
  * @return
  */
 public Page<MShipDTO> getList(MShipSearchDTO mShipSearchVo);
 

	/**
	  * 获取海轮资料列表（不分页）
	  * @param mShipSearchVo
	  * @return
	  */
	public List<MShipDTO> getAllList(MShipSearchDTO mShipSearchVo);

/**
  * 导出海轮资料列表
  * @param mShipSearchDTO
  * @return
  */
 public List<MShipDTO> exportList(MShipSearchDTO mShipSearchDTO);

 /**
  * 根据id获取海轮资料
  * @param id 主键
  * @return
  */
 public MShipDTO getById(Long id);

 public List<MShipLogPO> getByShipId(Long shipId);

 public List<MShipDTO> getByIds(@Param("ids") List<Long> ids);

 public List<MShipDTO> getByShipKindCode(@Param("shipKindCode") String shipKindCode);

 public MShipDTO getByBHTId(Integer boHaiTongId);

 /**
  * 新增海轮资料
  * @param mShipDTO
  * @return
  */
 @Edit
 public int insert(MShipDTO mShipDTO);

 /**
  * 修改海轮资料
  * @param mShipDTO
  * @return
  */
 @Edit
 public int update(MShipDTO mShipDTO);

 @Edit
 public int updatePhoneById(MShipDTO mShipDTO);
 public int updatePhoneByBHTId(MShipDTO mShipDTO);


 /**
  * 根据id删除海轮资料
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);
 public int deleteByBHTId(Long boHaiTongId);

 /**
  * 驳回
  * @param mShipDTO 主键
  * @return
  */
 @Edit
 public int reject(MShipDTO mShipDTO);

 int approveCancelById(MShipDTO tmpDto);


	public void updateBoHaiTongIdById(@Param("id") Long id, @Param("boHaiTongId") Integer boHaiTongId);

    int getShipVoyageByShipId(@Param("id") Long id);

    List<MShipDTO> getCheckList(MShipSearchDTO mShipSearchDTO);


    @Edit
    public int insertShipLog(MShipLogPO shipLogPO);

    MShipDTO getShipBlackByIMO(@Param("imo") String imo);
}

