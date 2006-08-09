package org.codehaus.plexus.xfire.old;

import javax.xml.namespace.QName;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;

/**
 * Extends and configures the TypeMappingRegistry.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 31, 2004
 */
public class TypeMappingRegistry
    extends DefaultTypeMappingRegistry
    implements LogEnabled, Configurable, Initializable
{
    private Logger logger;

    public void configure(PlexusConfiguration config)
        throws PlexusConfigurationException
    {
        PlexusConfiguration tmConfig[] = config.getChildren("typeMapping");

        for ( int i = 0; i < tmConfig.length; i++ )
        {
            configureTypeMapping( tmConfig[i] );
        }
    }

    private void configureTypeMapping(PlexusConfiguration configuration)
        throws PlexusConfigurationException
    {
        TypeMapping tm = createTypeMapping(false);

        register( configuration.getAttribute( "namespace" ), tm );

        if ( Boolean.valueOf( configuration.getAttribute("default", "false") ).booleanValue() )
            registerDefault( tm );

        PlexusConfiguration[] types = configuration.getChildren( "type" );

        for ( int i = 0; i < types.length; i++ )
        {
            configureType( types[i], tm );
        }
    }

    private void configureType( PlexusConfiguration configuration, TypeMapping tm )
        throws PlexusConfigurationException
    {
        try
        {
            String ns = configuration.getAttribute("namespace");
            String name = configuration.getAttribute("name");
            QName qname = new QName(ns, name);

            Class clazz = ClassLoaderUtils.loadClass( configuration.getAttribute("class"), getClass() );
            Class typeClass = ClassLoaderUtils.loadClass( configuration.getAttribute("type"), getClass() );

            Type type = (Type) typeClass.newInstance();

            tm.register(clazz, qname, type);

            logger.debug( "Registered " + typeClass.getName() +
                              " for " + qname + " with class " + clazz.getName() );
        }
        catch (Exception e)
        {
            if ( e instanceof PlexusConfigurationException )
                throw (PlexusConfigurationException) e;

            throw new PlexusConfigurationException( "Could not configure type.", e );
        }
    }

    public void enableLogging(Logger logger)
    {
        this.logger = logger;
    }

    public void initialize()
    {
        createDefaultMappings();
    }
}
