package be.uantwerpen.sd.project.model.domain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javafx.collections.FXCollections;
import java.util.Map;
import javafx.collections.ObservableList;

public class GroceryListGenerator implements PropertyChangeListener {
    private WeeklyMealPlan plan;
    // Use ObservableList for JavaFX binding
    private ObservableList<GroceryItem> items = FXCollections.observableArrayList();
    private final Map<String, Double> manualItems = new HashMap<>();

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
        Map<String, Boolean> boughtStatus = new HashMap<>();
        for (GroceryItem item : items) {
            boughtStatus.put(item.getName(), item.isBought());
        }

        // Calculate totals from the Weekly Plan
        Map<String, Double> totals = new HashMap<>();

        for (DayPlan day : plan.getDayPlans()) {
            for (PlannedMeal plannedMeal : day.getPlannedMeals()) {
                if (plannedMeal.getMealComponent() != null) {
                    for (Ingredient ingredient : plannedMeal.getMealComponent().getIngredients()) {
                        totals.merge(ingredient.getName(), ingredient.getQuantity(), Double::sum);
                    }
                }
            }
        }

        // Merge in the manual items
        manualItems.forEach((name, qty) ->
                totals.merge(name, qty, Double::sum)
        );

        // Rebuild the list
        items.clear();
        totals.forEach((name, qty) -> {
            GroceryItem newItem = new GroceryItem(name, qty);

            // Restore the bought checkmark
            if (boughtStatus.getOrDefault(name, false)) {
                newItem.setBought(true);
            }
            items.add(newItem);
        });

        System.out.println("Regenerated Grocery List: " + items);
    }

    public ObservableList<GroceryItem> getItems() {
        return items;
    }

    public void addManualItem(String name, double quantity) {
        manualItems.merge(name, quantity, Double::sum);
        regenerate();
    }
}
