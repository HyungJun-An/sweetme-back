package com.sweetme.back.profile.domain;

import com.sweetme.back.auth.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

import static com.sweetme.back.profile.domain.ProfileConstants.*;

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
    @Size(max = MAX_DESCRIPTION_LENGTH)
    private String description = "";

    @Column(name = "profile_url", nullable = false)
    @Size(max = MAX_URL_LENGTH)
    private String profileUrl = "";

    @Column(name = "image_path", nullable = false)
    @Size(max = MAX_URL_LENGTH)
    private String imagePath = "";

    @ManyToMany
    @BatchSize(size = 10) // 10개씩 한 번에 조회
    @JoinTable(
            name = "tbl_profile_stack",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "stack_id")
    )
    private List<Stack> stacks = new ArrayList<>();

    @ManyToMany
    @BatchSize(size = 10) // 10개씩 한 번에 조회
    @JoinTable(
            name = "tbl_profile_position",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "position_id")
    )
    private List<Position> positions = new ArrayList<>();

    public void changeProfile(String description,
                              String profileUrl,
                              String imagePath,
                              List<Stack> stacks,
                              List<Position> positions) {

        this.description = description;
        this.profileUrl = profileUrl;
        this.imagePath = imagePath;
        this.stacks = stacks;
        this.positions = positions;
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