package org.codehaus.plexus.hibernate;

import java.io.File;
import java.net.URL;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * Hibernate service.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 */
public class DefaultHibernateService
    extends AbstractLogEnabled
    implements HibernateService, Initializable
{
    private SessionFactory sessionFactory;
    private net.sf.hibernate.cfg.Configuration hibConfig;
    private String mapping;
    
    /**
     * @see org.codehaus.plexus.hibernate.HibernateService#getSessionFactory()
     */
    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        getLogger().info( "Initializing Hibernate." );
        hibConfig = new net.sf.hibernate.cfg.Configuration();
        
        try
        {
            getLogger().info("Configuration mapping " + mapping);
            File file = new File( mapping );
            
            if ( file.exists() )
                hibConfig.configure( file );
            else
            {
                URL url = getClass().getResource(mapping);
                if ( url != null )
                {
                    hibConfig.configure(url);
                }
                else
                {
                	throw new RuntimeException("Couldn't find mapping file: " + mapping);
                }
            }
        }
        catch (HibernateException e)
        {
            throw new PlexusConfigurationException( "Mapping problem.", e );
        }

        sessionFactory = hibConfig.buildSessionFactory();
    }
}
