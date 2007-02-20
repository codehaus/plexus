package org.codehaus.plexus.naming;

import java.util.Properties;

public class Resource
{
    private String name;

    private String type;

    private Properties properties;

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public Properties getProperties()
    {
        return properties;
    }
}
