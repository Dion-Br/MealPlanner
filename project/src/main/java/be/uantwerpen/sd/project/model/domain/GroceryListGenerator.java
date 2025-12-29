package be.uantwerpen.sd.project.model.domain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javafx.collections.FXCollections;
import java.util.Map;
import javafx.collections.ObservableList;
import be.uantwerpen.sd.project.model.domain.enums.Unit;

public class GroceryListGenerator implements PropertyChangeListener {
    private WeeklyMealPlan plan;
    private ObservableList<GroceryItem> items = FXCollections.observableArrayList();
    private final Map<String, Double> manualItems = new HashMap<>();

    public GroceryListGenerator(WeeklyMealPlan plan) {
        this.plan = plan;
        plan.addListener(this);
        regenerate();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // We only need to listen to the Meal Plan.
        // If a Recipe changes, WeeklyMealPlan catches it and fires "mealPlanUpdated",
        // triggering this method automatically.
        if ("mealPlanUpdated".equals(evt.getPropertyName())) {
            regenerate();
        }
    }

    private void regenerate() {
        // Keep bought status
        Map<String, Boolean> boughtStatus = new HashMap<>();
        for (GroceryItem item : items) {
            String key = item.getName() + "|" + item.getUnit().name();
            boughtStatus.put(key, item.isBought());
        }

        Map<String, Double> totals = new HashMap<>();

        // Add Ingredients from Plan
        for (DayPlan day : plan.getDayPlans()) {
            for (PlannedMeal plannedMeal : day.getPlannedMeals()) {
                if (plannedMeal.getMealComponent() != null) {
                    // This will fetch the UPDATED ingredients list because the Recipe object is modified in memory
                    for (Ingredient ingredient : plannedMeal.getMealComponent().getIngredients()) {
                        Unit unit = ingredient.getUnit();
                        Unit baseUnit = unit.getBaseUnit();
                        double baseQty = unit.toBaseQuantity(ingredient.getQuantity());

                        String key = ingredient.getName() + "|" + baseUnit.name();
                        totals.merge(key, baseQty, Double::sum);
                    }
                }
            }
        }

        // Add Manual Items
        manualItems.forEach((key, qty) ->
                totals.merge(key, qty, Double::sum)
        );

        // Rebuild List
        items.clear();
        totals.forEach((key, qty) -> {
            try {
                String[] parts = key.split("\\|");
                String name = parts[0];
                Unit unit = Unit.valueOf(parts[1]);

                GroceryItem newItem = new GroceryItem(name, qty, unit);

                if (boughtStatus.getOrDefault(key, false)) {
                    newItem.setBought(true);
                }
                items.add(newItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ObservableList<GroceryItem> getItems() {
        return items;
    }

    public void addManualItem(String name, double quantity, Unit unit) {
        Unit baseUnit = unit.getBaseUnit();
        double baseQty = unit.toBaseQuantity(quantity);
        String key = name + "|" + baseUnit.name();
        manualItems.merge(key, baseQty, Double::sum);
        regenerate();
    }
}