package com.tomato.bookstore.integration;

import static com.tomato.bookstore.constant.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.constant.OrderStatus;
import com.tomato.bookstore.constant.PaymentMethod;
import com.tomato.bookstore.dto.CartDTO;
import com.tomato.bookstore.dto.CheckoutDTO;
import com.tomato.bookstore.dto.QuantityUpdateDTO;
import com.tomato.bookstore.model.Cart;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.model.Stockpile;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.repository.CartRepository;
import com.tomato.bookstore.repository.ProductRepository;
import com.tomato.bookstore.repository.StockpileRepository;
import com.tomato.bookstore.repository.UserRepository;
import com.tomato.bookstore.util.TestDataFactory;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CartIntegrationTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private StockpileRepository stockpileRepository;
  @Autowired private CartRepository cartRepository;
  @Autowired private TestDataFactory testDataFactory;

  private User testUser;
  private Product testProduct;
  private Stockpile testStockpile;
  private String userToken;

  @MockitoBean private AlipayClient alipayClient;

  @MockitoBean private Clock clock;

  @BeforeEach
  void setUp() throws AlipayApiException {
    // 清理数据
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

  private void setupTestData() {
    // 创建测试用户
    testUser = testDataFactory.createTestUser(TEST_CART_USERNAME);

    // 创建测试商品
    testProduct = testDataFactory.createTestProduct("测试购物车商品", new BigDecimal("99.00"));

    // 获取库存对象
    testStockpile = stockpileRepository.findByProductId(testProduct.getId()).orElseThrow();

    // 获取用户令牌
    userToken = testDataFactory.getUserToken(TEST_CART_USERNAME, TEST_PASSWORD);
  }

  @Test
  @DisplayName("添加商品到购物车 - 成功")
  void addToCartSuccess() throws Exception {
    // 准备请求数据
    CartDTO cartDTO = new CartDTO();
    cartDTO.setProductId(testProduct.getId());
    cartDTO.setQuantity(2);

    // 发送请求并验证
    MvcResult result =
        mockMvc
            .perform(
                post(ApiConstants.CART_BASE_PATH)
                    .header("Authorization", "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cartDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.productId").value(testProduct.getId()))
            .andExpect(jsonPath("$.data.quantity").value(2))
            .andReturn();

    // 验证数据库中已添加
    List<Cart> carts = cartRepository.findByUser(testUser);
    assertFalse(carts.isEmpty());
    assertEquals(1, carts.size());
    assertEquals(testProduct.getId(), carts.get(0).getProduct().getId());
    assertEquals(2, carts.get(0).getQuantity());
  }

  @Test
  @DisplayName("添加商品到购物车 - 更新已有商品数量")
  void addToCartUpdateExistingItemQuantity() throws Exception {
    // 先添加一次
    CartDTO cartDTO = new CartDTO();
    cartDTO.setProductId(testProduct.getId());
    cartDTO.setQuantity(2);

    mockMvc
        .perform(
            post(ApiConstants.CART_BASE_PATH)
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartDTO)))
        .andExpect(status().isOk());

    // 再添加一次
    mockMvc
        .perform(
            post(ApiConstants.CART_BASE_PATH)
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartDTO)))
        .andExpect(status().isOk());

    // 验证数据库中数量更新
    List<Cart> carts = cartRepository.findByUser(testUser);
    assertEquals(1, carts.size());
    assertEquals(4, carts.get(0).getQuantity()); // 2 + 2 = 4
  }

  @Test
  @DisplayName("获取购物车 - 成功")
  void getUserCartSuccess() throws Exception {
    // 先添加商品到购物车
    Cart cart = testDataFactory.createTestCartItem(testUser, testProduct, 3);

    // 获取购物车并验证
    mockMvc
        .perform(get(ApiConstants.CART_BASE_PATH).header("Authorization", "Bearer " + userToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.total").value(1))
        .andExpect(jsonPath("$.data.items[0].productId").value(testProduct.getId()))
        .andExpect(jsonPath("$.data.items[0].quantity").value(3))
        .andExpect(jsonPath("$.data.items[0].title").value(testProduct.getTitle()))
        .andExpect(
            jsonPath("$.data.totalAmount")
                .value(testProduct.getPrice().multiply(new BigDecimal(3)).doubleValue()));
  }

  @Test
  @DisplayName("更新购物车商品数量 - 成功")
  void updateCartItemQuantitySuccess() throws Exception {
    // 先添加商品到购物车
    Cart cart = testDataFactory.createTestCartItem(testUser, testProduct, 1);

    // 更新数量
    QuantityUpdateDTO updateDTO = new QuantityUpdateDTO();
    updateDTO.setQuantity(5);

    mockMvc
        .perform(
            patch(ApiConstants.CART_ITEM_PATH.replace("{cartItemId}", cart.getId().toString()))
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));

    // 验证数据库中数量已更新
    Optional<Cart> updatedCart = cartRepository.findById(cart.getId());
    assertTrue(updatedCart.isPresent());
    assertEquals(5, updatedCart.get().getQuantity());
  }

  @Test
  @DisplayName("从购物车删除商品 - 成功")
  void removeFromCartSuccess() throws Exception {
    // 先添加商品到购物车
    Cart cart = testDataFactory.createTestCartItem(testUser, testProduct, 2);

    // 删除购物车商品
    mockMvc
        .perform(
            delete(ApiConstants.CART_ITEM_PATH.replace("{cartItemId}", cart.getId().toString()))
                .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));

    // 验证数据库中已删除
    Optional<Cart> deletedCart = cartRepository.findById(cart.getId());
    assertFalse(deletedCart.isPresent());
  }

  @Test
  @DisplayName("结算购物车 - 成功创建订单")
  void checkoutCartSuccess() throws Exception {
    // 先添加商品到购物车
    Cart cart = testDataFactory.createTestCartItem(testUser, testProduct, 2);

    // 准备结算数据
    CheckoutDTO checkoutDTO = new CheckoutDTO();
    checkoutDTO.setCartItemIds(Collections.singletonList(cart.getId()));
    checkoutDTO.setUserId(testUser.getId());
    checkoutDTO.setShippingAddress("测试地址，测试街道123号");
    checkoutDTO.setPaymentMethod(PaymentMethod.ALIPAY);

    // 结算购物车
    MvcResult result =
        mockMvc
            .perform(
                post(ApiConstants.CART_CHECKOUT_PATH)
                    .header("Authorization", "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(checkoutDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.status").value(OrderStatus.PENDING.name()))
            .andExpect(
                jsonPath("$.data.totalAmount")
                    .value(testProduct.getPrice().multiply(new BigDecimal(2)).doubleValue()))
            .andExpect(jsonPath("$.data.shippingAddress").value("测试地址，测试街道123号"))
            .andReturn();

    // 验证返回的订单
    String responseJson = result.getResponse().getContentAsString();
    JsonNode responseData = objectMapper.readTree(responseJson).get("data");
    assertNotNull(responseData.get("orderId"));

    // 验证库存已锁定
    Stockpile updatedStockpile =
        stockpileRepository.findByProductId(testProduct.getId()).orElseThrow();
    assertEquals(TEST_STOCK_AMOUNT - 2, updatedStockpile.getAmount()); // 100 - 2
    assertEquals(2, updatedStockpile.getFrozen()); // 0 + 2
  }

  @Test
  @DisplayName("结算购物车 - 失败，购物车为空")
  void checkoutCartFailsWhenCartEmpty() throws Exception {
    // 准备结算数据，但不添加商品
    CheckoutDTO checkoutDTO = new CheckoutDTO();
    checkoutDTO.setCartItemIds(Collections.singletonList(999L)); // 不存在的ID
    checkoutDTO.setUserId(testUser.getId());
    checkoutDTO.setShippingAddress("测试地址");
    checkoutDTO.setPaymentMethod(PaymentMethod.ALIPAY);

    // 结算购物车
    mockMvc
        .perform(
            post(ApiConstants.CART_CHECKOUT_PATH)
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkoutDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(BusinessErrorCode.CART_ITEM_NOT_FOUND.getCode()))
        .andExpect(jsonPath("$.msg").value("未找到选择的购物车商品"));
  }
}
