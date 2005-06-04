package org.codehaus.plexus.summit.rundata;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ----------------------------------------------------------------------------
 */

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.plexus.summit.SummitComponent;
import org.codehaus.plexus.summit.parameters.RequestParameters;
import org.codehaus.plexus.summit.resolver.Resolution;

/**
 * RunData is an interface to run-rime information that is passed within
 * Summit. This provides the threading mechanism for the entire system because
 * multiple requests can potentially come in at the same time. Thus, there is
 * only one RunData implementation for each request that is being serviced.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public interface RunData
    extends SummitComponent
{
    /**
     * Role name.
     */
    public static final String ROLE = RunData.class.getName();

    /**
     * Gets the target attribute of the RunData object
     */
    String getTarget();

    /**
     * Sets the target attribute of the RunData object
     */
    void setTarget( String template );

    /**
     * Description of the Method
     */
    boolean hasTarget();

    /**
     * Sets the request attribute of the RunData object
     */
    void setRequest( HttpServletRequest r );

    /**
     * Gets the request attribute of the RunData object
     */
    HttpServletRequest getRequest();

    /**
     * Sets the response attribute of the RunData object
     */
    void setResponse( HttpServletResponse r );

    /**
     * Gets the response attribute of the RunData object
     */
    HttpServletResponse getResponse();

    /**
     * Gets the session attribute of the RunData object
     */
    HttpSession getSession();

    /**
     * Sets the servletConfig attribute of the RunData object
     */
    void setServletConfig( ServletConfig servletConfig );

    /**
     * Gets the servletConfig attribute of the RunData object
     */
    ServletConfig getServletConfig();

    /**
     * Sets the map attribute of the RunData object
     */
    void setMap( Map map );

    /**
     * Gets the map attribute of the RunData object
     */
    Map getMap();

    /**
     * Gets the HTTP content type to return. If a charset has been specified, it
     * is included in the content type. If the charset has not been specified
     * and the main type of the content type is "text", the default charset is
     * included. If the default charset is undefined, but the default locale is
     * defined and it is not the US locale, a locale specific charset is
     * included.
     *
     * @return the content type or an empty string.
     */
    String getContentType();

    /**
     * Sets the HTTP content type to return.
     *
     * @param ct the new content type.
     */
    void setContentType( String ct );

    /**
     * Gets the request parameters.
     */
    RequestParameters getParameters();

    /**
     * Sets the Resolver attribute of the RunData object
     */
    void setResolution( Resolution resolution );

    /**
     * Gets the Resolver attribute of the RunData object
     */
    Resolution getResolution();

    /**
     * Get the server name.
     */
    public String getServerName();

    /**
     * Get the server port.
     */
    public int getServerPort();

    /**
     * Get the server scheme.
     */
    public String getServerScheme();


    /**
     * Get the initial script name.
     */
    public String getScriptName();

    /**
     * Get the servlet's context path for this webapp.
     */
    public String getContextPath();

    /**
     * Get the servlet context for this turbine webapp.
     */
    public ServletContext getServletContext();
}
