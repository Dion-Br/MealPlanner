package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.model.domain.Recipe;

import java.util.List;

public interface RecipeView {
    void showRecipes(List<Recipe> recipes);
    void showDetails(Recipe recipe);
    void showError(String message);
}
