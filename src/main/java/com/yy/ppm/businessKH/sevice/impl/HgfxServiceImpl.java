package com.yy.ppm.businessKH.sevice.impl;
import com.yy.common.page.Pages;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.businessKH.mapper.BtShwHgfxMapper;
import com.yy.ppm.businessKH.mapper.CargoInfoMapper;
import com.yy.ppm.businessKH.model.BtShwHgfx;
import com.yy.ppm.businessKH.model.CargoInfo;
import com.yy.ppm.businessKH.sevice.CargoInfoService;
import com.yy.ppm.businessKH.sevice.HgfxService;
import com.yy.ppm.businessKH.vo.req.HgfxSearchReqVo;
import com.yy.ppm.businessKH.vo.req.SaveOrUpdateHgfxReqVo;
import com.yy.ppm.businessKH.vo.resp.BaseInfoRespVo;
import com.yy.ppm.businessKH.vo.resp.FxdInfoRespVo;
import com.yy.ppm.businessKH.vo.resp.HgfxRespVo;
import com.yy.ppm.businessKH.vo.resp.ZygsInfoRespVo;
import org.geotools.ows.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
     * @author lihuijie
     * @version 1.0.0
     * @ClassName HgfxServiceImpl.java
     * @Description TODO
     * @createTime 2022年05月09日 15:50:00
     */
    @Service
    public class HgfxServiceImpl  implements HgfxService {

        @Autowired
        private BtShwHgfxMapper btShwHgfxMapper;
        @Autowired
        private CargoInfoMapper cargoInfoMapper;
        @Autowired
        private CargoInfoService cargoInfoService;
        @Resource
        private SecurityUtils securityUtils;



    /**
     *
     * @param hgfxSearchReqVo: 查询入参
     * @title getHgfxList
     * @description   查询海关放行单List
     * @author lihuijie
     * @updateTime 2022-06-28 16:52
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.spgtech.businessKH.vo.resp.HgfxRespVo>
     */
    @Override
   public Pages<HgfxRespVo> getHgfxList(HgfxSearchReqVo hgfxSearchReqVo){
       Pages<HgfxRespVo> pages = PageHelperUtils.limit(hgfxSearchReqVo, () -> {
            return btShwHgfxMapper.getHgfxList(hgfxSearchReqVo);
        });

        return pages;
    };

    /**
     *
     * @param saveOrUpdateHgfxReqVo: 修改内容类
     * @title updateHgfx
     * @description  修改海关放行单
     * @author lihuijie
     * @updateTime 2022-06-28 16:53
     *
     */
    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public void updateHgfx(SaveOrUpdateHgfxReqVo saveOrUpdateHgfxReqVo) throws ParseException {
        BtShwHgfx btShwHgfx =btShwHgfxMapper.selectOneHgfxById(saveOrUpdateHgfxReqVo.getId ());
        //出口进口都需要更新的是海关放行人
        btShwHgfx.setHqcyrid(saveOrUpdateHgfxReqVo.getHqcyrid());//货权持有人id
        if(saveOrUpdateHgfxReqVo.getHqcyrid ()!=null&&saveOrUpdateHgfxReqVo.getHqcyrid ()!=""){
            btShwHgfx.setHqcyr (btShwHgfxMapper.hqcyrInfo (saveOrUpdateHgfxReqVo.getHqcyrid (),"").get (0).getHqcyr ());
        }
         //类别（进出口或者null）
        if(btShwHgfx.getLb()==null){
            btShwHgfx.setLb ("进口");
        }
        //进口和出口两种逻辑保存
        if(btShwHgfx.getLb().equals("进口")){
            CargoInfo khgl = cargoInfoMapper.selectOneByCargokey (btShwHgfx.getCargokey ());
            String note=btShwHgfx.getNote();
            String xnote=saveOrUpdateHgfxReqVo.getNote();//把查询出来的备注和新输入的备注单独存起来
            SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd");//设置日期格式
            btShwHgfx.setFxrq(df.parse (saveOrUpdateHgfxReqVo.getFxrq()));//放行日期
            //更新对应单票货海关放行人(货权持有人)
            khgl.setHqcyrid(saveOrUpdateHgfxReqVo.getHqcyrid());//货权持有人
            if(saveOrUpdateHgfxReqVo.getHqcyrid ()!=null&&saveOrUpdateHgfxReqVo.getHqcyrid ()!=""){
                khgl.setHqcyr (btShwHgfxMapper.hqcyrInfo (saveOrUpdateHgfxReqVo.getHqcyrid (),"").get (0).getHqcyr ());
            }
                        //控货方式为重量、件数或体积时，货键通关信息修改。
            if("重量".equals(khgl.getKhfs())){
                khgl.setTgs(khgl.getTgs().add(saveOrUpdateHgfxReqVo.getFxshl()).subtract(btShwHgfx.getFxshl ()));
            }
            if("件数".equals(khgl.getKhfs())){
                khgl.setTgs(khgl.getTgs().add(saveOrUpdateHgfxReqVo.getKhjsh()).subtract(btShwHgfx.getKhjsh()));
            }
            if("体积".equals(khgl.getKhfs())){
                khgl.setTgs(khgl.getTgs().add(saveOrUpdateHgfxReqVo.getKhvol()).subtract(btShwHgfx.getKhvol()));
            }

            //只有进口才需要更新单票货
            cargoInfoService.saveOrUpdate(khgl);
            //xnote不为空只能是全部通关，则更新序号和备注（传flag=1），其他的置空序号和备注（传flag=0）
            Map<String,Object> map=new HashMap<> ();
            map.put ("cargokey",btShwHgfx.getCargokey ());
            if((note==null||note.length()==0)&&(xnote!=null&&xnote.length()>0)){
                map.put("flag","1");
                btShwHgfxMapper.updateHgfxXh(map);
            }
            if((note!=null&&note.length()>0)&&(xnote==null||xnote.length()==0)){
                map.put("flag","0");
                btShwHgfxMapper.updateHgfxXh(map);
                //这两步的原因是过程已经把这两个字段置空了，但是前面查出来的btShwHgfx是有数的，需要手动置空，不然后面更新又回去了
                btShwHgfx.setXh (null);
                btShwHgfx.setNote ("");
            }

            btShwHgfx.setTdh (saveOrUpdateHgfxReqVo.getTdh ());
            btShwHgfx.setImo (saveOrUpdateHgfxReqVo.getImo ());
            btShwHgfx.setDlhc (saveOrUpdateHgfxReqVo.getDlhc ());
            btShwHgfx.setBgdh (saveOrUpdateHgfxReqVo.getBgdh ());
            btShwHgfx.setKhjsh (saveOrUpdateHgfxReqVo.getKhjsh ());
            btShwHgfx.setFxshl (saveOrUpdateHgfxReqVo.getFxshl ());

        }else{
            btShwHgfx.setNote(saveOrUpdateHgfxReqVo.getNote());//备注(出口只更新本表备注，不需要调用过程)
            SimpleDateFormat dh = new SimpleDateFormat ("yyyy-MM-dd");//设置日期格式
            btShwHgfx.setDgrq(dh.parse (saveOrUpdateHgfxReqVo.getDgrq()));//抵港日期
            btShwHgfx.setHwdmXl(saveOrUpdateHgfxReqVo.getHwdmXl());//集团货物小类代码
            btShwHgfx.setHwmchXl(saveOrUpdateHgfxReqVo.getHwmchXl());//集团货物小类名称
            btShwHgfx.setBgdh (saveOrUpdateHgfxReqVo.getBgdh ());
            btShwHgfx.setSbdwmch(saveOrUpdateHgfxReqVo.getSbdwmch ());
            btShwHgfx.setJydwmch (saveOrUpdateHgfxReqVo.getJydwmch ());
            btShwHgfx.setTdh (saveOrUpdateHgfxReqVo.getTdh ());
            btShwHgfx.setTghwmch (saveOrUpdateHgfxReqVo.getTghwmch ());
            btShwHgfx.setFxshl (saveOrUpdateHgfxReqVo.getFxshl ());
            btShwHgfx.setTgjsh (saveOrUpdateHgfxReqVo.getTgjsh ());
            btShwHgfx.setFxrq (dh.parse (saveOrUpdateHgfxReqVo.getFxrq ()));
            btShwHgfx.setDlhc (saveOrUpdateHgfxReqVo.getDlhc ());
            btShwHgfx.setImo (saveOrUpdateHgfxReqVo.getImo ());



        }
        this.saveOrUpdateHgfx(btShwHgfx);

    };

    /**
     * @description 新增或者保存海关放行表
     * @title saveOrUpdateHgfx
     * @author lihuijie
     * @updateTime 2022-06-28 17:04
     * @throws
     * @param btShwHgfx:  保存数据类
     */
    public Long saveOrUpdateHgfx(BtShwHgfx btShwHgfx) {
        Long id =null;
        //取当前登录人的信息
        UserInfo loginUserInfo = securityUtils.getUserInfo ();
        if (null == btShwHgfx.getId ()) {
            btShwHgfx.setCreateby (loginUserInfo.getUserName ());
            btShwHgfx.setCreatebyid (loginUserInfo.getId ().toString ());
            btShwHgfx.setCreateon (new Date ());

           btShwHgfxMapper.insert (btShwHgfx);
            id = btShwHgfx.getId ();
        } else {
            BtShwHgfx btShwHgfxOld = btShwHgfxMapper.selectOne (new QueryWrapper<BtShwHgfx> ().eq ("id", btShwHgfx.getId ()));
            if (btShwHgfxOld != null) {
                BeanUtils.copyProperties (btShwHgfx, btShwHgfxOld);
                btShwHgfxMapper.updateById (btShwHgfxOld);
            } else {
                btShwHgfx.setCreateby (loginUserInfo.getUserName ());
                btShwHgfx.setCreatebyid (loginUserInfo.getId ().toString ());
                btShwHgfx.setCreateon (new Date ());
                btShwHgfxMapper.insert (btShwHgfx);
            }
        }
        return id;
    }
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
    @Override
   public  List<HgfxRespVo> hqcyrInfo(String hqcyrid,String hqcyr){
        List<HgfxRespVo> hgfxRespVo =null;
           hgfxRespVo = btShwHgfxMapper.hqcyrInfo (hqcyrid,hqcyr);
        return hgfxRespVo;
    };


    /**
     * @description  删除海关放行单
     * @title deleteHgfx
     * @author lihuijie
     * @updateTime 2022-06-28 16:57
     * @throws
     * @param id:  id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public  void deleteHgfx(Long id) throws ServiceException, ParseException {
        //增加校验(放行数量大于控货剩余数的情况下，不能删除！)
        BtShwHgfx btShwHgfx =btShwHgfxMapper.selectOneHgfxById(id);
        //类别（进出口或者null）
        if(btShwHgfx.getLb()==null){
            btShwHgfx.setLb ("进口");
        }
        if(btShwHgfx.getLb().equals("进口")){
            CargoInfo khgl = cargoInfoMapper.selectOneByCargokey (btShwHgfx.getCargokey ());
            if(!khgl.getZygsdm().equals("21")&&((khgl.getKhfs().equals("重量")&&btShwHgfx.getFxshl().compareTo(khgl.getSys())==1)
                    ||(khgl.getKhfs().equals("件数")&&btShwHgfx.getKhjsh()!=null&&btShwHgfx.getKhjsh().compareTo(khgl.getSys())==1)
                    ||(khgl.getKhfs().equals("体积")&&btShwHgfx.getKhvol()!=null&&btShwHgfx.getKhvol().compareTo(khgl.getSys())==1))){
                throw new ServiceException ("放行数量/件数/体积大于控货剩余数，该放行单不允许删除！");
            }else {
                //更新控货管理数据信息
                if(khgl.getKhfs().equals("件数")){
                    khgl.setTgs(khgl.getTgs().subtract(btShwHgfx.getKhjsh()));
                }else if(khgl.getKhfs().equals("体积")){
                    khgl.setTgs(khgl.getTgs().subtract(btShwHgfx.getKhvol()));
                }else{
                //更新控货管理数据信息
                khgl.setTgs (khgl.getTgs ().subtract (btShwHgfx.getFxshl ()));
                }
                cargoInfoService.saveOrUpdate(khgl);
                //删除的数据如果是全部通关单票货，先将序号和备注置空
                if(null !=btShwHgfx.getNote()){
                    Map<String,Object> map=new HashMap<> ();
                    map.put ("cargokey",btShwHgfx.getCargokey ());
                    map.put("flag","0");
                    btShwHgfxMapper.updateHgfxXh(map);
                }
                QueryWrapper<BtShwHgfx> queryWrapper = new QueryWrapper<> ();
                queryWrapper.eq ("id", id);
                btShwHgfxMapper.delete (queryWrapper);
            }
        }else{
            QueryWrapper<BtShwHgfx> queryWrapper = new QueryWrapper<> ();
            queryWrapper.eq ("id", id);
            btShwHgfxMapper.delete (queryWrapper);
        }
    };







    /**
     * @description 作业公司信息
     * @title zygsInfo
     * @author lihuijie
     * @updateTime 2022-06-28 16:57
     * @throws
     * @return java.util.List<com.spgtech.businessKH.vo.resp.ZygsInfoRespVo>
     */
    public List<ZygsInfoRespVo> zygsInfo(){
       return btShwHgfxMapper.zygsInfo ();
    };

    /**
     * @description 查询控货管理中的票货信息(新增进出口海关放行单)
     * @title getKhxx
     * @author lihuijie
     * @updateTime 2022-06-28 16:58
     * @throws
     * @param zygsdm: 作业公司id
     * @param zhwchm: 中文船名
     * @param lb:  类别
     * @return java.util.List<com.spgtech.businessKH.vo.resp.BaseInfoRespVo>
     */
   public List<BaseInfoRespVo> getKhxx(String zygsdm, String zhwchm,String lb) throws ServiceException {
       List<BaseInfoRespVo> getKhxx =null;
//          if(zygsdm==null||zygsdm==""){
//              throw new ServiceException ("作业公司不能为空");
//          }else{
              if(lb.equals ("进口")){
                  getKhxx =  btShwHgfxMapper.getKhxxJK (zygsdm,zhwchm);
              }else{
                  getKhxx = btShwHgfxMapper.getKhxxCK (zygsdm,zhwchm);
              }

//          }
          return getKhxx;
   };

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
   public List<FxdInfoRespVo>queryHgfxInfoByBgdh(String bgdh, String lb) throws ServiceException {
       List<FxdInfoRespVo> queryHgfxInfoByBgdh =null;
       if(bgdh==null||bgdh==""){
           throw new ServiceException ("报关单号不能为空");
       }else{
           if(lb.equals ("进口")){
               queryHgfxInfoByBgdh =  btShwHgfxMapper.queryHgfxInfoJKByBgdh (bgdh);
           }else{
               queryHgfxInfoByBgdh =  btShwHgfxMapper.queryHgfxInfoCKByBgdh (bgdh);
           }

       }
       return queryHgfxInfoByBgdh;
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
     * @return java.util.List<com.spgtech.businessKH.vo.resp.FxdInfoRespVo>
     */
   public List<FxdInfoRespVo>queryHgfxInfoCKByTdhOrImoOrDlhc(String tdh,String imo,String dlhc){
      return btShwHgfxMapper.queryHgfxInfoCKByTdhOrImoOrDlhc (tdh,imo,dlhc);
   };



    /**
     * @description 海关放行单进出口保存
     * @title saveHgfxJCK
     * @author lihuijie
     * @updateTime 2022-06-28 17:00
     * @throws
     * @param saveOrUpdateHgfxReqVo:  保存数据类
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public void saveHgfxJCK(SaveOrUpdateHgfxReqVo saveOrUpdateHgfxReqVo) throws ParseException {
        BtShwHgfx btShwHgfx= new BtShwHgfx ();
        btShwHgfx.setLb (saveOrUpdateHgfxReqVo.getLb ());
        btShwHgfx.setCargokey (saveOrUpdateHgfxReqVo.getCargokey ());
        btShwHgfx.setYwchm (saveOrUpdateHgfxReqVo.getYwchm ());
        btShwHgfx.setZygsdm (saveOrUpdateHgfxReqVo.getZygsdm ());
        btShwHgfx.setHwdmXl (saveOrUpdateHgfxReqVo.getHwdmXl ());
        btShwHgfx.setHwmchXl (saveOrUpdateHgfxReqVo.getHwmchXl ());
        btShwHgfx.setHqcyrid (saveOrUpdateHgfxReqVo.getHqcyrid ());
        if(saveOrUpdateHgfxReqVo.getHqcyrid ()!=null&&saveOrUpdateHgfxReqVo.getHqcyrid ()!=""){
            btShwHgfx.setHqcyr (btShwHgfxMapper.hqcyrInfo (saveOrUpdateHgfxReqVo.getHqcyrid (),"").get (0).getHqcyr ());
        }
        //货权持有人名字自己查还没写方法
        btShwHgfx.setZhl (saveOrUpdateHgfxReqVo.getZhl ());
        btShwHgfx.setFxshl (saveOrUpdateHgfxReqVo.getFxshl ());
        SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd");//设置日期格式
        btShwHgfx.setFxrq(df.parse (saveOrUpdateHgfxReqVo.getFxrq()));//放行日期
        SimpleDateFormat dh = new SimpleDateFormat ("yyyy-MM-dd");//设置日期格式
        btShwHgfx.setDgrq(dh.parse (saveOrUpdateHgfxReqVo.getDgrq()));//抵港日期
        btShwHgfx.setTdh (saveOrUpdateHgfxReqVo.getTdh ());
        btShwHgfx.setBgdh (saveOrUpdateHgfxReqVo.getBgdh ());
        btShwHgfx.setNote (saveOrUpdateHgfxReqVo.getNote ());
        //件数控货
        btShwHgfx.setKhjsh(saveOrUpdateHgfxReqVo.getKhjsh());
        btShwHgfx.setKhvol(saveOrUpdateHgfxReqVo.getKhvol());
//		进口海关放行单IMO DLHC--郭立燕2020.8.26
        btShwHgfx.setDlhc(saveOrUpdateHgfxReqVo.getDlhc());
        btShwHgfx.setImo(saveOrUpdateHgfxReqVo.getImo());
        btShwHgfx.setXcdId (saveOrUpdateHgfxReqVo.getXcdId ());

        //以上为进出口都有的信息，下面是出口才有的字段
        btShwHgfx.setHc(saveOrUpdateHgfxReqVo.getHc());//航次这个暂未查出来，再看看
        btShwHgfx.setHghc(saveOrUpdateHgfxReqVo.getHghc());
        btShwHgfx.setSbdwmch(saveOrUpdateHgfxReqVo.getSbdwmch());
        btShwHgfx.setJydwmch(saveOrUpdateHgfxReqVo.getJydwmch());
        btShwHgfx.setTghwmch(saveOrUpdateHgfxReqVo.getTghwmch());
        btShwHgfx.setZhwchm (saveOrUpdateHgfxReqVo.getZhwchm ());//中文船名
        btShwHgfx.setTgjsh(saveOrUpdateHgfxReqVo.getTgjsh());

        Long idhgfx =  this.saveOrUpdateHgfx(btShwHgfx);

        Map<String,Object> map=new HashMap<> ();



        if(saveOrUpdateHgfxReqVo.getLb ().equals("进口")){

            if(saveOrUpdateHgfxReqVo.getNote()==null||saveOrUpdateHgfxReqVo.getNote()==""){
                map.put("cargokey",saveOrUpdateHgfxReqVo.getCargokey ());
                map.put("flag","0");
                btShwHgfxMapper.updateHgfxXh(map);
            }else if(saveOrUpdateHgfxReqVo.getNote().equals("全部通关")){
                map.put("cargokey",saveOrUpdateHgfxReqVo.getCargokey ());
                map.put("flag","1");
                btShwHgfxMapper.updateHgfxXh(map);
            }
            //更新对应单票货通关数量、可发数量、剩余数量
            CargoInfo khgl = cargoInfoMapper.selectOneByCargokey (btShwHgfx.getCargokey ());
//现在是按照都控货的方式 所以下面都注销了，只修改海关放行人和通关数，不控货的话，通关数量得加到可发数和剩余数
            if("件数".equals(khgl.getKhfs())){
                khgl.setTgs(khgl.getTgs().add(saveOrUpdateHgfxReqVo.getKhjsh()));
              //  khgl.setKfs(khgl.getKfs().add(saveOrUpdateHgfxReqVo.getKhjsh()));
              //  khgl.setSys(khgl.getSys().add(saveOrUpdateHgfxReqVo.getKhjsh()));
            }
            else if("体积".equals(khgl.getKhfs())){
                khgl.setTgs(khgl.getTgs().add(saveOrUpdateHgfxReqVo.getKhvol()));
             //   khgl.setKfs(khgl.getKfs().add(saveOrUpdateHgfxReqVo.getKhvol()));
            //    khgl.setSys(khgl.getSys().add(saveOrUpdateHgfxReqVo.getKhvol()));
            }else{
                khgl.setTgs(khgl.getTgs().add(saveOrUpdateHgfxReqVo.getFxshl()));
//                if(("11").equals (khgl.getEjzygsdm ())){
//                    //当二级作业公司是11，不控货
//                    khgl.setKfs(khgl.getKfs().add(saveOrUpdateHgfxReqVo.getFxshl()));
//                    khgl.setSys(khgl.getSys().add(saveOrUpdateHgfxReqVo.getFxshl()));
//                }else{
//                    String bTime=new String("2023-01-01");
//                    SimpleDateFormat bf = new SimpleDateFormat ("yyyy-MM-dd");
//                    Date sd1=bf.parse(bTime);
//                    if(khgl.getDgrqS ().before(sd1)){
//                        //当最初集港时间在2023-1-1之前，岚山的按照控货，其他的按照不控货
//                        if(!saveOrUpdateHgfxReqVo.getZygsdm().equals("21")){
//                            khgl.setKfs(khgl.getKfs().add(saveOrUpdateHgfxReqVo.getFxshl()));
//                            khgl.setSys(khgl.getSys().add(saveOrUpdateHgfxReqVo.getFxshl()));
//                        }
//                    }
//                }
//
            }
                khgl.setHqcyrid(saveOrUpdateHgfxReqVo.getHqcyrid());
                if(saveOrUpdateHgfxReqVo.getHqcyrid ()!=null&&saveOrUpdateHgfxReqVo.getHqcyrid ()!=""){
                    khgl.setHqcyr (btShwHgfxMapper.hqcyrInfo (saveOrUpdateHgfxReqVo.getHqcyrid (),"").get (0).getHqcyr ());
                }
                cargoInfoService.saveOrUpdate(khgl);

        }
    };



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
   public List<BaseInfoRespVo> hwInfo(String zygsdm, String hwmchXl) throws ServiceException {
       List<BaseInfoRespVo> queryHwInfo =null;
//       if(zygsdm==null||zygsdm==""){
//           throw new ServiceException ("作业公司不能为空");
//       }else{

           queryHwInfo =  btShwHgfxMapper.hwInfo (zygsdm,hwmchXl);


    //   }
       return queryHwInfo;

   };




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
   public List<HgfxRespVo>queryHgfxMassInfo(String lb,String bgdh){

       if(lb.equalsIgnoreCase("出口")){
           return btShwHgfxMapper.queryHgfxMassInfock(bgdh);
       }else{
           return btShwHgfxMapper.queryHgfxMassInfo(bgdh);
       }
   };

}
