package org.codehaus.bacon.component.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.bacon.component.ComponentAttribute;
import org.codehaus.bacon.component.ComponentDescriptor;
import org.codehaus.bacon.component.ComponentReference;

public class BasicComponentDescriptor
    implements ComponentDescriptor
{
    private Set references = new HashSet();

    private Set attributes = new HashSet();

    private String interfaceName;

    private String instanceName;

    private String implementation;

    private String instantiationStrategy;

    private String language;
    
    private List constructionRequirements = new ArrayList();

    // calculated.
    private Map referencesById;

    // calculated.
    private Map attributesById;

    public List getConstructionRequirements()
    {
        return constructionRequirements;
    }

    public void setConstructionRequirements( List constructionRequirements )
    {
        this.constructionRequirements.clear();
        this.constructionRequirements.addAll( constructionRequirements );
    }
    
    public void addConstructionRequirement( String compositionSourceId )
    {
        this.constructionRequirements.add( compositionSourceId );
    }

    public String getInterfaceName()
    {
        return interfaceName;
    }

    public void setInterfaceName( String interfaceName )
    {
        this.interfaceName = interfaceName;
    }

    public String getInstanceName()
    {
        return instanceName;
    }

    public void setInstanceName( String instanceName )
    {
        this.instanceName = instanceName;
    }

    public String getImplementation()
    {
        return implementation;
    }

    public void setImplementation( String implementation )
    {
        this.implementation = implementation;
    }

    public Set getComponentReferences()
    {
        return Collections.unmodifiableSet( references );
    }

    public void setComponentReferences( Set componentReferences )
    {
        this.references.clear();
        this.references.addAll( componentReferences );
    }

    public void addComponentReference( ComponentReference componentReference )
    {
        references.add( componentReference );
    }

    public Set getComponentAttributes()
    {
        return Collections.unmodifiableSet( attributes );
    }

    public void setComponentAttributes( Set componentAttributes )
    {
        this.attributes.clear();
        this.attributes.addAll( componentAttributes );
    }

    public void addComponentAttribute( ComponentAttribute componentAttribute )
    {
        attributes.add( componentAttribute );
    }

    public String toString()
    {
        return "Basic Component Descriptor (" + interfaceName + ":" + (instanceName == null ? ComponentDescriptor.NO_INSTANCE_NAME : instanceName ) + ")";
    }

    public String getInstantiationStrategy()
    {
        return instantiationStrategy;
    }
    
    public void setInstantiationStrategy( String instantiationStrategy )
    {
        this.instantiationStrategy = instantiationStrategy;
    }
    
    public String getImplementationLanguage()
    {
        return language;
    }
    
    public void setImplementationLanguage( String language )
    {
        this.language = language;
    }

    public Map getComponentReferencesById()
    {
        if ( referencesById == null )
        {
            if ( references != null && !references.isEmpty() )
            {
                referencesById = new HashMap();
                
                for ( Iterator it = references.iterator(); it.hasNext(); )
                {
                    ComponentReference reference = (ComponentReference) it.next();

                    referencesById.put( reference.getId(), reference );
                }
                
                referencesById = Collections.unmodifiableMap( referencesById );
            }
            else
            {
                referencesById = Collections.EMPTY_MAP;
            }
        }
        
        return referencesById;
    }

    public Map getComponentAttributesById()
    {
        if ( attributesById == null )
        {
            if ( attributes != null && !attributes.isEmpty() )
            {
                attributesById = new HashMap();
                
                for ( Iterator it = attributes.iterator(); it.hasNext(); )
                {
                    ComponentAttribute attribute = (ComponentAttribute) it.next();

                    attributesById.put( attribute.getId(), attribute );
                }
                
                attributesById = Collections.unmodifiableMap( attributesById );
            }
            else
            {
                attributesById = Collections.EMPTY_MAP;
            }
        }
        
        return attributesById;
    }

}
