package com.yy.ppm.business.service.impl;

import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.google.common.collect.*;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.business.controller.PoundbillController;

import com.yy.ppm.business.service.PoundbillService;
import com.yy.ppm.business.mapper.PoundbillMapper;
import com.yy.ppm.business.bean.dto.PoundbillDTO;
import com.yy.ppm.business.bean.dto.PoundbillSearchDTO;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

 /**
 * @ClassName 单船测试记录(TStdShipRecord)ServiceImpl
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月31日 10:35:00
 */
@Service
public class PoundbillServiceImpl implements PoundbillService {

    @Resource
    private PoundbillMapper poundbillMapper;

	/**
	* 日志组件
	**/
	private static final MicroLogger LOGGER = new MicroLogger(PoundbillController.class);

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<PoundbillDTO> getPageList(PoundbillSearchDTO searchDTO) {
		final String methodName = "PoundbillServiceImpl:getPageList";
		try{
			LOGGER.info(methodName,"获取列表（翻页）");
			//按照创建时间倒叙排列
			Pages<PoundbillDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
			Page<PoundbillDTO> page = poundbillMapper.getPageList(searchDTO);
            return page;
			});
			return pages;
        }catch (Exception e){
            LOGGER.error(methodName,e.getMessage());
            return new Pages<>();
        }
    }

	 /**
     * 获取列表
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<PoundbillDTO> getDetailListByCondition(PoundbillSearchDTO searchDTO) {
	    final String methodName = "TStdShipRecordServiceImpl:getListByCondition";
		try{
			LOGGER.info(methodName,"获取列表");
			List<PoundbillDTO> list = poundbillMapper.getDetailList(searchDTO);
			return list;
        }catch (Exception e){
            LOGGER.error(methodName,e.getMessage());
            return Lists.newArrayList();
        }
    }

}

