/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.integrationTest;

import java.io.File;
import java.net.URL;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.cling.CLIngConstants;
import org.codehaus.plexus.cling.Launcher;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class IntegrationTestLauncher
    extends TestCase
{
    
    public void testShouldExecuteWithSpecifiedAppDirectory() {
        // app.xml should be in the classpath...retrieve its URL
        URL appXmlCPUrl = getClass().getClassLoader().getResource("app.xml");
        
        // now use the app.xml URL to create a File pointing to it.
        File appXmlFile = new File(appXmlCPUrl.getPath());
        
        // next, set the appdir sysprop equal to the app.xml File's parent path
        System.setProperty(CLIngConstants.APPDIR_SYSPROP, appXmlFile.getParent());
        
        System.setProperty(CLIngConstants.APP_RESULT_SYSPROP, "result");
        
        String[] args = {
            "--output"
        };
        
        ClassWorld cw = new ClassWorld("root", getClass().getClassLoader());
        Launcher.main(args, cw);
        
        assertEquals("0", System.getProperty("result"));
    }

}
