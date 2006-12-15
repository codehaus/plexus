/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

import java.util.regex.Pattern;

public class UserToken extends IRCToken
{

    private String userName;

    /**
     * @param token content of the token
     */
    protected UserToken( String token )
    {
        super( token );
    }

    /**
     * to parse the content of the content and to get the corresponding UserToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new UserToken( sToken ) );
    }

    /**
     * to get the content of the token
     */
    public String getUserName()
    {
        return userName;
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
            if ( Pattern.matches( "[^ |\015|\012|:|#|&|*|$|@][^ |\015|\012]+", sToken ) )
            {
                setUserName( sToken );
            }
            else
            {
                throw new FatalException( "The token 'userToken' is incorrect !!" );
            }
        }
        else
        {
            throw new FatalException( "The token 'userToken' is null !!" );
        }
    }

    /**
     * to set the content of the token
     */
    private void setUserName( String userName )
    {
        this.userName = userName;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        return userName == null ? VOID : userName;
    }
}
