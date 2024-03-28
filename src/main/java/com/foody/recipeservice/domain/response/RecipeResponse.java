package com.foody.recipeservice.domain.response;

import com.foody.recipeservice.domain.request.IngredientRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponse {
    private Long id;
    private String title;
    private String description;
    private String time;
    private List<String> imgUrls;
    private List<IngredientRequest> ingredients;
    private List<String> steps;
}
