package com.yy.ppm.master.bean.dto;

import com.yy.ppm.master.bean.po.MPortPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 港口信息DTO
 * @author yangcl
 * */
@Getter
@Setter
@ToString
public class MPortDTO extends MPortPO implements Serializable {
    private static final long serialVersionUID = -7328782298769814176L;

}
