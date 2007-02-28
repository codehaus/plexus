/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.codehaus.plexus.component.factory.groovy;

import java.net.URL;
import java.net.MalformedURLException;

import groovy.lang.GroovyResourceLoader;

/**
 * Helps to load Groovy script resources from a class loader.
 *
 * @version $Rev$ $Date$
 */
public class GroovyResourceLoaderImpl
    implements GroovyResourceLoader
{
    private ClassLoader classLoader;

    public GroovyResourceLoaderImpl(final ClassLoader classLoader) {
        assert classLoader != null;
        
        this.classLoader = classLoader;
    }

    public URL loadGroovySource(final String className) throws MalformedURLException {
        return resolveGroovySource(className, classLoader);
    }

    protected URL resolveGroovySource(final String className, final ClassLoader classLoader) {
        assert className != null;
        assert classLoader != null;

        // Figure out what resource to load
        String resource = className;
        if (!resource.startsWith("/")) {
            resource = "/" + resource;
        }
        if (!resource.endsWith(".groovy")) {
            resource = resource.replace('.', '/');
            resource += ".groovy";
        }

        URL source = classLoader.getResource(resource);
        if (source == null) {
            //
            // NOTE: Not sure if this is nessicary or not...
            //
            source = Thread.currentThread().getContextClassLoader().getResource(resource);
        }

        return source;
    }
}
