package org.codehaus.plexus.formproc;

import java.io.File;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.formproc.FormManager;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 15, 2003
 */
public class DefaultFormProcService
    implements FormProcService, Configurable, Initializable
{
    protected final static String CONFIGURATION = "formproc-config";
    
    private FormManager manager;
    
    private String configuration;
    
    /**
     * @see org.codehaus.plexus.formproc.FormProcService#getFormManager()
     */
    public FormManager getFormManager()
    {
        return manager;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException
    {
        configuration = config.getAttribute( CONFIGURATION, "formproc.xml" );
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        File configFile = new File( configuration );
        
        if ( configFile.exists() )
        {
            manager = new FormManager( configFile );
        }
        else
        {
            manager = new FormManager(
                getClass().getResourceAsStream( configuration ) );        
        }
    }

}
