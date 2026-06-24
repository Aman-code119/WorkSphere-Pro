package com.example.CHAT.ANNOUNCEMENT.SERVICE.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class individualChatDTO {
    @NotNull(message = "sender_id  is compulsory!")
    private Long senderId;

    @NotNull(message = "receiver_id is compulsory!")
    private Long receiverId;
}