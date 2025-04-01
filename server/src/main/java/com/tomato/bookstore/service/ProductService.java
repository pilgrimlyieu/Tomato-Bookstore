package com.tomato.bookstore.service;

import com.tomato.bookstore.dto.ProductDTO;
import com.tomato.bookstore.dto.StockpileDTO;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface ProductService {
  /**
   * 获取所有商品
   *
   * @return 商品列表
   */
  List<ProductDTO> getAllProducts();

  /**
   * 获取指定 ID 的商品
   *
   * @param id 商品 ID
   * @return 商品信息
   * @throws ResourceNotFoundException 商品不存在
   */
  ProductDTO getProductById(Long id) throws ResourceNotFoundException;

  /**
   * 创建商品
   *
   * @param productDTO 商品信息
   * @return 创建成功的商品
   * @throws BusinessException 商品名称已存在
   */
  @Transactional
  ProductDTO createProduct(ProductDTO productDTO) throws BusinessException;

  /**
   * 更新商品信息
   *
   * @param productDTO 商品信息
   * @throws ResourceNotFoundException 商品不存在
   * @throws BusinessException 商品名称已存在
   */
  @Transactional
  void updateProduct(ProductDTO productDTO) throws ResourceNotFoundException, BusinessException;

  /**
   * 删除商品
   *
   * @param id 商品 ID
   * @throws ResourceNotFoundException 商品不存在
   */
  @Transactional
  void deleteProduct(Long id) throws ResourceNotFoundException;

  /**
   * 获取指定商品的库存
   *
   * @param productId 商品 ID
   * @return 商品库存信息
   * @throws ResourceNotFoundException 商品不存在或库存记录不存在
   */
  StockpileDTO getStockpile(Long productId) throws ResourceNotFoundException;

  /**
   * 更新商品库存
   *
   * @param productId 商品 ID
   * @param stockpileDTO 库存信息
   * @throws ResourceNotFoundException 商品不存在
   */
  @Transactional
  void updateStockpile(Long productId, StockpileDTO stockpileDTO) throws ResourceNotFoundException;
}
