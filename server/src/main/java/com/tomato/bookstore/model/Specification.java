package com.tomato.bookstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** 商品规格实体类 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "specifications")
public class Specification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String item;

  @Column(nullable = false)
  private String value;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Product product;
}
