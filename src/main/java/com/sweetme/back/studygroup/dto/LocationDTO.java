package com.sweetme.back.studygroup.dto;

import com.sweetme.back.studygroup.domain.Location;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {

    private Long id;
    private String name;
    private Location.LocationType type;

    public static LocationDTO from(Location location) {
        if (location == null) return null;

        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setType(location.getType());
        return dto;
    }
}
