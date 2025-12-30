package be.uantwerpen.sd.project.model;

import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeBuilder {

    private String name;
    private String description;
    private final List<MealComponent> components;

    public RecipeBuilder() {
        this.components = new ArrayList<>();
    }

    public RecipeBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public RecipeBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public RecipeBuilder addComponents(List<MealComponent> componentList) {
        this.components.addAll(componentList);
        return this;
    }

    public Recipe build() {
        Recipe recipe = new Recipe(name, description);
        for (MealComponent component : components) {
            recipe.add(component);
        }
        return recipe;
    }
}