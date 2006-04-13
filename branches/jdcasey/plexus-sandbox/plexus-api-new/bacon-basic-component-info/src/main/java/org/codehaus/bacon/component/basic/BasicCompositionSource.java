package org.codehaus.bacon.component.basic;

import org.codehaus.bacon.component.InjectionDescriptor;

public abstract class BasicCompositionSource
    implements InjectionDescriptor
{

    private boolean required = false;

    private String target;

    private String strategy;

    private String id;

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired( boolean required )
    {
        this.required = required;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId( String id )
    {
        this.id = id;
    }

    public String getInjectionTarget()
    {
        return target;
    }

    public void setInjectionTarget( String destination )
    {
        this.target = destination;
    }

    public String getInjectionStrategy()
    {
        return strategy;
    }

    public void setInjectionStrategy( String strategy )
    {
        this.strategy = strategy;
    }

}
