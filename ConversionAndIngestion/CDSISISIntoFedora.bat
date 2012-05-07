@echo off

set "classToRun=org.freshwaterlife.ingestion.client.CDSISISIntoFedora"

set "cdsisisInputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.CDSISIS.xml"

set "cdsisisErrorFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Malformed.xml"

set "cdsisisInvalidElementsFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Invalid.Elements.txt"

set "keywords=DRIVE:/PATH/FBACatalogueKeywordCodes.csv"

set "modsXSLTFile=DRIVE:/PATH/mods.xslt"

set maxRecordsPerFile=100

set "foxmlTemplateFile=DRIVE:\PATH\library-catalogue-object-foxml-template.xml"

set "fedoraURL=http://SERVER:8080/fedora"

set "fedoraUsername=USERNAME"

set "fedoraPassword=PASSWORD"

set "fedoraIngestFormat=info:fedora/fedora-system:FOXML-1.1"

set "fedoraLogMessage=Library Catalogue Batch Ingest"

set "commandLineArgs=%classToRun% "%cdsisisInputFile%" "%cdsisisErrorFile%" "%cdsisisInvalidElementsFile%" "%keywords%" "%modsXSLTFile%" "%maxRecordsPerFile%" "%foxmlTemplateFile%" "%fedoraURL%" "%fedoraUsername%" "%fedoraPassword%" "%fedoraIngestFormat%" "%fedoraLogMessage%""

java -cp ".;./dist/lib/*.jar;./dist/ConversionAndIngestion.jar" %commandLineArgs%
