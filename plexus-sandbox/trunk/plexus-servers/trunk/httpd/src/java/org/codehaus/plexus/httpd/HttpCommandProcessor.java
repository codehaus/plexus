/*
 * Copyright (C) MX4J.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package org.codehaus.plexus.httpd;

import java.io.IOException;

import org.apache.velocity.context.Context;

/**
 * HttpCommandProcessor sets the structure of a command processor
 *
 * @author <a href="mailto:tibu@users.sourceforge.net">Carlos Quiroz</a>
 * @version $Revision$
 */
public interface HttpCommandProcessor
{

    /**
     * Executes an HTTP request. It assumes the request is well formed
     *
     * @param in Input request
     * @return An XML Document
     * @exception java.io.IOException
     */
    public Context executeRequest( HttpInputStream in ) throws IOException;
}
