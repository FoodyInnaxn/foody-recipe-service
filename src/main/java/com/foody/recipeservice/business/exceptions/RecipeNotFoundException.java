package com.foody.recipeservice.business.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RecipeNotFoundException  extends ResponseStatusException {
    public RecipeNotFoundException() {
        super(HttpStatus.NOT_FOUND, "INVALID_RECIPE_ID");
    }
}
