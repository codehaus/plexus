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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.codehaus.plexus.cache.CacheStatistics;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * EhcacheCache 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.cache.Cache" role-hint="ehcache"
 */
public class EhcacheCache
    extends AbstractLogEnabled
    implements org.codehaus.plexus.cache.Cache, Initializable, Disposable
{
    class Stats
        implements CacheStatistics
    {
        public void clear()
        {
            ehcache.clearStatistics();
        }

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

        public long getCacheHits()
        {
            return ehcache.getStatistics().getCacheHits();
        }

        public long getCacheMiss()
        {
            return ehcache.getStatistics().getCacheMisses();
        }

        public long getSize()
        {
            return ehcache.getMemoryStoreSize() + ehcache.getDiskStoreSize();
        }

    }

    /**
     * how often to run the disk store expiry thread. A large number of 120 seconds plus is recommended
     * 
     * @plexus.configuration default-value="600"
     */
    private long diskExpiryThreadIntervalSeconds = 600;

    /**
     * Whether to persist the cache to disk between JVM restarts.
     * 
     * @plexus.configuration default-value="true"
     */
    private boolean diskPersistent = true;

    /**
     * Location on disk for the ehcache store.
     * 
     * @plexus.configuration default-value="${java.io.tmpdir}/ehcache"
     */
    private String diskStorePath = System.getProperty( "java.io.tmpdir" ) + "/ehcache";

    /**
     * @plexus.configuration default-value="false"
     */
    private boolean eternal = false;

    /**
     * @plexus.configuration default-value="1000"
     */
    private int maxElementsInMemory = 1000;

    /**
     * @plexus.configuration default-value="LRU"
     */
    private String memoryEvictionPolicy = "LRU";

    /**
     * @plexus.configuration default-value="cache"
     */
    private String name = "cache";

    /**
     * Flag indicating when to use the disk store.
     * 
     * @plexus.configuration default-value="false"
     */
    private boolean overflowToDisk = false;

    /**
     * @plexus.configuration default-value="600"
     */
    private int timeToIdleSeconds = 600;

    /**
     * @plexus.configuration default-value="300"
     */
    private int timeToLiveSeconds = 300;

    private CacheManager cacheManager;

    private net.sf.ehcache.Cache ehcache;

    private Stats stats;

    public void clear()
    {
        ehcache.removeAll();
        stats.clear();
    }

    public void dispose()
    {
        if ( cacheManager.getStatus().equals( Status.STATUS_ALIVE ) )
        {
            getLogger().info( "Disposing cache: " + ehcache );
            if ( this.ehcache != null )
            {
                cacheManager.removeCache( this.ehcache.getName() );
                ehcache = null;
            }
        }
        else
        {
            getLogger().debug( "Not disposing cache, because cacheManager is not alive: " + ehcache );
        }
    }

    public Object get( Object key )
    {
        Element elem = ehcache.get( key );
        if ( elem == null )
        {
            return null;
        }
        return elem.getObjectValue();
    }

    public long getDiskExpiryThreadIntervalSeconds()
    {
        return diskExpiryThreadIntervalSeconds;
    }

    public String getDiskStorePath()
    {
        return diskStorePath;
    }

    public int getMaxElementsInMemory()
    {
        return maxElementsInMemory;
    }

    public String getMemoryEvictionPolicy()
    {
        return memoryEvictionPolicy;
    }

    public MemoryStoreEvictionPolicy getMemoryStoreEvictionPolicy()
    {
        return MemoryStoreEvictionPolicy.fromString( memoryEvictionPolicy );
    }

    public String getName()
    {
        return name;
    }

    public CacheStatistics getStatistics()
    {
        return stats;
    }

    public int getTimeToIdleSeconds()
    {
        return timeToIdleSeconds;
    }

    public int getTimeToLiveSeconds()
    {
        return timeToLiveSeconds;
    }

    public boolean hasKey( Object key )
    {
        return ehcache.isKeyInCache( key );
    }

    public void initialize()
        throws InitializationException
    {
        stats = new Stats();
        cacheManager = CacheManager.getInstance();

        if ( cacheManager.cacheExists( getName() ) )
        {
            throw new InitializationException( "A previous cache with name [" + getName() + "] exists." );
        }

        ehcache = new Cache( getName(), getMaxElementsInMemory(), getMemoryStoreEvictionPolicy(), isOverflowToDisk(),
                             getDiskStorePath(), isEternal(), getTimeToLiveSeconds(), getTimeToIdleSeconds(),
                             isDiskPersistent(), getDiskExpiryThreadIntervalSeconds(), null );

        cacheManager.addCache( ehcache );
    }

    public boolean isDiskPersistent()
    {
        return diskPersistent;
    }

    public boolean isEternal()
    {
        return eternal;
    }

    public boolean isOverflowToDisk()
    {
        return overflowToDisk;
    }

    public void register( Object key, Object value )
    {
        ehcache.put( new Element( key, value ) );
    }
    
    public Object put( Object key, Object value )
    {
        // Multiple steps done to satisfy Cache API requirement for Previous object return.
        Element elem = null;
        Object previous = null;
        elem = ehcache.get( key );
        if ( elem != null )
        {
            previous = elem.getObjectValue();
        }
        elem = new Element( key, value );
        ehcache.put( elem );
        return previous;
    }

    public Object remove( Object key )
    {
        Element elem = null;
        Object previous = null;
        elem = ehcache.get( key );
        if ( elem != null )
        {
            previous = elem.getObjectValue();
            ehcache.remove( key );
        }

        return previous;
    }

    public void setDiskExpiryThreadIntervalSeconds( long diskExpiryThreadIntervalSeconds )
    {
        this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
    }

    public void setDiskPersistent( boolean diskPersistent )
    {
        this.diskPersistent = diskPersistent;
    }

    public void setDiskStorePath( String diskStorePath )
    {
        this.diskStorePath = diskStorePath;
    }

    public void setEternal( boolean eternal )
    {
        this.eternal = eternal;
    }

    public void setMaxElementsInMemory( int maxElementsInMemory )
    {
        this.maxElementsInMemory = maxElementsInMemory;
    }

    public void setMemoryEvictionPolicy( String memoryEvictionPolicy )
    {
        this.memoryEvictionPolicy = memoryEvictionPolicy;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setOverflowToDisk( boolean overflowToDisk )
    {
        this.overflowToDisk = overflowToDisk;
    }

    public void setTimeToIdleSeconds( int timeToIdleSeconds )
    {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public void setTimeToLiveSeconds( int timeToLiveSeconds )
    {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }
}
