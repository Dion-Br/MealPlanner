package be.uantwerpen.sd.project.model.domain.enums;

public enum Unit {

    GRAM("g", null, 1.0),
    KILOGRAM("kg", GRAM, 1000.0),
    MILLILITER("ml", null, 1.0),
    LITER("l", MILLILITER, 1000.0),
    TABLESPOON("tbsp", MILLILITER, 15.0),
    PIECE("pc", null, 1.0);

    private final String label;
    private final Unit baseUnit;
    private final double conversionFactor;

    Unit(String label, Unit baseUnit, double conversionFactor) {
        this.label = label;
        this.baseUnit = baseUnit;
        this.conversionFactor = conversionFactor;
    }

    @Override
    public String toString() {
        return label;
    }

    public Unit getBaseUnit() {
        return baseUnit != null ? baseUnit : this;
    }

    public double toBaseQuantity(double quantity) {
        return quantity * conversionFactor;
    }

    public String getLabel() {
        return label;
    }
}