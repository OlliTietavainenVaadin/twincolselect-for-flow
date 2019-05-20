package org.vaadin.olli;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;

import java.util.HashSet;
import java.util.Set;

@Route("")
public class DemoView extends VerticalLayout {

    public DemoView() {
        basicTests();
        typedTests();
    }

    private void typedTests() {
        Set<Cake> cakes = new HashSet<>();
        Cake firstCake = new Cake();
        firstCake.setFlavor("Lemon");
        firstCake.setLayers(3);
        Cake secondCake = new Cake();
        secondCake.setFlavor("Chocolate");
        secondCake.setLayers(5);
        cakes.add(firstCake);
        cakes.add(secondCake);
        TwinColSelect<Cake> cakeSelector = new TwinColSelect<>();
        cakeSelector.setRenderer(
                new TextRenderer<>(cake -> cake.getFlavor() + String.format(" (%s layers)", cake.getLayers())));
        cakeSelector.setItems(cakes);
        add(new Span("Typed selector:"), cakeSelector);

    }

    private void basicTests() {
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

    public static class Cake {
        private int layers;
        private String flavor;

        public int getLayers() {
            return layers;
        }

        public void setLayers(int layers) {
            this.layers = layers;
        }

        public String getFlavor() {
            return flavor;
        }

        public void setFlavor(String flavor) {
            this.flavor = flavor;
        }


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
