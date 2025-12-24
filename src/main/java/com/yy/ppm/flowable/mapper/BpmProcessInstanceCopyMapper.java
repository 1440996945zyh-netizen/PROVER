package com.yy.ppm.flowable.mapper;
import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceCopySearchDTO;
import com.yy.ppm.flowable.bean.po.BpmProcessInstanceCopyPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BpmProcessInstanceCopyMapper {

    /**
     * 列表查询
     */
    Pages<BpmProcessInstanceCopyPO> selectPage(BpmProcessInstanceCopySearchDTO reqVO);


    /**
     * 删除抄送流程
     * @param processInstanceId
     */
    public void deleteByProcessInstanceId(String processInstanceId);

    /**
     * 新增
     * @param copyList
     */
    void insertBatch(List<BpmProcessInstanceCopyPO> copyList);
}
