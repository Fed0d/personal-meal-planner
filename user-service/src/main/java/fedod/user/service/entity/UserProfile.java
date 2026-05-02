package fedod.user.service.entity;

import fedod.user.service.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 30)
    private Gender gender;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserBodyMetrics userBodyMetrics;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserGoal userGoal;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserActivity userActivity;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserCookingPreferences userCookingPreferences;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserCalorieTargets userCalorieTargets;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserCuisinePreferences cuisinePreferences;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAllergens allergens;

    public void setBodyMetrics(UserBodyMetrics bodyMetrics) {
        this.userBodyMetrics = bodyMetrics;
        if (bodyMetrics != null) {
            bodyMetrics.setUserProfile(this);
        }
    }

    public void setGoal(UserGoal goal) {
        this.userGoal = goal;
        if (goal != null) {
            goal.setUserProfile(this);
        }
    }

    public void setActivity(UserActivity activity) {
        this.userActivity = activity;
        if (activity != null) {
            activity.setUserProfile(this);
        }
    }

    public void setCookingPreferences(UserCookingPreferences cookingPreferences) {
        this.userCookingPreferences = cookingPreferences;
        if (cookingPreferences != null) {
            cookingPreferences.setUserProfile(this);
        }
    }

    public void setCalorieTargets(UserCalorieTargets calorieTargets) {
        this.userCalorieTargets = calorieTargets;
        if (calorieTargets != null) {
            calorieTargets.setUserProfile(this);
        }
    }

    public void setCuisinePreferences(UserCuisinePreferences cuisinePreferences) {
        this.cuisinePreferences = cuisinePreferences;
        if (cuisinePreferences != null) {
            cuisinePreferences.setUserProfile(this);
        }
    }

    public void setAllergens(UserAllergens allergens) {
        this.allergens = allergens;
        if (allergens != null) {
            allergens.setUserProfile(this);
        }
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
