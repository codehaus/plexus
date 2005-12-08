package org.codehaus.plexus.logging.log4j;

/*
 * LICENSE
 */

import java.util.Properties;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id$
 */
public class Appender
{
    /** */
    private String id;

    /** */
    private String threshold;

    /** */
    private String type;

    /** */
    private String conversionPattern;

    /** */
    private Properties properties;

    public Appender()
    {
    }

    /**
     * @return Returns the id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * @return Returns the threshold.
     */
    public String getThreshold()
    {
        return threshold;
    }

    /**
     * @param threshold
     *            The threshold to set.
     */
    public void setThreshold( String threshold )
    {
        this.threshold = threshold;
    }

    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType( String type )
    {
        this.type = type;
    }

    /**
     * @return Returns the conversionPattern.
     */
    public String getConversionPattern()
    {
        return conversionPattern;
    }

    /**
     * @param conversionPattern
     *            The conversionPattern to set.
     */
    public void setConversionPattern( String conversionPattern )
    {
        this.conversionPattern = conversionPattern;
    }

    /**
     * @return Returns the properties.
     */
    public Properties getProperties()
    {
        if ( properties == null )
        {
            return new Properties();
        }

        return properties;
    }

    /**
     * @param key
     *            The property key.
     * @return The property value.
     */
    public String getProperty( String key )
    {
        return properties.getProperty( key );
    }

    /**
     * @param properties
     *            The properties to set.
     */
    public void setProperties( Properties properties )
    {
        this.properties = properties;
    }
}
