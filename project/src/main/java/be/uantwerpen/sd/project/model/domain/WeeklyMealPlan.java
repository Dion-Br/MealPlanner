package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class WeeklyMealPlan{
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this); //registration and notification of listeners
    private List<DayPlan> dayPlans;

    public WeeklyMealPlan() {
        dayPlans = new ArrayList<>();
        for (DaysOfTheWeek day : DaysOfTheWeek.values()) {
            DayPlan dp = new DayPlan(day);
            dp.setParent(this);
            dayPlans.add(dp);
        }
    }
    public void addListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    protected void notifyObservers() {
        pcs.firePropertyChange("mealPlanUpdated", null, this);
    }

    public List<DayPlan> getDayPlans() {
        return dayPlans;
    }

    public void setDayPlans(List<DayPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }
}
