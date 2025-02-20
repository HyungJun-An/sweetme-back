package com.sweetme.back.chat.controller;

import com.sweetme.back.chat.dto.ChatResponseDTO;
import com.sweetme.back.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")  // CORS 설정 추가
public class ChatController {

    private final ChatService chatService;

    // 채팅글 페이징 처리
    @GetMapping("/{studyId}/chat")
    public ResponseEntity<Page<ChatResponseDTO>> getChatsByStudyId(
            @PathVariable Long studyId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size) {

        Page<ChatResponseDTO> chats = chatService.getChatsByStudyId(studyId, page, size);
        return ResponseEntity.ok(chats);
    }

    // 채팅방 유저 프로필 조회
    @GetMapping("/{studyId}/chat/member")
    public ResponseEntity<List<Map<String, Long>>> getUserByStudyId(
            @PathVariable Long studyId) {
        List<Long> users = chatService.getUserByStudyId(studyId);
        List<Map<String, Long>> userProfiles = new ArrayList<>();

        for (Long userId : users) {
            Map<String, Long> userProfile = new HashMap<>();
            userProfile.put("userId", userId);  // userId를 Long으로 저장
            userProfile.put("profileId", chatService.getProfileIdByUserId(userId));  // profileId를 Long으로 저장
            userProfiles.add(userProfile);
        }

        return ResponseEntity.ok(userProfiles);
    }
}