/*
 * Copyright (C) MX4J.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package org.codehaus.plexus.httpd;

import org.apache.velocity.context.Context;

import java.io.IOException;

/**
 * Processor ManagementBean, just defines a generic description
 *
 * @author <a href="mailto:tibu@users.sourceforge.net">Carlos Quiroz</a>
 * @version $Revision$
 */
public interface Processor
{
    public String getName();

    /**
     * The method will process the result string and produce an output. The
     * implementor is also responsible to set the mime type, response code and
     * send the headers before answering as follow:
     * <code>
     *		out.setCode(HttpConstants.STATUS_OKAY);
     *		out.setHeader("Content-type", "text/html");
     *		out.sendHeaders();
     *	  out.write("some text");
     * </code>
     *
     * @param out The output stream
     * @param in The input stream
     * @param context
     */
    public void writeResponse( HttpOutputStream out,
                               HttpInputStream in,
                               Context context,
                               String template )
        throws Exception;

    /**
     * The method will process the result exception and produce output. The
     * implementor is also responsible to set the mime type, response code and
     * send the headers before answering as follow:
     * <code>
     *		out.setCode(HttpConstants.STATUS_OKAY);
     *		out.setHeader("Content-type", "text/html");
     *		out.sendHeaders();
     *	  out.write("some text");
     * </code>
     *
     * @param out The output stream
     * @param in The input stream
     * @param e The exception to be reported
     */
    public void writeError( HttpOutputStream out,
                            HttpInputStream in,
                            Exception e )
        throws IOException;

    /**
     * Preprocess a path and return a replacement path. For instance the / path
     * could be replaced by the server path
     *
     * @param path The original path
     * @return the replacement path. If not modification the path param should
     * be returned
     */
    public String preProcess( String path );

    /**
     * Let the processor load internally a not found element. This can be used
     * to load images, stylesheets and so on. If return is not null, the path is
     * processed
     *
     * @param path The request element
     * @param out The output stream
     * @param in The input stream
     */
    public String notFoundElement( String path, HttpOutputStream out, HttpInputStream in ) throws IOException, HttpException;

}
