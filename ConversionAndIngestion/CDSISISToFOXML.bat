@echo off

set "classToRun=org.freshwaterlife.conversion.client.CDSISISToFOXML"

set "cdsisisInputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.CDSISIS.xml"

set "cdsisisErrorFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Malformed.xml"

set "cdsisisInvalidElementsFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Invalid.Elements.txt"

set "keywords=U:/LIS/NISC/FBACatNiscConverter/FBACatalogueKeywordCodes.csv"

set "modsXSLTFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_2.MODS/mods.xslt"

set maxRecordsPerFile=10

set "foxmlOutputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_3.FOXML/output/All.FOXML.Output.xml"

set "foxmlTemplateFile=C:\Documents and Settings\pjohnson\My Documents\My Dropbox\Public\workspace\fedora-library-catalogue-objects\library-catalogue-object-foxml-template.xml"

set "commandLineArgs=%classToRun% "%cdsisisInputFile%" "%cdsisisErrorFile%" "%cdsisisInvalidElementsFile%" "%keywords%" "%modsXSLTFile%" "%maxRecordsPerFile%" "%foxmlOutputFile%" "%foxmlTemplateFile%""

java -cp ".;./dist/lib/*.jar;./dist/ConversionAndIngestion.jar" %commandLineArgs%