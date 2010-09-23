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

import junit.framework.TestCase;

public class PrefixedValueSourceWrapperTest
    extends TestCase
{

    public void testShouldReturnValueForPropertyVSWRappedWithSinglePrefix()
    {
        String prefix = "prefix.";
        String key = "key";
        String value = "value";

        Properties props = new Properties();
        props.setProperty( key, value );

        PrefixedValueSourceWrapper wrapper = new PrefixedValueSourceWrapper( new PropertiesBasedValueSource( props ), prefix );

        assertEquals( value, wrapper.getValue( prefix + key ) );
    }

    public void testShouldReturnNullForIncorrectPrefixUsingPropertyVSWRappedWithSinglePrefix()
    {
        String prefix = "prefix.";
        String otherPrefix = "other.";
        String key = "key";
        String value = "value";

        Properties props = new Properties();
        props.setProperty( key, value );

        PrefixedValueSourceWrapper wrapper = new PrefixedValueSourceWrapper( new PropertiesBasedValueSource( props ), prefix );

        assertNull( wrapper.getValue( otherPrefix + key ) );
    }

    public void testShouldNullForMissingValueInPropertyVSWRappedWithSinglePrefix()
    {
        String prefix = "prefix.";
        String key = "key";

        Properties props = new Properties();

        PrefixedValueSourceWrapper wrapper = new PrefixedValueSourceWrapper( new PropertiesBasedValueSource( props ), prefix );

        assertNull( wrapper.getValue( prefix + key ) );
    }

}
