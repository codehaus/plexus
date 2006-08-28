package org.codehaus.bacon.component.injection;

import org.codehaus.bacon.component.ComponentException;

public class ComponentInjectionException
    extends ComponentException
{

    private static final long serialVersionUID = 1L;

    public ComponentInjectionException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ComponentInjectionException( String message )
    {
        super( message );
    }

}
