package com.tomato.bookstore.model;

import com.tomato.bookstore.constant.OrderStatus;
import com.tomato.bookstore.constant.PaymentMethod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 订单实体类 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private BigDecimal totalAmount;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @Column(nullable = false)
  private String shippingAddress;

  private String tradeNo;

  private LocalDateTime paymentTime;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  @Builder.Default
  private List<CartsOrdersRelation> orderItems = new ArrayList<>();
}
