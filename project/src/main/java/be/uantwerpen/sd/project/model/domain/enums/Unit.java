package be.uantwerpen.sd.project.model.domain.enums;

public enum Unit {
    GRAM("g"),
    KILOGRAM("kg"),
    LITER("l"),
    MILLILITER("ml"),
    TABLESPOON("tbsp"),
    PIECE("pc");

    private final String label;

    Unit(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public Unit getBaseUnit() {
        return switch (this) {
            case KILOGRAM -> GRAM;
            case LITER, TABLESPOON -> MILLILITER;
            default -> this; // GRAM, MILLILITER, PIECE stay as they are
        };
    }

    public double toBaseQuantity(double quantity) {
        return switch (this) {
            case KILOGRAM, LITER -> quantity * 1000;
            case TABLESPOON -> quantity * 15; // Approximation: 1 tbsp = 15 ml
            default -> quantity;
        };
    }
}