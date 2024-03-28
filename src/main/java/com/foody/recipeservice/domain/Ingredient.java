package com.foody.recipeservice.domain;

import com.foody.recipeservice.persistence.entity.RecipeEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Ingredient {
    private Long id;
    private String name;
    private String quantity;
    private RecipeEntity recipe;
}
