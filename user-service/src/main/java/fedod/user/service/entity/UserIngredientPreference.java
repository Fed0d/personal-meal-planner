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
@Table(name = "user_ingredient_preferences")
public class UserIngredientPreference {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "fish")
    private Integer fish;

    @Column(name = "seafood")
    private Integer seafood;

    @Column(name = "pork")
    private Integer pork;

    @Column(name = "beef")
    private Integer beef;

    @Column(name = "chicken")
    private Integer chicken;

    @Column(name = "cheese")
    private Integer cheese;

    @Column(name = "potato")
    private Integer potato;

    @Column(name = "onion")
    private Integer onion;

    @Column(name = "garlic")
    private Integer garlic;

    @Column(name = "tomatoes")
    private Integer tomatoes;

    @Column(name = "liver")
    private Integer liver;

    @Column(name = "milk")
    private Integer milk;

    @Column(name = "cottage_cheese")
    private Integer cottageCheese;

    @Column(name = "olives")
    private Integer olives;

    @Column(name = "celery")
    private Integer celery;

    @Column(name = "cilantro")
    private Integer cilantro;

    @Column(name = "pumpkin")
    private Integer pumpkin;

    @Column(name = "eggplant")
    private Integer eggplant;

    @Column(name = "nuts")
    private Integer nuts;

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
