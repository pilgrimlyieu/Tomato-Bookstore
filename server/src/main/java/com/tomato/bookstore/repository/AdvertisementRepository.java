package com.tomato.bookstore.repository;

import com.tomato.bookstore.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 广告存储库接口 */
@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
  // TBC
}
