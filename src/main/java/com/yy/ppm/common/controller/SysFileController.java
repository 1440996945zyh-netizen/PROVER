package com.yy.ppm.common.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.magic.FileMagicUtils;
import com.yy.common.magic.FileType;
import com.yy.common.magic.FileUploadBusinessTypeEnum;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.config.MinioConfig;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.dto.FileRelationDTO;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.SysFilePO;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.system.enums.SysEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

/**
 * minio文件对象存储
 */
@RestController
@RequestMapping(value = "/api/internal/file")
@Validated
public class SysFileController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SysFileController.class);

    /**
     * 文件上传最大配置,5MB
     **/
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 5 * 20;


    @Resource
    private MinioConfig minIoConfig;

    private final SysFileService sysFileService;

    private final Snowflake snowflake;

    public SysFileController(
            SysFileService sysFileService,
            Snowflake snowflake
    ){
        this.snowflake = snowflake;
        this.sysFileService = sysFileService;
    }
    /**
     * 文件上传
     *
     * @param fileArray    对象数据
     * @param businessType 业务类型
     * @param businessId  业务id
     * @return 响应数据 文件所在服务器的url[]数据
     * @throws Exception
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(MultipartFile[] fileArray, String businessType, String businessId)
            throws Exception {
        final String methodName = "MinioController:upload";
        LOGGER.enter(methodName, "上传文件[start]");

        // 参数校验
        Map<String, Object> errorResponse = validateBasicParams(fileArray, businessType);
        if (!errorResponse.isEmpty()) {
            return errorResponse;
        }

        // 文件批量校验
        errorResponse = validateFiles(fileArray);
        if (!errorResponse.isEmpty()) {
            return errorResponse;
        }

        // 准备上传环境
        String bucketName = SysEnum.SysParamEnum.FILE_BUCKET.getDefValue();
        String route = FileUploadBusinessTypeEnum.getRoute(businessType);
        ensureBucketExists(bucketName);

        // 上传文件并保存记录
        List<SysFileDTO> fileList = uploadAndSaveFiles(fileArray, businessType, bucketName, route);
        sysFileService.save(fileList);

        // 构建响应结果
        Map<String, Object> result = buildUploadResult(fileList);
        LOGGER.exit(methodName, "上传文件[end]");
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 校验基本参数
     */
    private Map<String, Object> validateBasicParams(MultipartFile[] fileArray, String businessType) {
        if (fileArray.length == 0) {
            LOGGER.warn("缺失上传文件数据~");
            return Response.FAIL.newBuilder()
                    .addGateWayCode(Response.GateWayCode.E0201)
                    .out("缺失上传文件数据~")
                    .toResult();
        }

        if (isBlank(businessType)) {
            LOGGER.warn("业务类型必填~");
            return Response.FAIL.newBuilder()
                    .addGateWayCode(Response.GateWayCode.E0201)
                    .toResult();
        }

        if (!FileUploadBusinessTypeEnum.valid(businessType)) {
            LOGGER.warn("业务类型不匹配~");
            return Response.FAIL.newBuilder()
                    .addGateWayCode(Response.GateWayCode.E0205)
                    .toResult();
        }

        return new HashMap<>();
    }

    /**
     * 批量校验文件（大小、类型、文件名长度）
     */
    private Map<String, Object> validateFiles(MultipartFile[] fileArray) throws IOException {
        for (MultipartFile file : fileArray) {
            Map<String, Object> errorResponse = validateSingleFile(file);
            if (errorResponse != null) {
                return errorResponse;
            }
        }
        return new HashMap<>();
    }

    /**
     * 校验单个文件
     */
    private Map<String, Object> validateSingleFile(MultipartFile file) throws IOException {
        // 校验文件名长度
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.length() > 100) {
            LOGGER.warn("文件名过长~");
            return Response.FAIL.newBuilder()
                    .addGateWayCode(Response.GateWayCode.E0403)
                    .toResult();
        }

        // 校验文件类型
        String type = FileMagicUtils.getFileType(file.getBytes());
        if (!FileType.isInclude(type)) {
            LOGGER.warn("不支持的文件上传格式~");
            return Response.FAIL.newBuilder()
                    .addGateWayCode(Response.GateWayCode.E0402)
                    .toResult();
        }

        // 校验文件大小
        if (file.getBytes().length > MAX_FILE_SIZE) {
            LOGGER.warn("文件上传大小限制5及以下~");
            return Response.FAIL.newBuilder()
                    .addGateWayCode(Response.GateWayCode.E0401)
                    .toResult();
        }

        return new HashMap<>();
    }

    /**
     * 确保桶存在
     */
    private void ensureBucketExists(String bucketName) throws Exception {
        if (!minIoConfig.checkBucket(bucketName)) {
            minIoConfig.createBucket(bucketName);
        }
    }

    /**
     * 上传文件并保存记录
     */
    private List<SysFileDTO> uploadAndSaveFiles(MultipartFile[] fileArray, String businessType,
                                                String bucketName, String route) throws Exception {
        List<SysFileDTO> fileList = new ArrayList<>();
        String bucketDate = getBucketDate();
        String path = buildPath(route, bucketDate);

        for (MultipartFile file : fileArray) {
            SysFileDTO fileDto = uploadSingleFile(file, businessType, bucketName, path);
            fileList.add(fileDto);
        }

        return fileList;
    }

    /**
     * 获取桶日期路径
     */
    private String getBucketDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date()) + "/";
    }

    /**
     * 构建存储路径
     */
    private String buildPath(String route, String bucketDate) {
        return "".equals(route) ? bucketDate : (route + File.separator + bucketDate);
    }

    /**
     * 上传单个文件
     */
    private SysFileDTO uploadSingleFile(MultipartFile file, String businessType,
                                        String bucketName, String path) throws Exception {
        String fileName = file.getOriginalFilename();
        Long id = snowflake.nextId();
        String saveName = id + fileName;

        // 上传到MinIO
        minIoConfig.putObject(bucketName, path + saveName, file.getInputStream());

        // 构建文件DTO
        SysFileDTO fileDto = new SysFileDTO();
        fileDto.setId(id);
        fileDto.setBusinessType(businessType);
        fileDto.setFileBucket(bucketName);
        fileDto.setFilePath(path);
        fileDto.setFileSaveName(saveName);
        fileDto.setFileName(fileName);

        if (fileName != null) {
            fileDto.setFileSuffix(fileName.substring(fileName.indexOf(".") + 1));
        }

        return fileDto;
    }

    /**
     * 构建上传结果
     */
    private Map<String, Object> buildUploadResult(List<SysFileDTO> fileList) {
        Map<String, Object> result = new HashMap<>();
        result.put("files", fileList);

        String[] urls = generateFileUrls(fileList);
        result.put("urls", urls);

        return result;
    }

    /**
     * 生成文件访问URL
     */
    private String[] generateFileUrls(List<SysFileDTO> fileList) {
        String[] urls = new String[fileList.size()];
        String minioUrl = minIoConfig.getMinioUrl();

        for (int i = 0; i < fileList.size(); i++) {
            SysFileDTO file = fileList.get(i);
            // 格式: http://域名/minio/桶名/路径/文件名
            urls[i] = minioUrl + "/minio/" + file.getFileBucket() + "/"
                    + file.getFilePath() + file.getFileSaveName();
        }

        return urls;
    }

    /**
     * 文件下载
     *
     * @param id   附件id
     * @param resp 输出流
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/download")
    public Map<String, Object> downLoad(@NotBlank(message = "附件Id必填~") String id, HttpServletResponse resp) throws Exception {
        final String methodName = "MinioController:downLoad";
        LOGGER.enter(methodName, "下载文件[start], 附件id: " + id);

        if (!isNumeric(id)) {
            LOGGER.warn("数值格式不正确~");
            throw new BusinessRuntimeException("附件Id数值格式不正确~");
        }

        //根据fileId获取文件信息
        SysFilePO sysFilePO  =  sysFileService.getFileById(Long.valueOf(id));

        if (null == sysFilePO) {
            LOGGER.warn("不存在的附件~");
            throw new BusinessRuntimeException("不存在的附件~");
        }

        //因为是根据fileId查询，所以最多只有一个文件
        String bucketName = sysFilePO.getFileBucket();
        String path = sysFilePO.getFilePath();
        String saveName = sysFilePO.getFileSaveName();
        byte[] fileByteArray = minIoConfig.getObject(bucketName, path + saveName);
        resp.setHeader("content-disposition",
                "attachment;filename=" + URLEncoder.encode(sysFilePO.getFileName(), "UTF-8"));
        try (OutputStream outputStream = resp.getOutputStream()) {
            outputStream.write(fileByteArray);
        }

        LOGGER.exit(methodName, "下载文件[end], result: success~");
        return Response.SUCCESS.newBuilder().toResult();
    }

    /**
     * 删除文件
     *
     * @param fileId     文件id
     * @param businessId 业务id
     * @return 响应数据
     */
    @GetMapping("/deletefile")
    public Map<String, Object> deleteFile(@RequestParam(required = false,value = "fileId") Long fileId,
                                          @RequestParam(required = false,value = "businessId") Long businessId) {
        final String methodName = "MinioController:deleteFile";
        LOGGER.enter(methodName, "业务执行");

        List<SysFileDTO> files = sysFileService.getFile(fileId, businessId, null);

        if (files != null) {
            for (SysFileDTO file : files) {
                String bucketName = file.getFileBucket();
                String path = file.getFilePath();
                String saveName = file.getFileSaveName();
                minIoConfig.delete(bucketName, path + saveName);
            }

            sysFileService.delete(fileId, businessId);
        }

        LOGGER.exit(methodName, org.apache.commons.lang3.StringUtils.EMPTY);
        return Response.SUCCESS.newBuilder().toResult();
    }

    /**
     * 获取文件信息
     *
     * @param id          文件id
     * @param businessType 业务类型
     * @param businessId 业务id
     * @param code 是否切库，使用DNEnum
     * @return 响应数据
     */
    @GetMapping("/getfile")
    public Map<String, Object> getFile(@RequestParam(required = false, value = "fileId") Long id,
                                       @RequestParam(required = false, value = "businessType") String businessType,
                                       @RequestParam(required = false, value = "businessId") Long businessId,
                                       @RequestParam(required = false,value = "code") String code) {
        final String methodName = "MinioController:getFile";
        LOGGER.enter(methodName, "业务执行");
        if (id == null && StringUtil.isEmpty(businessType) && businessId == null) {
            throw new BusinessRuntimeException("【附件】参数不能为空~");
        }
        List<SysFileDTO> files =  sysFileService.getFile(id, businessId, businessType);
        LOGGER.exit(methodName, org.apache.commons.lang3.StringUtils.EMPTY);
        return Response.SUCCESS.newBuilder().toResult(files);
    }

    /**
     * 获取文件信息
     *
     * @param businessType 业务类型
     * @param businessId 业务id
     * @return 响应数据
     */
    @GetMapping("/getBusFiles")
    public Map<String, Object> getBusFiles(
                                       @RequestParam(required = false, value = "businessId") String businessId,
                                       @RequestParam(required = false, value = "businessType") String businessType) {
        final String methodName = "MinioController:getBusfiles";
        LOGGER.enter(methodName, "业务执行");
        if (StringUtil.isEmpty(businessType) || businessId == null) {
            throw new BusinessRuntimeException("参数不能为空~");
        }
        List<SysFileDTO> files =  sysFileService.getBusFiles(businessId, businessType);
        LOGGER.exit(methodName, org.apache.commons.lang3.StringUtils.EMPTY);
        return Response.SUCCESS.newBuilder().toResult(files);
    }

    /**
     * 获取文件地址
     *
     * @param id          文件id
     * @param businessType 业务类型
     * @param businessId 业务id
     * @param code 是否切库，使用DNEnum
     * @return 带url的SysFilePO集合
     */
    @GetMapping("/getfileurl")
    public Map<String, Object> getFileUrl(@RequestParam(required = false, value = "fileId") Long id,
                                          @RequestParam(required = false, value = "businessType") String businessType,
                                          @RequestParam(required = false, value = "businessId") Long businessId,
                                          @RequestParam(required = false,value = "code") String code) {
        final String methodName = "MinioController:getFileUrl";
        LOGGER.enter(methodName, "业务执行");
        if (id == null) {
            throw new BusinessRuntimeException("【附件】参数不能为空~");
        }
        List<SysFileDTO> files =  sysFileService.getFile(id, businessId, businessType);

        //生成url并返回
        if (files != null) {
            for (SysFileDTO file : files) {
                file.setFileUrl(minIoConfig.getMinioUrl() + ":" + minIoConfig.getPort() + "/" + file.getFileBucket() + "/" + file.getFilePath() + file.getFileSaveName());
            }
        }
        LOGGER.exit(methodName, org.apache.commons.lang3.StringUtils.EMPTY);
        return Response.SUCCESS.newBuilder().toResult(files);
    }

    /**
     * 拷贝文件
     * @param bucketName 源存储桶名称
     * @param destBucketName 目标存储桶名称
     * @param sourceBusinessId 源业务主键
     * @param targetBusinessId 目标业务主键
     * @param code 是否切库，使用DNEnum
     * @return 响应数据
     */
    @PostMapping("/copy")
    public Map<String, Object> copyFile(String bucketName, String destBucketName,
                                        String sourceBusinessId, String targetBusinessId,
                                        @RequestParam(required = false,value = "code") String code) {
        final String methodName = "MinioController:copyFile";
        LOGGER.enter(methodName, "业务执行");
        if (StringUtil.isEmpty(bucketName) || StringUtil.isEmpty(destBucketName) || StringUtil.isEmpty(sourceBusinessId) || StringUtil.isEmpty(targetBusinessId)) {
            throw new BusinessRuntimeException("参数异常~");
        }

        sysFileService.copyFile(bucketName, destBucketName, sourceBusinessId, targetBusinessId);

        LOGGER.exit(methodName, org.apache.commons.lang3.StringUtils.EMPTY);
        return Response.SUCCESS.newBuilder().toResult();
    }

    /**
     * 文件在线预览
     *
     * @author gewx
     * @param id   附件id
     * @param resp 输出流
     * @param code 是否切库，使用DNEnum
     * @throws Exception
     * @return 文件对象
     */
    @GetMapping("/onlineexternal/{id}")
    public void onlineExternal(@NotBlank(message = "附件Id必填~")@PathVariable String id, HttpServletResponse resp,
                               @RequestParam(required = false,value = "code") String code) throws Exception {
        final String methodName = "MinioController:onlineExternal";
        LOGGER.enter(methodName, "文件在线阅览[start], 附件id: " + id);

        if (!isNumeric(id)) {
            LOGGER.warn("数值格式不正确~");
            throw new BusinessRuntimeException("附件Id数值格式不正确~");
        }

        List<SysFileDTO> files = sysFileService.getFile(Long.valueOf(id), null, null);
        if (files == null) {
            LOGGER.warn("不存在的附件~");
            throw new BusinessRuntimeException("不存在的附件~");
        }
        SysFileDTO file = files.get(0);

        String bucketName = file.getFileBucket();
        String path = file.getFilePath();
        String saveName = file.getFileSaveName();
        byte[] fileByteArray = minIoConfig.getObject(bucketName, path + saveName);

        String[] array = org.apache.commons.lang3.StringUtils.split(file.getFileName(), ".");
        if ("PDF".equals(array[array.length - 1].toUpperCase())) {
            //pdf
            resp.setContentType("application/pdf;charset=UTF-8");
        }else if("JPEG".equals(array[array.length - 1].toUpperCase())){
            resp.setContentType("image/jpeg;charset=UTF-8");
        }
        else if("JPG".equals(array[array.length - 1].toUpperCase())){
            resp.setContentType("image/jpeg;charset=UTF-8");
        }
        else if("PNG".equals(array[array.length - 1].toUpperCase())){
            resp.setContentType("image/png;charset=UTF-8");
        }else {
            resp.setHeader("content-disposition",
                    "attachment;filename=" + URLEncoder.encode(file.getFileName(), "UTF-8"));
        }
        try (OutputStream outputStream = resp.getOutputStream()) {
            outputStream.write(fileByteArray);
        }

        LOGGER.exit(methodName, "文件在线阅览[end], result: success~");
    }

    /**
     * 下载模板文件【模板文件默认存放在minio服务器的yyy桶下template目录下】
     * @param templateFileName 模板名称
     * @param response 响应数据
     * @return
     */
    @GetMapping("/downloadtemplate")
    public Map<String, Object> downloadTemplate(@RequestParam(value = "templateFileName") String templateFileName,
                                                HttpServletResponse response) {
        final String methodName = "BaseController:downloadTemplate";
        LOGGER.enter(methodName + "[start]", "templateFileName:" + templateFileName);

        try {
            byte[] fileByteArray = minIoConfig.getObject("yyy", "/template/" + templateFileName);
            response.setHeader("content-disposition",
                    "attachment;filename=" + URLEncoder.encode(templateFileName, "UTF-8"));
            response.getOutputStream().write(fileByteArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessRuntimeException("下载模板异常！");
        }
        LOGGER.exit(methodName + "result:" + templateFileName);
        return Response.SUCCESS.newBuilder().toResult();
    }

    /**
     * @Description: 文件批量下载
     * @Param [java.lang.String, jakarta.servlet.http.HttpServletResponse]
     * @return void
     */
    @GetMapping("/batchDown")
    public void batchDown(@NotBlank(message = "业务id~") String businessId, HttpServletResponse resp) throws Exception {

        if (!isNumeric(businessId)) {
            throw new BusinessRuntimeException("业务Id数值格式不正确~");
        }

        List<SysFileDTO> sysFiles = sysFileService.getFile(null, Long.valueOf(businessId), null);
        if (sysFiles==null||sysFiles.size() == 0){
            throw new Exception("不存在该业务的附件~");
        }
        resp.setHeader("content-disposition",
                "attachment;filename=附件压缩包.zip");
        OutputStream out = resp.getOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(out);
        for (SysFileDTO sysFile : sysFiles) {
            byte[] fileByteArray = minIoConfig.getObject(sysFile.getFileBucket(), sysFile.getFileUrl());
            ZipEntry entry = new ZipEntry(sysFile.getFileName());//创建压缩文件中的条目
            zipOutputStream.putNextEntry(entry);//将创建好的条目加入到压缩文件中
            zipOutputStream.write(fileByteArray);
            zipOutputStream.closeEntry();
        }
        zipOutputStream.flush();
        zipOutputStream.close();
    }

    /**
     * 保存文件和业务的关系
     * @param dto
     * @return
     */
    @PostMapping("/saveFileBusRelation")
    public Map<String, Object> saveFileBusRelation(@RequestBody FileRelationDTO dto) {
        final String methodName = "MinioController:saveFileBusRelation";
        LOGGER.enter(methodName, "业务执行");

        List<Long> fileIds = dto.getFileIds();
        String businessId = dto.getBusinessId();

        if (CollUtil.isEmpty(fileIds) || StringUtil.isEmpty(businessId)) {
            throw new BusinessRuntimeException("参数异常~");
        }

        sysFileService.saveFileBusRelation(fileIds, businessId);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return Response.SUCCESS.newBuilder().toResult();
    }
}
