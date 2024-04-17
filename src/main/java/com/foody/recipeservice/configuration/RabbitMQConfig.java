package com.foody.recipeservice.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String RECIPE_EXCHANGE = "recipe_exchange";
    public static final String RECIPE_FANOUT_EXCHANGE = "recipe_fanout_exchange";
    public static final String RECIPE_QUEUE_IMG = "recipe_queue_img";
    public static final String IMG_QUEUE = "img_queue";
    public static final String RECIPE_QUEUE_SEARCH = "recipe_queue_search";
    public static final String RECIPE_ROUTING_KEY_CREATE_SEARCH = "recipe_routingKey_create_search";
    public static final String RECIPE_ROUTING_KEY_CREATE_IMG = "recipe_routingKey_create_img";
    public static final String RECIPE_ROUTING_KEY_UPDATE_SEARCH = "recipe_routingKey_update_search";
    public static final String RECIPE_ROUTING_KEY_UPDATE_IMG = "recipe_routingKey_update_img";
    //saved recipes
    public static final String SAVED_RECIPE_QUEUE = "savedRecipe_queue";

    //fanout
    public static final String FANOUT_IMG_QUEUE = "fanout_img_queue";
    public static final String FANOUT_COMMENT_QUEUE = "fanout_comment_queue";
    public static final String FANOUT_SEARCH_QUEUE = "fanout_search_queue";
    public static final String FANOUT_SAVED_QUEUE = "fanout_saved_queue";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(RECIPE_EXCHANGE);
    }

    // queues and bindings for when recipe is created and updated
    @Bean
    public Queue recipeQueueImg() {
        return new Queue(RECIPE_QUEUE_IMG);
    }

    @Bean
    public Binding bindingRecipeQueueImgCreate(Queue recipeQueueImg, DirectExchange exchange) {
        return BindingBuilder.bind(recipeQueueImg)
                .to(exchange)
                .with(RECIPE_ROUTING_KEY_CREATE_IMG);
    }

    @Bean
    public Binding bindingRecipeQueueImgUpdate(Queue recipeQueueImg, DirectExchange exchange) {
        return BindingBuilder.bind(recipeQueueImg)
                .to(exchange)
                .with(RECIPE_ROUTING_KEY_UPDATE_IMG);
    }
    @Bean
    public Queue recipeQueueSearch() {
        return new Queue(RECIPE_QUEUE_SEARCH);
    }

    @Bean
    public Binding bindingRecipeQueueSearchCreate(Queue recipeQueueSearch, DirectExchange exchange) {
        return BindingBuilder.bind(recipeQueueSearch)
                .to(exchange)
                .with(RECIPE_ROUTING_KEY_CREATE_SEARCH);
    }

    @Bean
    public Binding bindingRecipeQueueSearchUpdate(Queue recipeQueueSearch, DirectExchange exchange) {
        return BindingBuilder.bind(recipeQueueSearch)
                .to(exchange)
                .with(RECIPE_ROUTING_KEY_UPDATE_SEARCH);
    }
    // end queues and bindings for when recipe is created and updated

    @Bean
    public Queue imgQueue() {
        return new Queue(IMG_QUEUE);
    }

    @Bean
    public Queue savedRecipeQueue() {
        return new Queue(SAVED_RECIPE_QUEUE);
    }
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(RECIPE_FANOUT_EXCHANGE);
    }

    @Bean
    public Queue fanoutImg() {
        return new Queue(FANOUT_IMG_QUEUE);
    }
    @Bean
    public Queue fanoutSearch() {
        return new Queue(FANOUT_SEARCH_QUEUE);
    }
    @Bean
    public Queue fanoutComment() {
        return new Queue(FANOUT_COMMENT_QUEUE);
    }
    @Bean
    public Queue fanoutSaved() {
        return new Queue(FANOUT_SAVED_QUEUE);
    }

    @Bean
    public Binding bindingImgQueue(Queue fanoutImg, FanoutExchange exchange) {
        return BindingBuilder.bind(fanoutImg)
                .to(exchange);
    }

    @Bean
    public Binding bindingSearchQueue(Queue fanoutSearch, FanoutExchange exchange) {
        return BindingBuilder.bind(fanoutSearch)
                .to(exchange);
    }
    @Bean
    public Binding bindingCommentQueue(Queue fanoutComment, FanoutExchange exchange) {
        return BindingBuilder.bind(fanoutComment)
                .to(exchange);
    }

    @Bean
    public Binding bindingSavedQueue(Queue fanoutSaved, FanoutExchange exchange) {
        return BindingBuilder.bind(fanoutSaved)
                .to(exchange);
    }
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}