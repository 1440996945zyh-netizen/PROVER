package com.yy.ppm.produce.service;

import com.yy.common.page.Pages;
import com.yy.ppm.common.bean.po.SysFilePO;
import com.yy.ppm.produce.bean.dto.TTruckLogDTO;
import com.yy.ppm.system.bean.dto.SysVersionControlDTO;
import com.yy.ppm.system.bean.po.SysVersionControlPO;

import java.util.List;

public interface TTruckLogService {

	Pages<TTruckLogDTO> getList(TTruckLogDTO tTruckLogDTO);

}
