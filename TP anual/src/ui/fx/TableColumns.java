package ui.fx;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.util.function.Function;

public final class TableColumns {

    private static final String NUMERIC_STYLE_CLASS = "numeric-column";

    private TableColumns() {
    }

    public static <T> TableColumn<T, String> text(String title, Function<T, String> value) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(value.apply(data.getValue())));
        column.setSortable(false);
        return column;
    }

    public static <T> TableColumn<T, String> numeric(String title, Function<T, String> value) {
        TableColumn<T, String> column = text(title, value);
        column.getStyleClass().add(NUMERIC_STYLE_CLASS);
        column.setCellFactory(ignored -> buildNumericCell());
        return column;
    }

    private static <T> TableCell<T, String> buildNumericCell() {
        TableCell<T, String> cell = new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
            }
        };
        cell.getStyleClass().add("numeric-cell");
        return cell;
    }
}
