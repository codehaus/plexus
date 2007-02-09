/*
 * Copyright (C) MX4J.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package org.codehaus.plexus.httpd;

/**
 * Define constants for the HTTP request processing
 *
 * @author <a href="mailto:tibu@users.sourceforge.org">Carlos Quiroz</a>
 * @version $Revision$
 */
public class HttpConstants
{
    /**
     * Server info header
     */
    public final static String SERVER_INFO = "MX4J-HTTPD/1.0";

    /**
     * HTTP implemented version
     */
    public final static String HTTP_VERSION = "HTTP/1.0 ";

    /**
     * Get method header
     */
    public final static String METHOD_GET = "GET";

    /**
     * Post method header
     */
    public final static String METHOD_POST = "POST";

    /**
     * Status code OK
     */
    public final static int STATUS_OKAY = 200;

    /**
     * Status code NO CONTENT
     */
    public final static int STATUS_NO_CONTENT = 204;

    /**
     * Status code MOVED PERMANENTLY
     */
    public final static int STATUS_MOVED_PERMANENTLY = 301;

    /**
     * Status code MOVED TEMPORARILY
     */
    public final static int STATUS_MOVED_TEMPORARILY = 302;

    /**
     * Status code BAD REQUEST
     */
    public final static int STATUS_BAD_REQUEST = 400;

    /**
     * Status code AUTHENTICATE
     */
    public final static int STATUS_AUTHENTICATE = 401;

    /**
     * Status code FORBIDDEN
     */
    public final static int STATUS_FORBIDDEN = 403;

    /**
     * Status code NOT FOUND
     */
    public final static int STATUS_NOT_FOUND = 404;

    /**
     * Status code NOT ALLOWED
     */
    public final static int STATUS_NOT_ALLOWED = 405;

    /**
     * Status code INTERNAL ERROR
     */
    public final static int STATUS_INTERNAL_ERROR = 500;

    /**
     * Status code NOT IMPLEMENTED
     */
    public final static int STATUS_NOT_IMPLEMENTED = 501;

}
