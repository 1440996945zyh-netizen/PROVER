package com.yy.ppm.common.bean.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * file实体类
 * @date 2021/2/23 11:46
 * @author ChenLP
 */
@Getter
@Setter
@ToString
public class SysFilePO extends BasePO implements Serializable {

    private static final long serialVersionUID = -39211053247997902L;
    /**
     * 主键Id
     */
    private Long id;
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
     * 文件url
     */
    private String fileUrl;
    /***
     * 文件后缀
     */
    private String fileSuffix;
}
