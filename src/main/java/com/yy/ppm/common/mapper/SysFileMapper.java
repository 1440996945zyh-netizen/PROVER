package com.yy.ppm.common.mapper;


import com.yy.framework.annotation.Edit;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.SysFilePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (SysFile)表数据库访问层
 */
public interface SysFileMapper {

    /**
     * 通过id查询文件信息
     *
     * @param id 主键
     * @return 实例对象
     */
    SysFilePO getFileById(Long id);

    /**
     * 按业务获取附件信息
     *
     * @param businessType 业务类型
     * @param businessId 业务id
     * @return SysFile对象
     */
    List<SysFileDTO> getBusFiles(Long businessId, String businessType);

    /**
     * 查询文件信息
     *
     * @param file 查询条件
     * @return 实例对象集合
     */
    List<SysFileDTO> getFiles(SysFileDTO file);

    /**
     * 新增数据
     *
     * @param sysFile 实例对象
     * @return 影响行数
     */
    @Edit
    int insert(SysFileDTO sysFile);

    /**
     * 批量新增数据
     *
     * @param list 实例对象集
     * @return 影响行数
     */
    int insertFiles(List<SysFileDTO> list);

    /**
     * 删除文件信息
     * @param id 附件id
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 删除文件信息
     * @param businessId businessId
     * @return 影响行数
     */
    int deleteRelationByBusinessId(Long businessId);

    /**
     * 删除文件信息
     * @return 影响行数
     */
    int deleteFiles(@Param("filesIdList") List<Long> filesIdList);

    /**
     * 删除业务附件中间表数据
     * @return
     */
    int deleteBusinessFiles(@Param("filesIdList") List<Long> filesIdList);

    /**
     *
     * @param businessId
     * @return
     */
    List<Long> selectFileIdListByBusinessId(@Param("businessId") Long businessId);

    /**
     * 新增 业务附件中间表
     * @param businessId
     * @param fileList
     * @return
     */
    int insertFilesBusiness(@Param("businessId") Long businessId, @Param("fileList") List<Long> fileList);


    /**
     * 新增 业务附件中间表
     * @param businessId
     * @param fileId
     * @return
     */
    int insertFileBusiness(@Param("fileId") Long fileId, @Param("businessId") Long businessId);
}
