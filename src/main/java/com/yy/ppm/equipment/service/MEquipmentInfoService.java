package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EquipQrCodeIdReqDTO;
import com.yy.ppm.equipment.bean.dto.EquipmentSelectDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoSearchDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 设备台账信息Service接口
 * @author system
 */
public interface MEquipmentInfoService {

    /**
     * 查询设备台账信息列表（分页）
     */
    Pages<MEquipmentInfoDTO> getList(MEquipmentInfoSearchDTO searchDTO);

    /**
     * 根据ID查询设备台账信息
     */
    MEquipmentInfoDTO getById(Long id);

    /**
     * 新增设备台账信息
     */
    void save(MEquipmentInfoDTO dto);

    /**
     * 删除设备台账信息
     */
    void deleteById(Long id);

    /**
     * 修改设备基本信息
     */
    void updateBasicInfo(MEquipmentInfoDTO dto);

    /**
     * 修改财务/供货信息
     */
    void updateFinanceSupply(MEquipmentInfoDTO dto);

    /**
     * 修改特种设备信息
     */
    void updateSpecialInfo(MEquipmentInfoDTO dto);

    /**
     * 保存设备照片
     */
    void updateEquipmentImages(MEquipmentInfoDTO dto);

    /**
     * 查询设备选择列表（用于下拉框）
     * @param keyword 搜索关键词（设备名称或编码）
     */
    List<EquipmentSelectDTO> getEquipmentSelectList(String keyword);

    /**
     * 根据设备ID查询备品备件列表
     * @param equipId 设备ID
     * @param materialName 物资名称（可选，用于模糊查询）
     * @param warehouseName 仓库名称（可选，用于过滤）
     * @return 备品备件列表
     */
    List<com.yy.ppm.equipment.bean.dto.EquipmentSpareDTO> getSpareList(String equipId, String materialName, String warehouseName);

    /**
     * 功能描述: 添加变更记录
     * @param equipId
     * @param oldData
     * @param newData
     * @return : void
     */
    void recordBasicInfoChange(Long equipId, MEquipmentInfoDTO oldData, MEquipmentInfoDTO newData);

    /**
     * 根据设备id导出设备二维码
     * @param response 输出流
     * @param userAccount 用户账号
     * @param equipQrCodeIdReqDTO 请求参数
     */
    void getEquipQRCode(HttpServletResponse response, String userAccount, EquipQrCodeIdReqDTO equipQrCodeIdReqDTO);
}

