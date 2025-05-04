import { PRODUCT_MODULE } from "@/constants/apiPrefix";
import type { ApiResponse } from "@/types/api";
import type {
  Product,
  ProductParams,
  Stockpile,
  StockpileParams,
} from "@/types/product";
import apiClient from "@/utils/apiClient";

/**
 * 商品服务
 */
export default {
  /**
   * 获取所有商品
   *
   * @returns {Promise<ApiResponse<Product[]>>} 所有商品列表
   */
  getAllProducts(): Promise<ApiResponse<Product[]>> {
    return apiClient.get(`${PRODUCT_MODULE}`);
  },

  /**
   * 获取指定 ID 的商品
   *
   * @param {number} id 商品 ID
   * @returns {Promise<ApiResponse<Product>>} 商品详细信息
   */
  getProductById(id: number): Promise<ApiResponse<Product>> {
    return apiClient.get(`${PRODUCT_MODULE}/${id}`);
  },

  /**
   * 创建商品（管理员）
   *
   * @param {ProductParams} params 商品参数
   * @returns {Promise<ApiResponse<Product>>} 创建的商品
   */
  createProduct(params: ProductParams): Promise<ApiResponse<Product>> {
    return apiClient.post(`${PRODUCT_MODULE}`, params);
  },

  /**
   * 更新商品（管理员）
   *
   * @param {ProductParams} params 商品参数
   * @returns {Promise<ApiResponse<string>>} 更新结果
   */
  updateProduct(params: ProductParams): Promise<ApiResponse<string>> {
    return apiClient.put(`${PRODUCT_MODULE}`, params);
  },

  /**
   * 删除商品（管理员）
   *
   * @param {number} id 商品 ID
   * @returns {Promise<ApiResponse<string>>} 删除结果
   */
  deleteProduct(id: number): Promise<ApiResponse<string>> {
    return apiClient.delete(`${PRODUCT_MODULE}/${id}`);
  },

  /**
   * 获取商品库存
   *
   * @param {number} productId 商品 ID
   * @returns {Promise<ApiResponse<Stockpile>>} 商品库存信息
   */
  getStockpile(productId: number): Promise<ApiResponse<Stockpile>> {
    return apiClient.get(`${PRODUCT_MODULE}/stockpile/${productId}`);
  },

  /**
   * 更新商品库存（管理员）
   *
   * @param {number} productId 商品 ID
   * @param {StockpileParams} params 库存参数
   * @returns {Promise<ApiResponse<string>>} 更新结果
   */
  updateStockpile(
    productId: number,
    params: StockpileParams,
  ): Promise<ApiResponse<string>> {
    return apiClient.patch(`${PRODUCT_MODULE}/stockpile/${productId}`, params);
  },
};
