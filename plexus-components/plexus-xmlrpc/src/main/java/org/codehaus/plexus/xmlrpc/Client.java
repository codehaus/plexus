package org.codehaus.plexus.xmlrpc;

import org.codehaus.plexus.configuration.PlexusConfigurationException;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Client
{
    /** */
    private String hostname;

    public Client()
    {
    }

    /**
     * @return Returns the hostname.
     */
    public String getHostname()
        throws PlexusConfigurationException
    {
        hostname = hostname.trim();

        if ( hostname.length() == 0 )
            throw new PlexusConfigurationException( "The 'hostname' element cannot be empty." );

        // TODO: Add more validation of the IP/hostname itself

        return hostname;
    }
}
