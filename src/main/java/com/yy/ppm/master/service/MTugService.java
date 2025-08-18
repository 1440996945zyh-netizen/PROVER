package com.yy.ppm.master.service;



import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MTugDTO;
import com.yy.ppm.master.bean.dto.MTugSearchDTO;

 /**
 * @ClassName 拖轮资料(MTug)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 14:20:00
 */
public interface MTugService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<MTugDTO> getList(MTugSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public MTugDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mTugDTO
     * @return 是否成功
     */
    public boolean doSave(MTugDTO mTugDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

}

