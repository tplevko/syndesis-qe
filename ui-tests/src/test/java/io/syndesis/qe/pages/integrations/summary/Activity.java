package io.syndesis.qe.pages.integrations.summary;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import io.syndesis.qe.pages.SyndesisPageObject;

import org.openqa.selenium.By;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Activity extends SyndesisPageObject {

    private static final class Element {
        public static final By ROOT = By.className("pf-c-page__main");

        public static final By ACTIVITY_WRAPPED = By.className("list-group-item");
        public static final By ACTIVITY_EXPAND_BUTTON = By.className("fa-angle-right");

        public static final By DATE = By.className("list-group-item-heading");
        public static final By TIME = By.className("list-group-item-text");
        public static final By VERSION = By.className("list-view-pf-additional-info-item");
        public static final By ERRORS = By.className("integration-detail-activity-item__status-item");

        public static final By ONE_ROW_TABLE = By.cssSelector("table > tbody");
        public static final By ONE_CELL_IN_TABLE = By.xpath(".//td");
    }

    private static final class Button {
        public static final By REFRESH = By.cssSelector("button[data-testid=\"integration-detail-activity-refresh-button\"]");
    }

    public enum COLUMN {
        STEP(0),
        TIME(1),
        DURATION(2),
        STATUS(3),
        OUTPUT(4);

        private final int value;

        COLUMN(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }

    @Override
    public SelenideElement getRootElement() {
        return $(Element.ROOT).shouldBe(visible);
    }

    @Override
    public boolean validate() {
        return getRootElement().is(visible);
    }

    public ElementsCollection getAllActivities() {
        refresh();
        return getRootElement().findAll(Element.ACTIVITY_WRAPPED);
    }

    public SelenideElement getActivity(int indexOfActivity) {
        return this.getAllActivities().get(indexOfActivity);
    }

    public String getActivityDate(int indexOfActivity) {
        return this.getActivity(indexOfActivity).find(Element.DATE).getText();
    }

    public String getActivityVersion(int indexOfActivity) {
        return this.getActivity(indexOfActivity).find(Element.VERSION).getText();
    }

    public String getActivityTime(int indexOfActivity) {
        return this.getActivity(indexOfActivity).find(Element.TIME).getText();
    }

    public String getActivityError(int indexOfActivity) {
        return this.getActivity(indexOfActivity).find(Element.ERRORS).getText();
    }

    public void clickOnActivity(int indexOfActivity) {
        this.getActivity(indexOfActivity).find(Element.ACTIVITY_EXPAND_BUTTON).click();
    }

    public ElementsCollection getActivityLogRows(int indexOfActivity) {
        if (!isActivityDisplayed(indexOfActivity)) {
            this.clickOnActivity(indexOfActivity);
        }
        return this.getActivity(indexOfActivity).findAll(Element.ONE_ROW_TABLE);
    }

    public ElementsCollection getRowInActivityLog(int indexOfActivity, int indexOfRow) {
        if (!isActivityDisplayed(indexOfActivity)) {
            this.clickOnActivity(indexOfActivity);
        }
        return this.getActivity(indexOfActivity).findAll(Element.ONE_ROW_TABLE).get(indexOfRow)
            .findAll(Element.ONE_CELL_IN_TABLE);
    }

    public String getColumnInRowInActivityLog(int indexOfActivity, int indexOfRow, Activity.COLUMN column) {
        if (!isActivityDisplayed(indexOfActivity)) {
            this.clickOnActivity(indexOfActivity);
        }
        return this.getRowInActivityLog(indexOfActivity, indexOfRow).get(column.value).getText();
    }

    private boolean isActivityDisplayed(int indexOfActivity) {
        return this.getActivity(indexOfActivity).find(Element.ONE_CELL_IN_TABLE).isDisplayed();
    }

    private void refresh() {
        getRootElement().find(Button.REFRESH).shouldBe(visible).click();
    }
}
