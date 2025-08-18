package com.yy.ppm.businessKH.controller;
/**
 * @ClassName CargoInfoController.java
 * @author lihuijie
 * @version 1.0.0
 * @Description 控货管理
 * @createTime 2022年4月24日 16:15:01
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.businessKH.mapper.CargoInfoDetailMapper;
import com.yy.ppm.businessKH.mapper.CargoInfoMapper;
import com.yy.ppm.businessKH.model.CargoInfo;
import com.yy.ppm.businessKH.model.CargoInfoDetail;
import com.yy.ppm.businessKH.model.CargoInfoVW;
import com.yy.ppm.businessKH.sevice.CargoInfoService;
import com.yy.ppm.businessKH.vo.req.CargoInfoSearchReqVo;
import com.yy.ppm.businessKH.vo.req.FhksCargoInfoReqVo;
import com.yy.ppm.businessKH.vo.resp.CargoInfoRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/khgl")
@Transactional
//@DS("wfport_rzport")
public class CargoInfoController {

@Autowired
private CargoInfoService cargoInfoService ;
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(CargoInfoController.class);

    @Autowired
    private CargoInfoMapper cargoInfoMapper ;

    @Autowired
    private CargoInfoDetailMapper cargoInfoDetailMapper ;



    /**
     *
     * @param cargoInfoSearchReqVo: 查询入参
     * @title getCargoInfoList
     * @description 查询控货管理list
     * @author 李慧洁
     * @updateTime 2022-06-28 16:33
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.spgtech.khgl.model.CargoInfoVW>
     */

    @PostMapping("/getCargoInfoListPage")
    @PreAuthorize("hasAuthority('business:khglInfo:query')")
    public Map<String, Object> getCargoInfoListPage(@RequestBody CargoInfoSearchReqVo cargoInfoSearchReqVo){

        try {
            final String methodName = "CargoInfoController:getCargoInfoListPage";
            LOGGER.enter(methodName + "[start]", "cargoInfoSearchReqVo:" + cargoInfoSearchReqVo);
            Pages<CargoInfoVW> getCargoInfoListPage =cargoInfoService.getCargoInfoList(cargoInfoSearchReqVo);
            LOGGER.exit( methodName + "result:" + getCargoInfoListPage);
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(getCargoInfoListPage);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("查询失败："+e.getMessage()).toResult();
        }

    }



    /**
     * @description 归档同修改
     * @title gd
     * @author lihuijie
     * @updateTime 2022-06-28 17:06
     * @throws
     * @param cargokey:  控货主键
     * @return java.lang.Object
     */
    @RequestMapping("/gd")
    @PreAuthorize("hasAuthority('business:khglInfo:gd')")
    public Object gd(@RequestParam("cargokey") String cargokey) {
        try {
            CargoInfo cargoInfo = cargoInfoMapper.selectOne(new QueryWrapper<CargoInfo> ().eq("cargokey", cargokey));
            if (cargoInfo != null) {
                if (cargoInfo.getFlag().equals("否")) {
                    cargoInfo.setFlag("是");
                } else {
                    cargoInfo.setFlag("否");
                }
                QueryWrapper<CargoInfo> queryWrapper = new QueryWrapper<> ();
                queryWrapper.eq ("cargokey",   cargoInfo.getCargokey());
                cargoInfoMapper.update (cargoInfo,queryWrapper);
               // cargoInfoMapper.update(cargoInfo, new QueryWrapper<CargoInfo>().eq("cargokey", cargoInfo.getCargokey()));
            }
            return Response.SUCCESS.newBuilder().out("归档成功").toResult();
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("归档失败："+e.getMessage()).toResult();
        }

    }


    /**
     * @description 查询单条货键
     * @title queryOneCargokey
     * @author lihuijie
     * @updateTime 2022-06-28 17:06
     * @throws
     * @param cargokey:  控货主键
     * @return java.lang.Object
     */
    @RequestMapping("/queryOneCargokey")
    public Object queryOneCargokey(String cargokey) {
        try {
            CargoInfoRespVo btNewShwCargoinfo =new CargoInfoRespVo();
            if(cargokey !=null) {
                 btNewShwCargoinfo = cargoInfoService.selectOneByCargokey (cargokey);
            }
           return Response.SUCCESS.newBuilder().out("查询成功").toResult (btNewShwCargoinfo);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("查询失败："+e.getMessage()).toResult();
        }
    }

    /**
     * @description 加扣数分类汇总
     * @title getTypeLb
     * @author lihuijie
     * @updateTime 2022-12-01 14:55
     * @throws
     * @param cargokey:  货健
     * @return java.lang.Object
     */

    @RequestMapping("/getTypeLb")
    public Object getTypeLb(String cargokey) {
        if (cargokey != null) {
            List<Map> map =  cargoInfoMapper.getTypeLb (cargokey);
            return Response.SUCCESS.newBuilder().out("查询成功").toResult (map);
        } else {
            return Response.FAIL.newBuilder().out("cargokey不为空,刷新后重试：").toResult();

        }

    }


    /**
     * @description 加扣数保存
     * @title updateJKS
     * @author lihuijie
     * @updateTime 2022-06-28 17:07
     * @throws
     * @param btShwCargoinfoDetail:  详情表数据类
     * @return java.lang.Object
     */

    @RequestMapping("/updateJKS")
    @PreAuthorize("hasAuthority('business:khglInfo:updateJKS')")
    public Object updateJKS(@RequestBody CargoInfoDetail btShwCargoinfoDetail) throws ParseException {

        try {
            final String methodName = "CargoInfoController:updateJKS";
            LOGGER.enter(methodName + "[start]", "btShwCargoinfoDetail:" + btShwCargoinfoDetail);
            cargoInfoService.updateJKS (btShwCargoinfoDetail);
            LOGGER.exit( methodName);

            return Response.SUCCESS.newBuilder().out("保存成功").toResult();
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("保存失败："+e.getMessage()).toResult();
        }




    }





    /**
     * @description 通过货健查询控货详情
     * @title getKhglByCargokey
     * @author lihuijie
     * @updateTime 2022-06-28 17:07
     * @throws
     * @param cargokey:  控货主键
     * @return java.lang.Object
     */

    @RequestMapping("/getKhglByCargokey")
    public Object getKhglByCargokey(String cargokey) {

        try {
            CargoInfoRespVo btNewShwCargoinfo =new CargoInfoRespVo();
            if(cargokey !=null) {
                btNewShwCargoinfo = cargoInfoService.getKhglByCargokey (cargokey);
            }
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(btNewShwCargoinfo);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("查询失败："+e.getMessage()).toResult();
        }


    }






    /**
     * @description  删除加扣数
     * @title deleteDetail
     * @author lihuijie
     * @updateTime 2022-08-11 17:34
     * @throws
     * @param id:从表id
     * @return java.lang.Object
     */
    @PostMapping("/deleteDetail")
   // @RequiresPermissions("deleteDetail")
    public synchronized Object deleteDetail(@RequestParam String id) {
        try {
            final String methodName = "CargoInfoController:deleteDetail";
            LOGGER.enter(methodName + "[start]", "id:" + id);
            CargoInfoDetail cargoInfoDetail = cargoInfoDetailMapper.selectOne(new QueryWrapper<CargoInfoDetail>().eq("id", id));
            if (cargoInfoDetail != null) {
                cargoInfoDetailMapper.deleteById(id);
            }
            LOGGER.exit( methodName);
            return Response.SUCCESS.newBuilder().out("删除成功").toResult();
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("删除失败："+e.getMessage()).toResult();
        }

    }
    /**
     * @description 分货控数查询
     * @title qryFHKS
     * @author lihuijie
     * @updateTime 2023-01-31 14:59
     * @throws
     * @param cargokey:  货健
     * @return java.lang.Object
     */
    @RequestMapping("/qryFHKS")
    public Object qryFHKS(String cargokey) {
        try {
            CargoInfoRespVo btNewShwCargoinfo =new CargoInfoRespVo();
            if(cargokey !=null) {
                btNewShwCargoinfo = cargoInfoService.qryFHKS (cargokey);
            }
           return Response.SUCCESS.newBuilder().out("查询成功").toResult(btNewShwCargoinfo);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("查询失败："+e.getMessage()).toResult();
        }

    }

    /**
     * @description 分货控数保存
     * @title saveOrUpdateFhks
     * @author lihuijie
     * @updateTime 2023-01-31 16:25
     * @throws
     * @param fhksCargoInfoReqVo:  入参
     * @return java.lang.Object
     */

    @RequestMapping("/saveOrUpdateFhks")
    @PreAuthorize("hasAuthority('business:khglInfo:saveOrUpdateFhks')")
    public Object saveOrUpdateFhks(@RequestBody FhksCargoInfoReqVo fhksCargoInfoReqVo) {

        try {
            final String methodName = "CargoInfoController:saveOrUpdateFhks";
            LOGGER.enter(methodName + "[start]", "fhksCargoInfoReqVo:" + fhksCargoInfoReqVo);
            cargoInfoService.saveOrUpdateFhks (fhksCargoInfoReqVo);
            LOGGER.exit( methodName);
            return Response.SUCCESS.newBuilder().out("分货控数保存成功").toResult();
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("分货控数保存失败："+e.getMessage()).toResult();
        }

    }
}
