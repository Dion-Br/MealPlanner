package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.controller.RecipeController;
import be.uantwerpen.sd.project.model.domain.Ingredient;
import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;
import be.uantwerpen.sd.project.model.domain.enums.Unit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class RecipeFxView extends VBox implements RecipeView{
    private final ListView<String> recipeListView = new ListView<>();
    private List<Recipe> currentRecipes;
    private final TextArea recipeDetailsArea = new TextArea();

    // Input fields
    private final TextField nameField = new TextField();
    private final TextArea descriptionField = new TextArea();
    private final TextField ingredientNameField = new TextField();
    private final TextField ingredientQuantityField = new TextField();
    private final ListView<String> ingredientListView = new ListView<>();
    private final ObservableList<String> ingredientItems = FXCollections.observableArrayList();
    private final List<MealComponent> ingredients = new ArrayList<>();
    private final ComboBox<Unit> unitComboBox = new ComboBox<>(FXCollections.observableArrayList(Unit.values()));

    private RecipeController controller;

    public RecipeFxView() {
        setSpacing(10);
        setPadding(new Insets(10));

        Label formLabel = new Label("Create New Recipe");

        nameField.setPromptText("Recipe Name");
        descriptionField.setPromptText("Recipe Description");
        descriptionField.setPrefRowCount(3);

        ingredientNameField.setPromptText("Ingredient Name");
        ingredientQuantityField.setPromptText("Quantity");
        unitComboBox.setPromptText("Unit");
        // Default to the first unit
        unitComboBox.getSelectionModel().selectFirst();

        Button addIngredientBtn = new Button("Add Ingredient");
        addIngredientBtn.setOnAction(e -> {
            String inName = ingredientNameField.getText();
            String inQty = ingredientQuantityField.getText();
            Unit selectedUnit = unitComboBox.getValue();
            if (inName != null && !inName.isBlank() && inQty != null && !inQty.isBlank() && selectedUnit != null) {
                try {
                    double qty = Double.parseDouble(inQty);
                    controller.addIngredient(inName, qty, selectedUnit);
                    ingredientItems.add(inName + " (" + qty + " " + selectedUnit + ")");
                    ingredientNameField.clear();
                    ingredientQuantityField.clear();
                    unitComboBox.getSelectionModel().selectFirst();
                } catch (NumberFormatException ex) {
                    showError("Quantity must be a number.");
                }
            } else {
                showError("Ingredient name, quantity and unit cannot be empty.");
            }
        });

        Button removeRecipeBtn = new Button("Remove Recipe");
        removeRecipeBtn.setOnAction(e -> {
            int index = recipeListView.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                Recipe selectedRecipe = currentRecipes.get(index);
                controller.removeRecipe(selectedRecipe);
                recipeDetailsArea.clear();
            }
        });

        Button removeIngredientBtn = new Button("Remove Ingredient");
        removeIngredientBtn.setOnAction(e -> {
            int index = ingredientListView.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                controller.removeIngredient(index);
                ingredientItems.remove(index);
                recipeDetailsArea.clear();
            }
        });

        ingredientListView.setItems(ingredientItems);
        ingredientListView.setPrefHeight(100);

        HBox ingredientBox = new HBox(5, ingredientNameField, ingredientQuantityField, unitComboBox, addIngredientBtn, removeIngredientBtn);

        Button saveRecipeBtn = new Button("Save Recipe");
        saveRecipeBtn.setOnAction(e -> {
            if (controller != null) {
                controller.addRecipe(nameField.getText(), descriptionField.getText());
                nameField.clear();
                descriptionField.clear();
                ingredients.clear();
                ingredientItems.clear();
            }
        });

        VBox formBox = new VBox(5, formLabel, nameField, descriptionField, ingredientBox, ingredientListView, saveRecipeBtn, removeRecipeBtn);

        // --- Recipe display ---
        Label recipeLabel = new Label("Recipes");
        recipeListView.setPrefHeight(200);

        recipeDetailsArea.setEditable(false);
        recipeDetailsArea.setPrefHeight(150);

        getChildren().addAll(formBox, recipeLabel, recipeListView, recipeDetailsArea);

        // Selection listener
        recipeListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (currentRecipes != null && newVal.intValue() >= 0) {
                showDetails(currentRecipes.get(newVal.intValue()));
            }
        });
    }

    public void setController(RecipeController controller) {
        this.controller = controller;
    }

    @Override
    public void showRecipes(List<Recipe> recipes) {
        this.currentRecipes = recipes;
        ObservableList<String> items = FXCollections.observableArrayList(
                recipes.stream().map(Recipe::getName).toList()
        );
        recipeListView.setItems(items);
        if (!recipes.isEmpty()) {
            recipeListView.getSelectionModel().selectFirst();
        }
    }

    @Override
    public void showDetails(Recipe recipe) {
        String ingredients = String.join(", ",
                recipe.getIngredients().stream()
                        .map(i -> i.getName() + " (" + i.getQuantity() + ")")
                        .toList()
        );
        recipeDetailsArea.setText(recipe.getName() + ": " + recipe.getDescription()
                + "\nIngredients: " + ingredients);
    }

    @Override
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
