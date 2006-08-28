package org.codehaus.plexus.httpserver;

import org.apache.avalon.framework.activity.Startable;

/**
 * Configuration: Accepts "port" as an integer specifying the port to listen on
 * Configuration: Accepts "behaviour.role" as a role name specifying the HttpBehaviour component to use
 * @author  Ben Walding
 * @version $Id$
 */
public interface HttpServer extends Startable
{
	String ROLE = HttpServer.class.getName();
}
