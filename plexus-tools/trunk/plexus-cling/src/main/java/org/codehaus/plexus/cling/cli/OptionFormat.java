package org.codehaus.plexus.cling.cli;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * typesafe enumeration class that contains several implementations of itself,
 * which each represent a different validation rule for a command line option,
 * and each translate the supplied string value into the appropriate object type
 * for this option type.
 * 
 * @author John Casey
 */
public abstract class OptionFormat
{

    private static int indexCounter = 0;

    private String description;

    private int ordinal;

    /**
     * Create a new OptionFormat.
     * 
     * @param description
     *            The description for this option type.
     * @param ordinal
     *            The internal index of the option type.
     */
    protected OptionFormat( String description )
    {
        this.ordinal = indexCounter++;
        this.description = description;
    }

    /*
     * override these methods to produce a valid, functioning formatter.
     * --------
     */

    /**
     * Returns whether the supplied command line value is valid in accordance
     * with this option type.
     * 
     * @param value
     *            The value to validate.
     * @return true if the value obeys this type's validation rules, else false.
     */
    public abstract boolean isValid( String value );

    /**
     * Return the value from the command line, translated by this option type
     * formatter.
     * 
     * @param value
     *            The command line value to translate.
     * @return the translated object derived from the command line value string.
     */
    public abstract Object getValue( String value );

    /**
     * Return the description of this option format.
     * 
     * @return the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Return the internal index of this option format.
     * 
     * @return the internal index.
     */
    public int getOrdinal()
    {
        return ordinal;
    }

    /**
     * Lookup the OptionFormat instance corresponding to the specified internal
     * index.
     * 
     * @param ordinal
     *            The internal index to lookup.
     * @return the corresponding OptionFormat instance, or null if no match
     *         found.
     */
    public static OptionFormat valueOf( int ordinal )
    {
        if ( ordinal < 0 || ordinal >= FORMATS.length )
        {
            return null;
        }
        else
        {
            return FORMATS[ordinal];
        }
    }

    /**
     * Lookup the OptionFormat instance corresponding to the specified
     * description.
     * 
     * @param description
     *            The description to lookup.
     * @return the corresponding OptionFormat instance, or null if no match
     *         found.
     */
    public static OptionFormat valueOf( String description )
    {
        for ( int i = 0, len = FORMATS.length; i < len; i++ )
        {
            if ( FORMATS[i].description.equals( description ) )
            {
                return FORMATS[i];
            }
        }
        return null;
    }

    /**
     * Return a debug-appropriate string representation of this OptionFormat
     * instance.
     * 
     * @return the representation.
     */
    public String toString()
    {
        return "OptionFormat: " + description + "[ordinal=" + ordinal + "]";
    }

    /**
     * Perform an inline replacement of one OptionFormat instance for another in
     * the case of a deserialization event from java.io.ObjectInputStream. This
     * allows us to keep only one instance of a particular OptionFormat type,
     * even when the OptionFormat instance is read from a stream.
     * 
     * @return The replacement OptionFormat for the object "this"
     */
    private Object readResolve()
    {
        return valueOf( ordinal );
    }

    /**
     * Validation format for command line parameters of type String. This is
     * really pretty trivial, but provided for completeness.
     */
    public static final OptionFormat STRING_FORMAT = new OptionFormat( "String Format" ) {
        public boolean isValid( String value )
        {
            return true;
        }

        public Object getValue( String value )
        {
            return value;
        }
    };

    /**
     * Validation format for command line parameters of a numeric type.
     * Translations here are to java.lang.Long.
     */
    public static final OptionFormat NUMBER_FORMAT = new OptionFormat( "Number Format" ) {
        public boolean isValid( String value )
        {
            for ( int i = 0, len = value.length(); i < len; i++ )
            {
                if ( !Character.isDigit( value.charAt( i ) ) )
                {
                    return false;
                }
            }
            return true;
        }

        public Object getValue( String value )
        {
            return new Long( value );
        }
    };

    /**
     * Validation format for command line parameters of a numeric type with the
     * possibility of having fractional values (digits after the decimal point).
     * Translations here are to java.lang.Double.
     */
    public static final OptionFormat FRACTIONAL_NUMBER_FORMAT = new OptionFormat( "Fractional Number Format" ) {
        public boolean isValid( String value )
        {
            for ( int i = 0, len = value.length(); i < len; i++ )
            {
                char val = value.charAt( i );
                if ( !Character.isDigit( val ) && val != '.' )
                {
                    return false;
                }
            }
            return true;
        }

        public Object getValue( String value )
        {
            return new Double( value );
        }
    };

    /**
     * Validation format for command line parameters of a boolean type. This
     * implementation uses string comparison to resolve whether or not the
     * parameter is valid. <br>
     * Valid true values are: <br>
     * <ul>
     * <li>true</li>
     * <li>on</li>
     * <li>yes</li>
     * <li>+</li>
     * </ul>
     * <br>
     * Valid false values are: <br>
     * <ul>
     * <li>false</li>
     * <li>off</li>
     * <li>no</li>
     * <li>-</li>
     * </ul>
     * <br>
     * NOTE: comparisons are <b>NOT </b> case-sensitive.
     */
    public static final OptionFormat BOOLEAN_FORMAT = new OptionFormat( "Boolean Format" ) {
        public boolean isValid( String value )
        {
            return Boolean.valueOf( value ) != null;
        }

        public Object getValue( String value )
        {
            return Boolean.valueOf( value );
        }
    };

    private static final SimpleDateFormat dateFormatMMDDYYYY = new SimpleDateFormat( "MM/dd/yyyy" );

    /** Validation format for date strings of the form MM/dd/yyyy */
    public static final OptionFormat DATE_FORMAT_MM_dd_yyyy = new OptionFormat( "Date Format (MM/dd/yyyy)" ) {
        public boolean isValid( String value )
        {
            try
            {
                dateFormatMMDDYYYY.parse( value );
                return true;
            }
            catch ( ParseException e )
            {
                return false;
            }
        }

        public Object getValue( String value )
        {
            try
            {
                return dateFormatMMDDYYYY.parse( value );
            }
            catch ( ParseException e )
            {
                throw new IllegalArgumentException( "invalid date for format: MM/dd/yyyy" );
            }
        }
    };

    private static final SimpleDateFormat dateFormatYYYYMMDDKKMMSS = new SimpleDateFormat( "yyyy-MM-dd kk:mm:ss" );

    /**
     * Validation format for date/time strings of the form yyyy-MM-dd kk:mm:ss
     * (Standard SQL format).
     */
    public static final OptionFormat DATE_FORMAT_yyyy_MM_dd_kk_mm_ss = new OptionFormat(
        "Date Format (yyyy-MM-dd kk:mm:ss)" ) {
        public boolean isValid( String value )
        {
            try
            {
                dateFormatYYYYMMDDKKMMSS.parse( value );
                return true;
            }
            catch ( ParseException e )
            {
                return false;
            }
        }

        public Object getValue( String value )
        {
            try
            {
                return dateFormatYYYYMMDDKKMMSS.parse( value );
            }
            catch ( ParseException e )
            {
                e.printStackTrace();
                throw new IllegalArgumentException( "invalid date for format: yyyy-MM-dd hh:mm:ss" );
            }
        }
    };

    /**
     * Validation format for command line parameters representing a file. The
     * isValid() method returns a trivial true value, without really checking
     * anything. This is because the file may not yet exist, and yet may be a
     * valid name. The current java.io package doesn't allow for checks against
     * the validity of a filename without opening a stream and catching the
     * IOException. The return type for the getValue() method is actually
     * java.io.File.
     */
    public static final OptionFormat FILE_FORMAT = new OptionFormat( "File Format" ) {
        public boolean isValid( String value )
        {
            return true;
        }

        public Object getValue( String value )
        {
            return new java.io.File( value );
        }
    };

    /**
     * Validation format for command line parameters representing a directory.
     * The isValid() method returns true if the dir doesn't yet exist, or if it
     * does, if the dir is actually a directory, not a regular file. The return
     * type for the getValue() method is actually java.io.File.
     */
    public static final OptionFormat DIR_FORMAT = new OptionFormat( "Directory Format" ) {
        public boolean isValid( String value )
        {
            File f = new File( value );
            return !f.exists() || f.isDirectory();
        }

        public Object getValue( String value )
        {
            return new java.io.File( value );
        }
    };

    /**
     * Validation format for command line parameters representing a java
     * package. The isValid() method checks the input against a regular
     * expression for validity.
     */
    public static final OptionFormat JAVA_PACKAGE_FORMAT = new OptionFormat( "Java Package Format" ) {
        private static final String PATTERN = "([a-z]+\\.?)+";

        public boolean isValid( String value )
        {
            return value.matches( PATTERN );
        }

        public Object getValue( String value )
        {
            return value;
        }
    };

    /**
     * Validation format for command line parameters representing a java
     * package. The isValid() method checks the input against a regular
     * expression for validity.
     */
    public static final OptionFormat JAVA_CLASS_FORMAT = new OptionFormat( "Java Class Format" ) {
        public boolean isValid( String value )
        {
            try
            {
                Class.forName( value );
                return true;
            }
            catch ( ClassNotFoundException ex )
            {
                return false;
            }
        }

        public Object getValue( String value )
        {
            try
            {
                return Class.forName( value );
            }
            catch ( ClassNotFoundException e )
            {
                return null;
            }
        }
    };

    private static final OptionFormat[] FORMATS = { STRING_FORMAT, NUMBER_FORMAT, FRACTIONAL_NUMBER_FORMAT,
        BOOLEAN_FORMAT, DATE_FORMAT_MM_dd_yyyy, DATE_FORMAT_yyyy_MM_dd_kk_mm_ss, FILE_FORMAT, DIR_FORMAT,
        JAVA_PACKAGE_FORMAT, JAVA_CLASS_FORMAT };

}