package org.codehaus.plexus.personality.avalon;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Wrapper which translates the Plexus Configuration to an AvalonConfiguration.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 6, 2004
 */
public class AvalonConfiguration
    implements Configuration
{
    org.codehaus.plexus.configuration.Configuration config;
    
    public AvalonConfiguration(org.codehaus.plexus.configuration.Configuration config)
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
        return config.getLocation();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getNamespace()
     */
    public String getNamespace() throws org.apache.avalon.framework.configuration.ConfigurationException
    {
        try
        {
            return config.getNamespace();
        }
        catch ( org.codehaus.plexus.configuration.ConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
        
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getChild(java.lang.String)
     */
    public Configuration getChild(String child)
    {
        return new AvalonConfiguration( config.getChild(child) );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getChild(java.lang.String, boolean)
     */
    public Configuration getChild(String child, boolean value)
    {
        return new AvalonConfiguration( config.getChild(child, value) );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getChildren()
     */
    public Configuration[] getChildren()
    {
        org.codehaus.plexus.configuration.Configuration[] children = config.getChildren();
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
    public Configuration[] getChildren(String name)
    {
        org.codehaus.plexus.configuration.Configuration[] children = config.getChildren(name);
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
    public String getAttribute(String attr) throws ConfigurationException
    {
        try
        {
            return config.getAttribute(attr);
        }
        catch ( org.codehaus.plexus.configuration.ConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsInteger(java.lang.String)
     */
    public int getAttributeAsInteger(String attr) throws ConfigurationException
    {
        try
        {
            return config.getAttributeAsInteger(attr);
        }
        catch ( org.codehaus.plexus.configuration.ConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsLong(java.lang.String)
     */
    public long getAttributeAsLong(String attr) throws ConfigurationException
    {
        try
        {
            return config.getAttributeAsLong(attr);
        }
        catch ( org.codehaus.plexus.configuration.ConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsFloat(java.lang.String)
     */
    public float getAttributeAsFloat(String attr) throws ConfigurationException
    {
        try
        {
            return config.getAttributeAsFloat(attr);
        }
        catch ( org.codehaus.plexus.configuration.ConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsBoolean(java.lang.String)
     */
    public boolean getAttributeAsBoolean(String attr) throws ConfigurationException
    {
        try
        {
            return config.getAttributeAsBoolean(attr);
        }
        catch ( org.codehaus.plexus.configuration.ConfigurationException e )
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
        catch ( org.codehaus.plexus.configuration.ConfigurationException e )
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
            return config.getValueAsInteger();
        }
        catch ( org.codehaus.plexus.configuration.ConfigurationException e )
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
            return config.getValueAsFloat();
        }
        catch ( org.codehaus.plexus.configuration.ConfigurationException e )
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
            return config.getValueAsBoolean();
        }
        catch ( org.codehaus.plexus.configuration.ConfigurationException e )
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
            return config.getValueAsLong();
        }
        catch ( org.codehaus.plexus.configuration.ConfigurationException e )
        {
            throw new ConfigurationException( "ConfigurationException.", e.getCause() );
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValue(java.lang.String)
     */
    public String getValue(String defaultValue)
    {
        return config.getValue( defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsInteger(int)
     */
    public int getValueAsInteger(int defaultValue)
    {
        return config.getValueAsInteger( defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsLong(long)
     */
    public long getValueAsLong(long defaultValue)
    {
        return config.getValueAsLong( defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsFloat(float)
     */
    public float getValueAsFloat(float defaultValue)
    {
        return config.getValueAsFloat( defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValueAsBoolean(boolean)
     */
    public boolean getValueAsBoolean(boolean defaultValue)
    {
        return config.getValueAsBoolean( defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttribute(java.lang.String, java.lang.String)
     */
    public String getAttribute(String attr, String defaultValue)
    {
        return config.getAttribute( attr, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsInteger(java.lang.String, int)
     */
    public int getAttributeAsInteger(String attr, int defaultValue)
    {
        return config.getAttributeAsInteger( attr, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsLong(java.lang.String, long)
     */
    public long getAttributeAsLong(String attr, long defaultValue)
    {
        return config.getAttributeAsLong( attr, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsFloat(java.lang.String, float)
     */
    public float getAttributeAsFloat(String attr, float defaultValue)
    {
        return config.getAttributeAsFloat( attr, defaultValue );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsBoolean(java.lang.String, boolean)
     */
    public boolean getAttributeAsBoolean(String attr, boolean defaultValue)
    {
        return config.getAttributeAsBoolean( attr, defaultValue );
    }

}
