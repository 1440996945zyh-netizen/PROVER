package com.yy.ppm.dispatch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Snowflake;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.google.common.collect.Lists;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.SpringUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusCustomerPO;
import com.yy.ppm.business.bean.po.TBusTrustCargoPO;
import com.yy.ppm.business.bean.po.TBusTrustPO;
import com.yy.ppm.business.mapper.TBusTrustCargoMapper;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationDTO;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationSearchDTO;
import com.yy.ppm.dispatch.bean.po.TBusTrustLocationPO;
import com.yy.ppm.dispatch.controller.TBusTrustLocationController;
import com.yy.ppm.dispatch.mapper.TBusTrustLocationMapper;
import com.yy.ppm.dispatch.service.TBusTrustLocationService;
import com.yy.ppm.master.bean.po.MDictDataPO;
import com.yy.ppm.master.bean.po.MTrustTypePO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @ClassName 集疏港作业通知单位置表，传输渤海通使用(TBusTrustLocation)ServiceImpl
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2023年09月27日 14:34:00
 */
@Service
public class TBusTrustLocationServiceImpl implements TBusTrustLocationService {

    @Resource
    private TBusTrustLocationMapper tBusTrustLocationMapper;
    @Resource
    private CommonService commonService;
    @Resource
    private Snowflake snowflake;
    @Resource
    private TBusTrustCargoMapper tBusTrustCargoMapper;
    @Autowired
    private Environment environment;
    @Resource
    private TBusTrustMapper tBusTrustMapper;

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TBusTrustLocationController.class);

    /**
     * 派工，派场地
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean update(TBusTrustLocationDTO trustLocationDTO) {
        if(trustLocationDTO==null){
            throw new BusinessRuntimeException("数据为空~");
        }

        if(CollectionUtils.isEmpty(trustLocationDTO.getLocationListTarget())){
            throw new BusinessRuntimeException("请选择库场之后再进行保存~");
        }

        List<TBusTrustLocationPO> pos = Lists.newArrayList();
        trustLocationDTO.getLocationListTarget().forEach(location -> {
            TBusTrustLocationPO po = new TBusTrustLocationPO();
            BeanUtil.copyProperties(location,po);
            po.setId(snowflake.nextId());
            po.setTrustId(trustLocationDTO.getTrustId());
            pos.add(po);
        });
        TBusTrustLocationDTO del = new TBusTrustLocationDTO();
        del.setTrustId(trustLocationDTO.getTrustId());
        deleteByCondition(del);
        //判断本次选择位置的所属区域是否是一个，如果存在不同的区域则不场地安排失败
        List<MDictDataPO> workArealist = tBusTrustLocationMapper.getWorkArea(pos);
        if(workArealist==null || workArealist.size()>1){
            throw new BusinessRuntimeException("所选位置的所属作业港区不同，请选择同一作业港区的区域");
        }
        tBusTrustLocationMapper.insertList(pos);

        // 写入作业通知单港区字段。
        TBusTrustDTO tBustTrustDTO = new TBusTrustDTO();
        tBustTrustDTO.setPortCode(workArealist.get(0).getDictValue());
        tBustTrustDTO.setPortName(workArealist.get(0).getDictLabel());
        tBustTrustDTO.setId(trustLocationDTO.getTrustId());
        tBusTrustMapper.updatePort(tBustTrustDTO);

        //根据指令ID获取作业计划编号
        List<String> businessNoList = tBusTrustLocationMapper.getBusinessNo(trustLocationDTO.getTrustId());
        if("东作业区".equals(workArealist.get(0).getDictLabel())){
            workArealist.get(0).setDictValue("1");
            workArealist.get(0).setDictLabel("东港");
        }else if("西作业区".equals(workArealist.get(0).getDictLabel())){
            workArealist.get(0).setDictValue("2");
            workArealist.get(0).setDictLabel("西港");
        }
        //场地安排成功后将所派场地所属区域写入动脉计划表
/*        String env = environment.getProperty("deploy.env");
        if ("prd".equals(env)) {
            // 发布写入过磅
            SpringUtils.getBean(this.getClass()).writeSimeautoLocation(businessNoList,workArealist.get(0));
        }*/
        return true;
    }

    /**
     * 获取场地详情
     * @param trustId
     * @return
     */
    public TBusTrustLocationDTO getDetail(Long trustId) {
        TBusTrustLocationDTO result = new TBusTrustLocationDTO();
        TBusTrustLocationSearchDTO searchDTO = new TBusTrustLocationSearchDTO();
        searchDTO.setTrustId(trustId);
        List<TBusTrustLocationDTO> list = getListByCondition(searchDTO);
        if(CollectionUtils.isNotEmpty(list)){
            List<TBusTrustLocationDTO.Location> locations = Lists.newArrayList();
            List<String> regionIdsTarget = Lists.newArrayList();
            String storehouseName = new String();
            for(TBusTrustLocationDTO dto:list){
                storehouseName +=","+dto.getStorehouseName()+"/"+dto.getRegionName();
                TBusTrustLocationDTO.Location location = new TBusTrustLocationDTO.Location();
                BeanUtil.copyProperties(dto,location);
                locations.add(location);
                regionIdsTarget.add(dto.getRegionId());
            }
            result.setLocationListTarget(locations);
            storehouseName = storehouseName.substring(1);
            result.setMassNamesTarget(storehouseName);
            result.setRegionIdsTarget(regionIdsTarget);
        }
        result.setTrustId(trustId);
        result.setCargoList(tBusTrustCargoMapper.getList(trustId));
        return result;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////








	 /**
     * 获取列表
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<TBusTrustLocationDTO> getListByCondition(TBusTrustLocationSearchDTO searchDTO) {
	    final String methodName = "TBusTrustLocationServiceImpl:getListByCondition";
		try{
			LOGGER.info(methodName,"获取列表");
			List<TBusTrustLocationDTO> list = tBusTrustLocationMapper.exportList(searchDTO);
			//按照创建时间倒叙排列
			list.sort(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
			return list;
        }catch (Exception e){
            LOGGER.error(methodName,e.getMessage());
            return Lists.newArrayList();
        }
    }

	/**
     * 批量删除
     * @param  tBusTrustLocationDTO
     * @return 是否成功
     */
    @Override
    public boolean deleteByCondition(TBusTrustLocationDTO tBusTrustLocationDTO) {
        return tBusTrustLocationMapper.deleteByCondition(tBusTrustLocationDTO) >= 1;
    }

    /**
     * 库场计划指派位置后判断位置所属区域，写入动脉计划表中
     *
     * @param businessNoList
     */
    @DS("simeauto")
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void writeSimeautoLocation(List<String> businessNoList,MDictDataPO workArea) {
        tBusTrustLocationMapper.writeSimeautoLocation(businessNoList,workArea);
    }

    /**
     * 库场计划返回值
     * @param dto
     * @return
     */
    @Override
    public List<Map<String, Object>> getMassIdsWithTrustId(TBusTrustLocationDTO dto) {
        return  tBusTrustLocationMapper.getMassIdsWithTrustId(dto);
    }
}

