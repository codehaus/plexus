package org.codehaus.plexus.httpserver;

import java.io.IOException;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public interface HttpBehaviour
{
	//No ROLE as there is no default implementation
    
    /**
     * @param request The http request that this behaviour must fill.
     */
    HttpResponse getResponse(HttpRequest request) throws IOException;
}
