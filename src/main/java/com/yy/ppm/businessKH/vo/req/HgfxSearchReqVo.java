package com.yy.ppm.businessKH.vo.req;
/**
 * @ClassName CargoInfoSearchReqVo.java
 * @author lihuijie
 * @version 1.0.0
 * @Description 控货管理查询入参VO
 * @createTime 2022-04-24 14:35
 */
import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

@Data
public class HgfxSearchReqVo extends PageParameter implements Serializable {
    private String startTm;//开始时间
    private String endTm;//结束时间
    private String zhwchm;//中文船名
    private String hwmchXl;//货物名称
    private String bgdh;//报关单号
    private String zygsdm;//作业公司代码
    private String xh;//序号
    private String xh2;//序号2
    private String lb;//类别
    private String zhlmch;//中类名称


}
