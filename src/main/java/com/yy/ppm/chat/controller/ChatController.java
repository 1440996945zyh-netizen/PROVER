package com.yy.ppm.chat.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.chat.bean.dto.*;
import com.yy.ppm.chat.service.AgentService;
import com.yy.ppm.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聊天Controller
 *
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/chat")
public class ChatController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private SecurityUtils securityUtils;


    /**
     * 获取智能体列表
     */
    @GetMapping("/agents")
    @PreAuthorize("hasAuthority('system:chat:query')")
    public Map<String, Object> getAgents() {
        final String methodName = "ChatController:getAgents";
        LOGGER.enter(methodName + "[start]");

        // 从 SYS_AGENT 表获取智能体列表
        List<AgentDTO> agents = agentService.getAgents();

        // 移除 apiKey 字段，不返回给前端
        List<AgentDTO> resultList = agents.stream()
                .map(agent -> {
                    AgentDTO dto = new AgentDTO();
                    dto.setId(agent.getId());
                    dto.setAvatar(agent.getAvatar());
                    dto.setName(agent.getName());
                    dto.setDescription(agent.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("获取成功").toResult(resultList);
    }


    /**
     * 获取智能体参数（开场白、推荐问题等）
     */
    @GetMapping("/parameters")
    @PreAuthorize("hasAuthority('system:chat:query')")
    public Map<String, Object> getAgentParameters(@RequestParam("agentId") String agentId) {
        final String methodName = "ChatController:getAgentParameters";
        LOGGER.enter(methodName + "[start]", "agentId:" + agentId);

        Long userId = securityUtils.getLoginUserId();

        AgentParametersDTO result = chatService.getAgentParameters(agentId, userId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("获取成功").toResult(result);
    }

    /**
     * 发送聊天消息（简单版本，只传query参数）- 流式响应
     * 支持 GET 和 POST 两种方式（EventSource 只支持 GET）
     */
    @GetMapping(value = "/message/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAuthority('system:chat:send')")
    public SseEmitter sendMessageStream(
            @RequestParam("query") String query,
            @RequestParam("agentId") String agentId,
            @RequestParam(value = "conversationId", required = false) String conversationId) {
        final String methodName = "ChatController:sendMessageStream";
        LOGGER.enter(methodName + "[start]", "query:" + query + ", agentId:" + agentId + ", conversationId:" + conversationId);

        // 前置校验：避免空参数导致后续流程阻塞
        if (query == null || query.trim().isEmpty()) {
            SseEmitter emitter = new SseEmitter(1000L); // 短超时
            try {
                emitter.completeWithError(new BusinessRuntimeException("查询内容不能为空"));
            } catch (Exception e) {
                LOGGER.error("前置校验失败" + e);
            }
            return emitter;
        }
        if (agentId == null || agentId.trim().isEmpty()) {
            SseEmitter emitter = new SseEmitter(1000L);
            try {
                emitter.completeWithError(new BusinessRuntimeException("智能体ID不能为空"));
            } catch (Exception e) {
                LOGGER.error("前置校验失败" + e);
            }
            return emitter;
        }

        // 获取当前用户信息（在主线程中完成，避免异步线程上下文丢失）
        Long userId = securityUtils.getLoginUserId();

        // 调整超时时间：延长到10分钟（600000毫秒），适配大模型慢响应
        SseEmitter emitter = new SseEmitter(600000L);

        // 超时回调：增加日志，明确超时原因
        emitter.onTimeout(() -> {
            LOGGER.warn(methodName + "[timeout]",
                    "SSE连接超时");
            try {
                // 超时前主动发送超时提示给前端
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"msg\":\"请求超时，请重试\"}"));
                emitter.complete();
            } catch (Exception e) {
                LOGGER.warn("超时回调发送失败" + e);
            }
        });

        // 错误回调：捕获权限异常
        emitter.onError((ex) -> {
            if (ex instanceof AuthorizationDeniedException) {
                LOGGER.error(methodName + "[error]", "权限拒绝：{}");
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"msg\":\"权限不足，请重新登录\"}"));
                    emitter.complete();
                } catch (Exception e) {
                    LOGGER.warn("权限异常回调发送失败" + e);
                }
            } else {
                LOGGER.error(methodName + "[error]", "SSE连接错误: ");
                try {
                    emitter.complete();
                } catch (Exception e) {
                    LOGGER.warn("错误回调关闭失败" + e);
                }
            }
        });

        // 异步处理流式响应（传递已获取的userId，避免异步线程再次查询）
        chatService.sendMessageStream(query, emitter, userId, agentId, conversationId);
        LOGGER.exit(methodName + "[end]");
        return emitter;
    }

    /**
     * 发送聊天消息（简单版本，只传query参数）- 非流式
     */
    @PostMapping("/message")
    @PreAuthorize("hasAuthority('system:chat:send')")
    public Map<String, Object> sendMessage(@RequestParam("query") String query) {
        final String methodName = "ChatController:sendMessage";
        LOGGER.enter(methodName + "[start]", "query:" + query);
        ChatMessageResponseDTO result = chatService.sendMessage(query);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("发送成功").toResult(result);

    }

    /**
     * 获取聊天历史记录 + 获取会话列表
     */
    @GetMapping("/history")
    @PreAuthorize("hasAuthority('system:chat:query')")
    public Map<String, Object> getChatHistory(@RequestParam("agentId") String agentId) {
        final String methodName = "ChatController:getChatHistory";
        LOGGER.enter(methodName + "[start]", "agentId:" + agentId);

        // 获取当前用户信息
        Long userId = securityUtils.getLoginUserId();

        ChatHistoryResponseDTO result = chatService.getChatHistory(agentId, userId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("获取成功").toResult(result);
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    @PreAuthorize("hasAuthority('system:chat:query')")
    public Map<String, Object> getConversations(@RequestParam("agentId") String agentId) {
        final String methodName = "ChatController:getConversations";
        LOGGER.enter(methodName + "[start]", "agentId:" + agentId);

        // 获取当前用户信息
        Long userId = securityUtils.getLoginUserId();

        List<ConversationDTO> result = chatService.getConversations(agentId, userId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("获取成功").toResult(result);
    }

    /**
     * 根据会话ID获取聊天记录
     */
    @GetMapping("/messages")
    @PreAuthorize("hasAuthority('system:chat:query')")
    public Map<String, Object> getMessagesByConversationId(
            @RequestParam("agentId") String agentId,
            @RequestParam("conversationId") String conversationId) {
        final String methodName = "ChatController:getMessagesByConversationId";
        LOGGER.enter(methodName + "[start]", "agentId:" + agentId + ", conversationId:" + conversationId);

        // 获取当前用户信息
        Long userId = securityUtils.getLoginUserId();

        List<MessageDTO> result = chatService.getMessagesByConversationId(agentId, userId, conversationId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("获取成功").toResult(result);
    }

    /**
     * 停止聊天响应
     * 通过任务ID调用外部停止接口，终止当前生成任务
     */
    @PostMapping("/message/stop")
    @PreAuthorize("hasAuthority('system:chat:send')")
    public Map<String, Object> stopMessage(
            @RequestParam("agentId") String agentId,
            @RequestParam("taskId") String taskId) {
        final String methodName = "ChatController:stopMessage";
        LOGGER.enter(methodName + "[start]", "agentId:" + agentId + ", taskId:" + taskId);

        // 获取当前用户信息，用于构造 user 标识
        Long userId = securityUtils.getLoginUserId();

        // 调用服务层停止接口，如失败会抛出业务异常
        chatService.stopMessage(agentId, userId, taskId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("停止成功").toResult();
    }

    /**
     * 消息反馈（点赞 / 点踩 / 撤销）
     */
    @PostMapping("/message/feedback")
    @PreAuthorize("hasAuthority('system:chat:feedback')")
    public Map<String, Object> feedbackMessage(
            @RequestParam("agentId") String agentId,
            @RequestParam("messageId") String messageId,
            @RequestBody ChatFeedbackRequestDTO requestDTO) {
        final String methodName = "ChatController:feedbackMessage";
        LOGGER.enter(methodName + "[start]", "agentId:" + agentId + ", messageId:" + messageId + ", rating:" + requestDTO.getRating());

        Long userId = securityUtils.getLoginUserId();

        chatService.feedbackMessage(agentId, userId, messageId, requestDTO.getRating(), requestDTO.getContent());

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("反馈成功").toResult();
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/conversation")
    @PreAuthorize("hasAuthority('system:chat:delete')")
    public Map<String, Object> deleteConversation(
            @RequestParam("agentId") String agentId,
            @RequestParam("conversationId") String conversationId) {
        final String methodName = "ChatController:deleteConversation";
        LOGGER.enter(methodName + "[start]", "agentId:" + agentId + ", conversationId:" + conversationId);

        Long userId = securityUtils.getLoginUserId();

        chatService.deleteConversation(agentId, userId, conversationId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 重命名会话
     */
    @PostMapping("/conversation/rename")
    @PreAuthorize("hasAuthority('system:chat:edit')")
    public Map<String, Object> renameConversation(
            @RequestParam("agentId") String agentId,
            @RequestParam("conversationId") String conversationId,
            @RequestParam(value = "name", required = false) String name) {
        final String methodName = "ChatController:renameConversation";
        LOGGER.enter(methodName + "[start]", "agentId:" + agentId + ", conversationId:" + conversationId + ", name:" + name);

        Long userId = securityUtils.getLoginUserId();

        ConversationDTO result = chatService.renameConversation(agentId, userId, conversationId, name);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("重命名成功").toResult(result);
    }

    /**
     * 单据识别文件上传
     *
     */
    @PostMapping(value = "/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> upload(@RequestParam("fileArray") MultipartFile fileArray) {
        final String methodName = "ChatController:upload";
        LOGGER.enter(methodName + "[start]");

        Long userId = securityUtils.getLoginUserId();

        ChatFileDTO result = chatService.upload(fileArray, userId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("上传成功").toResult(result);
    }

    /**
     * 通过文件获取识别内容 -- 流模式
     * @param uploadFileId
     * @return
     */
    @GetMapping(value = "/workflowsStream/run", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter workflowsStreamRun(@RequestParam("uploadFileId") String uploadFileId) {
        final String methodName = "ChatController:/workflowsStream/run";
        LOGGER.enter(methodName + "[start]");

        // 前置校验：避免空参数导致后续流程阻塞
        if (uploadFileId == null || uploadFileId.isEmpty()) {
            SseEmitter emitter = new SseEmitter(1000L); // 短超时
            try {
                emitter.completeWithError(new BusinessRuntimeException("文件ID不能为空"));
            } catch (Exception e) {
                LOGGER.error("前置校验失败" + e);
            }
            return emitter;
        }

        // 获取当前用户信息（在主线程中完成，避免异步线程上下文丢失）
        Long userId = securityUtils.getLoginUserId();

        // 调整超时时间：延长到10分钟（600000毫秒），适配大模型慢响应
        SseEmitter emitter = new SseEmitter(600000L);

        // 超时回调：增加日志，明确超时原因
        emitter.onTimeout(() -> {
            LOGGER.warn(methodName + "[timeout]",
                    "SSE连接超时");
            try {
                // 超时前主动发送超时提示给前端
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"msg\":\"请求超时，请重试\"}"));
                emitter.complete();
            } catch (Exception e) {
                LOGGER.warn("超时回调发送失败" + e);
            }
        });

        // 错误回调：捕获权限异常
        emitter.onError((ex) -> {
            if (ex instanceof AuthorizationDeniedException) {
                LOGGER.error(methodName + "[error]", "权限拒绝：{}");
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"msg\":\"权限不足，请重新登录\"}"));
                    emitter.complete();
                } catch (Exception e) {
                    LOGGER.warn("权限异常回调发送失败" + e);
                }
            } else {
                LOGGER.error(methodName + "[error]", "SSE连接错误: ");
                try {
                    emitter.complete();
                } catch (Exception e) {
                    LOGGER.warn("错误回调关闭失败" + e);
                }
            }
        });

        // 异步处理流式响应（传递已获取的userId，避免异步线程再次查询）
        chatService.workflowsStream(uploadFileId, emitter, userId);
        LOGGER.exit(methodName + "[end]");
        return emitter;
    }

    /**
     * 通过文件获取识别内容 -- 阻塞模式
     * @param uploadFileId
     * @return
     */
    @GetMapping("/workflows/run")
    public Map<String, Object> workflowsRun(@RequestParam("uploadFileId") String uploadFileId) {
        final String methodName = "ChatController:/workflows/run";
        LOGGER.enter(methodName + "[start]");

        // 前置校验：避免空参数导致后续流程阻塞
        if (uploadFileId == null || uploadFileId.isEmpty()) {
            new BusinessRuntimeException("文件ID不能为空");
        }

        // 获取当前用户信息（在主线程中完成，避免异步线程上下文丢失）
        Long userId = securityUtils.getLoginUserId();

        ChatFileDTO chatFileDTO = chatService.workflowsRun(uploadFileId, userId);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("识别成功").toResult(chatFileDTO);
    }

    /**
     * 合同审查文件上传
     *
     */
    @PostMapping(value = "/contract/filesUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> contractUpload(@RequestParam("fileArray") MultipartFile fileArray) {
        final String methodName = "ChatController:upload";
        LOGGER.enter(methodName + "[start]");

        Long userId = securityUtils.getLoginUserId();

        ChatFileDTO result = chatService.contractUpload(fileArray, userId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("上传成功").toResult(result);
    }

    /**
     * 通过文件获取识别内容 -- 合同审查
     * @param uploadFileId
     * @return
     */
    @GetMapping(value = "/contract/chatMessage", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatMessage(@RequestParam("uploadFileId") String uploadFileId) {
        final String methodName = "ChatController:/workflowsStream/run";
        LOGGER.enter(methodName + "[start]");

        // 前置校验：避免空参数导致后续流程阻塞
        if (uploadFileId == null || uploadFileId.isEmpty()) {
            SseEmitter emitter = new SseEmitter(1000L); // 短超时
            try {
                emitter.completeWithError(new BusinessRuntimeException("文件ID不能为空"));
            } catch (Exception e) {
                LOGGER.error("前置校验失败" + e);
            }
            return emitter;
        }

        // 获取当前用户信息（在主线程中完成，避免异步线程上下文丢失）
        Long userId = securityUtils.getLoginUserId();

        // 调整超时时间：延长到10分钟（600000毫秒），适配大模型慢响应
        SseEmitter emitter = new SseEmitter(600000L);

        // 超时回调：增加日志，明确超时原因
        emitter.onTimeout(() -> {
            LOGGER.warn(methodName + "[timeout]",
                    "SSE连接超时");
            try {
                // 超时前主动发送超时提示给前端
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"msg\":\"请求超时，请重试\"}"));
                emitter.complete();
            } catch (Exception e) {
                LOGGER.warn("超时回调发送失败" + e);
            }
        });

        // 错误回调：捕获权限异常
        emitter.onError((ex) -> {
            if (ex instanceof AuthorizationDeniedException) {
                LOGGER.error(methodName + "[error]", "权限拒绝：{}");
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"msg\":\"权限不足，请重新登录\"}"));
                    emitter.complete();
                } catch (Exception e) {
                    LOGGER.warn("权限异常回调发送失败" + e);
                }
            } else {
                LOGGER.error(methodName + "[error]", "SSE连接错误: ");
                try {
                    emitter.complete();
                } catch (Exception e) {
                    LOGGER.warn("错误回调关闭失败" + e);
                }
            }
        });

        // 异步处理流式响应（传递已获取的userId，避免异步线程再次查询）
        chatService.chatMessage(uploadFileId, emitter, userId);
        LOGGER.exit(methodName + "[end]");
        return emitter;
    }
}
