package org.codehaus.plexus.registry;

/*
 * Copyright 2007, Brett Porter
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

import java.io.File;
import java.util.List;

/**
 * The Plexus registry is a single source of external configuration for Plexus components and applications.
 * It can be used by components to source configuration, knowing that it can be used from within applications
 * without the information being hard coded into the component.
 */
public interface Registry
{
    /**
     * Dump the entire registry to a string, for debugging purposes.
     *
     * @return the registry contents
     */
    String dump();

    /**
     * Get a string value from the registry. If not found, <code>null</code> is returned.
     *
     * @param key the key in the registry
     * @return the value
     */
    String getString( String key );

    /**
     * Get a string value from the registry. If not found, the default value is used.
     *
     * @param key          the key in the registry
     * @param defaultValue the default value
     * @return the value
     */
    String getString( String key, String defaultValue );

    /**
     * Get an integer value from the registry. If not found, an exception is thrown.
     *
     * @param key the key in the registry
     * @return the value
     * @throws java.util.NoSuchElementException
     *          if the key is not found
     */
    int getInt( String key );

    /**
     * Get an integer value from the registry. If not found, the default value is used.
     *
     * @param key          the key in the registry
     * @param defaultValue the default value
     * @return the value
     */
    int getInt( String key, int defaultValue );

    /**
     * Get a boolean value from the registry. If not found, an exception is thrown.
     *
     * @param key the key in the registry
     * @return the value
     * @throws java.util.NoSuchElementException
     *          if the key is not found
     */
    boolean getBoolean( String key );

    /**
     * Load configuration from the given classloader resource.
     *
     * @param resource the location to load the configuration from
     * @throws RegistryException if a problem occurred reading the resource to add to the registry
     */
    void addConfigurationFromResource( String resource )
        throws RegistryException;

    /**
     * Load configuration from the given file.
     *
     * @param file the location to load the configuration from
     * @throws RegistryException if a problem occurred reading the resource to add to the registry
     */
    void addConfigurationFromFile( File file )
        throws RegistryException;

    // TODO!
    Registry getSubRegistry( String key );

    /**
     * Determine if the registry contains any elements.
     *
     * @return whether the registry contains any elements
     */
    boolean isEmpty();

    // TODO!
    List getList( String key );
}
