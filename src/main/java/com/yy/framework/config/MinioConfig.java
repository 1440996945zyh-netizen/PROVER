package com.yy.framework.config;

import com.yy.common.log.MicroLogger;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * minio客户端组件
 *
 * @author gewx
 **/
@Component
public class MinioConfig {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(MinioConfig.class);

	/**
	 * minio服务器地址
	 **/
	@Value("${minio.url}")
	private String minioUrl;

	/**
	 * minio服务器端口号
	 **/
	@Value("${minio.port}")
	private int port;

	/**
	 * access Key
	 **/
	@Value("${minio.accessKey}")
	private String accessKey;

	/**
	 * secret Key
	 **/
	@Value("${minio.secretKey}")
	private String secretKey;

	/**
	 * connectTimeout
	 **/
	@Value("${minio.connectTimeout}")
	private long connectTimeout;

	/**
	 * writeTimeout
	 **/
	@Value("${minio.writeTimeout}")
	private long writeTimeout;

	/**
	 * readTimeout
	 **/
	@Value("${minio.readTimeout}")
	private long readTimeout;

	/**
	 * secure
	 **/
	@Value("${minio.secure}")
	private boolean secure;

	/**
	 * default network I/O timeout unit:milliseconds
	 **/
	private static final long UNIT = 1000;

	/**
	 * minioClient Key
	 **/
	private static final String MINIO_CLIENT_KEY = "defaultClient";

	/**
	 * concurrentMap容器
	 **/
	private final Map<String, MinioClient> concurrentMap = new ConcurrentHashMap<>(4);

	/**
	 * lazy init MinioClient instance. minioClient is threadSafe, support http1.1
	 * persistent connectionPool
	 **/
	@SuppressWarnings("unchecked")
	private final Map<String, MinioClient> container = LazyMap.decorate(concurrentMap, () -> {
		synchronized (this) {
			MinioClient client = concurrentMap.get(MINIO_CLIENT_KEY);
			if (client == null) {
				try {
					client = new MinioClient(minioUrl, port, accessKey, secretKey, secure);
					client.setTimeout(connectTimeout * UNIT, writeTimeout * UNIT, readTimeout * UNIT);
				} catch (InvalidEndpointException | InvalidPortException e) {
					throw new RuntimeException("Minio NetWork Connection wait...");
				}
				concurrentMap.put(MINIO_CLIENT_KEY, client);
			}
			return client;
		}
	});

	/**
	 * 上传对象,对象上传完毕自动关闭输入流
	 *
	 * @author gewx
	 * @param bucketName 桶名称
	 * @param objectName 对象名称
	 * @param input      输入流
	 * @throws Exception
	 * @return void
	 **/
	public void putObject(String bucketName, String objectName, InputStream input) throws Exception {
		MinioClient minioClient = container.get(MINIO_CLIENT_KEY);
		try (final InputStream is = input) {
			minioClient.putObject(bucketName, objectName, is, null, null, null, null);
		}
	}

	/**
	 * 上传对象,对象上传完毕自动关闭输入流
	 *
	 * @author gewx
	 * @param bucketName 桶名称
	 * @param objectName 对象名称
	 * @param byteArray  字节数组
	 * @throws Exception
	 * @return void
	 **/
	public void putObject(String bucketName, String objectName, byte[] byteArray) throws Exception {
		MinioClient minioClient = container.get(MINIO_CLIENT_KEY);
		try (final ByteArrayInputStream input = new ByteArrayInputStream(byteArray)) {
			minioClient.putObject(bucketName, objectName, input, null, null, null, null);
		}
	}

	/**
	 * 上传对象,对象上传完毕自动关闭输入流
	 *
	 * @author gewx
	 * @param bucketName 桶名称
	 * @param objectName 对象名称
	 * @param filePath   本地文件路径
	 * @throws Exception
	 * @return void
	 **/
	public void putObject(String bucketName, String objectName, String filePath) throws Exception {
		MinioClient minioClient = container.get(MINIO_CLIENT_KEY);
		minioClient.putObject(bucketName, objectName, filePath, null, null, null, null);
	}

	/**
	 * 获取对象,对象下载完毕后自动关闭输入流
	 *
	 * @author gewx
	 * @param bucketName 桶名称
	 * @param objectName 对象名称
	 * @throws Exception
	 * @return byte[]
	 **/
	public byte[] getObject(String bucketName, String objectName)  {
		MinioClient minioClient = container.get(MINIO_CLIENT_KEY);
		try (final InputStream input = minioClient.getObject(bucketName, objectName)) {
			byte[] byteArray = IOUtils.toByteArray(input);
			return byteArray;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 检测桶是否存在
	 *
	 * @author gewx
	 * @param bucketName 桶名称
	 * @throws Exception
	 * @return boolean
	 **/
	public boolean checkBucket(String bucketName) throws Exception {
		MinioClient minioClient = container.get(MINIO_CLIENT_KEY);
		return minioClient.bucketExists(bucketName);
	}

	/**
	 * 创建桶
	 *
	 * @author gewx
	 * @param bucketName 桶名称
	 * @throws Exception
	 * @return void
	 **/
	public void createBucket(String bucketName) throws Exception {
		MinioClient minioClient = container.get(MINIO_CLIENT_KEY);
		minioClient.makeBucket(bucketName);
	}

    /**
     * @Description: 删除文件
     * @author: luyy
     * @date 2021年1月4日 上午10:32:57
     * @param bucketName 桶名称
     * @param objectName 文件名称
     */
    public void delete(String bucketName, String objectName) {
        try {
            MinioClient minioClient = container.get(MINIO_CLIENT_KEY);
            minioClient.removeObject(bucketName, objectName);
        } catch (Exception e) {
        	LOGGER.error("文件删除失败~");
        }
    }

	/**
	 * 拷贝对象
	 * @param bucketName 源存储桶名称
	 * @param objectName 源存储桶中的源对象名称
	 * @param destBucketName 目标存储桶名称
	 * @param destObjectName 要创建的目标对象名称,如果为空，默认为源对象名称
	 */
    public void copyObject(String bucketName, String objectName, String destBucketName, String destObjectName) {
		try {
			MinioClient minioClient = container.get(MINIO_CLIENT_KEY);
			minioClient.copyObject(bucketName, objectName, destBucketName, destObjectName);
		} catch (Exception e) {
			LOGGER.error("文件拷贝失败~");
		}
	}

	public String getMinioUrl(){
		return minioUrl;
	}

    public int getPort() {
        return this.port;
    }
}
