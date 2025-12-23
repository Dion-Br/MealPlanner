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
}