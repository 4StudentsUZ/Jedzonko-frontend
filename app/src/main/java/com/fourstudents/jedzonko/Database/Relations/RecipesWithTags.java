package com.fourstudents.jedzonko.Database.Relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.RecipeTagCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Tag;

import java.util.List;

public class RecipesWithTags {
    @Embedded
    public Recipe recipe;
    @Relation(
            parentColumn = "recipeId",
            entityColumn = "tagId",
            associateBy = @Junction(RecipeTagCrossRef.class)
    )
    public List<Tag> tags;
}
