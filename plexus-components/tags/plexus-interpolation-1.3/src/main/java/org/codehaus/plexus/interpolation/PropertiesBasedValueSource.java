package org.codehaus.plexus.interpolation;

/*
 * Copyright 2001-2008 Codehaus Foundation.
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

import java.util.Properties;

/**
 * {@link ValueSource} implementation that wraps a {@link Properties} instance,
 * and does a simple lookup of the entire expression string as the parameter for
 * {@link Properties#getProperty(String)}, returning the result as the resolved
 * value.
 *
 * @author jdcasey
 * @version $Id$
 */
public class PropertiesBasedValueSource
    implements ValueSource
{

    private final Properties properties;

    /**
     * Wrap the specified {@link Properties} object for use as a value source.
     * Nulls are allowed.
     *
     * @param properties The properties instance to wrap.
     */
    public PropertiesBasedValueSource( Properties properties )
    {
        this.properties = properties;
    }

    /**
     * @return the result of {@link Properties#getProperty(String)}, using the
     * entire expression as the key to lookup. If the wrapped properties instance
     * is null, simply return null.
     */
    public Object getValue( String expression )
    {
        return properties == null ? null : properties.getProperty( expression );
    }

}
