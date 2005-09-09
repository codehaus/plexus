package org.codehaus.plexus.summit.rundata;

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

import java.util.HashMap;
import java.util.Map;
import java.util.List;

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

    private Throwable error;

    private List resultMessages;

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

    // ----------------------------------------------------------------------
    // Error
    // ----------------------------------------------------------------------

    public boolean hasError()
    {
        return ( error != null );
    }

    public Throwable getError()
    {
        return error;
    }

    public void setError( Throwable error )
    {
        this.error = error;
    }

    // ----------------------------------------------------------------------
    // Result Messsages
    // ----------------------------------------------------------------------

    public boolean hasResultMessages()
    {
        return ( resultMessages != null );
    }

    public List getResultMessages()
    {
        return resultMessages;
    }

    public void setResultMessages( List resultMessages )
    {
        this.resultMessages = resultMessages;
    }
}
