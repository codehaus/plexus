package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public abstract class AbstractComponentComposer
    implements ComponentComposer
{
    public void assembleComponent( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   PlexusContainer container,
                                   ComponentRepository componentRepository )
        throws Exception
    {
        //!! We should check for cycles here before we attempt this. Look
        //   for possible ways to short-circuit the cycles.

        if ( componentDescriptor.getRequirements().size() > 0 )
        {
            Set requirements = componentDescriptor.getRequirements();

            for ( Iterator i = requirements.iterator(); i.hasNext(); )
            {
                String role = (String) i.next();

                Object requirement = container.lookup( role );

                assembleComponent( requirement, componentRepository.getComponentDescriptor( role ), container, componentRepository );

                assignComponent( component, requirement );
            }
        }
    }

    /**
     * Assign a requirement to a component object by setting the appropriate field in
     * the component object. We find a match by looking at the requirement object's class
     * and match it up with the field of the same type in the component.
     *
     * @param requirement Component to assign to the component.
     * @param component Target requirement to which the requirement will be assigned.
     */
    protected void assignComponent( Object component, Object requirement )
        throws CompositionException
    {
        if ( component == null )
        {
            throw new CompositionException( "Target object is null." );
        }

        Class requirementClass = requirement.getClass();

        Field[] fields = component.getClass().getDeclaredFields();

        Field field = null;

        for ( int i = 0; i < fields.length; i++ )
        {
            if ( fields[i].getType().isAssignableFrom( requirementClass ) )
            {
                field = fields[i];

                break;
            }
        }

        if ( field == null )
        {
            throw new CompositionException( "No field which is compatible in component object: " + requirement.getClass().getName() );
        }

        field.setAccessible( true );

        try
        {
            field.set( component, requirement );
        }
        catch ( Exception e )
        {
            throw new CompositionException( "Error assigning field." );
        }
    }
}
