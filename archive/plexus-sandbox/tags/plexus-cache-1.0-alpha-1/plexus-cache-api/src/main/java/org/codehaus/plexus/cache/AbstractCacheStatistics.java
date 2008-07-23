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
 * @since 5 févr. 07
 * @version $Id$
 * @author <a href="mailto:Olivier.LAMY@accor.com">Olivier Lamy</a>
 */
public abstract class AbstractCacheStatistics
    implements CacheStatistics
{

    private long cacheHits;

    private long cacheMiss;

    public AbstractCacheStatistics()
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

    public abstract long getSize();

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
