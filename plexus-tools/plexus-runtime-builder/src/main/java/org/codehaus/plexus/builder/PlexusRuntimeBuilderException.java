package org.codehaus.plexus.builder;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusRuntimeBuilderException
    extends Exception
{
    public PlexusRuntimeBuilderException( String msg )
    {
        super( msg );
    }
    
    public PlexusRuntimeBuilderException( String msg, Throwable cause )
    {
        super( msg, cause );
    }
}
