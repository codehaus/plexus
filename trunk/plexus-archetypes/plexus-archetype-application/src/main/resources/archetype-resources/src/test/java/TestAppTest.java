package $package;

import org.codehaus.plexus.PlexusTestCase;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version \$Id$
 */
public class TestAppTest
    extends PlexusTestCase
{
    public void setUp()
        throws Exception
    {
        super.setUp();

        String plexusHome = (String) getContainer().getContext().get( "plexus.home" );

        File lib = new File( plexusHome, "lib" );

        if ( !lib.exists() )
        {
            assertTrue( lib.mkdir() );
        }
    }

    public void testApplication()
        throws Exception
    {
        TestApp app = (TestApp) lookup( TestApp.ROLE );
    }

    protected InputStream getCustomConfiguration()
        throws Exception
    {
        return new FileInputStream( getTestFile( "src/conf/application.xml" ) );
    }
}
