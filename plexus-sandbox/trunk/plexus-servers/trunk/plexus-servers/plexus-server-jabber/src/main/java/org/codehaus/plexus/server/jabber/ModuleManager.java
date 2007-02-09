/*
 * BSD License http://open-im.org/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.org
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.org/
 */
package org.codehaus.plexus.server.jabber;

import org.codehaus.plexus.server.jabber.module.ServerModule;


/**
 * @author AlAg
 * @author PV
 * @version 1.0
 */
public interface ModuleManager
{
    /**
     * Registers a server module.
     *
     * @param module The module to be inserted.
     */
    public void registerModule( ServerModule module );

    /**
     * Unregisters a server module.
     *
     * @param module The module to be removed.
     */
    public void unregisterModule( ServerModule module );

    /** @return An array of server modules. */
    public ServerModule[] getModules();

    /**
     * @param discoveryId Id of the searched module.
     * @return A found module or null.
     */
    public ServerModule getModule( String discoveryId );

    /**
     * @param hostname
     * @return A server module with the specified hostname or null when not found.
     */
    public ServerModule getModuleByHostname( String hostname );
}


