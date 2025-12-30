package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.MealType;

public class PlannedMeal {

    private MealType mealType;
    private MealComponent mealComponent;
    private WeeklyMealPlan parent;

    public PlannedMeal(MealType mealType, MealComponent mealComponent) {
        this.mealType = mealType;
        this.mealComponent = mealComponent;
    }

    public void changeMealType(MealType newMealType) {
        this.mealType = newMealType;
    }

    public void changeMealComponent(MealComponent newMealComponent) {
        this.mealComponent = newMealComponent;
    }

    public void setMealComponent(MealComponent newComponent) {
        this.mealComponent = newComponent;
        notifyParent();
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

    private void notifyParent() {
        if (parent != null) {
            parent.notifyObservers();
        }
    }
}