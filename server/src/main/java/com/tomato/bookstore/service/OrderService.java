package com.tomato.bookstore.service;

import com.tomato.bookstore.dto.CheckoutDTO;
import com.tomato.bookstore.dto.OrderDTO;
import com.tomato.bookstore.dto.PaymentDTO;
import com.tomato.bookstore.dto.PaymentNotifyDTO;
import java.util.List;

/** 订单服务接口 */
public interface OrderService {
  /**
   * 创建订单
   *
   * @param checkoutDTO 结账数据
   * @return 创建的订单
   */
  OrderDTO createOrder(CheckoutDTO checkoutDTO);

  /**
   * 获取订单详情
   *
   * @param userId 用户 ID
   * @param orderId 订单 ID
   * @return 订单详情
   */
  OrderDTO getOrder(Long userId, Long orderId);

  /**
   * 支付订单
   *
   * @param userId 用户 ID
   * @param orderId 订单 ID
   * @return 支付表单
   */
  PaymentDTO payOrder(Long userId, Long orderId);

  /**
   * 处理支付回调
   *
   * @param notifyDTO 支付通知数据
   * @return 处理结果
   */
  PaymentNotifyDTO handlePaymentNotify(PaymentNotifyDTO notifyDTO);

  /**
   * 取消订单
   *
   * @param userId 用户 ID
   * @param orderId 订单 ID
   */
  void cancelOrder(Long userId, Long orderId);

  /** 处理过期订单 */
  void handleExpiredOrders();

  /**
   * 获取用户订单列表
   *
   * @param userId 用户 ID
   * @return 用户订单列表
   */
  List<OrderDTO> getUserOrderList(Long userId);
}
