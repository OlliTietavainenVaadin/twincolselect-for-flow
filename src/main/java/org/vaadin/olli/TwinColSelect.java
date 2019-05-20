package org.vaadin.olli;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.shared.Registration;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Tag("twin-column-select")
public class TwinColSelect<T> extends Composite<FlexLayout> implements MultiSelect<TwinColSelect<T>, T> {

    private Set<T> unselectedItems = new HashSet<>();
    private Set<T> selectedItems = new HashSet<>();
    private ListDataProvider<T> unselectedDP;
    private ListDataProvider<T> selectedDP;
    private Set<ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<TwinColSelect<T>, Set<T>>>> valueChangeListeners = new HashSet<>();

    protected ListBox<T> unselectedListBox;
    protected ListBox<T> selectedListBox;
    protected VerticalLayout buttonsLayout;
    protected Button selectAllButton;
    protected Button selectOneButton;
    protected Button deselectAllButton;
    protected Button deselectOneButton;

    protected static final String SELECT_ALL = ">>";
    protected static final String SELECT_ONE = ">";
    protected static final String DESELECT_ALL = "<<";
    protected static final String DESELECT_ONE = "<";

    public TwinColSelect() {
        unselectedListBox = new ListBox<>();
        unselectedListBox.getElement().getStyle().set("width", "40%");
        unselectedListBox.getElement().getStyle().set("flex-shrink", "0");
        selectedListBox = new ListBox<>();
        selectedListBox.getElement().getStyle().set("width", "40%");
        selectedListBox.getElement().getStyle().set("flex-shrink", "0");
        buttonsLayout = new VerticalLayout();
        buttonsLayout.setWidth("20%");
        selectAllButton = new Button(SELECT_ALL, this::selectAll);
        selectOneButton = new Button(SELECT_ONE, this::selectOne);
        deselectOneButton = new Button(DESELECT_ONE, this::deselectOne);
        deselectAllButton = new Button(DESELECT_ALL, this::deselectAll);
        setButtonsEnabled(false);
        buttonsLayout.add(selectAllButton, selectOneButton, deselectOneButton, deselectAllButton);
        buttonsLayout.setWidth("-1");
        getContent().add(unselectedListBox, buttonsLayout, selectedListBox);
    }

    protected void setButtonsEnabled(boolean enabled) {
        selectAllButton.setEnabled(enabled);
        selectOneButton.setEnabled(enabled);
        deselectAllButton.setEnabled(enabled);
        deselectOneButton.setEnabled(enabled);
    }

    public void setRenderer(ComponentRenderer<? extends Component, T> itemRenderer) {
        unselectedListBox.setRenderer(itemRenderer);
        selectedListBox.setRenderer(itemRenderer);
    }

    public TwinColSelect(Set<T> items) {
        this();
        setItems(items);
    }

    public void setItems(Set<T> items) {
        setButtonsEnabled(true);
        unselectedItems = new HashSet<>(items);
        unselectedDP = DataProvider.ofCollection(unselectedItems);
        unselectedListBox.setDataProvider(unselectedDP);
        Set<T> old = null;
        if (selectedItems != null) {
            old = new HashSet<>(selectedItems);
        }
        selectedItems = new HashSet<>();
        selectedDP = DataProvider.ofCollection(selectedItems);
        selectedListBox.setDataProvider(selectedDP);
        fireEvent(new AbstractField.ComponentValueChangeEvent<>(this, this, old, false));
    }

    @Override
    public void updateSelection(Set<T> addedItems, Set<T> removedItems) {
        if (addedItems == null) {
            addedItems = new HashSet<>();
        }
        if (removedItems == null) {
            removedItems = new HashSet<>();
        }
        // remove items from "addedItems" that are not in the unselected list
        addedItems = addedItems.stream().filter(item -> unselectedItems.contains(item)).collect(Collectors.toSet());
        // remove items from "removedItems" that are not in the selected list
        removedItems = removedItems.stream().filter(item -> selectedItems.contains(item)).collect(Collectors.toSet());
        // disregard all the items that are in both sets
        Set<T> intersection = new HashSet<>(addedItems);
        intersection.retainAll(removedItems);
        if (!intersection.isEmpty()) {
            addedItems.removeAll(intersection);
            removedItems.removeAll(intersection);
        }
        // select all "addedItems" and deselect all "removedItems"
        selectedItems.addAll(addedItems);
        unselectedItems.removeAll(addedItems);
        unselectedItems.addAll(removedItems);
        selectedItems.removeAll(removedItems);
        unselectedDP.refreshAll();
        selectedDP.refreshAll();

    }

    @Override
    public Set<T> getSelectedItems() {
        return new HashSet<>(selectedItems);
    }

    @Override
    public Registration addSelectionListener(MultiSelectionListener<TwinColSelect<T>, T> listener) {
        ComponentEventListener componentListener = event -> {
            listener.selectionChange(new MultiSelectionEvent<>(this, this, unselectedItems, false));
        };
        return ComponentUtil.addListener(this,
                MultiSelectionEvent.class, componentListener);
    }

    @Override
    public Registration addValueChangeListener(
            ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<TwinColSelect<T>, Set<T>>> valueChangeListener) {
        valueChangeListeners.add(valueChangeListener);
        return (Registration) () -> valueChangeListeners.remove(valueChangeListener);
    }

    private void deselectAll(ClickEvent<Button> e) {
        unselectedItems.addAll(selectedItems);
        selectedItems.clear();
        unselectedDP.refreshAll();
        selectedDP.refreshAll();
        fireEvent(new MultiSelectionEvent<>(this, this, selectedItems, true));
    }

    private void deselectOne(ClickEvent<Button> e) {
        singleSelection(selectedListBox, selectedItems, unselectedItems);
    }

    private void singleSelection(ListBox<T> sourceListBox, Set<T> from, Set<T> to) {
        T value = sourceListBox.getValue();
        if (value == null) {
            return;
        }
        from.remove(value);
        to.add(value);
        unselectedDP.refreshAll();
        selectedDP.refreshAll();
        fireEvent(new MultiSelectionEvent<>(this, this, from, true));
    }

    private void selectOne(ClickEvent<Button> e) {
        singleSelection(unselectedListBox, unselectedItems, selectedItems);
    }

    private void selectAll(ClickEvent<Button> e) {
        selectedItems.addAll(unselectedItems);
        unselectedItems.clear();
        unselectedDP.refreshAll();
        selectedDP.refreshAll();
        fireEvent(new MultiSelectionEvent<>(this, this, selectedItems, true));
    }
}
