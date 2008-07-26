package org.codehaus.plexus.cache;

import org.apache.commons.lang.CharSet;

import java.io.File;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * CacheHints 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class CacheHints
{
    /* To Other Plexus Cache Developers.
     * 
     * When considering if there is an appropriate value to put into this hints object,
     * consider if it is available in 2 or more cache providers.
     */

    /**
     * The name of the cache.
     */
    private String name;

    /**
     * To allow the cache to use the disk (or not) 
     */
    private boolean overflowToDisk = false;

    /**
     * The path on disk to overflow cache into.
     */
    private File diskOverflowPath = null;

    /**
     * To set the maximum number of elements that the cache tracks.
     */
    private int maxElements = 1000;

    /**
     * To set the maximum number of seconds that the element can exist in the cache, regardless of how 
     * popular and active it may be.
     */
    private int maxSecondsInCache = 0;

    /**
     * To set the maximum number of seconds that the element can exist idle in the cache. 
     */
    private int idleExpirationSeconds = 600;

    public CacheHints()
    {
        super();
    }

    public File getDiskOverflowPath()
    {
        return diskOverflowPath;
    }

    public int getIdleExpirationSeconds()
    {
        return idleExpirationSeconds;
    }

    public int getMaxElements()
    {
        return maxElements;
    }

    public int getMaxSecondsInCache()
    {
        return maxSecondsInCache;
    }

    public String getName()
    {
        return name;
    }

    public boolean isOverflowToDisk()
    {
        return overflowToDisk;
    }

    public void setDiskOverflowPath( File diskOverflowPath )
    {
        this.diskOverflowPath = diskOverflowPath;
    }

    public void setIdleExpirationSeconds( int idleExpirationSeconds )
    {
        this.idleExpirationSeconds = idleExpirationSeconds;
    }

    public void setMaxElements( int maxElements )
    {
        this.maxElements = maxElements;
    }

    public void setMaxSecondsInCache( int maxSecondsInCache )
    {
        this.maxSecondsInCache = maxSecondsInCache;
    }

    public void setName( String name )
    {
        CharSet illegal = CharSet.getInstance( ":/\\\"' " );
        for ( int i = 0; i < name.length(); i++ )
        {
            char c = name.charAt( i );
            if ( illegal.contains( c ) )
            {
                throw new IllegalArgumentException( "Name cannot contain characters from the set [" + illegal + "]" );
            }
        }
        this.name = name;
    }

    public void setOverflowToDisk( boolean overflowToDisk )
    {
        this.overflowToDisk = overflowToDisk;
    }
}
