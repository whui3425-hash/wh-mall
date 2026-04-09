package com.wanghui.vip.mall.goods.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.goods.model.Brand;
import com.wanghui.vip.mall.goods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/api/brand")
@CrossOrigin
public class BrandController {

    @Autowired
    private BrandService brandService;

    /****
     * Add method
     */
    @PostMapping
    public RespResult add(@RequestBody Brand brand){
        brandService.save(brand);
        return RespResult.ok();
    }

    /****
     * Update method
     */
    @PutMapping
    public RespResult update(@RequestBody Brand brand){
        brandService.updateById(brand);
        return RespResult.ok();
    }

    /****
     * Delete method
     */
    @DeleteMapping("/{id}")
    public RespResult delete(@PathVariable(value = "id")String id){
        brandService.removeById(id);
        return RespResult.ok();
    }

    /****
     * Conditional query
     */
    @PostMapping(value = "/search")
    public RespResult<List<Brand>> queryList(@RequestBody(required = false) Brand brand){
        if (brand == null) {
            brand = new Brand();
        }
        List<Brand> brands = brandService.queryList(brand);
        return RespResult.ok(brands);
    }

    /****
     * Conditional query
     */
    @PostMapping(value = "/search/{page}/{size}")
    public RespResult<Page<Brand>> queryPageList(
            @PathVariable(value = "page")Long page,
            @PathVariable(value = "size")Long size,
            @RequestBody(required = false) Brand brand){
        if (brand == null) {
            brand = new Brand();
        }
        Page<Brand> pageInfo = brandService.queryPageList(brand,page,size);
        return RespResult.ok(pageInfo);
    }

    /****
     * Query brand collection by category ID
     * http://localhost:9001/brand/category/11159
     * http://192.168.100.130/msitems/1.html
     */
    @GetMapping(value = "/category/{pid}")
    public RespResult<List<Brand>>  categoryBrands(@PathVariable(value = "pid")Integer pid) throws InterruptedException {
        System.out.println("Execute query start,,,,");
        List<Brand> brands = brandService.queryByCategoryId(pid);
        TimeUnit.SECONDS.sleep(10);
        System.out.println("Execute query complete,,,,");
        return RespResult.ok(brands);
    }
}
