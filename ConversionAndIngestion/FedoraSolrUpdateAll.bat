@echo off

set "classToRun=org.freshwaterlife.ingestion.client.UpdateAllFedoraSolr"

set "fedoraURL=http://SERVER:8080/fedora"

set "fedoraUsername=USERNAME"

set "fedoraPassword=PASSWORD"

set "fieldName=pid"

set "fieldValue=Library:*"

set "solrUpdateUrl="http://SERVER:8080/solr/fedora?action=savePid&pid=""

set "commandLineArgs=%classToRun% "%fedoraURL%" "%fedoraUsername%" "%fedoraPassword%" "%fieldName%" "%fieldValue%" "%solrUpdateUrl%""

java -cp ".;./dist/lib/*.jar;./dist/ConversionAndIngestion.jar" %commandLineArgs%
