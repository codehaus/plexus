package org.codehaus.plexus.cache.ehcache;

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

import org.apache.commons.lang.SystemUtils;
import org.codehaus.plexus.cache.Cache;
import org.codehaus.plexus.cache.CacheException;
import org.codehaus.plexus.cache.CacheHints;
import org.codehaus.plexus.cache.factory.CacheCreator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.io.File;

/**
 * EhcacheCreator - runtime creation of an ehcache. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class EhcacheCreator
    implements CacheCreator
{

    public Cache createCache( CacheHints hints )
        throws CacheException
    {
        EhcacheCache cache = new EhcacheCache();

        cache.setName( hints.getName() );

        cache.setDiskPersistent( hints.isOverflowToDisk() );
        if ( hints.isOverflowToDisk() )
        {
            File overflowPath = null;

            if ( hints.getDiskOverflowPath() != null )
            {
                overflowPath = hints.getDiskOverflowPath();
            }
            else
            {
                File tmpDir = SystemUtils.getJavaIoTmpDir();
                overflowPath = new File( tmpDir, "ehcache/" + hints.getName() );
            }

            cache.setDiskStorePath( overflowPath.getAbsolutePath() );
        }

        cache.setMaxElementsInMemory( hints.getMaxElements() );
        cache.setTimeToLiveSeconds( hints.getMaxSecondsInCache() );
        cache.setTimeToIdleSeconds( hints.getIdleExpirationSeconds() );

        try
        {
            cache.initialize();
        }
        catch ( InitializationException e )
        {
            throw new CacheException( "Unable to initialize EhcacheCache: " + e.getMessage(), e );
        }

        return cache;
    }
}
