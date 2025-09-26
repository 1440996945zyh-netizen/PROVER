package com.yy.ppm.setting.bean.dto;


import com.yy.ppm.setting.bean.po.TSettingAdSearchPO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName 高級查詢配置表(TSettingAdSearch)DTO
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年09月25日 15:35:00
 */
@Data
public class TSettingAdSearchDTO extends TSettingAdSearchPO {

    private static final long serialVersionUID = -77088986383147893L;

    List<TSettingAdSearchDTO> tSettingAdSearchDTOList;
}
