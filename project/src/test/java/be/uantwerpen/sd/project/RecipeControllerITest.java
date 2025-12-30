package be.uantwerpen.sd.project;

import be.uantwerpen.sd.project.controller.RecipeController;
import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;
import be.uantwerpen.sd.project.model.domain.enums.Unit;
import be.uantwerpen.sd.project.repository.RecipeRepository;
import be.uantwerpen.sd.project.service.RecipeService;
import be.uantwerpen.sd.project.view.RecipeView;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

public class RecipeControllerITest {

    private RecipeController controller;
    private RecipeService recipeService;
    private RecipeView view;

    @BeforeEach
    void setUp() throws Exception {
        // Mock the view (no new class created)
        view = mock(RecipeView.class);

        // Reset singleton repository between tests
        resetRepository();

        // Real repository + service (integration aspect)
        RecipeRepository repository = RecipeRepository.getInstance();
        recipeService = new RecipeService(repository);

        controller = new RecipeController(view, recipeService);
    }

    //Utility to clear the singleton repository using reflection
    private void resetRepository() throws Exception {
        RecipeRepository repo = RecipeRepository.getInstance();

        Field recipesField = RecipeRepository.class.getDeclaredField("recipes");
        recipesField.setAccessible(true);
        ((List<?>) recipesField.get(repo)).clear();
    }

    @Test
    void shouldCreateRecipeAndShowItInView() {
        // Arrange
        controller.addIngredient("Tomato", 2, Unit.PIECE, List.of("Fresh"));
        controller.addIngredient("Salt", 1, Unit.GRAM, List.of("Spice"));

        // Act
        controller.addRecipe("Tomato Salad", "Fresh and simple");

        // Assert: repository state
        List<Recipe> recipes = RecipeRepository.getInstance().findAll();
        assertEquals(1, recipes.size());

        Recipe recipe = recipes.get(0);
        assertEquals("Tomato Salad", recipe.getName());
        assertEquals("Fresh and simple", recipe.getDescription());
        assertEquals(2, recipe.getIngredients().size());

        // Assert: view interaction
        verify(view, atLeastOnce()).showRecipes(anyList());
        verify(view, never()).showError(anyString());
    }

    @Test
    void shouldShowErrorWhenAddingRecipeWithoutIngredients() {
        // Act
        controller.addRecipe("Empty Recipe", "No components");

        // Assert
        verify(view).showError(
                "Recipe must have a name, description, and at least one component."
        );
        verify(view, never()).showRecipes(any());
    }

    @Test
    void shouldRemoveRecipeAndUpdateView() {
        // Arrange
        controller.addIngredient("Flour", 500, Unit.GRAM, List.of("Baking"));
        controller.addRecipe("Bread", "Simple bread");

        Recipe recipe = RecipeRepository.getInstance().findAll().get(0);

        // Act
        controller.removeRecipe(recipe);

        // Assert
        assertTrue(RecipeRepository.getInstance().findAll().isEmpty());
        verify(view, atLeastOnce()).showRecipes(anyList());
    }

    @Test
    void shouldShowErrorOnInvalidIngredientInput() {
        // Act
        controller.addIngredient("", -1, null, null);
        // Assert
        verify(view).showError("Invalid input. Name, quantity and unit are required.");
    }

    @Test
    void shouldRemoveIngredientByIndex() {
        // Arrange
        controller.addIngredient("Sugar", 100, Unit.GRAM, List.of("Sweet"));
        controller.addIngredient("Butter", 50, Unit.GRAM, List.of("Dairy"));

        // Act
        controller.removeComponent(0);

        // Assert
        List<MealComponent> ingredients = controller.getCurrentComponents();
        assertEquals(1, ingredients.size());
        assertEquals("Butter", ingredients.get(0).getName());
    }

    @Test
    void shouldShowErrorOnInvalidComponentIndex() {
        // Arrange
        controller.addIngredient("Sugar", 100, Unit.GRAM, List.of("Sweet"));

        // Act
        controller.removeComponent(5);

        // Assert
        verify(view).showError("Invalid component selection.");
    }
}
