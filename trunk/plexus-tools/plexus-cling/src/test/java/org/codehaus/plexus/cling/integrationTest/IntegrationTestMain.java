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
    
    public int execute(Map parameters, List arguments) {
        
        int result = -1;
        
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        
        try
        {
            System.out.println("Loading javax.servlet.http.HttpServlet");
            cloader.loadClass("javax.servlet.http.HttpServlet");
        }
        catch ( ClassNotFoundException e )
        {
            e.printStackTrace();
        }
        
        URL resourceUrl = cloader.getResource("/testResource.txt");
        System.out.println("Attempting to load resource: " + resourceUrl + " (should be classpath:testResource.txt)");
        
        InputStream stream = cloader.getResourceAsStream("testResource.txt");
        if(stream != null) {
            System.out.println("resource found");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            try
            {
                line = reader.readLine();
                
                System.out.println("resource line: \'" + line + "\'");
                if("This is a test.".equals(line)) {
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
    
    public boolean getOutput() {
        return output;
    }
    
    public void setOutput(boolean output) {
        this.output = output;
    }

}
