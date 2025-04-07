package com.tomato.bookstore.service;

import com.tomato.bookstore.dto.CartDTO;
import com.tomato.bookstore.dto.CartListDTO;

/** 购物车服务接口 */
public interface CartService {
  /**
   * 添加商品到购物车
   *
   * @param userId 用户 ID
   * @param cartDTO 购物车商品
   * @return 添加后的购物车商品
   */
  CartDTO addToCart(Long userId, CartDTO cartDTO);

  /**
   * 删除购物车商品
   *
   * @param userId 用户 ID
   * @param cartItemId 购物车商品 ID
   */
  void removeFromCart(Long userId, Long cartItemId);

  /**
   * 更新购物车商品数量
   *
   * @param userId 用户 ID
   * @param cartItemId 购物车商品 ID
   * @param quantity 新的数量
   */
  void updateQuantity(Long userId, Long cartItemId, Integer quantity);

  /**
   * 获取用户的购物车列表
   *
   * @param userId 用户 ID
   * @return 购物车列表
   */
  CartListDTO getUserCart(Long userId);

  /**
   * 清空用户的购物车
   *
   * @param userId 用户 ID
   */
  void clearCart(Long userId);
}
