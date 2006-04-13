/*
 * Copyright (C) MX4J.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package org.codehaus.plexus.httpd;

import java.net.ServerSocket;
import java.io.IOException;

/**
 * The ServerSocket factory interface. <p>
 * It allows to create ServerSocket for JMX adaptors
 *
 * @author <a href="mailto:tibu@users.sourceforge.net">Carlos Quiroz</a>
 * @author <a href="mailto:biorn_steedom@users.sourceforge.net">Simone Bordet</a>
 * @version $Revision$
 */
public interface AdaptorServerSocketFactory
{
	/**
	 * Creates a new ServerSocket on the specified port, with the specified backlog and on the given host. <br>
	 * The last parameter is useful for hosts with more than one IP address.
	 */
	public ServerSocket createServerSocket(int port, int backlog, String host) throws IOException;
}
