package com.sweetme.back.chat.service;

import com.sweetme.back.chat.domain.Chat;
import com.sweetme.back.chat.dto.ChatResponseDTO;
import com.sweetme.back.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;

    public Page<ChatResponseDTO> getChatsByStudyId(Long studyId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Chat> chats = chatRepository.findByStudyIdOrderByCreatedAtAsc(studyId, pageRequest);
        return chats.map(ChatResponseDTO::new);
    }
}