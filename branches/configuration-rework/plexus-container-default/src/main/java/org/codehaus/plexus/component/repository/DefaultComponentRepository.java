package org.codehaus.plexus.component.repository;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.CompositionResolver;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.exception.InvalidComponentDescriptorException;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.component.repository.synthesizer.ComponentConfigurationDescriptorSynthesizer;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

/**
 * @author Jason van Zyl
 */
public class DefaultComponentRepository
    extends AbstractLogEnabled
    implements ComponentRepository
{
    private static String COMPONENTS = "components";

    private static String COMPONENT = "component";

    private PlexusConfiguration configuration;

    private Map componentDescriptorMaps;

    private Map componentDescriptors;

    private CompositionResolver compositionResolver;

    private ComponentConfigurationDescriptorSynthesizer synthesizer;

    private ClassRealm classRealm;

    public DefaultComponentRepository()
    {
        componentDescriptors = new HashMap();

        componentDescriptorMaps = new HashMap();
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    protected PlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public boolean hasComponent( String role )
    {
        return componentDescriptors.containsKey( role );
    }

    public boolean hasComponent( String role, String roleHint )
    {
        return componentDescriptors.containsKey( role + roleHint );
    }

    public Map getComponentDescriptorMap( String role )
    {
        return (Map) componentDescriptorMaps.get( role );
    }

    public ComponentDescriptor getComponentDescriptor( String key )
    {
        return (ComponentDescriptor) componentDescriptors.get( key );
    }

    public void setClassRealm( ClassRealm classRealm )
    {
        this.classRealm = classRealm;
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void configure( PlexusConfiguration configuration )
    {
        this.configuration = configuration;
    }

    public void initialize()
        throws ComponentRepositoryException
    {
        initializeComponentDescriptors();
    }

    public void initializeComponentDescriptors()
        throws ComponentRepositoryException
    {
        initializeComponentDescriptorsFromUserConfiguration();
    }

    private void initializeComponentDescriptorsFromUserConfiguration()
        throws ComponentRepositoryException
    {
        PlexusConfiguration[] componentConfigurations = configuration.getChild( COMPONENTS ).getChildren( COMPONENT );

        for ( int i = 0; i < componentConfigurations.length; i++ )
        {
            addComponentDescriptor( componentConfigurations[i] );
        }
    }

    // ----------------------------------------------------------------------
    //  Component Descriptor processing.
    // ----------------------------------------------------------------------

    public void addComponentDescriptor( PlexusConfiguration configuration )
        throws ComponentRepositoryException
    {
        ComponentDescriptor componentDescriptor;

        try
        {
            componentDescriptor = PlexusTools.buildComponentDescriptor( configuration );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ComponentRepositoryException( "Cannot unmarshall component descriptor:", e );
        }

        addComponentDescriptor( componentDescriptor );
    }

    public void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException
    {
        if ( componentDescriptor.getConfigurationDescriptor() == null )
        {
            try
            {
                ComponentConfigurationDescriptor ccd = synthesizer.synthesizeDescriptor( classRealm,
                                                                                         componentDescriptor );

                componentDescriptor.setConfigurationDescriptor( ccd );
            }
            catch ( ComponentImplementationNotFoundException e )
            {
                throw new ComponentRepositoryException( "Error while synthezising component configuration descriptor " +
                    "for " + componentDescriptor.getHumanReadableKey() + ".", e );
            }
        }

        validateComponentDescriptor( componentDescriptor );

        String role = componentDescriptor.getRole();

        String roleHint = componentDescriptor.getRoleHint();

        if ( roleHint != null )
        {
            if ( componentDescriptors.containsKey( role ) )
            {
                ComponentDescriptor desc = (ComponentDescriptor) componentDescriptors.get( role );
                if ( desc.getRoleHint() == null )
                {
                    String message = "Component descriptor " + componentDescriptor.getHumanReadableKey() +
                        " has a hint, but there are other implementations that don't";
                    throw new ComponentRepositoryException( message );
                }
            }

            Map map = (Map) componentDescriptorMaps.get( role );

            if ( map == null )
            {
                map = new HashMap();

                componentDescriptorMaps.put( role, map );
            }

            map.put( roleHint, componentDescriptor );
        }
        else
        {
            if ( componentDescriptorMaps.containsKey( role ) )
            {
                String message = "Component descriptor " + componentDescriptor.getHumanReadableKey() +
                    " has no hint, but there are other implementations that do";
                throw new ComponentRepositoryException( message );
            }
            else if ( componentDescriptors.containsKey( role ) )
            {
                if ( !componentDescriptors.get( role ).equals( componentDescriptor ) )
                {
                    String message = "Component role " + role +
                        " is already in the repository and different to attempted addition of " +
                        componentDescriptor.getHumanReadableKey();
                    throw new ComponentRepositoryException( message );
                }
            }
        }

        try
        {
            compositionResolver.addComponentDescriptor( componentDescriptor );
        }
        catch ( CompositionException e )
        {
            throw new ComponentRepositoryException( e.getMessage(), e );
        }

        componentDescriptors.put( componentDescriptor.getComponentKey(), componentDescriptor );

        // We need to be able to lookup by role only (in non-collection situations), even when the 
        // component has a roleHint.
        if ( !componentDescriptors.containsKey( role ) )
        {
            componentDescriptors.put( role, componentDescriptor );
        }
    }

    public void validateComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws InvalidComponentDescriptorException
    {
        // TODO: Make sure the component implementation classes can be found.
        // TODO: Make sure ComponentManager implementation can be found.
        // TODO: Validate lifecycle.
        // TODO: Validate the component configuration.
        // TODO: Validate the component profile if one is used.

        if ( StringUtils.isEmpty( componentDescriptor.getRole() ) )
        {
            throw new InvalidComponentDescriptorException( "The component descriptor is required to have a role." );
        }

        ComponentConfigurationDescriptor ccd  = componentDescriptor.getConfigurationDescriptor();
        if ( ccd == null )
        {
            throw new InvalidComponentDescriptorException( "Missing required configuration descriptor." );
        }

        List fields = ccd.getFields();

        if ( fields != null && fields.size() > 0 )
        {
            for ( Iterator it = fields.iterator(); it.hasNext(); )
            {
                ComponentConfigurationFieldDescriptor field = (ComponentConfigurationFieldDescriptor) it.next();

                String name = field.getName();

                if ( StringUtils.isEmpty( name ) )
                {
                    throw new InvalidComponentDescriptorException( "Each field has to have a name" );
                }

                if ( StringUtils.isEmpty( field.getType() ) )
                {
                    throw new InvalidComponentDescriptorException( "Missing 'type' on field " + name );
                }

                String injectionMethod = field.getInjectionMethod();

                if ( !injectionMethod.equals( "private" ) &&
                     !injectionMethod.equals( "setter" ) )
                {
                    throw new InvalidComponentDescriptorException( "Unknown injection method '" + injectionMethod + "' for field: " + name + "." );
                }
            }
        }
    }

    public List getComponentDependencies( ComponentDescriptor componentDescriptor )
    {
        return compositionResolver.getRequirements( componentDescriptor.getComponentKey() );
    }
}
