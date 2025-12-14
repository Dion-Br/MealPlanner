package be.uantwerpen.sd.project.service;

import be.uantwerpen.sd.project.model.RecipeBuilder;
import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;
import be.uantwerpen.sd.project.repository.RecipeRepository;

import java.util.List;

public class RecipeService {
    private final RecipeRepository recipeRepository = RecipeRepository.getInstance();

    public Recipe buildRecipe(String name, String description, List<MealComponent> components) {
        RecipeBuilder builder = new RecipeBuilder()
                .setName(name).
                setDescription(description);

        for (MealComponent c : components) {
            builder.addComponent(c);
        }

        Recipe recipe = builder.build();
        recipeRepository.addRecipe(recipe);
        return recipe;
    }

    public void removeRecipe(Recipe recipe) {
        recipeRepository.removeRecipe(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
}
