package com.foody.recipeservice.business.rabbit.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeImgEvent {
    @JsonIgnore
    private List<MultipartFile> images; // Do not serialize MultipartFile objects
    private List<String> encodedImages; // Include Base64-encoded strings for serialization
    private String folderName;
    private Long recipeId;
}