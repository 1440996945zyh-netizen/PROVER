package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.master.bean.dto.WaifuProcessPriceExcelDTO;
import com.yy.ppm.master.bean.dto.WaifuProcessPriceReq;
import com.yy.ppm.master.bean.dto.WaifuProcessPriceRes;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WaifuProcessPriceMapper {
    List<WaifuProcessPriceRes> getList(WaifuProcessPriceReq reqDto);
    Cursor<WaifuProcessPriceExcelDTO> exportExcel(WaifuProcessPriceReq reqDto);

    List<Map<String, String>> getMainProcessList();

    List<Map<String, String>> getPositionList();

    List<Map<String, String>> getProcessListNoMain(@Param("processCode") String processCode);
    List<Map<String, String>> getDeptOut();

    List<Map<String, String>> machinTypeList();

    void deleteFunc(Long id);

    void insertBatch(@Param("list") List<WaifuProcessPriceReq> insertList,@Param("loginUserId") Long loginUserId,@Param("loginUserName") String loginUserName, @Param("nowDate") Date nowDate);

    void updateBatch(@Param("list") List<WaifuProcessPriceReq> insertList,@Param("loginUserId") Long loginUserId,@Param("loginUserName") String loginUserName, @Param("nowDate") Date nowDate);

    List<Map<String, String>> waifuPackageCodeList();
}
