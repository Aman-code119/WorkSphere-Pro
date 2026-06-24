package com.example.CHAT.ANNOUNCEMENT.SERVICE.Controller;

import com.example.CHAT.ANNOUNCEMENT.SERVICE.DTO.groupchatDTO;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.DTO.individualChatDTO;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.DTO.selfchatDTO;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.Model.Announcement;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.Model.ChatType;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.Service.ChatCreate_service;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.feigncommunication.AuthServiceClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/chats")
public class ChatCreate_controller {

    @Autowired
    private ChatCreate_service service;

    @Autowired
    private AuthServiceClient authServiceClient;


    // 1/ For Self Chat
    @PostMapping("/self")
    public ResponseEntity<?> createSelfChat(@Valid @RequestBody selfchatDTO selfData) {
        try {
            // Direct Long (ID) mil rahi hai
            ChatType chat = service.createSelfchat(selfData.getUserId());
            return ResponseEntity.ok(chat);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. For 1-to-1 Chat
    @PostMapping("/individual")
    public ResponseEntity<?> createIndividualChat(@Valid @RequestBody individualChatDTO indData) {
        try {
            ChatType chat = service.createIndividualChat(indData.getSenderId(), indData.getReceiverId());
            return ResponseEntity.ok(chat);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. For Group Chat
    @PostMapping("/group")
    public ResponseEntity<?> createGroupChat(@Valid @RequestBody groupchatDTO request) {
        return ResponseEntity.ok(service.createGroupChat(request.getGroupName(), request.getCreatorId()));
    }


    // 4. fetch   old announcements lists
    @GetMapping("/announcement")
    public ResponseEntity<?> getAllAnnouncement(@RequestParam Long id) {
        try {
            // Sirf itna check karo ki kya user exist karta hai (Valid employee hai ya nahi)
            Boolean userExists = authServiceClient.checkUserExists(id);

            if (userExists == null || !userExists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User nahi mila ya valid nahi hai!"));
            }
            return ResponseEntity.ok(service.getAllAnnouncementChats());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Auth service se contact nahi ho paya: " + e.getMessage()));
        }
    }


    //5. for announcement krna
    @PostMapping("/announcement/send")
    public ResponseEntity<?> sendUrgentAnnouncement(@RequestParam String title, @RequestParam String message, @RequestParam Long senderId, @RequestParam List<Long> targetGroupIds) {
        try {
            // 1. Pehle doosri MS se bhejnewale (senderId) ka role poocho
            String userRole = authServiceClient.getUserRoleById(senderId);

            if (userRole == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Sender system me nahi mila!"));
            }

            // 2. Strict High rank filter lagaya (Sirf inko allow karenge)
            if (!userRole.equalsIgnoreCase("ADMIN") &&
                    !userRole.equalsIgnoreCase("CEO") &&
                    !userRole.equalsIgnoreCase("MANAGER") &&
                    !userRole.equalsIgnoreCase("HR")) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Aapke paas announcement BHEJNE ka hakk nahi hai! Sirf HR/CEO bhej sakte hain."));
            }


            Announcement announcement = service.broadcastAnnouncement(title, message, senderId, targetGroupIds);
            return ResponseEntity.ok(announcement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    //6.updates name of chat
    @PutMapping("/rename")
    public ResponseEntity<?> renameChat(@RequestParam Long chatId, @RequestParam String newChatName, @RequestParam Long userId) {
        try {
            ChatType updatedChat = service.updateChatName(chatId, newChatName, userId);
            return ResponseEntity.ok(updatedChat); // Naya updated chat object return karega
        } catch (RuntimeException e) {
            // Agar chat nahi mili ya unauthorized banda change kar raha h, to error jayega
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}