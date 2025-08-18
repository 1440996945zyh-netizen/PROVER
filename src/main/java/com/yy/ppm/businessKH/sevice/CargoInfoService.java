package com.yy.ppm.businessKH.sevice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yy.common.page.Pages;
import com.yy.ppm.businessKH.model.CargoInfo;
import com.yy.ppm.businessKH.model.CargoInfoDetail;
import com.yy.ppm.businessKH.model.CargoInfoVW;
import com.yy.ppm.businessKH.vo.req.CargoInfoSearchReqVo;
import com.yy.ppm.businessKH.vo.req.FhksCargoInfoReqVo;
import com.yy.ppm.businessKH.vo.req.PartCargoInfoReqVo;
import com.yy.ppm.businessKH.vo.resp.CargoInfoRespVo;
import org.geotools.ows.ServiceException;

import java.text.ParseException;

/**
 * @author lihuijie
 * @version 1.0.0
 * @ClassName CargoInfoService.java
 * @Description TODO
 * @createTime 2022年04月24日 15:50:00
 */
public interface CargoInfoService {

    /**
     *
     * @param cargoInfoSearchReqVo: 查询入参
     * @title getCargoInfoList
     * @description 查询控货管理list
     * @author 李慧洁
     * @updateTime 2022-06-28 16:33
     *
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.spgtech.businessKH.model.CargoInfoVW>
     */
    Pages<CargoInfoVW> getCargoInfoList(CargoInfoSearchReqVo cargoInfoSearchReqVo);

    /**
     *
     * @param cargokey: 主键
     * @title selectOneByCargokey
     * @description 查询单条货键加扣数详情
     * @author 李慧洁
     * @updateTime 2022-06-28 16:34
     * @return com.spgtech.businessKH.vo.resp.CargoInfoRespVo
     */
   CargoInfoRespVo selectOneByCargokey(String cargokey);



    /**
     *
     * @param cargoInfoDetail: 保存的数据类
     * @title updateJKS
     * @description 加扣数保存
     * @author 李慧洁
     * @updateTime 2022-06-28 16:37
     *
     */
   void updateJKS(CargoInfoDetail cargoInfoDetail) throws ParseException;


    /**
     *
     * @param btShwCargoinfo: 保存主表的数据类
     * @title saveOrUpdate
     * @description 新增或者保存控货主表信息
     * @author 李慧洁
     * @updateTime 2022-06-28 16:37
     *
     */
    String saveOrUpdate(CargoInfo btShwCargoinfo);



    /**
     *
     * @param cargokey: 查询主键
     * @title getKhglByCargokey
     * @description 通过货健查询控货详情
     * @author 李慧洁
     * @updateTime 2022-06-28 16:38
     * @return com.spgtech.businessKH.vo.resp.CargoInfoRespVo
     */
   CargoInfoRespVo getKhglByCargokey(String cargokey);






    /**
     * @description 查询分货控数
     * @title qryFHKS
     * @author lihuijie
     * @updateTime 2023-01-31 13:54
     * @throws
     * @param cargokey:  货健
     */
    CargoInfoRespVo qryFHKS(String cargokey);

    /**
     * @description  分货控数保存
     * @title saveOrUpdateFhks
     * @author lihuijie
     * @updateTime 2023-01-31 16:03
     * @throws
     * @param fhksCargoInfoReqVo:  分货控数保存入参
     */
    void saveOrUpdateFhks(FhksCargoInfoReqVo fhksCargoInfoReqVo) throws javax.xml.rpc.ServiceException;
}
