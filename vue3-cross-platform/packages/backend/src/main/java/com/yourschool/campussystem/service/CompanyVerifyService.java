package com.yourschool.campussystem.service;

import java.util.Map;

/**
 * 企业信息验证服务接口
 */
public interface CompanyVerifyService {

    /**
     * 根据统一社会信用代码查询企业信息
     * @param creditCode 统一社会信用代码
     * @param companyName 公司名称（用于验证）
     * @return 企业信息
     */
    Map<String, Object> queryCompanyInfo(String creditCode, String companyName);
}
