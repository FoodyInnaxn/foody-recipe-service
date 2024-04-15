package com.foody.recipeservice.business;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.foody.recipeservice.persistence.entity.Image;
import com.foody.recipeservice.persistence.entity.RecipeEntity;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    @Resource
    private Cloudinary cloudinary;

    @Override
    public Image uploadFile(MultipartFile file, String folderName, RecipeEntity recipe) {
        try{
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);
            Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");
            Image image = new Image();
            image.setPublicId(publicId);
            image.setUrl(cloudinary.url().secure(true).generate(publicId));
            image.setRecipe(recipe);
            return image;

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteImages(List<Image> images){
        for (Image img : images) {
            try {
                cloudinary.uploader().destroy(img.getPublicId(), ObjectUtils.asMap("resource_type","image"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
