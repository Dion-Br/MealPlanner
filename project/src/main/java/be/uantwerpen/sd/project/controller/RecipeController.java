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

    private final RecipeService recipeService;
    private final RecipeView view;
    private final List<MealComponent> currentIngredients;

    public RecipeController(RecipeView view, RecipeService recipeService) {
        this.view = view;
        this.recipeService = recipeService;
        this.currentIngredients = new ArrayList<>();
    }

    public void addIngredient(String name, double quantity, Unit unit, List<String> tags) {
        if (!isValidIngredientInput(name, quantity, unit)) {
            view.showError("Invalid input. Name, quantity and unit are required.");
            return;
        }
        List<String> ingredientTags = tags != null ? tags : new ArrayList<>();
        currentIngredients.add(new Ingredient(name, quantity, unit, ingredientTags));
    }

    public void removeIngredient(int index) {
        if (isValidIndex(index)) {
            currentIngredients.remove(index);
        } else {
            view.showError("Invalid ingredient selection.");
        }
    }

    public void addRecipe(String name, String description) {
        if (!isValidRecipeInput(name, description)) {
            view.showError("Recipe must have a name, description, and at least one ingredient.");
            return;
        }
        recipeService.buildRecipe(name, description, new ArrayList<>(currentIngredients));
        currentIngredients.clear();
        refreshView();
    }

    public void removeRecipe(Recipe recipe) {
        recipeService.removeRecipe(recipe);
        refreshView();
    }

    public void clearIngredients() {
        currentIngredients.clear();
    }

    public void prepareEdit(Recipe recipe) {
        currentIngredients.clear();
        currentIngredients.addAll(recipe.getComponents());
    }

    public void updateRecipe(Recipe originalRecipe, String name, String description) {
        if (!isValidRecipeInput(name, description)) {
            view.showError("Recipe must have a name, description, and at least one ingredient.");
            return;
        }
        recipeService.updateRecipe(originalRecipe, name, description, currentIngredients);
        currentIngredients.clear();
        refreshView();
    }

    public void refreshView() {
        view.showRecipes(recipeService.getAllRecipes());
    }

    public List<MealComponent> getCurrentIngredients() {
        return new ArrayList<>(currentIngredients);
    }

    private boolean isValidIngredientInput(String name, double quantity, Unit unit) {
        return name != null && !name.isBlank() && quantity > 0 && unit != null;
    }

    private boolean isValidRecipeInput(String name, String description) {
        return name != null && !name.isBlank()
                && description != null && !description.isBlank()
                && !currentIngredients.isEmpty();
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < currentIngredients.size();
    }
}