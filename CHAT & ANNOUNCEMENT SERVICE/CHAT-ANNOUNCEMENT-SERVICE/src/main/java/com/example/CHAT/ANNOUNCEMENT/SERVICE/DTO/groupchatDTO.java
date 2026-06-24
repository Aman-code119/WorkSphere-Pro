package com.example.CHAT.ANNOUNCEMENT.SERVICE.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class groupchatDTO {
        @NotBlank(message = "Group name NOT empty!")
        private String groupName;
        private Long creatorId;
        @NotBlank(message = "Role is COMPULSARY!")
        private String role;
}