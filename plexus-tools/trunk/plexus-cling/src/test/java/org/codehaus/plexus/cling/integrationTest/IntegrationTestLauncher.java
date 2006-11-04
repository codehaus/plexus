/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.integrationTest;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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
        // next, set the appdir sysprop equal to the app.xml File's parent path
        System.setProperty(CLIngConstants.APPDIR_SYSPROP, "src/test-app");
        System.setProperty(CLIngConstants.APP_RESULT_SYSPROP, "result");
        
        String[] args = {
            "--output"
        };
        
        ClassWorld cw = new ClassWorld("cling.root", getClass().getClassLoader());
        Launcher.main(args, cw);
        
        assertEquals("Execution result was not success.", "0", System.getProperty("result"));
    }

}
