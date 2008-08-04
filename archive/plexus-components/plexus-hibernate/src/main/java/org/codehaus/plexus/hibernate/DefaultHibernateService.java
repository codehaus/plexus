package org.codehaus.plexus.hibernate;

import java.io.File;
import java.net.URL;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

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
    private Configuration hibConfig;
    private String mapping;

    // ----------------------------------------------------------------------
    // HibernateService Implementation
    // ----------------------------------------------------------------------

    /**
     * @see org.codehaus.plexus.hibernate.HibernateService#getSessionFactory()
     */
    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        getLogger().info( "Initializing Hibernate." );
        hibConfig = new Configuration();
        
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
            throw new InitializationException( "Mapping problem.", e );
        }

        try
        {
            sessionFactory = hibConfig.buildSessionFactory();
        }
        catch ( HibernateException e )
        {
            throw new InitializationException( "Problem creating session factory: ", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public Configuration getConfiguration()
    {
        return hibConfig;
    }
}
