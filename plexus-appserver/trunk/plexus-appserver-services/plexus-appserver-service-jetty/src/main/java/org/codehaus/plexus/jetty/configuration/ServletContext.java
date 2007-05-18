package org.codehaus.plexus.jetty.configuration;

import java.util.List;

/**
 * @author Jason van Zyl
 */
public class ServletContext
    extends WebContext
{
    private String name;

    /**
     * This is going to be removed in favour of the servlet list
     * @deprecated
     */
    private String path;

    /**
     * This is going to be removed in favour of the servlet list
     * @deprecated
     */
    private String servlet;

    private List servlets;

    /**
     * This is going to be removed in favour of the servlet list
     * @deprecated
     */
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

    public List getServlets()
    {
        return servlets;
    }

    public List getInitParameters()
    {
        return initParameters;
    }
}
