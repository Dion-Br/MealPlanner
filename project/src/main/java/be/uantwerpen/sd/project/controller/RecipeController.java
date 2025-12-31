package be.uantwerpen.sd.project.controller;

import be.uantwerpen.sd.project.model.domain.Ingredient;
import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;
import be.uantwerpen.sd.project.model.domain.Tag;
import be.uantwerpen.sd.project.service.RecipeService;
import be.uantwerpen.sd.project.view.RecipeView;
import be.uantwerpen.sd.project.model.domain.enums.Unit;

import java.util.ArrayList;
import java.util.List;

public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeView view;
    private final List<MealComponent> currentComponents;

    public RecipeController(RecipeView view, RecipeService recipeService) {
        this.view = view;
        this.recipeService = recipeService;
        this.currentComponents = new ArrayList<>();
    }

    public void addIngredient(String name, double quantity, Unit unit, List<Tag> tags) {
        if (!isValidIngredientInput(name, quantity, unit)) {
            view.showError("Invalid input. Name, quantity and unit are required.");
            return;
        }
        List<Tag> ingredientTags = tags != null ? tags : new ArrayList<>();
        currentComponents.add(new Ingredient(name, quantity, unit, ingredientTags));
    }

    public void addSubRecipe(Recipe recipe) {
        if (recipe == null) {
            view.showError("Please select a recipe to add.");
            return;
        }
        currentComponents.add(recipe);
    }

    public void removeComponent(int index) {
        if (isValidIndex(index)) {
            currentComponents.remove(index);
        } else {
            view.showError("Invalid component selection.");
        }
    }

    public void addRecipe(String name, String description) {
        if (!isValidRecipeInput(name, description)) {
            view.showError("Recipe must have a name, description, and at least one component.");
            return;
        }
        recipeService.buildRecipe(name, description, new ArrayList<>(currentComponents));
        currentComponents.clear();
        refreshView();
    }

    public void removeRecipe(Recipe recipe) {
        recipeService.removeRecipe(recipe);
        refreshView();
    }

    public void clearComponents() {
        currentComponents.clear();
    }

    public void prepareEdit(Recipe recipe) {
        currentComponents.clear();
        currentComponents.addAll(recipe.getComponents());
    }

    public void updateRecipe(Recipe originalRecipe, String name, String description) {
        if (!isValidRecipeInput(name, description)) {
            view.showError("Recipe must have a name, description, and at least one component.");
            return;
        }
        recipeService.updateRecipe(originalRecipe, name, description, currentComponents);
        currentComponents.clear();
        refreshView();
    }

    public void refreshView() {
        view.showRecipes(recipeService.getAllRecipes());
    }

    public List<MealComponent> getCurrentComponents() {
        return new ArrayList<>(currentComponents);
    }

    public List<Recipe> getAvailableRecipesExcluding(Recipe excludeRecipe) {
        List<Recipe> recipes = new ArrayList<>(recipeService.getAllRecipes());
        if (excludeRecipe != null) {
            recipes.remove(excludeRecipe);
        }
        return recipes;
    }

    private boolean isValidIngredientInput(String name, double quantity, Unit unit) {
        return name != null && !name.isBlank() && quantity > 0 && unit != null;
    }

    private boolean isValidRecipeInput(String name, String description) {
        return name != null && !name.isBlank()
                && description != null && !description.isBlank()
                && !currentComponents.isEmpty();
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < currentComponents.size();
    }
}