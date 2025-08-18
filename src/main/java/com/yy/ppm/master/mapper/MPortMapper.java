package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MPortDTO;
import com.yy.ppm.master.bean.dto.MPortSearchDTO;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.dto.MShipSearchDTO;
import com.yy.ppm.master.bean.po.MPortPO;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * 港口管理DAO
 *
 * @author yangcl
 */
public interface MPortMapper {
    /**
     * 插入港口信息
     * */
    @Edit
    public Integer insertPort(MPortDTO po);

    /**
     * 更新港口信息
     * */
    @Edit
   public Integer updatePort(MPortDTO po);

    /**
     * 查询港口信息
     * */
    public Page<MPortDTO> getList(MPortSearchDTO searchDTO);

    /**
     * ID查询港口信息
     * */
    public MPortDTO getPortById(@Param("id") Long id);

    /**
     * ID查询港口信息
    */
    public MPortDTO getByPortCode(@Param("portCode") String portCode);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteById(Long id);


}
