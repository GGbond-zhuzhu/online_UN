package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.entity.*;
import com.yourschool.campussystem.enums.ParttimeStatusEnum;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.*;
import com.yourschool.campussystem.service.CommonService;
import com.yourschool.campussystem.service.CompanyVerifyService;
import com.yourschool.campussystem.service.MerchantService;
import com.yourschool.campussystem.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 商户管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;
    private final DepositRecordMapper depositRecordMapper;
    private final ParttimeMapper parttimeMapper;
    private final ParttimeApplyMapper parttimeApplyMapper;
    private final CommonService commonService;
    private final CompanyVerifyService companyVerifyService;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public Map<String, Object> applyMerchant(Long userId, String merchantName, String contactName,
                                             String contactPhone, String contactEmail,
                                             MultipartFile businessLicense, MultipartFile legalIdCard,
                                             BigDecimal depositAmount, String merchantType, String businessScope) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 检查是否已经申请过商户
        LambdaQueryWrapper<Merchant> merchantQuery = new LambdaQueryWrapper<>();
        merchantQuery.eq(Merchant::getUserId, userId).eq(Merchant::getIsDeleted, 0);
        Merchant existingMerchant = merchantMapper.selectOne(merchantQuery);
        if (existingMerchant != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "您已经申请过商户，请勿重复申请");
        }

        // 上传营业执照文件
        String businessLicenseUrl = null;
        if (businessLicense != null && !businessLicense.isEmpty()) {
            try {
                Map<String, Object> uploadResult = commonService.uploadFile(businessLicense);
                businessLicenseUrl = (String) uploadResult.get("url");
            } catch (Exception e) {
                log.error("上传营业执照失败", e);
                throw new BusinessException(ErrorCode.BAD_REQUEST, "营业执照上传失败");
            }
        }

        // 上传法人身份证文件
        String legalIdCardUrl = null;
        if (legalIdCard != null && !legalIdCard.isEmpty()) {
            try {
                Map<String, Object> uploadResult = commonService.uploadFile(legalIdCard);
                legalIdCardUrl = (String) uploadResult.get("url");
            } catch (Exception e) {
                log.error("上传法人身份证失败", e);
                throw new BusinessException(ErrorCode.BAD_REQUEST, "法人身份证上传失败");
            }
        }

        // 创建商户申请记录
        Merchant merchant = new Merchant();
        merchant.setUserId(userId);
        merchant.setMerchantName(merchantName);
        merchant.setMerchantType(merchantType);
        merchant.setBusinessScope(businessScope);
        merchant.setContactName(contactName);
        merchant.setContactPhone(contactPhone);
        merchant.setContactEmail(contactEmail);
        merchant.setBusinessLicenseUrl(businessLicenseUrl);
        merchant.setLegalIdCardUrl(legalIdCardUrl);
        merchant.setDepositAmount(depositAmount);
        merchant.setStatus("PENDING");
        merchant.setCreditScore(100); // 初始信用分
        merchant.setCreateTime(LocalDateTime.now());
        merchant.setUpdateTime(LocalDateTime.now());

        merchantMapper.insert(merchant);

        Map<String, Object> response = new HashMap<>();
        response.put("applyId", merchant.getId());
        response.put("status", merchant.getStatus());
        response.put("message", "商户入驻申请已提交，等待资质审核");
        response.put("depositAmount", depositAmount);
        response.put("applyTime", merchant.getCreateTime());

        log.info("商户入驻申请: userId={}, merchantId={}, merchantName={}, depositAmount={}", 
                userId, merchant.getId(), merchantName, depositAmount);

        return response;
    }

    @Override
    public Map<String, Object> verifyCompany(String creditCode, String companyName) {
        log.info("开始公司资格校验: creditCode={}, companyName={}", creditCode, companyName);
        
        // 调用企业信息查询服务
        Map<String, Object> companyInfo = companyVerifyService.queryCompanyInfo(creditCode, companyName);
        
        // 构建返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", companyInfo.get("isValid"));
        response.put("companyName", companyInfo.get("companyName"));
        response.put("creditCode", companyInfo.get("creditCode"));
        response.put("legalPerson", companyInfo.get("legalPerson"));
        response.put("registeredCapital", companyInfo.get("registeredCapital"));
        response.put("establishDate", companyInfo.get("establishDate"));
        response.put("status", companyInfo.get("status"));
        response.put("companyType", companyInfo.get("companyType"));
        response.put("address", companyInfo.get("address"));
        response.put("verifyTime", LocalDateTime.now());
        response.put("message", Boolean.TRUE.equals(companyInfo.get("isValid")) ? 
                "公司资格校验通过" : "公司资格校验未通过");

        log.info("公司资格校验完成: creditCode={}, isValid={}", creditCode, companyInfo.get("isValid"));
        return response;
    }

    @Override
    public Map<String, Object> getMerchantInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 从merchant表查询商户详细信息
        LambdaQueryWrapper<Merchant> merchantQuery = new LambdaQueryWrapper<>();
        merchantQuery.eq(Merchant::getUserId, userId).eq(Merchant::getIsDeleted, 0);
        Merchant merchantEntity = merchantMapper.selectOne(merchantQuery);
        
        if (merchantEntity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商户信息不存在，请先申请商户入驻");
        }

        // 查询最新的保证金记录
        LambdaQueryWrapper<DepositRecord> depositQuery = new LambdaQueryWrapper<>();
        depositQuery.eq(DepositRecord::getMerchantId, merchantEntity.getId())
                .orderByDesc(DepositRecord::getCreateTime)
                .last("LIMIT 1");
        DepositRecord latestDeposit = depositRecordMapper.selectOne(depositQuery);

        // 统计发布的兼职数
        LambdaQueryWrapper<Parttime> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Parttime::getPublisherId, userId)
                .eq(Parttime::getIsDeleted, 0);
        long publishedJobsCount = parttimeMapper.selectCount(queryWrapper);

        Map<String, Object> merchant = new HashMap<>();
        merchant.put("merchantId", merchantEntity.getId());
        merchant.put("merchantName", merchantEntity.getMerchantName());
        merchant.put("merchantType", merchantEntity.getMerchantType());
        merchant.put("status", merchantEntity.getStatus());
        merchant.put("depositAmount", merchantEntity.getDepositAmount());
        merchant.put("depositStatus", latestDeposit != null ? latestDeposit.getPayStatus() : "UNPAID");
        merchant.put("creditScore", merchantEntity.getCreditScore());
        merchant.put("publishedJobsCount", publishedJobsCount);
        merchant.put("approvedTime", merchantEntity.getApprovedTime());
        merchant.put("createTime", merchantEntity.getCreateTime());

        return merchant;
    }

    @Override
    @Transactional
    public Map<String, Object> payDeposit(Long userId, BigDecimal amount, String payMethod) {
        // 查询商户信息
        LambdaQueryWrapper<Merchant> merchantQuery = new LambdaQueryWrapper<>();
        merchantQuery.eq(Merchant::getUserId, userId).eq(Merchant::getIsDeleted, 0);
        Merchant merchant = merchantMapper.selectOne(merchantQuery);
        
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商户信息不存在");
        }

        // 生成订单号
        String orderId = "DEPOSIT_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);

        // 创建保证金记录（状态为PENDING，等待支付）
        DepositRecord depositRecord = new DepositRecord();
        depositRecord.setMerchantId(merchant.getId());
        depositRecord.setOrderId(orderId);
        depositRecord.setAmount(amount);
        depositRecord.setPayMethod(payMethod);
        depositRecord.setPayStatus("PENDING"); // 等待支付
        depositRecord.setCreateTime(LocalDateTime.now());
        depositRecord.setUpdateTime(LocalDateTime.now());

        depositRecordMapper.insert(depositRecord);

        // 调用支付服务创建支付订单并生成二维码
        Map<String, Object> paymentInfo = paymentService.createPayment(orderId, amount, payMethod);

        // 构建返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("amount", amount);
        response.put("payMethod", payMethod);
        response.put("status", "PENDING");
        response.put("qrCodeUrl", paymentInfo.get("qrCodeUrl"));
        response.put("qrCodeContent", paymentInfo.get("qrCodeContent"));
        response.put("expireTime", paymentInfo.get("expireTime"));
        response.put("message", paymentInfo.get("message"));
        response.put("createTime", LocalDateTime.now());

        log.info("创建保证金支付订单: userId={}, merchantId={}, orderId={}, amount={}, payMethod={}", 
                userId, merchant.getId(), orderId, amount, payMethod);

        return response;
    }

    @Override
    public Map<String, Object> refreshPaymentQrCode(String orderId) {
        // 验证订单是否存在
        LambdaQueryWrapper<DepositRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepositRecord::getOrderId, orderId);
        DepositRecord depositRecord = depositRecordMapper.selectOne(queryWrapper);
        
        if (depositRecord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        // 检查订单状态
        if (!"PENDING".equals(depositRecord.getPayStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "订单已支付或已取消，无法刷新二维码");
        }

        // 调用支付服务刷新二维码
        Map<String, Object> paymentInfo = paymentService.refreshPaymentQrCode(orderId);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("qrCodeUrl", paymentInfo.get("qrCodeUrl"));
        response.put("qrCodeContent", paymentInfo.get("qrCodeContent"));
        response.put("expireTime", paymentInfo.get("expireTime"));
        response.put("message", paymentInfo.get("message"));

        log.info("刷新支付二维码: orderId={}", orderId);
        return response;
    }

    @Override
    public Map<String, Object> queryPaymentStatus(String orderId) {
        // 查询保证金记录
        LambdaQueryWrapper<DepositRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepositRecord::getOrderId, orderId);
        DepositRecord depositRecord = depositRecordMapper.selectOne(queryWrapper);
        
        if (depositRecord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        // 调用支付服务查询状态
        Map<String, Object> paymentStatus = paymentService.queryPaymentStatus(orderId);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", depositRecord.getPayStatus());
        response.put("amount", depositRecord.getAmount());
        response.put("payMethod", depositRecord.getPayMethod());
        response.put("createTime", depositRecord.getCreateTime());
        response.put("payTime", depositRecord.getPayTime());
        response.put("isExpired", paymentStatus.get("isExpired"));
        response.put("expireTime", paymentStatus.get("expireTime"));

        return response;
    }

    @Override
    public Map<String, Object> getDepositRecords(Long userId, Integer page, Integer size) {
        // 查询商户信息
        LambdaQueryWrapper<Merchant> merchantQuery = new LambdaQueryWrapper<>();
        merchantQuery.eq(Merchant::getUserId, userId).eq(Merchant::getIsDeleted, 0);
        Merchant merchant = merchantMapper.selectOne(merchantQuery);
        
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商户信息不存在");
        }

        // 查询保证金记录
        LambdaQueryWrapper<DepositRecord> depositQuery = new LambdaQueryWrapper<>();
        depositQuery.eq(DepositRecord::getMerchantId, merchant.getId())
                .orderByDesc(DepositRecord::getCreateTime);

        Page<DepositRecord> pageObj = new Page<>(page, size);
        Page<DepositRecord> result = depositRecordMapper.selectPage(pageObj, depositQuery);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(record -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", record.getId());
                    item.put("orderId", record.getOrderId());
                    item.put("amount", record.getAmount());
                    item.put("payMethod", record.getPayMethod());
                    item.put("payStatus", record.getPayStatus());
                    item.put("payTime", record.getPayTime());
                    item.put("refundTime", record.getRefundTime());
                    item.put("refundReason", record.getRefundReason());
                    item.put("createTime", record.getCreateTime());
                    return item;
                })
                .collect(Collectors.toList());

        // 计算当前总保证金（已支付且未退款的记录）
        LambdaQueryWrapper<DepositRecord> paidQuery = new LambdaQueryWrapper<>();
        paidQuery.eq(DepositRecord::getMerchantId, merchant.getId())
                .eq(DepositRecord::getPayStatus, "PAID");
        List<DepositRecord> paidRecords = depositRecordMapper.selectList(paidQuery);
        BigDecimal currentDeposit = paidRecords.stream()
                .map(DepositRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", result.getTotal());
        response.put("currentDeposit", currentDeposit);

        return response;
    }

    @Override
    public Map<String, Object> getMerchantJobs(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Parttime> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Parttime::getPublisherId, userId)
                .eq(Parttime::getIsDeleted, 0)
                .orderByDesc(Parttime::getCreateTime);

        Page<Parttime> pageObj = new Page<>(page, size);
        Page<Parttime> result = parttimeMapper.selectPage(pageObj, queryWrapper);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(parttime -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", parttime.getId());
                    item.put("title", parttime.getTitle());
                    item.put("salaryPerHour", parttime.getSalaryPerHour());
                    item.put("recruitCount", parttime.getRecruitCount());
                    item.put("appliedCount", parttime.getAppliedCount());
                    item.put("status", parttime.getStatus() != null ? parttime.getStatus().name() : null);
                    item.put("workStartTime", parttime.getWorkStartTime());
                    item.put("workEndTime", parttime.getWorkEndTime());
                    item.put("createTime", parttime.getCreateTime());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", result.getTotal());

        return response;
    }

    @Override
    public Map<String, Object> getMerchantStatistics(Long userId) {
        // 查询商户信息
        LambdaQueryWrapper<Merchant> merchantQuery = new LambdaQueryWrapper<>();
        merchantQuery.eq(Merchant::getUserId, userId).eq(Merchant::getIsDeleted, 0);
        Merchant merchant = merchantMapper.selectOne(merchantQuery);
        
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商户信息不存在");
        }

        // 统计发布的兼职
        LambdaQueryWrapper<Parttime> parttimeQuery = new LambdaQueryWrapper<>();
        parttimeQuery.eq(Parttime::getPublisherId, userId)
                .eq(Parttime::getIsDeleted, 0);
        long totalJobs = parttimeMapper.selectCount(parttimeQuery);

        parttimeQuery.eq(Parttime::getStatus, ParttimeStatusEnum.RECRUITING);
        long activeJobs = parttimeMapper.selectCount(parttimeQuery);

        // 统计报名数
        LambdaQueryWrapper<ParttimeApply> applyQuery = new LambdaQueryWrapper<>();
        applyQuery.in(ParttimeApply::getParttimeId,
                parttimeMapper.selectList(new LambdaQueryWrapper<Parttime>()
                        .eq(Parttime::getPublisherId, userId)
                        .eq(Parttime::getIsDeleted, 0)
                        .select(Parttime::getId))
                        .stream()
                        .map(Parttime::getId)
                        .collect(Collectors.toList()));
        long totalApplications = parttimeApplyMapper.selectCount(applyQuery);

        applyQuery.eq(ParttimeApply::getStatus, "APPROVED");
        long approvedApplications = parttimeApplyMapper.selectCount(applyQuery);

        // TODO: 计算总收入（需要从订单表或支付记录表查询）
        BigDecimal totalRevenue = BigDecimal.ZERO;

        // 从商户表获取信用分
        int creditScore = merchant.getCreditScore() != null ? merchant.getCreditScore() : 100;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJobs", totalJobs);
        stats.put("activeJobs", activeJobs);
        stats.put("totalApplications", totalApplications);
        stats.put("approvedApplications", approvedApplications);
        stats.put("totalRevenue", totalRevenue);
        stats.put("creditScore", creditScore);

        return stats;
    }
}
