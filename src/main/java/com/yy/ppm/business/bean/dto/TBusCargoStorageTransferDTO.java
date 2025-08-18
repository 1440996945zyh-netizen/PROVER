package com.yy.ppm.business.bean.dto;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName 货权转移-港存动态
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2024年01月15日 19:37:00
 */
@Data
public class TBusCargoStorageTransferDTO implements Serializable {

    private static final long serialVersionUID = 575231257950234635L;
    private Long id;
    private List<TBusCargoStorageDTO> storageList;
}
