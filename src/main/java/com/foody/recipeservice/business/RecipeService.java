package com.foody.recipeservice.business;

import com.foody.recipeservice.domain.request.RecipeRequest;
import com.foody.recipeservice.domain.response.CreateRecipeResponse;
import com.foody.recipeservice.domain.response.RecipeResponse;

import java.util.List;

public interface RecipeService {
    CreateRecipeResponse createRecipe(RecipeRequest request);
    RecipeResponse getRecipeById(Long id);
    List<RecipeResponse> getRecipes(int page, int size);
    void updateRecipe(Long id, RecipeRequest request);
    void deleteRecipe(long id);
}