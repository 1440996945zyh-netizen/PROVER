package com.yy.ppm.statement.mapper.storageSettle;

import com.yy.ppm.statement.bean.dto.storageSettle.VWeightInfo;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-27 10:45
 */
public interface StorageSettleJGSGMapper {

    List<VWeightInfo> listJGWeightInfo(Long handoverlistId);

    List<VWeightInfo> listSGWeightInfo(Long handoverlistId);
}
