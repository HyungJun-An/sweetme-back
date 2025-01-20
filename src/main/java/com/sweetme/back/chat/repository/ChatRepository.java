// ChatRepository.java
package com.sweetme.back.chat.repository;

import com.sweetme.back.chat.domain.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
        Page<Chat> findByStudyIdOrderByCreatedAtAsc(Long studyId, Pageable pageable);

}