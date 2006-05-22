package org.codehaus.plexus.jetty.configuration;

import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 */
public class WebContext
{
    private String path;

    private String context;

    private List listeners;

    private String virtualHost;

    private List initParameters;

    public String getPath()
    {
        return path;
    }

    public String getContext()
    {
        return context;
    }

    public String getVirtualHost()
    {
        return virtualHost;
    }

    public List getListeners()
    {
        return listeners;
    }
}
