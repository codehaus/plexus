package org.codehaus.plexus.interpolation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

public class StringSearchInterpolatorTest
    extends TestCase
{

    public void testSimpleSubstitution()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator();
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        assertEquals( "This is a test value.", interpolator.interpolate( "This is a test ${key}." ) );
    }

    public void testSimpleSubstitution_TwoExpressions()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );
        p.setProperty( "key2", "value2" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator();
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        assertEquals( "value-value2", interpolator.interpolate( "${key}-${key2}" ) );
    }

    public void testBrokenExpression_LeaveItAlone()
        throws InterpolationException
    {
        Properties p = new Properties();
        p.setProperty( "key", "value" );

        StringSearchInterpolator interpolator = new StringSearchInterpolator();
        interpolator.addValueSource( new PropertiesBasedValueSource( p ) );

        assertEquals( "This is a test ${key.", interpolator.interpolate( "This is a test ${key." ) );
    }

    public void testShouldFailOnExpressionCycle()
    {
        Properties props = new Properties();
        props.setProperty( "key1", "${key2}" );
        props.setProperty( "key2", "${key1}" );

        StringSearchInterpolator rbi = new StringSearchInterpolator();
        rbi.addValueSource( new PropertiesBasedValueSource( props ) );

        try
        {
            rbi.interpolate( "${key1}", new SimpleRecursionInterceptor() );

            fail( "Should detect expression cycle and fail." );
        }
        catch ( InterpolationException e )
        {
            e.printStackTrace( System.out );
        }
    }

    public void testShouldResolveByMy_getVar_Method()
        throws InterpolationException
    {
        StringSearchInterpolator rbi = new StringSearchInterpolator();
        rbi.addValueSource( new ObjectBasedValueSource( this ) );
        String result = rbi.interpolate( "this is a ${var}" );

        assertEquals( "this is a testVar", result );
    }

    public void testShouldResolveByContextValue()
        throws InterpolationException
    {
        StringSearchInterpolator rbi = new StringSearchInterpolator();

        Map context = new HashMap();
        context.put( "var", "testVar" );

        rbi.addValueSource( new MapBasedValueSource( context ) );

        String result = rbi.interpolate( "this is a ${var}" );

        assertEquals( "this is a testVar", result );
    }

    public void testShouldResolveByEnvar()
        throws IOException, InterpolationException
    {
        StringSearchInterpolator rbi = new StringSearchInterpolator();

        rbi.addValueSource( new EnvarBasedValueSource() );

        String result = rbi.interpolate( "this is a ${env.HOME}" );

        assertFalse( "this is a ${HOME}".equals( result ) );
        assertFalse( "this is a ${env.HOME}".equals( result ) );
    }

    public void testUsePostProcessor_DoesNotChangeValue()
        throws InterpolationException
    {
        StringSearchInterpolator rbi = new StringSearchInterpolator();

        Map context = new HashMap();
        context.put( "test.var", "testVar" );

        rbi.addValueSource( new MapBasedValueSource( context ) );

        rbi.addPostProcessor( new InterpolationPostProcessor()
        {
            public Object execute( String expression, Object value )
            {
                return null;
            }
        } );

        String result = rbi.interpolate( "this is a ${test.var}" );

        assertEquals( "this is a testVar", result );
    }

    public void testUsePostProcessor_ChangesValue()
        throws InterpolationException
    {

        StringSearchInterpolator rbi = new StringSearchInterpolator();

        Map context = new HashMap();
        context.put( "test.var", "testVar" );

        rbi.addValueSource( new MapBasedValueSource( context ) );

        rbi.addPostProcessor( new InterpolationPostProcessor()
        {
            public Object execute( String expression, Object value )
            {
                return value + "2";
            }
        } );

        String result = rbi.interpolate( "this is a ${test.var}" );

        assertEquals( "this is a testVar2", result );
    }

    public String getVar()
    {
        return "testVar";
    }

}
