package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.controller.WeeklyMealPlanController;
import be.uantwerpen.sd.project.model.domain.*;
import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;
import be.uantwerpen.sd.project.model.domain.enums.MealType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WeeklyMealPlanFxView extends BorderPane implements PropertyChangeListener {
    private final WeeklyMealPlan model;
    private WeeklyMealPlanController controller;

    // UI Elements
    private final VBox daysContainer = new VBox(10);
    private final ComboBox<Recipe> recipeCombo = new ComboBox<>();

    // ComboBox for tags instead of TextField
    private final ComboBox<String> tagFilterCombo = new ComboBox<>();

    public WeeklyMealPlanFxView(WeeklyMealPlan model) {
        this.model = model;
        this.model.addListener(this);

        setPadding(new Insets(10));

        // --- TOP: Selection Form ---
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.setStyle("-fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");

        ComboBox<DaysOfTheWeek> dayCombo = new ComboBox<>(FXCollections.observableArrayList(DaysOfTheWeek.values()));
        dayCombo.setPromptText("Select Day");
        dayCombo.getSelectionModel().selectFirst();

        ComboBox<MealType> typeCombo = new ComboBox<>(FXCollections.observableArrayList(MealType.values()));
        typeCombo.setPromptText("Select Type");
        typeCombo.getSelectionModel().selectFirst();

        // Configure Tag Combo
        tagFilterCombo.setPromptText("Filter by Tag");
        tagFilterCombo.setPrefWidth(150);

        // When user selects a tag, auto-refresh the recipe list
        tagFilterCombo.setOnAction(e -> refreshRecipeList(false));

        recipeCombo.setPromptText("Select Recipe");
        recipeCombo.setPrefWidth(200);
        recipeCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Recipe recipe) {
                return recipe == null ? null : recipe.getName();
            }
            @Override
            public Recipe fromString(String s) { return null; }
        });

        Button addButton = new Button("Add to Plan");
        addButton.setOnAction(e -> {
            if (controller != null) {
                controller.addMealToPlan(
                        dayCombo.getValue(),
                        typeCombo.getValue(),
                        recipeCombo.getValue()
                );
            }
        });

        // Refresh button: Updates BOTH the tag list and the recipe list
        Button refreshBtn = new Button("↻");
        refreshBtn.setTooltip(new Tooltip("Refresh Recipes & Tags"));
        refreshBtn.setOnAction(e -> refreshRecipeList(true));

        topBar.getChildren().addAll(dayCombo, typeCombo, tagFilterCombo, recipeCombo, refreshBtn, addButton);
        setTop(topBar);

        ScrollPane scrollPane = new ScrollPane(daysContainer);
        scrollPane.setFitToWidth(true);
        setCenter(scrollPane);

        renderDays();
    }

    public void setController(WeeklyMealPlanController controller) {
        this.controller = controller;
        refreshRecipeList(true); // Initial load of tags and recipes
    }

    /**
     * Refreshes the recipe dropdown.
     * @param reloadTags If true, it also re-fetches the available tags from the controller.
     */
    public void refreshRecipeList(boolean reloadTags) {
        if (controller == null) return;

        // 1. Reload Tags if requested
        if (reloadTags) {
            String currentSelection = tagFilterCombo.getValue();
            List<String> tags = new ArrayList<>();
            tags.add("All"); // Add an option to clear filter
            tags.addAll(controller.getUniqueTags());

            tagFilterCombo.setItems(FXCollections.observableArrayList(tags));

            // Restore selection if still valid, otherwise select "All"
            if (currentSelection != null && tags.contains(currentSelection)) {
                tagFilterCombo.setValue(currentSelection);
            } else {
                tagFilterCombo.getSelectionModel().selectFirst();
            }
        }

        // 2. Fetch Recipes based on selected tag
        String filter = tagFilterCombo.getValue();
        List<Recipe> recipes = controller.getAvailableRecipes(filter);

        recipeCombo.setItems(FXCollections.observableArrayList(recipes));
        if (!recipes.isEmpty()) {
            recipeCombo.getSelectionModel().selectFirst();
        } else {
            recipeCombo.getSelectionModel().clearSelection();
        }
    }

    // [FIXED] Changed to true to ensure tags update when switching tabs
    public void refreshRecipeList() {
        refreshRecipeList(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mealPlanUpdated".equals(evt.getPropertyName())) {
            Platform.runLater(this::renderDays);
        }
    }

    private void renderDays() {
        daysContainer.getChildren().clear();

        for (DayPlan dayPlan : model.getDayPlans()) {
            VBox dayBox = new VBox(5);
            dayBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10; -fx-background-radius: 5;");

            Label dayTitle = new Label(dayPlan.getDay().name());
            dayTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            dayBox.getChildren().add(dayTitle);

            if (dayPlan.getPlannedMeals().isEmpty()) {
                dayBox.getChildren().add(new Label("No meals planned."));
            } else {
                List<PlannedMeal> sortedMeals = new ArrayList<>(dayPlan.getPlannedMeals());
                sortedMeals.sort(Comparator.comparing(PlannedMeal::getMealType));

                for (PlannedMeal pm : sortedMeals) {
                    HBox mealRow = new HBox(10);
                    String text = String.format("%-10s : %s", pm.getMealType(), pm.getMealComponent().getName());
                    Label mealLabel = new Label(text);

                    Button removeBtn = new Button("x");
                    removeBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-padding: 2 5 2 5;");
                    removeBtn.setOnAction(e -> {
                        if (controller != null) {
                            controller.removeMeal(pm, dayPlan);
                        }
                    });

                    mealRow.getChildren().addAll(mealLabel, removeBtn);
                    dayBox.getChildren().add(mealRow);
                }
            }
            daysContainer.getChildren().add(dayBox);
        }
    }
}