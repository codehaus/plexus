package org.codehaus.plexus.cdc;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PlexusCdcTest
    extends TestCase
{
    private String basedir = System.getProperty( "basedir" );

    public void testCdc()
        throws Exception
    {
        ComponentDescriptorCreator cdc = new ComponentDescriptorCreator();

        cdc.setBasedir( new File( basedir, "src/test-project" ).getPath() );

        cdc.setDestDir( new File( basedir, "target" ).getPath() );

        Properties properties = new Properties();

        properties.setProperty( "myProperty", "." );

        cdc.setProperties( properties );

        cdc.execute();
    }
}
