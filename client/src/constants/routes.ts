export const Routes = {
  HOME: "/",

  // 用户
  USER_LOGIN: "/user/login",
  USER_REGISTER: "/user/register",
  USER_PROFILE: "/user/profile",

  // 商品
  PRODUCT_LIST: "/products",
  PRODUCT_DETAIL: "/products/:id",
  PRODUCT_STOCKPILE: "/products/stockpile/:productId",

  // 购物车
  CART: "/cart",
  CART_ITEM: "/cart/:cartItemId",
  CART_CHECKOUT: "/cart/checkout",

  // 订单
  ORDER_LIST: "/orders",
  ORDER_DETAIL: "/orders/:orderId",
  ORDER_PAY: "/orders/:orderId/pay",
  ORDER_SUCCESS: "/orders/success",
  ORDER_FAIL: "/orders/fail",

  // 书评
  USER_REVIEWS: "/user/reviews",

  // 读书笔记
  USER_NOTES: "/user/notes",
  NOTE_DETAIL: "/notes/:noteId",
  NOTE_CREATE: "/notes/create/:productId",
  NOTE_EDIT: "/notes/edit/:noteId",

  // 管理员
  ADMIN: "/admin",
  ADMIN_PRODUCTS: "/admin/products",
  ADMIN_PRODUCT_CREATE: "/admin/products/create",
  ADMIN_PRODUCT_EDIT: "/admin/products/:id",
  ADMIN_REVIEWS: "/admin/reviews",
  ADMIN_NOTES: "/admin/notes",
  ADMIN_ADVERTISEMENTS: "/admin/advertisements",
  ADMIN_ADVERTISEMENT_CREATE: "/admin/advertisements/create",
  ADMIN_ADVERTISEMENT_EDIT: "/admin/advertisements/:id",
} as const;
