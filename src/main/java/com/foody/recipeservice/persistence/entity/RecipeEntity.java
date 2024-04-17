package com.foody.recipeservice.persistence.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="recipe")
public class RecipeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String time;
    private Integer numberSaved;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_img_urls", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "img_url")
    private List<String> imgUrls;

//    @OneToMany(fetch= FetchType.EAGER, mappedBy = "recipe", cascade = CascadeType.ALL)
//    private List<Image> images;

    @OneToMany(fetch= FetchType.EAGER, mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<IngredientEntity> ingredients;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "step")
    private List<String> steps;

}
