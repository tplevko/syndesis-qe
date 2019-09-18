## @sustainer: tplevko@redhat.com
#
#@ui
#@dynamodb
#@integrations-dynamodb
#Feature: Integration - Dynamodb to Dropbox
#
#  Background: Clean application state
#    Given clean application state
#    Given log into the Syndesis
#
#    Given created connections
#      | Dropbox | QE Dropbox | QE Dropbox | SyndesisQE Dropbox test |
#    And navigate to the "Home" page
#
##
##  1. Functionality test of upload and download
##
#  @dropbox-integration
