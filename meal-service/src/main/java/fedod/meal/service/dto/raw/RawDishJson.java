package fedod.meal.service.dto.raw;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RawDishJson(
        Long id,
        String url,
        String title,
        String description,
        @JsonProperty("dish_image") String dishImage,
        KbjuJson kbju,
        @JsonProperty("ready_in") String readyIn,
        @JsonProperty("kitchen_time") String kitchenTime,
        String cuisine,
        @JsonProperty("category_path") String categoryPath,
        @JsonProperty("common_allergens") List<String> commonAllergens,
        List<String> ingredients,
        String recipe
) {
}
