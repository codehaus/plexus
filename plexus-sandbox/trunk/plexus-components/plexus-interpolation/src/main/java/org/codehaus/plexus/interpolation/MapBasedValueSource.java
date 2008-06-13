package org.codehaus.plexus.interpolation;

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
import java.util.Map;

/**
 * Wraps a Map, and looks up the whole expression as a single key, returning the
 * value mapped to it.
 */
public class MapBasedValueSource
    implements ValueSource
{

    private final Map values;

    /**
     * Construct a new value source to wrap the supplied map.
     */
    public MapBasedValueSource( Map values )
    {
        this.values = values;
    }

    /**
     * Lookup the supplied expression as a key in the wrapped Map, and return
     * its value.
     */
    public Object getValue( String expression )
    {
        return values.get( expression );
    }

}
