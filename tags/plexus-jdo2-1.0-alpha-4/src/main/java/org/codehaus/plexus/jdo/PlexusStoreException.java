package org.codehaus.plexus.jdo;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class PlexusStoreException
    extends Exception
{
    public PlexusStoreException( String msg )
    {
        super( msg );
    }

    public PlexusStoreException( String msg, Exception ex )
    {
        super( msg, ex );
    }
}
