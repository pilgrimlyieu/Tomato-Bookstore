package com.tomato.bookstore.repository;

import com.tomato.bookstore.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 商品仓库接口 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  boolean existsByTitle(String title);

  boolean existsByTitleAndIdNot(String title, Long id);

  List<Product> findAllByOrderByCreatedAtDesc();
}
