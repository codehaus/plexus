package org.codehaus.plexus.spe.action;

import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.UnknownElement;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AntTargetConverter
    extends AbstractConfigurationConverter
{
    public static final String MAVEN_EXPRESSION_EVALUATOR_ID = "maven.expressionEvaluator";

    public final String ROLE = ConfigurationConverter.class.getName();

    public boolean canConvert( Class type )
    {
        return Target.class.isAssignableFrom( type );
    }

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                     Class baseType, ClassLoader classLoader,
                                     ExpressionEvaluator expressionEvaluator, ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        Object retValue = fromExpression( configuration, expressionEvaluator, type );
        if ( retValue != null )
        {
            return retValue;
        }

        Class implementation = getClassForImplementationHint( type, configuration, classLoader );

        retValue = instantiateObject( implementation );

        processConfiguration( (Target) retValue, configuration, expressionEvaluator );

        return retValue;
    }


    private void processConfiguration( Target target, PlexusConfiguration configuration,
                                       ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        Project project = new Project();
        project.setName( "DummyProject" );

        target.setName( "" );
        target.setProject( project );
        project.addTarget( target );

        project.addReference( MAVEN_EXPRESSION_EVALUATOR_ID, expressionEvaluator );

        initDefinitions( project, target );

        processConfiguration( null, project, target, configuration );

        project.init();
    }

    private void processConfiguration( RuntimeConfigurable parentWrapper, Project project, Target target,
                                       PlexusConfiguration configuration )
        throws ComponentConfigurationException
    {
        int items = configuration.getChildCount();

        Object parent = parentWrapper == null ? null : parentWrapper.getProxy();

        for ( int i = 0; i < items; i++ )
        {
            PlexusConfiguration childConfiguration = configuration.getChild( i );
            UnknownElement task = new UnknownElement( childConfiguration.getName() );
            task.setProject( project );
            task.setNamespace( "" );
            task.setQName( childConfiguration.getName() );
            task.setTaskType( ProjectHelper.genComponentName( task.getNamespace(), childConfiguration.getName() ) );
            task.setTaskName( childConfiguration.getName() );
            task.setOwningTarget( target );
            task.init();

            if ( parent != null )
            {
                ( (UnknownElement) parent ).addChild( task );
            }
            else
            {
                target.addTask( task );
            }

            RuntimeConfigurable wrapper = new RuntimeConfigurable( task, task.getTaskName() );

            try
            {
                if ( childConfiguration.getValue() != null )
                {
                    wrapper.addText( childConfiguration.getValue() );
                }
            }
            catch ( PlexusConfigurationException e )
            {
                throw new ComponentConfigurationException(
                    "Error reading text value from element '" + childConfiguration.getName() + "'", e );
            }

            String [] attrNames = childConfiguration.getAttributeNames();

            for ( String attrName : attrNames )
            {
                try
                {
                    String v = childConfiguration.getAttribute( attrName );
                    wrapper.setAttribute( attrName, v );
                }
                catch ( PlexusConfigurationException e )
                {
                    throw new ComponentConfigurationException(
                        "Error getting attribute '" + attrName + "' of tag '" + childConfiguration.getName() + "'", e );
                }
            }

            if ( parentWrapper != null )
            {
                parentWrapper.addChild( wrapper );
            }

            processConfiguration( wrapper, project, target, childConfiguration );
        }
    }

    protected void initDefinitions( Project project, Target unused )
    {
        ComponentHelper componentHelper = ComponentHelper.getComponentHelper( project );

        componentHelper.initDefaultDefinitions();
    }
}
