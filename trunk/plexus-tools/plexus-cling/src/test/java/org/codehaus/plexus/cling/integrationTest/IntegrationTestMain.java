/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.integrationTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    
    public int execute() {
        
        int result = -1;
        
        ClassLoader cloader = getClass().getClassLoader();
        
        try
        {
            cloader.loadClass("junit.framework.TestCase");
        }
        catch ( ClassNotFoundException e )
        {
            e.printStackTrace();
        }
        
        InputStream stream = cloader.getResourceAsStream("testResource.txt");
        if(stream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            try
            {
                line = reader.readLine();
                if("This is a test".equals(line)) {
                    result = 0;
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
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
    
    public void setOutput(boolean output) {
        this.output = output;
    }

}
