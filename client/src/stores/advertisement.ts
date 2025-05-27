import advertisementService from "@/services/advertisement-service";
import type { Advertisement } from "@/types/advertisement";
import { performAsyncAction } from "@/utils/asyncHelper";
import { HttpStatusCode } from "axios";
import { defineStore } from "pinia";

/**
 * 广告状态管理
 */
export const useAdvertisementStore = defineStore("advertisement", {
  state: () => ({
    advertisements: [] as Advertisement[], // 所有广告列表
    currentAdvertisement: null as Advertisement | null, // 当前查看的广告
    loading: false, // 加载状态
    adminLoading: false, // 管理员操作加载状态
  }),

  actions: {
    /**
     * 获取所有广告
     *
     * @returns {Promise<void>}
     */
    async fetchAllAdvertisements(): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => advertisementService.getAllAdvertisements(),
        (data) => {
          this.advertisements = data;
        },
        "获取广告列表失败：",
        true,
      );
    },

    /**
     * 获取指定 ID 的广告详情
     *
     * @param {number} id 广告 ID
     * @returns {Promise<void>}
     */
    async fetchAdvertisementById(id: number): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => advertisementService.getAdvertisementById(id),
        (data) => {
          this.currentAdvertisement = data;
        },
        `获取广告详情失败（ID: ${id}）：`,
        true,
      );
    },

    /**
     * 创建广告（管理员）
     *
     * @param {Advertisement} advertisement 广告参数
     * @returns {Promise<boolean>} 是否创建成功
     */
    async createAdvertisement(advertisement: Advertisement): Promise<boolean> {
      return await performAsyncAction(
        this,
        "adminLoading",
        () => advertisementService.createAdvertisement(advertisement),
        (created: Advertisement) => {
          this.advertisements.unshift(created);
        },
        "创建广告失败：",
        true,
        [HttpStatusCode.Ok],
        "广告创建成功",
      );
    },

    /**
     * 更新广告（管理员）
     * @param {Advertisement} advertisement 广告参数
     * @returns {Promise<boolean>} 是否更新成功
     */
    async updateAdvertisement(advertisement: Advertisement): Promise<boolean> {
      return await performAsyncAction(
        this,
        "adminLoading",
        () => advertisementService.updateAdvertisement(advertisement),
        (updated: Advertisement) => {
          // 更新本地广告列表
          const index = this.advertisements.findIndex(
            (a) => a.id === advertisement.id,
          );
          if (index !== -1) {
            this.advertisements[index] = updated;
          }
        },
        `更新广告失败（ID: ${advertisement.id}）：`,
        true,
        [HttpStatusCode.Ok],
        "广告更新成功",
      );
    },

    /**
     * 删除广告（管理员）
     * @param {number} id 广告 ID
     * @returns {Promise<boolean>} 是否删除成功
     */
    async deleteAdvertisement(id: number): Promise<boolean> {
      return await performAsyncAction(
        this,
        "adminLoading",
        () => advertisementService.deleteAdvertisement(id),
        () => {
          // 从本地列表移除
          this.advertisements = this.advertisements.filter((a) => a.id !== id);
        },
        `删除广告失败（ID: ${id}）：`,
        true,
        [HttpStatusCode.Ok],
        "广告删除成功",
      );
    },

    /**
     * 清除当前广告数据
     *
     * @returns {void}
     * @description 清除当前广告数据
     */
    clearCurrentAdvertisement(): void {
      this.currentAdvertisement = null;
    },
  },
});
