/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

import java.util.regex.Pattern;

public class TrailingToken extends IRCToken
{

    private String trailingValue;

    /**
     * @param token content of the token
     */
    public TrailingToken( String token )
    {
        super( token );
    }

    /**
     * to parse the content of the content and to get the corresponding TrailingToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new TrailingToken( sToken ) );
    }

    /**
     * to get the content of the token
     */
    public String getTrailingValue()
    {
        return trailingValue;
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
            if ( Pattern.matches( "[^\015|\012]+", sToken ) )
            {
                setTrailingValue( sToken );
            }
            else
            {
                throw new FatalException( "The token 'trailingToken' is incorrect !!" );
            }
        }
        else
        {
            throw new FatalException( "The token 'trailingToken' is null !!" );
        }
    }

    /**
     * to set the content of the token
     */
    private void setTrailingValue( String trailingValue )
    {
        this.trailingValue = trailingValue;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        return trailingValue == null ? VOID : trailingValue;
    }
}
