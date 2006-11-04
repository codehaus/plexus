/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.exception;

public class FatalException extends Exception
{

    private String message;

    /**
     * @param message the exception's message
     */
    public FatalException( String message )
    {
        this.message = message;
    }

    /**
     * to get the exception's message
     */
    public String getMessage()
    {
        return message;
    }
}

