package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.DishesInfo;
import cc.mrbird.febs.cos.entity.MerchantInfo;
import cc.mrbird.febs.cos.entity.UserInfo;
import cc.mrbird.febs.cos.service.IDishesInfoService;
import cc.mrbird.febs.cos.service.IMerchantInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.regexp.internal.RE;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/dishes-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DishesInfoController {

    private final IDishesInfoService dishesInfoService;

    private final IMerchantInfoService merchantInfoService;

    /**
     * 分页获取餐品信息
     *
     * @param page       分页对象
     * @param dishesInfo 餐品信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<DishesInfo> page, DishesInfo dishesInfo) {
        return R.ok(dishesInfoService.selectDishesPage(page, dishesInfo));
    }

    @GetMapping("/queryGooodsGroup")
    public R queryGooodsGroup() {
        List<DishesInfo> dishesInfos = dishesInfoService.list();
        List<MerchantInfo> merchantInfoList = merchantInfoService.list();
        Map<Integer, MerchantInfo> merchantMap = merchantInfoList.stream().collect(Collectors.toMap(MerchantInfo::getId, e -> e));

        Map<Integer, List<DishesInfo>> merchantDishesMap = dishesInfos.stream()
                .filter(dishes -> dishes.getMerchantId() != null)
                .collect(Collectors.groupingBy(DishesInfo::getMerchantId));

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> merchantList = new ArrayList<>();

        for (MerchantInfo merchant : merchantInfoList) {
            Map<String, Object> merchantData = new HashMap<>();
            merchantData.put("merchant", merchant);
            merchantData.put("dishes", merchantDishesMap.getOrDefault(merchant.getId(), new ArrayList<>()));
            merchantList.add(merchantData);
        }

        result.put("merchants", merchantList);
        return R.ok(result);
    }

    /**
     * 根据商家获取餐品信息
     *
     * @param merchantUserId 商家ID
     * @return 结果
     */
    @GetMapping("/selectDishesByMerchant/{merchantUserId}")
    public R selectDishesByMerchant(@PathVariable("merchantUserId") Integer merchantUserId) {
        // 获取所属商家
        MerchantInfo merchantInfo = merchantInfoService.getOne(Wrappers.<MerchantInfo>lambdaQuery().eq(MerchantInfo::getId, merchantUserId));
        if (merchantInfo == null) {
            return R.ok(Collections.emptyList());
        }
        return R.ok(dishesInfoService.list(Wrappers.<DishesInfo>lambdaQuery().eq(DishesInfo::getMerchantId, merchantInfo.getId()).eq(DishesInfo::getStatus, "1")));
    }

    /**
     * 获取ID获取餐品详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(dishesInfoService.getById(id));
    }

    /**
     * 获取餐品信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(dishesInfoService.list());
    }

    /**
     * 新增餐品信息
     *
     * @param dishesInfo 餐品信息
     * @return 结果
     */
    @PostMapping
    public R save(DishesInfo dishesInfo) {
        // 获取所属商家
        MerchantInfo merchantInfo = merchantInfoService.getOne(Wrappers.<MerchantInfo>lambdaQuery().eq(MerchantInfo::getUserId, dishesInfo.getMerchantId()));
        if (merchantInfo != null) {
            dishesInfo.setMerchantId(merchantInfo.getId());
        }
        dishesInfo.setCode("DIS-" + System.currentTimeMillis());
        dishesInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(dishesInfoService.save(dishesInfo));
    }

    /**
     * 修改餐品信息
     *
     * @param dishesInfo 餐品信息
     * @return 结果
     */
    @PutMapping
    public R edit(DishesInfo dishesInfo) {
        return R.ok(dishesInfoService.updateById(dishesInfo));
    }

    /**
     * 删除餐品信息
     *
     * @param ids ids
     * @return 餐品信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(dishesInfoService.removeByIds(ids));
    }
}
