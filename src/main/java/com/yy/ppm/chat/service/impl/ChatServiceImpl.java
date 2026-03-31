package com.yy.ppm.chat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yy.common.log.MicroLogger;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.chat.bean.dto.AgentParametersDTO;
import com.yy.ppm.chat.bean.dto.*;
import com.yy.ppm.chat.service.AgentService;
import com.yy.ppm.chat.service.ChatService;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysUserService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * 聊天服务实现类
 *
 * @author system
 */
@Service
public class ChatServiceImpl implements ChatService {

    /**
     * API地址
     */
    @Value("${dify.url}")
    private String difyUrl;

    /**
     * API密钥
     */
    @Value("${dify.key}")
    private String difyKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final MicroLogger LOGGER = new MicroLogger(ChatServiceImpl.class);

    /**
     * 获取完整的API地址
     */
    private String getApiUrl() {
        return difyUrl + "/chat-messages";
    }

    @Override
    public ChatMessageResponseDTO sendMessage(String query) {
        ChatMessageRequestDTO requestDTO = new ChatMessageRequestDTO();
        requestDTO.setQuery(query);
        requestDTO.setInputs(new HashMap<>());
        requestDTO.setResponseMode("streaming");
        requestDTO.setConversationId("");
        requestDTO.setUser("abc-123");
        return sendMessage(requestDTO);
    }

    @Override
    public ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO requestDTO) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(getApiUrl());

            // 设置请求头
            httpPost.setHeader("Authorization", "Bearer " + difyKey);
            httpPost.setHeader("Content-Type", "application/json");

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", requestDTO.getInputs() != null ? requestDTO.getInputs() : new HashMap<>());
            requestBody.put("query", requestDTO.getQuery());
            requestBody.put("response_mode", requestDTO.getResponseMode() != null ? requestDTO.getResponseMode() : "streaming");
            requestBody.put("conversation_id", requestDTO.getConversationId() != null ? requestDTO.getConversationId() : "");
            requestBody.put("user", requestDTO.getUser() != null ? requestDTO.getUser() : "abc-123");

            if (requestDTO.getFiles() != null && !requestDTO.getFiles().isEmpty()) {
                requestBody.put("files", requestDTO.getFiles());
            }

            // 将请求体转换为JSON字符串
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String responseString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);

                // 解析响应
                ChatMessageResponseDTO responseDTO = new ChatMessageResponseDTO();

                // 如果是流式响应，解析流式数据
                if ("streaming".equals(requestDTO.getResponseMode())) {
                    // 解析流式响应，提取 answer 字段
                    String content = parseStreamResponse(responseString, responseDTO);
                    responseDTO.setContent(content);
                } else {
                    // 如果是阻塞式响应，尝试解析JSON
                    try {
                        Map<String, Object> responseMap = objectMapper.readValue(responseString, Map.class);
                        responseDTO.setData(responseMap);
                        if (responseMap.containsKey("answer")) {
                            responseDTO.setContent(String.valueOf(responseMap.get("answer")));
                        }
                        if (responseMap.containsKey("conversation_id")) {
                            responseDTO.setConversationId(String.valueOf(responseMap.get("conversation_id")));
                        }
                        if (responseMap.containsKey("message_id")) {
                            responseDTO.setMessageId(String.valueOf(responseMap.get("message_id")));
                        }
                    } catch (Exception e) {
                        // 如果解析失败，直接返回原始内容
                        responseDTO.setContent(responseString);
                    }
                }

                return responseDTO;
            } else {
                throw new BusinessRuntimeException("接口返回值为空");
            }
        } catch (BusinessRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessRuntimeException("调用聊天接口失败:");
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    /**
     * 解析流式响应，提取 answer 字段
     * 格式: data: {"event":"message","answer":"..."}\n\ndata: {"event":"message","answer":"..."}\n\n...
     *
     * @param responseString 流式响应字符串
     * @param responseDTO    响应DTO对象，用于设置 conversationId 和 messageId
     * @return 拼接后的 answer 内容
     */
    private String parseStreamResponse(String responseString, ChatMessageResponseDTO responseDTO) {
        if (responseString == null || responseString.trim().isEmpty()) {
            return "";
        }

        StringBuilder content = new StringBuilder();
        String[] lines = responseString.split("\n");

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                continue;
            }

            // 处理以 "data: " 开头的行
            if (trimmedLine.startsWith("data:")) {
                try {
                    // 移除 "data: " 前缀
                    String jsonStr = trimmedLine.substring(5).trim();

                    // 解析 JSON
                    Map<String, Object> json = objectMapper.readValue(jsonStr, Map.class);

                    // 如果是 message_end 事件，停止解析并保存会话信息
                    if ("message_end".equals(json.get("event"))) {
                        if (json.containsKey("conversation_id")) {
                            responseDTO.setConversationId(String.valueOf(json.get("conversation_id")));
                        }
                        if (json.containsKey("message_id")) {
                            responseDTO.setMessageId(String.valueOf(json.get("message_id")));
                        }
                        break;
                    }

                    // 提取 answer 字段（event 为 message 时）
                    if ("message".equals(json.get("event")) && json.containsKey("answer")) {
                        String answer = String.valueOf(json.get("answer"));
                        if (answer != null && !"null".equals(answer)) {
                            content.append(answer);
                        }
                    }

                    // 保存会话ID和消息ID（从第一个 message 事件中获取）
                    if ("message".equals(json.get("event"))) {
                        if (json.containsKey("conversation_id") && responseDTO.getConversationId() == null) {
                            responseDTO.setConversationId(String.valueOf(json.get("conversation_id")));
                        }
                        if (json.containsKey("message_id") && responseDTO.getMessageId() == null) {
                            responseDTO.setMessageId(String.valueOf(json.get("message_id")));
                        }
                    }
                } catch (Exception e) {
                    // 解析失败，跳过这一行
                    // 可以记录日志，但不影响整体流程
                }
            }
        }

        return content.toString();
    }


    @Autowired
    SysUserService sysUserService;

    @Autowired
    AgentService agentService;

    /**
     * 发送聊天消息（流式响应）
     * 实时读取外部API的流式响应并转发给前端
     * @param query 查询内容
     * @param emitter SSE发射器
     * @param userId 用户ID
     * @param agentId 智能体ID
     * @param conversationId 会话ID（可选，新对话时为空）
     */
    @Override
    @Async
    public void sendMessageStream(String query, SseEmitter emitter, Long userId, String agentId, String conversationId) {
        CloseableHttpClient httpClient = null;
        BufferedReader reader = null;
        AtomicBoolean emitterCompleted = new AtomicBoolean(false);

        // 配置HTTP客户端超时（避免第三方接口无限阻塞）
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000) // 连接超时30秒
                .setSocketTimeout(550000) // 读取超时550秒（比SSE超时少50秒，预留处理时间）
                .build();

        try {
            // 1. 校验智能体信息（现在上下文已传递，权限校验正常）
            AgentDTO agent = agentService.getAgentById(agentId);
            if (agent == null) {
                if (emitterCompleted.compareAndSet(false, true)) {
                    try {
                        emitter.completeWithError(new BusinessRuntimeException("智能体不存在"));
                    } catch (Exception e) {
                        LOGGER.warn("发送智能体不存在错误失败" + e);
                    }
                }
                return;
            }
            String apiKey = agent.getApiKey();

            // 2. 获取用户账号（userId已在主线程获取，直接使用）
            String userAccount = "abc-123";
            if (userId != null) {
                try {
                    SysUserDTO userDTO = sysUserService.getById(userId);
                    if (userDTO != null && userDTO.getUserAccount() != null) {
                        userAccount = userDTO.getUserAccount();
                    }
                } catch (AuthorizationDeniedException e) {
                    // 捕获权限异常，主动关闭连接
                    LOGGER.error("查询用户信息权限拒绝" + e);
                    if (emitterCompleted.compareAndSet(false, true)) {
                        emitter.completeWithError(new BusinessRuntimeException("用户信息查询失败：权限不足"));
                    }
                    return;
                } catch (Exception e) {
                    LOGGER.warn("查询用户账号失败，使用默认值" + e);
                }
            }

            // 3. 构建HTTP请求（增加超时配置）
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build(); // 替换默认客户端，使用带超时的配置
            HttpPost httpPost = new HttpPost(getApiUrl());
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", new HashMap<>());
            requestBody.put("query", query);
            requestBody.put("response_mode", "streaming");
            requestBody.put("auto_generate_name", true);
            requestBody.put("conversation_id", conversationId != null && !conversationId.isEmpty() ? conversationId : "");
            requestBody.put("user", userAccount);

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            // 4. 执行请求并处理流式响应
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity == null) {
                if (emitterCompleted.compareAndSet(false, true)) {
                    try {
                        emitter.completeWithError(new BusinessRuntimeException("接口返回值为空"));
                    } catch (Exception e) {
                        LOGGER.warn("发送返回值为空错误失败" + e);
                    }
                }
                return;
            }

            InputStream inputStream = responseEntity.getContent();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            String responseConversationId = null;
            String responseMessageId = null;
            String responseTaskId = null;
            boolean messageEndReceived = false;

            // 5. 逐行读取并发送流式数据
            while ((line = reader.readLine()) != null) {
                if (emitterCompleted.get()) {
                    break;
                }

                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) {
                    continue;
                }

                if (trimmedLine.startsWith("data:")) {
                    try {
                        String jsonStr = trimmedLine.substring(5).trim();
                        if (jsonStr.isEmpty() || "null".equals(jsonStr)) {
                            continue;
                        }

                        Map<String, Object> json = objectMapper.readValue(jsonStr, Map.class);

                        if ("message_end".equals(json.get("event"))) {
                            messageEndReceived = true;
                            responseConversationId = json.containsKey("conversation_id") ? String.valueOf(json.get("conversation_id")) : null;
                            responseMessageId = json.containsKey("message_id") ? String.valueOf(json.get("message_id")) : null;
                            responseTaskId = json.containsKey("task_id") && responseTaskId == null ? String.valueOf(json.get("task_id")) : responseTaskId;

                            Map<String, Object> endEvent = new HashMap<>();
                            endEvent.put("event", "end");
                            endEvent.put("conversation_id", responseConversationId);
                            endEvent.put("message_id", responseMessageId);
                            if (responseTaskId != null) {
                                endEvent.put("task_id", responseTaskId);
                            }
                            // 透传检索引用信息（retriever_resources），用于前端展示“引用”列表
                            // Dify 文档：retriever_resources 在 message_end 事件中；有的版本可能在顶层，有的在 metadata 内
                            Object retrieverResources = null;
                            if (json.containsKey("retriever_resources")) {
                                retrieverResources = json.get("retriever_resources");
                            } else if (json.containsKey("metadata")) {
                                try {
                                    Object metaObj = json.get("metadata");
                                    if (metaObj instanceof Map) {
                                        Map<?, ?> metaMap = (Map<?, ?>) metaObj;
                                        if (metaMap.containsKey("retriever_resources")) {
                                            retrieverResources = metaMap.get("retriever_resources");
                                        }
                                    }
                                } catch (Exception ignored) {
                                    // 忽略解析 metadata 的异常，避免影响主流程
                                }
                            }
                            if (retrieverResources != null) {
                                endEvent.put("retriever_resources", retrieverResources);
                            }
                            emitter.send(SseEmitter.event()
                                    .name("end")
                                    .data(objectMapper.writeValueAsString(endEvent)));

                            emitterCompleted.set(true);
                            break;
                        }

                        if ("message".equals(json.get("event")) && json.containsKey("answer")) {
                            String answer = String.valueOf(json.get("answer"));
                            if (answer != null && !"null".equals(answer) && !answer.isEmpty()) {
                                if (responseConversationId == null && json.containsKey("conversation_id")) {
                                    responseConversationId = String.valueOf(json.get("conversation_id"));
                                }
                                if (responseMessageId == null && json.containsKey("message_id")) {
                                    responseMessageId = String.valueOf(json.get("message_id"));
                                }
                                if (responseTaskId == null && json.containsKey("task_id")) {
                                    responseTaskId = String.valueOf(json.get("task_id"));
                                }

                                Map<String, Object> data = new HashMap<>();
                                data.put("content", answer);
                                data.put("conversation_id", responseConversationId);
                                data.put("message_id", responseMessageId);
                                if (responseTaskId != null) {
                                    data.put("task_id", responseTaskId);
                                }

                                emitter.send(SseEmitter.event()
                                        .name("message")
                                        .data(objectMapper.writeValueAsString(data)));
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("处理流式数据失败" + e);
                        emitterCompleted.set(true);
                        break;
                    }
                }
            }

            // 6. 正常结束处理
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    if (messageEndReceived) {
                        emitter.complete();
                    } else {
                        emitter.completeWithError(new BusinessRuntimeException("未收到完整的响应结束标记"));
                    }
                } catch (Exception e) {
                    LOGGER.warn("正常结束时关闭emitter失败" + e);
                }
            }

        } catch (SocketTimeoutException e) {
            // 捕获HTTP读取超时，主动提示用户
            LOGGER.error("第三方接口响应超时" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"msg\":\"大模型响应超时，请稍后重试\"}"));
                    emitter.complete();
                } catch (Exception ex) {
                    LOGGER.warn("超时提示发送失败" + ex);
                }
            }
        } catch (AuthorizationDeniedException e) {
            // 专门捕获权限异常
            LOGGER.error("异步线程权限拒绝" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"msg\":\"权限不足，请重新登录后重试\"}"));
                } catch (Exception ex) {
                    LOGGER.warn("权限异常提示发送失败"+ ex);
                }
            }
        } catch (IOException e) {
            LOGGER.error("网络IO异常" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.completeWithError(new BusinessRuntimeException("网络连接异常: " + e.getMessage()));
                } catch (Exception ex) {
                    LOGGER.warn("IO异常时关闭emitter失败" + ex);
                }
            }
        } catch (Exception e) {
            LOGGER.error("调用聊天接口失败" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.completeWithError(new BusinessRuntimeException("调用聊天接口失败: " + e.getMessage()));
                } catch (Exception ex) {
                    LOGGER.warn("业务异常时关闭emitter失败" + ex);
                }
            }
        } finally {
            // 7. 资源关闭
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn("关闭流读取器失败" + e);
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    LOGGER.warn("关闭HTTP客户端失败" + e);
                }
            }
            // 最终兜底
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    LOGGER.warn("兜底关闭emitter失败" + e);
                }
            }
        }
    }

    /**
     * 获取用户账号（公共方法）
     * @param userId 用户ID
     * @return 用户账号
     */
    private String getUserAccount(Long userId) {
        String userCount = "abc-123";
        if (userId != null) {
            try {
                SysUserDTO userDTO = sysUserService.getById(userId);
                if (userDTO != null && userDTO.getUserAccount() != null) {
                    userCount = userDTO.getUserAccount();
                }
            } catch (Exception e) {
                // 查询失败，使用默认值
            }
        }
        return userCount;
    }

    /**
     * 获取会话列表（公共方法）
     * @param agentId 智能体ID
     * @param userId 用户ID
     * @return 会话列表
     */
    @Override
    public List<ConversationDTO> getConversations(String agentId, Long userId) {
        CloseableHttpClient httpClient = null;
        try {
            // 根据 agentId 获取智能体信息
            AgentDTO agent = agentService.getAgentById(agentId);
            if (agent == null) {
                throw new BusinessRuntimeException("智能体不存在");
            }
            String apiKey = agent.getApiKey();

            // 获取用户账号
            String userCount = getUserAccount(userId);

            httpClient = HttpClients.createDefault();

            // 获取会话列表
            String conversationsUrl = difyUrl + "/conversations?user=" + userCount + "&limit=20";
            HttpGet conversationsGet = new HttpGet(conversationsUrl);
            conversationsGet.setHeader("Authorization", "Bearer " + apiKey);

            HttpResponse conversationsResponse = httpClient.execute(conversationsGet);
            HttpEntity conversationsEntity = conversationsResponse.getEntity();
            String conversationsJson = EntityUtils.toString(conversationsEntity, StandardCharsets.UTF_8);

            Map<String, Object> conversationsResult = objectMapper.readValue(conversationsJson, new TypeReference<Map<String, Object>>() {});
            List<Map<String, Object>> conversationsData = (List<Map<String, Object>>) conversationsResult.get("data");

            List<ConversationDTO> conversations = new ArrayList<>();
            if (conversationsData != null && !conversationsData.isEmpty()) {
                for (Map<String, Object> conv : conversationsData) {
                    ConversationDTO dto = objectMapper.convertValue(conv, ConversationDTO.class);
                    conversations.add(dto);
                }
            }

            return conversations;
        } catch (Exception e) {
            throw new BusinessRuntimeException("获取会话列表失败: " + e.getMessage());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    /**
     * 根据会话ID获取聊天记录（公共方法）
     * @param agentId 智能体ID
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @return 消息列表
     */
    @Override
    public List<MessageDTO> getMessagesByConversationId(String agentId, Long userId, String conversationId) {
        CloseableHttpClient httpClient = null;
        try {
            // 根据 agentId 获取智能体信息
            AgentDTO agent = agentService.getAgentById(agentId);
            if (agent == null) {
                throw new BusinessRuntimeException("智能体不存在");
            }
            String apiKey = agent.getApiKey();

            // 获取用户账号
            String userCount = getUserAccount(userId);

            httpClient = HttpClients.createDefault();

            List<MessageDTO> messages = new ArrayList<>();
            if (conversationId != null && !conversationId.isEmpty()) {
                String messagesUrl = difyUrl + "/messages?user=" + userCount + "&conversation_id=" + conversationId + "&limit=20";
                HttpGet messagesGet = new HttpGet(messagesUrl);
                messagesGet.setHeader("Authorization", "Bearer " + apiKey);

                HttpResponse messagesResponse = httpClient.execute(messagesGet);
                HttpEntity messagesEntity = messagesResponse.getEntity();
                String messagesJson = EntityUtils.toString(messagesEntity, StandardCharsets.UTF_8);

                Map<String, Object> messagesResult = objectMapper.readValue(messagesJson, new TypeReference<Map<String, Object>>() {});
                List<Map<String, Object>> messagesData = (List<Map<String, Object>>) messagesResult.get("data");

                if (messagesData != null) {
                    for (Map<String, Object> msg : messagesData) {
                        MessageDTO dto = objectMapper.convertValue(msg, MessageDTO.class);
                        messages.add(dto);
                    }
                }
            }

            return messages;
        } catch (Exception e) {
            throw new BusinessRuntimeException("获取聊天记录失败: " + e.getMessage());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    @Override
    public ChatHistoryResponseDTO getChatHistory(String agentId, Long userId) {
        try {
            ChatHistoryResponseDTO response = new ChatHistoryResponseDTO();

            // 1. 获取会话列表
            List<ConversationDTO> conversations = getConversations(agentId, userId);
            response.setConversations(conversations);

            // 2. 获取最新会话的ID，然后获取历史记录
            String conversationId = null;
            if (!conversations.isEmpty()) {
                conversationId = conversations.get(0).getId();
            }

            // 3. 获取历史记录
            List<MessageDTO> messages = new ArrayList<>();
            if (conversationId != null && !conversationId.isEmpty()) {
                messages = getMessagesByConversationId(agentId, userId, conversationId);
            }
            response.setMessages(messages);

            return response;
        } catch (Exception e) {
            throw new BusinessRuntimeException("获取聊天历史失败: " + e.getMessage());
        }
    }

    /**
     * 停止聊天响应（调用外部停止接口）
     * @param agentId 智能体ID
     * @param userId 用户ID
     * @param taskId 任务ID（流式返回中的 message_id / task_id）
     */
    @Override
    public void stopMessage(String agentId, Long userId, String taskId) {
        CloseableHttpClient httpClient = null;
        try {
            // 根据 agentId 获取智能体信息
            AgentDTO agent = agentService.getAgentById(agentId);
            if (agent == null) {
                throw new BusinessRuntimeException("智能体不存在");
            }
            String apiKey = agent.getApiKey();

            // 获取用户账号，需与发送消息时保持一致
            String userAccount = getUserAccount(userId);

            httpClient = HttpClients.createDefault();

            // 构建停止接口地址：{dify.url}/chat-messages/{task_id}/stop
            String stopUrl = difyUrl + "/chat-messages/" + taskId + "/stop";
            HttpPost httpPost = new HttpPost(stopUrl);
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("user", userAccount);

            String jsonBody = objectMapper.writeValueAsString(body);
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            String responseStr = responseEntity != null
                    ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                    : null;

//            if (statusCode < 200 || statusCode >= 300) {
//                throw new BusinessRuntimeException("调用停止响应接口失败, 状态码:" + statusCode);
//            }

            if (responseStr != null && !responseStr.isEmpty()) {
                Map<String, Object> result = objectMapper.readValue(responseStr, Map.class);
                Object r = result.get("result");
                if (r == null || !"success".equals(String.valueOf(r))) {
                    throw new BusinessRuntimeException("停止响应失败");
                }
            }
        } catch (BusinessRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessRuntimeException("调用停止响应接口异常: " + e.getMessage());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    /**
     * 消息反馈（点赞 / 点踩 / 撤销点赞）
     * 调用外部接口：POST {dify.url}/messages/{message_id}/feedbacks
     */
    @Override
    public void feedbackMessage(String agentId, Long userId, String messageId, String rating, String content) {
        CloseableHttpClient httpClient = null;
        try {
            // 根据 agentId 获取智能体信息
            AgentDTO agent = agentService.getAgentById(agentId);
            if (agent == null) {
                throw new BusinessRuntimeException("智能体不存在");
            }
            String apiKey = agent.getApiKey();

            // 获取用户账号，需与发送消息时保持一致
            String userAccount = getUserAccount(userId);

            httpClient = HttpClients.createDefault();

            String feedbackUrl = difyUrl + "/messages/" + messageId + "/feedbacks";
            HttpPost httpPost = new HttpPost(feedbackUrl);
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            // rating: like / dislike / null（撤销点赞）
            body.put("rating", rating);
            body.put("user", userAccount);
            body.put("content", content);

            String jsonBody = objectMapper.writeValueAsString(body);
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            String responseStr = responseEntity != null
                    ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                    : null;

//            if (statusCode < 200 || statusCode >= 300) {
//                throw new BusinessRuntimeException("调用消息反馈接口失败, 状态码:" + statusCode);
//            }

            if (responseStr != null && !responseStr.isEmpty()) {
                Map<String, Object> result = objectMapper.readValue(responseStr, Map.class);
                Object r = result.get("result");
                if (r == null || !"success".equals(String.valueOf(r))) {
                    throw new BusinessRuntimeException("消息反馈失败");
                }
            }
        } catch (BusinessRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessRuntimeException("调用消息反馈接口异常: " + e.getMessage());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    /**
     * 删除会话
     * 调用外部接口：DELETE {dify.url}/conversations/{conversation_id}
     */
    @Override
    public void deleteConversation(String agentId, Long userId, String conversationId) {
        CloseableHttpClient httpClient = null;
        try {
            // 根据 agentId 获取智能体信息
            AgentDTO agent = agentService.getAgentById(agentId);
            if (agent == null) {
                throw new BusinessRuntimeException("智能体不存在");
            }
            String apiKey = agent.getApiKey();

            // 获取用户账号，需与发送消息时保持一致
            String userAccount = getUserAccount(userId);

            httpClient = HttpClients.createDefault();

            // 构建删除接口地址：{dify.url}/conversations/{conversation_id}
            String deleteUrl = difyUrl + "/conversations/" + conversationId;

            // 创建支持 body 的 DELETE 请求
            HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(deleteUrl);
            httpDelete.setHeader("Authorization", "Bearer " + apiKey);
            httpDelete.setHeader("Content-Type", "application/json");

            // DELETE 请求的 body
            Map<String, Object> body = new HashMap<>();
            body.put("user", userAccount);

            String jsonBody = objectMapper.writeValueAsString(body);
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            httpDelete.setEntity(entity);

            HttpResponse response = httpClient.execute(httpDelete);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            String responseStr = responseEntity != null
                    ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                    : null;

//            if (statusCode < 200 || statusCode >= 300) {
//                throw new BusinessRuntimeException("调用删除会话接口失败, 状态码:" + statusCode);
//            }

            if (responseStr != null && !responseStr.isEmpty()) {
                Map<String, Object> result = objectMapper.readValue(responseStr, Map.class);
                Object r = result.get("result");
                if (r == null || !"success".equals(String.valueOf(r))) {
                    throw new BusinessRuntimeException("删除会话失败");
                }
            }
        } catch (BusinessRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessRuntimeException("调用删除会话接口异常: " + e.getMessage());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    /**
     * 重命名会话
     * 调用外部接口：POST {dify.url}/conversations/{conversation_id}/name
     */
    @Override
    public ConversationDTO renameConversation(String agentId, Long userId, String conversationId, String name) {
        CloseableHttpClient httpClient = null;
        try {
            // 根据 agentId 获取智能体信息
            AgentDTO agent = agentService.getAgentById(agentId);
            if (agent == null) {
                throw new BusinessRuntimeException("智能体不存在");
            }
            String apiKey = agent.getApiKey();

            // 获取用户账号，需与发送消息时保持一致
            String userAccount = getUserAccount(userId);

            httpClient = HttpClients.createDefault();

            // 构建重命名接口地址：{dify.url}/conversations/{conversation_id}/name
            String renameUrl = difyUrl + "/conversations/" + conversationId + "/name";
            HttpPost httpPost = new HttpPost(renameUrl);
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");

            // 构建请求 body
            Map<String, Object> body = new HashMap<>();
            if (name != null && !name.trim().isEmpty()) {
                body.put("name", name);
                body.put("auto_generate", false);
            } else {
                body.put("auto_generate", true);
            }
            body.put("user", userAccount);

            String jsonBody = objectMapper.writeValueAsString(body);
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            String responseStr = responseEntity != null
                    ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                    : null;

//            if (statusCode < 200 || statusCode >= 300) {
//                throw new BusinessRuntimeException("调用重命名会话接口失败, 状态码:" + statusCode);
//            }

            if (responseStr == null || responseStr.isEmpty()) {
                throw new BusinessRuntimeException("重命名会话失败：响应为空");
            }

            // 解析响应
            Map<String, Object> result = objectMapper.readValue(responseStr, Map.class);
            ConversationDTO conversationDTO = objectMapper.convertValue(result, ConversationDTO.class);

            return conversationDTO;
        } catch (BusinessRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessRuntimeException("调用重命名会话接口异常: " + e.getMessage());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    /**
     * 获取智能体参数（开场白、推荐问题等）
     * 调用外部接口：GET {dify.url}/parameters
     */
    @Override
    public AgentParametersDTO getAgentParameters(String agentId, Long userId) {
        CloseableHttpClient httpClient = null;
        try {
            // 根据 agentId 获取智能体信息
            AgentDTO agent = agentService.getAgentById(agentId);
            if (agent == null) {
                throw new BusinessRuntimeException("智能体不存在");
            }
            String apiKey = agent.getApiKey();

            httpClient = HttpClients.createDefault();

            // 构建获取参数接口地址：{dify.url}/parameters
            String parametersUrl = difyUrl + "/parameters";
            HttpGet httpGet = new HttpGet(parametersUrl);
            httpGet.setHeader("Authorization", "Bearer " + apiKey);
            httpGet.setHeader("Content-Type", "application/json");

            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            String responseStr = responseEntity != null
                    ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                    : null;

//            if (statusCode < 200 || statusCode >= 300) {
//                throw new BusinessRuntimeException("调用获取智能体参数接口失败, 状态码:" + statusCode);
//            }

            if (responseStr == null || responseStr.isEmpty()) {
                throw new BusinessRuntimeException("获取智能体参数失败：响应为空");
            }

            // 解析响应
            Map<String, Object> result = objectMapper.readValue(responseStr, Map.class);
            AgentParametersDTO parametersDTO = objectMapper.convertValue(result, AgentParametersDTO.class);

            return parametersDTO;
        } catch (BusinessRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessRuntimeException("调用获取智能体参数接口异常: " + e.getMessage());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    @Override
    public ChatFileDTO upload(MultipartFile fileArray,Long userId) {
        // ========== 1. 严格校验参数（核心：确保文件非空且可读） ==========
        if (fileArray == null || fileArray.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空，且必须包含有效内容");
        }
        // 校验文件大小（防止空文件）
        if (fileArray.getSize() <= 0) {
            throw new IllegalArgumentException("文件大小为0，无法上传");
        }

        // ========== 2. 精准获取文件 ContentType（严格匹配 Dify 要求） ==========
        String fileExt = getFileExtension(fileArray.getOriginalFilename());
        String targetContentType = getValidContentType(fileExt);

        // ========== 3. 创建 HttpClient（禁用默认连接池，避免流复用问题） ==========
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .disableConnectionState() // 解决流传输异常问题
                .build()) {

            // 查询单据智能体
            AgentDTO agentDTO = agentService.getAgentByAgentCode("BILL");
            if (agentDTO == null) {
                throw new BusinessRuntimeException("未查询到智能体");
            }
            String uri = difyUrl+"/files/upload";
            String apiKey = agentDTO.getApiKey();
            HttpPost httpPost = new HttpPost(uri);

            // ========== 4. 设置请求头（必须按 Dify 要求） ==========
            httpPost.addHeader("Authorization", "Bearer " + apiKey);
            // 不手动设置 Content-Type，让 HttpClient 自动生成（包含正确的 boundary）
            // httpPost.addHeader("Content-Type", "multipart/form-data"); // 禁止手动设置！

            // ========== 5. 构建请求体（核心修复：保证文件流不提前关闭） ==========
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            // 设置编码（防止中文文件名乱码）
            builder.setCharset(StandardCharsets.UTF_8);
            // 模拟浏览器表单提交格式（关键：解决 Dify 识别不到文件的问题）
            builder.setContentType(ContentType.MULTIPART_FORM_DATA);

            // 5.1 添加文件字段（核心：流不关闭，直到请求发送完成）
            // ！！！注意：不要用 try-with-resources 包裹这里的 InputStream，会提前关闭！！！
            InputStream fileInputStream = fileArray.getInputStream();
            builder.addBinaryBody(
                    "file", // 字段名必须是 file（Dify 强制要求）
                    fileInputStream,
                    ContentType.create(targetContentType), // 精准的文件类型
                    fileArray.getOriginalFilename() // 保留原文件名（Dify 会校验文件名后缀）
            );

            // 5.2 添加 user 字段（必须在文件字段之后，部分服务对字段顺序敏感）
            builder.addTextBody(
                    "user",
                    userId!=null? String.valueOf(userId) :"abc-123",
                    ContentType.TEXT_PLAIN
            );

            // ========== 6. 构建并设置请求体 ==========
            HttpEntity requestEntity = builder.build();
            httpPost.setEntity(requestEntity);

            // ========== 7. 执行请求并处理响应 ==========
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // 打印响应信息（用于调试）
                HttpEntity responseEntity = response.getEntity();
                String responseContent = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                EntityUtils.consume(responseEntity);

                // 关闭文件流（请求发送完成后再关闭）
                fileInputStream.close();
                // 解析响应
                Map<String, Object> result = objectMapper.readValue(responseContent, Map.class);
                ChatFileDTO chatFileDTO = objectMapper.convertValue(result, ChatFileDTO.class);
                chatFileDTO.setEventData(responseContent);
                return chatFileDTO;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过上传文件返回的fileID查询识别内容
     * @param uploadFileId
     * @param emitter
     * @param userId
     */
    @Override
    public void workflowsStream(String uploadFileId, SseEmitter emitter, Long userId) {
        CloseableHttpClient httpClient = null;
        BufferedReader reader = null;
        AtomicBoolean emitterCompleted = new AtomicBoolean(false);

        // 配置HTTP客户端超时（避免第三方接口无限阻塞）
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000) // 连接超时30秒
                .setSocketTimeout(550000) // 读取超时550秒（比SSE超时少50秒，预留处理时间）
                .build();

        try {
            // 1. 构建HTTP请求（增加超时配置）
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build(); // 替换默认客户端，使用带超时的配置
            HttpPost httpPost = new HttpPost("http://114.215.173.72:10903/v1/workflows/run");
            httpPost.setHeader("Authorization", "Bearer " + "app-DuRD6AWG2eQQohDE6uxdrpNf");
            httpPost.setHeader("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            // 构建inputs对象
            Map<String, Object> inputs = new HashMap<>();
            Map<String, Object> imageUrl = new HashMap<>();
            imageUrl.put("type","image");
            imageUrl.put("transfer_method","local_file");
            imageUrl.put("url","");
            imageUrl.put("upload_file_id",uploadFileId);
            inputs.put("billType","交接清单");
            inputs.put("imageUrl",imageUrl);
            requestBody.put("inputs", inputs);
            requestBody.put("response_mode", "streaming");
            requestBody.put("user", userId!=null?String.valueOf(userId):"abc-123");

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            // 2. 执行请求并处理流式响应
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity == null) {
                if (emitterCompleted.compareAndSet(false, true)) {
                    try {
                        emitter.completeWithError(new BusinessRuntimeException("接口返回值为空"));
                    } catch (Exception e) {
                        LOGGER.warn("发送返回值为空错误失败" + e);
                    }
                }
                return;
            }

            InputStream inputStream = responseEntity.getContent();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            String responseWorkflowRunId = null;
            String responseMessageId = null;
            String responseTaskId = null;
            boolean messageEndReceived = false;

            // 3. 逐行读取并发送流式数据
            while ((line = reader.readLine()) != null) {
                if (emitterCompleted.get()) {
                    break;
                }

                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) {
                    continue;
                }

                if (trimmedLine.startsWith("data:")) {
                    try {
                        String jsonStr = trimmedLine.substring(5).trim();
                        if (jsonStr.isEmpty() || "null".equals(jsonStr)) {
                            continue;
                        }

                        Map<String, Object> json = objectMapper.readValue(jsonStr, Map.class);
                        if (!json.containsKey("data")) {
                            break;
                        }
                        Map<String, Object> dataMap = (Map<String, Object>) json.get("data");

                        if ("workflow_finished".equals(json.get("event"))) {
                            messageEndReceived = true;
                            responseWorkflowRunId = json.containsKey("workflow_run_id") ? String.valueOf(json.get("workflow_run_id")) : null;
                            responseTaskId = json.containsKey("task_id") && responseTaskId == null ? String.valueOf(json.get("task_id")) : responseTaskId;

                            Map<String, Object> endEvent = new HashMap<>();
                            endEvent.put("event", "end");
                            endEvent.put("workflow_run_id", responseWorkflowRunId);
                            if (responseTaskId != null) {
                                endEvent.put("task_id", responseTaskId);
                            }
                            // 透传检索引用信息（retriever_resources），用于前端展示“引用”列表
                            // Dify 文档：retriever_resources 在 message_end 事件中；有的版本可能在顶层，有的在 metadata 内
                            Object retrieverResources = null;
                            if (json.containsKey("data")) {
                                retrieverResources = json.get("data");
                            } else if (json.containsKey("data")) {
                                try {
                                    Object metaObj = json.get("data");
                                    if (metaObj instanceof Map) {
                                        Map<?, ?> metaMap = (Map<?, ?>) metaObj;
                                        if (metaMap.containsKey("outputs")) {
                                            retrieverResources = metaMap.get("outputs");
                                        }
                                    }
                                } catch (Exception ignored) {
                                    // 忽略解析 metadata 的异常，避免影响主流程
                                }
                            }
                            if (retrieverResources != null) {
                                endEvent.put("outputs", retrieverResources);
                            }
                            emitter.send(SseEmitter.event()
                                    .name("end")
                                    .data(objectMapper.writeValueAsString(endEvent)));

                            emitterCompleted.set(true);
                            break;
                        }

                        if ("text_chunk".equals(json.get("event")) && dataMap.containsKey("text")) {
                            String answer = String.valueOf(dataMap.get("text"));
                            if (answer != null && !"null".equals(answer) && !answer.isEmpty()) {
                                if (responseWorkflowRunId == null && json.containsKey("workflow_run_id")) {
                                    responseWorkflowRunId = String.valueOf(json.get("workflow_run_id"));
                                }
                                if (responseTaskId == null && json.containsKey("task_id")) {
                                    responseTaskId = String.valueOf(json.get("task_id"));
                                }

                                Map<String, Object> data = new HashMap<>();
                                data.put("content", answer);
                                data.put("workflow_run_id", responseWorkflowRunId);
                                if (responseTaskId != null) {
                                    data.put("task_id", responseTaskId);
                                }

                                emitter.send(SseEmitter.event()
                                        .name("message")
                                        .data(objectMapper.writeValueAsString(data)));
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("处理流式数据失败" + e);
                        emitterCompleted.set(true);
                        break;
                    }
                }
            }

            // 4. 正常结束处理
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    if (messageEndReceived) {
                        emitter.complete();
                    } else {
                        emitter.completeWithError(new BusinessRuntimeException("未收到完整的响应结束标记"));
                    }
                } catch (Exception e) {
                    LOGGER.warn("正常结束时关闭emitter失败" + e);
                }
            }

        } catch (SocketTimeoutException e) {
            // 捕获HTTP读取超时，主动提示用户
            LOGGER.error("第三方接口响应超时" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"msg\":\"大模型响应超时，请稍后重试\"}"));
                    emitter.complete();
                } catch (Exception ex) {
                    LOGGER.warn("超时提示发送失败" + ex);
                }
            }
        } catch (AuthorizationDeniedException e) {
            // 专门捕获权限异常
            LOGGER.error("异步线程权限拒绝" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"msg\":\"权限不足，请重新登录后重试\"}"));
                } catch (Exception ex) {
                    LOGGER.warn("权限异常提示发送失败"+ ex);
                }
            }
        } catch (IOException e) {
            LOGGER.error("网络IO异常" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.completeWithError(new BusinessRuntimeException("网络连接异常: " + e.getMessage()));
                } catch (Exception ex) {
                    LOGGER.warn("IO异常时关闭emitter失败" + ex);
                }
            }
        } catch (Exception e) {
            LOGGER.error("调用聊天接口失败" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.completeWithError(new BusinessRuntimeException("调用聊天接口失败: " + e.getMessage()));
                } catch (Exception ex) {
                    LOGGER.warn("业务异常时关闭emitter失败" + ex);
                }
            }
        } finally {
            // 5. 资源关闭
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn("关闭流读取器失败" + e);
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    LOGGER.warn("关闭HTTP客户端失败" + e);
                }
            }
            // 最终兜底
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    LOGGER.warn("兜底关闭emitter失败" + e);
                }
            }
        }
    }

    @Override
    public ChatFileDTO workflowsRun(String uploadFileId, Long userId) {
        CloseableHttpClient httpClient = null;

        // 配置HTTP客户端超时（避免第三方接口无限阻塞）
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000) // 连接超时30秒
                .setSocketTimeout(550000) // 读取超时550秒（比SSE超时少50秒，预留处理时间）
                .build();
        try {
            // 查询单据智能体
            AgentDTO agentDTO = agentService.getAgentByAgentCode("BILL");
            if (agentDTO == null) {
                throw new BusinessRuntimeException("未查询到智能体");
            }
            String uri = difyUrl+"/workflows/run";
            String apiKey = agentDTO.getApiKey();
            // 1. 构建HTTP请求（增加超时配置）
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build(); // 替换默认客户端，使用带超时的配置
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            // 构建inputs对象
            Map<String, Object> inputs = new HashMap<>();
            Map<String, Object> imageUrl = new HashMap<>();
            imageUrl.put("type","image");
            imageUrl.put("transfer_method","local_file");
            imageUrl.put("url","");
            imageUrl.put("upload_file_id",uploadFileId);
            inputs.put("billType","交接清单");
            inputs.put("imageUrl",imageUrl);
            requestBody.put("inputs", inputs);
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", userId!=null?String.valueOf(userId):"abc-123");

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            // 2. 执行请求并处理流式响应
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity messagesEntity = response.getEntity();
            String messagesJson = EntityUtils.toString(messagesEntity, StandardCharsets.UTF_8);

            Map<String, Object> messagesResult = objectMapper.readValue(messagesJson, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> messagesData = (Map<String, Object>) messagesResult.get("data");
            Map<String, Object> messagesContent = (Map<String, Object>) messagesData.get("outputs");
            if (messagesContent != null) {
                ChatFileDTO dto = objectMapper.convertValue(messagesContent, ChatFileDTO.class);
                dto.setEventData((String) messagesContent.get("text"));
                return dto;
            }
            return null;

        } catch (Exception e) {
            throw new BusinessRuntimeException("获取识别内容失败: " + e.getMessage());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    // 非法字符正则：匹配非字母、数字、下划线、中横线、点的字符
    private static final Pattern INVALID_FILENAME_PATTERN = Pattern.compile("[^\\\\u4e00-\\\\u9fa5a-zA-Z0-9_\\\\-\\\\.]");
    // 文件名最大长度（Dify 建议不超过 255 个字符）
    private static final int MAX_FILENAME_LENGTH = 200;
    @Override
    public ChatFileDTO contractUpload(MultipartFile fileArray,Long userId) {
        // ========== 1. 严格校验参数（核心：确保文件非空且可读） ==========
        if (fileArray == null || fileArray.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空，且必须包含有效内容");
        }
        // 校验文件大小（防止空文件）
        if (fileArray.getSize() <= 0) {
            throw new IllegalArgumentException("文件大小为0，无法上传");
        }

        String originalFilename = fileArray.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            // 如果原文件名为空，生成默认文件名（带合法后缀，避免扩展名校验失败）
            originalFilename = "contract_" + System.currentTimeMillis() + ".pdf";
        }
        // 清洗文件名：移除非法字符、截断超长名称、处理路径分隔符
        String sanitizedFilename = sanitizeFilename(originalFilename);

        // ========== 2. 精准获取文件 ContentType（严格匹配 Dify 要求） ==========
        String fileExt = getFileExtension(sanitizedFilename);
        String targetContentType = getValidWordContentType(fileExt);

        // ========== 3. 创建 HttpClient（禁用默认连接池，避免流复用问题） ==========
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .disableConnectionState() // 解决流传输异常问题
                .build()) {

            // 查询单据智能体
            AgentDTO agentDTO = agentService.getAgentByAgentCode("CONTRACT");
            if (agentDTO == null) {
                throw new BusinessRuntimeException("未查询到智能体");
            }
            String uri = difyUrl+"/files/upload";
            String apiKey = agentDTO.getApiKey();
            HttpPost httpPost = new HttpPost(uri);

            // ========== 4. 设置请求头（必须按 Dify 要求） ==========
            httpPost.addHeader("Authorization", "Bearer " + apiKey);
            // 不手动设置 Content-Type，让 HttpClient 自动生成（包含正确的 boundary）
            // httpPost.addHeader("Content-Type", "multipart/form-data"); // 禁止手动设置！

            // ========== 5. 构建请求体（核心修复：保证文件流不提前关闭） ==========
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            // 设置编码（防止中文文件名乱码）
            builder.setCharset(StandardCharsets.UTF_8);
            // 模拟浏览器表单提交格式（关键：解决 Dify 识别不到文件的问题）
            builder.setContentType(ContentType.MULTIPART_FORM_DATA);

            // 5.1 添加文件字段（核心：流不关闭，直到请求发送完成）
            // ！！！注意：不要用 try-with-resources 包裹这里的 InputStream，会提前关闭！！！
            InputStream fileInputStream = fileArray.getInputStream();
            builder.addBinaryBody(
                    "file", // 字段名必须是 file（Dify 强制要求）
                    fileInputStream,
                    ContentType.create(targetContentType), // 精准的文件类型
                    "1"+sanitizedFilename // 保留原文件名（Dify 会校验文件名后缀）
            );

            // 5.2 添加 user 字段（必须在文件字段之后，部分服务对字段顺序敏感）
            builder.addTextBody(
                    "user",
                    userId!=null? String.valueOf(userId) :"abc-123",
                    ContentType.TEXT_PLAIN
            );

            // ========== 6. 构建并设置请求体 ==========
            HttpEntity requestEntity = builder.build();
            httpPost.setEntity(requestEntity);

            // ========== 7. 执行请求并处理响应 ==========
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // 打印响应信息（用于调试）
                HttpEntity responseEntity = response.getEntity();
                String responseContent = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                EntityUtils.consume(responseEntity);

                // 关闭文件流（请求发送完成后再关闭）
                fileInputStream.close();
                // 解析响应
                Map<String, Object> result = objectMapper.readValue(responseContent, Map.class);
                ChatFileDTO chatFileDTO = objectMapper.convertValue(result, ChatFileDTO.class);
                chatFileDTO.setEventData(responseContent);
                return chatFileDTO;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过上传文件返回的fileID查询识别内容 -- 合同审查
     * @param uploadFileId
     * @param emitter
     * @param userId
     */
    @Override
    public void chatMessage(String uploadFileId, SseEmitter emitter, Long userId) {
        CloseableHttpClient httpClient = null;
        BufferedReader reader = null;
        AtomicBoolean emitterCompleted = new AtomicBoolean(false);

        // 配置HTTP客户端超时（避免第三方接口无限阻塞）
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000) // 连接超时30秒
                .setSocketTimeout(550000) // 读取超时550秒（比SSE超时少50秒，预留处理时间）
                .build();

        try {
            // 1. 构建HTTP请求（增加超时配置）
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build(); // 替换默认客户端，使用带超时的配置
            // 查询单据智能体
            AgentDTO agentDTO = agentService.getAgentByAgentCode("CONTRACT");
            if (agentDTO == null) {
                throw new BusinessRuntimeException("未查询到智能体");
            }
            String uri = difyUrl+"/chat-messages";
            String apiKey = agentDTO.getApiKey();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> objectMap = new HashMap<>();
            requestBody.put("inputs", new HashMap<>());
            requestBody.put("query", "合同审查");
            requestBody.put("response_mode", "streaming");
            requestBody.put("user", userId!=null?String.valueOf(userId):"abc-123");
            ArrayList arrayList = new ArrayList<>();
            objectMap.put("type","document");
            objectMap.put("transfer_method","local_file");
            objectMap.put("upload_file_id",uploadFileId);
            objectMap.put("url","");
            arrayList.add(objectMap);
            requestBody.put("files", arrayList);

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            // 2. 执行请求并处理流式响应
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity == null) {
                if (emitterCompleted.compareAndSet(false, true)) {
                    try {
                        emitter.completeWithError(new BusinessRuntimeException("接口返回值为空"));
                    } catch (Exception e) {
                        LOGGER.warn("发送返回值为空错误失败" + e);
                    }
                }
                return;
            }

            InputStream inputStream = responseEntity.getContent();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            String responseConversationId = null;
            String responseMessageId = null;
            String responseTaskId = null;
            boolean messageEndReceived = false;

            // 5. 逐行读取并发送流式数据
            while ((line = reader.readLine()) != null) {
                if (emitterCompleted.get()) {
                    break;
                }

                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) {
                    continue;
                }

                if (trimmedLine.startsWith("data:")) {
                    try {
                        String jsonStr = trimmedLine.substring(5).trim();
                        if (jsonStr.isEmpty() || "null".equals(jsonStr)) {
                            continue;
                        }

                        Map<String, Object> json = objectMapper.readValue(jsonStr, Map.class);

                        if ("message_end".equals(json.get("event"))) {
                            messageEndReceived = true;
                            responseConversationId = json.containsKey("conversation_id") ? String.valueOf(json.get("conversation_id")) : null;
                            responseMessageId = json.containsKey("message_id") ? String.valueOf(json.get("message_id")) : null;
                            responseTaskId = json.containsKey("task_id") && responseTaskId == null ? String.valueOf(json.get("task_id")) : responseTaskId;

                            Map<String, Object> endEvent = new HashMap<>();
                            endEvent.put("event", "end");
                            endEvent.put("conversation_id", responseConversationId);
                            endEvent.put("message_id", responseMessageId);
                            if (responseTaskId != null) {
                                endEvent.put("task_id", responseTaskId);
                            }
                            // 透传检索引用信息（retriever_resources），用于前端展示“引用”列表
                            // Dify 文档：retriever_resources 在 message_end 事件中；有的版本可能在顶层，有的在 metadata 内
                            Object retrieverResources = null;
                            if (json.containsKey("retriever_resources")) {
                                retrieverResources = json.get("retriever_resources");
                            } else if (json.containsKey("metadata")) {
                                try {
                                    Object metaObj = json.get("metadata");
                                    if (metaObj instanceof Map) {
                                        Map<?, ?> metaMap = (Map<?, ?>) metaObj;
                                        if (metaMap.containsKey("retriever_resources")) {
                                            retrieverResources = metaMap.get("retriever_resources");
                                        }
                                    }
                                } catch (Exception ignored) {
                                    // 忽略解析 metadata 的异常，避免影响主流程
                                }
                            }
                            if (retrieverResources != null) {
                                endEvent.put("retriever_resources", retrieverResources);
                            }
                            emitter.send(SseEmitter.event()
                                    .name("end")
                                    .data(objectMapper.writeValueAsString(endEvent)));

                            emitterCompleted.set(true);
                            break;
                        }

                        if ("message".equals(json.get("event")) && json.containsKey("answer")) {
                            String answer = String.valueOf(json.get("answer"));
                            if (answer != null && !"null".equals(answer) && !answer.isEmpty()) {
                                if (responseConversationId == null && json.containsKey("conversation_id")) {
                                    responseConversationId = String.valueOf(json.get("conversation_id"));
                                }
                                if (responseMessageId == null && json.containsKey("message_id")) {
                                    responseMessageId = String.valueOf(json.get("message_id"));
                                }
                                if (responseTaskId == null && json.containsKey("task_id")) {
                                    responseTaskId = String.valueOf(json.get("task_id"));
                                }

                                Map<String, Object> data = new HashMap<>();
                                data.put("content", answer);
                                data.put("conversation_id", responseConversationId);
                                data.put("message_id", responseMessageId);
                                if (responseTaskId != null) {
                                    data.put("task_id", responseTaskId);
                                }

                                emitter.send(SseEmitter.event()
                                        .name("message")
                                        .data(objectMapper.writeValueAsString(data)));
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("处理流式数据失败" + e);
                        emitterCompleted.set(true);
                        break;
                    }
                }
            }

            // 4. 正常结束处理
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    if (messageEndReceived) {
                        emitter.complete();
                    } else {
                        emitter.completeWithError(new BusinessRuntimeException("未收到完整的响应结束标记"));
                    }
                } catch (Exception e) {
                    LOGGER.warn("正常结束时关闭emitter失败" + e);
                }
            }

        } catch (SocketTimeoutException e) {
            // 捕获HTTP读取超时，主动提示用户
            LOGGER.error("第三方接口响应超时" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"msg\":\"大模型响应超时，请稍后重试\"}"));
                    emitter.complete();
                } catch (Exception ex) {
                    LOGGER.warn("超时提示发送失败" + ex);
                }
            }
        } catch (AuthorizationDeniedException e) {
            // 专门捕获权限异常
            LOGGER.error("异步线程权限拒绝" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"msg\":\"权限不足，请重新登录后重试\"}"));
                } catch (Exception ex) {
                    LOGGER.warn("权限异常提示发送失败"+ ex);
                }
            }
        } catch (IOException e) {
            LOGGER.error("网络IO异常" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.completeWithError(new BusinessRuntimeException("网络连接异常: " + e.getMessage()));
                } catch (Exception ex) {
                    LOGGER.warn("IO异常时关闭emitter失败" + ex);
                }
            }
        } catch (Exception e) {
            LOGGER.error("调用聊天接口失败" + e);
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.completeWithError(new BusinessRuntimeException("调用聊天接口失败: " + e.getMessage()));
                } catch (Exception ex) {
                    LOGGER.warn("业务异常时关闭emitter失败" + ex);
                }
            }
        } finally {
            // 5. 资源关闭
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn("关闭流读取器失败" + e);
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    LOGGER.warn("关闭HTTP客户端失败" + e);
                }
            }
            // 最终兜底
            if (emitterCompleted.compareAndSet(false, true)) {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    LOGGER.warn("兜底关闭emitter失败" + e);
                }
            }
        }
    }

    /**
     * 获取文件后缀（如 test.png → png）
     */
    private static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("文件名无效，缺少后缀（如 .png/.jpg）");
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    // Dify 支持的图片类型
    private static final String[] SUPPORTED_IMAGE_TYPES = {"png", "jpeg", "jpg", "webp", "gif"};

    /**
     * 校验并返回 Dify 支持的 ContentType
     */
    private static String getValidContentType(String ext) {
        for (String type : SUPPORTED_IMAGE_TYPES) {
            if (type.equals(ext)) {
                // jpg 兼容为 jpeg
                return "jpg".equals(ext) ? "image/jpeg" : "image/" + ext;
            }
        }
        throw new IllegalArgumentException("不支持的文件类型：" + ext + "，仅支持：" + String.join(",", SUPPORTED_IMAGE_TYPES));
    }
    // Dify 支持的文档类型
    private static final String[] SUPPORTED_WORD_TYPES = {"text/plain", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
    /**
     * 校验并返回 Dify 支持的 ContentType
     */
    private static String getValidWordContentType(String ext) {
        switch (ext) {
            case "txt":
                return SUPPORTED_WORD_TYPES[0];
            case "pdf":
                return SUPPORTED_WORD_TYPES[1];
            case "doc":
                return SUPPORTED_WORD_TYPES[2];
            case "docx":
                return SUPPORTED_WORD_TYPES[3];
            default:
                throw new IllegalArgumentException("不支持的文件类型：" + ext + "，仅支持：" + String.join(",", SUPPORTED_IMAGE_TYPES));
        }
    }

    /**
     * 支持 body 的 DELETE 请求类
     */
    private static class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "DELETE";

        public HttpDeleteWithBody(final String uri) {
            super();
            setURI(java.net.URI.create(uri));
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }
    }

    /**
     * 清洗文件名，移除非法字符，解决 Dify 报 Filename contains invalid characters 问题
     * @param originalFilename 原始文件名
     * @return 清洗后的合法文件名
     */
    private String sanitizeFilename(String originalFilename) {
        // 兜底：原始文件名为空时返回默认文件名
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return "contract_" + System.currentTimeMillis() + ".pdf";
        }

        // 步骤1：拆分主文件名和后缀（避免只保留后缀）
        String mainName;
        String ext;
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex <= 0) { // 无后缀 或 点在首位（如 .pdf）
            mainName = originalFilename;
            ext = "pdf"; // 兜底默认后缀
        } else {
            mainName = originalFilename.substring(0, lastDotIndex);
            ext = originalFilename.substring(lastDotIndex + 1);
        }

        // 步骤2：清洗主文件名（核心修改：保留中文，只过滤Dify禁止的非法字符）
        // Dify禁止的字符：\ / : * ? " < > | （路径分隔符+特殊符号）
        // 正则说明：[\u4e00-\u9fa5] 匹配中文，a-zA-Z0-9_\\-\\. 匹配字母/数字/合法符号
        // 只替换 非中文+非合法字符 的内容为下划线
        mainName = mainName.replaceAll("[^a-zA-Z0-9_\\-\\.]", "_");

        // 处理连续下划线、首尾下划线（美化）
        mainName = mainName.replaceAll("_+", "_");
        mainName = mainName.replaceAll("^[_\\.]|[_\\.]$", "");

        // 步骤3：兜底主文件名（清洗后为空则生成唯一名称）
        if (mainName.isEmpty()) {
            mainName = "合同文件_" + System.currentTimeMillis();
        }

        // 步骤4：截断超长主文件名（预留后缀长度）
        int maxMainNameLength = MAX_FILENAME_LENGTH - ext.length() - 1;
        if (mainName.length() > maxMainNameLength) {
            mainName = mainName.substring(0, maxMainNameLength);
        }

        // 步骤5：清洗后缀（只保留字母数字）
        ext = ext.replaceAll("[^a-zA-Z0-9]", "");
        if (ext.isEmpty()) {
            ext = "pdf";
        }

        // 步骤6：拼接最终文件名
        String finalFilename = mainName + "." + ext;

        // 最终兜底
        if (finalFilename.isEmpty() || finalFilename.equals(".")) {
            finalFilename = "contract_" + System.currentTimeMillis() + ".pdf";
        }

        return finalFilename;
    }
}
