package fedod.user.service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_cuisine_preferences")
public class UserCuisinePreferences {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "asian")
    private Integer asian;

    @Column(name = "european")
    private Integer european;

    @Column(name = "eastern")
    private Integer eastern;

    @Column(name = "slavic")
    private Integer slavic;

    @Column(name = "american")
    private Integer american;

    @Column(name = "mexican")
    private Integer mexican;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
        if (userProfile != null) {
            this.userId = userProfile.getUserId();
        }
    }

    @PrePersist
    public void prePersist() {
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
