package com.yy.ppm.common.bean.dto;

import lombok.Data;
import java.util.List;

@Data
public class FileRelationDTO {
    private List<Long> fileIds;
    private String businessId;
}