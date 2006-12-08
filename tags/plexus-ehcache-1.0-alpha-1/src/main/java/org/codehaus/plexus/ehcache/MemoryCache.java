package org.codehaus.plexus.ehcache;

/*
 * Copyright 2001-2006 The Codehaus.
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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * MemoryCache 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.ehcache.EhcacheComponent"
 *                   role-hint="memory"
 */
public class MemoryCache
    extends AbstractEhcacheComponent
    implements EhcacheComponent, Initializable
{
    public void initialize()
        throws InitializationException
    {
        CacheManager cacheManager = CacheManager.getInstance();
        boolean overflowToDisk = false;

        if ( cacheManager.cacheExists( getName() ) )
        {
            throw new InitializationException( "A previous cache with name [" + getName() + "] exists." );
        }

        cache = new Cache( getName(), getMaxElementsInMemory(), getMemoryStoreEvictionPolicy(), overflowToDisk, null,
                           isEternal(), getTimeToLiveSeconds(), getTimeToIdleSeconds(), false, 100, null );

        cacheManager.addCache( cache );
    }
}
