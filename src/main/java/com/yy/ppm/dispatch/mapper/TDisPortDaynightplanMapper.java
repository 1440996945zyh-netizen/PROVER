package com.yy.ppm.dispatch.mapper;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.yy.ppm.dispatch.bean.dto.TDisPortDaynightplanSearch2DTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.TDisPortDaynightplanDTO;

/**
 * @ClassName 集疏港昼夜计划(TDisPortDaynightplan)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年11月14日 10:31:00
 */
@Repository
public interface TDisPortDaynightplanMapper {


   /**
    * 获取集疏港昼夜计划列表
    * @param query
    * @return
    */
    public List<TDisPortDaynightplanDTO> getList(TDisPortDaynightplanSearch2DTO query);
    public List<TDisPortDaynightplanDTO> getByBusinessNo(@Param("planDate") String planDate,@Param("businessNo") String businessNo);
   /**
    * 根据id获取集疏港昼夜计划详情
    * @param id
    * @return
    */
   public TDisPortDaynightplanDTO getById(@Param("id") Long id);

   /**
    * 新增集疏港昼夜计划
    * @param tDisPortDaynightplanDTO
    * @return
    */
    @Edit
    public int insert(TDisPortDaynightplanDTO tDisPortDaynightplanDTO);

    /**
     * 修改集疏港昼夜计划
     * @param tDisPortDaynightplanDTO
     * @return
     */
    @Edit
    public int update(TDisPortDaynightplanDTO tDisPortDaynightplanDTO);

   /**
    * 根据id删除集疏港昼夜计划
    * @param id
    * @return
    */
   public int deleteById(Long id);

   /**
    * 查询当日未作计划的票货数据
    * @param tDisPortDaynightplanDTO
    * @return
   */
   public List<TDisPortDaynightplanDTO> getTrustCargoDetail(TDisPortDaynightplanDTO tDisPortDaynightplanDTO);
   /**
    * 更新审核状态
    * @param tDisPortDaynightplanDTO
    * @return
   */
   @Edit
   public int approveById(TDisPortDaynightplanDTO tDisPortDaynightplanDTO);

    /**
     * 更新审核状态
     * @param tDisPortDaynightplanDTO
     * @return
     */
    @Edit
    public int approveRevokeById(TDisPortDaynightplanDTO tDisPortDaynightplanDTO);
    
    /**
     * 修改车辆集疏港预约表中的开始、结束时间
     * @param data
     */
	public void updateVehicleReservation(TDisPortDaynightplanDTO data);
	

	/**
	* 获取集疏港昼夜计划列表
	* @param planDate
	* @return
	*/
	public List<TDisPortDaynightplanDTO> getListToBoHaiTong(String currentTimeString);
	
	/**
	 * 获取系统参数
	 * @return
	 */
	public String getSysParameter();

    BigDecimal getCount(String businessNo, Integer forecastWeight);

    BigDecimal getCargoTon(String businessNo);

    Integer getForecastWeight();


    int getEntrustById(Long id);

    BigDecimal getCountByTime(String businessNo, Integer forecastWeight, Date startDate, Date endDate);

    @Edit
    void updateApproveList(TDisPortDaynightplanDTO dto);

    @Edit
    void cancelApproveListRevoke(TDisPortDaynightplanDTO dto);

    List<String> getUserRoleById(Long loginUserId);

    List<TDisPortDaynightplanDTO> getBulkList(String planDate);

    BigDecimal getExpireTon(String businessNo, Date beginTime);

    BigDecimal getExpireAllTon(String businessNo);


    List<TDisPortDaynightplanDTO> getAllList(@Param("businessNoList") List<String> businessNoList);

    List<TDisPortDaynightplanDTO> getListById(@Param("ids")List<Long> ids);

    BigDecimal getWeighCount(String businessNo);
}

