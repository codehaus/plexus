package org.codehaus.plexus.interpolation.multi;

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
