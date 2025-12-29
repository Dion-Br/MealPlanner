package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.DaysOfTheWeek;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class WeeklyMealPlan implements PropertyChangeListener {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
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

    /**
     * React to changes in the RecipeRepository.
     * If a recipe is deleted, remove it from the plan.
     * If a recipe is updated, notify observers so UI and GroceryList update.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("recipesChanged".equals(evt.getPropertyName())) {
            // 1. Get the list of currently valid recipes
            List<?> validRecipes = (List<?>) evt.getNewValue();

            boolean planChanged = false;

            // 2. Check every day for recipes that no longer exist
            for (DayPlan day : dayPlans) {
                List<PlannedMeal> toRemove = new ArrayList<>();
                for (PlannedMeal pm : day.getPlannedMeals()) {
                    // Check if the component is a Recipe and if it is missing from the valid list
                    if (pm.getMealComponent() instanceof Recipe r) {
                        if (!validRecipes.contains(r)) {
                            toRemove.add(pm);
                        }
                    }
                }

                // Remove the dead meals
                if (!toRemove.isEmpty()) {
                    day.getPlannedMeals().removeAll(toRemove);
                    planChanged = true;
                }
            }

            // 3. Notify everyone (GroceryListGenerator, View) that the plan has changed (or data inside it updated)
            // We notify even if planChanged is false, because the ingredients INSIDE a recipe might have changed.
            notifyObservers();
        }
    }
}