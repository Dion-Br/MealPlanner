package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;
import java.util.*;

public class DayPlan {
    private DaysOfTheWeek day;
    private List<PlannedMeal> plannedMeals = new ArrayList<>();

    public DayPlan(DaysOfTheWeek day) {
        this.day = day;
    }

    public void addPlannedMeal(PlannedMeal newMeal){
        this.plannedMeals.add(newMeal);
    }

    // Eventueel als we een verschuif functie in de agenda willen gebruiken
    public void changeDayOfTheWeek(DaysOfTheWeek newDay){
        this.day = newDay;
    }
}
