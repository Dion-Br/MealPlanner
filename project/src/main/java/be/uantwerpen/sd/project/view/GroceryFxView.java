package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.model.domain.GroceryItem;
import be.uantwerpen.sd.project.model.domain.GroceryListGenerator;
import be.uantwerpen.sd.project.model.domain.enums.Unit;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class GroceryFxView extends VBox {

    private final GroceryListGenerator generator;
    private final TextField nameField;
    private final TextField quantityField;
    private final ComboBox<Unit> unitComboBox;

    public GroceryFxView(GroceryListGenerator generator) {
        this.generator = generator;
        this.nameField = new TextField();
        this.quantityField = new TextField();
        this.unitComboBox = new ComboBox<>(FXCollections.observableArrayList(Unit.values()));

        initializeLayout();
    }

    private void initializeLayout() {
        setPadding(new Insets(10));
        setSpacing(10);

        Label title = createTitle();
        ListView<GroceryItem> listView = createGroceryListView();
        HBox inputBox = createInputBox();

        getChildren().addAll(title, listView, inputBox);
    }

    private Label createTitle() {
        Label title = new Label("Grocery List");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        return title;
    }

    private ListView<GroceryItem> createGroceryListView() {
        ListView<GroceryItem> listView = new ListView<>();
        listView.setItems(generator.getItems());
        listView.setCellFactory(CheckBoxListCell.forListView(
                this::createBooleanProperty,
                createGroceryItemConverter()
        ));
        VBox.setVgrow(listView, Priority.ALWAYS);
        return listView;
    }

    private javafx.beans.value.ObservableValue<Boolean> createBooleanProperty(GroceryItem item) {
        try {
            return JavaBeanBooleanPropertyBuilder.create()
                    .bean(item)
                    .name("bought")
                    .build();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private StringConverter<GroceryItem> createGroceryItemConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(GroceryItem item) {
                if (item == null) return null;
                return String.format("%s (%.2f %s)", item.getName(), item.getQuantity(), item.getUnit());
            }

            @Override
            public GroceryItem fromString(String string) {
                return null;
            }
        };
    }

    private HBox createInputBox() {
        nameField.setPromptText("Item Name");
        quantityField.setPromptText("Qty");
        quantityField.setPrefWidth(60);
        unitComboBox.getSelectionModel().selectFirst();

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> handleAddItem());

        return new HBox(5, nameField, quantityField, unitComboBox, addButton);
    }

    private void handleAddItem() {
        try {
            String name = nameField.getText();
            if (name.isBlank()) return;

            double quantity = Double.parseDouble(quantityField.getText());
            Unit unit = unitComboBox.getValue();

            generator.addManualItem(name, quantity, unit);
            clearInputFields();
        } catch (NumberFormatException e) {
            // Invalid number input
        }
    }

    private void clearInputFields() {
        nameField.clear();
        quantityField.clear();
    }
}