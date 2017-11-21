package io.syndesis.qe.steps.connections;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;

import com.codeborne.selenide.SelenideElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.syndesis.qe.pages.connections.edit.ConnectionConfigurationComponent;
import io.syndesis.qe.pages.connections.edit.ConnectionCreatePage;
import io.syndesis.qe.pages.connections.detail.ConnectionDetailPage;
import io.syndesis.qe.pages.connections.edit.ConnectionsDetailsComponent;
import io.syndesis.qe.pages.connections.list.ConnectionsListComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by sveres on 11/10/17.
 */
@Slf4j
public class ConnectionSteps {

	private ConnectionConfigurationComponent connectionConfiguration = new ConnectionConfigurationComponent();
	private ConnectionDetailPage detailPage = new ConnectionDetailPage();
	private ConnectionsListComponent listComponent = new ConnectionsListComponent();
	private ConnectionsDetailsComponent connectionDetails = new ConnectionsDetailsComponent();

	@Then("^Camilla is presented with \"(\\w+)\" connection details")
	public void verifyConnectionDetails(String connectionName) {
		log.info("Connection detail page must show connection name");
		assertThat(detailPage.connectionName(), is(connectionName));
	}

	@Then("/^Camilla can see \"(\\w+)\" connection")
	public void expectConnectionTitlePresent(String connectionName) {
		SelenideElement connection = listComponent.getConnectionByTitle(connectionName).shouldBe(visible);
	}

	@Then("^Camilla can not see \"(\\w+)\" connection anymore")
	public void expectConnectionTitleNonPresent(String connectionName) {
		SelenideElement connection = listComponent.getConnectionByTitle(connectionName).shouldBe(not(exist));
	}

	@Then("^she is presented with a connection create page")
	public void editorOpened() {
		ConnectionCreatePage connPage = new ConnectionCreatePage();
		connPage.getRootElement();
	}

	@When("^Camilla deletes the \"(\\w+)\" connection")
	public void deleteConnection(String connectionName) {
		listComponent.deleteConnection(connectionName);
	}

	@When("^Camilla selects the \"(\\w+)\" connection.*")
	public void selectConnection(String connectionName) {
		listComponent.goToConnection(connectionName);
	}

	@When("^type \"(\\w+)\" into connection name")
	public void typeConnectionName(String name) {
		connectionDetails.getInputName().shouldBe(visible).sendKeys(name);
	}

	@When("^type \"(\\w+)\" into connection description")
	public void typeConnectionDescription(String description) {
		connectionDetails.getDescription().shouldBe(visible).sendKeys(description);
	}

	@When("^she fills \"(\\w+)\" connection details")
	public void fillConnectionDetails(String connectionName) throws Exception {
		//TODO(dsimansk) method for retrieving map of connection credentials.
		connectionConfiguration.fillDetails(this.world.getTestConfigConnection(connectionName));
	}

	//Kebab menu test, #553 -> part #550.
	@When("^clicks on the kebab menu icon of each available connection")
	public void clickOnAllKebabMenus() throws Exception {
		listComponent.clickOnAllKebabButtons();
	}

	@Then("^she is presented with at least \"(\\d+)\" connections")
	public void connectionCount(Integer connectionCount) {
		log.info("There should be {} available", connectionCount);
		assertThat(listComponent.countConnections(), is(connectionCount));
	}

	//Kebab menu test, #553 -> part #550.
	@Then("^she can see unveiled kebab menu of all connections, each of this menu consist of \"(\\w+)\", \"(\\w+)\" and \"(\\w+)\" actions$")
	public void checkAllVisibleKebabMenus(String action1, String action2, String action3) {
		List<String> actions = new ArrayList<>(Arrays.asList(action1, action2, action3));
		listComponent.checkAllKebabElementsAreDisplayed(true, actions);
	}
}