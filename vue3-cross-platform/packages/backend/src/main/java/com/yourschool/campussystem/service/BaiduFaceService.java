package com.yourschool.campussystem.service;

import java.util.Map;

/**
 * 百度AI云人脸识别服务接口
 */
public interface BaiduFaceService {

    /**
     * 人脸检测
     * @param imageBase64 人脸图片Base64编码
     * @return 检测结果
     */
    Map<String, Object> detectFace(String imageBase64);

    /**
     * 在线活体检测
     * @param imageBase64 人脸图片Base64编码
     * @return 活体检测结果
     */
    Map<String, Object> faceLiveness(String imageBase64);

    /**
     * 人脸比对
     * @param image1Base64 第一张人脸图片Base64编码
     * @param image2Base64 第二张人脸图片Base64编码（如身份证照片）
     * @return 比对结果
     */
    Map<String, Object> faceMatch(String image1Base64, String image2Base64);
}
