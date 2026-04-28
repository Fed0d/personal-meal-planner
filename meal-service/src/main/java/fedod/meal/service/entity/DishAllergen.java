package fedod.meal.service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dish_allergen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishAllergen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @Column(name = "allergen", nullable = false)
    private String allergen;
}
