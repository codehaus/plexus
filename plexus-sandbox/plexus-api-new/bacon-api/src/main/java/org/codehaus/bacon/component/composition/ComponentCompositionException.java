package org.codehaus.bacon.component.composition;

import org.codehaus.bacon.component.ComponentException;


public class ComponentCompositionException
    extends ComponentException
{
    private static final long serialVersionUID = 1L;

    protected ComponentCompositionException( String message, Throwable cause )
    {
        super( message, cause );
    }

    protected ComponentCompositionException( String message )
    {
        super( message );
    }

}
