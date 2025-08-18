package com.yy.ppm.produce.service.impl;

import com.google.api.client.util.Lists;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.common.enums.CleanMassSignEnum;
import com.yy.ppm.common.enums.InoutStorageEnum;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDetailDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.produce.mapper.TPrdPortStorageDetailMapper;
import com.yy.ppm.produce.service.TPrdPortStorageDetailService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 13:41
 */
@Service
public class TPrdPortStorageDetailServiceImpl implements TPrdPortStorageDetailService {

    @Autowired
    private TPrdPortStorageDetailMapper tPrdPortStorageDetailMapper;

    private static final String TZ_PROCESS_DETAIL_CODE = "10240001";

    private static final String TZ_PROCESS_DETAIL_NAME = "调账（子过程）";


	private static final String TZ_PROCESS_DETAIL_P1= "调账（涨吨）";
	private static final String TZ_PROCESS_DETAIL_P2= "调账（亏吨）";
	private static final String TZ_PROCESS_DETAIL_P3= "本地调账";

    @Autowired
    private BusinessCommonService businessCommonService;

    @Override
    public Pages<TPrdPortStorageDetailDTO> listPortStorageDetail(TPrdPortStorageDetailDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdPortStorageDetailMapper.listPortStorageDetail(query);
        });
    }

    @Override
    public Pages<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdPortStorageDetailMapper.listPortStorage(query);
        });
    }
    
	@Override
	public TPrdPortStorageDTO getPortStorage(TPrdPortStorageDTO query) {
		return tPrdPortStorageDetailMapper.getPortStorage(query);
	}

    @Override
    public void insertPortStorage(TPrdPortStorageDetailDTO prdPortStorageDetailDTO) {
    	
    	List<TPrdPortStorageDetailPO> portStorageDetails = Lists.newArrayList();
    	
    	// 本地调账
    	if(Optional.ofNullable(prdPortStorageDetailDTO.getLocalStorageChange()).orElse(false)) {
    		TPrdPortStorageDetailPO prdPortStorageDetailPO = new TPrdPortStorageDetailPO();
    		
    		prdPortStorageDetailPO.setWorkDate(prdPortStorageDetailDTO.getWorkDate());
    		prdPortStorageDetailPO.setClassCode(prdPortStorageDetailDTO.getClassCode());
    		prdPortStorageDetailPO.setClassName(prdPortStorageDetailDTO.getClassName());
    		prdPortStorageDetailPO.setCargoInfoId(prdPortStorageDetailDTO.getFromTrustCargoId());
			if("p1".equals(prdPortStorageDetailDTO.getProcessOtherCode())){
				prdPortStorageDetailPO.setProcessDetailName(TZ_PROCESS_DETAIL_P1);
			}else if("p2".equals(prdPortStorageDetailDTO.getProcessOtherCode())){
				prdPortStorageDetailPO.setProcessDetailName(TZ_PROCESS_DETAIL_P2);
			}else if("p3".equals(prdPortStorageDetailDTO.getProcessOtherCode())){
				prdPortStorageDetailPO.setProcessDetailCode("");
				prdPortStorageDetailPO.setProcessDetailName(TZ_PROCESS_DETAIL_P3);
			}
    		prdPortStorageDetailPO.setStorehouseId(prdPortStorageDetailDTO.getFromStorehouseId());
    		prdPortStorageDetailPO.setStorehouseName(prdPortStorageDetailDTO.getFromStorehouseName());
    		prdPortStorageDetailPO.setRegionId(prdPortStorageDetailDTO.getFromRegionId());
    		prdPortStorageDetailPO.setRegionName(prdPortStorageDetailDTO.getFromRegionName());
    		prdPortStorageDetailPO.setMassId(prdPortStorageDetailDTO.getFromMassId());
    		prdPortStorageDetailPO.setMassName(prdPortStorageDetailDTO.getFromMassName());
    		prdPortStorageDetailPO.setQuantity(prdPortStorageDetailDTO.getQuantity());
    		prdPortStorageDetailPO.setTon(prdPortStorageDetailDTO.getTon());
    		prdPortStorageDetailPO.setInoutStorageCode(InoutStorageEnum._40.getCode());
    		prdPortStorageDetailPO.setInoutStorageName(InoutStorageEnum._40.getLabel());
    		prdPortStorageDetailPO.setInoutDate(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate());
    		prdPortStorageDetailPO.setCleanMassSign(CleanMassSignEnum._0.getCode());
    		prdPortStorageDetailPO.setCompanyId(prdPortStorageDetailDTO.getCompanyId());
    		prdPortStorageDetailPO.setCompanyName(prdPortStorageDetailDTO.getCompanyName());
    		
    		portStorageDetails.add(prdPortStorageDetailPO);
    	} else {
    		
    		TPrdPortStorageDetailPO prdPortStorageDetailPO_From = new TPrdPortStorageDetailPO();
    		
    		prdPortStorageDetailPO_From.setWorkDate(prdPortStorageDetailDTO.getWorkDate());
    		prdPortStorageDetailPO_From.setClassCode(prdPortStorageDetailDTO.getClassCode());
    		prdPortStorageDetailPO_From.setClassName(prdPortStorageDetailDTO.getClassName());
    		prdPortStorageDetailPO_From.setCargoInfoId(prdPortStorageDetailDTO.getFromTrustCargoId());
    		prdPortStorageDetailPO_From.setProcessDetailCode(TZ_PROCESS_DETAIL_CODE);
    		prdPortStorageDetailPO_From.setProcessDetailName(TZ_PROCESS_DETAIL_NAME);
    		prdPortStorageDetailPO_From.setStorehouseId(prdPortStorageDetailDTO.getFromStorehouseId());
    		prdPortStorageDetailPO_From.setStorehouseName(prdPortStorageDetailDTO.getFromStorehouseName());
    		prdPortStorageDetailPO_From.setRegionId(prdPortStorageDetailDTO.getFromRegionId());
    		prdPortStorageDetailPO_From.setRegionName(prdPortStorageDetailDTO.getFromRegionName());
    		prdPortStorageDetailPO_From.setMassId(prdPortStorageDetailDTO.getFromMassId());
    		prdPortStorageDetailPO_From.setMassName(prdPortStorageDetailDTO.getFromMassName());
    		prdPortStorageDetailPO_From.setQuantity(prdPortStorageDetailDTO.getQuantity() * -1);
    		prdPortStorageDetailPO_From.setTon(prdPortStorageDetailDTO.getTon().multiply(new BigDecimal(-1)));
    		prdPortStorageDetailPO_From.setInoutStorageCode(InoutStorageEnum._40.getCode());
    		prdPortStorageDetailPO_From.setInoutStorageName(InoutStorageEnum._40.getLabel());
    		prdPortStorageDetailPO_From.setInoutDate(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate());
    		prdPortStorageDetailPO_From.setCleanMassSign(CleanMassSignEnum._0.getCode());
    		prdPortStorageDetailPO_From.setCompanyId(prdPortStorageDetailDTO.getCompanyId());
    		prdPortStorageDetailPO_From.setCompanyName(prdPortStorageDetailDTO.getCompanyName());
    		
    		portStorageDetails.add(prdPortStorageDetailPO_From);
    		
    		TPrdPortStorageDetailPO prdPortStorageDetailPO_To = new TPrdPortStorageDetailPO();
    		
    		prdPortStorageDetailPO_To.setWorkDate(prdPortStorageDetailDTO.getWorkDate());
    		prdPortStorageDetailPO_To.setClassCode(prdPortStorageDetailDTO.getClassCode());
    		prdPortStorageDetailPO_To.setClassName(prdPortStorageDetailDTO.getClassName());
    		prdPortStorageDetailPO_To.setCargoInfoId(prdPortStorageDetailDTO.getFromTrustCargoId());
    		prdPortStorageDetailPO_To.setProcessDetailCode(TZ_PROCESS_DETAIL_CODE);
    		prdPortStorageDetailPO_To.setProcessDetailName(TZ_PROCESS_DETAIL_NAME);
    		prdPortStorageDetailPO_To.setStorehouseId(prdPortStorageDetailDTO.getToStorehouseId());
    		prdPortStorageDetailPO_To.setStorehouseName(prdPortStorageDetailDTO.getToStorehouseName());
    		prdPortStorageDetailPO_To.setRegionId(prdPortStorageDetailDTO.getToRegionId());
    		prdPortStorageDetailPO_To.setRegionName(prdPortStorageDetailDTO.getToRegionName());
    		prdPortStorageDetailPO_To.setMassId(prdPortStorageDetailDTO.getToMassId());
    		prdPortStorageDetailPO_To.setMassName(prdPortStorageDetailDTO.getToMassName());
    		prdPortStorageDetailPO_To.setQuantity(prdPortStorageDetailDTO.getQuantity());
    		prdPortStorageDetailPO_To.setTon(prdPortStorageDetailDTO.getTon());
    		prdPortStorageDetailPO_To.setInoutStorageCode(InoutStorageEnum._40.getCode());
    		prdPortStorageDetailPO_To.setInoutStorageName(InoutStorageEnum._40.getLabel());
    		prdPortStorageDetailPO_To.setInoutDate(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate());
    		prdPortStorageDetailPO_To.setCleanMassSign(CleanMassSignEnum._0.getCode());
    		prdPortStorageDetailPO_To.setCompanyId(prdPortStorageDetailDTO.getCompanyId());
    		prdPortStorageDetailPO_To.setCompanyName(prdPortStorageDetailDTO.getCompanyName());
    		
    		portStorageDetails.add(prdPortStorageDetailPO_To);
    	}
        businessCommonService.insertPortStorageDetail(portStorageDetails);
    }
}