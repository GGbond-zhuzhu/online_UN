# 语音消息功能 - 快速实现清单

## 📋 需要修改的文件清单

### 1. 数据库修改
- [ ] 执行 SQL 脚本：`src/main/resources/db/migration/add_voice_message_fields.sql`
  - 添加 `voice_url` 字段
  - 添加 `duration` 字段

### 2. 实体类修改
- [ ] `src/main/java/com/yourschool/campussystem/entity/ChatMessage.java`
  - 添加 `voiceUrl` 字段
  - 添加 `duration` 字段

### 3. Controller 层修改
- [ ] `src/main/java/com/yourschool/campussystem/controller/ChatController.java`
  - 修改 `sendMessage` 方法，添加 `voiceUrl` 和 `duration` 参数

### 4. Service 层修改
- [ ] `src/main/java/com/yourschool/campussystem/service/ChatService.java`
  - 修改 `sendMessage` 方法签名，添加 `voiceUrl` 和 `duration` 参数
- [ ] `src/main/java/com/yourschool/campussystem/service/impl/ChatServiceImpl.java`
  - 实现 `sendMessage` 方法，支持 VOICE 类型
  - 修改 `getMessages` 方法，返回 `voiceUrl` 和 `duration`

### 5. 文件上传（可选）
- [ ] `src/main/java/com/yourschool/campussystem/controller/CommonController.java`
  - 可以添加专门的语音上传接口（或复用现有的 `uploadFile`）

### 6. 语音转文字（可选）
- [ ] `src/main/java/com/yourschool/campussystem/controller/ChatController.java`
  - 添加 `voiceToText` 接口
- [ ] `src/main/java/com/yourschool/campussystem/service/ChatService.java`
  - 添加 `voiceToText` 方法
- [ ] `src/main/java/com/yourschool/campussystem/service/impl/ChatServiceImpl.java`
  - 实现 `voiceToText` 方法（需要集成第三方语音识别服务）

---

## 🔑 关键代码片段

### ChatMessage 实体类添加字段
```java
@TableField("voice_url")
private String voiceUrl;

@TableField("duration")
private Integer duration;
```

### Controller 方法参数
```java
@RequestParam(required = false) String voiceUrl,
@RequestParam(required = false) Integer duration
```

### Service 方法验证
```java
if ("VOICE".equals(type)) {
    if (voiceUrl == null || voiceUrl.isEmpty()) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "语音消息必须提供语音文件URL");
    }
    if (duration == null || duration <= 0) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "语音消息必须提供有效的时长");
    }
}
```

---

## 📝 测试要点

1. ✅ 发送语音消息（带 voiceUrl 和 duration）
2. ✅ 获取消息列表（包含 voiceUrl 和 duration）
3. ✅ WebSocket 推送语音消息
4. ✅ 文件上传（语音文件）
5. ✅ 语音转文字（如果实现）

---

## ⚠️ 注意事项

1. **文件大小**：建议限制语音文件不超过 10MB
2. **文件格式**：支持 mp3, wav, m4a, amr, aac
3. **存储空间**：语音文件占用空间较大，注意清理
4. **安全性**：验证文件类型，防止恶意上传

---

详细实现说明请参考：`VOICE_MESSAGE_IMPLEMENTATION.md`

