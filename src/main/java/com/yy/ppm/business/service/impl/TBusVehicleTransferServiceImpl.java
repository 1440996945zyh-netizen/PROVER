package com.yy.ppm.business.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.machine.bean.dto.WeightRecordPoundDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.po.TBusVehicleTransferPO;
import com.yy.ppm.business.mapper.TBusRateMapper;
import com.yy.ppm.business.mapper.TBusTrustCargoMapper;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.business.mapper.TBusVehicleTransferMapper;
import com.yy.ppm.business.service.TBusVehicleTransferService;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.customs.bean.TosPlanDTO;
import com.yy.ppm.customs.mapper.TCustomsMapper;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;
import com.yy.ppm.statement.mapper.MiscBillingMapper;

import cn.hutool.core.lang.Snowflake;

import static com.baomidou.mybatisplus.core.toolkit.IdWorker.getId;

@Service
public class TBusVehicleTransferServiceImpl implements TBusVehicleTransferService {

    private static final MicroLogger LOGGER = new MicroLogger(TBusVehicleTransferServiceImpl.class);

    @Resource
    private TBusVehicleTransferMapper tBusVehicleTransferMapper;
    @Resource
    private BusinessCommonService businessCommonService;
    @Resource
    private Snowflake snowflake;
    @Resource
    private CommonService commonService;
    @Resource
    private CommonMapper commonMapper;
    @Resource
    private SecurityUtils securityUtils;
    @Resource
	private TBusTrustCargoMapper tBusTrustCargoMapper;
    @Resource
	private TBusRateMapper tBusRateMapper;
    @Resource
	private MiscBillingMapper miscBillingMapper;
    @Autowired
    private TCustomsMapper tCustomsMapper;
    @Resource
    private TBusTrustMapper tBusTrustMapper;


    private static final String YES_NO_YES = "1";
	private static final String RATE_STATUS_YES = "10";
	private static final String RATE_ITEM_GKBG = "02"; // 港口包干费
	private static final String PROCESS_CODE_TRANS = "1027"; // 作业过程-转运过磅
	private static final String PROCESS_CODE_DDZY = "1029"; // 作业过程-转运过磅

	private static final Integer MISC_STATUS = 10; // 杂项费审核状态-未发布

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TBusTrustCargoQueryDTO> getList(TBusTrustCargoQueryDTO searchDTO) {
    	Pages<TBusTrustCargoQueryDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tBusVehicleTransferMapper.getPage(searchDTO);
		});
		pages.getPages().forEach(item->{
			if(item.getIsRule()  == null || item.getIsRule().isEmpty()){
				item.setIsRule("1");
			}
		});
        return pages;
    }

	@Override
	public List<Map<String, Object>> getTrustCagroDispatchSecondary(Long trustId, Long trustCargoId) {
		return tBusVehicleTransferMapper.getTrustCagroDispatchSecondary(trustId, trustCargoId);
	}

	@Override
	@Transactional
	public void insertVehicleTransferList(TBusVehicleTransferDTO tBusVehicleTransferDTO) {


		TBusTrustCargoQueryDTO tBusTrustCargoQueryDTO = tBusVehicleTransferMapper.getListByTrustCargoId(tBusVehicleTransferDTO.getTrustCargoId());
		if(tBusTrustCargoQueryDTO.getStartTime() == null || tBusTrustCargoQueryDTO.getEndTime() == null){
			throw new BusinessRuntimeException("请联系市场营销中心配置计划的开始结束时间");
		}
		//如果等于1  直接添加
		if("1".equals(tBusVehicleTransferDTO.getFlag())) {
			if(CollectionUtils.isNotEmpty(tBusVehicleTransferDTO.getBusVehicleTransferList())) {
				for (TBusVehicleTransferPO tBusVehicleTransferPO : tBusVehicleTransferDTO.getBusVehicleTransferList()) {
					tBusVehicleTransferPO.setId(snowflake.nextId());
					tBusVehicleTransferPO.setTrustCargoId(tBusVehicleTransferDTO.getTrustCargoId());
					tBusVehicleTransferPO.setTrustId(tBusVehicleTransferDTO.getTrustId());
					tBusVehicleTransferPO.setStatus(tBusVehicleTransferPO.getStatus());
					tBusVehicleTransferPO.setDelFlag(0L);
					tBusVehicleTransferPO.setCreateBy(securityUtils.getLoginUserId());
					tBusVehicleTransferPO.setCreateByName(securityUtils.getLoginUserName());
					tBusVehicleTransferPO.setCreateTime(new Date());

					//获取主列表数据
					List<TBusTrustCargoQueryDTO> allList = tBusVehicleTransferMapper.getAllList();
					//过滤主列表数据  获取状态为开启的数据
					List<TBusTrustCargoQueryDTO> trustCargoList=allList.stream().filter(x->
							(!x.getTrustCargoId().equals(tBusVehicleTransferDTO.getTrustCargoId())) && (x.getStatus() ==1L)
					).collect(Collectors.toList());
					//获取主列表为开启状态的派车数据
					trustCargoList.forEach(x->{
						List<TBusVehicleTransferDTO> vehicleList1 = tBusVehicleTransferMapper.getVehicleList(x.getTrustId(), x.getTrustCargoId());
						//获取状态为开启的数据
						List<TBusVehicleTransferDTO> vehicleList = vehicleList1.stream().filter(z ->
								z.getStatus() == 1L
						).collect(Collectors.toList());
						//判断
						for(TBusVehicleTransferDTO tBusVehicleTransferDTO1 :vehicleList){
							if(tBusVehicleTransferDTO1.getEquipmentId().equals(tBusVehicleTransferPO.getEquipmentId())){
								throw new BusinessRuntimeException(tBusVehicleTransferPO.getEquipmentNo()+"机械编号已存在"+x.getBusinessNo() + "计划中");
							}
						}
					});
					tBusVehicleTransferMapper.insertVehicleTransfer(tBusVehicleTransferPO);

					//判断该计划是否开启
					if(tBusTrustCargoQueryDTO.getStatus() == 1L){
						//判断该车在其他计划下是否存在未完成的磅单
						int count = tBusVehicleTransferMapper.getBoundIsFinshedByIdAndNox(tBusVehicleTransferPO.getEquipmentNo(),tBusVehicleTransferDTO.getTrustCargoId());
						if(count>0) {
							throw new BusinessRuntimeException(tBusVehicleTransferPO.getEquipmentNo() + "存在未完成的过磅记录，请先联系磅房作废");
						}
					}


				}

				addCustomsCarList(tBusVehicleTransferDTO.getBusVehicleTransferList(), tBusVehicleTransferDTO);
			}
		}else{

				List<TBusVehicleTransferDTO> vehicleList = tBusVehicleTransferMapper.getVehicleList(tBusVehicleTransferDTO.getTrustId(), tBusVehicleTransferDTO.getTrustCargoId());
				for (TBusVehicleTransferPO vehicle : vehicleList) {
					boolean flag = false;
					//重写equals 比较id
					for (TBusVehicleTransferPO tBusVehicleTransferPO : tBusVehicleTransferDTO.getBusVehicleTransferList()) {
						if (vehicle.getId().equals(tBusVehicleTransferPO.getId())) {
							flag = true;
						}
					}
					if(!flag){
						Integer poundCOunt = tBusVehicleTransferMapper.getPoundInfo(vehicle);
						if(poundCOunt>0){
							throw new BusinessRuntimeException(vehicle.getEquipmentNo()+"已存在过磅数据，无法删除");
						}
						vehicle.setDelFlag(1L);
						//逻辑删除
						tBusVehicleTransferMapper.updateDelFlagVehicle(vehicle);
						continue;
					}else{
						flag = false;
					}
				}

			if(CollectionUtils.isNotEmpty(tBusVehicleTransferDTO.getBusVehicleTransferList())) {

				addCustomsCarList(tBusVehicleTransferDTO.getBusVehicleTransferList(), tBusVehicleTransferDTO);

				for (TBusVehicleTransferPO tBusVehicleTransferPO : tBusVehicleTransferDTO.getBusVehicleTransferList()) {
					if (tBusVehicleTransferPO.getId() != null) {
						tBusVehicleTransferPO.setId(tBusVehicleTransferPO.getId());
						tBusVehicleTransferPO.setTrustCargoId(tBusVehicleTransferDTO.getTrustCargoId());
						tBusVehicleTransferPO.setTrustId(tBusVehicleTransferDTO.getTrustId());
						tBusVehicleTransferPO.setStatus(tBusVehicleTransferPO.getStatus());
						tBusVehicleTransferPO.setDelFlag(tBusVehicleTransferPO.getDelFlag());
						tBusVehicleTransferPO.setCreateBy(securityUtils.getLoginUserId());
						tBusVehicleTransferPO.setCreateByName(securityUtils.getLoginUserName());
						tBusVehicleTransferPO.setCreateTime(new Date());

						tBusVehicleTransferMapper.updateVehicleTransfer(tBusVehicleTransferPO);

					} else {
						tBusVehicleTransferPO.setId(snowflake.nextId());
						tBusVehicleTransferPO.setTrustCargoId(tBusVehicleTransferDTO.getTrustCargoId());
						tBusVehicleTransferPO.setTrustId(tBusVehicleTransferDTO.getTrustId());
						tBusVehicleTransferPO.setStatus(tBusVehicleTransferPO.getStatus());
						tBusVehicleTransferPO.setDelFlag(0L);
						tBusVehicleTransferPO.setCreateBy(securityUtils.getLoginUserId());
						tBusVehicleTransferPO.setCreateByName(securityUtils.getLoginUserName());
						tBusVehicleTransferPO.setCreateTime(new Date());

						List<TBusTrustCargoQueryDTO> allList = tBusVehicleTransferMapper.getAllList();
						List<TBusTrustCargoQueryDTO> trustCargoList=allList.stream().filter(x->
								(!x.getTrustCargoId().equals(tBusVehicleTransferDTO.getTrustCargoId())) && (x.getStatus() ==1L)
						).collect(Collectors.toList());
						trustCargoList.forEach(x->{
							List<TBusVehicleTransferDTO> vehicleList2 = tBusVehicleTransferMapper.getVehicleList(x.getTrustId(), x.getTrustCargoId());
							for(TBusVehicleTransferDTO tBusVehicleTransferDTO1 :vehicleList2){
								if(tBusVehicleTransferDTO1.getEquipmentId().equals(tBusVehicleTransferPO.getEquipmentId())){
									throw new BusinessRuntimeException(tBusVehicleTransferPO.getEquipmentNo()+"机械编号已存在"+x.getBusinessNo() + "计划中");

								}
							}
						});
						tBusVehicleTransferMapper.insertVehicleTransfer(tBusVehicleTransferPO);
						//判断该计划是否开启
						if(tBusTrustCargoQueryDTO.getStatus() == 1L){
							//判断该车在其他计划下是否存在未完成的磅单
							int count = tBusVehicleTransferMapper.getBoundIsFinshedByIdAndNox(tBusVehicleTransferPO.getEquipmentNo(),tBusVehicleTransferDTO.getTrustCargoId());
							if(count>0) {
								throw new BusinessRuntimeException(tBusVehicleTransferDTO.getEquipmentNo() + "存在未完成的过磅记录，请先联系磅房作废");
							}
						}
					}
				}
			}

			// 删除已选择倒运设备信息
/*
			tBusVehicleTransferMapper.deleteVehicleTransferList(tBusVehicleTransferDTO.getTrustId(), tBusVehicleTransferDTO.getTrustCargoId());
*/
		}
		/*if(CollectionUtils.isNotEmpty(tBusVehicleTransferDTO.getBusVehicleTransferList())) {
			for (TBusVehicleTransferPO tBusVehicleTransferPO : tBusVehicleTransferDTO.getBusVehicleTransferList()) {
				tBusVehicleTransferPO.setId(snowflake.nextId());
				tBusVehicleTransferPO.setTrustCargoId(tBusVehicleTransferDTO.getTrustCargoId());
				tBusVehicleTransferPO.setTrustId(tBusVehicleTransferDTO.getTrustId());
				tBusVehicleTransferPO.setStatus(tBusVehicleTransferPO.getStatus());
				tBusVehicleTransferPO.setCreateBy(securityUtils.getLoginUserId());
				tBusVehicleTransferPO.setCreateByName(securityUtils.getLoginUserName());
				tBusVehicleTransferPO.setCreateTime(new Date());

				tBusVehicleTransferMapper.insertVehicleTransfer(tBusVehicleTransferPO);
			}

		}*/
	}

    private void addCustomsCarList(List<TBusVehicleTransferPO> dataList, TBusVehicleTransferDTO tBusVehicleTransferDTO) {
		try {
			for (TBusVehicleTransferPO data : dataList) {
				// 判断是否视外贸
				TBusTrustDTO busTrustDTO = tBusTrustMapper.getById(tBusVehicleTransferDTO.getTrustId());

				if(busTrustDTO != null && "内贸".equals(busTrustDTO.getTradeType())) {
					TBusTrustCargoDTO busTrustCargoDTO = tBusTrustCargoMapper.getById(data.getTrustCargoId());
					if(busTrustCargoDTO != null) {
						TBusCargoInfoDTO tBusCargoInfoDTO = tCustomsMapper.getCargoIngnById(busTrustCargoDTO.getCargoInfoId());
						if(tBusCargoInfoDTO != null) {
							TosPlanDTO tosPlanDTO = new TosPlanDTO();
							tosPlanDTO.setAutoId(snowflake.nextIdStr());
							tosPlanDTO.setScn(tBusCargoInfoDTO.getScn());
							tosPlanDTO.setBizType("MY02");
							tosPlanDTO.setIcCard(data.getEquipmentNo());
							tosPlanDTO.setTrafName(StringUtils.isNotBlank(tBusCargoInfoDTO.getShipName())?tBusCargoInfoDTO.getShipName():"*");
							tosPlanDTO.setTrafCode(tBusCargoInfoDTO.getShipvoyageId() == null?"*":tBusCargoInfoDTO.getShipvoyageId()+"");
							tosPlanDTO.setVoyageNo(tBusCargoInfoDTO.getVoyage());
							tosPlanDTO.setTradeName(tBusCargoInfoDTO.getCargoOwnerName());
							tosPlanDTO.setGoodsName(busTrustCargoDTO.getCargoName());
							tosPlanDTO.setCarNo(data.getEquipmentNo());
							tosPlanDTO.setGoodsWt(BigDecimal.ZERO);
							tosPlanDTO.setGrossWt(BigDecimal.ZERO);
							tosPlanDTO.setTareWt(BigDecimal.ZERO);
							tosPlanDTO.setIsinvalid("0");
							tosPlanDTO.setAreaCode("JN30");
							tosPlanDTO.setCustomsCode("4310");
							tosPlanDTO.setInformNo(busTrustCargoDTO.getBusinessNo());
							tosPlanDTO.setDeclareDate(new Date());

							tCustomsMapper.insert(tosPlanDTO);
						}
					}
				}
			}
		} catch (Exception e){
			LOGGER.error(e.getMessage());
		}
	}

	@Override
	public List<TBusVehicleTransferDTO> getVehicleTransferList(Long trustCargoId) {

		List<TBusVehicleTransferDTO> resultList = tBusVehicleTransferMapper.getVehicleTransferList(trustCargoId);

		if(CollectionUtils.isNotEmpty(resultList)) {
			List<Long> ids = resultList.stream().map(TBusVehicleTransferDTO::getId).collect(Collectors.toList());

			// 查询趟数
			List<Map<String, Object>> countList = tBusVehicleTransferMapper.getVehicleTransferCountList(ids);
			/*for (TBusVehicleTransferDTO data : resultList) {
				for (Map<String, Object> count : countList) {
					if(data.getId().toString().equals(count.get("tsptId").toString())) {
						data.setCarCount(Integer.parseInt(count.get("count").toString()));
						break;
					}
				}
			}*/
			for (TBusVehicleTransferDTO data : resultList) {
				if (data != null && data.getId() != null) {
					for (Map<String, Object> count : countList) {
						if (count != null && count.get("tsptId") != null &&
								data.getId().toString().equals(count.get("tsptId").toString())) {
							if (count.get("count") != null) {
								data.setCarCount(Integer.parseInt(count.get("count").toString()));
							} else {
								data.setCarCount(0);
							}
							break;
						}
					}
				}
			}

			// 查询吨数
			List<Map<String, Object>> tonsList = tBusVehicleTransferMapper.getVehicleTransferTonsList(ids);
			// 组织数据、 以及当前状态
			for (TBusVehicleTransferDTO data : resultList) {
				for (Map<String, Object> tons : tonsList) {
					if (data != null && data.getId() != null &&
							tons != null && tons.get("tsptId") != null) {
						if (data.getId().toString().equals(tons.get("tsptId").toString())) {
							if (tons.get("count") != null) {
								data.setTons(new BigDecimal(tons.get("count").toString()));
							} else {
								data.setTons(BigDecimal.ZERO);
							}
							break;
						}
					}
				}
			}
		}

		return resultList;
	}

    @Override
	public void updateInnertransportType(Long id, String innertransportType) {
		tBusVehicleTransferMapper.updateInnertransportType(id, innertransportType);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public int changeStatus(TBusTrustCargoDTO tBusTrustCargoDTO) {
		if(tBusTrustCargoDTO.getStatus().equals("1")) {
			List<TBusTrustCargoQueryDTO> allList = tBusVehicleTransferMapper.getAllList();
			List<TBusTrustCargoQueryDTO> trustCargoList = allList.stream().filter(x ->
					x.getStatus() == 1L
			).collect(Collectors.toList());
			trustCargoList.forEach(x -> {
				List<TBusVehicleTransferDTO> vehicleList1 = tBusVehicleTransferMapper.getVehicleList(x.getTrustId(), x.getTrustCargoId());
				List<TBusVehicleTransferDTO> vehicleList = vehicleList1.stream().filter(y ->
						y.getStatus() == 1L
				).collect(Collectors.toList());
				List<TBusVehicleTransferDTO> stopVehicleList1 = tBusVehicleTransferMapper.getVehicleList(tBusTrustCargoDTO.getTrustId(), tBusTrustCargoDTO.getTrustCargoId());
				List<TBusVehicleTransferDTO> stopVehicleList = stopVehicleList1.stream().filter(z ->
						z.getStatus() == 1L
				).collect(Collectors.toList());
				for (TBusVehicleTransferDTO tBusVehicleTransferDTO1 : vehicleList) {
					for (TBusVehicleTransferDTO stopVehicleTransferDTO : stopVehicleList) {
						if (tBusVehicleTransferDTO1.getEquipmentId().equals(stopVehicleTransferDTO.getEquipmentId())) {
							throw new BusinessRuntimeException(stopVehicleTransferDTO.getEquipmentNo() + "机械编号已存在"+x.getBusinessNo() + "计划中");
						}
					}
				}
			});
			//获取车辆数据
			List<TBusVehicleTransferDTO> vehicleList1 = tBusVehicleTransferMapper.getVehicleList(tBusTrustCargoDTO.getTrustId(), tBusTrustCargoDTO.getTrustCargoId());
			//获取车辆数据状态为开启的
			List<TBusVehicleTransferDTO> vehicleList2 = vehicleList1.stream().filter(x->x.getStatus() ==1L).collect(Collectors.toList());
			vehicleList2.forEach(x->{
				//查询地磅表
				int count = tBusVehicleTransferMapper.getBoundIsFinshedByIdAndNox(x.getEquipmentNo(),x.getTrustCargoId());
				if(count>0) {
					throw new BusinessRuntimeException(x.getEquipmentNo() + "存在未完成的过磅记录，请先联系磅房作废");
				}
			});
			TBusTrustDTO trustDto = tBusVehicleTransferMapper.getTrustByTrustId(tBusTrustCargoDTO.getTrustId());

			// 启用时删除已生成的二次倒运费
			List<TMiscBillingDTO> ddzyFeeList =
						miscBillingMapper.getListByStatementIdAndRate(tBusTrustCargoDTO.getTrustCargoId(), PROCESS_CODE_DDZY);

			List<TMiscBillingDTO>	transFeeList =
						miscBillingMapper.getListByStatementIdAndRate(tBusTrustCargoDTO.getTrustCargoId(), PROCESS_CODE_TRANS);
			if(CollectionUtils.isNotEmpty(ddzyFeeList)){
				ddzyFeeList.addAll(transFeeList);
			}else {
				ddzyFeeList = transFeeList;
			}

			if (!CollectionUtils.isEmpty(ddzyFeeList)) {
				ddzyFeeList.stream().forEach(item -> {
					if (!MISC_STATUS.equals(item.getStatus())) {
						throw new BusinessRuntimeException("杂项费用已审核，请先联系计费中心撤销审核");
					}
					miscBillingMapper.deleteMisc(item.getId());
				});
			}
		} else {
			// 停用时根据过磅量生成二次倒运费
			if (StringUtils.isNotBlank(tBusTrustCargoDTO.getIsSecondWeigh())
					&& tBusTrustCargoDTO.getWeighTon() != null
					&& YES_NO_YES.equals(tBusTrustCargoDTO.getIsSecondWeigh())
					&& tBusTrustCargoDTO.getWeighTon().compareTo(BigDecimal.ZERO) > 0) {
				TBusCargoInfoDTO trustCargo =
						tBusVehicleTransferMapper.getCargoByTrustCargoId(tBusTrustCargoDTO.getTrustCargoId());
				if(trustCargo==null){
					throw new BusinessRuntimeException("没有找到相关的票货信息");
				}
				TBusRateSearchDTO searchDTO = new TBusRateSearchDTO();
				TBusTrustDTO trustDto = tBusVehicleTransferMapper.getTrustByTrustId(tBusTrustCargoDTO.getTrustId());
				if(trustDto==null){
					throw new BusinessRuntimeException("未找到通知单数据");
				}

				tBusTrustCargoDTO.getFeeList().forEach(item->{
					if("倒运费".equals(item)){
						searchDTO.setStatus(RATE_STATUS_YES);
						searchDTO.setRateItemCode(RATE_ITEM_GKBG);
						searchDTO.setProcessCode(PROCESS_CODE_DDZY);
						searchDTO.setCurrTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd"));

						List<TBusRateDTO> rateList = tBusRateMapper.getBusRateList(searchDTO);
						if (!org.springframework.util.CollectionUtils.isEmpty(rateList)) {
							if (rateList.size() != 1) {
								throw new BusinessRuntimeException("匹配到多个费率，无法计算费用");
							}
							TBusRateDTO tBusRateDTO = rateList.get(0);
							TMiscBillingPO miscBillingPO = new TMiscBillingPO();
							miscBillingPO.setId(snowflake.nextId());
							miscBillingPO.setVehicleTransferFlag("1");
							miscBillingPO.setRateItemCode(tBusRateDTO.getRateItemCode());
							miscBillingPO.setRateName(tBusRateDTO.getRateItemName());
							miscBillingPO.setRate(tBusRateDTO.getRate());
							miscBillingPO.setTaxRate(tBusRateDTO.getTaxRate());
							miscBillingPO.setBillDate(new Date());
							miscBillingPO.setVoyageId(trustDto.getShipvoyageItemId());
							String shipNameVoyage = trustDto.getShipNameVoyage();
/*					if (StringUtils.isBlank(shipNameVoyage)&&(StringUtils.isNotBlank(trustCargo.getShipName()) && StringUtils.isNotBlank(trustCargo.getVoyage()))) {
						shipNameVoyage = trustCargo.getShipName() + "_" + trustCargo.getVoyage();
					}*/
							if("3".equals(trustDto.getTrustType())||StringUtils.isBlank(shipNameVoyage)){

								List<ShipNameVoyageResultDTO> shipNameVoyageList =  tBusVehicleTransferMapper.getShipNameVoyage(tBusTrustCargoDTO.getTrustCargoId());
								if (CollectionUtils.isNotEmpty(shipNameVoyageList)&&shipNameVoyageList.size()==1){
									shipNameVoyage = shipNameVoyageList.get(0).getShipNameVoyage().split(",").length>1?"":shipNameVoyageList.get(0).getShipNameVoyage();
									miscBillingPO.setVoyageId(shipNameVoyageList.get(0).getShipvoyageItemId().split(",").length>1?null:Long.parseLong(shipNameVoyageList.get(0).getShipvoyageItemId()));
								}
							}
							miscBillingPO.setShipVoyage(shipNameVoyage);
							miscBillingPO.setBillQuantity(tBusTrustCargoDTO.getWeighTon());
							miscBillingPO.setAmountMoney(tBusTrustCargoDTO.getWeighTon().multiply(tBusRateDTO.getRate()));
							BigDecimal taxRate = tBusRateDTO.getTaxRate().divide(new BigDecimal("100"));
							miscBillingPO.setTaxAmount(miscBillingPO.getAmountMoney()
									.divide(taxRate.add(new BigDecimal("1")),9,BigDecimal.ROUND_HALF_UP)
									.multiply(taxRate).setScale(2, BigDecimal.ROUND_HALF_UP));
							miscBillingPO.setCustomerId(trustCargo.getCargoOwnerId());
							miscBillingPO.setStatus(MISC_STATUS);
							miscBillingPO.setCompanyId(trustCargo.getCompanyId());
							miscBillingPO.setCompanyName(trustCargo.getCompanyName());
							miscBillingPO.setRateId(tBusRateDTO.getId());
							miscBillingPO.setUnitCode(tBusRateDTO.getMeasurementUnitCode1());
							miscBillingPO.setUnitName(tBusRateDTO.getMeasurementUnitName1());
							miscBillingPO.setProcessCode(tBusRateDTO.getProcessCode());
							miscBillingPO.setProcessName(tBusRateDTO.getProcessName());
							miscBillingPO.setCargoInfoId(trustCargo.getId());
							miscBillingPO.setCargoInfoName(trustCargo.getCargoInfoNo() + "-"
									+ trustCargo.getCargoName() + "-" + trustCargo.getTradeType());
							miscBillingPO.setOtherStatementId(tBusTrustCargoDTO.getTrustCargoId());
							miscBillingMapper.addMiscBilling(miscBillingPO);
						} else {
							throw new BusinessRuntimeException("未匹配到费率，无法计算费用");
						}


					}else if("过磅费".equals(item)){
						searchDTO.setStatus(RATE_STATUS_YES);
						searchDTO.setRateItemCode(RATE_ITEM_GKBG);
						searchDTO.setProcessCode(PROCESS_CODE_TRANS);
						searchDTO.setCurrTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd")); // 卸船取靠泊时间，集港取首次过磅时间？？？
						List<TBusRateDTO> rateList = tBusRateMapper.getBusRateList(searchDTO);
						if (!org.springframework.util.CollectionUtils.isEmpty(rateList)) {
							if (rateList.size() != 1) {
								throw new BusinessRuntimeException("匹配到多个费率，无法计算费用");
							}
							TBusRateDTO tBusRateDTO = rateList.get(0);
							TMiscBillingPO miscBillingPO = new TMiscBillingPO();
							miscBillingPO.setId(snowflake.nextId());
							miscBillingPO.setVehicleTransferFlag("1");
							miscBillingPO.setRateItemCode(tBusRateDTO.getRateItemCode());
							miscBillingPO.setRateName(tBusRateDTO.getRateItemName());
							miscBillingPO.setRate(tBusRateDTO.getRate());
							miscBillingPO.setTaxRate(tBusRateDTO.getTaxRate());
							miscBillingPO.setBillDate(new Date());
							miscBillingPO.setVoyageId(trustDto.getShipvoyageItemId());
							String shipNameVoyage = trustDto.getShipNameVoyage();
/*					if (StringUtils.isBlank(shipNameVoyage)&&(StringUtils.isNotBlank(trustCargo.getShipName()) && StringUtils.isNotBlank(trustCargo.getVoyage()))) {
						shipNameVoyage = trustCargo.getShipName() + "_" + trustCargo.getVoyage();
					}*/
							if("3".equals(trustDto.getTrustType())||StringUtils.isBlank(shipNameVoyage)){

								List<ShipNameVoyageResultDTO> shipNameVoyageList =  tBusVehicleTransferMapper.getShipNameVoyage(tBusTrustCargoDTO.getTrustCargoId());
								if (CollectionUtils.isNotEmpty(shipNameVoyageList)&&shipNameVoyageList.size()==1){
									shipNameVoyage = shipNameVoyageList.get(0).getShipNameVoyage().split(",").length>1?"":shipNameVoyageList.get(0).getShipNameVoyage();
									miscBillingPO.setVoyageId(shipNameVoyageList.get(0).getShipvoyageItemId().split(",").length>1?null:Long.parseLong(shipNameVoyageList.get(0).getShipvoyageItemId()));
								}
							}
							miscBillingPO.setShipVoyage(shipNameVoyage);
							miscBillingPO.setBillQuantity(tBusTrustCargoDTO.getWeighTon());
							miscBillingPO.setAmountMoney(tBusTrustCargoDTO.getWeighTon().multiply(tBusRateDTO.getRate()));
							BigDecimal taxRate = tBusRateDTO.getTaxRate().divide(new BigDecimal("100"));
							miscBillingPO.setTaxAmount(miscBillingPO.getAmountMoney()
									.divide(taxRate.add(new BigDecimal("1")),9,BigDecimal.ROUND_HALF_UP)
									.multiply(taxRate).setScale(2, BigDecimal.ROUND_HALF_UP));
							miscBillingPO.setCustomerId(trustCargo.getCargoOwnerId());
							miscBillingPO.setStatus(MISC_STATUS);
							miscBillingPO.setCompanyId(trustCargo.getCompanyId());
							miscBillingPO.setCompanyName(trustCargo.getCompanyName());
							miscBillingPO.setRateId(tBusRateDTO.getId());
							miscBillingPO.setUnitCode(tBusRateDTO.getMeasurementUnitCode1());
							miscBillingPO.setUnitName(tBusRateDTO.getMeasurementUnitName1());
							miscBillingPO.setProcessCode(tBusRateDTO.getProcessCode());
							miscBillingPO.setProcessName(tBusRateDTO.getProcessName());
							miscBillingPO.setCargoInfoId(trustCargo.getId());
							miscBillingPO.setCargoInfoName(trustCargo.getCargoInfoNo() + "-"
									+ trustCargo.getCargoName() + "-" + trustCargo.getTradeType());
							miscBillingPO.setOtherStatementId(tBusTrustCargoDTO.getTrustCargoId());
							miscBillingMapper.addMiscBilling(miscBillingPO);
						} else {
							throw new BusinessRuntimeException("未匹配到费率，无法计算费用");
						}
					}
				});
/*
				if("3".equals(trustDto.getTrustType())){
					//倒运类型的自动匹配
					searchDTO.setStatus(RATE_STATUS_YES);
					searchDTO.setRateItemCode(RATE_ITEM_GKBG);
					searchDTO.setProcessCode(PROCESS_CODE_DDZY);
					searchDTO.setCurrTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
				}else{
					searchDTO.setStatus(RATE_STATUS_YES);
					searchDTO.setRateItemCode(RATE_ITEM_GKBG);
					searchDTO.setProcessCode(PROCESS_CODE_TRANS);
					searchDTO.setCurrTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd")); // 卸船取靠泊时间，集港取首次过磅时间？？？
				}*/
			}
		}
		return tBusVehicleTransferMapper.changeStatus(tBusTrustCargoDTO);
	}

	@Override
	public List<TBusVehicleTransferDTO> getVehicleList(Long trustId, Long trustCargoId) {
		return tBusVehicleTransferMapper.getVehicleList(trustId,trustCargoId);
	}

	@Override
	public int changeDetailStatus(TBusVehicleTransferDTO tBusVehicleTransferDTO) {
    	if(tBusVehicleTransferDTO.getId()==null){
    		throw new BusinessRuntimeException("没有选择数据");
		}
		if(tBusVehicleTransferDTO.getStatus() == 1L) {
			//查询主列表数据
			List<TBusTrustCargoQueryDTO> allList = tBusVehicleTransferMapper.getAllList();
			//得到开启状态的数据 不包括自己
			List<TBusTrustCargoQueryDTO> trustCargoList = allList.stream().filter(x ->
					x.getStatus() == 1L && x.getTrustCargoId().compareTo(tBusVehicleTransferDTO.getTrustCargoId())!=0
			).collect(Collectors.toList());
			trustCargoList.forEach(x -> {
				//得到所有主列表状态为开启的配工数据
				List<TBusVehicleTransferDTO> vehicleList1 = tBusVehicleTransferMapper.getVehicleList(x.getTrustId(), x.getTrustCargoId());
				//得到状态为开启的配工数据
				List<TBusVehicleTransferDTO> vehicleList = vehicleList1.stream().filter(y ->
						y.getStatus() == 1L
				).collect(Collectors.toList());
				//查询当前数据的配工数据
				List<TBusVehicleTransferDTO> stopVehicleList = tBusVehicleTransferMapper.getVehicleList(tBusVehicleTransferDTO.getTrustId(), tBusVehicleTransferDTO.getTrustCargoId());
				for (TBusVehicleTransferDTO tBusVehicleTransferDTO1 : vehicleList) {
					for (TBusVehicleTransferDTO stopVehicleTransferDTO : stopVehicleList) {
						if (tBusVehicleTransferDTO.getId().equals(stopVehicleTransferDTO.getId())&& tBusVehicleTransferDTO1.getEquipmentId().equals(stopVehicleTransferDTO.getEquipmentId())) {
							throw new BusinessRuntimeException(stopVehicleTransferDTO.getEquipmentNo() + "机械编号已存在"+x.getBusinessNo() + "计划中");
						}
					}
				}
			});
			TBusTrustCargoQueryDTO tBusTrustCargoQueryDTO = tBusVehicleTransferMapper.getListByTrustCargoId(tBusVehicleTransferDTO.getTrustCargoId());
			if(tBusTrustCargoQueryDTO.getStatus() == 1L) {
				int count = tBusVehicleTransferMapper.getBoundIsFinshedByIdAndNox(tBusVehicleTransferDTO.getEquipmentNo(), tBusVehicleTransferDTO.getTrustCargoId());
				if (count > 0) {
					throw new BusinessRuntimeException(tBusVehicleTransferDTO.getEquipmentNo() + "存在未完成的过磅记录，请先联系磅房作废");
				}
			}
		}
		return tBusVehicleTransferMapper.changeDetailStatus(tBusVehicleTransferDTO);
	}

	@Override
	@Transactional
	public boolean doSave(TBusTrustCargoDTO tBusTrustCargoDTO) {
    	int count = 0;
    	if(tBusTrustCargoDTO.getWorkAreaCd() != null){
			count = tBusVehicleTransferMapper.updateTrustPort(tBusTrustCargoDTO);
			if(count !=1){
				throw new BusinessRuntimeException("更新港区失败");
			}
		}
    	if(tBusTrustCargoDTO.getTransportType().equals("2")){
			if(!tBusTrustCargoDTO.getInnertransportType().equals("1")){
				throw new BusinessRuntimeException("先重后轻，请选择一车一皮");
			}
		}
			return tBusVehicleTransferMapper.update(tBusTrustCargoDTO) == 1;
    }

	@Override
	public TBusTrustCargoDTO getEmptyHeavy(String cargoCode) {
		TBusTrustCargoDTO emptyHeavy = tBusVehicleTransferMapper.getEmptyHeavy(cargoCode);
		if(ObjectUtils.isEmpty(emptyHeavy)){
			throw new BusinessRuntimeException("未查询到空车重量和重车重量");
		}
		return emptyHeavy;
	}
	@Override
	public TBusTrustCargoDTO getTrustCargoById(Long id) {
		return tBusTrustCargoMapper.getById(id);
	}

	@Override
	public List<TMiscBillingDTO> getMiscFeeList(Long trustCargoId) {
		return tBusVehicleTransferMapper.getMiscFeeList(trustCargoId);
	}

}
