package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.controller.RecipeController;
import be.uantwerpen.sd.project.model.domain.Ingredient;
import be.uantwerpen.sd.project.model.domain.MealComponent;
import be.uantwerpen.sd.project.model.domain.Recipe;
import be.uantwerpen.sd.project.model.domain.Tag;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
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
    private final ListView<String> componentListView;
    private final ObservableList<String> componentItems;
    private final ComboBox<Unit> unitComboBox;
    private final ComboBox<Recipe> subRecipeComboBox;
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
        this.componentListView = new ListView<>();
        this.componentItems = FXCollections.observableArrayList();
        this.unitComboBox = new ComboBox<>(FXCollections.observableArrayList(Unit.values()));
        this.subRecipeComboBox = new ComboBox<>();
        this.saveRecipeButton = new Button("Save Recipe");
        this.cancelEditButton = new Button("Cancel");

        initializeLayout();
    }

    private void initializeLayout() {
        setSpacing(10);
        setPadding(new Insets(10));

        VBox formBox = createFormSection();
        VBox recipeListSection = createRecipeListSection();

        VBox.setVgrow(formBox, Priority.ALWAYS);
        VBox.setVgrow(recipeListSection, Priority.ALWAYS);

        getChildren().addAll(formBox, recipeListSection);
    }

    private VBox createFormSection() {
        Label formLabel = new Label("Create New Recipe");
        formLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        configureInputFields();

        HBox ingredientPane = createIngredientPane();
        HBox subRecipePane = createSubRecipePane();

        Label componentsLabel = new Label("Components (Ingredients & Sub-Recipes):");
        componentsLabel.setStyle("-fx-font-weight: bold;");

        componentListView.setItems(componentItems);
        componentListView.setPrefHeight(150);
        componentListView.setMinHeight(120);
        VBox.setVgrow(componentListView, Priority.ALWAYS);

        Button removeComponentButton = new Button("Remove Selected Component");
        removeComponentButton.setOnAction(e -> handleRemoveComponent());

        HBox actionBox = createActionButtons();

        VBox formBox = new VBox(8, formLabel, nameField, descriptionField,
                ingredientPane, subRecipePane,
                componentsLabel, componentListView, removeComponentButton, actionBox);
        formBox.setStyle("-fx-border-color: lightgray; -fx-padding: 10; -fx-border-radius: 5;");
        return formBox;
    }

    private void configureInputFields() {
        nameField.setPromptText("Recipe Name");
        descriptionField.setPromptText("Recipe Description");
        descriptionField.setPrefRowCount(2);
        descriptionField.setMaxHeight(60);
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

        configureSubRecipeComboBox();
    }

    private void configureSubRecipeComboBox() {
        subRecipeComboBox.setPromptText("Select existing recipe");
        subRecipeComboBox.setPrefWidth(250);
        subRecipeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Recipe recipe) {
                return recipe == null ? null : recipe.getName();
            }

            @Override
            public Recipe fromString(String s) {
                return null;
            }
        });
    }

    private HBox createIngredientPane() {
        Label label = new Label("Add Ingredient: ");
        label.setMinWidth(100);

        Button addIngredientButton = new Button("Add");
        addIngredientButton.disableProperty().bind(
                ingredientNameField.textProperty().isEmpty()
                        .or(ingredientQuantityField.textProperty().isEmpty())
                        .or(unitComboBox.valueProperty().isNull())
        );
        addIngredientButton.setOnAction(e -> handleAddIngredient());

        ingredientNameField.setPrefWidth(120);
        ingredientQuantityField.setPrefWidth(70);
        tagsField.setPrefWidth(150);

        HBox box = new HBox(5, label, ingredientNameField, ingredientQuantityField,
                unitComboBox, tagsField, addIngredientButton);
        box.setStyle("-fx-padding: 5; -fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 3;");
        return box;
    }

    private HBox createSubRecipePane() {
        Label label = new Label("Add Sub-Recipe: ");
        label.setMinWidth(100);

        Button addSubRecipeButton = new Button("Add");
        addSubRecipeButton.disableProperty().bind(subRecipeComboBox.valueProperty().isNull());
        addSubRecipeButton.setOnAction(e -> handleAddSubRecipe());

        Button refreshButton = new Button("↻");
        refreshButton.setOnAction(e -> refreshSubRecipeComboBox());

        HBox box = new HBox(5, label, subRecipeComboBox, addSubRecipeButton, refreshButton);
        box.setStyle("-fx-padding: 5; -fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 3;");
        return box;
    }

    private HBox createActionButtons() {
        cancelEditButton.setVisible(false);
        saveRecipeButton.setOnAction(e -> handleSaveRecipe());
        cancelEditButton.setOnAction(e -> resetForm());
        return new HBox(10, saveRecipeButton, cancelEditButton);
    }

    private VBox createRecipeListSection() {
        Label recipeLabel = new Label("Available Recipes");
        recipeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        recipeListView.setPrefHeight(120);
        recipeListView.setMinHeight(80);

        recipeDetailsArea.setEditable(false);
        recipeDetailsArea.setPrefHeight(100);
        recipeDetailsArea.setMinHeight(80);

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

        VBox section = new VBox(5, recipeLabel, recipeListView, listBox, recipeDetailsArea);
        VBox.setVgrow(recipeListView, Priority.SOMETIMES);
        VBox.setVgrow(recipeDetailsArea, Priority.SOMETIMES);
        return section;
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
            List<Tag> tags = parseTags(tagsField.getText());

            controller.addIngredient(name, quantity, unit, tags);
            componentItems.add(formatIngredient(name, quantity, unit, tags));
            clearIngredientFields();
        } catch (NumberFormatException ex) {
            showError("Quantity must be a number.");
        }
    }

    private void handleAddSubRecipe() {
        Recipe selectedRecipe = subRecipeComboBox.getValue();
        if (selectedRecipe == null) {
            showError("Please select a recipe to add.");
            return;
        }

        controller.addSubRecipe(selectedRecipe);
        componentItems.add(formatSubRecipe(selectedRecipe));
        subRecipeComboBox.getSelectionModel().clearSelection();
    }

    private void handleRemoveComponent() {
        int index = componentListView.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            controller.removeComponent(index);
            componentItems.remove(index);
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

        componentItems.clear();
        for (MealComponent component : controller.getCurrentComponents()) {
            componentItems.add(formatComponent(component));
        }

        saveRecipeButton.setText("Update Recipe");
        cancelEditButton.setVisible(true);
        refreshSubRecipeComboBox();
    }

    private void resetForm() {
        recipeToEdit = null;
        nameField.clear();
        descriptionField.clear();
        clearIngredientFields();
        componentItems.clear();
        controller.clearComponents();
        saveRecipeButton.setText("Save Recipe");
        cancelEditButton.setVisible(false);
        refreshSubRecipeComboBox();
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

    private void refreshSubRecipeComboBox() {
        if (controller == null) return;

        List<Recipe> available = controller.getAvailableRecipesExcluding(recipeToEdit);
        subRecipeComboBox.setItems(FXCollections.observableArrayList(available));
        subRecipeComboBox.getSelectionModel().clearSelection();
    }

    private List<Tag> parseTags(String tagString) {
        return Arrays.stream(tagString.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Tag::new) //conversion
                .collect(Collectors.toList());
    }

    private String formatComponent(MealComponent component) {
        if (component instanceof Ingredient ingredient) {
            return formatIngredient(ingredient.getName(), ingredient.getQuantity(),
                    ingredient.getUnit(), ingredient.getTags());
        } else if (component instanceof Recipe recipe) {
            return formatSubRecipe(recipe);
        }
        return component.getName();
    }

    private String formatIngredient(String name, double quantity, Unit unit, List<Tag> tags) {
        String tagStr = tags.isEmpty()
                ? ""
                : " [" +
                tags.stream()
                .map(Tag::getName)
                .collect(Collectors.joining(", "))
                + "]";
        return String.format("[Ingredient] %s (%.2f %s)%s", name, quantity, unit, tagStr);
    }

    private String formatSubRecipe(Recipe recipe) {
        int ingredientCount = recipe.getIngredients().size();
        return String.format("[Sub-Recipe] %s (%d ingredients)", recipe.getName(), ingredientCount);
    }

    @Override
    public void showRecipes(List<Recipe> recipes) {
        this.currentRecipes = recipes;
        ObservableList<String> items = FXCollections.observableArrayList(
                recipes.stream().map(Recipe::getName).toList()
        );
        recipeListView.setItems(items);
        refreshSubRecipeComboBox();
    }

    @Override
    public void showDetails(Recipe recipe) {
        StringBuilder details = new StringBuilder();
        details.append(recipe.getName()).append(": ").append(recipe.getDescription()).append("\n");

        String tags = recipe.calculateTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.joining(", "));
        details.append("Tags: ").append(tags.isEmpty() ? "None" : tags).append("\n\n");

        details.append("Components:\n");
        for (MealComponent component : recipe.getComponents()) {
            details.append("  ").append(formatComponent(component)).append("\n");
        }

        details.append("\nAll Ingredients (flattened):\n");
        for (Ingredient ingredient : recipe.getIngredients()) {
            details.append("  - ").append(ingredient.getName())
                    .append(" (").append(ingredient.getQuantity())
                    .append(" ").append(ingredient.getUnit()).append(")\n");
        }

        recipeDetailsArea.setText(details.toString());
    }

    @Override
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setController(RecipeController controller) {
        this.controller = controller;
        refreshSubRecipeComboBox();
    }
}