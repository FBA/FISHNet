package org.freshwaterlife.conversion.CDSISIS;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author pjohnson
 */
public class KeywordLookup {

    Map<String, String> keycodes = new HashMap<String, String>();
    BufferedReader reader;

    public KeywordLookup(String filepath) throws FileNotFoundException, IOException
    {
        this.reader = new BufferedReader(new FileReader(filepath));

        while(true)
        {
            String line = this.reader.readLine();
            if(line == null)
            {
                this.reader.close();
                return;
            }

            String[] sa = line.split(",");
            String key = sa[0];
            String value;

            if(sa.length < 2)
            {
                value = "missing keyword definition";
            }
            else
            {
                value = sa[1];
            }

            this.keycodes.put(key, value);
        }
    }

    public String getKeywords(String code)
    {
        return (String)this.keycodes.get(code.toLowerCase());
    }

}
