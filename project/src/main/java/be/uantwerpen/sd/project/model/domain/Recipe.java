package be.uantwerpen.sd.project.model.domain;


import org.yaml.snakeyaml.nodes.Tag;

import java.util.List;

public class Recipe {
    private String title;
    private String description;
    private List<Ingredient> ingredients;

    public Recipe(String title, String description, List<Ingredient> ingredients) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
