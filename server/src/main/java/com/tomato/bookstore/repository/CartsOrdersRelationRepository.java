package com.tomato.bookstore.repository;

import com.tomato.bookstore.model.CartsOrdersRelation;
import com.tomato.bookstore.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 购物车商品与订单关联仓库 */
@Repository
public interface CartsOrdersRelationRepository extends JpaRepository<CartsOrdersRelation, Long> {
  /**
   * 根据订单查找所有购物车商品与订单关联
   *
   * @param order 订单
   * @return 购物车商品与订单关联列表
   */
  List<CartsOrdersRelation> findByOrder(Order order);

  /**
   * 根据购物车商品 ID 查找所有购物车商品与订单关联
   *
   * @param cartId 购物车商品 ID
   * @return 购物车商品与订单关联列表
   */
  List<CartsOrdersRelation> findByCartId(Long cartId);

  /**
   * 根据订单删除所有购物车商品与订单关联
   *
   * @param order 订单
   */
  void deleteByOrder(Order order);
}
