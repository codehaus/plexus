package org.codehaus.plexus.component.factory.ant;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class AntScriptInvokerTest
    extends TestCase
{
    private static final String SCRIPT_RESOURCE = "test-scripts/AntScriptInvoker-build.xml";

    public void testInit_UsingResourceAndTarget()
        throws IOException, ComponentInstantiationException
    {
        String target = "test";

        AntScriptInvoker invoker = newInvoker( SCRIPT_RESOURCE, target );

        assertEquals( SCRIPT_RESOURCE, invoker.getScriptResource() );
        assertEquals( target, invoker.getTarget() );
    }

    private AntScriptInvoker newInvoker( String resource, String target )
        throws IOException, ComponentInstantiationException
    {
        String impl = resource + ( ( target == null ) ? "" : ( ":" + target ) );

        ComponentDescriptor cd = new ComponentDescriptor();
        cd.setRole( "invoker" );
        cd.setRoleHint( "test" );
        cd.setImplementation( impl );

        return new AntScriptInvoker( cd, Thread.currentThread().getContextClassLoader() );
    }

    public void testInit_UsingResource_NoTarget()
        throws IOException, ComponentInstantiationException
    {
        AntScriptInvoker invoker = newInvoker( SCRIPT_RESOURCE, null );

        assertEquals( SCRIPT_RESOURCE, invoker.getScriptResource() );
        assertNull( invoker.getTarget() );
    }

    public void testAddComponentRequirement_ShouldBePresentInReferences()
        throws IOException, ComponentInstantiationException, ComponentConfigurationException
    {
        Object component = new Object();
        String role = "role";
        String hint = "hint";

        ComponentRequirement cr = new ComponentRequirement();
        cr.setRole( role );
        cr.setRoleHint( hint );

        AntScriptInvoker invoker = newInvoker( SCRIPT_RESOURCE, null );
        invoker.getDescriptor().addRequirement( cr );

        invoker.addComponentRequirement( cr, component );

        Map references = invoker.getReferences();
        assertTrue( references.containsKey( role + "_" + hint ) );
    }

    public void testSetComponentConfiguration_StringToStringMappingGoesIntoProperties()
        throws IOException, ComponentInstantiationException, ComponentConfigurationException
    {
        AntScriptInvoker invoker = newInvoker( SCRIPT_RESOURCE, null );

        Map config = new HashMap();
        config.put( "basedir", new File( "." ).getAbsolutePath() );
        config.put( "key", "value" );

        invoker.setComponentConfiguration( config );

        assertEquals( "value", invoker.getProperties().get( "key" ) );
    }

    public void testSetComponentConfiguration_StringToNonStringMappingGoesIntoReferences()
        throws IOException, ComponentInstantiationException, ComponentConfigurationException
    {
        AntScriptInvoker invoker = newInvoker( SCRIPT_RESOURCE, null );

        Map config = new HashMap();
        config.put( "basedir", new File( "." ).getAbsolutePath() );
        
        Object value = new Object();
        config.put( "key", value );

        invoker.setComponentConfiguration( config );

        assertSame( value, invoker.getReferences().get( "key" ) );
    }
    
    public void testProjectInstanceConstructedWhenConfigurationIsSet()
        throws IOException, ComponentInstantiationException, ComponentConfigurationException
    {
        AntScriptInvoker invoker = newInvoker( SCRIPT_RESOURCE, null );

        Map config = new HashMap();
        String basedir = new File( "." ).getAbsolutePath();
        
        config.put( "basedir", basedir );

        invoker.setComponentConfiguration( config );
        
        assertNotNull( invoker.getProject() );
        assertEquals( new File( basedir ).getCanonicalFile(), invoker.getProject().getBaseDir() );
    }
    
    public void testExecute()
        throws IOException, ComponentInstantiationException, ComponentConfigurationException,
        AntComponentExecutionException
    {
        AntScriptInvoker invoker = newInvoker( SCRIPT_RESOURCE, "test" );

        Map config = new HashMap();
        String basedir = new File( "." ).getAbsolutePath();
        File testdir = File.createTempFile( "AntScriptInvoker.", ".dir.tmp" );
        testdir.delete();
        testdir.mkdirs();
        
        testdir.deleteOnExit();
        
        String testDir = testdir.getCanonicalPath();
        
        config.put( "basedir", basedir );
        config.put( "testDir", testDir );
        config.put( "messageLevel", "info" );

        invoker.setComponentConfiguration( config );
        
        final List messages = new ArrayList();
        
        BuildListener listener = new BuildListener()
        {
            public void buildFinished( BuildEvent arg0 )
            {
            }

            public void buildStarted( BuildEvent arg0 )
            {
            }

            public void messageLogged( BuildEvent event )
            {
                messages.add( event.getMessage() );
            }

            public void targetFinished( BuildEvent arg0 )
            {
            }

            public void targetStarted( BuildEvent arg0 )
            {
            }

            public void taskFinished( BuildEvent arg0 )
            {
            }

            public void taskStarted( BuildEvent arg0 )
            {
            }
        };
        
        invoker.getProject().addBuildListener( listener );
        
        invoker.invoke();
        
        boolean found = false;
        for ( Iterator it = messages.iterator(); it.hasNext(); )
        {
            String message = (String) it.next();
            if ( message.indexOf( testDir ) > -1 )
            {
                found = true;
                break;
            }
        }
        
        assertTrue( found );
    }

}
