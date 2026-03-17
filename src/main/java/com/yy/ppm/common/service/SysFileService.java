package com.yy.ppm.common.service;

import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.SysFilePO;

import java.util.List;

/**
 * minio服务接口
 */
public interface SysFileService {

    /**
     * 保存文件上传明细
     *
     * @param fileList 文件明细列表
     */
    void save(List<SysFileDTO> fileList);

    /**
     * 获取文件明细
     *
     * @param fileId 附件id
     * @param businessId 业务id
     * @return SysFile对象
     */
    List<SysFileDTO> getFile(Long fileId, Long businessId, String businessType);

    /**
     * 按业务获取附件信息
     *
     * @param businessType 业务类型
     * @param businessId 业务id
     * @return SysFile对象
     */
    List<SysFileDTO> getBusFiles(String businessId, String businessType);

    /**
     * 拷贝文件
     * @param bucketName 源存储桶名称
     * @param destBucketName 目标存储桶名称
     * @param sourceBusinessId 源业务主键
     * @param targetBusinessId 目标业务主键
     */
    void copyFile(String bucketName, String destBucketName, String sourceBusinessId, String targetBusinessId);

    /**
     * 保存单个附件
     * @param id
     * @return
     */
    SysFilePO getFileById(Long id);

    /**
     * 保存附件业务关系
     * @param fileIds
     * @param businessId
     */
    void saveFileBusRelation(List<Long> fileIds, String businessId);

    void saveFileBusRelation(List<Long> fileIds, Long businessId);

    /**
     * 删除附件
     * @param fileId
     * @param businessId
     * @return
     */
    int delete(Long fileId, Long businessId);

    /**
     * 删除附件
     * @param fileId
     * @return
     */
    int deleteById(Long fileId);
}
