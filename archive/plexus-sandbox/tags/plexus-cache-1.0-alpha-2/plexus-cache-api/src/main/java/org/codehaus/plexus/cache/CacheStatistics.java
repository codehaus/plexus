package org.codehaus.plexus.cache;

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

/**
 * CacheStatistics 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface CacheStatistics
{
    /**
     * Return the number of hits to content present in the cache.
     * 
     * @return the number of hits to content present in the cache.
     */
    public long getCacheHits();

    /**
     * Return the number of hits to keys that are not (yet) in the cache.
     * 
     * @return the number of requests for content missing from the cache.
     */
    public long getCacheMiss();

    /**
     * Compute for the efficiency of this cache.
     *
     * @return the ratio of cache hits to the cache misses to queries for cache objects
     */
    public double getCacheHitRate();

    /**
     * Return the size of the current cache.
     * 
     * @return the size of the current cache.
     */
    public long getSize();

    /**
     * Clear the statistics of the cache.
     */
    public void clear();
}
