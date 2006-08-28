package org.codehaus.bacon.component;

import org.codehaus.bacon.ContainerException;

public abstract class ComponentException
    extends ContainerException
{

    protected ComponentException( String message, Throwable cause )
    {
        super( message, cause );
    }

    protected ComponentException( String message )
    {
        super( message );
    }

}
