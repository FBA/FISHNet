@echo off

set "classToRun=org.freshwaterlife.ingestion.client.PurgeFedoraObjects"

set "fedoraURL=http://SERVER:8080/fedora"

set "fedoraUsername=USERNAME"

set "fedoraPassword=PASSWORD"

set "fedoraIngestFormat=info:fedora/fedora-system:FOXML-1.1"

set "fedoraLogMessage=Library Catalogue Batch Ingest"

set "fieldName=pid"

set "fieldValue=Library:*"

set "commandLineArgs=%classToRun% "%fedoraURL%" "%fedoraUsername%" "%fedoraPassword%" "%fedoraIngestFormat%" "%fedoraLogMessage%" "%fieldName%" "%fieldValue%""

java -cp ".;./dist/lib/*.jar;./dist/ConversionAndIngestion.jar" %commandLineArgs%
