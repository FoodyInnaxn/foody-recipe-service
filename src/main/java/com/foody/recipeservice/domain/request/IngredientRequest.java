package com.foody.recipeservice.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientRequest {
//    @NotBlank
    private String name;
//    @NotBlank
    private String quantity;
}
