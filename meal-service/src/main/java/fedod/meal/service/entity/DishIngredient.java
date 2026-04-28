package fedod.meal.service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dish_ingredient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @Column(name = "ingredient", nullable = false, length = 500)
    private String ingredient;
}
