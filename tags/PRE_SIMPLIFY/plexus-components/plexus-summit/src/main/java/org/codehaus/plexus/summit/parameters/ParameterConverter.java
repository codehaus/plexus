package org.codehaus.plexus.summit.parameters;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Interface that provides type-conversion methods for the values of a
 * parameter map.  The parameter map consists of name/value pairs.  For
 * example, HTTP request parameters obtained via GET/POST data, or
 * Cookies.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Revision$
 */
public interface ParameterConverter
{
    /**
     * Determine whether a given key has been inserted
     *
     * @param key An Object with the key to search for.
     * @return True if the object is found.
     */
    public boolean containsKey( Object key );

    /**
     * Get an Iterator for the parameter names.
     *
     * @return An Iterator of the keys.
     */
    public Iterator keys();

    /**
     * Get an array of all the parameter names.
     *
     * @return A object array with the keys.
     */
    public Object[] getKeys();

    /**
     * Return a boolean for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A boolean.
     */
    public boolean getBoolean( String name, boolean defaultValue );

    /**
     * Return a boolean for the given name.  If the name does not
     * exist, return false.
     *
     * @param name A String with the name.
     * @return A boolean.
     */
    public boolean getBoolean( String name );

    /**
     * Return a Boolean for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A Boolean.
     */
    public Boolean getBool( String name, boolean defaultValue );

    /**
     * Return a Boolean for the given name.  If the name does not
     * exist, return false.
     *
     * @param name A String with the name.
     * @return A Boolean.
     */
    public Boolean getBool( String name );

    /**
     * Return a double for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A double.
     */
    public double getDouble( String name, double defaultValue );

    /**
     * Return a double for the given name.  If the name does not
     * exist, return 0.0.
     *
     * @param name A String with the name.
     * @return A double.
     */
    public double getDouble( String name );

    /**
     * Return a float for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A float.
     */
    public float getFloat( String name, float defaultValue );

    /**
     * Return a float for the given name.  If the name does not
     * exist, return 0.0.
     *
     * @param name A String with the name.
     * @return A float.
     */
    public float getFloat( String name );

    /**
     * Return a BigDecimal for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A BigDecimal.
     */
    public BigDecimal getBigDecimal( String name, BigDecimal defaultValue );

    /**
     * Return a BigDecimal for the given name.  If the name does not
     * exist, return 0.0.
     *
     * @param name A String with the name.
     * @return A BigDecimal.
     */
    public BigDecimal getBigDecimal( String name );

    /**
     * Return an array of BigDecimals for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return A BigDecimal[].
     */
    public BigDecimal[] getBigDecimals( String name );

    /**
     * Return an int for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return An int.
     */
    public int getInt( String name, int defaultValue );

    /**
     * Return an int for the given name.  If the name does not exist,
     * return 0.
     *
     * @param name A String with the name.
     * @return An int.
     */
    public int getInt( String name );

    /**
     * Return an Integer for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return An Integer.
     */
    public Integer getInteger( String name, int defaultValue );

    /**
     * Return an Integer for the given name.  If the name does not
     * exist, return defaultValue.  You cannot pass in a null here for
     * the default value.
     *
     * @param name A String with the name.
     * @param def The default value.
     * @return An Integer.
     */
    public Integer getInteger( String name, Integer def );

    /**
     * Return an Integer for the given name.  If the name does not
     * exist, return 0.
     *
     * @param name A String with the name.
     * @return An Integer.
     */
    public Integer getInteger( String name );

    /**
     * Return an array of ints for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return An int[].
     */
    public int[] getInts( String name );

    /**
     * Return an array of Integers for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return An Integer[].
     */
    public Integer[] getIntegers( String name );

    /**
     * Return a long for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A long.
     */
    public long getLong( String name, long defaultValue );

    /**
     * Return a long for the given name.  If the name does not exist,
     * return 0.
     *
     * @param name A String with the name.
     * @return A long.
     */
    public long getLong( String name );

    /**
     * Return an array of longs for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A long[].
     */
    public long[] getLongs( String name );

    /**
     * Return an array of Longs for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A Long[].
     */
    public Long[] getLongObjects( String name );

    /**
     * Return a byte for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A byte.
     */
    public byte getByte( String name, byte defaultValue );

    /**
     * Return a byte for the given name.  If the name does not exist,
     * return 0.
     *
     * @param name A String with the name.
     * @return A byte.
     */
    public byte getByte( String name );

    /**
     * Return an array of bytes for the given name.  If the name does
     * not exist, return null. The array is returned according to the
     * HttpRequest's character encoding.
     *
     * @param name A String with the name.
     * @return A byte[].
     * @exception UnsupportedEncodingException
     */
    public byte[] getBytes( String name ) throws UnsupportedEncodingException;

    /**
     * Return a String for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return A String.
     */
    public String getString( String name );

    /**
     * Return a String for the given name.  If the name does not
     * exist, return null. It is the same as the getString() method
     * however has been added for simplicity when working with
     * template tools such as Velocity which allow you to do
     * something like this:
     *
     * <code>$data.Parameters.form_variable_name</code>
     *
     * @param name A String with the name.
     * @return A String.
     */
    public String get( String name );

    /**
     * Return a String for the given name.  If the name does not
     * exist, return the defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A String.
     */
    public String getString( String name, String defaultValue );

    /**
     * Return an array of Strings for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return A String[].
     */
    public String[] getStrings( String name );

    /**
     * Return an array of Strings for the given name.  If the name
     * does not exist, return the defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A String[].
     */
    public String[] getStrings( String name, String[] defaultValue );

    /**
     * Return an Object for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return An Object.
     */
    public Object getObject( String name );

    /**
     * Return an array of Objects for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return An Object[].
     */
    public Object[] getObjects( String name );

    /**
     * Returns a {@link java.util.Date} object.  String is parsed by supplied
     * DateFormat.  If the name does not exist, return the
     * defaultValue.
     *
     * @param name A String with the name.
     * @param df A DateFormat.
     * @param defaultValue The default value.
     * @return A Date.
     */
    public Date getDate( String name, DateFormat df, Date defaultValue );

    /**
     * Returns a {@link java.util.Date} object.  String is parsed by supplied
     * DateFormat.  If the name does not exist, return null.
     *
     * @param name A String with the name.
     * @param df A DateFormat.
     * @return A Date.
     */
    public Date getDate( String name, DateFormat df );

    /**
     * Returns a {@link java.util.Date} object.  String is parsed by supplied
     * DateFormat.  If the name does not exist, return null.
     *
     * @param name A String with the name.
     * @return A Date.
     */
    public Date getDate( String name );
}
