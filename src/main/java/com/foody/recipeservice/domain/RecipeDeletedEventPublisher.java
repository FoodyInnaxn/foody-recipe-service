package com.foody.recipeservice.domain;

import com.foody.recipeservice.configuration.RabbitMQConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecipeDeletedEventPublisher {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void publishRecipeDeletedEvent(Long event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.RECIPE_EXCHANGE, RabbitMQConfig.DELETE_RECIPE_ROUTING_KEY, event);
    }
}