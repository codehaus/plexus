package org.codehaus.plexus.jetty.configuration;

import java.util.List;

/**
 * @author Jason van Zyl
 */
public class ServletContext
    extends WebContext
{
    private String name;

    private String path;

    private String servlet;

    private List initParameters;

    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public String getServlet()
    {
        return servlet;
    }

    public List getInitParameters()
    {
        return initParameters;
    }
}
