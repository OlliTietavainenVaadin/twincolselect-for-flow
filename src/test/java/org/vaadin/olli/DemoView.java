package org.vaadin.olli;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.HashSet;
import java.util.Set;

@Route("")
public class DemoView extends VerticalLayout {

    public DemoView() {
        Set<String> strings = new HashSet<>();
        strings.add("foo");
        strings.add("bar");
        add(new Span("Constructor with values; changing selection shows notification"));
        TwinColSelect<String> tcs1 = new TwinColSelect<>(strings);
        tcs1.addSelectionListener(e -> {
            Notification.show("Selection changed: " + e.getValue());
        });
        add(tcs1);
        add(new Span("Constructor without values"));
        TwinColSelect<String> tcs2 = new TwinColSelect<>();
        tcs2.addValueChangeListener(e -> {
            Notification.show("set values to second twin col select: " + e.getValue());
        });
        add(tcs2);

        add(new Button("Click to use 'setItems' to populate second twincolselect", e -> {
            tcs2.setItems(strings);
        }));
        add(new Button("Check selected values from second twincolselect", e -> {
            add(new Span(setToString(tcs2.getSelectedItems())));
        }));
    }

    private String setToString(Set<String> set) {
        if (set == null) {
            return "<null>";
        }
        String result = "";
        for (String s : set) {
            result += s + ";";
        }
        return result;
    }
}
