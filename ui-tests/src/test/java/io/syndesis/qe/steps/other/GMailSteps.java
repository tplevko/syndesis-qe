package io.syndesis.qe.steps.other;

import io.syndesis.qe.utils.GMailUtils;
import io.syndesis.qe.utils.GoogleAccount;
import io.syndesis.qe.utils.TestUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.api.services.gmail.model.Message;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.List;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GMailSteps {

    @Autowired
    @Qualifier("QE Google Mail")
    private GoogleAccount googleAccount;
    private GMailUtils gmu;

    @PostConstruct
    public void setup() {
        gmu = new GMailUtils(googleAccount);
    }

    @When("^.*send an e-mail$")
    public void sendMail() {
        sendEmail("jbossqa.fuse@gmail.com");
    }

    @When("^.*send an e-mail to \"([^\"]*)\"$")
    public void sendEmail(String sendTo) {
        sendEmail(sendTo, "syndesis-tests");
    }

    @When("^.*send an e-mail to \"([^\"]*)\" with subject \"([^\"]*)\"$")
    public void sendEmail(String sendTo, String subject) {
        gmu.sendEmail("me", sendTo, subject, "Red Hat");
    }

    @Given("^delete emails from \"([^\"]*)\" with subject \"([^\"]*)\"$")
    public void deleteMails(String from, String subject) {
        gmu.deleteMessages(from, subject);
    }

    @Then("^check that email from \"([^\"]*)\" with subject \"([^\"]*)\" and text \"([^\"]*)\" exists$")
    public void checkMails(String from, String subject, String text) {
        TestUtils.waitFor(() -> checkMailExists(from, subject, text),
            1, 10,
            "Could not find specified mail");
    }

    private boolean checkMailExists(String from, String subject, String text) {
        try {
            List<Message> messages = gmu.getMessagesMatchingQuery("me", "subject:" + subject + " AND from:" + from);
            // if messages list is empty, we haven't found anything, that's false
            if (messages.size() == 0) {
                return false;
            }

            MimeMessage mime = gmu.getMimeMessage(messages.get(0).getId());
            // now, if subject is the same and content is the same (some connectors might send trailing whitespaces)
            // then we have found our message
            if (mime.getSubject().equalsIgnoreCase(subject)
                && mime.getContent().toString().trim().equalsIgnoreCase(text.trim())) {
                return true;
            }
        } catch (IOException | MessagingException e) {
            log.debug("There has been an error while checking for existing mail", e);
        }

        return false;
    }
}
