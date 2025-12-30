package be.uantwerpen.sd.project.model.domain;

import java.util.ArrayList;
import java.util.List;

public class Recipe extends MealComponent {

    private String description;
    private final List<MealComponent> components;

    public Recipe(String name, String description) {
        super(name);
        this.description = description;
        this.components = new ArrayList<>();
    }

    public List<String> calculateTags() {
        List<Ingredient> allIngredients = getIngredients();
        if (allIngredients.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> commonTags = new ArrayList<>(allIngredients.get(0).getTags());
        for (Ingredient ingredient : allIngredients) {
            commonTags.retainAll(ingredient.getTags());
        }
        return commonTags;
    }

    @Override
    public void add(MealComponent component) {
        components.add(component);
    }

    @Override
    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (MealComponent component : components) {
            ingredients.addAll(component.getIngredients());
        }
        return ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MealComponent> getComponents() {
        return new ArrayList<>(components);
    }

    public void setComponents(List<MealComponent> components) {
        this.components.clear();
        this.components.addAll(components);
    }

    @Override
    public String toString() {
        return String.format("Recipe{name='%s', description='%s', tags=%s, components=%s}",
                getName(), description, calculateTags(), components);
    }
}