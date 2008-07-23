/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.model;

import java.util.Properties;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class DefaultApplicationTest
    extends MockObjectTestCase
{

    public void testShouldConstructWithNoArgs() {
        DefaultApplication app = new DefaultApplication();
    }
    
    public void testShouldSetAndRetrieveMainObject() {
        DefaultApplication app = new DefaultApplication();
        
        Mock mainMock = mock(Main.class);
        Main main = (Main)mainMock.proxy();
        
        app.setMain(main);
        
        assertEquals(main, app.getMain());
    }
    
    public void testShouldSetAndRetrieveEnvironment() {
        DefaultApplication app = new DefaultApplication();
        
        Properties env = new Properties();
        env.setProperty("test", "value");
        
        app.setEnvironment(env);
        
        assertEquals("value", app.getEnvironment().getProperty("test"));
    }
    
    public void testShouldDefensivelyCopyEnvironment() {
        DefaultApplication app = new DefaultApplication();
        
        Properties env = new Properties();
        
        app.setEnvironment(env);
        
        env.setProperty("test", "value");
        
        assertNull(app.getEnvironment().getProperty("test"));
    }
    
    public void testShouldSetAndRetrieveClasspath() {
        DefaultApplication app = new DefaultApplication();
        
        Mock cpMock = mock(Classpath.class);
        Classpath cp = (Classpath)cpMock.proxy();
        
        app.setClasspath(cp);
        
        assertEquals(cp, app.getClasspath());
    }
    
    public void testShouldSetAndRetrieveLegalUsage() {
        DefaultApplication app = new DefaultApplication();
        
        Mock luMock = mock(LegalUsage.class);
        LegalUsage lu = (LegalUsage)luMock.proxy();
        
        app.setLegalUsage(lu);
        
        assertEquals(lu, app.getLegalUsage());
    }
    
    public void testShouldSetAndRetrieveApplicationDescription() {
        DefaultApplication app = new DefaultApplication();
        
        String appDesc = "Application description";
        app.setApplicationDescription(appDesc);
        
        assertEquals(appDesc, app.getApplicationDescription());
    }
    
    public void testShouldSetAndRetrieveArgumentDescription() {
        DefaultApplication app = new DefaultApplication();
        
        String argDesc = "Argument description";
        app.setArgumentDescription(argDesc);
        
        assertEquals(argDesc, app.getArgumentDescription());
    }
    
}
