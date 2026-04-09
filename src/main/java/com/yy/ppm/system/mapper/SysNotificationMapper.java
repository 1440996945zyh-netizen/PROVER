package com.yy.ppm.system.mapper;

import com.yy.ppm.system.bean.dto.SysNotificationDTO;
import com.yy.ppm.system.bean.po.SysNotificationPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysNotificationMapper {

    /**
     * 新增消息通知
     */
    void insert(SysNotificationPO po);

    /**
     * 查询消息列表（最近N条）
     */
    List<SysNotificationDTO> getList(@Param("receiverId") Long receiverId, @Param("limit") int limit);

    /**
     * 根据接收人ID查询消息数量
     */
    int countByReceiverId(@Param("receiverId") Long receiverId);
}
