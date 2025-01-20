package com.sweetme.back.studygroup.repository;

import com.sweetme.back.studygroup.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {
    @Query("select s from Study s join fetch s.leader where s.id = :id")
    Optional<Study> findByIdWithLeader(@Param("id") Long id);
}
