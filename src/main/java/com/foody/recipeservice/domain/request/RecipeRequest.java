package com.foody.recipeservice.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeRequest {
    @NotBlank
    private Long userId;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String time;
    private List<MultipartFile> images;
    private List<IngredientRequest> ingredients;
    private List<String> steps;
}
