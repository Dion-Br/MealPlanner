package be.uantwerpen.sd.project.repository;

import be.uantwerpen.sd.project.model.domain.Recipe;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeRepository {

    private static final String RECIPES_CHANGED = "recipesChanged";

    private static volatile RecipeRepository instance;

    private final List<Recipe> recipes;
    private final PropertyChangeSupport propertyChangeSupport;

    private RecipeRepository() {
        this.recipes = new ArrayList<>();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

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
        fireRecipesChanged();
    }

    public void removeRecipe(Recipe recipe) {
        recipes.remove(recipe);
        fireRecipesChanged();
    }

    public void notifyUpdate() {
        fireRecipesChanged();
    }

    public List<Recipe> findAll() {
        return Collections.unmodifiableList(recipes);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void fireRecipesChanged() {
        propertyChangeSupport.firePropertyChange(RECIPES_CHANGED, null, recipes);
    }

    protected static void resetInstance() {
        instance = null;
    }
}