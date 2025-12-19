package be.uantwerpen.sd.project;

import be.uantwerpen.sd.project.controller.RecipeController;
import be.uantwerpen.sd.project.model.domain.Ingredient;
import be.uantwerpen.sd.project.view.RecipeFxView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewApp extends Application {
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        RecipeFxView view = new RecipeFxView();
        RecipeController controller = new RecipeController(view);
        view.setController(controller);

        Scene scene = new Scene(view, 800, 700);
        stage.setScene(scene);
        stage.setTitle("Recipe Manager");
        stage.show();
    }
}
