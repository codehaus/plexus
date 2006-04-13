/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.jetty;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ProxyListener
{
    private String host;

    private int port;

    private String proxyHost;

    private int proxyPort;

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getProxyHost()
    {
        return proxyHost;
    }

    public int getProxyPort()
    {
        return proxyPort;
    }
}
