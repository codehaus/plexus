package org.codehaus.plexus.registry;

/*
 * Copyright 2007 The Codehaus Foundation.
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
 * Receives notifications of configuration changes in thre registry.
 */
public interface RegistryListener
{
    /**
     * Notify the object that there is about to be a configuration change.
     *
     * @param registry      the registry that was changed
     * @param propertyName  the property being changed
     * @param propertyValue the value the property is about to be changed to
     */
    void beforeConfigurationChange( Registry registry, String propertyName, Object propertyValue );

    /**
     * Notify the object that there has been a configuration change.
     *
     * @param registry      the registry that was changed
     * @param propertyName  the property what was changed
     * @param propertyValue the value the property was changed to
     */
    void afterConfigurationChange( Registry registry, String propertyName, Object propertyValue );
}
