# sustainer tplevko@redhat.com

@template-step
Feature: Template step

  Background: Clean application state
    Given clean application state
    And reset content of "contact" table
    And delete emails from "jbossqa.fuse@gmail.com" with subject "template-step-test"
    Given log into the Syndesis
    And created connections
      | Gmail | QE Google Mail | My GMail Connector | SyndesisQE Slack test |
    And navigate to the "Home" page

#
#  1. Send an e-mail
#
  @gmail-send-template
  Scenario: Send an e-mail with the use of template step

    # create integration
    When click on the "Create Integration" button to create a new integration.
    Then check visibility of visual integration editor
    And check that position of connection to fill is "Start"

    When select the "PostgresDB" connection
    And select "Periodic SQL Invocation" integration action
    And fill in periodic query input with "select company from contact limit(1)" value
    And fill in period input with "10" value
    And select "Minutes" from sql dropdown
    And click on the "Done" button

    Then check that position of connection to fill is "Finish"

    When select the "My GMail Connector" connection
    And select "Send Email" integration action
    And fill in values
      | Email to      | jbossqa.fuse@gmail.com |
      | Email subject | template-step-test     |
    And click on the "Done" button

    # add template step
    And click on the "Add a Step" button
    And select "Template" integration step
    And check visibility of "Template" step configuration page
    And set the template type "mustache"
    And set the template value "{{company}} sent this email"
    And click on the "Done" button

    And add integration "step" on position "0"
    And select "Data Mapper" integration step
    And open data bucket "1 - SQL Result"
    And create data mapper mappings
      | company | company |
    And click on the "Done" button

    And add integration "step" on position "2"
    And select "Data Mapper" integration step
    And open data bucket "3 - Template JSON Schema"
    And create data mapper mappings
      | message | text |
    And click on the "Done" button

    # finish and save integration
    When click on the "Save as Draft" button
    And set integration name "Integration_gmail_send_template"
    And click on the "Publish" button

    # assert integration is present in list
    Then check visibility of "Integration_gmail_send_template" integration details
    When navigate to the "Integrations" page

    Then Integration "Integration_gmail_send_template" is present in integrations list
    # wait for integration to get in active state
    And wait until integration "Integration_gmail_send_template" gets into "Running" state

    #give gmail time to receive mail
    When sleep for "10000" ms
    Then check that email from "jbossqa.fuse@gmail.com" with subject "template-step-test" and text "Red Hat sent this email" exists
