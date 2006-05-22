package org.codehaus.plexus.jetty.configuration;

/**
 * @author Jason van Zyl
 */
public class InitParameter
{
    private String name;

    private String value;

    private String directive;

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public String getDirective()
    {
        return directive;
    }
}
