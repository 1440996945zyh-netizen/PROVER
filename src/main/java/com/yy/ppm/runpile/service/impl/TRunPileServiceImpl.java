package com.yy.ppm.runpile.service.impl;

import java.math.BigDecimal;
import java.util.*;

import jakarta.annotation.Resource;

import com.yy.common.util.geojson.GeoJsonUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.produce.bean.SyncDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Lists;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.common.service.PublicService;
import com.yy.ppm.machine.bean.dto.TMacTerminalStackPositionDTO;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionDTO;
import com.yy.ppm.runpile.bean.dto.TRunPilePortStorageDetailDTO;
import com.yy.ppm.runpile.bean.dto.TStorageYardDTO;
import com.yy.ppm.runpile.bean.po.MStorageStackPositionPO;
import com.yy.ppm.runpile.mapper.TRunPileMapper;
import com.yy.ppm.runpile.service.TRunPileService;

import cn.hutool.core.lang.Snowflake;


@Service
public class TRunPileServiceImpl implements TRunPileService {

    @Resource
    private TRunPileMapper tRunPileMapper;

    @Resource
    private PublicService publicService;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

	@Override
	public List<TStorageYardDTO> getStorageYardList(String storageYardLevel, String parentId) {
		return tRunPileMapper.getStorageYardList(storageYardLevel, parentId);
	}

	@Override
	public List<TMacTerminalStackPositionDTO> getStackPositionList() {
		return tRunPileMapper.getStackPositionList();
	}

	@Override
	@Transactional
	public int saveStackPosition(MStorageStackPositionDTO storageStackPositionDTO) {

		MStorageStackPositionPO storageStackPositionPO = new MStorageStackPositionPO();
		storageStackPositionPO.setId(snowflake.nextId());
		storageStackPositionPO.setStackId(storageStackPositionDTO.getStackId());
		storageStackPositionPO.setStackCode(storageStackPositionDTO.getStackCode());
		storageStackPositionPO.setStackName(storageStackPositionDTO.getStackName());
		storageStackPositionPO.setPositionFrom("1");
		storageStackPositionPO.setPositionTime(new Date());

		String position = "";
		if(CollectionUtils.isNotEmpty(storageStackPositionDTO.getStorageStackPositionList())) {
			String[][] positionArr = new String[storageStackPositionDTO.getStorageStackPositionList().size() + 1][];
			for (int i = 0; i < storageStackPositionDTO.getStorageStackPositionList().size(); i++) {
				MStorageStackPositionPO data = storageStackPositionDTO.getStorageStackPositionList().get(i);
				positionArr[i] = new String[2];
				positionArr[i][0] = data.getStorageYardLon();
				positionArr[i][1] = data.getStorageYardLat();
			}

			MStorageStackPositionPO data = storageStackPositionDTO.getStorageStackPositionList().get(0);
			positionArr[storageStackPositionDTO.getStorageStackPositionList().size()] = new String[2];
			positionArr[storageStackPositionDTO.getStorageStackPositionList().size()][0] = data.getStorageYardLon();
			positionArr[storageStackPositionDTO.getStorageStackPositionList().size()][1] = data.getStorageYardLat();

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				position = objectMapper.writeValueAsString(positionArr);
				position = position.replaceAll("\"", "");
				storageStackPositionPO.setPosition(position);
			} catch (JsonProcessingException e) {
			}
		}
		isConflict(storageStackPositionPO);

		SyncDTO dto = new SyncDTO();
		dto.setId(snowflake.nextId());
		dto.setBizId(storageStackPositionDTO.getStackId());
		//dto.setBizType(BusSyncEnum.LIBRARY_PLACE.getCode());
		dto.setIsDelete("0");

		return tRunPileMapper.saveStackPosition(storageStackPositionPO);
	}

	/**
	 * 判断跑垛机建立的垛位区域是否与已存在的区域冲突
	 * @param
	 */
	public void isConflict(MStorageStackPositionPO storageStackPositionPO){
		String wktPolygon = formatToWktPolygon(storageStackPositionPO.getPosition());
		List<TMacTerminalStackPositionDTO> stackPositionList = tRunPileMapper.listByCondition(storageStackPositionPO);
		stackPositionList.forEach(e->{
            try {
				if(GeoJsonUtils.isConflict(wktPolygon,formatToWktPolygon(e.getPosition()))){
					throw new BusinessRuntimeException("垛位重合，请重新确定点位");
				}
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        });
	}

	private String formatToWktPolygon(String wktPolygon){
		List<String> list = new ArrayList<>(Arrays.asList(wktPolygon.replaceAll("\\[\\[","").replaceAll("]]","").split("],\\[")));
		if(CollectionUtils.isNotEmpty(list) && !list.get(0).equals(list.get(list.size()-1))){
			String firstStr = list.get(0);
			list.add(list.size(),firstStr);
			wktPolygon = "POLYGON ((";
			for (int i =0;i<list.size();i++) {
				String s = list.get(i);
				s = s.replaceAll(","," ");
				if(i==(list.size()-1)){
					wktPolygon+=s+"))";
				}else{
					wktPolygon +=s+",";
				}
			}
			return wktPolygon;
		}else{
			return wktPolygon.replaceAll(","," ")
					.replaceAll("] \\[",",")
					.replaceAll("] \\[",",")
					.replaceAll("\\[\\[","POLYGON ((")
					.replaceAll("]]","))");
		}
	}




	@Override
	@Transactional
	public int deleteStackPosition(MStorageStackPositionDTO storageStackPositionDTO) {
		return tRunPileMapper.updateStackPositionDelFlag(storageStackPositionDTO);
	}

	@Override
	public List<TRunPilePortStorageDetailDTO> getRunPileNeedList(Long massId, String runPileState) {

		List<TRunPilePortStorageDetailDTO> resList = Lists.newArrayList();

		// 查询基础数据
		Integer needRunpileQuantity = tRunPileMapper.getNeedRunpileQuantity();

		// 查询港存中所有的货垛、以及当前量（吨）
		List<TRunPilePortStorageDetailDTO> portStorageDetailList = tRunPileMapper.getPortStorageDetailList(massId);
		for (TRunPilePortStorageDetailDTO runPilePortStorageDetail : portStorageDetailList) {
			runPilePortStorageDetail.setRunPileState("10");
			// 判断哪些货垛没有跑垛
			if(runPilePortStorageDetail.getStackId() == null) {
				runPilePortStorageDetail.setRunPileState("30");
			}
		}

		// 判断当前量跟最后一次跑垛时的量的对比 超过5000的数据
		List<TRunPilePortStorageDetailDTO> prePortStorageDetailList = tRunPileMapper.getPrePortStorageDetailList(massId);
		for (TRunPilePortStorageDetailDTO runPilePortStorageDetail : portStorageDetailList) {
			for (TRunPilePortStorageDetailDTO runPilePortStorageDetail_pre : prePortStorageDetailList) {
				if(runPilePortStorageDetail.getMassId() == runPilePortStorageDetail_pre.getMassId()) {
					// 判断两个值相差多少
					BigDecimal ton1 = runPilePortStorageDetail.getTon() == null?BigDecimal.ZERO:runPilePortStorageDetail.getTon();
					BigDecimal ton2 = runPilePortStorageDetail_pre.getTon() == null?BigDecimal.ZERO:runPilePortStorageDetail_pre.getTon();

					BigDecimal absNum = ton1.subtract(ton2).abs();
					runPilePortStorageDetail.setChangeTon(absNum);
					if(absNum.compareTo(new BigDecimal(needRunpileQuantity)) >= 0) {
						runPilePortStorageDetail.setRunPileState("20");
					}
				}
			}
		}
		if(StringUtils.isNoneBlank(runPileState)) {
			for (TRunPilePortStorageDetailDTO data : portStorageDetailList) {
				if(runPileState.equals(data.getRunPileState())) {
					resList.add(data);
				}
			}
		} else {
			resList.addAll(portStorageDetailList);
		}

        Collections.sort(resList, (p1, p2) -> p2.getRunPileState().compareTo(p1.getRunPileState()));
		return resList;
	}
}
