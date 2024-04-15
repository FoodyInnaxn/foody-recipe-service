package com.foody.recipeservice.domain.comsumer;

import com.foody.recipeservice.business.exceptions.RecipeNotFoundException;
import com.foody.recipeservice.configuration.RabbitMQConfig;
import com.foody.recipeservice.persistence.RecipeRepository;
import com.foody.recipeservice.persistence.entity.RecipeEntity;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SavedRecipeEventSubscriber {
    @Autowired
    private RecipeRepository recipeRepository;

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

        System.out.println("Received recipe created event: " + savedRecipeCreatedEvent.getNumberSaved() + savedRecipeCreatedEvent.getRecipeId());
    }
}
