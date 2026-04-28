package fedod.meal.service.loader;

import fedod.meal.service.dto.raw.RawDishJson;
import fedod.meal.service.entity.Dish;
import fedod.meal.service.entity.DishAllergen;
import fedod.meal.service.entity.DishIngredient;
import fedod.meal.service.entity.enums.MealType;
import fedod.meal.service.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DishDataLoader implements ApplicationRunner {

    private final DishRepository dishRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) {
        if (dishRepository.count() > 0) {
            log.info("Dish data already loaded, skipping initialization");
            return;
        }

        log.info("Starting dish data initialization");
        loadFile("data/breakfast.jsonl", MealType.BREAKFAST);
        loadFile("data/lunch.jsonl", MealType.LUNCH);
        loadFile("data/dinner.jsonl", MealType.DINNER);
        log.info("Dish data initialization completed. Total dishes: {}", dishRepository.count());
    }

    private void loadFile(String classpathPath, MealType mealType) {
        ClassPathResource resource = new ClassPathResource(classpathPath);
        if (!resource.exists()) {
            log.warn("Data file not found: {}, skipping", classpathPath);
            return;
        }

        List<Dish> dishes = new ArrayList<>();
        int lineNum = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    RawDishJson raw = objectMapper.readValue(line, RawDishJson.class);
                    dishes.add(toEntity(raw, mealType));
                } catch (Exception e) {
                    log.warn("Failed to parse line {} in {}: {}", lineNum, classpathPath, e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Failed to read file {}: {}", classpathPath, e.getMessage());
            return;
        }

        dishRepository.saveAll(dishes);
        log.info("Loaded {} dishes from {} (mealType={})", dishes.size(), classpathPath, mealType);
    }

    private Dish toEntity(RawDishJson raw, MealType mealType) {
        Dish dish = Dish.builder()
                .id(raw.id())
                .url(raw.url())
                .title(raw.title())
                .description(raw.description())
                .dishImage(raw.dishImage())
                .calories(toDecimal(raw.kbju() != null ? raw.kbju().calories() : null))
                .protein(toDecimal(raw.kbju() != null ? raw.kbju().protein() : null))
                .fat(toDecimal(raw.kbju() != null ? raw.kbju().fat() : null))
                .carbs(toDecimal(raw.kbju() != null ? raw.kbju().carbs() : null))
                .readyIn(raw.readyIn())
                .kitchenTime(raw.kitchenTime())
                .cuisine(raw.cuisine())
                .categoryPath(raw.categoryPath())
                .recipe(raw.recipe())
                .mealType(mealType)
                .build();

        if (raw.commonAllergens() != null) {
            raw.commonAllergens().forEach(a ->
                    dish.getAllergens().add(DishAllergen.builder().dish(dish).allergen(a).build())
            );
        }

        if (raw.ingredients() != null) {
            raw.ingredients().forEach(i ->
                    dish.getIngredients().add(DishIngredient.builder().dish(dish).ingredient(i).build())
            );
        }

        return dish;
    }

    private BigDecimal toDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }
}
