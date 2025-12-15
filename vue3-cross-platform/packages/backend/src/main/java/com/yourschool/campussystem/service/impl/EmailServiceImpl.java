package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.entity.EmailVerificationCode;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.EmailVerificationCodeMapper;
import com.yourschool.campussystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 邮件服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailVerificationCodeMapper emailVerificationCodeMapper;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${spring.mail.enabled:false}")
    private boolean mailEnabled;

    // 验证码有效期（分钟）
    private static final int CODE_EXPIRE_MINUTES = 10;

    @Override
    public Map<String, Object> sendVerificationCode(String email, String codeType) {
        // 验证邮箱格式
        if (!isValidEmail(email)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "邮箱格式不正确");
        }

        // 生成6位数字验证码
        String code = generateCode();
        String codeId = UUID.randomUUID().toString();

        // 保存验证码到数据库
        EmailVerificationCode verificationCode = new EmailVerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setCodeId(codeId);
        verificationCode.setType(codeType != null ? codeType : "LOGIN");
        verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));
        verificationCode.setIsUsed(0);
        verificationCode.setCreateTime(LocalDateTime.now());

        emailVerificationCodeMapper.insert(verificationCode);

        // 发送邮件（如果邮件服务已配置）
        boolean emailSent = false;
        if (mailEnabled && StringUtils.hasText(fromEmail)) {
            try {
                sendEmail(email, code, codeType);
                emailSent = true;
                log.info("邮箱验证码发送成功: email={}, codeId={}", email, codeId);
            } catch (Exception e) {
                log.error("发送邮件失败: email={}", email, e);
                // 邮件发送失败不影响验证码生成，但记录日志
            }
        } else {
            log.warn("邮件服务未配置，验证码仅保存到数据库（模拟模式）: email={}, code={}, codeId={}", email, code, codeId);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("codeId", codeId);
        response.put("expireTime", CODE_EXPIRE_MINUTES * 60); // 返回秒数
        
        // 模拟模式：如果邮件未发送，在响应中返回验证码（仅开发/测试环境）
        // 生产环境应通过环境变量控制是否返回验证码
        if (!emailSent) {
            response.put("code", code); // 模拟模式返回验证码，方便测试
            log.info("模拟模式：验证码已返回在响应中，请勿在生产环境使用: email={}, code={}", email, code);
        }

        return response;
    }

    @Override
    public boolean verifyCode(String email, String code, String codeId) {
        // 查询验证码
        LambdaQueryWrapper<EmailVerificationCode> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmailVerificationCode::getEmail, email)
                .eq(EmailVerificationCode::getCode, code)
                .eq(EmailVerificationCode::getCodeId, codeId)
                .eq(EmailVerificationCode::getIsUsed, 0)
                .gt(EmailVerificationCode::getExpireTime, LocalDateTime.now())
                .orderByDesc(EmailVerificationCode::getCreateTime)
                .last("LIMIT 1");

        EmailVerificationCode verificationCode = emailVerificationCodeMapper.selectOne(queryWrapper);

        if (verificationCode == null) {
            return false;
        }

        // 标记为已使用
        verificationCode.setIsUsed(1);
        emailVerificationCodeMapper.updateById(verificationCode);

        return true;
    }

    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成100000-999999之间的6位数
        return String.valueOf(code);
    }

    /**
     * 验证邮箱格式
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // 简单的邮箱格式验证
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * 发送邮件
     */
    private void sendEmail(String toEmail, String code, String codeType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        
        if ("RESET_PASSWORD".equals(codeType)) {
            message.setSubject("【上大学Online】密码重置验证码");
            message.setText("您的密码重置验证码是：" + code + "，有效期10分钟。请勿泄露给他人。");
        } else {
            message.setSubject("【上大学Online】登录验证码");
            message.setText("您的登录验证码是：" + code + "，有效期10分钟。请勿泄露给他人。");
        }

        mailSender.send(message);
    }
}
