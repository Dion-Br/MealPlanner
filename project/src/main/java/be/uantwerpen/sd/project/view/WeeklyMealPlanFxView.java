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

    public WeeklyMealPlanFxView(WeeklyMealPlan model) {
        this.model = model;
        this.model.addListener(this); // Observe the model

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

        recipeCombo.setPromptText("Select Recipe");
        // Converter to show Recipe Name instead of Object ID
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

        // Refresh recipes button (since they might change in the other tab)
        Button refreshBtn = new Button("↻");
        refreshBtn.setTooltip(new Tooltip("Refresh Recipe List"));
        refreshBtn.setOnAction(e -> refreshRecipeList());

        topBar.getChildren().addAll(dayCombo, typeCombo, recipeCombo, refreshBtn, addButton);
        setTop(topBar);

        // --- CENTER: The 7 Days List ---
        ScrollPane scrollPane = new ScrollPane(daysContainer);
        scrollPane.setFitToWidth(true);
        setCenter(scrollPane);

        renderDays(); // Initial render
    }

    public void setController(WeeklyMealPlanController controller) {
        this.controller = controller;
        refreshRecipeList();
    }

    public void refreshRecipeList() {
        if(controller != null) {
            recipeCombo.setItems(FXCollections.observableArrayList(controller.getAvailableRecipes()));
        }
    }

    // React to Model Changes (Observer Pattern)
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mealPlanUpdated".equals(evt.getPropertyName())) {
            // JavaFX UI updates must run on the JavaFX thread
            Platform.runLater(this::renderDays);
        }
    }

    private void renderDays() {
        daysContainer.getChildren().clear();

        for (DayPlan dayPlan : model.getDayPlans()) { //
            VBox dayBox = new VBox(5);
            dayBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10; -fx-background-radius: 5;");

            Label dayTitle = new Label(dayPlan.getDay().name());
            dayTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            dayBox.getChildren().add(dayTitle);

            if (dayPlan.getPlannedMeals().isEmpty()) { //
                dayBox.getChildren().add(new Label("No meals planned."));
            } else {
                // [CHANGED] Create a copy of the list and sort it by MealType (enum order)
                List<PlannedMeal> sortedMeals = new ArrayList<>(dayPlan.getPlannedMeals());
                sortedMeals.sort(Comparator.comparing(PlannedMeal::getMealType));

                for (PlannedMeal pm : sortedMeals) {
                    // Create a container for the row
                    HBox mealRow = new HBox(10);

                    // Text for the meal
                    String text = String.format("%-10s : %s",
                            pm.getMealType(),
                            pm.getMealComponent().getName());
                    Label mealLabel = new Label(text);

                    // Remove Button
                    Button removeBtn = new Button("x");
                    removeBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-padding: 2 5 2 5;");
                    removeBtn.setOnAction(e -> {
                        if (controller != null) {
                            controller.removeMeal(pm, dayPlan);
                        }
                    });

                    // Add label and button to the row
                    mealRow.getChildren().addAll(mealLabel, removeBtn);

                    // Add the row to the day's box
                    dayBox.getChildren().add(mealRow);
                }
            }
            daysContainer.getChildren().add(dayBox);
        }
    }
}