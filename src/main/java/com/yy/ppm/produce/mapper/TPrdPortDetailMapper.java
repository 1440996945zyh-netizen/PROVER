package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.portStorage.*;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TPrdPortDetailMapper {

    Page<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageQueryDTO query);

    Map<String, Object> summaryQuantityTon(TPrdPortStorageQueryDTO query);

    Cursor<TPrdPortStorageDTO> cursorListPortStorage(TPrdPortStorageQueryDTO query);


}
