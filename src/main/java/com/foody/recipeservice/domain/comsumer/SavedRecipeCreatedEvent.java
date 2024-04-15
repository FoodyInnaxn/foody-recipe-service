package com.foody.recipeservice.domain.comsumer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedRecipeCreatedEvent {
    private Long recipeId;
    private Integer numberSaved;
}