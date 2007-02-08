/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

import java.util.regex.Pattern;

public class MiddleToken extends IRCToken
{

    private String middleValue;

    /**
     * @param token content of the token
     */
    protected MiddleToken( String token )
    {
        super( token );
    }

    /**
     * to get the content of the token
     */
    public String getMiddleValue()
    {
        return middleValue;
    }

    /**
     * to parse the content of the content and to get the corresponding MiddleToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new MiddleToken( sToken ) );
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
            if ( Pattern.matches( "[^ |\015|\012|:][^ |\015|\012]*", sToken ) )
            {
                setMiddleValue( sToken );
            }
            else
            {
                throw new FatalException( "The token 'middleToken' is incorrect !!" );
            }
        }
        else
        {
            throw new FatalException( "The token 'middleToken' is null !!" );
        }
    }

    /**
     * to set the content of the token
     */
    private void setMiddleValue( String middleValue )
    {
        this.middleValue = middleValue;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        return middleValue == null ? VOID : middleValue;
    }
}
