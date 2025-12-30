package be.uantwerpen.sd.project.model.domain;
import java.util.ArrayList;
import java.util.List;

public class Recipe extends MealComponent {
    private String description;
    private List<MealComponent> components = new ArrayList<>(); //either sub-recipes or ingredients

    public Recipe(String name, String description) {
        super(name);
        this.description = description;
    }

    public List<String> calculateTags() {
        List<Ingredient> allIngredients = getIngredients();
        if (allIngredients.isEmpty()) {
            return new ArrayList<>();
        }

        // Start with the tags of the first ingredient
        List<String> commonTags = new ArrayList<>(allIngredients.get(0).getTags());

        // Check for matches is all other ingredients
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
                "name='" + getName() + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + calculateTags() + '\'' +
                ", components=" + components +
                "}";
    }
}
