package com.yy.ppm.master.bean.po;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 作业工艺工人配置
 * @author yangcl
 * */
@Data
public class MOperationTechnologyWorkerPO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 工人岗位code 字典WORKER_POST
     */
    private String workerPost;

    /**
     * 工人岗位名称
     */
    private String workerPostName;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 工艺ID
     */
    private Long technologyId;

    private static final long serialVersionUID = 1L;
}

