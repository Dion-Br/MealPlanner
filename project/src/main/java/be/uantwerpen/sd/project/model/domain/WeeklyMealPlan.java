package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class WeeklyMealPlan implements PropertyChangeListener {
    private List<DayPlan> dayPlans;

    public WeeklyMealPlan() {
        dayPlans = new ArrayList<>();
        for (DaysOfTheWeek day : DaysOfTheWeek.values()) {
            dayPlans.add(new DayPlan(day));
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public List<DayPlan> getDayPlans() {
        return dayPlans;
    }

    public void setDayPlans(List<DayPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }
}
