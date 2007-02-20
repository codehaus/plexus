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
import org.codehaus.plexus.cache.Cache;
import org.codehaus.plexus.cache.CacheStatistics;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

/**
 * configuration file see https://whirlycache.dev.java.net/sample-whirlycache-xml-file.html
 * @since 5 feb. 07
 * @version $Id$
 * @author <a href="mailto:olamy@codehaus.org">Olivier Lamy</a>
 */
public class WhirlyCacheCache
    extends AbstractLogEnabled
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
                this.whirlyCache = CacheManager.getInstance().getCache( this.whirlyCacheName );
            }
        }
        catch ( CacheException e )
        {
            this.getLogger().error( "CacheException " + e.getMessage(), e );
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
        Object object = this.whirlyCache.retrieve( key );
        if ( object != null )
        {
            this.whirlyCacheStatistics.hit();
        }
        else
        {
            this.whirlyCacheStatistics.miss();
        }
        return object;
    }

    public CacheStatistics getStatistics()
    {
        return this.whirlyCacheStatistics;
    }

    public boolean hasKey( Object key )
    {
        // TODO if null increase/decrease statistics ?
        return this.whirlyCache.retrieve( key ) == null;
    }

    public Object put( Object key, Object value )
    {
        Object previous = this.whirlyCache.retrieve( key );
        // TODO long expireTime ?? if yes in component configuration
        // normaly handled with <tuner-sleeptime> in whirlycache.xml
        this.whirlyCache.store( key, value );
        return previous;
    }

    public Object remove( Object key )
    {
        Object previous = this.whirlyCache.retrieve( key );
        this.whirlyCache.remove( key );
        return previous;
    }

    public void register( Object key, Object value )
    {
        this.whirlyCache.store( key, value );

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

}
