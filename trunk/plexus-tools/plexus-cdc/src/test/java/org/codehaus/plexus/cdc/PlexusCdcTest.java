package org.codehaus.plexus.cdc;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 *
 * @version $Id$
 */
public class PlexusCdcTest
    extends PlexusTestCase
{
    public void testCdc()
        throws Exception
    {
        ComponentDescriptorCreator cdc = (ComponentDescriptorCreator)lookup( ComponentDescriptorCreator.ROLE );

        cdc.setBasedir( new File( basedir, "src/test-project" ).getPath() );

        cdc.setDestDir( new File( basedir, "target" ).getPath() );

        cdc.execute();

        // TODO: Add some assertions
    }
}
