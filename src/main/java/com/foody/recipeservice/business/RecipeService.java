package com.foody.recipeservice.business;

import com.foody.recipeservice.business.rabbit.event.ImageEvent;
import com.foody.recipeservice.business.rabbit.event.SavedRecipeCreatedEvent;
import com.foody.recipeservice.domain.request.RecipeRequest;
import com.foody.recipeservice.domain.response.CreateRecipeResponse;
import com.foody.recipeservice.domain.response.RecipeResponse;
import com.foody.recipeservice.domain.response.RecipesResponse;

import java.util.concurrent.CompletableFuture;

public interface RecipeService {
    CompletableFuture<CreateRecipeResponse> createRecipe(RecipeRequest request);
    CompletableFuture<RecipeResponse> getRecipeById(Long id);
    CompletableFuture<RecipesResponse> getRecipes(int page, int size);
    CompletableFuture<RecipesResponse> getRecipesByUserId(Long id, int page, int size);
    void updateRecipe(Long id, RecipeRequest request);
    void deleteRecipe(Long id);
    void updateRecipeRating(Long recipeId, double rating);
    void handleImages(ImageEvent imageEvent);
    void receiveSavedRecipeEvent(SavedRecipeCreatedEvent savedRecipeCreatedEvent);
}