package org.codehaus.plexus.servlet;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.lifecycle.avalon.AvalonServiceManager;

/**
 * <code>ServletContextUtils</code> provides methods to embed a Plexus
 * container within a Servlet context.
 *
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
final class ServletContextUtils {
    static final String PLEXUS_CONFIG_PARAM = "plexus-config";

    private static final String DEFAULT_PLEXUS_CONFIG = "/WEB-INF/plexus.xml";

    // prevent instantiation
    private ServletContextUtils() {
    }

    private static String resolveConfig(ServletContext context, String plexusConf) {
        if (plexusConf == null) {
            plexusConf = context.getInitParameter( PLEXUS_CONFIG_PARAM );
        }
        if (plexusConf == null) {
            plexusConf = DEFAULT_PLEXUS_CONFIG;
        }
        
        return plexusConf;
    }
    
    /**
     * Create a Plexus container using the {@link Embedder}. This method
     * should be called from an environment where a
     * <code>ServletContext</code> is available. It will create and initialize
     * the Plexus container and place references to the container and the
     * Avalon service manager into the context.
     *
     * @param context The servlet context to place the container in.
     * @param plexusConf Name of the Plexus configuration file to load, or
     * <code>null</code> to fall back to the default behaviour.
     * @return a Plexus container that has been initialized and started.
     * @throws ServletException If the Plexus container could not be started.
     */
    static Embedder createContainer(ServletContext context, String plexusConf)
        throws ServletException
    {
        final Embedder embedder = new Embedder();
        
        String realPath = context.getRealPath( "/WEB-INF" );
        if ( realPath != null ) {
            File f = new File( realPath );
            embedder.addContextValue( "plexus.home", f.getAbsolutePath() );
        } else {
            context.log("Not setting plexus.home as plexus is running inside webapp with no 'real path'");
        }
        
        try
        {
            plexusConf = resolveConfig( context, plexusConf );
            embedder.setConfiguration( context.getResource(plexusConf) );
            embedder.start();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new ServletException( "Could not start Plexus!", e );
        }
        
        PlexusContainer plexus = embedder.getContainer();
        context.setAttribute( PlexusConstants.PLEXUS_KEY, plexus );
        
        ServiceManager serviceManager = new AvalonServiceManager( plexus.getComponentRepository() );
        context.setAttribute( PlexusConstants.SERVICE_MANAGER_KEY, serviceManager );
        return embedder;
    }

    static void destroyContainer(Embedder embedder, ServletContext context)
    {
        try
        {
            if ( embedder != null )
            {
                embedder.stop();
            }
        }
        catch (Exception e)
        {
            context.log("Trying to dispose of Plexus container", e);
        }
        finally
        {
            context.removeAttribute( PlexusConstants.PLEXUS_KEY );
            context.removeAttribute( PlexusConstants.SERVICE_MANAGER_KEY );
        }
    }
}
