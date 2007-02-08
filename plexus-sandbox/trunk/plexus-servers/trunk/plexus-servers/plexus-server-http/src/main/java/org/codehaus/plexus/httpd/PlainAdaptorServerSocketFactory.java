/*
 * Copyright (C) MX4J.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package org.codehaus.plexus.httpd;

import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.IOException;

/**
 * Creates plain ServerSockets.
 *
 * @author <a href="mailto:tibu@users.sourceforge.net">Carlos Quiroz</a>
 * @author <a href="mailto:biorn_steedom@users.sourceforge.net">Simone Bordet</a>
 * @version $Revision$
 */
public class PlainAdaptorServerSocketFactory implements AdaptorServerSocketFactory
{
	public ServerSocket createServerSocket(int port, int backlog, String host) throws IOException
	{
		return new ServerSocket(port, backlog, InetAddress.getByName(host));
	}
}
