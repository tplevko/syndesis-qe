package io.syndesis.qe.steps.integrations.template;

import cucumber.api.java.en.And;
import io.syndesis.qe.pages.integrations.editor.add.steps.Template;

public class TemplateSteps {

    private Template template = new Template("");

    @And("^set the template type \"([^\"]*)\"$")
    public void setTemplateType(String option) {
        template.setTemplateType(option);
    }

    @And("^set the template value \"([^\"]*)\"$")
    public void insertTemplateIntoEditor(String option) {
        template.setTemplate(option);
    }

    @And("^upload the template from file \"([^\"]*)\"$")
    public void uploadTemplate(String file) {
        //TODO
    }
}
