/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.exception;

import org.codehaus.plexus.ircd.utils.Replies;

public class IRCException extends Exception
{

    private Replies replies;
    private String[] parameters;

    /**
     * @param replies the replies corresponding to the exception
     */
    public IRCException( Replies replies )
    {
        this.replies = replies;
    }

    /**
     * @param replies the replies corresponding to the exception
     * @param sParameters parameters needed by the replies
     */
    public IRCException( Replies replies, String[] sParameters )
    {
        this.replies = replies;
        parameters = sParameters;
    }

    /**
     * to get the parameters
     */
    public String[] getParameters()
    {
        return parameters;
    }

    /**
     * to get the replies
     */
    public Replies getReplies()
    {
        return replies;
    }
}

