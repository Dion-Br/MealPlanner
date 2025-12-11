package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;
import be.uantwerpen.sd.project.model.domain.enums.MealType;

public class PlannedMeal {
    private MealType mealType;
    private MealComponent mealComponent;
    private WeeklyMealPlan parent;

    public PlannedMeal(MealType mealType, MealComponent mealComponent) {
        this.mealType = mealType;
        this.mealComponent = mealComponent;
    }

    //changing mealtype from Breakfast to Lunch for example
    public void changeMealType(MealType newMealType) {
        this.mealType = newMealType;
    }

    //changing a complete meal by something else
    //will probably not get used
    public void changeMealComponent(MealComponent newMealComponent) {
        this.mealComponent = newMealComponent;
    }

    public void setMealComponent(MealComponent newComponent) {
        MealComponent old = this.mealComponent;
        this.mealComponent = newComponent;

        if (parent != null) {
            parent.notifyObservers();
        }
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public MealComponent getMealComponent() {
        return mealComponent;
    }

    public WeeklyMealPlan getParent() {
        return parent;
    }

    public void setParent(WeeklyMealPlan parent) {
        this.parent = parent;
    }
}
