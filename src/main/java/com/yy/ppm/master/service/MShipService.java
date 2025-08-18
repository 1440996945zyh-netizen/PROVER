package com.yy.ppm.master.service;



import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.dto.MShipSearchDTO;
import com.yy.ppm.master.bean.po.MShipLogPO;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 海轮资料(MShip)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月27日 15:44:00
 */
public interface MShipService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<MShipDTO> getList(MShipSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public MShipDTO getDetail(Long id);

     public List<MShipLogPO> getShipLog(Long id);

    /**
     * 保存
     *
     * @param mShipDTO
     * @return 是否成功
     */
    public boolean doSave(MShipDTO mShipDTO);

     /**
      * 驳回
      *
      * @param mShipDTO
      * @return 是否成功
      */
     public boolean doReject(MShipDTO mShipDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

     boolean approveById(Long id);

     boolean cancelById(Long id);



    /**
     * 批量同步
     *
     * @param list
     * @return 是否成功
     */
    public boolean sync(List<MShipDTO> list);



    boolean approve(MShipDTO mShipDTO);




    String getBlackShip(List<String> list);
}

