package org.codehaus.plexus.configuration;


import java.util.Properties;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PropertiesPlexusConfiguration
    extends DefaultPlexusConfiguration
{
    private Properties properties;

    public PropertiesPlexusConfiguration()
    {
        super( "" );
    }

    public PropertiesPlexusConfiguration( Properties properties )
    {
        super( "" );

        this.properties = properties;
    }

    public void setProperty( String key, String value )
    {
        getProperties().setProperty( key, value );
    }

    private Properties getProperties()
    {
        if ( properties == null )
        {
            properties = new Properties();
        }

        return properties;
    }

    public PlexusConfiguration getChild( String name )
    {
        String value = properties.getProperty( name );

        if ( value == null )
        {
            value = "";
        }

        return new ValueConfiguration( name, value );
    }

    public String getValue()
    {
        throw new UnsupportedOperationException();
    }

    public String getName()
    {
        throw new UnsupportedOperationException();
    }

    public String getAttribute( String name )
    {
        throw new UnsupportedOperationException();
    }

    public String[] getAttributeNames()
    {
        throw new UnsupportedOperationException();
    }

    public PlexusConfiguration[] getChildren()
    {
        throw new UnsupportedOperationException();
    }

    public PlexusConfiguration[] getChildren( String name )
    {
        throw new UnsupportedOperationException();
    }

    public String getNamespace()
    {
        throw new UnsupportedOperationException();
    }

    public String getPrefix()
    {
        throw new UnsupportedOperationException();
    }

    public String getLocation()
    {
        throw new UnsupportedOperationException();
    }

    public PlexusConfiguration getChild( int i )
    {
        return null;
    }

    public int getChildCount()
    {
        return 0;
    }

    static class ValueConfiguration
        extends DefaultPlexusConfiguration
    {
        private String name;

        private String value;

        public ValueConfiguration( String name, String value )
        {
            super( "" );

            this.name = name;

            this.value = value;
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }

        public PlexusConfiguration getChild( String name )
        {
            throw new UnsupportedOperationException();
        }

        public String getAttribute( String name )
        {
            throw new UnsupportedOperationException();
        }

        public String[] getAttributeNames()
        {
            throw new UnsupportedOperationException();
        }

        public PlexusConfiguration[] getChildren()
        {
            throw new UnsupportedOperationException();
        }

        public PlexusConfiguration[] getChildren( String name )
        {
            throw new UnsupportedOperationException();
        }

        public String getNamespace()
        {
            throw new UnsupportedOperationException();
        }

        public String getPrefix()
        {
            throw new UnsupportedOperationException();
        }

        public String getLocation()
        {
            return "unknown";
        }

        public PlexusConfiguration getChild( int i )
        {
            return null;
        }

        public int getChildCount()
        {
            return 0;
        }
    }
}
