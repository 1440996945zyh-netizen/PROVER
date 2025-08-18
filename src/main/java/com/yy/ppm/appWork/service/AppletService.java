package com.yy.ppm.appWork.service;

import java.util.List;
import java.util.Map;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.appWork.bean.dto.*;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.appWork.bean.po.TYardTallyPO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondarySearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;

public interface AppletService {

    List<Map<String, Object>> getPortCondition();

    Map<String, Object> blacklistQuery(String plateNumber);

}
