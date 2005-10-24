package org.codehaus.plexus.summit.parameters;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
