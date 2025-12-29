package be.uantwerpen.sd.project.repository;

import be.uantwerpen.sd.project.model.domain.Recipe;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeRepository {
    private static volatile RecipeRepository instance;
    private final List<Recipe> recipes = new ArrayList<>();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

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
        pcs.firePropertyChange("recipesChanged", null, recipes);
    }

    public void removeRecipe(Recipe recipe) {
        recipes.remove(recipe);
        pcs.firePropertyChange("recipesChanged", null, recipes);
    }

    public void notifyUpdate() {
        pcs.firePropertyChange("recipesChanged", null, recipes);
    }

    public List<Recipe> findAll() {
        return Collections.unmodifiableList(recipes);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
