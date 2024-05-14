package com.foody.recipeservice.persistence;

import com.foody.recipeservice.persistence.entity.RecipeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {
    @Transactional
    @Modifying
    @Query(value = "UPDATE recipe SET numberSaved = numberSaved + :increment WHERE id = :recipeId", nativeQuery = true)
    void updateNumberSaved(@Param("recipeId") Long recipeId, @Param("increment") int increment);

    Page<RecipeEntity> findByUserId(Long userId, Pageable pageable);

}
