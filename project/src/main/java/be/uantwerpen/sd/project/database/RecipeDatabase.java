package be.uantwerpen.sd.project.database;

import be.uantwerpen.sd.project.model.domain.Recipe;

import java.util.List;

public class RecipeDatabase {
    private List<Recipe> recipes;

    public RecipeDatabase(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    //add Recipe
    public void addRecipe(Recipe r){
        recipes.add(r);
    }


    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
