package com.tomato.bookstore.service.impl;

import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.constant.ExceptionMessages;
import com.tomato.bookstore.dto.AdvertisementDTO;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.exception.ResourceNotFoundException;
import com.tomato.bookstore.model.Advertisement;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.repository.AdvertisementRepository;
import com.tomato.bookstore.repository.ProductRepository;
import com.tomato.bookstore.service.AdvertisementService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 广告服务实现类 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementServiceImpl implements AdvertisementService {
  private final AdvertisementRepository advertisementRepository;
  private final ProductRepository productRepository;

  @Override
  @Transactional(readOnly = true)
  public List<AdvertisementDTO> getAllAdvertisements() {
    log.info("获取所有广告");
    List<Advertisement> advertisements = advertisementRepository.findAll();
    return advertisements.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public AdvertisementDTO getAdvertisementById(Long id) throws ResourceNotFoundException {
    log.info("获取广告 id={}", id);
    Advertisement advertisement = findAdvertisementById(id);
    return convertToDTO(advertisement);
  }

  @Override
  @Transactional
  public AdvertisementDTO createAdvertisement(AdvertisementDTO advertisementDTO)
      throws BusinessException {
    log.info("创建广告：{}", advertisementDTO.getTitle());

    // 检查商品是否存在
    Product product = findProductById(advertisementDTO.getProductId());

    Advertisement advertisement =
        Advertisement.builder()
            .title(advertisementDTO.getTitle())
            .content(advertisementDTO.getContent())
            .imageUrl(advertisementDTO.getImageUrl())
            .product(product)
            .build();

    Advertisement savedAdvertisement = advertisementRepository.save(advertisement);
    log.info("广告创建成功，ID：{}", savedAdvertisement.getId());

    return convertToDTO(savedAdvertisement);
  }

  @Override
  @Transactional
  public AdvertisementDTO updateAdvertisement(AdvertisementDTO advertisementDTO)
      throws ResourceNotFoundException, BusinessException {
    log.info("更新广告 id={}", advertisementDTO.getId());

    // 检查广告是否存在
    Advertisement advertisement = findAdvertisementById(advertisementDTO.getId());

    // 检查商品是否存在
    Product product = findProductById(advertisementDTO.getProductId());

    // 更新广告信息
    advertisement.setTitle(advertisementDTO.getTitle());
    advertisement.setContent(advertisementDTO.getContent());
    advertisement.setImageUrl(advertisementDTO.getImageUrl());
    advertisement.setProduct(product);

    Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);
    log.info("广告更新成功 id={}", updatedAdvertisement.getId());

    return convertToDTO(updatedAdvertisement);
  }

  @Override
  @Transactional
  public void deleteAdvertisement(Long id) throws ResourceNotFoundException {
    log.info("删除广告 id={}", id);

    // 检查广告是否存在
    Advertisement advertisement = findAdvertisementById(id);

    advertisementRepository.delete(advertisement);
    log.info("广告已删除 id={}", id);
  }

  /**
   * 根据 ID 查找广告，如果不存在则抛出异常
   *
   * @param id 广告 ID
   * @return 广告实体
   * @throws ResourceNotFoundException 当广告不存在时抛出
   */
  private Advertisement findAdvertisementById(Long id) throws ResourceNotFoundException {
    return advertisementRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.warn("广告不存在 id={}", id);
              return new ResourceNotFoundException(
                  String.format(ExceptionMessages.ADVERTISEMENT_NOT_FOUND, id));
            });
  }

  /**
   * 根据 ID 查找商品，如果不存在则抛出异常
   *
   * @param id 商品 ID
   * @return 商品实体
   * @throws BusinessException 当商品不存在时抛出
   */
  private Product findProductById(Long id) throws BusinessException {
    return productRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.warn("商品不存在 id={}", id);
              return new BusinessException(
                  BusinessErrorCode.PRODUCT_NOT_FOUND,
                  String.format(ExceptionMessages.PRODUCT_NOT_FOUND, id));
            });
  }

  /**
   * 将 Advertisement 实体转换为 AdvertisementDTO
   *
   * @param advertisement 广告实体
   * @return 广告 DTO
   */
  private AdvertisementDTO convertToDTO(Advertisement advertisement) {
    return AdvertisementDTO.builder()
        .id(advertisement.getId())
        .title(advertisement.getTitle())
        .content(advertisement.getContent())
        .imageUrl(advertisement.getImageUrl())
        .productId(advertisement.getProduct().getId())
        .build();
  }
}
