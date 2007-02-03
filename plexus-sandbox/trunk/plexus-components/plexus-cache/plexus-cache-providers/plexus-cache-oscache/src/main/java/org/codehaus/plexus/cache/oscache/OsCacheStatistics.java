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
import org.codehaus.plexus.cache.CacheStatistics;

import com.opensymphony.oscache.base.Cache;

/**
 * @since 3 févr. 07
 * @version $Id$
 * @author <a href="mailto:Olivier.LAMY@accor.com">Olivier Lamy</a>
 */
public class OsCacheStatistics
    implements CacheStatistics
{

    private Cache cache;

    private long cacheHits = 0;

    private long cacheMiss = 0;

    protected OsCacheStatistics( Cache cache )
    {
        this.cache = cache;
    }

    //-------------------------------------------------------
    // CacheStatistics methods
    //-------------------------------------------------------

    /** 
     * @see org.codehaus.plexus.cache.CacheStatistics#clear()
     */
    public void clear()
    {
        this.cacheHits = 0;
        this.cacheMiss = 0;
    }

    /** 
     * @see org.codehaus.plexus.cache.CacheStatistics#getCacheHitRate()
     */
    public double getCacheHitRate()
    {
        double hits = getCacheHits();
        double miss = getCacheMiss();

        if ( ( hits == 0 ) && ( hits == 0 ) )
        {
            return 0.0;
        }

        return (double) hits / (double) ( hits + miss );
    }

    /** 
     * @see org.codehaus.plexus.cache.CacheStatistics#getCacheHits()
     */
    public long getCacheHits()
    {
        return this.cacheHits;
    }

    public void increaseCacheHits()
    {
        this.cacheHits++;
    }

    /** 
     * @see org.codehaus.plexus.cache.CacheStatistics#getCacheMiss()
     */
    public long getCacheMiss()
    {
        return this.cacheMiss;
    }

    public void increaseCcacheMiss()
    {
        this.cacheMiss++;
    }

    /** 
     * @see org.codehaus.plexus.cache.CacheStatistics#getSize()
     */
    public long getSize()
    {
        return this.cache.getNbEntries();
    }

}
