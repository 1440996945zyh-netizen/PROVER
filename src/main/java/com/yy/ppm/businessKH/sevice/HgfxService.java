package com.yy.ppm.businessKH.sevice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yy.ppm.businessKH.vo.req.HgfxSearchReqVo;
import com.yy.ppm.businessKH.vo.req.SaveOrUpdateHgfxReqVo;
import com.yy.ppm.businessKH.vo.resp.BaseInfoRespVo;
import com.yy.ppm.businessKH.vo.resp.FxdInfoRespVo;
import com.yy.ppm.businessKH.vo.resp.HgfxRespVo;
import com.yy.ppm.businessKH.vo.resp.ZygsInfoRespVo;
import com.yy.common.page.Pages;
import org.geotools.ows.ServiceException;

import java.text.ParseException;
import java.util.List;

/**
 * @author lihuijie
 * @version 1.0.0
 * @ClassName CargoInfoService.java
 * @Description TODO
 * @createTime 2022年05月09日 15:50:00
 */
public interface HgfxService  {


     /**
      *
      * @param hgfxSearchReqVo: 查询入参
      * @title getHgfxList
      * @description   查询海关放行单List
      * @author lihuijie
      * @updateTime 2022-06-28 16:52
      * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.spgtech.businessKH.vo.resp.HgfxRespVo>
      */
     Pages<HgfxRespVo> getHgfxList(HgfxSearchReqVo hgfxSearchReqVo);

    /**
     *
     * @param saveOrUpdateHgfxReqVo: 修改内容类
     * @title updateHgfx
     * @description  修改海关放行单
     * @author lihuijie
     * @updateTime 2022-06-28 16:53
     *
     */
    void updateHgfx(SaveOrUpdateHgfxReqVo saveOrUpdateHgfxReqVo) throws ParseException;


    /**
     * @description 海关放行人信息
     * @title hqcyrInfo
     * @author lihuijie
     * @updateTime 2022-06-28 16:56
     * @throws
     * @param hqcyrid: 货权持有人id
     * @param hqcyr:  货权持有人
     * @return java.util.List<com.spgtech.businessKH.vo.resp.HgfxRespVo>
     */
    List<HgfxRespVo> hqcyrInfo(String hqcyrid, String hqcyr);


    /**
     * @description  删除海关放行单
     * @title deleteHgfx
     * @author lihuijie
     * @updateTime 2022-06-28 16:57
     * @throws
     * @param id:  id
     */
    void deleteHgfx(Long id) throws ServiceException, ParseException;


   /**
    * @description 作业公司信息
    * @title zygsInfo
    * @author lihuijie
    * @updateTime 2022-06-28 16:57
    * @throws
    * @return java.util.List<com.spgtech.businessKH.vo.resp.ZygsInfoRespVo>
    */
    List<ZygsInfoRespVo> zygsInfo();


  /**
   * @description 查询控货管理中的票货信息(新增进出口海关放行单)
   * @title getKhxx
   * @author lihuijie
   * @updateTime 2022-06-28 16:58
   * @throws
   * @param zygsdm: 作业公司id
   * @param zhwchm: 中文船名
   * @param zxb:
   * @param lb:  类别
   * @return java.util.List<com.spgtech.businessKH.vo.resp.BaseInfoRespVo>
   */
 List<BaseInfoRespVo> getKhxx(String zygsdm, String zhwchm, String lb) throws ServiceException;


  /**
   * @description 查询海关放行信息通过报关单号
   * @title queryHgfxInfoByBgdh
   * @author lihuijie
   * @updateTime 2022-06-28 16:59
   * @throws
   * @param bgdh: 报关单号
   * @param lb:  类别
   * @return java.util.List<com.spgtech.businessKH.vo.resp.FxdInfoRespVo>
   */
 List<FxdInfoRespVo>queryHgfxInfoByBgdh(String bgdh, String lb) throws ServiceException;



 /**
  * @description 查询海关放行信息出口通过提单号、IMO、代理航次
  * @title queryHgfxInfoCKByTdhOrImoOrDlhc
  * @author lihuijie
  * @updateTime 2022-06-28 17:00
  * @throws
  * @param tdh: 提单号
  * @param imo: imo
  * @param dlhc:  代理航次
  * @return java.util.List<com.spgtech.businessKH.vo.resp.FxdInfoRespVo>
  */
 List<FxdInfoRespVo>queryHgfxInfoCKByTdhOrImoOrDlhc(String tdh, String imo, String dlhc);





 /**
  * @description 海关放行单进出口保存
  * @title saveHgfxJCK
  * @author lihuijie
  * @updateTime 2022-06-28 17:00
  * @throws
  * @param saveOrUpdateHgfxReqVo:  保存数据类
  */
  void saveHgfxJCK(SaveOrUpdateHgfxReqVo saveOrUpdateHgfxReqVo) throws ParseException;


 /**
  * @description 查询货物信息通过作业公司代码
  * @title hwInfo
  * @author lihuijie
  * @updateTime 2022-06-28 17:01
  * @throws
  * @param zygsdm: 作业公司代码
  * @param hwmchXl:  货物名称大类
  * @return java.util.List<com.spgtech.businessKH.vo.resp.BaseInfoRespVo>
  */
 List<BaseInfoRespVo> hwInfo(String zygsdm, String hwmchXl) throws ServiceException;





 /**
  * @description 批量新增海关放行单时，海关信息查询
  * @title queryHgfxMassInfo
  * @author lihuijie
  * @updateTime 2022-10-11 11:38
  * @throws
  * @param lb: 类别
  * @param bgdh:  报关单号
  * @return java.util.List<com.spgtech.businessKH.vo.resp.HgfxRespVo>
  */
  List<HgfxRespVo>queryHgfxMassInfo(String lb, String bgdh);




}
