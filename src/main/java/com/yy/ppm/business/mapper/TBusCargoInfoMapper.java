package com.yy.ppm.business.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.bean.dto.cargoInfo.ExportDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.statement.bean.po.TCostStorageSettlePO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 票货信息表(TBusCargoInfo)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 18:47:00
 */
@Repository
public interface TBusCargoInfoMapper {

      /**
        * 获取票货信息表列表
        * @param tBusCargoInfoSearchVo
        * @return
        */
       public Page<TBusCargoInfoDTO> getList(TBusCargoInfoSearchDTO tBusCargoInfoSearchVo);
       public List<Map<String,String>> getTrustKM(@Param("ids")List<Long> ids);

       Cursor<ExportDTO> cursorListCargoInfo(TBusCargoInfoSearchDTO tBusCargoInfoSearchVo);

       public List<Map<String,Object>> getPoundbillList(PoundbillSearchDTO searchDTO);

       List<Map<String, Object>> listHandoverlistTon(@Param("ids") List<Long> ids);

       List<Map<String, Object>> listTrustCargoTon(@Param("ids") List<Long> ids);
       List<Map<String, Object>> listTrustCargoTonJG(@Param("ids") List<Long> ids);
       List<Map<String, Object>> listTrustCargoTonSG(@Param("ids") List<Long> ids);

       List<Map<String, Object>> listWeightGoodss(@Param("ids") List<Long> ids);
       List<Map<String, Object>> listWeightGoodssJG(@Param("ids") List<Long> ids);
       List<Map<String, Object>> listWeightGoodssSG(@Param("ids") List<Long> ids);

       List<Map<String, Object>> listReleaseNameTime(@Param("ids") List<Long> ids);

       Map<String, Object> getRateInfo(@Param("id") Long id);

       Map<String,Object> summary(TBusCargoInfoSearchDTO tBusCargoInfoSearchVo);

      /**
        * 导出票货信息表列表
        * @param tBusCargoInfoSearchDTO
        * @return
        */
       public List<TBusCargoInfoDTO> exportList(TBusCargoInfoSearchDTO tBusCargoInfoSearchDTO);

       /**
        * 根据id获取票货信息表
        * @param id 主键
        * @return
        */
       public TBusCargoInfoDTO getById(Long id);
       public TBusCargoInfoDTO getCargoVoyageById(Long id);

       /**
        * 新增票货信息表
        * @param tBusCargoInfoDTO
        * @return
        */
       @Edit
       public int insert(TBusCargoInfoDTO tBusCargoInfoDTO);

       /**
        * 修改票货信息表
        * @param tBusCargoInfoDTO
        * @return
        */
       @Edit
       public int update(TBusCargoInfoDTO tBusCargoInfoDTO);

       @Edit
       public int updateIsPrePay(TBusCargoInfoDTO tBusCargoInfoDTO);

 /**
  * 修改票货信息表
  * @param tBusCargoInfoDTO
  * @return
  */
 @Edit
 public int updateSurplusRightsQuantity(TBusCargoInfoDTO tBusCargoInfoDTO);


       /**
        * 根据id删除票货信息表
        * @param id 主键
        * @return
        */
       public int deleteById(Long id);

       @Edit
       int clean(TBusCargoInfoPO cargoInfo);

       List<Map<String, Object>> listAllMass(Long cargoInfoId);

       List<Map<String, Object>> listStorageYard(Long cargoInfoId);

       List<TCostStorageSettlePO> listStorageSettle(Long cargoInfoId);

       int cancelClean(Long id);

       int cancelDeleteStorageStackPosition(@Param("stackIds") List<Long> stackIds);

       int getCargoListCountByCargoId(@Param("id") Long id,@Param("businessType") String businessType);

       Long getQuantityByParentId(@Param("parentId") Long parentId);

       Integer deleteCargoCoilnumByCargoId(@Param("id") Long id);
       /**
        * 批量导入
        * @param cargoListInfoDTOS
        * @return
        */
       @Edit
       int importCargoList(@Param("list") List<CargoListInfoDTO> cargoListInfoDTOS);
       List<CargoListInfoDTO> getCargoListDataByCargoId(@Param("cargoId") Long id,@Param("businessType") String businessType);

       List<TBusCargoInfoDTO> getCargoListDataByVoyageItemId(@Param("shipVoyageItemId") Long shipVoyageItemId);

    /**
     * 根据客户查询票货
     * @param customerId
     * @return
     */
       List<TBusCargoInfoDTO> getCargoListByCustomerId(@Param("customerId") Long customerId,
                                                       @Param("shipvoyageItemId") Long shipvoyageItemId);

    List<CargoListInfoDTO> getCargoListByCargoId(@Param("cargoInfoId") Long id,@Param("businessType") String businessType);

    List<CargoBoxListInfoDTO> getCargoBoxListByCargoId(@Param("cargoInfoId") Long id,@Param("businessType") String businessType);


    void deleteCargoCoilnumByCoilList(@Param("list") List<CargoListInfoDTO> coilNumDel);
    @Edit
    void updateBatchByCoilList(@Param("list") List<CargoListInfoDTO> coilNumUpdate);

    void deleteCargoBoxnumByBoxList(@Param("list") List<CargoBoxListInfoDTO> boxNumDel);
    @Edit
    void updateBatchByBoxList(@Param("list") List<CargoBoxListInfoDTO> boxNumUpdate);
    @Edit
    int importCargoBoxList(@Param("list") List<CargoBoxListInfoDTO> boxNumAdd);

    List<Long> getTrustCargoIdByCargoInfoId(Long cargoInfoId);
    @Edit
    int isLogoutStatus(TBusCargoInfoDTO dto);

    int getPoundByCargoInfoId(@Param("cargoInfoId") Long id);

    int getCargoTransferByCargoInfoId(@Param("cargoInfoId") Long id);

    int getMixByCargoInfoId(@Param("cargoInfoId") Long id);

    int getPortStorageByCargoInfoId(@Param("cargoInfoId") Long id);

    int getTallyByCargoInfoId(@Param("cargoInfoId") Long id);

    int updateIsHq(TBusCargoInfoDTO tBusCargoInfoDTO);

}
