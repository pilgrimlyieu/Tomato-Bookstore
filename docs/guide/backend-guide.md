# 后端开发指南

本文档详细介绍了番茄商城项目的后端架构、开发规范和最佳实践。

## 技术栈概览

番茄商城后端采用以下技术栈：

- **核心框架**：[Spring Boot](https://spring.io/projects/spring-boot) 3.4.x
- **ORM 框架**：[Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- **数据库**：[MySQL](https://www.mysql.com/) 9.0+
- **安全框架**：[Spring Security](https://spring.io/projects/spring-security)
- **JWT**：[jjwt](https://github.com/jwtk/jjwt)
- **API 文档与调试**：[ApiFox](https://apifox.com/)
- **构建工具**：[Maven](https://maven.apache.org/)
- **代码格式化工具**：[spotify-fmt](https://github.com/spotify/fmt-maven-plugin)
- **辅助工具**：[Lombok](https://projectlombok.org/)

## 项目结构

```
server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/tomato/bookstore/
│   │   │       ├── config/           # 配置类
│   │   │       ├── constant/         # 常量定义
│   │   │       ├── controller/       # 控制器
│   │   │       ├── dto/              # 数据传输对象
│   │   │       ├── entity/           # 实体类
│   │   │       ├── exception/        # 自定义异常
│   │   │       ├── repository/       # 数据访问层
│   │   │       ├── security/         # 安全相关
│   │   │       ├── service/          # 业务逻辑层
│   │   │       ├── util/             # 工具类
│   │   │       └── BookstoreApplication.java # 启动类
│   │   └── resources/
│   │       ├── application.yml       # 应用配置
│   │       └── ...
│   └── test/                         # 测试代码
├── pom.xml                           # Maven 配置
└── ...
```

## 开发规范

### 代码风格

我们使用 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) 作为代码风格规范，并通过 spotify-fmt-maven-plugin 自动格式化代码。

### 命名约定

- **包名**：全小写，如 `com.tomato.bookstore.service`
- **类名**：使用 PascalCase，如 `BookController`
- **接口名**：使用 PascalCase，通常以形容词结尾，如 `Searchable`
- **方法名**：使用 camelCase，如 `getUserById`
- **变量名**：使用 camelCase，如 `bookTitle`
- **常量**：全大写下划线分隔，如 `MAX_RETRY_COUNT`
- **枚举**：使用 PascalCase，枚举值使用全大写下划线分隔

### 注释规范

关键代码应添加适当的 Javadoc 注释：

```java
/**
 * 根据用户 ID 获取用户信息
 *
 * @param id 用户 ID
 * @return 用户信息 DTO
 * @throws UserNotFoundException 当用户不存在时抛出
 */
public UserDTO getUserById(Long id) throws UserNotFoundException {
    // 实现代码…
}
```

## API 设计规范

### RESTful API 设计

我们使用 RESTful 风格设计 API，示例如下：

| HTTP 方法 | 操作         | 示例                                |
| :-        | :-           | :-                                  |
| GET       | 获取资源     | `GET /api/books` - 获取所有图书     |
| POST      | 创建资源     | `POST /api/books` - 创建新图书      |
| PUT       | 全量更新资源 | `PUT /api/books/1` - 更新图书       |
| PATCH     | 部分更新资源 | `PATCH /api/books/1` - 部分更新图书 |
| DELETE    | 删除资源     | `DELETE /api/books/1` - 删除图书    |

### URL 设计

- 使用复数名词表示资源集合：`/api/books`
- 使用 ID 定位具体资源：`/api/books/{id}`
- 使用嵌套表示从属关系：`/api/books/{id}/reviews`

### 请求参数

- GET 请求参数使用查询字符串：`GET /api/books?page=1&size=10`
- POST, PUT, PATCH 请求数据使用 JSON 格式

### 响应格式

所有 API 响应使用统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 实际数据
  }
}
```

错误响应：

```json
{
  "code": 404,
  "message": "Book not found with id: 123",
  "data": null
}
```

### 状态码使用

示例：
- 200 OK：请求成功
- 201 Created：资源创建成功
- 400 Bad Request：请求参数错误
- 401 Unauthorized：未授权（未登录）
- 403 Forbidden：无权限访问
- 404 Not Found：资源不存在
- 500 Internal Server Error：服务器内部错误

## 数据库设计

### 实体类设计

使用 JPA 注解映射实体类和数据库表：

```java
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 关系映射

JPA 中的实体关系映射：

#### 一对多关系

```java
// 一本书有多个评论
@Entity
public class Book {
    // …其他字段

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
}

@Entity
public class Review {
    // …其他字段

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
```

#### 多对多关系

```java
// 多本书属于多个类别
@Entity
public class Book {
    // …其他字段

    @ManyToMany
    @JoinTable(
        name = "book_category",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}

@Entity
public class Category {
    // …其他字段

    @ManyToMany(mappedBy = "categories")
    private Set<Book> books = new HashSet<>();
}
```

### Repository 层

使用 Spring Data JPA 的接口定义数据访问操作：

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT b FROM Book b WHERE b.price < :price AND b.isActive = true")
    List<Book> findActiveBooksWithPriceLessThan(@Param("price") BigDecimal price);

    @Modifying
    @Query("UPDATE Book b SET b.stock = b.stock - :quantity WHERE b.id = :id")
    int decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);
}
```

## 安全

### Spring Security 配置

基本的 Spring Security 配置：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // 对于 REST API 通常禁用 CSRF
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### JWT 认证

JWT 工具类：

```java
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
```

JWT 过滤器：

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                String username = jwtUtil.getUsernameFromToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

## 异常处理

### 全局异常处理

使用 `@RestControllerAdvice` 进行全局异常处理：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse response = new ApiResponse(404, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequestException(BadRequestException ex) {
        ApiResponse response = new ApiResponse(400, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        ApiResponse response = new ApiResponse(500, "服务器内部错误", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse response = new ApiResponse(400, "输入验证失败", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
```

### 自定义异常

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
```

## 日志

使用 SLF4J 和 Logback 进行日志记录：

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public Book findById(Long id) {
        logger.info("Fetching book with id: {}", id);

        try {
            return bookRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Book not found with id: {}", id);
                    return new ResourceNotFoundException("Book", "id", id);
                });
        } catch (Exception ex) {
            logger.error("Error occurred while fetching book with id: {}", id, ex);
            throw ex;
        }
    }
}
```

### 日志配置

在 `application.yml` 中配置日志：

```yaml
logging:
  level:
    root: INFO
    com.tomato.bookstore: DEBUG
    org.hibernate.SQL: DEBUG
  file:
    name: logs/bookstore.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 测试

### 单元测试

使用 JUnit 5 和 Mockito 进行单元测试：

```java
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void findById_ShouldReturnBook_WhenBookExists() {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("测试图书");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Act
        Book foundBook = bookService.findById(bookId);

        // Assert
        assertNotNull(foundBook);
        assertEquals(bookId, foundBook.getId());
        assertEquals("测试图书", foundBook.getTitle());

        verify(bookRepository).findById(bookId);
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenBookDoesNotExist() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.findById(bookId);
        });

        verify(bookRepository).findById(bookId);
    }
}
```

### 集成测试

使用 Spring Boot Test 进行集成测试：

```java
@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() throws Exception {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("测试图书");

        when(bookService.findById(bookId)).thenReturn(book);

        // Act & Assert
        mockMvc.perform(get("/api/books/{id}", bookId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(bookId))
            .andExpect(jsonPath("$.data.title").value("测试图书"));
    }

    @Test
    void createBook_ShouldReturnCreatedBook() throws Exception {
        // Arrange
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("新图书");
        bookDTO.setAuthor("测试作者");
        bookDTO.setPrice(new BigDecimal("39.90"));

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle(bookDTO.getTitle());
        savedBook.setAuthor(bookDTO.getAuthor());
        savedBook.setPrice(bookDTO.getPrice());

        when(bookService.createBook(any(BookDTO.class))).thenReturn(savedBook);

        // Act & Assert
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value(201))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("新图书"));
    }
}
```

## 部署

### 构建可执行 JAR

```bash
mvn clean package
```

## 常见问题

### 解决 N + 1 查询问题

使用 JOIN FETCH 解决 N+1 查询：

```java
@Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories WHERE b.isActive = true")
List<Book> findAllActiveWithCategories();
```

### 处理大量数据

使用分页查询处理大量数据：

```java
@GetMapping("/books")
public ResponseEntity<ApiResponse> getAllBooks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    Page<Book> bookPage = bookService.findAll(PageRequest.of(page, size));

    Map<String, Object> response = new HashMap<>();
    response.put("books", bookPage.getContent());
    response.put("currentPage", bookPage.getNumber());
    response.put("totalItems", bookPage.getTotalElements());
    response.put("totalPages", bookPage.getTotalPages());

    return ResponseEntity.ok(new ApiResponse(200, "Success", response));
}
```

### 事务管理

使用 `@Transactional` 注解确保事务完整性：

```java
@Service
public class OrderService {

    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        // 1. 创建订单
        Order order = new Order();
        // 设置订单属性…
        orderRepository.save(order);

        // 2. 减少商品库存
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            bookService.decreaseStock(itemDTO.getBookId(), itemDTO.getQuantity());

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            // 设置订单项属性…
            orderItemRepository.save(orderItem);
        }

        return order;
    }
}
```

### JPA 性能优化

1. 使用适当的 Fetch 类型（LAZY/EAGER）
2. 添加适当的索引
3. 使用查询缓存
4. 批量处理操作

```java
@Bean
public JpaVendorAdapter jpaVendorAdapter() {
    HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setShowSql(true);
    adapter.setGenerateDdl(true);

    Properties properties = new Properties();
    properties.setProperty("hibernate.jdbc.batch_size", "30");
    properties.setProperty("hibernate.order_inserts", "true");
    properties.setProperty("hibernate.order_updates", "true");

    return adapter;
}
```
