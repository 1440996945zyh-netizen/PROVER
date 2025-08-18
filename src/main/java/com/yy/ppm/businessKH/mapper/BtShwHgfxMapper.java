package com.yy.ppm.businessKH.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.yy.ppm.businessKH.model.BtShwHgfx;
import com.yy.ppm.businessKH.vo.req.HgfxSearchReqVo;
import com.yy.ppm.businessKH.vo.resp.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author lihuijie
 * @version 1.0.0
 * @ClassName BtShwHgfxMapper.java
 * @Description TODO 控货管理
 * @createTime 2022年05月09日 17:58:00
 */
@Mapper
@Repository
public interface BtShwHgfxMapper extends BaseMapper<BtShwHgfx> {
    /**
     * @title  查询海关放行单LIst
     * @description
     * @author lihuijie
     * @updateTime 2022-05-09 17:24
     * @throws
     */
   // public  Page<HgfxRespVo> getHgfxList(HgfxSearchReqVo hgfxSearchReqVo);

    /**
     * @title  查询海关放行单LIst
     * @description
     * @author lihuijie
     * @updateTime 2022-05-09 17:24
     * @throws
     */
     Page<HgfxRespVo> getHgfxList(@Param ("hgfxSearchReqVo") HgfxSearchReqVo hgfxSearchReqVo);

    /**
     * @title  查询单条海关放行单通过id
     * @description
     * @author lihuijie
     * @updateTime 2022-05-10 11:26
     * @throws
     */
    BtShwHgfx selectOneHgfxById(Long id);

    /**
     * @title 更新序号
     * @description
     * @author lihuijie
     * @updateTime 2022-05-11 11:19
     * @throws
     */
    void updateHgfxXh(Map map);

    /**
     * @title 海关放行人信息
     * @description
     * @author lihuijie
     * @updateTime 2022-05-13 17:00
     * @throws
     */
    List<HgfxRespVo> hqcyrInfo(String hqcyrid, String hqcyr);

    /**
     * @description 作业委托人信息
     * @title zywtrInfo
     * @author lihuijie
     * @updateTime 2023-02-23 15:31
     * @throws
     * @param zywtrid: 作业委托人id
     * @param zywtr:  作业委托人
     * @return java.util.List<com.spgtech.businessKH.vo.resp.ZywtrInfoRespVo>
     */
    List<ZywtrInfoRespVo>zywtrInfo(String zywtrid, String zywtr);

    /**
     * @title 作业公司信息
     * @description
     * @author lihuijie
     * @updateTime 2022-05-17 10:04
     * @throws
     */
    List<ZygsInfoRespVo> zygsInfo();

    /**
     * @title 查询控货管理中的票货信息(进口海关放行单新增)
     * @description
     * @author lihuijie
     * @updateTime 2022-05-17 11:14
     * @throws
     */
    List<BaseInfoRespVo> getKhxxJK(String zygsdm, String zhwchm);

    /**
     * @title 查询控货管理中的票货信息(出口海关放行单新增)
     * @description
     * @author lihuijie
     * @updateTime 2022-05-17 11:14
     * @throws
     */
    List<BaseInfoRespVo> getKhxxCK(String zygsdm, String zhwchm);

    /**
     * @title  查询海关放行信息进口通过报关单号
     * @description
     * @author lihuijie
     * @updateTime 2022-05-17 14:56
     * @throws
     */
    List<FxdInfoRespVo>queryHgfxInfoJKByBgdh(String bgdh);

    /**
     * @title  查询海关放行信息出口通过报关单号
     * @description
     * @author lihuijie
     * @updateTime 2022-05-17 14:56
     * @throws
     */
    List<FxdInfoRespVo>queryHgfxInfoCKByBgdh(String bgdh);

    /**
     * @title  查询海关放行信息出口通过提单号、IMO、代理航次
     * @description
     * @author lihuijie
     * @updateTime 2022-05-17 14:56
     * @throws
     */
    List<FxdInfoRespVo>queryHgfxInfoCKByTdhOrImoOrDlhc(@Param("tdh") String tdh, @Param("imo") String imo, @Param("dlhc") String dlhc);


    /**
     * @title 查询货物信息通过作业公司代码
     * @description
     * @author lihuijie
     * @updateTime 2022-05-20 14:14
     * @throws
     */
    List<BaseInfoRespVo> hwInfo(String zygsdm, String hwmchXl);




    /**
     * @description 批量新增海关放行单时，海关信息查询（进口）
     * @title queryHgfxMassInfo
     * @author lihuijie
     * @updateTime 2022-10-11 11:33
     * @throws
     * @param bgdh:  报关单号
     * @return java.util.List<com.spgtech.businessKH.vo.resp.HgfxRespVo>
     */
   List<HgfxRespVo>queryHgfxMassInfo(String bgdh);


    /**
     * @description 批量新增出口海关放行单时，海关信息查询（出口）
     * @title queryHgfxMassInfock
     * @author lihuijie
     * @updateTime 2022-10-11 11:33
     * @throws
     * @param bgdh:  报关单号
     * @return java.util.List<com.spgtech.businessKH.vo.resp.HgfxRespVo>
     */
    List<HgfxRespVo>queryHgfxMassInfock(String bgdh);



}
