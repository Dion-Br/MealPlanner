package be.uantwerpen.sd.project.model.domain;


import org.yaml.snakeyaml.nodes.Tag;

import java.util.ArrayList;
import java.util.List;

public class Recipe extends MealComponent {
    private String description;
    private List<MealComponent> components = new ArrayList<>(); //either sub-recipes or ingredients

    public Recipe(String name, String description) {
        super(name);
        this.description = description;
    }

    @Override
    public void add(MealComponent component) {
        components.add(component);
    }

    @Override
    public void remove(MealComponent component) {
        components.remove(component);
    }

    @Override
    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (MealComponent component : components){
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
        return components;
    }

    public void setComponents(List<MealComponent> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "description='" + getDescription() + '\'' +
                ", components=" + components +
                ", name='" + getName() + '\'' +
                ", name='" + getName() + '\'' +
                "} " + super.toString();
    }
}
