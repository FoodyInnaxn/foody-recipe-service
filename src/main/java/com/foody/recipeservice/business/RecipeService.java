package com.foody.recipeservice.business;

import com.foody.recipeservice.business.rabbit.event.ImageEvent;
import com.foody.recipeservice.business.rabbit.event.SavedRecipeCreatedEvent;
import com.foody.recipeservice.domain.request.RecipeRequest;
import com.foody.recipeservice.domain.response.CreateRecipeResponse;
import com.foody.recipeservice.domain.response.RecipeResponse;
import com.foody.recipeservice.domain.response.RecipesResponse;

import java.util.List;

public interface RecipeService {
    CreateRecipeResponse createRecipe(RecipeRequest request);
    RecipeResponse getRecipeById(Long id);
    RecipesResponse getRecipes(int page, int size);
    RecipesResponse getRecipesByUserId(Long id, int page, int size);
    void updateRecipe(Long id, RecipeRequest request);
    void deleteRecipe(Long id);
    void handleImages(ImageEvent imageEvent);
    void receiveSavedRecipeEvent(SavedRecipeCreatedEvent savedRecipeCreatedEvent);
}