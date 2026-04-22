/**
 * C 端店铺租户：与网关 TenantRoutingFilter、后端演示数据一致（shop1→1001，shop2→1002）。
 * 请求拦截器与页面主题必须共用此逻辑，保证注册/登录携带的 X-Tenant-Id 与当前店一致。
 */
export function resolveTenantIdFromHostname(hostname) {
  const h = (hostname || '').toLowerCase()
  // 与历史 request 逻辑一致：shop1 优先于 shop2（避免异常域名同时包含两段时歧义）
  if (h.includes('shop1')) {
    return '1001'
  }
  if (h.includes('shop2')) {
    return '1002'
  }
  return '1001'
}
