package com.sweetme.back.studygroup.repository;

import com.sweetme.back.studygroup.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

//    //도시(CITY) 목록 조회
//    @Query("SELECT l FROM Location l WHERE l.type = 'CITY'")
//    List<Location> findAllCities();
//
//    // 특정 도시에 속한 동네(TOWN) 목록 조회
//    @Query("select l from Location l where l.parent.id = :cityId AND l.type ='TOWN'")
//    List<Location> findTownsByParentId(@Param("cityId") Long cityId);
//
//    // 이름으로 위치 검색 (도시나 동네)
//    List<Location> findByNameContaining(String name);
//
//    // 특정 타입의 위치 검색
//    List<Location> findByType(Location.LocationType type);
//
//    // 도시와 동네 이름으로 위치 검색
//    @Query("select l from Location l where l.parent.name = :cityName and l.name = :townName and l.type = 'TOWN'")
//    Optional<Location> findByParentNameAndName(@Param("cityName") String cityName, @Param("townName") String townName);
//
//    // 계층 구조로 된 전체 위치 정보 조회
//    @Query("select l from Location l left join fetch l.parent")
//    List<Location> findAllWithParent();
//
//    // 특정 도시/동네가 존재하는지 확인
//    boolean existByNameAndType(String name, Location.LocationType type);
//
//    // 특정 도시에 속한 동네인지 확인
//    boolean existByParentIdAndNameAndType(Long parentId, String name, Location.LocationType type);

}
