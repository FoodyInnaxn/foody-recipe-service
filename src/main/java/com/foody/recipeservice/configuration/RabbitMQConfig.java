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

    public static final String SEARCH_QUEUE = "search_recipe_queue";
    public static final String DELETE_QUEUE = "delete_recipe_queue";
    public static final String SEARCH_ROUTING_KEY = "search_recipe_routingKey";
    public static final String DELETE_RECIPE_ROUTING_KEY = "delete_recipe_routingKey";
    public static final String SAVED_RECIPE_QUEUE = "savedRecipe_queue";
    public static final String SAVED_RECIPE_ROUTING_KEY = "savedRecipe_routingKey";

    // Define a TopicExchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(RECIPE_EXCHANGE);
    }

    @Bean
    public Queue search_queue() {
        return new Queue(SEARCH_QUEUE);
    }

    // Create a binding between "search_recipe_queue" and the exchange with the routing key pattern "order.logs.customer.#"
    @Bean
    public Binding bindingCreateSearch(Queue search_queue, TopicExchange exchange) {
        return BindingBuilder.bind(search_queue)
                .to(exchange)
                .with(SEARCH_ROUTING_KEY);
    }

    @Bean
    public Queue delete_queue() {
        return new Queue(DELETE_QUEUE);
    }

    @Bean
    public Binding bindingDeleteRecipe(Queue delete_queue, TopicExchange exchange) {
        return BindingBuilder.bind(delete_queue)
                .to(exchange)
                .with(DELETE_RECIPE_ROUTING_KEY);
    }

    @Bean
    public Queue saved_recipe_queue() {
        return new Queue(SAVED_RECIPE_QUEUE);
    }

    @Bean
    public Binding bindingSavedRecipe(Queue delete_queue, TopicExchange exchange) {
        return BindingBuilder.bind(delete_queue)
                .to(exchange)
                .with(SAVED_RECIPE_ROUTING_KEY);
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
