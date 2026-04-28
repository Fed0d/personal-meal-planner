package fedod.meal.service.entity;

import fedod.meal.service.entity.enums.MealType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dish")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dish {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "dish_image", length = 500)
    private String dishImage;

    @Column(name = "calories", precision = 7, scale = 2)
    private BigDecimal calories;

    @Column(name = "protein", precision = 6, scale = 2)
    private BigDecimal protein;

    @Column(name = "fat", precision = 6, scale = 2)
    private BigDecimal fat;

    @Column(name = "carbs", precision = 6, scale = 2)
    private BigDecimal carbs;

    @Column(name = "ready_in", length = 50)
    private String readyIn;

    @Column(name = "kitchen_time", length = 50)
    private String kitchenTime;

    @Column(name = "cuisine", length = 100)
    private String cuisine;

    @Column(name = "category_path", length = 500)
    private String categoryPath;

    @Column(name = "recipe", columnDefinition = "text")
    private String recipe;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 20)
    private MealType mealType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DishAllergen> allergens = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DishIngredient> ingredients = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
