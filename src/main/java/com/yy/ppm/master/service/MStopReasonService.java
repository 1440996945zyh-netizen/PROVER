package com.yy.ppm.master.service;



import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MStopReasonDTO;
import com.yy.ppm.master.bean.dto.MStopReasonSearchDTO;

 /**
 * @ClassName 船舶停时原因维护(MStopReason)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 17:21:00
 */
public interface MStopReasonService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<MStopReasonDTO> getList(MStopReasonSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public MStopReasonDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mStopReasonDTO
     * @return 是否成功
     */
    public boolean doSave(MStopReasonDTO mStopReasonDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

}

