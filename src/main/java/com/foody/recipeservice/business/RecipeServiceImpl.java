package com.foody.recipeservice.business;

import com.foody.recipeservice.domain.request.IngredientRequest;
import com.foody.recipeservice.domain.request.RecipeRequest;
import com.foody.recipeservice.domain.response.CreateRecipeResponse;
import com.foody.recipeservice.domain.response.RecipeResponse;
import com.foody.recipeservice.persistence.RecipeRepository;
import com.foody.recipeservice.persistence.entity.IngredientEntity;
import com.foody.recipeservice.persistence.entity.RecipeEntity;
import com.foody.recipeservice.business.exceptions.RecipeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;

    @Override
    public CreateRecipeResponse createRecipe(RecipeRequest request) {
        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setTitle(request.getTitle());
        recipeEntity.setDescription(request.getDescription());
        recipeEntity.setImgUrls(request.getImgUrls());
        recipeEntity.setTime(request.getTime());

        List<IngredientEntity> ingredients = request.getIngredients().stream()
                .map(ingredientRequest -> {
                    IngredientEntity ingredientEntity = new IngredientEntity();
                    ingredientEntity.setName(ingredientRequest.getName());
                    ingredientEntity.setQuantity(ingredientRequest.getQuantity());
                    ingredientEntity.setRecipe(recipeEntity);
                    return ingredientEntity;
                })
                .collect(Collectors.toList());

        recipeEntity.setIngredients(ingredients);
        recipeEntity.setSteps(request.getSteps());

        RecipeEntity savedRecipe = recipeRepository.save(recipeEntity);

        RecipeResponse res = new RecipeResponse();
        res.setId(savedRecipe.getId());

        return CreateRecipeResponse.builder()
                .id(savedRecipe.getId())
                .build();
    }

    @Override
    public RecipeResponse getRecipeById(Long id) {
        Optional<RecipeEntity> recipeOptional = recipeRepository.findById(id);
        if (recipeOptional.isPresent()) {
            RecipeEntity recipeEntity = recipeOptional.get();
            RecipeResponse response = new RecipeResponse();
            response.setId(recipeEntity.getId());
            response.setTitle(recipeEntity.getTitle());
            response.setTime(recipeEntity.getTime());
            response.setDescription(recipeEntity.getDescription());
            response.setImgUrls(recipeEntity.getImgUrls());
            List<IngredientRequest> ingredientRequests = recipeEntity.getIngredients().stream()
                    .map(ingredientEntity -> new IngredientRequest(ingredientEntity.getName(), ingredientEntity.getQuantity()))
                    .collect(Collectors.toList());
            response.setIngredients(ingredientRequests);
            response.setSteps(recipeEntity.getSteps());
            return response;

        } else {
            throw new RecipeNotFoundException();
        }
    }

    @Override
    public List<RecipeResponse> getRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeEntity> recipePage = recipeRepository.findAll(pageable);

        List<RecipeResponse> recipeResponses = recipePage.getContent().stream()
                .map(this::mapToRecipeResponse)
                .collect(Collectors.toList());

        if (recipeResponses.isEmpty()) {
            throw new RecipeNotFoundException();
        }

        return recipeResponses;
    }

    private RecipeResponse mapToRecipeResponse(RecipeEntity recipeEntity) {
        RecipeResponse response = new RecipeResponse();
        response.setId(recipeEntity.getId());
        response.setTitle(recipeEntity.getTitle());
        response.setDescription(recipeEntity.getDescription());
        response.setTime(recipeEntity.getTime());
        response.setImgUrls(recipeEntity.getImgUrls());
        List<IngredientRequest> ingredientRequests = recipeEntity.getIngredients().stream()
                .map(ingredientEntity -> new IngredientRequest(ingredientEntity.getName(), ingredientEntity.getQuantity()))
                .collect(Collectors.toList());
        response.setIngredients(ingredientRequests);
        response.setSteps(recipeEntity.getSteps());
        return response;
    }

    @Override
    public void updateRecipe(Long id, RecipeRequest request) {
        RecipeEntity recipeEntity = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException());

        recipeEntity.setTitle(request.getTitle());
        recipeEntity.setTime(request.getTime());
        recipeEntity.setDescription(request.getDescription());
        recipeEntity.setImgUrls(request.getImgUrls());
        recipeEntity.setIngredients(mapToIngredientEntities(recipeEntity, request.getIngredients()));
        recipeEntity.setSteps(request.getSteps());

        recipeRepository.save(recipeEntity);
    }
    private List<IngredientEntity> mapToIngredientEntities(RecipeEntity recipeEntity, List<IngredientRequest> ingredientRequests) {
        // get the existing ingredients associated with the recipe
        List<IngredientEntity> existingIngredients = recipeEntity.getIngredients();

        // map of existing ingredient names to their corresponding entities for efficient lookup
        Map<String, IngredientEntity> existingIngredientMap = existingIngredients.stream()
                .collect(Collectors.toMap(IngredientEntity::getName, Function.identity()));

        // update existing ingredients / create new ones if they don't exist
        List<IngredientEntity> updatedIngredients = ingredientRequests.stream()
                .map(ingredientRequest -> {
                    String ingredientName = ingredientRequest.getName();
                    IngredientEntity existingIngredient = existingIngredientMap.get(ingredientName);

                    if (existingIngredient != null) {
                        // update the existing ingredient
                        existingIngredient.setName(ingredientRequest.getName());
                        existingIngredient.setQuantity(ingredientRequest.getQuantity());
                        return existingIngredient;
                    } else {
                        //  new ingredient
                        IngredientEntity newIngredient = new IngredientEntity();
                        newIngredient.setName(ingredientName);
                        newIngredient.setQuantity(ingredientRequest.getQuantity());
                        newIngredient.setRecipe(recipeEntity);
                        return newIngredient;
                    }
                })
                .collect(Collectors.toList());

        return updatedIngredients;
    }

    @Override
    public void deleteRecipe(long id) {
        this.recipeRepository.deleteById(id);
    }

}

