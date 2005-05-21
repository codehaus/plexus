/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.ircbot;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface IrcBot
{
    String ROLE = IrcBot.class.getName();

    public boolean ircsend( String message );
}
