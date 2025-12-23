package be.uantwerpen.sd.project.model.domain;

public class GroceryItem {
    private String name;
    private double quantity;
    private boolean bought;

    public GroceryItem(String name, double quantity) {
        this.name = name;
        this.quantity = quantity;
        this.bought = false;
    }

    public void addQuantity(double amount) {
        this.quantity += amount;
    }

    // Getters and Setters
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public boolean isBought() { return bought; }
    public void setBought(boolean bought) { this.bought = bought; }

    @Override
    public String toString() {
        return name + " (" + quantity + ")";
    }
}