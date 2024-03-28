package com.foody.recipeservice.domain.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRecipeResponse {
    private Long id;
}
