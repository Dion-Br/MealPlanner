package be.uantwerpen.sd.project;

import be.uantwerpen.sd.project.model.domain.Ingredient;
import be.uantwerpen.sd.project.model.domain.Recipe;
import be.uantwerpen.sd.project.model.domain.enums.Unit;
import be.uantwerpen.sd.project.repository.RecipeRepository;
import be.uantwerpen.sd.project.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeServiceUTest {
    private RecipeService service;


    @BeforeEach
    void setUp() throws Exception {
        service = new RecipeService();
        resetRepository();
    }
    private void resetRepository() throws Exception {
        RecipeRepository repo = RecipeRepository.getInstance();

        Field recipesField = RecipeRepository.class.getDeclaredField("recipes");
        recipesField.setAccessible(true);
        ((List<?>) recipesField.get(repo)).clear();
    }

    //buildrecipe method
    @Test
    void buildRecipe_shouldCreateAndStoreRecipe() {
        // Arrange
        Ingredient tomato = new Ingredient("Tomato", 2, Unit.PIECE);
        Ingredient salt = new Ingredient("Salt", 1, Unit.GRAM);

        // Act
        Recipe recipe = service.buildRecipe(
                "Tomato Salad",
                "Fresh and simple",
                List.of(tomato, salt)
        );

        // Assert
        assertNotNull(recipe);
        assertEquals("Tomato Salad", recipe.getName());
        assertEquals("Fresh and simple", recipe.getDescription());
        assertEquals(2, recipe.getIngredients().size());

        List<Recipe> allRecipes = service.getAllRecipes();
        assertEquals(1, allRecipes.size());
        assertSame(recipe, allRecipes.get(0));
    }

    @Test
    void buildRecipe_shouldAllowEmptyComponentList() {
        // Act
        Recipe recipe = service.buildRecipe(
                "Empty Recipe",
                "No ingredients yet",
                List.of()
        );

        // Assert
        assertNotNull(recipe);
        assertTrue(recipe.getIngredients().isEmpty());
        assertEquals(1, service.getAllRecipes().size());
    }

    @Test
    void buildRecipe_shouldSupportNestedComponents() {
        // Arrange
        Ingredient flour = new Ingredient("Flour", 500, Unit.GRAM);
        Ingredient water = new Ingredient("Water", 300, Unit.MILLILITER);

        Recipe dough = new Recipe("Dough", "Basic dough");
        dough.add(flour);
        dough.add(water);

        Ingredient yeast = new Ingredient("Yeast", 5, Unit.GRAM);

        // Act
        Recipe bread = service.buildRecipe(
                "Bread",
                "Simple bread",
                List.of(dough, yeast)
        );

        // Assert
        List<Ingredient> ingredients = bread.getIngredients();
        assertEquals(3, ingredients.size());

        assertTrue(ingredients.stream().anyMatch(i -> i.getName().equals("Flour")));
        assertTrue(ingredients.stream().anyMatch(i -> i.getName().equals("Water")));
        assertTrue(ingredients.stream().anyMatch(i -> i.getName().equals("Yeast")));
    }

    //removeRecipe method
    @Test
    void removeRecipe_shouldRemoveExistingRecipe() {
        // Arrange
        Recipe recipe = service.buildRecipe(
                "Soup",
                "Hot soup",
                List.of(new Ingredient("Water", 1, Unit.LITER))
        );

        assertEquals(1, service.getAllRecipes().size());

        // Act
        service.removeRecipe(recipe);

        // Assert
        assertTrue(service.getAllRecipes().isEmpty());
    }

    @Test
    void removeRecipe_shouldDoNothingWhenRecipeDoesNotExist() {
        // Arrange
        Recipe recipe1 = service.buildRecipe(
                "Tea",
                "Hot tea",
                List.of(new Ingredient("Water", 250, Unit.MILLILITER))
        );

        Recipe notStored = new Recipe("Coffee", "Black coffee");

        // Act
        service.removeRecipe(notStored);

        // Assert
        List<Recipe> all = service.getAllRecipes();
        assertEquals(1, all.size());
        assertSame(recipe1, all.get(0));
    }

    //getallrecipes
    @Test
    void getAllRecipes_shouldReturnUnmodifiableList() {
        // Arrange
        service.buildRecipe(
                "Pasta",
                "Simple pasta",
                List.of(new Ingredient("Pasta", 200, Unit.GRAM))
        );

        // Act
        List<Recipe> recipes = service.getAllRecipes();

        // Assert
        assertThrows(UnsupportedOperationException.class, () ->
                recipes.add(new Recipe("Hack", "Should fail"))
        );
    }


    @Test
    void getAllRecipes_shouldReturnEmptyListInitially() {
        // Act
        List<Recipe> recipes = service.getAllRecipes();

        // Assert
        assertNotNull(recipes);
        assertTrue(recipes.isEmpty());
    }
}
