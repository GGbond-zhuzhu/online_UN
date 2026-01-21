# 语音消息功能后端实现指南

## 概述
本文档说明如何在后端实现语音消息功能，包括语音文件上传、语音消息发送和语音转文字功能。

---

## 一、数据库修改

### 1.1 修改 `chat_message` 表

需要在 `chat_message` 表中添加以下字段：

```sql
-- 添加语音文件URL字段
ALTER TABLE `chat_message` 
ADD COLUMN `voice_url` VARCHAR(500) DEFAULT NULL COMMENT '语音文件URL（VOICE类型消息使用）' AFTER `file_url`;

-- 添加语音时长字段（单位：毫秒）
ALTER TABLE `chat_message` 
ADD COLUMN `duration` INT DEFAULT NULL COMMENT '语音时长（毫秒，VOICE类型消息使用）' AFTER `voice_url`;
```

### 1.2 更新表结构说明

- `voice_url`: 存储语音文件的访问URL
- `duration`: 存储语音时长，用于前端显示（如：00:15）

---

## 二、实体类修改

### 2.1 修改 `ChatMessage.java`

在 `ChatMessage` 实体类中添加以下字段：

```java
@TableField("voice_url")
private String voiceUrl;  // 语音文件URL

@TableField("duration")
private Integer duration;  // 语音时长（毫秒）
```

---

## 三、Controller 层修改

### 3.1 修改 `ChatController.java`

修改 `sendMessage` 方法，添加 `duration` 参数支持：

```java
@Operation(summary = "发送消息", description = "发送聊天消息，支持文本、图片、文件、语音")
@PostMapping("/send")
public ApiResponse<Map<String, Object>> sendMessage(
        HttpServletRequest request,
        @Parameter(description = "目标用户ID", required = true)
        @RequestParam Long targetUserId,
        @Parameter(description = "消息内容", required = true)
        @RequestParam String content,
        @Parameter(description = "消息类型（TEXT/IMAGE/FILE/VOICE）", example = "TEXT")
        @RequestParam(defaultValue = "TEXT") String type,
        @Parameter(description = "图片URL（如果是图片消息）")
        @RequestParam(required = false) String imageUrl,
        @Parameter(description = "文件URL（如果是文件消息）")
        @RequestParam(required = false) String fileUrl,
        @Parameter(description = "语音文件URL（如果是语音消息）")
        @RequestParam(required = false) String voiceUrl,
        @Parameter(description = "语音时长（毫秒，VOICE类型必填）")
        @RequestParam(required = false) Integer duration) {
    Long senderId = UserContextUtils.getUserIdRequired(request);
    Map<String, Object> response = chatService.sendMessage(
        senderId, targetUserId, content, type, imageUrl, fileUrl, voiceUrl, duration);
    return ApiResponse.success("发送成功", response);
}
```

### 3.2 添加语音文件上传接口（可选）

如果需要专门的语音上传接口，可以在 `CommonController` 中添加：

```java
@Operation(summary = "上传语音文件", description = "上传语音文件，返回文件URL")
@PostMapping("/upload/voice")
public ApiResponse<Map<String, Object>> uploadVoice(
        HttpServletRequest request,
        @Parameter(description = "语音文件", required = true)
        @RequestParam("file") MultipartFile file) {
    Long userId = UserContextUtils.getUserIdRequired(request);
    
    // 验证文件类型（mp3, wav, m4a等）
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "文件名不能为空");
    }
    
    String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
    if (!Arrays.asList("mp3", "wav", "m4a", "amr", "aac").contains(extension)) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "不支持的语音文件格式");
    }
    
    // 验证文件大小（最大10MB）
    if (file.getSize() > 10 * 1024 * 1024) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "语音文件大小不能超过10MB");
    }
    
    // 调用通用文件上传服务
    Map<String, Object> result = commonService.uploadFile(file);
    
    // 可以在这里添加语音文件处理逻辑（如格式转换、压缩等）
    
    return ApiResponse.success("上传成功", result);
}
```

---

## 四、Service 层修改

### 4.1 修改 `ChatService.java` 接口

```java
/**
 * 发送聊天消息
 * @param senderId 发送者ID
 * @param receiverId 接收者ID
 * @param content 消息内容
 * @param type 消息类型（TEXT/IMAGE/FILE/VOICE）
 * @param imageUrl 图片URL（如果是图片消息）
 * @param fileUrl 文件URL（如果是文件消息）
 * @param voiceUrl 语音文件URL（如果是语音消息）
 * @param duration 语音时长（毫秒，VOICE类型必填）
 * @return 消息信息
 */
Map<String, Object> sendMessage(Long senderId, Long receiverId, String content, 
                                String type, String imageUrl, String fileUrl, 
                                String voiceUrl, Integer duration);
```

### 4.2 修改 `ChatServiceImpl.java` 实现

```java
@Override
@Transactional
public Map<String, Object> sendMessage(Long senderId, Long receiverId, String content, 
                                      String type, String imageUrl, String fileUrl,
                                      String voiceUrl, Integer duration) {
    // 验证参数
    if (content == null || content.isEmpty()) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "消息内容不能为空");
    }
    if (type == null) {
        type = "TEXT";
    }
    
    // 验证语音消息参数
    if ("VOICE".equals(type)) {
        if (voiceUrl == null || voiceUrl.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "语音消息必须提供语音文件URL");
        }
        if (duration == null || duration <= 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "语音消息必须提供有效的时长");
        }
        // 语音消息的content可以存储格式化的时长显示文本，如 "[语音消息 00:15]"
    }

    // 查找或创建会话
    Long conversationId = findOrCreateConversation(senderId, receiverId);
    ChatConversation conversation = conversationMapper.selectById(conversationId);

    // 创建消息
    ChatMessage message = new ChatMessage();
    message.setConversationId(conversationId);
    message.setSenderId(senderId);
    message.setReceiverId(receiverId);
    message.setContent(content);
    message.setType(type);
    message.setImageUrl(imageUrl);
    message.setFileUrl(fileUrl);
    message.setVoiceUrl(voiceUrl);  // 新增：设置语音文件URL
    message.setDuration(duration);  // 新增：设置语音时长
    message.setIsRead(0);
    message.setCreateTime(LocalDateTime.now());

    messageMapper.insert(message);

    // 更新会话的最后消息信息
    conversation.setLastMessageId(message.getId());
    conversation.setLastMessageTime(LocalDateTime.now());
    
    // 更新未读消息数
    if (conversation.getUser1Id().equals(receiverId)) {
        conversation.setUser1UnreadCount(
            (conversation.getUser1UnreadCount() != null ? conversation.getUser1UnreadCount() : 0) + 1);
    } else {
        conversation.setUser2UnreadCount(
            (conversation.getUser2UnreadCount() != null ? conversation.getUser2UnreadCount() : 0) + 1);
    }
    
    conversation.setUpdateTime(LocalDateTime.now());
    conversationMapper.updateById(conversation);

    log.info("发送聊天消息: senderId={}, receiverId={}, conversationId={}, type={}", 
             senderId, receiverId, conversationId, type);

    // 通过WebSocket推送新消息
    try {
        Map<String, Object> wsPayload = new HashMap<>();
        wsPayload.put("messageId", message.getId());
        wsPayload.put("conversationId", conversationId);
        wsPayload.put("senderId", senderId);
        wsPayload.put("receiverId", receiverId);
        wsPayload.put("content", content);
        wsPayload.put("type", type);
        wsPayload.put("voiceUrl", voiceUrl);  // 新增：推送语音URL
        wsPayload.put("duration", duration);  // 新增：推送语音时长
        wsPayload.put("createTime", message.getCreateTime());

        String destination = "/topic/chat/" + receiverId;
        messagingTemplate.convertAndSend(destination, wsPayload);
    } catch (Exception e) {
        log.warn("通过WebSocket推送聊天消息失败: senderId={}, receiverId={}, conversationId={}", 
                 senderId, receiverId, conversationId, e);
    }

    // 返回消息信息
    Map<String, Object> response = new HashMap<>();
    response.put("messageId", message.getId());
    response.put("conversationId", conversationId);
    response.put("createTime", message.getCreateTime());

    return response;
}
```

### 4.3 修改 `getMessages` 方法

在返回消息列表时，需要包含 `voiceUrl` 和 `duration` 字段：

```java
// 在 getMessages 方法中，修改消息转换部分
item.put("voiceUrl", message.getVoiceUrl());  // 新增
item.put("duration", message.getDuration());   // 新增
```

---

## 五、语音转文字功能（可选）

### 5.1 添加语音转文字接口

在 `ChatController` 中添加：

```java
@Operation(summary = "语音转文字", description = "将语音文件转换为文字")
@PostMapping("/voice-to-text")
public ApiResponse<Map<String, Object>> voiceToText(
        HttpServletRequest request,
        @Parameter(description = "语音文件", required = true)
        @RequestParam("file") MultipartFile file) {
    Long userId = UserContextUtils.getUserIdRequired(request);
    
    // 验证文件
    if (file == null || file.isEmpty()) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "语音文件不能为空");
    }
    
    // 调用语音识别服务
    Map<String, Object> result = chatService.voiceToText(file);
    
    return ApiResponse.success("识别成功", result);
}
```

### 5.2 添加语音识别服务

在 `ChatService` 接口中添加：

```java
/**
 * 语音转文字
 * @param file 语音文件
 * @return 识别结果
 */
Map<String, Object> voiceToText(MultipartFile file);
```

在 `ChatServiceImpl` 中实现：

```java
@Override
public Map<String, Object> voiceToText(MultipartFile file) {
    // 方案1：使用第三方语音识别服务（推荐）
    // 例如：百度语音识别、讯飞语音识别、阿里云语音识别等
    
    // 方案2：使用本地语音识别库（如 CMU Sphinx）
    
    // 这里提供一个示例框架，实际需要集成具体的语音识别服务
    try {
        // 1. 将文件保存到临时目录
        String tempFilePath = saveTempFile(file);
        
        // 2. 调用语音识别API
        String recognizedText = callVoiceRecognitionAPI(tempFilePath);
        
        // 3. 清理临时文件
        deleteTempFile(tempFilePath);
        
        Map<String, Object> result = new HashMap<>();
        result.put("text", recognizedText);
        result.put("confidence", 0.95); // 识别置信度
        
        return result;
    } catch (Exception e) {
        log.error("语音转文字失败", e);
        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "语音识别失败");
    }
}

// 示例：调用百度语音识别API（需要配置API Key）
private String callVoiceRecognitionAPI(String filePath) {
    // 这里需要集成百度语音识别SDK
    // 参考：https://ai.baidu.com/ai-doc/SPEECH/Vk38lxily
    // 或者使用其他服务商的API
    
    // 返回识别结果
    return "这是语音转文字的结果";
}
```

### 5.3 语音识别服务集成建议

#### 方案1：百度语音识别
- 优点：准确率高，支持多种语言
- 缺点：需要申请API Key，有调用次数限制
- 文档：https://ai.baidu.com/ai-doc/SPEECH/Vk38lxily

#### 方案2：讯飞语音识别
- 优点：中文识别准确率高
- 缺点：需要申请账号，有调用限制
- 文档：https://www.xfyun.cn/doc/asr/voicedictation/API.html

#### 方案3：阿里云语音识别
- 优点：稳定性好，支持实时识别
- 缺点：需要阿里云账号，按量计费
- 文档：https://help.aliyun.com/product/84430.html

#### 方案4：腾讯云语音识别
- 优点：与微信生态集成好
- 缺点：需要腾讯云账号
- 文档：https://cloud.tencent.com/document/product/1093

---

## 六、文件上传配置

### 6.1 修改文件上传大小限制

在 `application.yml` 或 `application.properties` 中配置：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB  # 单个文件最大10MB
      max-request-size: 50MB  # 请求最大50MB
```

### 6.2 语音文件存储路径

确保 `CommonService` 中的 `uploadPath` 配置包含语音文件目录：

```yaml
file:
  upload:
    path: /path/to/uploads
    url-prefix: http://your-domain.com/uploads
```

---

## 七、API 调用示例

### 7.1 发送语音消息

```http
POST /api/chat/send
Content-Type: application/x-www-form-urlencoded

targetUserId=123
content=[语音消息 00:15]
type=VOICE
voiceUrl=http://your-domain.com/uploads/voices/1234567890_voice.mp3
duration=15000
```

### 7.2 上传语音文件

```http
POST /api/common/upload/file
Content-Type: multipart/form-data

file: [语音文件]
```

### 7.3 语音转文字

```http
POST /api/chat/voice-to-text
Content-Type: multipart/form-data

file: [语音文件]
```

---

## 八、测试建议

### 8.1 单元测试

- 测试语音消息发送
- 测试语音文件上传
- 测试语音转文字（使用模拟数据）

### 8.2 集成测试

- 测试完整的语音消息流程
- 测试大文件上传
- 测试并发上传

### 8.3 性能测试

- 测试语音文件上传性能
- 测试语音识别响应时间
- 测试存储空间占用

---

## 九、注意事项

1. **文件大小限制**：建议语音文件不超过10MB
2. **文件格式支持**：mp3, wav, m4a, amr, aac等
3. **存储空间**：语音文件会占用较多存储空间，建议定期清理
4. **安全性**：验证文件类型，防止恶意文件上传
5. **性能优化**：可以考虑对语音文件进行压缩
6. **CDN加速**：如果用户量大，建议使用CDN加速语音文件访问

---

## 十、总结

实现语音消息功能需要：

1. ✅ 数据库添加 `voice_url` 和 `duration` 字段
2. ✅ 实体类添加对应字段
3. ✅ Controller 添加 `duration` 参数
4. ✅ Service 支持 VOICE 类型消息
5. ✅ 文件上传接口支持语音文件
6. ⚠️ 语音转文字功能（可选，需要集成第三方服务）

按照以上步骤实现，即可支持完整的语音消息功能。

