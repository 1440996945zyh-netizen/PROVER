package com.yy.ppm.common.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.framework.config.MinioConfig;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.SysFilePO;
import com.yy.ppm.common.mapper.SysFileMapper;
import com.yy.ppm.common.service.SysFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Minio服务实现类
 */
@Service
public class SysFileServiceImpl implements SysFileService {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SysFileServiceImpl.class);

    @Resource
    private SysFileMapper sysFileMapper;

    @Resource
    private MinioConfig minioConfig;

    @Autowired
    private Snowflake snowflake;

    /**
     * 保存文件上传明细
     * @param fileList 文件明细列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(List<SysFileDTO> fileList) {
        insert(fileList);
    }

    /**
     * 获取文件明细
     * @param fileId 文件id
     * @param businessType 业务类型
     * @param businessId 业务id
     * @return List<SysFilePO>
     */
    @Override
    public List<SysFileDTO> getFile(Long fileId, Long businessId, String businessType) {
        return getList(fileId, businessId, businessType);
    }


    /**
     * 按业务获取附件信息
     *
     * @param businessType 业务类型
     * @param businessId 业务id
     * @return SysFile对象
     */
    @Override
    public List<SysFileDTO> getBusFiles(String businessId, String businessType) {
        return sysFileMapper.getBusFiles(businessId, businessType);
    }

    @Override
    public List<SysFileDTO> getBusFiles(Long businessId, String businessType) {
        return getBusFiles( String.valueOf(businessId), businessType);
    }

    /**
     * 拷贝文件
     * @param bucketName 源存储桶名称
     * @param destBucketName 目标存储桶名称
     * @param sourceBusinessId 源业务主键
     * @param targetBusinessId 目标业务主键
     */
    @Override
    public void copyFile(String bucketName, String destBucketName, String sourceBusinessId, String targetBusinessId) {
        copy(bucketName, destBucketName, sourceBusinessId, targetBusinessId);
    }

    @Override
    public SysFilePO getFileById(Long id) {
        return sysFileMapper.getFileById(id);
    }

    /**
     * 新增附件
     * @param fileList
     */
    private int insert(List<SysFileDTO> fileList) {
        final String methodName = "MinioServiceImpl:insert";
        LOGGER.enter(methodName, "业务执行");
        int count = fileList.stream().mapToInt(val -> sysFileMapper.insert(val)).sum();
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return count;
    }

    /**
     * 查询附件
     * @param fileId
     * @param businessId
     * @param businessType
     * @return
     */
    private List<SysFileDTO> getList(Long fileId, Long businessId, String businessType){
        final String methodName = "MinioServiceImpl:getList";
        LOGGER.enter(methodName, "业务执行");
        SysFileDTO file = new SysFileDTO();
        file.setId(fileId);
        file.setBusinessType(businessType);
        List<SysFileDTO> files = sysFileMapper.getFiles(file);
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return files;
    }


    /**
     * 删除附件
     * @param fileId
     * @param businessId
     * @return
     */
    @Override
    public int delete(Long fileId, Long businessId) {
        final String methodName = "MinioServiceImpl:delete";
        LOGGER.enter(methodName, "业务执行");
        List<Long> filesIdList = new ArrayList<>();
        int count = 0;
        // 附件id为空 且业务主键不为空
        if(null == fileId && null != businessId){
            // 根据业务主键查询附件id
            filesIdList = sysFileMapper.selectFileIdListByBusinessId(businessId.toString());
        }else{
            filesIdList.add(fileId);
        }

        if(filesIdList.size() > 0){
            // 删除业务附件中间表
            count = sysFileMapper.deleteBusinessFiles(filesIdList);
            count = sysFileMapper.deleteFiles(filesIdList);
        }


        LOGGER.exit(methodName, StringUtils.EMPTY);
        return count;
    }

    /**
     * 删除附件
     * @param fileId
     * @return
     */
//    @Override
    public int deleteById(Long fileId) {
        final String methodName = "MinioServiceImpl:delete";
        LOGGER.enter(methodName, "业务执行");
        int count = 0;
        //删除
        if(null == fileId ){
            count = sysFileMapper.deleteById(fileId);
        }
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return count;
    }


    /**
     * 拷贝附件
     * @param bucketName 源存储桶名称
     * @param destBucketName 目标存储桶名称
     * @param sourceBusinessId 源业务主键
     * @param targetBusinessId 目标业务主键
     */
    private void copy(String bucketName, String destBucketName, String sourceBusinessId, String targetBusinessId){
        final String methodName = "MinioServiceImpl:copy";
        LOGGER.enter(methodName, "业务执行");

        //获取待拷贝的源文件
        SysFileDTO file = new SysFileDTO();
        file.setFileBucket(bucketName);
        List<SysFileDTO> files = sysFileMapper.getFiles(file);

        //minio拷贝源文件到目标桶
        for (SysFileDTO po : files){
            //源文件名
            String objectName = po.getFilePath() + po.getFileSaveName();
            //目标文件名
            String destObjectName = targetBusinessId + po.getFileSaveName().substring(19);
            //操作minio服务器
            try {
                if (!minioConfig.checkBucket(destBucketName)) {
                    minioConfig.createBucket(destBucketName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("创建桶失败~");
            }
            //拷贝文件
            minioConfig.copyObject(bucketName, objectName, destBucketName, po.getFilePath() + destObjectName);

            //复制文件信息,修改id和桶名
            po.setId(snowflake.nextId());
            po.setFileSaveName(destObjectName);
            po.setFileBucket(destBucketName);
            sysFileMapper.insert(po);
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    /**
     * 保存附件业务关系
     * @param fileIds
     * @param businessId
     */
    @Override
    public void saveFileBusRelation(List<Long> fileIds, String businessId) {

        // 先删除
        sysFileMapper.deleteRelationByBusinessId(businessId);

        // 再插入
        if (fileIds != null && fileIds.size() > 0) {
            for (Long fileId : fileIds) {
                sysFileMapper.insertFileBusiness(fileId, businessId);
            }
        }

    }

    @Override
    public void saveFileBusRelation(List<Long> fileIds, Long businessId) {
        saveFileBusRelation(fileIds, String.valueOf(businessId));
    }

}
