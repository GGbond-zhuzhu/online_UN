package com.yourschool.campussystem.service.impl;

import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.service.CompanyVerifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 企业信息验证服务实现类（天眼查API）
 */
@Slf4j
@Service
public class CompanyVerifyServiceImpl implements CompanyVerifyService {

    @Value("${tianyancha.api.token:}")
    private String apiToken;

    @Value("${tianyancha.api.url:https://open.api.tianyancha.com/services/v3/open/baseInfoV3}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public CompanyVerifyServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Map<String, Object> queryCompanyInfo(String creditCode, String companyName) {
        // 如果未配置API Token，返回模拟数据
        if (apiToken == null || apiToken.isEmpty()) {
            log.warn("天眼查API Token未配置，返回模拟数据");
            return createMockResponse(creditCode, companyName);
        }

        try {
            // 构建请求URL（天眼查API支持通过统一社会信用代码查询）
            String url = apiUrl + "?keyword=" + java.net.URLEncoder.encode(creditCode, "UTF-8");
            
            // 设置请求头（天眼查API使用token作为Authorization）
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", apiToken);  // 直接使用token，不需要Bearer前缀
            headers.set("Content-Type", "application/json");
            
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            log.debug("调用天眼查企业信息查询API: creditCode={}, url={}", creditCode, url.replace(apiToken, "***"));

            // 发送HTTP请求
            @SuppressWarnings("unchecked")
            org.springframework.http.ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, 
                    org.springframework.http.HttpMethod.GET, 
                    entity, 
                    (Class<Map<String, Object>>) (Class<?>) Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                // 解析返回结果
                return parseResponse(responseBody, creditCode, companyName);
            } else {
                log.error("天眼查API调用失败: status={}", response.getStatusCode());
                throw new BusinessException(ErrorCode.BAD_REQUEST, "企业信息查询失败");
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用天眼查企业信息查询API异常: creditCode={}", creditCode, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "企业信息查询服务异常: " + e.getMessage());
        }
    }

    /**
     * 解析API返回结果
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseResponse(Map<String, Object> responseBody, String creditCode, String companyName) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查返回状态
        Object stateObj = responseBody.get("state");
        if (stateObj == null || !"200".equals(String.valueOf(stateObj))) {
            String message = String.valueOf(responseBody.get("message"));
            log.warn("天眼查API返回错误: state={}, message={}", stateObj, message);
            throw new BusinessException(ErrorCode.BAD_REQUEST, "企业信息查询失败: " + message);
        }

        // 获取企业信息
        Map<String, Object> data = (Map<String, Object>) responseBody.get("result");
        if (data == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "未找到企业信息");
        }

        // 验证统一社会信用代码
        String returnedCreditCode = (String) data.get("creditCode");
        if (returnedCreditCode == null || !returnedCreditCode.equals(creditCode)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "统一社会信用代码不匹配");
        }

        // 验证公司名称（允许部分匹配）
        String returnedCompanyName = (String) data.get("name");
        if (returnedCompanyName == null || 
            (!returnedCompanyName.equals(companyName) && !returnedCompanyName.contains(companyName))) {
            log.warn("公司名称不匹配: 输入={}, 查询结果={}", companyName, returnedCompanyName);
            // 名称不完全匹配时给出警告，但不阻止验证
        }

        // 构建返回结果
        result.put("isValid", true);
        result.put("creditCode", returnedCreditCode);
        result.put("companyName", returnedCompanyName);
        result.put("legalPerson", data.get("legalPersonName"));
        result.put("registeredCapital", data.get("regCapital"));
        result.put("establishDate", data.get("estiblishTime"));
        result.put("status", data.get("regStatus"));
        result.put("companyType", data.get("companyOrgType"));
        result.put("address", data.get("regLocation"));
        result.put("scope", data.get("scope"));
        
        // 保存完整的企业信息
        result.put("companyInfo", data);

        log.info("企业信息查询成功: creditCode={}, companyName={}", creditCode, returnedCompanyName);
        return result;
    }

    /**
     * 创建模拟响应（用于测试或未配置API时）
     */
    private Map<String, Object> createMockResponse(String creditCode, String companyName) {
        Map<String, Object> result = new HashMap<>();
        result.put("isValid", true);
        result.put("creditCode", creditCode);
        result.put("companyName", companyName);
        result.put("legalPerson", "模拟法人");
        result.put("registeredCapital", "1000万元");
        result.put("establishDate", "2020-01-01");
        result.put("status", "存续");
        result.put("message", "当前为模拟数据，请配置API Token后使用真实数据");
        return result;
    }
}
