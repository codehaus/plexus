/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.integrationTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author jdcasey
 */
public class IntegrationTestMain
{

    private boolean bool;
    private boolean input;
    private String file;
    private boolean output;

    public IntegrationTestMain()
    {
    }
    
    public int execute(List arguments) {
        
        int result = -1;
        
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        
        try
        {
            cloader.loadClass("org.apache.commons.lang.enum.Enum");
        }
        catch ( ClassNotFoundException e )
        {
            e.printStackTrace();
            result = -2;
        }
        
        InputStream stream = cloader.getResourceAsStream("testResource.txt");
        if(stream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            try
            {
                line = reader.readLine();
                
                if("This is a test.".equals(line)) {
                    if(output) {
                        result = 0;
                    }
                    else {
                        result = -5;
                    }
                }
                else {
                    result = -3;
                }
                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                result = -4;
            }
        }
        
        return result;
    }
    
    public void setBool(boolean bool) {
        this.bool = bool;
    }
    
    public void setInput(boolean input) {
        this.input = input;
    }
    
    public void setFile(String file) {
        this.file = file;
    }
    
    public boolean getOutput() {
        return output;
    }
    
    public void setOutput(boolean output) {
        this.output = output;
    }

}
