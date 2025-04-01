package com.tomato.bookstore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** 商品库存实体类 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stockpiles")
public class Stockpile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer amount;

  @Column(nullable = false)
  private Integer frozen;

  @OneToOne
  @JoinColumn(name = "product_id", nullable = false)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  private Product product;
}
