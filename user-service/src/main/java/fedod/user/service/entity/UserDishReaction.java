package fedod.user.service.entity;

import fedod.user.service.entity.enums.ReactionType;
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
    name = "user_dish_reactions",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_user_dish_reaction",
        columnNames = {"user_id", "dish_id"}
    )
)
public class UserDishReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "dish_id", nullable = false)
    private Long dishId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction", nullable = false, length = 10)
    private ReactionType reaction;
}
