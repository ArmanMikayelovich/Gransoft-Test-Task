package com.client;

import com.gargoylesoftware.htmlunit.javascript.host.Console;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Gwt implements EntryPoint {
    private static int idForButton = 0;
    private static final int BUTTONS_ON_ROW = 10;
    private int buttonCount;
    private Label label = new Label("How many numbers to display?");
    private TextBox textBox = new TextBox();
    private Button button = new Button("Enter");
    private VerticalPanel firstPageVerticalPanel = new VerticalPanel();

    private HorizontalPanel secondPageVerticalPanel = new HorizontalPanel();

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        addClickHandlerToButton(button);
        firstPageVerticalPanel.add(label);
        firstPageVerticalPanel.add(textBox);
        firstPageVerticalPanel.add(button);
        RootPanel.get("interaction").add(firstPageVerticalPanel);

    }

    private void addClickHandlerToButton(Button button) {
        button.addClickHandler(event -> {
            String buttonValue = textBox.getValue();

            if (!buttonValue.matches("\\d+")) {
                Window.alert("Please type only digits.");
                return;
            }
            this.buttonCount = Integer.parseInt(buttonValue);

            RootPanel.get("interaction").remove(firstPageVerticalPanel);
            Window.alert(secondPageVerticalPanel.toString());
            buildSecondPage(secondPageVerticalPanel);
            Window.alert(secondPageVerticalPanel.toString());
            RootPanel.get("interaction").add(secondPageVerticalPanel);
        });
    }

    private void buildSecondPage(Panel secondPageVerticalPanel) {
        int layoutPanelCount;
        if (buttonCount % 10 > 0) {
            layoutPanelCount = (buttonCount / 10) + 1;
        } else {
            layoutPanelCount = buttonCount / 10;
        }
        ArrayList<VerticalPanel> layoutPanels = new ArrayList<>();
        for (int x = 0; x < layoutPanelCount; x++) {
            layoutPanels.add(new VerticalPanel());
        }
        ArrayList<Button> buttonList = new ArrayList<>();
        for (int x = 0; x < buttonCount; x++) {
            //FIXME at least one should be less or equals 30
            buttonList.add(new ButtonWithNumber(1 + Random.nextInt(1000)));
        }

        //separate buttons with vertical panels
        for (int layoutPanelOffset = 0; layoutPanelOffset < layoutPanels.size(); layoutPanelOffset++) {
            VerticalPanel widgets = layoutPanels.get(layoutPanelOffset);
            for (int buttonOffset = layoutPanelOffset * BUTTONS_ON_ROW; buttonOffset < buttonCount; buttonOffset++) {
                widgets.add(buttonList.get(buttonOffset));
//                if (buttonOffset >= 10 && buttonOffset % 10 == 0) {
//                    break;
//                }
                if (buttonOffset == (layoutPanelOffset * BUTTONS_ON_ROW) + BUTTONS_ON_ROW) {
                    break;
                }
            }
            secondPageVerticalPanel.add(widgets);
        }



    }

    private class ButtonWithNumber extends Button {
        private int number;

        public ButtonWithNumber(int number) {
            super("" + number);
            setId(Gwt.idForButton++);
            this.number = number;
            addClickHandler(this);
        }

        public int getId() {
            return Integer.parseInt(this.getElement().getId());
        }

        private void setId(int id) {
            this.getElement().setId("" + id);
        }

        private void addClickHandler(ButtonWithNumber button) {
            button.addClickHandler(event -> {
                if (this.number > 30) {
                    Window.alert("Please select a value smaller or equal to 30.");
                    return;
                }
                buildSecondPage(secondPageVerticalPanel);
            });
        }
    }
}
