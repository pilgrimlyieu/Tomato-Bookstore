package com.tomato.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.dto.AdvertisementDTO;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.exception.ResourceNotFoundException;
import com.tomato.bookstore.model.Advertisement;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.repository.AdvertisementRepository;
import com.tomato.bookstore.repository.ProductRepository;
import com.tomato.bookstore.service.impl.AdvertisementServiceImpl;
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
public class AdvertisementServiceImplTest {
  @Mock private AdvertisementRepository advertisementRepository;
  @Mock private ProductRepository productRepository;

  @InjectMocks private AdvertisementServiceImpl advertisementService;

  private Advertisement advertisement;
  private Product product;
  private AdvertisementDTO advertisementDTO;

  private static final Long ADVERTISEMENT_ID = 1L;
  private static final Long PRODUCT_ID = 1L;
  private static final String TITLE = "测试广告";
  private static final String CONTENT = "这是一条测试广告内容";
  private static final String IMAGE_URL = "https://example.com/image.jpg";

  @BeforeEach
  void setUp() {
    // 设置测试数据
    product = new Product();
    product.setId(PRODUCT_ID);
    product.setTitle("测试商品");

    advertisement = new Advertisement();
    advertisement.setId(ADVERTISEMENT_ID);
    advertisement.setTitle(TITLE);
    advertisement.setContent(CONTENT);
    advertisement.setImageUrl(IMAGE_URL);
    advertisement.setProduct(product);

    advertisementDTO = new AdvertisementDTO();
    advertisementDTO.setId(ADVERTISEMENT_ID);
    advertisementDTO.setTitle(TITLE);
    advertisementDTO.setContent(CONTENT);
    advertisementDTO.setImageUrl(IMAGE_URL);
    advertisementDTO.setProductId(PRODUCT_ID);
  }

  @Test
  @DisplayName("获取所有广告 - 成功")
  void getAllAdvertisementsSuccess() {
    // 准备
    List<Advertisement> advertisements = Arrays.asList(advertisement);
    when(advertisementRepository.findAll()).thenReturn(advertisements);

    // 执行
    List<AdvertisementDTO> result = advertisementService.getAllAdvertisements();

    // 验证
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(ADVERTISEMENT_ID, result.get(0).getId());
    assertEquals(TITLE, result.get(0).getTitle());
    assertEquals(CONTENT, result.get(0).getContent());
    assertEquals(IMAGE_URL, result.get(0).getImageUrl());
    assertEquals(PRODUCT_ID, result.get(0).getProductId());
    verify(advertisementRepository).findAll();
  }

  @Test
  @DisplayName("按 ID 获取广告 - 成功")
  void getAdvertisementByIdSuccess() {
    // 准备
    when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.of(advertisement));

    // 执行
    AdvertisementDTO result = advertisementService.getAdvertisementById(ADVERTISEMENT_ID);

    // 验证
    assertNotNull(result);
    assertEquals(ADVERTISEMENT_ID, result.getId());
    assertEquals(TITLE, result.getTitle());
    assertEquals(CONTENT, result.getContent());
    assertEquals(IMAGE_URL, result.getImageUrl());
    assertEquals(PRODUCT_ID, result.getProductId());
    verify(advertisementRepository).findById(ADVERTISEMENT_ID);
  }

  @Test
  @DisplayName("按 ID 获取广告 - 广告不存在")
  void getAdvertisementByIdAdvertisementNotFound() {
    // 准备
    when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              advertisementService.getAdvertisementById(ADVERTISEMENT_ID);
            });

    assertTrue(exception.getMessage().contains("广告不存在"));
    verify(advertisementRepository).findById(ADVERTISEMENT_ID);
  }

  @Test
  @DisplayName("创建广告 - 成功")
  void createAdvertisementSuccess() {
    // 准备
    when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
    when(advertisementRepository.save(any(Advertisement.class))).thenReturn(advertisement);

    // 执行
    AdvertisementDTO result = advertisementService.createAdvertisement(advertisementDTO);

    // 验证
    assertNotNull(result);
    assertEquals(ADVERTISEMENT_ID, result.getId());
    assertEquals(TITLE, result.getTitle());
    assertEquals(CONTENT, result.getContent());
    assertEquals(IMAGE_URL, result.getImageUrl());
    assertEquals(PRODUCT_ID, result.getProductId());
    verify(productRepository).findById(PRODUCT_ID);
    verify(advertisementRepository).save(any(Advertisement.class));
  }

  @Test
  @DisplayName("创建广告 - 商品不存在")
  void createAdvertisementProductNotFound() {
    // 准备
    when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              advertisementService.createAdvertisement(advertisementDTO);
            });

    assertEquals(BusinessErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    verify(productRepository).findById(PRODUCT_ID);
    verify(advertisementRepository, never()).save(any(Advertisement.class));
  }

  @Test
  @DisplayName("更新广告 - 成功")
  void updateAdvertisementSuccess() {
    // 准备
    when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.of(advertisement));
    when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
    when(advertisementRepository.save(any(Advertisement.class))).thenReturn(advertisement);

    // 执行
    AdvertisementDTO result = advertisementService.updateAdvertisement(advertisementDTO);

    // 验证
    assertNotNull(result);
    assertEquals(ADVERTISEMENT_ID, result.getId());
    assertEquals(TITLE, result.getTitle());
    assertEquals(CONTENT, result.getContent());
    assertEquals(IMAGE_URL, result.getImageUrl());
    assertEquals(PRODUCT_ID, result.getProductId());
    verify(advertisementRepository).findById(ADVERTISEMENT_ID);
    verify(productRepository).findById(PRODUCT_ID);
    verify(advertisementRepository).save(any(Advertisement.class));
  }

  @Test
  @DisplayName("更新广告 - 广告不存在")
  void updateAdvertisementAdvertisementNotFound() {
    // 准备
    when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              advertisementService.updateAdvertisement(advertisementDTO);
            });

    assertTrue(exception.getMessage().contains("广告不存在"));
    verify(advertisementRepository).findById(ADVERTISEMENT_ID);
    verify(productRepository, never()).findById(anyLong());
    verify(advertisementRepository, never()).save(any(Advertisement.class));
  }

  @Test
  @DisplayName("更新广告 - 商品不存在")
  void updateAdvertisementProductNotFound() {
    // 准备
    when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.of(advertisement));
    when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              advertisementService.updateAdvertisement(advertisementDTO);
            });

    assertEquals(BusinessErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    verify(advertisementRepository).findById(ADVERTISEMENT_ID);
    verify(productRepository).findById(PRODUCT_ID);
    verify(advertisementRepository, never()).save(any(Advertisement.class));
  }

  @Test
  @DisplayName("删除广告 - 成功")
  void deleteAdvertisementSuccess() {
    // 准备
    when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.of(advertisement));
    doNothing().when(advertisementRepository).delete(advertisement);

    // 执行
    advertisementService.deleteAdvertisement(ADVERTISEMENT_ID);

    // 验证
    verify(advertisementRepository).findById(ADVERTISEMENT_ID);
    verify(advertisementRepository).delete(advertisement);
  }

  @Test
  @DisplayName("删除广告 - 广告不存在")
  void deleteAdvertisementAdvertisementNotFound() {
    // 准备
    when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              advertisementService.deleteAdvertisement(ADVERTISEMENT_ID);
            });

    assertTrue(exception.getMessage().contains("广告不存在"));
    verify(advertisementRepository).findById(ADVERTISEMENT_ID);
    verify(advertisementRepository, never()).delete(any(Advertisement.class));
  }
}
