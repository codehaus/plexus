package org.codehaus.plexus.component.factory.judo;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.judo.JudoBSFInvoker;

/**
 * 
 * @author eredmond
 */
public class JudoComponentFactoryTest
    extends PlexusTestCase
{
    public void testHello()
        throws Exception
    {
        JudoBSFInvoker invoker = (JudoBSFInvoker)lookup( "hello" );
        assertNotNull( invoker );

        invoker.inputValue( "hello_from", JudoComponentFactoryTest.class.getName() );

        invoker.invoke();
    }

    public void testInjected()
        throws Exception
    {
        JudoBSFInvoker invoker = (JudoBSFInvoker)lookup( "injected" );
        assertNotNull( invoker );

        invoker.invoke();
    }

//    public void testLog()
//        throws Exception
//    {
//        JudoBSFInvoker invoker = (JudoBSFInvoker)lookup( "hello" );
//        assertNotNull( invoker );
//
//        invoker.inputValue( "hello_from", JudoComponentFactoryTest.class );
//
//        StringOutputStream stdout = new StringOutputStream();
//        StringOutputStream stderr = new StringOutputStream();
//        invoker.invoke( stdout, stderr );
//        logOutput( stdout, false, "hello" );
//        logOutput( stderr, true, "hello" );
//    }
//    
//    private void logOutput( StringOutputStream out, boolean error, String component )
//    {
//        String output = out.toString();
//        if ( output != null && output.length() > 0 )
//        {
//            for ( StringTokenizer tokens = new StringTokenizer( output, "\n" ); tokens.hasMoreTokens(); )
//            {
//                if ( error )
//                {
//                    getContainer().getLoggerManager().getLoggerForComponent( component ).error( tokens.nextToken() );
//                }
//                else
//                {
//                    getContainer().getLoggerManager().getLoggerForComponent( component ).info( tokens.nextToken() );
//                }
//            }
//        }
//    }
}
