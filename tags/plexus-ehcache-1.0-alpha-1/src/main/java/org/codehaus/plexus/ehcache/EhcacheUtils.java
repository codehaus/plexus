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
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import org.codehaus.plexus.logging.Logger;

/**
 * EhcacheUtils 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class EhcacheUtils
{
    public static void clearAllCaches( Logger logger )
    {
        try
        {
            CacheManager cacheManager = CacheManager.getInstance();

            String names[] = cacheManager.getCacheNames();
            for ( int i = 0; i < names.length; i++ )
            {
                try
                {
                    Cache cache = cacheManager.getCache( names[i] );
                    cache.removeAll();
                }
                catch ( IllegalStateException e )
                {
                    logger.warn( "Unable to remove all elements from cache [" + names[i] + "]", e );
                }
                catch ( CacheException e )
                {
                    logger.warn( "Unable to remove all elements from cache [" + names[i] + "]", e );
                }
            }
        }
        catch ( IllegalStateException e )
        {
            logger.error( "Unable to clear all caches: ", e );
        }
    }
}
