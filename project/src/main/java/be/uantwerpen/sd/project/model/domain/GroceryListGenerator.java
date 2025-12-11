package be.uantwerpen.sd.project.model.domain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class GroceryListGenerator implements PropertyChangeListener {
    private WeeklyMealPlan plan;
    private Map<String, Double> groceryList = new HashMap<>(); //useful for merges of quantities

    public GroceryListGenerator(WeeklyMealPlan plan) {
        this.plan = plan;
        plan.addListener(this);
        regenerate(); //initial
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mealPlanUpdated".equals(evt.getPropertyName())) {
            regenerate();
        }
    }

    private void regenerate() {
        groceryList.clear();

        for (DayPlan day : plan.getDayPlans()) {
            for (PlannedMeal plannedMeal : day.getPlannedMeals()) {
                if (plannedMeal.getMealComponent() != null) {
                    for (Ingredient ingredient : plannedMeal.getMealComponent().getIngredients()) {
                        groceryList.merge(ingredient.getName(), ingredient.getQuantity(), Double::sum); //Double::sum to merge quantities!
                    }
                }
            }
        }
        System.out.println("Updated grocery list: " + groceryList);
    }
}
