/*
 * BSD License http://open-im.net/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.net
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.net/
 */
package net.java.dev.openim;

import java.util.List;


/**
 * @author AlAg
 * @version 1.0
 */
public interface ServerParameters
{
    public int getLocalClientPort();

    public int getLocalSSLClientPort();

    public int getLocalServerPort();

    public int getLocalSSLServerPort();

    public List getHostNameList();

    public String getHostName();

    public int getRemoteServerPort();
}


