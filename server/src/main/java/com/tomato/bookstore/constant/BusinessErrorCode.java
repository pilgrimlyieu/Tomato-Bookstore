package com.tomato.bookstore.constant;

/**
 * 业务错误代码枚举
 * 规则：前两位表示模块，后三位表示具体错误
 * 模块划分：
 * 10: 用户模块
 * 20: 商品模块
 * 30: 交易模块
 * 40: 订单模块
 * 50: 书评模块
 * 60: 笔记模块
 * 90: 系统通用
 */
public enum BusinessErrorCode {
    // 用户模块（10xxx）
    USER_NOT_FOUND(10001, "用户不存在"),
    USER_ALREADY_EXISTS(10002, "用户名已存在"),
    PASSWORD_INCORRECT(10003, "密码不正确"),
    USER_ACCOUNT_LOCKED(10004, "账户已被锁定"),
    USER_UNAUTHORIZED(10005, "用户未授权"),
    USERNAME_ALREADY_EXISTS(20001, "用户名已存在"),
    EMAIL_ALREADY_EXISTS(20002, "邮箱已存在"),
    INCORRECT_PASSWORD(20004, "密码不正确"),

    // 商品模块（20xxx）
    PRODUCT_NOT_FOUND(20001, "商品不存在"),
    PRODUCT_OUT_OF_STOCK(20002, "商品库存不足"),
    PRODUCT_OFFLINE(20003, "商品已下架"),
    PRODUCT_PRICE_CHANGED(20004, "商品价格已变更"),

    // 交易模块（30xxx）
    CART_EMPTY(30001, "购物车为空"),
    CART_ITEM_NOT_FOUND(30002, "购物车商品不存在"),
    CART_ITEM_MAX_LIMIT(30003, "已达购买数量上限"),

    // 订单模块（40xxx）
    ORDER_NOT_FOUND(40001, "订单不存在"),
    ORDER_ALREADY_PAID(40002, "订单已支付"),
    ORDER_ALREADY_CANCELLED(40003, "订单已取消"),
    ORDER_CANNOT_CANCEL(40004, "订单无法取消"),
    ORDER_STATUS_ERROR(40005, "订单状态错误"),

    // 书评模块（50xxx）
    REVIEW_NOT_FOUND(50001, "书评不存在"),
    REVIEW_ALREADY_EXISTS(50002, "已发表过书评"),
    REVIEW_CONTENT_INVALID(50003, "书评内容不符合规范"),

    // 笔记模块（60xxx）
    NOTE_NOT_FOUND(60001, "笔记不存在"),
    NOTE_CONTENT_INVALID(60002, "笔记内容不符合规范"),

    // 系统通用（90xxx）
    SYSTEM_ERROR(90001, "系统错误"),
    OPERATION_TOO_FREQUENT(90002, "操作过于频繁"),
    INVALID_PARAMETER(90003, "参数无效"),
    ACCESS_DENIED(90004, "访问被拒绝");

    private final int code;
    private final String message;

    BusinessErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
