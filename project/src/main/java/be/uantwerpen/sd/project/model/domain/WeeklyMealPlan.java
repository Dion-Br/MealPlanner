package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class WeeklyMealPlan implements PropertyChangeListener {

    private static final String MEAL_PLAN_UPDATED = "mealPlanUpdated";
    private static final String RECIPES_CHANGED = "recipesChanged";

    private final PropertyChangeSupport propertyChangeSupport;
    private final List<DayPlan> dayPlans;

    public WeeklyMealPlan() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.dayPlans = new ArrayList<>();
        initializeDayPlans();
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void notifyObservers() {
        propertyChangeSupport.firePropertyChange(MEAL_PLAN_UPDATED, null, this);
    }

    public List<DayPlan> getDayPlans() {
        return dayPlans;
    }

    public void setDayPlans(List<DayPlan> dayPlans) {
        this.dayPlans.clear();
        this.dayPlans.addAll(dayPlans);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (RECIPES_CHANGED.equals(evt.getPropertyName())) {
            handleRecipesChanged(evt);
        }
    }

    private void initializeDayPlans() {
        for (DaysOfTheWeek day : DaysOfTheWeek.values()) {
            DayPlan dayPlan = new DayPlan(day);
            dayPlan.setParent(this);
            dayPlans.add(dayPlan);
        }
    }

    private void handleRecipesChanged(PropertyChangeEvent evt) {
        List<?> validRecipes = (List<?>) evt.getNewValue();
        removeInvalidMeals(validRecipes);
        notifyObservers();
    }

    private void removeInvalidMeals(List<?> validRecipes) {
        for (DayPlan day : dayPlans) {
            List<PlannedMeal> toRemove = findInvalidMeals(day, validRecipes);
            day.getPlannedMeals().removeAll(toRemove);
        }
    }

    private List<PlannedMeal> findInvalidMeals(DayPlan day, List<?> validRecipes) {
        List<PlannedMeal> toRemove = new ArrayList<>();
        for (PlannedMeal meal : day.getPlannedMeals()) {
            if (meal.getMealComponent() instanceof Recipe recipe) {
                if (!validRecipes.contains(recipe)) {
                    toRemove.add(meal);
                }
            }
        }
        return toRemove;
    }
}