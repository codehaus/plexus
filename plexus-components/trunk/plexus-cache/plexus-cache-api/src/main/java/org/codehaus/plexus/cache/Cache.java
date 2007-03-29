package org.codehaus.plexus.cache;

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

/**
 * Cache interface. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface Cache
{
    public static final String ROLE = Cache.class.getName();

    /**
     * Tests to see if the provided key exists within the cache.
     * 
     * NOTE: Due to synchronization issues, if this method returns true, a subsequent request
     * to the {@link #get(Object)} method on the same key might return null as the period of time
     * between the 2 request might have been long enough for the underlying Cache implementation
     * to remove the key. 
     * 
     * @param key the key to test.
     * @return true if the key exists.
     */
    public boolean hasKey( Object key );

    /**
     * Get the value of the specified key, if it exists in the cache.
     * 
     * @param key the key to fetch the contents of.
     * @return the value of the key, or null if not found.
     */
    public Object get( Object key );

    /**
     * Put the specified value into the cache under the provided key.
     * 
     * @param key the key to put the value into
     * @param value the object to place into the cache.
     * @return the previous value for the key, or null if the key contained no value.
     */
    public Object put( Object key, Object value );

    /**
     * Register the specified value into the cache under the provided key.
     * 
     * This {@link #register(Object, Object)} method is just an optimized version of the {@link #put(Object, Object)} 
     * method, but does not return the previous value contained with the specified key.  
     * 
     * @param key the key to put the value into
     * @param value the object to place into the cache.
     */
    public void register( Object key, Object value );

    /**
     * Remove the specified key and value from the cache.
     * 
     * @param key the key to the value to remove. 
     * @return the value of the key that was removed.
     */
    public Object remove( Object key );

    /**
     * Clear the cache of all entries.
     */
    public void clear();

    /**
     * Obtain a set of Statistics about the performance of the cache.
     * 
     * @return the cache statistics.
     */
    public CacheStatistics getStatistics();

}
