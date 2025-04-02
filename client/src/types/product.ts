/**
 * 商品规格接口
 */
export interface Specification {
  id?: number;
  item: string;
  value: string;
}

/**
 * 商品接口
 */
export interface Product {
  id?: number;
  title: string;
  price: number;
  rate: number;
  description?: string;
  cover?: string;
  detail?: string;
  specifications?: Specification[];
}

/**
 * 商品参数接口（用于创建/更新）
 */
export type ProductParams = Product;

/**
 * 商品库存接口
 */
export interface Stockpile {
  id?: number;
  amount: number;
  frozen: number;
  productId?: number;
}

/**
 * 商品库存参数接口（用于更新）
 */
export interface StockpileParams {
  amount: number;
  frozen: number;
}
