package com.yy.ppm.businessKH.vo.resp;

import lombok.Data;

/**
 * @author 李慧洁
 * @date 2021-09-08
 * @desciption 公司信息查询结果vo
 */
@Data
public class CompanyRespVo {

    /**
     * 公司ID
     */
    private String orgid;
    /**
     * 公司名称
     */
    private String org;

    /**
     * 二级单位
     */
    private String dep;

    /**
     * 二级单位ID
     */
    private String depid;

    /**
     * 登录人账号
     */
    private String accounts;

    /**
     * 登录人姓名
     */
    private String name;

    /**
     * 角色1
     */
    private Boolean roleOne;

    /**
     * 角色2
     */
    private Boolean roleTwo;


}
