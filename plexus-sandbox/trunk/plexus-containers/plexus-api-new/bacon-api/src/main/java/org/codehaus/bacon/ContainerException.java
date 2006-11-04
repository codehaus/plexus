package org.codehaus.bacon;

public abstract class ContainerException
    extends Exception
{

    protected ContainerException( String message, Throwable cause )
    {
        super( message, cause );
    }

    protected ContainerException( String message )
    {
        super( message );
    }

}
