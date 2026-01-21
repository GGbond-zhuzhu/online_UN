package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "发送聊天消息请求（兼容Web端messages页面）")
public class ChatSendDTO {

    @Schema(description = "会话ID（私聊）", example = "1")
    private Long conversationId;

    @Schema(description = "目标用户ID（私聊）", example = "2")
    private Long targetUserId;

    @Schema(description = "群ID（群聊）", example = "1001")
    private Long groupId;

    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "消息类型（TEXT/IMAGE/FILE）", example = "TEXT")
    private String type = "TEXT";

    @Schema(description = "图片URL（图片消息）")
    private String imageUrl;

    @Schema(description = "文件URL（文件消息）")
    private String fileUrl;
}

