package com.foody.recipeservice.business.rabbit.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeCreatedSearchEvent {
    private Long recipeId;
    private String title;
    private String description;

}
