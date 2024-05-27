package com.foody.recipeservice.controller;

import com.foody.recipeservice.domain.request.IngredientRequest;
import com.foody.recipeservice.domain.response.CreateRecipeResponse;
import com.foody.recipeservice.domain.response.RecipeResponse;
import com.foody.recipeservice.business.RecipeService;
import com.foody.recipeservice.business.exceptions.RecipeNotFoundException;
import com.foody.recipeservice.domain.response.RecipesResponse;
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

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping(value = "/{id}/operations/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRecipeResponse createRecipe(@PathVariable("id") Long userId, @RequestPart(value = "title") String title, @RequestPart(value = "description") String description,
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
    public RecipeResponse getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }
    @GetMapping("/view")
    public RecipesResponse getRecipes(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        RecipesResponse recipeResponses = recipeService.getRecipes(page, size);
        if (recipeResponses.getRecipes().isEmpty()) {
            throw new RecipeNotFoundException();
        }
        return recipeResponses;
    }

    @GetMapping("/{id}/operations")
    public RecipesResponse getRecipes(@PathVariable Long id, @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "5") int size) {
        RecipesResponse recipeResponses = recipeService.getRecipesByUserId(id, page, size);
        if (recipeResponses.getRecipes().isEmpty()) {
            throw new RecipeNotFoundException();
        }
        return recipeResponses;
    }

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
}
