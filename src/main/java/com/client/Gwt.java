package com.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Gwt implements EntryPoint {
    private static final String INTRO_SCREEN_TEXT = "Intro screen";
    private static final String SORT_SCREEN_TEXT = "Sortscreen";
    private Label introLabel = new Label();
    private static int idForButton = 0;
    private static final int BUTTONS_ON_ROW = 10;
    private int buttonCount;

    private Label label = new Label("How many numbers to display?");
    private TextBox textBox = new TextBox();
    private Button button = new Button("Enter");
    private VerticalPanel firstPageVerticalPanel = new VerticalPanel();

    private HorizontalPanel secondPageHorizontalPanel = new HorizontalPanel();
    ArrayList<VerticalPanel> verticalPanels;
    ArrayList<Button> buttonList;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        RootPanel.get("screenText").add(introLabel);

        initFirstPage();
    }

    private void initFirstPage() {
        changeIntroLabelText("Intro screen");

        button.addClickHandler(event -> {
            String buttonValue = textBox.getValue();
            textBox.setValue("");
            if (!buttonValue.matches("\\d+") || Integer.parseInt(buttonValue) < 1) {
                Window.alert("Please type digits bigger than 1.");
                return;
            }
            this.buttonCount = Integer.parseInt(buttonValue);

            RootPanel.get("interaction").remove(firstPageVerticalPanel);
            initSecondPage(secondPageHorizontalPanel);
            RootPanel.get("interaction").add(secondPageHorizontalPanel);
        });


        firstPageVerticalPanel.add(label);
        firstPageVerticalPanel.add(textBox);
        firstPageVerticalPanel.add(button);
        RootPanel.get("interaction").add(firstPageVerticalPanel);
    }

    private void changeIntroLabelText(String text) {
        introLabel.setText(text);
    }


    private void initSecondPage(Panel secondPageVerticalPanel) {
        // add introducer text to second page
        changeIntroLabelText(SORT_SCREEN_TEXT);

        int layoutPanelCount;
        if (buttonCount % 10 > 0) {
            layoutPanelCount = (buttonCount / 10) + 1;
        } else {
            layoutPanelCount = buttonCount / 10;
        }
        //create vertical panels for buttons
        verticalPanels = new ArrayList<>();
        for (int x = 0; x < layoutPanelCount; x++) {
            verticalPanels.add(new VerticalPanel());
        }
        //create buttons with their own id and number
        buttonList = createButtonsWithRandomNumbers();
        //separate buttons with vertical panels
        fillVerticalPanels(verticalPanels, buttonList);

        verticalPanels.forEach(secondPageVerticalPanel::add);

        //create buttons RESET and SORT
        createButtonsForSecondPage(secondPageVerticalPanel);
    }

    private void createButtonsForSecondPage(Panel secondPageVerticalPanel) {
        Button resetButton = new Button("Reset");
        resetButton.addClickHandler(event -> {
            //clean second page
            buttonList.clear();
            verticalPanels.clear();
            secondPageVerticalPanel.clear();//FIXME perhaps I can found more effective way for switching to intro page

            RootPanel.get("interaction").clear();
            changeIntroLabelText(INTRO_SCREEN_TEXT);
            RootPanel.get("interaction").add(firstPageVerticalPanel);
        });


        Button sortButton = new Button("Sort");

        VerticalPanel processButtonsPanel = new VerticalPanel();
        processButtonsPanel.add(sortButton);
        processButtonsPanel.add(resetButton);
        secondPageVerticalPanel.add(processButtonsPanel);
    }

    private ArrayList<Button> createButtonsWithRandomNumbers() {
        ArrayList<Button> buttonList = new ArrayList<>();
        for (int x = 0; x < buttonCount; x++) {
            //FIXME at least one should be less or equals 30
            buttonList.add(new ButtonWithNumber(1 + Random.nextInt(1000)));
        }
        return buttonList;
    }

    private void fillVerticalPanels(ArrayList<VerticalPanel> verticalPanels, ArrayList<Button> buttonList) {
        for (VerticalPanel panel : verticalPanels) {
            panel.clear();
        }

        for (int layoutPanelOffset = 0; layoutPanelOffset < verticalPanels.size(); layoutPanelOffset++) {
            VerticalPanel widgets = verticalPanels.get(layoutPanelOffset);
            for (int buttonOffset = layoutPanelOffset * BUTTONS_ON_ROW; buttonOffset < buttonCount; buttonOffset++) {
                widgets.add(buttonList.get(buttonOffset));
                if (buttonOffset == (layoutPanelOffset * BUTTONS_ON_ROW) + BUTTONS_ON_ROW) {
                    break;
                }
            }
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
                buttonList = createButtonsWithRandomNumbers();
                fillVerticalPanels(verticalPanels, buttonList);
            });
        }
    }
}
