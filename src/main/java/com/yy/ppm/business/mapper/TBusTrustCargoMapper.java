package com.yy.ppm.business.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusCargoShipDTO;
import com.yy.ppm.business.bean.dto.TBusHandoverListDTO;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.trust.TrustCargoDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName (TBusTrustCargo)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月05日 09:21:00
 */
@Repository
public interface TBusTrustCargoMapper {

   /**
     * 获取列表
     * @param trustId
     * @return
     */
    public List<TBusTrustCargoDTO> getList(Long trustId);

    /**
     * 根据id获取
     * @param id 主键
     * @return
     */
    public TBusTrustCargoDTO getById(Long id);

    public TBusTrustCargoDTO getByBHTId(String bhtId);

    public TBusTrustCargoDTO getByBusinessNo(String businessNo);

    /**
     * 新增
     * @param tBusTrustCargoDTO
     * @return
     */
    @Edit
    public int insert(TBusTrustCargoDTO tBusTrustCargoDTO);

    /**
     * 更新委托人
     * @param trustCargoDTO
     * @return
     */
    @Edit
    public int updateConsigner(TrustCargoDTO trustCargoDTO);

    /**
     * 修改
     * @param tBusTrustCargoDTO
     * @return
     */
    @Edit
    public int update(TBusTrustCargoDTO tBusTrustCargoDTO);


  /**
   * 修改
   * @param tBusTrustCargoDTO
   * @return
   */
  @Edit
  public int updateNotNull(TBusTrustCargoDTO tBusTrustCargoDTO);


    /**
     * 根据id删除
     * @param id 主键
     * @return
     */
    public int deleteById(Long id);

    /**
     * 查询船舶信息
     * */
    Map<String, Object> getShipInfo(@Param("id") Long id);

    List<TBusTrustCargoDTO> getSignList(@Param("id") Long id, @Param("isOrderByCreateTime") boolean isOrderByCreateTime);

    BigDecimal getCount(String businessNo);

    List<TBusCargoInfoPO> getCargoInfoListByTrustId(Long id);

    List<TBusCargoShipDTO> getCargoShipWithTrust(@Param("trustId") Long id,@Param("cargoInfoId") Long id1,@Param("shipVoyageItemId") Long shipVoyageItemId);

    @Edit
    void insertCargoShip(TBusCargoShipDTO tBusCargoShipDTO);

    void deleteCargoShipByCargoTrust(@Param("cargoInfoId") Long id, @Param("trustId") Long id1);

    void updateXCCargoInfo(@Param("cargoInfoIds") List<Long> collect, @Param("trustId") Long id, @Param("updateBy") Long updateBy, @Param("updateByName") String updateByName, @Param("updateTime") Date updateDate);
    void updateXCHandoverlist(@Param("trustCargoId") Long trustCargoId,@Param("cargoInfoId") Long cargoInfoId,@Param("updateBy") Long updateBy, @Param("updateByName") String updateByName, @Param("updateTime") Date updateDate);

    List<Long> getCargoTrustIdByIds(@Param("ids") List<Long> collect);

    /**
     * 获取卸船交接清单
     * @param collect
     * @return
     */
    List<TBusHandoverListDTO> gethandoverListByCargoInfoId(@Param("cargoInfoIds") List<Long> collect);
    List<TBusHandoverListDTO> handoverListByCargoInfoId(@Param("loadUnload") String loadUnload,@Param("cargoInfoIds") List<Long> collect);
    @Edit
    void updateEntrustDetail(TBusTrustCargoDTO cargo);

    @Edit
    void updateentrustDetailForCargoInfo(TBusTrustCargoDTO busTrustCargoDTO);

    void insertEntrustDetial(TBusTrustCargoDTO cargo);

    List<TBusCargoInfoPO> getcargoinfoList(@Param("cargoInfoIds") List<Long> collect);
}

