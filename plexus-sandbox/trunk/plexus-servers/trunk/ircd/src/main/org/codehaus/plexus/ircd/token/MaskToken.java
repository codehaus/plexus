/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

import java.util.regex.Pattern;

public class MaskToken extends IRCToken
{

    private String maskValue;

    /**
     * @param token content of the token
     */
    protected MaskToken( String token )
    {
        super( token );
    }

    /**
     * to get the content of the token
     */
    public String getMaskValue()
    {
        return maskValue;
    }

    /**
     * to parse the content of the content and to get the corresponding MaskToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new MaskToken( sToken ) );
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
            if ( Pattern.matches( "(*|$)[^\u0020|\u0007|,|\015|\012]+", sToken ) )
            {
                setMaskValue( sToken );
            }
            else
            {
                throw new FatalException( "The token 'maskToken' is incorrect !!" );
            }
        }
        else
        {
            throw new FatalException( "The token 'maskToken' is null !!" );
        }
    }

    /**
     * to set the content of the token
     */
    private void setMaskValue( String maskValue )
    {
        this.maskValue = maskValue;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        return maskValue == null ? VOID : maskValue;
    }
}
