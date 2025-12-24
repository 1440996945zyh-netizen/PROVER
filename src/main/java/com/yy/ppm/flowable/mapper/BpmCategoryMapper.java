package com.yy.ppm.flowable.mapper;
import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.flowable.bean.dto.BpmCategorySearchDTO;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.Collection;
import java.util.List;

/**
 * BPM 流程分类 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface BpmCategoryMapper {
    /**
     * 根据名称或者编码查询
     * @param value
     * @return
     */
    public BpmCategoryPO selectBy(String value);

    /**
     * 根据编码list查询
     * @param codes
     * @return
     */
    List<BpmCategoryPO> selectListByCode(@Param("codes") Collection<String> codes);

    /**
     * 保存
     * @param category
     * @return
     */
    @Edit
    int insert(BpmCategoryPO category);

    /**
     * 更新
     */
    @Edit
    int updateById(BpmCategoryPO updateObj);


    /**
     * 删除流程分类
     *
     * @param id 编号
     */
    int deleteById(Long id);

    /**
     * 查看详情
     * @param id
     * @return
     */
    BpmCategoryPO selectById(Long id);

    /**
     * 获得流程分类分页
     *
     * @param pageReqVO 分页查询
     * @return 流程分类分页
     */
    Page<BpmCategoryPO> getList(BpmCategorySearchDTO pageReqVO);
}
