package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;

import java.util.ArrayList;
import java.util.List;

public class DayPlan {

    private DaysOfTheWeek day;
    private final List<PlannedMeal> plannedMeals;
    private WeeklyMealPlan parent;

    public DayPlan(DaysOfTheWeek day) {
        this.day = day;
        this.plannedMeals = new ArrayList<>();
    }

    public void addPlannedMeal(PlannedMeal newMeal) {
        plannedMeals.add(newMeal);
        newMeal.setParent(parent);
        notifyParent();
    }

    public void removePlannedMeal(PlannedMeal mealToRemove) {
        if (plannedMeals.remove(mealToRemove)) {
            notifyParent();
        }
    }

    public void changeDayOfTheWeek(DaysOfTheWeek newDay) {
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
        this.plannedMeals.clear();
        this.plannedMeals.addAll(plannedMeals);
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