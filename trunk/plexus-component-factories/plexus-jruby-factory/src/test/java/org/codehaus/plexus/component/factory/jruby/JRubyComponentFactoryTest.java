package org.codehaus.plexus.component.factory.jruby;

import java.util.Random;
import java.util.StringTokenizer;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.jruby.JRubyInvoker;
import org.codehaus.plexus.util.StringOutputStream;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * 
 * @author eredmond
 */
public class JRubyComponentFactoryTest
    extends PlexusTestCase
{
    public void testComponent()
        throws Exception
    {
        JRubyInvoker invoker = (JRubyInvoker) lookup( "hello" );

        assertNotNull( invoker );

        invoker.inputValue( "from_class", JRubyComponentFactoryTest.class );
        invoker.inputValue( "random", new Random() );

        StringOutputStream stdout = new StringOutputStream();
        StringOutputStream stderr = new StringOutputStream();

        IRubyObject result = invoker.invoke( stdout, stderr );
        ((IRubyObject)result).callMethod( "execute" );

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
