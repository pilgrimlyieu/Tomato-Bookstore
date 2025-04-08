package com.tomato.bookstore.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.tomato.bookstore.config.AlipayConfig;
import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.constant.ExceptionMessages;
import com.tomato.bookstore.constant.OrderStatus;
import com.tomato.bookstore.dto.CartDTO;
import com.tomato.bookstore.dto.CheckoutDTO;
import com.tomato.bookstore.dto.OrderDTO;
import com.tomato.bookstore.dto.PaymentDTO;
import com.tomato.bookstore.dto.PaymentNotifyDTO;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.exception.ResourceNotFoundException;
import com.tomato.bookstore.model.Cart;
import com.tomato.bookstore.model.CartsOrdersRelation;
import com.tomato.bookstore.model.Order;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.model.Stockpile;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.repository.CartRepository;
import com.tomato.bookstore.repository.CartsOrdersRelationRepository;
import com.tomato.bookstore.repository.OrderRepository;
import com.tomato.bookstore.repository.StockpileRepository;
import com.tomato.bookstore.repository.UserRepository;
import com.tomato.bookstore.service.CartService;
import com.tomato.bookstore.service.OrderService;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/** 订单服务实现 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
  private final Clock clock;
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final CartsOrdersRelationRepository relationRepository;
  private final StockpileRepository stockpileRepository;
  private final AlipayClient alipayClient;
  private final AlipayConfig alipayConfig;
  private final CartService cartService;

  // 订单过期时间：30 分钟
  private static int ORDER_EXPIRATION_MINUTES = 30;

  @Override
  @Transactional
  public OrderDTO createOrder(CheckoutDTO checkoutDTO) {
    log.info(
        "创建订单：userId={}, cartItemIds={}", checkoutDTO.getUserId(), checkoutDTO.getCartItemIds());

    if (CollectionUtils.isEmpty(checkoutDTO.getCartItemIds())) {
      throw new BusinessException(
          BusinessErrorCode.ORDER_EMPTY_CART_ITEMS, ExceptionMessages.ORDER_EMPTY_CART_ITEMS);
    }

    User user = findUser(checkoutDTO.getUserId());

    // 获取待结算的购物车商品
    List<Cart> cartItems = cartRepository.findByUserAndIdIn(user, checkoutDTO.getCartItemIds());
    if (cartItems.isEmpty()) {
      throw new BusinessException(BusinessErrorCode.CART_ITEM_NOT_FOUND, "未找到选择的购物车商品");
    }

    // 计算订单总金额
    BigDecimal totalAmount = calculateTotalAmount(cartItems);

    // 预先检查所有库存，确保在创建订单前所有商品都有足够库存
    for (Cart cart : cartItems) {
      Stockpile stockpile =
          stockpileRepository
              .findByProductId(cart.getProduct().getId())
              .orElseThrow(
                  () -> {
                    log.warn("商品库存记录不存在：productId={}", cart.getProduct().getId());
                    return new BusinessException(
                        BusinessErrorCode.PRODUCT_STOCK_NOT_FOUND,
                        String.format(
                            ExceptionMessages.PRODUCT_STOCK_NOT_FOUND, cart.getProduct().getId()));
                  });

      // 检查可用库存
      if (stockpile.getAmount() < cart.getQuantity()) {
        Product product = stockpile.getProduct();
        log.warn(
            "商品库存不足：productId={}, title={}, 库存={}, 需要={}",
            product.getId(),
            product.getTitle(),
            stockpile.getAmount(),
            cart.getQuantity());
        throw new BusinessException(
            BusinessErrorCode.PRODUCT_STOCK_INSUFFICIENT,
            String.format(
                "商品「%s」库存不足，当前库存：%d，需要数量：%d",
                product.getTitle(), stockpile.getAmount(), cart.getQuantity()));
      }
    }

    // 创建订单
    LocalDateTime now = LocalDateTime.now();
    Order order =
        Order.builder()
            .user(user)
            .totalAmount(totalAmount)
            .paymentMethod(checkoutDTO.getPaymentMethod())
            .status(OrderStatus.PENDING)
            .shippingAddress(checkoutDTO.getShippingAddress())
            .createdAt(now)
            .updatedAt(now)
            .orderItems(new ArrayList<>())
            .build();

    Order savedOrder = orderRepository.save(order);

    // 创建订单项关联并锁定库存
    List<CartsOrdersRelation> orderItems = new ArrayList<>();
    for (Cart cart : cartItems) {
      // 锁定库存
      lockStock(cart.getProduct().getId(), cart.getQuantity());

      // 创建关联
      CartsOrdersRelation relation =
          CartsOrdersRelation.builder()
              .cart(cart)
              .order(savedOrder)
              .quantity(cart.getQuantity())
              .build();
      orderItems.add(relation);
    }

    // 保存关联
    relationRepository.saveAll(orderItems);
    savedOrder.setOrderItems(orderItems);

    log.info("订单创建成功：orderId={}, totalAmount={}", savedOrder.getId(), totalAmount);
    return convertToOrderDTO(savedOrder);
  }

  @Override
  @Transactional(readOnly = true)
  public OrderDTO getOrder(Long userId, Long orderId) {
    log.info("获取订单详情：userId={}, orderId={}", userId, orderId);
    Order order = findOrderByUserAndId(userId, orderId);
    return convertToOrderDTO(order);
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderDTO> getUserOrderList(Long userId) {
    log.info("获取用户订单列表：userId={}", userId);
    User user = findUser(userId);
    List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);

    return orders.stream().map(this::convertToOrderDTO).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public PaymentDTO payOrder(Long userId, Long orderId) {
    log.info("支付订单：userId={}, orderId={}", userId, orderId);
    Order order = findOrderByUserAndId(userId, orderId);

    // 检查订单状态
    if (order.getStatus() != OrderStatus.PENDING) {
      log.warn("订单状态错误：orderId={}, status={}", orderId, order.getStatus());
      throw new BusinessException(
          BusinessErrorCode.ORDER_STATUS_ERROR,
          String.format(
              ExceptionMessages.ORDER_STATUS_ERROR, order.getStatus(), OrderStatus.PENDING));
    }

    try {
      // 创建支付宝支付表单
      String paymentForm = createAlipayForm(order);

      log.info("生成支付表单成功：orderId={}", orderId);
      return PaymentDTO.builder()
          .paymentForm(paymentForm)
          .orderId(orderId)
          .totalAmount(order.getTotalAmount())
          .paymentMethod(order.getPaymentMethod())
          .build();
    } catch (AlipayApiException e) {
      log.error("生成支付表单失败：orderId={}, error={}", orderId, e.getMessage(), e);
      throw new BusinessException(BusinessErrorCode.ORDER_PAY_FAILED, "创建支付表单失败：" + e.getMessage());
    }
  }

  @Override
  @Transactional
  public PaymentNotifyDTO handlePaymentNotify(PaymentNotifyDTO notifyDTO) {
    log.info(
        "处理支付回调：orderId={}, status={}, tradeNo={}",
        notifyDTO.getOrderId(),
        notifyDTO.getStatus(),
        notifyDTO.getTradeNo());

    Order order =
        orderRepository
            .findById(notifyDTO.getOrderId())
            .orElseThrow(
                () -> {
                  log.warn("订单不存在：orderId={}", notifyDTO.getOrderId());
                  return new ResourceNotFoundException(
                      String.format(ExceptionMessages.ORDER_NOT_FOUND, notifyDTO.getOrderId()));
                });

    // 检查订单状态
    if (order.getStatus() != OrderStatus.PENDING) {
      log.warn("订单状态错误：orderId={}, status={}", notifyDTO.getOrderId(), order.getStatus());
      throw new BusinessException(
          BusinessErrorCode.ORDER_STATUS_ERROR,
          String.format(
              ExceptionMessages.ORDER_STATUS_ERROR, order.getStatus(), OrderStatus.PENDING));
    }

    // 验证支付金额
    if (order.getTotalAmount().compareTo(notifyDTO.getTotalAmount()) != 0) {
      log.warn(
          "支付金额不匹配：orderId={}, 订单金额={}, 支付金额={}",
          notifyDTO.getOrderId(),
          order.getTotalAmount(),
          notifyDTO.getTotalAmount());
      throw new BusinessException(BusinessErrorCode.ORDER_PAYMENT_AMOUNT_ERROR, "支付金额与订单金额不匹配");
    }

    // 更新订单状态
    order.setStatus(OrderStatus.PAID);
    order.setTradeNo(notifyDTO.getTradeNo());
    order.setPaymentTime(notifyDTO.getPaymentTime());
    order.setUpdatedAt(LocalDateTime.now());
    orderRepository.save(order);

    // 减少商品库存
    reduceStock(order);

    // 清空用户购物车
    cartService.clearCart(order.getUser().getId());
    log.info("已清空用户购物车：userId={}", order.getUser().getId());

    log.info("订单支付成功：orderId={}, tradeNo={}", order.getId(), notifyDTO.getTradeNo());
    return notifyDTO;
  }

  @Override
  @Transactional
  public void cancelOrder(Long userId, Long orderId) {
    log.info("取消订单：userId={}, orderId={}", userId, orderId);
    Order order = findOrderByUserAndId(userId, orderId);

    // 检查订单状态
    if (order.getStatus() != OrderStatus.PENDING) {
      log.warn("订单状态错误，不可取消：orderId={}, status={}", orderId, order.getStatus());
      throw new BusinessException(BusinessErrorCode.ORDER_CANNOT_CANCEL, "非待支付状态的订单无法取消");
    }

    // 更新订单状态
    order.setStatus(OrderStatus.CANCELLED);
    order.setUpdatedAt(LocalDateTime.now());
    orderRepository.save(order);

    // 解锁库存
    unlockStock(order);

    log.info("订单已取消：orderId={}", orderId);
  }

  @Override
  @Scheduled(fixedRate = 60000) // 每分钟执行一次
  @Transactional
  public void handleExpiredOrders() {
    log.info("处理过期订单");

    // 查找过期的待支付订单
    LocalDateTime expiredTime = LocalDateTime.now(clock).minusMinutes(ORDER_EXPIRATION_MINUTES);
    List<Order> expiredOrders =
        orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, expiredTime);

    if (expiredOrders.isEmpty()) {
      log.info("没有过期订单需要处理");
      return;
    }

    log.info("发现 {} 个过期订单", expiredOrders.size());

    for (Order order : expiredOrders) {
      // 更新订单状态
      order.setStatus(OrderStatus.TIMEOUT);
      order.setUpdatedAt(LocalDateTime.now());

      // 解锁库存
      unlockStock(order);

      log.info("订单已标记为超时：orderId={}", order.getId());
    }

    // 批量保存订单状态
    orderRepository.saveAll(expiredOrders);

    log.info("过期订单处理完成");
  }

  /**
   * 计算订单总金额
   *
   * @param cartItems 购物车商品列表
   * @return 订单总金额
   */
  private BigDecimal calculateTotalAmount(List<Cart> cartItems) {
    return cartItems.stream()
        .map(cart -> cart.getProduct().getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * 创建支付宝支付表单
   *
   * @param order 订单对象
   * @return 支付表单字符串
   * @throws AlipayApiException 如果调用支付宝接口失败
   */
  private String createAlipayForm(Order order) throws AlipayApiException {
    AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
    request.setNotifyUrl(alipayConfig.getNotifyUrl());
    request.setReturnUrl(alipayConfig.getReturnUrl());

    // 构建请求参数
    String subject = "番茄书店订单-" + order.getId();
    String outTradeNo = order.getId().toString(); // 以订单 ID 作为商户订单号
    String totalAmount = order.getTotalAmount().toString();

    StringBuilder builder = new StringBuilder();
    builder.append("{\"out_trade_no\":\"").append(outTradeNo).append("\",");
    builder.append("\"total_amount\":\"").append(totalAmount).append("\",");
    builder.append("\"subject\":\"").append(subject).append("\",");
    builder.append("\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

    request.setBizContent(builder.toString());

    log.debug(
        "支付宝请求参数：notifyUrl={}, returnUrl={}, bizContent={}",
        alipayConfig.getNotifyUrl(),
        alipayConfig.getReturnUrl(),
        builder);

    AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
    if (!response.isSuccess()) {
      log.error(
          "调用支付宝接口失败：code={}, msg={}, subCode={}, subMsg={}",
          response.getCode(),
          response.getMsg(),
          response.getSubCode(),
          response.getSubMsg());
      throw new AlipayApiException("调用支付宝接口失败");
    }

    return response.getBody();
  }

  /**
   * 锁定商品库存
   *
   * @param productId 商品 ID
   * @param quantity 锁定数量
   */
  private void lockStock(Long productId, Integer quantity) {
    Stockpile stockpile =
        stockpileRepository
            .findByProductId(productId)
            .orElseThrow(
                () -> {
                  log.warn("商品库存记录不存在：productId={}", productId);
                  return new BusinessException(
                      BusinessErrorCode.PRODUCT_STOCK_NOT_FOUND,
                      String.format(ExceptionMessages.PRODUCT_STOCK_NOT_FOUND, productId));
                });

    // 检查可用库存
    if (stockpile.getAmount() < quantity) {
      Product product = stockpile.getProduct();
      log.warn(
          "商品库存不足：productId={}, title={}, 库存={}, 需要={}",
          productId,
          product.getTitle(),
          stockpile.getAmount(),
          quantity);
      throw new BusinessException(
          BusinessErrorCode.PRODUCT_STOCK_INSUFFICIENT,
          String.format(
              "商品「%s」库存不足，当前库存：%d，需要数量：%d", product.getTitle(), stockpile.getAmount(), quantity));
    }

    // 锁定库存
    stockpile.setFrozen(stockpile.getFrozen() + quantity);
    stockpile.setAmount(stockpile.getAmount() - quantity);
    stockpileRepository.save(stockpile);

    log.info(
        "商品库存已锁定：productId={}, quantity={}, 剩余库存={}, 冻结库存={}",
        productId,
        quantity,
        stockpile.getAmount(),
        stockpile.getFrozen());
  }

  /**
   * 解锁商品库存
   *
   * @param order 订单对象
   */
  private void unlockStock(Order order) {
    List<CartsOrdersRelation> relations = relationRepository.findByOrder(order);

    for (CartsOrdersRelation relation : relations) {
      Cart cart = relation.getCart();
      Long productId = cart.getProduct().getId();
      Integer quantity = relation.getQuantity();

      Stockpile stockpile =
          stockpileRepository
              .findByProductId(productId)
              .orElseThrow(
                  () -> {
                    log.warn("商品库存记录不存在：productId={}", productId);
                    return new BusinessException(
                        BusinessErrorCode.PRODUCT_STOCK_NOT_FOUND,
                        String.format(ExceptionMessages.PRODUCT_STOCK_NOT_FOUND, productId));
                  });

      // 解锁库存
      stockpile.setFrozen(Math.max(0, stockpile.getFrozen() - quantity));
      stockpile.setAmount(stockpile.getAmount() + quantity);
      stockpileRepository.save(stockpile);

      log.info(
          "商品库存已解锁：productId={}, quantity={}, 剩余库存={}, 冻结库存={}",
          productId,
          quantity,
          stockpile.getAmount(),
          stockpile.getFrozen());
    }
  }

  /**
   * 减少商品库存（支付成功后）
   *
   * @param order 订单对象
   */
  private void reduceStock(Order order) {
    List<CartsOrdersRelation> relations = relationRepository.findByOrder(order);

    for (CartsOrdersRelation relation : relations) {
      Cart cart = relation.getCart();
      Long productId = cart.getProduct().getId();
      Integer quantity = relation.getQuantity();

      Stockpile stockpile =
          stockpileRepository
              .findByProductId(productId)
              .orElseThrow(
                  () -> {
                    log.warn("商品库存记录不存在：productId={}", productId);
                    return new BusinessException(
                        BusinessErrorCode.PRODUCT_STOCK_NOT_FOUND,
                        String.format(ExceptionMessages.PRODUCT_STOCK_NOT_FOUND, productId));
                  });

      // 减少冻结的库存
      stockpile.setFrozen(Math.max(0, stockpile.getFrozen() - quantity));
      stockpileRepository.save(stockpile);

      log.info(
          "商品库存已减少：productId={}, quantity={}, 剩余库存={}, 冻结库存={}",
          productId,
          quantity,
          stockpile.getAmount(),
          stockpile.getFrozen());
    }
  }

  /**
   * 根据用户 ID 和订单 ID 查找订单
   *
   * @param userId 用户 ID
   * @return 订单对象
   */
  private Order findOrderByUserAndId(Long userId, Long orderId) {
    return orderRepository
        .findByUserAndId(findUser(userId), orderId)
        .orElseThrow(
            () -> {
              log.warn("订单不存在：userId={}, orderId={}", userId, orderId);
              return new ResourceNotFoundException(
                  String.format(ExceptionMessages.ORDER_NOT_FOUND, orderId));
            });
  }

  /**
   * 查找用户
   *
   * @param userId 用户 ID
   * @return 用户对象
   */
  private User findUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> {
              log.warn("用户不存在：userId={}", userId);
              return new ResourceNotFoundException(
                  String.format(ExceptionMessages.USER_NOT_FOUND, userId));
            });
  }

  /**
   * 转换为 OrderDTO
   *
   * @param order 订单对象
   * @return OrderDTO 对象
   */
  private OrderDTO convertToOrderDTO(Order order) {
    List<CartDTO> orderItems =
        order.getOrderItems().stream()
            .map(
                relation -> {
                  Cart cart = relation.getCart();
                  Product product = cart.getProduct();
                  return CartDTO.builder()
                      .cartItemId(cart.getId())
                      .productId(product.getId())
                      .title(product.getTitle())
                      .price(product.getPrice())
                      .description(product.getDescription())
                      .cover(product.getCover())
                      .quantity(relation.getQuantity())
                      .build();
                })
            .collect(Collectors.toList());

    return OrderDTO.builder()
        .orderId(order.getId())
        .userId(order.getUser().getId())
        .totalAmount(order.getTotalAmount())
        .paymentMethod(order.getPaymentMethod())
        .status(order.getStatus())
        .shippingAddress(order.getShippingAddress())
        .tradeNo(order.getTradeNo())
        .paymentTime(order.getPaymentTime())
        .createTime(order.getCreatedAt())
        .updateTime(order.getUpdatedAt())
        .orderItems(orderItems)
        .build();
  }
}
