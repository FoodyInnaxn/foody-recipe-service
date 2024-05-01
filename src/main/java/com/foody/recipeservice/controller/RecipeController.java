package com.foody.recipeservice.controller;

import com.foody.recipeservice.domain.request.IngredientRequest;
import com.foody.recipeservice.domain.response.CreateRecipeResponse;
import com.foody.recipeservice.domain.response.RecipeResponse;
import com.foody.recipeservice.business.RecipeService;
import com.foody.recipeservice.business.exceptions.RecipeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.foody.recipeservice.domain.request.RecipeRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public CreateRecipeResponse createRecipe(@RequestBody RecipeRequest request) {
//        return recipeService.createRecipe(request);
//    }

    @PostMapping("/{id}/operations/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRecipeResponse createRecipe(@PathVariable("id") Long userId, @RequestPart(value = "title") String title, @RequestPart(value = "description") String description,
                                             @RequestPart(value = "time") String time,
                                             @RequestPart(value = "ingredients") List<IngredientRequest> ingredientRequest,
                                             @RequestPart(value = "steps") List<String> steps,
                                             @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) {
        RecipeRequest request = new RecipeRequest();
        request.setUserId(userId);
        request.setTitle(title);
        request.setDescription(description);
        request.setTime(time);
        request.setIngredients(ingredientRequest);
        request.setSteps(steps);
        request.setImages(imageFiles);
        return recipeService.createRecipe(request);
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public CreateRecipeResponse createRecipe(@Valid RecipeRequest request,
//                                             @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
//        request.setImages(imageFiles);
//        return recipeService.createRecipe(request);
//    }

    @GetMapping("/view/{id}")
    public RecipeResponse getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }
    @GetMapping("/view")
    public List<RecipeResponse> getRecipes(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        List<RecipeResponse> recipeResponses = recipeService.getRecipes(page, size);
        if (recipeResponses.isEmpty()) {
            throw new RecipeNotFoundException();
        }
        return recipeResponses;
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Void> updateRecipe(@PathVariable Long id, @RequestBody RecipeRequest request) {
//        recipeService.updateRecipe(id, request);
//        return ResponseEntity.noContent().build();
//    }

    @PutMapping("/{id}/operations/update/{recipeId}")
    public ResponseEntity<Void> updateRecipe(@PathVariable("id") Long userId,
                                             @PathVariable("recipeId") Long recipeId, @RequestPart(value = "title") String title, @RequestPart(value = "description") String description,
                                             @RequestPart(value = "time") String time,
                                             @RequestPart(value = "ingredients") List<IngredientRequest> ingredientRequest,
                                             @RequestPart(value = "steps") List<String> steps,
                                             @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) {
        RecipeRequest request = new RecipeRequest();
        request.setUserId(userId);
        request.setTitle(title);
        request.setDescription(description);
        request.setTime(time);
        request.setIngredients(ingredientRequest);
        request.setSteps(steps);
        request.setImages(imageFiles);
        recipeService.updateRecipe(recipeId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/operations/delete/{recipeId}")
    public ResponseEntity<RecipeService> deletePatient(@PathVariable("id") Long userId,
                                                       @PathVariable("recipeId") Long recipeId){
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.noContent().build();
    }
}
