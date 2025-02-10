package com.sweetme.back.profile.dto;

import com.sweetme.back.profile.domain.Stack;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SimpleStackDTO {
    private Long id;
    private String name;
    private String logoURL;

    // Entity -> DTO
    public static SimpleStackDTO from(Stack stack) {
        return SimpleStackDTO.builder()
                .id(stack.getId())
                .name(stack.getName())
                .logoURL(stack.getLogoURL())
                .build();
    }
}
