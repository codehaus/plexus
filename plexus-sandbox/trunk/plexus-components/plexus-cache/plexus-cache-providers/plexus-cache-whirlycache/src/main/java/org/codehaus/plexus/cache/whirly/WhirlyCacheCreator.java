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
import org.codehaus.plexus.cache.CacheHints;
import org.codehaus.plexus.cache.factory.CacheCreator;

/**
 * WhirlyCacheCreator 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class WhirlyCacheCreator
implements CacheCreator
{

    public Cache createCache( CacheHints hints )
    {
        WhirlyCacheCache cache = new WhirlyCacheCache();
        
        cache.setWhirlyCacheName( hints.getName() );
        cache.setRefreshTime( hints.getIdleExpirationSeconds() );

        /* Does not support the following
         * 
         * hints.isOverflowToDisk()
         * hints.getDiskOverflowPath()
         * hints.getMaxElements()
         * hints.getMaxSecondsInCache()
         */
        
        return cache;
    }

}
