@echo off

set "classToRun=org.freshwaterlife.conversion.client.MODSToFOXML"

set "modsInputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_2.MODS/All.MODS.xml"

set "foxmlOutputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_3.FOXML/All.FOXML.Output.xml"

set "foxmlTemplateFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_3.FOXML/FOXML.Template.xml"

set "commandLineArgs=%classToRun% "%modsInputFile%" "%foxmlOutputFile%" "%foxmlTemplateFile%""

java -cp ".;./dist/lib/*.jar;./dist/ConversionAndIngestion.jar" %commandLineArgs%
