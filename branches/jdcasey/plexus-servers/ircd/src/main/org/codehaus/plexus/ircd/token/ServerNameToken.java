/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

import java.net.URI;

public class ServerNameToken extends IRCToken
{

    private String serverName;

    /**
     * @param token content of the token
     */
    protected ServerNameToken( String token )
    {
        super( token );
    }

    /**
     * to get the content of the token
     */
    public String getServerName()
    {
        return serverName;
    }

    /**
     * to parse the content of the content and to get the corresponding ServerNameToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new ServerNameToken( sToken ) );
    }

    /**
     * to parse the given token
     * @param sToken the token to parse
     * @throws FatalException parsing's exception
     */
    public void parseToken( String sToken ) throws FatalException
    {
        if ( sToken != null )
        {
            try
            {
                new URI( "codehaus://" + sToken );
                setServerName( sToken );
            }
            catch ( Exception e )
            {
                throw new FatalException( e.getMessage() );
            }
        }
        else
        {
            throw new FatalException( "The token 'serverName' is null !!" );
        }
    }

    /**
     * to set the content of the token
     */
    private void setServerName( String serverName )
    {
        this.serverName = serverName;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        return serverName == null ? VOID : serverName;
    }
}

