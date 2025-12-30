package be.uantwerpen.sd.project.controller;

import be.uantwerpen.sd.project.model.domain.Ingredient;
import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;
import be.uantwerpen.sd.project.service.RecipeService;
import be.uantwerpen.sd.project.view.RecipeView;
import be.uantwerpen.sd.project.model.domain.enums.Unit;

import java.util.ArrayList;
import java.util.List;

public class RecipeController {
    private final RecipeService recipeService = new RecipeService();
    private RecipeView view;
    private final List<MealComponent> currentIngredients = new ArrayList<>();

    public RecipeController(RecipeView view) {
        this.view = view;
    }

    public void addIngredient(String name, double quantity, Unit unit, List<String> tags) {
        if (name == null || name.isBlank() || quantity <= 0 || unit == null) {
            view.showError("Invalid input. Name, quantity and unit are required.");
            return;
        }
        currentIngredients.add(new Ingredient(name, quantity, unit, tags));
    }

    public void removeIngredient(int index) {
        if (index >= 0 && index < currentIngredients.size()) {
            currentIngredients.remove(index);
        } else {
            view.showError("Invalid ingredient selection.");
        }
    }

    public void addRecipe(String name, String description) {
        if (name == null || name.isBlank()
                || description == null || description.isBlank()
                || currentIngredients.isEmpty()) {
            view.showError("Recipe must have a name, description, and at least one ingredient.");
            return;
        }
        recipeService.buildRecipe(name, description, new ArrayList<>(currentIngredients));
        currentIngredients.clear();
        getRecipes();
    }

    public void removeRecipe(Recipe recipe) {
        recipeService.removeRecipe(recipe);
        getRecipes();
    }

    public void clearIngredients() {
        currentIngredients.clear();
    }

    public void prepareEdit(Recipe recipe) {
        currentIngredients.clear();
        currentIngredients.addAll(recipe.getComponents());
    }

    private boolean isInvalidRecipe(String name, String description) {
        if (name == null || name.isBlank()
                || description == null || description.isBlank()
                || currentIngredients.isEmpty()) {
            view.showError("Recipe must have a name, description, and at least one ingredient.");
            return true;
        }
        return false;
    }

    public void updateRecipe(Recipe originalRecipe, String name, String description) {
        if (isInvalidRecipe(name, description)) return;

        recipeService.updateRecipe(originalRecipe, name, description, currentIngredients);
        currentIngredients.clear();
        getRecipes();
    }

    public void getRecipes() {
        view.showRecipes(recipeService.getAllRecipes());
    }

    // Expose current ingredient list for display in view
    public List<MealComponent> getCurrentIngredients() {
        return new ArrayList<>(currentIngredients);
    }
}
