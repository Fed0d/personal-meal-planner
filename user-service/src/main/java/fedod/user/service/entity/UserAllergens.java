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
@Table(name = "user_allergens")
public class UserAllergens {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "nuts", nullable = false)
    private Boolean nuts;

    @Column(name = "sesame", nullable = false)
    private Boolean sesame;

    @Column(name = "peanut", nullable = false)
    private Boolean peanut;

    @Column(name = "fish", nullable = false)
    private Boolean fish;

    @Column(name = "crustaceans", nullable = false)
    private Boolean crustaceans;

    @Column(name = "molluscs", nullable = false)
    private Boolean molluscs;

    @Column(name = "dairy", nullable = false)
    private Boolean dairy;

    @Column(name = "gluten", nullable = false)
    private Boolean gluten;

    @Column(name = "egg", nullable = false)
    private Boolean egg;

    @Column(name = "celery", nullable = false)
    private Boolean celery;

    @Column(name = "soy", nullable = false)
    private Boolean soy;

    @Column(name = "food_additives", nullable = false)
    private Boolean foodAdditives;

    @Column(name = "mustard", nullable = false)
    private Boolean mustard;

    @Column(name = "strawberry", nullable = false)
    private Boolean strawberry;

    @Column(name = "none", nullable = false)
    private Boolean none;

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
