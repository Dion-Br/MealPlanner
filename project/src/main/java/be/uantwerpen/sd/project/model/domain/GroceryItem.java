package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.Unit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GroceryItem {
    private String name;
    private double quantity;
    private Unit unit;
    private boolean bought;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public GroceryItem(String name, double quantity, Unit unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.bought = false;
    }

    public void addQuantity(double amount) {
        this.quantity += amount;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        boolean oldValue = this.bought;
        this.bought = bought;
        pcs.firePropertyChange("bought", oldValue, this.bought);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return name + " (" + quantity + " " + unit + ")";
    }
}