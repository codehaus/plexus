package org.codehaus.plexus.webdav;

/*
 * Copyright 2001-2007 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * DefaultDavServerManager 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.webdav.DavServerManager" role-hint="default"
 */
public class DefaultDavServerManager
    implements DavServerManager, Contextualizable, Disposable
{
    private PlexusContainer container;

    /**
     * The provider hint for created server components.
     * 
     * @plexus.configuration default-value="simple"
     */
    private String providerHint;

    private Map servers;

    public DefaultDavServerManager()
    {
        servers = new HashMap();
    }

    public DavServerComponent createServer( String prefix, File rootDirectory )
        throws DavServerException
    {
        if ( servers.containsKey( prefix ) )
        {
            throw new DavServerException( "Unable to create a new server on a pre-existing prefix [" + prefix + "]" );
        }

        try
        {
            Object o = container.lookup( DavServerComponent.ROLE, providerHint );
            DavServerComponent server = (DavServerComponent) o;
            server.setPrefix( prefix );
            server.setRootDirectory( rootDirectory );

            servers.put( prefix, server );

            return server;
        }
        catch ( ComponentLookupException e )
        {
            throw new DavServerException( "Unable to lookup component [" + DavServerComponent.ROLE + "], hint ["
                + providerHint + "]." );
        }
    }

    public DavServerComponent getServer( String prefix )
    {
        return (DavServerComponent) servers.get( prefix );
    }

    public void removeServer( String prefix )
    {
        servers.remove( prefix );
    }

    public Collection getServers()
    {
        return servers.values();
    }

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void dispose()
    {
        Iterator it = getServers().iterator();
        while ( it.hasNext() )
        {
            try
            {
                container.release( it.next() );
            }
            catch ( ComponentLifecycleException e )
            {
                /* ignore */
            }
        }
    }
}
