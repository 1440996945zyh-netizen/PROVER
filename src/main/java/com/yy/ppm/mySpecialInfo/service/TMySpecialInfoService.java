package com.yy.ppm.mySpecialInfo.service;



import com.yy.ppm.mySpecialInfo.bean.dto.TMySpecialInfoDTO;

/**
 * @ClassName 个人特别信息表(TMySpecialInfo)Service
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年01月17日 10:17:00
 */
public interface TMySpecialInfoService {


     /**
      * 查询单条记录
      *
      * @param
      * @return 实体
      */
     public TMySpecialInfoDTO getPageNum(TMySpecialInfoDTO dto);

    /**
     * 关注项目
     *
     * @param dto
     * @return 是否成功
     */
    public boolean careSave(TMySpecialInfoDTO dto);

    /**
     * 去关项目
     *
     * */
    public boolean noCareSave(TMySpecialInfoDTO dto);
    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);



    boolean updatePageNum(TMySpecialInfoDTO dto);

//     boolean projectInsert(List<TMySpecialInfoDTO> list);


}

