package com.tomato.bookstore.service;

import static com.tomato.bookstore.constant.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.dto.ProductDTO;
import com.tomato.bookstore.dto.SpecificationDTO;
import com.tomato.bookstore.dto.StockpileDTO;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.exception.ResourceNotFoundException;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.model.Specification;
import com.tomato.bookstore.model.Stockpile;
import com.tomato.bookstore.repository.ProductRepository;
import com.tomato.bookstore.repository.SpecificationRepository;
import com.tomato.bookstore.repository.StockpileRepository;
import com.tomato.bookstore.service.impl.ProductServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
  @Mock private ProductRepository productRepository;
  @Mock private SpecificationRepository specificationRepository;
  @Mock private StockpileRepository stockpileRepository;

  @InjectMocks private ProductServiceImpl productService;

  private Product product;
  private ProductDTO productDTO;
  private Stockpile stockpile;
  private StockpileDTO stockpileDTO;
  private Specification specification;
  private SpecificationDTO specificationDTO;
  private List<Specification> specifications;

  @BeforeEach
  void setUp() {
    // 设置测试数据
    LocalDateTime now = LocalDateTime.now();

    // 创建产品实体
    product = new Product();
    product.setId(1L);
    product.setTitle(TEST_PRODUCT_TITLE);
    product.setPrice(TEST_PRODUCT_PRICE);
    product.setRate(TEST_PRODUCT_RATE);
    product.setDescription(TEST_PRODUCT_DESCRIPTION);
    product.setCover(TEST_PRODUCT_COVER);
    product.setDetail(TEST_PRODUCT_DETAIL);
    product.setCreatedAt(now);
    product.setUpdatedAt(now);

    // 创建规格
    specification = new Specification();
    specification.setId(1L);
    specification.setItem(TEST_SPEC_ITEM);
    specification.setValue(TEST_SPEC_VALUE);
    specification.setProduct(product);
    specifications = new ArrayList<>();
    specifications.add(specification);
    product.setSpecifications(specifications);

    // 创建产品 DTO
    specificationDTO = new SpecificationDTO();
    specificationDTO.setId(1L);
    specificationDTO.setItem(TEST_SPEC_ITEM);
    specificationDTO.setValue(TEST_SPEC_VALUE);
    specificationDTO.setProductId(1L);

    productDTO = new ProductDTO();
    productDTO.setId(1L);
    productDTO.setTitle(TEST_PRODUCT_TITLE);
    productDTO.setPrice(TEST_PRODUCT_PRICE);
    productDTO.setRate(TEST_PRODUCT_RATE);
    productDTO.setDescription(TEST_PRODUCT_DESCRIPTION);
    productDTO.setCover(TEST_PRODUCT_COVER);
    productDTO.setDetail(TEST_PRODUCT_DETAIL);
    productDTO.setSpecifications(Collections.singletonList(specificationDTO));

    // 创建库存
    stockpile = new Stockpile();
    stockpile.setId(1L);
    stockpile.setProduct(product);
    stockpile.setAmount(TEST_STOCK_AMOUNT);
    stockpile.setFrozen(TEST_STOCK_FROZEN);
    product.setStockpile(stockpile);

    // 创建库存 DTO
    stockpileDTO = new StockpileDTO();
    stockpileDTO.setId(1L);
    stockpileDTO.setProductId(1L);
    stockpileDTO.setAmount(TEST_STOCK_AMOUNT);
    stockpileDTO.setFrozen(TEST_STOCK_FROZEN);
  }

  @Test
  @DisplayName("获取所有商品成功")
  void getAllProductsSuccess() {
    // 准备
    List<Product> products = Arrays.asList(product);
    when(productRepository.findAllByOrderByCreatedAtDesc()).thenReturn(products);

    // 执行
    List<ProductDTO> result = productService.getAllProducts();

    // 验证
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(product.getId(), result.get(0).getId());
    assertEquals(product.getTitle(), result.get(0).getTitle());
    verify(productRepository).findAllByOrderByCreatedAtDesc();
  }

  @Test
  @DisplayName("根据ID获取商品成功")
  void getProductByIdSuccess() {
    // 准备
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    // 执行
    ProductDTO result = productService.getProductById(1L);

    // 验证
    assertNotNull(result);
    assertEquals(product.getId(), result.getId());
    assertEquals(product.getTitle(), result.getTitle());
    verify(productRepository).findById(1L);
  }

  @Test
  @DisplayName("根据ID获取商品失败 - 商品不存在")
  void getProductByIdFailsWhenProductNotFound() {
    // 准备
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              productService.getProductById(1L);
            });

    assertTrue(exception.getMessage().contains(PRODUCT_NOT_FOUND_MESSAGE));
    verify(productRepository).findById(1L);
  }

  @Test
  @DisplayName("创建商品成功")
  void createProductSuccess() {
    // 准备
    when(productRepository.existsByTitle(anyString())).thenReturn(false);
    when(productRepository.save(any(Product.class))).thenReturn(product);
    when(specificationRepository.saveAll(any())).thenReturn(specifications);
    when(stockpileRepository.save(any(Stockpile.class))).thenReturn(stockpile);

    // 执行
    ProductDTO result = productService.createProduct(productDTO);

    // 验证
    assertNotNull(result);
    assertEquals(product.getId(), result.getId());
    assertEquals(product.getTitle(), result.getTitle());
    verify(productRepository).existsByTitle(productDTO.getTitle());
    verify(productRepository).save(any(Product.class));
    verify(stockpileRepository).save(any(Stockpile.class));
  }

  @Test
  @DisplayName("创建商品失败 - 商品名称已存在")
  void createProductFailsWhenTitleExists() {
    // 准备
    when(productRepository.existsByTitle(anyString())).thenReturn(true);

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              productService.createProduct(productDTO);
            });

    assertEquals(BusinessErrorCode.PRODUCT_TITLE_ALREADY_EXISTS, exception.getErrorCode());
    verify(productRepository).existsByTitle(productDTO.getTitle());
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  @DisplayName("更新商品成功")
  void updateProductSuccess() {
    // 准备
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
    when(productRepository.existsByTitleAndIdNot(anyString(), anyLong())).thenReturn(false);
    when(productRepository.save(any(Product.class))).thenReturn(product);

    // 执行
    productService.updateProduct(productDTO);

    // 验证
    verify(productRepository).findById(productDTO.getId());
    verify(productRepository).existsByTitleAndIdNot(productDTO.getTitle(), productDTO.getId());
    verify(productRepository).save(any(Product.class));
  }

  @Test
  @DisplayName("更新商品失败 - 商品不存在")
  void updateProductFailsWhenProductNotFound() {
    // 准备
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              productService.updateProduct(productDTO);
            });

    assertTrue(exception.getMessage().contains(PRODUCT_NOT_FOUND_MESSAGE));
    verify(productRepository).findById(productDTO.getId());
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  @DisplayName("更新商品失败 - 商品名称已被其他商品使用")
  void updateProductFailsWhenTitleUsedByOther() {
    // 准备
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
    when(productRepository.existsByTitleAndIdNot(anyString(), anyLong())).thenReturn(true);

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              productService.updateProduct(productDTO);
            });

    assertEquals(BusinessErrorCode.PRODUCT_TITLE_ALREADY_EXISTS, exception.getErrorCode());
    verify(productRepository).findById(productDTO.getId());
    verify(productRepository).existsByTitleAndIdNot(productDTO.getTitle(), productDTO.getId());
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  @DisplayName("删除商品成功")
  void deleteProductSuccess() {
    // 准备
    when(productRepository.existsById(anyLong())).thenReturn(true);

    // 执行
    productService.deleteProduct(1L);

    // 验证
    verify(productRepository).existsById(1L);
    verify(specificationRepository).deleteByProductId(1L);
    verify(stockpileRepository).deleteByProductId(1L);
    verify(productRepository).deleteById(1L);
  }

  @Test
  @DisplayName("删除商品失败 - 商品不存在")
  void deleteProductFailsWhenProductNotFound() {
    // 准备
    when(productRepository.existsById(anyLong())).thenReturn(false);

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              productService.deleteProduct(1L);
            });

    assertTrue(exception.getMessage().contains(PRODUCT_NOT_FOUND_MESSAGE));
    verify(productRepository).existsById(1L);
    verify(productRepository, never()).deleteById(anyLong());
  }

  @Test
  @DisplayName("获取商品库存成功")
  void getStockpileSuccess() {
    // 准备
    when(productRepository.existsById(anyLong())).thenReturn(true);
    when(stockpileRepository.findByProductId(anyLong())).thenReturn(Optional.of(stockpile));

    // 执行
    StockpileDTO result = productService.getStockpile(1L);

    // 验证
    assertNotNull(result);
    assertEquals(stockpile.getAmount(), result.getAmount());
    assertEquals(stockpile.getFrozen(), result.getFrozen());
    verify(productRepository).existsById(1L);
    verify(stockpileRepository).findByProductId(1L);
  }

  @Test
  @DisplayName("获取商品库存失败 - 商品不存在")
  void getStockpileFailsWhenProductNotFound() {
    // 准备
    when(productRepository.existsById(anyLong())).thenReturn(false);

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              productService.getStockpile(1L);
            });

    assertTrue(exception.getMessage().contains(PRODUCT_NOT_FOUND_MESSAGE));
    verify(productRepository).existsById(1L);
    verify(stockpileRepository, never()).findByProductId(anyLong());
  }

  @Test
  @DisplayName("获取商品库存失败 - 库存记录不存在")
  void getStockpileFailsWhenStockpileNotFound() {
    // 准备
    when(productRepository.existsById(anyLong())).thenReturn(true);
    when(stockpileRepository.findByProductId(anyLong())).thenReturn(Optional.empty());

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              productService.getStockpile(1L);
            });

    assertEquals(BusinessErrorCode.PRODUCT_STOCK_NOT_FOUND, exception.getErrorCode());
    verify(productRepository).existsById(1L);
    verify(stockpileRepository).findByProductId(1L);
  }

  @Test
  @DisplayName("更新商品库存成功 - 已存在库存记录")
  void updateStockpileSuccessWithExistingStockpile() {
    // 准备
    when(productRepository.existsById(anyLong())).thenReturn(true);
    when(stockpileRepository.findByProductId(anyLong())).thenReturn(Optional.of(stockpile));
    when(stockpileRepository.save(any(Stockpile.class))).thenReturn(stockpile);

    // 执行
    productService.updateStockpile(1L, stockpileDTO);

    // 验证
    verify(productRepository).existsById(1L);
    verify(stockpileRepository).findByProductId(1L);
    verify(stockpileRepository).save(any(Stockpile.class));
  }

  @Test
  @DisplayName("更新商品库存成功 - 新建库存记录")
  void updateStockpileSuccessWithNewStockpile() {
    // 准备
    when(productRepository.existsById(anyLong())).thenReturn(true);
    when(stockpileRepository.findByProductId(anyLong())).thenReturn(Optional.empty());
    when(productRepository.getReferenceById(anyLong())).thenReturn(product);
    when(stockpileRepository.save(any(Stockpile.class))).thenReturn(stockpile);

    // 执行
    productService.updateStockpile(1L, stockpileDTO);

    // 验证
    verify(productRepository).existsById(1L);
    verify(stockpileRepository).findByProductId(1L);
    verify(productRepository).getReferenceById(1L);
    verify(stockpileRepository).save(any(Stockpile.class));
  }

  @Test
  @DisplayName("更新商品库存失败 - 商品不存在")
  void updateStockpileFailsWhenProductNotFound() {
    // 准备
    when(productRepository.existsById(anyLong())).thenReturn(false);

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              productService.updateStockpile(1L, stockpileDTO);
            });

    assertTrue(exception.getMessage().contains(PRODUCT_NOT_FOUND_MESSAGE));
    verify(productRepository).existsById(1L);
    verify(stockpileRepository, never()).save(any(Stockpile.class));
  }
}
