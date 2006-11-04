package org.codehaus.plexus.hibernate;

import java.util.Properties;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.SessionFactory;

/**
 * Hibernate service.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 */
public class DefaultHibernateService
    extends AbstractLogEnabled
    implements HibernateService, Configurable, Initializable
{
    SessionFactory sessionFactory;
    net.sf.hibernate.cfg.Configuration hibConfig;
    
    /**
     * @see org.codehaus.plexus.hibernate.HibernateService#getSessionFactory()
     */
    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException
    {
        hibConfig = new net.sf.hibernate.cfg.Configuration();
        
        String mapping = config.getAttribute( "mapping" );
        try
        {
            getLogger().debug("Configuration mapping " + mapping);
            hibConfig.configure( mapping );
        }
        catch (HibernateException e)
        {
            throw new ConfigurationException( "Mapping problem.", e );
        }
        
        Configuration[] classes = config.getChild( "classes" ).getChildren();
        if ( classes != null )
        {
            configureClasses( classes );
        }

        Configuration[] properties = config.getChild( "properties" ).getChildren();
        Properties props = new Properties();
        for ( int i = 0; i < properties.length; i++ )
        {
            props.setProperty( properties[i].getAttribute("name"),
                               properties[i].getAttribute("value") );
        }
    
        hibConfig.addProperties( props );
    }

    private void configureClasses(Configuration[] classes) throws ConfigurationException
    {
        for ( int i = 0; i < classes.length; i++ )
        {
            try
            {
                hibConfig.addClass( Class.forName( classes[i].getValue() ) );
            }
            catch ( ClassNotFoundException e )
            {
                throw new ConfigurationException( "Could not find class " 
                                                    + classes[i].getValue(),
                                                    e );
            } 
            catch (MappingException e)
            {
                throw new ConfigurationException( "Mapping problem.", e );
            } 
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        getLogger().info( "Initializing Hibernate." );
        sessionFactory = hibConfig.buildSessionFactory();
    }
}
