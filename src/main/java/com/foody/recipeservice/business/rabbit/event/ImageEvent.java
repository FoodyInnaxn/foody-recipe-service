package com.foody.recipeservice.business.rabbit.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageEvent {
    private List<String> imagesUrls;
    private Long recipeId;
}
