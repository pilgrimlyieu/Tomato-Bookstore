package com.tomato.bookstore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 购物车实体类 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts")
public class Cart {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<CartsOrdersRelation> cartOrderRelations = new ArrayList<>();
}
