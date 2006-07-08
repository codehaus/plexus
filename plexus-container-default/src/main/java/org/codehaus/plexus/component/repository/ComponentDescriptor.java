package org.codehaus.plexus.component.repository;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.classworlds.ClassRealm;

import java.util.ArrayList;
import java.util.List;

/**
 * Component instantiation description.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class ComponentDescriptor
{
    private String alias;

    private String role;

    private String roleHint;

    private String implementation;

    private String version;

    private String componentType;

    private ComponentConfigurationDescriptor configurationDescriptor;

    private PlexusConfiguration configuration;

    private String instantiationStrategy;

    private String lifecycleHandler;

    private String componentProfile;

    private List requirements;

    private String componentFactory;

    private String componentComposer;

    private String componentConfigurator;

    private String description;

    // ----------------------------------------------------------------------
    // These two fields allow for the specification of an isolated class realm
    // and dependencies that might be specified in a component configuration
    // setup by a user i.e. this is here to allow isolation for components
    // that are not picked up by the discovery mechanism.
    // ----------------------------------------------------------------------

    private boolean isolatedRealm;

    private List dependencies;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private ComponentSetDescriptor componentSetDescriptor;

    // ----------------------------------------------------------------------
    //  Instance methods
    // ----------------------------------------------------------------------

    public String getComponentKey()
    {
        if ( getRoleHint() != null )
        {
            return getRole() + getRoleHint();
        }

        return getRole();
    }

    public String getHumanReadableKey()
    {
        StringBuffer key = new StringBuffer();

        key.append( "role: '" ).append( role ).append( "', " );

        key.append( "implementation: '" ).append( implementation ).append( "'" );

        if ( roleHint != null )
        {
            key.append( ", role hint: '" ).append( roleHint ).append( "'" );
        }

        if ( alias != null )
        {
            key.append( ", alias: '" ).append( alias ).append( "'" );
        }

        return key.toString();
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias( String alias )
    {
        this.alias = alias;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole( String role )
    {
        this.role = role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public void setRoleHint( String roleHint )
    {
        this.roleHint = roleHint;
    }

    public String getImplementation()
    {
        return implementation;
    }

    public void setImplementation( String implementation )
    {
        this.implementation = implementation;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getComponentType()
    {
        return componentType;
    }

    public void setComponentType( String componentType )
    {
        this.componentType = componentType;
    }

    public ComponentConfigurationDescriptor getConfigurationDescriptor()
    {
        return configurationDescriptor;
    }

    public void setConfigurationDescriptor( ComponentConfigurationDescriptor configurationDescriptor )
    {
        this.configurationDescriptor = configurationDescriptor;
    }

    public String getInstantiationStrategy()
    {
        return instantiationStrategy;
    }

    public PlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration( PlexusConfiguration configuration )
    {
        this.configuration = configuration;
    }

    public boolean hasConfiguration()
    {
        return configuration != null;
    }

    public String getLifecycleHandler()
    {
        return lifecycleHandler;
    }

    public void setLifecycleHandler( String lifecycleHandler )
    {
        this.lifecycleHandler = lifecycleHandler;
    }

    public String getComponentProfile()
    {
        return componentProfile;
    }

    public void setComponentProfile( String componentProfile )
    {
        this.componentProfile = componentProfile;
    }

    public void addRequirement( final ComponentRequirement requirement )
    {
        getRequirements().add( requirement );
    }

    public void addRequirements( List requirements )
    {
        getRequirements().addAll( requirements );
    }

    public List getRequirements()
    {
        if ( requirements == null )
        {
            requirements = new ArrayList();
        }
        return requirements;
    }

    public String getComponentFactory()
    {
        return componentFactory;
    }

    public void setComponentFactory( String componentFactory )
    {
        this.componentFactory = componentFactory;
    }

    public String getComponentComposer()
    {
        return componentComposer;
    }

    public void setComponentComposer( String componentComposer )
    {
        this.componentComposer = componentComposer;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setInstantiationStrategy( String instantiationStrategy )
    {
        this.instantiationStrategy = instantiationStrategy;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public boolean isIsolatedRealm()
    {
        return isolatedRealm;
    }

    public void setComponentSetDescriptor( ComponentSetDescriptor componentSetDescriptor )
    {
        this.componentSetDescriptor = componentSetDescriptor;
    }

    public ComponentSetDescriptor getComponentSetDescriptor()
    {
        return componentSetDescriptor;
    }

    public void setIsolatedRealm( boolean isolatedRealm )
    {
        this.isolatedRealm = isolatedRealm;
    }

    public List getDependencies()
    {
        return dependencies;
    }

    public String getComponentConfigurator()
    {
        return componentConfigurator;
    }

    public void setComponentConfigurator( String componentConfigurator )
    {
        this.componentConfigurator = componentConfigurator;
    }

    // Component identity established here!
    public boolean equals( Object other )
    {
        if ( other == null || !( other instanceof ComponentDescriptor ) )
        {
            return false;
        }
        else
        {
            ComponentDescriptor otherDescriptor = (ComponentDescriptor) other;

            String role = getRole();
            String roleHint = getRoleHint();
            String otherRole = otherDescriptor.getRole();
            String otherRoleHint = otherDescriptor.getRoleHint();

            if ( role.equals( otherRole ) && roleHint == null && otherRoleHint == null )
            {
                return true;
            }

            return roleHint != null && otherRoleHint != null && roleHint.equals( otherRoleHint );
        }
    }

    public String toString()
    {
        return this.getClass().getName() + " [role: '" + getRole() + "', hint: '" + getRoleHint() + "']";
    }

    public int hashCode()
    {
        int result = getRole().hashCode();

        String hint = getRoleHint();

        if ( hint != null )
        {
            result += hint.hashCode() * 37;
        }

        return result;
    }
}
