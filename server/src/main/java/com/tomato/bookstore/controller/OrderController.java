package com.tomato.bookstore.controller;

import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.constant.RoleConstants;
import com.tomato.bookstore.dto.ApiResponse;
import com.tomato.bookstore.dto.OrderDTO;
import com.tomato.bookstore.dto.PaymentDTO;
import com.tomato.bookstore.dto.PaymentNotifyDTO;
import com.tomato.bookstore.security.UserPrincipal;
import com.tomato.bookstore.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** 订单控制器 */
@RestController
@RequestMapping(ApiConstants.ORDER_BASE_PATH)
@RequiredArgsConstructor
@Slf4j
@Validated
public class OrderController {
  private final OrderService orderService;

  @Value("${app.frontend.base-url}")
  private String frontendBaseUrl;

  /**
   * 获取订单详情
   *
   * @param orderId 订单 ID
   * @param userPrincipal 当前用户
   * @return 订单详情
   */
  @GetMapping(ApiConstants.ORDER_DETAIL)
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<OrderDTO> getOrder(
      @PathVariable Long orderId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户「{}」查看订单：orderId={}", userPrincipal.getUsername(), orderId);
    OrderDTO order = orderService.getOrder(userPrincipal.getUserId(), orderId);
    return ApiResponse.success(order);
  }

  /**
   * 获取用户的订单列表
   *
   * @param userPrincipal 当前用户
   * @return 订单列表
   */
  @GetMapping
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<List<OrderDTO>> getUserOrderList(
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户「{}」获取订单列表", userPrincipal.getUsername());
    List<OrderDTO> orders = orderService.getUserOrderList(userPrincipal.getUserId());
    return ApiResponse.success(orders);
  }

  /**
   * 支付订单
   *
   * @param orderId 订单 ID
   * @param userPrincipal 当前用户
   * @return 支付信息
   */
  @PostMapping(ApiConstants.ORDER_PAY)
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<PaymentDTO> payOrder(
      @PathVariable Long orderId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户「{}」支付订单：orderId={}", userPrincipal.getUsername(), orderId);
    PaymentDTO paymentDTO = orderService.payOrder(userPrincipal.getUserId(), orderId);
    return ApiResponse.success(paymentDTO);
  }

  /**
   * 取消订单
   *
   * @param orderId 订单 ID
   * @param userPrincipal 当前用户
   * @return 取消结果
   */
  @DeleteMapping(ApiConstants.ORDER_DETAIL)
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<String> cancelOrder(
      @PathVariable Long orderId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户「{}」取消订单：orderId={}", userPrincipal.getUsername(), orderId);
    orderService.cancelOrder(userPrincipal.getUserId(), orderId);
    return ApiResponse.success("订单已取消");
  }

  /**
   * 支付通知回调
   *
   * <p>支付宝支付成功后，支付宝会向该接口发送支付结果通知。
   */
  @PostMapping(
      value = ApiConstants.ORDER_NOTIFY,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ApiResponse<PaymentNotifyDTO> handlePaymentNotify(
      @RequestParam("out_trade_no") String outTradeNo,
      @RequestParam("trade_no") String tradeNo,
      @RequestParam("trade_status") String tradeStatus,
      @RequestParam("total_amount") String totalAmount,
      @RequestParam(value = "gmt_payment", required = false) String gmtPayment) {
    log.info(
        "收到支付回调通知：outTradeNo={}, tradeNo={}, status={}, amount={}, time={}",
        outTradeNo,
        tradeNo,
        tradeStatus,
        totalAmount,
        gmtPayment);

    // 构建支付通知对象
    LocalDateTime paymentTime = null;
    if (gmtPayment != null && !gmtPayment.isEmpty()) {
      try {
        paymentTime =
            LocalDateTime.parse(gmtPayment, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      } catch (Exception e) {
        log.error("解析支付时间失败：", e);
      }
    }

    PaymentNotifyDTO notifyDTO =
        PaymentNotifyDTO.builder()
            .orderId(Long.valueOf(outTradeNo))
            .tradeNo(tradeNo)
            .status(tradeStatus)
            .totalAmount(new BigDecimal(totalAmount))
            .paymentTime(paymentTime)
            .build();

    // 处理支付通知
    PaymentNotifyDTO result = orderService.handlePaymentNotify(notifyDTO);
    return ApiResponse.success("支付成功", result);
  }

  /**
   * 支付同步返回
   *
   * <p>支付宝支付成功后，用户会被重定向到该接口。
   */
  @GetMapping(value = ApiConstants.ORDER_RETURN)
  public String handlePaymentReturn(
      @RequestParam("out_trade_no") String outTradeNo,
      @RequestParam("trade_no") String tradeNo,
      @RequestParam(value = "total_amount", required = false) String totalAmount,
      HttpServletResponse response) {

    log.info("收到支付同步返回：outTradeNo={}, tradeNo={}, amount={}", outTradeNo, tradeNo, totalAmount);

    try {
      // 重定向到前端订单成功页面
      String redirectUrl = frontendBaseUrl + "/orders/success?orderId=" + outTradeNo;
      log.info("重定向到前端订单成功页面：{}", redirectUrl);
      response.sendRedirect(redirectUrl);
      return null;
    } catch (IOException e) {
      log.error("重定向失败：", e);
      return "支付处理中，请等待系统确认";
    }
  }
}
