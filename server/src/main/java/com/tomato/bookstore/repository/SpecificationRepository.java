package com.tomato.bookstore.repository;

import com.tomato.bookstore.model.Specification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 商品规格仓库接口 */
@Repository
public interface SpecificationRepository extends JpaRepository<Specification, Long> {
  List<Specification> findByProductId(Long productId);

  void deleteByProductId(Long productId);
}
