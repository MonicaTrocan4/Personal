package com.example.demo.services;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dtos.ChatRuleDTO;
import com.example.demo.dtos.MessageDTO;
import com.example.demo.entities.ChatRule;
import com.example.demo.entities.Message;
import com.example.demo.repositories.ChatRuleRepository;
import com.example.demo.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    private final MessageRepository messageRepository;
    private final ChatRuleRepository chatRuleRepository;
    private final RabbitTemplate rabbitTemplate;

    private final GeminiService geminiService;

    public ChatService(MessageRepository messageRepository,
                       ChatRuleRepository chatRuleRepository,
                       RabbitTemplate rabbitTemplate,
                       GeminiService geminiService) {
        this.messageRepository = messageRepository;
        this.chatRuleRepository = chatRuleRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.geminiService = geminiService;
    }

    public void sendMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(messageDTO.getIsRead() != null ? messageDTO.getIsRead() : false);

        Message savedMessage = messageRepository.save(message);

        MessageDTO savedDTO = mapToDTO(savedMessage);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_QUEUE, savedDTO);

        checkAndSendAutoReply(savedMessage);
    }

    private void checkAndSendAutoReply(Message originalMessage) {
        List<ChatRule> rules = chatRuleRepository.findAll();
        String contentLower = originalMessage.getContent().toLowerCase();

        for (ChatRule rule : rules) {
            if (contentLower.contains(rule.getKeyword().toLowerCase())) {
                LOGGER.info("Rule matched: {}", rule.getKeyword());
                sendSystemResponse(originalMessage, "[RULE]: " + rule.getResponseMessage());
                return;
            }
        }

        LOGGER.info("No rule matched. Asking Gemini AI...");
        String aiResponse = geminiService.getAIResponse(originalMessage.getContent());
        sendSystemResponse(originalMessage, "[AI]: " + aiResponse);
    }

    private void sendSystemResponse(Message originalMessage, String responseText) {
        Message autoReply = new Message();
        autoReply.setSenderId(originalMessage.getReceiverId());
        autoReply.setReceiverId(originalMessage.getSenderId());
        autoReply.setContent(responseText);
        autoReply.setTimestamp(LocalDateTime.now());
        autoReply.setRead(false);

        Message savedReply = messageRepository.save(autoReply);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_QUEUE, mapToDTO(savedReply));
    }

    public List<MessageDTO> getChatHistory(UUID userId1, UUID userId2) {
        return messageRepository.findChatHistory(userId1, userId2).stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    public ChatRuleDTO addRule(ChatRuleDTO ruleDTO) {
        ChatRule rule = new ChatRule(ruleDTO.getKeyword(), ruleDTO.getResponseMessage());
        return new ChatRuleDTO(chatRuleRepository.save(rule).getId(), rule.getKeyword(), rule.getResponseMessage());
    }
    public List<ChatRuleDTO> getAllRules() {
        return chatRuleRepository.findAll().stream().map(r -> new ChatRuleDTO(r.getId(), r.getKeyword(), r.getResponseMessage())).collect(Collectors.toList());
    }
    public void deleteRule(UUID id) { chatRuleRepository.deleteById(id); }

    private MessageDTO mapToDTO(Message m) {
        return new MessageDTO(m.getId(), m.getSenderId(), m.getReceiverId(), m.getContent(), m.getTimestamp(), m.isRead());
    }
}