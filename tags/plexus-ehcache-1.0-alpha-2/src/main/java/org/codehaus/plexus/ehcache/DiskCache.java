package org.codehaus.plexus.ehcache;

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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * DiskCache 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.ehcache.EhcacheComponent"
 * role-hint="disk"
 */
public class DiskCache
    extends AbstractEhcacheComponent
{
    /**
     * Where to use the disk store.
     * 
     * @plexus.configuration default-value="true"
     */
    private boolean overflowToDisk;

    /**
     * Location on disk for the ehcache store.
     * 
     * @plexus.configuration default-value="${java.io.tmpdir}/ehcache"
     */
    private String diskStorePath;

    /**
     * Whether to persist the cache to disk between JVM restarts.
     * 
     * @plexus.configuration default-value="true"
     */
    private boolean diskPersistent;

    /**
     * how often to run the disk store expiry thread. A large number of 120 seconds plus is recommended
     * 
     * @plexus.configuration default-value="600"
     */
    private long diskExpiryThreadIntervalSeconds;

    public void initialize()
        throws InitializationException
    {
        super.initialize();

        if ( cacheManager.cacheExists( getName() ) )
        {
            throw new InitializationException( "A previous cache with name [" + getName() + "] exists." );
        }

        cache = new Cache( getName(), getMaxElementsInMemory(), getMemoryStoreEvictionPolicy(), isOverflowToDisk(),
                           getDiskStorePath(), isEternal(), getTimeToLiveSeconds(), getTimeToIdleSeconds(),
                           isDiskPersistent(), getDiskExpiryThreadIntervalSeconds(), null );

        cacheManager.addCache( cache );
    }

    public String getDiskStorePath()
    {
        return diskStorePath;
    }

    public void setDiskStorePath( String diskStorePath )
    {
        this.diskStorePath = diskStorePath;
    }

    public boolean isOverflowToDisk()
    {
        return overflowToDisk;
    }

    public void setOverflowToDisk( boolean overflowToDisk )
    {
        this.overflowToDisk = overflowToDisk;
    }

    public long getDiskExpiryThreadIntervalSeconds()
    {
        return diskExpiryThreadIntervalSeconds;
    }

    public void setDiskExpiryThreadIntervalSeconds( long diskExpiryThreadIntervalSeconds )
    {
        this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
    }

    public boolean isDiskPersistent()
    {
        return diskPersistent;
    }

    public void setDiskPersistent( boolean diskPersistent )
    {
        this.diskPersistent = diskPersistent;
    }
}
