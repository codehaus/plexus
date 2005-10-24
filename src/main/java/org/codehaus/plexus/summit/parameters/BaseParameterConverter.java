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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Base implementation of a {@link ParameterConverter} which relies
 * on an underlying Map of name/value pairs where each value is an
 * array of strings (even if it contains a single value). For example,
 * the Map returned by {@link HttpRequest#getParameterMap()}.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Revision$
 */
public class BaseParameterConverter
    implements ParameterConverter
{
    /**
     * Map used to store the parameters that were parsed.
     */
    protected Map parameters;

    /**
     * The character encoding to use when converting to byte arrays.
     */
    protected String characterEncoding = "US-ASCII";

    /**
     * Constructor that takes a map of parameters and uses the default
     * character encoding, "US-ASCII", when converting to byte arrays.
     * The map of parameters must contain name/value pairs as described
     * in the class documentation.
     *
     * @param parameters A map of name/value pairs representing parameters.
     * @throws NullPointerException If the parameters map is null.
     */
    public BaseParameterConverter( Map parameters )
    {
        this( parameters, null );
    }

    /**
     * Constructor that takes a map of parameters and a character encoding
     * that is used when converting to byte arrays.  The map of parameters
     * must contain name/value pairs as described in the class documentation.
     *
     * @param parameters        A map of name/value pairs representing parameters.
     * @param characterEncoding The character encoding used to convert byte arrays.
     *                          If the specified value is <tt>null</tt>, the default encoding of
     *                          "US-ASCII" is used.
     * @throws NullPointerException If the parameters map is null.
     */
    public BaseParameterConverter( Map parameters, String characterEncoding )
    {
        if ( parameters == null )
        {
            throw new NullPointerException( "parameter map is null" );
        }

        if ( characterEncoding != null )
        {
            this.characterEncoding = characterEncoding;
        }

        this.parameters = parameters;
    }

    public Map getParametersMap()
    {
        return parameters;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#containsKey(java.lang.Object)
     */
    public boolean containsKey( Object key )
    {
        return parameters.containsKey( key );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#keys()
     */
    public Iterator keys()
    {
        return parameters.keySet().iterator();
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getKeys()
     */
    public Object[] getKeys()
    {
        return parameters.keySet().toArray();
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getBoolean(java.lang.String, boolean)
     */
    public boolean getBoolean( String name, boolean defaultValue )
    {
        boolean value = defaultValue;
        Object object = parameters.get( name );
        if ( object != null )
        {
            String tmp = getString( name );
            if ( tmp.equalsIgnoreCase( "1" )
                || tmp.equalsIgnoreCase( "true" )
                || tmp.equalsIgnoreCase( "on" ) )
            {
                value = true;
            }
            if ( tmp.equalsIgnoreCase( "0" )
                || tmp.equalsIgnoreCase( "false" )
                || tmp.equalsIgnoreCase( "off" ) )
            {
                value = false;
            }
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getBoolean(java.lang.String)
     */
    public boolean getBoolean( String name )
    {
        return getBoolean( name, false );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getBool(java.lang.String, boolean)
     */
    public Boolean getBool( String name, boolean defaultValue )
    {
        return new Boolean( getBoolean( name, defaultValue ) );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getBool(java.lang.String)
     */
    public Boolean getBool( String name )
    {
        return new Boolean( getBoolean( name, false ) );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getDouble(java.lang.String, double)
     */
    public double getDouble( String name, double defaultValue )
    {
        double value = defaultValue;
        try
        {
            Object object = parameters.get( name );
            if ( object != null )
            {
                value = Double.valueOf( ( (String[]) object )[0] ).doubleValue();
            }
        }
        catch ( NumberFormatException exception )
        {
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getDouble(java.lang.String)
     */
    public double getDouble( String name )
    {
        return getDouble( name, 0.0 );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getFloat(java.lang.String, float)
     */
    public float getFloat( String name, float defaultValue )
    {
        float value = defaultValue;
        try
        {
            Object object = parameters.get( name );
            if ( object != null )
            {
                value = Float.valueOf( ( (String[]) object )[0] ).floatValue();
            }
        }
        catch ( NumberFormatException exception )
        {
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getFloat(java.lang.String)
     */
    public float getFloat( String name )
    {
        return getFloat( name, 0.0f );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getBigDecimal(java.lang.String, java.math.BigDecimal)
     */
    public BigDecimal getBigDecimal( String name, BigDecimal defaultValue )
    {
        BigDecimal value = defaultValue;
        try
        {
            Object object = parameters.get( name );
            if ( object != null )
            {
                String temp = ( (String[]) object )[0];
                if ( temp.length() > 0 )
                {
                    value = new BigDecimal( ( (String[]) object )[0] );
                }
            }
        }
        catch ( NumberFormatException exception )
        {
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getBigDecimal(java.lang.String)
     */
    public BigDecimal getBigDecimal( String name )
    {
        return getBigDecimal( name, new BigDecimal( 0.0 ) );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getBigDecimals(java.lang.String)
     */
    public BigDecimal[] getBigDecimals( String name )
    {
        BigDecimal[] value = null;
        Object object = getStrings( name );
        if ( object != null )
        {
            String[] temp = (String[]) object;
            value = new BigDecimal[temp.length];
            for ( int i = 0; i < temp.length; i++ )
            {
                value[i] = new BigDecimal( temp[i] );
            }
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getInt(java.lang.String, int)
     */
    public int getInt( String name, int defaultValue )
    {
        int value = defaultValue;
        try
        {
            Object object = parameters.get( name );
            if ( object != null )
            {
                value = Integer.valueOf( ( (String[]) object )[0] ).intValue();
            }
        }
        catch ( NumberFormatException exception )
        {
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getInt(java.lang.String)
     */
    public int getInt( String name )
    {
        return getInt( name, 0 );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getInteger(java.lang.String, int)
     */
    public Integer getInteger( String name, int defaultValue )
    {
        return new Integer( getInt( name, defaultValue ) );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getInteger(java.lang.String, java.lang.Integer)
     */
    public Integer getInteger( String name, Integer def )
    {
        return new Integer( getInt( name, def.intValue() ) );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getInteger(java.lang.String)
     */
    public Integer getInteger( String name )
    {
        return new Integer( getInt( name, 0 ) );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getInts(java.lang.String)
     */
    public int[] getInts( String name )
    {
        int[] value = null;
        Object object = getStrings( name );
        if ( object != null )
        {
            String[] temp = (String[]) object;
            value = new int[temp.length];
            for ( int i = 0; i < temp.length; i++ )
            {
                value[i] = Integer.parseInt( temp[i] );
            }
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getIntegers(java.lang.String)
     */
    public Integer[] getIntegers( String name )
    {
        Integer[] value = null;
        Object object = getStrings( name );
        if ( object != null )
        {
            String[] temp = (String[]) object;
            value = new Integer[temp.length];
            for ( int i = 0; i < temp.length; i++ )
            {
                value[i] = Integer.valueOf( temp[i] );
            }
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getLong(java.lang.String, long)
     */
    public long getLong( String name, long defaultValue )
    {
        long value = defaultValue;
        try
        {
            Object object = parameters.get( name );
            if ( object != null )
            {
                value = Long.valueOf( ( (String[]) object )[0] ).longValue();
            }
        }
        catch ( NumberFormatException exception )
        {
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getLong(java.lang.String)
     */
    public long getLong( String name )
    {
        return getLong( name, 0 );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getLongs(java.lang.String)
     */
    public long[] getLongs( String name )
    {
        long[] value = null;
        Object object = getStrings( name );
        if ( object != null )
        {
            String[] temp = (String[]) object;
            value = new long[temp.length];
            for ( int i = 0; i < temp.length; i++ )
            {
                value[i] = Long.parseLong( temp[i] );
            }
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getLongObjects(java.lang.String)
     */
    public Long[] getLongObjects( String name )
    {
        Long[] value = null;
        Object object = getStrings( name );
        if ( object != null )
        {
            String[] temp = (String[]) object;
            value = new Long[temp.length];
            for ( int i = 0; i < temp.length; i++ )
            {
                value[i] = Long.valueOf( temp[i] );
            }
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getByte(java.lang.String, byte)
     */
    public byte getByte( String name, byte defaultValue )
    {
        byte value = defaultValue;
        try
        {
            Object object = parameters.get( name );
            if ( object != null )
            {
                value = Byte.valueOf( ( (String[]) object )[0] ).byteValue();
            }
        }
        catch ( NumberFormatException exception )
        {
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getByte(java.lang.String)
     */
    public byte getByte( String name )
    {
        return getByte( name, (byte) 0 );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getBytes(java.lang.String)
     */
    public byte[] getBytes( String name ) throws UnsupportedEncodingException
    {
        String tempStr = getString( name );
        if ( tempStr != null )
        {
            return tempStr.getBytes( characterEncoding );
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getString(java.lang.String)
     */
    public String getString( String name )
    {
        try
        {
            String value = null;
            Object object = parameters.get( name );
            if ( object != null )
            {
                value = ( (String[]) object )[0];
            }
            if ( value == null || value.equals( "null" ) )
            {
                return null;
            }
            return value;
        }
        catch ( ClassCastException e )
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#get(java.lang.String)
     */
    public String get( String name )
    {
        return getString( name );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getString(java.lang.String, java.lang.String)
     */
    public String getString( String name, String defaultValue )
    {
        String value = getString( name );
        if ( value == null || value.length() == 0 || value.equals( "null" ) )
            return defaultValue;
        else
            return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getStrings(java.lang.String)
     */
    public String[] getStrings( String name )
    {
        String[] value = null;
        Object object = parameters.get( name );
        if ( object != null )
        {
            value = ( (String[]) object );
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getStrings(java.lang.String, java.lang.String[])
     */
    public String[] getStrings( String name, String[] defaultValue )
    {
        String[] value = getStrings( name );
        if ( value == null || value.length == 0 )
            return defaultValue;
        else
            return value;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getObject(java.lang.String)
     */
    public Object getObject( String name )
    {
        try
        {
            Object value = null;
            Object object = parameters.get( name );
            if ( object != null )
            {
                value = ( (Object[]) object )[0];
            }
            return value;
        }
        catch ( ClassCastException e )
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getObjects(java.lang.String)
     */
    public Object[] getObjects( String name )
    {
        try
        {
            return (Object[]) parameters.get( name );
        }
        catch ( ClassCastException e )
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getDate(java.lang.String, java.text.DateFormat, java.util.Date)
     */
    public Date getDate( String name, DateFormat df, Date defaultValue )
    {
        Date date = null;

        if ( containsKey( name ) )
        {
            try
            {
                // Reject invalid dates.
                df.setLenient( false );
                date = df.parse( getString( name ) );
            }
            catch ( ParseException e )
            {
                // Thrown if couldn't parse date.
                date = defaultValue;
            }
        }
        else
        {
            date = defaultValue;
        }

        return date;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getDate(java.lang.String, java.text.DateFormat)
     */
    public Date getDate( String name, DateFormat df )
    {
        return getDate( name, df, null );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.parameters.ParameterConverter#getDate(java.lang.String)
     */
    public Date getDate( String name )
    {
        DateFormat df = DateFormat.getDateInstance();
        return getDate( name, df, null );
    }

    /**
     * Provides a textual representation of the parameters represented
     * by this object.  Each parameter name and its corresponding value
     * is generated.
     *
     * @return A textual representation of the name/value pairs.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        for ( Iterator i = parameters.keySet().iterator(); i.hasNext(); )
        {
            String name = (String) i.next();

            sb.append( '{' );
            sb.append( name );
            sb.append( '=' );

            String[] values = (String[]) parameters.get( name );

            if ( values == null )
            {
                sb.append( "null" );
            }
            else if ( values.length == 1 )
            {
                sb.append( values[0] );
            }
            else
            {
                for ( int j = 0; j < values.length; j++ )
                {
                    if ( j != 0 )
                    {
                        sb.append( ", " );
                    }
                    sb.append( '[' );
                    sb.append( values[j] );
                    sb.append( ']' );
                }
            }
            sb.append( "}\n" );
        }

        return sb.toString();
    }
}
