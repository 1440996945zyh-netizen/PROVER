package com.yy.ppm.system.service;



import com.yy.common.page.Pages;
import com.yy.ppm.system.bean.dto.SysLoginLogDTO;
import com.yy.ppm.system.bean.dto.SysLoginLogSearchDTO;

 /**
 * @ClassName 登录日志表(SysLoginLog)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 15:51:00
 */
public interface SysLoginLogService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<SysLoginLogDTO> getList(SysLoginLogSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public SysLoginLogDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param sysLoginLogDTO
     * @return 是否成功
     */
    public boolean doSave(SysLoginLogDTO sysLoginLogDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

}

