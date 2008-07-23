package org.codehaus.plexus.jetty.configuration;

import java.util.List;

/**
 * @author Jason van Zyl
 */
public class Servlet
    extends WebContext
{
    private String name;

    private String path;

    private String servletClass;

    private List initParameters;

    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public String getServletClass()
    {
        return servletClass;
    }

    public List getInitParameters()
    {
        return initParameters;
    }
}
