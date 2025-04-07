package com.tomato.bookstore.service;

import static com.tomato.bookstore.constant.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.dto.CartDTO;
import com.tomato.bookstore.dto.CartListDTO;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.exception.ResourceNotFoundException;
import com.tomato.bookstore.model.Cart;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.model.Stockpile;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.repository.CartRepository;
import com.tomato.bookstore.repository.ProductRepository;
import com.tomato.bookstore.repository.StockpileRepository;
import com.tomato.bookstore.repository.UserRepository;
import com.tomato.bookstore.service.impl.CartServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {
  @Mock private CartRepository cartRepository;
  @Mock private UserRepository userRepository;
  @Mock private ProductRepository productRepository;
  @Mock private StockpileRepository stockpileRepository;

  @InjectMocks private CartServiceImpl cartService;

  private User user;
  private Product product;
  private Stockpile stockpile;
  private Cart cart;
  private CartDTO cartDTO;
  private static final Long USER_ID = 1L;
  private static final Long PRODUCT_ID = 1L;
  private static final Long CART_ITEM_ID = 1L;
  private static final Integer QUANTITY = 2;

  @BeforeEach
  void setUp() {
    // 设置测试数据
    LocalDateTime now = LocalDateTime.now();

    // 创建用户
    user = new User();
    user.setId(USER_ID);
    user.setUsername(TEST_USERNAME);
    user.setEmail(TEST_EMAIL);
    user.setPhone(TEST_PHONE);
    user.setCreatedAt(now);
    user.setUpdatedAt(now);

    // 创建商品
    product = new Product();
    product.setId(PRODUCT_ID);
    product.setTitle(TEST_PRODUCT_TITLE);
    product.setPrice(TEST_PRODUCT_PRICE);
    product.setDescription(TEST_PRODUCT_DESCRIPTION);
    product.setCover(TEST_PRODUCT_COVER);
    product.setDetail(TEST_PRODUCT_DETAIL);
    product.setCreatedAt(now);
    product.setUpdatedAt(now);

    // 创建商品库存
    stockpile = new Stockpile();
    stockpile.setId(1L);
    stockpile.setProduct(product);
    stockpile.setAmount(TEST_STOCK_AMOUNT);
    stockpile.setFrozen(TEST_STOCK_FROZEN);
    product.setStockpile(stockpile);

    // 创建购物车项
    cart = new Cart();
    cart.setId(CART_ITEM_ID);
    cart.setUser(user);
    cart.setProduct(product);
    cart.setQuantity(QUANTITY);
    cart.setCreatedAt(now);
    cart.setUpdatedAt(now);

    // 创建购物车DTO
    cartDTO = new CartDTO();
    cartDTO.setProductId(PRODUCT_ID);
    cartDTO.setQuantity(QUANTITY);
  }

  @Test
  @DisplayName("添加商品到购物车 - 成功添加新商品")
  void addToCartSuccessNewItem() {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
    when(stockpileRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(stockpile));
    when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());
    when(cartRepository.save(any(Cart.class)))
        .thenAnswer(
            i -> {
              Cart savedCart = i.getArgument(0);
              savedCart.setId(CART_ITEM_ID);
              return savedCart;
            });

    // 执行
    CartDTO result = cartService.addToCart(USER_ID, cartDTO);

    // 验证
    assertNotNull(result);
    assertEquals(PRODUCT_ID, result.getProductId());
    assertEquals(QUANTITY, result.getQuantity());
    assertEquals(TEST_PRODUCT_TITLE, result.getTitle());
    verify(cartRepository).save(any(Cart.class));
  }

  @Test
  @DisplayName("添加商品到购物车 - 成功更新已有商品")
  void addToCartSuccessExistingItem() {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
    when(stockpileRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(stockpile));
    when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(cart));
    when(cartRepository.save(any(Cart.class))).thenReturn(cart);

    // 执行
    CartDTO result = cartService.addToCart(USER_ID, cartDTO);

    // 验证
    assertNotNull(result);
    assertEquals(CART_ITEM_ID, result.getCartItemId());
    assertEquals(PRODUCT_ID, result.getProductId());
    assertEquals(QUANTITY + QUANTITY, cart.getQuantity()); // 数量应该增加
    verify(cartRepository).save(cart);
  }

  @Test
  @DisplayName("添加商品到购物车 - 失败，用户不存在")
  void addToCartFailsWhenUserNotFound() {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              cartService.addToCart(USER_ID, cartDTO);
            });

    assertTrue(exception.getMessage().contains("用户不存在"));
    verify(cartRepository, never()).save(any(Cart.class));
  }

  @Test
  @DisplayName("添加商品到购物车 - 失败，商品不存在")
  void addToCartFailsWhenProductNotFound() {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              cartService.addToCart(USER_ID, cartDTO);
            });

    assertTrue(exception.getMessage().contains("商品不存在"));
    verify(cartRepository, never()).save(any(Cart.class));
  }

  @Test
  @DisplayName("添加商品到购物车 - 失败，库存不足")
  void addToCartFailsWhenStockInsufficient() {
    // 准备
    stockpile.setAmount(1); // 设置库存小于请求数量
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
    when(stockpileRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(stockpile));

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              cartService.addToCart(USER_ID, cartDTO);
            });

    assertEquals(BusinessErrorCode.CART_ITEM_EXCEED_STOCK, exception.getErrorCode());
    verify(cartRepository, never()).save(any(Cart.class));
  }

  @Test
  @DisplayName("从购物车删除商品 - 成功")
  void removeFromCartSuccess() {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(cartRepository.findByUserAndId(user, CART_ITEM_ID)).thenReturn(Optional.of(cart));

    // 执行
    cartService.removeFromCart(USER_ID, CART_ITEM_ID);

    // 验证
    verify(cartRepository).delete(cart);
  }

  @Test
  @DisplayName("从购物车删除商品 - 失败，购物车项不存在")
  void removeFromCartFailsWhenCartItemNotFound() {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(cartRepository.findByUserAndId(user, CART_ITEM_ID)).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              cartService.removeFromCart(USER_ID, CART_ITEM_ID);
            });

    assertTrue(exception.getMessage().contains("购物车商品不存在"));
    verify(cartRepository, never()).delete(any(Cart.class));
  }

  @Test
  @DisplayName("更新购物车商品数量 - 成功")
  void updateQuantitySuccess() {
    // 准备
    Integer newQuantity = 5;
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(cartRepository.findByUserAndId(user, CART_ITEM_ID)).thenReturn(Optional.of(cart));
    when(stockpileRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(stockpile));
    when(cartRepository.save(any(Cart.class))).thenReturn(cart);

    // 执行
    cartService.updateQuantity(USER_ID, CART_ITEM_ID, newQuantity);

    // 验证
    assertEquals(newQuantity, cart.getQuantity());
    verify(cartRepository).save(cart);
  }

  @Test
  @DisplayName("更新购物车商品数量 - 失败，数量无效")
  void updateQuantityFailsWhenQuantityInvalid() {
    // 准备
    Integer invalidQuantity = 0;

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              cartService.updateQuantity(USER_ID, CART_ITEM_ID, invalidQuantity);
            });

    assertEquals(BusinessErrorCode.CART_ITEM_INVALID_QUANTITY, exception.getErrorCode());
    verify(cartRepository, never()).save(any(Cart.class));
  }

  @Test
  @DisplayName("更新购物车商品数量 - 失败，库存不足")
  void updateQuantityFailsWhenStockInsufficient() {
    // 准备
    Integer excessiveQuantity = TEST_STOCK_AMOUNT + 10;
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(cartRepository.findByUserAndId(user, CART_ITEM_ID)).thenReturn(Optional.of(cart));
    when(stockpileRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(stockpile));

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              cartService.updateQuantity(USER_ID, CART_ITEM_ID, excessiveQuantity);
            });

    assertEquals(BusinessErrorCode.CART_ITEM_EXCEED_STOCK, exception.getErrorCode());
    verify(cartRepository, never()).save(any(Cart.class));
  }

  @Test
  @DisplayName("获取用户购物车 - 成功")
  void getUserCartSuccess() {
    // 准备
    List<Cart> cartItems = Arrays.asList(cart);
    BigDecimal expectedTotalAmount = product.getPrice().multiply(BigDecimal.valueOf(QUANTITY));

    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(cartRepository.findByUser(user)).thenReturn(cartItems);

    // 执行
    CartListDTO result = cartService.getUserCart(USER_ID);

    // 验证
    assertNotNull(result);
    assertEquals(1, result.getTotal());
    assertEquals(1, result.getItems().size());
    assertEquals(expectedTotalAmount, result.getTotalAmount());
    assertEquals(PRODUCT_ID, result.getItems().get(0).getProductId());
    assertEquals(QUANTITY, result.getItems().get(0).getQuantity());
  }

  @Test
  @DisplayName("获取用户购物车 - 成功，空购物车")
  void getUserCartSuccessEmptyCart() {
    // 准备
    List<Cart> emptyCart = new ArrayList<>();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(cartRepository.findByUser(user)).thenReturn(emptyCart);

    // 执行
    CartListDTO result = cartService.getUserCart(USER_ID);

    // 验证
    assertNotNull(result);
    assertEquals(0, result.getTotal());
    assertEquals(0, result.getItems().size());
    assertEquals(BigDecimal.ZERO, result.getTotalAmount());
  }

  @Test
  @DisplayName("清空用户购物车 - 成功")
  void clearCartSuccess() {
    // 准备
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    // 执行
    cartService.clearCart(USER_ID);

    // 验证
    verify(cartRepository).deleteByUser(user);
  }
}
