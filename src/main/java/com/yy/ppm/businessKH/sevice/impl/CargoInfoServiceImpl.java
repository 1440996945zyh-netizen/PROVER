package com.yy.ppm.businessKH.sevice.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.businessKH.mapper.CargoInfoDetailMapper;
import com.yy.ppm.businessKH.mapper.CargoInfoFhksMapper;
import com.yy.ppm.businessKH.mapper.CargoInfoMapper;
import com.yy.ppm.businessKH.model.CargoInfo;
import com.yy.ppm.businessKH.model.CargoInfoDetail;
import com.yy.ppm.businessKH.model.CargoInfoFhks;
import com.yy.ppm.businessKH.model.CargoInfoVW;
import com.yy.ppm.businessKH.sevice.CargoInfoService;
import com.yy.ppm.businessKH.vo.req.CargoInfoSearchReqVo;
import com.yy.ppm.businessKH.vo.req.FhksCargoInfoReqVo;
import com.yy.ppm.businessKH.vo.resp.CargoInfoRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.text.ParseException;

import java.util.*;

/**
     * @author lihuijie
     * @version 1.0.0
     * @ClassName CargoInfoServiceImpl.java
     * @Description TODO
     * @createTime 2022年04月24日 15:50:00
     */
    @Service
    public class CargoInfoServiceImpl implements CargoInfoService {

        @Resource
        private SecurityUtils securityUtils;

        @Autowired
        private CargoInfoMapper cargoInfoMapper;

        @Autowired
        private CargoInfoDetailMapper cargoInfoDetailMapper;


    //    @Autowired
    //    PublicResourceMapper publicResourceMapper;

   //     @Autowired
   //     private LogUtil logUtil;

        @Autowired
        CargoInfoFhksMapper cargoInfoFhksMapper;



    /**
     *
     * @param cargoInfoSearchReqVo: 查询入参
     * @title getCargoInfoList
     * @description 查询控货管理list
     * @author 李慧洁
     * @updateTime 2022-06-28 16:33
    * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.spgtech.businessKH.model.CargoInfoVW>
     */
 @Override
   public Pages<CargoInfoVW> getCargoInfoList(CargoInfoSearchReqVo cargoInfoSearchReqVo){
     Pages<CargoInfoVW> pages = PageHelperUtils.limit(cargoInfoSearchReqVo, () -> {
         return cargoInfoMapper.getCargoInfoList(cargoInfoSearchReqVo);
     });
     return pages;

   };
     /**
      *
      * @param cargokey: 主键
      * @title selectOneByCargokey
      * @description 查询单条货键加扣数详情
      * @author 李慧洁
      * @updateTime 2022-06-28 16:34
      * @return com.spgtech.businessKH.vo.resp.CargoInfoRespVo
      */
   public CargoInfoRespVo selectOneByCargokey(String cargokey){
           CargoInfoRespVo selectOneByCargokey = cargoInfoMapper.selectOneByCargokey (cargokey);
           List<CargoInfoDetail> cargoInfoDetail = cargoInfoMapper.selectCargoInfoDetailByCargokey (cargokey);
           selectOneByCargokey.setCargoInfoDetailList (cargoInfoDetail);
       return selectOneByCargokey;
   };


    /**
     *
     * @param cargoInfoDetail: 保存的数据类
     * @title updateJKS
     * @description 加扣数保存
     * @author 李慧洁
     * @updateTime 2022-06-28 16:37
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
   public void updateJKS(CargoInfoDetail cargoInfoDetail) throws ParseException {
     //  取当前登录人的信息
       UserInfo loginUserInfo = securityUtils.getUserInfo ();
    //   对类赋值
       cargoInfoDetail.setCreateby(loginUserInfo.getUserName ());
       cargoInfoDetail.setCreatebyid(loginUserInfo.getId().toString());
       cargoInfoDetail.setCreateon (new Date ());
        cargoInfoDetail.setCreateBmid (loginUserInfo.getDeptId ().toString ());
        cargoInfoDetail.setCreateBmmch (loginUserInfo.getDeptName ());
       if(cargoInfoDetail.getLb ().equals ("扣数")){
           BigDecimal a =new BigDecimal(0);
           BigDecimal  ksshl =a.subtract(cargoInfoDetail.getCdshl());//过来的扣数都是正数，得变成负数
           cargoInfoDetail.setCdshl (ksshl);
       }
       //保存货键从表加扣数
       cargoInfoDetailMapper.insert(cargoInfoDetail);

       //组织获取主表sum(js)
       List<CargoInfoDetail> btShwCargoinfoDetailjs = cargoInfoDetailMapper.selectList
               (new QueryWrapper<CargoInfoDetail> ().eq("cargokey", cargoInfoDetail.getCargokey()).eq("lb", "加数").isNull ("status"));
       BigDecimal sumjs = BigDecimal.valueOf(0);
       for (CargoInfoDetail shwCargoinfoDetail : btShwCargoinfoDetailjs) {
           sumjs = sumjs.add(shwCargoinfoDetail.getCdshl());
       }
       //组织获取主表sum(ks)
       List<CargoInfoDetail> btShwCargoinfoDetailks = cargoInfoDetailMapper.selectList
               (new QueryWrapper<CargoInfoDetail>().eq("cargokey", cargoInfoDetail.getCargokey()).eq("lb", "扣数").isNull ("status"));
       BigDecimal sumks = BigDecimal.valueOf(0);
       for (CargoInfoDetail shwCargoinfoDetail : btShwCargoinfoDetailks) {
           sumks = sumks.add(shwCargoinfoDetail.getCdshl());
       }
       //更新主表加数扣数
       CargoInfo btShwCargoinfo = cargoInfoMapper.selectOne(new QueryWrapper<CargoInfo>().eq("cargokey", cargoInfoDetail.getCargokey()));
       btShwCargoinfo.setSumJs(sumjs);//加数
       btShwCargoinfo.setSumKs(sumks);//扣数
       //全部按照控货逻辑
       btShwCargoinfo.setKfs (sumjs.add (sumks));//可发数=加数和（正数）+扣数和（负数）
       btShwCargoinfo.setSys (btShwCargoinfo.getKfs ().subtract (btShwCargoinfo.getXds ()));//剩余数=可发数-下达数
       saveOrUpdate(btShwCargoinfo);
   };


    /**
     *
     * @param btShwCargoinfo: 保存主表的数据类
     * @title saveOrUpdate
     * @description 新增或者保存控货主表信息
     * @author 李慧洁
     * @updateTime 2022-06-28 16:37
     *
     */
    public String saveOrUpdate(CargoInfo btShwCargoinfo) {
        String cargoKey =null;
        //取当前登录人的信息
        UserInfo loginUserInfo = securityUtils.getUserInfo ();
        if (null == btShwCargoinfo.getCargokey ()) {
            btShwCargoinfo.setCreateby(loginUserInfo.getUserName ());
            btShwCargoinfo.setCreatebyid(loginUserInfo.getId ().toString ());
            btShwCargoinfo.setCreateon(new Date ());
            btShwCargoinfo.setCreateBmid(loginUserInfo.getDeptId ().toString ());
            btShwCargoinfo.setCreateBmmch(loginUserInfo.getDeptName ());
            cargoInfoMapper.insert (btShwCargoinfo);
           cargoKey =  btShwCargoinfo.getCargokey ();
        } else {
            CargoInfo btShwCargoinfoOld = cargoInfoMapper.selectOne (new QueryWrapper<CargoInfo> ().eq ("cargokey", btShwCargoinfo.getCargokey ()));
            if (btShwCargoinfoOld != null) {
                BeanUtils.copyProperties (btShwCargoinfo, btShwCargoinfoOld);
                btShwCargoinfoOld.setModifyby (loginUserInfo.getUserName ());
                btShwCargoinfoOld.setModifybyid (loginUserInfo.getId ().toString ());
                btShwCargoinfoOld.setModifyon (new Date ());
                cargoInfoMapper.updateById (btShwCargoinfoOld);
            } else {
                btShwCargoinfo.setCreateby(loginUserInfo.getUserName ());
                btShwCargoinfo.setCreatebyid(loginUserInfo.getId ().toString ());
                btShwCargoinfo.setCreateon(new Date ());
                btShwCargoinfo.setCreateBmid(loginUserInfo.getDeptId ().toString ());
                btShwCargoinfo.setCreateBmmch(loginUserInfo.getDeptName ());
                cargoInfoMapper.insert (btShwCargoinfo);
            }
        }
        return cargoKey;
    }


        /**
         *
         * @param cargokey: 查询主键
         * @title getKhglByCargokey
         * @description 通过货健查询控货详情
         * @author 李慧洁
         * @updateTime 2022-06-28 16:38
         * @return com.spgtech.businessKH.vo.resp.CargoInfoRespVo
         */
      public CargoInfoRespVo getKhglByCargokey(String cargokey){

          CargoInfoRespVo ct =cargoInfoMapper.selectOneByCargokey(cargokey);
          ct.setKc (cargoInfoMapper.kc(cargokey));
          ct.setQk (cargoInfoMapper.qk (cargokey));
          ct.setGlshgl (cargoInfoMapper.jhglshgl (cargokey));
          ct.setTlshgl (cargoInfoMapper.jhtlshgl (cargokey));
          ct.setJhzhchl (cargoInfoMapper.jhzhchl (cargokey));

          //原系统是用的过程取 但是取出来是游标 不知道哪个字段是哪个字段 没用
          Map<String,Object> map=new HashMap<> ();
          map.put ("cargokey",cargokey);
          cargoInfoMapper.selectOneKhglqtByCargoKey(map);
          if(map.get("result")!=null&&ct!=null){
              ArrayList<Map<String,Object>> cursorList=(ArrayList<Map<String,Object>>)map.get("result");
              if(cursorList!=null&&cursorList.size()!=0){
                  for (Map<String, Object> qt : cursorList) {
//                    //   ct.setGlshgl(qt.get(23).toString ());//公路疏港量
//                      ct.setGlshgl(qt.get(":B12").toString ());//公路疏港量
//                      //  ct.setTlshgl(qt.get(25).toString ());//铁路疏港量
//                      ct.setTlshgl(qt.get(":B11").toString ());//铁路疏港量
//                      // ct.setJhzhchl(qt.get(26).toString ());//计划装船量
//                      ct.setJhzhchl(qt.get(":B18").toString ());//计划装船量
//                      //  ct.setShjzhchl(qt.get(27).toString ());//实际装船量
//                      ct.setShjzhchl(qt.get(":B17").toString ());//实际装船量
//                      // ct.setQk(qt.get(29).toString ());//欠款
//                      ct.setQk(qt.get(":B18").toString ());//欠款
//                      //    ct.setQkchb(qt.get(30).toString ());//船舶欠款
//                      ct.setQkchb(qt.get(":B17").toString ());//船舶欠款
//                      //  ct.setQkcus(qt.get(31).toString ());//客户欠款
//                      ct.setQkcus(qt.get(":B16").toString ());//客户欠款
//                      // ct.setKc(qt.get(32).toString ());//库存
//                      ct.setKc(qt.get("::B37").toString ());//库存
                  }
              }
          }
              return ct;

      }



    /**
     * @description 查询分货控数
     * @title qryFHKS
     * @author lihuijie
     * @updateTime 2023-01-31 13:54
     * @throws
     * @param cargokey:  货健
     */
   public CargoInfoRespVo qryFHKS(String cargokey){

       CargoInfoRespVo selectOneByCargokey = cargoInfoMapper.selectOneByCargokey (cargokey);
       List<CargoInfoFhks> cargoInfoFhks = cargoInfoFhksMapper.getCargoInfoFhksList (cargokey);
       if(cargoInfoFhks!=null && cargoInfoFhks.size()>0) {
           selectOneByCargokey.setFhks (cargoInfoFhks.get (0).getFhks ());
           selectOneByCargokey.setJhy (cargoInfoFhks.get (0).getJhy ());
           selectOneByCargokey.setNote (cargoInfoFhks.get (0).getNote ());
       }
       return selectOneByCargokey;
   };

    /**
     * @description  分货控数保存
     * @title saveOrUpdateFhks
     * @author lihuijie
     * @updateTime 2023-01-31 16:03
     * @throws
     * @param fhksCargoInfoReqVo:  分货控数保存入参
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public void saveOrUpdateFhks(FhksCargoInfoReqVo fhksCargoInfoReqVo) throws ServiceException {
        //取当前登录人的信息
        UserInfo loginUserInfo = securityUtils.getUserInfo ();
        if(fhksCargoInfoReqVo.getCargokey ()==null||fhksCargoInfoReqVo.getCargokey ()==""){
            throw  new ServiceException("请刷新后重试");
        }else{
           String fhkscount = cargoInfoFhksMapper.getFhksCount (fhksCargoInfoReqVo.getCargokey ());
           if(fhkscount.equals ("0")){
               CargoInfoFhks cargoInfoFhks =new CargoInfoFhks();
               cargoInfoFhks.setCargokey (fhksCargoInfoReqVo.getCargokey ());
               cargoInfoFhks.setFhks (fhksCargoInfoReqVo.getFhks ());
               cargoInfoFhks.setKhrq (new Date ());
               cargoInfoFhks.setJhy (loginUserInfo.getUserName ());
               cargoInfoFhks.setNote (fhksCargoInfoReqVo.getNote ());
               cargoInfoFhks.setCreateby (loginUserInfo.getUserName ());
               cargoInfoFhks.setCreateon (new Date ());
               cargoInfoFhks.setCreatebyid (loginUserInfo.getId ().toString ());
               cargoInfoFhks.setCreateBmid (loginUserInfo.getDeptId ().toString ());
               cargoInfoFhks.setCreateBmmch (loginUserInfo.getDeptName ());
               cargoInfoFhksMapper.insert (cargoInfoFhks);
           }else{
               CargoInfoFhks cargoInfoFhks =new CargoInfoFhks();
               cargoInfoFhks.setCargokey (fhksCargoInfoReqVo.getCargokey ());
               cargoInfoFhks.setFhks (fhksCargoInfoReqVo.getFhks ());
               cargoInfoFhks.setKhrq (new Date ());
               cargoInfoFhks.setJhy (loginUserInfo.getUserName ());
               cargoInfoFhks.setNote (fhksCargoInfoReqVo.getNote ());
               cargoInfoFhks.setModifyby (loginUserInfo.getUserName ());
               cargoInfoFhks.setModifybyid (loginUserInfo.getId ().toString ());
               cargoInfoFhks.setModifyon (new Date ());
               cargoInfoFhksMapper.updateById (cargoInfoFhks);

           }
        }
    };

}
