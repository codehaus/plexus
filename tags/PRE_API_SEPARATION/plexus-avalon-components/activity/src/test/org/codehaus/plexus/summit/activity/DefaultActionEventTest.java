package org.codehaus.plexus.summit.activity;

import java.lang.reflect.Method;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * Tests the DefaultActionEvent service.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Mar 1, 2003
 */
public class DefaultActionEventTest extends PlexusTestCase
{
    DefaultActionEventService actionEvent;
    
    /**
     * @param name
     */
    public DefaultActionEventTest(String name)
    {
        super(name);
    }
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        actionEvent = ( DefaultActionEventService ) lookup( ActionEventService.ROLE );        
    }
    
    public void testMethodNaming() throws Exception
    {
        // Test template name to method name conversion
        String method = actionEvent.formatString( "eventSubmit_doMyMethod" );
        
        assertTrue( method.equals("doMymethod") );
    }
    
    public void testClassFinder() throws Exception
    {
        Class testClass = actionEvent.getClass( "activity.SomeAction" );
        Object someAction = testClass.newInstance();
        
        assertTrue( someAction instanceof SomeAction );        
    }
    
    public void testMethodLookup() throws Exception
    {
        Class testClass = actionEvent.getClass( "activity.SomeAction" );
        Method myMethod = actionEvent.getMethod( testClass,
                                                 RunData.class,
                                                 "doMymethod",
                                                 "doPerform" );
        assertTrue( myMethod.getName().equals("doMymethod") );
                                          
        myMethod = actionEvent.getMethod( testClass, 
                                          RunData.class,
                                          "eventSubmit_doNoMethod",
                                          "doPerform" );
        assertTrue( myMethod.getName().equals("doPerform") );                                      
    }
}
