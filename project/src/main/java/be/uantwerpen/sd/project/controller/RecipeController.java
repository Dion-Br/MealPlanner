package be.uantwerpen.sd.project.controller;

import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;
import be.uantwerpen.sd.project.service.RecipeService;
import be.uantwerpen.sd.project.view.RecipeView;

import java.util.List;

public class RecipeController {
    private final RecipeService recipeService = new RecipeService();
    private RecipeView view;

    public RecipeController(RecipeView view) {
        this.view = view;
    }

    public void addRecipe(String name, String description, List<MealComponent> components) {
        if (name != null && !name.isBlank()
                && description != null && !description.isBlank()
                && components != null && !components.isEmpty()) {
            recipeService.buildRecipe(name, description, components);
            getRecipes();
        } else {
            view.showError("Recipe must have a name, description, and at least one ingredient");
        }
    }

    public void removeRecipe(int index) {
        List<Recipe> recipes = recipeService.getAllRecipes();
        if (index >= 0 && index < recipes.size()) {
            Recipe recipe = recipes.get(index);
            recipeService.removeRecipe(recipe);
            getRecipes();
        }
    }

    public void getRecipes() {
        view.showRecipes(recipeService.getAllRecipes());
    }
}
