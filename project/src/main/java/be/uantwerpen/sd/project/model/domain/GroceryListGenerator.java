package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.Unit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class GroceryListGenerator implements PropertyChangeListener {

    private static final String KEY_SEPARATOR = "|";

    private final WeeklyMealPlan plan;
    private final ObservableList<GroceryItem> items;
    private final Map<String, Double> manualItems;

    public GroceryListGenerator(WeeklyMealPlan plan) {
        this.plan = plan;
        this.items = FXCollections.observableArrayList();
        this.manualItems = new HashMap<>();
        plan.addListener(this);
        regenerate();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mealPlanUpdated".equals(evt.getPropertyName())) {
            regenerate();
        }
    }

    public ObservableList<GroceryItem> getItems() {
        return items;
    }

    public void addManualItem(String name, double quantity, Unit unit) {
        Unit baseUnit = unit.getBaseUnit();
        double baseQty = unit.toBaseQuantity(quantity);
        String key = createKey(name, baseUnit);
        manualItems.merge(key, baseQty, Double::sum);
        regenerate();
    }

    private void regenerate() {
        Map<String, Boolean> boughtStatus = saveBoughtStatus();
        Map<String, Double> totals = calculateTotals();
        rebuildItemsList(totals, boughtStatus);
    }

    private Map<String, Boolean> saveBoughtStatus() {
        Map<String, Boolean> boughtStatus = new HashMap<>();
        for (GroceryItem item : items) {
            String key = createKey(item.getName(), item.getUnit());
            boughtStatus.put(key, item.isBought());
        }
        return boughtStatus;
    }

    private Map<String, Double> calculateTotals() {
        Map<String, Double> totals = new HashMap<>();
        addIngredientsFromPlan(totals);
        addManualItemsToTotals(totals);
        return totals;
    }

    private void addIngredientsFromPlan(Map<String, Double> totals) {
        for (DayPlan day : plan.getDayPlans()) {
            for (PlannedMeal plannedMeal : day.getPlannedMeals()) {
                if (plannedMeal.getMealComponent() != null) {
                    addMealIngredients(plannedMeal, totals);
                }
            }
        }
    }

    private void addMealIngredients(PlannedMeal plannedMeal, Map<String, Double> totals) {
        for (Ingredient ingredient : plannedMeal.getMealComponent().getIngredients()) {
            Unit unit = ingredient.getUnit();
            Unit baseUnit = unit.getBaseUnit();
            double baseQty = unit.toBaseQuantity(ingredient.getQuantity());
            String key = createKey(ingredient.getName(), baseUnit);
            totals.merge(key, baseQty, Double::sum);
        }
    }

    private void addManualItemsToTotals(Map<String, Double> totals) {
        manualItems.forEach((key, qty) -> totals.merge(key, qty, Double::sum));
    }

    private void rebuildItemsList(Map<String, Double> totals, Map<String, Boolean> boughtStatus) {
        items.clear();
        totals.forEach((key, qty) -> {
            GroceryItem newItem = createGroceryItem(key, qty);
            if (newItem != null) {
                restoreBoughtStatus(newItem, key, boughtStatus);
                items.add(newItem);
            }
        });
    }

    private GroceryItem createGroceryItem(String key, double quantity) {
        try {
            String[] parts = key.split("\\" + KEY_SEPARATOR);
            String name = parts[0];
            Unit unit = Unit.valueOf(parts[1]);
            return new GroceryItem(name, quantity, unit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void restoreBoughtStatus(GroceryItem item, String key, Map<String, Boolean> boughtStatus) {
        if (boughtStatus.getOrDefault(key, false)) {
            item.setBought(true);
        }
    }

    private String createKey(String name, Unit unit) {
        return name + KEY_SEPARATOR + unit.name();
    }
}