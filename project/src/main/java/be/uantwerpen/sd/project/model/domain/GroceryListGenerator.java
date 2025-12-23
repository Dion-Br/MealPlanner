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
        // Calculate totals using a temporary map
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

        // Update the ObservableList keep "bought" status if possible,
        // or just clear and refill for simplicity initially)
        items.clear();
        totals.forEach((name, qty) -> items.add(new GroceryItem(name, qty)));

        // Debug
        System.out.println("Regenerated Grocery List: " + items);
    }

    public ObservableList<GroceryItem> getItems() {
        return items;
    }

    // Method to add manual items (Requirement)
    public void addManualItem(String name, double quantity) {
        // Check if exists
        for(GroceryItem item : items) {
            if(item.getName().equalsIgnoreCase(name)) {
                item.addQuantity(quantity);
                // Force refresh in UI might be needed depending on implementation
                return;
            }
        }
        items.add(new GroceryItem(name, quantity));
    }
}
