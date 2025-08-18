package com.yy.ppm.gis.dto.route;

import com.yy.ppm.gis.po.TRoutesPO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TRoutesDTO extends TRoutesPO implements Serializable {

    private BigDecimal startLon;

    private BigDecimal startLat;

    private BigDecimal endLon;

    private BigDecimal endLat;

    private static final long serialVersionUID = 1L;
}

