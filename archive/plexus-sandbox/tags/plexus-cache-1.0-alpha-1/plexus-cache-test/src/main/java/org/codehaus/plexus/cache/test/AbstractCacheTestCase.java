package org.codehaus.plexus.cache.test;

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
import org.codehaus.plexus.cache.CacheStatistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * AbstractCacheTestCase 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractCacheTestCase
    extends PlexusTestCase
{
    protected Cache cache;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        cache = (Cache) lookup( Cache.ROLE, getProviderHint() );
    }

    protected void tearDown()
        throws Exception
    {
        release( cache );
        super.tearDown();
    }

    public abstract String getProviderHint();

    public void testSimplePutGet()
    {
        Integer fooInt = new Integer( 42 );
        cache.put( "foo", fooInt );

        Integer val = (Integer) cache.get( "foo" );
        assertEquals( 42, val.intValue() );

        assertNull( cache.get( "bar" ) );
    }

    public void testLargePutGet()
    {
        EnglishNumberFormat fmt = new EnglishNumberFormat();

        for ( int i = 4500; i <= 5000; i++ )
        {
            String key = fmt.toText( i );
            cache.put( key, new Integer( i ) );
        }

        // Put some holes into the list.
        List removedKeys = new ArrayList();
        removedKeys.add( fmt.toText( 4600 ) );
        removedKeys.add( fmt.toText( 4700 ) );
        removedKeys.add( fmt.toText( 4800 ) );

        Iterator it = removedKeys.iterator();
        while ( it.hasNext() )
        {
            cache.remove( it.next() );
        }

        // Some direct gets
        assertEquals( new Integer( 4590 ), cache.get( "four thousand five hundred ninety" ) );
        assertEquals( new Integer( 4912 ), cache.get( "four thousand nine hundred twelve" ) );
        int DIRECT = 2;

        // Fetch the list repeatedly
        int ITERS = 100;
        int LOW = 4590;
        int HIGH = 4810;
        for ( int iter = 0; iter < ITERS; iter++ )
        {
            for ( int num = LOW; num < HIGH; num++ )
            {
                String key = fmt.toText( num );
                Integer expected = new Integer( num );
                Integer val = (Integer) cache.get( key );

                // Intentionally removed entries?
                if ( removedKeys.contains( key ) )
                {
                    assertNull( "Removed key [" + key + "] should have no value.", val );
                }
                else
                {
                    assertEquals( expected, val );
                }
            }
        }

        // Test the statistics.
        CacheStatistics stats = cache.getStatistics();

        int expectedHits = ( ( ( HIGH - LOW - removedKeys.size() ) * ITERS ) + DIRECT );
        int expectedMiss = ( ITERS * removedKeys.size() );

        /* Due to the nature of how the various providers do their work, the expected values
         * should be viewed as minimum values, not exact values.
         */
        assertTrue( "Cache hit count should exceed [" + expectedHits + "], but was actually [" + stats.getCacheHits()
            + "]", expectedHits <= stats.getCacheHits() );

        assertTrue( "Cache miss count should exceed [" + expectedMiss + "], but was actually [" + stats.getCacheMiss()
            + "]", expectedMiss <= stats.getCacheMiss() );

        /* For the same reason as above, the hit rate is completely un-testable.
         * Leaving this commented so that future developers understand the reason we are not
         * testing this value.
         
        double expectedHitRate = (double) expectedHits / (double) ( expectedHits + expectedMiss );
        assertTrue( "Cache hit rate should exceed [" + expectedHitRate + "], but was actually ["
            + stats.getCacheHitRate() + "]", expectedHitRate <= stats.getCacheHitRate() );
            
         */
    }
}
