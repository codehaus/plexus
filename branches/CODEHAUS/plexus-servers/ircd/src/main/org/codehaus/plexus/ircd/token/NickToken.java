/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

import java.util.regex.Pattern;

public class NickToken extends IRCToken
{

    private String nickName;

    /**
     * @param token content of the token
     */
    protected NickToken( String token )
    {
        super( token );
    }

    /**
     * to get the content of the token
     */
    public String getNickName()
    {
        return nickName;
    }

    /**
     * to parse the content of the content and to get the corresponding NickToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new NickToken( sToken ) );
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
            if ( Pattern.matches( "[a-zA-Z][[a-zA-Z] | \\d | \\-|\\[|\\]|\\\\|\\`|\\^|\\{|\\}]*", sToken ) )
            {
                setNickName( sToken );
            }
            else
            {
                throw new FatalException( "The token 'nickToken' is incorrect !!" );
            }
        }
        else
        {
            throw new FatalException( "The token 'nickToken' is null !!" );
        }
    }

    /**
     * to set the content of the token
     */
    private void setNickName( String nickName )
    {
        this.nickName = nickName;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        return nickName == null ? VOID : nickName;
    }
}

