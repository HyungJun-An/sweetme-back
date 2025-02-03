package com.sweetme.back.profile.domain;

import com.sweetme.back.auth.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_profile")
@Getter
@Setter
@ToString(exclude = {"user", "stacks", "positions"})
public class Profile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "`desc`", nullable = false)
    private String description;

    @Column(name = "profile_url", nullable = false)
    private String profileUrl = "";

    @Column(name = "image_path", nullable = false)
    private String imagePath = "";

    @ManyToMany
    @JoinTable(
            name = "tbl_profile_stack",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "stack_id")
    )
    private List<Stack> stacks = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tbl_profile_position",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "position_id")
    )
    private List<Position> positions = new ArrayList<>();

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void changeImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void addStack(Stack stack) {
        stacks.add(stack);
    }

    public void clearStack() {
        stacks.clear();
    }

    public void addPosition(Position position) {
        positions.add(position);
    }

    public void clearPosition() {
        positions.clear();
    }
}