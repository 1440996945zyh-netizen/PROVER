package com.yy.ppm.master.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.PinYin4jUtils;
import com.yy.common.util.UserHelper;

import com.yy.common.util.str.StringUtil;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.service.MStopReasonService;
import com.yy.ppm.master.mapper.MStopReasonMapper;
import com.yy.ppm.master.bean.dto.MStopReasonDTO;
import com.yy.ppm.master.bean.dto.MStopReasonSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;

 /**
 * @ClassName 船舶停时原因维护(MStopReason)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 17:21:00
 */
@Service
public class MStopReasonServiceImpl implements MStopReasonService {

    @Resource
    private MStopReasonMapper mStopReasonMapper;

    @Resource
	private Snowflake snowflake;
    @Resource
    private CommonService commonService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<MStopReasonDTO> getList(MStopReasonSearchDTO searchDTO) {

    	Pages<MStopReasonDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mStopReasonMapper.getList(searchDTO);
		});

        return pages;
    }

    /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     @Override
     public MStopReasonDTO getDetail(Long id) {
         return mStopReasonMapper.getById(id);
     }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MStopReasonDTO dto) {

        // 停时name验证重复
        commonService.isRepeate("M_STOP_REASON", "STOP_REASON_NAME", dto.getStopReasonName(), StringUtil.getString(dto.getId()), "停工名称", null);

        //如果助记码为空，则自动生成。
        if(StringUtil.isEmpty(dto.getShorthandCode())){
            dto.setShorthandCode(PinYin4jUtils.getPinYinHeadChar(dto.getStopReasonName(), dto.getStopReasonName().length()));
        }

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mStopReasonMapper.insert(dto) == 1;

            // 修改
        } else {
            return mStopReasonMapper.update(dto) == 1;
        }

    }

    /**
     * 删除
     *
     * @param  id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return mStopReasonMapper.deleteById(id) == 1;

    }
}

