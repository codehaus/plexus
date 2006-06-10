package org.codehaus.plexus.xfire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.xfire.PlexusXFireComponent;
import org.codehaus.plexus.xfire.ConfigurationService;
import org.codehaus.plexus.xfire.Configurator;
import org.codehaus.plexus.service.Service;

/**
 * Loads in XFire components from the XFire configuration file.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DefaultConfigurationService
    extends PlexusXFireComponent
    implements Initializable, ConfigurationService, Configurable
{ 
    private Configurator configurator;
    
    private PlexusConfiguration services;
    
	public void initialize() throws InitializationException
	{
	    try
	    {	        
            createServices(services);
            
	        Reader reader = findConfigurationReader();
	             
	        if ( reader == null )
	        {
	           return;
	        }             
	       
	        PlexusConfiguration configuration = new XmlPlexusConfiguration( Xpp3DomBuilder.build(reader) );
	        
            createServices( configuration.getChild("services") );
	    }
	    catch( Exception e )
	    {
	        getLogger().error("Could not start the configuration service.", e);
	        throw new InitializationException("Could not start configuration service.", e);
	    }
	}
    
	private void createServices(PlexusConfiguration child) 
        throws Exception
    {
        PlexusConfiguration[] service = child.getChildren("service");
        
        for ( int i = 0; i < service.length; i++ )
        {
            createService( service[i] );
        }
    }
    
    private void createService(PlexusConfiguration c) 
        throws Exception
    {
        Service endpoint = configurator.createService(c);
    }

	protected Reader findConfigurationReader() throws FileNotFoundException
	{
		String configFileName = System.getProperty("xfire.config");
        
        Reader reader = null;
        
        if ( configFileName == null )
        {
            getLogger().info("No configuration file specified. Looking for xfire.xml in the current directory.");
            configFileName = "xfire.xml";
        }

        File file = new File( configFileName );
        
        if ( file.exists() )
        {
            getLogger().info("Found configuration file " + file.getAbsolutePath());
        	reader = new FileReader( file );
        }
        else
        {
            getLogger().info("Could not find configuration file " + file.getAbsolutePath() +
                             ". Looking in the classpath.");
        
            InputStream is = getClass().getResourceAsStream(configFileName);
            
            if ( is == null )
            {
                getLogger().info("Could not find configuration file " + configFileName +
                                 " on classpath. Looking for META-INF/xfire/xfire.xml " +
                                 "on the classpath");
            
                is = getClass().getResourceAsStream("META-INF/plexus/xfire.xml");
                
                if ( is == null )
                {
                    getLogger().info("No configuration found.");
                    return null;
                }
            }
            
            reader = new InputStreamReader( is );
        }
        
		return reader;
	}

    /**
     * @param arg0
     * @throws PlexusConfigurationException
     */
    public void configure(PlexusConfiguration config)
        throws PlexusConfigurationException
    {
        services = config.getChild("services");
    }
}
