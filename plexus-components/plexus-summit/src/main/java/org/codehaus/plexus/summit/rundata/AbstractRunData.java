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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.parameters.RequestParameterParser;
import org.codehaus.plexus.summit.parameters.RequestParameters;
import org.codehaus.plexus.summit.resolver.Resolution;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * <p>The base class from which all RunData implementations are derived. </p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @todo We should make a RunData implemenation that deals with Resolutions. It
 * Is possible that some might like to avoid the whole resolution process
 * and entirely do their own thing.
 */
public abstract class AbstractRunData
    extends AbstractSummitComponent
    implements RunData, Initializable, Disposable
{
    /**
     * Target view.
     */
    private String target;

    /**
     * Servlet Request.
     */
    private HttpServletRequest request;

    /**
     * Servlet Respsonse.
     */
    private HttpServletResponse response;

    /**
     * Servlet Config.
     */
    private ServletConfig servletConfig;

    /**
     * Content type.
     */
    private String contentType;

    /**
     * General storage.
     */
    private Map map = new HashMap();

    /**
     * Resolution produced by Resolver.
     */
    private Resolution resolution;

    /**
     * Request parameters.
     */
    private RequestParameters parameters;

    /**
     * The RequestParameterParser used to parse the request
     */
    private RequestParameterParser parameterParser;

    //TODO: use a requirement to create an instance of the parameter parser
    public void initialize()
        throws InitializationException
    {
        try
        {
            parameterParser = (RequestParameterParser) lookup( RequestParameterParser.ROLE );
        }
        catch ( ComponentLookupException e )
        {
            throw new InitializationException( "Can't lookup parameter parser: ", e );
        }
    }

    public void dispose()
    {
        try
        {
            getContainer().release( parameterParser );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets the target attribute of the AbstractRunData object
     */
    public String getTarget()
    {
        return target;
    }

    /**
     * Sets the target attribute of the AbstractRunData object
     */
    public void setTarget( String target )
    {
        this.target = target;
    }

    /**
     * Description of the Method
     */
    public boolean hasTarget()
    {
        return ( target != null );
    }

    /**
     * Sets the request attribute of the AbstractRunData object
     */
    public void setRequest( HttpServletRequest request )
    {
        this.request = request;
        this.parameters = parameterParser.parse( request );
    }

    /**
     * Gets the request attribute of the AbstractRunData object
     */
    public HttpServletRequest getRequest()
    {
        return request;
    }

    /**
     * Sets the response attribute of the AbstractRunData object
     */
    public void setResponse( HttpServletResponse response )
    {
        this.response = response;
    }

    /**
     * Gets the response attribute of the AbstractRunData object
     */
    public HttpServletResponse getResponse()
    {
        return response;
    }

    /**
     * Gets the session attribute of the AbstractRunData object
     */
    public HttpSession getSession()
    {
        return request.getSession();
    }

    /**
     * Sets the servletConfig attribute of the AbstractRunData object
     */
    public void setServletConfig( ServletConfig servletConfig )
    {
        this.servletConfig = servletConfig;
    }

    /**
     * Gets the servletConfig attribute of the AbstractRunData object
     */
    public ServletConfig getServletConfig()
    {
        return servletConfig;

    }

    /**
     * Sets the map attribute of the AbstractRunData object
     */
    public void setMap( Map map )
    {
        this.map = map;
    }

    /**
     * Gets the map attribute of the AbstractRunData object
     */
    public Map getMap()
    {
        return map;
    }

    /**
     * Sets the HTTP content type to return.
     *
     * @param contentType the new content type.
     */
    public void setContentType( String contentType )
    {
        this.contentType = contentType;
    }

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
    public String getContentType()
    {
        return contentType;
    }

    /**
     * Gets the request parameters.
     */
    public RequestParameters getParameters()
    {
        return parameters;
    }

    /**
     * Sets the Resolver attribute of the AbstractRunData object
     */
    public void setResolution( Resolution resolution )
    {
        this.resolution = resolution;
    }

    /**
     * Gets the Resolver attribute of the AbstractRunData object
     */
    public Resolution getResolution()
    {
        return resolution;
    }

    /**
     * Create a static link to resources in the webapp space.
     *
     * @param path Path to the static resource in the webapp space.
     * @return Full URL to the static resource in the webapp space.
     */
    public String getLink( String path )
    {
        StringBuffer sb = new StringBuffer();
        sb.append( getServerScheme() ); //http
        sb.append( "://" );
        sb.append( getServerName() ); //www.foo.com
        sb.append( ':' );
        sb.append( getServerPort() ); //port webserver running on (8080 for TDK)
        //the context for tomcat adds a / so no need to add another
        sb.append( getRequest().getContextPath() ); //the tomcat context
        sb.append( '/' );
        sb.append( path );
        return sb.toString();
    }


    /**
     * @see org.codehaus.plexus.summit.rundata.RunData#getContextPath()
     */
    public String getContextPath()
    {
        return request.getContextPath();
    }

    /**
     * @see org.codehaus.plexus.summit.rundata.RunData#getScriptName()
     */
    public String getScriptName()
    {
        return request.getServletPath();
    }

    /**
     * @see org.codehaus.plexus.summit.rundata.RunData#getServerName()
     */
    public String getServerName()
    {
        return request.getServerName();
    }

    /**
     * @see org.codehaus.plexus.summit.rundata.RunData#getServerPort()
     */
    public int getServerPort()
    {
        return request.getServerPort();
    }

    /**
     * @see org.codehaus.plexus.summit.rundata.RunData#getServerScheme()
     */
    public String getServerScheme()
    {
        return request.getScheme();
    }

    /**
     * @see org.codehaus.plexus.summit.rundata.RunData#getServletContext()
     */
    public ServletContext getServletContext()
    {
        return servletConfig.getServletContext();
    }
}
