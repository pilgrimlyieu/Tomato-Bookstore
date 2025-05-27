import { UPLOAD_MODULE } from "@/constants/apiPrefix";
import type { ApiResponse } from "@/types/api";
import apiClient from "@/utils/apiClient";

/**
 * 文件上传服务
 */
export default {
  /**
   * 上传用户头像
   *
   * @param {File} file 头像文件
   * @returns {Promise<ApiResponse<string>>} 头像 URL
   */
  uploadAvatar(file: File): Promise<ApiResponse<string>> {
    const formData = new FormData();
    formData.append("file", file);
    return apiClient.post(`${UPLOAD_MODULE}/avatar`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  /**
   * 上传商品封面
   *
   * @param {File} file 封面文件
   * @returns {Promise<ApiResponse<string>>} 封面 URL
   */
  uploadProductCover(file: File): Promise<ApiResponse<string>> {
    const formData = new FormData();
    formData.append("file", file);
    return apiClient.post(`${UPLOAD_MODULE}/product-cover`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  /**
   * 上传广告图片
   *
   * @param {File} file 广告图片文件
   * @returns {Promise<ApiResponse<string>>} 图片 URL
   */
  uploadAdvertisementImage(file: File): Promise<ApiResponse<string>> {
    const formData = new FormData();
    formData.append("file", file);
    return apiClient.post(`${UPLOAD_MODULE}/advertisement-cover`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  /**
   * 通用图片上传
   *
   * @param {File} file 图片文件
   * @param {string} folder 存储文件夹
   * @returns {Promise<ApiResponse<string>>} 图片 URL
   */
  uploadImage(
    file: File,
    folder: string = "general",
  ): Promise<ApiResponse<string>> {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("folder", folder);
    return apiClient.post(`${UPLOAD_MODULE}/image`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },
};
