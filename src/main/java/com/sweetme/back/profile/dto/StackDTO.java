package com.sweetme.back.profile.dto;

import com.sweetme.back.profile.domain.Stack;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StackDTO {

    private Long id;
    private String name;
    private String logoURL;
    private String described;
    private String assort;

    // Entity -> DTO
    public static StackDTO from(Stack stack) {
        StackDTO dto = new StackDTO();
        dto.id = stack.getId();
        dto.name = stack.getName();
        dto.logoURL = stack.getLogoURL();
        dto.described = stack.getDescribed();
        dto.assort = stack.getAssort();

        return dto;
    }

    // DTO -> Entity
    public Stack toEntity() {
        Stack stack = new Stack();
        stack.setId(this.id);
        stack.setName(this.name);
        stack.setLogoURL(this.logoURL);
        stack.setDescribed(this.described);
        stack.setAssort(this.assort);
        return stack;
    }
}
