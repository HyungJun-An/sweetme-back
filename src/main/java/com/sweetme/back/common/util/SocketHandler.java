package com.sweetme.back.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.repository.UserRepository;
import com.sweetme.back.chat.domain.Chat;
import com.sweetme.back.chat.repository.ChatRepository;
import com.sweetme.back.common.exception.CustomWebSocketException;
import com.sweetme.back.studygroup.domain.Study;
import com.sweetme.back.studygroup.repository.StudyRepository;
import com.sweetme.back.studygroup.service.StudyService;
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
    public static final Map<Long, Set<WebSocketSession>> studyRooms = new ConcurrentHashMap<>();

    // 세션 ID를 키로 하고, 스터디 ID를 값으로 하는 Map (세션이 어느 방에 있는지 추적)
    public static final Map<String, Long> sessionStudyMap = new ConcurrentHashMap<>();

    private final ChatRepository chatRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final StudyService studyService;

    /******************************
    ; 초기 입장 설정                  ;
    ;*****************************/

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws CustomWebSocketException {
        // URL에서 studyId 추출
        Long studyId = extractStudyId(session);

        // 존재하는 studyId 일 시 스터디 방 입장
        joinStudyRoom(session, studyId);
    }


    /**********************************
    ; 메시지 송신 시 스서티 그룹 별 전송      ;
    ; 메시지 내용 DB 저장                 ;
    **********************************/

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws CustomWebSocketException, IOException {
        String payload = message.getPayload();
        Long studyId = null;
        Long userId = null;
        String messageContent = null;
        try {
            Map<String, Object> chatMessage = objectMapper.readValue(payload, Map.class);
            studyId = Long.parseLong(chatMessage.get("studyId").toString());
            userId = Long.parseLong(chatMessage.get("userId").toString());
            messageContent = (String) chatMessage.get("message");

            // 최초 접속 시 방 입장 처리
            if (!sessionStudyMap.containsKey(session.getId())) {
                joinStudyRoom(session, studyId);
            }

            // DB에 채팅 저장
            Study study = studyService.getStudy(studyId);
            User user = userRepository.findById(userId).orElseThrow();

            Chat chat = new Chat();
            chat.setMessage(messageContent);
            chat.setStudy(study);
            chat.setUser(user);
            chatRepository.save(chat);

            // 같은 스터디 방에 있는 사용자들에게만 메시지 전송
            TextMessage broadcastMessage = new TextMessage(objectMapper.writeValueAsString(Map.of(
                    "userId", userId,
                    "nickname", user.getNickname(),
                    "message", messageContent,
                    "createdAt", new Date()
            )));

            Set<WebSocketSession> roomSessions = studyRooms.get(studyId);
            if (roomSessions != null) {
                for (WebSocketSession client : roomSessions) {
                    if (client.isOpen()) {
                        client.sendMessage(broadcastMessage);
                    }
                }
            }
        }
        catch (NullPointerException e) {
            if(studyId == null ){
                session.sendMessage(new TextMessage("studyId가 비어있습니다."));
                throw new CustomWebSocketException("studyId가 비어있습니다.");
            } else if (userId == null ) {
                session.sendMessage(new TextMessage("userId가 비어있습니다."));
                throw new CustomWebSocketException("userId가 비어있습니다.");
            } else if (messageContent == null) {
                session.sendMessage(new TextMessage("message가 비어있습니다."));
                throw new CustomWebSocketException("message가 비어있습니다.");
            }
            session.sendMessage(new TextMessage("메시지 처리 중 오류가 발생했습니다." + e.getMessage()));
            throw new CustomWebSocketException(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            session.sendMessage(new TextMessage(e.getMessage())); // session 으로 에러 메세지 전달
            throw new CustomWebSocketException(e.getMessage()); //study 혹은 user 에서 예외 처리
        } catch (Exception e) {
            session.sendMessage(new TextMessage("Json 형식이 잘못 되었습니다." + e.getMessage()));
            throw new CustomWebSocketException("Json 형식이 잘못 되었습니다." + e.getMessage());
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

        try {

            String path = session.getUri().getPath();
            String[] parts = path.split("/");
            // /studies/{studyId}/chat 형식에서 studyId 추출
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("studies") && i + 1 < parts.length) {
                    Long studyId = Long.parseLong(parts[i + 1]);
                        if( studyService.getStudy(studyId) != null) { // 해당하는 study 없을 시 IllegalArgumentException 발생
                            return studyId;
                        }
                        else{
                            throw new CustomWebSocketException("studyId에 해당하는 Study가 없습니다.");
                        }
                }
            }
            throw new CustomWebSocketException("URL에 studyId가 없습니다.");
        }
        catch (NumberFormatException e) {
            throw new CustomWebSocketException("studyId는 숫자여야 합니다.");
        }
        catch (IllegalArgumentException e) {
            throw new CustomWebSocketException(e.getMessage());
        }

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