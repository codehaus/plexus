package org.codehaus.plexus.component.factory.jruby;

import java.io.StringReader;
import java.util.Random;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.apache.maven.plugin.Mojo;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.jruby.JRubyInvoker;
import org.codehaus.plexus.util.StringOutputStream;

/**
 * 
 * @author eredmond
 */
public class JRubyComponentFactoryTest
    extends PlexusTestCase
{
    public void testHello()
        throws Exception
    {
        JRubyInvoker invoker = (JRubyInvoker) lookup( "hello" );
        assertNotNull( invoker );

        invoker.inputValue( "hello_from", JRubyComponentFactoryTest.class );

        invoker.invoke();
    }

    public void testExecute()
        throws Exception
    {
        JRubyInvoker invoker = (JRubyInvoker) lookup( "execute" );
        assertNotNull( invoker );

        invoker.inputValue( "random", new Random() );

        Executor result = (Executor)invoker.invoke();
        result.execute();

        invoker.setReader( new StringReader( "puts 'new script'" ) );
        invoker.invoke();
    }

    public void testJUnit()
        throws Exception
    {
        JRubyInvoker invoker = (JRubyInvoker) lookup( "junit" );
        assertNotNull( invoker );

        // invoker.inputValue( "random", new Random() );

        TestCase result = (TestCase)invoker.invoke();

        result.run();
    }

    public void xtestGem()
        throws Exception
    {
        JRubyInvoker invoker = (JRubyInvoker) lookup( "gem" );
        assertNotNull( invoker );

        invoker.inputValue( "args", "install rake" );

        invoker.invoke();
    }

    public void testInjected()
        throws Exception
    {
        JRubyInvoker invoker = (JRubyInvoker) lookup( "injected" );
        assertNotNull( invoker );

        invoker.invoke();
    }

    public void testMojo()
        throws Exception
    {
        JRubyInvoker invoker = (JRubyInvoker) lookup( "mojo" );
        assertNotNull( invoker );

        invoker.inputValue( "LOG", getContainer().getLogger() );

        Mojo mojo = (Mojo)invoker.invoke();
        mojo.execute();
    }

    public void testLog()
        throws Exception
    {
        JRubyInvoker invoker = (JRubyInvoker) lookup( "hello" );
        assertNotNull( invoker );

        invoker.inputValue( "hello_from", JRubyComponentFactoryTest.class );

        StringOutputStream stdout = new StringOutputStream();
        StringOutputStream stderr = new StringOutputStream();
        invoker.invoke( stdout, stderr );
        logOutput( stdout, false );
        logOutput( stderr, true );
    }
    
    private void logOutput( StringOutputStream out, boolean error )
    {
        String output = out.toString();
        if ( output != null && output.length() > 0 )
        {
            for ( StringTokenizer tokens = new StringTokenizer( output, "\n" ); tokens.hasMoreTokens(); )
            {
                if ( error )
                {
                    getContainer().getLogger().error( tokens.nextToken() );
                }
                else
                {
                    getContainer().getLogger().info( tokens.nextToken() );
                }
            }
        }
    }
}
