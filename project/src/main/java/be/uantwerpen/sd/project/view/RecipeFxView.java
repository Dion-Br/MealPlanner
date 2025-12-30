package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.controller.RecipeController;
import be.uantwerpen.sd.project.model.domain.Ingredient;
import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;
import be.uantwerpen.sd.project.model.domain.enums.Unit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class RecipeFxView extends VBox implements RecipeView {

    private final ListView<String> recipeListView;
    private final TextArea recipeDetailsArea;
    private final TextField nameField;
    private final TextArea descriptionField;
    private final TextField ingredientNameField;
    private final TextField ingredientQuantityField;
    private final TextField tagsField;
    private final ListView<String> ingredientListView;
    private final ObservableList<String> ingredientItems;
    private final ComboBox<Unit> unitComboBox;
    private final Button saveRecipeButton;
    private final Button cancelEditButton;

    private List<Recipe> currentRecipes;
    private Recipe recipeToEdit;
    private RecipeController controller;

    public RecipeFxView() {
        this.recipeListView = new ListView<>();
        this.recipeDetailsArea = new TextArea();
        this.nameField = new TextField();
        this.descriptionField = new TextArea();
        this.ingredientNameField = new TextField();
        this.ingredientQuantityField = new TextField();
        this.tagsField = new TextField();
        this.ingredientListView = new ListView<>();
        this.ingredientItems = FXCollections.observableArrayList();
        this.unitComboBox = new ComboBox<>(FXCollections.observableArrayList(Unit.values()));
        this.saveRecipeButton = new Button("Save Recipe");
        this.cancelEditButton = new Button("Cancel");

        initializeLayout();
    }

    private void initializeLayout() {
        setSpacing(10);
        setPadding(new Insets(10));

        VBox formBox = createFormSection();
        VBox recipeListSection = createRecipeListSection();

        getChildren().addAll(formBox, recipeListSection);
    }

    private VBox createFormSection() {
        Label formLabel = new Label("Create New Recipe");

        configureInputFields();
        HBox ingredientBox = createIngredientInputBox();
        HBox actionBox = createActionButtons();

        VBox formBox = new VBox(5, formLabel, nameField, descriptionField, ingredientBox, ingredientListView, actionBox);
        formBox.setStyle("-fx-border-color: lightgray; -fx-padding: 10; -fx-border-radius: 5;");
        return formBox;
    }

    private void configureInputFields() {
        nameField.setPromptText("Recipe Name");
        descriptionField.setPromptText("Recipe Description");
        descriptionField.setPrefRowCount(3);
        ingredientNameField.setPromptText("Ingredient Name");
        ingredientQuantityField.setPromptText("Quantity");
        tagsField.setPromptText("Tags (comma separated)");

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d*(\\.\\d*)?")) {
                return change;
            }
            return null;
        };
        ingredientQuantityField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), null, filter));

        unitComboBox.setPromptText("Unit");
        unitComboBox.getSelectionModel().selectFirst();
        ingredientListView.setItems(ingredientItems);
        ingredientListView.setPrefHeight(100);
    }

    private HBox createIngredientInputBox() {
        Button addIngredientButton = new Button("Add Ingredient");
        addIngredientButton.disableProperty().bind(
                ingredientNameField.textProperty().isEmpty()
                        .or(ingredientQuantityField.textProperty().isEmpty())
                        .or(unitComboBox.valueProperty().isNull())
        );
        addIngredientButton.setOnAction(e -> handleAddIngredient());

        Button removeIngredientButton = new Button("Remove Ingredient");
        removeIngredientButton.setOnAction(e -> handleRemoveIngredient());

        return new HBox(5, ingredientNameField, ingredientQuantityField, unitComboBox, tagsField,
                addIngredientButton, removeIngredientButton);
    }

    private HBox createActionButtons() {
        cancelEditButton.setVisible(false);
        saveRecipeButton.setOnAction(e -> handleSaveRecipe());
        cancelEditButton.setOnAction(e -> resetForm());
        return new HBox(10, saveRecipeButton, cancelEditButton);
    }

    private VBox createRecipeListSection() {
        Label recipeLabel = new Label("Available Recipes");
        recipeListView.setPrefHeight(200);
        recipeDetailsArea.setEditable(false);
        recipeDetailsArea.setPrefHeight(100);

        Button editButton = new Button("Edit Selected");
        Button removeButton = new Button("Remove Selected");
        editButton.setOnAction(e -> handleEditRecipe());
        removeButton.setOnAction(e -> handleRemoveRecipe());

        HBox listBox = new HBox(10, editButton, removeButton);

        recipeListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (currentRecipes != null && newVal.intValue() >= 0) {
                showDetails(currentRecipes.get(newVal.intValue()));
            }
        });

        return new VBox(5, recipeLabel, recipeListView, listBox, recipeDetailsArea);
    }

    private void handleAddIngredient() {
        String name = ingredientNameField.getText();
        String quantityText = ingredientQuantityField.getText();
        Unit unit = unitComboBox.getValue();

        if (name == null || name.isBlank() || quantityText == null || quantityText.isBlank() || unit == null) {
            showError("Ingredient name, quantity and unit cannot be empty.");
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityText);
            List<String> tags = parseTags(tagsField.getText());

            controller.addIngredient(name, quantity, unit, tags);
            ingredientItems.add(formatIngredient(name, quantity, unit, tags));
            clearIngredientFields();
        } catch (NumberFormatException ex) {
            showError("Quantity must be a number.");
        }
    }

    private void handleRemoveIngredient() {
        int index = ingredientListView.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            controller.removeIngredient(index);
            ingredientItems.remove(index);
            recipeDetailsArea.clear();
        }
    }

    private void handleSaveRecipe() {
        String name = nameField.getText();
        String description = descriptionField.getText();

        if (recipeToEdit == null) {
            controller.addRecipe(name, description);
        } else {
            controller.updateRecipe(recipeToEdit, name, description);
        }
        resetForm();
    }

    private void handleEditRecipe() {
        Recipe selected = getSelectedRecipe();
        if (selected != null) {
            enableEditMode(selected);
        }
    }

    private void handleRemoveRecipe() {
        Recipe selected = getSelectedRecipe();
        if (selected != null) {
            controller.removeRecipe(selected);
            recipeDetailsArea.clear();
            resetForm();
        }
    }

    private void enableEditMode(Recipe recipe) {
        this.recipeToEdit = recipe;
        nameField.setText(recipe.getName());
        descriptionField.setText(recipe.getDescription());
        controller.prepareEdit(recipe);

        ingredientItems.clear();
        for (MealComponent component : controller.getCurrentIngredients()) {
            if (component instanceof Ingredient ingredient) {
                ingredientItems.add(formatIngredient(ingredient.getName(), ingredient.getQuantity(),
                        ingredient.getUnit(), ingredient.getTags()));
            } else {
                ingredientItems.add(component.getName());
            }
        }
        saveRecipeButton.setText("Update Recipe");
        cancelEditButton.setVisible(true);
    }

    private void resetForm() {
        recipeToEdit = null;
        nameField.clear();
        descriptionField.clear();
        clearIngredientFields();
        ingredientItems.clear();
        controller.clearIngredients();
        saveRecipeButton.setText("Save Recipe");
        cancelEditButton.setVisible(false);
    }

    private void clearIngredientFields() {
        ingredientNameField.clear();
        ingredientQuantityField.clear();
        tagsField.clear();
        unitComboBox.getSelectionModel().selectFirst();
    }

    private Recipe getSelectedRecipe() {
        int index = recipeListView.getSelectionModel().getSelectedIndex();
        if (index >= 0 && currentRecipes != null) {
            return currentRecipes.get(index);
        }
        return null;
    }

    private List<String> parseTags(String tagString) {
        return Arrays.stream(tagString.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    private String formatIngredient(String name, double quantity, Unit unit, List<String> tags) {
        String tagStr = tags.isEmpty() ? "" : " [" + String.join(", ", tags) + "]";
        return String.format("%s (%.2f %s)%s", name, quantity, unit, tagStr);
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
        String tags = String.join(", ", recipe.calculateTags());
        String ingredients = recipe.getIngredients().stream()
                .map(i -> formatIngredient(i.getName(), i.getQuantity(), i.getUnit(), i.getTags()))
                .collect(Collectors.joining("\n"));

        String details = String.format("%s: %s\nTags: %s\nIngredients:\n%s",
                recipe.getName(),
                recipe.getDescription(),
                tags.isEmpty() ? "None" : tags,
                ingredients);

        recipeDetailsArea.setText(details);
    }

    @Override
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setController(RecipeController controller) {
        this.controller = controller;
    }
}