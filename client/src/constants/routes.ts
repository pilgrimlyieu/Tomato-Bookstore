export const Routes = {
  HOME: "/",

  // 用户
  USER_LOGIN: "/user/login",
  USER_REGISTER: "/user/register",
  USER_PROFILE: "/user/profile",

  // 商品
  PRODUCTS: "/products",
  PRODUCTS_DETAIL: "/products/:id",
  PRODUCTS_SEARCH: "/products/search",

  // 购物车
  CART: "/cart",
  ORDER: "/order",
} as const;
