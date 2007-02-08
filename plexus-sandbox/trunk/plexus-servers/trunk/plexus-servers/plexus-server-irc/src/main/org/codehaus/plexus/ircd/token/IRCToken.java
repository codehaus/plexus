/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;
import org.codehaus.plexus.ircd.utils.IRCString;

public abstract class IRCToken implements IRCString
{

    private String token;

    /**
     * @param token content of the token
     */
    protected IRCToken( String token )
    {
        this.token = token;
    }

    /**
     * to parse the content of the content and to get the corresponding Token
     * @param ircToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( IRCToken ircToken )
    {
        try
        {
            ircToken.parseToken( ircToken.token );
            return ircToken;
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    /**
     * to parse a token
     * @param sToken content the token parsed
     * @throws FatalException parsing's exception
     */
    public abstract void parseToken( String sToken ) throws FatalException;
}

