package com.yourschool.campussystem.service;

/**
 * IP地理位置解析服务接口
 */
public interface IpLocationService {

    /**
     * 根据IP地址获取地理位置信息
     * @param ip IP地址
     * @return 地理位置信息（省份、城市、区县、详细地址）
     */
    String getLocationByIp(String ip);
}
