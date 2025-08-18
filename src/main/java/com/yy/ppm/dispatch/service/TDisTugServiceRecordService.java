package com.yy.ppm.dispatch.service;



import com.yy.common.page.Pages;
import com.yy.ppm.dispatch.bean.dto.TDisTugServiceRecordDTO;
import com.yy.ppm.dispatch.bean.dto.TDisTugServiceRecordSearchDTO;

 /**
 * @ClassName 拖轮服务记录(TDisTugServiceRecord)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:45:00
 */
public interface TDisTugServiceRecordService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<TDisTugServiceRecordDTO> getList(TDisTugServiceRecordSearchDTO searchDTO);

    public byte[] export(TDisTugServiceRecordSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public TDisTugServiceRecordDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tDisTugServiceRecordDTO
     * @return 是否成功
     */
    public boolean doSave(TDisTugServiceRecordDTO tDisTugServiceRecordDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

}

