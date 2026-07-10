package com.github.argon.sos.mod.sdk.ui.util;

import com.github.argon.sos.mod.sdk.game.action.Valuable;
import com.github.argon.sos.mod.sdk.ui.button.Button;
import com.github.argon.sos.mod.sdk.ui.table.ColumnRow;
import com.github.argon.sos.mod.sdk.ui.table.Table;
import init.sprite.SPRITES;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A list of ui elements where additional elements can be added or removed.
 *
 * @param <Value> type of the value contained in the ui elements
 * @param <Element> type of te ui element
 */
@Getter
public class UiList<Value, Element extends RENDEROBJ> extends Section implements Valuable<List<Value>> {
    private final Table<Value> table;
    private final Button<Value> addButton;
    private final Function<@Nullable Value, Element> elementSupplier;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Consumer<List<Value>> valueChangeAction = o -> {};

    @Getter
    private final List<Element> elements;

    /**
     * Creates a new {@link UiList}.
     *
     * @param elements to add to the list
     * @param height available height
     * @param elementSupplier for creating and adding new elements
     */
    public UiList(List<Element> elements, int height, Function<@Nullable Value, Element> elementSupplier) {
        this.elements = elements;
        List<ColumnRow<Value>> rows = elements.stream()
            .map(this::listEntry)
            .toList();

        this.elementSupplier = elementSupplier;
        this.addButton = new Button<>(SPRITES.icons().m.plus);
        this.table = Table.<Value>builder()
            .displayHeight(height - addButton.body().height() - 10)
            .rows(rows)
            .evenOdd(false)
            .scrollable(true)
            .build();
        this.addButton.body().setWidth(table.body().width());
        this.addButton.bg(COLOR.WHITE10);
        this.addButton.hoverInfoSet("Add new");

        this.addButton.clickActionSet(() -> {
            Element element = elementSupplier.apply(null);

            if (element == null) {
                return;
            }

            ColumnRow<Value> row = listEntry(element);
            addRow(row);
            elements.add(element);
            valueChangeAction.accept(getValue());
        });

        addDownC(0, this.addButton);
        addDownC(10, this.table);
    }

    /**
     * Returns a list of values from all ui elements in the list.
     *
     * @return list of values from all ui elements in the list
     */
    @Override
    public List<Value> getValue() {
        Map<String, @Nullable Value> values = table.getValue();

        if (values == null) {
            return List.of();
        }

        return values.values().stream()
            .toList();
    }

    /**
     * Sets values to all ui elements in the list.
     *
     * @param values to set
     */
    @Override
    public void setValue(List<Value> values) {
        table.clearRows();
        values.stream()
            .map(elementSupplier)
            .filter(Objects::nonNull)
            .peek(elements::add)
            .map(this::listEntry)
            .forEach(this::addRow);
        valueChangeAction.accept(values);
    }

    private void addRow(ColumnRow<Value> row) {
        table.addRow(row);
    }

    private void removeRow(ColumnRow<Value> row) {
        table.removeRow(row);
    }

    private ColumnRow<Value> listEntry(Element element) {
        Supplier<Value> valueSupplier = null;
        if (element instanceof Valuable) {
            valueSupplier = () -> {
                try {
                    //noinspection unchecked
                    return ((Valuable<Value>) element).getValue();
                } catch (Exception e) {
                    return null;
                }
            };
        }

        Button<String> deleteButton = new Button<>(SPRITES.icons().m.trash);
        ColumnRow<Value> columnRow = ColumnRow.<Value>builder()
            .valueSupplier(valueSupplier)
            .column(element)
            .column(deleteButton)
            .build();

        deleteButton.clickActionSet(() -> {
            removeRow(columnRow);
            valueChangeAction.accept(getValue());
        });

        columnRow.init();
        return columnRow;
    }
}
