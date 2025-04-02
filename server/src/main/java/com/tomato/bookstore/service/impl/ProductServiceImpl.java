package com.tomato.bookstore.service.impl;

import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.constant.ExceptionMessages;
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
import com.tomato.bookstore.service.ProductService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;
  private final SpecificationRepository specificationRepository;
  private final StockpileRepository stockpileRepository;

  @Override
  @Transactional(readOnly = true)
  public List<ProductDTO> getAllProducts() {
    log.info("获取所有商品");
    List<Product> products = productRepository.findAllByOrderByCreatedAtDesc();
    return products.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public ProductDTO getProductById(Long id) {
    log.info("获取商品 id={}", id);
    return convertToDTO(findProductById(id));
  }

  @Override
  @Transactional
  public ProductDTO createProduct(ProductDTO productDTO) {
    log.info("创建商品：{}", productDTO.getTitle());

    // 检查商品名称是否已存在
    if (productRepository.existsByTitle(productDTO.getTitle())) {
      log.warn("商品名称已存在：{}", productDTO.getTitle());
      throw new BusinessException(BusinessErrorCode.PRODUCT_TITLE_ALREADY_EXISTS);
    }

    // 创建产品实体
    LocalDateTime now = LocalDateTime.now();
    Product product =
        Product.builder()
            .title(productDTO.getTitle())
            .price(productDTO.getPrice())
            .rate(productDTO.getRate())
            .description(productDTO.getDescription())
            .cover(productDTO.getCover())
            .detail(productDTO.getDetail())
            .createdAt(now)
            .updatedAt(now)
            .specifications(new ArrayList<>())
            .build();

    // 保存商品
    Product savedProduct = productRepository.save(product);
    log.debug("商品基本信息已保存（id={}）", savedProduct.getId());

    // 处理商品规格
    if (!CollectionUtils.isEmpty(productDTO.getSpecifications())) {
      List<Specification> specifications =
          productDTO.getSpecifications().stream()
              .map(
                  specDTO ->
                      Specification.builder()
                          .item(specDTO.getItem())
                          .value(specDTO.getValue())
                          .product(savedProduct)
                          .build())
              .collect(Collectors.toList());

      savedProduct.setSpecifications(specificationRepository.saveAll(specifications));
      log.debug("商品规格已保存，规格数量：{}", specifications.size());
    }

    // 创建初始库存，默认为0
    Stockpile stockpile = Stockpile.builder().product(savedProduct).amount(0).frozen(0).build();
    savedProduct.setStockpile(stockpileRepository.save(stockpile));
    log.debug("商品库存已初始化");

    log.info("商品创建成功，ID：{}", savedProduct.getId());
    return convertToDTO(savedProduct);
  }

  @Override
  @Transactional
  public void updateProduct(ProductDTO productDTO) {
    log.info("更新商品 id={}", productDTO.getId());

    // 检查商品是否存在
    Product product = findProductById(productDTO.getId());

    // 检查商品名称是否已被其他商品使用
    if (productRepository.existsByTitleAndIdNot(productDTO.getTitle(), product.getId())) {
      log.warn("商品名称已被其他商品使用：{}", productDTO.getTitle());
      throw new BusinessException(BusinessErrorCode.PRODUCT_TITLE_ALREADY_EXISTS);
    }

    // 更新商品基本信息
    product.setTitle(productDTO.getTitle());
    product.setPrice(productDTO.getPrice());
    product.setRate(productDTO.getRate());
    product.setDescription(productDTO.getDescription());
    product.setCover(productDTO.getCover());
    product.setDetail(productDTO.getDetail());
    product.setUpdatedAt(LocalDateTime.now());

    updateProductSpecifications(product, productDTO);

    // 保存商品
    productRepository.save(product);
    log.info("商品更新成功 id={}", product.getId());
  }

  @Override
  @Transactional
  public void deleteProduct(Long id) {
    log.info("删除商品 id={}", id);
    // 商品存在性验证
    findProductById(id);

    try {
      // 删除关联的规格和库存
      specificationRepository.deleteByProductId(id);
      stockpileRepository.deleteByProductId(id);

      // 删除商品
      productRepository.deleteById(id);
      log.info("商品已删除 id={}", id);
    } catch (Exception e) {
      log.error("删除商品失败 id={}，原因: {}", id, e.getMessage());
      throw e;
    }
  }

  @Override
  public StockpileDTO getStockpile(Long productId) {
    log.info("获取商品库存 productId={}", productId);

    // 检查商品是否存在
    findProductById(productId);

    // 获取库存信息
    Stockpile stockpile =
        stockpileRepository
            .findByProductId(productId)
            .orElseThrow(
                () -> {
                  log.warn("商品库存记录不存在 productId={}", productId);
                  return new BusinessException(
                      BusinessErrorCode.PRODUCT_STOCK_NOT_FOUND, "商品库存记录不存在");
                });

    return convertToStockpileDTO(stockpile);
  }

  @Override
  @Transactional
  public void updateStockpile(Long productId, StockpileDTO stockpileDTO) {
    log.info(
        "更新商品库存（productId={}, amount={}, frozen={}）",
        productId,
        stockpileDTO.getAmount(),
        stockpileDTO.getFrozen());

    // 检查商品是否存在
    findProductById(productId);

    // 获取并更新库存
    Stockpile stockpile =
        stockpileRepository
            .findByProductId(productId)
            .orElseGet(() -> createNewStockpile(productId));

    stockpile.setAmount(stockpileDTO.getAmount());
    stockpile.setFrozen(stockpileDTO.getFrozen());
    stockpileRepository.save(stockpile);
    log.info("商品库存更新成功 productId={}", productId);
  }

  /**
   * 更新商品规格
   *
   * @param product 商品实体
   * @param productDTO 商品数据传输对象
   */
  private void updateProductSpecifications(Product product, ProductDTO productDTO) {
    if (product.getSpecifications() == null) {
      product.setSpecifications(new ArrayList<>());
    }

    // 构建当前规格的 ID 映射，便于快速查找
    Map<Long, Specification> existingSpecsMap = buildSpecificationsMap(product.getSpecifications());

    // 处理新的规格列表
    List<Specification> updatedSpecs = new ArrayList<>();
    if (!CollectionUtils.isEmpty(productDTO.getSpecifications())) {
      for (SpecificationDTO specDTO : productDTO.getSpecifications()) {
        if (specDTO.getId() != null && existingSpecsMap.containsKey(specDTO.getId())) {
          // 更新已存在的规格
          Specification existingSpec = existingSpecsMap.get(specDTO.getId());
          existingSpec.setItem(specDTO.getItem());
          existingSpec.setValue(specDTO.getValue());
          updatedSpecs.add(existingSpec);
          existingSpecsMap.remove(specDTO.getId()); // 从映射中移除，剩下的将被删除
        } else {
          // 创建新规格
          Specification newSpec =
              Specification.builder()
                  .item(specDTO.getItem())
                  .value(specDTO.getValue())
                  .product(product)
                  .build();
          updatedSpecs.add(newSpec);
        }
      }
    }

    // 删除不再使用的旧规格
    if (!existingSpecsMap.isEmpty()) {
      specificationRepository.deleteAll(existingSpecsMap.values());
    }

    // 更新规格列表
    product.getSpecifications().clear();
    product.getSpecifications().addAll(updatedSpecs);
  }

  /**
   * 构建规格 ID 到规格实体的映射
   *
   * @param specifications 规格列表
   * @return 规格 ID 到规格实体的映射
   */
  private Map<Long, Specification> buildSpecificationsMap(List<Specification> specifications) {
    Map<Long, Specification> specsMap = new HashMap<>();
    if (specifications != null && !specifications.isEmpty()) {
      for (Specification spec : specifications) {
        if (spec.getId() != null) {
          specsMap.put(spec.getId(), spec);
        }
      }
    }
    return specsMap;
  }

  /**
   * 根据 ID 查找商品，如果不存在则抛出异常
   *
   * @param id 商品 ID
   * @return 商品实体
   * @throws ResourceNotFoundException 当商品不存在时抛出
   */
  private Product findProductById(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.warn("商品不存在 id={}", id);
              return new ResourceNotFoundException(
                  String.format(ExceptionMessages.PRODUCT_NOT_FOUND, id));
            });
  }

  /**
   * 创建新的库存记录
   *
   * @param productId 商品ID
   * @return 新的库存记录
   */
  private Stockpile createNewStockpile(Long productId) {
    log.debug("商品库存记录不存在，创建新的记录 productId={}", productId);
    Product product = productRepository.getReferenceById(productId);
    return Stockpile.builder().product(product).amount(0).frozen(0).build();
  }

  /**
   * 将 Product 实体转换为 ProductDTO
   *
   * @param product 商品实体
   * @return 商品 DTO
   */
  private ProductDTO convertToDTO(Product product) {
    return ProductDTO.builder()
        .id(product.getId())
        .title(product.getTitle())
        .price(product.getPrice())
        .rate(product.getRate())
        .description(product.getDescription())
        .cover(product.getCover())
        .detail(product.getDetail())
        .specifications(convertSpecificationsToDTO(product.getSpecifications()))
        .build();
  }

  /**
   * 将规格列表转换为 DTO 列表
   *
   * @param specifications 规格列表
   * @return 规格 DTO 列表
   */
  private List<SpecificationDTO> convertSpecificationsToDTO(List<Specification> specifications) {
    if (specifications == null || specifications.isEmpty()) {
      return new ArrayList<>();
    }

    return specifications.stream().map(this::convertToSpecDTO).collect(Collectors.toList());
  }

  /**
   * 将 Specification 实体转换为 SpecificationDTO
   *
   * @param specification 商品规格实体
   * @return 商品规格 DTO
   */
  private SpecificationDTO convertToSpecDTO(Specification specification) {
    return SpecificationDTO.builder()
        .id(specification.getId())
        .item(specification.getItem())
        .value(specification.getValue())
        .productId(specification.getProduct().getId())
        .build();
  }

  /**
   * 将 Stockpile 实体转换为 StockpileDTO
   *
   * @param stockpile 商品库存实体
   * @return 商品库存 DTO
   */
  private StockpileDTO convertToStockpileDTO(Stockpile stockpile) {
    return StockpileDTO.builder()
        .id(stockpile.getId())
        .amount(stockpile.getAmount())
        .frozen(stockpile.getFrozen())
        .productId(stockpile.getProduct().getId())
        .build();
  }
}
