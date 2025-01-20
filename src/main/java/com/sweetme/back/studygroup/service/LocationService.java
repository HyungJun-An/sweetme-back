package com.sweetme.back.studygroup.service;

import com.sweetme.back.studygroup.domain.Location;
import com.sweetme.back.studygroup.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
//    private final LocationRepository locationRepository;
//
//    // 도시 목록 조회
//    public List<Location> getAllCities() {
//        return locationRepository.findAllCities();
//    }
//
//    // 특정 도시의 동네 목록 조회
//    public List<Location> getTownsByCity(Long cityId) {
//        return locationRepository.findTownsByParentId(cityId);
//    }
//
//    // 위치 검색
//    public List<Location> searchLocations(String keyword) {
//        return locationRepository.findByNameContaining(keyword);
//    }

    // 특정 도시와 동네로 위치 찾기
//    public Location findLocation(String cityName, String townName) {
//        return locationRepository.findByParentNameAndName(cityName, townName)
//                .orElseThrow(() -> new LocationNotFoundException("해당 위치를 찾을 수 없습니다."));
//    }
}
