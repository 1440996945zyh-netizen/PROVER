package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.mapper.TBusContractRateMapper;
import com.yy.ppm.business.mapper.TBusRateMapper;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.MCargoCategoryDTO;
import com.yy.ppm.master.bean.dto.MCargoCategorySearchDTO;
import com.yy.ppm.master.bean.dto.MCargoDTO;
import com.yy.ppm.master.bean.dto.MCargoSearchDTO;
import com.yy.ppm.master.bean.po.MCargoPO;
import com.yy.ppm.master.mapper.MCargoMapper;
import com.yy.ppm.master.service.MCargoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * (MCargoType)表服务实现类
 *
 * @author makejava
 * @date 2021-03-08 11:18:37
 */
@Service
public class MCargoServiceImpl implements MCargoService {

    /**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(MCargoServiceImpl.class);

	@Resource
	private TBusRateMapper tBusRateMapper;

	@Autowired
	private Snowflake snowflake;

    @Resource
    private MCargoMapper mCargoTypeMapper;

	@Resource
	private CommonService commonService;

	@Resource
	private CommonMapper commonMapper;

	@Resource
	private TBusContractRateMapper tBusContractRateMapper;

    @Override
	public Pages<MCargoCategoryDTO> getListCargoCategory(MCargoCategorySearchDTO mCargoTypeSearchDTO) {
		final String methodName = "getListCargoCategory";
		LOGGER.enter(methodName, "业务执行");

		Pages<MCargoCategoryDTO> pages = PageHelperUtils.limit(mCargoTypeSearchDTO, () -> {
            return mCargoTypeMapper.getListCargoCategory(mCargoTypeSearchDTO);
		});

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return pages;
	}


	@Override
	public MCargoCategoryDTO getCargoCategoryById(Long id) {
		final String methodName = "getCargoCategoryById";
		LOGGER.enter(methodName, "业务执行");

		MCargoCategoryDTO mCargoTypeDTO = mCargoTypeMapper.getCargoCategoryById(id);

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return mCargoTypeDTO;
	}


	@Override
	@Transactional
	public int saveCargoCategory(MCargoCategoryDTO mCargoCategoryPO) {
		final String methodName = "insertCargoCategory";
		LOGGER.enter(methodName, "业务执行");

		// 验证货种名称重复
		commonService.isRepeate("M_CARGO_CATEGORY", "CARGO_CATEGORY_NAME",
				mCargoCategoryPO.getCargoCategoryName(), StringUtil.getString(mCargoCategoryPO.getId()), "货种名称", null);

		int count = 0;

//		mCargoCategoryPO.setCargoCategoryCode(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.CARGO_CATEGORY, mCargoCategoryPO.getCargoTypeCode()));

		if (mCargoCategoryPO.getId() == null) {
			mCargoCategoryPO.setId(snowflake.nextId());
			mCargoCategoryPO.setCargoCategoryCode(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.CARGO_CATEGORY, mCargoCategoryPO.getCargoTypeCode()));
			count = mCargoTypeMapper.insertCargoCategory(mCargoCategoryPO);
		} else {
			count = mCargoTypeMapper.updateCargoCategory(mCargoCategoryPO);
		}

		return count;
	}

	@Override
	public int deleteCargoCategory(Long id) {
		final String methodName = "deleteCargoCategory";
		LOGGER.enter(methodName, "业务执行");

		MCargoCategoryDTO dto = mCargoTypeMapper.getCargoCategoryById(id);

		int count = 1;

		if (dto != null) {
			count = commonMapper.getCount("M_CARGO", "CARGO_CATEGORY_CODE", dto.getCargoCategoryCode());

			if (count > 0) {
				throw new BusinessRuntimeException("该货种下有货物不能删除~");
			}
			count = mCargoTypeMapper.deleteCargoCategory(id);
		}

		return count;
	}

	//↑ 货种操作
	//↓ 货物操作

	@Override
	public Pages<MCargoDTO> getListCargo(MCargoSearchDTO mCargoSearchDTO) {
		final String methodName = "getListCargo";
		LOGGER.enter(methodName, "业务执行");

		Pages<MCargoDTO> pages = PageHelperUtils.limit(mCargoSearchDTO, () -> {
			return mCargoTypeMapper.getListCargo(mCargoSearchDTO);
		});

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return pages;
	}


	@Override
	public MCargoDTO getCargoById(Long id) {
		final String methodName = "getCargoById";
		LOGGER.enter(methodName, "业务执行");

		MCargoDTO mCargoDTO = mCargoTypeMapper.getCargoById(id);

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return mCargoDTO;
	}

	@Override
	public MCargoDTO getDetailById(Long id) {
		final String methodName = "getCargoById";
		LOGGER.enter(methodName, "业务执行");

		MCargoDTO mCargoDTO = mCargoTypeMapper.getDetailById(id);

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return mCargoDTO;
	}


	@Override
	@Transactional
	public int insertCargo(MCargoPO mCargoPO) {
		final String methodName = "insertCargo";
		LOGGER.enter(methodName, "业务执行");

		int count = 0;

		// 验证货物名称重复
//		commonService.isRepeate("M_CARGO", "CARGO_NAME",
//				mCargoPO.getCargoName(), StringUtil.getString(mCargoPO.getId()), "货物名称", null);

		List<MCargoDTO> tmpList = mCargoTypeMapper.getCargoIsRepeate(mCargoPO);


		if(!CollectionUtils.isEmpty(tmpList)){

			Map<String,List<MCargoDTO>> tmpMap =
					tmpList.stream().collect(Collectors.groupingBy(o->{
						return o.getCargoName()+"/"+String.valueOf(o.getSign());
					}));
			if(!CollectionUtils.isEmpty(tmpMap.get(mCargoPO.getCargoName()+"/"+(StringUtils.isEmpty(mCargoPO.getSign())?"null":mCargoPO.getSign())))){

				List<MCargoDTO> mCargoDTOS = tmpMap.get(mCargoPO.getCargoName() + "/" + (StringUtils.isEmpty(mCargoPO.getSign())?"null":mCargoPO.getSign()));
				mCargoDTOS.stream().forEach(o->{
					if(!o.getId().equals(mCargoPO.getId())){
						throw new BusinessRuntimeException("货名和货物标识码重复");
					}
				});


			}
		}

		mCargoPO.setId(snowflake.nextId());
		mCargoPO.setCargoCode(mCargoTypeMapper.nextCargoCode(mCargoPO.getCargoCategoryCode()));
		count = mCargoTypeMapper.insertCargo(mCargoPO);

		return count;
	}


	@Override
	public int updateCargo(MCargoPO mCargoPO) {
		final String methodName = "updateCargo";
		LOGGER.enter(methodName, "业务执行");

//		// 验证货物名称重复
//		commonService.isRepeate("M_CARGO", "CARGO_NAME",
//				mCargoPO.getCargoName(), StringUtil.getString(mCargoPO.getId()),  "货物名称", null);
		List<MCargoDTO> tmpList = mCargoTypeMapper.getCargoIsRepeate(mCargoPO);


		if(!CollectionUtils.isEmpty(tmpList)){

			Map<String,List<MCargoDTO>> tmpMap =
			tmpList.stream().collect(Collectors.groupingBy(o->{
				return o.getCargoName()+"/"+String.valueOf(o.getSign());
			}));
			if(!CollectionUtils.isEmpty(tmpMap.get(mCargoPO.getCargoName()+"/"+(StringUtils.isEmpty(mCargoPO.getSign())?"null":mCargoPO.getSign())))){

				List<MCargoDTO> mCargoDTOS = tmpMap.get(mCargoPO.getCargoName() + "/" + (StringUtils.isEmpty(mCargoPO.getSign())?"null":mCargoPO.getSign()));
				mCargoDTOS.stream().forEach(o->{
					if(!o.getId().equals(mCargoPO.getId())){
						throw new BusinessRuntimeException("货名和货物标识码重复");
					}
				});


			}
		}

		int count = 0;
		count = mCargoTypeMapper.updateCargo(mCargoPO);
		return count;
	}


	@Override
	public int deleteCargo(Long id) {
		final String methodName = "deleteCargo";
		LOGGER.enter(methodName, "业务执行");
		MCargoDTO cargo = mCargoTypeMapper.getCargoById(id);

		int count2 = tBusContractRateMapper.getCargoNameByCode(cargo.getCargoCode());
		if(count2 >0){
			throw new BusinessRuntimeException("货物被合同使用无法删除");
		}

		int count3 =tBusRateMapper.getCargoNameByCode(cargo.getCargoCode());
		if(count3 >0){
			throw new BusinessRuntimeException("货物被货物包干费使用无法删除");
		}

		int count4 =mCargoTypeMapper.getCargoInfoByCode(cargo.getCargoCode());
		if(count4 >0){
			throw new BusinessRuntimeException("货物被票货使用无法删除");
		}

		int count = 0;
		count = mCargoTypeMapper.deleteCargo(id);
		return count;
	}

	@Override
	public Pages<MCargoDTO> getListCargoNew(MCargoSearchDTO mCargoSearchDTO) {
		final String methodName = "getListCargo";
		LOGGER.enter(methodName, "业务执行");

		Pages<MCargoDTO> pages = PageHelperUtils.limit(mCargoSearchDTO, () -> {
			return mCargoTypeMapper.getListCargoNew(mCargoSearchDTO);
		});

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return pages;
	}
	@Override
	public Pages<MCargoDTO> getOutwardGoods(MCargoSearchDTO mCargoSearchDTO) {
		final String methodName = "getListCargo";
		LOGGER.enter(methodName, "业务执行");
		Pages<MCargoDTO> pages = PageHelperUtils.limit(mCargoSearchDTO, () -> {
			return mCargoTypeMapper.getOutwardGoods(mCargoSearchDTO);
		});
		LOGGER.exit(methodName, StringUtils.EMPTY);
		return pages;
	}

	@Override
	public void updateStatus(Long id, String status) {
		mCargoTypeMapper.updateStatus(id, status);
	}
}
