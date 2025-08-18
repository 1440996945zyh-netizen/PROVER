package com.yy.ppm.business.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;
import java.util.Map;

public interface ScheduledForCloseTrustMapper {
    List<Map<String, Object>> getCargoInfo();

    void trustCargoSetStop(@Param("list") List<Long> updateList);
}
