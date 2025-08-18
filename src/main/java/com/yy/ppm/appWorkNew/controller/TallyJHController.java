package com.yy.ppm.appWorkNew.controller;

import cn.hutool.core.util.ObjectUtil;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.appWork.bean.dto.AppTallyLadingDTO;
import com.yy.ppm.appWork.bean.dto.TYardMeasureSearchDTO;
import com.yy.ppm.appWorkNew.bean.dto.WorkPlanSearchDTO;
import com.yy.ppm.appWorkNew.service.TallyNewService;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * App理货
 *
 * @author chenfs
 * @since 2023年9月14日
 */
@RestController
@RequestMapping("/api/external/TallyJH")
@Validated
public class TallyJHController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(TallyJHController.class);


    @Autowired
    private TallyNewService tallyService;

    /**
     * 查询计划
     * @param
     * @param searchDTO 实例对象
     * @return
     */
    @GetMapping("/getJHWorkPlan")
    public Map<String, Object> getJHWorkPlan(WorkPlanSearchDTO searchDTO) {
        List<TPrdWorkPlanDTO> list = tallyService.getWorkPlan(searchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }




    /**
     * 场/船
     * 查询出入库数据(港存)
     * @param tYardMeasureSearchDTO
     * @return
     */
    @GetMapping("/getPortStorage")
    public Map<String, Object> getPortStorage(TYardMeasureSearchDTO tYardMeasureSearchDTO) {
        List<Map<String, Object>> list = tallyService.getCarDetailedListNew(tYardMeasureSearchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }


}
