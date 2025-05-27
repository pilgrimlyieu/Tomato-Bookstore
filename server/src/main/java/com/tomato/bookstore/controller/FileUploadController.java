package com.tomato.bookstore.controller;

import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.dto.ApiResponse;
import com.tomato.bookstore.security.UserPrincipal;
import com.tomato.bookstore.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** 文件上传控制器 */
@RestController
@RequestMapping(ApiConstants.FILE_UPLOAD)
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {
  private final FileUploadService fileUploadService;

  /**
   * 上传用户头像
   *
   * @param file 头像文件
   * @param userPrincipal 当前用户
   * @return 头像访问 URL
   */
  @PostMapping(ApiConstants.FILE_UPLOAD_AVATAR)
  public ApiResponse<String> uploadAvatar(
      @RequestParam("file") MultipartFile file,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户「{}」上传头像", userPrincipal.getUsername());
    String imageUrl = fileUploadService.uploadImage(file, "avatar");
    return ApiResponse.success("头像上传成功", imageUrl);
  }

  /**
   * 上传商品封面
   *
   * @param file 封面文件
   * @param userPrincipal 当前用户
   * @return 封面访问 URL
   */
  @PostMapping(ApiConstants.FILE_UPLOAD_PRODUCT_COVER)
  public ApiResponse<String> uploadProductCover(
      @RequestParam("file") MultipartFile file,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户「{}」上传商品封面", userPrincipal.getUsername());
    String imageUrl = fileUploadService.uploadImage(file, "product");
    return ApiResponse.success("商品封面上传成功", imageUrl);
  }

  /**
   * 上传广告图片
   *
   * @param file 广告图片文件
   * @param userPrincipal 当前用户
   * @return 广告图片访问 URL
   */
  @PostMapping(ApiConstants.FILE_UPLOAD_ADVERTISEMENT_COVER)
  public ApiResponse<String> uploadAdvertisementImage(
      @RequestParam("file") MultipartFile file,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户「{}」上传广告图片", userPrincipal.getUsername());
    String imageUrl = fileUploadService.uploadImage(file, "advertisement");
    return ApiResponse.success("广告图片上传成功", imageUrl);
  }

  /**
   * 通用图片上传
   *
   * @param file 图片文件
   * @param folder 存储文件夹
   * @param userPrincipal 当前用户
   * @return 图片访问 URL
   */
  @PostMapping(ApiConstants.FILE_UPLOAD_COMMON)
  public ApiResponse<String> uploadImage(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "folder", defaultValue = "general") String folder,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户「{}」上传图片到文件夹：{}", userPrincipal.getUsername(), folder);
    String imageUrl = fileUploadService.uploadImage(file, folder);
    return ApiResponse.success("图片上传成功", imageUrl);
  }
}
