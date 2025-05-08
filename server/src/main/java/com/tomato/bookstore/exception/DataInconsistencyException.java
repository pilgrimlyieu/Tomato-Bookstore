package com.tomato.bookstore.exception;

/**
 * 数据一致性异常
 *
 * <p>当系统发现数据不一致（比如外键关联不存在）时抛出此异常
 */
public class DataInconsistencyException extends RuntimeException {
  public DataInconsistencyException(String message) {
    super(message);
  }

  /**
   * 创建数据一致性异常
   *
   * @param entityName 实体名称
   * @param id 实体 ID
   * @param relatedEntityName 关联实体名称
   * @param relatedId 关联实体 ID
   * @return 数据一致性异常
   */
  public static DataInconsistencyException create(
      String entityName, Object id, String relatedEntityName, Object relatedId) {
    return new DataInconsistencyException(
        String.format(
            "严重的数据一致性问题：%s（ID=%s）引用了不存在的 %s（ID=%s）", entityName, id, relatedEntityName, relatedId));
  }
}
