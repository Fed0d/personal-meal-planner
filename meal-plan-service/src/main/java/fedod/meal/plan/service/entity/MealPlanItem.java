package fedod.meal.plan.service.entity;

import fedod.meal.plan.service.entity.enums.MealSlot;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meal_plan_item")
@Getter
@Setter
@NoArgsConstructor
public class MealPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_slot", nullable = false)
    private MealSlot mealSlot;

    @Column(name = "dish_id", nullable = false)
    private UUID dishId;

    @Column(name = "dish_name", nullable = false, length = 200)
    private String dishName;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal calories;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
