package org.codehaus.plexus.summit.parameters;

import javax.servlet.http.HttpServletRequest;

/**
 * Parses an HTTP GET/POST request for parameters passed via the query
 * info and/or path info.  The parser generates a map of parameters
 * which is then wrapped in a {@link RequestParameters} object that
 * provides numerous convienence methods that operate on the map.
 *
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 */
public interface RequestParameterParser
{
    /**
     * The role.
     */
    public static final String ROLE = RequestParameterParser.class.getName();

    /**
     * Parses the query info and path info of an HTTP request for
     * parameters in the form of name/value pairs.  The parameters
     * are returned to the user wrapped by {@link RequestParameters}.
     *
     * @param request The HTTP request to parse for parameters.
     * @return RequestParameters The requested parameters wrapped in
     *         a RequestParameters object for easy access to the parameters.
     */
    public RequestParameters parse( HttpServletRequest request );
}
