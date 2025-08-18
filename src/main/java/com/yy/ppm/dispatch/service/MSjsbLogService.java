package com.yy.ppm.dispatch.service;



import com.yy.common.page.Pages;
import com.yy.ppm.dispatch.bean.dto.MSjsbLogDTO;
import com.yy.ppm.dispatch.bean.dto.MSjsbLogSearchDTO;

 /**
 * @ClassName 数据上报日志表(MSjsbLog)Service
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2025年05月20日 10:40:00
 */
public interface MSjsbLogService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<MSjsbLogDTO> getList(MSjsbLogSearchDTO searchDTO);


     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public MSjsbLogDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mSjsbLogDTO
     * @return 是否成功
     */
    public boolean doSave(MSjsbLogDTO mSjsbLogDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

}

