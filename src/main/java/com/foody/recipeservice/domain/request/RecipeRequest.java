package com.foody.recipeservice.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String time;
    private List<String> imgUrls;
    private List< IngredientRequest> ingredients;
    private List<String> steps;

}
