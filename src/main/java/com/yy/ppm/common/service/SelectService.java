package com.yy.ppm.common.service;

import com.yy.ppm.common.bean.dto.ResponsePopupTrustDTO;
import com.yy.ppm.common.bean.dto.SelecSearchDTO;
import com.yy.ppm.master.bean.dto.MCityDTO;
import com.yy.ppm.master.bean.po.*;
import com.yy.ppm.system.bean.dto.SysDeptDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下拉框数据源
 */
public interface SelectService {

    /**
     * 远程下拉框
     * @param selectCommonSearch
     * @return
     */
    List<Map<String, Object>> getRemoteSelect(SelecSearchDTO selectCommonSearch);

    /**
     * 获取本地下拉框数据源
     * @param params
     * @return
     */
    List<Map<String, Object>> getLocalSelect(Map<String, Object> params);

    /**
     * 获取本地下拉框数据源
     * @param types
     * @return
     */
    HashMap<String, List<Map<String, Object>>> getLocalSelects(String types);

    /**
     * 通用指令信息
     */
    public List<ResponsePopupTrustDTO> getPopupTrust(Map<String, Object> params);

}
