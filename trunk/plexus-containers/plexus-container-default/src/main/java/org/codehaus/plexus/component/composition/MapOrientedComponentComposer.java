package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.MapOrientedComponent;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class MapOrientedComponentComposer
    extends AbstractComponentComposer
{

    private static final String SINGLE_MAPPING_TYPE = "single";

    private static final String MAP_MAPPING_TYPE = "map";

    private static final String SET_MAPPING_TYPE = "set";

    private static final String DEFAULT_MAPPING_TYPE = SINGLE_MAPPING_TYPE;

    public List assembleComponent( Object component, ComponentDescriptor componentDescriptor, PlexusContainer container )
        throws CompositionException
    {
        if ( !( component instanceof MapOrientedComponent ) )
        {
            throw new CompositionException( "Cannot compose component: " + component.getClass().getName()
                + "; it does not implement " + MapOrientedComponent.class.getName() );
        }

        List retValue = new LinkedList();

        List requirements = componentDescriptor.getRequirements();

        for ( Iterator i = requirements.iterator(); i.hasNext(); )
        {
            ComponentRequirement requirement = (ComponentRequirement) i.next();

            List descriptors = addRequirement( (MapOrientedComponent) component, container, requirement );

            retValue.addAll( descriptors );
        }

        return retValue;
    }

    private List addRequirement( MapOrientedComponent component, PlexusContainer container,
                                ComponentRequirement requirement )
        throws CompositionException
    {
        try
        {
            List retValue;

            String role = requirement.getRole();
            String hint = requirement.getRoleHint();
            String mappingType = requirement.getFieldMappingType();

            Object value = null;

            // if the hint is not empty, we don't care about mapping type...it's a single-value, not a collection.
            if ( StringUtils.isNotEmpty( hint ) )
            {
                String key = requirement.getRequirementKey();

                value = container.lookup( key );

                ComponentDescriptor componentDescriptor = container.getComponentDescriptor( key );

                retValue = Collections.singletonList( componentDescriptor );
            }
            else if ( SINGLE_MAPPING_TYPE.equals( mappingType ) )
            {
                String key = requirement.getRequirementKey();

                value = container.lookup( key );

                ComponentDescriptor componentDescriptor = container.getComponentDescriptor( key );

                retValue = Collections.singletonList( componentDescriptor );
            }
            else if ( MAP_MAPPING_TYPE.equals( mappingType ) )
            {
                value = container.lookupMap( role );

                retValue = container.getComponentDescriptorList( role );
            }
            else if ( SET_MAPPING_TYPE.equals( mappingType ) )
            {
                value = new HashSet( container.lookupList( role ) );

                retValue = container.getComponentDescriptorList( role );
            }
            else
            {
                String key = requirement.getRequirementKey();

                value = container.lookup( key );

                ComponentDescriptor componentDescriptor = container.getComponentDescriptor( key );

                retValue = Collections.singletonList( componentDescriptor );
            }

            component.addComponentRequirement( requirement, value );

            return retValue;
        }
        catch ( ComponentLookupException e )
        {
            throw new CompositionException( "Composition failed in object of type " + component.getClass().getName()
                + " because the requirement " + requirement + " was missing", e );
        }
        catch ( ComponentConfigurationException e )
        {
            throw new CompositionException( "Composition failed in object of type " + component.getClass().getName()
                                            + " because the requirement " + requirement + " cannot be set on the component.", e );
        }
    }

}
