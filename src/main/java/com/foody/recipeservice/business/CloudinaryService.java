package com.foody.recipeservice.business;

import com.foody.recipeservice.persistence.entity.Image;
import com.foody.recipeservice.persistence.entity.RecipeEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CloudinaryService {
    Image uploadFile(MultipartFile file, String folderName, RecipeEntity recipe);
    void deleteImages(List<Image> images);
}
