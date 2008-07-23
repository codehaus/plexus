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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.cache.Cache;
import org.codehaus.plexus.cache.CacheStatistics;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * For configuration see documentation : http://opensymphony.com/oscache/wiki/Configuration.html
 * 
 * @since 3 February, 2007
 * @version $Id$
 * @author <a href="mailto:olamy@codehaus.org">Olivier Lamy</a>
 * 
 * @plexus.component role="org.codehaus.plexus.cache.Cache" role-hint="oscache"
 */
public class OsCacheCache
    extends AbstractLogEnabled
    implements Cache, Initializable
{

    private GeneralCacheAdministrator generalCacheAdministrator;

    private OsCacheStatistics osCacheStatistics;

    //---------------------------------------------
    // Configuration attributes
    //--------------------------------------------- 
    /**
     * use memory cache
     * 
     * @plexus.configuration default-value="true"
     */
    private boolean cacheMemory = true;

    /**
     * maximum item numbers default value -1 means unlimited
     * 
     * @plexus.configuration default-value="-1"
     */
    private int capacity = -1;

    /**
     * cache algorithm
     * @plexus.configuration
     */
    private String cacheAlgorithm;

    /**
     * @plexus.configuration default-value="false"
     */
    private boolean blockingCache = false;

    /**
     * @plexus.configuration default-value="false"
     */
    private boolean cacheUnlimitedDisk = false;

    /**
     * @plexus.configuration
     */
    private String cachePersistenceClass;

    /**
     * @plexus.configuration
     */
    private String cachePath;

    /**
     * @plexus.configuration default-value="false"
     */
    private boolean cachePersistenceOverflowOnly = false;

    /**
     * A default one will be added to provided CacheStatistics
     * 
     * @plexus.configuration
     */
    private List cacheEventListeners;

    private String cacheKey;

    /**
     * @plexus.configuration default-value="false"
     */
    private boolean cacheUseHostDomainInKey = false;

    /**
     * in order to add some other osCache properties
     */
    private Map additionnalProperties;

    /**
     * default Value {@link CacheEntry#INDEFINITE_EXPIRY}
     * @plexus.configuration
     */
    private int refreshPeriod = CacheEntry.INDEFINITE_EXPIRY;

    //---------------------------------------------
    // Component lifecycle
    //---------------------------------------------    
    public void initialize()
        throws InitializationException
    {
        Properties cacheProperties = new Properties();
        if ( this.getAdditionnalProperties() != null )
        {
            cacheProperties.putAll( this.getAdditionnalProperties() );
        }
        if ( this.getCacheAlgorithm() != null )
        {
            cacheProperties.put( GeneralCacheAdministrator.CACHE_ALGORITHM_KEY, this.getCacheAlgorithm() );
        }
        cacheProperties.put( GeneralCacheAdministrator.CACHE_BLOCKING_KEY, Boolean.toString( this.isBlockingCache() ) );
        cacheProperties.put( GeneralCacheAdministrator.CACHE_CAPACITY_KEY, Integer.toString( this.getCapacity() ) );
        cacheProperties.put( GeneralCacheAdministrator.CACHE_DISK_UNLIMITED_KEY, Boolean.toString( this
            .isCacheUnlimitedDisk() ) );

        String cacheEventListenersAsString = this.getCacheEventListenersAsString();
        if ( cacheEventListenersAsString != null )
        {
            cacheProperties
                .put( GeneralCacheAdministrator.CACHE_ENTRY_EVENT_LISTENERS_KEY, cacheEventListenersAsString );
        }
        cacheProperties.put( GeneralCacheAdministrator.CACHE_MEMORY_KEY, Boolean.toString( this.isCacheMemory() ) );
        cacheProperties.put( GeneralCacheAdministrator.CACHE_PERSISTENCE_OVERFLOW_KEY, Boolean.toString( this
            .isCachePersistenceOverflowOnly() ) );

        if ( this.getCachePersistenceClass() != null )
        {
            cacheProperties.put( GeneralCacheAdministrator.PERSISTENCE_CLASS_KEY, this.getCachePersistenceClass() );
        }

        cacheProperties.put( "cache.unlimited.disk", Boolean.toString( this.isCacheUnlimitedDisk() ) );
        if ( this.getCachePath() != null )
        {
            cacheProperties.put( "cache.path", this.getCachePath() );
        }
        if ( this.getCacheKey() != null )
        {
            cacheProperties.put( "cache.key", this.getCacheKey() );
        }
        cacheProperties.put( "cache.use.host.domain.in.key", Boolean.toString( this.isCacheUseHostDomainInKey() ) );
        this.generalCacheAdministrator = new GeneralCacheAdministrator( cacheProperties );
        this.osCacheStatistics = new OsCacheStatistics( this.generalCacheAdministrator.getCache() );
    }

    //---------------------------------------------
    // Interface implementation
    //---------------------------------------------

    public void clear()
    {
        this.generalCacheAdministrator.flushAll();
    }

    public Object get( Object key )
    {
        try
        {
            Object object = this.generalCacheAdministrator.getFromCache( key.toString(), this.getRefreshPeriod() );
            if ( object != null )
            {
                this.osCacheStatistics.hit();
            }
            else
            {
                this.osCacheStatistics.miss();
            }
            return object;
        }
        catch ( NeedsRefreshException e )
        {
            this.generalCacheAdministrator.cancelUpdate( key.toString() );
            this.osCacheStatistics.miss();
            return null;
        }
    }

    public CacheStatistics getStatistics()
    {
        // osCacheStatistics to update ??
        return this.osCacheStatistics;
    }

    public boolean hasKey( Object key )
    {
        // TODO if null increase/decrease statistics ?
        return this.get( key ) == null;
    }

    public void register( Object key, Object value )
    {
        this.generalCacheAdministrator.putInCache( key.toString(), value );
    }
    
    public Object put( Object key, Object value )
    {
        Object previous = null;
        try
        {
            previous = this.generalCacheAdministrator.getFromCache( key.toString(), this.getRefreshPeriod() );
        }
        catch ( NeedsRefreshException e )
        {
            // ignore this because the content will be updated
        }
        this.generalCacheAdministrator.putInCache( key.toString(), value );
        return previous;
    }

    public Object remove( Object key )
    {
        Object previous = null;
        try
        {
            previous = this.generalCacheAdministrator.getFromCache( key.toString(), this.getRefreshPeriod() );

        }
        catch ( NeedsRefreshException e )
        {
            // ignore this because the content will be updated
        }
        this.generalCacheAdministrator.flushEntry( key.toString() );
        return previous;
    }

    //---------------------------------------------
    // getters/setters
    //---------------------------------------------    

    public Map getAdditionnalProperties()
    {
        return additionnalProperties;
    }

    public void setAdditionnalProperties( Map additionnalProperties )
    {
        this.additionnalProperties = additionnalProperties;
    }

    public boolean isBlockingCache()
    {
        return blockingCache;
    }

    public void setBlockingCache( boolean blockingCache )
    {
        this.blockingCache = blockingCache;
    }

    public String getCacheAlgorithm()
    {
        return cacheAlgorithm;
    }

    public void setCacheAlgorithm( String cacheAlgorithm )
    {
        this.cacheAlgorithm = cacheAlgorithm;
    }

    public List getCacheEventListeners()
    {
        return cacheEventListeners;
    }

    /**
     * @return list values in a String separated with comma 
     */
    private String getCacheEventListenersAsString()
    {
        if ( this.getCacheEventListeners() == null )
        {
            return null;
        }
        if ( this.getCacheEventListeners().isEmpty() )
        {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for ( Iterator iterator = this.getCacheEventListeners().iterator(); iterator.hasNext(); )
        {
            stringBuffer.append( iterator.next() ).append( ',' );
        }
        return stringBuffer.toString().substring( 0, stringBuffer.toString().length() - 1 );
    }

    public void setCacheEventListeners( List cacheEventListeners )
    {
        this.cacheEventListeners = cacheEventListeners;
    }

    public String getCacheKey()
    {
        return cacheKey;
    }

    public void setCacheKey( String cacheKey )
    {
        this.cacheKey = cacheKey;
    }

    public boolean isCacheMemory()
    {
        return cacheMemory;
    }

    public void setCacheMemory( boolean cacheMemory )
    {
        this.cacheMemory = cacheMemory;
    }

    public String getCachePath()
    {
        return cachePath;
    }

    public void setCachePath( String cachePath )
    {
        this.cachePath = cachePath;
    }

    public String getCachePersistenceClass()
    {
        return cachePersistenceClass;
    }

    public void setCachePersistenceClass( String cachePersistenceClass )
    {
        this.cachePersistenceClass = cachePersistenceClass;
    }

    public boolean isCachePersistenceOverflowOnly()
    {
        return cachePersistenceOverflowOnly;
    }

    public void setCachePersistenceOverflowOnly( boolean cachePersistenceOverflowOnly )
    {
        this.cachePersistenceOverflowOnly = cachePersistenceOverflowOnly;
    }

    public boolean isCacheUnlimitedDisk()
    {
        return cacheUnlimitedDisk;
    }

    public void setCacheUnlimitedDisk( boolean cacheUnlimitedDisk )
    {
        this.cacheUnlimitedDisk = cacheUnlimitedDisk;
    }

    public boolean isCacheUseHostDomainInKey()
    {
        return cacheUseHostDomainInKey;
    }

    public void setCacheUseHostDomainInKey( boolean cacheUseHostDomainInKey )
    {
        this.cacheUseHostDomainInKey = cacheUseHostDomainInKey;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public void setCapacity( int capacity )
    {
        this.capacity = capacity;
    }

    public GeneralCacheAdministrator getGeneralCacheAdministrator()
    {
        return generalCacheAdministrator;
    }

    public void setGeneralCacheAdministrator( GeneralCacheAdministrator generalCacheAdministrator )
    {
        this.generalCacheAdministrator = generalCacheAdministrator;
    }

    public int getRefreshPeriod()
    {
        return refreshPeriod;
    }

    public void setRefreshPeriod( int refreshPeriod )
    {
        this.refreshPeriod = refreshPeriod;
    }

}
