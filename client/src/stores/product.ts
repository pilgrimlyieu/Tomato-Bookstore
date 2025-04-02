import { HttpStatusCode } from "@/constants/httpStatusCode";
import productService from "@/services/product-service";
import type { Product, ProductParams, StockpileParams } from "@/types/product";
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

  getters: {
    // 获取特色商品（评分最高的前 4 个）
    featuredProducts: (state) => {
      return [...state.products].sort((a, b) => b.rate - a.rate).slice(0, 4);
    },
  },

  actions: {
    /**
     * 获取所有商品
     *
     * @returns {Promise<void>}
     */
    async fetchAllProducts(): Promise<void> {
      try {
        this.loading = true;
        const response = await productService.getAllProducts();

        if (response.code === HttpStatusCode.OK) {
          this.products = response.data;
        }
      } catch (error) {
        console.error("获取商品列表失败：", error);
        ElMessage.error("获取商品列表失败");
      } finally {
        this.loading = false;
      }
    },

    /**
     * 获取指定 ID 的商品详情
     *
     * @param {number} id 商品 ID
     * @returns {Promise<void>}
     */
    async fetchProductById(id: number): Promise<void> {
      try {
        this.loading = true;
        const response = await productService.getProductById(id);

        if (response.code === HttpStatusCode.OK) {
          this.currentProduct = response.data;
        }
      } catch (error) {
        console.error(`获取商品详情失败（ID: ${id}）：`, error);
        ElMessage.error("获取商品详情失败");
      } finally {
        this.loading = false;
      }
    },

    /**
     * 获取商品库存
     *
     * @param {number} productId 商品 ID
     * @returns {Promise<void>}
     */
    async fetchStockpile(productId: number): Promise<void> {
      try {
        this.loading = true;
        const response = await productService.getStockpile(productId);

        if (response.code === HttpStatusCode.OK) {
          this.currentStockpile = response.data;
        }
      } catch (error) {
        console.error(`获取商品库存失败（ID: ${productId}）：`, error);
      } finally {
        this.loading = false;
      }
    },

    /**
     * 创建商品（管理员）
     *
     * @param {ProductParams} product 商品参数
     * @returns {Promise<boolean>} 是否创建成功
     */
    async createProduct(product: ProductParams): Promise<boolean> {
      try {
        this.adminLoading = true;
        const response = await productService.createProduct(product);

        if (response.code === HttpStatusCode.OK) {
          ElMessage.success("商品创建成功");
          return true;
        }
        return false;
      } catch (error) {
        console.error("创建商品失败：", error);
        return false;
      } finally {
        this.adminLoading = false;
      }
    },

    /**
     * 更新商品（管理员）
     * @param {ProductParams} product 商品参数
     * @returns {Promise<boolean>} 是否更新成功
     */
    async updateProduct(product: ProductParams): Promise<boolean> {
      try {
        this.adminLoading = true;
        const response = await productService.updateProduct(product);

        if (response.code === HttpStatusCode.OK) {
          // 更新本地商品列表
          const index = this.products.findIndex((p) => p.id === product.id);
          if (index !== -1) {
            this.products[index] = { ...this.products[index], ...product };
          }
          return true;
        }
        return false;
      } catch (error) {
        console.error(`更新商品失败（ID: ${product.id}）：`, error);
        return false;
      } finally {
        this.adminLoading = false;
      }
    },

    /**
     * 删除商品（管理员）
     * @param {number} id 商品 ID
     * @returns {Promise<boolean>} 是否删除成功
     */
    async deleteProduct(id: number): Promise<boolean> {
      try {
        this.adminLoading = true;
        const response = await productService.deleteProduct(id);

        if (response.code === HttpStatusCode.OK) {
          // 从本地列表移除
          this.products = this.products.filter((p) => p.id !== id);
          ElMessage.success("商品删除成功");
          return true;
        }
        return false;
      } catch (error) {
        console.error(`删除商品失败（ID: ${id}）：`, error);
        return false;
      } finally {
        this.adminLoading = false;
      }
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
      try {
        this.adminLoading = true;
        const response = await productService.updateStockpile(
          productId,
          params,
        );

        if (response.code === HttpStatusCode.OK) {
          if (
            this.currentStockpile &&
            this.currentStockpile.productId === productId
          ) {
            this.currentStockpile = { ...this.currentStockpile, ...params };
          }
          ElMessage.success("库存更新成功");
          return true;
        }
        return false;
      } catch (error) {
        console.error(`更新商品库存失败（ID: ${productId}）：`, error);
        return false;
      } finally {
        this.adminLoading = false;
      }
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
