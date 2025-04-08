package com.tomato.bookstore.repository;

import com.tomato.bookstore.constant.OrderStatus;
import com.tomato.bookstore.model.Order;
import com.tomato.bookstore.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 订单仓库 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  /**
   * 根据用户查找所有订单
   *
   * @param user 用户
   * @return 订单列表
   */
  List<Order> findByUser(User user);

  /**
   * 根据用户和订单状态查找订单
   *
   * @param user 用户
   * @param status 订单状态
   * @return 订单列表
   */
  List<Order> findByUserAndStatus(User user, OrderStatus status);

  /**
   * 根据用户和订单 ID 查找订单
   *
   * @param user 用户
   * @param id 订单 ID
   * @return 订单（如果存在）
   */
  Optional<Order> findByUserAndId(User user, Long id);

  /**
   * 根据订单状态查找过期订单
   *
   * @param status 订单状态
   * @param expiredTime 过期时间
   * @return 过期订单列表
   */
  List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime expiredTime);

  /**
   * 根据用户查询所有订单并按创建时间降序排序
   *
   * @param user 用户
   * @return 该用户的所有订单
   */
  List<Order> findByUserOrderByCreatedAtDesc(User user);
}
