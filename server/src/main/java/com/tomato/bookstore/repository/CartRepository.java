package com.tomato.bookstore.repository;

import com.tomato.bookstore.model.Cart;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 购物车仓库 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
  /**
   * 根据用户查找所有购物车商品
   *
   * @param user 用户
   * @return 购物车商品列表
   */
  List<Cart> findByUser(User user);

  /**
   * 根据用户查找所有购物车商品 ID 列表
   *
   * @param user 用户
   * @param ids ID 列表
   * @return 购物车商品列表
   */
  List<Cart> findByUserAndIdIn(User user, List<Long> ids);

  /**
   * 根据用户和商品查找购物车商品
   *
   * @param user 用户
   * @param product 商品
   * @return 购物车商品（如果存在）
   */
  Optional<Cart> findByUserAndProduct(User user, Product product);

  /**
   * 根据用户和购物车商品 ID 查找购物车商品
   *
   * @param user 用户
   * @param id 购物车商品 ID
   * @return 购物车商品（如果存在）
   */
  Optional<Cart> findByUserAndId(User user, Long id);

  /**
   * 删除用户的所有购物车商品
   *
   * @param user 用户
   */
  void deleteByUser(User user);
}
