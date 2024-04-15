package com.foody.recipeservice.business;

import com.foody.recipeservice.domain.ImageModel;
import com.foody.recipeservice.domain.RecipeCreatedEvent;
import com.foody.recipeservice.domain.RecipeCreatedEventPublisher;
import com.foody.recipeservice.domain.RecipeDeletedEventPublisher;
import com.foody.recipeservice.domain.request.IngredientRequest;
import com.foody.recipeservice.domain.request.RecipeRequest;
import com.foody.recipeservice.domain.response.CreateRecipeResponse;
import com.foody.recipeservice.domain.response.RecipeResponse;
import com.foody.recipeservice.persistence.ImageRepository;
import com.foody.recipeservice.persistence.RecipeRepository;
import com.foody.recipeservice.persistence.entity.Image;
import com.foody.recipeservice.persistence.entity.IngredientEntity;
import com.foody.recipeservice.persistence.entity.RecipeEntity;
import com.foody.recipeservice.business.exceptions.RecipeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final ImageRepository imageRepository;
    private final RecipeCreatedEventPublisher eventPublisher;
    private final RecipeDeletedEventPublisher recipeDeletedEventPublisher;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public CreateRecipeResponse createRecipe(RecipeRequest request) {
        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setTitle(request.getTitle());
        recipeEntity.setDescription(request.getDescription());
        recipeEntity.setNumberSaved(0);

        List<Image> images = new ArrayList<>();
        try {
            if (request.getImages() != null) {
                for (MultipartFile file : request.getImages()) {
                    if (!file.isEmpty()) {
                        Image img = cloudinaryService.uploadFile(file, "recipes_1", recipeEntity);
                        images.add(img);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

        recipeEntity.setImages(images);

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

        RecipeCreatedEvent event = new RecipeCreatedEvent();
        event.setRecipeId(savedRecipe.getId());
        event.setTitle(savedRecipe.getTitle());
        event.setDescription(savedRecipe.getDescription());

        eventPublisher.publishRecipeCreatedEvent(event);
        System.out.println("hey " + event);

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
            response.setNumberSaved(recipeEntity.getNumberSaved());
            List<ImageModel> imageModels = recipeEntity.getImages().stream()
                    .map(image -> new ImageModel(image.getUrl()))
                    .toList();
            response.setImgUrls(imageModels);
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
        response.setNumberSaved(recipeEntity.getNumberSaved());
        List<ImageModel> imageModels = recipeEntity.getImages().stream()
                .map(image -> new ImageModel(image.getUrl()))
                .toList();
        response.setImgUrls(imageModels);
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

        List<Image> imgs = imageRepository.findByRecipeId(recipeEntity.getId());
        cloudinaryService.deleteImages(imgs);

        recipeEntity.setTitle(request.getTitle());
        recipeEntity.setDescription(request.getDescription());
        recipeEntity.setTime(request.getTime());

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<Image> images = new ArrayList<>();
            try {
                if (request.getImages() != null) {
                    for (MultipartFile file : request.getImages()) {
                        if (!file.isEmpty()) {
                            Image img = cloudinaryService.uploadFile(file, "recipe_1", recipeEntity);
                            images.add(img);
                        }
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            recipeEntity.setImages(images);
            System.out.println(recipeEntity);

        }

        List<IngredientEntity> updatedIngredients = mapToIngredientEntities(recipeEntity, request.getIngredients());
        recipeEntity.setIngredients(updatedIngredients);

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
        List<Image> imgs = imageRepository.findByRecipeId(id);
        cloudinaryService.deleteImages(imgs);
        this.recipeRepository.deleteById(id);
        recipeDeletedEventPublisher.publishRecipeDeletedEvent(id);
        System.out.println("hey delete recipe" + id);
    }
}