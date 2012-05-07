@echo off

set "classToRun=org.freshwaterlife.conversion.client.MODSToSOLR"

set "modsInputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_2.MODS/All.MODS.xml"

set "foxmlTemplateFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_3.FOXML/FOXML.Template.xml"

set "solrXSLTFile=C:/solr/conf/xslt/solr.xslt"

set "solrOutputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_4.SOLR/output/All.SOLR.Output.xml"

set "commandLineArgs=%classToRun% "%modsInputFile%" "%foxmlTemplateFile%" "%solrXSLTFile%" "%solrOutputFile%""

java -cp ".;./dist/lib/*.jar;./dist/ConversionAndIngestion.jar" %commandLineArgs%
