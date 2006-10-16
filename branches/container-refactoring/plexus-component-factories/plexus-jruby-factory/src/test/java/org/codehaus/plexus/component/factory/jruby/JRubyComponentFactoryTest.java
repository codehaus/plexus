package org.codehaus.plexus.component.factory.jruby;

import java.util.Random;
import java.util.StringTokenizer;

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
    }

    public void testInjected()
        throws Exception
    {
        JRubyInvoker invoker = (JRubyInvoker) lookup( "injected" );
        assertNotNull( invoker );

        invoker.invoke();
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
