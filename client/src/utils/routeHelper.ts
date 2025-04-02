/**
 * 替换路由中的参数
 *
 * @param route 路由模板（例如 "/products/:id"）
 * @param params 参数对象（例如 { id: 1 }）
 * @returns 替换后的路由（例如 "/products/1"）
 */
export function buildRoute(
  route: string,
  params: Record<string, string | number>,
): string {
  let result = route;

  for (const [key, value] of Object.entries(params)) {
    result = result.replace(`:${key}`, value.toString());
  }

  return result;
}
