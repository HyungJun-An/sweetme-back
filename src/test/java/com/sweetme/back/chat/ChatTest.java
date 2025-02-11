package com.sweetme.back.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.repository.UserRepository;
import com.sweetme.back.chat.domain.Chat;
import com.sweetme.back.chat.repository.ChatRepository;
import com.sweetme.back.common.exception.CustomWebSocketException;
import com.sweetme.back.common.util.SocketHandler;
import com.sweetme.back.studygroup.domain.Study;
import com.sweetme.back.studygroup.repository.StudyRepository;
import com.sweetme.back.studygroup.service.StudyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ChatTest {
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private StudyRepository studyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebSocketSession session;
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private StudyService studyService;

    @InjectMocks
    private SocketHandler socketHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*********************************************
    ; 스터디Id 1번방 입장 제대로 되는지 확인하는 테스트 코드 ;
    *********************************************/
    @Test
    void whenConnectionEstablished_thenJoinStudyRoom() throws Exception {
        // Given
        URI uri = new URI("/studies/1/chat");
        when(session.getUri()).thenReturn(uri);
        when(session.getId()).thenReturn("test-session-id");

        // When
        socketHandler.afterConnectionEstablished(session);

        // Then
        verify(session).getId();
        verify(session).getUri();
    }

    /***********************************************
    ; 메세지 입력시 DB 저장 메서드 호출 되는지 확인          ;
    ***********************************************/
    @Test
    void whenReceiveMessage_thenSaveAndBroadcast() throws Exception {
        // Given
        SocketHandler handler = new SocketHandler(
                chatRepository,
                studyRepository,
                userRepository,
                new ObjectMapper(),  // 실제 ObjectMapper 사용
                studyService
        );

        String messagePayload = "{\"studyId\":\"1\",\"userId\":\"1\",\"message\":\"test\"}";
        TextMessage message = new TextMessage(messagePayload);
        Study study = new Study();
        User user = new User();

        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(session.getId()).thenReturn("test-session-id");

        // When
        handler.handleTextMessage(session, message);

        // Then
        verify(chatRepository).save(any(Chat.class));
    }

    /*************************************
    ; 종료 시 세션 반납 제대로 이루어 지는 지 확인 ;
    *************************************/
    @Test
    void whenConnectionClosed_thenLeaveStudyRoom() throws Exception {
        // Given
        String sessionId = "test-session-id";
        Long studyId = 1L;
        when(session.getId()).thenReturn(sessionId);
        when(session.getUri()).thenReturn(new URI("/studies/" + studyId + "/chat"));

        // 먼저 방에 입장
        socketHandler.afterConnectionEstablished(session);

        // When
        socketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // Then
        // sessionStudyMap에서 해당 세션이 제거되었는지 확인
        assertNull(socketHandler.sessionStudyMap.get(sessionId));
        // studyRooms에서 해당 세션이 제거되었는지 확인
        assertTrue(socketHandler.studyRooms.get(studyId) == null ||
                !socketHandler.studyRooms.get(studyId).contains(session));
    }

    /***********************
    ; URL 경로가 잘못 되었을 때 ;
    ***********************/
    @Test
    void whenInvalidStudyId_thenThrowException() {
        // Given
        URI uri1 = URI.create("/invalid/url");
        when(session.getUri()).thenReturn(uri1);

        // When & Then
        assertThrows(CustomWebSocketException.class, () -> {
            socketHandler.afterConnectionEstablished(session);
        });

        URI uri2 = URI.create("/studies/한글/chat");
        when(session.getUri()).thenReturn(uri2);

        // When & Then
        assertThrows(CustomWebSocketException.class, () -> {
            socketHandler.afterConnectionEstablished(session);
        });

        URI uri3 = URI.create("/studies/99999/chat");
        when(session.getUri()).thenReturn(uri3);

        // When & Then
        assertThrows(CustomWebSocketException.class, () -> {
            socketHandler.afterConnectionEstablished(session);
        });
    }

    /****************************
    ; Json 형식이 잘못 되었을 때     ;
    ****************************/
    @Test
    void whenHandleMessageFails_thenSendErrorMessage() throws Exception {

        // Given
        TextMessage message = new TextMessage("invalid json");
        TextMessage message1 = new TextMessage("{\"userId\":\"1\",\"message\":\"test\"}");
        TextMessage message2 = new TextMessage("{\"studyId\":\"1\",\"message\":\"test\"}");
        TextMessage message3 = new TextMessage("{\"studyId\":\"1\",\"userId\":\"1\"}");
        SocketHandler socketHandler = new SocketHandler(
                chatRepository,
                studyRepository,
                userRepository,
                new ObjectMapper(),
                studyService
        );

        // When & Then
        assertThrows(CustomWebSocketException.class, () -> {    //Json 형식이 완전히 잘못 되었을 때
            socketHandler.handleTextMessage(session, message);
        });
        assertThrows(CustomWebSocketException.class, () -> {    //studyId가 비어 있을 때
            socketHandler.handleTextMessage(session, message1);
        });
        assertThrows(CustomWebSocketException.class, () -> {    //userId가 없을 때
            socketHandler.handleTextMessage(session, message2);
        });
        assertThrows(CustomWebSocketException.class, () -> {    //message가 없을 때
            socketHandler.handleTextMessage(session, message3);
        });

    }
}