package org.codehaus.plexus.cache.test.examples.wine;

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

import org.codehaus.plexus.cache.CacheBuilder;

/**
 * @since 5 February, 2007
 * @version $Id$
 * @author <a href="mailto:Olivier.LAMY@accor.com">Olivier Lamy</a>
 * @plexus.component
 *   role="org.codehaus.plexus.cache.test.examples.wine.WineService" role-hint="default"
 */
public class DefaultWineService
    implements WineService
{
    /**
     * @plexus.requirement
     *  role-hint="default"
     */
    private CacheBuilder cacheBuilder;

    /**
     * @plexus.requirement
     *  role-hint="mock"
     */
    private WineDao wineDao;

    public Wine getWine( String name )
    {
        Wine wine = (Wine) cacheBuilder.getCache( Wine.class ).get( name );
        if ( wine == null )
        {
            wine = wineDao.getWine( name );
            cacheBuilder.getCache( Wine.class ).put( name, wine );
        }
        return wine;
    }

}
