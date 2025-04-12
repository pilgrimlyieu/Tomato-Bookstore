/**
 * 广告数据类型
 */
export interface Advertisement {
  /**
   * 广告 ID
   */
  id?: number;

  /**
   * 广告标题
   */
  title: string;

  /**
   * 广告内容
   */
  content: string;

  /**
   * 广告图片 URL
   */
  imageUrl: string;

  /**
   * 关联商品 ID
   */
  productId: number;
}
