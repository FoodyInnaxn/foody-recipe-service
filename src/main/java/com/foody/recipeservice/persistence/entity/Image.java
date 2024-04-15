package com.foody.recipeservice.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "url_image")
    private String url;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    @NotNull
    private RecipeEntity recipe;

}
