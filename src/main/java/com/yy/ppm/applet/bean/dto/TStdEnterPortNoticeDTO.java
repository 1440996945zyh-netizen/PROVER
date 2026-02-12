package com.yy.ppm.applet.bean.dto;


import com.yy.ppm.applet.bean.po.TStdEnterPortNoticePO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName 入港公告(TStdEnterPortNotice)DTO
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月01日 14:08:00
 */
@Data
public class TStdEnterPortNoticeDTO extends TStdEnterPortNoticePO {

    private static final long serialVersionUID = 189029197808890980L;

	private Integer flag;//0代表删除，1代表新增，2代表更新

    private List<String> noticeRoleCodes;
    private List<String> noticeRoleNames;

}
