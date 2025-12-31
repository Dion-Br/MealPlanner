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

    public MealType getMealType() {
        return mealType;
    }

    public MealComponent getMealComponent() {
        return mealComponent;
    }

    public void setParent(WeeklyMealPlan parent) {
        this.parent = parent;
    }

    private void notifyParent() { //??
        if (parent != null) {
            parent.notifyObservers();
        }
    }
}