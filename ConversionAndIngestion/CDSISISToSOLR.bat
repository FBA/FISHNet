@echo off

set "classToRun=org.freshwaterlife.conversion.client.CDSISISToSOLR"

set "cdsisisInputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.CDSISIS.xml"

set "cdsisisErrorFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Malformed.xml"

set "cdsisisInvalidElementsFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Invalid.Elements.txt"

set "keywords=U:/LIS/NISC/FBACatNiscConverter/FBACatalogueKeywordCodes.csv"

set "modsXSLTFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_2.MODS/mods.xslt"

set maxRecordsPerFile=10000

set "foxmlTemplateFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_3.FOXML/FOXML.Template.xml"

set "solrXSLTFile=C:/solr/conf/xslt/solr.xslt"

set "solrOutputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_4.SOLR/output/All.SOLR.Output.xml"

set "commandLineArgs=%classToRun% "%cdsisisInputFile%" "%cdsisisErrorFile%" "%cdsisisInvalidElementsFile%" "%keywords%" "%modsXSLTFile%" "%maxRecordsPerFile%" "%foxmlTemplateFile%" "%solrXSLTFile%" "%solrOutputFile%""

java -cp ".;./dist/lib/*.jar;./dist/ConversionAndIngestion.jar" %commandLineArgs%
