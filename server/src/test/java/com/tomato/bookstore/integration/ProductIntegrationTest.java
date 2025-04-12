package com.tomato.bookstore.integration;

import static com.tomato.bookstore.constant.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.dto.LoginDTO;
import com.tomato.bookstore.dto.ProductDTO;
import com.tomato.bookstore.dto.SpecificationDTO;
import com.tomato.bookstore.dto.StockpileDTO;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.model.Specification;
import com.tomato.bookstore.model.Stockpile;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.model.User.UserRole;
import com.tomato.bookstore.repository.ProductRepository;
import com.tomato.bookstore.repository.SpecificationRepository;
import com.tomato.bookstore.repository.StockpileRepository;
import com.tomato.bookstore.repository.UserRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductIntegrationTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ProductRepository productRepository;
  @Autowired private SpecificationRepository specificationRepository;
  @Autowired private StockpileRepository stockpileRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @MockitoBean private Clock clock;

  private Product testProduct;
  private ProductDTO productDTO;
  private String adminToken;
  private User adminUser;

  @BeforeEach
  void setUp() {
    // 清理数据
    specificationRepository.deleteAll();
    stockpileRepository.deleteAll();
    productRepository.deleteAll();

    // 创建测试商品
    testProduct = createTestProduct();

    // 创建商品 DTO 用于测试
    productDTO = createProductDTO();

    // 创建管理员账户并获取 Token
    createAdminAndGetToken();
  }

  private Product createTestProduct() {
    LocalDateTime now = LocalDateTime.now();

    // 创建商品并保存
    Product product = new Product();
    product.setTitle(TEST_PRODUCT_TITLE);
    product.setPrice(TEST_PRODUCT_PRICE);
    product.setRate(TEST_PRODUCT_RATE);
    product.setDescription(TEST_PRODUCT_DESCRIPTION);
    product.setCover(TEST_PRODUCT_COVER);
    product.setDetail(TEST_PRODUCT_DETAIL);
    product.setCreatedAt(now);
    product.setUpdatedAt(now);
    product.setSpecifications(new ArrayList<>());

    Product savedProduct = productRepository.save(product);

    // 创建并保存规格
    Specification spec = new Specification();
    spec.setItem(TEST_SPEC_ITEM);
    spec.setValue(TEST_SPEC_VALUE);
    spec.setProduct(savedProduct);
    Specification savedSpec = specificationRepository.save(spec);

    List<Specification> specs = new ArrayList<>();
    specs.add(savedSpec);
    savedProduct.setSpecifications(specs);

    // 创建并保存库存
    Stockpile stockpile = new Stockpile();
    stockpile.setProduct(savedProduct);
    stockpile.setAmount(TEST_STOCK_AMOUNT);
    stockpile.setFrozen(TEST_STOCK_FROZEN);
    Stockpile savedStockpile = stockpileRepository.save(stockpile);

    savedProduct.setStockpile(savedStockpile);

    return productRepository.save(savedProduct);
  }

  private ProductDTO createProductDTO() {
    // 创建规格 DTO
    SpecificationDTO specDTO = new SpecificationDTO();
    specDTO.setItem("新规格");
    specDTO.setValue("新规格值");

    // 创建商品 DTO
    ProductDTO dto = new ProductDTO();
    dto.setTitle("新测试商品");
    dto.setPrice(TEST_PRODUCT_PRICE);
    dto.setRate(TEST_PRODUCT_RATE);
    dto.setDescription(TEST_PRODUCT_DESCRIPTION);
    dto.setCover(TEST_PRODUCT_COVER);
    dto.setDetail(TEST_PRODUCT_DETAIL);
    dto.setSpecifications(Arrays.asList(specDTO));

    return dto;
  }

  private void createAdminAndGetToken() {
    // 创建管理员用户
    adminUser = new User();
    adminUser.setUsername(TEST_ADMIN_USERNAME);
    adminUser.setPassword(passwordEncoder.encode(TEST_ADMIN_PASSWORD));
    adminUser.setEmail("admin@example.com");
    adminUser.setPhone("13900000000");
    adminUser.setRole(UserRole.ADMIN);
    adminUser.setCreatedAt(LocalDateTime.now());
    adminUser.setUpdatedAt(LocalDateTime.now());

    userRepository.save(adminUser);

    // 登录获取 Token
    try {
      LoginDTO loginDTO = new LoginDTO();
      loginDTO.setUsername(TEST_ADMIN_USERNAME);
      loginDTO.setPassword(TEST_ADMIN_PASSWORD);

      MvcResult result =
          mockMvc
              .perform(
                  post(ApiConstants.USER_LOGIN_PATH)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(loginDTO)))
              .andReturn();

      String responseJson = result.getResponse().getContentAsString();
      JsonNode root = objectMapper.readTree(responseJson);
      adminToken = root.path("data").asText();
    } catch (Exception e) {
      throw new RuntimeException("获取管理员 Token 失败", e);
    }
  }

  @Test
  @DisplayName("获取所有商品")
  void getAllProductsTest() throws Exception {
    mockMvc
        .perform(
            get(ApiConstants.PRODUCT_BASE_PATH).header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].title").value(TEST_PRODUCT_TITLE));
  }

  @Test
  @DisplayName("获取指定商品详情")
  void getProductByIdTest() throws Exception {
    mockMvc
        .perform(
            get(ApiConstants.PRODUCT_DETAIL_PATH.replace("{id}", testProduct.getId().toString()))
                .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.data.id").value(testProduct.getId()))
        .andExpect(jsonPath("$.data.title").value(testProduct.getTitle()));
  }

  @Test
  @DisplayName("获取指定商品详情 - 商品不存在")
  void getProductByIdNotFoundTest() throws Exception {
    Long nonExistingId = 9999L;
    mockMvc
        .perform(
            get(ApiConstants.PRODUCT_DETAIL_PATH.replace("{id}", nonExistingId.toString()))
                .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
        .andExpect(jsonPath("$.msg").value(String.format("商品不存在，ID：%s", nonExistingId)));
  }

  @Test
  @DisplayName("创建商品成功 - 管理员")
  void createProductAsAdminTest() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post(ApiConstants.PRODUCT_BASE_PATH)
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data.title").value(productDTO.getTitle()))
            .andReturn();

    // 验证商品已创建
    String responseJson = result.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseJson);
    Long newProductId = root.path("data").path("id").asLong();

    assertTrue(productRepository.existsById(newProductId));
    assertTrue(stockpileRepository.findByProductId(newProductId).isPresent());
  }

  @Test
  @DisplayName("创建商品失败 - 未授权")
  void createProductUnauthorizedTest() throws Exception {
    mockMvc
        .perform(
            post(ApiConstants.PRODUCT_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("创建商品失败 - 商品名已存在")
  void createProductTitleExistsTest() throws Exception {
    // 使用已存在的商品名
    productDTO.setTitle(TEST_PRODUCT_TITLE);

    mockMvc
        .perform(
            post(ApiConstants.PRODUCT_BASE_PATH)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.code").value(BusinessErrorCode.PRODUCT_TITLE_ALREADY_EXISTS.getCode()))
        .andExpect(
            jsonPath("$.msg").value(BusinessErrorCode.PRODUCT_TITLE_ALREADY_EXISTS.getMessage()));
  }

  @Test
  @DisplayName("更新商品成功 - 管理员")
  void updateProductAsAdminTest() throws Exception {
    // 更新已存在的商品
    ProductDTO updateDTO = new ProductDTO();
    updateDTO.setId(testProduct.getId());
    updateDTO.setTitle("更新后的商品名称");
    updateDTO.setPrice(TEST_PRODUCT_PRICE);
    updateDTO.setRate(TEST_PRODUCT_RATE);
    updateDTO.setDescription("更新后的描述");
    updateDTO.setCover(TEST_PRODUCT_COVER);
    updateDTO.setDetail(TEST_PRODUCT_DETAIL);

    mockMvc
        .perform(
            put(ApiConstants.PRODUCT_BASE_PATH)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk());

    // 验证数据库中已更新
    Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
    assertEquals("更新后的商品名称", updatedProduct.getTitle());
    assertEquals("更新后的描述", updatedProduct.getDescription());
  }

  @Test
  @DisplayName("删除商品成功 - 管理员")
  void deleteProductAsAdminTest() throws Exception {
    mockMvc
        .perform(
            delete(ApiConstants.PRODUCT_DETAIL_PATH.replace("{id}", testProduct.getId().toString()))
                .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk());

    // 验证商品已删除
    assertFalse(productRepository.existsById(testProduct.getId()));
    assertTrue(specificationRepository.findByProductId(testProduct.getId()).isEmpty());
    assertFalse(stockpileRepository.findByProductId(testProduct.getId()).isPresent());
  }

  @Test
  @DisplayName("获取商品库存")
  void getStockpileTest() throws Exception {
    mockMvc
        .perform(
            get(ApiConstants.PRODUCT_STOCKPILE_DETAIL_PATH.replace(
                    "{productId}", testProduct.getId().toString()))
                .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.data.amount").value(TEST_STOCK_AMOUNT))
        .andExpect(jsonPath("$.data.frozen").value(TEST_STOCK_FROZEN));
  }

  @Test
  @DisplayName("更新商品库存成功 - 管理员")
  void updateStockpileAsAdminTest() throws Exception {
    // 创建库存更新 DTO
    StockpileDTO updateDTO = new StockpileDTO();
    updateDTO.setAmount(200);
    updateDTO.setFrozen(10);

    mockMvc
        .perform(
            patch(
                    ApiConstants.PRODUCT_STOCKPILE_DETAIL_PATH.replace(
                        "{productId}", testProduct.getId().toString()))
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk());

    // 验证库存已更新
    Stockpile updatedStockpile =
        stockpileRepository.findByProductId(testProduct.getId()).orElseThrow();
    assertEquals(200, updatedStockpile.getAmount());
    assertEquals(10, updatedStockpile.getFrozen());
  }

  @Test
  @DisplayName("完整商品流程 - 创建、更新、获取、库存管理、删除")
  void completeProductFlowTest() throws Exception {
    // 1. 创建商品
    MvcResult createResult =
        mockMvc
            .perform(
                post(ApiConstants.PRODUCT_BASE_PATH)
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDTO)))
            .andExpect(status().isOk())
            .andReturn();

    String createJson = createResult.getResponse().getContentAsString();
    Long newProductId = objectMapper.readTree(createJson).path("data").path("id").asLong();

    // 2. 获取商品详情
    mockMvc
        .perform(
            get(ApiConstants.PRODUCT_DETAIL_PATH.replace("{id}", newProductId.toString()))
                .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value(productDTO.getTitle()));

    // 3. 更新商品
    ProductDTO updateDTO = new ProductDTO();
    updateDTO.setId(newProductId);
    updateDTO.setTitle("流程更新商品");
    updateDTO.setPrice(TEST_PRODUCT_PRICE);
    updateDTO.setRate(TEST_PRODUCT_RATE);
    updateDTO.setDescription(TEST_PRODUCT_DESCRIPTION);
    updateDTO.setCover(TEST_PRODUCT_COVER);
    updateDTO.setDetail(TEST_PRODUCT_DETAIL);

    mockMvc
        .perform(
            put(ApiConstants.PRODUCT_BASE_PATH)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk());

    // 4. 更新库存
    StockpileDTO stockUpdateDTO = new StockpileDTO();
    stockUpdateDTO.setAmount(300);
    stockUpdateDTO.setFrozen(15);

    mockMvc
        .perform(
            patch(
                    ApiConstants.PRODUCT_STOCKPILE_DETAIL_PATH.replace(
                        "{productId}", newProductId.toString()))
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockUpdateDTO)))
        .andExpect(status().isOk());

    // 5. 获取更新后的库存
    mockMvc
        .perform(
            get(ApiConstants.PRODUCT_STOCKPILE_DETAIL_PATH.replace(
                    "{productId}", newProductId.toString()))
                .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.amount").value(300))
        .andExpect(jsonPath("$.data.frozen").value(15));

    // 6. 删除商品
    mockMvc
        .perform(
            delete(ApiConstants.PRODUCT_DETAIL_PATH.replace("{id}", newProductId.toString()))
                .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk());

    // 验证商品已删除
    assertFalse(productRepository.existsById(newProductId));
  }
}
