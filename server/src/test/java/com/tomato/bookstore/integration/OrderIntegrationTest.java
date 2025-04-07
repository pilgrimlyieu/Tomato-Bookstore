package com.tomato.bookstore.integration;

import static com.tomato.bookstore.constant.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alipay.api.AlipayClient;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.constant.OrderStatus;
import com.tomato.bookstore.constant.PaymentMethod;
import com.tomato.bookstore.dto.CheckoutDTO;
import com.tomato.bookstore.model.Cart;
import com.tomato.bookstore.model.Order;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.model.Stockpile;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.repository.CartRepository;
import com.tomato.bookstore.repository.CartsOrdersRelationRepository;
import com.tomato.bookstore.repository.OrderRepository;
import com.tomato.bookstore.repository.ProductRepository;
import com.tomato.bookstore.repository.StockpileRepository;
import com.tomato.bookstore.repository.UserRepository;
import com.tomato.bookstore.util.TestDataFactory;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单集成测试
 *
 * <p>测试订单相关API接口的功能
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OrderIntegrationTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private StockpileRepository stockpileRepository;
  @Autowired private CartRepository cartRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private CartsOrdersRelationRepository relationRepository;
  @Autowired private TestDataFactory testDataFactory;

  @MockitoBean private AlipayClient alipayClient;

  @MockitoBean private Clock clock;

  private User testUser;
  private Product testProduct;
  private Cart testCartItem;
  private Order testOrder;
  private String userToken;

  /** 测试准备工作 */
  @BeforeEach
  void setUp() throws Exception {
    // 清理数据
    relationRepository.deleteAll();
    orderRepository.deleteAll();
    cartRepository.deleteAll();

    AlipayTradePagePayResponse mockResponse = Mockito.mock(AlipayTradePagePayResponse.class);
    Mockito.when(mockResponse.isSuccess()).thenReturn(true);
    Mockito.when(mockResponse.getBody())
        .thenReturn(
            "<form id='alipaysubmit' name='alipaysubmit' action='https://openapi-sandbox.dl.alipaydev.com/gateway.do'></form>");
    Mockito.when(alipayClient.pageExecute(Mockito.any())).thenReturn(mockResponse);

    // 模拟系统时钟
    Mockito.when(clock.instant()).thenReturn(java.time.Instant.now());
    Mockito.when(clock.getZone()).thenReturn(java.time.ZoneId.systemDefault());

    // 创建测试数据
    setupTestData();
  }

  /** 设置测试数据 */
  private void setupTestData() {
    // 创建测试用户
    testUser = testDataFactory.createTestUser(TEST_ORDER_USERNAME);

    // 创建测试商品
    testProduct = testDataFactory.createTestProduct("测试订单商品", new BigDecimal("88.00"));

    // 获取用户令牌
    userToken = testDataFactory.getUserToken(TEST_ORDER_USERNAME, TEST_PASSWORD);

    // 创建购物车项
    testCartItem = testDataFactory.createTestCartItem(testUser, testProduct, TEST_ORDER_QUANTITY);

    // 创建测试订单
    testOrder =
        testDataFactory.createTestOrder(
            testUser, testProduct, testCartItem, TEST_ORDER_QUANTITY, TEST_SHIPPING_ADDRESS);
  }

  @Test
  @DisplayName("获取订单详情 - 成功")
  void getOrderDetailSuccess() throws Exception {
    // 获取订单详情
    mockMvc
        .perform(
            get(ApiConstants.ORDER_DETAIL_PATH.replace("{orderId}", testOrder.getId().toString()))
                .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.orderId").value(testOrder.getId()))
        // 使用字符串比较避免BigDecimal格式问题
        .andExpect(
            jsonPath("$.data.totalAmount")
                .value(
                    testProduct
                        .getPrice()
                        .multiply(BigDecimal.valueOf(TEST_ORDER_QUANTITY))
                        .doubleValue()))
        .andExpect(jsonPath("$.data.status").value(OrderStatus.PENDING.name()))
        .andExpect(jsonPath("$.data.shippingAddress").value(TEST_SHIPPING_ADDRESS))
        .andExpect(jsonPath("$.data.orderItems[0].productId").value(testProduct.getId()));
  }

  @Test
  @DisplayName("获取订单详情 - 失败，订单不存在")
  void getOrderDetailFailsWhenOrderNotFound() throws Exception {
    Long nonExistingId = 9999L;
    mockMvc
        .perform(
            get(ApiConstants.ORDER_DETAIL_PATH.replace("{orderId}", nonExistingId.toString()))
                .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.msg").value("订单不存在，ID：" + nonExistingId));
  }

  @Test
  @DisplayName("支付订单 - 成功")
  void payOrderSuccess() throws Exception {
    // 支付订单
    MvcResult result =
        mockMvc
            .perform(
                post(ApiConstants.ORDER_PAY_PATH.replace("{orderId}", testOrder.getId().toString()))
                    .header("Authorization", "Bearer " + userToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.orderId").value(testOrder.getId()))
            .andExpect(
                jsonPath("$.data.totalAmount")
                    .value(
                        testProduct
                            .getPrice()
                            .multiply(BigDecimal.valueOf(TEST_ORDER_QUANTITY))
                            .doubleValue()))
            .andExpect(jsonPath("$.data.paymentMethod").value(PaymentMethod.ALIPAY.name()))
            .andReturn();

    // 验证返回的支付表单
    String responseJson = result.getResponse().getContentAsString();
    JsonNode responseData = objectMapper.readTree(responseJson).get("data");
    assertNotNull(responseData.get("paymentForm"));
  }

  @Test
  @DisplayName("支付订单 - 失败，订单状态错误")
  void payOrderFailsWhenOrderStatusError() throws Exception {
    // 修改订单状态为已支付
    testOrder.setStatus(OrderStatus.PAID);
    orderRepository.save(testOrder);

    mockMvc
        .perform(
            post(ApiConstants.ORDER_PAY_PATH.replace("{orderId}", testOrder.getId().toString()))
                .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(BusinessErrorCode.ORDER_STATUS_ERROR.getCode()))
        .andExpect(jsonPath("$.msg").value("订单状态错误，当前状态：PAID，期望状态：PENDING"));
  }

  @Test
  @DisplayName("取消订单 - 成功")
  void cancelOrderSuccess() throws Exception {
    // 取消订单
    mockMvc
        .perform(
            delete(
                    ApiConstants.ORDER_DETAIL_PATH.replace(
                        "{orderId}", testOrder.getId().toString()))
                .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()));

    // 验证数据库中订单状态已更新
    Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
    assertEquals(OrderStatus.CANCELLED, updatedOrder.getStatus());

    // 验证库存已解锁
    Stockpile updatedStockpile =
        stockpileRepository.findByProductId(testProduct.getId()).orElseThrow();
    assertEquals(TEST_STOCK_AMOUNT, updatedStockpile.getAmount());
    assertEquals(0, updatedStockpile.getFrozen());
  }

  @Test
  @DisplayName("取消订单 - 失败，订单状态错误")
  void cancelOrderFailsWhenOrderStatusError() throws Exception {
    // 修改订单状态为已支付
    testOrder.setStatus(OrderStatus.PAID);
    orderRepository.save(testOrder);

    mockMvc
        .perform(
            delete(
                    ApiConstants.ORDER_DETAIL_PATH.replace(
                        "{orderId}", testOrder.getId().toString()))
                .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(BusinessErrorCode.ORDER_CANNOT_CANCEL.getCode()))
        .andExpect(jsonPath("$.msg").value("非待支付状态的订单无法取消"));
  }

  @Test
  @DisplayName("支付通知处理 - 成功")
  void handlePaymentNotifySuccess() throws Exception {
    // 构建支付通知参数
    String notifyParams =
        "out_trade_no="
            + testOrder.getId()
            + "&trade_no="
            + TEST_ALIPAY_TRADE_NO
            + "&trade_status=TRADE_SUCCESS"
            + "&total_amount="
            + testOrder.getTotalAmount()
            + "&gmt_payment=2023-11-28 12:34:56";

    // 发送支付通知
    mockMvc
        .perform(
            post(ApiConstants.ORDER_NOTIFY_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(notifyParams))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.msg").value("支付成功"));

    // 验证数据库中订单状态已更新
    Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
    assertEquals(OrderStatus.PAID, updatedOrder.getStatus());
    assertEquals(TEST_ALIPAY_TRADE_NO, updatedOrder.getTradeNo());
    assertNotNull(updatedOrder.getPaymentTime());

    // 验证库存锁定状态已更新（冻结库存释放）
    Stockpile updatedStockpile =
        stockpileRepository.findByProductId(testProduct.getId()).orElseThrow();
    assertEquals(TEST_STOCK_AMOUNT - TEST_ORDER_QUANTITY, updatedStockpile.getAmount());
    assertEquals(0, updatedStockpile.getFrozen());
  }

  @Test
  @DisplayName("支付通知处理 - 失败，金额不匹配")
  void handlePaymentNotifyFailsWhenAmountMismatch() throws Exception {
    // 构建支付通知参数，设置错误的金额
    String notifyParams =
        "out_trade_no="
            + testOrder.getId()
            + "&trade_no="
            + TEST_ALIPAY_TRADE_NO
            + "&trade_status=TRADE_SUCCESS"
            + "&total_amount=999.99"
            + // 错误的金额
            "&gmt_payment=2023-11-28 12:34:56";

    // 发送支付通知
    mockMvc
        .perform(
            post(ApiConstants.ORDER_NOTIFY_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(notifyParams))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(BusinessErrorCode.ORDER_PAYMENT_AMOUNT_ERROR.getCode()))
        .andExpect(jsonPath("$.msg").value("支付金额与订单金额不匹配"));

    // 验证订单状态未变化
    Order unchangedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
    assertEquals(OrderStatus.PENDING, unchangedOrder.getStatus());
  }

  @Test
  @DisplayName("完整订单流程 - 创建、支付、取消")
  void completeOrderFlowTest() throws Exception {
    // 准备结算数据
    CheckoutDTO checkoutDTO = new CheckoutDTO();
    checkoutDTO.setCartItemIds(Collections.singletonList(testCartItem.getId()));
    checkoutDTO.setUserId(testUser.getId());
    checkoutDTO.setShippingAddress("完整流程测试地址");
    checkoutDTO.setPaymentMethod(PaymentMethod.ALIPAY);

    // 1. 创建订单（结算购物车）
    MvcResult createResult =
        mockMvc
            .perform(
                post(ApiConstants.CART_CHECKOUT_PATH)
                    .header("Authorization", "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(checkoutDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.status").value(OrderStatus.PENDING.name()))
            .andReturn();

    // 提取新订单ID
    String createJson = createResult.getResponse().getContentAsString();
    Long newOrderId = objectMapper.readTree(createJson).get("data").get("orderId").asLong();

    // 2. 获取订单详情
    mockMvc
        .perform(
            get(ApiConstants.ORDER_DETAIL_PATH.replace("{orderId}", newOrderId.toString()))
                .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.orderId").value(newOrderId))
        .andExpect(jsonPath("$.data.shippingAddress").value("完整流程测试地址"));

    // 3. 取消订单
    mockMvc
        .perform(
            delete(ApiConstants.ORDER_DETAIL_PATH.replace("{orderId}", newOrderId.toString()))
                .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isOk());

    // 验证订单状态
    Order cancelledOrder = orderRepository.findById(newOrderId).orElseThrow();
    assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
  }
}
