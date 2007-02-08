/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

import java.util.regex.Pattern;

public class ChannelToken extends IRCToken
{

    private String channelName;

    /**
     * @param token content of the token
     */
    protected ChannelToken( String token )
    {
        super( token );
    }

    /**
     * to get the channel name
     */
    public String getChannelName()
    {
        return channelName;
    }

    /**
     * to parse the content of the content and to get the corresponding ChannelToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new ChannelToken( sToken ) );
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
            if ( Pattern.matches( "(#|&)[^\u0020|\u0007|,|\015|\012]+", sToken ) )
            {
                setChannelName( sToken );
            }
            else
            {
                throw new FatalException( "The token 'channelToken' is incorrect !!" );
            }
        }
        else
        {
            throw new FatalException( "The token 'channelToken' is null !!" );
        }
    }

    /**
     * to set the channel name
     */
    private void setChannelName( String channelName )
    {
        this.channelName = channelName;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        return channelName == null ? VOID : channelName;
    }
}

