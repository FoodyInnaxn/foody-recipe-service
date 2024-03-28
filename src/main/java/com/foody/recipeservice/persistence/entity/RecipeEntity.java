package com.foody.recipeservice.persistence.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String title;

    @NotBlank
    private String description;
    @NotBlank
    private String time;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_img_urls", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "img_url")
    private List<String> imgUrls;

    @OneToMany(fetch= FetchType.EAGER, mappedBy = "recipe", cascade = CascadeType.ALL)
//    @NotBlank
    private List<IngredientEntity> ingredients;

//    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
//    private List<StepEntity> steps;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "step")
    private List<String> steps;


    //userid
    //username
}
