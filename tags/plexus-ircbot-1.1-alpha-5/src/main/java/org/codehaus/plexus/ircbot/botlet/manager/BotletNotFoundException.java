/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.ircbot.botlet.manager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class BotletNotFoundException
    extends Exception
{
    public BotletNotFoundException( String message )
    {
        super( message );
    }

    public BotletNotFoundException( Throwable cause )
    {
        super( cause );
    }

    public BotletNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
