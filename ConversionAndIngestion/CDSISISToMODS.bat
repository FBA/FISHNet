@echo off

set "classToRun=org.freshwaterlife.conversion.client.CDSISISToMODS"

set "cdsisisInputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.CDSISIS.xml"

set "cdsisisErrorFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Malformed.xml"

set "cdsisisInvalidElementsFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Invalid.Elements.txt"

set "keywords=U:/LIS/NISC/FBACatNiscConverter/FBACatalogueKeywordCodes.csv"

set "modsOutputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_2.MODS/All.MODS.Output.xml"

set "modsXSLTFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_2.MODS/mods.xslt"

set maxRecordsPerFile=10000

set "commandLineArgs=%classToRun% "%cdsisisInputFile%" "%cdsisisErrorFile%" "%cdsisisInvalidElementsFile%" "%keywords%" "%modsOutputFile%" "%modsXSLTFile%" "%maxRecordsPerFile%""

java -cp ".;./dist/lib/*.jar;./dist/ConversionAndIngestion.jar" %commandLineArgs%
