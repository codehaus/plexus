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
