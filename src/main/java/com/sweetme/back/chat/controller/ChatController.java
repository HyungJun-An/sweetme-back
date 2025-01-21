package com.sweetme.back.chat.controller;

import com.sweetme.back.chat.dto.ChatResponseDTO;
import com.sweetme.back.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{studyId}/chat")
    public ResponseEntity<Page<ChatResponseDTO>> getChatsByStudyId(
            @PathVariable Long studyId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size) {

        Page<ChatResponseDTO> chats = chatService.getChatsByStudyId(studyId, page, size);
        return ResponseEntity.ok(chats);
    }
}
