package org.codehaus.plexus.personality.pico.factory;

import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;

/**
 * Component Factory for pico components
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class PicoComponentFactory
        extends AbstractComponentFactory
{
    /**
     * @todo pico components can have theoretically more then one constructor
     * which is rather stupid idea
     */
    public Object newInstance( ComponentDescriptor componentDescriptor, ClassRealm classRealm, PlexusContainer container )
            throws ComponentInstantiationException
    {
        try
        {

            String implementation = componentDescriptor.getImplementation();

            Class implementationClass = classRealm.loadClass( implementation );

            Constructor constructor = implementationClass.getConstructors()[ 0 ];

            Class[] parameterTypes = constructor.getParameterTypes();

            Object[] params = new Object[ parameterTypes.length ];

            for ( int i = 0; i < parameterTypes.length; i++ )
            {
                Class parameterType = parameterTypes[ i ];

                params[ i ] = lookupComponent( parameterType, componentDescriptor, container );

            }

            Object retValue = constructor.newInstance( params );

            return retValue;
                        
        }
        catch ( Exception e )
        {
            String msg = "Component " + componentDescriptor.getHumanReadableKey() + " cannot be instantiated";

            throw new ComponentInstantiationException( msg, e );
        }
    }


    private Object lookupComponent( Class parameterType, ComponentDescriptor componentDescriptor, PlexusContainer container ) throws ComponentInstantiationException
    {
        try
        {
            String role;

            if ( parameterType.isArray() )
            {
                role = parameterType.getComponentType().getName();

            }
            else
            {
                role = parameterType.getName();
            }

            Object retValue = null;

            List requirements = componentDescriptor.getRequirements();

            ComponentRequirement requirement = getMatchingRequirement( componentDescriptor, requirements );

            if ( requirement == null )
            {
                if ( parameterType.isArray() )
                {

                    List dependencies = container.lookupList( role );

                    retValue = Array.newInstance( parameterType.getComponentType(), dependencies.size() );

                    int len = dependencies.size();

                    Object[] array = ( Object[] ) Array.newInstance( parameterType.getComponentType(), len );

                    for ( int i = 0; i < len; i++ )
                    {
                        array[ i ] = dependencies.get( i );

                    }

                    retValue = array;
                }

                else
                {

                    retValue = container.lookup( role );
                }
            }
            else
            {
                if ( parameterType.isArray() )
                {
                    List dependencies = container.lookupList( role );

                    int len = dependencies.size();

                    Object[] array = ( Object[] ) Array.newInstance( parameterType.getComponentType(), len );

                    for ( int i = 0; i < len; i++ )
                    {
                        array[ i ] = dependencies.get( i );


                    }

                    retValue = array;

                }
                else
                {
                    retValue = container.lookup( role );
                }
            }

            return retValue;
        }
        catch ( ComponentLookupException e )
        {
            throw new ComponentInstantiationException( e );
        }
    }

    private ComponentRequirement getMatchingRequirement( ComponentDescriptor componentDescriptor, List requirements )
    {

        ComponentRequirement retValue = null;

        String role = componentDescriptor.getRole();

        String roleHint = componentDescriptor.getRoleHint();

        if ( componentDescriptor.getRoleHint() != null )
        {
            for ( Iterator iterator = requirements.iterator(); iterator.hasNext(); )
            {
                ComponentRequirement requirement = ( ComponentRequirement ) iterator.next();

                if ( role.equals( requirement.getRole() ) && roleHint.equals( requirement.getRoleHint() ) )
                {
                    retValue = requirement;

                    break;
                }
            }
        }
        else
        {
            for ( Iterator iterator = requirements.iterator(); iterator.hasNext(); )
            {
                ComponentRequirement requirement = ( ComponentRequirement ) iterator.next();

                if ( role.equals( requirement.getRole() ) )
                {
                    retValue = requirement;

                    break;
                }
            }

        }

        return retValue;

    }
}
