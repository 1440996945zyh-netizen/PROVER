package com.yy.ppm.business.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.trate.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-08 13:44
 */
@Repository
public interface TBusTrateMapper {

    boolean verifyUnique(
            @Param("contractNo") String contractNo, @Param("customerId") Long customerId,
            @Param("startTime") Date startTime, @Param("endTime") Date endTime,
            @Param("cargoCode") String cargoCode,
            @Param("ignoreId") Long ignoreId
    );

    @Edit
    int insertTrate(TBusTrateDTO trate);

    int insertTrateContract(@Param("contracts") List<TBusTrateContractDTO> contracts);

    int insertTrateCustomer(@Param("customers") List<TBusTrateCustomerDTO> customers);

    int insertTrateItem(@Param("items") List<TBusTrateItemDTO> items);

    int updateTrateItem(TBusTrateItemDTO item);

    int deleteTrateItemById(@Param("trateItemIds") List<Long> trateItemIds);

    int insertTrateCargo(@Param("cargos") List<TBusTrateItemCargoDTO> cargos);

    int insertTrateDetail(@Param("details") List<TBusTrateItemDetailDTO> details);

    boolean isUsedByContract(Long id);

    TBusTrateDTO getTrate(Long id);

    int deleteTrate(Long id);

    int deleteTrateContract(Long trateId);

    int deleteTrateCustomer(Long trateId);

    int deleteTrateCargo(Long trateId);

    int deleteTrateDetail(Long trateId);

    int deleteTrateItem(Long trateId);

    List<Long> listUsedItem(Long trateId);

    @Edit
    int updateTrate(TBusTrateDTO trate);

    Page<TBusTrateDTO> listTrate(TBusTrateQueryDTO query);

    List<TBusTrateContractDTO> listTrateContract(@Param("trateIds") List<Long> trateIds);

    List<TBusTrateCustomerDTO> listTrateCustomer(@Param("trateIds") List<Long> trateIds);

    List<TBusTrateItemDTO> listTrateItem(@Param("trateIds") List<Long> trateIds);

    List<TBusTrateItemCargoDTO> listTrateItemCargo(@Param("trateItemIds") List<Long> trateItemIds);

    List<TBusTrateItemDetailDTO> listTrateItemDetail(@Param("trateItemIds") List<Long> trateItemIds);

    int release(Long id);

    int cancelRelease(Long id);

    int updateOriginAccNumber(@Param("id") Long id, @Param("originAccNumber") BigDecimal originAccNumber);
}
