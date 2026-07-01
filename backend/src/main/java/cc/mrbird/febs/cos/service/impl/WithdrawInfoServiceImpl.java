package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.dao.WithdrawInfoMapper;
import cc.mrbird.febs.cos.entity.StaffInfo;
import cc.mrbird.febs.cos.entity.WithdrawInfo;
import cc.mrbird.febs.cos.service.IStaffInfoService;
import cc.mrbird.febs.cos.service.IWithdrawInfoService;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class WithdrawInfoServiceImpl extends ServiceImpl<WithdrawInfoMapper, WithdrawInfo> implements IWithdrawInfoService {

    @Resource
    private IStaffInfoService staffInfoService;

    /**
     * 分页获取提现记录信息
     *
     * @param page         分页对象
     * @param withdrawInfo 提现记录信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectWithdrawPage(Page<WithdrawInfo> page, WithdrawInfo withdrawInfo) {
        return baseMapper.selectWithdrawPage(page, withdrawInfo);
    }

    /**
     * 管理员审核提现申请
     *
     * @param withdrawInfo 提现记录
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditWithdraw(WithdrawInfo withdrawInfo) throws FebsException {
        // 服务人员信息
        StaffInfo staffInfo = staffInfoService.getById(withdrawInfo.getStaffId());

        if (withdrawInfo.getWithdrawPrice().compareTo(staffInfo.getPrice()) > 0) {
            throw new FebsException("服务人员余额不足");
        }
        // 更新服务人员余额
        if ("1".equals(withdrawInfo.getStatus())) {
            BigDecimal staffPrice = NumberUtil.sub(staffInfo.getPrice(), withdrawInfo.getWithdrawPrice());
            staffInfo.setPrice(staffPrice);
            staffInfoService.updateById(staffInfo);
        }
        return this.updateById(withdrawInfo);
    }
}
