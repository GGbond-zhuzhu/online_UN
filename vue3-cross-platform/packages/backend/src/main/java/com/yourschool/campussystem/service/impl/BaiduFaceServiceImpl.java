package com.yourschool.campussystem.service.impl;

import com.baidu.aip.face.AipFace;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.service.BaiduFaceService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度AI云人脸识别服务实现类
 */
@Slf4j
@Service
public class BaiduFaceServiceImpl implements BaiduFaceService {

    @Value("${baidu.ai.app-id}")
    private String appId;

    @Value("${baidu.ai.api-key}")
    private String apiKey;

    @Value("${baidu.ai.secret-key}")
    private String secretKey;

    private AipFace client;

    /**
     * 初始化百度AI云客户端
     */
    @PostConstruct
    public void init() {
        // 初始化AipFace客户端
        client = new AipFace(appId, apiKey, secretKey);
        
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        
        log.info("百度AI云人脸识别客户端初始化成功，AppID: {}", appId);
    }

    @Override
    public Map<String, Object> detectFace(String imageBase64) {
        try {
            // 调用人脸检测接口
            HashMap<String, Object> options = new HashMap<>();
            options.put("face_field", "age,beauty,expression,face_shape,gender,glasses,landmark,landmark72,landmark150,quality,eye_status,emotion,face_type,spoofing");
            options.put("max_face_num", "1");
            options.put("face_type", "LIVE");
            
            JSONObject result = client.detect(imageBase64, "BASE64", options);
            
            // 解析结果
            Map<String, Object> response = new HashMap<>();
            
            if (result.has("error_code") && result.getInt("error_code") != 0) {
                String errorMsg = result.optString("error_msg", "人脸检测失败");
                log.error("百度AI云人脸检测失败: {}", errorMsg);
                throw new BusinessException(ErrorCode.BAD_REQUEST, "人脸检测失败: " + errorMsg);
            }
            
            // 检查是否检测到人脸
            if (result.has("result") && result.getJSONObject("result").has("face_num")) {
                int faceNum = result.getJSONObject("result").getInt("face_num");
                if (faceNum == 0) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "未检测到人脸，请确保照片中有人脸");
                }
                
                response.put("faceNum", faceNum);
                response.put("faceList", result.getJSONObject("result").optJSONArray("face_list"));
                response.put("success", true);
            } else {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "人脸检测结果异常");
            }
            
            return response;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("百度AI云人脸检测异常", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "人脸检测服务异常: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> faceLiveness(String imageBase64) {
        try {
            // 调用在线活体检测接口（faceverify需要传入图片列表）
            ArrayList<com.baidu.aip.face.FaceVerifyRequest> images = new ArrayList<>();
            com.baidu.aip.face.FaceVerifyRequest request = new com.baidu.aip.face.FaceVerifyRequest(imageBase64, "BASE64");
            images.add(request);
            
            // 调用faceverify接口
            JSONObject result = client.faceverify(images);
            
            // 解析结果
            Map<String, Object> response = new HashMap<>();
            
            if (result.has("error_code") && result.getInt("error_code") != 0) {
                String errorMsg = result.optString("error_msg", "活体检测失败");
                log.error("百度AI云活体检测失败: {}", errorMsg);
                throw new BusinessException(ErrorCode.BAD_REQUEST, "活体检测失败: " + errorMsg);
            }
            
            // 解析活体检测结果
            if (result.has("result") && result.getJSONObject("result").has("face_list")) {
                org.json.JSONArray faceList = result.getJSONObject("result").getJSONArray("face_list");
                if (faceList.length() > 0) {
                    org.json.JSONObject faceInfo = faceList.getJSONObject(0);
                    // 百度AI云的faceverify返回的是face_liveness字段
                    double livenessScore = faceInfo.optDouble("face_liveness", 0.0);
                    boolean isAlive = livenessScore > 0.8; // 活体阈值，可根据实际情况调整
                    
                    response.put("livenessScore", livenessScore);
                    response.put("isAlive", isAlive);
                    response.put("faceList", faceList);
                    response.put("success", true);
                    
                    if (!isAlive) {
                        throw new BusinessException(ErrorCode.BAD_REQUEST, 
                            String.format("活体检测未通过，活体分数: %.2f（阈值: 0.8）", livenessScore));
                    }
                } else {
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "未检测到人脸");
                }
            } else {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "活体检测结果异常");
            }
            
            return response;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("百度AI云活体检测异常", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "活体检测服务异常: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> faceMatch(String image1Base64, String image2Base64) {
        try {
            // 调用人脸比对接口（match需要传入MatchRequest列表）
            ArrayList<com.baidu.aip.face.MatchRequest> requests = new ArrayList<>();
            com.baidu.aip.face.MatchRequest request1 = new com.baidu.aip.face.MatchRequest(image1Base64, "BASE64");
            com.baidu.aip.face.MatchRequest request2 = new com.baidu.aip.face.MatchRequest(image2Base64, "BASE64");
            requests.add(request1);
            requests.add(request2);
            
            JSONObject result = client.match(requests);
            
            // 解析结果
            Map<String, Object> response = new HashMap<>();
            
            if (result.has("error_code") && result.getInt("error_code") != 0) {
                String errorMsg = result.optString("error_msg", "人脸比对失败");
                log.error("百度AI云人脸比对失败: {}", errorMsg);
                throw new BusinessException(ErrorCode.BAD_REQUEST, "人脸比对失败: " + errorMsg);
            }
            
            // 解析比对结果
            if (result.has("result") && result.getJSONObject("result").has("score")) {
                double score = result.getJSONObject("result").getDouble("score");
                boolean isMatch = score > 80.0; // 相似度阈值，可根据实际情况调整
                
                response.put("score", score);
                response.put("isMatch", isMatch);
                response.put("success", true);
                
                if (!isMatch) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST, 
                        String.format("人脸比对未通过，相似度分数: %.2f（阈值: 80.0）", score));
                }
            } else {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "人脸比对结果异常");
            }
            
            return response;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("百度AI云人脸比对异常", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "人脸比对服务异常: " + e.getMessage());
        }
    }
}
