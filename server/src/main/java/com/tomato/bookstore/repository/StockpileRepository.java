package com.tomato.bookstore.repository;

import com.tomato.bookstore.model.Stockpile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 商品库存仓库接口 */
@Repository
public interface StockpileRepository extends JpaRepository<Stockpile, Long> {
  Optional<Stockpile> findByProductId(Long productId);

  void deleteByProductId(Long productId);
}
