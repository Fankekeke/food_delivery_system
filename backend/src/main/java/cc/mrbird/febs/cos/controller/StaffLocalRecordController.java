package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.MerchantInfo;
import cc.mrbird.febs.cos.entity.StaffInfo;
import cc.mrbird.febs.cos.entity.StaffLocalRecord;
import cc.mrbird.febs.cos.service.IMerchantInfoService;
import cc.mrbird.febs.cos.service.IStaffInfoService;
import cc.mrbird.febs.cos.service.IStaffLocalRecordService;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/staff-local-record")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StaffLocalRecordController {

    private final IStaffLocalRecordService staffLocalRecordService;

    private final IMerchantInfoService merchantInfoService;

    private final IStaffInfoService staffInfoService;

    /**
     * 查询商家员工信息
     *
     * @return 结果
     */
    @GetMapping("/queryStaffByMerchant")
    public R queryStaffByMerchant() {
        List<StaffInfo> staffInfoList = staffInfoService.list();
        if (CollectionUtil.isEmpty(staffInfoList)) {
            return R.ok(Collections.emptyList());
        }
        List<StaffLocalRecord> staffLocalRecordList = staffLocalRecordService.list();
        Map<Integer, StaffLocalRecord> staffLocalRecordMap = staffLocalRecordList.stream().collect(Collectors.toMap(StaffLocalRecord::getStaffId, v -> v));

        for (StaffInfo staffInfo : staffInfoList) {
            StaffLocalRecord staffLocalRecord = staffLocalRecordMap.get(staffInfo.getId());
            if (staffLocalRecord != null) {
                staffInfo.setLat(staffLocalRecord.getLat());
                staffInfo.setLng(staffLocalRecord.getLng());
            }
        }
        return R.ok(staffInfoList);
    }

    /**
     * 添加员工位置信息
     *
     * @param staffLocalRecord 员工位置信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public R addStaffLocalRecord(StaffLocalRecord staffLocalRecord) {
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, staffLocalRecord.getStaffId()));
        staffLocalRecord.setStaffId(staffInfo.getId());
        staffLocalRecord.setCreateDate(staffInfo.getCreateDate());
        staffLocalRecordService.remove(Wrappers.<StaffLocalRecord>lambdaQuery().eq(StaffLocalRecord::getStaffId, staffInfo.getId()));
        return R.ok(staffLocalRecordService.save(staffLocalRecord));
    }

}
