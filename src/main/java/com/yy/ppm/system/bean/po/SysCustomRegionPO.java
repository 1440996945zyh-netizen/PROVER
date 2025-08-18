package com.yy.ppm.system.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * (SysCustomRegion)PO
 *
 * @author 张超
 * @date 2021-07-27 11:34:37
 */
@Getter
@Setter
@ToString
public class SysCustomRegionPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 999246007063785765L;


    /**主键*/
    private Long id;
    /**登录账号*/
    private String userAccount;
    private Long userId;
    private Long menuId;
    /**名称*/
    private String menuName;
    /**前端路由*/
    private String menuRouter;
    /**排序号*/
    private Integer sortNum;
}
