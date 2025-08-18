package com.yy.ppm.master.service;



import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MCityDTO;
import com.yy.ppm.master.bean.dto.MCitySearchDTO;

 /**
 * @ClassName (MCity)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月30日 13:29:00
 */
public interface MCityService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<MCityDTO> getList(MCitySearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public MCityDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mCityDTO
     * @return 是否成功
     */
    public boolean doSave(MCityDTO mCityDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

}

