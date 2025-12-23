package be.uantwerpen.sd.project.controller;

import be.uantwerpen.sd.project.model.domain.*;
import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;
import be.uantwerpen.sd.project.model.domain.enums.MealType;
import be.uantwerpen.sd.project.service.RecipeService;
import be.uantwerpen.sd.project.view.WeeklyMealPlanFxView;

import java.util.List;

public class WeeklyMealPlanController {
    private final WeeklyMealPlan model;
    private final RecipeService recipeService;
    private WeeklyMealPlanFxView view;

    public WeeklyMealPlanController(WeeklyMealPlan model) {
        this.model = model;
        // We use the existing service to get the recipes created in the other tab
        this.recipeService = new RecipeService();
    }

    public void setView(WeeklyMealPlanFxView view) {
        this.view = view;
    }

    public List<Recipe> getAvailableRecipes() {
        return recipeService.getAllRecipes();
    }

    public void addMealToPlan(DaysOfTheWeek day, MealType type, Recipe recipe) {
        if (day == null || type == null || recipe == null) {
            // show error in view
            System.err.println("Invalid input for meal plan");
            return;
        }

        // Find the correct DayPlan
        for (DayPlan dayPlan : model.getDayPlans()) {
            if (dayPlan.getDay() == day) {
                // Create the planned meal
                PlannedMeal meal = new PlannedMeal(type, recipe);
                // Add it (this will trigger the observer in WeeklyMealPlan -> GroceryListGenerator)
                dayPlan.addPlannedMeal(meal);
                break;
            }
        }
    }

    public void removeMeal(PlannedMeal meal, DayPlan dayPlan) {
        if (meal != null && dayPlan != null) {
            dayPlan.removePlannedMeal(meal);
        }
    }
}