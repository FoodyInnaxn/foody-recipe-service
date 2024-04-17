package com.foody.recipeservice.business.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foody.recipeservice.business.rabbit.event.RecipeCreatedSearchEvent;
import com.foody.recipeservice.business.rabbit.event.RecipeImgEvent;
import com.foody.recipeservice.configuration.RabbitMQConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeEventPublisher {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void publishRecipeCreatedSearchEvent(RecipeCreatedSearchEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.RECIPE_EXCHANGE, RabbitMQConfig.RECIPE_ROUTING_KEY_CREATE_SEARCH, createMessageSearch(event));
    }

    public void publishRecipeUpdatedSearchEvent(RecipeCreatedSearchEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.RECIPE_EXCHANGE, RabbitMQConfig.RECIPE_ROUTING_KEY_UPDATE_SEARCH, createMessageSearch(event));
    }

    public void publishRecipeCreatedImgEvent(RecipeImgEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.RECIPE_EXCHANGE, RabbitMQConfig.RECIPE_ROUTING_KEY_CREATE_IMG, createMessage(event));
    }

    public void publishRecipeUpdatedImgEvent(RecipeImgEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.RECIPE_EXCHANGE, RabbitMQConfig.RECIPE_ROUTING_KEY_UPDATE_IMG, createMessage(event));
    }

    public void publishDelete(Long recipeId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.RECIPE_FANOUT_EXCHANGE, "", recipeId);
    }

    private Message createMessageSearch(Object request){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("application/json");
            messageProperties.setContentEncoding("UTF-8");
            Message msg = new Message(objectMapper.writeValueAsBytes(request), messageProperties);
            return msg;
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle or log the exception as needed
            return null; // or throw a custom exception
        }
    }
    private Message createMessage(RecipeImgEvent event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("application/json");
            messageProperties.setContentEncoding("UTF-8");

            // Encode MultipartFile objects to Base64 strings
            List<String> encodedImages = event.getImages().stream()
                    .map(this::encodeFileToBase64)
                    .collect(Collectors.toList());

            // Create a new event object with the encoded images
            RecipeImgEvent eventWithEncodedImages = RecipeImgEvent.builder()
                    .encodedImages(encodedImages)
                    .folderName(event.getFolderName())
                    .recipeId(event.getRecipeId())
                    .build();

            // Serialize the modified event object
            byte[] messageBody = objectMapper.writeValueAsBytes(eventWithEncodedImages);
            Message msg = new Message(messageBody, messageProperties);
            return msg;
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle or log the exception as needed
            return null; // or throw a custom exception
        }
    }

    private String encodeFileToBase64(MultipartFile file) {
        try {
            return Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace(); // Handle or log the exception as needed
            return null; // or throw a custom exception
        }
    }

}
