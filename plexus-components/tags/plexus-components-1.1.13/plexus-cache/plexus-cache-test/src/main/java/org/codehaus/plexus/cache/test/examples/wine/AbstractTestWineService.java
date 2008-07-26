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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.cache.Cache;

/**
 * 
 * @since 5 February, 2007
 * @version $Id$
 * @author <a href="mailto:Olivier.LAMY@accor.com">Olivier Lamy</a>
 */
public abstract class AbstractTestWineService
    extends PlexusTestCase
{
    public void testBordeaux()
        throws Exception
    {
        WineService wineService = (WineService) this.lookup( WineService.ROLE );
        Wine firstWine = wineService.getWine( "bordeaux" );
        Cache cache = (Cache) this.lookup( Cache.ROLE, Wine.class.getName() );
        assertEquals( 1, cache.getStatistics().getSize() );
        Wine secondWine = wineService.getWine( "bordeaux" );
        // testing on hashCode to be sure it's the same object
        assertEquals( firstWine.hashCode(), secondWine.hashCode() );

        // clear 
        cache.clear();
        secondWine = wineService.getWine( "bordeaux" );
        // after clear a new instance of wine has been created not same hashCode
        assertFalse( firstWine.hashCode() == secondWine.hashCode() );
    }
}
