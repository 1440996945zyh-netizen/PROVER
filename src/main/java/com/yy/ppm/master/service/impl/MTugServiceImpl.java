package com.yy.ppm.master.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.JwtUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.common.util.str.StringUtil;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.service.MTugService;
import com.yy.ppm.master.mapper.MTugMapper;
import com.yy.ppm.master.bean.dto.MTugDTO;
import com.yy.ppm.master.bean.dto.MTugSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @ClassName 拖轮资料(MTug)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 14:20:00
 */
@Service
public class MTugServiceImpl implements MTugService {

    @Resource
    private MTugMapper mTugMapper;

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
    public Pages<MTugDTO> getList(MTugSearchDTO searchDTO) {

    	Pages<MTugDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mTugMapper.getList(searchDTO);
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
     public MTugDTO getDetail(Long id) {
         return mTugMapper.getById(id);
     }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MTugDTO dto) {

        //拖轮编号验重
        commonService.isRepeate("M_TUG", "TUG_CODE", dto.getTugCode(), StringUtil.getString(dto.getId()), "拖轮编号", null);
        //拖轮名称验重
        commonService.isRepeate("M_TUG", "TUG_NAME", dto.getTugName(), StringUtil.getString(dto.getId()), "拖轮名称", null);

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mTugMapper.insert(dto) == 1;

            // 修改
        } else {
            return mTugMapper.update(dto) == 1;
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

        return mTugMapper.deleteById(id) == 1;

    }
}

