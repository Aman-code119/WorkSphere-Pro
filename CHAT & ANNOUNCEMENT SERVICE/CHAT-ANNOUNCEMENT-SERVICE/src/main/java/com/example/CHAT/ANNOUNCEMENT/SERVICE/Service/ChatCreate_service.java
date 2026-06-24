package com.example.CHAT.ANNOUNCEMENT.SERVICE.Service;

import com.example.CHAT.ANNOUNCEMENT.SERVICE.Model.Announcement;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.Model.AnnouncementReadStatus;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.Model.ChatType;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.Repository.AnnouncementReadStatus_Repository;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.Repository.Announcement_Repository;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.Repository.Chat_Repository;
import com.example.CHAT.ANNOUNCEMENT.SERVICE.feigncommunication.AuthServiceClient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatCreate_service {

    @Autowired
    private AnnouncementReadStatus_Repository annostatusrepo;

    @Autowired
    private Chat_Repository chatrepo;

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private Announcement_Repository annorepo;


    // 1.rename chat
    public ChatType updateChatName(@NotNull(message = "Chat ID compulsory hai!") Long chatId,
                                          @NotBlank(message = "Naya naam khali nahi hona chahiye!") String newChatName,
                                          @NotNull(message = "User ID compulsory hai!") Long userId) {

            // Step A: Check karo kya yeh Chat Room database me hai?
            ChatType chat = chatrepo.findById(chatId)
                    .orElseThrow(() -> new RuntimeException("Error: Chat Room nahi mila!"));

            // Step B: SECURITY CHECK (Kahin koi anjaan banda naam to nahi badal raha?)
            // Group, Self, ya Individual jisne bhi banaya tha (CreatedBy), sirf wahi badal sake
            if (!chat.getCreatedBy().equals(userId)) {
                throw new RuntimeException("Error: Aapke paas is chat ka naam badalne ka hakk nahi hai!");
            }

            // Step C: Naya naam set karo aur save kar do
            chat.setChatName(newChatName);
            return chatrepo.save(chat);
    }

    //1.self caht
    public ChatType createSelfchat(@NotNull(message = "userId  is compulsory") Long userId) {

        // Auth-MS se live checking ho rahi hai bina direct table link kiye!
        Boolean userExists = authServiceClient.checkUserExists(userId);

        if (userExists == null || !userExists) {
            throw new RuntimeException("Error: User nahi mila! Chat room nahi ban sakta.");
        }
        ChatType chat = new ChatType();
        chat.setCreatedBy(userId);
        chat.setChatType("SELF");
        chat.setChatName("Yourself");
        return chatrepo.save(chat);
    }

    //2. individual caht
    public ChatType createIndividualChat(@NotNull(message = "sender_id  is compulsory!") Long senderId, @NotNull(message = "receiver_id is compulsory!") Long receiverId) {

        //check A:ky sender asli h?
        Boolean senderExists = authServiceClient.checkUserExists(senderId);
        if (senderExists == null || !senderExists) {
            throw new RuntimeException("Error: Sender (ID: " + senderId + ") system me nahi mila!");
        }

        // Check B: Kya Receiver asli hai?
        Boolean receiverExists = authServiceClient.checkUserExists(receiverId);
        if (receiverExists == null || !receiverExists) {
            throw new RuntimeException("Error: Receiver (ID: " + receiverId + ") system me nahi mila!");
        }

        // Check C: Kahin user khud se hi individual chat to nahi bana raha? (Self chat check)
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Error: Apne aap se individual chat nahi bana sakte, Self Chat use karein!");
        }

        // Agar dono valid hain, tabhi chat room banega
        ChatType chat = new ChatType();
        chat.setChatName("");
        chat.setCreatedBy(senderId); // Kisne chat initiate ki
        chat.setChatType("ONE_TO_ONE");
        return chatrepo.save(chat);
    }


    //3.group chat
    public ChatType createGroupChat(@NotBlank(message = "Group name NOT empty!") String groupName, Long creatorId) {

        Boolean creatorExists = authServiceClient.checkUserExists(creatorId);
        if (creatorExists == null || !creatorExists) {
            throw new RuntimeException("Error: Group Creator (ID: " + creatorId + ") valid user nahi hai!");
        }

        ChatType chat = new ChatType();
        chat.setChatName(groupName);
        chat.setCreatedBy(creatorId); // Group Admin/Creator ki ID
        chat.setChatType("GROUP");

        return chatrepo.save(chat);
    }


    //4. Announcement bhejna
    public Announcement broadcastAnnouncement(String title, String message, Long hrId, List<Long> targetGroupIds) {

        // Step A: Pehle check karo ki user valid hai ya nahi
        Boolean hrExists = authServiceClient.checkUserExists(hrId);
        if (hrExists == null || !hrExists) {
            throw new RuntimeException("Error: Announcement karne wala User system me nahi hai!");
        }

        // Step B: Database se wo saare Groups nikal lo jinki IDs HR ne select ki hain
        List<ChatType> groupsToNotify = chatrepo.findAllById(targetGroupIds);
        if (groupsToNotify.isEmpty()) {
            throw new RuntimeException("Error: Koi bhi valid Group select nahi kiya gaya!");
        }

        // Step C: Announcement object taiyar karo
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setMessage(message);
        announcement.setSenderId(hrId);
        announcement.setTargetGroups(groupsToNotify); // Mapping set ho gayi!

        // Step D: Save kar do (Hibernate automatic join table me entries daal dega)
        return annorepo.save(announcement);
    }


    //5.fetch old announcement
    public List<ChatType> getAllAnnouncementChats() {
        return chatrepo.findByChatType("ANNOUNCEMENT");
    }


    // 6. Mark as Read: Jab employee notice par click karega
    public void markAsRead(Long userId, Long announcementId) {
        // Check karo kahin pehle se read to nahi hai
        boolean alreadyRead = annostatusrepo.existsByUserIdAndAnnouncementId(userId, announcementId);

        if (!alreadyRead) {
            AnnouncementReadStatus status = new AnnouncementReadStatus();
            status.setUserId(userId);
            status.setAnnouncementId(announcementId);
            annostatusrepo.save(status);
        }
    }

    // 7. Unread Count Nikalna: Jo Red Badge par chamkega
    public Long getUnreadAnnouncementCount(Long userId, List<Long> userGroupIds) {
        // A. Pehle wo saari announcements nikaalo jo is user ke groups ke liye aayi hain
        List<Long> allAccessibleAnnouncementIds = annorepo.findAnnouncementIdsByGroupIds(userGroupIds);

        // B. Ab dekhon unme se kitni announcements ki entry status table me is userId ke sath nahi hai
        Long readCount = annostatusrepo.countByUserIdAndAnnouncementIdIn(userId, allAccessibleAnnouncementIds);

        // C. Unread = Total - Read
        return allAccessibleAnnouncementIds.size() - readCount;
    }
}