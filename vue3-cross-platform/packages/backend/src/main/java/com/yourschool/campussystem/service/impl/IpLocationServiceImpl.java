package com.yourschool.campussystem.service.impl;

import com.yourschool.campussystem.service.IpLocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * IP地理位置解析服务实现类（高德地图API）
 */
@Slf4j
@Service
public class IpLocationServiceImpl implements IpLocationService {

    @Value("${amap.ip-location.key:}")
    private String apiKey;

    @Value("${amap.ip-location.secret:}")
    private String secretKey;

    private static final String AMAP_IP_LOCATION_URL = "https://restapi.amap.com/v3/ip";

    private final RestTemplate restTemplate;

    public IpLocationServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String getLocationByIp(String ip) {
        // 如果未配置API Key，返回null
        if (apiKey == null || apiKey.isEmpty()) {
            log.debug("高德地图API Key未配置，跳过IP地理位置解析");
            return null;
        }

        // 本地IP或内网IP，不进行解析
        if (ip == null || ip.isEmpty() || 
            ip.equals("127.0.0.1") || 
            ip.equals("localhost") ||
            ip.startsWith("192.168.") ||
            ip.startsWith("10.") ||
            ip.startsWith("172.16.") ||
            ip.startsWith("172.17.") ||
            ip.startsWith("172.18.") ||
            ip.startsWith("172.19.") ||
            ip.startsWith("172.20.") ||
            ip.startsWith("172.21.") ||
            ip.startsWith("172.22.") ||
            ip.startsWith("172.23.") ||
            ip.startsWith("172.24.") ||
            ip.startsWith("172.25.") ||
            ip.startsWith("172.26.") ||
            ip.startsWith("172.27.") ||
            ip.startsWith("172.28.") ||
            ip.startsWith("172.29.") ||
            ip.startsWith("172.30.") ||
            ip.startsWith("172.31.")) {
            log.debug("IP地址为本地或内网地址，跳过地理位置解析: {}", ip);
            return null;
        }

        try {
            // 构建请求URL
            String url = AMAP_IP_LOCATION_URL + "?key=" + apiKey + "&ip=" + ip;
            
            // 如果有Secret Key，需要计算签名（高德地图API签名算法）
            if (secretKey != null && !secretKey.isEmpty()) {
                // 高德地图签名计算：MD5(请求参数+SecretKey)
                String sign = calculateSign(ip, secretKey);
                url += "&sig=" + sign;
            }

            log.debug("调用高德地图IP定位API: {}", url.replace(apiKey, "***"));

            // 发送HTTP请求
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                log.warn("高德地图IP定位API返回空响应");
                return null;
            }

            // 检查返回状态
            String status = String.valueOf(response.get("status"));
            if (!"1".equals(status)) {
                String info = String.valueOf(response.get("info"));
                log.warn("高德地图IP定位API返回错误: status={}, info={}", status, info);
                return null;
            }

            // 解析地理位置信息
            String province = (String) response.get("province");
            String city = (String) response.get("city");

            // 构建地理位置字符串
            StringBuilder location = new StringBuilder();
            if (province != null && !province.isEmpty() && !province.equals("[]")) {
                location.append(province);
            }
            if (city != null && !city.isEmpty() && !city.equals("[]") && !city.equals(province)) {
                if (location.length() > 0) {
                    location.append(" ");
                }
                location.append(city);
            }

            String result = location.length() > 0 ? location.toString() : null;
            log.debug("IP地理位置解析成功: ip={}, location={}", ip, result);
            return result;

        } catch (Exception e) {
            log.error("调用高德地图IP定位API异常: ip={}", ip, e);
            // IP定位失败不影响登录流程，返回null
            return null;
        }
    }

    /**
     * 计算高德地图API签名
     * 签名算法：MD5(请求参数（按key排序）+ SecretKey)
     */
    private String calculateSign(String ip, String secretKey) {
        try {
            // 构建参数字符串（按key排序）
            String params = "ip=" + ip + "&key=" + apiKey;
            
            // MD5加密
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest((params + secretKey).getBytes("UTF-8"));
            
            // 转换为16进制字符串
            StringBuilder sign = new StringBuilder();
            for (byte b : bytes) {
                sign.append(String.format("%02x", b));
            }
            
            return sign.toString();
        } catch (Exception e) {
            log.error("计算高德地图API签名失败", e);
            return "";
        }
    }
}
