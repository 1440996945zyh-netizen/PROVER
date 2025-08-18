package com.yy.ppm.dispatch.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.TDisShipDaynigttplanDTO;
import com.yy.ppm.dispatch.bean.dto.TDisShipDaynigttplanSearchDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO2;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 船舶昼夜计划(TDisShipDaynigttplan)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月17日 10:31:00
 */
@Repository
public interface TDisShipDaynigttplanMapper {


   Long getWorkNum(Long shipvoyageItemId,String planDate);

   List<TDisShipvoyageItemPO> getByShipvoyageId(Long shipvoyageId);


   /**
     * 获取船舶昼夜计划列表
     * @param planDate
     * @return
     */
    public List<TDisShipDaynigttplanDTO> getList(@Param("planDate") String planDate);

    /**
     * 获取前一天船舶昼夜计划列表
     * @param planDate
     * @return
     */
    public List<TDisShipDaynigttplanDTO> getList2(@Param("planDate") String planDate);
    /**
     * 新增船舶昼夜计划
     * @param tDisShipDaynigttplanDTO
     * @return
     */
    @Edit
    public int insert(TDisShipDaynigttplanDTO tDisShipDaynigttplanDTO);

    /**
     * 根据日期删除船舶昼夜计划
     * @param planDate
     * @return
     */
    public int deleteByPlanDate(String planDate);

    List<Map<String, String>> getShipVoyage();

}

