package be.uantwerpen.sd.project;

import be.uantwerpen.sd.project.controller.RecipeController;
import be.uantwerpen.sd.project.controller.WeeklyMealPlanController;
import be.uantwerpen.sd.project.model.domain.GroceryListGenerator;
import be.uantwerpen.sd.project.model.domain.WeeklyMealPlan;
import be.uantwerpen.sd.project.view.GroceryFxView;
import be.uantwerpen.sd.project.view.RecipeFxView;
import be.uantwerpen.sd.project.view.WeeklyMealPlanFxView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class ViewApp extends Application {
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Create Models
        WeeklyMealPlan mealPlan = new WeeklyMealPlan();
        GroceryListGenerator groceryGen = new GroceryListGenerator(mealPlan);

        // 2. Create Views
        RecipeFxView recipeView = new RecipeFxView();
        WeeklyMealPlanFxView planView = new WeeklyMealPlanFxView(mealPlan); // Observe model
        GroceryFxView groceryView = new GroceryFxView(groceryGen);

        // 3. Setup Controllers
        RecipeController recipeController = new RecipeController(recipeView);
        recipeView.setController(recipeController);

        WeeklyMealPlanController planController = new WeeklyMealPlanController(mealPlan);
        planController.setView(planView);
        planView.setController(planController);

        // 4. Layout with Tabs
        TabPane tabPane = new TabPane();

        Tab recipeTab = new Tab("Manage Recipes", recipeView);
        recipeTab.setClosable(false);

        // Add the new tab
        Tab planTab = new Tab("Weekly Plan", planView);
        planTab.setClosable(false);
        // Auto-refresh recipe list when clicking this tab
        planTab.setOnSelectionChanged(e -> {
            if (planTab.isSelected()) planView.refreshRecipeList();
        });

        Tab groceryTab = new Tab("Grocery List", groceryView);
        groceryTab.setClosable(false);

        tabPane.getTabs().addAll(recipeTab, planTab, groceryTab);

        Scene scene = new Scene(tabPane, 900, 700);
        stage.setScene(scene);
        stage.setTitle("Smart Meal Planner");
        stage.show();
    }
}