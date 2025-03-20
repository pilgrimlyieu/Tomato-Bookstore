# 测试指南

本文档详细介绍了番茄商城项目的测试规范和最佳实践，旨在帮助团队成员编写高质量的测试代码，确保项目的稳定性和可靠性。

## 测试策略概述

番茄商城项目采用多层次测试策略，确保软件质量：

1. **单元测试**：测试独立的代码单元（函数、方法、组件）
2. **集成测试**：测试多个单元之间的交互
3. **API 测试**：测试后端 API 端点
4. **端到端测试**：模拟用户操作，测试整个系统流程

### 测试金字塔

遵循测试金字塔原则，自下而上：

- **单元测试**：数量最多，运行最快
- **集成测试**：数量适中
- **端到端测试**：数量最少，耗时较长

## 前端测试

前端测试使用 [Vitest](https://vitest.dev/) 作为测试框架和 [@vue/test-utils](https://test-utils.vuejs.org/) 作为 Vue 组件测试工具。

### 单元测试

#### 工具函数测试

针对 `utils/` 目录中的工具函数：

```typescript
// src/utils/__tests__/formatters.spec.ts
import { describe, it, expect } from 'vitest'
import { formatPrice, formatDate } from '../formatters'

describe('formatPrice', () => {
  it('formats price with currency symbol', () => {
    expect(formatPrice(100)).toBe('¥100.00')
    expect(formatPrice(99.9)).toBe('¥99.90')
  })

  it('handles zero correctly', () => {
    expect(formatPrice(0)).toBe('¥0.00')
  })
})
```

#### 组件测试

测试 Vue 组件的渲染、交互和数据反应：

```typescript
// src/components/__tests__/BookCard.spec.ts
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import BookCard from '../BookCard.vue'

describe('BookCard', () => {
  const book = {
    id: 1,
    title: '测试图书',
    author: '张三',
    price: 49.9
  }

  it('renders book information correctly', () => {
    const wrapper = mount(BookCard, {
      props: { book }
    })

    expect(wrapper.find('h3').text()).toBe('测试图书')
    expect(wrapper.text()).toContain('张三')
    expect(wrapper.text()).toContain('¥49.90')
  })

  it('emits add-to-cart event when button is clicked', async () => {
    const wrapper = mount(BookCard, {
      props: { book }
    })

    await wrapper.find('button').trigger('click')

    expect(wrapper.emitted('add-to-cart')).toBeTruthy()
    expect(wrapper.emitted('add-to-cart')[0]).toEqual([1])
  })
})
```

#### Store 测试

测试 Pinia store：

```typescript
// src/stores/__tests__/cart.spec.ts
import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useCartStore } from '../cart'

describe('Cart Store', () => {
  beforeEach(() => {
    // 创建一个新的 Pinia 实例
    setActivePinia(createPinia())
  })

  it('adds item to cart', () => {
    const store = useCartStore()
    const book = { id: 1, title: '测试图书', price: 49.9 }

    store.addToCart(book)

    expect(store.items.length).toBe(1)
    expect(store.items[0].book).toEqual(book)
    expect(store.items[0].quantity).toBe(1)
  })

  it('increases quantity for existing item', () => {
    const store = useCartStore()
    const book = { id: 1, title: '测试图书', price: 49.9 }

    store.addToCart(book)
    store.addToCart(book)

    expect(store.items.length).toBe(1)
    expect(store.items[0].quantity).toBe(2)
  })

  it('calculates total price correctly', () => {
    const store = useCartStore()

    store.addToCart({ id: 1, title: '图书1', price: 50 })
    store.addToCart({ id: 2, title: '图书2', price: 30 }, 2)

    expect(store.totalPrice).toBe(110) // 50 + (30 * 2)
  })
})
```

### 模拟 API 请求

使用 [vi.mock()](https://vitest.dev/api/vi.html#vi-mock) 模拟 API 调用：

```typescript
// src/services/__tests__/book-service.spec.ts
import { describe, it, expect, vi, beforeEach } from 'vitest'
import apiClient from '@/utils/api'
import bookService from '../book-service'

// 模拟 API 客户端
vi.mock('@/utils/api', () => ({
  default: {
    get: vi.fn()
  }
}))

describe('Book Service', () => {
  beforeEach(() => {
    vi.resetAllMocks()
  })

  it('fetches books with correct parameters', async () => {
    // 设置模拟返回值
    apiClient.get.mockResolvedValue({
      data: [{ id: 1, title: '测试图书' }]
    })

    // 调用服务
    await bookService.getBooks(2, 20)

    // 验证 API 调用
    expect(apiClient.get).toHaveBeenCalledWith('/books', {
      params: { page: 2, size: 20 }
    })
  })
})
```

### 运行前端测试

```bash
# 运行所有测试
bun run test

# 运行特定文件的测试
bun run test src/components/__tests__/BookCard.spec.ts

# 以监视模式运行测试（在开发时很有用）
bun run test:watch
```

## 后端测试

后端测试使用 JUnit 5, Mockito 和 Spring Boot Test 框架。

### 单元测试

单元测试主要关注服务层和工具类：

```java
// src/test/java/com/tomato/bookstore/service/BookServiceTest.java
package com.tomato.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tomato.bookstore.exception.ResourceNotFoundException;
import com.tomato.bookstore.model.Book;
import com.tomato.bookstore.repository.BookRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

  @Mock private BookRepository bookRepository;

  @InjectMocks private BookService bookService;

  @Test
  void findById_ShouldReturnBook_WhenBookExists() {
    // Arrange
    Long bookId = 1L;
    Book book = new Book();
    book.setId(bookId);
    book.setTitle("测试图书");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

    // Act
    Book result = bookService.findById(bookId);

    // Assert
    assertNotNull(result);
    assertEquals(bookId, result.getId());
    assertEquals("测试图书", result.getTitle());
    verify(bookRepository).findById(bookId);
  }

  @Test
  void findById_ShouldThrowException_WhenBookNotFound() {
    // Arrange
    Long bookId = 1L;
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          bookService.findById(bookId);
        });
    verify(bookRepository).findById(bookId);
  }
}
```

### 集成测试

集成测试主要关注控制器层和 API 端点：

```java
// src/test/java/com/tomato/bookstore/controller/BookControllerTest.java
package com.tomato.bookstore.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomato.bookstore.dto.BookDTO;
import com.tomato.bookstore.model.Book;
import com.tomato.bookstore.service.BookService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
public class BookControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookService bookService;

  @Test
  void getBookById_ShouldReturnBook() throws Exception {
    // Arrange
    Long bookId = 1L;
    Book book = new Book();
    book.setId(bookId);
    book.setTitle("测试图书");
    book.setAuthor("测试作者");
    book.setPrice(new BigDecimal("49.90"));

    when(bookService.findById(bookId)).thenReturn(book);

    // Act & Assert
    mockMvc
        .perform(get("/api/books/{id}", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", is(200)))
        .andExpect(jsonPath("$.data.id", is(1)))
        .andExpect(jsonPath("$.data.title", is("测试图书")));
  }

  @Test
  void createBook_ShouldReturnCreatedBook() throws Exception {
    // Arrange
    BookDTO bookDTO = new BookDTO();
    bookDTO.setTitle("新图书");
    bookDTO.setAuthor("新作者");
    bookDTO.setPrice(new BigDecimal("59.90"));

    Book savedBook = new Book();
    savedBook.setId(1L);
    savedBook.setTitle(bookDTO.getTitle());
    savedBook.setAuthor(bookDTO.getAuthor());
    savedBook.setPrice(bookDTO.getPrice());

    when(bookService.createBook(any(BookDTO.class))).thenReturn(savedBook);

    // Act & Assert
    mockMvc
        .perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code", is(201)))
        .andExpect(jsonPath("$.data.id", is(1)))
        .andExpect(jsonPath("$.data.title", is("新图书")));
  }
}
```

### 数据库集成测试

使用 `@DataJpaTest` 测试数据库操作：

```java
// src/test/java/com/tomato/bookstore/repository/BookRepositoryTest.java
package com.tomato.bookstore.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.tomato.bookstore.model.Book;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class BookRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private BookRepository bookRepository;

  @Test
  void findByTitleContainingIgnoreCase_ShouldReturnMatchingBooks() {
    // Arrange
    Book book1 = new Book();
    book1.setTitle("Java 编程思想");
    book1.setAuthor("Bruce Eckel");
    book1.setPrice(new BigDecimal("99.00"));
    book1.setIsActive(true);
    entityManager.persist(book1);

    Book book2 = new Book();
    book2.setTitle("深入理解 Java 虚拟机");
    book2.setAuthor("周志明");
    book2.setPrice(new BigDecimal("79.00"));
    book2.setIsActive(true);
    entityManager.persist(book2);

    Book book3 = new Book();
    book3.setTitle("Python 基础教程");
    book3.setAuthor("Magnus Lie Hetland");
    book3.setPrice(new BigDecimal("69.00"));
    book3.setIsActive(true);
    entityManager.persist(book3);

    entityManager.flush();

    // Act
    List<Book> result = bookRepository.findByTitleContainingIgnoreCase("java");

    // Assert
    assertEquals(2, result.size());
    assertTrue(
        result.stream()
            .allMatch(book -> book.getTitle().toLowerCase().contains("java")));
  }
}
```

### 运行后端测试

```bash
# 使用 Maven 运行测试
mvn test

# 运行特定测试类
mvn test -Dtest=BookServiceTest

# 运行特定测试方法
mvn test -Dtest=BookServiceTest#findById_ShouldReturnBook_WhenBookExists
```

## 测试数据管理

### 前端测试数据

使用工厂函数创建测试数据：

```typescript
// src/test/factories/book.ts
export function createBook(overrides = {}) {
  return {
    id: 1,
    title: '测试图书',
    author: '测试作者',
    price: 49.9,
    description: '这是一本测试用图书的详细描述',
    ...overrides
  }
}

export function createBookList(count = 3) {
  return Array.from({ length: count }, (_, index) =>
    createBook({ id: index + 1, title: `测试图书 ${index + 1}` })
  )
}
```

### 后端测试数据

使用测试数据构建器：

```java
// src/test/java/com/tomato/bookstore/utils/TestDataBuilder.java
package com.tomato.bookstore.utils;

import com.tomato.bookstore.model.Book;
import com.tomato.bookstore.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestDataBuilder {

  public static Book buildBook() {
    Book book = new Book();
    book.setTitle("测试图书");
    book.setAuthor("测试作者");
    book.setPrice(new BigDecimal("49.90"));
    book.setDescription("这是一本测试图书描述");
    book.setStock(100);
    book.setIsActive(true);
    book.setCreatedAt(LocalDateTime.now());
    book.setUpdatedAt(LocalDateTime.now());
    return book;
  }

  public static List<Book> buildBookList(int count) {
    List<Book> books = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Book book = buildBook();
      book.setTitle("测试图书 " + (i + 1));
      books.add(book);
    }
    return books;
  }

  public static User buildUser() {
    User user = new User();
    user.setUsername("testuser");
    user.setEmail("test@example.com");
    user.setPassword("$2a$10$encrypted");
    user.setName("测试用户");
    return user;
  }
}
```

## 常见问题

### 测试数据管理问题

**问题**：测试之间共享测试数据导致测试不独立。

**解决方案**：
- 每个测试创建自己需要的数据
- 在 `setUp` 和 `tearDown` 方法中清理测试数据
- 使用事务回滚确保测试不影响数据库状态

```java
@Test
@Transactional
public void testCreateBook() {
    // 测试代码...
    // 方法结束后事务自动回滚
}
```

### 模拟外部依赖

**问题**：如何测试依赖第三方服务的代码？

**解决方案**：
- 使用模拟对象替代外部依赖
- 对于 HTTP 请求，使用 WireMock 或类似工具
- 创建测试专用配置以覆盖生产环境配置

```java
@MockBean
private PaymentService paymentService;

@Test
void testOrderProcessing() {
    // 配置模拟行为
    when(paymentService.processPayment(any())).thenReturn(true);

    // 测试代码...

    // 验证交互
    verify(paymentService).processPayment(any());
}
```

### 处理异步测试

**问题**：如何测试异步操作？

**解决方案**：
- 前端：使用 `await` 等待异步操作完成
- 后端：使用 `CompletableFuture` 和 `awaitility` 库

```typescript
// 前端异步测试
it('loads data asynchronously', async () => {
  const wrapper = mount(AsyncComponent);
  await flushPromises(); // 等待所有 Promise 解析
  expect(wrapper.text()).toContain('Loaded data');
});
```

```java
// 后端异步测试
@Test
void testAsyncOperation() {
    service.triggerAsyncOperation();

    await().atMost(5, TimeUnit.SECONDS)
           .until(() -> repository.findAll().size() > 0);
}
```

### 测试配置问题

**问题**：测试环境配置与开发或生产环境不一致。

**解决方案**：
- 创建测试专用配置文件，如 `application-test.yml`
- 使用 `@ActiveProfiles("test")` 激活测试配置
- 使用内存数据库如 H2 进行测试

```java
@SpringBootTest
@ActiveProfiles("test")
public class ApplicationIntegrationTest {
    // 测试代码...
}
```

### 测试执行速度慢

**问题**：测试执行太慢，影响开发效率。

**解决方案**：
- 区分快速运行的单元测试和较慢的集成测试
- 使用标签（如 JUnit 5 的 `@Tag`）将测试分类
- 开发时只运行快速测试，完整测试留给 CI 系统

```java
@Tag("fast")
@Test
void testSimpleBusinessLogic() {
    // 快速的单元测试...
}

@Tag("slow")
@Test
void testDatabaseIntegration() {
    // 较慢的集成测试...
}
```
