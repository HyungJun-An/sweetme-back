package com.sweetme.back.chat.dto;

import com.sweetme.back.chat.domain.Chat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatResponseDTO {
    private Long userId;
    private String message;
    private LocalDateTime createdAt;

    public ChatResponseDTO(Chat chat) {
        this.userId = chat.getUser().getId();
        this.message = chat.getMessage();
        this.createdAt = chat.getCreatedAt();
    }
}
