package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;
import java.util.*;

public class DayPlan {
    private DaysOfTheWeek day;
    private List<PlannedMeal> plannedMeals = new ArrayList<>();
    private WeeklyMealPlan parent;

    public DayPlan(DaysOfTheWeek day) {
        this.day = day;
    }

    public void addPlannedMeal(PlannedMeal newMeal){
        this.plannedMeals.add(newMeal);

        // bubble event to WeeklyMealPlan
        newMeal.setParent(parent);

        if (parent != null) parent.notifyObservers();
    }

    // Eventueel als we een verschuif functie in de agenda willen gebruiken
    public void changeDayOfTheWeek(DaysOfTheWeek newDay){
        this.day = newDay;
    }

    public DaysOfTheWeek getDay() {
        return day;
    }

    public void setDay(DaysOfTheWeek day) {
        this.day = day;
    }

    public List<PlannedMeal> getPlannedMeals() {
        return plannedMeals;
    }

    public void setPlannedMeals(List<PlannedMeal> plannedMeals) {
        this.plannedMeals = plannedMeals;
    }

    public WeeklyMealPlan getParent() {
        return parent;
    }

    public void setParent(WeeklyMealPlan parent) {
        this.parent = parent;
    }
}
