package com.sweetme.back.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.repository.UserRepository;
import com.sweetme.back.chat.domain.Chat;
import com.sweetme.back.chat.repository.ChatRepository;
import com.sweetme.back.studygroup.domain.Study;
import com.sweetme.back.studygroup.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {

    // 스터디 ID를 키로 하고, 해당 스터디의 WebSocket 세션 목록을 값으로 하는 Map
    private static final Map<Long, Set<WebSocketSession>> studyRooms = new ConcurrentHashMap<>();

    // 세션 ID를 키로 하고, 스터디 ID를 값으로 하는 Map (세션이 어느 방에 있는지 추적)
    private static final Map<String, Long> sessionStudyMap = new ConcurrentHashMap<>();

    private final ChatRepository chatRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /******************************
    ; 초기 입장 설정                  ;
    ;*****************************/

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        // URL에서 studyId 추출
        Long studyId = extractStudyId(session);

        // 즉시 해당 스터디 방에 입장
        joinStudyRoom(session, studyId);
        System.out.println("클라이언트 연결됨: " + session.getId() + ", 스터디 방: " + studyId);
    }


    /**********************************
    ; 메시지 송신 시 스서티 그룹 별 전송      ;
    ; 메시지 내용 DB 저장                 ;
    **********************************/

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();

        try {
            Map<String, Object> chatMessage = objectMapper.readValue(payload, Map.class);
            Long studyId = Long.parseLong(chatMessage.get("studyId").toString());
            Long userId = Long.parseLong(chatMessage.get("userId").toString());
            String messageContent = (String) chatMessage.get("message");

            // 최초 접속 시 방 입장 처리
            if (!sessionStudyMap.containsKey(session.getId())) {
                joinStudyRoom(session, studyId);
            }

            // DB에 채팅 저장
            Study study = studyRepository.findById(studyId).orElseThrow();
            User user = userRepository.findById(userId).orElseThrow();

            Chat chat = new Chat();
            chat.setMessage(messageContent);
            chat.setStudy(study);
            chat.setUser(user);
            chatRepository.save(chat);

            // 같은 스터디 방에 있는 사용자들에게만 메시지 전송
            TextMessage broadcastMessage = new TextMessage(objectMapper.writeValueAsString(Map.of(
                    "userId", userId,
                    "message", messageContent,
                    "timestamp", new Date()
            )));

            Set<WebSocketSession> roomSessions = studyRooms.get(studyId);
            if (roomSessions != null) {
                for (WebSocketSession client : roomSessions) {
                    if (client.isOpen()) {
                        client.sendMessage(broadcastMessage);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("메시지 처리 중 오류 발생: " + e.getMessage());
            session.sendMessage(new TextMessage("메시지 처리 중 오류가 발생했습니다."));
        }
    }

    /**********************************
    ; 세션 종료 시 connection close      ;
    **********************************/

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 세션 종료 시 방에서 제거
        Long studyId = sessionStudyMap.get(session.getId());
        if (studyId != null) {
            leaveStudyRoom(session, studyId);
        }
        System.out.println("클라이언트 연결 해제됨: " + session.getId());
    }

    /***********************************
    ; 메시지 핸들링 시 오류 발생 예외 처리     ;
    **********************************/

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.out.println("전송 에러 발생: " + exception.getMessage());
    }

    /********************
    ; 기타 사용자 정의 함수  ;
    ********************/

    // URL에서 studyId 추출하는 메서드
    private Long extractStudyId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] parts = path.split("/");
        // /studies/{studyId}/chat 형식에서 studyId 추출
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("studies") && i + 1 < parts.length) {
                return Long.parseLong(parts[i + 1]);
            }
        }
        throw new IllegalArgumentException("Invalid URL format");
    }

    // 스터디 방 입장
    private void joinStudyRoom(WebSocketSession session, Long studyId) {
        studyRooms.computeIfAbsent(studyId, k -> Collections.synchronizedSet(new HashSet<>()))
                .add(session);
        sessionStudyMap.put(session.getId(), studyId);
    }

    // 스터디 방 퇴장
    private void leaveStudyRoom(WebSocketSession session, Long studyId) {
        Set<WebSocketSession> roomSessions = studyRooms.get(studyId);
        if (roomSessions != null) {
            roomSessions.remove(session);
            if (roomSessions.isEmpty()) {
                studyRooms.remove(studyId);
            }
        }
        sessionStudyMap.remove(session.getId());
    }
}