package be.uantwerpen.sd.project.controller;

import be.uantwerpen.sd.project.model.domain.*;
import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;
import be.uantwerpen.sd.project.model.domain.enums.MealType;
import be.uantwerpen.sd.project.service.RecipeService;
import be.uantwerpen.sd.project.view.WeeklyMealPlanView;

import java.util.List;
import java.util.stream.Collectors;

public class WeeklyMealPlanController {

    private final WeeklyMealPlan model;
    private final RecipeService recipeService;
    private WeeklyMealPlanView view;

    public WeeklyMealPlanController(WeeklyMealPlan model, RecipeService recipeService) {
        this.model = model;
        this.recipeService = recipeService;
    }

    public void setView(WeeklyMealPlanView view) {
        this.view = view;
    }

    public List<Tag> getUniqueTags() {
        return recipeService.getAllRecipes().stream()
                .flatMap(r -> r.calculateTags().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Recipe> getAvailableRecipes(Tag filterTag) {
        List<Recipe> allRecipes = recipeService.getAllRecipes();

        if (isEmptyFilter(filterTag)) {
            return allRecipes;
        }

        return allRecipes.stream()
                .filter(r -> r.calculateTags().contains(filterTag))
                .collect(Collectors.toList());
    }

    public void addMealToPlan(DaysOfTheWeek day, MealType type, Recipe recipe) {
        if (!isValidMealInput(day, type, recipe)) {
            System.err.println("Invalid input for meal plan");
            return;
        }

        DayPlan dayPlan = findDayPlan(day);
        if (dayPlan != null) {
            PlannedMeal meal = new PlannedMeal(type, recipe);
            dayPlan.addPlannedMeal(meal);
        }
    }

    public void removeMeal(PlannedMeal meal, DayPlan dayPlan) {
        if (meal != null && dayPlan != null) {
            dayPlan.removePlannedMeal(meal);
        }
    }

    private boolean isEmptyFilter(Tag filterTag) {
        return filterTag == null || filterTag.getName().equalsIgnoreCase("All");
    }

    private boolean isValidMealInput(DaysOfTheWeek day, MealType type, Recipe recipe) {
        return day != null && type != null && recipe != null;
    }

    private DayPlan findDayPlan(DaysOfTheWeek day) {
        for (DayPlan dayPlan : model.getDayPlans()) {
            if (dayPlan.getDay() == day) {
                return dayPlan;
            }
        }
        return null;
    }
}