package com.yy.ppm.businessKH.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.yy.ppm.businessKH.model.CargoInfo;
import com.yy.ppm.businessKH.model.CargoInfoDetail;
import com.yy.ppm.businessKH.model.CargoInfoVW;
import com.yy.ppm.businessKH.vo.req.CargoInfoSearchReqVo;
import com.yy.ppm.businessKH.vo.resp.CargoInfoRespVo;
import com.yy.ppm.businessKH.vo.resp.ZywtrInfoRespVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author lihuijie
 * @version 1.0.0
 * @ClassName CargoInfoMapper.java
 * @Description TODO 控货管理
 * @createTime 2022年04月24日 14:58:00
 */
@Mapper
@Repository
public interface CargoInfoMapper extends BaseMapper<CargoInfo> {
    /**
     * @title 查询控货管理list
     * @description
     * @author lihuijie
     * @updateTime 2022-04-24 14:36
     * @throws
     */
    Page<CargoInfoVW> getCargoInfoList(@Param("cargoInfoSearchReqVo") CargoInfoSearchReqVo cargoInfoSearchReqVo);

    /**
     * @title  查询单条货键（查询比较快）
     * @description
     * @author lihuijie
     * @updateTime 2022-05-04 11:26
     * @throws
     */

    CargoInfoRespVo selectOneByCargokey(String cargokey);


    /**
     * @title 查询加扣数详情（通过货健）
     * @description
     * @author lihuijie
     * @updateTime 2022-05-04 13:32
     * @throws
     */
    List<CargoInfoDetail> selectCargoInfoDetailByCargokey(String cargokey);

    /**
     * @title  查询单条货健的其他控货数据(公路疏港量/铁路疏港量/计划装船量等)
     * @description
     * @author lihuijie
     * @updateTime 2022-05-06 11:43
     * @throws
     */
    CargoInfoRespVo selectOneKhglqtByCargoKey(Map map);

    /**
     * @title 按航次查询船舶计划号
     * @description
     * @author lihuijie
     * @updateTime 2022-05-07 16:01
     * @throws
     */
    List<String> queryChbJhhByHc(String hc);



    /**
     * @description 加扣数分类汇总
     * @title getTypeLb
     * @author lihuijie
     * @updateTime 2022-12-01 14:48
     * @throws
     * @param cargokey: 主键
     * @return java.util.Map
     */
    List<Map> getTypeLb(String cargokey);

    /**
     * @description 查询作业委托人
     * @title getZywtrInfo
     * @author lihuijie
     * @updateTime 2023-01-17 14:55
     * @throws
     * @param zywtrid:  作业委托人id
     * @return com.spgtech.businessKH.vo.resp.ZywtrInfoRespVo
     */
    ZywtrInfoRespVo getZywtrInfo(String zywtrid);




    /**
     * @title 根据货健查询库存数
     * @description
     * @author lihuijie
     * @updateTime 2022-05-30 14:10
     * @throws
     */
    String kc(String cargokey);
    /**
     * @title 根据货健查询欠款
     * @description
     * @author lihuijie
     * @updateTime 2022-05-30 14:10
     * @throws
     */
    String qk(String cargokey);
    /**
     * @title 根据货健查询公路疏港计划量
     * @description
     * @author lihuijie
     * @updateTime 2022-05-30 14:10
     * @throws
     */
    String jhglshgl(String cargokey);
    /**
     * @title 根据货健查询铁路疏港计划量
     * @description
     * @author lihuijie
     * @updateTime 2022-05-30 14:10
     * @throws
     */
    String jhtlshgl(String cargokey);
    /**
     * @title 根据货健查询计划装船量
     * @description
     * @author lihuijie
     * @updateTime 2022-05-30 14:10
     * @throws
     */
    String jhzhchl(String cargokey);
}
