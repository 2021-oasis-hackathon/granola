package spring.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import spring.server.domain.ChatMessage;
import spring.server.domain.ChatRoom;
import spring.server.domain.User;
import spring.server.repository.ChatMessageRepository;
import spring.server.repository.ChatRoomRepository;
import spring.server.repository.UserChatRoomRepository;
import spring.server.service.UserService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final UserService userService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessage message) {
        if (!ChatMessage.MessageType.JOIN.equals(message.getType())) {
//            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
            User user = userService.findById(message.getSenderId()).orElseThrow(RuntimeException::new);
            message.setUser(user.getUserInfo());
            message.setCreateTime(LocalDateTime.now());
            ChatRoom room = chatRoomRepository.findRoomById(message.getRoomId());
            room.setRenewalTime(LocalDateTime.now());
            room.setRenewalMessage(message.getMessage());
            userChatRoomRepository.updateCheckFalse(room.getId(), user.getId());
            chatRoomRepository.save(room);
            chatMessageRepository.save(message);
            messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        }
    }
}
