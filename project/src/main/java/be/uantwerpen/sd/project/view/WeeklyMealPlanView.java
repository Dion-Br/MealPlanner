package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.controller.WeeklyMealPlanController;

public interface WeeklyMealPlanView {
    void refreshRecipeList();
    void refreshRecipeList(boolean reloadTags);
    void setController(WeeklyMealPlanController controller);
}
