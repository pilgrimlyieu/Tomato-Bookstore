package com.tomato.bookstore.integration;

import static com.tomato.bookstore.constant.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.dto.AdvertisementDTO;
import com.tomato.bookstore.model.Advertisement;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.model.User.UserRole;
import com.tomato.bookstore.repository.AdvertisementRepository;
import com.tomato.bookstore.repository.ProductRepository;
import com.tomato.bookstore.repository.UserRepository;
import com.tomato.bookstore.util.TestDataFactory;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
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
public class AdvertisementIntegrationTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private AdvertisementRepository advertisementRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private TestDataFactory testDataFactory;

  @MockitoBean private Clock clock;

  private User adminUser;
  private User customerUser;
  private Product testProduct;
  private Advertisement testAdvertisement;
  private String adminToken;
  private String customerToken;

  @BeforeEach
  void setUp() {
    // 配置模拟的 Clock 对象
    when(clock.instant()).thenReturn(Instant.now());
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    // 清理数据
    advertisementRepository.deleteAll();

    // 创建测试数据
    setupTestData();
  }

  private void setupTestData() {
    // 创建管理员用户
    adminUser = testDataFactory.createTestUser(TEST_ADMIN_USERNAME);
    adminUser.setRole(UserRole.ADMIN);
    adminUser = userRepository.save(adminUser);

    // 创建普通用户
    customerUser = testDataFactory.createTestUser(TEST_CUSTOMER_USERNAME);

    // 创建测试商品
    testProduct =
        testDataFactory.createTestProduct(TEST_ADVERTISEMENT_PRODUCT_NAME, TEST_PRODUCT_PRICE);

    // 创建测试广告
    testAdvertisement =
        testDataFactory.createTestAdvertisement(
            TEST_ADVERTISEMENT_TITLE, TEST_ADVERTISEMENT_CONTENT, testProduct);

    // 获取用户令牌
    adminToken = testDataFactory.getUserToken(TEST_ADMIN_USERNAME, TEST_PASSWORD);
    customerToken = testDataFactory.getUserToken(TEST_CUSTOMER_USERNAME, TEST_PASSWORD);
  }

  @Test
  @DisplayName("获取所有广告")
  void getAllAdvertisementsTest() throws Exception {
    mockMvc
        .perform(
            get(ApiConstants.ADVERTISEMENT_BASE_PATH)
                .header("Authorization", "Bearer " + customerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].title").value(TEST_ADVERTISEMENT_TITLE));
  }

  @Test
  @DisplayName("获取指定广告详情")
  void getAdvertisementByIdTest() throws Exception {
    mockMvc
        .perform(
            get(ApiConstants.ADVERTISEMENT_DETAIL_PATH.replace(
                    "{id}", testAdvertisement.getId().toString()))
                .header("Authorization", "Bearer " + customerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.data.id").value(testAdvertisement.getId()))
        .andExpect(jsonPath("$.data.title").value(testAdvertisement.getTitle()))
        .andExpect(jsonPath("$.data.content").value(testAdvertisement.getContent()))
        .andExpect(jsonPath("$.data.imageUrl").value(testAdvertisement.getImageUrl()))
        .andExpect(jsonPath("$.data.productId").value(testProduct.getId()));
  }

  @Test
  @DisplayName("获取指定广告详情 - 广告不存在")
  void getAdvertisementByIdNotFoundTest() throws Exception {
    Long nonExistingId = 9999L;
    mockMvc
        .perform(
            get(ApiConstants.ADVERTISEMENT_DETAIL_PATH.replace("{id}", nonExistingId.toString()))
                .header("Authorization", "Bearer " + customerToken))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
        .andExpect(jsonPath("$.msg").value(String.format("广告不存在，ID：%s", nonExistingId)));
  }

  @Test
  @DisplayName("创建广告成功 - 管理员")
  void createAdvertisementAsAdminTest() throws Exception {
    // 创建广告 DTO
    AdvertisementDTO advertisementDTO = new AdvertisementDTO();
    advertisementDTO.setTitle("新测试广告");
    advertisementDTO.setContent("这是新广告内容");
    advertisementDTO.setImageUrl("https://example.com/new-image.jpg");
    advertisementDTO.setProductId(testProduct.getId());

    MvcResult result =
        mockMvc
            .perform(
                post(ApiConstants.ADVERTISEMENT_BASE_PATH)
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(advertisementDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data.title").value("新测试广告"))
            .andReturn();

    // 验证广告已创建
    String responseJson = result.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseJson);
    Long newAdvertisementId = root.path("data").path("id").asLong();

    Optional<Advertisement> savedAdvertisement =
        advertisementRepository.findById(newAdvertisementId);
    assertTrue(savedAdvertisement.isPresent());
    assertEquals("新测试广告", savedAdvertisement.get().getTitle());
    assertEquals("这是新广告内容", savedAdvertisement.get().getContent());
  }

  @Test
  @DisplayName("创建广告失败 - 非管理员用户")
  void createAdvertisementAsCustomerFailsTest() throws Exception {
    // 创建广告 DTO
    AdvertisementDTO advertisementDTO = new AdvertisementDTO();
    advertisementDTO.setTitle("新测试广告");
    advertisementDTO.setContent("这是新广告内容");
    advertisementDTO.setImageUrl("https://example.com/new-image.jpg");
    advertisementDTO.setProductId(testProduct.getId());

    mockMvc
        .perform(
            post(ApiConstants.ADVERTISEMENT_BASE_PATH)
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(advertisementDTO)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("创建广告失败 - 商品不存在")
  void createAdvertisementProductNotFoundTest() throws Exception {
    // 创建广告 DTO 用不存在的商品 ID
    AdvertisementDTO advertisementDTO = new AdvertisementDTO();
    advertisementDTO.setTitle("新测试广告");
    advertisementDTO.setContent("这是新广告内容");
    advertisementDTO.setImageUrl("https://example.com/new-image.jpg");
    advertisementDTO.setProductId(9999L); // 不存在的商品 ID

    mockMvc
        .perform(
            post(ApiConstants.ADVERTISEMENT_BASE_PATH)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(advertisementDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(BusinessErrorCode.PRODUCT_NOT_FOUND.getCode()))
        .andExpect(jsonPath("$.msg").value(String.format("商品不存在，ID：%s", 9999L)));
  }

  @Test
  @DisplayName("更新广告成功 - 管理员")
  void updateAdvertisementAsAdminTest() throws Exception {
    // 更新广告 DTO
    AdvertisementDTO updateDTO = new AdvertisementDTO();
    updateDTO.setId(testAdvertisement.getId());
    updateDTO.setTitle("更新后的广告标题");
    updateDTO.setContent("更新后的广告内容");
    updateDTO.setImageUrl(testAdvertisement.getImageUrl());
    updateDTO.setProductId(testProduct.getId());

    mockMvc
        .perform(
            put(ApiConstants.ADVERTISEMENT_BASE_PATH)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.data.title").value("更新后的广告标题"))
        .andExpect(jsonPath("$.data.content").value("更新后的广告内容"));

    // 验证数据库中已更新
    Advertisement updatedAdvertisement =
        advertisementRepository.findById(testAdvertisement.getId()).orElseThrow();
    assertEquals("更新后的广告标题", updatedAdvertisement.getTitle());
    assertEquals("更新后的广告内容", updatedAdvertisement.getContent());
  }

  @Test
  @DisplayName("删除广告成功 - 管理员")
  void deleteAdvertisementAsAdminTest() throws Exception {
    mockMvc
        .perform(
            delete(
                    ApiConstants.ADVERTISEMENT_DETAIL_PATH.replace(
                        "{id}", testAdvertisement.getId().toString()))
                .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.data").value("删除成功"));

    // 验证广告已删除
    Optional<Advertisement> deletedAdvertisement =
        advertisementRepository.findById(testAdvertisement.getId());
    assertFalse(deletedAdvertisement.isPresent());
  }

  @Test
  @DisplayName("完整广告流程测试 - 创建、查询、更新、删除")
  void completeAdvertisementFlowTest() throws Exception {
    // 1. 创建新广告
    AdvertisementDTO newDTO = new AdvertisementDTO();
    newDTO.setTitle("流程测试广告");
    newDTO.setContent("这是完整流程测试广告内容");
    newDTO.setImageUrl("https://example.com/flow-test.jpg");
    newDTO.setProductId(testProduct.getId());

    MvcResult createResult =
        mockMvc
            .perform(
                post(ApiConstants.ADVERTISEMENT_BASE_PATH)
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newDTO)))
            .andExpect(status().isOk())
            .andReturn();

    // 获取新创建的广告 ID
    String createJson = createResult.getResponse().getContentAsString();
    Long newAdId = objectMapper.readTree(createJson).path("data").path("id").asLong();

    // 2. 查询新创建的广告
    mockMvc
        .perform(
            get(ApiConstants.ADVERTISEMENT_DETAIL_PATH.replace("{id}", newAdId.toString()))
                .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("流程测试广告"))
        .andExpect(jsonPath("$.data.content").value("这是完整流程测试广告内容"));

    // 3. 更新广告
    AdvertisementDTO updateDTO = new AdvertisementDTO();
    updateDTO.setId(newAdId);
    updateDTO.setTitle("已更新的流程测试广告");
    updateDTO.setContent("已更新的完整流程测试广告内容");
    updateDTO.setImageUrl("https://example.com/updated-flow-test.jpg");
    updateDTO.setProductId(testProduct.getId());

    mockMvc
        .perform(
            put(ApiConstants.ADVERTISEMENT_BASE_PATH)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("已更新的流程测试广告"));

    // 4. 删除广告
    mockMvc
        .perform(
            delete(ApiConstants.ADVERTISEMENT_DETAIL_PATH.replace("{id}", newAdId.toString()))
                .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk());

    // 验证广告已删除
    Optional<Advertisement> deletedAd = advertisementRepository.findById(newAdId);
    assertFalse(deletedAd.isPresent());
  }
}
