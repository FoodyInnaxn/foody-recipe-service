package com.foody.recipeservice.business;

import com.foody.recipeservice.business.rabbit.event.ImageEvent;
import com.foody.recipeservice.business.rabbit.event.RecipeImgEvent;
import com.foody.recipeservice.business.rabbit.event.SavedRecipeCreatedEvent;
import com.foody.recipeservice.configuration.RabbitMQConfig;
import com.foody.recipeservice.business.rabbit.event.RecipeCreatedSearchEvent;
import com.foody.recipeservice.business.rabbit.RecipeEventPublisher;
import com.foody.recipeservice.domain.request.IngredientRequest;
import com.foody.recipeservice.domain.request.RecipeRequest;
import com.foody.recipeservice.domain.response.CreateRecipeResponse;
import com.foody.recipeservice.domain.response.RecipeResponse;
import com.foody.recipeservice.domain.response.RecipesResponse;
import com.foody.recipeservice.persistence.RecipeRepository;
import com.foody.recipeservice.persistence.entity.IngredientEntity;
import com.foody.recipeservice.persistence.entity.RecipeEntity;
import com.foody.recipeservice.business.exceptions.RecipeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeEventPublisher eventPublisher;

    @Override
    public CreateRecipeResponse createRecipe(RecipeRequest request) {
        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setUserId(request.getUserId());
        recipeEntity.setTitle(request.getTitle());
        recipeEntity.setDescription(request.getDescription());
        recipeEntity.setNumberSaved(0);
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

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            // sending to image server
            RecipeImgEvent imgEvent = new RecipeImgEvent();
            imgEvent.setRecipeId(savedRecipe.getId());
            imgEvent.setFolderName("recipes_1");
            imgEvent.setImages(request.getImages());
            eventPublisher.publishRecipeCreatedImgEvent(imgEvent);
            System.out.println("hey sending to img " + imgEvent);
        }

        // sending info to the search queue
        RecipeCreatedSearchEvent searchEvent = new RecipeCreatedSearchEvent();
        searchEvent.setRecipeId(savedRecipe.getId());
        searchEvent.setTitle(savedRecipe.getTitle());
        searchEvent.setDescription(savedRecipe.getDescription());
        eventPublisher.publishRecipeCreatedSearchEvent(searchEvent);
        System.out.println("hey sending to search " + searchEvent);


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
            response.setUserId(recipeEntity.getUserId());
            response.setTime(recipeEntity.getTime());
            response.setDescription(recipeEntity.getDescription());
            response.setNumberSaved(recipeEntity.getNumberSaved());
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
    public RecipesResponse getRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeEntity> recipePage = recipeRepository.findAll(pageable);

        List<RecipeResponse> recipeResponses = recipePage.getContent().stream()
                .map(this::mapToRecipeResponse)
                .collect(Collectors.toList());

        if (recipeResponses.isEmpty()) {
            throw new RecipeNotFoundException();
        }

        return new RecipesResponse(recipeResponses, recipePage.getTotalPages());
    }

    @Override
    public RecipesResponse getRecipesByUserId(Long id, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeEntity> recipePage = recipeRepository.findByUserId(id, pageable);

        if (recipePage.isEmpty()) {
            throw new RecipeNotFoundException();
        }

        List<RecipeResponse> recipeResponses = recipePage.getContent().stream()
                .map(this::mapToRecipeResponse)
                .collect(Collectors.toList());

        if (recipeResponses.isEmpty()) {
            throw new RecipeNotFoundException();
        }

        return new RecipesResponse(recipeResponses, recipePage.getTotalPages());
    }

    private RecipeResponse mapToRecipeResponse(RecipeEntity recipeEntity) {
        RecipeResponse response = new RecipeResponse();
        response.setId(recipeEntity.getId());
        response.setTitle(recipeEntity.getTitle());
        response.setDescription(recipeEntity.getDescription());
        response.setTime(recipeEntity.getTime());
        response.setUserId(recipeEntity.getUserId());
        response.setNumberSaved(recipeEntity.getNumberSaved());
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
//        recipeEntity.setUserId(recipeEntity.getUserId());
        recipeEntity.setDescription(request.getDescription());
        recipeEntity.setTime(request.getTime());
        List<IngredientEntity> updatedIngredients = mapToIngredientEntities(recipeEntity, request.getIngredients());
        recipeEntity.setIngredients(updatedIngredients);
        recipeEntity.setSteps(request.getSteps());

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                // sending to image server
                RecipeImgEvent imgEvent = new RecipeImgEvent();
                imgEvent.setRecipeId(id);
                imgEvent.setFolderName("recipes_1");
                imgEvent.setImages(request.getImages());
                eventPublisher.publishRecipeUpdatedImgEvent(imgEvent);
                System.out.println("hey sending to img to update " + imgEvent);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        recipeRepository.save(recipeEntity);

        // sending info to the search queue
        RecipeCreatedSearchEvent searchEvent = new RecipeCreatedSearchEvent();
        searchEvent.setRecipeId(id);
        searchEvent.setTitle(request.getTitle());
        searchEvent.setDescription(request.getDescription());
        eventPublisher.publishRecipeUpdatedSearchEvent(searchEvent);
        System.out.println("hey sending to search " + searchEvent);
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
    public void deleteRecipe(Long id) {
        this.recipeRepository.deleteById(id);
        eventPublisher.publishDelete(id);
        System.out.println("hey delete recipe" + id);
    }

    @Override
    @RabbitListener(queues = RabbitMQConfig.IMG_QUEUE)
    public void handleImages(ImageEvent imageEvent) {
        RecipeEntity recipeEntity = recipeRepository.findById(imageEvent.getRecipeId())
                .orElseThrow(() -> new RecipeNotFoundException());

        if (recipeEntity.getImgUrls().isEmpty()) {
            try {
                recipeEntity.setImgUrls(imageEvent.getImagesUrls());
                recipeRepository.save(recipeEntity);
            } catch (Exception e) {
                System.out.println("Error saving images: " + e.getMessage());
            }
        }

        System.out.println("Saved recipe " + imageEvent.getRecipeId() + " with images.");
    }

    @Override
    @RabbitListener(queues = RabbitMQConfig.SAVED_RECIPE_QUEUE)
    public void receiveSavedRecipeEvent(SavedRecipeCreatedEvent savedRecipeCreatedEvent) {
        Optional<RecipeEntity> recipeOptional = recipeRepository.findById(savedRecipeCreatedEvent.getRecipeId());
        if (recipeOptional.isEmpty()) {
            throw new RecipeNotFoundException();
        }
        RecipeEntity recipeEntity = recipeOptional.get();

        if (savedRecipeCreatedEvent.getNumberSaved() == 1 ){
            recipeEntity.setNumberSaved(recipeEntity.getNumberSaved() + 1);
            recipeRepository.save(recipeEntity);
        }
        else if(savedRecipeCreatedEvent.getNumberSaved() == -1 ){
            recipeEntity.setNumberSaved(recipeEntity.getNumberSaved() - 1);
            recipeRepository.save(recipeEntity);
        }
        else {
            System.out.println("NESHTOSTANA" );
            throw new RecipeNotFoundException();
        }

        System.out.println("Received saved recipe event: " + savedRecipeCreatedEvent.getNumberSaved() + savedRecipeCreatedEvent.getRecipeId());
    }

}