package com.tomato.bookstore.util;

import static com.tomato.bookstore.constant.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.constant.OrderStatus;
import com.tomato.bookstore.constant.PaymentMethod;
import com.tomato.bookstore.dto.LoginDTO;
import com.tomato.bookstore.model.Advertisement;
import com.tomato.bookstore.model.Cart;
import com.tomato.bookstore.model.CartsOrdersRelation;
import com.tomato.bookstore.model.Order;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.model.Stockpile;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.model.User.UserRole;
import com.tomato.bookstore.repository.AdvertisementRepository;
import com.tomato.bookstore.repository.CartRepository;
import com.tomato.bookstore.repository.CartsOrdersRelationRepository;
import com.tomato.bookstore.repository.OrderRepository;
import com.tomato.bookstore.repository.ProductRepository;
import com.tomato.bookstore.repository.StockpileRepository;
import com.tomato.bookstore.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 测试数据工厂
 *
 * <p>用于创建测试所需的各种数据
 */
@Component
public class TestDataFactory {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private ProductRepository productRepository;

  @Autowired private StockpileRepository stockpileRepository;

  @Autowired private CartRepository cartRepository;

  @Autowired private OrderRepository orderRepository;

  @Autowired private CartsOrdersRelationRepository relationRepository;

  @Autowired private AdvertisementRepository advertisementRepository;

  @Autowired private Clock clock;

  /**
   * 创建测试用户
   *
   * @param username 用户名
   * @return 创建的用户
   */
  public User createTestUser(String username) {
    LocalDateTime now = LocalDateTime.now(clock);

    // 检查用户是否已存在
    Optional<User> existingUser = userRepository.findByUsername(username);
    if (existingUser.isPresent()) {
      return existingUser.get();
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(
        "$2a$10$C/TgJT0E./j3ltVhuzDWheDO0bvxbz6f2MDfeLJL6evs0r5HMNufK"); // 加密的 password123
    user.setEmail(username + "@example.com");
    user.setPhone("13800138000");
    user.setRole(UserRole.CUSTOMER);
    user.setCreatedAt(now);
    user.setUpdatedAt(now);

    return userRepository.save(user);
  }

  /**
   * 创建测试商品
   *
   * @param title 商品标题
   * @param price 商品价格
   * @return 创建的商品
   */
  public Product createTestProduct(String title, BigDecimal price) {
    LocalDateTime now = LocalDateTime.now(clock);

    Product product = new Product();
    product.setTitle(title);
    product.setPrice(price);
    product.setRate(TEST_PRODUCT_RATE);
    product.setDescription(TEST_PRODUCT_DESCRIPTION);
    product.setCover(TEST_PRODUCT_COVER);
    product.setDetail(TEST_PRODUCT_DETAIL);
    product.setCreatedAt(now);
    product.setUpdatedAt(now);

    product = productRepository.save(product);

    // 创建并保存库存
    Stockpile stockpile = new Stockpile();
    stockpile.setProduct(product);
    stockpile.setAmount(TEST_STOCK_AMOUNT);
    stockpile.setFrozen(TEST_STOCK_FROZEN);
    stockpileRepository.save(stockpile);

    return product;
  }

  /**
   * 创建测试购物车项
   *
   * @param user 用户
   * @param product 商品
   * @param quantity 数量
   * @return 创建的购物车项
   */
  public Cart createTestCartItem(User user, Product product, int quantity) {
    // 检查是否已存在
    Optional<Cart> existingCart = cartRepository.findByUserAndProduct(user, product);
    if (existingCart.isPresent()) {
      Cart cart = existingCart.get();
      cart.setQuantity(quantity);
      return cartRepository.save(cart);
    }

    Cart cart = new Cart();
    cart.setUser(user);
    cart.setProduct(product);
    cart.setQuantity(quantity);
    cart.setCreatedAt(LocalDateTime.now(clock));

    return cartRepository.save(cart);
  }

  /**
   * 创建测试订单
   *
   * @param user 用户
   * @param product 商品
   * @param cartItem 购物车项
   * @param quantity 数量
   * @param shippingAddress 收货地址
   * @return 创建的订单
   */
  public Order createTestOrder(
      User user, Product product, Cart cartItem, int quantity, String shippingAddress) {
    Order order = new Order();
    order.setUser(user);
    order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
    order.setPaymentMethod(PaymentMethod.ALIPAY);
    order.setStatus(OrderStatus.PENDING);
    order.setShippingAddress(shippingAddress);
    order.setCreatedAt(LocalDateTime.now(clock));
    order.setOrderItems(new ArrayList<>());
    order = orderRepository.save(order);

    // 创建订单项关联
    CartsOrdersRelation orderItem = new CartsOrdersRelation();
    orderItem.setCart(cartItem);
    orderItem.setOrder(order);
    orderItem.setQuantity(quantity);
    orderItem = relationRepository.save(orderItem);

    // 更新订单与订单项关系
    order.getOrderItems().add(orderItem);
    order = orderRepository.save(order);

    // 更新库存
    Stockpile stockpile = stockpileRepository.findByProductId(product.getId()).orElseThrow();
    stockpile.setAmount(stockpile.getAmount() - quantity);
    stockpile.setFrozen(quantity);
    stockpileRepository.save(stockpile);

    return order;
  }

  /**
   * 创建测试广告
   *
   * @param title 广告标题
   * @param content 广告内容
   * @param product 关联商品
   * @return 创建的广告
   */
  public Advertisement createTestAdvertisement(String title, String content, Product product) {
    Advertisement advertisement = new Advertisement();
    advertisement.setTitle(title);
    advertisement.setContent(content);
    advertisement.setImageUrl("https://example.com/test-ad-image.jpg");
    advertisement.setProduct(product);

    return advertisementRepository.save(advertisement);
  }

  /**
   * 获取用户 JWT 令牌
   *
   * @param username 用户名
   * @param password 密码
   * @return JWT 令牌
   */
  public String getUserToken(String username, String password) {
    try {
      LoginDTO loginDTO = new LoginDTO();
      loginDTO.setUsername(username);
      loginDTO.setPassword(password);

      MvcResult result =
          mockMvc
              .perform(
                  post(ApiConstants.USER_LOGIN_PATH)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(loginDTO)))
              .andExpect(status().isOk())
              .andReturn();

      String responseJson = result.getResponse().getContentAsString();
      JsonNode root = objectMapper.readTree(responseJson);
      return root.path("data").asText();
    } catch (Exception e) {
      throw new RuntimeException("获取用户 Token 失败", e);
    }
  }
}
