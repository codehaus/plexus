package org.codehaus.plexus.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.codehaus.plexus.embed.Embedder;

/**
 * By adding this to the listeners for your web application, a Plexus container
 * will be instantiated and added to the attributes of the ServletContext.
 * <p>
 * The interface that this class implements appeared in the Java Servlet
 * API 2.3. For compatability with Java Servlet API 2.2 and before use
 * {@link PlexusLoaderServlet}.
 *
 * @see PlexusLoaderServlet
 *
 * @author <a href="bwalding@apache.org">Ben Walding</a>
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Id$
 */
public class PlexusServletContextListener implements ServletContextListener
{
    private Embedder embedder = null;

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext context = sce.getServletContext();
        String configName = context.getInitParameter( ServletContextUtils.PLEXUS_CONFIG_PARAM );

        context.log("Initializing Plexus container...");
        try
        {
            embedder = ServletContextUtils.createContainer(context, configName);
        }
        catch (ServletException e)
        {
            throw new RuntimeException(e.getLocalizedMessage());
        }
        context.log("Plexus container initialized.");
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)
    {
        ServletContext context = sce.getServletContext();
        context.log( "Disposing of Plexus container." );
        ServletContextUtils.destroyContainer( embedder, context );
    }
}
