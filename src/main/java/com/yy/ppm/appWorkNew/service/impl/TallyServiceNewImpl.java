package com.yy.ppm.appWorkNew.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.appWork.bean.dto.AppTallyLadingDTO;
import com.yy.ppm.appWork.bean.dto.TYardMeasureSearchDTO;
import com.yy.ppm.appWork.mapper.TallyMapper;
import com.yy.ppm.appWorkNew.bean.dto.WorkPlanSearchDTO;
import com.yy.ppm.appWorkNew.mapper.TallyJHMapper;
import com.yy.ppm.appWorkNew.service.TallyNewService;
import com.yy.ppm.common.enums.SourceTargetTypeEnum;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TallyServiceNewImpl implements TallyNewService {


    @Resource
    private TallyJHMapper tallyJHMapper;

    @Resource
    private SecurityUtils securityUtils;
    @Resource
    private TallyMapper tallyMapper;
    @Resource
    private SysParameterMapper sysParameterMapper;


    @Override
    public List<TPrdWorkPlanDTO> getWorkPlan(WorkPlanSearchDTO searchDTO) {
        //查询该用户是否是调度室和库场部的值班主任
        Integer flag = tallyJHMapper.getIsDept(securityUtils.getLoginUserId());
        searchDTO.setFlag(flag);
        searchDTO.setLoginId(securityUtils.getLoginUserId() + "");
        List<TPrdWorkPlanDTO> workPlanList = tallyJHMapper.getWorkPlan(searchDTO);
        List<Map<String, Object>> maps = tallyJHMapper.getWorkPlanCargoInfo(searchDTO);
        if (!CollectionUtils.isEmpty(workPlanList)) {
            for (TPrdWorkPlanDTO prdWorkPlanDTO : workPlanList) {
                if ("1".equals(prdWorkPlanDTO.getIsDirectAccess())) {
                    //直取
                    List<String> type;
                    //源是船的为船-车
                    if (SourceTargetTypeEnum._01.getCode().equals(prdWorkPlanDTO.getSourceCd())) {
                        type = Collections.singletonList("疏港");
                    } else if (SourceTargetTypeEnum._05.getCode().equals(prdWorkPlanDTO.getSourceCd())) {
                        //场-车 (陆集陆疏)
                        type = Collections.singletonList("陆销");
                    } else {
                        //目的是船是车-船
                        type = Stream.of("集港", "拆箱集港").collect(Collectors.toList());
                    }
                    List<Map<String, Object>> tBusCargoInfoDTOS = tallyJHMapper.getTrustInfoNo(prdWorkPlanDTO.getTrustId().toString(), type);
                    if (!CollectionUtils.isEmpty(tBusCargoInfoDTOS)) {
                        prdWorkPlanDTO.setCargoInfoList(tBusCargoInfoDTOS);
                    }
                } else {
                    List<Map<String, Object>> tBusCargoInfoDTOS = maps.stream()
                            .filter(obj -> obj.get("planId").toString().equals(prdWorkPlanDTO.getId().toString()))
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(tBusCargoInfoDTOS)) {
                        prdWorkPlanDTO.setCargoInfoList(tBusCargoInfoDTOS);
                    }
                }

            }
        }
        return workPlanList;
    }

    @Override
    public List<Map<String, Object>> getCarDetailedListNew(TYardMeasureSearchDTO tYardMeasureSearchDTO) {
        //根据指令查票货
        String str = tallyMapper.getTrustCargoId(tYardMeasureSearchDTO.getTrustId());
        if (str == null) {
            throw new BusinessRuntimeException("查询失败，未查询到票货信息");
        }
        str = "(" + str + ")";
        tYardMeasureSearchDTO.setTrustIds(str);

        SysParameterDTO sysParameter = sysParameterMapper.getByKey("TALLY_STORAGE_IS_FRONTIER");
        boolean switchIsFrontier = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
        if (switchIsFrontier) {
            tYardMeasureSearchDTO.setSwitchIsFrontier("1");
            //校验根据作业过程进行校验
            String isFrontier = tallyMapper.getProcessIsFrontier(tYardMeasureSearchDTO.getProcessCode());
            if (StringUtils.isNotEmpty(isFrontier)) {
                tYardMeasureSearchDTO.setIsFrontier(isFrontier);
            }
        } else {
            tYardMeasureSearchDTO.setSwitchIsFrontier(null);
        }
        //获取货物的港存情况
        List<Map<String, Object>> carDetailedList = tallyMapper.getPortStorgeByPlanId(tYardMeasureSearchDTO);
        //获取作业的垛位配置情况
        List<Map<String, Object>> storeList = tallyMapper.getStore(Long.valueOf(tYardMeasureSearchDTO.getWorkPlanId()));;
        //开始组装数据  stackPositionId
        List<Map<String,Object>> stackList = Lists.newArrayList();
        for(Map<String,Object> map : storeList){
            String massId = String.valueOf(map.get("massId"));
            map.put("id",String.valueOf(map.get("massId")));
            for(Map<String, Object> tallyLadingDTO : carDetailedList){
                String stackPositionId = String.valueOf(tallyLadingDTO.get("stackPositionId"));
                if(massId.equals(stackPositionId)){
                    map.put("quantitySurplus",tallyLadingDTO.get("quantitySurplus"));
                    map.put("tonSurplus",tallyLadingDTO.get("tonSurplus"));
                    stackList.add(map);
                }
            }
        }
        carDetailedList.forEach(e->e.put("stackList",stackList));
        return carDetailedList;
    }



}
