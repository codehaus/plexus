package org.codehaus.plexus.spe.execution;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.Serializable;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusActionStepExecutor
    extends AbstractLogEnabled
    implements StepExecutor, Initializable
{
    /**
     * @plexus.requirement
     */
    private PlexusContainer container;

    private ClassRealm classRealm;

    // ----------------------------------------------------------------------
    // StepExecutor Implementation
    // ----------------------------------------------------------------------

    public void execute( StepDescriptor stepDescriptor, Map<String, Serializable> context )
        throws ProcessException
    {
        Xpp3Dom executorConfiguration = (Xpp3Dom) stepDescriptor.getExecutorConfiguration();

        String actionId = getChild( executorConfiguration, "actionId" );

        String configuratorId = getChild( executorConfiguration, "configuratorId", "basic" );

        Action action;

        ComponentConfigurator configurator;

        // ----------------------------------------------------------------------
        // Look up the action and the configurator
        // ----------------------------------------------------------------------

        try
        {
            action = (Action) container.lookup( Action.ROLE, actionId );
        }
        catch ( ComponentLookupException e )
        {
            throw new ProcessException( "Could not look up Hauskeeper action '" + actionId + "'.", e );
        }

        try
        {
            configurator = (ComponentConfigurator) container.lookup( ComponentConfigurator.ROLE, configuratorId );
        }
        catch ( ComponentLookupException e )
        {
            throw new ProcessException( "Could not look up component configurator '" + configuratorId + "'.", e );
        }
        finally
        {
            rel
        }

        // ----------------------------------------------------------------------
        // Configure the action
        // ----------------------------------------------------------------------

        Xpp3Dom configuration = (Xpp3Dom) stepDescriptor.getConfiguration();

        PlexusConfiguration componentConfiguration = new XmlPlexusConfiguration( configuration );

        try
        {
            configurator.configureComponent( action, componentConfiguration, classRealm);
        }
        catch ( ComponentConfigurationException e )
        {
            throw new ProcessException( "Error while configuring the action.", e );
        }

        // ----------------------------------------------------------------------
        // Execute the action
        // ----------------------------------------------------------------------

        try
        {
            action.execute( context );
        }
        catch ( Exception e )
        {
            throw new ProcessException( "Error while executing action.", e );
        }
        finally
        {
            try
            {
                container.release( configurator );
            }
            catch ( ComponentLifecycleException e )
            {
                getLogger().error( "Error while releasing component configurator.", e );
            }
            try
            {
                container.release( action );
            }
            catch ( ComponentLifecycleException e )
            {
                getLogger().error( "Error while releasing action.", e );
            }
        }
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        // TODO: Is this the correct realm to use? I would think so as it's the same as the one that the action
        // itself is looked up from.
        classRealm = container.getContainerRealm();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String getChild( Xpp3Dom configuration, String key )
        throws ProcessException
    {
        if ( configuration.getChild( key ) == null || StringUtils.isEmpty( configuration.getChild( key ).getValue() ) )
        {
            throw new ProcessException( "Invalid configuration: Missing '" + key + "'." );
        }

        return configuration.getChild( key ).getValue();
    }

    private String getChild( Xpp3Dom configuration, String key, String defaultValue )
        throws ProcessException
    {
        if ( configuration.getChild( key ) == null || StringUtils.isEmpty( configuration.getChild( key ).getValue() ) )
        {
            return defaultValue;
        }

        return configuration.getChild( key ).getValue();
    }

    private void release( Object component )
    {
        try
        {
            container.release( component );
        }
        catch ( ComponentLifecycleException e )
        {
            getLogger().error( "Error while releasing ", e );
        }
    }
}
