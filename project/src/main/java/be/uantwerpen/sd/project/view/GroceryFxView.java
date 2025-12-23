package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.model.domain.enums.Unit;
import be.uantwerpen.sd.project.model.domain.GroceryItem;
import be.uantwerpen.sd.project.model.domain.GroceryListGenerator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GroceryFxView extends VBox {
    private final GroceryListGenerator generator;

    public GroceryFxView(GroceryListGenerator generator) {
        this.generator = generator;
        setPadding(new Insets(10));
        setSpacing(10);

        Label title = new Label("Grocery List");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // The List View
        ListView<GroceryItem> listView = new ListView<>();
        listView.setItems(generator.getItems());

        // Custom Cell Factory to show Checkbox + Text
        listView.setCellFactory(param -> new CheckBoxListCell<>(GroceryItem::boughtProperty) {
            @Override
            public void updateItem(GroceryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(String.format("%s (%.2f %s)", item.getName(), item.getQuantity(), item.getUnit()));
                } else {
                    setText(null);
                }
            }
        });
        VBox.setVgrow(listView, Priority.ALWAYS);

        // Manual Add Section
        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");
        TextField qtyField = new TextField();
        qtyField.setPromptText("Qty");
        qtyField.setPrefWidth(60);
        ComboBox<Unit> unitCombo = new ComboBox<>(FXCollections.observableArrayList(Unit.values()));
        unitCombo.getSelectionModel().selectFirst();

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> {
            try {
                String name = nameField.getText();
                double qty = Double.parseDouble(qtyField.getText());
                Unit unit = unitCombo.getValue();

                if (!name.isBlank()) {
                    generator.addManualItem(name, qty, unit);
                    nameField.clear();
                    qtyField.clear();
                }
            } catch (NumberFormatException ex) {
                // Ignore invalid numbers
            }
        });

        HBox inputBox = new HBox(5, nameField, qtyField, unitCombo, addBtn);

        getChildren().addAll(title, listView, inputBox);
    }
}