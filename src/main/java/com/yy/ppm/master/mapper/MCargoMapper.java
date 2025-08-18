package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MCargoCategoryDTO;
import com.yy.ppm.master.bean.dto.MCargoCategorySearchDTO;
import com.yy.ppm.master.bean.dto.MCargoDTO;
import com.yy.ppm.master.bean.dto.MCargoSearchDTO;
import com.yy.ppm.master.bean.po.MCargoCategoryPO;
import com.yy.ppm.master.bean.po.MCargoPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * (MCargoType)Dao
 *
 * @author makejava
 * @date 2021-03-08 11:18:02
 */
public interface MCargoMapper {

    /**
	 * 获取列表
	 * @param mCargoTypeSearchDTO SearchDTO
	 * @return
	 */
	Page<MCargoCategoryDTO> getListCargoCategory(MCargoCategorySearchDTO mCargoTypeSearchDTO);

	/**
	 * 根据gid获取
	 * @param id 主键
	 * @return
	 */
	MCargoCategoryDTO getCargoCategoryById(Long id);

	/**
	 * 新增
	 * @param mCargoTypePO
	 * @return
	 */
	@Edit
	int insertCargoCategory(MCargoCategoryPO mCargoTypePO);

	/**
	 * 修改
	 * @param mCargoTypeDTO DTO
	 * @return
	 */
	@Edit
	int updateCargoCategory(MCargoCategoryDTO mCargoTypeDTO);


	/**
	 * 删除
	 * @param id
	 * @return
	 */
	int deleteCargoCategory(Long id);

	//↑ 货种操作
	//↓货物操作



	/**
	 * 获取列表
	 * @param mCargoSearchDTO SearchDTO
	 * @return
	 */
	Page<MCargoDTO> getListCargo(MCargoSearchDTO mCargoSearchDTO);
	List<MCargoDTO> getList(MCargoSearchDTO mCargoSearchDTO);
	Page<MCargoDTO> getListCargoNew(MCargoSearchDTO mCargoSearchDTO);
	Page<MCargoDTO> getOutwardGoods(MCargoSearchDTO mCargoSearchDTO);

	/**
	 * 根据gid获取
	 * @param id 主键
	 * @return
	 */
	MCargoDTO getCargoById(Long id);
	MCargoDTO getDetailById(Long id);

	/**
	 * cargoCode
	 * cargoName
	 * @param
	 * @return
	 */
	MCargoDTO getCargoByCondition(String cargoCode,String cargoName);




	/**
	 * 新增
	 * @param mCargoPO
	 * @return
	 */
	@Edit
	int insertCargo(MCargoPO mCargoPO);

	/**
	 * 修改
	 * @param mCargoPO
	 * @return
	 */
	@Edit
	int updateCargo(MCargoPO mCargoPO);


	/**
	 * 删除
	 * @param id
	 * @return
	 */
	int deleteCargo(Long id);
	
	/**
	 * 根据渤海通ID查询货类名称
	 * @param bhtId
	 * @return
	 */
	MCargoDTO getCargoCategoryNameByBHTId(@Param("bhtId") String bhtId);

	MCargoDTO getCargoByCargoCode(String cargoCode);

	List<MCargoDTO> getCargoIsRepeate(MCargoPO mCargoPO);

	int updateStatus(@Param("id") Long id, @Param("status") String status);

    String nextCargoCode(String cargoCategoryCode);

    int getCargoInfoByCode(String cargoCode);
}
