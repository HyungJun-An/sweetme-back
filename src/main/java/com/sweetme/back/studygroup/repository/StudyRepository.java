package com.sweetme.back.studygroup.repository;

import com.sweetme.back.studygroup.domain.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {

    /**
     * 주어진 필터 조건에 맞는 스터디 목록을 페이징하여 조회함.
     * 연관된 리더와 위치 정보를 함께 조회하여 N+1 문제를 방지
     *
     * @param locationId 필터링할 위치 ID (null인 경우 필터링하지 않음)
     * @param isOnline 온라인/오프라인 여부 (null인 경우 필터링하지 않음)
     * @param type 스터디 유형(STUDY/PROJECT) (null인 경우 필터링하지 않음)
     * @param isOpened 모집 상태 (null인 경우 필터링하지 않음)
     * @param pageable 페이징 정보 (페이지 번호, 크기, 정렬 조건)
     * @return 필터링된 스터디 목록을 포함한 Page 객체
     */
    @Query(
    value = "select distinct s from Study s " +
            "left join fetch s.leader " +
            "left join fetch s.location " +
            "where (:locationId is NULL OR s.location.id = :locationId) " +
            "and (:isOnline is null or s.isOnline = :isOnline) " +
            "and (:type is null or s.type = :type) " +
            "and (:isOpened is null or s.isOpened = :isOpened)" +
            "order by s.createdAt DESC",
    countQuery = "select count(distinct s) from Study s " +
                "where (:locationId is NULL OR s.location.id = :locationId) " +
                "and (:isOnline is null or s.isOnline = :isOnline) " +
                "and (:type is null or s.type = :type) " +
                "and (:isOpened is null or s.isOpened = :isOpened)"
    )
    Page<Study> findStudiesWithFilters(
            @Param("locationId") Long locationId,
            @Param("isOnline") Boolean isOnline,
            @Param("type")Study.StudyType type,
            @Param("isOpened") Boolean isOpened,
            Pageable pageable
            );

    /**
     * 모든 스터디를 조회하며, 연관된 리더와 위치 정보를 함께 조회 (N+1 문제 방지)
     * 생성일 기준 내림차순으로 정렬
     *
     * @return 모든 스터디 목록
     */
    @Query(
        value = "select distinct s from Study s " +
            "LEFT join fetch s.leader " +
            "LEFT join fetch s.location " +
            "ORDER BY s.createdAt DESC",
        countQuery = "select count(distinct s) from Study s"
    )
    Page<Study> findAllStudies(Pageable pageable);


    /**
     * 스터디 ID로 스터디를 조회하며, 연관된 리더 정보를 함께 조회함. (N+1 문제 방지)
     *
     * @param id 조회할 스터디 ID
     * @return 리더 정보가 포함된 스터디 Optional 객체
     */
    @Query("select s from Study s join fetch s.leader where s.id = :id")
    Optional<Study> findByIdWithLeader(@Param("id") Long id);
}
