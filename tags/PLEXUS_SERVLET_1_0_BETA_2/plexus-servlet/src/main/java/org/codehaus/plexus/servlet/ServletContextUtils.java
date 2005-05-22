package org.codehaus.plexus.servlet;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.util.PropertyUtils;

/**
 * <code>ServletContextUtils</code> provides methods to embed a Plexus
 * container within a Servlet context.
 *
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
final class ServletContextUtils {
    
    private static final String PLEXUS_HOME = "plexus.home";
    
    static final String PLEXUS_CONFIG_PARAM = "plexus-config";

    private static final String PLEXUS_PROPERTIES_PARAM = "plexus-properties";
    
    private static final String DEFAULT_PLEXUS_CONFIG = "/WEB-INF/plexus.xml";

    private static final String DEFAULT_PLEXUS_PROPERTIES = "/WEB-INF/plexus.properties";
    
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
    
    private static Properties resolveContextProperties( ServletContext context ) 
    {
        
        Properties properties = new Properties();
        
        String filename = context.getInitParameter( PLEXUS_PROPERTIES_PARAM );
        
        if ( filename == null )
        {
            filename = DEFAULT_PLEXUS_PROPERTIES;  
        }
                
                
        context.log( "Loading plexus context properties from: '" + filename + "'" );
        
        try
        {
            URL url = context.getResource( filename );
            // bwalding: I think we'd be better off not using this exception swallower!
            properties = PropertyUtils.loadProperties( url  );
        }
        catch( Exception e)
        {
            // michal: I don't think it is that good idea to ignore this error.
            // bwalding: it's actually pretty difficult to get here as the PropertyUtils.loadProperties absorbs all Exceptions
            context.log( "Could not load plexus context properties from: '" + filename + "'" );
        }


        if (properties == null)
        {
            context.log( "Could not load plexus context properties from: '" + filename + "'" );
            properties = new Properties();
        }
        
        if ( !properties.containsKey( PLEXUS_HOME ) )
        {
            setPlexusHome( context, properties );
        }
                
        return properties;
    }
    
    /**
     * Set plexus.home context variable
     * @param context
     * @param embedder
     */
    private static void setPlexusHome( final ServletContext context, final Properties contexProperties  )
    {
        String realPath = context.getRealPath( "/WEB-INF" );
        
        if ( realPath != null ) 
        {
            File f = new File( realPath );
            
            contexProperties.setProperty( PLEXUS_HOME, f.getAbsolutePath()  );
            
        } 
        else 
        {
            context.log("Not setting 'plexus.home' as plexus is running inside webapp with no 'real path'");
        }
    }
    
    
    /**
     * Create a Plexus container using the {@link Embedder}. This method
     * should be called from an environment where a
     * <code>ServletContext</code> is available. It will create and initialize
     * the Plexus container and place references to the container into the context.
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

        try
        {            
            plexusConf = resolveConfig( context, plexusConf );                       
            
            embedder.setConfiguration( context.getResource(plexusConf) );
            
            Properties properties = resolveContextProperties( context );
            
            embedder.setProperties( properties );
            
            embedder.start();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new ServletException( "Could not start Plexus!", e );
        }
        
        PlexusContainer plexus = embedder.getContainer();
        
        context.setAttribute( PlexusConstants.PLEXUS_KEY, plexus );
        
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
        }
    }
}
