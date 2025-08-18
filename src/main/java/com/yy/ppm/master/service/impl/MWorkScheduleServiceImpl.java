package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;

import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.master.bean.dto.MWorkScheduleDTO;
import com.yy.ppm.master.mapper.MWorkScheduleMapper;
import com.yy.ppm.master.service.MWorkScheduleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * (MWorkSchedule)表服务实现类
 *
 * @author 张超
 * @date 2021-03-11 14:45:21
 */
@Service
public class MWorkScheduleServiceImpl implements MWorkScheduleService {

	@Autowired
	private Snowflake snowflake;

    @Resource
    private MWorkScheduleMapper mWorkScheduleMapper;

	@Resource
	private CommonMapper commonMapper;

    @Override
	public List<MWorkScheduleDTO> getList() {
		return mWorkScheduleMapper.getList();
	}

	@Override
	@Transactional
	public int save(List<MWorkScheduleDTO> workScheduleList) {

    	// 先删除后新增
		commonMapper.deleteAll("M_WORK_SCHEDULE");

		int count = 0;
		// 批量新增
		for(MWorkScheduleDTO dto:workScheduleList){
			dto.setId(snowflake.nextId());
			count += mWorkScheduleMapper.insert(dto);
		}

		return count;
	}
}
