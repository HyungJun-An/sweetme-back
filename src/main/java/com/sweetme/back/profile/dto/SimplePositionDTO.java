package com.sweetme.back.profile.dto;

import com.sweetme.back.profile.domain.Position;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimplePositionDTO {
    private Long id;
    private String name;

    // Entity -> DTO
    public static SimplePositionDTO from(Position position) {
        return SimplePositionDTO.builder()
                .id(position.getId())
                .name(position.getName())
                .build();
    }
}
