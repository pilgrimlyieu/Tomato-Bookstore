package com.tomato.bookstore.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.tomato.bookstore.config.OssConfig;
import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.service.FileUploadService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** 文件上传服务实现类 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
  private final OSS ossClient;
  private final OssConfig ossConfig;

  private static final String[] ALLOWED_IMAGE_TYPES = {
    "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
  };
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

  @Override
  public String uploadImage(MultipartFile file, String folder) throws BusinessException {
    log.info("开始上传图片到 OSS，文件夹：{}, 文件名：{}", folder, file.getOriginalFilename());

    // 验证文件
    validateImageFile(file);

    try {
      // 生成文件名
      String fileName = generateFileName(file, folder);

      // 上传文件到 OSS
      PutObjectRequest putObjectRequest =
          new PutObjectRequest(ossConfig.getBucketName(), fileName, file.getInputStream());
      ossClient.putObject(putObjectRequest);

      // 生成访问 URL
      String imageUrl = generateImageUrl(fileName);
      log.info("图片上传成功，访问地址：{}", imageUrl);

      return imageUrl;
    } catch (IOException e) {
      log.error("图片上传失败，文件读取异常：{}", e.getMessage(), e);
      throw new BusinessException(BusinessErrorCode.SYSTEM_ERROR, "图片上传失败：文件读取异常");
    } catch (Exception e) {
      log.error("图片上传失败：{}", e.getMessage(), e);
      throw new BusinessException(BusinessErrorCode.SYSTEM_ERROR, "图片上传失败");
    }
  }

  @Override
  public void deleteImage(String imageUrl) throws BusinessException {
    if (imageUrl == null || imageUrl.trim().isEmpty()) {
      return;
    }

    try {
      // 从 URL 中提取对象键
      String objectKey = extractObjectKeyFromUrl(imageUrl);
      if (objectKey != null) {
        ossClient.deleteObject(ossConfig.getBucketName(), objectKey);
        log.info("图片删除成功：{}", imageUrl);
      }
    } catch (Exception e) {
      log.error("图片删除失败：{}", e.getMessage(), e);
      throw new BusinessException(BusinessErrorCode.SYSTEM_ERROR, "图片删除失败");
    }
  }

  /**
   * 验证图片文件
   *
   * @param file 上传的文件
   * @throws BusinessException 验证失败时抛出
   */
  private void validateImageFile(MultipartFile file) throws BusinessException {
    if (file == null || file.isEmpty()) {
      throw new BusinessException(BusinessErrorCode.INVALID_PARAMETER, "上传的文件为空");
    }

    // 检查文件大小
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new BusinessException(BusinessErrorCode.INVALID_PARAMETER, "文件大小不能超过 5MB");
    }

    // 检查文件类型
    String contentType = file.getContentType();
    boolean isValidType = false;
    for (String allowedType : ALLOWED_IMAGE_TYPES) {
      if (allowedType.equals(contentType)) {
        isValidType = true;
        break;
      }
    }

    if (!isValidType) {
      throw new BusinessException(
          BusinessErrorCode.INVALID_PARAMETER, "文件类型不支持，仅支持 JPG、PNG、GIF、WebP 格式的图片");
    }
  }

  /**
   * 生成文件名
   *
   * @param file 上传的文件
   * @param folder 文件夹名称
   * @return 生成的文件名
   */
  private String generateFileName(MultipartFile file, String folder) {
    String originalFilename = file.getOriginalFilename();
    String extension = "";
    if (originalFilename != null && originalFilename.contains(".")) {
      extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    // 生成格式：folder/yyyy/MM/dd/uuid.ext
    String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    String uuid = UUID.randomUUID().toString().replace("-", "");

    return String.format("%s/%s/%s%s", folder, datePath, uuid, extension);
  }

  /**
   * 生成图片访问 URL
   *
   * @param fileName 文件名
   * @return 图片访问 URL
   */
  private String generateImageUrl(String fileName) {
    // 如果 endpoint 包含协议，直接使用；否则添加 https 协议
    String endpoint = ossConfig.getEndpoint();
    if (!endpoint.startsWith("http")) {
      endpoint = "https://" + endpoint;
    }
    return String.format(
        "%s/%s", endpoint.replace("//", "//" + ossConfig.getBucketName() + "."), fileName);
  }

  /**
   * 从 URL 中提取对象键
   *
   * @param imageUrl 图片 URL
   * @return 对象键
   */
  private String extractObjectKeyFromUrl(String imageUrl) {
    try {
      String bucketDomain =
          ossConfig.getBucketName()
              + "."
              + ossConfig.getEndpoint().replace("https://", "").replace("http://", "");
      if (imageUrl.contains(bucketDomain)) {
        return imageUrl.substring(imageUrl.indexOf(bucketDomain) + bucketDomain.length() + 1);
      }
    } catch (Exception e) {
      log.warn("无法从 URL 中提取对象键：{}", imageUrl);
    }
    return null;
  }
}
