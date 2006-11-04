package org.codehaus.plexus.summit.rundata;

import java.util.Map;
import java.util.List;

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
    public static final String ROLE = RunData.class.getName();

    //TODO: the target methods should be renamed to refer to a view.

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

    /** Determine whether an error has occured during request processing */
    public boolean hasError();

    /** Set the error that occurred during request processing */
    public void setError( Throwable error );

    /** Get the error that occurred during request processing */
    public Throwable getError();

    public boolean hasResultMessages();

    public void setResultMessages( List resulttMessages );

    public List getResultMessages();
}
