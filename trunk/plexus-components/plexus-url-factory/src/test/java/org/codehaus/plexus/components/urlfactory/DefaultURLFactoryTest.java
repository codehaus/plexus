package org.codehaus.plexus.components.urlfactory;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.IOUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class DefaultURLFactoryTest extends PlexusTestCase
{

    private URLFactory factory = null;


    public void setUp() throws Exception
    {
        super.setUp();

        factory = ( URLFactory ) lookup( URLFactory.ROLE );

    }

    public void testClassloaderProtocol()
    {
        testURLFactory( "classloader:foo/baa/test.txt" );

    }

    public void testFileProtocol()
    {

        final File file = getTestFile( "/target/test-classes/foo/baa/test.txt" );

        try
        {
            testURLFactory( file.toURL().toString() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testURLFactory( final String urlStr )
    {
        URL url = null;

        try
        {
            url = factory.getURL( urlStr );
        }

        catch ( Exception e )
        {
            e.printStackTrace();
            
            fail( "Protocol handler was not assigned as it was expected" );
        }

        InputStream in = null;

        ByteArrayOutputStream out = null;
        try
        {

            in = url.openStream();

            out = new ByteArrayOutputStream();

            IOUtil.copy( in, out );

            final String content = new String( out.toByteArray() );

            assertEquals( "it works!", content );

        }
        catch ( Exception e )
        {
            fail( "Protocol handler provided unexpected data: " + e.getMessage() );
        }
        finally
        {
            IOUtil.close( in );

            IOUtil.close( out );
        }
     }
}
