package com.sweetme.back.profile.repository;

import com.sweetme.back.profile.domain.Profile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @EntityGraph(attributePaths = {"user", "stacks", "positions"})
    Optional<Profile> findById(Long profileId);


    // stack 리스트 fetch join 메서드
    @EntityGraph(attributePaths = {"user", "stacks"})
//    @Query("select distinct p from Profile p join fetch p.stacks where p.user.id = :userId")
    Optional<Profile> findWithStacksByUserId(@Param("userId") Long userId);

    // position 리스트 fetch join 메서드
    @EntityGraph(attributePaths = {"user", "positions"})
//    @Query("select distinct p from Profile p join fetch p.positions where p.user.id = :userId")
    Optional<Profile> findWithPositionsByUserId(@Param("userId") Long userId);
}
