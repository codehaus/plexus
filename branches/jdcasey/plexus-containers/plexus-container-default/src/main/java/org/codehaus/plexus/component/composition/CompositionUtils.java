package org.codehaus.plexus.component.composition;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author Jason van Zyl
 * @version $Id$
 */

public class CompositionUtils
{
    public static Requirement findRequirement( Object component,
                                               Class clazz,
                                               PlexusContainer container,
                                               ComponentRequirement requirement )
        throws CompositionException
    {
        try
        {
            List componentDescriptors;

            Object assignment;

            String role = requirement.getRole();

            if ( clazz.isArray() )
            {
                List dependencies = container.lookupList( role );

                Object[] array = (Object[]) Array.newInstance( clazz, dependencies.size() );

                componentDescriptors = container.getComponentDescriptorList( role );

                assignment = dependencies.toArray( array );
            }
            else if ( Map.class.isAssignableFrom( clazz ) )
            {
                assignment = container.lookupMap( role );

                componentDescriptors = container.getComponentDescriptorList( role );
            }
            else if ( List.class.isAssignableFrom( clazz ) )
            {
                assignment = container.lookupList( role );

                componentDescriptors = container.getComponentDescriptorList( role );
            }
            else if ( Set.class.isAssignableFrom( clazz ) )
            {
                assignment = container.lookupMap( role );

                componentDescriptors = container.getComponentDescriptorList( role );
            }
            else
            {
                String key = requirement.getRequirementKey();

                assignment = container.lookup( key );

                ComponentDescriptor componentDescriptor = container.getComponentDescriptor( key );

                componentDescriptors = new ArrayList( 1 );

                componentDescriptors.add( componentDescriptor );
            }

            return new Requirement( assignment, componentDescriptors );
        }
        catch ( ComponentLookupException e )
        {
            throw new CompositionException( "Composition failed of field " + requirement.getFieldName() + " " +
                "in object of type " + component.getClass().getName() + " because the requirement " + requirement + " was missing", e );
        }
    }
}
