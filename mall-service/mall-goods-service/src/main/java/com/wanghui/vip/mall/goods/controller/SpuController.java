package com.wanghui.vip.mall.goods.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.goods.model.Sku;
import com.wanghui.vip.mall.goods.model.Product;
import com.wanghui.vip.mall.goods.model.Spu;
import com.wanghui.vip.mall.goods.service.SKuService;
import com.wanghui.vip.mall.goods.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/spu")
@CrossOrigin
public class SpuController {

    @Autowired
    private SpuService spuService;

    @Autowired
    private SKuService skuService;

    /*****
     * Save product
     */
    @PostMapping(value = "/save")
    public RespResult save(@RequestBody Product product){
        spuService.saveProduct(product);
        return RespResult.ok();
    }

    /***
     * Query Product
     */
    @GetMapping(value = "/product/{id}")
    public RespResult<Product> one(@PathVariable(value = "id")String id){
        Product product = spuService.findBySupId(id);
        return RespResult.ok(product);
    }

    /****
     * 【B端管理后台】查询店铺商品列表
     * GET /api/spu/admin/list
     *
     * 【数据隔离】底层 TenantWebInterceptor 会自动注入当前租户的 tenant_id
     * 【级联查询】关联查询 SKU 获取价格、库存信息
     *
     * @return 当前租户下的商品列表（含价格、库存、图片）
     */
    @GetMapping(value = "/admin/list")
    public RespResult<List<Map<String, Object>>> adminList() {
        // 1. 查询当前租户的所有 SPU
        List<Spu> spuList = spuService.list();
        System.out.println("[Admin SpuController] 查询到 " + spuList.size() + " 个 SPU");

        // 2. 为每个 SPU 关联查询 SKU 信息
        List<Map<String, Object>> result = new ArrayList<>();
        for (Spu spu : spuList) {
            Map<String, Object> item = new HashMap<>();

            // SPU 基础信息
            item.put("id", spu.getId());
            item.put("name", spu.getName());
            item.put("intro", spu.getIntro());
            item.put("isMarketable", spu.getIsMarketable());

            // 处理图片 - 取第一张
            String images = spu.getImages();
            if (images != null && !images.isEmpty()) {
                String firstImage = images.split(",")[0].trim();
                item.put("image", firstImage);
                item.put("images", images);
            } else {
                item.put("image", "");
                item.put("images", "");
            }

            // 关联查询 SKU 获取价格和库存（取第一个 SKU - 最低价）
            QueryWrapper<Sku> skuQuery = new QueryWrapper<>();
            skuQuery.eq("spu_id", spu.getId());
            skuQuery.orderByAsc("price"); // 按价格升序
            // 使用 list 然后取第一条，避免 getOne 的多记录异常
            List<Sku> skuList = skuService.list(skuQuery);
            Sku sku = skuList.isEmpty() ? null : skuList.get(0);

            if (sku != null) {
                item.put("price", sku.getPrice()); // 价格（分）
                item.put("num", sku.getNum());     // 库存
                item.put("skuId", sku.getId());
                // 如果 SKU 有图片，优先使用 SKU 图片
                if (sku.getImage() != null && !sku.getImage().isEmpty()) {
                    item.put("image", sku.getImage());
                }
            } else {
                item.put("price", 0);
                item.put("num", 0);
                item.put("skuId", "");
            }

            result.add(item);
        }

        return RespResult.ok(result);
    }

    /****
     * 【B端管理后台】获取单个商品详情
     * GET /api/spu/admin/{id}
     *
     * @param id SPU 商品 ID
     * @return 商品详情（含价格、库存）
     */
    @GetMapping(value = "/admin/{id}")
    public RespResult<Map<String, Object>> getAdminProductDetail(@PathVariable(value = "id") String id) {
        // 1. 查询 SPU
        Spu spu = spuService.getById(id);
        if (spu == null) {
            return RespResult.error("商品不存在");
        }

        // 2. 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("id", spu.getId());
        result.put("name", spu.getName());
        result.put("intro", spu.getIntro());
        result.put("isMarketable", spu.getIsMarketable());

        // 处理图片
        String images = spu.getImages();
        if (images != null && !images.isEmpty()) {
            String firstImage = images.split(",")[0].trim();
            result.put("image", firstImage);
            result.put("images", images);
        } else {
            result.put("image", "");
            result.put("images", "");
        }

        // 3. 关联查询 SKU 获取价格和库存
        QueryWrapper<Sku> skuQuery = new QueryWrapper<>();
        skuQuery.eq("spu_id", spu.getId());
        skuQuery.orderByAsc("price");
        List<Sku> skuList = skuService.list(skuQuery);
        Sku sku = skuList.isEmpty() ? null : skuList.get(0);

        if (sku != null) {
            result.put("price", sku.getPrice()); // 价格（分）
            result.put("num", sku.getNum());     // 库存
            result.put("skuId", sku.getId());
            if (sku.getImage() != null && !sku.getImage().isEmpty()) {
                result.put("image", sku.getImage());
            }
        } else {
            result.put("price", 0);
            result.put("num", 0);
            result.put("skuId", "");
        }

        return RespResult.ok(result);
    }

    /****
     * 【B端管理后台】更新商品信息
     * PUT /api/spu/admin/update
     *
     * @param productData 商品数据
     * @return 操作结果
     */
    @PutMapping(value = "/admin/update")
    public RespResult updateAdminProduct(@RequestBody Map<String, Object> productData) {
        String id = (String) productData.get("id");
        if (id == null || id.isEmpty()) {
            return RespResult.error("商品ID不能为空");
        }

        // 1. 查询商品是否存在
        Spu spu = spuService.getById(id);
        if (spu == null) {
            return RespResult.error("商品不存在");
        }

        // 2. 更新 SPU 基本信息
        String name = (String) productData.get("name");
        String intro = (String) productData.get("intro");
        Object isMarketableObj = productData.get("isMarketable");
        String image = (String) productData.get("image");

        if (name != null) {
            spu.setName(name);
        }
        if (intro != null) {
            spu.setIntro(intro);
        }
        if (isMarketableObj != null) {
            spu.setIsMarketable(Integer.valueOf(isMarketableObj.toString()));
        }
        if (image != null && !image.isEmpty()) {
            // 更新图片（替换第一张）
            String images = spu.getImages();
            if (images != null && !images.isEmpty()) {
                String[] imageArray = images.split(",");
                imageArray[0] = image;
                spu.setImages(String.join(",", imageArray));
            } else {
                spu.setImages(image);
            }
        }

        boolean spuUpdated = spuService.updateById(spu);

        // 3. 更新 SKU 价格和图片
        Object priceObj = productData.get("price");
        if (priceObj != null) {
            Integer price = Integer.valueOf(priceObj.toString());
            QueryWrapper<Sku> skuQuery = new QueryWrapper<>();
            skuQuery.eq("spu_id", id);
            skuQuery.orderByAsc("price");
            List<Sku> skuList = skuService.list(skuQuery);

            if (!skuList.isEmpty()) {
                Sku firstSku = skuList.get(0);
                firstSku.setPrice(price);
                if (image != null && !image.isEmpty()) {
                    firstSku.setImage(image);
                }
                skuService.updateById(firstSku);
            }
        }

        if (spuUpdated) {
            System.out.println("[Admin SpuController] 商品更新成功，id=" + id);
            return RespResult.ok();
        } else {
            return RespResult.error("更新失败，请稍后重试");
        }
    }

    /****
     * 【B端管理后台】商品上下架切换
     * PUT /api/spu/admin/status/{id}/{status}
     *
     * @param id     SPU 商品 ID
     * @param status 目标状态：1=上架，0=下架
     * @return 操作结果
     */
    @PutMapping(value = "/admin/status/{id}/{status}")
    public RespResult updateMarketableStatus(
            @PathVariable(value = "id") String id,
            @PathVariable(value = "status") Integer status) {

        // 1. 参数校验
        if (status == null || (status != 0 && status != 1)) {
            return RespResult.error("状态参数错误，只能为 0(下架) 或 1(上架)");
        }

        // 2. 查询商品是否存在
        Spu spu = spuService.getById(id);
        if (spu == null) {
            return RespResult.error("商品不存在");
        }

        // 3. 更新上下架状态
        spu.setIsMarketable(status);
        boolean success = spuService.updateById(spu);

        if (success) {
            String action = status == 1 ? "上架" : "下架";
            System.out.println("[Admin SpuController] 商品" + action + "成功，id=" + id);
            return RespResult.ok();
        } else {
            return RespResult.error("操作失败，请稍后重试");
        }
    }
}
