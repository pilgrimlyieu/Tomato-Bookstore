package com.tomato.bookstore.controller;

import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.constant.RoleConstants;
import com.tomato.bookstore.dto.ApiResponse;
import com.tomato.bookstore.dto.CartDTO;
import com.tomato.bookstore.dto.CartListDTO;
import com.tomato.bookstore.dto.CheckoutDTO;
import com.tomato.bookstore.dto.OrderDTO;
import com.tomato.bookstore.dto.QuantityUpdateDTO;
import com.tomato.bookstore.security.UserPrincipal;
import com.tomato.bookstore.service.CartService;
import com.tomato.bookstore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** 购物车控制器 */
@RestController
@RequestMapping(ApiConstants.CART_BASE_PATH)
@RequiredArgsConstructor
@Slf4j
@Validated
public class CartController {
  private final CartService cartService;
  private final OrderService orderService;

  /**
   * 添加商品到购物车
   *
   * @param cartDTO 购物车商品信息
   * @param userPrincipal 当前用户
   * @return 添加结果
   */
  @PostMapping
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<CartDTO> addToCart(
      @RequestBody @Valid CartDTO cartDTO, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info(
        "用户「{}」添加商品到购物车：productId={}, quantity={}",
        userPrincipal.getUsername(),
        cartDTO.getProductId(),
        cartDTO.getQuantity());
    CartDTO result = cartService.addToCart(userPrincipal.getUserId(), cartDTO);
    return ApiResponse.success(result);
  }

  /**
   * 从购物车删除商品
   *
   * @param cartItemId 购物车商品 ID
   * @param userPrincipal 当前用户
   * @return 删除结果
   */
  @DeleteMapping(ApiConstants.CART_ITEM)
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<String> removeFromCart(
      @PathVariable Long cartItemId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户「{}」从购物车删除商品：cartItemId={}", userPrincipal.getUsername(), cartItemId);
    cartService.removeFromCart(userPrincipal.getUserId(), cartItemId);
    return ApiResponse.success("删除成功");
  }

  /**
   * 更新购物车商品数量
   *
   * @param cartItemId 购物车商品 ID
   * @param updateDTO 更新信息
   * @param userPrincipal 当前用户
   * @return 更新结果
   */
  @PatchMapping(ApiConstants.CART_ITEM)
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<String> updateQuantity(
      @PathVariable Long cartItemId,
      @RequestBody @Valid QuantityUpdateDTO updateDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info(
        "用户「{}」修改购物车商品数量：cartItemId={}, quantity={}",
        userPrincipal.getUsername(),
        cartItemId,
        updateDTO.getQuantity());
    cartService.updateQuantity(userPrincipal.getUserId(), cartItemId, updateDTO.getQuantity());
    return ApiResponse.success("修改数量成功");
  }

  /**
   * 获取用户购物车
   *
   * @param userPrincipal 当前用户
   * @return 用户购物车信息
   */
  @GetMapping
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<CartListDTO> getUserCart(
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户「{}」获取购物车", userPrincipal.getUsername());
    CartListDTO cart = cartService.getUserCart(userPrincipal.getUserId());
    return ApiResponse.success(cart);
  }

  /**
   * 结算购物车（创建订单）
   *
   * @param checkoutDTO 结算信息
   * @param userPrincipal 当前用户
   * @return 订单信息
   */
  @PostMapping(ApiConstants.CART_CHECKOUT)
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<OrderDTO> checkout(
      @RequestBody @Valid CheckoutDTO checkoutDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info(
        "用户「{}」结算购物车：cartItemIds={}", userPrincipal.getUsername(), checkoutDTO.getCartItemIds());
    checkoutDTO.setUserId(userPrincipal.getUserId());
    OrderDTO order = orderService.createOrder(checkoutDTO);
    return ApiResponse.success(order);
  }

  /**
   * 清空购物车
   *
   * @param userPrincipal 当前用户
   * @return 清空结果
   */
  @DeleteMapping
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<String> clearCart(
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户「{}」清空购物车", userPrincipal.getUsername());
    cartService.clearCart(userPrincipal.getUserId());
    return ApiResponse.success("清空购物车成功");
  }
}
