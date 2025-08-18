package com.yy.ppm.dispatch.service;



import com.yy.common.page.Pages;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationDTO;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationSearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;

import java.util.*;

 /**
 * @ClassName 集疏港作业通知单位置表，传输渤海通使用(TBusTrustLocation)Service
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2023年09月27日 14:34:00
 */
public interface TBusTrustLocationService {



     /**
      * 派工，派场地
      * @return
      */
     public boolean update(TBusTrustLocationDTO dto);


     /**
      * 查询单条记录
      *
      * @param trustId
      * @return 实体
      */
     public TBusTrustLocationDTO getDetail(Long trustId);




	public List<TBusTrustLocationDTO> getListByCondition(TBusTrustLocationSearchDTO searchDTO);
    
	/**
     * 批量删除
     * @param  tBusTrustLocationDTO
     * @return 是否成功
     */
	public boolean deleteByCondition(TBusTrustLocationDTO tBusTrustLocationDTO);

     List<Map<String, Object>> getMassIdsWithTrustId(TBusTrustLocationDTO dto);
 }

