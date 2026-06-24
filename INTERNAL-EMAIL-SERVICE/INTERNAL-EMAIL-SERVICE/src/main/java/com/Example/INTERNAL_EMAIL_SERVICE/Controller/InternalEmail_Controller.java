package com.Example.INTERNAL_EMAIL_SERVICE.Controller;

import com.Example.INTERNAL_EMAIL_SERVICE.Model.Internal_Emails;
import com.Example.INTERNAL_EMAIL_SERVICE.Service.InternalEmail_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/internal-emails")
public class InternalEmail_Controller {

    @Autowired
    private InternalEmail_Service emailservice;

    // 1. Send/Broadcast email (targetType parameter me "ALL", "TECH", या "HR" pass hoga)
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestParam Long senderId,
                                       @RequestParam String subject,
                                       @RequestParam String body,
                                       @RequestParam String targetType,
                                       @RequestParam(required = false) List<String> attachments) {
        try {
            String email = emailservice.broadcastEmail(senderId, subject, body, targetType, attachments);
            return ResponseEntity.ok(email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    // 2.UNDO EMAIL ACTION
    // URL: POST http://localhost:8082/api/internal-emails/undo
    @PostMapping("/undo")
    public ResponseEntity<?> undoEmail(@RequestParam String taskId) {
        boolean isUndone = emailservice.undoEmail(taskId);

        if (isUndone) {
            return ResponseEntity.ok(Map.of("message", "Email successfully retracted/undone!"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Too late! Email has already been delivered to database."));
        }
    }

    // 3.GET RECEIVED INBOX (Inbox Tab - With Read/Unread Status)
    // URL: GET http://localhost:8082/api/internal-emails/received?userId=101
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedInbox(@RequestParam Long userId) {
        try {
            List<Map<String, Object>> inbox = emailservice.getUserReceivedInbox(userId);
            return ResponseEntity.ok(inbox);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 4.GET SENT INBOX (Sent Items Tab)
    // URL: GET http://localhost:8082/api/internal-emails/sent?senderId=101
    @GetMapping("/sent")
    public ResponseEntity<?> getSentInbox(@RequestParam Long senderId) {
        try {
            List<Internal_Emails> sentItems = emailservice.getUserSentInbox(senderId);
            return ResponseEntity.ok(sentItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 5. MARK EMAIL AS READ
    // URL: POST http://localhost:8082/api/internal-emails/read
    @PostMapping("/read")
    public ResponseEntity<?> markAsRead(@RequestParam Long emailId, @RequestParam Long userId) {
        try {
            emailservice.markAsRead(emailId, userId);
            return ResponseEntity.ok(Map.of("message", "Email status updated to READ."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 6. REPLY TO AN EMAIL (With 10s Undo Buffer)
    // URL: POST http://localhost:8082/api/internal-emails/reply
    @PostMapping("/reply")
    public ResponseEntity<?> replyEmail(@RequestParam Long senderId,
                                        @RequestParam Long parentEmailId,
                                        @RequestParam String body,
                                        @RequestParam(required = false) List<String> attachment) {
        try {
            // Service method match: queueReplyEmail
            String taskId = emailservice.ReplyEmail(senderId, parentEmailId, body, attachment);
            return ResponseEntity.ok(Map.of(
                    "message", "Reply queue mein hai (10 seconds remaining for Undo)",
                    "taskId", taskId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 7.FORWARD AN EMAIL (With 10s Undo Buffer)
    // URL: POST http://localhost:8082/api/internal-emails/forward
    @PostMapping("/forward")
    public ResponseEntity<?> forwardEmail(@RequestParam Long senderId,
                                          @RequestParam Long originalEmailId,
                                          @RequestParam String targetType,
                                          @RequestParam(required = false) String extraComment) {
        try {
            // Service method match: queueForwardEmail
            // Agar extraComment khali aata hai frontend se toh use safe empty string de dete hain
            String comment = (extraComment == null) ? "" : extraComment;
            String taskId = emailservice.ForwardEmail(senderId, originalEmailId, targetType, comment);
            return ResponseEntity.ok(Map.of(
                    "message", "Forward email queue mein hai (10 seconds remaining for Undo)",
                    "taskId", taskId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}