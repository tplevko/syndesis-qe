package io.syndesis.qe.pages.integrations.editor.add.steps;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import io.syndesis.qe.pages.integrations.editor.add.steps.getridof.AbstractStep;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Template extends AbstractStep {

    private String templateStepDef;

    public Template(String templateStepDef) {
        super();
        this.templateStepDef = templateStepDef;
    }

    private static final class Textarea {
        public static final By TEMPLATE = By.cssSelector("div[class='CodeMirror cm-s-default CodeMirror-wrap']");
    }

    public String getTemplateStepDef() {
        return templateStepDef;
    }

    public void setTemplateStepDef(String templateStepDef) {
        this.templateStepDef = templateStepDef;
    }

    public void fillConfiguration() {
        String template = getTemplateStepDef();
        this.setTemplate(template);
    }

    public boolean validate() {
        log.debug("Validating advanced filter configure page");
        return this.getTemplateTextarea().waitWhile(Condition.not(visible), 5 * 1000).isDisplayed();
    }

    public void initialize() {
        String template = this.getTemplateTextareaValue();
        this.setParameter(template);
    }

    public void setTemplateType(String templateType) {
        log.info("setting template type");
        $(By.id(templateType + "-choice")).shouldBe(visible).setSelected(true);
    }

    public void uploadTemplateFromFile(String fileName) {
    }

    public void setTemplate(String template) {
        log.info("Setting integration step template to {}", template);

        WebDriver driver = WebDriverRunner.getWebDriver();
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        WebElement queryInput = driver.findElement(Template.Textarea.TEMPLATE);

        jse.executeScript("arguments[0].CodeMirror.setValue(\"" + template + "\");", queryInput);

        SelenideElement yourButton = $(By.cssSelector("button[class='btn btn-primary']"));

        jse.executeScript("arguments[0].removeAttribute('disabled','disabled')", yourButton);

        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.elementToBeClickable(yourButton));
    }

    public void setParameter(String templateData) {
        setTemplateStepDef(templateData);
    }

    public SelenideElement getTemplateTextarea() {
        log.debug("Searching for template text area");
        return this.getRootElement().find(Template.Textarea.TEMPLATE);
    }

    public String getTemplateTextareaValue() {
        return this.getTemplateTextarea().shouldBe(visible).getText();
    }

    public String getParameter() {
        return this.templateStepDef;
    }
}
