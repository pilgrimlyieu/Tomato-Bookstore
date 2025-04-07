package com.tomato.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.tomato.bookstore.config.AlipayConfig;
import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.constant.OrderStatus;
import com.tomato.bookstore.constant.PaymentMethod;
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
import com.tomato.bookstore.service.impl.OrderServiceImpl;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
  @Mock private OrderRepository orderRepository;
  @Mock private UserRepository userRepository;
  @Mock private CartRepository cartRepository;
  @Mock private CartsOrdersRelationRepository relationRepository;
  @Mock private StockpileRepository stockpileRepository;
  @Mock private AlipayClient alipayClient;
  @Mock private AlipayConfig alipayConfig;
  @Mock private AlipayTradePagePayResponse alipayResponse;

  @InjectMocks private OrderServiceImpl orderService;

  private Clock fixedClock;
  private User user;
  private Product product;
  private Stockpile stockpile;
  private Cart cart;
  private Order order;
  private CheckoutDTO checkoutDTO;
  private CartsOrdersRelation orderItem;
  private List<Long> cartItemIds;
  private PaymentNotifyDTO paymentNotifyDTO;

  private static final Long USER_ID = 1L;
  private static final Long ORDER_ID = 1L;
  private static final Long PRODUCT_ID = 1L;
  private static final Long CART_ITEM_ID = 1L;
  private static final Integer QUANTITY = 2;
  private static final String SHIPPING_ADDRESS = "测试地址";
  private static final BigDecimal PRICE = new BigDecimal("49.90");
  private static final String PAYMENT_FORM = "<form>支付表单</form>";
  private static final String ALIPAY_TRADE_NO = "2023112822001123456789";

  @BeforeEach
  void setUp() {
    // 设置测试数据
    fixedClock = Clock.fixed(Instant.parse("2025-05-15T08:00:00.238770100Z"), ZoneId.of("UTC"));
    ReflectionTestUtils.setField(orderService, "clock", fixedClock);
    LocalDateTime now = LocalDateTime.now(fixedClock);

    // 创建用户
    user = new User();
    user.setId(USER_ID);
    user.setUsername("testuser");
    user.setEmail("test@example.com");
    user.setCreatedAt(now);

    // 创建商品
    product = new Product();
    product.setId(PRODUCT_ID);
    product.setTitle("测试商品");
    product.setPrice(PRICE);
    product.setCreatedAt(now);

    // 创建库存
    stockpile = new Stockpile();
    stockpile.setId(1L);
    stockpile.setProduct(product);
    stockpile.setAmount(100);
    stockpile.setFrozen(0);
    product.setStockpile(stockpile);

    // 创建购物车项
    cart = new Cart();
    cart.setId(CART_ITEM_ID);
    cart.setUser(user);
    cart.setProduct(product);
    cart.setQuantity(QUANTITY);
    cart.setCreatedAt(now);

    // 创建订单
    order = new Order();
    order.setId(ORDER_ID);
    order.setUser(user);
    order.setTotalAmount(PRICE.multiply(BigDecimal.valueOf(QUANTITY)));
    order.setPaymentMethod(PaymentMethod.ALIPAY);
    order.setStatus(OrderStatus.PENDING);
    order.setShippingAddress(SHIPPING_ADDRESS);
    order.setCreatedAt(now);
    order.setOrderItems(new ArrayList<>());

    // 创建订单项关联
    orderItem = new CartsOrdersRelation();
    orderItem.setId(1L);
    orderItem.setCart(cart);
    orderItem.setOrder(order);
    orderItem.setQuantity(QUANTITY);

    List<CartsOrdersRelation> orderItems = new ArrayList<>();
    orderItems.add(orderItem);
    order.setOrderItems(orderItems);

    // 准备结账DTO
    cartItemIds = Collections.singletonList(CART_ITEM_ID);
    checkoutDTO = new CheckoutDTO();
    checkoutDTO.setUserId(USER_ID);
    checkoutDTO.setCartItemIds(cartItemIds);
    checkoutDTO.setShippingAddress(SHIPPING_ADDRESS);
    checkoutDTO.setPaymentMethod(PaymentMethod.ALIPAY);

    // 准备支付通知
    paymentNotifyDTO =
        PaymentNotifyDTO.builder()
            .orderId(ORDER_ID)
            .status("TRADE_SUCCESS")
            .tradeNo(ALIPAY_TRADE_NO)
            .totalAmount(PRICE.multiply(BigDecimal.valueOf(QUANTITY)))
            .paymentTime(now)
            .build();
  }

  @Test
  @DisplayName("创建订单 - 成功")
  void createOrderSuccess() {
    // 准备
    List<Cart> cartItems = Collections.singletonList(cart);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(cartRepository.findByUserAndIdIn(user, cartItemIds)).thenReturn(cartItems);
    when(stockpileRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(stockpile));
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(
            i -> {
              Order savedOrder = i.getArgument(0);
              savedOrder.setId(ORDER_ID);
              return savedOrder;
            });
    when(relationRepository.saveAll(anyList())).thenReturn(Collections.singletonList(orderItem));

    // 执行
    OrderDTO result = orderService.createOrder(checkoutDTO);

    // 验证
    assertNotNull(result);
    assertEquals(ORDER_ID, result.getOrderId());
    assertEquals(USER_ID, result.getUserId());
    assertEquals(PRICE.multiply(BigDecimal.valueOf(QUANTITY)), result.getTotalAmount());
    assertEquals(PaymentMethod.ALIPAY, result.getPaymentMethod());
    assertEquals(OrderStatus.PENDING, result.getStatus());
    assertEquals(SHIPPING_ADDRESS, result.getShippingAddress());

    verify(orderRepository).save(any(Order.class));
    verify(relationRepository).saveAll(anyList());
    verify(stockpileRepository).save(stockpile);

    // 验证库存锁定
    assertEquals(QUANTITY, stockpile.getFrozen());
    assertEquals(100 - QUANTITY, stockpile.getAmount());
  }

  @Test
  @DisplayName("创建订单 - 失败，购物车为空")
  void createOrderFailsWhenCartEmpty() {
    // 准备
    checkoutDTO.setCartItemIds(new ArrayList<>());

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              orderService.createOrder(checkoutDTO);
            });

    assertEquals(BusinessErrorCode.ORDER_EMPTY_CART_ITEMS, exception.getErrorCode());
    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  @DisplayName("创建订单 - 失败，购物车商品不存在")
  void createOrderFailsWhenCartItemsNotFound() {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(cartRepository.findByUserAndIdIn(user, cartItemIds)).thenReturn(new ArrayList<>());

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              orderService.createOrder(checkoutDTO);
            });

    assertEquals(BusinessErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  @DisplayName("创建订单 - 失败，库存不足")
  void createOrderFailsWhenStockInsufficient() {
    // 准备
    List<Cart> cartItems = Collections.singletonList(cart);
    stockpile.setAmount(1); // 设置库存小于购买数量

    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(cartRepository.findByUserAndIdIn(user, cartItemIds)).thenReturn(cartItems);
    when(stockpileRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(stockpile));

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              orderService.createOrder(checkoutDTO);
            });

    assertEquals(BusinessErrorCode.PRODUCT_STOCK_INSUFFICIENT, exception.getErrorCode());
    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  @DisplayName("获取订单详情 - 成功")
  void getOrderSuccess() {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(orderRepository.findByUserAndId(user, ORDER_ID)).thenReturn(Optional.of(order));

    // 执行
    OrderDTO result = orderService.getOrder(USER_ID, ORDER_ID);

    // 验证
    assertNotNull(result);
    assertEquals(ORDER_ID, result.getOrderId());
    assertEquals(USER_ID, result.getUserId());
    assertEquals(OrderStatus.PENDING, result.getStatus());
  }

  @Test
  @DisplayName("获取订单详情 - 失败，订单不存在")
  void getOrderFailsWhenOrderNotFound() {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(orderRepository.findByUserAndId(user, ORDER_ID)).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              orderService.getOrder(USER_ID, ORDER_ID);
            });

    assertTrue(exception.getMessage().contains("订单不存在"));
  }

  @Test
  @DisplayName("支付订单 - 成功")
  void payOrderSuccess() throws Exception {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(orderRepository.findByUserAndId(user, ORDER_ID)).thenReturn(Optional.of(order));
    when(alipayConfig.getNotifyUrl()).thenReturn("http://localhost:8080/api/orders/notify");
    when(alipayConfig.getReturnUrl()).thenReturn("http://localhost:8080/api/orders/return");

    // Mock支付宝响应
    when(alipayResponse.isSuccess()).thenReturn(true);
    when(alipayResponse.getBody()).thenReturn(PAYMENT_FORM);
    when(alipayClient.pageExecute(any(AlipayTradePagePayRequest.class))).thenReturn(alipayResponse);

    // 执行
    PaymentDTO result = orderService.payOrder(USER_ID, ORDER_ID);

    // 验证
    assertNotNull(result);
    assertEquals(ORDER_ID, result.getOrderId());
    assertEquals(PAYMENT_FORM, result.getPaymentForm());
    assertEquals(order.getTotalAmount(), result.getTotalAmount());
    assertEquals(PaymentMethod.ALIPAY, result.getPaymentMethod());

    verify(alipayClient).pageExecute(any(AlipayTradePagePayRequest.class));
  }

  @Test
  @DisplayName("支付订单 - 失败，订单状态错误")
  void payOrderFailsWhenOrderStatusError() throws AlipayApiException {
    // 准备
    order.setStatus(OrderStatus.PAID); // 已支付状态
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(orderRepository.findByUserAndId(user, ORDER_ID)).thenReturn(Optional.of(order));

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              orderService.payOrder(USER_ID, ORDER_ID);
            });

    assertEquals(BusinessErrorCode.ORDER_STATUS_ERROR, exception.getErrorCode());
    verify(alipayClient, never()).pageExecute(any(AlipayTradePagePayRequest.class));
  }

  @Test
  @DisplayName("支付订单 - 失败，支付宝接口错误")
  void payOrderFailsWhenAlipayError() throws Exception {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(orderRepository.findByUserAndId(user, ORDER_ID)).thenReturn(Optional.of(order));
    when(alipayConfig.getNotifyUrl()).thenReturn("http://localhost:8080/api/orders/notify");
    when(alipayConfig.getReturnUrl()).thenReturn("http://localhost:8080/api/orders/return");

    // Mock支付宝响应失败
    when(alipayResponse.isSuccess()).thenReturn(false);
    when(alipayClient.pageExecute(any(AlipayTradePagePayRequest.class))).thenReturn(alipayResponse);

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              orderService.payOrder(USER_ID, ORDER_ID);
            });

    assertEquals(BusinessErrorCode.ORDER_PAY_FAILED, exception.getErrorCode());
  }

  @Test
  @DisplayName("处理支付通知 - 成功")
  void handlePaymentNotifySuccess() {
    // 准备
    when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(relationRepository.findByOrder(order)).thenReturn(Collections.singletonList(orderItem));
    when(stockpileRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(stockpile));

    // 执行
    PaymentNotifyDTO result = orderService.handlePaymentNotify(paymentNotifyDTO);

    // 验证
    assertNotNull(result);
    assertEquals(OrderStatus.PAID, order.getStatus());
    assertEquals(ALIPAY_TRADE_NO, order.getTradeNo());
    assertNotNull(order.getPaymentTime());

    verify(orderRepository).save(order);
    verify(stockpileRepository).save(stockpile);

    // 验证库存更新
    assertEquals(0, stockpile.getFrozen()); // 冻结库存应减少
  }

  @Test
  @DisplayName("处理支付通知 - 失败，订单不存在")
  void handlePaymentNotifyFailsWhenOrderNotFound() {
    // 准备
    when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              orderService.handlePaymentNotify(paymentNotifyDTO);
            });

    assertTrue(exception.getMessage().contains("订单不存在"));
    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  @DisplayName("处理支付通知 - 失败，订单状态错误")
  void handlePaymentNotifyFailsWhenOrderStatusError() {
    // 准备
    order.setStatus(OrderStatus.PAID); // 设置为已支付状态
    when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              orderService.handlePaymentNotify(paymentNotifyDTO);
            });

    assertEquals(BusinessErrorCode.ORDER_STATUS_ERROR, exception.getErrorCode());
    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  @DisplayName("处理支付通知 - 失败，支付金额不匹配")
  void handlePaymentNotifyFailsWhenAmountMismatch() {
    // 准备
    paymentNotifyDTO.setTotalAmount(new BigDecimal("100.00")); // 设置不匹配的金额
    when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              orderService.handlePaymentNotify(paymentNotifyDTO);
            });

    assertEquals(BusinessErrorCode.ORDER_PAYMENT_AMOUNT_ERROR, exception.getErrorCode());
    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  @DisplayName("取消订单 - 成功")
  void cancelOrderSuccess() {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(orderRepository.findByUserAndId(user, ORDER_ID)).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(relationRepository.findByOrder(order)).thenReturn(Collections.singletonList(orderItem));
    when(stockpileRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(stockpile));

    // 设置初始库存状态
    stockpile.setAmount(98);
    stockpile.setFrozen(2);

    // 执行
    orderService.cancelOrder(USER_ID, ORDER_ID);

    // 验证
    assertEquals(OrderStatus.CANCELLED, order.getStatus());
    verify(orderRepository).save(order);
    verify(stockpileRepository).save(stockpile);

    // 验证库存更新
    assertEquals(0, stockpile.getFrozen()); // 冻结库存应减少
    assertEquals(100, stockpile.getAmount()); // 可用库存应增加
  }

  @Test
  @DisplayName("取消订单 - 失败，订单状态错误")
  void cancelOrderFailsWhenOrderStatusError() {
    // 准备
    order.setStatus(OrderStatus.PAID); // 设置为已支付状态
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(orderRepository.findByUserAndId(user, ORDER_ID)).thenReturn(Optional.of(order));

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              orderService.cancelOrder(USER_ID, ORDER_ID);
            });

    assertEquals(BusinessErrorCode.ORDER_CANNOT_CANCEL, exception.getErrorCode());
    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  @DisplayName("处理过期订单")
  void handleExpiredOrdersSuccess() {
    // 准备
    LocalDateTime expiredTime = LocalDateTime.now(fixedClock).minusMinutes(30);
    List<Order> expiredOrders = Collections.singletonList(order);

    when(orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, expiredTime))
        .thenReturn(expiredOrders);
    when(relationRepository.findByOrder(order)).thenReturn(Collections.singletonList(orderItem));
    when(stockpileRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(stockpile));

    // 设置初始库存状态
    stockpile.setAmount(98);
    stockpile.setFrozen(2);

    // 覆盖计划任务中使用的当前时间
    ReflectionTestUtils.setField(orderService, "ORDER_EXPIRATION_MINUTES", 30);

    // 执行
    orderService.handleExpiredOrders();

    // 验证
    assertEquals(OrderStatus.TIMEOUT, order.getStatus());
    verify(orderRepository).saveAll(expiredOrders);
    verify(stockpileRepository).save(stockpile);

    // 验证库存更新
    assertEquals(0, stockpile.getFrozen()); // 冻结库存应减少
    assertEquals(100, stockpile.getAmount()); // 可用库存应增加
  }
}
