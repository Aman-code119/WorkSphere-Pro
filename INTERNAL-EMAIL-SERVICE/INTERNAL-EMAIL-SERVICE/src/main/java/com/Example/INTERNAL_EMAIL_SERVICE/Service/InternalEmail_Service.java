package com.Example.INTERNAL_EMAIL_SERVICE.Service;

import com.Example.INTERNAL_EMAIL_SERVICE.FeignClientIntegration.AuthServiceClient;
import com.Example.INTERNAL_EMAIL_SERVICE.Model.EmailReadStatus;
import com.Example.INTERNAL_EMAIL_SERVICE.Model.Internal_Emails;
import com.Example.INTERNAL_EMAIL_SERVICE.Repository.EmailReadStatus_Repository;
import com.Example.INTERNAL_EMAIL_SERVICE.Repository.InternalEmail_Repository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class InternalEmail_Service {

    @Autowired
    private EmailReadStatus_Repository readStatusRepo;

    @Autowired
    private InternalEmail_Repository emailrepo;

    @Autowired
    private AuthServiceClient authservice;

    // 1. ObjectMapper declare aur initialize kiya JSON conversion ke liye
    private final ObjectMapper objectmapper = new ObjectMapper();

    // 2. Scheduler ko define kiya jo background threads me 10-second ka timer chalayega
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // 3. emailBufferCache ko ConcurrentHashMap ke sath declare aur initialize kiya
    // Yeh thread-safe hai aur emails ko DB me jaane se pehle 10 second tak hold rakhega
    private final Map<String, Internal_Emails> emailBufferCache = new ConcurrentHashMap<>();


    //1. brodcast email
    public String broadcastEmail(Long senderId, String subject, String body, String targetType, List<String> attachment) {

        List<Long> targetUserIds;

        // 1. Pehle target nikaalo (Jo tumne code likha)
        if ("ALL".equalsIgnoreCase(targetType)) {
            targetUserIds = authservice.getAllActiveUserIds();
        } else {
            targetUserIds = authservice.getUserIdsByDepartment(targetType);
        }

        if (targetUserIds == null || targetUserIds.isEmpty()) {
            throw new RuntimeException("Error: Employee not found for this target");
        }

        // 2. JSON me convert karo (Jo tumne code likha)
        String jsonReceipientIds;
        try {
            jsonReceipientIds = objectmapper.writeValueAsString(targetUserIds);
        } catch (Exception e) {
            throw new RuntimeException("Error: Serialization failed");
        }

        // 3. Email Object taiyar karo
        Internal_Emails email = new Internal_Emails();
        email.setSenderId(senderId);
        email.setSubject(subject);
        email.setBody(body);
        email.setRecipientIds(jsonReceipientIds);
        email.setAttachmentFilePaths(attachment);

        // 4. 🚨 Ab direct save karne ke bajay, use 10-second buffer me daalo (Undo Logic)
        String taskId = UUID.randomUUID().toString();
        emailBufferCache.put(taskId, email); // Memory buffer me hold kiya

        // 10 Second ka timer start
        scheduler.schedule(() -> {
            if (emailBufferCache.containsKey(taskId)) {
                Internal_Emails finalEmail = emailBufferCache.remove(taskId);
                emailrepo.save(finalEmail); // 10 sec baad database me permanently save hoga!
                System.out.println("Email broadcasted to DB after 10s buffer.");
            }
        }, 10, TimeUnit.SECONDS);

        return taskId; // Frontend ko taskId dedo taaki wo 10 sec ke andar undo daba sake
    }

    // 2. UNDO ACTION (Agar user ne 10 second ke andar 'Undo' click kiya)
    public boolean undoEmail(String taskId) {
        // Agar 10 second pure nahi hue hain, toh data cache me mil jayega
        if (emailBufferCache.containsKey(taskId)) {
            emailBufferCache.remove(taskId); // Memory se saaf! DB me entry kabhi nahi jayegi.
            System.out.println("Email cancelled successfully for task: " + taskId);
            return true; // Undo Successful
        }
        return false; // Time limit crossed, email deliver ho chuki h
    }

    // 3. REPLY EMAIL (With 10-Second Undo Buffer)
    public String ReplyEmail(Long senderId, Long parentEmailId, String body, List<String> attachment) {

        // Step A: Purani (Parent) email ko database se nikaalo taaki details verify ho sakein
        Internal_Emails parentEmail = emailrepo.findById(parentEmailId)
                .orElseThrow(() -> new RuntimeException("Error: Original email nahi mili jispar reply karna hai!"));

        // Step B: Reply hamesha original sender ko jata hai.
        // Toh is naye email ka recipient kaun hoga? Purani email ka sender!
        List<Long> targetUserId = List.of(parentEmail.getSenderId());

        String jsonReceipientIds;
        try {
            jsonReceipientIds = objectmapper.writeValueAsString(targetUserId);
        } catch (Exception e) {
            throw new RuntimeException("Error: Serialization failed");
        }

        // Step C: Subject me automatic "Re: " prefix lagana (Jaise real Gmail me hota hai)
        String replySubject = parentEmail.getSubject().startsWith("Re:") ?
                parentEmail.getSubject() : "Re: " + parentEmail.getSubject();

        // Step D: Email object taiyar karo
        Internal_Emails replyEmail = new Internal_Emails();
        replyEmail.setSenderId(senderId);
        replyEmail.setSubject(replySubject);
        replyEmail.setBody(body);
        replyEmail.setRecipientIds(jsonReceipientIds);
        replyEmail.setAttachmentFilePaths(attachment);
        replyEmail.setParentEmailId(parentEmailId); // Chain jodi
        replyEmail.setEmailType("REPLY");

        // Step E: Undo Buffer me daal do
        String taskId = UUID.randomUUID().toString();
        emailBufferCache.put(taskId, replyEmail);

        scheduler.schedule(() -> {
            if (emailBufferCache.containsKey(taskId)) {
                Internal_Emails finalEmail = emailBufferCache.remove(taskId);
                emailrepo.save(finalEmail);
                System.out.println("Reply email permanently saved in DB.");
            }
        }, 10, TimeUnit.SECONDS);

        return taskId;
    }


    // 4. FORWARD EMAIL (With 10-Second Undo Buffer)
    public String ForwardEmail(Long senderId, Long originalEmailId, String targetType, String extraComment) {

        // Step A: Jis email ko forward karna hai, use DB se uthao
        Internal_Emails originalEmail = emailrepo.findById(originalEmailId)
                .orElseThrow(() -> new RuntimeException("Error: Original email nahi mili jise forward karna hai!"));

        // Step B: Naye recipients dhundo (ALL ya Department) ke hisab se jo tumne pehle kiya tha
        List<Long> targetUserIds;
        if ("ALL".equalsIgnoreCase(targetType)) {
            targetUserIds = authservice.getAllActiveUserIds();
        } else {
            targetUserIds = authservice.getUserIdsByDepartment(targetType);
        }

        if (targetUserIds == null || targetUserIds.isEmpty()) {
            throw new RuntimeException("Error: Target employees not found for forward");
        }

        String jsonReceipientIds;
        try {
            jsonReceipientIds = objectmapper.writeValueAsString(targetUserIds);
        } catch (Exception e) {
            throw new RuntimeException("Error: Serialization failed");
        }

        // Step C: Subject me "Fwd: " jodna aur body me extra comment ke sath purani body attach karna
        String forwardSubject = originalEmail.getSubject().startsWith("Fwd:") ?
                originalEmail.getSubject() : "Fwd: " + originalEmail.getSubject();

        // Real email me likha hota h na ki kisne forward kiya h, wahi dynamic format kiya h yahan:
        String forwardBody = extraComment + "\n\n---------- Forwarded message ----------\n" + originalEmail.getBody();

        // Step D: Email object taiyar karo (Purane attachments automatic naye forward me copy ho jayenge!)
        Internal_Emails forwardEmail = new Internal_Emails();
        forwardEmail.setSenderId(senderId);
        forwardEmail.setSubject(forwardSubject);
        forwardEmail.setBody(forwardBody);
        forwardEmail.setRecipientIds(jsonReceipientIds);
        forwardEmail.setAttachmentFilePaths(originalEmail.getAttachmentFilePaths()); // Old attachments copied!
        forwardEmail.setEmailType("FORWARD");

        // Step E: Undo Buffer me daal do
        String taskId = UUID.randomUUID().toString();
        emailBufferCache.put(taskId, forwardEmail);

        scheduler.schedule(() -> {
            if (emailBufferCache.containsKey(taskId)) {
                Internal_Emails finalEmail = emailBufferCache.remove(taskId);
                emailrepo.save(finalEmail);
                System.out.println("Forwarded email permanently saved in DB.");
            }
        }, 10, TimeUnit.SECONDS);

        return taskId;
    }

    // 5. MARK EMAIL AS READ (Jab user mail open karega)
    public void markAsRead(Long emailId, Long userId) {
        // Check karo ki kahin database me pehle se entry to nahi hai (Taaki duplicate entries na hon)
        Optional<EmailReadStatus> existingStatus = readStatusRepo.findByEmailIdAndUserId(emailId, userId);

        if (existingStatus.isEmpty()) {
            EmailReadStatus status = new EmailReadStatus();
            status.setEmailId(emailId);
            status.setUserId(userId);
            status.setRead(true);
            status.setReadAt(LocalDateTime.now()); // Exact reading time record kiya

            readStatusRepo.save(status);
            System.out.println("Email " + emailId + " marked as READ for user " + userId);
        }
    }

    // 6. GET RECEIVED EMAILS (Inbox Tab - With Read/Unread Flag)
    public List<Map<String, Object>> getUserReceivedInbox(Long userId) {

        // Step A: Database se saari emails nikal lo bina kisi complex native SQL ke
        List<Internal_Emails> receivedEmails = emailrepo.findByRecipientIdCustom(String.valueOf(userId));

        // Step B: ReadStatus table se wo saari email IDs nikaalo jo ye user padh chuka hai
        List<Long> readEmailIds = readStatusRepo.findReadEmailIdsByUserId(userId);

        // 🚨 C. REBUILD LOGIC (Java Stream Filter):
        // Agar tumne delete feature add kiya hoga, toh uski check yahan Java code me automatic ho jayegi
        return receivedEmails.stream()
                // Agar tum chaho to yahan filter laga sakte ho: .filter(email -> !deletedIds.contains(email.getId()))
                .map(email -> {
                    boolean isRead = readEmailIds.contains(email.getId());

                    // Map.of use karke structured data frontend ko bhej rahe hain
                    return Map.of(
                            "emailDetails", email,
                            "isRead", isRead
                    );
                }).collect(java.util.stream.Collectors.toList());
    }

    // 7. GET SENT EMAILS (Sent Items Tab)
    public List<Internal_Emails> getUserSentInbox(Long senderId) {
        // Isme hume Read/Unread check karne ki zaroorat nahi hai, kyunki sender khud apni mail to nahi padhega na!
        // Yeh direct un saari emails ki list dega jo is user ne logo ko bheji hain.
        return emailrepo.findBySenderIdOrderBySentAtDesc(senderId);
    }
}