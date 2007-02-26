package org.codehaus.plexus.cache.builder;

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
import org.codehaus.plexus.cache.Cache;
import org.codehaus.plexus.cache.impl.NoCacheCache;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * Ability to obtain cache  
 * 
 * @author <a href="mailto:Olivier.LAMY@accor.com">Olivier Lamy</a>
 * @version $Id$
 * @since 5 February, 2007
 * 
 * @plexus.component
 *   role="org.codehaus.plexus.cache.builder.CacheBuilder" role-hint="default"
 */
public class DefaultCacheBuilder
    extends AbstractLogEnabled
    implements CacheBuilder, Initializable, Contextualizable, Disposable

{
    private Cache defaultCache;

    private PlexusContainer plexusContainer;

    private Cache noCache = new NoCacheCache();

    public void contextualize( Context context )
        throws ContextException
    {
        this.plexusContainer = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void initialize()
        throws InitializationException
    {
        if ( this.plexusContainer.hasComponent( Cache.ROLE, "default" ) )
        {
            try
            {
                this.defaultCache = (Cache) this.plexusContainer.lookup( Cache.ROLE, "default" );
            }
            catch ( ComponentLookupException e )
            {
                String emsg = "error during lookup of Cache with role-hint default ";
                getLogger().warn( emsg, e );
                throw new InitializationException( emsg, e );
            }
        }
        else
        {
            getLogger().info( "Cache with role-hint default doesn't exists, default will be no cache" );
            this.defaultCache = new NoCacheCache();
        }
    }

    public Cache getCache( String roleHint )
    {
        try
        {
            return (Cache) this.plexusContainer.lookup( Cache.ROLE, roleHint );
        }
        catch ( ComponentLookupException e )
        {
            getLogger().warn( "error during lookup of Cache with roleHint " + roleHint );
        }
        
        return this.getDefaultCache();
    }

    public Cache getCache( Class clazz )
    {
        return this.getCache( clazz.getName() );
    }

    public void dispose()
    {
        // TODO dispose default ? 
        // Cache extends Disposable ?
    }

    public Cache getDefaultCache()
    {
        return defaultCache == null ? this.noCache : this.defaultCache;
    }
}
