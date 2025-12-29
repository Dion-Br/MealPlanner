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
import javafx.util.converter.DoubleStringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class RecipeFxView extends VBox implements RecipeView {
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
    // private final List<MealComponent> ingredients = new ArrayList<>(); // Unused, safe to remove
    private final ComboBox<Unit> unitComboBox = new ComboBox<>(FXCollections.observableArrayList(Unit.values()));

    // Editing state
    private Recipe recipeToEdit = null;
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

        // Allow digits only in quantity field
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) return change;
            if (newText.matches("\\d*(\\.\\d*)?")) return change;
            return null;
        };

        ingredientQuantityField.setTextFormatter(
                new TextFormatter<>(new DoubleStringConverter(), null, filter)
        );
        unitComboBox.setPromptText("Unit");
        unitComboBox.getSelectionModel().selectFirst();

        Button addIngredientBtn = new Button("Add Ingredient");
        addIngredientBtn.disableProperty().bind(
                ingredientNameField.textProperty().isEmpty()
                        .or(ingredientQuantityField.textProperty().isEmpty())
                        .or(unitComboBox.valueProperty().isNull())
        );
        addIngredientBtn.setOnAction(e -> {
            String inName = ingredientNameField.getText();
            String inQty = ingredientQuantityField.getText();
            Unit selectedUnit = unitComboBox.getValue();
            if (inName != null && !inName.isBlank() && inQty != null && !inQty.isBlank() && selectedUnit != null) {
                try {
                    double qty = Double.parseDouble(inQty);
                    controller.addIngredient(inName, qty, selectedUnit);
                    ingredientItems.add(formatIngredientString(inName, qty, selectedUnit));
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

        // [FIX 1] Removed the duplicate removeRecipeBtn definition here.
        // We only keep the "Remove Ingredient" button for the form.

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
        Button cancelEditBtn = new Button("Cancel");
        cancelEditBtn.setVisible(false);

        saveRecipeBtn.setOnAction(e -> {
            String name = nameField.getText();
            String description = descriptionField.getText();

            if (recipeToEdit == null) {
                controller.addRecipe(name, description);
            } else {
                controller.updateRecipe(recipeToEdit, name, description);
            }
            resetForm(saveRecipeBtn, cancelEditBtn);
        });

        cancelEditBtn.setOnAction(e -> resetForm(saveRecipeBtn, cancelEditBtn));

        // [FIX 3] Ensure the actionBox (Save + Cancel) is created
        HBox actionBox = new HBox(10, saveRecipeBtn, cancelEditBtn);

        // [FIX 3 & 1] Add 'actionBox' to formBox instead of adding 'saveRecipeBtn' alone.
        // Also removed 'removeRecipeBtn' from here.
        VBox formBox = new VBox(5, formLabel, nameField, descriptionField, ingredientBox, ingredientListView, actionBox);
        formBox.setStyle("-fx-border-color: lightgray; -fx-padding: 10; -fx-border-radius: 5;");

        // --- Recipe display ---
        Label recipeLabel = new Label("Available Recipes");
        recipeListView.setPrefHeight(200);

        recipeDetailsArea.setEditable(false);
        recipeDetailsArea.setPrefHeight(100);

        Button editRecipeBtn = new Button("Edit Selected");

        // [FIX 1] This is the correct place for removeRecipeBtn (next to the list)
        Button removeRecipeBtn = new Button("Remove Selected");

        editRecipeBtn.setOnAction(e -> {
            Recipe selected = getSelectedRecipe();
            if (selected != null) {
                enableEditMode(selected, saveRecipeBtn, cancelEditBtn);
            }
        });

        removeRecipeBtn.setOnAction(e -> {
            Recipe selected = getSelectedRecipe();
            if (selected != null) {
                controller.removeRecipe(selected);
                recipeDetailsArea.clear();
                resetForm(saveRecipeBtn, cancelEditBtn);
            }
        });

        HBox listBox = new HBox(10, editRecipeBtn, removeRecipeBtn);

        // [FIX 4] Changed 'listLabel' (undefined) to 'recipeLabel'
        getChildren().addAll(formBox, recipeLabel, recipeListView, listBox, recipeDetailsArea);

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
    }

    @Override
    public void showDetails(Recipe recipe) {
        String ingredients = String.join(", ",
                recipe.getIngredients().stream()
                        .map(i -> i.getName() + " (" + i.getQuantity() + " " + i.getUnit() + ")")
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

    // --- Helper Methods ---
    private Recipe getSelectedRecipe() {
        int index = recipeListView.getSelectionModel().getSelectedIndex();
        if (index >= 0 && currentRecipes != null) {
            return currentRecipes.get(index);
        }
        return null;
    }

    private void enableEditMode(Recipe recipe, Button saveBtn, Button cancelBtn) {
        this.recipeToEdit = recipe;

        nameField.setText(recipe.getName());
        descriptionField.setText(recipe.getDescription());

        controller.prepareEdit(recipe);

        ingredientItems.clear();
        for (MealComponent mc : controller.getCurrentIngredients()) {
            if (mc instanceof Ingredient i) {
                // [FIX 5] This method was missing
                ingredientItems.add(formatIngredientString(i.getName(), i.getQuantity(), i.getUnit()));
            } else {
                ingredientItems.add(mc.getName());
            }
        }

        saveBtn.setText("Update Recipe");
        cancelBtn.setVisible(true);
    }

    private void resetForm(Button saveBtn, Button cancelBtn) {
        this.recipeToEdit = null;
        nameField.clear();
        descriptionField.clear();
        ingredientNameField.clear();
        ingredientQuantityField.clear();
        ingredientItems.clear();
        controller.clearIngredients();

        saveBtn.setText("Save Recipe");
        cancelBtn.setVisible(false);
    }

    // [FIX 5] Added missing helper method
    private String formatIngredientString(String name, double qty, Unit unit) {
        return name + " (" + qty + " " + unit + ")";
    }
}