package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;

import java.util.List;
//more for debugging purposes
public class ConsoleRecipeView implements RecipeView{
    @Override
    public void showRecipes(List<Recipe> recipes) {
        if (recipes.isEmpty()) {
            System.out.println("No recipes found.");
        } else {
            System.out.println("Recipes:");
            for (Recipe r : recipes) {
                System.out.println("- " + r.getName());
            }
        }
    }

    @Override
    public void showDetails(Recipe recipe) {
        System.out.println("- " + recipe.getName() + ": " + recipe.getDescription());
        System.out.println("  Ingredients:");
        for (MealComponent c : recipe.getIngredients()) {
            System.out.println("    • " + c.getName());
        }
    }


    @Override
    public void showError(String message) {
        System.err.println(message);
    }
}
