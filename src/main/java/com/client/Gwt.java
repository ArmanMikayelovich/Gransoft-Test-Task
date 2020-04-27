package com.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Gwt implements EntryPoint {
    private static final int MAX_NUMBER_VALUE = 1000;

    //Common fields
    private static final String INTRO_SCREEN_TEXT = "Intro screen";
    private static final String SORT_SCREEN_TEXT = "Sortscreen";
    private static final int BUTTONS_ON_ROW = 10;

    private static int idForButton = 0;

    //Common fields
    private Label introLabel = new Label();
    private int buttonCount;

    //Fields for Intro screen
    private Label firstPageQuestionLabel;
    private TextBox firstPageTextBox;
    private Button firstPageButton;
    private VerticalPanel firstPageVerticalPanel;

    //Fields for Sort screen
    private HorizontalPanel secondPageHorizontalPanel = new HorizontalPanel();
    private ArrayList<VerticalPanel> verticalPanels;
    private ButtonWithValue[] buttonArray;
    private boolean isDESC = true;
    private PopupPanel popupPanel;



    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        RootPanel.get("screenText").add(introLabel);

        initFirstPage();
    }

    private void initFirstPage() {
        changeIntroLabelText("Intro screen");
        firstPageVerticalPanel = new VerticalPanel();
        firstPageQuestionLabel = new Label("How many numbers to display?");
        firstPageTextBox = new TextBox();
        firstPageButton = new Button("Enter");
        firstPageButton.addClickHandler(event -> {
            String buttonValue = firstPageTextBox.getValue();
            firstPageTextBox.setValue("");
            if (!buttonValue.matches("\\d+") || Integer.parseInt(buttonValue) < 1) {
                Window.alert("Please type digits bigger than 1.");
                return;
            }
            this.buttonCount = Integer.parseInt(buttonValue);

            RootPanel.get("interaction").remove(firstPageVerticalPanel);
            initSecondPage(secondPageHorizontalPanel);
            RootPanel.get("interaction").add(secondPageHorizontalPanel);
        });

        firstPageVerticalPanel.add(firstPageQuestionLabel);
        firstPageVerticalPanel.add(firstPageTextBox);
        firstPageVerticalPanel.add(firstPageButton);
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
        buttonArray = createButtonsWithRandomValues();
        //separate buttons with vertical panels
        fillVerticalPanels(verticalPanels, buttonArray);

        verticalPanels.forEach(secondPageVerticalPanel::add);
        //create buttons RESET and SORT
        createButtonsForSecondPage(secondPageVerticalPanel);

        popupPanel = new PopupPanel(true);
        popupPanel.setWidget(new Label("Please select a value smaller or equal to 30."));
    }

    private void createButtonsForSecondPage(Panel secondPageVerticalPanel) {
        Button resetButton = new Button("Reset");
        resetButton.addClickHandler(event -> {
            //clean second page
            buttonArray = null;
            verticalPanels.clear();
            secondPageVerticalPanel.clear();//FIXME perhaps I can found more effective way for switching to intro page

            RootPanel.get("interaction").clear();
            changeIntroLabelText(INTRO_SCREEN_TEXT);
            RootPanel.get("interaction").add(firstPageVerticalPanel);
        });


        Button sortButton = new Button("Sort");
        sortButton.addClickHandler(event -> {
            if (isDESC) {
                quickSortDESC(buttonArray, 0, buttonArray.length - 1);
            } else {
                quickSortASC(buttonArray, 0, buttonArray.length - 1);
            }
            isDESC = !isDESC;
            fillVerticalPanels(verticalPanels, buttonArray);
        });

        VerticalPanel processButtonsPanel = new VerticalPanel();
        processButtonsPanel.add(sortButton);
        processButtonsPanel.add(resetButton);
        secondPageVerticalPanel.add(processButtonsPanel);
    }

    private ButtonWithValue[] createButtonsWithRandomValues() {
        ButtonWithValue[] buttonsWithValue = new ButtonWithValue[buttonCount];
        boolean isNeedChange = true;

        for (int offset = 0; offset < buttonCount; offset++) {
            int randomValue = Random.nextInt(MAX_NUMBER_VALUE) + 1;
            buttonsWithValue[offset] = new ButtonWithValue(randomValue);
            if (isNeedChange && randomValue <= 30) {
                isNeedChange = false;
            }
        }

        if (isNeedChange) {
            int randomOffset = Random.nextInt(buttonCount);
            buttonsWithValue[randomOffset].changeValue(Random.nextInt(30) + 1);
        }
        return buttonsWithValue;

    }

    private void fillVerticalPanels(ArrayList<VerticalPanel> verticalPanels, ButtonWithValue[] buttonArray) {
        for (VerticalPanel panel : verticalPanels) {
            panel.clear();
        }

        for (int layoutPanelOffset = 0; layoutPanelOffset < verticalPanels.size(); layoutPanelOffset++) {
            VerticalPanel widgets = verticalPanels.get(layoutPanelOffset);
            for (int buttonOffset = layoutPanelOffset * BUTTONS_ON_ROW; buttonOffset < buttonCount; buttonOffset++) {
                widgets.add(buttonArray[buttonOffset]);
                if (buttonOffset == (layoutPanelOffset * BUTTONS_ON_ROW) + BUTTONS_ON_ROW) {
                    break;
                }
            }
        }
    }

    private void quickSortASC(ButtonWithValue[] arr, int left, int right) {

        new Timer() {
            @Override
            public void run() {
                if (left < right) {
                    int partitionIndex = partitionASC(arr, left, right);

                    quickSortASC(arr, left, partitionIndex - 1);
                    quickSortASC(arr, partitionIndex + 1, right);
                }
                fillVerticalPanels(verticalPanels, buttonArray);
            }
        }.schedule(500);

    }

    private int partitionASC(ButtonWithValue[] arr, int left, int right) {
        int pivot = arr[right].getValue();
        int i = left - 1;

        for (int j = left; j < right; j++) {
            if (arr[j].getValue() <= pivot) {
                i++;

                ButtonWithValue temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        ButtonWithValue temp = arr[i + 1];
        arr[i + 1] = arr[right];
        arr[right] = temp;

        return i + 1;
    }

    public void quickSortDESC(ButtonWithValue[] arr, int left, int right) {
        if (left < right) {
            new Timer() {
                @Override
                public void run() {
                    int partitionIndex = partitionDESC(arr, left, right);
                    quickSortDESC(arr, left, partitionIndex);
                    quickSortDESC(arr, partitionIndex + 1, right);
                    fillVerticalPanels(verticalPanels, buttonArray);
                }
            }.schedule(500);


        }
    }

    public int partitionDESC(ButtonWithValue[] arr, int left, int right) {
        int pivot = arr[left].getValue();
        int i = left;
        for (int j = left + 1; j <= right; j++) {
            if (arr[j].getValue() > pivot) {
                i = i + 1;
                ButtonWithValue temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;

            }
        }

        ButtonWithValue temp = arr[i];
        arr[i] = arr[left];
        arr[left] = temp;

        return i;

    }

    private void showPopUp() {
      popupPanel.show();
    }

    private class ButtonWithValue extends Button {
        private int value;

        public ButtonWithValue(int value) {
            super("" + value);
            setId(Gwt.idForButton++);
            this.value = value;
            addClickHandler(this);
        }

        public int getValue() {
            return value;
        }

        public int getId() {
            return Integer.parseInt(this.getElement().getId());
        }

        private void setId(int id) {
            this.getElement().setId("" + id);
        }

        private void addClickHandler(ButtonWithValue button) {
            button.addClickHandler(event -> {
                if (this.value > 30) {
                    showPopUp();
                    return;
                }
                buttonArray = createButtonsWithRandomValues();
                fillVerticalPanels(verticalPanels, buttonArray);
            });
        }

        public void changeValue(int i) {
            this.value = i;
            this.setHTML(i + "");
        }
    }

}
