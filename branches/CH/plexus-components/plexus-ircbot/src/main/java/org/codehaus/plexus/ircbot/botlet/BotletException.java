/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.ircbot.botlet;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class BotletException
    extends Exception
{
    public BotletException( String message )
    {
        super( message );
    }

    public BotletException( Throwable cause )
    {
        super( cause );
    }

    public BotletException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
