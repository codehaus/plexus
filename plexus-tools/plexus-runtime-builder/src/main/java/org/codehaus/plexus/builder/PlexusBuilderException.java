package org.codehaus.plexus.builder;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusBuilderException
    extends Exception
{
    public PlexusBuilderException( String msg )
    {
        super( msg );
    }

    public PlexusBuilderException( String msg, Throwable cause )
    {
        super( msg, cause );
    }
}
