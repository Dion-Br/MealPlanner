package be.uantwerpen.sd.project.service;

import be.uantwerpen.sd.project.model.RecipeBuilder;
import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;
import be.uantwerpen.sd.project.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;

public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe buildRecipe(String name, String description, List<MealComponent> components) {
        Recipe recipe = new RecipeBuilder()
                .setName(name)
                .setDescription(description)
                .addComponents(components)
                .build();

        recipeRepository.addRecipe(recipe);
        return recipe;
    }

    public void updateRecipe(Recipe recipe, String name, String description, List<MealComponent> components) {
        recipe.setName(name);
        recipe.setDescription(description);
        recipe.setComponents(new ArrayList<>(components));
        recipeRepository.notifyUpdate();
    }

    public void removeRecipe(Recipe recipe) {
        recipeRepository.removeRecipe(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
}