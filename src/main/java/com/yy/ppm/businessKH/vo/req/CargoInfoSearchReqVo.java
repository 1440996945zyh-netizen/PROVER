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
public class CargoInfoSearchReqVo extends PageParameter implements Serializable {
    private String zhwchm;//船名
    private String hwmch;//货物名称
    private String zywtr;//作业委托人
    private String hz;//货主
    private String hqcyr;//货权持有人
    private String hthLsh;//合同号岚山
    private String hth;//合同号
    private String zygsdm;//作业公司代码

    private String dgrqSStartTime;//最初集港计划开始日期

    private String dgrqSEndTime;//最初集港计划结束日期

    private String dgrqEStartTime;//最终集港计划开始日期

    private String dgrqEEndTime;//最终集港计划结束日期
    private String flag;//标志位（是否归档）
    /**
     * 手机查询名称
     */
    private String mch;
}
