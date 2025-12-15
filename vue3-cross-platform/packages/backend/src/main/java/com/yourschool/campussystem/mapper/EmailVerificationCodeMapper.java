package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.EmailVerificationCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮箱验证码Mapper接口
 */
@Mapper
public interface EmailVerificationCodeMapper extends BaseMapper<EmailVerificationCode> {
}
