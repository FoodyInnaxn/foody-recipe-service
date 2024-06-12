package com.foody.recipeservice.controller;

import com.foody.recipeservice.domain.request.IngredientRequest;
import com.foody.recipeservice.domain.response.CreateRecipeResponse;
import com.foody.recipeservice.domain.response.RecipeResponse;
import com.foody.recipeservice.business.RecipeService;
import com.foody.recipeservice.business.exceptions.RecipeNotFoundException;
import com.foody.recipeservice.domain.response.RecipesResponse;
import com.foody.recipeservice.persistence.entity.RecipeEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.foody.recipeservice.domain.request.RecipeRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping(value = "/{id}/operations/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<CreateRecipeResponse> createRecipe(@PathVariable("id") Long userId, @RequestPart(value = "title") String title, @RequestPart(value = "description") String description,
                                                                @RequestPart(value = "time") String time,
                                                                @RequestPart(value = "steps") String steps,
                                                                @RequestPart(value = "ingredients") String ingredientRequest,
                                                                @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles
                                             ) {
        Gson gson = new Gson();
        List<String> stepList = gson.fromJson(steps, List.class);
        List<IngredientRequest> ingr = new Gson().fromJson(ingredientRequest, new TypeToken<List<IngredientRequest>>(){}.getType());
        RecipeRequest request = new RecipeRequest();
        request.setUserId(userId);
        request.setTitle(title);
        request.setDescription(description);
        request.setTime(time);
        request.setIngredients(ingr);
        request.setSteps(stepList);
        request.setImages(imageFiles);
        return recipeService.createRecipe(request);
    }

    @GetMapping("/view/{id}")
    public CompletableFuture<RecipeResponse> getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }
    @GetMapping("/view")
    public CompletableFuture<RecipesResponse> getRecipes(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
//        CompletableFuture<RecipesResponse> recipeResponses = recipeService.getRecipes(page, size);
////        if (recipeResponses.getRecipes().isEmpty()) {
////            throw new RecipeNotFoundException();
////        }
////        return recipeResponses;

        return recipeService.getRecipes(page, size)
                .thenApply(recipeResponses -> {
                    if (recipeResponses.getRecipes().isEmpty()) {
                        throw new RecipeNotFoundException();
                    }
                    return recipeResponses;
                })
                .exceptionally(ex -> {
                    // Handle exception and return appropriate response or rethrow
                    if (ex.getCause() instanceof RecipeNotFoundException) {
                        throw (RecipeNotFoundException) ex.getCause();
                    }
                    throw new RuntimeException(ex);
                });
    }

    @GetMapping("/{id}/operations")
    public CompletableFuture<RecipesResponse> getRecipes(@PathVariable Long id, @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "5") int size) {
        return recipeService.getRecipesByUserId(id, page, size)
                .thenApply(recipeResponses -> {
                    if (recipeResponses.getRecipes().isEmpty()) {
                        throw new RecipeNotFoundException();
                    }
                    return recipeResponses;
                })
                .exceptionally(ex -> {
                    // Handle exception and return appropriate response or rethrow
                    if (ex.getCause() instanceof RecipeNotFoundException) {
                        throw (RecipeNotFoundException) ex.getCause();
                    }
                    throw new RuntimeException(ex);
                });
    }
//        RecipesResponse recipeResponses = recipeService.getRecipesByUserId(id, page, size);
//        if (recipeResponses.getRecipes().isEmpty()) {
//            throw new RecipeNotFoundException();
//        }
//        return recipeResponses;
//    }

    @PutMapping("/{id}/operations/update/{recipeId}")
    public ResponseEntity<Void> updateRecipe(@PathVariable("id") Long userId,
                                             @PathVariable("recipeId") Long recipeId, @RequestPart(value = "title") String title, @RequestPart(value = "description") String description,
                                             @RequestPart(value = "time") String time,
                                             @RequestPart(value = "ingredients") String ingredientRequest,
                                             @RequestPart(value = "steps") String steps,
                                             @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) {
        Gson gson = new Gson();
        List<String> stepList = gson.fromJson(steps, List.class);
        List<IngredientRequest> ingr = new Gson().fromJson(ingredientRequest, new TypeToken<List<IngredientRequest>>(){}.getType());
        RecipeRequest request = new RecipeRequest();
        request.setUserId(userId);
        request.setTitle(title);
        request.setDescription(description);
        request.setTime(time);
        request.setIngredients(ingr);
        request.setSteps(stepList);
        request.setImages(imageFiles);
        recipeService.updateRecipe(recipeId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/operations/delete/{recipeId}")
    public ResponseEntity<RecipeService> deleteRecipe(@PathVariable("id") Long userId,
                                                       @PathVariable("recipeId") Long recipeId){
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{recipeId}/rating")
//    public ResponseEntity<Double> getRecipeRating(@PathVariable Long recipeId) {
//        try {
//            RecipeResponse recipeResponse = recipeService.getRecipeById(recipeId);
//            double rating = recipeResponse.getRating();
//            return ResponseEntity.ok(rating);
//        } catch (RecipeNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
    public CompletableFuture<ResponseEntity<Double>> getRecipeRating(@PathVariable Long recipeId) {
        return recipeService.getRecipeById(recipeId)
                .thenApply(recipeResponse -> {
                    double rating = recipeResponse.getRating();
                    return ResponseEntity.ok(rating);
                })
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof RecipeNotFoundException) {
                        return ResponseEntity.notFound().build();
                    }
                    throw new RuntimeException(ex);
                });
    }

    @PutMapping("/{recipeId}/rating")
    public ResponseEntity<Void> updateRecipeRating(@PathVariable Long recipeId, @RequestParam double rating) {
        try {
            recipeService.updateRecipeRating(recipeId, rating);
            return ResponseEntity.noContent().build();
        } catch (RecipeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
