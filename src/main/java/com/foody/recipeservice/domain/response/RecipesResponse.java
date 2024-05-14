package com.foody.recipeservice.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipesResponse {
    private List<RecipeResponse> recipes;
    private int totalPages;

}
