package be.uantwerpen.sd.project.repository;

import be.uantwerpen.sd.project.model.domain.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeRepository {
    private static volatile RecipeRepository instance;
    private final List<Recipe> recipes = new ArrayList<>();

    private RecipeRepository() {}

    public static RecipeRepository getInstance() {
        if (instance == null) {
            synchronized (RecipeRepository.class) {
                if (instance == null) {
                    instance = new RecipeRepository();
                }
            }
        }
        return instance;
    }

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public void removeRecipe(Recipe recipe) {
        recipes.remove(recipe);
    }

    public List<Recipe> findAll() {
        return Collections.unmodifiableList(recipes);
    }
}
