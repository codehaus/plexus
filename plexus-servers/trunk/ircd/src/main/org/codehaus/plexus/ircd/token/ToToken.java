/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

public class ToToken extends IRCToken
{

    private AnyToken anyToken;
    private UserToken userToken;
    private NickToken nickToken;
    private MaskToken maskToken;
    private ChannelToken channelToken;
    private ServerNameToken serverNameToken;

    /**
     * @param token content of the token
     */
    protected ToToken( String token )
    {
        super( token );
    }

    /**
     * to get the any token
     */
    public AnyToken getAnyToken()
    {
        return anyToken;
    }

    /**
     * to get the channel token
     */
    public ChannelToken getChannelToken()
    {
        return channelToken;
    }

    /**
     * to get the mask token
     */
    public MaskToken getMaskToken()
    {
        return maskToken;
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
     * to parse the content of the content and to get the corresponding ToToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new ToToken( sToken ) );
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
            IRCToken ircToken = ChannelToken.getToken( sToken );
            if ( ircToken != null )
            {
                setChannelToken( (ChannelToken) ircToken );
            }
            ircToken = MaskToken.getToken( sToken );
            if ( ircToken != null )
            {
                setMaskToken( (MaskToken) ircToken );
            }
            ircToken = NickToken.getToken( sToken );
            if ( ircToken != null )
            {
                setNickToken( (NickToken) ircToken );
            }
            int index = sToken.indexOf( '@' );
            if ( index != -1 )
            {
                ircToken = UserToken.getToken( sToken.substring( 0, index ) );
                if ( ircToken != null )
                {
                    setUserToken( (UserToken) ircToken );
                }
                ircToken = ServerNameToken.getToken( sToken.substring( index + 1 ) );
                if ( ircToken != null )
                {
                    setServerNameToken( (ServerNameToken) ircToken );
                }
            }
            ircToken = AnyToken.getToken( sToken );
            if ( ircToken != null )
            {
                setAnyToken( (AnyToken) ircToken );
            }
        }
        else
        {
            throw new FatalException( "The token 'toToken' is null !!" );
        }
    }

    /**
     * to set the any token
     */
    private void setAnyToken( AnyToken anyToken )
    {
        this.anyToken = anyToken;
    }

    /**
     * to set the channel token
     */
    private void setChannelToken( ChannelToken channelToken )
    {
        this.channelToken = channelToken;
    }

    /**
     * to set the mask token
     */
    private void setMaskToken( MaskToken maskToken )
    {
        this.maskToken = maskToken;
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
        StringBuffer buffer = new StringBuffer( 50 );
        if ( channelToken != null )
        {
            buffer.append( channelToken );
        }
        if ( nickToken != null )
        {
            buffer.append( nickToken );
        }
        if ( maskToken != null )
        {
            buffer.append( maskToken );
        }
        if ( userToken != null )
        {
            buffer.append( userToken );
        }
        if ( serverNameToken != null )
        {
            buffer.append( "@" + serverNameToken );
        }
        return buffer.toString();
    }
}
