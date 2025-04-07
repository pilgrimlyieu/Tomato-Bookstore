package com.tomato.bookstore.service.impl;

import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.constant.ExceptionMessages;
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
import com.tomato.bookstore.service.CartService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 购物车服务实现 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
  private final CartRepository cartRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final StockpileRepository stockpileRepository;

  @Override
  @Transactional
  public CartDTO addToCart(Long userId, CartDTO cartDTO) {
    log.info(
        "添加商品到购物车：userId={}, productId={}, quantity={}",
        userId,
        cartDTO.getProductId(),
        cartDTO.getQuantity());

    User user = findUser(userId);
    Product product = findProduct(cartDTO.getProductId());

    // 检查商品库存是否充足
    Stockpile stockpile = findStockpile(product.getId());
    if (stockpile.getAmount() < cartDTO.getQuantity()) {
      log.warn(
          "商品库存不足：productId={}, 库存={}, 请求数量={}",
          product.getId(),
          stockpile.getAmount(),
          cartDTO.getQuantity());
      throw new BusinessException(
          BusinessErrorCode.CART_ITEM_EXCEED_STOCK,
          String.format(
              ExceptionMessages.CART_ITEM_EXCEED_STOCK,
              product.getTitle(),
              stockpile.getAmount(),
              cartDTO.getQuantity()));
    }

    // 检查用户是否已将此商品加入购物车
    Cart cart =
        cartRepository
            .findByUserAndProduct(user, product)
            .orElse(
                Cart.builder()
                    .user(user)
                    .product(product)
                    .quantity(0)
                    .createdAt(LocalDateTime.now())
                    .build());

    // 更新数量和时间
    cart.setQuantity(cart.getQuantity() + cartDTO.getQuantity());
    cart.setUpdatedAt(LocalDateTime.now());

    // 再次检查更新后的数量是否超过库存
    if (cart.getQuantity() > stockpile.getAmount()) {
      log.warn(
          "购物车商品总数量超过库存：productId={}, 库存={}, 总数量={}",
          product.getId(),
          stockpile.getAmount(),
          cart.getQuantity());
      throw new BusinessException(
          BusinessErrorCode.CART_ITEM_EXCEED_STOCK,
          String.format(
              ExceptionMessages.CART_ITEM_EXCEED_STOCK,
              product.getTitle(),
              stockpile.getAmount(),
              cart.getQuantity()));
    }

    cart = cartRepository.save(cart);
    log.info("商品已添加到购物车：cartItemId={}", cart.getId());

    return convertToCartDTO(cart);
  }

  @Override
  @Transactional
  public void removeFromCart(Long userId, Long cartItemId) {
    log.info("从购物车删除商品：userId={}, cartItemId={}", userId, cartItemId);

    User user = findUser(userId);
    Cart cart =
        cartRepository
            .findByUserAndId(user, cartItemId)
            .orElseThrow(
                () -> {
                  log.warn("购物车商品不存在：cartItemId={}", cartItemId);
                  return new ResourceNotFoundException(
                      String.format(ExceptionMessages.CART_ITEM_NOT_FOUND, cartItemId));
                });

    cartRepository.delete(cart);
    log.info("商品已从购物车删除：cartItemId={}", cartItemId);
  }

  @Override
  @Transactional
  public void updateQuantity(Long userId, Long cartItemId, Integer quantity) {
    log.info("更新购物车商品数量：userId={}, cartItemId={}, quantity={}", userId, cartItemId, quantity);

    if (quantity <= 0) {
      throw new BusinessException(
          BusinessErrorCode.CART_ITEM_INVALID_QUANTITY,
          ExceptionMessages.CART_ITEM_INVALID_QUANTITY);
    }

    User user = findUser(userId);
    Cart cart =
        cartRepository
            .findByUserAndId(user, cartItemId)
            .orElseThrow(
                () -> {
                  log.warn("购物车商品不存在：cartItemId={}", cartItemId);
                  return new ResourceNotFoundException(
                      String.format(ExceptionMessages.CART_ITEM_NOT_FOUND, cartItemId));
                });

    // 检查库存
    Stockpile stockpile = findStockpile(cart.getProduct().getId());
    if (quantity > stockpile.getAmount()) {
      log.warn(
          "请求数量超过库存：productId={}, 库存={}, 请求数量={}",
          cart.getProduct().getId(),
          stockpile.getAmount(),
          quantity);
      throw new BusinessException(
          BusinessErrorCode.CART_ITEM_EXCEED_STOCK,
          String.format(
              ExceptionMessages.CART_ITEM_EXCEED_STOCK,
              cart.getProduct().getTitle(),
              stockpile.getAmount(),
              quantity));
    }

    cart.setQuantity(quantity);
    cart.setUpdatedAt(LocalDateTime.now());
    cartRepository.save(cart);

    log.info("购物车商品数量已更新：cartItemId={}, quantity={}", cartItemId, quantity);
  }

  @Override
  @Transactional(readOnly = true)
  public CartListDTO getUserCart(Long userId) {
    log.info("获取用户购物车：userId={}", userId);

    User user = findUser(userId);
    List<Cart> carts = cartRepository.findByUser(user);

    List<CartDTO> cartItems =
        carts.stream().map(this::convertToCartDTO).collect(Collectors.toList());

    // 计算总价
    BigDecimal totalAmount =
        cartItems.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return CartListDTO.builder()
        .items(cartItems)
        .total(cartItems.size())
        .totalAmount(totalAmount)
        .build();
  }

  @Override
  @Transactional
  public void clearCart(Long userId) {
    log.info("清空用户购物车：userId={}", userId);
    User user = findUser(userId);
    cartRepository.deleteByUser(user);
    log.info("用户购物车已清空：userId={}", userId);
  }

  /**
   * 找到用户
   *
   * @param userId 用户 ID
   * @return User 用户对象
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
   * 找到商品
   *
   * @param productId 商品 ID
   * @return Product 商品对象
   */
  private Product findProduct(Long productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(
            () -> {
              log.warn("商品不存在：productId={}", productId);
              return new ResourceNotFoundException(
                  String.format(ExceptionMessages.PRODUCT_NOT_FOUND, productId));
            });
  }

  /**
   * 找到商品库存
   *
   * @param productId 商品 ID
   * @return Stockpile 商品库存对象
   */
  private Stockpile findStockpile(Long productId) {
    return stockpileRepository
        .findByProductId(productId)
        .orElseThrow(
            () -> {
              log.warn("商品库存记录不存在：productId={}", productId);
              return new BusinessException(
                  BusinessErrorCode.PRODUCT_STOCK_NOT_FOUND,
                  String.format(ExceptionMessages.PRODUCT_STOCK_NOT_FOUND, productId));
            });
  }

  /**
   * 转换为 CartDTO
   *
   * @param cart 购物车对象
   * @return CartDTO 购物车数据传输对象
   */
  private CartDTO convertToCartDTO(Cart cart) {
    Product product = cart.getProduct();
    return CartDTO.builder()
        .cartItemId(cart.getId())
        .productId(product.getId())
        .title(product.getTitle())
        .price(product.getPrice())
        .description(product.getDescription())
        .cover(product.getCover())
        .detail(product.getDetail())
        .quantity(cart.getQuantity())
        .build();
  }
}
