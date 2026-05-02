package fedod.user.service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "user_ingredient_preferences",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_user_ingredient",
        columnNames = {"user_id", "ingredient_name"}
    )
)
public class UserIngredientPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "ingredient_name", nullable = false, length = 100)
    private String ingredientName;

    @Column(name = "score", nullable = false)
    private Integer score;
}
