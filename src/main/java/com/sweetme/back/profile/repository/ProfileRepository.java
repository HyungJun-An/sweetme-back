package com.sweetme.back.profile.repository;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.profile.domain.Profile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @EntityGraph(attributePaths = {"stacks", "positions"})
    Optional<Profile> findById(Long id);

    @EntityGraph(attributePaths = {"stacks", "positions"})
    List<Profile> findAll();

//    @EntityGraph(attributePaths = {"stacks", "positions"})
    Optional<Profile> findByUser(User user);
}
