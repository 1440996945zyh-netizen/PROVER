package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MWorkScheduleDTO;

import java.util.List;

/**
 * (MWorkSchedule)Dao
 *
 * @author 张超
 * @date 2021-03-11 14:44:48
 */
public interface MWorkScheduleMapper {

    /**
	 * 获取列表
	 * @param
	 * @return
	 */
	public List<MWorkScheduleDTO> getList();

	/**
	 * 新增
	 * @param mWorkScheduleDTO DTO
	 * @return
	 */
	@Edit
	public int insert(MWorkScheduleDTO mWorkScheduleDTO);

}
