import { ADVERTISEMENT_MODULE } from "@/constants/apiPrefix";
import type { Advertisement } from "@/types/advertisement";
import type { ApiResponse } from "@/types/api";
import apiClient from "@/utils/apiClient";

/**
 * 广告服务类
 */
export default {
  /**
   * 获取所有广告
   *
   * @returns {Promise<ApiResponse<Advertisement[]>>} 广告列表
   */
  getAllAdvertisements(): Promise<ApiResponse<Advertisement[]>> {
    return apiClient.get(`${ADVERTISEMENT_MODULE}`);
  },

  /**
   * 根据 ID 获取广告详情
   *
   * @param {number} id 广告 ID
   * @returns {Promise<ApiResponse<Advertisement>>} 广告详情
   */
  getAdvertisementById(id: number): Promise<ApiResponse<Advertisement>> {
    return apiClient.get(`${ADVERTISEMENT_MODULE}/${id}`);
  },

  /**
   * 创建广告（管理员）
   *
   * @param {Advertisement} advertisement 广告数据
   * @returns {Promise<ApiResponse<Advertisement>>} 创建的广告
   */
  createAdvertisement(
    advertisement: Advertisement,
  ): Promise<ApiResponse<Advertisement>> {
    return apiClient.post(`${ADVERTISEMENT_MODULE}`, advertisement);
  },

  /**
   * 更新广告（管理员）
   *
   * @param {Advertisement} advertisement 广告数据
   * @returns {Promise<ApiResponse<Advertisement>>} 更新的广告
   */
  updateAdvertisement(
    advertisement: Advertisement,
  ): Promise<ApiResponse<Advertisement>> {
    return apiClient.put(`${ADVERTISEMENT_MODULE}`, advertisement);
  },

  /**
   * 删除广告（管理员）
   *
   * @param {number} id 广告 ID
   * @returns {Promise<ApiResponse<string>>} 删除结果
   */
  deleteAdvertisement(id: number): Promise<ApiResponse<string>> {
    return apiClient.delete(`${ADVERTISEMENT_MODULE}/${id}`);
  },
};
