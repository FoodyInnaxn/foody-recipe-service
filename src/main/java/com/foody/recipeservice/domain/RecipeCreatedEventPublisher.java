package com.foody.recipeservice.domain;

import com.foody.recipeservice.configuration.RabbitMQConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecipeCreatedEventPublisher {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void publishRecipeCreatedEvent(RecipeCreatedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.RECIPE_EXCHANGE, RabbitMQConfig.SEARCH_ROUTING_KEY, event);
    }
}
