package org.codehaus.plexus.summit.parameters;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Pete Kazmier
 */
public class BaseParameterConverterTest extends TestCase
{
    public BaseParameterConverterTest( String name )
    {
        super( name );
    }

    public static Test suite()
    {
        return new TestSuite( BaseParameterConverterTest.class );
    }

    public void testBooleanConverter()
    {
        Map map = new HashMap();
        map.put( "param1", new String[]{"true"} );
        map.put( "param2", new String[]{"false"} );
        map.put( "param3", new String[]{"on"} );
        map.put( "param4", new String[]{"off"} );
        map.put( "param5", new String[]{"1"} );
        map.put( "param6", new String[]{"0"} );
        map.put( "param7", new String[]{"not a boolean"} );

        BaseParameterConverter converter = new BaseParameterConverter( map );

        assertTrue( converter.getBoolean( "param1" ) );
        assertTrue( !converter.getBoolean( "param2" ) );

        assertTrue( converter.getBoolean( "param3" ) );
        assertTrue( !converter.getBoolean( "param4" ) );

        assertTrue( converter.getBoolean( "param5" ) );
        assertTrue( !converter.getBoolean( "param6" ) );

        assertTrue( !converter.getBoolean( "param7" ) );
        assertTrue( converter.getBoolean( "param7", true ) );
        assertTrue( !converter.getBoolean( "param7", false ) );

        // No such parameter
        assertTrue( !converter.getBoolean( "param8" ) );
        assertTrue( converter.getBoolean( "param8", true ) );
        assertTrue( !converter.getBoolean( "param8", false ) );

        // Also verify the Boolean wrapper works as expected
        assertTrue( converter.getBool( "param1" ).booleanValue() );
        assertTrue( !converter.getBool( "param2" ).booleanValue() );
    }

    public void testDoubleConverter()
    {
        Map map = new HashMap();
        map.put( "param1", new String[]{"2.5"} );
        map.put( "param2", new String[]{"1"} );
        map.put( "param3", new String[]{"not a double"} );

        BaseParameterConverter converter = new BaseParameterConverter( map );

        assertTrue( 2.5 == converter.getDouble( "param1" ) );
        assertTrue( 1.0 == converter.getDouble( "param2" ) );
        assertTrue( 0.0 == converter.getDouble( "param3" ) );
        assertTrue( 0.0 == converter.getDouble( "param4" ) );
        assertTrue( 0.5 == converter.getDouble( "param4", 0.5 ) );
    }

    public void testFloatConverter()
    {
        Map map = new HashMap();
        map.put( "param1", new String[]{"2.5"} );
        map.put( "param2", new String[]{"1"} );
        map.put( "param3", new String[]{"not a double"} );

        BaseParameterConverter converter = new BaseParameterConverter( map );

        assertTrue( 2.5f == converter.getDouble( "param1" ) );
        assertTrue( 1.0f == converter.getDouble( "param2" ) );
        assertTrue( 0.0f == converter.getDouble( "param3" ) );
        assertTrue( 0.0f == converter.getDouble( "param4" ) );
        assertTrue( 0.5f == converter.getDouble( "param4", 0.5f ) );
    }

    public void testBigDecimalConverter()
    {
        Map map = new HashMap();
        map.put( "param1", new String[]{"2.5"} );
        map.put( "param2", new String[]{"1"} );
        map.put( "param3", new String[]{"not a double"} );
        map.put( "param4", new String[]{"1.5", "2.5"} );

        BaseParameterConverter converter = new BaseParameterConverter( map );

        assertEquals( new BigDecimal( 2.5 ), converter.getBigDecimal( "param1" ) );
        assertEquals( new BigDecimal( 1.0 ), converter.getBigDecimal( "param2" ) );
        assertEquals( new BigDecimal( 0.0 ), converter.getBigDecimal( "param3" ) );
        assertTrue( Arrays.equals( new BigDecimal[]{new BigDecimal( 1.5 ), new BigDecimal( 2.5 )},
                                   converter.getBigDecimals( "param4" ) ) );

    }

    public void testIntegerConverter()
    {
        Map map = new HashMap();
        map.put( "param1", new String[]{"2"} );
        map.put( "param2", new String[]{"1"} );
        map.put( "param3", new String[]{"not a integer"} );
        map.put( "param4", new String[]{"1", "2"} );

        BaseParameterConverter converter = new BaseParameterConverter( map );

        assertEquals( new Integer( 2 ), converter.getInteger( "param1" ) );
        assertEquals( 1, converter.getInt( "param2" ) );
        assertEquals( 0, converter.getInt( "param3" ) );
        assertTrue( Arrays.equals( new int[]{1, 2}, converter.getInts( "param4" ) ) );

    }

    public void testLongConverter()
    {
        Map map = new HashMap();
        map.put( "param1", new String[]{"2"} );
        map.put( "param2", new String[]{"1"} );
        map.put( "param3", new String[]{"not a long"} );
        map.put( "param4", new String[]{"1", "2"} );

        BaseParameterConverter converter = new BaseParameterConverter( map );

        assertEquals( (long) 2, converter.getLong( "param1" ) );
        assertEquals( (long) 1, converter.getLong( "param2" ) );
        assertEquals( (long) 0, converter.getLong( "param3" ) );
        assertTrue( Arrays.equals( new long[]{1, 2}, converter.getLongs( "param4" ) ) );
    }

    public void testByteConverter() throws Exception
    {
        Map map = new HashMap();
        map.put( "param1", new String[]{"5"} );
        map.put( "param2", new String[]{"test"} );

        BaseParameterConverter converter = new BaseParameterConverter( map );

        assertEquals( (byte) 5, converter.getByte( "param1" ) );
        assertTrue( Arrays.equals( "test".getBytes( "US-ASCII" ),
                                   converter.getBytes( "param2" ) ) );
    }

    public void testStringConverter()
    {
        Map map = new HashMap();
        map.put( "param1", new String[]{"one"} );
        map.put( "param2", new String[]{"two"} );
        map.put( "param3", new String[]{"one", "two", "three"} );

        BaseParameterConverter converter = new BaseParameterConverter( map );

        assertEquals( "one", converter.getString( "param1" ) );
        assertEquals( "two", converter.getString( "param2" ) );
        assertTrue( Arrays.equals( new String[]{"one", "two", "three"},
                                   converter.getStrings( "param3" ) ) );

    }

    public void testDateConverter() throws ParseException
    {
        Map map = new HashMap();
        map.put( "param1", new String[]{"01/01/2002"} );
        map.put( "param2", new String[]{"not a date"} );

        BaseParameterConverter converter = new BaseParameterConverter( map );

        DateFormat df = DateFormat.getDateInstance( DateFormat.SHORT, Locale.US );
        assertEquals( df.parse( "01/01/2002" ), converter.getDate( "param1", df ) );
        assertEquals( null, converter.getDate( "param2", df ) );
    }
}
