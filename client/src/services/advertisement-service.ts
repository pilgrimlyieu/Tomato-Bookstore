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
};
