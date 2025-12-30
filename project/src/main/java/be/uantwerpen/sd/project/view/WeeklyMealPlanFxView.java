package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.controller.WeeklyMealPlanController;
import be.uantwerpen.sd.project.model.domain.DayPlan;
import be.uantwerpen.sd.project.model.domain.PlannedMeal;
import be.uantwerpen.sd.project.model.domain.Recipe;
import be.uantwerpen.sd.project.model.domain.WeeklyMealPlan;
import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;
import be.uantwerpen.sd.project.model.domain.enums.MealType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WeeklyMealPlanFxView extends BorderPane implements PropertyChangeListener, WeeklyMealPlanView {

    private final WeeklyMealPlan model;
    private final VBox daysContainer;
    private final ComboBox<Recipe> recipeComboBox;
    private final ComboBox<String> tagFilterComboBox;
    private final ComboBox<DaysOfTheWeek> dayComboBox;
    private final ComboBox<MealType> typeComboBox;

    private WeeklyMealPlanController controller;

    public WeeklyMealPlanFxView(WeeklyMealPlan model) {
        this.model = model;
        this.daysContainer = new VBox(10);
        this.recipeComboBox = new ComboBox<>();
        this.tagFilterComboBox = new ComboBox<>();
        this.dayComboBox = new ComboBox<>(FXCollections.observableArrayList(DaysOfTheWeek.values()));
        this.typeComboBox = new ComboBox<>(FXCollections.observableArrayList(MealType.values()));

        model.addListener(this);
        initializeLayout();
    }

    private void initializeLayout() {
        setPadding(new Insets(10));

        HBox topBar = createTopBar();
        setTop(topBar);

        ScrollPane scrollPane = new ScrollPane(daysContainer);
        scrollPane.setFitToWidth(true);
        setCenter(scrollPane);

        renderDays();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.setStyle("-fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");

        configureDayComboBox();
        configureTypeComboBox();
        configureTagFilterComboBox();
        configureRecipeComboBox();

        Button addButton = createAddButton();
        Button refreshButton = createRefreshButton();

        topBar.getChildren().addAll(dayComboBox, typeComboBox, tagFilterComboBox,
                recipeComboBox, refreshButton, addButton);
        return topBar;
    }

    private void configureDayComboBox() {
        dayComboBox.setPromptText("Select Day");
        dayComboBox.getSelectionModel().selectFirst();
    }

    private void configureTypeComboBox() {
        typeComboBox.setPromptText("Select Type");
        typeComboBox.getSelectionModel().selectFirst();
    }

    private void configureTagFilterComboBox() {
        tagFilterComboBox.setPromptText("Filter by Tag");
        tagFilterComboBox.setPrefWidth(150);
        tagFilterComboBox.setOnAction(e -> refreshRecipeList(false));
    }

    private void configureRecipeComboBox() {
        recipeComboBox.setPromptText("Select Recipe");
        recipeComboBox.setPrefWidth(200);
        recipeComboBox.setConverter(new StringConverter<>() {
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

    private Button createAddButton() {
        Button addButton = new Button("Add to Plan");
        addButton.setOnAction(e -> {
            if (controller != null) {
                controller.addMealToPlan(
                        dayComboBox.getValue(),
                        typeComboBox.getValue(),
                        recipeComboBox.getValue()
                );
            }
        });
        return addButton;
    }

    private Button createRefreshButton() {
        Button refreshButton = new Button("↻");
        refreshButton.setTooltip(new Tooltip("Refresh Recipes & Tags"));
        refreshButton.setOnAction(e -> refreshRecipeList(true));
        return refreshButton;
    }

    public void setController(WeeklyMealPlanController controller) {
        this.controller = controller;
        refreshRecipeList(true);
    }

    @Override
    public void refreshRecipeList() {
        refreshRecipeList(true);
    }

    @Override
    public void refreshRecipeList(boolean reloadTags) {
        if (controller == null) return;

        if (reloadTags) {
            reloadTagFilter();
        }
        loadRecipes();
    }

    private void reloadTagFilter() {
        String currentSelection = tagFilterComboBox.getValue();
        List<String> tags = new ArrayList<>();
        tags.add("All");
        tags.addAll(controller.getUniqueTags());

        tagFilterComboBox.setItems(FXCollections.observableArrayList(tags));

        if (currentSelection != null && tags.contains(currentSelection)) {
            tagFilterComboBox.setValue(currentSelection);
        } else {
            tagFilterComboBox.getSelectionModel().selectFirst();
        }
    }

    private void loadRecipes() {
        String filter = tagFilterComboBox.getValue();
        List<Recipe> recipes = controller.getAvailableRecipes(filter);

        recipeComboBox.setItems(FXCollections.observableArrayList(recipes));
        if (!recipes.isEmpty()) {
            recipeComboBox.getSelectionModel().selectFirst();
        } else {
            recipeComboBox.getSelectionModel().clearSelection();
        }
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
            VBox dayBox = createDayBox(dayPlan);
            daysContainer.getChildren().add(dayBox);
        }
    }

    private VBox createDayBox(DayPlan dayPlan) {
        VBox dayBox = new VBox(5);
        dayBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10; -fx-background-radius: 5;");

        Label dayTitle = new Label(dayPlan.getDay().name());
        dayTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        dayBox.getChildren().add(dayTitle);

        if (dayPlan.getPlannedMeals().isEmpty()) {
            dayBox.getChildren().add(new Label("No meals planned."));
        } else {
            addMealRows(dayBox, dayPlan);
        }

        return dayBox;
    }

    private void addMealRows(VBox dayBox, DayPlan dayPlan) {
        List<PlannedMeal> sortedMeals = new ArrayList<>(dayPlan.getPlannedMeals());
        sortedMeals.sort(Comparator.comparing(PlannedMeal::getMealType));

        for (PlannedMeal meal : sortedMeals) {
            HBox mealRow = createMealRow(meal, dayPlan);
            dayBox.getChildren().add(mealRow);
        }
    }

    private HBox createMealRow(PlannedMeal meal, DayPlan dayPlan) {
        HBox mealRow = new HBox(10);

        String text = String.format("%-10s : %s", meal.getMealType(), meal.getMealComponent().getName());
        Label mealLabel = new Label(text);

        Button removeButton = new Button("x");
        removeButton.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-padding: 2 5 2 5;");
        removeButton.setOnAction(e -> {
            if (controller != null) {
                controller.removeMeal(meal, dayPlan);
            }
        });

        mealRow.getChildren().addAll(mealLabel, removeButton);
        return mealRow;
    }
}