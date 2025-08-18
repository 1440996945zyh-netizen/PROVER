package com.yy.ppm.businessKH.controller;
/**
 * @ClassName CargoInfoController.java
 * @author lihuijie
 * @version 1.0.0
 * @Description 控货管理
 * @createTime 2022年4月24日 16:15:01
 */

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.businessKH.mapper.BtShwHgfxMapper;
import com.yy.ppm.businessKH.sevice.HgfxService;
import com.yy.ppm.businessKH.vo.req.HgfxSearchReqVo;
import com.yy.ppm.businessKH.vo.req.SaveOrUpdateHgfxReqVo;
import com.yy.ppm.businessKH.vo.resp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.xml.rpc.ServiceException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import com.yy.common.page.Pages;


@RestController
@RequestMapping("/hgfx")
//@DS("wfport_rzport")
public class HgfxController {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(HgfxController.class);

    @Autowired
    private HgfxService hgfxService ;
    @Autowired
    private BtShwHgfxMapper btShwHgfxMapper ;



   /**
    * @description  海关放行单查询
    * @title getHgfxListPage
    * @author lihuijie
    * @updateTime 2023-08-28 14:45
    * @throws
    * @param hgfxSearchReqVo: 海关放行单查询vo
    * @return java.util.Map<java.lang.String,java.lang.Object>
    */
    @PostMapping("/getHgfxListPage")
    @PreAuthorize("hasAuthority('business:hgPermit:query')")
    public Map<String, Object> getHgfxListPage( HgfxSearchReqVo hgfxSearchReqVo){
        try {
            final String methodName = "HgfxController:getHgfxListPage";
            LOGGER.enter(methodName + "[start]", "hgfxSearchReqVo:" + hgfxSearchReqVo);
            Pages<HgfxRespVo> getCargoInfoListPage =hgfxService.getHgfxList(hgfxSearchReqVo);
            LOGGER.exit( methodName + "result:" + getCargoInfoListPage);
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(getCargoInfoListPage);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("查询失败："+e.getMessage()).toResult();
        }



    };


    /**
     * @description 修改海关放行单
     * @title updateHgfx
     * @author lihuijie
     * @updateTime 2022-06-28 17:13
     * @throws
     * @param saveOrUpdateHgfxReqVo:  修改内容类
     * @return java.lang.Object
     */
    @PostMapping("/updateHgfx")
    @PreAuthorize("hasAuthority('business:hgPermit:update')")
    public Object updateHgfx(@RequestBody SaveOrUpdateHgfxReqVo saveOrUpdateHgfxReqVo) {
        try {
            final String methodName = "HgfxController:updateHgfx";
            LOGGER.enter(methodName + "[start]", "saveOrUpdateHgfxReqVo:" + saveOrUpdateHgfxReqVo);
            hgfxService.updateHgfx (saveOrUpdateHgfxReqVo);
            LOGGER.exit(methodName);

            return Response.SUCCESS.newBuilder().out( "修改成功").toResult();
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("修改失败："+e.getMessage()).toResult();
        }
    }


    /**
     * @description 海关放行人信息
     * @title getHqcyrInfo
     * @author lihuijie
     * @updateTime 2022-06-28 17:13
     * @throws
     * @param hqcyrid: 货权持有人id
     * @param hqcyr:  货权持有人
     * @return com.rzg.web.responsehandling.vo.BaseResponse
     */

    @PostMapping("/getHqcyrInfo")
 //   @PreAuthorize("hasAuthority('business:hgPermit:getHqcyrInfo')")
    public Map<String, Object> getHqcyrInfo(String hqcyrid,String hqcyr){
        final String methodName = "HgfxController:getHqcyrInfo";
        LOGGER.enter(methodName + "[start]", "hqcyrid:" + hqcyrid);
        List<HgfxRespVo> hqcyrInfo =hgfxService.hqcyrInfo(hqcyrid,hqcyr);
        LOGGER.exit( methodName + "result:" + hqcyrInfo);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(hqcyrInfo);
    };

    /**
     * @description 作业委托人信息
     * @title getZywtrInfo
     * @author lihuijie
     * @updateTime 2023-02-23 15:35
     * @throws
     * @param zywtrid: 作业委托人id
     * @param zywtr:  作业委托人
     * @return com.rzg.web.responsehandling.vo.BaseResponse
     */
    @PostMapping("/getZywtrInfo")
  //  @PreAuthorize("hasAuthority('business:hgPermit:getZywtrInfo')")
    public Map<String, Object> getZywtrInfo(String zywtrid,String zywtr){
        final String methodName = "HgfxController:getZywtrInfo";
        LOGGER.enter(methodName + "[start]", "zywtrid:" + zywtrid);
            List<ZywtrInfoRespVo> zywtrInfo =btShwHgfxMapper.zywtrInfo(zywtrid,zywtr);
        LOGGER.exit( methodName + "result:" + zywtrInfo);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(zywtrInfo);
    };

    /**
     * @description 删除海关放行单
     * @title deleteHgfx
     * @author lihuijie
     * @updateTime 2022-06-28 17:14
     * @throws
     * @param id:  id
     * @return java.lang.Object
     */
    @PostMapping("/deleteHgfx")
    @PreAuthorize("hasAuthority('business:hgPermit:delete')")
    public Object deleteHgfx(Long id) throws ServiceException, ParseException {
        try {
            final String methodName = "HgfxController:deleteHgfx";
            LOGGER.enter(methodName + "[start]", "id:" + id);
            hgfxService.deleteHgfx (id);
            LOGGER.exit(methodName);
            return Response.SUCCESS.newBuilder().out("删除成功").toResult();
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("删除失败："+e.getMessage ()).toResult();
        }


    }

    /**
     * @description 作业公司信息
     * @title getZygsInfo
     * @author lihuijie
     * @updateTime 2022-06-28 17:14
     * @throws
     * @return com.rzg.web.responsehandling.vo.BaseResponse
     */
    @PostMapping("/getZygsInfo")
   // @PreAuthorize("hasAuthority('business:hgPermit:getZygsInfo')")
    public Map<String, Object> getZygsInfo(){
        try {
            List<ZygsInfoRespVo> zygsInfo =hgfxService.zygsInfo();
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(zygsInfo);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("查询失败："+e.getMessage()).toResult();

        }
    };

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
     * @return java.util.List<com.spgtech.khgl.vo.resp.BaseInfoRespVo>
     */
    @PostMapping("/getKhxxByZygsdm")
  //  @PreAuthorize("hasAuthority('business:hgPermit:getKhxxByZygsdm')")
    public Map<String, Object> getKhxx(String zygsdm, String zhwchm,String lb){
        try {
            List<BaseInfoRespVo> getKhxx =hgfxService.getKhxx(zygsdm,zhwchm,lb);
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(getKhxx);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("查询失败："+e.getMessage()).toResult();
        }
    };

    /**
     * @description 查询海关放行信息通过报关单号(进出口分别查询)
     * @title queryHgfxInfoByBgdh
     * @author lihuijie
     * @updateTime 2022-06-28 16:59
     * @throws
     * @param bgdh: 报关单号
     * @param lb:  类别
     * @return java.util.List<com.spgtech.khgl.vo.resp.FxdInfoRespVo>
     */
    @PostMapping("/getKhxx")
  //  @PreAuthorize("hasAuthority('business:hgPermit:getKhxx')")
    public Map<String, Object> queryHgfxInfoByBgdh(String bgdh,String lb){
        try {
            List<FxdInfoRespVo> queryHgfxInfoByBgdh =hgfxService.queryHgfxInfoByBgdh(bgdh,lb);
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(queryHgfxInfoByBgdh);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("查询失败："+e.getMessage()).toResult();
        }
    };


    /**
     * @description 查询海关放行信息出口通过提单号、IMO、代理航次
     * @title queryHgfxInfoCKByTdhOrImoOrDlhc
     * @author lihuijie
     * @updateTime 2022-06-28 17:00
     * @throws
     * @param tdh: 提单号
     * @param imo: imo
     * @param dlhc:  代理航次
     * @return java.util.List<com.spgtech.khgl.vo.resp.FxdInfoRespVo>
     */
    @PostMapping("/queryHgfxInfoCKByTdhOrImoOrDlhc")
  //  @PreAuthorize("hasAuthority('business:hgPermit:queryHgfxInfoCKByTdhOrImoOrDlhc')")
    public Map<String, Object> queryHgfxInfoCKByTdhOrImoOrDlhc(String tdh,String imo,String dlhc){
        try {
            List<FxdInfoRespVo> queryHgfxInfoCKByTdhOrImoOrDlhc =hgfxService.queryHgfxInfoCKByTdhOrImoOrDlhc(tdh,imo,dlhc);
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(queryHgfxInfoCKByTdhOrImoOrDlhc);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("查询失败："+e.getMessage()).toResult();
        }
    };

    /**
     * @description 新增海关放行单
     * @title saveHgfxJCK
     * @author lihuijie
     * @updateTime 2022-06-28 17:00
     * @throws
     * @param saveOrUpdateHgfxReqVo:  保存数据类
     */
    @PostMapping("/insertHgfx")
    @PreAuthorize("hasAuthority('business:hgPermit:add')")
    public Object insertHgfx(@RequestBody SaveOrUpdateHgfxReqVo saveOrUpdateHgfxReqVo) {

        try {
            final String methodName = "HgfxController:insertHgfx";
           LOGGER.enter(methodName + "[start]", "saveOrUpdateHgfxReqVo:" + saveOrUpdateHgfxReqVo);
            hgfxService.saveHgfxJCK (saveOrUpdateHgfxReqVo);
            LOGGER.exit(methodName);

            return Response.SUCCESS.newBuilder().out("新增海关放行单成功").toResult();
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("新增海关放行单失败："+e.getMessage()).toResult();
        }

    }

    /**
     * @description 批量新增海关放行单
     * @title insertHgfxPL
     * @author lihuijie
     * @updateTime 2022-11-30 17:41
     * @throws
     * @param saveOrUpdateHgfxReqVoList:
     * @return java.lang.Object
     */
    @PostMapping("/insertHgfxPL")
   // @PreAuthorize("hasAuthority('business:hgPermit:insertHgfxPL')")
    public Object insertHgfxPL(@RequestBody List<SaveOrUpdateHgfxReqVo> saveOrUpdateHgfxReqVoList) {
        try {
            final String methodName = "HgfxController:insertHgfx";
            LOGGER.enter(methodName + "[start]", "saveOrUpdateHgfxReqVoList:" + saveOrUpdateHgfxReqVoList);
            for(SaveOrUpdateHgfxReqVo saveOrUpdateHgfxReqVo : saveOrUpdateHgfxReqVoList) {
                hgfxService.saveHgfxJCK (saveOrUpdateHgfxReqVo);
            }
            LOGGER.exit(methodName);
            return Response.SUCCESS.newBuilder().out( "批量新增海关放行单成功").toResult();
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("批量新增海关放行单失败："+e.getMessage()).toResult();
        }

    }


    /**
     * @description 查询货物信息通过作业公司代码
     * @title hwInfo
     * @author lihuijie
     * @updateTime 2022-06-28 17:01
     * @throws
     * @param zygsdm: 作业公司代码
     * @param hwmchXl:  货物名称大类
     * @return java.util.List<com.spgtech.khgl.vo.resp.BaseInfoRespVo>
     */
    @PostMapping("/getHwInfo")
    public Map<String, Object> hwInfo(String zygsdm,String hwmchXl){
        try {
            List<BaseInfoRespVo> hwInfo =hgfxService.hwInfo(zygsdm,hwmchXl);
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(hwInfo);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("查询失败："+e.getMessage()).toResult();
        }
    };





    /**
     * @description 批量新增海关放行单时，海关信息查询
     * @title queryHgfxMassInfo
     * @author lihuijie
     * @updateTime 2022-10-11 11:51
     * @throws
     * @param lb: 类别
     * @param bgdh:  报关单号
     * @return com.rzg.web.responsehandling.vo.BaseResponse
     */
    @PostMapping("/queryHgfxMassInfo")
    public Map<String, Object> queryHgfxMassInfo(String lb,String bgdh) {
        try {
            List<HgfxRespVo>   queryHgfxMassInfo = this.hgfxService.queryHgfxMassInfo(lb,bgdh);
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(queryHgfxMassInfo);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("查询失败："+e.getMessage()).toResult();
        }
    }
}
