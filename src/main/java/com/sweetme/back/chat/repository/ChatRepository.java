package com.sweetme.back.chat.repository;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.chat.domain.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
        Page<Chat> findByStudyIdOrderByCreatedAtDesc(Long studyId, Pageable pageable);

        // user 조회
        @Query("SELECT m.user  FROM Member m WHERE m.study.id = :studyId and m.status = 'ACCEPTED'")
        List<User> findByStudyId(@Param("studyId") Long studyId);
}
