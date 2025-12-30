package be.uantwerpen.sd.project;

import be.uantwerpen.sd.project.controller.RecipeController;
import be.uantwerpen.sd.project.controller.WeeklyMealPlanController;
import be.uantwerpen.sd.project.model.domain.GroceryListGenerator;
import be.uantwerpen.sd.project.model.domain.WeeklyMealPlan;
import be.uantwerpen.sd.project.repository.RecipeRepository;
import be.uantwerpen.sd.project.service.RecipeService;
import be.uantwerpen.sd.project.view.GroceryFxView;
import be.uantwerpen.sd.project.view.RecipeFxView;
import be.uantwerpen.sd.project.view.WeeklyMealPlanFxView;
import be.uantwerpen.sd.project.view.WeeklyMealPlanView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ViewApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        WeeklyMealPlan mealPlan = new WeeklyMealPlan();
        GroceryListGenerator groceryGen = new GroceryListGenerator(mealPlan);
        RecipeRepository recipeRepository = RecipeRepository.getInstance();
        RecipeService recipeService = new RecipeService(recipeRepository);

        recipeRepository.addPropertyChangeListener(mealPlan);

        RecipeFxView recipeView = new RecipeFxView();
        WeeklyMealPlanView planView = new WeeklyMealPlanFxView(mealPlan);
        GroceryFxView groceryView = new GroceryFxView(groceryGen);

        RecipeController recipeController = new RecipeController(recipeView, recipeService);
        recipeView.setController(recipeController);

        WeeklyMealPlanController planController = new WeeklyMealPlanController(mealPlan, recipeService);
        planController.setView(planView);
        planView.setController(planController);

        TabPane tabPane = createTabPane(recipeView, planView, groceryView);

        Scene scene = new Scene(tabPane, 900, 700);
        stage.setScene(scene);
        stage.setTitle("Smart Meal Planner");
        stage.show();
    }

    private TabPane createTabPane(RecipeFxView recipeView, WeeklyMealPlanView  planView, GroceryFxView groceryView) {
        TabPane tabPane = new TabPane();

        Tab recipeTab = new Tab("Manage Recipes", recipeView);
        recipeTab.setClosable(false);

        Tab planTab = new Tab("Weekly Plan", (BorderPane) planView);
        planTab.setClosable(false);
        planTab.setOnSelectionChanged(e -> {
            if (planTab.isSelected()) {
                planView.refreshRecipeList();
            }
        });

        Tab groceryTab = new Tab("Grocery List", groceryView);
        groceryTab.setClosable(false);

        tabPane.getTabs().addAll(recipeTab, planTab, groceryTab);
        return tabPane;
    }
}