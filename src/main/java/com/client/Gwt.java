package com.client;

import com.google.gwt.core.client.EntryPoint;
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
    private static final String INTERACTION_DIV = "interaction";
    private static final String INTRO_SCREEN_TEXT = "Intro screen";
    private static final String SORT_SCREEN_TEXT = "Sortscreen";
    private static final int BUTTONS_ON_ROW = 10;

    //Common fields
    private Label introLabel = new Label();
    private int buttonCount;

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

    /**
     * Initialize Intro screen.
     */
    private void initFirstPage() {
        changeIntroLabelText(INTRO_SCREEN_TEXT);
        firstPageVerticalPanel = new VerticalPanel();
        //Fields for Intro screen
        Label firstPageQuestionLabel = new Label("How many numbers to display?");
        TextBox firstPageTextBox = new TextBox();
        Button firstPageButton = new Button("Enter");
        firstPageButton.addClickHandler(event -> {
            String buttonValue = firstPageTextBox.getValue();
            firstPageTextBox.setValue("");
            if (!buttonValue.matches("\\d+") || Integer.parseInt(buttonValue) < 1) {
                Window.alert("Please type number bigger than 1.");
                return;
            }
            this.buttonCount = Integer.parseInt(buttonValue);

            RootPanel.get(INTERACTION_DIV).remove(firstPageVerticalPanel);
            initSecondPage(secondPageHorizontalPanel);
            RootPanel.get(INTERACTION_DIV).add(secondPageHorizontalPanel);
        });

        firstPageVerticalPanel.add(firstPageQuestionLabel);
        firstPageVerticalPanel.add(firstPageTextBox);
        firstPageVerticalPanel.add(firstPageButton);
        RootPanel.get(INTERACTION_DIV).add(firstPageVerticalPanel);
    }

    /**
     * Changes the text at the top of the page
     * @param text new text for label
     */
    private void changeIntroLabelText(String text) {
        introLabel.setText(text);
    }

    /**
     * Initializes second screen:
     *  - creates vertical panels and fill buttons with random values.
     *   - creates action button for second page (Sort, Reset).
     *   - creates PopupPanel for error message.
     * @param secondPageVerticalPanel empty panel for filling.
     */
    private void initSecondPage(Panel secondPageVerticalPanel) {
        // add introduce text to second page
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

    /**
     *  Create actions buttons( Sort, Reset).
     *   - Sort - The first hit starts sorting in descending order, the next changes sorting in ascending order.
     *   - Reset - return to intro screen.
     * @param secondPageVerticalPanel initialized panel for adding actions buttons.
     */
    private void createButtonsForSecondPage(Panel secondPageVerticalPanel) {
        Button resetButton = new Button("Reset");
        resetButton.addClickHandler(event -> {
            //clean second page
            buttonArray = null;
            verticalPanels.clear();
            secondPageVerticalPanel.clear();

            RootPanel.get(INTERACTION_DIV).clear();
            changeIntroLabelText(INTRO_SCREEN_TEXT);
            RootPanel.get(INTERACTION_DIV).add(firstPageVerticalPanel);
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

        VerticalPanel actionButtonsPanel = new VerticalPanel();
        actionButtonsPanel.add(sortButton);
        actionButtonsPanel.add(resetButton);
        secondPageVerticalPanel.add(actionButtonsPanel);
    }

    /**
     * Create buttons with random numbers with the criteria:
     *  - The max number value is 1000
     * - At least one value should be equal or less than 30
     * @return Button array with X length and filled with random numbers
     */
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

    /**
     * Fills the panels with buttons, 10 in each panel.
     * @param verticalPanels Panels to fill.
     * @param buttonArray Array with buttons.
     */
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

    /**
     * This class extends Button because it need value field in number format for sorting with quick sort.
     */
    private class ButtonWithValue extends Button {
        private int value;

        /**
         * Set value and Html text on Button
         * Adds click handler to button, which meets the following condition:
         *  - If the clicked value is more than 30, pop up a message “Please select a value smaller or equal to 30.
         *  - If the clicked value is equal or less than 30, present X new random numbers on the screen.
         * @param value set value of Button
         */
        public ButtonWithValue(int value) {
            super("" + value);
            this.value = value;

            this.addClickHandler(event -> {
                if (this.value > 30) {
                    popupPanel.show();
                } else {
                    buttonArray = createButtonsWithRandomValues();
                    fillVerticalPanels(verticalPanels, buttonArray);
                }
            });
        }

        public int getValue() {
            return value;
        }

        public void changeValue(int i) {
            this.value = i;
            this.setHTML(i + "");
        }
    }

}
