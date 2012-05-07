package org.freshwaterlife.conversion.CDSISIS;

import java.io.*;

import org.jdom.input.SAXBuilder;
import org.jdom.JDOMException;
import org.jdom.Document;

import org.freshwaterlife.conversion.IXMLOutputSource;

/**
 *
 * @author pjohnson
 */
public class CDSISISBatchImporter {

    private IXMLOutputSource oOutput;
    private String sErrorFile;
    private DataOutputStream fErrors;

    private int nStartOfRecord = -1;
    private String sCurrentString = "";
    private String sFindToken = "<RECORD;>";
    private String sReplaceWithToken = "<RECORD>";
    private String sEndToken = "</RECORD>";

    private int nTotalErrors = 0;

    public CDSISISBatchImporter(IXMLOutputSource output, String errorFile)
    {
        this.oOutput = output;
        this.sErrorFile = errorFile;
    }

    public void readFile(String inputFile)
    {
        System.out.println("reading");

        // processFile
        // Read source file
        FileInputStream fIn;

        try
        {
            fIn = new FileInputStream(inputFile);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Input file not found");
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));

        String line;
        try
        {
            while (null != ((line = reader.readLine())))
            {
                this.processLine(line);
            }
            System.out.println("Finished Reading Input File.");
            if (this.sCurrentString != "")
            {
                // Find location of </RECORD> element.
                int x=this.sCurrentString.lastIndexOf(this.sEndToken);
                processRecord(this.sCurrentString.substring(0, (x+this.sEndToken.length())));
            }
        }
        catch (Exception e)
        {
            System.out.println("IO Error " + e.getMessage() + e.toString() + e.getStackTrace().toString()+ " - " + this.sCurrentString.lastIndexOf(this.sEndToken) + " - " + this.sCurrentString);
            int x=this.sCurrentString.lastIndexOf(this.sEndToken);
            System.out.println("IO Error " + e.getMessage() + e.toString() + e.getStackTrace().toString()+ " - " + this.sCurrentString.lastIndexOf(this.sEndToken) + " - " + this.sCurrentString + " - " + this.sCurrentString.substring(0, (x+this.sEndToken.length())));
            e.printStackTrace();
        }

        this.oOutput.finished();
        
    }
    
    private void processLine(String line)
    {
        line = formatLine(line);
        
        int x=0;
        int y=0;

        // Extract RECORD element
        while ((x=line.indexOf(this.sReplaceWithToken, y))>-1)
        {
            //System.out.println("***Found RECORD");
            if (this.nStartOfRecord >= 0)
            {
                // Start of prev RECORD is in this line.

                // Add text from start of prev RECORD (record_start) to start of this record (x) into current_string
                this.sCurrentString = line.substring(this.nStartOfRecord, x);

            }
            else if (this.sCurrentString != "")
            {
                // Some of the prev RECORD's data is on previous lines and stored in current_string.

                // Add text from start of this line (0) to start of this record (x) into current_string.
                this.sCurrentString += line.substring(0, x);
            }

            if (this.sCurrentString != "")
            {
                // We have a complete prev RECORD to process
                this.processRecord(this.sCurrentString);
                this.sCurrentString = "";

            }

            this.nStartOfRecord = x;
            
            y=x+this.sReplaceWithToken.length();
            
        }

        if (this.nStartOfRecord >= 0)
        {
            // RECORD starts on this line, but start of next RECORD is on another line.
            // Add text from record_start to end of line into current_string.
            this.sCurrentString += line.substring(this.nStartOfRecord);
            //System.out.println("newly set current_string: " + this.sCurrentString);
            this.nStartOfRecord = -1;
        }
        else if (this.sCurrentString != "")
        {
            // Add entire line to current string.
            this.sCurrentString += line;
        }

        // Otherwise we ignore this line as the are no records on this line or prior lines.
    }
    
    private void processRecord(String record)
    {
        // Convert string to xml element.
        SAXBuilder sb;
        Document srcDoc;

        try
        {
            sb = new SAXBuilder();
            srcDoc = sb.build(new StringReader(record));
            // Send to supplied output source
            this.oOutput.process(srcDoc.getRootElement());
        }
        catch (JDOMException e)
        {
            // Store malformed RECORD string.
            storeMalformedRecord(record);
        }
        catch (IOException e)
        {
            // Store malformed RECORD string.
            storeMalformedRecord(record);
        }

    }

    private void storeMalformedRecord(String record)
    {
        System.out.println("Malformed Record (See Malformed xml file for details):");

        try
        {
            if (this.nTotalErrors == 0)
            {
                createErrorsFile();
            }
            this.nTotalErrors++;
            this.fErrors.writeBytes(record + "\n");
        }
        catch (IOException ex)
        {
            System.out.println("Could not write to error file");
        }
    }

    private void createErrorsFile()
    {
        FileOutputStream fos;
        try
        {
           fos = new FileOutputStream(this.sErrorFile);
        }
        catch (FileNotFoundException e)
        {
           System.out.println("Errors file cannot be created");
           return;
        }

        fErrors = new DataOutputStream(fos);
    }

    // Replace all occurrences of RECORD; with RECORD
    private String formatLine(String line)
    {
        //System.out.println("Reading line" + line);
        int x=0;
        int y=0;
        String result="";

        // Tidy up RECORD; element name
        while ((x=line.indexOf(this.sFindToken, y))>-1)
        {
            //System.out.println("**Found RECORD;");
            result+=line.substring(y,x);
            result+=this.sReplaceWithToken;
            y=x+this.sFindToken.length();
        }

        if (result != "")
        {
            line = result;
        }

        return line;
    }

}
