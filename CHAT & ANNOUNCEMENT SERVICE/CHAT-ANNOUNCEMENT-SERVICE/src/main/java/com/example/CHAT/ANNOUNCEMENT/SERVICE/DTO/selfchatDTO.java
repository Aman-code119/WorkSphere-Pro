package com.example.CHAT.ANNOUNCEMENT.SERVICE.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class selfchatDTO {
    @NotNull(message = "userId  is compulsory")
    private Long userId;
}
