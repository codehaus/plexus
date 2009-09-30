package org.codehaus.plexus.interpolation.multi;

/*
 * Copyright 2001-2009 Codehaus Foundation.
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

import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.interpolation.multi.MultiDelimiterStringSearchInterpolator;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class MultiDelimiterStringSearchInterpolatorTest
    extends TestCase
{

    public void testInterpolationWithDifferentDelimiters()
        throws InterpolationException
    {
        Map ctx = new HashMap();
        ctx.put( "name", "User" );
        ctx.put( "otherName", "@name@" );

        String input = "${otherName}";

        ValueSource vs = new MapBasedValueSource( ctx );
        MultiDelimiterStringSearchInterpolator interpolator = new MultiDelimiterStringSearchInterpolator().addDelimiterSpec( "@" )
                                                                      .withValueSource( vs );

        String result = interpolator.interpolate( input );

        assertEquals( ctx.get( "name" ), result );
    }

    public void testSuccessiveInterpolationWithDifferentDelimiters_ReversedDelimiterSequence()
        throws InterpolationException
    {
        Map ctx = new HashMap();
        ctx.put( "name", "User" );
        ctx.put( "otherName", "${name}" );

        String input = "@otherName@";

        ValueSource vs = new MapBasedValueSource( ctx );
        MultiDelimiterStringSearchInterpolator interpolator = new MultiDelimiterStringSearchInterpolator().addDelimiterSpec( "@" )
                                                                      .withValueSource( vs );

        String result = interpolator.interpolate( input );

        assertEquals( ctx.get( "name" ), result );
    }

}
