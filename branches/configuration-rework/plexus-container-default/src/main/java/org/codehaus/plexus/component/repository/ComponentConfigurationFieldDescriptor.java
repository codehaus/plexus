package org.codehaus.plexus.component.repository;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ComponentConfigurationFieldDescriptor
{
    private String name;

    private String type;

    private String injectionMethod;

    private String description;

    private String expression;

    private boolean required;

    private boolean readOnly;

    private String since;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getInjectionMethod()
    {
        return injectionMethod;
    }

    public void setInjectionMethod( String injectionMethod )
    {
        this.injectionMethod = injectionMethod;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired( boolean required )
    {
        this.required = required;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly( boolean readOnly )
    {
        this.readOnly = readOnly;
    }

    public String getSince()
    {
        return since;
    }

    public void setSince( String since )
    {
        this.since = since;
    }
}
