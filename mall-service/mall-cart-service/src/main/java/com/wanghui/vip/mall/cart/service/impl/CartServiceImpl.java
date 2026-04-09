package com.wanghui.vip.mall.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.cart.config.tenant.TenantContextHolder;
import com.wanghui.vip.mall.cart.mapper.CartMapper;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.cart.service.CartService;
import com.wanghui.vip.mall.goods.feign.SkuFeign;
import com.wanghui.vip.mall.goods.model.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现类
 * 【核心功能】支持多租户、用户隔离、Feign调用商品服务
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private SkuFeign skuFeign;

    /**
     * 【核心】添加商品到购物车（核心业务逻辑）
     * 1. 通过Feign调用商品服务，查询SKU真实信息（价格、名称、图片）
     * 2. 检查当前用户购物车是否已存在该SKU
     * 3. 已存在：数量累加（Update）
     * 4. 不存在：新增记录（Insert）
     * 5. 自动注入当前租户ID（从网关传递的Header中解析）
     *
     * 【兼容性处理】前端可能传递SPU ID（如SPU001），此时自动查找该SPU下的第一个SKU
     * @param userId 用户ID（从JWT Header X-User-Id 获取）
     * @param userName 用户名（从JWT Header X-User-Name 获取）
     * @param skuId SKU ID 或 SPU ID
     * @param num 添加数量
     */
    @Override
    public void addCartItem(String userId, String userName, String skuId, Integer num) {
        // 1. 通过Feign调用商品服务，获取SKU详细信息
        RespResult<Sku> skuResp = skuFeign.one(skuId);
        Sku sku = null;

        if (skuResp != null && skuResp.getData() != null) {
            sku = skuResp.getData();
        } else {
            // 【兼容性】如果直接查询SKU失败，尝试作为SPU ID查询第一个SKU
            System.out.println("[Cart] SKU not found by direct ID, trying SPU lookup for: " + skuId);
            RespResult<Sku> spuSkuResp = skuFeign.oneBySpuId(skuId);
            if (spuSkuResp != null && spuSkuResp.getData() != null) {
                sku = spuSkuResp.getData();
                System.out.println("[Cart] Found SKU by SPU lookup: " + sku.getId() + " for SPU: " + skuId);
            }
        }

        if (sku == null) {
            throw new RuntimeException("商品不存在或已下架，skuId/spuId=" + skuId);
        }

        // 2. 查询当前用户的购物车是否已存在该SKU（基于userId）
        Cart existingCart = findByUserIdAndSkuId(userId, skuId);

        if (existingCart != null) {
            // 3. 已存在：累加数量，更新记录
            existingCart.setNum(existingCart.getNum() + num);
            cartMapper.updateById(existingCart);
            System.out.println("[Cart] Update existing item - CartId: " + existingCart.getId() + ", New Num: " + existingCart.getNum());
        } else {
            // 4. 不存在：插入新记录
            Cart cart = new Cart();
            cart.setUserId(userId);          // 【关键】设置用户ID
            cart.setUserName(userName);
            cart.setName(sku.getName());
            cart.setPrice(sku.getPrice());
            cart.setImage(sku.getImage());
            cart.setSkuId(skuId);
            cart.setNum(num);
            // 5. 获取当前租户ID（从ThreadLocal上下文，由网关传入X-Tenant-Id）
            cart.setTenantId(TenantContextHolder.getTenantId());
            cartMapper.insert(cart);
            System.out.println("[Cart] Insert new item - UserId: " + userId + ", SkuId: " + skuId);
        }
    }

    /**
     * 【核心】根据用户ID查询购物车列表（自动附加当前租户过滤）
     * MyBatis-Plus 租户插件会自动在SQL中添加 tenant_id = ? 条件
     * @param userId 用户ID（从JWT Header X-User-Id 获取）
     * @param userName 用户名（从JWT Header X-User-Name 获取，可为空）
     * @return 购物车列表
     */
    @Override
    public List<Cart> listByUserId(String userId, String userName) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        // 【关键】基于 user_id 查询，确保用户只能看到自己的购物车
        queryWrapper.eq("user_id", userId);
        // 可选：同时按 user_name 过滤（双重验证）
        if (userName != null && !userName.isEmpty()) {
            queryWrapper.eq("user_name", userName);
        }
        // 租户过滤由MyBatis-Plus插件自动处理
        queryWrapper.orderByDesc("id");
        return cartMapper.selectList(queryWrapper);
    }

    /**
     * 查询指定用户的指定SKU购物车记录
     * 用于判断购物车中是否已存在该商品
     * @param userId 用户ID
     * @param skuId SKU ID
     * @return 购物车记录，不存在返回null
     */
    @Override
    public Cart findByUserIdAndSkuId(String userId, String skuId) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        // 【关键】基于 user_id 查询
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("sku_id", skuId);
        // 租户过滤由MyBatis-Plus自动附加
        return cartMapper.selectOne(queryWrapper);
    }

    /**
     * 【核心】根据ID删除购物车记录（带用户权限验证）
     * 只能删除属于自己的购物车记录
     * @param id 购物车记录ID
     * @param userId 当前登录用户ID（从JWT Header获取）
     * @return 删除成功返回true，失败返回false（无权限或记录不存在）
     */
    @Override
    public boolean deleteById(Long id, String userId) {
        // 1. 先查询记录，验证归属权
        Cart cart = cartMapper.selectById(id);
        if (cart == null) {
            System.out.println("[Cart] Delete failed - Record not found, ID: " + id);
            return false;
        }
        // 2. 验证该记录是否属于当前用户
        if (!userId.equals(cart.getUserId())) {
            System.out.println("[Cart] Delete failed - Permission denied, ID: " + id + ", Owner: " + cart.getUserId() + ", Requester: " + userId);
            return false;
        }
        // 3. 执行删除
        int result = cartMapper.deleteById(id);
        System.out.println("[Cart] Delete success - ID: " + id + ", UserId: " + userId);
        return result > 0;
    }

    /**
     * 【核心】修改购物车商品数量（带用户权限验证）
     * 只能修改属于自己的购物车记录
     * @param id 购物车记录ID
     * @param userId 当前登录用户ID（从JWT Header获取）
     * @param num 新的数量
     * @return 更新成功返回true，失败返回false（无权限或记录不存在）
     */
    @Override
    public boolean updateNum(Long id, String userId, Integer num) {
        // 1. 先查询记录，验证归属权
        Cart cart = cartMapper.selectById(id);
        if (cart == null) {
            System.out.println("[Cart] Update failed - Record not found, ID: " + id);
            return false;
        }
        // 2. 验证该记录是否属于当前用户
        if (!userId.equals(cart.getUserId())) {
            System.out.println("[Cart] Update failed - Permission denied, ID: " + id + ", Owner: " + cart.getUserId() + ", Requester: " + userId);
            return false;
        }
        // 3. 更新数量
        cart.setNum(num);
        int result = cartMapper.updateById(cart);
        System.out.println("[Cart] Update success - ID: " + id + ", New Num: " + num + ", UserId: " + userId);
        return result > 0;
    }

    /**
     * 【核心】批量删除购物车记录（带用户权限验证）
     * 只能删除属于自己的购物车记录
     * @param ids 购物车记录ID列表
     * @param userId 当前登录用户ID（从JWT Header获取），为null时跳过验证（订单服务调用场景）
     */
    @Override
    public void deleteBatch(List<Long> ids, String userId) {
        for (Long id : ids) {
            if (userId != null && !userId.isEmpty()) {
                // 有userId时进行权限验证（用户直接调用）
                deleteById(id, userId);
            } else {
                // 无userId时直接删除（订单服务调用场景，已在订单提交前校验过权限）
                cartMapper.deleteById(id);
            }
        }
        System.out.println("[Cart] Batch delete completed - IDs: " + ids + ", UserId: " + userId);
    }

    /***
     * Delete shopping cart list by IDs (legacy method)
     */
    @Override
    public void delete(List<String> ids) {
        for (String id : ids) {
            cartMapper.deleteById(Long.valueOf(id));
        }
    }

    /**
     * 【订单服务调用】根据购物车商品ID列表查询商品详情
     * @param cartItemIds 购物车商品ID列表
     * @return 购物车列表
     */
    @Override
    public List<Cart> listByIds(List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", cartItemIds);
        // 租户过滤由MyBatis-Plus自动附加
        return cartMapper.selectList(queryWrapper);
    }

    /***
     * Query shopping cart list by ID collection (legacy)
     */
    @Override
    public List<Cart> list(List<String> ids) {
        if (ids != null && !ids.isEmpty()) {
            QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", ids);
            return cartMapper.selectList(queryWrapper);
        }
        return null;
    }

    /***
     * Shopping cart list
     */
    @Override
    public List<Cart> list(String userName) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        return cartMapper.selectList(queryWrapper);
    }

    /***
     * Add to shopping cart (legacy implementation)
     */
    @Override
    public void add(String id, String userName, Integer num) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName).eq("sku_id", id);
        Cart existingCart = cartMapper.selectOne(queryWrapper);

        if (existingCart != null) {
            cartMapper.deleteById(existingCart.getId());
        }

        if (num > 0) {
            RespResult<Sku> skuResp = skuFeign.one(id);
            Sku sku = skuResp.getData();
            Cart cart = new Cart();
            cart.setUserName(userName);
            cart.setName(sku.getName());
            cart.setPrice(sku.getPrice());
            cart.setImage(sku.getImage());
            cart.setSkuId(id);
            cart.setNum(num);
            cart.setTenantId(TenantContextHolder.getTenantId());
            cartMapper.insert(cart);
        }
    }
}
