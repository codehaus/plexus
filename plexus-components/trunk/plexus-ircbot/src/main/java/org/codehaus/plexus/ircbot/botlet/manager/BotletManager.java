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
public interface BotletManager
{
    Botlet lookup( String id )
        throws BotletNotFoundException;

    Map getBotlets();
}
