package org.codehaus.plexus.personality.avalon;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

/**
 * Wrapper which translates the Plexus Configuration to an AvalonConfiguration.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 6, 2004
 */
public class AvalonConfiguration
    implements Configuration
{
    PlexusConfiguration config;

    public AvalonConfiguration( PlexusConfiguration config )
    {
        this.config = config;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getName()
     */
    public String getName()
    {
        return config.getName();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getLocation()
     */
    public String getLocation()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getNamespace()
     */
    public String getNamespace()
        throws ConfigurationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getChild(java.lang.String)
     */
    public Configuration getChild( String child )
    {
        return new AvalonConfiguration( config.getChild( child ) );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getChild(java.lang.String, boolean)
     */
    public Configuration getChild( String child, boolean value )
    {
        PlexusConfiguration c = config.getChild( child, value );
        if ( c != null )
        {
            return new AvalonConfiguration( c );
        }
        else
        {
            return null;
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getChildren()
     */
    public Configuration[] getChildren()
    {
        PlexusConfiguration[] children = config.getChildren();
        List childList = new ArrayList();

        for ( int i = 0; i < children.length; i++ )
        {
            childList.add( new AvalonConfiguration( children[i] ) );
        }

        return (Configuration[]) childList.toArray( new AvalonConfiguration[]{} );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getChildren(java.lang.String)
     */
    public Configuration[] getChildren( String name )
    {
        PlexusConfiguration[] children = config.getChildren( name );
        List childList = new ArrayList();

        for ( int i = 0; i < children.length; i++ )
        {
            childList.add( new AvalonConfiguration( children[i] ) );
        }

        return (Configuration[]) childList.toArray( new AvalonConfiguration[]{} );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeNames()
     */
    public String[] getAttributeNames()
    {
        return config.getAttributeNames();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttribute(java.lang.String)
     */
    public String getAttribute( String attr ) throws ConfigurationException
    {
        try
        {
            return config.getAttribute( attr );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsInteger(java.lang.String)
     */
    public int getAttributeAsInteger( String attr ) throws ConfigurationException
    {
        try
        {
            return getAttributeAsInteger( config, attr );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsLong(java.lang.String)
     */
    public long getAttributeAsLong( String attr ) throws ConfigurationException
    {
        try
        {
            return getAttributeAsLong( config, attr );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsFloat(java.lang.String)
     */
    public float getAttributeAsFloat( String attr ) throws ConfigurationException
    {
        try
        {
            return getAttributeAsFloat( config, attr );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsBoolean(java.lang.String)
     */
    public boolean getAttributeAsBoolean( String attr ) throws ConfigurationException
    {
        try
        {
            return getAttributeAsBoolean( config, attr );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValue()
     */
    public String getValue() throws ConfigurationException
    {
        try
        {
            return config.getValue();
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsInteger()
     */
    public int getValueAsInteger() throws ConfigurationException
    {
        try
        {
            return getValueAsInteger( config );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsFloat()
     */
    public float getValueAsFloat() throws ConfigurationException
    {
        try
        {
            return getValueAsFloat( config );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsBoolean()
     */
    public boolean getValueAsBoolean() throws ConfigurationException
    {
        try
        {
            return getValueAsBoolean( config );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsLong()
     */
    public long getValueAsLong() throws ConfigurationException
    {
        try
        {
            return getValueAsLong( config );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValue(java.lang.String)
     */
    public String getValue( String defaultValue )
    {
        return config.getValue( defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsInteger(int)
     */
    public int getValueAsInteger( int defaultValue )
    {
        return getValueAsInteger( config, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsLong(long)
     */
    public long getValueAsLong( long defaultValue )
    {
        return getValueAsLong( config, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsFloat(float)
     */
    public float getValueAsFloat( float defaultValue )
    {
        return getValueAsFloat( config, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsBoolean(boolean)
     */
    public boolean getValueAsBoolean( boolean defaultValue )
    {
        return getValueAsBoolean( config, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttribute(java.lang.String, java.lang.String)
     */
    public String getAttribute( String attr, String defaultValue )
    {
        return config.getAttribute( attr, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsInteger(java.lang.String, int)
     */
    public int getAttributeAsInteger( String attr, int defaultValue )
    {
        return getAttributeAsInteger( config, attr, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsLong(java.lang.String, long)
     */
    public long getAttributeAsLong( String attr, long defaultValue )
    {
        return getAttributeAsLong( config, attr, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsFloat(java.lang.String, float)
     */
    public float getAttributeAsFloat( String attr, float defaultValue )
    {
        return getAttributeAsFloat( config, attr, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsBoolean(java.lang.String, boolean)
     */
    public boolean getAttributeAsBoolean( String attr, boolean defaultValue )
    {
        return getAttributeAsBoolean( config, attr, defaultValue );
    }

    // ----------------------------------------------------------------------
    // Little helpers to translate to PlexusConfiguration/
    // ----------------------------------------------------------------------

    public  String getPrefix()
    {
        throw new UnsupportedOperationException();
    }

    public int getValueAsInteger( PlexusConfiguration configuration )
        throws PlexusConfigurationException
    {
        String value = configuration.getValue();
        
        if ( value == null )
            throw new PlexusConfigurationException("No value.");
        
        value = value.trim();
        
        try
        {
            return Integer.parseInt( value );
        }
        catch ( Exception nfe )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as an integer in the configuration element \""
                + getName() + "\" at " + getLocation();
            throw new PlexusConfigurationException( message );
        }
    }

    public int getValueAsInteger( PlexusConfiguration configuration, int defaultValue )
    {
        try
        {
            return getValueAsInteger();
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public long getValueAsLong( PlexusConfiguration configuration )
        throws PlexusConfigurationException
    {
        String value = configuration.getValue();
        
        if ( value == null )
            throw new PlexusConfigurationException("No value.");
        
        value = value.trim();
        
        try
        {
            return Long.parseLong( value );
        }
        catch ( Exception nfe )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a long in the configuration element \""
                + getName() + "\" at " + getLocation();
            throw new PlexusConfigurationException( message );
        }
    }

    public long getValueAsLong( PlexusConfiguration configuration, long defaultValue )
    {
        try
        {
            return getValueAsLong();
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public float getValueAsFloat( PlexusConfiguration configuration )
        throws PlexusConfigurationException
    {
        String value = configuration.getValue();
        
        if ( value == null )
            throw new PlexusConfigurationException("No value.");
        
        value = value.trim();
        
        try
        {
            return Float.parseFloat( value );
        }
        catch ( Exception nfe )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a float in the configuration element \""
                + getName() + "\" at " + getLocation();
            throw new PlexusConfigurationException( message );
        }
    }

    public float getValueAsFloat( PlexusConfiguration configuration, float defaultValue )
    {
        try
        {
            return getValueAsFloat();
        }
        catch ( ConfigurationException ce )
        {
            return ( defaultValue );
        }
    }

    public boolean getValueAsBoolean( PlexusConfiguration configuration )
        throws PlexusConfigurationException
    {
        String value = configuration.getValue();
        
        if ( value == null )
            throw new PlexusConfigurationException("No value.");
        
        value = value.trim();
        
        if ( isTrue( value ) )
        {
            return true;
        }
        else if ( isFalse( value ) )
        {
            return false;
        }
        else
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a boolean in the configuration element \""
                + getName() + "\" at " + getLocation();
            throw new PlexusConfigurationException( message );
        }
    }

    public boolean getValueAsBoolean( PlexusConfiguration configuration, boolean defaultValue )
    {
        try
        {
            return getValueAsBoolean();
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public String getValue( PlexusConfiguration configuration, String defaultValue )
    {
        try
        {
            return getValue();
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public int getAttributeAsInteger( PlexusConfiguration configuration,
                                      String name )
        throws PlexusConfigurationException
    {
        String value = configuration.getAttribute( name );
        
        if ( value == null )
            throw new PlexusConfigurationException("No value.");
        
        value = value.trim();
        
        try
        {

            return Integer.parseInt( value );
        }
        catch ( Exception nfe )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as an integer in the attribute \""
                + name + "\" at " + getLocation();
            throw new PlexusConfigurationException( message );
        }
    }

    public int getAttributeAsInteger( PlexusConfiguration configuration, String name, int defaultValue )
    {
        try
        {
            return getAttributeAsInteger( name );
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public long getAttributeAsLong( PlexusConfiguration configuration, String name )
        throws PlexusConfigurationException
    {
        String value = configuration.getAttribute( name );
        
        if ( value == null )
            throw new PlexusConfigurationException("No value.");
        
        value = value.trim();
        
        try
        {
            return Long.parseLong( value );
        }
        catch ( Exception nfe )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a long in the attribute \""
                + name + "\" at " + getLocation();
            throw new PlexusConfigurationException( message );
        }
    }

    public long getAttributeAsLong( PlexusConfiguration configuration, String name, long defaultValue )
    {
        try
        {
            return getAttributeAsLong( name );
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public float getAttributeAsFloat( PlexusConfiguration configuration, String name )
        throws PlexusConfigurationException
    {
        String value = configuration.getAttribute( name );
        
        if ( value == null )
            throw new PlexusConfigurationException("No value.");
        
        value = value.trim();
        
        try
        {
            return Float.parseFloat( value );
        }
        catch ( Exception e )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a float in the attribute \""
                + name + "\" at " + getLocation();
            throw new PlexusConfigurationException( message );
        }
    }

    public float getAttributeAsFloat( PlexusConfiguration configuration, String name, float defaultValue )
    {
        try
        {
            return getAttributeAsFloat( name );
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public boolean getAttributeAsBoolean( PlexusConfiguration configuration, String name )
        throws PlexusConfigurationException
    {
        String value = configuration.getAttribute( name );
        
        if ( value == null )
            throw new PlexusConfigurationException("No value.");
        
        value = value.trim();
        
        if ( isTrue( value ) )
        {
            return true;
        }
        else if ( isFalse( value ) )
        {
            return false;
        }
        else
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a boolean in the attribute \""
                + name + "\" at " + getLocation();
            throw new PlexusConfigurationException( message );
        }
    }

    private boolean isTrue( String value )
    {
        return value.equalsIgnoreCase( "true" )
            || value.equalsIgnoreCase( "yes" )
            || value.equalsIgnoreCase( "on" )
            || value.equalsIgnoreCase( "1" );
    }

    private boolean isFalse( String value )
    {
        return value.equalsIgnoreCase( "false" )
            || value.equalsIgnoreCase( "no" )
            || value.equalsIgnoreCase( "off" )
            || value.equalsIgnoreCase( "0" );
    }

    protected boolean getAttributeAsBoolean( PlexusConfiguration configuration,
                                             String name,
                                             boolean defaultValue )
    {
        try
        {
            return getAttributeAsBoolean( name );
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public String getAttribute( PlexusConfiguration configuration, String name, String defaultValue )
    {
        try
        {
            return getAttribute( name );
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }
}
