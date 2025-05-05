import productService from "@/services/product-service";
import type { Product, ProductParams, StockpileParams } from "@/types/product";
import { performAsync, performAsyncAction } from "@/utils/asyncHelper";
import { HttpStatusCode } from "axios";
import { ElMessage } from "element-plus";
import { defineStore } from "pinia";

/**
 * 商品状态管理
 */
export const useProductStore = defineStore("product", {
  state: () => ({
    products: [] as Product[], // 所有商品列表
    currentProduct: null as Product | null, // 当前查看的商品
    currentStockpile: null as {
      id?: number;
      amount: number;
      frozen: number;
      productId?: number;
    } | null, // 当前商品的库存
    loading: false, // 加载状态
    adminLoading: false, // 管理员操作加载状态
  }),

  actions: {
    /**
     * 获取所有商品
     *
     * @returns {Promise<void>}
     */
    async fetchAllProducts(): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => productService.getAllProducts(),
        (data) => {
          this.products = data;
        },
        "获取商品列表失败：",
        true,
      );
    },

    /**
     * 获取指定 ID 的商品详情
     *
     * @param {number} id 商品 ID
     * @returns {Promise<void>}
     */
    async fetchProductById(id: number): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => productService.getProductById(id),
        (data) => {
          this.currentProduct = data;
        },
        `获取商品详情失败（ID: ${id}）：`,
        true,
      );
    },

    /**
     * 获取商品库存
     *
     * @param {number} productId 商品 ID
     * @returns {Promise<void>}
     */
    async fetchStockpile(productId: number): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => productService.getStockpile(productId),
        (data) => {
          this.currentStockpile = data;
        },
        `获取商品库存失败（ID: ${productId}）：`,
        false, // 不显示错误消息
      );
    },

    /**
     * 创建商品（管理员）
     *
     * @param {ProductParams} product 商品参数
     * @returns {Promise<boolean>} 是否创建成功
     */
    async createProduct(product: ProductParams): Promise<boolean> {
      return await performAsyncAction(
        this,
        "adminLoading",
        () => productService.createProduct(product),
        (created: Product) => {
          this.products.unshift(created);
        },
        "创建商品失败：",
        true,
        [HttpStatusCode.Ok],
        "商品创建成功",
      );
    },

    /**
     * 更新商品（管理员）
     * @param {ProductParams} product 商品参数
     * @returns {Promise<boolean>} 是否更新成功
     */
    async updateProduct(product: ProductParams): Promise<boolean> {
      return await performAsyncAction(
        this,
        "adminLoading",
        () => productService.updateProduct(product),
        () => {
          // 更新本地商品列表
          const index = this.products.findIndex((p) => p.id === product.id);
          if (index !== -1) {
            this.products[index] = { ...this.products[index], ...product };
          }
        },
        `更新商品失败（ID: ${product.id}）：`,
        true,
      );
    },

    /**
     * 删除商品（管理员）
     * @param {number} id 商品 ID
     * @returns {Promise<boolean>} 是否删除成功
     */
    async deleteProduct(id: number): Promise<boolean> {
      return await performAsyncAction(
        this,
        "adminLoading",
        () => productService.deleteProduct(id),
        () => {
          // 从本地列表移除
          this.products = this.products.filter((p) => p.id !== id);
        },
        `删除商品失败（ID: ${id}）：`,
        true,
        [HttpStatusCode.Ok],
        "商品删除成功",
      );
    },

    /**
     * 更新商品库存（管理员）
     * @param {number} productId 商品 ID
     * @param {StockpileParams} params 库存参数
     * @returns {Promise<boolean>} 是否更新成功
     */
    async updateStockpile(
      productId: number,
      params: StockpileParams,
    ): Promise<boolean> {
      return await performAsyncAction(
        this,
        "adminLoading",
        () => productService.updateStockpile(productId, params),
        () => {
          if (
            this.currentStockpile &&
            this.currentStockpile.productId === productId
          ) {
            this.currentStockpile = { ...this.currentStockpile, ...params };
          }
        },
        `更新商品库存失败（ID: ${productId}）：`,
        true,
        [HttpStatusCode.Ok],
        "库存更新成功",
      );
    },

    /**
     * 清除当前商品数据
     *
     * @returns {void}
     * @description 清除当前商品和库存数据
     */
    clearCurrentProduct(): void {
      this.currentProduct = null;
      this.currentStockpile = null;
    },
  },
});
