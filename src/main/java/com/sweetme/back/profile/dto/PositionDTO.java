package com.sweetme.back.profile.dto;

import com.sweetme.back.profile.domain.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionDTO {

    private Long id;
    private String name;

    // Entity -> DTO
    public static PositionDTO from(Position position) {
        PositionDTO dto = new PositionDTO();
        dto.setId(position.getId());
        dto.setName(position.getName());

        return dto;
    }

    // DTO -> Entity
    public Position toEntity() {
        Position position = new Position();
        position.setId(this.id);
        position.setName(this.name);
        return position;
    }
}
