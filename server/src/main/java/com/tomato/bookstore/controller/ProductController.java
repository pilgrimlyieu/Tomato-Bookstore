package com.tomato.bookstore.controller;

import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.constant.RoleConstants;
import com.tomato.bookstore.dto.ApiResponse;
import com.tomato.bookstore.dto.ProductDTO;
import com.tomato.bookstore.dto.StockpileDTO;
import com.tomato.bookstore.security.UserPrincipal;
import com.tomato.bookstore.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 *
 * <p>该类包含商品相关的接口，包括获取商品列表、创建商品、更新商品、删除商品等操作。
 */
@RestController
@RequestMapping(ApiConstants.PRODUCTS)
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProductController {
  private final ProductService productService;

  /**
   * 获取所有商品
   *
   * @return 商品列表
   */
  @GetMapping
  public ApiResponse<List<ProductDTO>> getAllProducts() {
    log.info("获取所有商品列表");
    List<ProductDTO> products = productService.getAllProducts();
    return ApiResponse.success(products);
  }

  /**
   * 获取指定 ID 的商品
   *
   * @param id 商品 ID
   * @return 商品信息
   */
  @GetMapping(ApiConstants.PRODUCT_DETAIL)
  public ApiResponse<ProductDTO> getProductById(@PathVariable Long id) {
    log.info("获取商品详情 id={}", id);
    ProductDTO product = productService.getProductById(id);
    return ApiResponse.success(product);
  }

  /**
   * 创建商品（仅管理员）
   *
   * @param productDTO 商品信息
   * @param userPrincipal 当前用户
   * @return 创建成功的商品
   */
  @PostMapping
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<ProductDTO> createProduct(
      @RequestBody @Valid ProductDTO productDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("管理员「{}」创建商品：{}", userPrincipal.getUsername(), productDTO.getTitle());
    ProductDTO createdProduct = productService.createProduct(productDTO);
    return ApiResponse.success(createdProduct);
  }

  /**
   * 更新商品信息（仅管理员）
   *
   * @param productDTO 商品信息
   * @param userPrincipal 当前用户
   * @return 更新结果
   */
  @PutMapping
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<String> updateProduct(
      @RequestBody @Valid ProductDTO productDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("管理员「{}」更新商品 id={}", userPrincipal.getUsername(), productDTO.getId());
    productService.updateProduct(productDTO);
    return ApiResponse.success("更新成功");
  }

  /**
   * 删除商品（仅管理员）
   *
   * @param id 商品 ID
   * @param userPrincipal 当前用户
   * @return 删除结果
   */
  @DeleteMapping(ApiConstants.PRODUCT_DETAIL)
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<String> deleteProduct(
      @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("管理员「{}」删除商品 id={}", userPrincipal.getUsername(), id);
    productService.deleteProduct(id);
    return ApiResponse.success("删除成功");
  }

  /**
   * 获取指定商品的库存
   *
   * @param productId 商品 ID
   * @return 商品库存信息
   */
  @GetMapping(ApiConstants.STOCKPILE_DETAIL)
  public ApiResponse<StockpileDTO> getStockpile(@PathVariable Long productId) {
    log.info("获取商品库存信息 productId={}", productId);
    StockpileDTO stockpile = productService.getStockpile(productId);
    return ApiResponse.success(stockpile);
  }

  /**
   * 更新商品库存（仅管理员）
   *
   * @param productId 商品 ID
   * @param stockpileDTO 库存信息
   * @param userPrincipal 当前用户
   * @return 更新结果
   */
  @PatchMapping(ApiConstants.STOCKPILE_DETAIL)
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<String> updateStockpile(
      @PathVariable Long productId,
      @RequestBody @Valid StockpileDTO stockpileDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info(
        "管理员「{}」调整商品库存 productId={}，可售库存={}，冻结库存={}",
        userPrincipal.getUsername(),
        productId,
        stockpileDTO.getAmount(),
        stockpileDTO.getFrozen());
    productService.updateStockpile(productId, stockpileDTO);
    return ApiResponse.success("调整库存成功");
  }
}
