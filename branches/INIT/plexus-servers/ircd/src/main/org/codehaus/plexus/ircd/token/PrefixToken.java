/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

public class PrefixToken extends IRCToken
{

    private NickToken nickToken;
    private UserToken userToken;
    private ServerNameToken hostToken;
    private ServerNameToken serverNameToken;

    /**
     * @param token content of the token
     */
    protected PrefixToken( String token )
    {
        super( token );
    }

    /**
     * to get the host token
     */
    public ServerNameToken getHostToken()
    {
        return hostToken;
    }

    /**
     * to get the nick token
     */
    public NickToken getNickToken()
    {
        return nickToken;
    }

    /**
     * to get the server name token
     */
    public ServerNameToken getServerNameToken()
    {
        return serverNameToken;
    }

    /**
     * to parse the content of the content and to get the corresponding PrefixToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new PrefixToken( sToken ) );
    }

    /**
     * to get the user token
     */
    public UserToken getUserToken()
    {
        return userToken;
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
            String sFirstToken = sToken;
            int index2 = sToken.indexOf( '@' );
            if ( index2 >= 0 )
            {
                sFirstToken = sToken.substring( 0, index2 );
            }
            int index1 = sToken.indexOf( '!' );
            if ( index1 >= 0 )
            {
                sFirstToken = sToken.substring( 0, index1 );
            }
            IRCToken ircToken = NickToken.getToken( sFirstToken );
            if ( ircToken != null )
            {
                setNickToken( (NickToken) ircToken );
                if ( index1 != -1 )
                {
                    if ( index2 != -1 )
                    {
                        setUserToken( (UserToken) UserToken.getToken( sToken.substring( index1 + 1, index2 ) ) );
                    }
                    else
                    {
                        setUserToken( (UserToken) UserToken.getToken( sToken.substring( index1 + 1 ) ) );
                    }
                }
                if ( index2 != -1 )
                {
                    setHostToken( (ServerNameToken) ServerNameToken.getToken( sToken.substring( index2 + 1 ) ) );
                }
            }
            else
            {
                setServerNameToken( (ServerNameToken) ServerNameToken.getToken( sFirstToken ) );
            }
        }
        else
        {
            throw new FatalException( "The token 'prefixToken' is null !!" );
        }
    }

    /**
     * to set the host token
     */
    private void setHostToken( ServerNameToken serverNameToken )
    {
        hostToken = serverNameToken;
    }

    /**
     * to set the nick token
     */
    private void setNickToken( NickToken nickToken )
    {
        this.nickToken = nickToken;
    }

    /**
     * to set the server name token
     */
    private void setServerNameToken( ServerNameToken serverNameToken )
    {
        this.serverNameToken = serverNameToken;
    }

    /**
     * to set the user token
     */
    private void setUserToken( UserToken userToken )
    {
        this.userToken = userToken;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer( 100 );
        if ( serverNameToken != null )
        {
            buffer.append( serverNameToken );
        }
        if ( nickToken != null )
        {
            buffer.append( nickToken );
        }
        if ( userToken != null )
        {
            buffer.append( "!" + userToken );
        }
        if ( hostToken != null )
        {
            buffer.append( "@" + hostToken );
        }
        return buffer.toString();
    }
}
