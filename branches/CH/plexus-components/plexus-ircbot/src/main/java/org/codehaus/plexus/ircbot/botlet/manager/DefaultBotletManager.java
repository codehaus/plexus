/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.ircbot.botlet.manager;

import org.codehaus.plexus.ircbot.botlet.Botlet;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultBotletManager
    implements BotletManager
{
    private Map botlets;

    public Botlet lookup( String id )
        throws BotletNotFoundException
    {
        Botlet botlet = (Botlet) botlets.get( id );

        if ( botlet == null )
        {
            throw new BotletNotFoundException( "Cannot find a botlet with an id of " + id );
        }

        return botlet;
    }

    public Map getBotlets()
    {
        return botlets;
    }
}
