package com.yy.ppm.common.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 文件信息DTO
 */
@Getter
@Setter
@ToString
public class SysFileDTO extends BasePO implements Serializable {
    private static final long serialVersionUID = 1403991192565776724L;
    /**
     * 主键Id
     */
    private Long id;
    /**
     * 业务ID
     */
    private Long businessId;
    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 桶
     */
    private String fileBucket;
    /**
     * 服务器路径
     */
    private String filePath;
    /**
     * 文件保存在服务器名称
     */
    private String fileSaveName;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件显示图标
     */
    private String fileIcon;
    /**
     * 文件类型（0：临时文件 1：正式文件）
     */
    private String fileType;

    /***
     * 文件后缀
     */
    private String fileSuffix;

    private String fileUrl;
}
