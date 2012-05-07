@echo off

set "classToRun=org.freshwaterlife.conversion.client.CDSISISToCDSISIS"

set "cdsisisOutputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.CDSISIS.Output.xml"

set "cdsisisInputFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.CDSISIS.xml"

set "cdsisisErrorFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Malformed.xml"

set "cdsisisInvalidElementsFile=C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Invalid.Elements.txt"

set "keywords=U:/LIS/NISC/FBACatNiscConverter/FBACatalogueKeywordCodes.csv"

set maxRecordsPerFile=10000

set "commandLineArgs=%classToRun% "%cdsisisOutputFile%" "%cdsisisInputFile%" "%cdsisisErrorFile%" "%cdsisisInvalidElementsFile%" "%keywords%" "%maxRecordsPerFile%""

java -cp ".;./dist/lib/*.jar;./dist/ConversionAndIngestion.jar" %commandLineArgs%
