package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;

import java.util.List;

public class ConsoleRecipeView implements RecipeView {

    @Override
    public void showRecipes(List<Recipe> recipes) {
        if (recipes.isEmpty()) {
            System.out.println("No recipes found.");
            return;
        }

        System.out.println("Recipes:");
        for (Recipe recipe : recipes) {
            System.out.println("- " + recipe.getName());
        }
    }

    @Override
    public void showDetails(Recipe recipe) {
        System.out.println("- " + recipe.getName() + ": " + recipe.getDescription());
        System.out.println("  Ingredients:");
        for (MealComponent component : recipe.getIngredients()) {
            System.out.println("    - " + component.getName());
        }
    }

    @Override
    public void showError(String message) {
        System.err.println("Error: " + message);
    }
}