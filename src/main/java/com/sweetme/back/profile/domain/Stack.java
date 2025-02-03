package com.sweetme.back.profile.domain;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tbl_stack")
@Getter
@Setter
@ToString
public class Stack {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stack_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "logo_url", nullable = false)
    private String logoURL;

    @Column(nullable = false, length = 1000)
    private String described;

    @Column(nullable = false)
    private String assort;
}