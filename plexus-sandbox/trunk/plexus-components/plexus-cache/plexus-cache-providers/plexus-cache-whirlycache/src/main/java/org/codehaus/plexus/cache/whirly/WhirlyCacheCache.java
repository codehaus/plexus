package org.codehaus.plexus.cache.whirly;

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
import com.whirlycott.cache.CacheConfiguration;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

import org.codehaus.plexus.cache.Cache;
import org.codehaus.plexus.cache.CacheStatistics;
import org.codehaus.plexus.cache.CacheableWrapper;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.util.Arrays;
import java.util.List;

/**
 * configuration file see https://whirlycache.dev.java.net/sample-whirlycache-xml-file.html
 * 
 * <p>
 * Configure the refreshTime in seconds value configure a ttl of object life in cache.
 * Object get( Object key ) :
 * <ul>
 *   <li> &lt; 0 : method will always return null (no cache)</li>
 *   <li> = 0 : first stored object will be return (infinite life in the cache)</li>
 *   <li> > 0 : after a live (stored time) of refreshTime the object will be remove from the cache 
 *              and a no object will be returned by the method</li> 
 * </ul>
 * </p>
 * @since 5 feb. 07
 * @version $Id$
 * @author <a href="mailto:olamy@codehaus.org">Olivier Lamy</a>
 */
public class WhirlyCacheCache
    implements Cache, Initializable
{
    private com.whirlycott.cache.Cache whirlyCache;

    private WhirlyCacheStatistics whirlyCacheStatistics;

    //---------------------------------------------
    // Configuration attributes
    //--------------------------------------------- 
    /**
     * @plexus.configuration
     */
    private String whirlyCacheName;
    
    /**
     * @plexus.configuration default-value="com.whirlycott.cache.impl.ConcurrentHashMapImpl"
     */
    private String backend = "com.whirlycott.cache.impl.ConcurrentHashMapImpl";
    
    /**
     * @plexus.configuration default-value="com.whirlycott.cache.policy.LFUMaintenancePolicy"
     */
    private String policy = "com.whirlycott.cache.policy.LFUMaintenancePolicy";
    
    /**
     * @plexus.configuration default-value="30"
     */
    private int tunerSleepTime = 30;
    
    /**
     * @plexus.configuration default-value="10000"
     */
    private int maxSize = 10000;

    /**
     * 
     * @plexus.configuration
     *  default-value="0"
     */
    private int refreshTime;

    //---------------------------------------------
    // Component lifecycle
    //---------------------------------------------     
    public void initialize()
        throws InitializationException
    {
        try
        {
            if ( this.whirlyCacheName == null )
            {
                this.whirlyCache = CacheManager.getInstance().getCache();
            }
            else
            {
                String cacheNames[] = CacheManager.getInstance().getCacheNames();
                List cacheNameList = Arrays.asList( cacheNames );
                
                if( cacheNameList.contains( this.whirlyCacheName ))
                {
                    // Cache exists! use it.
                    this.whirlyCache = CacheManager.getInstance().getCache( this.whirlyCacheName );
                }
                else
                {
                    // Cache needs to be created.
                    CacheConfiguration cacheConfig = new CacheConfiguration();
                    cacheConfig.setName( this.whirlyCacheName );
                    cacheConfig.setBackend( this.backend );
                    cacheConfig.setPolicy( this.policy );
                    cacheConfig.setTunerSleepTime( this.tunerSleepTime );
                    cacheConfig.setMaxSize( this.maxSize );
                    
                    this.whirlyCache = CacheManager.getInstance().createCache( cacheConfig );
                }
            }
        }
        catch ( CacheException e )
        {
            throw new InitializationException( "CacheException " + e.getMessage(), e );
        }
        this.whirlyCacheStatistics = new WhirlyCacheStatistics( this.whirlyCache );
    }

    //---------------------------------------------
    // Interface implementation
    //---------------------------------------------    

    public void clear()
    {
        this.whirlyCache.clear();

    }

    public Object get( Object key )
    {
        CacheableWrapper retValue = (CacheableWrapper) this.whirlyCache.retrieve( key );

        if ( retValue != null )
        {
            if ( needRefresh( retValue ) )
            {
                this.whirlyCacheStatistics.miss();
                return null;
            }
            this.whirlyCacheStatistics.hit();
        }
        else
        {
            this.whirlyCacheStatistics.miss();
            return null;
        }
        return retValue.getValue();
    }

    public CacheStatistics getStatistics()
    {
        return this.whirlyCacheStatistics;
    }

    public boolean hasKey( Object key )
    {
        // TODO if null increase/decrease statistics ?
        // prevent search
        if ( !this.isCacheAvailable() )
        {
            return false;
        }
        return this.whirlyCache.retrieve( key ) == null;
    }

    public Object put( Object key, Object value )
    {
        Object previous = this.whirlyCache.retrieve( key );
        this.whirlyCache.store( key, new CacheableWrapper( value, System.currentTimeMillis() ) );
        return previous;
    }

    public Object remove( Object key )
    {
        CacheableWrapper previous = (CacheableWrapper) this.whirlyCache.retrieve( key );
        this.whirlyCache.remove( key );
        return previous == null ? null : previous.getValue();
    }

    public void register( Object key, Object value )
    {
        this.whirlyCache.store( key, new CacheableWrapper( value, System.currentTimeMillis() ) );
    }

    protected boolean needRefresh( CacheableWrapper cacheableWrapper )
    {
        if ( cacheableWrapper == null )
        {
            return true;
        }
        if ( this.getRefreshTime() == 0 )
        {
            return false;
        }
        boolean result = ( System.currentTimeMillis() - cacheableWrapper.getStoredTime() ) > ( this.getRefreshTime() * 1000 );
        return result;
    }

    //---------------------------------------------
    // getters/setters
    //---------------------------------------------     

    public String getWhirlyCacheName()
    {
        return whirlyCacheName;
    }

    public void setWhirlyCacheName( String whirlyCacheName )
    {
        this.whirlyCacheName = whirlyCacheName;
    }

    /** 
     * @see org.codehaus.plexus.cache.Cache#getRefreshTime()
     */
    public int getRefreshTime()
    {
        return refreshTime;
    }

    /** 
     * @see org.codehaus.plexus.cache.Cache#setRefreshTime(int)
     */
    public void setRefreshTime( int refreshTime )
    {
        this.refreshTime = refreshTime;
    }

    /**
     * @return
     */
    protected boolean isCacheAvailable()
    {
        return this.getRefreshTime() >= 0;
    }

    public String getBackend()
    {
        return backend;
    }

    public void setBackend( String backend )
    {
        this.backend = backend;
    }

    public int getMaxSize()
    {
        return maxSize;
    }

    public void setMaxSize( int maxSize )
    {
        this.maxSize = maxSize;
    }

    public String getPolicy()
    {
        return policy;
    }

    public void setPolicy( String policy )
    {
        this.policy = policy;
    }

    public int getTunerSleepTime()
    {
        return tunerSleepTime;
    }

    public void setTunerSleepTime( int tunerSleepTime )
    {
        this.tunerSleepTime = tunerSleepTime;
    }
}
