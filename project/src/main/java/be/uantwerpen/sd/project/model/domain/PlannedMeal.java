package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;
import be.uantwerpen.sd.project.model.domain.enums.MealType;

public class PlannedMeal {
    private MealType mealType;
    private MealComponent mealComponent;

    public PlannedMeal(MealType mealType, MealComponent mealComponent) {
        this.mealType = mealType;
        this.mealComponent = mealComponent;
    }

    // Maaltijd veranderen van Breakfast naar Lunch bv.
    public void changeMealType(MealType newMealType){
        this.mealType = newMealType;
    }

    // De volledige maaltijd vervangen door iets anders..
    // Wordt waarschijnlijk nooit gebruikt
    public void changeMealComponent(MealComponent newMealComponent){
        this.mealComponent = newMealComponent;
    }
}
