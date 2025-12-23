package be.uantwerpen.sd.project;

import be.uantwerpen.sd.project.controller.RecipeController;
import be.uantwerpen.sd.project.model.domain.GroceryListGenerator;
import be.uantwerpen.sd.project.model.domain.WeeklyMealPlan;
import be.uantwerpen.sd.project.view.GroceryFxView;
import be.uantwerpen.sd.project.view.RecipeFxView;
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
        WeeklyMealPlan mealPlan = new WeeklyMealPlan(); // The source of truth
        GroceryListGenerator groceryGen = new GroceryListGenerator(mealPlan); // The observer

        // 2. Create Views
        RecipeFxView recipeView = new RecipeFxView();
        GroceryFxView groceryView = new GroceryFxView(groceryGen);

        // 3. Setup Controllers
        RecipeController recipeController = new RecipeController(recipeView);
        recipeView.setController(recipeController);

        // 4. Layout with Tabs
        TabPane tabPane = new TabPane();

        Tab recipeTab = new Tab("Recipes", recipeView);
        recipeTab.setClosable(false);

        Tab groceryTab = new Tab("Grocery List", groceryView);
        groceryTab.setClosable(false);

        tabPane.getTabs().addAll(recipeTab, groceryTab);

        Scene scene = new Scene(tabPane, 800, 700);
        stage.setScene(scene);
        stage.setTitle("Smart Meal Planner");
        stage.show();
    }
}
