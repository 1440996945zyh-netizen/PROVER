package com.yy.ppm.businessKH.vo.resp;
/**
 * @ClassName CargoInfoSearchReqVo.java
 * @author lihuijie
 * @version 1.0.0
 * @Description 海关放行查询出参VO
 * @createTime 2022-05-09 14:35
 */
import lombok.Data;

import java.io.Serializable;

@Data
public class ZygsInfoRespVo implements Serializable {

    private String zygsdm;//作业公司代码
    private String zygsmch;//作业公司名称


}
