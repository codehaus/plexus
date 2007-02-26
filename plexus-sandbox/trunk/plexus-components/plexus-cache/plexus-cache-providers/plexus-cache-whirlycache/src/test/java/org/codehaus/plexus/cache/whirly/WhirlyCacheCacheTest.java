/**
 * Copyright @2007 - Accor - All Rights Reserved
 * www.accorhotels.com
 */
package org.codehaus.plexus.cache.whirly;

import org.codehaus.plexus.cache.Cache;
import org.codehaus.plexus.cache.test.AbstractCacheTestCase;

/**
 * 
 * @author <a href="mailto:Olivier.LAMY@accor.com">Olivier Lamy</a>
 * @since 5 February, 2007
 * @version $Id$
 */
public class WhirlyCacheCacheTest
    extends AbstractCacheTestCase
{
    public String getProviderHint()
    {
        return "whirlycache";
    }

    public Cache getAlwaysRefresCache()
        throws Exception
    {
        return (Cache) getContainer().lookup( Cache.ROLE, "alwaysrefresh" );
    }

    public Cache getNeverRefresCache()
        throws Exception
    {
        return (Cache) getContainer().lookup( Cache.ROLE, "neverrefresh" );
    }

    public Cache getOneSecondRefresCache()
        throws Exception
    {
        return (Cache) getContainer().lookup( Cache.ROLE, "onesecondrefresh" );
    }

    public Cache getTwoSecondRefresCache()
        throws Exception
    {
        return (Cache) getContainer().lookup( Cache.ROLE, "twosecondrefresh" );
    }

    public Class getCacheClass()
    {
        return WhirlyCacheCache.class;
    }
}
