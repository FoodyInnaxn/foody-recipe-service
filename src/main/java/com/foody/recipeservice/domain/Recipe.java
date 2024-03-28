package com.foody.recipeservice.domain;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Recipe {

    private Long id;
    private List<String> imgUrls;
    private String title;
    private String description;
//    private Long likeId;
    private List<String> steps;
    private List<Ingredient> ingredients;
    // Long userId;
    // private String username;
}
