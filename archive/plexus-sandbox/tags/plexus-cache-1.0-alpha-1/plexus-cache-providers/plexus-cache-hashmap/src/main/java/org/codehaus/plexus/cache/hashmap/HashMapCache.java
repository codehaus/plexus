package org.codehaus.plexus.cache.hashmap;

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

import org.codehaus.plexus.cache.Cache;
import org.codehaus.plexus.cache.CacheStatistics;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * HashMapCache - this is a Cache implementation taken from the Archiva project.
 * </p>
 * 
 * <p>
 * Original class written by Edwin Punzalan for purposes of addressing the 
 * jira ticket <a href="http://jira.codehaus.org/browse/MRM-39">MRM-39</a>
 * </p>   
 * 
 * @author Edwin Punzalan
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.cache.Cache" role-hint="hashmap"
 */
public class HashMapCache
    implements Cache, Initializable
{
    class Stats
        implements CacheStatistics
    {
        private long cacheHits;

        private long cacheMiss;

        public Stats()
        {
            this.cacheHits = 0;
            this.cacheMiss = 0;
        }

        public long getCacheHits()
        {
            return this.cacheHits;
        }

        public long getCacheMiss()
        {
            return this.cacheMiss;
        }

        public double getCacheHitRate()
        {
            return cacheHits == 0 && cacheMiss == 0 ? 0 : (double) cacheHits / (double) ( cacheHits + cacheMiss );
        }

        public long getSize()
        {
            synchronized ( cache )
            {
                return cache.size();
            }
        }

        public void hit()
        {
            this.cacheHits++;
        }

        public void miss()
        {
            this.cacheMiss++;
        }

        public void clear()
        {
            this.cacheHits = 0;
            this.cacheMiss = 0;
        }
    }

    private Map cache;

    /**
     * @plexus.configuration default-value="1.0"
     */
    private double cacheHitRatio = 1.0;

    /**
     * @plexus.configuration default-value="0"
     */
    private int cacheMaxSize = 0;

    private Stats stats;

    public HashMapCache()
    {
        
    }

    /**
     * Empty the cache and reset the cache hit rate
     */
    public void clear()
    {
        synchronized ( cache )
        {
            stats.clear();
            cache.clear();
        }
    }

    /**
     * Check for a cached object and return it if it exists. Returns null when the keyed object is not found
     *
     * @param key the key used to map the cached object
     * @return the object mapped to the given key, or null if no cache object is mapped to the given key
     */
    public Object get( Object key )
    {
        Object retValue = null;

        synchronized ( cache )
        {
            if ( cache.containsKey( key ) )
            {
                // remove and put: this promotes it to the top since we use a linked hash map
                retValue = cache.remove( key );

                cache.put( key, retValue );

                stats.hit();
            }
            else
            {
                stats.miss();
            }
        }

        return retValue;
    }

    public CacheStatistics getStatistics()
    {
        return stats;
    }

    /**
     * Check if the specified key is already mapped to an object.
     *
     * @param key the key used to map the cached object
     * @return true if the cache contains an object associated with the given key
     */
    public boolean hasKey( Object key )
    {
        boolean contains;
        synchronized ( cache )
        {
            contains = cache.containsKey( key );

            if ( contains )
            {
                stats.hit();
            }
            else
            {
                stats.miss();
            }
        }

        return contains;
    }

    public void initialize()
        throws InitializationException
    {
        stats = new Stats();

        if ( cacheMaxSize > 0 )
        {
            cache = new LinkedHashMap( cacheMaxSize );
        }
        else
        {
            cache = new LinkedHashMap();
        }
    }

    /**
     * Cache the given value and map it using the given key
     *
     * @param key   the object to map the valued object
     * @param value the object to cache
     */
    public Object put( Object key, Object value )
    {
        Object ret = null;

        // remove and put: this promotes it to the top since we use a linked hash map
        synchronized ( cache )
        {
            if ( cache.containsKey( key ) )
            {
                cache.remove( key );
            }

            ret = cache.put( key, value );
        }

        manageCache();

        return ret;
    }
    
    /**
     * Cache the given value and map it using the given key
     *
     * @param key   the object to map the valued object
     * @param value the object to cache
     */
    public void register( Object key, Object value )
    {
        // remove and put: this promotes it to the top since we use a linked hash map
        synchronized ( cache )
        {
            if ( cache.containsKey( key ) )
            {
                cache.remove( key );
            }

            cache.put( key, value );
        }

        manageCache();
    }

    public Object remove( Object key )
    {
        synchronized ( cache )
        {
            if ( cache.containsKey( key ) )
            {
                return cache.remove( key );
            }
        }

        return null;
    }

    private void manageCache()
    {
        synchronized ( cache )
        {
            Iterator iterator = cache.entrySet().iterator();
            if ( cacheMaxSize == 0 )
            {
                // desired HitRatio is reached, we can trim the cache to conserve memory
                if ( cacheHitRatio <= stats.getCacheHitRate() )
                {
                    iterator.next();
                    iterator.remove();
                }
            }
            else if ( cache.size() > cacheMaxSize )
            {
                // maximum cache size is reached
                while ( cache.size() > cacheMaxSize )
                {
                    iterator.next();
                    iterator.remove();
                }
            }
            else
            {
                // even though the max has not been reached, the desired HitRatio is already reached,
                // so we can trim the cache to conserve memory
                if ( cacheHitRatio <= stats.getCacheHitRate() )
                {
                    iterator.next();
                    iterator.remove();
                }
            }
        }
    }
}
