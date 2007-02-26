package org.codehaus.plexus.cache.oscache;

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
import org.codehaus.plexus.cache.CacheHints;
import org.codehaus.plexus.cache.factory.CacheCreator;

import java.io.File;

/**
 * OsCacheCreator 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class OsCacheCreator
    implements CacheCreator
{

    public Cache createCache( CacheHints hints )
    {
        OsCacheCache cache = new OsCacheCache();
        
        cache.setCacheKey( hints.getName() );

        cache.setCachePersistenceOverflowOnly( hints.isOverflowToDisk() );
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
                overflowPath = new File( tmpDir, "oscache/" + hints.getName() );
            }

            cache.setCachePath( overflowPath.getAbsolutePath() );
        }

        cache.setCapacity( hints.getMaxElements() );
        cache.setRefreshPeriod( hints.getIdleExpirationSeconds() );
        
        // Does not support:  hints.getMaxSecondsInCache()
        
        return cache;
    }

}
