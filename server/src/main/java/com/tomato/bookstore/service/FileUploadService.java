package com.tomato.bookstore.service;

import com.tomato.bookstore.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

/** 文件上传服务接口 */
public interface FileUploadService {
  /**
   * 上传图片
   *
   * @param file 图片文件
   * @param folder 存储文件夹
   * @return 图片访问 URL
   * @throws BusinessException 上传失败时抛出
   */
  String uploadImage(MultipartFile file, String folder) throws BusinessException;

  /**
   * 删除图片
   *
   * @param imageUrl 图片 URL
   * @throws BusinessException 删除失败时抛出
   */
  void deleteImage(String imageUrl) throws BusinessException;
}
