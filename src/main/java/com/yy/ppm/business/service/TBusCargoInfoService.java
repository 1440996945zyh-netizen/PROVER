package com.yy.ppm.business.service;


import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.bean.dto.cargoInfo.CleanAllPortStorageDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 票货信息表(TBusCargoInfo)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 18:47:00
 */
public interface TBusCargoInfoService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<TBusCargoInfoDTO> getList(TBusCargoInfoSearchDTO searchDTO);

    byte[] export(TBusCargoInfoSearchDTO searchDTO);

    public List<Map<String,Object>> getPoundbillList(PoundbillSearchDTO searchDTO);

    /**
     * 汇总
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Map<String, Object> summary(TBusCargoInfoSearchDTO searchDTO);

    /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public TBusCargoInfoDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tBusCargoInfoDTO
     * @return 是否成功
     */
    public boolean doSave(TBusCargoInfoDTO tBusCargoInfoDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

     /**
      * 货权转移记录
      *
      * @param cargoInfoId
      * @return 对象列表
      */
     public List<TBusCargoTransferDTO> getTransferList(Long cargoInfoId);

    void cleanAllPortStorage(CleanAllPortStorageDTO cleanAllPortStorage);

    void cancelCleanAllPortStorage(Long id);

    Map<String, Object> getCargoListByCargoCode(Long id,String businessType);

    boolean importCargoList(Long id,MultipartFile file);

    List<CargoListInfoDTO> getCargoListInfoByCargoId(Long id,String businessType);

    void exportTemplate(HttpServletResponse response);

    /**
     * 根据客户查询票货
     *
     * @param customerId
     * @param shipvoyageItemId
     * @return 实体
     */
    List<TBusCargoInfoDTO> getCargoListByCustomerId(Long customerId, Long shipvoyageItemId);

    void exportBoxTemplate(HttpServletResponse response);

    boolean importCargoBoxList(Long id, MultipartFile file);

    boolean isLogoutStatus(TBusCargoInfoDTO tBusCargoInfoDTO);

    boolean updateIsHq(TBusCargoInfoDTO tBusCargoInfoDTO);
}

