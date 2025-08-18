package com.yy.ppm.master.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MPortDTO;
import com.yy.ppm.master.bean.dto.MPortSearchDTO;
import com.yy.ppm.master.bean.po.MPortPO;

/**
 * 港口信息操作
 * @author yangcl
 * */
public interface MPortService {

    /**
     * 查询港口信息集合
     * */
    public Pages<MPortDTO> getList(MPortSearchDTO searchDTO);

    /**
     * ID查询港口信息集合
     * */
    public MPortDTO getPortById(Long id);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteById(Long id);

    /**
     * 更新港口信息
     * @param po
     * @return
     */
    int savePort(MPortDTO po);


}
