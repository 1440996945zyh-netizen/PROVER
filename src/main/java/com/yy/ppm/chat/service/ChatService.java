package com.yy.ppm.chat.service;

import com.yy.ppm.chat.bean.dto.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 聊天服务接口
 * @author system
 */
public interface ChatService {

    /**
     * 发送聊天消息
     * @param query 查询内容
     * @return 响应结果
     */
    ChatMessageResponseDTO sendMessage(String query);

    /**
     * 发送聊天消息（完整参数）
     * @param requestDTO 请求参数
     * @return 响应结果
     */
    ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO requestDTO);

    /**
     * 发送聊天消息（流式响应）
     * @param query 查询内容
     * @param emitter SSE发射器
     * @param userId 用户ID
     * @param agentId 智能体ID
     * @param conversationId 会话ID（可选，新对话时为空）
     */
    void sendMessageStream(String query, SseEmitter emitter, Long userId, String agentId, String conversationId);

    /**
     * 获取聊天历史记录
     * @param agentId 智能体ID
     * @param userId 用户ID
     * @return 聊天历史记录
     */
    ChatHistoryResponseDTO getChatHistory(String agentId, Long userId);

    /**
     * 获取会话列表
     * @param agentId 智能体ID
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ConversationDTO> getConversations(String agentId, Long userId);

    /**
     * 根据会话ID获取聊天记录
     * @param agentId 智能体ID
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @return 消息列表
     */
    List<MessageDTO> getMessagesByConversationId(String agentId, Long userId, String conversationId);

    /**
     * 停止聊天响应（调用外部停止接口）
     * @param agentId 智能体ID
     * @param userId 用户ID
     * @param taskId 任务ID（流式返回中的 message_id / task_id）
     */
    void stopMessage(String agentId, Long userId, String taskId);

    /**
     * 消息反馈（点赞 / 点踩 / 撤销点赞）
     * @param agentId 智能体ID
     * @param userId 用户ID
     * @param messageId 消息ID
     * @param rating 反馈结果：like / dislike / null
     * @param content 反馈内容
     */
    void feedbackMessage(String agentId, Long userId, String messageId, String rating, String content);

    /**
     * 删除会话
     * @param agentId 智能体ID
     * @param userId 用户ID
     * @param conversationId 会话ID
     */
    void deleteConversation(String agentId, Long userId, String conversationId);

    /**
     * 重命名会话
     * @param agentId 智能体ID
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @param name 会话名称（可选，如果为空则自动生成）
     * @return 会话信息
     */
    ConversationDTO renameConversation(String agentId, Long userId, String conversationId, String name);

    /**
     * 获取智能体参数（开场白、推荐问题等）
     * @param agentId 智能体ID
     * @param userId 用户ID
     * @return 智能体参数
     */
    AgentParametersDTO getAgentParameters(String agentId, Long userId);

    /**
     * 文件上传
     * @param fileArray
     * @param userId
     * @return
     */
    ChatFileDTO upload(MultipartFile fileArray,Long userId);

    /**
     * 通过上传文件返回的fileID查询识别内容
     * @param uploadFileId
     * @param emitter
     * @param userId
     */
    void workflowsStream(String uploadFileId, SseEmitter emitter, Long userId);

    /**
     * 通过上传文件返回的fileID查询识别内容
     * @param uploadFileId
     * @param userId
     * @return
     */
    ChatFileDTO workflowsRun(String uploadFileId, Long userId);

    ChatFileDTO contractUpload(MultipartFile fileArray, Long userId);

    /**
     * 通过上传文件返回的fileID查询识别内容 -- 合同审查
     * @param uploadFileId
     * @param userId
     * @return
     */
    void chatMessage(String uploadFileId, SseEmitter emitter, Long userId);
}
