# @sustainer: mmelko@redhat.com
@ui
@google-sheets
@database
@datamapper
@integrations-google-sheets
Feature: Google Sheets Connector

  Background: Clean application state
    Given clean application state
    And deploy ActiveMQ broker
    And clean "contact" table
    And log into the Syndesis
    And navigate to the "Settings" page
    And check that settings item "Google Sheets" has button "Register"
    And fill "Google Sheets" oauth settings "QE Google Sheets"
    And renew access token for "QE Google Sheets" google account
    And create connection "Google Sheets" with name "Google Sheets" using oauth
    And created connections
      | Red Hat AMQ | AMQ | AMQ | AMQ connection |
    And navigate to the "Home" page

  Scenario: create spreadsheet
    When click on the "Create Integration" button to create a new integration.
    And select the "Timer" connection
    And select "Simple Timer" integration action
    And fill in values
      | Period | 1000 |
    Then click on the "Done" button

    When select the "AMQ" connection
    And select "Publish Messages" integration action
    And fill in values
      | Destination Name | sheets |
      | Destination Type | Queue  |
    And click on the "Next" button
    And fill in values
      | Select Type    | JSON Instance |
      | Data Type Name | spreadsheetID |
    And fill in values by element ID
      | specification | {"id":"id"} |
    Then click on the "Done" button

    When add integration step on position "0"
    And select the "Google Sheets" connection
    And select "Create spreadsheet" integration action
    And fill in values
      | Title | brand-new-spreadsheet |
    Then click on the "Done" button

    When add integration step on position "1"
    And select the "Data Mapper" connection
    And create data mapper mappings
      | spreadsheetId | id |
    Then click on the "Done" button

    When click on the "Publish" button
    And set integration name "create-sheet"

    And click on the "Publish" button
    And navigate to the "Integrations" page
    And wait until integration "create-sheet" gets into "Running" state
    And sleep for "3000" ms
    Then verify that spreadsheet was created

  Scenario: Append messages from DB
    When clear test spreadsheet
    And inserts into "contact" table
      | Matej | Foo    | Red Hat | db |
      | Matej | Bar    | Red Hat | db |
      | Fuse  | Online | Red Hat | db |

    Then click on the "Create Integration" button to create a new integration
    When select the "PostgresDB" connection
    And select "Periodic SQL invocation" integration action
    And fill in values
      | SQL statement | SELECT * FROM contact |
      | Period        | 1000                  |
    Then click on the "Done" button

    When select the "Google Sheets" connection
    And select "Append values to a sheet" integration action
    And fill in values
      | Range | A1:E1 |
    And fill spreadsheet ID
    Then click on the "Next" button

    When add integration step on position "0"
    And select the "Data Mapper" connection
    And check visibility of data mapper ui
    And open data bucket "Properties"
    And define spreadsheetID as property in data mapper
    And open data mapper collection mappings
    And create data mapper mappings
      | spreadsheetId | spreadsheetId |
      | first_name    | A             |
      | last_name     | B             |
      | company       | C             |
      | lead_source   | D             |
      | create_date   | E             |

    Then click on the "Done" button

    When click on the "Publish" button
    And set integration name "from-db-to-sheets"
    And click on the "Publish" button
    And navigate to the "Integrations" page
    And wait until integration "from-db-to-sheets" gets into "Running" state
    And sleep for "3000" ms
    Then verify that test sheet contains values on range "A1:E5"
      | Matej | Foo    | Red Hat | db |
      | Matej | Bar    | Red Hat | db |
      | Fuse  | Online | Red Hat | db |

  Scenario: Update messages from DB - Rows
    When inserts into "contact" table
      | New    | Updated | Red Hat | db |
      | Second | Update  | IBM     | db |

    Then click on the "Create Integration" button to create a new integration
    When select the "PostgresDB" connection
    And select "Periodic SQL invocation" integration action
    And fill in values
      | SQL statement | SELECT * FROM contact COUNT |
      | Period        | 1000                        |
    Then click on the "Done" button

    When select the "Google Sheets" connection
    And select "Update sheet values" integration action
    And fill in values
      | Range           | A2:E3 |
      | Major dimension | Rows  |
    Then click on the "Next" button

    When add integration step on position "0"
    And select the "Data Mapper" connection
    And check visibility of data mapper ui
    And open data bucket "Properties"
    And define spreadsheetID as property in data mapper
    And open data mapper collection mappings
    And create data mapper mappings
      | spreadsheetId | spreadsheetId |
      | first_name    | A             |
      | last_name     | B             |
      | company       | C             |
      | lead_source   | D             |
      | create_date   | E             |
    Then click on the "Done" button

    When click on the "Publish" button
    And set integration name "from-db-to-sheets-update"
    And click on the "Publish" button
    And navigate to the "Integrations" page
    And wait until integration "from-db-to-sheets-update" gets into "Running" state
    Then verify that test sheet contains values on range "A2:D2"
      | New | Updated | Red Hat | db |

  Scenario: Update messages from DB - Columns
    When inserts into "contact" table
      | Matej | Foo    | Red Hat | db |
      | Matej | Bar    | Red Hat | db |
      | Fuse  | Online | Red Hat | db |
    Then click on the "Create Integration" button to create a new integration

    When select the "PostgresDB" connection
    And select "Periodic SQL invocation" integration action
    And fill in values
      | SQL statement | SELECT * FROM contact |
      | Period        | 1000                  |
    Then click on the "Done" button

    When select the "Google Sheets" connection
    And select "Update sheet values" integration action
    And fill in values
      | Range           | E1:E5   |
      | Major dimension | Columns |
    Then click on the "Next" button

    When add integration step on position "0"
    And select the "Split" connection
    And add integration step on position "1"
    And select the "Data Mapper" connection
    And check visibility of data mapper ui
    And open data bucket "Properties"
    And define spreadsheetID as property in data mapper
    And open data mapper collection mappings
    And create data mapper mappings
      | spreadsheetId | spreadsheetId |
      | first_name    | #1            |
      | last_name     | #2            |
      | company       | #3            |
      | lead_source   | #4            |
      | create_date   | #5            |
    Then click on the "Done" button

    When click on the "Publish" button
    And set integration name "from-db-to-sheets-update-column"
    And click on the "Publish" button
    And navigate to the "Integrations" page
    And wait until integration "from-db-to-sheets-update-column" gets into "Running" state

    Then verify that test sheet contains values on range "E1:E5"
      | Fuse    |
      | Online  |
      | Red Hat |
      | db      |

  Scenario: create pivottable from sample data
    When clear range "'pivot rows'" in data test spreadsheet
    And clear range "'pivot-columns'" in data test spreadsheet
    And click on the "Create Integration" button to create a new integration.
    When select the "Google Sheets" connection
    And select "Get spreadsheet properties" integration action
    And fill in values
      | SpreadsheetId | 1_OLTcj_y8NwST9KHhg8etB10xr6t3TrzaFXwW2dhpXw |
    Then click on the "Done" button

    When select the "Google Sheets" connection
    And select "Add pivot tables" integration action
    Then click on the "Done" button

    When add integration step on position "0"
    And select the "Google Sheets" connection
    And select "Add pivot tables" integration action
    Then click on the "Done" button

    When add integration step on position "0"
    And select the "Data Mapper" connection
    And check visibility of data mapper ui
    And define property "sheetId" with value "31438639" of type "String" in data mapper
    And define property "label" with value "countries" of type "String" in data mapper
    And define property "range" with value "A2:E36625" of type "String" in data mapper
    And define property "sourceSheetId" with value "0" of type "Integer" in data mapper
    And define property "sourceGroupColumn" with value "C" of type "String" in data mapper
    And define property "sourceValuesColumn" with value "D" of type "String" in data mapper
    And open data bucket "Properties"
    And create data mapper mappings
      | spreadsheetId      | spreadsheetId             |
      | sheetId            | sheetId                   |
      | sourceGroupColumn  | columnGroups.sourceColumn |
      | sourceValuesColumn | valueGroups.sourceColumn  |
      | label              | columnGroups.label        |
      | range              | sourceRange               |
      | sourceSheetId      | sourceSheetId             |
    Then click on the "Done" button

    When add integration step on position "2"
    And select the "Data Mapper" connection
    And check visibility of data mapper ui
    And define property "sheetId" with value "789378340" of type "String" in data mapper
    And define property "label" with value "countries" of type "String" in data mapper
    And define property "range" with value "A2:R36625" of type "String" in data mapper
    And define property "sourceSheetId" with value "0" of type "Integer" in data mapper
    And define property "sourceGroupColumn" with value "C" of type "String" in data mapper
    And define property "sourceValuesColumn" with value "R" of type "String" in data mapper
    And define property "layout" with value "vertical" of type "String" in data mapper
    And define property "start" with value "A1" of type "String" in data mapper

    And open data bucket "Properties"
    And open data bucket "1 - Spreadsheet"
    And create data mapper mappings
      | spreadsheetId      | spreadsheetId            |
      | sheetId            | sheetId                  |
      | sourceValuesColumn | valueGroups.sourceColumn |
      | label              | rowGroups.label          |
      | range              | sourceRange              |
      | sourceSheetId      | sourceSheetId            |
      | sourceGroupColumn  | rowGroups.sourceColumn   |
      | start              | start                    |
      | layout             | valueLayout              |
    Then click on the "Done" button

    When click on the "Publish" button
    And set integration name "pivot-table"
    And click on the "Publish" button
    And navigate to the "Integrations" page
    And wait until integration "pivot-table" gets into "Running" state
    Then verify that data test sheet contains values on range "'pivot rows'!A1:B90"
      | LAFAYETTE COUNTY | 223   |
      | LAKE COUNTY      | 364   |
      | LEE COUNTY       | 1122  |
      | LEON COUNTY      | 345   |
      | LEVY COUNTY      | 236   |
      | LIBERTY COUNTY   | 79    |
      | MADISON COUNTY   | 183   |
      | Grand Total      | 60085 |

    And verify that data test sheet contains values on range "'pivot-columns'!A2:D4"
      |               | ALACHUA COUNTY | BAKER COUNTY | BAY COUNTY  |
      | SUM of 498960 | 424134760.5    | 2854645.2    | 640241168.4 |


  Scenario: Update title
    When click on the "Create Integration" button to create a new integration.
    And select the "Timer" connection
    And select "Simple Timer" integration action
    And fill in values
      | Period | 1000 |
    Then click on the "Done" button

    When select the "Google Sheets" connection
    And select "Update spreadsheet properties" integration action
    And fill spreadsheet ID
    And fill in values
      | Title | updated-title |
    Then click on the "Done" button

    When click on the "Publish" button
    And set integration name "update-sheet-title"
    And click on the "Publish" button
    And navigate to the "Integrations" page
    And wait until integration "update-sheet-title" gets into "Running" state
    And sleep for "3000" ms
    Then verify that spreadsheet title match "updated-title"

  Scenario: get properties of sheet
    When click on the "Create Integration" button to create a new integration.
    And select the "Google Sheets" connection
    And select "Get spreadsheet properties" integration action
    And fill in values
      | SpreadsheetId | 1_OLTcj_y8NwST9KHhg8etB10xr6t3TrzaFXwW2dhpXw |
    Then click on the "Done" button

    When select the "AMQ" connection
    And select "Publish Messages" integration action
    And fill in values
      | Destination Name | sheety |
      | Destination Type | Queue  |
    And click on the "Next" button
    And fill in values
      | Select Type    | JSON Instance |
      | Data Type Name | spreadsheetID |
    And fill in values by element ID
      | specification | {"sheets":[{"sheet":"id"}]} |
    Then click on the "Done" button

    When add integration step on position "0"
    And select the "Data Mapper" connection
    And check visibility of data mapper ui
    And create data mapper mappings
      | sheets.title     | sheets.sheet    |

    Then click on the "Done" button

    When click on the "Publish" button
    And set integration name "properties"
    And click on the "Publish" button
    And navigate to the "Integrations" page
    And wait until integration "properties" gets into "Running" state
    Then verify that message from "sheety" queue contains "Sheet1,sheet2,pivot-columns,pivot rows"

  Scenario: add chart
    When click on the "Create Integration" button to create a new integration.
    And select the "Google Sheets" connection
    And select "Get spreadsheet properties" integration action
    And fill in values
      | SpreadsheetId | 1_OLTcj_y8NwST9KHhg8etB10xr6t3TrzaFXwW2dhpXw |
    Then click on the "Done" button

    When select the "Google Sheets" connection
    And select "Add charts" integration action
    Then click on the "Done" button

    When add integration step on position "0"
    And select the "Data Mapper" connection
    And check visibility of data mapper ui
    And define property "sheetId" with value "789378340" of type "String" in data mapper
    And define property "dataRange" with value "B1:B20" of type "String" in data mapper
    And define property "domainRange" with value "A1:A20" of type "String" in data mapper
    And open data bucket "Properties"
    And create data mapper mappings
      | spreadsheetId | spreadsheetId        |
      | sheetId       | sourceSheetId        |
      | dataRange     | pieChart.dataRange   |
      | domainRange   | pieChart.domainRange |
    Then click on the "Done" button

    When click on the "Publish" button
    And set integration name "charts"
    And click on the "Publish" button
    And navigate to the "Integrations" page
    And wait until integration "charts" gets into "Running" state
  Then verify that chart was created

  Scenario: Copy big spreadsheet
    When clear test spreadsheet
    And click on the "Create Integration" button to create a new integration.
    And select the "Google Sheets" connection
    And select "Get sheet values" integration action
    And fill in values
      | SpreadsheetId | 1_OLTcj_y8NwST9KHhg8etB10xr6t3TrzaFXwW2dhpXw |
      | Range         | A1:R25000                                    |
    Then click on the "Done" button

    When select the "Google Sheets" connection
    And select "Append values to a sheet" integration action
    And fill spreadsheet ID
    And  fill in values
      | Range | A1:H25000 |
    Then click on the "Done" button

    When add integration step on position "0"
    And select the "Data Mapper" connection
    And check visibility of data mapper ui
    And open data mapper collection mappings
    And create data mapper mappings
      | A | A |
      | B | B |
      | C | C |
      | D | D |
      | E | E |
      | F | F |
      | G | G |
      | H | H |
    Then click on the "Done" button

    When click on the "Publish" button
    And set integration name "copy"
    And click on the "Publish" button
    And navigate to the "Integrations" page
    And wait until integration "copy" gets into "Running" state
    Then verify that data test sheet contains values on range "A25000:H25000"
      | 241178 | FL | PALM BEACH COUNTY | 0 | 1810826.38 | 0 | 0 | 1810826.38 |
