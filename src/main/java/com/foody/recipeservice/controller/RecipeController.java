package com.foody.recipeservice.controller;

import com.foody.recipeservice.domain.response.CreateRecipeResponse;
import com.foody.recipeservice.domain.response.RecipeResponse;
import com.foody.recipeservice.business.RecipeService;
import com.foody.recipeservice.business.exceptions.RecipeNotFoundException;
import com.foody.recipeservice.persistence.RecipeRepository;
import com.foody.recipeservice.persistence.entity.RecipeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.foody.recipeservice.domain.request.RecipeRequest;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRecipeResponse createRecipe(@RequestBody RecipeRequest request) {
        return recipeService.createRecipe(request);
    }

    @GetMapping("/{id}")
    public RecipeResponse getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }
    @GetMapping()
    public List<RecipeResponse> getRecipes(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        List<RecipeResponse> recipeResponses = recipeService.getRecipes(page, size);
        if (recipeResponses.isEmpty()) {
            throw new RecipeNotFoundException();
        }
        return recipeResponses;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRecipe(@PathVariable Long id, @RequestBody RecipeRequest request) {
        recipeService.updateRecipe(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<RecipeService> deletePatient(@PathVariable Long id){
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
