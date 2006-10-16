package org.codehaus.bacon.component.factory;

import org.codehaus.bacon.component.ComponentException;

public class InstantiationException
    extends ComponentException
{
    private static final long serialVersionUID = 1L;

    public InstantiationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public InstantiationException( String message )
    {
        super( message );
    }

}
